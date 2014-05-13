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

package net.nikr.eve.jeveasset.gui.tabs.jobs;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsJobs;


public enum IndustryJobTableFormat implements EnumTableColumn<MyIndustryJob> {
	STATE(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnState();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getState();
		}
	},
	ACTIVITY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnActivity();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getActivity();
		}
	},
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnName();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getName();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getOwner();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnLocation();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getLocation().getLocation();
		}
	},
	REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnRegion();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getLocation().getRegion();
		}
	},
	INSTALL_DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnInstallDate();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getInstallTime();
		}
	},
	START_DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnStartDate();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getBeginProductionTime();
		}
	},
	END_DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnEndDate();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getEndProductionTime();
		}
	},
	RUNS(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnRuns();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getRuns();
		}
	},
	BP_ME(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnBpMe();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getInstalledItemMaterialLevel();
		}
	},
	BP_PE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnBpPe();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getInstalledItemProductivityLevel();
		}
	},
	OUTPUT_COUNT(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnOutputCount();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getOutputCount();
		}
	},
	OUTPUT_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnOutputValue();
		}
		@Override
		public Object getColumnValue(final MyIndustryJob from) {
			return from.getOutputValue();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;
	private IndustryJobTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	public MyIndustryJob setColumnValue(final Object baseObject, final Object editedValue) {
		return null;
	}
	//XXX - TableFormat.getColumnValue(...) Workaround
	@Override public abstract Object getColumnValue(final MyIndustryJob from);
}
