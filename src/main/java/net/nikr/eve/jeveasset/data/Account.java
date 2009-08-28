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

import java.util.Date;
import java.util.List;
import java.util.Vector;


public class Account {
	private int userID;
	private String apiKey;
	private List<Human> humans;
	private Date charactersNextUpdate;

	public Account(int userID, String apiKey, Date charactersNextUpdate) {
		this.userID = userID;
		this.apiKey = apiKey;
		this.charactersNextUpdate = charactersNextUpdate;
		//Default
		humans = new Vector<Human>();
	}
	
	public Account(int userID, String apiKey) {
		this.userID = userID;
		this.apiKey = apiKey;
		//Default
		humans = new Vector<Human>();
		charactersNextUpdate = Settings.getGmtNow();
	}
	public String getApiKey() {
		return apiKey;
	}

	public int getUserID() {
		return userID;
	}

	public Date getCharactersNextUpdate() {
		return charactersNextUpdate;
	}

	public void setCharactersNextUpdate(Date charactersNextUpdate) {
		this.charactersNextUpdate = charactersNextUpdate;
	}

	public List<Human> getHumans() {
		return humans;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setHumans(List<Human> humans) {
		this.humans = humans;
	}

	@Override
	public String toString(){
		return userID+"::"+apiKey;
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof Account){
			return equals( (Account) o);
		}
		return false;
	}

	public boolean equals(Account a){
		return (this.apiKey.equals(a.getApiKey()) && this.getUserID() == a.getUserID());
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 61 * hash + this.userID;
		hash = 61 * hash + (this.apiKey != null ? this.apiKey.hashCode() : 0);
		return hash;
	}
}
