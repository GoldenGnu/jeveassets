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
import enterprises.orbital.evekit.client.model.WalletTransaction;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;


public class EveKitTransactionsGetter extends AbstractEveKitGetter implements EveKitPagesHandler<WalletTransaction> {

	public EveKitTransactionsGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, false, owner.getTransactionsNextUpdate(), TaskType.TRANSACTIONS, false, null);
	}

	@Override
	protected void get(ApiClient apiClient, Long at, boolean first) throws ApiException {
		List<WalletTransaction> data = updatePages(this);
		if (data == null) {
			return;
		}
		owner.setTransactions(EveKitConverter.toTransactions(data, owner, loadCID() != null));
	}

	

	@Override
	public List<WalletTransaction> get(ApiClient apiClient, String at, Long contid, Integer maxResults) throws ApiException {
		return getCommonApi(apiClient).getWalletTransactions(owner.getAccessKey(), owner.getAccessCred(), at, contid, maxResults, false,
				null, null, dateFilter(Settings.get().getEveKitTransactionsHistory()), null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Override
	public long getCID(WalletTransaction obj) {
		return obj.getCid();
	}

	@Override
	public Long getLifeStart(WalletTransaction obj) {
		return obj.getLifeStart();
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.TRANSACTIONS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setTransactionsNextUpdate(date);
	}

	@Override
	public void saveCID(Long contid) {
		owner.setTransactionsCID(contid);
	}

	@Override
	public Long loadCID() {
		return owner.getTransactionsCID();
	}
}
