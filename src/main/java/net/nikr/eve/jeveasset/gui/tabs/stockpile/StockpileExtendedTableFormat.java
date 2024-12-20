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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public enum StockpileExtendedTableFormat implements EnumTableColumn<StockpileItem> {
	STOCKPILE_NAME(String.class) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().getFilterStockpileName();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getStockpile().getName();
		}
	},
	STOCKPILE_OWNER(String.class) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().getFilterStockpileOwner();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getStockpile().getOwnerName();
		}
	},
	STOCKPILE_LOCATION(String.class) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().getFilterStockpileLocation();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getStockpile().getLocationName();
		}
	},
	STOCKPILE_FLAG(String.class) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().getFilterStockpileFlag();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getStockpile().getFlagName();
		}
	},
	STOCKPILE_CONTAINER(String.class) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().getFilterStockpileContainer();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getStockpile().getContainerName();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;
	private StockpileExtendedTableFormat(final Class<?> type) {
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
