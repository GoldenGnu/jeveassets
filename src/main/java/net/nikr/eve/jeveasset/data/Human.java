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
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.Program;


public class Human {
	private String name;
	private long characterID;
	private String corporation;


	private boolean updateCorporationAssets;
	private boolean showAssets;
	private Date assetNextUpdate;
	private Date balanceNextUpdate;
	private Account parentAccount;
	private List<ApiAccountBalance> accountBalances;
	private List<ApiAccountBalance> corporationAccountBalances;
	
	private List<EveAsset> assets;

	public Human(Account parentAccount, String name, long characterID, String corporation, boolean bCorporationAssets, boolean showAssets, Date nextUpdate, Date balanceNextUpdate) {
		this.parentAccount = parentAccount;
		this.name = name;
		this.characterID = characterID;
		this.corporation = corporation;
		this.updateCorporationAssets = bCorporationAssets;
		this.showAssets = showAssets;
		this.assetNextUpdate = nextUpdate;
		this.balanceNextUpdate = balanceNextUpdate;
		//Default
		assets = new Vector<EveAsset>();
		accountBalances = new  Vector<ApiAccountBalance>();
		corporationAccountBalances = new  Vector<ApiAccountBalance>();
	}

	public Human(Account parentAccount, String name, long characterID, String corporation) {
		this.name = name;
		this.characterID = characterID;
		this.corporation = corporation;
		this.parentAccount = parentAccount;
		//Default
		assetNextUpdate = Settings.getGmtNow();
		balanceNextUpdate = Settings.getGmtNow();
		assets = new Vector<EveAsset>();
		accountBalances = new  Vector<ApiAccountBalance>();
		corporationAccountBalances = new  Vector<ApiAccountBalance>();
		updateCorporationAssets = true;
		showAssets = true;
	}

	public void setAccountBalances(List<ApiAccountBalance> accountBalances) {
		this.accountBalances = accountBalances;
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

	public void setCorporationAccountBalances(List<ApiAccountBalance> corporationAccountBalances) {
		this.corporationAccountBalances = corporationAccountBalances;
	}

	public void setUpdateCorporationAssets(boolean updateCorporationAssets) {
		this.updateCorporationAssets = updateCorporationAssets;
	}

	public void setName(String name) {
		this.name = name;
	}


	public List<ApiAccountBalance> getAccountBalances() {
		return accountBalances;
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

	public List<ApiAccountBalance> getCorporationAccountBalances() {
		return corporationAccountBalances;
	}

	public boolean isUpdateCorporationAssets() {
		return updateCorporationAssets;
	}
	public boolean isAssetsUpdatable(){
		return ((Settings.getGmtNow().after(this.getAssetNextUpdate())
				|| Settings.getGmtNow().equals(this.getAssetNextUpdate())
				|| Program.FORCE_UPDATE )
				&& !Program.FORCE_NO_UPDATE);
	}
	public boolean isBalanceUpdatable(){
		return (Settings.getGmtNow().after(this.getBalanceNextUpdate())
				|| Settings.getGmtNow().equals(this.getBalanceNextUpdate()) );
	}

	public boolean isShowAssets() {
		return showAssets;
	}

	public void setShowAssets(boolean showAssets) {
		this.showAssets = showAssets;
	}

	public String getCorporation() {
		return corporation;
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