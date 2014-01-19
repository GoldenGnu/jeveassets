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

package net.nikr.eve.jeveasset.gui.tabs.journal;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsJournal;


public enum JournalTableFormat implements EnumTableColumn<Journal> {
	DATE(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnDate();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getDate();
		}
	},
	REF_TYPE(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnRefType();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getRefTypeFormated();
		}
	},
	AMOUNT(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnAmount();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getAmount();
		}
	},
	BALANCE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnBalance();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getBalance();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getOwnerName();
		}
	},
	OWNER_NAME_1(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnOwnerName1();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getOwnerName1();
		}
	},
	OWNER_NAME_2(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnOwnerName2();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getOwnerName2();
		}
	},
	ACCOUNT_KEY(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnAccountKey();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getAccountKeyFormated();
		}
	},
	TAX_AMOUNT(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnTaxAmount();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getTaxAmount();
		}
	},
	REASON(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnReason();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getReason();
		}
	};
	/*
	ARG_ID_1(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnArgID1();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getArgID1();
		}
	},
	ARG_NAME_1(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsJournal.get().columnArgName1();
		}
		@Override
		public Object getColumnValue(final Journal from) {
			return from.getArgName1();
		}
	};
	*/
	private Class<?> type;
	private Comparator<?> comparator;
	private JournalTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	public Journal setColumnValue(final Object baseObject, final Object editedValue) {
		return null;
	}
	//XXX - TableFormat.getColumnValue(...) Workaround
	@Override public abstract Object getColumnValue(final Journal from);
	//XXX - TableFormat.getColumnName() Workaround
	@Override public abstract String getColumnName();
}