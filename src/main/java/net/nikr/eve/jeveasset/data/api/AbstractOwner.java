/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.api;

import com.beimin.eveapi.model.shared.Blueprint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.MyAccountBalance;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.journal.MyJournal;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.transaction.MyTransaction;


public abstract class AbstractOwner implements OwnerType, Comparable<OwnerType> {

	private final List<MyAccountBalance> accountBalances = new ArrayList<MyAccountBalance>();
	private final List<MyMarketOrder> marketOrders = new ArrayList<MyMarketOrder>();
	private final Set<MyTransaction> transactions = new HashSet<MyTransaction>();
	private final Set<MyJournal> journal = new HashSet<MyJournal>();
	private final List<MyIndustryJob> industryJobs = new ArrayList<MyIndustryJob>();
	private final Map<MyContract, List<MyContractItem>> contracts = new HashMap<MyContract, List<MyContractItem>>();
	private final List<MyAsset> assets = new ArrayList<MyAsset>();
	private final Map<Long, Blueprint> blueprints = new HashMap<Long, Blueprint>();

	private String ownerName;
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
	public void setAssetNextUpdate(final Date nextUpdate) {
		this.assetNextUpdate = nextUpdate;
	}

	@Override
	public void setBalanceNextUpdate(final Date balanceNextUpdate) {
		this.balanceNextUpdate = balanceNextUpdate;
	}

	@Override
	public void setBlueprintsNextUpdate(Date blueprintsNextUpdate) {
		this.blueprintsNextUpdate = blueprintsNextUpdate;
	}

	@Override
	public void setContractsNextUpdate(final Date contractsNextUpdate) {
		this.contractsNextUpdate = contractsNextUpdate;
	}

	@Override
	public void setIndustryJobsNextUpdate(final Date industryJobsNextUpdate) {
		this.industryJobsNextUpdate = industryJobsNextUpdate;
	}

	@Override
	public void setLocationsNextUpdate(Date locationsNextUpdate) {
		this.locationsNextUpdate = locationsNextUpdate;
	}

	@Override
	public void setMarketOrdersNextUpdate(final Date marketOrdersNextUpdate) {
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
	}

	@Override
	public void setJournalNextUpdate(Date journalNextUpdate) {
		this.journalNextUpdate = journalNextUpdate;
	}

	@Override
	public void setTransactionsNextUpdate(final Date transactionsNextUpdate) {
		this.transactionsNextUpdate = transactionsNextUpdate;
	}

	@Override
	public Date getTransactionsNextUpdate() {
		return transactionsNextUpdate;
	}

	@Override
	public Date getAssetNextUpdate() {
		return assetNextUpdate;
	}

	@Override
	public Date getBalanceNextUpdate() {
		return balanceNextUpdate;
	}

	@Override
	public Date getBlueprintsNextUpdate() {
		return blueprintsNextUpdate;
	}

	@Override
	public Date getContractsNextUpdate() {
		return contractsNextUpdate;
	}

	@Override
	public Date getIndustryJobsNextUpdate() {
		return industryJobsNextUpdate;
	}

	@Override
	public Date getLocationsNextUpdate() {
		return locationsNextUpdate;
	}

	@Override
	public Date getMarketOrdersNextUpdate() {
		return marketOrdersNextUpdate;
	}

	@Override
	public Date getJournalNextUpdate() {
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
	public final  boolean isShowOwner() {
		return showOwner;
	}

	@Override
	public final Date getAssetLastUpdate() {
		return assetLastUpdate;
	}

	@Override
	public final  Date getBalanceLastUpdate() {
		return balanceLastUpdate;
	}

	@Override
	public final long getOwnerID() {
		return ownerID;
	}

	@Override
	public final List<MyAccountBalance> getAccountBalances() {
		return accountBalances;
	}

	@Override
	public final List<MyMarketOrder> getMarketOrders() {
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
	public final Map<Long, Blueprint> getBlueprints() {
		return blueprints;
	}

	@Override
	public final void setShowOwner(boolean showOwner) {
		this.showOwner = showOwner;
	}

	@Override
	public final void setBlueprints(Map<Long, Blueprint> blueprints) {
		this.blueprints.clear();
		this.blueprints.putAll(blueprints);
	}

	@Override
	public final void setIndustryJobs(List<MyIndustryJob> industryJobs) {
		this.industryJobs.clear();
		this.industryJobs.addAll(industryJobs);
	}

	@Override
	public final void setTransactions(Set<MyTransaction> transactions) {
		this.transactions.clear();
		this.transactions.addAll(transactions);
	}

	@Override
	public final void setJournal(Set<MyJournal> journal) {
		this.journal.clear();
		this.journal.addAll(journal);
	}

	@Override
	public final void setMarketOrders(List<MyMarketOrder> marketOrders) {
		this.marketOrders.clear();
		this.marketOrders.addAll(marketOrders);
	}

	@Override
	public final void setContracts(Map<MyContract, List<MyContractItem>> contracts) {
		this.contracts.clear();
		this.contracts.putAll(contracts);
	}

	@Override
	public final void setAssets(List<MyAsset> assets) {
		this.assets.clear();
		this.assets.addAll(assets);
	}

	@Override
	public final void setAccountBalances(List<MyAccountBalance> accountBalances) {
		this.accountBalances.clear();
		this.accountBalances.addAll(accountBalances);
	}

	@Override
	public final int compareTo(final OwnerType o) {
		return this.getOwnerName().compareToIgnoreCase(o.getOwnerName());
	}

}
