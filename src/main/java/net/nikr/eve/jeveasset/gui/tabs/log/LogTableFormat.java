/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.log;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.i18n.TabsLog;


public enum LogTableFormat implements EnumTableColumn<AssetLogSource> {
	DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsLog.get().columnDate();
		}
		@Override
		public Object getColumnValue(final AssetLogSource from) {
			return from.getDate();
		}
	},
	ACTION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsLog.get().columnAction();
		}
		@Override
		public Object getColumnValue(final AssetLogSource from) {
			return from.getSourceType().getAction();
		}
	},
	SOURCE(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsLog.get().columnSource();
		}
		@Override
		public Object getColumnValue(final AssetLogSource from) {
			return from.getSourceType().getSource();
		}
	},
	MATCH(Percent.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsLog.get().columnMatch();
		}
		@Override
		public Object getColumnValue(final AssetLogSource from) {
			return new Percent(from.getPercent() / 100.0);
		}
	},
	COUNT(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsLog.get().columnCount();
		}
		@Override
		public Object getColumnValue(final AssetLogSource from) {
			return from.getCount();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsLog.get().columnLocation();
		}
		@Override
		public Object getColumnValue(final AssetLogSource from) {
			return from.getLocation();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private LogTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	public boolean isColumnEditable(final Object baseObject) {
		return false;
	}
	@Override
	public boolean isShowDefault() {
		return true;
	}
	@Override
	public boolean setColumnValue(final Object baseObject, final Object editedValue) {
		return false;
	}
	@Override
	public String toString() {
		return getColumnName();
	}
	//XXX - TableFormat.getColumnValue(...) Workaround
	@Override public abstract Object getColumnValue(final AssetLogSource from);
	//XXX - TableFormat.getColumnName() Workaround
	@Override public abstract String getColumnName();
}
