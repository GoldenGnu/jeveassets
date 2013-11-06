/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.transaction;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsTransaction;


public enum TransactionTableFormat implements EnumTableColumn<Transaction> {
	DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionDate();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getTransactionDateTime();
		}
	},
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnName();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getItem().getTypeName();
		}
	},
	QUANTITY(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnQuantity();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getQuantity();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnPrice();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getPrice();
		}
	},
	VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnValue();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getValue();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getOwnerName();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnStationName();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getStationName();
		}
	},
	REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnRegion();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getLocation().getRegion();
		}
	},
	CLIENT(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnClientName();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getClientName();
		}
	},
	TYPE(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionType();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getTransactionTypeFormatted();
		}
	},
	TRANSACTION_FOR(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionFor();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getTransactionForFormatted();
		}
	},
	ACCOUNT_KEY(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnAccountKey();
		}
		@Override
		public Object getColumnValue(final Transaction from) {
			return from.getAccountKeyFormated();
		}
	};
	private Class<?> type;
	private Comparator<?> comparator;
	private TransactionTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	public Transaction setColumnValue(final Object baseObject, final Object editedValue) {
		return null;
	}
	//XXX - TableFormat.getColumnValue(...) Workaround
	@Override public abstract Object getColumnValue(final Transaction from);
	//XXX - TableFormat.getColumnName() Workaround
	@Override public abstract String getColumnName();
}