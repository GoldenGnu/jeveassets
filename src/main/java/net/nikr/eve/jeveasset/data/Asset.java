/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.types.BlueprintType;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.InfoItem;
import net.nikr.eve.jeveasset.i18n.DataModelAsset;

public class Asset implements Comparable<Asset>, InfoItem, LocationType, ItemType, BlueprintType, PriceType {

	public enum PriceMode {
		PRICE_SELL_MAX() {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceSellMax();
			}
		},
		PRICE_SELL_AVG {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceSellAvg();
			}
		},
		PRICE_SELL_MEDIAN {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceSellMedian();
			}
		},
		PRICE_SELL_PERCENTILE {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceSellPercentile();
			}
		},
		PRICE_SELL_MIN {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceSellMin();
			}
		},
		PRICE_MIDPOINT {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceMidpoint();
			}
		},
		PRICE_BUY_MAX {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceBuyMax();
			}
		},
		PRICE_BUY_PERCENTILE {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceBuyPercentile();
			}
		},
		PRICE_BUY_AVG {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceBuyAvg();
			}
		},
		PRICE_BUY_MEDIAN {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceBuyMedian();
			}
		},
		PRICE_BUY_MIN {
			@Override
			String getI18N() {
				return DataModelAsset.get().priceBuyMin();
			}
		};
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
	}

	//Default
	private static final PriceMode DEFAULT_PRICE_TYPE = PriceMode.PRICE_MIDPOINT;

	private static PriceMode priceType = PriceMode.PRICE_MIDPOINT;
	private static PriceMode priceReprocessedType = PriceMode.PRICE_MIDPOINT;

	//Static values (set by constructor)
	private List<Asset> assets = new ArrayList<Asset>();
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
	//Dynamic values cache
	private boolean userNameSet = false;
	private boolean userPriceSet = false;

	/**
	 * For mockups...
	 */
	protected Asset() { }

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
		this.bpo = (item.isBlueprint() && rawQuantity == -1);
		this.bpc = (item.isBlueprint() && rawQuantity == -2);
		//2nd
		if (item.isBlueprint()) {
			if (isBPO()) {
				this.typeName = item.getTypeName() + " (BPO)";
			} else {
				this.typeName = item.getTypeName() + " (BPC)";
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

	public double getDefaultPrice() {
		return getDefaultPrice(getPriceData());
	}

	/*
	 * These should be methods on the PriceMode enum.
	 */
	public static double getDefaultPrice(final PriceData priceData) {
		return getDefaultPrice(priceData, priceType);
	}
	public static double getDefaultPriceReprocessed(final PriceData priceData) {
		return getDefaultPrice(priceData, priceReprocessedType);
	}
	private static double getDefaultPrice(final PriceData priceData, final PriceMode priceMode) {
		if (priceData != null) {
			if (priceMode.equals(PriceMode.PRICE_SELL_MAX)) {
				return priceData.getSellMax();
			}
			if (priceMode.equals(PriceMode.PRICE_SELL_AVG)) {
				return priceData.getSellAvg();
			}
			if (priceMode.equals(PriceMode.PRICE_SELL_MEDIAN)) {
				return priceData.getSellMedian();
			}
			if (priceMode.equals(PriceMode.PRICE_SELL_PERCENTILE)) {
				return priceData.getSellPercentile();
			}
			if (priceMode.equals(PriceMode.PRICE_SELL_MIN)) {
				return priceData.getSellMin();
			}
			if (priceMode.equals(PriceMode.PRICE_MIDPOINT)) {
				return (priceData.getSellMin() + priceData.getBuyMax()) / 2;
			}
			if (priceMode.equals(PriceMode.PRICE_BUY_MAX)) {
				return priceData.getBuyMax();
			}
			if (priceMode.equals(PriceMode.PRICE_BUY_AVG)) {
				return priceData.getBuyAvg();
			}
			if (priceMode.equals(PriceMode.PRICE_BUY_MEDIAN)) {
				return priceData.getBuyMedian();
			}
			if (priceMode.equals(PriceMode.PRICE_BUY_PERCENTILE)) {
				return priceData.getBuyPercentile();
			}
			if (priceMode.equals(PriceMode.PRICE_BUY_MIN)) {
				return priceData.getBuyMin();
			}
		}
		return 0;
	}

	public static PriceMode getDefaultPriceType() {
		return DEFAULT_PRICE_TYPE;
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
		//UserPrice
		if (this.getUserPrice() != null) {
			return this.getUserPrice().getValue();
		}

		//Blueprint Copy (Default Zero)
		if (item.isBlueprint() && !isBPO()) {
			return 0.0;
		}

		//PriceData
		return getDefaultPrice();
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

	public static PriceMode getPriceType() {
		return priceType;
	}

	public static PriceMode getPriceReprocessedType() {
		return priceReprocessedType;
	}

	public int getRawQuantity() {
		return rawQuantity;
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

	@Override
	public double getVolumeTotal() {
		return volume * count;
	}

	@Override
	public boolean isBPO() {
		return bpo;
	}

	@Override
	public boolean isBPC() {
		return bpc;
	}

	public boolean isCorporation() {
		return owner.isCorporation();
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

	public void setName(final String name) {
		this.name = name;
		userNameSet = !getName().equals(getTypeName());
	}

	public void setPriceData(final PriceData priceData) {
		this.priceData = priceData;
	}

	public void setPriceReprocessed(final double priceReprocessed) {
		this.priceReprocessed = priceReprocessed;
	}

	public static void setPriceType(final PriceMode priceSource) {
		Asset.priceType = priceSource;
	}

	public static void setPriceReprocessedType(final PriceMode reprocessedPriceType) {
		Asset.priceReprocessedType = reprocessedPriceType;
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
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Asset other = (Asset) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}
}
