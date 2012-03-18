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

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public enum StockpileExtendedTableFormat implements EnumTableColumn<StockpileItem> {
		STOCKPILE_NAME(String.class, GlazedLists.comparableComparator()) {
			@Override
			public String getColumnName() {
				return TabsStockpile.get().getFilterStockpileName();
			}
			@Override
			public Object getColumnValue(StockpileItem from) {
				return from.getStockpile().getName();
			}
		},
		STOCKPILE_OWNER(String.class, GlazedLists.comparableComparator()) {
			@Override
			public String getColumnName() {
				return TabsStockpile.get().getFilterStockpileOwner();
			}
			@Override
			public Object getColumnValue(StockpileItem from) {
				return from.getStockpile().getOwner();
			}
		},
		STOCKPILE_LOCATION(String.class, GlazedLists.comparableComparator()) {
			@Override
			public String getColumnName() {
				return TabsStockpile.get().getFilterStockpileLocation();
			}
			@Override
			public Object getColumnValue(StockpileItem from) {
				return from.getStockpile().getLocation();
			}
		},
		STOCKPILE_FLAG(String.class, GlazedLists.comparableComparator()) {
			@Override
			public String getColumnName() {
				return TabsStockpile.get().getFilterStockpileFlag();
			}
			@Override
			public Object getColumnValue(StockpileItem from) {
				return from.getStockpile().getFlag();
			}
		},
		STOCKPILE_CONTAINER(String.class, GlazedLists.comparableComparator()) {
			@Override
			public String getColumnName() {
				return TabsStockpile.get().getFilterStockpileContainer();
			}
			@Override
			public Object getColumnValue(StockpileItem from) {
				return from.getStockpile().getContainer();
			}
		},
		;
		
		Class type;
		Comparator<?> comparator;
		private StockpileExtendedTableFormat(Class type, Comparator<?> comparator) {
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
		@Override
		public String getColumnName() {
			return getColumnName();
		}
		//XXX - TableFormat.getColumnValue(...) Workaround
		@Override
		public Object getColumnValue(StockpileItem from) {
			return getColumnValue(from);
		}
		@Override
		public String toString() {
			return getColumnName();
		}
		@Override public boolean isColumnEditable(Object baseObject) {
			return false;
		}
		@Override public StockpileItem setColumnValue(Object baseObject, Object editedValue) {
			return null;
		}
	}
