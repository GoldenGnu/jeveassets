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

package net.nikr.eve.jeveasset.gui.tabs.prices;

import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.gui.tabs.prices.PriceChangesTab.PriceChange;
import net.nikr.eve.jeveasset.i18n.TabsPriceChanges;


public enum PriceChangesTableFormat implements EnumTableColumn<PriceChange> {
	NAME(String.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnName();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getItem().getTypeName();
		}
	},
	GROUP(String.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnGroup();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getItem().getGroup();
		}
	},
	CATEGORY(String.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnCategory();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getItem().getCategory();
		}
	},
	COUNT(Long.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnCount();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnCountToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getItemCount();
		}
	},
	PRICE_FROM(Double.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnPriceFrom();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getPriceFrom();
		}
	},
	PRICE_TO(Double.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnPriceTo();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getPriceTo();
		}
	},
	PRICE_CHANGE_PERCENT(Percent.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnChangePercent();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnChangePercentToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getChangePercent();
		}
	},
	PRICE_CHANGE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnChange();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnChangeToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getChange();
		}
	},
	PRICE_TOTAL(Double.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnTotal();
		}

		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnTotalToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getTotal();
		}
	},
	;

	private final Class<?> type;
	private final Comparator<?> comparator;

	private PriceChangesTableFormat(final Class<?> type) {
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
