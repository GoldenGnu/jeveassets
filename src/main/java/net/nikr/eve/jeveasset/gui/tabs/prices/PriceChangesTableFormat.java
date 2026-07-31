/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
			return TabsPriceChanges.get().columnCountNow();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnCountNowToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getCountNow();
		}
	},
	COUNT_FROM(Long.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnCountFrom();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnCountFromToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getCountFrom();
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
	VALUE_FROM(Double.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnValueFrom();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnValueFromToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getValueFrom();
		}
	},
	COUNT_TO(Long.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnCountTo();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnCountToToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getCountTo();
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
	VALUE_TO(Double.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnValueTo();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnValueToToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getValueTo();
		}
	},
	PRICE_CHANGE_PERCENT(Percent.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnPriceDifferencePercent();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnPriceDifferencePercentToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getPriceChangePercent();
		}
	},

	PRICE_CHANGE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnPriceDifference();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getPriceChange();
		}
	},
	COUNT_CHANGE_PERCENT(Percent.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnCountDifferencePercent();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnCountDifferencePercentToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getCountChangePercent();
		}
	},
	COUNT_CHANGE(Long.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnCountDifference();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnCountDifferenceToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getCountChange();
		}
	},
	VALUE_CHANGE_PERCENT(Percent.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnValueDifferencePercent();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnValueDifferencePercentToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getValueChangePercent();
		}
	},
	VALUE_CHANGE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsPriceChanges.get().columnValueDifference();
		}
		@Override
		public String getColumnToolTip() {
			return TabsPriceChanges.get().columnValueDifferenceToolTip();
		}
		@Override
		public Object getColumnValue(final PriceChange from) {
			return from.getValueChange();
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
