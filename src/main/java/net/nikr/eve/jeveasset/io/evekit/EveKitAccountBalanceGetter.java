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
package net.nikr.eve.jeveasset.io.evekit;


import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.AccountBalance;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitAccountBalanceGetter extends AbstractEveKitGetter {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected void get(EveKitOwner owner) throws ApiException {
	  // As written, this will always get the latest account balances.  No changes needed.
		List<AccountBalance> accountBalance = getCommonApi().getAccountBalance(owner.getAccessKey(), owner.getAccessCred(), null, null, Integer.MAX_VALUE, null,
				null, null);
		Date balanceLastUpdate = null;
		for (AccountBalance balance : accountBalance) {
			if (balanceLastUpdate == null || balanceLastUpdate.getTime() < balance.getLifeStart()) { //Newer
				balanceLastUpdate = new Date(balance.getLifeStart());
			}
		}
		owner.setAccountBalances(EveKitConverter.convertAccountBalance(accountBalance, owner));
		owner.setBalanceLastUpdate(balanceLastUpdate);
	}

	@Override
	protected String getTaskName() {
		return "Account Balance";
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.ACCOUNT_BALANCE.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		owner.setBalanceNextUpdate(date);
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}
}
