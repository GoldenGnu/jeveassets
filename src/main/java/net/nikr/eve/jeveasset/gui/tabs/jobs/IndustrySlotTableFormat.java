/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsIndustrySlots;


public enum IndustrySlotTableFormat implements EnumTableColumn<IndustrySlot> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getName();
		}
	},
	MANUFACTURING_DONE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnManufacturingDone();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getManufacturingDone();
		}
	},
	MANUFACTURING_FREE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnManufacturingFree();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getManufacturingFree();
		}
	},
	MANUFACTURING_ACTIVE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnManufacturingActive();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getManufacturingActive();
		}
	},
	MANUFACTURING_MAX(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnManufacturingMax();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getManufacturingMax();
		}
	},
	RESEARCH_DONE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnResearchDone();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getResearchDone();
		}
	},
	RESEARCH_FREE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnResearchFree();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getResearchFree();
		}
	},
	RESEARCH_ACTIVE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnResearchActive();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getResearchActive();
		}
	},
	RESEARCH_MAX(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnResearchMax();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getResearchMax();
		}
	},
	REACTIONS_DONE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnReactionsDone();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getReactionsDone();
		}
	},
	REACTIONS_FREE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnReactionsFree();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getReactionsFree();
		}
	},
	REACTIONS_ACTIVE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnReactionsActive();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getReactionsActive();
		}
	},
	REACTIONS_MAX(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnReactionsMax();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getReactionsMax();
		}
	},
	CURRENT_SHIP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnCurrentShip();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getActiveShip();
		}
	},
	CURRENT_STATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnCurrentStation();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getCurrentStation();
		}
	},
	CURRENT_SYSTEM(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnCurrentSystem();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getCurrentSystem();
		}
	},
	CURRENT_CONSTELLATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnCurrentConstellation();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getCurrentConstellation();
		}
	},
	CURRENT_REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsIndustrySlots.get().columnCurrentRegion();
		}
		@Override
		public Object getColumnValue(final IndustrySlot from) {
			return from.getCurrentRegion();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private IndustrySlotTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	@Override public abstract Object getColumnValue(final IndustrySlot from);
	//XXX - TableFormat.getColumnName() Workaround
	@Override public abstract String getColumnName();
}
