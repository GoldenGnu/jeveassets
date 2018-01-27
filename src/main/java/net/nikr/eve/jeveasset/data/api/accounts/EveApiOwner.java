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

package net.nikr.eve.jeveasset.data.api.accounts;

import com.beimin.eveapi.parser.ApiAuthorization;
import java.util.Date;


public class EveApiOwner extends AbstractOwner implements OwnerType {
	private final EveApiAccount parentAccount;
	private boolean migrated;

	public EveApiOwner(EveApiAccount parentAccount, boolean migrated) {
		this.parentAccount = parentAccount;
		this.migrated = migrated;
	}

	public EveApiOwner(final EveApiAccount parentAccount, final EveApiOwner owner) {
		super(owner);
		this.parentAccount = parentAccount;
		this.migrated = owner.isMigrated();
	}

	public EveApiOwner(final EveApiAccount parentAccount, final String ownerName, final long ownerID) {
		super(ownerName, ownerID);
		this.parentAccount = parentAccount;
		this.migrated = false;
	}

	public EveApiAccount getParentAccount() {
		return parentAccount;
	}

	public boolean isMigrated() {
		return migrated;
	}

	public void setMigrated(boolean migrated) {
		this.migrated = migrated;
	}

	public boolean canMigrate() {
		return !isMigrated();
	}

	@Override
	public boolean isCorporation() {
		return getParentAccount().isCorporation();
	}

	@Override
	public boolean isAssetList() {
		return getParentAccount().isAssetList();
	}

	@Override
	public boolean isAccountBalance() {
		return getParentAccount().isAccountBalance();
	}

	@Override
	public boolean isBlueprints() {
		return isAssetList();
	}

	@Override
	public boolean isIndustryJobs() {
		return getParentAccount().isIndustryJobs();
	}

	@Override
	public boolean isMarketOrders() {
		return getParentAccount().isMarketOrders();
	}

	@Override
	public boolean isTransactions() {
		return getParentAccount().isTransactions();
	}

	@Override
	public boolean isJournal() {
		return getParentAccount().isJournal();
	}

	@Override
	public boolean isContracts() {
		return getParentAccount().isContracts();
	}

	@Override
	public boolean isLocations() {
		return getParentAccount().isLocations();
	}

	@Override
	public boolean isStructures() {
		return false; //Not supported by the XML API
	}

	@Override
	public boolean isShip() {
		return false; //Not supported by the XML API
	}

	@Override
	public boolean isOpenWindows() {
		return false; //Not supported by the XML API
	}

	@Override
	public boolean isAutopilot() {
		return false; //Not supported by the XML API
	}

	@Override
	public boolean isContainerLogs() {
		return false; //Not supported by the XML API
	}

	@Override
	public Date getExpire() {
		return getParentAccount().getExpires();
	}

	@Override
	public String getComparator() {
		return "eveonline" + getAccountName() + getParentAccount().getKeyID();
	}

	@Override
	public boolean isInvalid() {
		return getParentAccount().isInvalid();
	}

	@Override
	public String getAccountName() {
		if (getParentAccount().getName().isEmpty()) {
			return String.valueOf(getParentAccount().getKeyID());
		} else {
			return getParentAccount().getName();
		}
	}

	@Override
	public void setResetAccountName() {
		getParentAccount().setName(String.valueOf(getParentAccount().getKeyID()));
	}

	@Override
	public void setAccountName(String name) {
		getParentAccount().setName(name);
	}

	@Override
	public ApiType getAccountAPI() {
		return ApiType.EVE_ONLINE;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EveApiOwner other = (EveApiOwner) obj;
		if (this.getOwnerID() != other.getOwnerID()) {
			return false;
		}
		if (this.parentAccount != other.parentAccount && (this.parentAccount == null || !this.parentAccount.equals(other.parentAccount))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + (int) (this.getOwnerID() ^ (this.getOwnerID() >>> 32));
		hash = 89 * hash + (this.parentAccount != null ? this.parentAccount.hashCode() : 0);
		return hash;
	}

	public static ApiAuthorization getApiAuthorization(final EveApiAccount account) {
		return new ApiAuthorization(account.getKeyID(), account.getVCode());
	}
	public static ApiAuthorization getApiAuthorization(final EveApiOwner owner) {
		return getApiAuthorization(owner.getParentAccount(), owner.getOwnerID());
	}
	private static ApiAuthorization getApiAuthorization(final EveApiAccount account, final long ownerID) {
		return new ApiAuthorization(account.getKeyID(), ownerID, account.getVCode());
	}
}
