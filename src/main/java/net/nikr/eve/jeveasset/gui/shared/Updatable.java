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

package net.nikr.eve.jeveasset.gui.shared;

import java.util.Date;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.MyAccount;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;


public class Updatable {

	private Program program;

	public Updatable(final Program program) {
		this.program = program;
	}

	public boolean isUpdatable() {
		if (isUpdatable(program.getPriceDataGetter().getNextUpdate(), false)) {
			return true;
		}
		for (MyAccount account : program.getAccounts()) {
			//Account
			if (isUpdatable(account.getAccountNextUpdate())) {
				return true;
			}
			for (Owner owner : account.getOwners()) {
				if (owner.isShowOwner()) {
					if (isUpdatable(owner.getIndustryJobsNextUpdate())){
						return true;
					}
					if (isUpdatable(owner.getMarketOrdersNextUpdate())){
						return true;
					}
					if (isUpdatable(owner.getAssetNextUpdate())){
						return true;
					}
					if (isUpdatable(owner.getBalanceNextUpdate())){
						return true;
					}
					if (isUpdatable(owner.getContractsNextUpdate())){
						return true;
					}
					if (isUpdatable(owner.getTransactionsNextUpdate())){
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isUpdatable(final Date nextUpdate) {
		return isUpdatable(nextUpdate, true);
	}

	private boolean isUpdatable(Date nextUpdate, final boolean ignoreOnProxy) {
		if (nextUpdate == null) {
			return false;
		}
		if (Settings.get().isUpdatable(nextUpdate, ignoreOnProxy)) {
			return true;
		} else {
			return false;
		}
	}
}
