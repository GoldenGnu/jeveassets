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
package net.nikr.eve.jeveasset.data;

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.i18n.DataModelEveAsset;

public class Asset implements Comparable<Asset> {

	public enum PriceMode {
		PRICE_SELL_MAX() {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceSellMax();
			}
		},
		PRICE_SELL_AVG {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceSellAvg();
			}
		},
		PRICE_SELL_MEDIAN {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceSellMedian();
			}
		},
		PRICE_SELL_PERCENTILE {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceSellPercentile();
			}
		},
		PRICE_SELL_MIN {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceSellMin();
			}
		},
		PRICE_MIDPOINT {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceMidpoint();
			}
		},
		PRICE_BUY_MAX {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceBuyMax();
			}
		},
		PRICE_BUY_PERCENTILE {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceBuyPercentile();
			}
		},
		PRICE_BUY_AVG {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceBuyAvg();
			}
		},
		PRICE_BUY_MEDIAN {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceBuyMedian();
			}
		},
		PRICE_BUY_MIN {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceBuyMin();
			}
		};
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
	}

	//Default
	private static PriceMode defaultPriceType = PriceMode.PRICE_MIDPOINT;

	private static PriceMode priceType = PriceMode.PRICE_MIDPOINT;

	private List<Asset> assets = new ArrayList<Asset>();
	private long locationID; //LocationID : long
	private long itemID; //ItemID : long
	private long solarSystemID; //LocationID : long
	private int typeID; //TypeID : int
	private int flagID; //FlagID : int
	private String typeName;
	private String name;
	private String group;
	private String category;
	private String owner;
	private long count;
	private String location;
	private String container = "";
	private List<Asset> parents;
	private String flag;
	private double priceBase;
	private String tech;
	private int meta;
	private boolean marketGroup;
	private PriceData priceData;
	private UserItem<Integer, Double> userPrice;
	private boolean corporation;
	private float volume;
	private String region;
	private long typeCount = 0;
	private boolean singleton;
	private String security;
	private double priceReprocessed;
	private String system;
	private int rawQuantity;
	private boolean piMaterial;

	/**
	 * For mockups...
	 */
	protected Asset() { }

	public Asset(final String typeName, final String group, final String category, final String owner, final long count, final String location, final List<Asset> parents, final String flag, final int flagID, final double priceBase, final int meta, final String tech, final long itemID, final int typeID, final boolean marketGroup, final boolean corporation, final float volume, final String region, final long locationID, final boolean singleton, final String security, final String system, final long solarSystemID, final int rawQuantity, final boolean piMaterial) {
		this.typeName = typeName;
		this.name = getTypeName();
		this.group = group;
		this.category = category;
		this.owner = owner;
		this.count = count;
		this.location = location;
		this.parents = parents;
		this.flag = flag;
		this.flagID = flagID;
		this.priceBase = priceBase;
		this.meta = meta;
		this.tech = tech;
		this.itemID = itemID;
		this.typeID = typeID;
		this.marketGroup = marketGroup;
		this.corporation = corporation;
		this.volume = volume;
		this.region = region;
		this.locationID = locationID;
		this.singleton = singleton;
		this.security = security;
		this.system = system;
		this.solarSystemID = solarSystemID;
		this.rawQuantity = rawQuantity;
		this.piMaterial = piMaterial;
	}

	public void addEveAsset(final Asset eveAsset) {
		assets.add(eveAsset);
	}

	public List<Asset> getAssets() {
		return assets;
	}

	public String getCategory() {
		return category;
	}

	public String getContainer() {
		return container;
	}

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
		if (priceData != null) {
			if (priceType.equals(PriceMode.PRICE_SELL_MAX)) {
				return priceData.getSellMax();
			}
			if (priceType.equals(PriceMode.PRICE_SELL_AVG)) {
				return priceData.getSellAvg();
			}
			if (priceType.equals(PriceMode.PRICE_SELL_MEDIAN)) {
				return priceData.getSellMedian();
			}
			if (priceType.equals(PriceMode.PRICE_SELL_PERCENTILE)) {
				return priceData.getSellPercentile();
			}
			if (priceType.equals(PriceMode.PRICE_SELL_MIN)) {
				return priceData.getSellMin();
			}
			if (priceType.equals(PriceMode.PRICE_MIDPOINT)) {
				return (priceData.getSellMin() + priceData.getBuyMax()) / 2;
			}
			if (priceType.equals(PriceMode.PRICE_BUY_MAX)) {
				return priceData.getBuyMax();
			}
			if (priceType.equals(PriceMode.PRICE_BUY_AVG)) {
				return priceData.getBuyAvg();
			}
			if (priceType.equals(PriceMode.PRICE_BUY_MEDIAN)) {
				return priceData.getBuyMedian();
			}
			if (priceType.equals(PriceMode.PRICE_BUY_PERCENTILE)) {
				return priceData.getBuyPercentile();
			}
			if (priceType.equals(PriceMode.PRICE_BUY_MIN)) {
				return priceData.getBuyMin();
			}
		}
		return 0;
	}

	public static PriceMode getDefaultPriceType() {
		return defaultPriceType;
	}

	public String getFlag() {
		return flag;
	}

	public int getFlagID() {
		return flagID;
	}

	public String getGroup() {
		return group;
	}

	public long getItemID() {
		return itemID;
	}

	public String getLocation() {
		return location;
	}

	public long getLocationID() {
		return locationID;
	}

	public int getMeta() {
		return meta;
	}

	public String getTech() {
		return tech;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public List<Asset> getParents() {
		return parents;
	}

	public double getPrice() {
		//UserPrice
		if (this.getUserPrice() != null) {
			return this.getUserPrice().getValue();
		}

		//Blueprint Copy (Default Zero)
		if (isBlueprint() && !isBpo()) {
			return 0;
		}

		//PriceData
		return getDefaultPrice();
	}

	public double getPriceBase() {
		return priceBase;
	}

	public double getPriceBuyMax() {
		if (isBlueprint() && !isBpo()) {
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

	public double getPriceSellMin() {
		if (isBlueprint() && !isBpo()) {
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

	public int getRawQuantity() {
		return rawQuantity;
	}

	public String getRegion() {
		return region;
	}

	public String getSecurity() {
		return security;
	}

	public long getSolarSystemID() {
		return solarSystemID;
	}

	public String getSystem() {
		return system;
	}

	public long getTypeCount() {
		return typeCount;
	}

	public int getTypeID() {
		return typeID;
	}

	public final String getTypeName() {
		if (isBlueprint()) {
			if (isBpo()) {
				return typeName + " (BPO)";
			} else {
				return typeName + " (BPC)";
			}
		} else {
			return typeName;
		}
	}

	public UserItem<Integer, Double> getUserPrice() {
		return userPrice;
	}

	public double getValue() {
		return Formater.round(this.getPrice() * this.getCount(), 2);
	}

	public double getValueReprocessed() {
		return Formater.round(this.getPriceReprocessed() * this.getCount(), 2);
	}

	public float getVolume() {
		return volume;
	}

	public float getVolumeTotal() {
		return volume * count;
	}

	public boolean isBlueprint() {
		return (typeName.toLowerCase().contains("blueprint"));
	}

	public boolean isBpo() {
		return rawQuantity != -2;
	}

	public boolean isCorporation() {
		return corporation;
	}

	public boolean isMarketGroup() {
		return marketGroup;
	}

	public boolean isPiMaterial() {
		return piMaterial;
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
			return "Unpackaged"; //FIXME i18n
		} else {
			return "Packaged"; //FIXME i18n
		}
	}

	public boolean isUserName() {
		return !getName().equals(getTypeName());
	}

	public boolean isUserPrice() {
		return (this.getUserPrice() != null);
	}

	public void setContainer(final String container) {
		this.container = container;
	}

	public void setName(final String name) {
		this.name = name;
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

	public void setTypeCount(final long typeCount) {
		this.typeCount = typeCount;
	}

	public void setUserPrice(final UserItem<Integer, Double> userPrice) {
		this.userPrice = userPrice;
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
		return this.getName().compareTo(o.getName());
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
