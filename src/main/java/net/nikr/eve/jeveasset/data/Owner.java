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

import com.beimin.eveapi.core.ApiAuthorization;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.gui.tabs.contracts.Contract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractItem;


public class Owner implements Comparable<Owner> {
	private String name;
	private long ownerID;
	private boolean showAssets;
	private Date assetNextUpdate;
	private Date balanceNextUpdate;
	private Date marketOrdersNextUpdate;
	private Date industryJobsNextUpdate;
	private Date contractsNextUpdate;
	private Account parentAccount;
	private List<AccountBalance> accountBalances;
	private List<MarketOrder> marketOrders;
	private List<IndustryJob> industryJobs;
	private Map<Contract, List<ContractItem>> contracts;
	private List<Asset> assets;

	public Owner(final Account parentAccount, final Owner owner) {
		this(parentAccount,
				owner.getName(),
				owner.getOwnerID(),
				owner.isShowAssets(),
				owner.getAssetNextUpdate(),
				owner.getBalanceNextUpdate(),
				owner.getMarketOrdersNextUpdate(),
				owner.getIndustryJobsNextUpdate(),
				owner.getContractsNextUpdate());
		accountBalances = owner.getAccountBalances();
		marketOrders = owner.getMarketOrders();
		industryJobs = owner.getIndustryJobs();
		assets = owner.getAssets();
		contracts = owner.getContracts();
	}

	public Owner(final Account parentAccount, final String name, final long ownerID) {
		this(parentAccount, name, ownerID, true, Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow());
	}

	public Owner(final Account parentAccount, final String name, final long ownerID, final boolean showAssets, final Date assetNextUpdate, final Date balanceNextUpdate, final Date marketOrdersNextUpdate, final Date industryJobsNextUpdate, final Date contractsNextUpdate) {
		this.parentAccount = parentAccount;
		this.name = name;
		this.ownerID = ownerID;
		this.showAssets = showAssets;

		this.assetNextUpdate = assetNextUpdate;
		this.balanceNextUpdate = balanceNextUpdate;
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
		this.industryJobsNextUpdate = industryJobsNextUpdate;
		this.contractsNextUpdate = contractsNextUpdate;
		//Default
		assets = new ArrayList<Asset>();
		accountBalances = new  ArrayList<AccountBalance>();
		marketOrders = new  ArrayList<MarketOrder>();
		industryJobs = new  ArrayList<IndustryJob>();
		contracts = new HashMap<Contract, List<ContractItem>>();
	}

	public void setAccountBalances(final List<AccountBalance> accountBalances) {
		this.accountBalances = accountBalances;
	}

	public void setAssets(final List<Asset> assets) {
		this.assets = assets;
	}

	public void setAssetNextUpdate(final Date nextUpdate) {
		this.assetNextUpdate = nextUpdate;
	}

	public void setBalanceNextUpdate(final Date balanceNextUpdate) {
		this.balanceNextUpdate = balanceNextUpdate;
	}

	public void setContracts(Map<Contract, List<ContractItem>> contracts) {
		this.contracts = contracts;
	}

	public void setContractsNextUpdate(Date contractsNextUpdate) {
		this.contractsNextUpdate = contractsNextUpdate;
	}

	public void setIndustryJobs(final List<IndustryJob> industryJobs) {
		this.industryJobs = industryJobs;
	}

	public void setIndustryJobsNextUpdate(final Date industryJobsNextUpdate) {
		this.industryJobsNextUpdate = industryJobsNextUpdate;
	}

	public void setMarketOrders(final List<MarketOrder> marketOrders) {
		this.marketOrders = marketOrders;
	}

	public void setMarketOrdersNextUpdate(final Date marketOrdersNextUpdate) {
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOwnerID(final long ownerID) {
		this.ownerID = ownerID;
	}

	public void setShowAssets(final boolean showAssets) {
		this.showAssets = showAssets;
	}

	//FIXME - isShowAssets is not a good name - should be isShowOwner
	public boolean isShowAssets() {
		return showAssets;
	}

	public boolean isCorporation() {
		return parentAccount.isCorporation();
	}

	public boolean isCharacter() {
		return parentAccount.isCharacter();
	}

	public List<AccountBalance> getAccountBalances() {
		return accountBalances;
	}

	public List<Asset> getAssets() {
		return assets;
	}

	public Date getAssetNextUpdate() {
		return assetNextUpdate;
	}

	public Date getBalanceNextUpdate() {
		return balanceNextUpdate;
	}

	public Map<Contract, List<ContractItem>> getContracts() {
		return contracts;
	}

	public Date getContractsNextUpdate() {
		return contractsNextUpdate;
	}

	public List<IndustryJob> getIndustryJobs() {
		return industryJobs;
	}

	public Date getIndustryJobsNextUpdate() {
		return industryJobsNextUpdate;
	}

	public List<MarketOrder> getMarketOrders() {
		return marketOrders;
	}

	public Date getMarketOrdersNextUpdate() {
		return marketOrdersNextUpdate;
	}

	public String getName() {
		return name;
	}

	public long getOwnerID() {
		return ownerID;
	}

	public Account getParentAccount() {
		return parentAccount;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Owner other = (Owner) obj;
		if (this.ownerID != other.ownerID) {
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
		hash = 89 * hash + (int) (this.ownerID ^ (this.ownerID >>> 32));
		hash = 89 * hash + (this.parentAccount != null ? this.parentAccount.hashCode() : 0);
		return hash;
	}

	@Override
	public int compareTo(final Owner o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@Override
	public String toString() {
		return getName();
	}

	public static ApiAuthorization getApiAuthorization(final Account account) {
		return new ApiAuthorization(account.getKeyID(), account.getVCode());
	}
	public static ApiAuthorization getApiAuthorization(final Owner owner) {
		return getApiAuthorization(owner.getParentAccount(), owner.getOwnerID());
	}
	private static ApiAuthorization getApiAuthorization(final Account account, final long ownerID) {
		return new ApiAuthorization(account.getKeyID(), ownerID, account.getVCode());
	}
}
