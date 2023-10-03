/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.contracts;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.LongInt;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Runs;
import net.nikr.eve.jeveasset.gui.shared.table.containers.YesNo;
import net.nikr.eve.jeveasset.i18n.TabsContracts;


public enum ContractsTableFormat implements EnumTableColumn<MyContractItem> {
	NAME(String.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnName();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getName();
		}
	},
	TITLE(String.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnTitle();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getTitle();
		}
	},
	TYPE(String.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnType();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getTypeName();
		}
	},
	STATUS(String.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnStatus();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getStatusFormatted();
		}
	},
	AVAILABILITY(String.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnAvailability();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getAvailabilityFormatted();
		}
	},
	INCLUDED(String.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnIncluded();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getIncluded();
		}
	},
	QUANTITY(Long.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnQuantity();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getQuantity();
		}
	},
	SINGLETON(String.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnSingleton();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getSingleton();
		}
	},
	TYPE_ID(Integer.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnTypeID();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getTypeID();
		}
	},
	VOLUME(Double.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnVolume();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getVolume();
		}
	},
	Runs(Runs.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnRuns();
		}
		@Override
		public String getColumnToolTip() {
			return TabsContracts.get().columnRunsToolTip();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return new Runs(from.getRuns());
		}
	},
	ME(Integer.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnMaterialEfficiency();
		}
		@Override
		public String getColumnToolTip() {
			return TabsContracts.get().columnMaterialEfficiencyToolTip();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getME();
		}
	},
	TE(Integer.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnTimeEfficiency();
		}
		@Override
		public String getColumnToolTip() {
			return TabsContracts.get().columnTimeEfficiencyToolTip();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getTE();
		}
	},
	MARKET_PRICE(Double.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnMarketPrice();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getDynamicPrice();
		}
	},
	PRICE_REPROCESSED(Double.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnPriceReprocessed();
		}
		@Override
		public String getColumnToolTip() {
			return TabsContracts.get().columnPriceReprocessedToolTip();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getItem().getPriceReprocessed();
		}
	},
	PRICE_MANUFACTURING(Double.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnPriceManufacturing();
		}
		@Override
		public String getColumnToolTip() {
			return TabsContracts.get().columnPriceManufacturingToolTip();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getItem().getPriceManufacturing();
		}
	},
	MARKET_VALUE(Double.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnMarketValue();
		}
		@Override
		public String getColumnToolTip() {
			return TabsContracts.get().columnMarketValueToolTip();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getDynamicPrice() * from.getQuantity();
		}
	},
	REPROCESSED_VALUE(Double.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnValueReprocessed();
		}
		@Override
		public String getColumnToolTip() {
			return TabsContracts.get().columnValueReprocessedToolTip();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getItem().getPriceReprocessed() * from.getQuantity();
		}
	},
	MANUFACTURING_VALUE(Double.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnValueManufacturing();
		}
		@Override
		public String getColumnToolTip() {
			return TabsContracts.get().columnValueManufacturingToolTip();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getItem().getPriceManufacturing() * from.getQuantity();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnPrice();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getPrice();
		}
	},
	BUYOUT(Double.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnBuyout();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getBuyout();
		}
	},
	COLLATERAL(Double.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnCollateral();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getCollateral();
		}
	},
	NUM_DAYS(Integer.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnNumDays();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getDaysToComplete();
		}
	},
	REWARD(Double.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnReward();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getReward();
		}
	},
	FOR_CORP(YesNo.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnForCorp();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return new YesNo(from.getContract().isForCorp());
		}
	},
	ISSUER(String.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnIssuer();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getIssuer();
		}
	},
	ISSUER_CORP(String.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnIssuerCorp();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getIssuerCorp();
		}
	},
	ASSIGNEE(String.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnAssignee();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getAssignee();
		}
	},
	ACCEPTOR(String.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnAcceptor();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getAcceptor();
		}
	},
	OWNED(YesNo.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnOwned();
		}
		@Override
		public String getColumnToolTip() {
			return TabsContracts.get().columnOwnedToolTip();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return new YesNo(from.getContract().isOwned());
		}
	},
	START_STATION(String.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnStartStation();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getStartLocation();
		}
	},
	END_STATION(String.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnEndStation();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getEndLocation();
		}
	},
	ISSUED_DATE(Date.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnIssued();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getDateIssued();
		}
	},
	EXPIRED_DATE(Date.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnExpired();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getDateExpired();
		}
	},
	ACCEPTED_DATE(Date.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnAccepted();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getDateAccepted();
		}
	},
	COMPLETED_DATE(Date.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnCompleted();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getDateCompleted();
		}
	},
	CONTRACT_ID(Integer.class, GlazedLists.comparableComparator(), true) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnContractID();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return from.getContract().getContractID();
		}
	},
	RECORD_ID(LongInt.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnRecordID();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return new LongInt(from.getRecordID());
		}
	},
	ITEM_ID(LongInt.class, GlazedLists.comparableComparator(), false) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnItemID();
		}
		@Override
		public Object getColumnValue(final MyContractItem from) {
			return new LongInt(from.getItemID());
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;
	private final boolean contract;

	private ContractsTableFormat(final Class<?> type, final Comparator<?> comparator, boolean contract) {
		this.type = type;
		this.comparator = comparator;
		this.contract = contract;
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
	public boolean isContract() {
		return contract;
	}
}
