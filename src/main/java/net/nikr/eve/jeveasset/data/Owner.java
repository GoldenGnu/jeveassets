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

package net.nikr.eve.jeveasset.data;

import com.beimin.eveapi.core.ApiAuthorization;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.Contract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.journal.Journal;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.transaction.Transaction;


public class Owner implements Comparable<Owner> {
	private String name;
	private long ownerID;
	private boolean showOwner;
	private Date assetLastUpdate;
	private Date assetNextUpdate;
	private Date balanceNextUpdate;
	private Date marketOrdersNextUpdate;
	private Date journalNextUpdate;
	private Date transactionsNextUpdate;
	private Date industryJobsNextUpdate;
	private Date contractsNextUpdate;
	private Date locationsNextUpdate;
	private Account parentAccount;
	private List<AccountBalance> accountBalances;
	private List<MarketOrder> marketOrders;
	private Map<Long, Transaction> transactions;
	private Map<Long, Journal> journal;
	private List<IndustryJob> industryJobs;
	private Map<Contract, List<ContractItem>> contracts;
	private List<Asset> assets;

	public Owner(final Account parentAccount, final Owner owner) {
		this(parentAccount,
				owner.getName(),
				owner.getOwnerID(),
				owner.isShowOwner(),
				owner.getAssetLastUpdate(),
				owner.getAssetNextUpdate(),
				owner.getBalanceNextUpdate(),
				owner.getMarketOrdersNextUpdate(),
				owner.getJournalNextUpdate(),
				owner.getTransactionsNextUpdate(),
				owner.getIndustryJobsNextUpdate(),
				owner.getContractsNextUpdate(),
				owner.getLocationsNextUpdate());
		accountBalances = owner.getAccountBalances();
		marketOrders = owner.getMarketOrders();
		industryJobs = owner.getIndustryJobs();
		assets = owner.getAssets();
		contracts = owner.getContracts();
		transactions = owner.getTransactions();
		journal = owner.getJournal();
	}

	public Owner(final Account parentAccount, final String name, final long ownerID) {
		this(parentAccount, name, ownerID, true, null, Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow());
	}

	public Owner(final Account parentAccount, final String name, final long ownerID, final boolean showOwner, final Date assetLastUpdate, final Date assetNextUpdate, final Date balanceNextUpdate, final Date marketOrdersNextUpdate, final Date journalNextUpdate, final Date transactionsNextUpdate, final Date industryJobsNextUpdate, final Date contractsNextUpdate, final Date locationsNextUpdate) {
		this.parentAccount = parentAccount;
		this.name = name;
		this.ownerID = ownerID;
		this.showOwner = showOwner;
		this.assetLastUpdate = assetLastUpdate;
		this.assetNextUpdate = assetNextUpdate;
		this.balanceNextUpdate = balanceNextUpdate;
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
		this.journalNextUpdate = journalNextUpdate;
		this.transactionsNextUpdate = transactionsNextUpdate;
		this.industryJobsNextUpdate = industryJobsNextUpdate;
		this.contractsNextUpdate = contractsNextUpdate;
		this.locationsNextUpdate = locationsNextUpdate;
		//Default
		assets = new ArrayList<Asset>();
		accountBalances = new  ArrayList<AccountBalance>();
		marketOrders = new  ArrayList<MarketOrder>();
		transactions = new HashMap<Long, Transaction>();
		industryJobs = new  ArrayList<IndustryJob>();
		contracts = new HashMap<Contract, List<ContractItem>>();
		journal = new HashMap<Long, Journal>();
	}

	public void setAccountBalances(final List<AccountBalance> accountBalances) {
		this.accountBalances = accountBalances;
	}

	public void setAssets(final List<Asset> assets) {
		this.assets = assets;
	}

	public void setAssetLastUpdate(final Date assetLastUpdate) {
		this.assetLastUpdate = assetLastUpdate;
	}

	public void setAssetNextUpdate(final Date nextUpdate) {
		this.assetNextUpdate = nextUpdate;
	}

	public void setBalanceNextUpdate(final Date balanceNextUpdate) {
		this.balanceNextUpdate = balanceNextUpdate;
	}

	public void setContracts(final Map<Contract, List<ContractItem>> contracts) {
		this.contracts = contracts;
	}

	public void setContractsNextUpdate(final Date contractsNextUpdate) {
		this.contractsNextUpdate = contractsNextUpdate;
	}

	public void setIndustryJobs(final List<IndustryJob> industryJobs) {
		this.industryJobs = industryJobs;
	}

	public void setIndustryJobsNextUpdate(final Date industryJobsNextUpdate) {
		this.industryJobsNextUpdate = industryJobsNextUpdate;
	}

	public void setLocationsNextUpdate(Date locationsNextUpdate) {
		this.locationsNextUpdate = locationsNextUpdate;
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

	public void setShowOwner(final boolean showOwner) {
		this.showOwner = showOwner;
	}

	public void setJournal(final Map<Long, Journal> journal) {
		this.journal = journal;
	}

	public void setJournalNextUpdate(Date journalNextUpdate) {
		this.journalNextUpdate = journalNextUpdate;
	}

 	public void setTransactions(final Map<Long, Transaction> transactions) {
		this.transactions = transactions;
	}

	public void setTransactionsNextUpdate(final Date transactionsNextUpdate) {
		this.transactionsNextUpdate = transactionsNextUpdate;
	}

	public boolean isShowOwner() {
		return showOwner;
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

	public Date getAssetLastUpdate() {
		return assetLastUpdate;
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

	public Date getLocationsNextUpdate() {
		return locationsNextUpdate;
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

	public Map<Long, Journal> getJournal() {
		return journal;
	}

	public Date getJournalNextUpdate() {
		return journalNextUpdate;
	}

	public Map<Long, Transaction> getTransactions() {
 		return transactions;
	}

	public Date getTransactionsNextUpdate() {
		return transactionsNextUpdate;
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
