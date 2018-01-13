/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import enterprises.orbital.evekit.client.ApiClient;
import enterprises.orbital.evekit.client.ApiException;
import enterprises.orbital.evekit.client.model.AccountBalance;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;


public class EveKitAccountBalanceGetter extends AbstractEveKitGetter implements EveKitPagesHandler<AccountBalance>{

	public EveKitAccountBalanceGetter(UpdateTask updateTask, EveKitOwner owner, boolean first) {
		super(updateTask, owner, false, owner.getBalanceNextUpdate(), TaskType.ACCOUNT_BALANCE, first, null);
	}

	public EveKitAccountBalanceGetter(UpdateTask updateTask, EveKitOwner owner, Long at) {
		super(updateTask, owner, false, owner.getBalanceNextUpdate(), TaskType.ACCOUNT_BALANCE, false, at);
	}

	public EveKitAccountBalanceGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, false, owner.getBalanceNextUpdate(), TaskType.ACCOUNT_BALANCE, false, null);
	}

	@Override
	protected void get(ApiClient apiClient, Long at, boolean first) throws ApiException {
		List<AccountBalance> data = updatePages(this);
		if (data == null) {
			return;
		}
		Date balanceLastUpdate = null;
		for (AccountBalance balance : data) {
			if (balanceLastUpdate == null || balanceLastUpdate.getTime() < balance.getLifeStart()) { //Newer
				balanceLastUpdate = new Date(balance.getLifeStart());
			}
		}
		owner.setAccountBalances(EveKitConverter.toAccountBalance(data, owner));
		owner.setBalanceLastUpdate(balanceLastUpdate);
	}

	@Override
	public List<AccountBalance> get(ApiClient apiClient, String at, Long contid, Integer maxResults) throws ApiException {
		return getCommonApi(apiClient).getAccountBalance(owner.getAccessKey(), owner.getAccessCred(), at, contid, maxResults, false,
				null, null);
	}

	@Override
	public long getCID(AccountBalance obj) {
		return obj.getCid();
	}

	@Override
	public Long getLifeStart(AccountBalance obj) {
		return obj.getLifeStart();
	}

	@Override
	public long getAccessMask() {
		return EveKitAccessMask.ACCOUNT_BALANCE.getAccessMask();
	}

	@Override
	public void setNextUpdate(Date date) {
		owner.setBalanceNextUpdate(date);
	}

	@Override
	public void saveCID(Long cid) {	} //Always get all data

	@Override
	public Long loadCID() {
		return null; //Always get all data
	}
}
