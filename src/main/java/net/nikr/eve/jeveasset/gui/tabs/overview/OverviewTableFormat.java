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

package net.nikr.eve.jeveasset.gui.tabs.overview;


import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Security;
import net.nikr.eve.jeveasset.i18n.TabsOverview;


enum OverviewTableFormat implements EnumTableColumn<Overview> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOverview.get().columnName();
		}
		@Override
		public Object getColumnValue(final Overview from) {
			return from.getName();
		}
	},
	SYSTEM(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOverview.get().columnSystem();
		}
		@Override
		public Object getColumnValue(final Overview from) {
			return from.getLocation().getSystem();
		}
	},
	REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOverview.get().columnRegion();
		}
		@Override
		public Object getColumnValue(final Overview from) {
			return from.getLocation().getRegion();
		}
	},
	SECURITY(Security.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOverview.get().columnSecurity();
		}
		@Override
		public Object getColumnValue(final Overview from) {
			return from.getLocation().getSecurityObject();
		}
	},
	VOLUME(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOverview.get().columnVolume();
		}
		@Override
		public Object getColumnValue(final Overview from) {
			return from.getVolumeTotal();
		}
	},
	VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOverview.get().columnValue();
		}
		@Override
		public Object getColumnValue(final Overview from) {
			return from.getValue();
		}
	},
	VALUE_PER_VOLUME(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public  String getColumnName() {
			return TabsOverview.get().columnValuePerVolume();
		}
		@Override
		public Object getColumnValue(final Overview from) {
			return from.getValuePerVolume();
		}
	},
	COUNT(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOverview.get().columnCount();
		}
		@Override
		public Object getColumnValue(final Overview from) {
			return from.getCount();
		}
	},
	AVERAGE_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOverview.get().columnAverageValue();
		}
		@Override
		public Object getColumnValue(final Overview from) {
			return from.getAverageValue();
		}
	},
	REPROCESSED_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOverview.get().columnReprocessedValue();
		}
		@Override
		public Object getColumnValue(final Overview from) {
			return from.getValueReprocessed();
		}
	};
	private Class<?> type;
	private Comparator<?> comparator;
	private OverviewTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	public Overview setColumnValue(final Object baseObject, final Object editedValue) {
		return null;
	}
	//XXX - TableFormat.getColumnName() Workaround
	@Override public abstract String getColumnName();
	@Override
	public String toString() {
		return getColumnName();
	}
}
