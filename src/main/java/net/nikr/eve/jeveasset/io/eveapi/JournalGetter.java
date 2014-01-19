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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.wallet.journal.ApiJournalEntry;
import com.beimin.eveapi.shared.wallet.journal.WalletJournalResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Account.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.journal.Journal;
import net.nikr.eve.jeveasset.io.shared.AbstractApiAccountKeyGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class JournalGetter extends AbstractApiAccountKeyGetter<WalletJournalResponse, Journal> {

	private boolean saveHistory;

	public JournalGetter() {
		super("Journal");
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<Account> accounts, final boolean saveHistory) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
		this.saveHistory = saveHistory;
	}

	@Override
	protected void set(Map<Long, Journal> values, Date nextUpdate) {
		getOwner().setJournal(values);
		getOwner().setJournalNextUpdate(nextUpdate);
	}

	@Override
	protected Map<Long, Journal> get() {
		if (saveHistory) {
			return getOwner().getJournal();
		} else {
			return new HashMap<Long, Journal>();
		}
	}

	@Override
	protected WalletJournalResponse getResponse(final boolean bCorp, final int accountKey, final long fromID, final int rowCount) throws ApiException {
		if (bCorp) {
			return com.beimin.eveapi.corporation
					.wallet.journal.WalletJournalParser.getInstance()
					.getResponse(Owner.getApiAuthorization(getOwner()), accountKey, fromID, rowCount);
		} else {
			return com.beimin.eveapi.character
					.wallet.journal.WalletJournalParser.getInstance()
					.getResponse(Owner.getApiAuthorization(getOwner()), fromID, rowCount);
		}
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getJournalNextUpdate();
	}

	@Override
	protected Map<Long, Journal> convertData(final WalletJournalResponse response, final int accountKey) {
		List<ApiJournalEntry> api = new ArrayList<ApiJournalEntry>(response.getAll());
		return ApiConverter.convertJournals(api, getOwner(), accountKey);
	}

	@Override
	protected void updateFailed(final Owner ownerFrom, final Owner ownerTo) {
		ownerTo.setJournal(ownerFrom.getJournal());
		ownerTo.setJournalNextUpdate(ownerFrom.getJournalNextUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		if (bCorp) {
			return AccessMask.JOURNAL_CORP.getAccessMask();
		} else {
			return AccessMask.JOURNAL_CHAR.getAccessMask();
		}
	}
}