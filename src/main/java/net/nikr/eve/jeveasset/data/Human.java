/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

import com.beimin.eveapi.ApiAuthorization;
import com.beimin.eveapi.balance.ApiAccountBalance;
import com.beimin.eveapi.industry.ApiIndustryJob;
import com.beimin.eveapi.order.ApiMarketOrder;
import java.util.Date;
import java.util.List;
import java.util.Vector;


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

	public Human(Account parentAccount, String name, long characterID, String corporation, boolean bCorporationAssets, boolean showAssets, Date nextUpdate, Date balanceNextUpdate, Date marketOrdersNextUpdate, Date industryJobsNextUpdate) {
		this.parentAccount = parentAccount;
		this.name = name;
		this.characterID = characterID;
		this.corporation = corporation;
		this.updateCorporationAssets = bCorporationAssets;
		this.showAssets = showAssets;
		this.assetNextUpdate = nextUpdate;
		this.balanceNextUpdate = balanceNextUpdate;
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
		this.industryJobsNextUpdate = industryJobsNextUpdate;
		//Default
		assets = new Vector<EveAsset>();
		accountBalances = new  Vector<ApiAccountBalance>();
		accountBalancesCorporation = new  Vector<ApiAccountBalance>();
		marketOrders = new  Vector<ApiMarketOrder>();
		marketOrdersCorporation = new  Vector<ApiMarketOrder>();
		industryJobs = new  Vector<ApiIndustryJob>();
		industryJobsCorporation = new  Vector<ApiIndustryJob>();
	}

	public Human(Account parentAccount, String name, long characterID, String corporation) {
		this.name = name;
		this.characterID = characterID;
		this.corporation = corporation;
		this.parentAccount = parentAccount;
		//Default
		assetNextUpdate = Settings.getGmtNow();
		balanceNextUpdate = Settings.getGmtNow();
		marketOrdersNextUpdate = Settings.getGmtNow();
		industryJobsNextUpdate = Settings.getGmtNow();
		assets = new Vector<EveAsset>();
		accountBalances = new  Vector<ApiAccountBalance>();
		accountBalancesCorporation = new  Vector<ApiAccountBalance>();
		marketOrders = new  Vector<ApiMarketOrder>();
		marketOrdersCorporation = new  Vector<ApiMarketOrder>();
		updateCorporationAssets = true;
		showAssets = true;
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

	public boolean isAssetsUpdatable(){
		return Settings.isUpdatable(getAssetNextUpdate());
	}

	public boolean isBalanceUpdatable(){
		return Settings.isUpdatable(getBalanceNextUpdate());
	}

	public boolean isIndustryJobsUpdatable(){
		return Settings.isUpdatable(getIndustryJobsNextUpdate());
	}

	public boolean isMarkerOrdersUpdatable(){
		return Settings.isUpdatable(getMarketOrdersNextUpdate());
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
	public boolean equals(Object o){
		if (o instanceof Human){
			return equals( (Human) o);
		}
		return false;
	}
	public boolean equals(Human h){
		return (this.getCharacterID() == h.getCharacterID() );
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + (int) (this.characterID ^ (this.characterID >>> 32));
		return hash;
	}

	public static ApiAuthorization getApiAuthorization(Account account){
		return getApiAuthorization(account, 0);
	}
	public static ApiAuthorization getApiAuthorization(Human human){
		return getApiAuthorization(human.getParentAccount(), (int)human.getCharacterID());
	}
	public static ApiAuthorization getApiAuthorization(Account account, Human human){
		return getApiAuthorization(account, (int)human.getCharacterID());
	}
	public static ApiAuthorization getApiAuthorization(Account account, long characterID){
		return getApiAuthorization(account, (int)characterID);
	}
	public static ApiAuthorization getApiAuthorization(Account account, int characterID){
		return new ApiAuthorization(account.getUserID(), characterID, account.getApiKey());
	}
}