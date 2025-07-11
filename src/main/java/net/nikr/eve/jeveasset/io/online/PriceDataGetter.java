/*
 * Copyright 2009-2025 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.eve.jeveasset.io.online;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceMode;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.settings.PriceHistoryDatabase;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.me.candle.eve.pricing.Pricing;
import uk.me.candle.eve.pricing.PricingFactory;
import uk.me.candle.eve.pricing.PricingListener;
import uk.me.candle.eve.pricing.options.LocationType;
import uk.me.candle.eve.pricing.options.PriceLocation;
import uk.me.candle.eve.pricing.options.PriceType;
import uk.me.candle.eve.pricing.options.PricingFetch;
import uk.me.candle.eve.pricing.options.PricingOptions;


public class PriceDataGetter implements PricingListener {

	private static final Logger LOG = LoggerFactory.getLogger(PriceDataGetter.class);

	private static final String JANICE = "";
	private static final long PRICE_CACHE_TIMER = 1 * 60 * 60 * 1000L; // 1 hour (hours*min*sec*ms)
	private static final int ATTEMPT_COUNT = 2;
	private static final int ZERO_PRICES_WARNING_LIMIT = 10;
	private static final double FAILED_PERCENT_CANCEL_LIMIT = 5.0;
	private static final int PLEX_TYPE_ID = 44992;

	private UpdateTask updateTask;
	private boolean update;
	private Set<Integer> typeIDs;
	private Set<Integer> failed;
	private Set<Integer> okay;
	private Set<Integer> zero;
	private Set<Integer> queue;
	private final Map<Integer, PriceData> updatedList = Collections.synchronizedMap(new HashMap<>());
	private final Map<Integer, PriceData> priceDataList = Collections.synchronizedMap(new HashMap<>());

	private long nextUpdate = 0;

	public void load() {
		Map<Integer, PriceData> priceData = processLoad();
		if (priceData != null) {
			Settings.get().setPriceData(priceData);
		}
	}

	/**
	 * Load price data from cache and only update missing price data.
	 * @param profileData
	 * @param task UpdateTask to track progress
	 * @return
	 */
	public boolean updateNew(final ProfileData profileData, final UpdateTask task) {
		return processUpdate(profileData, task, false);
	}

	/**
	 * Update of all price data.
	 * @param profileData
	 * @param task UpdateTask to track progress
	 * @return
	 */
	public boolean updateAll(final ProfileData profileData, final UpdateTask task) {
		return processUpdate(profileData, task, true);
	}

	/**
	 * Load data from price cache
	 * @return available price data
	 */
	private Map<Integer, PriceData> processLoad() {
		Pricing pricing = PricingFactory.getPricing(PricingFetch.FUZZWORK, new DefaultPricingOptions());
		LOG.info("Price data loading");
		for (Item item : StaticData.get().getItems().values()) { //For each typeID
			if (!item.isMarketGroup()) {
				continue;
			}
			int typeID = item.getTypeID();
			PriceData priceData = priceDataList.get(typeID);
			if (priceData == null) {
				priceData = new PriceData();
				priceDataList.put(typeID, priceData);
			}
			boolean ok = false;
			for (PriceMode priceMode : PriceMode.values()) { //For each PriceMode (all combinations of PricingNumber & PricingType)
				PriceType priceType = priceMode.getPricingType();
				if (priceType == null) {
					continue; //Ignore calculated prices - f.ex. PriceMode.PRICE_MIDPOINT
				}
				Double price = pricing.getPriceCache(typeID, priceType);
				if (price != null) {
					ok = true; //Something is set
					PriceMode.setDefaultPrice(priceData, priceMode, price);
				}
			}
			if (!ok) {
				priceDataList.remove(typeID); //Remove failed typeID
			}
			long nextUpdateTemp = pricing.getNextUpdateTime(typeID);
			if (nextUpdateTemp >= 0 && nextUpdateTemp > getNextUpdateTime()) {
				setUpdateNext(nextUpdateTemp);
			}
		}
		if (!priceDataList.isEmpty()) {
			LOG.info("	Price data loaded");
			Map<Integer, PriceData> hashMap = new HashMap<>();
			hashMap.putAll(priceDataList);
			return hashMap; //Return copy of Map
		} else {
			LOG.info("	Price data not loaded");
			return null;
		}
	}

	/**
	 * Update settings with new price data
	 * @param task UpdateTask to update progress on
	 * @param updateAll if true update all prices, if false only update new/missing prices
	 * @return true if OK or false if FAILED
	 */
	private boolean processUpdate(final ProfileData profileData, final UpdateTask task, final boolean updateAll) {
		Set<Integer> priceTypeIDs = profileData.getPriceTypeIDs();
		//Remove plex
		boolean plex;
		if (Settings.get().getPriceDataSettings().getSource() == PriceSource.FUZZWORK) {
			plex = priceTypeIDs.remove(PLEX_TYPE_ID);
		} else {
			plex = false;
		}
		//Update normal
		Map<Integer, PriceData> priceData = processUpdate(task, updateAll, new DefaultPricingOptions(), priceTypeIDs, Settings.get().getPriceDataSettings().getSource());
		//Update plex
		Map<Integer, PriceData> plexPriceData = null;
		if (plex) {
			plexPriceData = processUpdate(task, updateAll, new PlexPricingOptions(0), Collections.singleton(PLEX_TYPE_ID), Settings.get().getPriceDataSettings().getSource());
		}
		if (priceData != null) {
			if (plexPriceData != null) {
				priceData.putAll(plexPriceData);
			}
			Settings.get().setPriceData(priceData);
			return true;
		} else {
			return false;
		}
	}

	/**
	 *
	 * @param task UpdateTask to update progress on
	 * @param updateAll true to update all prices. false to only update new/missing prices
	 * @param pricingOptions Options used doing update
	 * @param typeIDs TypeIDs to get price data for
	 * @param priceSource Price data source to update from (only used in log)
	 * @return
	 */
	protected Map<Integer, PriceData> processUpdate(final UpdateTask task, final boolean updateAll, final PricingOptions pricingOptions, final Set<Integer> typeIDs, final PriceSource priceSource) {
		this.updateTask = task;
		this.update = updateAll;
		this.typeIDs = Collections.synchronizedSet(new HashSet<>(typeIDs));
		this.failed = Collections.synchronizedSet(new HashSet<>());
		this.zero = Collections.synchronizedSet(new HashSet<>());
		this.okay = Collections.synchronizedSet(new HashSet<>());
		this.queue = Collections.synchronizedSet(new HashSet<>(typeIDs));
		this.updatedList.clear();

		if (priceSource == PriceSource.JANICE) {
			String janiceKey = Settings.get().getPriceDataSettings().getJaniceKey();
			if (janiceKey != null && !janiceKey.isEmpty()) {
				pricingOptions.addHeader("X-ApiKey", janiceKey);
			} else if (JANICE != null && !JANICE.isEmpty()) {
				pricingOptions.addHeader("X-ApiKey", JANICE);
			} else if (updateTask != null) {
				updateTask.addError("Price data", "No Janice API Key");
				return null;
			}
		}

		if (updateAll) {
			LOG.info("Price data update all (" + priceSource + "):");
		} else {
			LOG.info("Price data update new (" + priceSource + "):");
		}

		Pricing pricing = PricingFactory.getPricing(priceSource.getPricingFetch(), pricingOptions);

		pricing.addPricingListener(this);

		if (updateAll) { //Update all
			pricing.updatePrices(typeIDs);
		} else { //Update new
			for (int id : typeIDs) {
				createPriceData(id, pricing);
			}
		}

		while (!queue.isEmpty()) {
			try {
				synchronized (this) {
					wait(1000);
				}
			} catch (InterruptedException ex) {
				LOG.info("Failed to update price");
				pricing.cancelAll();
				if (updateTask != null) {
					updateTask.addWarning("Price data", "Cancelled");
					updateTask.setTaskProgress(100, 100, 0, 100);
					updateTask = null;
				}
				clear(pricing);
				return null;
			}
		}
		boolean updated = !okay.isEmpty() && typeIDs.size() * FAILED_PERCENT_CANCEL_LIMIT / 100 > failed.size();
		
		if (!failed.isEmpty()) {
			StringBuilder errorString = new StringBuilder();
			boolean first = true;
			synchronized (failed) {
				for (int typeID : failed) {
					if (first) {
						first = false;
					} else {
						errorString.append(", ");
					}
					errorString.append(typeID);
				}
			}
			LOG.error("Failed to update price data for the following typeIDs: " + errorString.toString());
			if (updated && updateTask != null) {
				updateTask.addError("Price data", "Failed to update price data for " + failed.size() + " of " + typeIDs.size() + " item types");
			}
		}
		if (!zero.isEmpty()) {
			StringBuilder errorString = new StringBuilder();
			synchronized (zero) {
				boolean first = true;
				for (int typeID : zero) {
					if (first) {
						first = false;
					} else {
						errorString.append(", ");
					}
					errorString.append(typeID);
				}
			}
			LOG.warn("Price data is zero for the following typeIDs: " + errorString.toString());
			if (updated && updateTask != null && typeIDs.size() * ZERO_PRICES_WARNING_LIMIT / 100 < zero.size()) {
				updateTask.addWarning("Price data", "Price data is zero for " + zero.size() + " of " + typeIDs.size() + " item types");
			}
		}
		if (updated) { //All Updated
			if (updateAll) {
				LOG.info("	Price data updated");
			} else {
				LOG.info("	Price data loaded");
			}
			try {
				pricing.writeCache();
				LOG.info("	Price data cached saved");
			} catch (IOException ex) {
				LOG.error("Failed to write price data cache", ex);
			}
			//We only set the price data if everthing worked (AKA all updated)
			try {
				//return new HashMap<Integer, PriceData>(priceDataList);
				// XXX - Workaround for ConcurrentModificationException in HashMap constructor
				Map<Integer, PriceData> hashMap = new HashMap<>();
				priceDataList.keySet().removeAll(failed); //Remove failed
				hashMap.putAll(priceDataList);
				PriceHistoryDatabase.setPriceData(updatedList);
				return hashMap;
			} finally {
				clear(pricing);
			}
		} else { //None or some updated
			LOG.info("	Failed to update price data");
			if (updateTask != null) {
				updateTask.addError("Price data", "Failed to update price data");
				updateTask.setTaskProgress(100, 100, 0, 100);
			}
			clear(pricing);
			return null;
		}
	}

	public synchronized Date getNextUpdate() {
		return new Date(nextUpdate + PRICE_CACHE_TIMER);
	}

	private synchronized long getNextUpdateTime() {
		return nextUpdate;
	}

	private synchronized void setUpdateNext(long nextUpdate) {
		this.nextUpdate = nextUpdate;
	}

	private void clear(Pricing pricing) {
		//Memory
		SplashUpdater.setSubProgress(100);
		this.updateTask = null;
		this.typeIDs.clear();
		this.failed.clear();
		pricing.removePricingListener(this);
	}

	@Override
	public void priceUpdated(final int typeID, final Pricing pricing) {
		createPriceData(typeID, pricing);
		queue.remove(typeID);
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void priceUpdateFailed(final int typeID, final Pricing pricing) {
		failed.add(typeID);
		queue.remove(typeID);
		synchronized (this) {
			notify();
		}
	}

	private void createPriceData(final int typeID, final Pricing pricing) {
		PriceData priceData = priceDataList.get(typeID);
		if (priceData == null) {
			priceData = new PriceData();
			priceDataList.put(typeID, priceData);
		}
		boolean ok = false;
		boolean isZero = true;
		for (PriceMode priceMode : PriceMode.values()) {
			PriceType priceType = priceMode.getPricingType();
			if (priceType == null) {
				continue; //Ignore calculated prices - f.ex. PriceMode.PRICE_MIDPOINT
			}
			Double price = pricing.getPrice(typeID, priceType);
			if (price != null) {
				ok = true; //Something is set
				PriceMode.setDefaultPrice(priceData, priceMode, price);
				if (price != 0) {
					isZero = false;
				}
			}
		}
		if (ok) {
			if (isZero) {
				zero.add(typeID);
			}
			updatedList.put(typeID, priceData);
			okay.add(typeID);
			failed.remove(typeID);
			queue.remove(typeID); //Load price...
		} else {
			failed.add(typeID);
		}
		long nextUpdateTemp = pricing.getNextUpdateTime(typeID);

		if (nextUpdateTemp >= 0 && nextUpdateTemp > getNextUpdateTime()) {
			setUpdateNext(nextUpdateTemp);
		}
		if (updateTask != null) {
			updateTask.setTaskProgress(typeIDs.size(), okay.size(), 0, 100);
		}
		if (!okay.isEmpty() && !typeIDs.isEmpty()) {
			SplashUpdater.setSubProgress((int) (okay.size() * 100.0 / typeIDs.size()));
		}
	}

	private class PlexPricingOptions extends DefaultPricingOptions {

		private final int globalPlexMarketRegionID;

		public PlexPricingOptions(int globalPlexMarketRegionID) {
			this.globalPlexMarketRegionID = globalPlexMarketRegionID;
		}

		@Override
		public PriceLocation getLocation() {
			return new PriceLocation() {
				@Override
				public long getRegionID() {
					return globalPlexMarketRegionID;
				}
				@Override
				public long getLocationID() {
					return globalPlexMarketRegionID;
				}
			};
		}

		@Override
		public LocationType getLocationType() {
			return LocationType.REGION;
		}
		
	}

	private class DefaultPricingOptions implements PricingOptions {

		@Override
		public long getPriceCacheTimer() {
			return PRICE_CACHE_TIMER;
		}

		@Override
		public LocationType getLocationType() {
			return Settings.get().getPriceDataSettings().getLocationType();
		}

		@Override
		public PriceLocation getLocation() {
			return ApiIdConverter.getLocation(Settings.get().getPriceDataSettings().getLocationID());
		}

		@Override
		public InputStream getCacheInputStream() throws IOException {
			File file = new File(FileUtil.getPathPriceData());
			if (file.exists()) {
				return new FileInputStream(file);
			}
			return null;
		}

		@Override
		public OutputStream getCacheOutputStream() throws IOException {
			return new FileOutputStream(FileUtil.getPathPriceData());
		}

		@Override
		public boolean getCacheTimersEnabled() {
			return update;
		}

		@Override
		public Proxy getProxy() {
			return null;
		}

		@Override
		public int getAttemptCount() {
			return ATTEMPT_COUNT;
		}

		@Override
		public boolean getUseBinaryErrorSearch() {
			return false;
		}

		@Override
		public int getTimeout() {
			return 20000;
		}

		@Override
		public String getUserAgent() {
			return Program.PROGRAM_USER_AGENT;
		}
	}
}
