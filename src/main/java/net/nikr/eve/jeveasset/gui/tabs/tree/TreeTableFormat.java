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

package net.nikr.eve.jeveasset.gui.tabs.tree;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.data.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.LongInt;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.i18n.TabsAssets;


public enum TreeTableFormat implements EnumTableColumn<TreeAsset> {
	NAME(HierarchyColumn.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnName();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getHierarchyColumn();
		}
	},
	TAGS(Tags.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTags();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTags();
		}
	},
	GROUP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnGroup();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getGroup();
		}
	},
	CATEGORY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnCategory();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getCategory();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getOwner();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnLocation();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getLocation().getLocation();
		}
	},
	SECURITY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnSecurity();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getSecurity();
		}
	},
	REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnRegion();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getLocation().getRegion();
		}
	},
	CONTAINER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnContainer();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getContainer();
		}
	},
	FLAG(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnFlag();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getFlag();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPrice();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getDynamicPrice();
		}
	},
	PRICE_SELL_MIN(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceSellMin();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getPriceSellMin();
		}
	},
	PRICE_BUY_MAX(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceBuyMax();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getPriceBuyMax();
		}
	},
	PRICE_REPROCESSED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceReprocessed();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getPriceReprocessed();
		}
	},
	MARKET_ORDER_LATEST(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnMarketOrderLatest();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getPriceMarketLatest();
		}
	},
	MARKET_ORDER_AVERAGE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnMarketOrderAverage();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getPriceMarketAverage();
		}
	},
	MARKET_ORDER_MAXIMUM(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnMarketOrderMaximum();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getPriceMarketMaximum();
		}
	},
	MARKET_ORDER_MINIMUM(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnMarketOrderMinimum();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getPriceMarketMinimum();
		}
	},
	PRICE_BASE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceBase();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getPriceBase();
		}
	},
	VALUE_REPROCESSED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnValueReprocessed();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getValueReprocessed();
		}
	},
	VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnValue();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getValue();
		}
	},
	PRICE_REPROCESSED_DIFFERENCE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceReprocessedDifference();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getPriceReprocessedDifference();
		}
	},
	PRICE_REPROCESSED_PERCENT(Percent.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceReprocessedPercent();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return new Percent(from.getPriceReprocessedPercent());
		}
	},
	COUNT(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnCount();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getCount();
		}
	},
	COUNT_TYPE(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTypeCount();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTypeCount();
		}
	},
	META(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnMeta();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getMeta();
		}
	},
	TECH(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTech();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getTech();
		}
	},
	VOLUME(Float.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnVolume();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getVolume();
		}
	},
	VOLUME_TOTAL(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnVolumeTotal();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getVolumeTotal();
		}
	},
	VALUE_PER_VOLUME(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnValuePerVolume();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getValuePerVolume();
		}
	},
	SINGLETON(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnSingleton();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getSingleton();
		}
	},
	ADDED(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnAdded();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAdded();
		}
		@Override
		public boolean isShowDefault() {
			return false;
		}
	},
	ITEM_ID(LongInt.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnItemID();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return new LongInt(from.getItemID());
		}
	},
	TYPE_ID(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTypeID();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getTypeID();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private TreeTableFormat(final Class<?> type, final Comparator<?> comparator) {
		this.type = type;
		this.comparator = comparator;
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
	@Override
	public boolean isColumnEditable(final Object baseObject) {
		return false;
	}
	@Override
	public boolean isShowDefault() {
		return true;
	}
	@Override
	public TreeAsset setColumnValue(final Object baseObject, final Object editedValue) {
		return null;
	}
	//XXX - TableFormat.getColumnValue(...) Workaround
	@Override public abstract Object getColumnValue(final TreeAsset from);

	//FIXME - - > TreeTableFormat: Move inner classes to containers
	public static class HierarchyColumn implements Comparable<HierarchyColumn>{
		private final String export;
		private final String gui;
		//private final String compare;

		public HierarchyColumn(String text, boolean parent) {
			this.gui = text.trim();
			if (parent) {
				int split = text.indexOf(gui);
				this.export = text.substring(0, split) + "+" + text.substring(split);
			} else {
				this.export = text;
			}
		}

		public String getExport() {
			return export;
		}

		@Override
		public int compareTo(HierarchyColumn o) {
			return this.toString().compareTo(o.toString());
		}

		@Override
		public String toString() {
			return gui;
		}
	}
}
