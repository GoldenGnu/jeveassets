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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Account {
	private int userID;
	private String apiKey;
	private List<Human> humans;
	private Date charactersNextUpdate;
	private String name;

	public Account(int userID, String apiKey) {
		this(userID, apiKey, String.valueOf(userID), Settings.getGmtNow());
	}

	public Account(int userID, String apiKey, String name, Date charactersNextUpdate) {
		this.userID = userID;
		this.apiKey = apiKey;
		this.name = name;
		this.charactersNextUpdate = charactersNextUpdate;
		//Default
		humans = new ArrayList<Human>();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Account other = (Account) obj;
		if (this.userID != other.userID) {
			return false;
		}
		if ((this.apiKey == null) ? (other.apiKey != null) : !this.apiKey.equals(other.apiKey)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + this.userID;
		hash = 83 * hash + (this.apiKey != null ? this.apiKey.hashCode() : 0);
		return hash;
	}
}
