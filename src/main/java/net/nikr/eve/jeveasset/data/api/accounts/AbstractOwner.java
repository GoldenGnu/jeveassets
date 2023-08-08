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
import net.nikr.eve.jeveasset.data.api.my.MyExtraction;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.api.my.MyShip;
import net.nikr.eve.jeveasset.data.api.my.MySkill;
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
	private List<MySkill> skills = new ArrayList<>();
	private List<MyMining> mining = new ArrayList<>();
	private List<MyExtraction> extractions = new ArrayList<>();
	private Long totalSkillPoints = null;
	private Integer unallocatedSkillPoints = null;

	private String ownerName;
	private String corporationName = null;
	private long ownerID;
	private boolean showOwner = true;
	private boolean invalid = false;
	private MyShip activeShip;

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
	private Date bookmarksNextUpdate = Settings.getNow();
	private Date skillsNextUpdate = Settings.getNow();
	private Date miningNextUpdate = Settings.getNow();

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
		walletDivisions.putAll(abstractOwner.walletDivisions);
		assetDivisions.putAll(abstractOwner.assetDivisions);
		skills.addAll(abstractOwner.skills);
		mining.addAll(abstractOwner.mining);
		extractions.addAll(abstractOwner.extractions);
		this.totalSkillPoints = abstractOwner.totalSkillPoints;
		this.unallocatedSkillPoints = abstractOwner.unallocatedSkillPoints;
		this.ownerName = abstractOwner.ownerName;
		this.ownerID = abstractOwner.ownerID;
		this.showOwner = abstractOwner.showOwner;
		this.invalid = abstractOwner.invalid;
		this.activeShip = abstractOwner.activeShip;
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
		this.bookmarksNextUpdate = abstractOwner.bookmarksNextUpdate;
		this.skillsNextUpdate = abstractOwner.skillsNextUpdate;
		this.miningNextUpdate = abstractOwner.miningNextUpdate;
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
	public synchronized void setBlueprintsNextUpdate(final Date blueprintsNextUpdate) {
		this.blueprintsNextUpdate = blueprintsNextUpdate;
	}

	@Override
	public synchronized void setBookmarksNextUpdate(Date bookmarksNextUpdate) {
		this.bookmarksNextUpdate = bookmarksNextUpdate;
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
	public synchronized void setLocationsNextUpdate(final Date locationsNextUpdate) {
		this.locationsNextUpdate = locationsNextUpdate;
	}

	@Override
	public synchronized void setMarketOrdersNextUpdate(final Date marketOrdersNextUpdate) {
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
	}

	@Override
	public synchronized void setJournalNextUpdate(final Date journalNextUpdate) {
		this.journalNextUpdate = journalNextUpdate;
	}

	@Override
	public void setSkillsNextUpdate(Date skillsNextUpdate) {
		this.skillsNextUpdate = skillsNextUpdate;
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
	public synchronized Date getAssetNextUpdate() {
		return assetNextUpdate;
	}

	@Override
	public synchronized Date getBalanceNextUpdate() {
		return balanceNextUpdate;
	}

	@Override
	public synchronized Date getBlueprintsNextUpdate() {
		return blueprintsNextUpdate;
	}

	@Override
	public synchronized Date getBookmarksNextUpdate() {
		return bookmarksNextUpdate;
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
	public Date getSkillsNextUpdate() {
		return skillsNextUpdate;
	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public final void setOwnerName(final String ownerName) {
		this.ownerName = ownerName;
	}

	@Override
	public final void setAssetLastUpdate(final Date assetLastUpdate) {
		this.assetLastUpdate = assetLastUpdate;
	}

	@Override
	public final void setBalanceLastUpdate(final Date balanceLastUpdate) {
		this.balanceLastUpdate = balanceLastUpdate;
	}

	@Override
	public final void setOwnerID(final long ownerID) {
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
	public synchronized boolean isInvalid() {
		return invalid;
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
	public MyShip getActiveShip() {
		return activeShip;
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
	public synchronized final Set<MyMarketOrder> getMarketOrders() {
		return marketOrders;
	}

	@Override
	public Date getMiningNextUpdate() {
		return miningNextUpdate;
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
	public final synchronized List<MyAsset> getAssets() {
		return assets;
	}

	public final synchronized void addAsset(MyAsset asset) {
		assets.add(asset);
	}

	public final synchronized void removeAssets(List<MyAsset> remove) {
		assets.removeAll(remove);
	}

	@Override
	public final Map<Long, RawBlueprint> getBlueprints() {
		return blueprints;
	}

	@Override
	public List<MySkill> getSkills() {
		return skills;
	}

	@Override
	public List<MyMining> getMining() {
		return mining;
	}

	@Override
	public List<MyExtraction> getExtractions() {
		return extractions;
	}

	@Override
	public Long getTotalSkillPoints() {
		return totalSkillPoints;
	}

	@Override
	public Integer getUnallocatedSkillPoints() {
		return unallocatedSkillPoints;
	}

	@Override
	public final void setShowOwner(final boolean showOwner) {
		this.showOwner = showOwner;
	}

	@Override
	public synchronized void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	@Override
	public final void setBlueprints(final Map<Long, RawBlueprint> blueprints) {
		this.blueprints = blueprints;
	}

	@Override
	public final void setIndustryJobs(final List<MyIndustryJob> industryJobs) {
		this.industryJobs = industryJobs;
	}

	@Override
	public final void setTransactions(final Set<MyTransaction> transactions) {
		this.transactions = transactions;
	}

	@Override
	public final void setJournal(final Set<MyJournal> journal) {
		this.journal = journal;
	}

	@Override
	public final void setMarketOrders(final Set<MyMarketOrder> marketOrders) {
		this.marketOrders = marketOrders;
	}

	@Override
	public void setMiningNextUpdate(Date miningNextUpdate) {
		this.miningNextUpdate = miningNextUpdate;
	}

	@Override
	public final void setContracts(final Map<MyContract, List<MyContractItem>> contracts) {
		this.contracts = contracts;
	}

	@Override
	public final synchronized void setAssets(final List<MyAsset> assets) {
		this.assets = assets;
	}

	@Override
	public final void setAccountBalances(final List<MyAccountBalance> accountBalances) {
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
	public void setSkills(List<MySkill> skills) {
		this.skills = skills;
	}

	@Override
	public void setMining(List<MyMining> mining) {
		this.mining = mining;
	}

	@Override
	public void setExtractions(List<MyExtraction> extractions) {
		this.extractions = extractions;
	}

	@Override
	public void setTotalSkillPoints(Long totalSkillPoints) {
		this.totalSkillPoints = totalSkillPoints;
	}

	@Override
	public void setUnallocatedSkillPoints(Integer unallocatedSkillPoints) {
		this.unallocatedSkillPoints = unallocatedSkillPoints;
	}

	@Override
	public void setActiveShip(MyShip activeShip) {
		this.activeShip = activeShip;
	}

	@Override
	public final int compareTo(final OwnerType o) {
		return this.getOwnerName().compareToIgnoreCase(o.getOwnerName());
	}

}
