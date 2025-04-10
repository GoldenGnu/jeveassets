/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public enum ReprocessedTableFormat implements EnumTableColumn<ReprocessedInterface> {
	NAME(String.class) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnName();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getName();
		}
	},
	QUANTITY_100(Long.class) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnQuantity100();
		}
		@Override
		public String getColumnToolTip() {
			return TabsReprocessed.get().columnQuantity100ToolTip();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getQuantity100();
		}
	},
	QUANTITY_MAX(Long.class) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnQuantityMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsReprocessed.get().columnQuantityMaxToolTip();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getQuantityMax();
		}
	},
	QUANTITY_SKILL(Long.class) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnQuantitySkill();
		}
		@Override
		public String getColumnToolTip() {
			return TabsReprocessed.get().columnQuantitySkillToolTip();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getQuantitySkill();
		}
	},
	PRICE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnPrice();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getDynamicPrice();
		}
	},
	VALUE_MAX(Double.class) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnValueMax();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getValueMax();
		}
	},
	VALUE_SKILL(Double.class) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnValueSkill();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getValueSkill();
		}
	},
	VALUE_DIFFERENCE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnValueDifference();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getValueDifference();
		}
	},
	TYPE_ID(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnTypeID();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getItem().getTypeID();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;
	private ReprocessedTableFormat(final Class<?> type) {
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
