/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class Stockpile implements Comparable<Stockpile> {
	private String name;
	private long ownerID;
	private String owner;
	private long locationID;
	private String location;
	private String system;
	private String region;
	private int flagID;
	private String flag;
	private String container;
	private boolean inventory;
	private boolean sellOrders;
	private boolean buyOrders;
	private boolean jobs;
	private final List<StockpileItem> items = new ArrayList<StockpileItem>();
	private final StockpileTotal totalItem = new StockpileTotal(this);
	private boolean expanded = true;
	private double percentFull;

	private Stockpile(final Stockpile stockpile) {
		update(stockpile);
		for (StockpileItem item : stockpile.getItems()) {
			if (item.getItemTypeID() != 0) { //Ignore Total
				items.add(new StockpileItem(this, item));
			}
		}
		items.add(totalItem);
	}

	public Stockpile(final String name, final long ownerID, final String owner, final long locationID, final String location, final String system, final String region, final int flagID, final String flag, final String container, final boolean inventory, final boolean sellOrders, final boolean buyOrders, final boolean jobs) {
		this.name = name;
		this.ownerID = ownerID;
		setOwner(owner);
		this.locationID = locationID;
		setLocation(location);
		this.system = system;
		this.region = region;
		this.flagID = flagID;
		setFlag(flag);
		this.container = container;
		this.inventory = inventory;
		this.sellOrders = sellOrders;
		this.buyOrders = buyOrders;
		this.jobs = jobs;
		items.add(totalItem);
	}

	final void update(final Stockpile stockpile) {
		this.name = stockpile.getName();
		this.ownerID = stockpile.getOwnerID();
		this.owner = stockpile.getOwner();
		this.locationID = stockpile.getLocationID();
		this.location = stockpile.getLocation();
		this.system = stockpile.getSystem();
		this.region = stockpile.getRegion();
		this.flagID = stockpile.getFlagID();
		this.flag = stockpile.getFlag();
		this.container = stockpile.getContainer();
		this.inventory = stockpile.isInventory();
		this.sellOrders = stockpile.isSellOrders();
		this.buyOrders = stockpile.isBuyOrders();
		this.jobs = stockpile.isJobs();
	}

	public boolean isOK() {
		return totalItem.isOK();
	}

	public boolean isHalf() {
		return totalItem.isHalf();
	}

	public boolean isEmpty() {
		return (items.size() <= 1);
	}

	public void add(final StockpileItem item) {
		if (!items.contains(item)) { //Only one of each type
			items.add(item);
			Collections.sort(items);
		}
	}

	public void remove(final StockpileItem item) {
		if (items.contains(item)) {
			items.remove(item);
		}
		if (items.isEmpty()) {
			items.add(totalItem);
		}
	}

	public void reset() {
		for (StockpileItem item : items) {
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

	public long getOwnerID() {
		return ownerID;
	}

	public String getOwner() {
		return owner;
	}

	public final void setOwner(final String owner) {
		if (owner == null) {
			this.owner = TabsStockpile.get().all();
		} else {
			this.owner = owner;
		}
	}

	private void setLocation(final String location) {
		if (location == null) {
			this.location = TabsStockpile.get().all();
		} else {
			this.location = location;
		}
	}

	public String getContainer() {
		return container;
	}

	public String getFlag() {
		return flag;
	}

	public final void setFlag(final String flag) {
		if (flag == null) {
			this.flag = TabsStockpile.get().all();
		} else {
			this.flag = flag;
		}
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

	public void setExpanded(final boolean expanded) {
		this.expanded = expanded;
	}

	public double getPercentFull() {
		return percentFull;
	}

	public void updateTotal() {
		totalItem.reset();
		percentFull = Double.MAX_VALUE;
		items.remove(totalItem);
		for (StockpileItem item : items) {
			double percent;
			if (item.getCountNow() == 0) {
				percent = 0;
			} else {
				percent = item.getCountNow() / ((double) item.getCountMinimum());
			}
			percentFull = Math.min(percent, percentFull);
			totalItem.updateTotal(item);
		}
		if (percentFull == Double.MAX_VALUE) { //Default value
			percentFull = 1;
		}
		items.add(totalItem);
	}

	public StockpileTotal getTotal() {
		return totalItem;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(final Object obj) {
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
	public int compareTo(final Stockpile o) {
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

		public StockpileItem(final Stockpile stockpile, final StockpileItem stockpileItem) {
			this(stockpile,
					stockpileItem.getTypeName(),
					stockpileItem.getGroup(),
					stockpileItem.getItemTypeID(),
					stockpileItem.getCountMinimum()
					);
		}

		public StockpileItem(final Stockpile stockpile, final String name, final String group, final int typeID, final long countMinimum) {
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
			return getCountNow() >= (getCountMinimum() / 2.0);
		}

		private void reset() {
			inventoryCountNow = 0;
			sellOrdersCountNow = 0;
			buyOrdersCountNow = 0;
			jobsCountNow = 0;
			price = 0.0;
			volume = 0.0f;
			marketGroup = false;
		}
		public void updateValues(final double updatePrice, final float updateVolume, final boolean updateMarketGroup) {
			this.price = updatePrice;
			this.volume = updateVolume;
			this.marketGroup = updateMarketGroup;
		}

		public void updateAsset(final Asset asset, final Long characterID, final Long regionID) {
			if (asset != null && characterID != null && regionID != null //better safe then sorry
					&& (typeID == asset.getTypeID() && (!asset.isBlueprint() || asset.isBpo()))
						|| (typeID == -asset.getTypeID() && asset.isBlueprint() && !asset.isBpo()) //Copy
					&& (stockpile.getOwnerID() == characterID || stockpile.getOwnerID() < 0)
					&& (asset.getContainer().contains(stockpile.getContainer()) || stockpile.getContainer().equals(TabsStockpile.get().all()))
					&& matchFlag(asset, stockpile.getFlagID())
					&& (stockpile.getLocation().equals(asset.getLocation()) //LocationID can be an office...
					|| stockpile.getLocationID() == asset.getSolarSystemID()
					|| stockpile.getLocationID() == regionID
					|| stockpile.getLocationID() < 0)
					) {
				inventoryCountNow = inventoryCountNow + asset.getCount();
			}
		}

		private boolean matchFlag(final Asset asset, final int flagID) {
			if (flagID < 0) { //Ignore flag
				return true;
			}
			if (asset.getFlagID() == flagID) { //Match self
				return true;
			}
			for (Asset parentAsset : asset.getParents()) { //Test parents
				if (parentAsset.getFlagID() == flagID) { //Parent match
					return true;
				}
			}
			return false; //No match
		}

		void updateMarketOrder(final ApiMarketOrder marketOrder, final Long ownerID, final Location location) {
			if (marketOrder != null && ownerID != null && location != null //better safe then sorry
					&& typeID == marketOrder.getTypeID()
					&& (stockpile.getOwnerID() == ownerID || stockpile.getOwnerID() < 0)
					&& marketOrder.getOrderState() == 0 //Open/Active
					&& (stockpile.getLocationID() == location.getLocationID()
					|| stockpile.getLocationID() == location.getSystemID()
					|| stockpile.getLocationID() == location.getRegionID()
					|| stockpile.getLocationID() < 0)
					) {
				if (marketOrder.getBid() < 1) { //Sell
					if (stockpile.isSellOrders()) {
						sellOrdersCountNow = sellOrdersCountNow + marketOrder.getVolRemaining();
					}
				} else { //Buy
					if (stockpile.isBuyOrders()) {
						buyOrdersCountNow = buyOrdersCountNow + marketOrder.getVolRemaining();
					}
				}
			}
		}

		void updateIndustryJob(final ApiIndustryJob industryJob, final ItemFlag itemFlag, final Long characterID, final Location location, final Item itemType) {
			if (industryJob != null && itemFlag != null && characterID != null && location != null && itemType != null //better safe then sorry
					&& typeID == industryJob.getOutputTypeID() //Produced only
					&& (stockpile.getOwnerID() == characterID || stockpile.getOwnerID() < 0)
					&& (stockpile.getFlagID() == itemFlag.getFlagID() || stockpile.getFlagID() < 0)
					&& (stockpile.getLocationID() == location.getLocationID()
					|| stockpile.getLocationID() == location.getSystemID()
					|| stockpile.getLocationID() == location.getRegionID()
					|| stockpile.getLocationID() < 0)
					&& industryJob.getActivityID() == 1 //Manufacturing
					&& industryJob.getCompletedStatus() == 0 //Inprogress AKA not delivered
					) {
				jobsCountNow = jobsCountNow + (industryJob.getRuns() * itemType.getPortion());
			}
		}

		public String getGroup() {
			return group;
		}

		public void setCountMinimum(final long countMinimum) {
			this.countMinimum = countMinimum;
			this.getStockpile().updateTotal();
		}

		public String getSeperator() {
			return stockpile.getName();
		}

		public Stockpile getStockpile() {
			return stockpile;
		}

		public boolean isBPC(){
			return (typeID < 0);
		}

		public boolean isBPO(){
			return isBlueprint() && !isBPC();
		}

		public boolean isBlueprint(){
			return name.toLowerCase().contains("blueprint");
		}

		public String getName() {
			if (isBPC()) { //Blueprint copy
				return name+" (BPC)";
			} else if (isBPO()) { //Blueprint original
				return name+" (BPO)";
			} else { //Everything else
				return name;
			}
		}

		public String getTypeName(){
			return name;
		}

		public long getCountMinimum() {
			return countMinimum;
		}

		public long getCountNow() {
			return inventoryCountNow + buyOrdersCountNow + jobsCountNow + sellOrdersCountNow;
		}

		public double getPercentNeeded() {
			double percent;
			if (getCountNow() == 0) {
				percent = 0;
			} else {
				percent = getCountNow() / ((double) getCountMinimum());
			}
			return percent;
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

		public int getItemTypeID() {
			return typeID;
		}

		public int getTypeID() {
			return Math.abs(typeID);
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
		public String toString() {
			return getName();
		}

		@Override
		public boolean equals(final Object obj) {
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
		public int compareTo(final StockpileItem item) {
			//Total should always be last...
			if (item instanceof StockpileTotal) { //this is Before item
				return -1;
			}
			if (this instanceof StockpileTotal) { //this is After item
				return 1;
			}
			//Compare groups
			int value = this.getGroup().compareTo(item.getGroup());
			if (value != 0) { //Not same group
				return value;
			} else { //Same group - compare names
				return this.getName().compareTo(item.getName());
			}
		}
	}

	public static class StockpileTotal extends StockpileItem {

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

		public StockpileTotal(final Stockpile stockpile) {
			super(stockpile, TabsStockpile.get().totalStockpile(), "", 0, 0);
		}

		private void reset() {
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

		private void updateTotal(final StockpileItem item) {
			if (!item.isOK()) {
				ok = false;
			}
			if (!item.isHalf()) {
				half = false;
			}
			inventoryCountNow = inventoryCountNow + item.getInventoryCountNow();
			sellOrdersCountNow = sellOrdersCountNow + item.getSellOrdersCountNow();
			buyOrdersCountNow = buyOrdersCountNow + item.getBuyOrdersCountNow();
			jobsCountNow = jobsCountNow + item.getJobsCountNow();
			//Only add if negative
			if (item.getCountNeeded() < 0) {
				countNeeded = countNeeded + item.getCountNeeded();
			}
			countMinimum = countMinimum + item.getCountMinimum();
			totalPrice = totalPrice + item.getPrice();
			totalPriceCount++;
			valueNow = valueNow + item.getValueNow();
			//Only add if negative
			if (item.getValueNeeded() < 0) {
				valueNeeded = valueNeeded + item.getValueNeeded();
			}
			volumeNow = volumeNow + item.getVolumeNow();
			//Only add if negative
			if (item.getVolumeNeeded() < 0) {
				volumeNeeded = volumeNeeded + item.getVolumeNeeded();
			}
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
			if (totalPriceCount <= 0 || totalPrice <= 0) {
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

		@Override
		public double getPercentNeeded() {
			return getStockpile().getPercentFull();
		}
	}

	static class Percent implements Comparable<Percent> {
		private double percent;

		public Percent(final double percent) {
			this.percent = percent;
		}

		@Override
		public String toString() {
			if (Double.isInfinite(percent)) {
				return Formater.integerFormat(percent);
			} else {
				return Formater.percentFormat(percent);
			}
		}

		@Override
		public int compareTo(final Percent o) {
			return Double.compare(percent, o.percent);
		}
	}
}
