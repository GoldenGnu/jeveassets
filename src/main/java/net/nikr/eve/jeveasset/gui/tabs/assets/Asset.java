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
package net.nikr.eve.jeveasset.gui.tabs.assets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.MarketPriceData;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.data.tag.TagID;
import net.nikr.eve.jeveasset.data.tag.Tags;
import net.nikr.eve.jeveasset.data.types.BlueprintType;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.data.types.TagsType;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.InfoItem;
import net.nikr.eve.jeveasset.i18n.DataModelAsset;

public class Asset implements Comparable<Asset>, InfoItem, LocationType, ItemType, BlueprintType, PriceType, TagsType {

	//Static values (set by constructor)
	private final List<Asset> assets = new ArrayList<Asset>();
	private Item item;
	private Location location;
	private Owner owner;
	private long count;
	private List<Asset> parents;
	private String flag;
	private int flagID; //FlagID : int
	private long itemID; //ItemID : long
	private boolean singleton;
	private int rawQuantity;
	//Static values cache
	private String typeName;
	private boolean bpo;
	private boolean bpc;
	//Dynamic values
	private String name;
	private String container = "";
	private PriceData priceData;
	private UserItem<Integer, Double> userPrice;
	private float volume;
	private long typeCount = 0;
	private double priceReprocessed;
	private MarketPriceData marketPriceData;
	private Date added;
	private double price;
	private Tags tags;
	//Dynamic values cache
	private boolean userNameSet = false;
	private boolean eveNameSet = false;
	private boolean userPriceSet = false;

	/**
	 * For mockups...
	 */
	protected Asset() { }

	protected Asset(Asset asset) {
		this(asset.item,
				asset.location,
				asset.owner,
				asset.count,
				asset.parents,
				asset.flag,
				asset.flagID,
				asset.itemID,
				asset.singleton,
				asset.rawQuantity);
		//this.assets = asset.assets;
		this.added = asset.added;
		this.container = asset.container;
		this.price = asset.price;
		this.tags = asset.tags;
		this.marketPriceData = asset.marketPriceData;
		this.name = asset.name;
		this.priceData = asset.priceData;
		this.priceReprocessed = asset.priceReprocessed;
		this.typeCount = asset.typeCount;
		this.userPrice = asset.userPrice;
		this.volume = asset.volume;
		this.userNameSet = asset.userNameSet;
		this.userPriceSet = asset.userPriceSet;
		this.eveNameSet = asset.eveNameSet;
	}

	public Asset(final Item item, final Location location, final Owner owner, final long count, final List<Asset> parents, final String flag, final int flagID, final long itemID, final boolean singleton, final int rawQuantity) {
		this.item = item;
		this.location = location;
		this.owner = owner;
		this.count = count;
		this.parents = parents;
		this.flag = flag;
		this.flagID = flagID;
		this.itemID = itemID;
		this.volume = item.getVolume();
		this.singleton = singleton;
		this.rawQuantity = rawQuantity;
		//The order matter!
		//1st
		//rawQuantity: -1 = BPO. Only BPOs can be packaged (singleton == false). Only packaged items can be stacked (count > 1)
		this.bpo = (item.isBlueprint() && (rawQuantity == -1 || !singleton || count > 1));
		//rawQuantity: -2 = BPC
		this.bpc = (item.isBlueprint() && rawQuantity == -2);
		//2nd
		if (item.isBlueprint()) {
			if (isBPO()) {
				this.typeName = item.getTypeName() + " (BPO)";
			} else if (isBPC()){
				this.typeName = item.getTypeName() + " (BPC)";
			} else {
				this.bpc = true;
				this.typeName = item.getTypeName() + " (BP)";
			}
		} else {
			this.typeName = item.getTypeName();
		}
		//3rd
		this.name = getTypeName();
	}

	public void addAsset(final Asset asset) {
		assets.add(asset);
	}

	public Date getAdded() {
		return added;
	}

	public List<Asset> getAssets() {
		return assets;
	}

	public String getContainer() {
		return container;
	}

	@Override
	public long getCount() {
		return count;
	}

	public String getFlag() {
		return flag;
	}

	public int getFlagID() {
		return flagID;
	}

