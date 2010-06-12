/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.me.candle.eve.pricing.Pricing;
import uk.me.candle.eve.pricing.PricingFactory;
import uk.me.candle.eve.pricing.PricingListener;
import uk.me.candle.eve.pricing.options.PricingNumber;
import uk.me.candle.eve.pricing.options.PricingOptions;
import uk.me.candle.eve.pricing.options.PricingType;


public class PriceDataGetter implements PricingListener {

	private final static Logger LOG = LoggerFactory.getLogger(PriceDataGetter.class);

	private Settings settings;
	private UpdateTask updateTask;
	private boolean updated;
	private long nextUpdate = 0;
	private long priceCacheTimer = 60*60*1000l; // 1 hour
	private boolean enableCacheTimers = true;
	private Map<Integer, PriceData> priceDataList;

	public PriceDataGetter(Settings settings) {
		this.settings = settings;
	}

	public boolean load(UpdateTask task, boolean forceUpdate, boolean enableCacheTimers){
		this.updateTask = task;
		this.enableCacheTimers = enableCacheTimers;
		updated = false;

		if (forceUpdate){
			LOG.info("Price data updating:");
		} else if (!enableCacheTimers) {
			LOG.info("Price data loading:");
		} else {
			LOG.info("Price data loading (updating as needed):");
		}
		//Create new price data map (Will only be used if task complete)
		priceDataList = new HashMap<Integer, PriceData>();

		//Get all price ids
		List<Integer> ids = settings.getUniqueIds();

		PricingFactory.setPricingOptions( new EveAssetPricingOptions() );
		Pricing pricing = PricingFactory.getPricing();
		pricing.addPricingListener(this);
		//Reset cache timers...
		if (forceUpdate){
			for (int a = 0; a < ids.size(); a++){
				pricing.setPrice(ids.get(a), -1.0);
			}
		}
		
		//Load price data (Update as needed)
		for (int a = 0; a < ids.size(); a++){
			createPriceData(ids.get(a), pricing);
		}
		//Wait to complete
		while (settings.getUniqueIds().size() >  priceDataList.size()){
			try {
				synchronized(this) {
                    wait();
                }
			} catch (InterruptedException ex) {
				LOG.info("Failed to update price");
				this.updateTask.addError("Price data", "Cancelled");
				this.updateTask.setTaskProgress(100, 100, 0, 100);
				this.updateTask = null;
				return false;
			}
		}
		settings.setPriceData( priceDataList );
		if (!enableCacheTimers && updated){
			LOG.info("	Price data loaded (updated as needed)");
		} else if (!enableCacheTimers) {
			LOG.info("	Price data loaded");
		} else if (updated){
			LOG.info("	Price data updated");
		}
		try {
			pricing.writeCache();
			LOG.info("	Price data cached saved");
		} catch (IOException ex) {
			LOG.error("Failed to write price data cache", ex);
		}
		return updated;
	}

	public Date getNextUpdate() {
		return Settings.getGmt( new Date(nextUpdate+priceCacheTimer) );
	}

	@Override
	public void priceUpdated(int typeID, Pricing pricing) {
		createPriceData(typeID, pricing);
		updated = true;
		synchronized(this) {
			notify();
		}
	}

	private void createPriceData(int typeID, Pricing pricing){
		Double sellMax = pricing.getPrice(typeID, PricingType.HIGH, PricingNumber.SELL);
		Double sellAvg = pricing.getPrice(typeID, PricingType.MEAN, PricingNumber.SELL);
		Double sellMedian = pricing.getPrice(typeID, PricingType.MEDIAN, PricingNumber.SELL);
		Double sellMin = pricing.getPrice(typeID, PricingType.LOW, PricingNumber.SELL);
		Double buyMax = pricing.getPrice(typeID, PricingType.HIGH, PricingNumber.BUY);
		Double buyAvg = pricing.getPrice(typeID, PricingType.MEAN, PricingNumber.BUY);
		Double buyMedian = pricing.getPrice(typeID, PricingType.MEDIAN, PricingNumber.BUY);
		Double buyMin = pricing.getPrice(typeID, PricingType.LOW, PricingNumber.BUY);

		if (sellMax != null && sellAvg != null && sellMedian != null && sellMin != null
			&& buyMax != null && buyAvg != null && buyMedian != null && buyMin != null
				){
			PriceData priceData = new PriceData();
			priceData.setSellMax(sellMax);
			priceData.setSellAvg(sellAvg);
			priceData.setSellMedian(sellMedian);
			priceData.setSellMin(sellMin);
			priceData.setBuyMax(buyMax);
			priceData.setBuyAvg(buyAvg);
			priceData.setBuyMedian(buyMedian);
			priceData.setBuyMin(buyMin);
			priceDataList.put(typeID, priceData);
		}
		long nextUpdateTemp = pricing.getNextUpdateTime(typeID);
		if (nextUpdateTemp >= 0 && nextUpdateTemp > nextUpdate ){
			nextUpdate = nextUpdateTemp;
		}
		if (updateTask != null) updateTask.setTaskProgress(settings.getUniqueIds().size(), priceDataList.size(), 0, 100);
	}

	private class EveAssetPricingOptions implements PricingOptions {

		@Override
		public long getPriceCacheTimer() {
			return priceCacheTimer; // 1 hour
		}

		@Override
		public String getPricingFetchImplementation() {
			return settings.getPriceDataSettings().getSource();
		}

		@Override
		public List<Long> getRegions() {
			return settings.getPriceDataSettings().getRegions();
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
			if (file.exists()){
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
			return enableCacheTimers;
		}

		@Override
		public Proxy getProxy() {
			return settings.getProxy();
		}
	}
}
