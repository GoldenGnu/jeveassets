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
import com.beimin.eveapi.parser.character.CharAccountBalanceParser;
import com.beimin.eveapi.parser.corporation.CorpAccountBalanceParser;
import com.beimin.eveapi.response.shared.AccountBalanceResponse;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccount;
import net.nikr.eve.jeveasset.data.eveapi.EveApiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;

public class AccountBalanceGetter extends AbstractApiGetter<AccountBalanceResponse> {

	public AccountBalanceGetter() {
		super("Account Balance", true, false);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<EveApiAccount> accounts) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected AccountBalanceResponse getResponse(final boolean bCorp) throws ApiException {
		if (bCorp) {
			return new CorpAccountBalanceParser()
					.getResponse(EveApiOwner.getApiAuthorization(getOwner()));
		} else {
			return new CharAccountBalanceParser()
					.getResponse(EveApiOwner.getApiAuthorization(getOwner()));
		}
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getOwner().setBalanceNextUpdate(nextUpdate);
		getOwner().setBalanceLastUpdate(Settings.getNow());
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getBalanceNextUpdate();
	}

	@Override
	protected void setData(final AccountBalanceResponse response) {
		getOwner().setAccountBalances(EveApiConverter.toAccountBalance(response.getAll(), getOwner()));
	}

	@Override
	protected void updateFailed(final EveApiOwner ownerFrom, final EveApiOwner ownerTo) {
		ownerTo.setAccountBalances(ownerFrom.getAccountBalances());
		ownerTo.setBalanceNextUpdate(ownerFrom.getBalanceNextUpdate());
		ownerTo.setBalanceLastUpdate(ownerFrom.getBalanceLastUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		return EveApiAccessMask.ACCOUNT_BALANCE.getAccessMask();
	}
}
