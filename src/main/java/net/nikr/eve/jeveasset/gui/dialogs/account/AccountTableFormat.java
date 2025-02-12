/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import java.util.Comparator;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.ExpirerDate;
import net.nikr.eve.jeveasset.gui.shared.table.containers.YesNo;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;


public enum AccountTableFormat implements EnumTableColumn<OwnerType> {
	SHOW_ASSETS(Boolean.class) {
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
	NAME(String.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatName();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return from.getOwnerName();
		}
	},
	CORPORATION(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatCorporation();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isCorporation());
		}
	},
	ASSET_LIST(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatAssetList();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isAssetList());
		}
	},
	ACCOUNT_BALANCE(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatAccountBalance();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isAccountBalance());
		}
	},
	INDUSTRY_JOBS(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatIndustryJobs();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isIndustryJobs());
		}
	},
	MARKET_ORDERS(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatMarketOrders();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isMarketOrders());
		}
	},
	TRANSACTIONS(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatTransactions();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isTransactions());
		}
	},
	JOURNAL(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatJournal();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isJournal());
		}
	},
	CONTRACTS(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatContracts();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isContracts());
		}
	},
	LOCATIONS(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatLocations();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isLocations());
		}
	},
	STRUCTURES(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatStructures();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isStructures());
		}
	},
	MARKET_STRUCTURES(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatMarketStructures();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isMarketStructures());
		}
	},
	BLUEPRINTS(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatBlueprints();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isBlueprints());
		}
	},
	DIVISIONS(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatDivisions();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isDivisions());
		}
	},
	SHIP(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatShip();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isShip());
		}
	},
	PLANETARY_INTERACTION(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatPlanetaryInteraction();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isPlanetaryInteraction());
		}
	},
	OPEN_WINDOWS(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatOpenWindows();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isOpenWindows());
		}
	},
	AUTOPILOT(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatAutopilot();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isAutopilot());
		}
	},
	SKILLS(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatSkills();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isSkills());
		}
	},
	MINING(YesNo.class) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatMining();
		}
		@Override
		public Object getColumnValue(final OwnerType from) {
			return new YesNo(from.isMining());
		}
	},
	EXPIRES(ExpirerDate.class) {
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
	private AccountTableFormat(final Class<?> type) {
		this.type = type;
		this.comparator = EnumTableColumn.getComparator(type);
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
