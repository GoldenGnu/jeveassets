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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsAssets;


public enum EveAssetTableFormat implements EnumTableColumn<Asset> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnName();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getName();
		}
	},
	GROUP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnGroup();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getGroup();
		}
	},
	CATEGORY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnCategory();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getCategory();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnOwner();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getOwner();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnLocation();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getLocation();
		}
	},
	SECURITY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnSecurity();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getSecurity();
		}
	},
	REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnRegion();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getRegion();
		}
	},
	CONTAINER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnContainer();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getContainer();
		}
	},
	FLAG(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnFlag();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getFlag();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPrice();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getPrice();
		}
	},
	PRICE_SELL_MIN(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceSellMin();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getPriceSellMin();
		}
	},
	PRICE_BUY_MAX(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceBuyMax();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getPriceBuyMax();
		}
	},
	PRICE_REPROCESSED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceReprocessed();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getPriceReprocessed();
		}
	},
	PRICE_BASE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceBase();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getPriceBase();
		}
	},
	VALUE_REPROCESSED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnValueReprocessed();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getValueReprocessed();
		}
	},
	VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnValue();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getValue();
		}
	},
	
	COUNT(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnCount();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getCount();
		}
	},
	COUNT_TYPE(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTypeCount();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getTypeCount();
		}
	},
	META(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnMeta();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getMeta();
		}
	},
	VOLUME(Float.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnVolume();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getVolume();
		}
	},
	VOLUME_TOTAL(Float.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnVolumeTotal();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getVolumeTotal();
		}
	},
	SINGLETON(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnSingleton();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getSingleton();
		}
	},
	ITEM_ID(LongInt.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnItemID();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return new LongInt(from.getItemID());
		}
	},
	
	TYPE_ID(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTypeID();
		}
		@Override
		public Object getColumnValue(Asset from) {
			return from.getTypeID();
		}
	},
	;

	Class type;
	Comparator<?> comparator;
	private EveAssetTableFormat(Class type, Comparator<?> comparator) {
		this.type = type;
		this.comparator = comparator;
	}
	@Override
	public Class getType() {
		return type;
	}
	@Override
	public Comparator getComparator() {
		return comparator;
	}
	//XXX - TableFormat.getColumnValue(...) Workaround
	@Override
	public Object getColumnValue(Asset from) {
		return getColumnValue(from);
	}
	@Override
	public String toString() {
		return getColumnName();
	}
	@Override public boolean isColumnEditable(Object baseObject) {
		return false;
	}
	@Override public Asset setColumnValue(Object baseObject, Object editedValue) {
		return null;
	}
	
	public class LongInt implements Comparable<LongInt> {
		Long number;

		public LongInt(Long number) {
			this.number = number;
		}
		
		public Long getNumber() {
			return number;
		}

		@Override
		public String toString() {
			return Formater.integerFormat(number);
		}

		@Override
		public int compareTo(LongInt o) {
			return number.compareTo(o.getNumber());
		}
	}
}
