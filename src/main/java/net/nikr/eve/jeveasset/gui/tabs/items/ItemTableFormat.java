/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.items;

import java.util.Comparator;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsItems;


public enum ItemTableFormat implements EnumTableColumn<Item> {
	NAME(String.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnName();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getTypeName();
		}
	},
	GROUP(String.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnGroup();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getGroup();
		}
	},
	CATEGORY(String.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnCategory();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getCategory();
		}
	},
	SLOT(String.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnSlot();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getSlot();
		}
	},
	CHARGE_SIZE(String.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnChargeSize();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getChargeSize();
		}
	},
	PRICE_BASE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnPriceBase();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getPriceBase();
		}
	},
	PRICE_REPROCESSED(Double.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnPriceReprocessed();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getPriceReprocessed();
		}
	},
	PRICE_MANUFACTURING(Double.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnPriceManufacturing();
		}
		@Override
		public String getColumnToolTip() {
			return TabsItems.get().columnPriceManufacturingToolTip();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getItem().getPriceManufacturing();
		}
	},
	META(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnMeta();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getMeta();
		}
	},
	TECH(String.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnTech();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getTech();
		}
	},
	VOLUME(Float.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnVolume();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getVolume();
		}
	},
	VOLUME_PACKAGED(Float.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnVolumePackaged();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getVolumePackaged();
		}
	},
	TYPE_ID(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsItems.get().columnTypeID();
		}
		@Override
		public Object getColumnValue(final Item from) {
			return from.getTypeID();
		}
	};
	private final Class<?> type;
	private final Comparator<?> comparator;
	private ItemTableFormat(final Class<?> type) {
		this.type = type;
		this.comparator = EnumTableColumn.getComparator(type);
	}
	@Override
	public Class<?> getType() {
		return type;
	}
	@Override
	public Comparator<?> getComparator() {
		return comparator;
	}
	@Override
	public String toString() {
		return getColumnName();
	}

}
