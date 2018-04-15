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

import java.util.Date;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.settings.Settings;


public class EveKitOwner extends AbstractOwner implements OwnerType {
	private final Integer accessKey;
	private final String accessCred;

	private Date expire = null;
	private long accessMask;
	private boolean corporation;
	private Date limit = null;
	private String accountName;
	private Long journalCID = null;
	private Long transactionsCID = null;
	private Long contractsCID = null;
	private Long industryJobsCID = null;
	private Long marketOrdersCID = null;
	private Date accountNextUpdate = Settings.getNow();

	//New owner
	public EveKitOwner(Integer accessKey, String accessCred) {
		this.accessKey = accessKey;
		this.accessCred = accessCred;
	}

	//Load owner
	public EveKitOwner(Integer accessKey, String accessCred, Date expire, long accessMask, boolean corporation, Date limit, String accountName) {
		this.accessKey = accessKey;
		this.accessCred = accessCred;
		this.expire = expire;
		this.accessMask = accessMask;
		this.corporation = corporation;
		this.limit = limit;
		this.accountName = accountName;
	}

	//Copy owner
	public EveKitOwner(Integer accessKey, String accessCred, EveKitOwner editEveKitOwner) {
		super(editEveKitOwner);
		this.accessKey = accessKey;
		this.accessCred = accessCred;
		this.expire = editEveKitOwner.expire;
		this.accessMask = editEveKitOwner.accessMask;
		this.corporation = editEveKitOwner.corporation;
		this.limit = editEveKitOwner.limit;
		this.accountName = editEveKitOwner.accountName;
		this.journalCID = editEveKitOwner.journalCID;
		this.transactionsCID = editEveKitOwner.transactionsCID;
		this.contractsCID = editEveKitOwner.contractsCID;
		this.industryJobsCID = editEveKitOwner.industryJobsCID;
		this.marketOrdersCID = editEveKitOwner.marketOrdersCID;
		this.accountNextUpdate = editEveKitOwner.accountNextUpdate;
	}

	public Long getJournalCID() {
		return journalCID;
	}

	public void setJournalCID(Long journalCID) {
		this.journalCID = journalCID;
	}

	public Long getTransactionsCID() {
		return transactionsCID;
	}

	public void setTransactionsCID(Long transactionsCID) {
		this.transactionsCID = transactionsCID;
	}

	public Long getContractsCID() {
		return contractsCID;
	}

	public void setContractsCID(Long contractsCID) {
		this.contractsCID = contractsCID;
	}

	public Long getIndustryJobsCID() {
		return industryJobsCID;
	}

	public void setIndustryJobsCID(Long industryJobsCID) {
		this.industryJobsCID = industryJobsCID;
	}

	public Long getMarketOrdersCID() {
		return marketOrdersCID;
	}

	public void setMarketOrdersCID(Long marketOrdersCID) {
		this.marketOrdersCID = marketOrdersCID;
	}

	public Date getAccountNextUpdate() {
		return accountNextUpdate;
	}

	public void setAccountNextUpdate(Date accountNextUpdate) {
		this.accountNextUpdate = accountNextUpdate;
	}

	public Integer getAccessKey() {
		return accessKey;
	}

	public String getAccessCred() {
		return accessCred;
	}

	public void setLimit(Date limit) {
		this.limit = limit;
	}

	public long getAccessMask() {
		return accessMask;
	}

	public Date getLimit() {
		return limit;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}

	public void setAccessMask(long accessMask) {
		this.accessMask = accessMask;
	}

	public void setCorporation(boolean corporation) {
		this.corporation = corporation;
	}

	@Override
	public boolean isAssetList() {
		return (getAccessMask() & EveKitAccessMask.ASSET_LIST.getAccessMask()) == EveKitAccessMask.ASSET_LIST.getAccessMask();
	}

	@Override
	public boolean isAccountBalance() {
		return (getAccessMask() & EveKitAccessMask.ACCOUNT_BALANCE.getAccessMask()) == EveKitAccessMask.ACCOUNT_BALANCE.getAccessMask();
	}

	@Override
	public boolean isIndustryJobs() {
		return (getAccessMask() & EveKitAccessMask.INDUSTRY_JOBS.getAccessMask()) == EveKitAccessMask.INDUSTRY_JOBS.getAccessMask();
	}

	@Override
	public boolean isBlueprints() {
		return isAssetList();
	}

	@Override
	public boolean isMarketOrders() {
		return (getAccessMask() & EveKitAccessMask.MARKET_ORDERS.getAccessMask()) == EveKitAccessMask.MARKET_ORDERS.getAccessMask();
	}

	@Override
	public boolean isTransactions() {
		return (getAccessMask() & EveKitAccessMask.TRANSACTIONS.getAccessMask()) == EveKitAccessMask.TRANSACTIONS.getAccessMask();
		
	}

	@Override
	public boolean isJournal() {
		return (getAccessMask() & EveKitAccessMask.JOURNAL.getAccessMask()) == EveKitAccessMask.JOURNAL.getAccessMask();
	}

	@Override
	public boolean isContracts() {
		return (getAccessMask() & EveKitAccessMask.CONTRACTS.getAccessMask()) == EveKitAccessMask.CONTRACTS.getAccessMask();
		
	}

	@Override
	public boolean isLocations() {
		return (getAccessMask() & EveKitAccessMask.LOCATIONS.getAccessMask()) == EveKitAccessMask.LOCATIONS.getAccessMask();
	}

	@Override
	public boolean isStructures() {
		return false; //Not supported by the EveKit, Yet?
	}

	@Override
	public boolean isShip() {
		return isAssetList() && isCharacter();
	}

	@Override
	public boolean isOpenWindows() {
		return false; //Not supported by the EveKit, Yet?
	}

	@Override
	public boolean isAutopilot() {
		return false; //Not supported by the EveKit, Yet?
	}

	@Override
	public boolean isContainerLogs() {
		return false; //Not supported by the EveKit, Yet?
	}

	@Override
	public String getComparator() {
		return "evekit" + getAccountName() + getAccessKey();
	}

	@Override
	public String getAccountName() {
		if (accountName == null || accountName.isEmpty()) {
			accountName = getOwnerName();
		}
		return accountName;
	}

	@Override
	public void setResetAccountName() {
		this.accountName = getOwnerName();
	}

	@Override
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Override
	public boolean isInvalid() {
		return false;
	}

	@Override
	public ApiType getAccountAPI() {
		return ApiType.EVEKIT;
	}

	@Override
	public Date getExpire() {
		return expire;
	}

	@Override
	public boolean isCorporation() {
		return corporation;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 59 * hash + Objects.hashCode(this.accessKey);
		hash = 59 * hash + Objects.hashCode(this.accessCred);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EveKitOwner other = (EveKitOwner) obj;
		if (!Objects.equals(this.accessCred, other.accessCred)) {
			return false;
		}
		return Objects.equals(this.accessKey, other.accessKey);
	}
}
