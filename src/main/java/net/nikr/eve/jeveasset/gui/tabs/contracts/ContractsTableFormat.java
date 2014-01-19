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

package net.nikr.eve.jeveasset.gui.tabs.contracts;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsContracts;


public enum ContractsTableFormat implements EnumTableColumn<ContractItem> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnName();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getName();
		}
	},
	STATUS(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnStatus();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getStatusFormated();
		}
	},
	INCLUDED(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnIncluded();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getIncluded();
		}
	},
	QUANTITY(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnQuantity();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getQuantity();
		}
	},
	SINGLETON(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnSingleton();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getSingleton();
		}
	},
	TYPE_ID(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnTypeID();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getTypeID();
		}
	},
	VOLUME(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnVolume();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getVolume();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnPrice();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getPrice();
		}
	},
	BUYOUT(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnBuyout();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getBuyout();
		}
	},
	COLLATERAL(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnCollateral();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getCollateral();
		}
	},
	NUM_DAYS(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnNumDays();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getNumDays();
		}
	},
	REWARD(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnReward();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getReward();
		}
	},
	ISSUER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnIssuer();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getIssuer();
		}
	},
	ISSUER_CORP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnIssuerCorp();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getIssuerCorp();
		}
	},
	ASSIGNEE(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnAssignee();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getAssignee();
		}
	},
	ACCEPTOR(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnAcceptor();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getAcceptor();
		}
	},
	START_STATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnStartStation();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getStartStation();
		}
	},
	END_STATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnEndStation();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getEndStation();
		}
	},
	ISSUED_DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnIssued();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getDateIssued();
		}
	},
	EXPIRED_DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnExpired();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getDateExpired();
		}
	},
	ACCEPTED_DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnAccepted();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getDateAccepted();
		}
	},
	COMPLETED_DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsContracts.get().columnCompleted();
		}
		@Override
		public Object getColumnValue(final ContractItem from) {
			return from.getContract().getDateCompleted();
		}
	};

	private Class<?> type;
	private Comparator<?> comparator;
	private ContractsTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	@Override public ContractItem setColumnValue(final Object baseObject, final Object editedValue) {
		return null;
	}
	@Override
	public String toString() {
		return getColumnName();
	}
	//XXX - TableFormat.getColumnValue(...) Workaround
	@Override public abstract Object getColumnValue(final ContractItem from);
	//XXX - TableFormat.getColumnName() Workaround
	@Override public abstract String getColumnName();
}
