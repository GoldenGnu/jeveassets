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
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.ProfileData;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.me.candle.eve.pricing.Pricing;
import uk.me.candle.eve.pricing.PricingFactory;
import uk.me.candle.eve.pricing.PricingListener;
import uk.me.candle.eve.pricing.options.LocationType;
import uk.me.candle.eve.pricing.options.PricingNumber;
import uk.me.candle.eve.pricing.options.PricingOptions;
import uk.me.candle.eve.pricing.options.PricingType;


public class PriceDataGetter implements PricingListener {

	private static final Logger LOG = LoggerFactory.getLogger(PriceDataGetter.class);

	private ProfileData profileData;
	private UpdateTask updateTask;
	private long nextUpdate = 0;
	private long priceCacheTimer = 1 * 60 * 60 * 1000L; // 1 hour (hours*min*sec*ms)
	private Map<Integer, PriceData> priceDataList;
	private final int attemptCount = 5;
	private boolean update;
	private boolean failed;
	private Set<Integer> ids;

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

	private boolean process(final UpdateTask task, final boolean processUpdate) {
		this.updateTask = task;
		this.update = processUpdate;

		if (processUpdate) {
			LOG.info("Price data update (" + Settings.get().getPriceDataSettings().getSource() + "):");
		} else {
			LOG.info("Price data loading (" + Settings.get().getPriceDataSettings().getSource() + "):");
		}
		//Create new price data map (Will only be used if task complete)
		priceDataList = new HashMap<Integer, PriceData>();
		failed = false;

		//Get all price ids
		ids = profileData.getPriceTypeIDs();

		PricingFactory.setPricingOptions(new DefaultPricingOptions());
		Pricing pricing = PricingFactory.getPricing();
		pricing.addPricingListener(this);
		pricing.resetAllAttemptCounters();
		//Reset cache timers...
		if (processUpdate) {
			for (int id : ids) {
				pricing.setPrice(id, -1.0);
			}
		}

		//Load price data (Update as needed)
		for (int id : ids) {
			createPriceData(id, pricing);
		}
		//Wait to complete
		while (ids.size() > priceDataList.size() && !failed) {
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

				return false;
			}
		}
		boolean updated = (!priceDataList.isEmpty() && !failed);
		if (updated) { //All Updated
			if (processUpdate) {
				LOG.info("	Price data updated");
			} else {
				LOG.info("	Price data loaded");
			}
			//We only set the price data if everthing worked (AKA all updated)
			Settings.get().setPriceData(priceDataList);
			try {
				pricing.writeCache();
				LOG.info("	Price data cached saved");
			} catch (IOException ex) {
				LOG.error("Failed to write price data cache", ex);
			}
		} else { //None or some updated
			LOG.info("	Failed to update price data");
			if (updateTask != null) {
				updateTask.addError("Price data", "Failed to update price data");
				updateTask.setTaskProgress(100, 100, 0, 100);
			}
		}
		updateTask = null; //Memory
		return updated;
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
		pricing.cancelAll();
		failed = true;
		synchronized (this) {
			notify();
		}
	}

	private void createPriceData(final int typeID, final Pricing pricing) {
		Double sellMax = pricing.getPrice(typeID, PricingType.HIGH, PricingNumber.SELL);
		Double sellAvg = pricing.getPrice(typeID, PricingType.MEAN, PricingNumber.SELL);
		Double sellMedian = pricing.getPrice(typeID, PricingType.MEDIAN, PricingNumber.SELL);
		Double sellPercentile = pricing.getPrice(typeID, PricingType.PERCENTILE, PricingNumber.SELL);
		Double sellMin = pricing.getPrice(typeID, PricingType.LOW, PricingNumber.SELL);
		Double buyMax = pricing.getPrice(typeID, PricingType.HIGH, PricingNumber.BUY);
		Double buyAvg = pricing.getPrice(typeID, PricingType.MEAN, PricingNumber.BUY);
		Double buyMedian = pricing.getPrice(typeID, PricingType.MEDIAN, PricingNumber.BUY);
		Double buyPercentile = pricing.getPrice(typeID, PricingType.PERCENTILE, PricingNumber.BUY);
		Double buyMin = pricing.getPrice(typeID, PricingType.LOW, PricingNumber.BUY);

		if (sellMax != null && sellAvg != null && sellMedian != null && sellPercentile != null && sellMin != null
			&& buyMax != null && buyAvg != null && buyMedian != null && buyPercentile != null && buyMin != null
				) {
			PriceData priceData = new PriceData();
			priceData.setSellMax(sellMax);
			priceData.setSellAvg(sellAvg);
			priceData.setSellMedian(sellMedian);
			priceData.setSellPercentile(sellPercentile);
			priceData.setSellMin(sellMin);
			priceData.setBuyMax(buyMax);
			priceData.setBuyAvg(buyAvg);
			priceData.setBuyMedian(buyMedian);
			priceData.setBuyPercentile(buyPercentile);
			priceData.setBuyMin(buyMin);
			priceDataList.put(typeID, priceData);
		}
		long nextUpdateTemp = pricing.getNextUpdateTime(typeID);
		if (nextUpdateTemp >= 0 && nextUpdateTemp > nextUpdate) {
			nextUpdate = nextUpdateTemp;
		}
		if (updateTask != null) {
			updateTask.setTaskProgress(ids.size(), priceDataList.size(), 0, 100);
		}
		if (!priceDataList.isEmpty() && !ids.isEmpty()) {
			SplashUpdater.setSubProgress((int) (priceDataList.size() * 100.0 / ids.size()));
		}
	}

	private class DefaultPricingOptions implements PricingOptions {

		@Override
		public long getPriceCacheTimer() {
			return priceCacheTimer;
		}

		@Override
		public String getPricingFetchImplementation() {
			return Settings.get().getPriceDataSettings().getSource().getName();
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
