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


package net.nikr.eve.jeveasset.gui.tabs.materials;

import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.gui.shared.Formater;


public class Material implements Comparable<Material> {

	public enum MaterialType {
		LOCATIONS(2, 1, 1),
		LOCATIONS_TOTAL(2, 2, 1),
		LOCATIONS_ALL(2, 2, 2),
		SUMMARY(1, 1, 1),
		SUMMARY_TOTAL(1, 2, 1),
		SUMMARY_ALL(1, 2, 2);

		private int locationOrder;
		private int goupeOrder;
		private int nameOrder;
		private MaterialType(final int locationOrder, final int goupeOrder, final int nameOrder) {
			this.locationOrder = locationOrder;
			this.goupeOrder = goupeOrder;
			this.nameOrder = nameOrder;
		}

		public int getLocationOrder() {
			return locationOrder;
		}

		public int getGoupeOrder() {
			return goupeOrder;
		}

		public int getNameOrder() {
			return nameOrder;
		}
	}

	private final String name;
	private final String location;
	private final String group;
	private final String typeName;
	private final boolean marketGroup;
	private final Integer typeID; //TypeID : int
	private final String station;
	private final String system;
	private final String region;
	private double value = 0;
	private long count = 0;
	private boolean first = false;
	private final Double price;
	private final MaterialType type;

	public Material(final MaterialType type, final String name, final String location, final String group, final Asset eveAsset) {
		this.type = type;
		this.name = name;
		this.location = location;
		this.group = group;
		if (eveAsset != null) {
			//Has item
			if (type == MaterialType.LOCATIONS || type == MaterialType.SUMMARY){
				this.typeName = eveAsset.getName();
				this.marketGroup = eveAsset.isMarketGroup();
				this.typeID = eveAsset.getTypeID();
				this.price = eveAsset.getPrice();
			} else {
				this.typeName = null;
				this.marketGroup = false;
				this.typeID = null;
				this.price = null;
			}
			//Has location
			if (type == MaterialType.LOCATIONS || type == MaterialType.LOCATIONS_TOTAL || type == MaterialType.LOCATIONS_ALL){
				this.station = eveAsset.getLocation();
				this.system = eveAsset.getSystem();
				this.region = eveAsset.getRegion();
			} else {
				this.station = null;
				this.system = null;
				this.region = null;
			}
		} else {
			this.typeName = null;
			this.marketGroup = false;
			this.typeID = null;
			this.station = null;
			this.system = null;
			this.region = null;
			this.price = null;
		}
	}

	public void updateValue(final long updateCount, final double updatePrice) {
		this.count = this.count + updateCount;
		this.value = this.value + (updateCount * updatePrice);
	}

	public long getCount() {
		return count;
	}

	public String getGroup() {
		return group;
	}

	public String getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public boolean isMarketGroup() {
		return marketGroup;
	}

	public String getRegion() {
		return region;
	}

	public String getSystem() {
		return system;
	}

	public MaterialType getType() {
		return type;
	}

	public Integer getTypeID() {
		return typeID;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getStation() {
		return station;
	}

	public boolean isFirst() {
		return first;
	}

	public void first() {
		first = true;
	}

	public Double getPrice() {
		return price;
	}

	public double getValue() {
		return Formater.round(value, 2);
	}

	public String getSeperator() {
		return type.getLocationOrder() + location + type.getGoupeOrder() + group;
	}

	protected String getCompare() {
		return type.getLocationOrder() + location + type.getGoupeOrder() + group + type.getNameOrder() + name;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Material other = (Material) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if ((this.location == null) ? (other.location != null) : !this.location.equals(other.location)) {
			return false;
		}
		if ((this.group == null) ? (other.group != null) : !this.group.equals(other.group)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 29 * hash + (this.location != null ? this.location.hashCode() : 0);
		hash = 29 * hash + (this.group != null ? this.group.hashCode() : 0);
		return hash;
	}

	@Override
	public int compareTo(final Material o) {
		return this.getCompare().compareToIgnoreCase(o.getCompare());
	}

	public static class ISK {

		private String price;

		public ISK(final String price) {
			this.price = price;
		}

		@Override
		public String toString() {
			return price;
		}
	}

}
