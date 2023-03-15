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

package net.nikr.eve.jeveasset.gui.tabs.transaction;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.LongInt;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.i18n.TabsTransaction;


public enum TransactionTableFormat implements EnumTableColumn<MyTransaction> {
	DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionDate();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getDate();
		}
	},
	ADDED(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnAdded();
		}
		@Override
		public String getColumnToolTip() {
			return TabsTransaction.get().columnAddedToolTip();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getAdded();
		}
	},
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnName();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getItem().getTypeName();
		}
	},
	QUANTITY(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnQuantity();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getQuantity();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnPrice();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getPrice();
		}
	},
	TAX(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTax();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getTax();
		}
	},
	VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnValue();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getValue();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getOwnerName();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnStationName();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getLocation();
		}
	},
	SYSTEM(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnSystem();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getLocation().getSystem();
		}
	},
	CONSTELLATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnConstellation();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getLocation().getConstellation();
		}
	},
	REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnRegion();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getLocation().getRegion();
		}
	},
	TRANSACTION_PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionPrice();
		}
		@Override
		public String getColumnToolTip() {
			return TabsTransaction.get().columnTransactionPriceToolTip();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getTransactionPrice();
		}
	},
	TRANSACTION_MARGIN(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionMargin();
		}
		@Override
		public String getColumnToolTip() {
			return TabsTransaction.get().columnTransactionMarginToolTip();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getTransactionMargin();
		}
	},
	TRANSACTION_PROFIT(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionProfitDifference();
		}
		@Override
		public String getColumnToolTip() {
			return TabsTransaction.get().columnTransactionProfitDifferenceToolTip();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getTransactionProfitDifference();
		}
	},
	TRANSACTION_PROFIT_PERCENT(Percent.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionProfitPercent();
		}
		@Override
		public String getColumnToolTip() {
			return TabsTransaction.get().columnTransactionProfitPercentToolTip();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getTransactionProfitPercent();
		}
	},
	CLIENT(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnClientName();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getClientName();
		}
	},
	TYPE(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionType();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getTransactionTypeFormatted();
		}
	},
	TRANSACTION_FOR(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionFor();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getTransactionForFormatted();
		}
	},
	ACCOUNT_KEY(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnAccountKey();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getAccountKeyFormatted();
		}
	},
	VOLUME(Float.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnVolume();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return from.getItem().getVolume();
		}
	},
	TRANSACTION_ID(LongInt.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTransaction.get().columnTransactionID();
		}
		@Override
		public Object getColumnValue(final MyTransaction from) {
			return new LongInt(from.getTransactionID());
		}
		@Override
		public boolean isShowDefault() {
			return false;
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

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

}