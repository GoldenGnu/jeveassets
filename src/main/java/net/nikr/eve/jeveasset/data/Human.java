/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import com.beimin.eveapi.shared.accountbalance.EveAccountBalance;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Human implements Comparable<Human> {
	private String name;
	private long characterID;
	private boolean showAssets;
	private Date assetNextUpdate;
	private Date balanceNextUpdate;
	private Date marketOrdersNextUpdate;
	private Date industryJobsNextUpdate;
	private Account parentAccount;
	private List<EveAccountBalance> accountBalances;
	private List<ApiMarketOrder> marketOrders;
	private List<ApiIndustryJob> industryJobs;
	private List<Asset> assets;

	public Human(Account parentAccount, Human human) {
		name = human.getName();
		characterID = human.getCharacterID();
		showAssets = human.isShowAssets();
		assetNextUpdate = human.getAssetNextUpdate();
		balanceNextUpdate = human.getBalanceNextUpdate();
		marketOrdersNextUpdate = human.getMarketOrdersNextUpdate();
		industryJobsNextUpdate = human.getIndustryJobsNextUpdate();
		this.parentAccount = parentAccount;
		accountBalances = human.getAccountBalances();
		marketOrders = human.getMarketOrders();
		industryJobs = human.getIndustryJobs();
		assets = human.getAssets();
	}

	public Human(Account parentAccount, String name, long characterID) {
		this(parentAccount, name, characterID, true, Settings.getGmtNow(), Settings.getGmtNow(), Settings.getGmtNow(), Settings.getGmtNow());
	}

	public Human(Account parentAccount, String name, long characterID, boolean showAssets, Date assetNextUpdate, Date balanceNextUpdate, Date marketOrdersNextUpdate, Date industryJobsNextUpdate) {
		this.parentAccount = parentAccount;
		this.name = name;
		this.characterID = characterID;
		this.showAssets = showAssets;

		this.assetNextUpdate = assetNextUpdate;
		this.balanceNextUpdate = balanceNextUpdate;
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
		this.industryJobsNextUpdate = industryJobsNextUpdate;
		//Default
		assets = new ArrayList<Asset>();
		accountBalances = new  ArrayList<EveAccountBalance>();
		marketOrders = new  ArrayList<ApiMarketOrder>();
		industryJobs = new  ArrayList<ApiIndustryJob>();
	}

	public void setAccountBalances(List<EveAccountBalance> accountBalances) {
		this.accountBalances = accountBalances;
	}

	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}

	public void setAssetNextUpdate(Date nextUpdate) {
		this.assetNextUpdate = nextUpdate;
	}

	public void setBalanceNextUpdate(Date balanceNextUpdate) {
		this.balanceNextUpdate = balanceNextUpdate;
	}

	public void setCharacterID(long characterID) {
		this.characterID = characterID;
	}

	public void setIndustryJobs(List<ApiIndustryJob> industryJobs) {
		this.industryJobs = industryJobs;
	}

	public void setIndustryJobsNextUpdate(Date industryJobsNextUpdate) {
		this.industryJobsNextUpdate = industryJobsNextUpdate;
	}

	public void setMarketOrders(List<ApiMarketOrder> marketOrders) {
		this.marketOrders = marketOrders;
	}

	public void setMarketOrdersNextUpdate(Date marketOrdersNextUpdate) {
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setShowAssets(boolean showAssets) {
		this.showAssets = showAssets;
	}

	public boolean isShowAssets() {
		return showAssets;
	}

	public boolean isCorporation() {
		return parentAccount.isCorporation();
	}
	
	public boolean isCharacter(){
		return parentAccount.isCharacter();
	}

	public List<EveAccountBalance> getAccountBalances() {
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

	public long getCharacterID() {
		return characterID;
	}

	public List<ApiIndustryJob> getIndustryJobs() {
		return industryJobs;
	}

	public Date getIndustryJobsNextUpdate() {
		return industryJobsNextUpdate;
	}

	public List<ApiMarketOrder> getMarketOrders() {
		return marketOrders;
	}

	public Date getMarketOrdersNextUpdate() {
		return marketOrdersNextUpdate;
	}

	public String getName() {
		return name;
	}
	
	public Account getParentAccount() {
		return parentAccount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Human other = (Human) obj;
		if (this.characterID != other.characterID) {
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
		hash = 89 * hash + (int) (this.characterID ^ (this.characterID >>> 32));
		hash = 89 * hash + (this.parentAccount != null ? this.parentAccount.hashCode() : 0);
		return hash;
	}
	
	@Override
	public int compareTo(Human o) {
		return this.getName().compareTo(o.getName());
	}
	
	@Override
	public String toString(){
		return getName();
	}

	public static ApiAuthorization getApiAuthorization(Account account){
		return getApiAuthorization(account, 0);
	}
	public static ApiAuthorization getApiAuthorization(Human human){
		return getApiAuthorization(human.getParentAccount(), human.getCharacterID());
	}
	private static ApiAuthorization getApiAuthorization(Account account, long characterID){
		return new ApiAuthorization(account.getKeyID(), characterID, account.getVCode());
	}
}