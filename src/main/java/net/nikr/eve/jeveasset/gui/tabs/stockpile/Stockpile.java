/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class Stockpile implements Comparable<Stockpile> {
	private String name;
	private long characterID;
	private long locationID;
	private String location;
	private String system;
	private String region;
	private int flagID;
	private String container;
	private boolean inventory;
	private boolean sellOrders;
	private boolean buyOrders;
	private boolean jobs;
	private final List<StockpileItem> items = new ArrayList<StockpileItem>();
	private final StockpileTotal totalItem = new StockpileTotal(this);
	private boolean expanded = true;

	private Stockpile(Stockpile stockpile) {
		update(stockpile);
		for (StockpileItem item : stockpile.getItems()){
			if (item.getTypeID() > 0){ //Ignore Total
				items.add( new StockpileItem(this, item) );
			}
		}
		items.add(totalItem);
	}

	public Stockpile(String name, long characterID, long locationID, String location, String system, String region, int flagID, String container, boolean inventory, boolean sellOrders, boolean buyOrders, boolean jobs) {
		this.name = name;
		this.characterID = characterID;
		this.locationID = locationID;
		this.location = location;
		this.system = system;
		this.region = region;
		this.flagID = flagID;
		this.container = container;
		this.inventory = inventory;
		this.sellOrders = sellOrders;
		this.buyOrders = buyOrders;
		this.jobs = jobs;
		items.add(totalItem);
	}
	
	final void update(Stockpile stockpile) {
		this.name = stockpile.getName();
		this.characterID = stockpile.getCharacterID();
		this.locationID = stockpile.getLocationID();
		this.location = stockpile.getLocation();
		this.system = stockpile.getSystem();
		this.region = stockpile.getRegion();
		this.flagID = stockpile.getFlagID();
		this.container = stockpile.getContainer();
		this.inventory = stockpile.isInventory();
		this.sellOrders = stockpile.isSellOrders();
		this.buyOrders = stockpile.isBuyOrders();
		this.jobs = stockpile.isJobs();
	}
	
	public boolean isOK(){
		return totalItem.isOK();
	}
	
	public boolean isHalf(){
		return totalItem.isHalf();
	}
	
	public boolean isEmpty(){
		return (items.size() <= 1);
	}
	
	public void add(StockpileItem item){
		items.add(item);
		Collections.sort(items);
	}
	
	public void remove(StockpileItem item) {
		if (items.contains(item)){
			items.remove(item);
		}
		if (items.isEmpty()) items.add(totalItem);
	}
	
	public void reset(){
		for (StockpileItem item : items){
			item.reset();
		}
	}

	public String getName() {
		return name;
	}

	public boolean isBuyOrders() {
		return buyOrders;
	}

	public boolean isInventory() {
		return inventory;
	}

	public boolean isJobs() {
		return jobs;
	}

	public boolean isSellOrders() {
		return sellOrders;
	}

	public long getCharacterID() {
		return characterID;
	}

	public String getContainer() {
		return container;
	}

	public int getFlagID() {
		return flagID;
	}

	public long getLocationID() {
		return locationID;
	}

	public String getLocation() {
		return location;
	}

	public String getRegion() {
		return region;
	}

	public String getSystem() {
		return system;
	}

	public List<StockpileItem> getItems() {
		return items;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	public void updateTotal() {
		totalItem.reset();
		items.remove(totalItem);
		for (StockpileItem item : items){
			totalItem.updateTotal(item);
		}
		items.add(totalItem);
		
	}

	public StockpileTotal getTotal() {
		return totalItem;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Stockpile other = (Stockpile) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public Stockpile clone() {
		return new Stockpile(this);
	}

	@Override
	public int compareTo(Stockpile o) {
		return this.getName().compareTo(o.getName());
	}
	
	public static class StockpileItem implements Comparable<StockpileItem> {
		//Constructor
		private Stockpile stockpile;
		private String name;
		private String group;
		private int typeID;
		private long countMinimum;

		//Updated values
		private boolean marketGroup;
		private double price = 0.0;
		private double volume = 0.0f;
		
		//Updated counts
		private long inventoryCountNow = 0;
		private long sellOrdersCountNow = 0;
		private long buyOrdersCountNow = 0;
		private long jobsCountNow = 0;
		
		
		public StockpileItem(Stockpile stockpile, StockpileItem stockpileItem) {
			this(	stockpile,
					stockpileItem.getName(),
					stockpileItem.getGroup(),
					stockpileItem.getTypeID(),
					stockpileItem.getCountMinimum()
					);
		}

		public StockpileItem(Stockpile stockpile, String name, String group, int typeID, long countMinimum) {
			this.stockpile = stockpile;
			this.name = name;
			this.group = group;
			this.typeID = typeID;
			this.countMinimum = countMinimum;
		}
		
		public boolean isOK() {
			return getCountNeeded() >= 0;
		}
		
		public boolean isHalf() {
			return getCountNow() >= (getCountMinimum() / 2.0) ;
		}
		
		private void reset(){
			inventoryCountNow = 0;
			sellOrdersCountNow = 0;
			buyOrdersCountNow = 0;
			jobsCountNow = 0;
			price = 0.0;
			volume = 0.0f;
			marketGroup = false;
		}
		public void updateValues(double price, float volume, boolean marketGroup){
			this.price = price;
			this.volume = volume;
			this.marketGroup = marketGroup;
		}
		
		public void updateAsset(Asset asset,  ItemFlag itemFlag, Long characterID, Long regionID){
			if (asset != null && itemFlag != null && characterID != null && regionID != null //better save then sorry
					&& typeID == asset.getTypeID()
					&& (stockpile.getCharacterID() == characterID || stockpile.getCharacterID() < 0)
					&& (asset.getContainer().contains(stockpile.getContainer()) || stockpile.getContainer().equals(TabsStockpile.get().all()))
					&& (stockpile.getFlagID() == itemFlag.getFlagID() || stockpile.getFlagID() < 0)
					&& ((stockpile.getLocation() != null
					&& stockpile.getLocation().equals(asset.getLocation())) //LocationID can be an office...
					|| stockpile.getLocationID() == asset.getSolarSystemID()
					|| stockpile.getLocationID() == regionID
					|| stockpile.getLocationID() < 0)
					){
				inventoryCountNow = inventoryCountNow + asset.getCount();
			}
		}
		
		void updateMarketOrder(ApiMarketOrder marketOrder, Long characterID, Location location) {
			if (marketOrder != null && characterID != null && location != null //better save then sorry
					&& typeID == marketOrder.getTypeID()
					&& (stockpile.getCharacterID() == characterID || stockpile.getCharacterID() < 0)
					&& (stockpile.getLocationID() == location.getLocationID()
					|| stockpile.getLocationID() == location.getSystemID()
					|| stockpile.getLocationID() == location.getRegionID()
					|| stockpile.getLocationID() < 0)
					){
				if (marketOrder.getBid() < 1){ //Sell
					if (stockpile.isSellOrders()) sellOrdersCountNow = sellOrdersCountNow + marketOrder.getVolRemaining();
				} else { //Buy
					if (stockpile.isBuyOrders())  buyOrdersCountNow = buyOrdersCountNow + marketOrder.getVolRemaining();
				}
			}
		}
		
		void updateIndustryJob(ApiIndustryJob industryJob, ItemFlag itemFlag, Long characterID, Location location, Item itemType) {
			if (industryJob != null && itemFlag != null && characterID != null && location != null && itemType != null //better save then sorry
					&& typeID == industryJob.getOutputTypeID() //Produced only
					&& (stockpile.getCharacterID() == characterID || stockpile.getCharacterID() < 0)
					&& (stockpile.getFlagID() == itemFlag.getFlagID() || stockpile.getFlagID() < 0)
					&& (stockpile.getLocationID() == location.getLocationID()
					|| stockpile.getLocationID() == location.getSystemID()
					|| stockpile.getLocationID() == location.getRegionID()
					|| stockpile.getLocationID() < 0)
					&& industryJob.getActivityID() == 1 //Manufacturing
					&& industryJob.getCompletedStatus() == 0 //Inprogress AKA not delivered
					){
				jobsCountNow = jobsCountNow + (industryJob.getRuns() * itemType.getPortion());
			}
		}

		public String getGroup() {
			return group;
		}

		public void setCountMinimum(long countMinimum) {
			this.countMinimum = countMinimum;
			this.getStockpile().updateTotal();
		}

		public String getSeperator() {
			return stockpile.getName();
		}

		public Stockpile getStockpile() {
			return stockpile;
		}

		public String getName() {
			return name;
		}

		public long getCountMinimum() {
			return countMinimum;
		}

		public long getCountNow() {
			return inventoryCountNow + buyOrdersCountNow + jobsCountNow + sellOrdersCountNow;
		}
		
		public long getInventoryCountNow() {
			return inventoryCountNow;
		}

		public long getBuyOrdersCountNow() {
			return buyOrdersCountNow;
		}

		public long getJobsCountNow() {
			return jobsCountNow;
		}

		public long getSellOrdersCountNow() {
			return sellOrdersCountNow;
		}
		
		public long getCountNeeded() {
			return getCountNow() - countMinimum;
		}

		public double getPrice() {
			return price;
		}

		public int getTypeID() {
			return typeID;
		}

		public double getVolume() {
			return volume;
		}

		public double getValueNow() {
			return getCountNow() * price;
		}

		public double getValueNeeded() {
			return getCountNeeded() * price;
		}

		public double getVolumeNow() {
			return getCountNow() * volume;
		}

		public double getVolumeNeeded() {
			return getCountNeeded() * volume;
		}
		
		public boolean isMarketGroup() {
			return marketGroup;
		}
		
		@Override
		public String toString(){
			return getName();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final StockpileItem other = (StockpileItem) obj;
			if (this.typeID != other.typeID) {
				return false;
			}
			if (this.stockpile != other.stockpile && (this.stockpile == null || !this.stockpile.equals(other.stockpile))) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 29 * hash + this.typeID;
			hash = 29 * hash + (this.stockpile != null ? this.stockpile.hashCode() : 0);
			return hash;
		}

		@Override
		public int compareTo(StockpileItem item) {
			//Total should always be last...
			if (item instanceof StockpileTotal) return -1; //this is Before item 
			if (this instanceof StockpileTotal) return 1; //this is After item
			//Compare groups
			int value = this.getGroup().compareTo(item.getGroup());
			if (value != 0){ //Not same group
				return value;
			} else { //Same group - compare names
				return this.getName().compareTo(item.getName());
			}
		}
	}
	
	public static class StockpileTotal extends StockpileItem{

		private boolean ok = true;
		private boolean half = true;
		private long inventoryCountNow = 0;
		private long sellOrdersCountNow = 0;
		private long buyOrdersCountNow = 0;
		private long jobsCountNow = 0;
		private long countNeeded = 0;
		private long countMinimum = 0;
		private double totalPrice;
		private double totalPriceCount;
		private double valueNow = 0;
		private double valueNeeded = 0;
		private double volumeNow = 0;
		private double volumeNeeded = 0;
		
		public StockpileTotal(Stockpile stockpile) {
			super(stockpile, TabsStockpile.get().totalStockpile(), "", 0, 0);
		}
		
		private void reset(){
			ok = true;
			half = true;
			inventoryCountNow = 0;
			sellOrdersCountNow = 0;
			buyOrdersCountNow = 0;
			jobsCountNow = 0;
			countNeeded = 0;
			countMinimum = 0;
			totalPrice = 0;
			totalPriceCount = 0;
			valueNow = 0;
			valueNeeded = 0;
			volumeNow = 0;
			volumeNeeded = 0;
		}
		
		private void updateTotal(StockpileItem item){
			if (!item.isOK()) ok = false;
			if (!item.isHalf()) half = false;
			inventoryCountNow = inventoryCountNow + item.getInventoryCountNow();
			sellOrdersCountNow = sellOrdersCountNow + item.getSellOrdersCountNow();
			buyOrdersCountNow = buyOrdersCountNow + item.getBuyOrdersCountNow();
			jobsCountNow = jobsCountNow + item.getJobsCountNow();
			//Only add if negative
			if (item.getCountNeeded() < 0) countNeeded = countNeeded + item.getCountNeeded();
			countMinimum = countMinimum + item.getCountMinimum();
			totalPrice = totalPrice + item.getPrice();
			totalPriceCount++;
			valueNow = valueNow + item.getValueNow();
			//Only add if negative
			if (item.getValueNeeded() < 0) valueNeeded = valueNeeded + item.getValueNeeded();
			volumeNow = volumeNow + item.getVolumeNow();
			//Only add if negative
			if (item.getVolumeNeeded() < 0) volumeNeeded = volumeNeeded + item.getVolumeNeeded();
		}
		
		@Override
		public boolean isOK() {
			return ok;
		}
		
		@Override
		public boolean isHalf() {
			return half;
		}

		@Override
		public long getCountMinimum() {
			return countMinimum;
		}

		@Override
		public long getCountNeeded() {
			return countNeeded;
		}

		@Override
		public long getCountNow() {
			return inventoryCountNow + buyOrdersCountNow + jobsCountNow + sellOrdersCountNow;
		}

		@Override
		public long getInventoryCountNow() {
			return inventoryCountNow;
		}

		@Override
		public long getBuyOrdersCountNow() {
			return buyOrdersCountNow;
		}

		@Override
		public long getJobsCountNow() {
			return jobsCountNow;
		}

		@Override
		public long getSellOrdersCountNow() {
			return sellOrdersCountNow;
		}

		@Override
		public double getPrice() {
			if (totalPriceCount <= 0 || totalPrice <= 0){
				return 0;
			} else {
				return totalPrice / totalPriceCount;
			}
		}

		@Override
		public double getValueNeeded() {
			return valueNeeded;
		}

		@Override
		public double getValueNow() {
			return valueNow;
		}

		@Override
		public double getVolumeNeeded() {
			return volumeNeeded;
		}

		@Override
		public double getVolumeNow() {
			return volumeNow;
		}

		@Override
		public boolean isMarketGroup() {
			return false;
		}
	}
}
