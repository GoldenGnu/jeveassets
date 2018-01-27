/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.account;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.ExpirerDate;
import net.nikr.eve.jeveasset.gui.shared.table.containers.YesNo;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;


public enum AccountTableFormat implements EnumTableColumn<OwnerType> {
	SHOW_ASSETS(Boolean.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return "";
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return from.isShowOwner() && !from.getOwnerName().equals(DialoguesAccount.get().noOwners());
		}
		@Override
		public boolean isColumnEditable(final Object baseObject) {
			if (baseObject instanceof OwnerType) {
				OwnerType owner = (OwnerType) baseObject;
				return !owner.getOwnerName().equals(DialoguesAccount.get().noOwners());
			}
			return true;
		}
		@Override
		public boolean setColumnValue(final Object baseObject, final Object editedValue) {
			if ((editedValue instanceof Boolean) && (baseObject instanceof OwnerType)) {
				OwnerType owner = (OwnerType) baseObject;
				boolean before = owner.isShowOwner();
				boolean after = (Boolean) editedValue;
				owner.setShowOwner(after);
				return before != after;
			}
			return false;
		}
	},
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatName();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return from.getOwnerName();
		}
	},
	CORPORATION(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatCorporation();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isCorporation());
		}
	},
	ASSET_LIST(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatAssetList();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isAssetList());
		}
	},
	ACCOUNT_BALANCE(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatAccountBalance();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isAccountBalance());
		}
	},
	INDUSTRY_JOBS(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatIndustryJobs();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isIndustryJobs());
		}
	},
	MARKET_ORDERS(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatMarketOrders();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isMarketOrders());
		}
	},
	TRANSACTIONS(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatTransactions();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isTransactions());
		}
	},
	JOURNAL(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatJournal();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isJournal());
		}
	},
	CONTRACTS(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatContracts();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isContracts());
		}
	},
	LOCATIONS(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatLocations();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isLocations());
		}
	},
	STRUCTURES(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatStructures();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isStructures());
		}
	},
	BLUEPRINTS(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatBlueprints();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isBlueprints());
		}
	},
	SHIP(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatShip();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isShip());
		}
	},
	OPEN_WINDOWS(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatOpenWindows();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isOpenWindows());
		}
	},
	AUTOPILOT(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatAutopilot();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isAutopilot());
		}
	},
	CONTAINER_LOGS(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatContainerLogs();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isContainerLogs());
		}
	},
	EXPIRES(ExpirerDate.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatExpires();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new ExpirerDate(from.getExpire());
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;
	private AccountTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	
}
