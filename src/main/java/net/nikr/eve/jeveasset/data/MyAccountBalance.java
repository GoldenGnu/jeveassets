/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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

import com.beimin.eveapi.model.shared.EveAccountBalance;
import net.nikr.eve.jeveasset.data.api.OwnerType;


public class MyAccountBalance extends EveAccountBalance {
	private final EveAccountBalance eveAccountBalance;
	private final OwnerType owner;

	public MyAccountBalance(EveAccountBalance eveAccountBalance, OwnerType owner) {
		this.eveAccountBalance = eveAccountBalance;
		this.owner = owner;
	}

	@Override
	public int getAccountID() {
		return eveAccountBalance.getAccountID();
	}

	@Override
	public void setAccountID(int accountID) {
		eveAccountBalance.setAccountID(accountID);
	}

	@Override
	public int getAccountKey() {
		return eveAccountBalance.getAccountKey();
	}

	@Override
	public void setAccountKey(int accountKey) {
		eveAccountBalance.setAccountKey(accountKey);
	}

	@Override
	public double getBalance() {
		return eveAccountBalance.getBalance();
	}

	@Override
	public void setBalance(double balance) {
		eveAccountBalance.setBalance(balance);
	}

	public String getOwner() {
		return owner.getOwnerName();
	}

	public long getOwnerID() {
		return owner.getOwnerID();
	}

	public boolean isCorporation() {
		return owner.isCorporation();
	}
}
