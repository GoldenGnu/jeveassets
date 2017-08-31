/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.parser.character.CharWalletTransactionsParser;
import com.beimin.eveapi.parser.corporation.CorpWalletTransactionsParser;
import com.beimin.eveapi.response.shared.WalletTransactionsResponse;
import java.util.Date;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class TransactionsGetter extends AbstractApiAccountKeyGetter<WalletTransactionsResponse, MyTransaction> {

	private boolean saveHistory;

	public TransactionsGetter() {
		super("Wallet Transactions");
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<EveApiAccount> accounts, final boolean saveHistory) {
		this.saveHistory = saveHistory;
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected void set(Set<MyTransaction> values, Date nextUpdate) {
		getOwner().setTransactions(values);
		getOwner().setTransactionsNextUpdate(nextUpdate);
	}

	@Override
	protected WalletTransactionsResponse getResponse(boolean bCorp, int accountKey, long fromID, int rowCount) throws ApiException {
		if (bCorp) {
			return new CorpWalletTransactionsParser()
					.getResponse(EveApiOwner.getApiAuthorization(getOwner()), accountKey, fromID, rowCount);
		} else {
			return new CharWalletTransactionsParser()
					.getTransactionsResponse(EveApiOwner.getApiAuthorization(getOwner()), fromID, rowCount);
		}
	}

	@Override
	protected Set<MyTransaction> convertData(WalletTransactionsResponse response, int accountKey) {
		return EveApiConverter.toTransactions(response.getAll(), getOwner(), accountKey, saveHistory);
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getTransactionsNextUpdate();
	}

	@Override
	protected void updateFailed(final EveApiOwner ownerFrom, final EveApiOwner ownerTo) {
		ownerTo.setTransactions(ownerFrom.getTransactions());
		ownerTo.setTransactionsNextUpdate(ownerFrom.getTransactionsNextUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		if (bCorp) {
			return EveApiAccessMask.TRANSACTIONS_CORP.getAccessMask();
		} else {
			return EveApiAccessMask.TRANSACTIONS_CHAR.getAccessMask();
		}
	}

	@Override
	protected long getId(MyTransaction v) {
		return v.getTransactionID();
	}

}
