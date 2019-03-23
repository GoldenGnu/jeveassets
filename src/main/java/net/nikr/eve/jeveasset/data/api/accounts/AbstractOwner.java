/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.settings.Settings;


public abstract class AbstractOwner implements OwnerType, Comparable<OwnerType> {

	private List<MyAccountBalance> accountBalances = new ArrayList<>();
	private Set<MyMarketOrder> marketOrders = new HashSet<>();
	private Set<MyTransaction> transactions = new HashSet<>();
	private Set<MyJournal> journal = new HashSet<>();
	private List<MyIndustryJob> industryJobs = new ArrayList<>();
	private Map<MyContract, List<MyContractItem>> contracts = new HashMap<>();
	private List<MyAsset> assets = new ArrayList<>();
	private Map<Long, RawBlueprint> blueprints = new HashMap<>();
	private Map<Integer, String> walletDivisions = new HashMap<>();
	private Map<Integer, String> assetDivisions = new HashMap<>();

	private String ownerName;
	private String corporationName = null;
	private long ownerID;
	private boolean showOwner = true;

	private Date assetLastUpdate = null;
	private Date assetNextUpdate = Settings.getNow();
	private Date balanceLastUpdate = null;
	private Date balanceNextUpdate = Settings.getNow();
	private Date marketOrdersNextUpdate = Settings.getNow();
	private Date journalNextUpdate = Settings.getNow();
	private Date transactionsNextUpdate = Settings.getNow();
	private Date industryJobsNextUpdate = Settings.getNow();
	private Date contractsNextUpdate = Settings.getNow();
	private Date locationsNextUpdate = Settings.getNow();
	private Date blueprintsNextUpdate = Settings.getNow();

	public AbstractOwner() { }

	public AbstractOwner(String ownerName, long ownerID) {
		this.ownerName = ownerName;
		this.ownerID = ownerID;
	}

	public AbstractOwner(AbstractOwner abstractOwner) {
		accountBalances.addAll(abstractOwner.accountBalances);
		marketOrders.addAll(abstractOwner.marketOrders);
		transactions.addAll(abstractOwner.transactions);
		journal.addAll(abstractOwner.journal);
		industryJobs.addAll(abstractOwner.industryJobs);
		contracts.putAll(abstractOwner.contracts);
		assets.addAll(abstractOwner.assets);
		blueprints.putAll(abstractOwner.blueprints);
		this.ownerName = abstractOwner.ownerName;
		this.ownerID = abstractOwner.ownerID;
		this.showOwner = abstractOwner.showOwner;
		this.assetLastUpdate = abstractOwner.assetLastUpdate;
		this.assetNextUpdate = abstractOwner.assetNextUpdate;
		this.balanceLastUpdate = abstractOwner.balanceLastUpdate;
		this.balanceNextUpdate = abstractOwner.balanceNextUpdate;
		this.marketOrdersNextUpdate = abstractOwner.marketOrdersNextUpdate;
		this.journalNextUpdate = abstractOwner.journalNextUpdate;
		this.transactionsNextUpdate = abstractOwner.transactionsNextUpdate;
		this.industryJobsNextUpdate = abstractOwner.industryJobsNextUpdate;
		this.contractsNextUpdate = abstractOwner.contractsNextUpdate;
		this.locationsNextUpdate = abstractOwner.locationsNextUpdate;
		this.blueprintsNextUpdate = abstractOwner.blueprintsNextUpdate;
	}

	@Override
	public synchronized void setAssetNextUpdate(final Date nextUpdate) {
		this.assetNextUpdate = nextUpdate;
	}

	@Override
	public synchronized void setBalanceNextUpdate(final Date balanceNextUpdate) {
		this.balanceNextUpdate = balanceNextUpdate;
	}

	@Override
	public synchronized void setBlueprintsNextUpdate(Date blueprintsNextUpdate) {
		this.blueprintsNextUpdate = blueprintsNextUpdate;
	}

	@Override
	public synchronized void setContractsNextUpdate(final Date contractsNextUpdate) {
		this.contractsNextUpdate = contractsNextUpdate;
	}

	@Override
	public synchronized void setIndustryJobsNextUpdate(final Date industryJobsNextUpdate) {
		this.industryJobsNextUpdate = industryJobsNextUpdate;
	}

	@Override
	public synchronized void setLocationsNextUpdate(Date locationsNextUpdate) {
		this.locationsNextUpdate = locationsNextUpdate;
	}

	@Override
	public synchronized void setMarketOrdersNextUpdate(final Date marketOrdersNextUpdate) {
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
	}

	@Override
	public synchronized void setJournalNextUpdate(Date journalNextUpdate) {
		this.journalNextUpdate = journalNextUpdate;
	}

	@Override
	public synchronized void setTransactionsNextUpdate(final Date transactionsNextUpdate) {
		this.transactionsNextUpdate = transactionsNextUpdate;
	}

