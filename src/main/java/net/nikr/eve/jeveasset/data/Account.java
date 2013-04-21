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

package net.nikr.eve.jeveasset.data;

import com.beimin.eveapi.shared.KeyType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Account {

	public enum AccessMask {
		OPEN(0L),
		ACCOUNT_BALANCE(1L),
		ASSET_LIST(2L),
		INDUSTRY_JOBS(128L),
		MARKET_ORDERS(4096L),
		WALLET_TRANSACTIONS_CHAR(4194304L),
		WALLET_TRANSACTIONS_CORP(2097152L),
		CONTRACTS_CORP(8388608L),
		CONTRACTS_CHAR(67108864L);

		private final long accessMask;

		private AccessMask(long accessMask) {
			this.accessMask = accessMask;
		}

		public long getAccessMask() {
			return accessMask;
		}
	}

	private int keyID;
	private String vCode;
	private String name;
	private Date accountNextUpdate;
	private long accessMask;
	private KeyType type;
	private Date expires;

	private List<Owner> owners = new ArrayList<Owner>();

	public Account(final Account account) {
		this(account.getKeyID(),
				account.getVCode(),
				account.getName(),
				account.getAccountNextUpdate(),
				account.getAccessMask(),
				account.getType(),
				account.getExpires());
		for (Owner owner : account.getOwners()) {
			owners.add(new Owner(this, owner));
		}
	}

	public Account(final int keyID, final String vCode) {
		this(keyID, vCode, Integer.toString(keyID), Settings.getNow(), 0, null, null);
	}

	public Account(final int keyID, final String vCode, final String name, final Date accountNextUpdate, final long accessMask, final KeyType type, final Date expires) {
		this.keyID = keyID;
		this.vCode = vCode;
		this.name = name;
		this.accountNextUpdate = accountNextUpdate;
		this.accessMask = accessMask;
		this.type = type;
		this.expires = expires;
	}

	public String getVCode() {
		return vCode;
	}

	public int getKeyID() {
		return keyID;
	}

	public Date getAccountNextUpdate() {
		return accountNextUpdate;
	}

	public void setAccountNextUpdate(final Date accountNextUpdate) {
		this.accountNextUpdate = accountNextUpdate;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public long getAccessMask() {
		return accessMask;
	}

	public void setAccessMask(final long accessMask) {
		this.accessMask = accessMask;
	}

	public Date getExpires() {
		return expires;
	}

	public boolean isExpired() {
		if (getExpires() == null) {
			return false;
		} else {
			return Settings.getNow().after(getExpires());
		}
	}

	public void setExpires(final Date expires) {
		this.expires = expires;
	}

	public KeyType getType() {
		return type;
	}

	public void setType(final KeyType type) {
		this.type = type;
	}

	/**
	 * Compare Type - Simplified to only consider Corporation or not Corporation.
	 * @param accountType Account | Character | Corporation
	 * @return true if equal (both corp or both not corp) - false if not equal (one corp other not corp)
	 */
	public boolean compareTypes(final KeyType accountType) {
		boolean corp = accountType == KeyType.Corporation; //Enum can be null
		return (isCorporation() == corp);
	}

	public boolean isCorporation() {
		return type == KeyType.Corporation; //Enum can be null
	}

	public boolean isCharacter() {
		return !isCorporation(); //type.equals("Character") || type.equals("Account");
	}

	public List<Owner> getOwners() {
		return owners;
	}

	public void setvCode(final String vCode) {
		this.vCode = vCode;
	}

	public void setOwners(final List<Owner> owners) {
		this.owners = owners;
	}

	public boolean isAccountBalance() {
		return ((getAccessMask() & AccessMask.ACCOUNT_BALANCE.getAccessMask()) == AccessMask.ACCOUNT_BALANCE.getAccessMask());
	}

	public boolean isAssetList() {
		return ((getAccessMask() & AccessMask.ASSET_LIST.getAccessMask()) == AccessMask.ASSET_LIST.getAccessMask());
	}

	public boolean isMarketOrders() {
		return ((getAccessMask() & AccessMask.MARKET_ORDERS.getAccessMask()) == AccessMask.MARKET_ORDERS.getAccessMask());
	}

	public boolean isIndustryJobs() {
		return ((getAccessMask() & AccessMask.INDUSTRY_JOBS.getAccessMask()) == AccessMask.INDUSTRY_JOBS.getAccessMask());
	}

	public boolean isContracts() {
		if (isCorporation()) {
			return ((getAccessMask() & AccessMask.CONTRACTS_CORP.getAccessMask()) == AccessMask.CONTRACTS_CORP.getAccessMask());
		} else {
			return ((getAccessMask() & AccessMask.CONTRACTS_CHAR.getAccessMask()) == AccessMask.CONTRACTS_CHAR.getAccessMask());
		}
	}

	public boolean isWalletTransactions() {
		if (isCorporation()) {
			return ((getAccessMask() & AccessMask.WALLET_TRANSACTIONS_CORP.getAccessMask()) == AccessMask.WALLET_TRANSACTIONS_CORP.getAccessMask());
		} else {
			return ((getAccessMask() & AccessMask.WALLET_TRANSACTIONS_CHAR.getAccessMask()) == AccessMask.WALLET_TRANSACTIONS_CHAR.getAccessMask());
		}
	}

	@Override
	public String toString() {
		return keyID + "::" + vCode;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Account other = (Account) obj;
		if (this.keyID != other.keyID) {
			return false;
		}
		if ((this.vCode == null) ? (other.vCode != null) : !this.vCode.equals(other.vCode)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + this.keyID;
		hash = 53 * hash + (this.vCode != null ? this.vCode.hashCode() : 0);
		return hash;
	}

	public void setKeyID(final int keyID) {
		this.keyID = keyID;
	}
}