	public long getItemID() {
		return itemID;
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	public MarketPriceData getMarketPriceData() {
		if (marketPriceData != null) {
			return marketPriceData;
		} else {
			return new MarketPriceData();
		}
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner.getName();
	}

	public long getOwnerID() {
		return owner.getOwnerID();
	}

	public List<Asset> getParents() {
		return parents;
	}

	@Override
	public Double getDynamicPrice() {
		return price;
	}

	public void setDynamicPrice(double price) {
		this.price = price;
	}

	public double getPriceBuyMax() {
		if (item.isBlueprint() && !isBPO()) {
			return 0;
		}

		if (this.getPriceData() != null) {
			return this.getPriceData().getBuyMax();
		}

		return 0;
	}

	public PriceData getPriceData() {
		return priceData;
	}

	public double getPriceReprocessed() {
		return priceReprocessed;
	}

	public double getPriceReprocessedDifference() {
		return getPriceReprocessed() - getDynamicPrice();
	}

	public double getPriceReprocessedPercent() {
		if (getDynamicPrice() > 0 && getPriceReprocessed() > 0) {
			return (getPriceReprocessed() / getDynamicPrice());
		} else {
			return 0;
		}
	}

	public double getPriceSellMin() {
		if (item.isBlueprint() && !isBPO()) {
			return 0;
		}

		if (this.getPriceData() != null) {
			return this.getPriceData().getSellMin();
		}

		return 0;
	}

	public int getRawQuantity() {
		return rawQuantity;
	}

	@Override
	public Tags getTags() {
		return tags;
	}

	@Override
	public TagID getTagID() {
		return new TagID(AssetsTab.NAME, getItemID());
	}

	public long getTypeCount() {
		return typeCount;
	}

	public final String getTypeName() {
		return typeName;
	}

	public UserItem<Integer, Double> getUserPrice() {
		return userPrice;
	}

	@Override
	public double getValue() {
		return Formater.round(this.getDynamicPrice() * this.getCount(), 2);
	}

	@Override
	public double getValueReprocessed() {
		return Formater.round(this.getPriceReprocessed() * this.getCount(), 2);
	}

	public float getVolume() {
		return volume;
	}

	public double getValuePerVolume () {
		if (getVolume() > 0 && getDynamicPrice() > 0) {
			return getDynamicPrice() / getVolume();
		} else {
			return 0;
		}
	}

	@Override
	public double getVolumeTotal() {
		return volume * count;
	}

	@Override
	public final boolean isBPO() {
		return bpo;
	}

	@Override
	public final boolean isBPC() {
		return bpc;
	}

	public boolean isCorporation() {
		return owner.isCorporation();
	}

	public boolean isEveName() {
		return eveNameSet;
	}

	/**
	 * Singleton: Unpackaged.
	 *
	 * @return true if unpackaged - false if packaged
	 */
	public boolean isSingleton() {
		return singleton;
	}
	public String getSingleton() {
		if (singleton) {
			return DataModelAsset.get().unpackaged();
		} else {
			return DataModelAsset.get().packaged();
		}
	}

	public boolean isUserName() {
		return userNameSet;
	}

	public boolean isUserPrice() {
		return userPriceSet;
	}

	public void setAdded(final Date added) {
		this.added = added;
	}

	public void setContainer(final String container) {
		this.container = container;
	}

	public void setMarketPriceData(final MarketPriceData marketPriceData) {
		this.marketPriceData = marketPriceData;
	}

	public void setName(final String name, final boolean userNameSet, final boolean eveNameSet) {
		this.name = name;
		this.userNameSet = userNameSet;
		this.eveNameSet = eveNameSet;
	}

	public void setPriceData(final PriceData priceData) {
		this.priceData = priceData;
	}

	public void setPriceReprocessed(final double priceReprocessed) {
		this.priceReprocessed = priceReprocessed;
	}

	@Override
	public void setTags(Tags tags) {
		this.tags = tags;
	}

	public void setTypeCount(final long typeCount) {
		this.typeCount = typeCount;
	}

	public void setUserPrice(final UserItem<Integer, Double> userPrice) {
		this.userPrice = userPrice;
		userPriceSet = (this.getUserPrice() != null);
	}

	public void setVolume(final float volume) {
		this.volume = volume;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int compareTo(final Asset o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.owner != null ? this.owner.hashCode() : 0);
		hash = 97 * hash + (int) (this.itemID ^ (this.itemID >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Asset other = (Asset) obj;
		if (this.owner != other.owner && (this.owner == null || !this.owner.equals(other.owner))) {
			return false;
		}
		return this.itemID == other.itemID;
	}
}
