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


package net.nikr.eve.jeveasset.gui.tabs.materials;

import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler.CopySeparator;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;


public class Material implements Comparable<Material>, LocationType, ItemType, PriceType, CopySeparator {

	public enum MaterialType {
		LOCATIONS(2, 1, 1),
		LOCATIONS_TOTAL(2, 2, 1),
		LOCATIONS_ALL(2, 2, 2),
		SUMMARY(1, 1, 1),
		SUMMARY_TOTAL(1, 2, 1),
		SUMMARY_ALL(1, 2, 2);

		private int headerOrder;
		private int goupeOrder;
		private int nameOrder;
		private MaterialType(final int headerOrder, final int goupeOrder, final int nameOrder) {
			this.headerOrder = headerOrder;
			this.goupeOrder = goupeOrder;
			this.nameOrder = nameOrder;
		}

		public int getHeaderOrder() {
			return headerOrder;
		}

		public int getGoupeOrder() {
			return goupeOrder;
		}

		public int getNameOrder() {
			return nameOrder;
		}
	}

	private final String header;
	private final String group;
	private final String name;
	private final Location location;
	private final Item item;

	private double value = 0;
	private long count = 0;
	private boolean first = false;
	private final Double price;
	private final MaterialType type;

	public Material(final MaterialType type, final Asset asset, final String header, final String group, final String name) {
		this.type = type;
		this.header = header;
		this.group = group;
		this.name = name;
		if (asset != null) {
			//Has item
			if (type == MaterialType.LOCATIONS || type == MaterialType.SUMMARY) {
				this.item = asset.getItem();
				this.price = asset.getDynamicPrice();
			} else {
				this.item = new Item(0);
				this.price = null;
			}

			
			//Has location
			if (type == MaterialType.LOCATIONS || type == MaterialType.LOCATIONS_TOTAL || type == MaterialType.LOCATIONS_ALL) {
				location = asset.getLocation();
			} else {
				location = new Location(0);
			}
		} else {
			location = new Location(0);
			this.item = new Item(0);
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

	public String getHeader() {
		return header;
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	public String getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public MaterialType getType() {
		return type;
	}

	public boolean isFirst() {
		return first;
	}

	public void first() {
		first = true;
	}

	@Override
	public Double getDynamicPrice() {
		return price;
	}

	public double getValue() {
		return Formater.round(value, 2);
	}

	public String getSeparator() {
		return type.getHeaderOrder() + header + type.getGoupeOrder() + group;
	}

	protected String getCompare() {
		return type.getHeaderOrder() + header + type.getGoupeOrder() + group + type.getNameOrder() + name;
	}

	@Override
	public String getCopyString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getHeader());
		builder.append("\t");
		builder.append(getGroup());
		return builder.toString();
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
		if ((this.header == null) ? (other.header != null) : !this.header.equals(other.header)) {
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
		hash = 29 * hash + (this.header != null ? this.header.hashCode() : 0);
		hash = 29 * hash + (this.group != null ? this.group.hashCode() : 0);
		return hash;
	}

	@Override
	public int compareTo(final Material o) {
		return this.getCompare().compareToIgnoreCase(o.getCompare());
	}
}
