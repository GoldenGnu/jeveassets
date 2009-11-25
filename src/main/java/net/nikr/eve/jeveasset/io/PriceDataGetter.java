/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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


package net.nikr.eve.jeveasset.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.UpdateTask;
import net.nikr.log.Log;
import uk.me.candle.eve.pricing.Pricing;
import uk.me.candle.eve.pricing.PricingFactory;
import uk.me.candle.eve.pricing.PricingListener;
import uk.me.candle.eve.pricing.options.PricingNumber;
import uk.me.candle.eve.pricing.options.PricingOptions;
import uk.me.candle.eve.pricing.options.PricingType;


public class PriceDataGetter implements PricingListener {

	private Settings settings;
	private int progress;
	private UpdateTask task;
	private boolean updated;
	private long nextUpdate = 0;
	private long priceCacheTimer = 60*60*1000l; // 1 hour
	private boolean enableCacheTimers = true;

	final PriceDataGetter lock = this;

	public PriceDataGetter(Settings settings) {
		this.settings = settings;
		updatePriceData(null, false, false);
	}

	public boolean updatePriceData(){
		return updatePriceData(null);
	}

	public boolean updatePriceData(UpdateTask task){
		return updatePriceData(null, false, true);
	}

	public boolean updatePriceData(boolean forceUpdate){
		return updatePriceData(null, forceUpdate, true);
	}

	public boolean updatePriceData(UpdateTask task, boolean forceUpdate){
		return updatePriceData(null, forceUpdate, true);
	}

	public boolean updatePriceData(UpdateTask task, boolean forceUpdate, boolean enableCacheTimers){
		this.task = task;
		this.enableCacheTimers = enableCacheTimers;
		if (task != null) progress = task.getProgress();
		updated = false;

		if (enableCacheTimers) Log.info("Updating price data...");
		PricingFactory.setPricingOptions( new EveAssetPricingOptions() );
		Pricing pricing = PricingFactory.getPricing();
		pricing.addPricingListener(this);
		
		//Reset price data
		settings.setPriceData( new HashMap<Integer, PriceData>() );

		//Get all price ids
		List<Integer> ids = settings.getUniqueIds();

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
		while (settings.getUniqueIds().size() !=  settings.getPriceData().size()){
			try {
				synchronized(this) {
                    wait();
                }
			} catch (InterruptedException ex) {
				Log.error("Failed to update price", ex);
			}
		}
		if (enableCacheTimers){
			Log.info("	Price data updated");
			try {
				pricing.writeCache();
				Log.info("	Price data cached saved");
			} catch (IOException ex) {
				Log.error("Failed to write price data cache", ex);
			}
		} else {
			Log.info("Price data loaded");
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
		//Log.info("	"+settings.getPriceData().size()+" of "+settings.getUniqueIds().size()+" done");
		Double buyAvg = pricing.getPrice(typeID, PricingType.MEAN, PricingNumber.BUY);
		Double buyMax = pricing.getPrice(typeID, PricingType.HIGH, PricingNumber.BUY);
		Double buyMin = pricing.getPrice(typeID, PricingType.LOW, PricingNumber.BUY);
		Double buyMedian = pricing.getPrice(typeID, PricingType.MEDIAN, PricingNumber.BUY);
		Double sellAvg = pricing.getPrice(typeID, PricingType.MEAN, PricingNumber.SELL);
		Double sellMax = pricing.getPrice(typeID, PricingType.HIGH, PricingNumber.SELL);
		Double sellMin = pricing.getPrice(typeID, PricingType.LOW, PricingNumber.SELL);
		Double sellMedian = pricing.getPrice(typeID, PricingType.MEDIAN, PricingNumber.SELL);

		if (buyAvg != null
				&& buyMax != null
				&& buyMin != null
				&& buyMedian != null
				&& sellAvg != null
				&& sellMax != null
				&& sellMin != null
				&& sellMedian != null){
			PriceData priceData = new PriceData();
			priceData.setBuyAvg(buyAvg);
			priceData.setBuyMax(buyMax);
			priceData.setBuyMin(buyMin);
			priceData.setBuyMedian(buyMedian);
			priceData.setSellAvg(sellAvg);
			priceData.setSellMax(sellMax);
			priceData.setSellMin(sellMin);
			priceData.setSellMedian(sellMedian);
			priceData.setBuyMax(buyMax);
			settings.getPriceData().put(typeID, priceData);
		}
		long nextUpdateTemp = pricing.getNextUpdateTime(typeID);
		if (nextUpdateTemp >= 0 && nextUpdateTemp > nextUpdate ){
			nextUpdate = nextUpdateTemp;
		}

		if (task != null) task.setTaskProgress(settings.getUniqueIds().size(), settings.getPriceData().size(), progress, 100);
	}

	class EveAssetPricingOptions implements PricingOptions {

		@Override
		public long getPriceCacheTimer() {
			return priceCacheTimer; // 1 hour
		}

		@Override
		public String getPricingFetchImplementation() {
			//return "eve-metrics";
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

	}



}
