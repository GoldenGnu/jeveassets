/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;
import com.beimin.eveapi.shared.wallet.transactions.WalletTransactionsResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Account.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Transaction;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class TransactionsGetter extends AbstractApiGetter<WalletTransactionsResponse> {

	public TransactionsGetter() {
		super("Transaction", true, false);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<Account> accounts) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected WalletTransactionsResponse getResponse(final boolean bCorp) throws ApiException {
		if (bCorp) {
			return com.beimin.eveapi.corporation
					.wallet.transactions.WalletTransactionsParser.getInstance()
					.getResponse(Owner.getApiAuthorization(getOwner()),0);
		} else {
			return com.beimin.eveapi.character
					.wallet.transactions.WalletTransactionsParser.getInstance()
					.getResponse(Owner.getApiAuthorization(getOwner()),0);
		}
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getTransactionsNextUpdate();
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getOwner().setTransactionsNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(final WalletTransactionsResponse response) {
		List<Transaction> transactions = ApiConverter.convertTransactions(new ArrayList<ApiWalletTransaction>(response.getAll()), getOwner());
		getOwner().setTransactions(transactions);
	}

	@Override
	protected void updateFailed(final Owner ownerFrom, final Owner ownerTo) {
		ownerTo.setTransactions(ownerFrom.getTransactions());
		ownerTo.setTransactionsNextUpdate(ownerFrom.getTransactionsNextUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		if (bCorp) {
			return AccessMask.TRANSACTIONS_CORP.getAccessMask();
		} else {
			return AccessMask.TRANSACTIONS_CHAR.getAccessMask();
		}
	}
}