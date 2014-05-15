/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import java.io.*;
import java.net.Proxy;
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
	private final int attemptCount = 5;
	private final Set<Integer> failed = new HashSet<Integer>();

	private UpdateTask updateTask;
	private Map<Integer, PriceData> priceDataList;
	private Set<Integer> typeIDs;
	private boolean update;
	private long nextUpdate = 0;

	public PriceDataGetter(final ProfileData profileData) {
		this.profileData = profileData;
	}
	/**
	 * Load price data from cache and only update missing price data.
	 * @return
	 */
	public boolean load() {
		return process(null, false);
	}
	/**
	 * Load price data from cache and only update missing price data.
	 * @param task UpdateTask to track progress
	 * @return
	 */
	public boolean load(final UpdateTask task) {
		return process(task, false);
	}
	/**
	 * Update of all price data.
	 * @param task UpdateTask to track progress
	 * @return
	 */

	public boolean update(final UpdateTask task) {
		return process(task, true);
	}

	private boolean process(final UpdateTask task, final boolean processUpdates) {
		Map<Integer, PriceData> priceData = process(task, processUpdates, new DefaultPricingOptions(), profileData.getPriceTypeIDs(), Settings.get().getPriceDataSettings().getSource());
		if (priceData != null) {
			Settings.get().setPriceData(priceData);
			return true;
		} else {
			return false;
		}
	}

	protected Map<Integer, PriceData> process(final UpdateTask task, final boolean processUpdate, final PricingOptions pricingOptions, final Set<Integer> typeIDs, final PriceSource priceSource) {
		this.updateTask = task;
		this.update = processUpdate;
		this.typeIDs = new HashSet<Integer>(typeIDs);
		//Create new price data map (Will only be used if task complete)
		priceDataList = new HashMap<Integer, PriceData>();

		if (processUpdate) {
			LOG.info("Price data update (" + priceSource + "):");
		} else {
			LOG.info("Price data loading (" + priceSource + "):");
		}

		Pricing pricing = PricingFactory.getPricing(pricingOptions);
		pricing.resetAllAttemptCounters();
		pricing.addPricingListener(this);

		//Reset cache timers...
		if (processUpdate) {
			for (int id : typeIDs) {
				pricing.setPrice(id, -1.0);
			}
		}

		//Load price data (Update as needed)
		for (int id : typeIDs) {
			createPriceData(id, pricing);
		}
		//Wait to complete
		failed.clear();
		try {
			while (typeIDs.size() > (priceDataList.size() + failed.size())) {
				try {
					synchronized (this) {
						wait();
						//System.out.println(priceDataList.size() + " of " + ids.size() + " done - " + fail.size() + " failed");
					}
				} catch (InterruptedException ex) {
					LOG.info("Failed to update price");
					pricing.cancelAll();
					if (updateTask != null) {
						updateTask.addError("Price data", "Cancelled");
						updateTask.setTaskProgress(100, 100, 0, 100);
						updateTask = null;
					}
					return null;
				}
			}
			if (!failed.isEmpty()) {
				StringBuilder errorString = new StringBuilder();
				for (int typeID : failed) {
					if (!errorString.toString().isEmpty()) {
						errorString.append(", ");
					}
					errorString.append(typeID);
				}
				LOG.error("Failed to update price data for the following typeIDs: " + errorString.toString());
				if (updateTask != null) {
					updateTask.addError("Price data", "Failed to update price data for " + failed.size() + " item types");
				}
			}
			boolean updated = (!priceDataList.isEmpty() && (typeIDs.size() * 5 / 100) > failed.size()); //
			if (updated) { //All Updated
				if (processUpdate) {
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
				return new HashMap<Integer, PriceData>(priceDataList);
			} else { //None or some updated
				LOG.info("	Failed to update price data");
				if (updateTask != null) {
					updateTask.addError("Price data", "Failed to update price data");
					updateTask.setTaskProgress(100, 100, 0, 100);
				}
				return null;
			}
		} finally {
			//Memory
			this.updateTask = null;
			this.typeIDs.clear();
			this.typeIDs = null;
			this.priceDataList.clear();
			this.priceDataList = null;
			this.failed.clear();
		}
	}

	public Date getNextUpdate() {
		return new Date(nextUpdate + priceCacheTimer);
	}

	@Override
	public void priceUpdated(final int typeID, final Pricing pricing) {
		createPriceData(typeID, pricing);
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void priceUpdateFailed(final int typeID, final Pricing pricing) {
		failed.add(typeID);
		synchronized (this) {
			notify();
		}
	}

	private void createPriceData(final int typeID, final Pricing pricing) {
		PriceData priceData = new PriceData();
		boolean ok = false;
		for (PriceMode priceMode : PriceMode.values()) {
			PricingType pricingType = priceMode.getPricingType();
			PricingNumber pricingNumber = priceMode.getPricingNumber();
			if (pricingNumber == null || pricingType == null) {
				continue; //Ignore calculated prices - f.ex. PriceMode.PRICE_MIDPOINT
			}
			Double price = pricing.getPrice(typeID, pricingType, pricingNumber);
			if (price != null) {
				ok = true;
			} else {
				price = 0.0;
			}
			PriceMode.setDefaultPrice(priceData, priceMode, price);
		}
		if (ok) {
			failed.remove(typeID);
			priceDataList.put(typeID, priceData);
		} else {
			failed.add(typeID);
		}
		long nextUpdateTemp = pricing.getNextUpdateTime(typeID);
		if (nextUpdateTemp >= 0 && nextUpdateTemp > nextUpdate) {
			nextUpdate = nextUpdateTemp;
		}
		if (updateTask != null) {
			updateTask.setTaskProgress(typeIDs.size(), priceDataList.size(), 0, 100);
		}
		if (!priceDataList.isEmpty() && !typeIDs.isEmpty()) {
			SplashUpdater.setSubProgress((int) (priceDataList.size() * 100.0 / typeIDs.size()));
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
	}
}
