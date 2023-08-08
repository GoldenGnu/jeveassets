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

import java.util.Date;
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


public interface OwnerType extends Comparable<OwnerType> {

	//Info
	public String getOwnerName();
	public String getCorporationName();
	public void setOwnerName(final String ownerName);
	public void setCorporationName(String corporationName);
	public long getOwnerID();
	public void setOwnerID(final long ownerID);
	public boolean isCorporation();
	public boolean isShowOwner();
	public Date getExpire();
	public String getComparator();
	public String getAccountName();
	public boolean isExpired();
	public boolean isInvalid();
	public ApiType getAccountAPI();
	public void setShowOwner(final boolean showOwner);
	public void setInvalid(boolean invalid);
	public void setResetAccountName();
	public void setAccountName(final String accountName);
	public MyShip getActiveShip();
	public void setActiveShip(MyShip activeShip);
	//Data
	public List<MyAccountBalance> getAccountBalances();
	public Set<MyMarketOrder> getMarketOrders();
	public Set<MyTransaction> getTransactions();
	public Set<MyJournal> getJournal();
	public List<MyIndustryJob> getIndustryJobs();
	public Map<MyContract, List<MyContractItem>> getContracts();
	public List<MyAsset> getAssets();
	public Map<Long, RawBlueprint> getBlueprints();
	public Map<Integer, String> getWalletDivisions();
	public Map<Integer, String> getAssetDivisions();
	public List<MySkill> getSkills();
	public Long getTotalSkillPoints();
	public Integer getUnallocatedSkillPoints();
	public List<MyMining> getMining();
	public List<MyExtraction> getExtractions();
	public void setBlueprints(final Map<Long, RawBlueprint> blueprints);
	public void setIndustryJobs(final List<MyIndustryJob> industryJobs);
	public void setTransactions(final Set<MyTransaction> transactions);
	public void setJournal(final Set<MyJournal> journal);
	public void setMarketOrders(final Set<MyMarketOrder> marketOrders);
	public void setContracts(final Map<MyContract, List<MyContractItem>> contracts);
	public void setAssets(final List<MyAsset> assets);
	public void setAccountBalances(final List<MyAccountBalance> accountBalances);
	public void setWalletDivisions(final Map<Integer, String> walletDivisions);
	public void setAssetDivisions(final Map<Integer, String> assetDivisions);
	public void setSkills(final List<MySkill> skills);
	public void setTotalSkillPoints(final Long totalSkillPoints);
	public void setUnallocatedSkillPoints(final Integer unallocatedSkillPoints);
	public void setMining(List<MyMining> mining);
	public void setExtractions(List<MyExtraction> extractions);
	//Account Mask
	public boolean isCharacter();
	public boolean isAssetList();
	public boolean isAccountBalance();
	public boolean isIndustryJobs();
	public boolean isMarketOrders();
	public boolean isTransactions();
	public boolean isJournal();
	public boolean isContracts();
	public boolean isLocations();
	public boolean isStructures();
	public boolean isMarketStructures();
	public boolean isBlueprints();
	public boolean isBookmarks();
	public boolean isShip();
	public boolean isOpenWindows();
	public boolean isPlanetaryInteraction();
	public boolean isAutopilot();
	public boolean isDivisions();
	public boolean isPrivilegesLimited();
	public boolean isPrivilegesInvalid();
	public boolean isSkills();
	public boolean isMining();
	//Last Update
	public Date getAssetLastUpdate();
	public Date getBalanceLastUpdate();
	public void setAssetLastUpdate(final Date assetLastUpdate);
	public void setBalanceLastUpdate(final Date balanceLastUpdate);
	//Next Update
	public void setAssetNextUpdate(final Date nextUpdate);
	public void setBalanceNextUpdate(final Date balanceNextUpdate);
	public void setBlueprintsNextUpdate(Date blueprintsNextUpdate);
	public void setBookmarksNextUpdate(Date bookmarksNextUpdate);
	public void setContractsNextUpdate(final Date contractsNextUpdate);
	public void setIndustryJobsNextUpdate(final Date industryJobsNextUpdate);
	public void setLocationsNextUpdate(final Date locationsNextUpdate);
	public void setMarketOrdersNextUpdate(final Date marketOrdersNextUpdate);
	public void setJournalNextUpdate(final Date journalNextUpdate);
	public void setTransactionsNextUpdate(final Date transactionsNextUpdate);
	public void setSkillsNextUpdate(final Date skillsNextUpdate);
	public void setMiningNextUpdate(final Date miningNextUpdate);
	public Date getTransactionsNextUpdate();
	public Date getAssetNextUpdate();
	public Date getBalanceNextUpdate();
	public Date getBlueprintsNextUpdate();
	public Date getBookmarksNextUpdate();
	public Date getContractsNextUpdate();
	public Date getIndustryJobsNextUpdate();
	public Date getLocationsNextUpdate();
	public Date getMarketOrdersNextUpdate();
	public Date getJournalNextUpdate();
	public Date getSkillsNextUpdate();
	public Date getMiningNextUpdate();
}
