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
import com.beimin.eveapi.shared.wallet.journal.ApiJournalEntry;
import com.beimin.eveapi.shared.wallet.journal.WalletJournalResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Account.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.journal.Journal;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class JournalGetter extends AbstractApiGetter<WalletJournalResponse> {

	private int accountKey = 1000;
	private boolean updateInProgress = false;
	private List<Journal> corpJournal = new ArrayList<Journal>();

	public JournalGetter() {
		super("Journal", true, false);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<Account> accounts) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected boolean load(Date nextUpdate, boolean updateCorporation, String updateName) {
		if (updateCorporation) {
			boolean ok = false;
			updateInProgress = true;
			for (int i = 1000; i <= 1006; i++) {
				accountKey = i;
				boolean updated = super.load(nextUpdate, updateCorporation, updateName+" (accountKey: " + accountKey + ")");
				ok = ok || updated;
			}
			if (ok) {
				getOwner().setJournal(corpJournal);
			}
			updateInProgress = false;
			return ok;
		} else {
			return super.load(nextUpdate, updateCorporation, updateName);
		}
	}

	@Override
	protected WalletJournalResponse getResponse(final boolean bCorp) throws ApiException {
		if (bCorp) {
			return com.beimin.eveapi.corporation
					.wallet.journal.WalletJournalParser.getInstance()
					.getResponse(Owner.getApiAuthorization(getOwner()), accountKey, 2560);
		} else {
			return com.beimin.eveapi.character
					.wallet.journal.WalletJournalParser.getInstance()
					.getResponse(Owner.getApiAuthorization(getOwner()), 0, 2560);
		}
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getJournalNextUpdate();
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getOwner().setJournalNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(final WalletJournalResponse response) {
		List<ApiJournalEntry> api = new ArrayList<ApiJournalEntry>(response.getAll());
		if (updateInProgress) {
			for (ApiJournalEntry apiJournal : api) {
				Journal journal = ApiConverter.convertJournal(apiJournal, getOwner(), accountKey);
				corpJournal.add(journal);
			}
		} else {
			List<Journal> charJournal = new ArrayList<Journal>();
			for (ApiJournalEntry apiJournal : api) {
				Journal journal = ApiConverter.convertJournal(apiJournal, getOwner(), accountKey);
				charJournal.add(journal);
			}
			getOwner().setJournal(charJournal);
		}
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