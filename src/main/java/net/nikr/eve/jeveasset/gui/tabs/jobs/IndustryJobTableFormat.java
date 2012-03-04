/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsJobs;


enum IndustryJobTableFormat implements EnumTableColumn<IndustryJob> {
	//XXX Removed extra id columns from IndustryJobTableFormat (Industry Plot Releated?)
	/*
	JOB_ID(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return "Job ID";
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getJobID();
		}
	},
	CONTAINER_ID(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return "Container ID";
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getContainerID();
		}
	},
	CONTAINER_LOCATION_ID(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return "Container Location";
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getContainerLocationID();
		}
	},
	LINE_ID(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return "Line ID";
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getAssemblyLineID();
		}
	},
	 */
	
	
	STATE(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnState();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getState();
		}
	},
	ACTIVITY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnActivity();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getActivity();
		}
	},
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnName();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getName();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnOwner();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getOwner();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnLocation();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getLocation();
		}
	},
	REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnRegion();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getRegion();
		}
	},
	INSTALL_DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnInstallDate();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getInstallTime();
		}
	},
	START_DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnStartDate();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getBeginProductionTime();
		}
	},
	END_DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnEndDate();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getEndProductionTime();
		}
	},
	BP_ME(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnBpMe();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getInstalledItemMaterialLevel();
		}
	},
	BP_PE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJobs.get().columnBpPe();
		}
		@Override
		public Object getColumnValue(IndustryJob from) {
			return from.getInstalledItemProductivityLevel();
		}
	},
	;

	Class type;
	Comparator<?> comparator;
	private IndustryJobTableFormat(Class type, Comparator<?> comparator) {
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
	@Override public boolean isColumnEditable(Object baseObject) {
		return false;
	}
	@Override public IndustryJob setColumnValue(Object baseObject, Object editedValue) {
		return null;
	}
}
