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

package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public enum ReprocessedExtendedTableFormat implements EnumTableColumn<ReprocessedInterface> {
	TOTAL_NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnTotalName();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getTotal().getTypeName();
		}
	},
	TOTAL_PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnTotalPrice();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getTotal().getSellPrice();
		}
	},
	TOTAL_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnTotalValue();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getTotal().getValue();
		}
	},
	TOTAL_BATCH(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsReprocessed.get().columnTotalBatch();
		}
		@Override
		public Object getColumnValue(final ReprocessedInterface from) {
			return from.getTotal().getPortionSize();
		}
	};

	private Class<?> type;
	private Comparator<?> comparator;
	private ReprocessedExtendedTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	@Override public ReprocessedInterface setColumnValue(final Object baseObject, final Object editedValue) {
		return null;
	}
	//XXX - TableFormat.getColumnValue(...) Workaround
	@Override public abstract Object getColumnValue(final ReprocessedInterface from);
}
