/*
 * Copyright 2009-2015 Contributors (see credits.txt)
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.PriceDataSettings.PriceMode;
import net.nikr.eve.jeveasset.data.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.ProfileData;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.me.candle.eve.pricing.Pricing;
import uk.me.candle.eve.pricing.PricingFactory;
import uk.me.candle.eve.pricing.PricingListener;
import uk.me.candle.eve.pricing.options.LocationType;
import uk.me.candle.eve.pricing.options.PricingFetch;
import uk.me.candle.eve.pricing.options.PricingNumber;
import uk.me.candle.eve.pricing.options.PricingOptions;
import uk.me.candle.eve.pricing.options.PricingType;


public class PriceDataGetter implements PricingListener {

	private static final Logger LOG = LoggerFactory.getLogger(PriceDataGetter.class);

	private final ProfileData profileData;
	private final long priceCacheTimer = 1 * 60 * 60 * 1000L; // 1 hour (hours*min*sec*ms)
	private final int attemptCount = 2;

	private UpdateTask updateTask;
	private boolean update;
	private Set<Integer> typeIDs;
	private Set<Integer> failed;
	private Set<Integer> okay;
	private Set<Integer> queue;
	private Map<Integer, PriceData> priceDataList;
	
	private long nextUpdate = 0;

	public PriceDataGetter(final ProfileData profileData) {
		this.profileData = profileData;
	}

	/**
	 * Load price data from cache. Don't update missing prices
	 * @return
	 */
	public boolean load() {
		Map<Integer, PriceData> priceData = processLoad(profileData.getPriceTypeIDs());
		if (priceData != null) {
			Settings.get().setPriceData(priceData);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Load price data from cache and only update missing price data.
	 * @param task UpdateTask to track progress
	 * @return
	 */
	public boolean updateNew(final UpdateTask task) {
		return processUpdate(task, false);
	}

	/**
	 * Update of all price data.
	 * @param task UpdateTask to track progress
	 * @return
	 */
	public boolean updateAll(final UpdateTask task) {
		return processUpdate(task, true);
	}

	/**
	 * Load data from price cache
	 * @param typeIDs typeIDs to load from cache
	 * @return available price data
	 */
	protected Map<Integer, PriceData> processLoad(Set<Integer> typeIDs) {
		Pricing pricing = PricingFactory.getPricing(new DefaultPricingOptions());
		LOG.info("Price data loading");
		priceDataList = new HashMap<Integer, PriceData>();
		for (int typeID : typeIDs) { //For each typeID
			PriceData priceData = priceDataList.get(typeID);
			if (priceData == null) {
				priceData = new PriceData();
				priceDataList.put(typeID, priceData);
			}
			boolean ok = false;
			for (PriceMode priceMode : PriceMode.values()) { //For each PriceMode (all combinations of PricingNumber & PricingType)
				PricingType pricingType = priceMode.getPricingType();
				PricingNumber pricingNumber = priceMode.getPricingNumber();
				if (pricingNumber == null || pricingType == null) {
					continue; //Ignore calculated prices - f.ex. PriceMode.PRICE_MIDPOINT
				}
				Double price = pricing.getPriceCache(typeID, pricingType, pricingNumber);
				if (price != null) {
					ok = true; //Something is set
					PriceMode.setDefaultPrice(priceData, priceMode, price);
				}
			}
			if (!ok) {
				priceDataList.remove(typeID); //Remove failed typeID
			}
			long nextUpdateTemp = pricing.getNextUpdateTime(typeID);
			if (nextUpdateTemp >= 0 && nextUpdateTemp > nextUpdate) {
				nextUpdate = nextUpdateTemp;
			}
		}
		if (!priceDataList.isEmpty()) {
			LOG.info("	Price data loaded");
			Map<Integer, PriceData> hashMap = new HashMap<Integer, PriceData>();
			hashMap.putAll(priceDataList);
			priceDataList.clear(); //Free memory
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
	private boolean processUpdate(final UpdateTask task, final boolean updateAll) {
		Map<Integer, PriceData> priceData = processUpdate(task, updateAll, new DefaultPricingOptions(), profileData.getPriceTypeIDs(), Settings.get().getPriceDataSettings().getSource());
		if (priceData != null) {
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
		this.typeIDs =  Collections.synchronizedSet(new HashSet<Integer>(typeIDs));
		this.failed = Collections.synchronizedSet(new HashSet<Integer>());
		this.okay = Collections.synchronizedSet(new HashSet<Integer>());
		this.queue = Collections.synchronizedSet(new HashSet<Integer>(typeIDs));
		this.priceDataList = Collections.synchronizedMap(new HashMap<Integer, PriceData>());

		if (updateAll) {
			LOG.info("Price data update all (" + priceSource + "):");
		} else {
			LOG.info("Price data update new (" + priceSource + "):");
		}

		Pricing pricing = PricingFactory.getPricing(pricingOptions);

		//Reset cache timers...
		if (updateAll) {
			for (int id : typeIDs) {
				pricing.setPrice(id, -1.0);
			}
		}
		pricing.addPricingListener(this);

		//Load price data (Update as needed)
		for (int id : typeIDs) {
			createPriceData(id, pricing);
		}
		while (!queue.isEmpty()) {
			try {
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException ex) {
				LOG.info("Failed to update price");
				pricing.cancelAll();
				if (updateTask != null) {
					updateTask.addError("Price data", "Cancelled");
					updateTask.setTaskProgress(100, 100, 0, 100);
					updateTask = null;
				}
				clear(pricing);
				return null;
			}
		}
		if (!failed.isEmpty()) {
			StringBuilder errorString = new StringBuilder();
			synchronized (failed) {
				for (int typeID : failed) {
					if (!errorString.toString().isEmpty()) {
						errorString.append(", ");
					}
					errorString.append(typeID);
				}
			}
			LOG.error("Failed to update price data for the following typeIDs: " + errorString.toString());
			if (updateTask != null) {
				updateTask.addError("Price data", "Failed to update price data for " + failed.size() + " item types");
			}
		}
		boolean updated = (!priceDataList.isEmpty() && (typeIDs.size() * 5 / 100) > failed.size()); //
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
				Map<Integer, PriceData> hashMap = new HashMap<Integer, PriceData>();
				priceDataList.keySet().removeAll(failed);
				hashMap.putAll(priceDataList);
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

	public Date getNextUpdate() {
		return new Date(nextUpdate + priceCacheTimer);
	}

	private void clear(Pricing pricing) {
		//Memory
		SplashUpdater.setSubProgress(100);
		this.updateTask = null;
		this.typeIDs.clear();
		this.priceDataList.clear();
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
		for (PriceMode priceMode : PriceMode.values()) {
			PricingType pricingType = priceMode.getPricingType();
			PricingNumber pricingNumber = priceMode.getPricingNumber();
			if (pricingNumber == null || pricingType == null) {
				continue; //Ignore calculated prices - f.ex. PriceMode.PRICE_MIDPOINT
			}
			Double price = pricing.getPrice(typeID, pricingType, pricingNumber);
			if (price != null) {
				ok = true; //Something is set
				PriceMode.setDefaultPrice(priceData, priceMode, price);
			}
		}
		if (ok) {
			okay.add(typeID);
			failed.remove(typeID);
			queue.remove(typeID); //Load price...
		} else {
			failed.add(typeID);
		}
		long nextUpdateTemp = pricing.getNextUpdateTime(typeID);
		if (nextUpdateTemp >= 0 && nextUpdateTemp > nextUpdate) {
			nextUpdate = nextUpdateTemp;
		}
		if (updateTask != null) {
			updateTask.setTaskProgress(typeIDs.size(), okay.size(), 0, 100);
		}
		if (!okay.isEmpty() && !typeIDs.isEmpty()) {
			SplashUpdater.setSubProgress((int) (okay.size() * 100.0 / typeIDs.size()));
		}
	}

	private class DefaultPricingOptions implements PricingOptions {

		@Override
		public long getPriceCacheTimer() {
			return priceCacheTimer;
		}

		@Override
		public PricingFetch getPricingFetchImplementation() {
			return Settings.get().getPriceDataSettings().getSource().getPricingFetch();
		}

		@Override
		public LocationType getLocationType() {
			return Settings.get().getPriceDataSettings().getLocationType();
		}

		@Override
		public List<Long> getLocations() {
			return Settings.get().getPriceDataSettings().getLocations();
		}

		@Override
		public PricingType getPricingType() {
			return PricingType.LOW;
		}

		@Override
		public PricingNumber getPricingNumber() {
			return PricingNumber.SELL;
		}

		@Override
		public InputStream getCacheInputStream() throws IOException {
			File file = new File(Settings.getPathPriceData());
			if (file.exists()) {
				return new FileInputStream(file);
			}
			return null;
		}

		@Override
		public OutputStream getCacheOutputStream() throws IOException {
			return new FileOutputStream(Settings.getPathPriceData());
		}

		@Override
		public boolean getCacheTimersEnabled() {
			return update;
		}

		@Override
		public Proxy getProxy() {
			return Settings.get().getProxy();
		}

		@Override
		public int getAttemptCount() {
			return attemptCount;
		}

		@Override
		public boolean getUseBinaryErrorSearch() {
			return false;
		}
	}
}
