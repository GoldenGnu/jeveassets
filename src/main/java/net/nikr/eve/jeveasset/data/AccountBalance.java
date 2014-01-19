/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import com.beimin.eveapi.shared.accountbalance.EveAccountBalance;


public class AccountBalance extends EveAccountBalance {
	private EveAccountBalance eveAccountBalance;
	private Owner owner;

	public AccountBalance(EveAccountBalance eveAccountBalance, Owner owner) {
		this.eveAccountBalance = eveAccountBalance;
		this.owner = owner;
	}

	@Override
	public int getAccountID() {
		return eveAccountBalance.getAccountID(); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setAccountID(int accountID) {
		eveAccountBalance.setAccountID(accountID); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getAccountKey() {
		return eveAccountBalance.getAccountKey(); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setAccountKey(int accountKey) {
		eveAccountBalance.setAccountKey(accountKey); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public double getBalance() {
		return eveAccountBalance.getBalance(); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setBalance(double balance) {
		eveAccountBalance.setBalance(balance); //To change body of generated methods, choose Tools | Templates.
	}

	public String getOwner() {
		return owner.getName();
	}

	public long getOwnerID() {
		return owner.getOwnerID();
	}

	public boolean isCorporation() {
		return owner.isCorporation();
	}
}
