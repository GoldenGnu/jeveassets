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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.parser.character.CharWalletJournalParser;
import com.beimin.eveapi.parser.corporation.CorpWalletJournalParser;
import com.beimin.eveapi.response.shared.WalletJournalResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccount;
import net.nikr.eve.jeveasset.data.eveapi.EveApiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.journal.MyJournal;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class JournalGetter extends AbstractApiAccountKeyGetter<WalletJournalResponse, MyJournal> {

	private boolean saveHistory;

	public JournalGetter() {
		super("Wallet Journal");
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<EveApiAccount> accounts, final boolean saveHistory) {
		this.saveHistory = saveHistory;
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected void set(Set<MyJournal> values, Date nextUpdate) {
		getOwner().setJournal(values);
		getOwner().setJournalNextUpdate(nextUpdate);
	}

	@Override
	protected Set<MyJournal> get() {
		if (saveHistory) {
			return getOwner().getJournal();
		} else {
			return new HashSet<MyJournal>();
		}
	}

	@Override
	protected WalletJournalResponse getResponse(final boolean bCorp, final int accountKey, final long fromID, final int rowCount) throws ApiException {
		if (bCorp) {
			return new CorpWalletJournalParser()
					.getResponse(EveApiOwner.getApiAuthorization(getOwner()), accountKey, fromID, rowCount);
		} else {
			return new CharWalletJournalParser()
					.getWalletJournalResponse(EveApiOwner.getApiAuthorization(getOwner()), fromID, rowCount);
		}
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getJournalNextUpdate();
	}

	@Override
	protected Set<MyJournal> convertData(final WalletJournalResponse response, final int accountKey) {
		return ApiConverter.convertJournals(response.getAll(), getOwner(), accountKey);
	}

	@Override
	protected void updateFailed(final EveApiOwner ownerFrom, final EveApiOwner ownerTo) {
		ownerTo.setJournal(ownerFrom.getJournal());
		ownerTo.setJournalNextUpdate(ownerFrom.getJournalNextUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		if (bCorp) {
			return EveApiAccessMask.JOURNAL_CORP.getAccessMask();
		} else {
			return EveApiAccessMask.JOURNAL_CHAR.getAccessMask();
		}
	}

	@Override
	protected long getId(MyJournal v) {
		return v.getRefID();
	}
}