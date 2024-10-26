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

package net.nikr.eve.jeveasset.gui.tabs.slots;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsSlots;


public enum SlotsTableFormat implements EnumTableColumn<Slots> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getName();
		}
	},
	MANUFACTURING_DONE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnManufacturingDone();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnManufacturingDone();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getManufacturingDone();
		}
	},
	MANUFACTURING_FREE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnManufacturingFree();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnManufacturingFree();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getManufacturingFree();
		}
	},
	MANUFACTURING_ACTIVE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnManufacturingActive();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnManufacturingActive();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getManufacturingActive();
		}
	},
	MANUFACTURING_MAX(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnManufacturingMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnManufacturingMax();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getManufacturingMax();
		}
	},
	RESEARCH_DONE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnResearchDone();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnResearchDone();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getResearchDone();
		}
	},
	RESEARCH_FREE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnResearchFree();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnResearchFree();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getResearchFree();
		}
	},
	RESEARCH_ACTIVE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnResearchActive();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnResearchActive();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getResearchActive();
		}
	},
	RESEARCH_MAX(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnResearchMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnResearchMax();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getResearchMax();
		}
	},
	REACTIONS_DONE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnReactionsDone();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnReactionsDone();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getReactionsDone();
		}
	},
	REACTIONS_FREE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnReactionsFree();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnReactionsFree();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getReactionsFree();
		}
	},
	REACTIONS_ACTIVE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnReactionsActive();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnReactionsActive();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getReactionsActive();
		}
	},
	REACTIONS_MAX(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnReactionsMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnReactionsMax();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getReactionsMax();
		}
	},
	MARKET_ORDERS_FREE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnMarketOrdersFree();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnMarketOrdersFree();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getMarketOrdersFree();
		}
	},
	MARKET_ORDERS_ACTIVE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnMarketOrdersActive();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnMarketOrdersActive();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getMarketOrdersActive();
		}
	},
	MARKET_ORDERS_MAX(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnMarketOrdersMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnMarketOrdersMax();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getMarketOrdersMax();
		}
	},
	CONTRACT_CHARACTER_FREE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnContractCharacterFree();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnContractCharacterFree();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getContractCharacterFree();
		}
	},
	CONTRACT_CHARACTER_ACTIVE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnContractCharacterActive();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnContractCharacterActive();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getContractCharacterActive();
		}
	},
	CONTRACT_CHARACTER_MAX(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnContractCharacterMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnContractCharacterMax();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getContractCharacterMax();
		}
	},
	CONTRACT_CORPORATION_FREE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnContractCorporationFree();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnContractCorporationFree();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getContractCorporationFree();
		}
	},
	CONTRACT_CORPORATION_ACTIVE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnContractCorporationActive();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnContractCorporationActive();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getContractCorporationActive();
		}
	},
	CONTRACT_CORPORATION_MAX(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnContractCorporationMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsSlots.get().columnContractCorporationMax();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getContractCorporationMax();
		}
	},
	CURRENT_SHIP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnCurrentShip();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getActiveShip();
		}
	},
	CURRENT_STATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnCurrentStation();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getCurrentStation();
		}
	},
	CURRENT_SYSTEM(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnCurrentSystem();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getCurrentSystem();
		}
	},
	CURRENT_CONSTELLATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnCurrentConstellation();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getCurrentConstellation();
		}
	},
	CURRENT_REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSlots.get().columnCurrentRegion();
		}
		@Override
		public Object getColumnValue(final Slots from) {
			return from.getCurrentRegion();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private SlotsTableFormat(final Class<?> type, final Comparator<?> comparator) {
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

}