	@Override
	public synchronized Date getTransactionsNextUpdate() {
		return transactionsNextUpdate;
	}

	@Override
	public synchronized  Date getAssetNextUpdate() {
		return assetNextUpdate;
	}

	@Override
	public synchronized  Date getBalanceNextUpdate() {
		return balanceNextUpdate;
	}

	@Override
	public synchronized Date getBlueprintsNextUpdate() {
		return blueprintsNextUpdate;
	}

	@Override
	public synchronized Date getContractsNextUpdate() {
		return contractsNextUpdate;
	}

	@Override
	public synchronized Date getIndustryJobsNextUpdate() {
		return industryJobsNextUpdate;
	}

	@Override
	public synchronized Date getLocationsNextUpdate() {
		return locationsNextUpdate;
	}

	@Override
	public synchronized Date getMarketOrdersNextUpdate() {
		return marketOrdersNextUpdate;
	}

	@Override
	public synchronized Date getJournalNextUpdate() {
		return journalNextUpdate;
	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public final void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	@Override
	public final void setAssetLastUpdate(Date assetLastUpdate) {
		this.assetLastUpdate = assetLastUpdate;
	}

	@Override
	public final void setBalanceLastUpdate(Date balanceLastUpdate) {
		this.balanceLastUpdate = balanceLastUpdate;
	}

	@Override
	public final void setOwnerID(long ownerID) {
		this.ownerID = ownerID;
	}

	@Override
	public final String toString() {
		return getOwnerName();
	}

	@Override
	public final boolean isCharacter() {
		return !isCorporation();
	}

	@Override
	public final boolean isExpired() {
		if (getExpire() == null) {
			return false;
		} else {
			return getExpire().before(new Date());
		}
	}

	@Override
	public final String getOwnerName() {
		if (ownerName == null || ownerName.isEmpty()) {
			return "Owner #" + getOwnerID();
		} else {
			return ownerName;
		}
	}

	@Override
	public final boolean isShowOwner() {
		return showOwner;
	}

	@Override
	public final Date getAssetLastUpdate() {
		return assetLastUpdate;
	}

	@Override
	public final Date getBalanceLastUpdate() {
		return balanceLastUpdate;
	}

	@Override
	public final long getOwnerID() {
		return ownerID;
	}

	@Override
	public String getCorporationName() {
		return corporationName;
	}

	@Override
	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}

	@Override
	public final List<MyAccountBalance> getAccountBalances() {
		return accountBalances;
	}

	@Override
	public final Set<MyMarketOrder> getMarketOrders() {
		return marketOrders;
	}

	@Override
	public final Set<MyTransaction> getTransactions() {
		return transactions;
	}

	@Override
	public final Set<MyJournal> getJournal() {
		return journal;
	}

	@Override
	public final List<MyIndustryJob> getIndustryJobs() {
		return industryJobs;
	}

	@Override
	public final Map<MyContract, List<MyContractItem>> getContracts() {
		return contracts;
	}

	@Override
	public final List<MyAsset> getAssets() {
		return assets;
	}

	@Override
	public final Map<Long, RawBlueprint> getBlueprints() {
		return blueprints;
	}

	@Override
	public final void setShowOwner(boolean showOwner) {
		this.showOwner = showOwner;
	}

	@Override
	public final void setBlueprints(Map<Long, RawBlueprint> blueprints) {
		this.blueprints = blueprints;
	}

	@Override
	public final void setIndustryJobs(List<MyIndustryJob> industryJobs) {
		this.industryJobs = industryJobs;
	}

	@Override
	public final void setTransactions(Set<MyTransaction> transactions) {
		this.transactions = transactions;
	}

	@Override
	public final void setJournal(Set<MyJournal> journal) {
		this.journal = journal;
	}

	@Override
	public final void setMarketOrders(Set<MyMarketOrder> marketOrders) {
		this.marketOrders = marketOrders;
	}

	@Override
	public final void setContracts(Map<MyContract, List<MyContractItem>> contracts) {
		this.contracts = contracts;
	}

	@Override
	public final void setAssets(List<MyAsset> assets) {
		this.assets = assets;
	}

	@Override
	public final void setAccountBalances(List<MyAccountBalance> accountBalances) {
		this.accountBalances = accountBalances;
	}

	@Override
	public Map<Integer, String> getWalletDivisions() {
		return walletDivisions;
	}

	@Override
	public void setWalletDivisions(Map<Integer, String> walletDivisions) {
		this.walletDivisions = walletDivisions;
	}

	@Override
	public Map<Integer, String> getAssetDivisions() {
		return assetDivisions;
	}

	@Override
	public void setAssetDivisions(Map<Integer, String> assetDivisions) {
		this.assetDivisions = assetDivisions;
	}

	@Override
	public final int compareTo(final OwnerType o) {
		return this.getOwnerName().compareToIgnoreCase(o.getOwnerName());
	}

}
