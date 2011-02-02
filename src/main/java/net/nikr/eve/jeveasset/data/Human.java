/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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
import com.beimin.eveapi.shared.accountbalance.ApiAccountBalance;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Human {
	private String name;
	private long characterID;
	private String corporation;


	private boolean updateCorporationAssets;
	private boolean showAssets;
	private Date assetNextUpdate;
	private Date balanceNextUpdate;
	private Date marketOrdersNextUpdate;
	private Date industryJobsNextUpdate;
	private Account parentAccount;
	private List<ApiAccountBalance> accountBalances;
	private List<ApiAccountBalance> accountBalancesCorporation;
	private List<ApiMarketOrder> marketOrders;
	private List<ApiMarketOrder> marketOrdersCorporation;
	private List<ApiIndustryJob> industryJobs;
	private List<ApiIndustryJob> industryJobsCorporation;
	private List<EveAsset> assets;
	private List<EveAsset> assetsCorporation;

	public Human(Account parentAccount, String name, long characterID, String corporation) {
		this(parentAccount, name, characterID, corporation, true, true, Settings.getGmtNow(), Settings.getGmtNow(), Settings.getGmtNow(), Settings.getGmtNow());
	}

	public Human(Account parentAccount, String name, long characterID, String corporation, boolean bCorporationAssets, boolean showAssets, Date assetNextUpdate, Date balanceNextUpdate, Date marketOrdersNextUpdate, Date industryJobsNextUpdate) {
		this.parentAccount = parentAccount;
		this.name = name;
		this.characterID = characterID;
		this.corporation = corporation;
		this.updateCorporationAssets = bCorporationAssets;
		this.showAssets = showAssets;

		this.assetNextUpdate = assetNextUpdate;
		this.balanceNextUpdate = balanceNextUpdate;
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
		this.industryJobsNextUpdate = industryJobsNextUpdate;
		//Default
		assets = new ArrayList<EveAsset>();
		assetsCorporation = new ArrayList<EveAsset>();
		accountBalances = new  ArrayList<ApiAccountBalance>();
		accountBalancesCorporation = new  ArrayList<ApiAccountBalance>();
		marketOrders = new  ArrayList<ApiMarketOrder>();
		marketOrdersCorporation = new  ArrayList<ApiMarketOrder>();
		industryJobs = new  ArrayList<ApiIndustryJob>();
		industryJobsCorporation = new  ArrayList<ApiIndustryJob>();
	}

	public void setAccountBalances(List<ApiAccountBalance> accountBalances) {
		this.accountBalances = accountBalances;
	}

	public void setAccountBalancesCorporation(List<ApiAccountBalance> accountBalancesCorporation) {
		this.accountBalancesCorporation = accountBalancesCorporation;
	}

	public void setAssets(List<EveAsset> assets) {
		this.assets = assets;
	}

	public void setAssetsCorporation(List<EveAsset> assetsCorporation) {
		this.assetsCorporation = assetsCorporation;
	}

	public void setAssetNextUpdate(Date nextUpdate) {
		this.assetNextUpdate = nextUpdate;
	}

	public void setBalanceNextUpdate(Date balanceNextUpdate) {
		this.balanceNextUpdate = balanceNextUpdate;
	}

	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}

	public void setIndustryJobs(List<ApiIndustryJob> industryJobs) {
		this.industryJobs = industryJobs;
	}

	public void setIndustryJobsCorporation(List<ApiIndustryJob> industryJobsCorporation) {
		this.industryJobsCorporation = industryJobsCorporation;
	}

	public void setIndustryJobsNextUpdate(Date industryJobsNextUpdate) {
		this.industryJobsNextUpdate = industryJobsNextUpdate;
	}

	public void setMarketOrders(List<ApiMarketOrder> marketOrders) {
		this.marketOrders = marketOrders;
	}

	public void setMarketOrdersCorporation(List<ApiMarketOrder> marketOrdersCorporation) {
		this.marketOrdersCorporation = marketOrdersCorporation;
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

	public void setUpdateCorporationAssets(boolean updateCorporationAssets) {
		this.updateCorporationAssets = updateCorporationAssets;
	}

	public boolean isShowAssets() {
		return showAssets;
	}

	public boolean isUpdateCorporationAssets() {
		return updateCorporationAssets;
	}

	public List<ApiAccountBalance> getAccountBalances() {
		return accountBalances;
	}

	public List<ApiAccountBalance> getAccountBalancesCorporation() {
		return accountBalancesCorporation;
	}
	
	public List<EveAsset> getAssets() {
		return assets;
	}

	public List<EveAsset> getAssetsCorporation() {
		return assetsCorporation;
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

	public String getCorporation() {
		return corporation;
	}

	public List<ApiIndustryJob> getIndustryJobsCorporation() {
		return industryJobsCorporation;
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

	public List<ApiMarketOrder> getMarketOrdersCorporation() {
		return marketOrdersCorporation;
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
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + (int) (this.characterID ^ (this.characterID >>> 32));
		return hash;
	}

	public static ApiAuthorization getApiAuthorization(Account account){
		return getApiAuthorization(account, 0);
	}
	public static ApiAuthorization getApiAuthorization(Human human){
		return getApiAuthorization(human.getParentAccount(), human.getCharacterID());
	}
	private static ApiAuthorization getApiAuthorization(Account account, long characterID){
		return new ApiAuthorization(account.getUserID(), characterID, account.getApiKey());
	}
}