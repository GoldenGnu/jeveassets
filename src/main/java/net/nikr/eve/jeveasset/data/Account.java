/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

import com.beimin.eveapi.account.apikeyinfo.ApiKeyInfoResponse.AccessMask;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Account {
	private int keyID;
	private String vCode;
	private String name;
	private Date charactersNextUpdate;
	private int accessMask;
	private String type;
	private Date expires;
	
	private List<Human> humans = new ArrayList<Human>();;

	public Account(Account account) {
		this.keyID = account.getKeyID();
		this.vCode = account.getVCode();
		this.name = account.getName();
		this.charactersNextUpdate = account.getCharactersNextUpdate();
		this.accessMask = account.getAccessMask();
		this.type = account.getType();
		this.expires = account.getExpires();
		for (Human human : account.getHumans()){
			humans.add(new Human(this, human));
		}
	}

	public Account(int keyID, String vCode) {
		this(keyID, vCode, Integer.toString(keyID), Settings.getGmtNow(), 0, "", null);
	}

	public Account(int keyID, String vCode, String name, Date charactersNextUpdate, int accessMask, String type, Date expires) {
		this.keyID = keyID;
		this.vCode = vCode;
		this.name = name;
		this.charactersNextUpdate = charactersNextUpdate;
		this.accessMask = accessMask;
		this.type = type;
		this.expires = expires;
	}

	public String getVCode() {
		return vCode;
	}

	public int getKeyID() {
		return keyID;
	}

	public Date getCharactersNextUpdate() {
		return charactersNextUpdate;
	}

	public void setCharactersNextUpdate(Date charactersNextUpdate) {
		this.charactersNextUpdate = charactersNextUpdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAccessMask() {
		return accessMask;
	}

	public void setAccessMask(int accessMask) {
		this.accessMask = accessMask;
	}

	public Date getExpires() {
		return expires;
	}
	
	public boolean isExpired(){
		return getExpires() == null ? false : Settings.getGmtNow().after(getExpires());
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Compare Type - Simplified to only consider Corporation or not Corporation
	 * @param type Account | Character | Corporation
	 * @return true if equal (both corp or both not corp) - false if not equal (one corp other not corp)
	 */
	public boolean compareTypes(String type){
		boolean corp = type.equals("Corporation");
		return (isCorporation() == corp);
	}
	
	public boolean isCorporation(){
		return type.equals("Corporation");
	}
	
	public boolean isCharacter(){
		return !isCorporation(); //type.equals("Character") || type.equals("Account");
	}

	public List<Human> getHumans() {
		return humans;
	}

	public void setVCode(String vCode) {
		this.vCode = vCode;
	}

	public void setHumans(List<Human> humans) {
		this.humans = humans;
	}
	
	public boolean isAccountBalance(){
		return ((getAccessMask() & AccessMask.ACCOUNT_BALANCE.getAccessMask()) == AccessMask.ACCOUNT_BALANCE.getAccessMask());
	}
	
	public boolean isAssetList(){
		return ((getAccessMask() & AccessMask.ASSET_LIST.getAccessMask()) == AccessMask.ASSET_LIST.getAccessMask());
	}
	
	public boolean isMarketOrders(){
		return ((getAccessMask() & AccessMask.MARKET_ORDERS.getAccessMask()) == AccessMask.MARKET_ORDERS.getAccessMask());
	}
	
	public boolean isIndustryJobs(){
		return ((getAccessMask() & AccessMask.INDUSTRY_JOBS.getAccessMask()) == AccessMask.INDUSTRY_JOBS.getAccessMask());
	}

	@Override
	public String toString(){
		return keyID+"::"+vCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Account other = (Account) obj;
		if (this.keyID != other.keyID) {
			return false;
		}
		if ((this.vCode == null) ? (other.vCode != null) : !this.vCode.equals(other.vCode)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + this.keyID;
		hash = 53 * hash + (this.vCode != null ? this.vCode.hashCode() : 0);
		return hash;
	}

	public void setKeyID(int keyID) {
		this.keyID = keyID;
	}
}
