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
package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.JournalEntry;
import com.beimin.eveapi.parser.character.CharWalletJournalParser;
import com.beimin.eveapi.parser.corporation.CorpWalletJournalParser;
import com.beimin.eveapi.response.shared.WalletJournalResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;

public class JournalGetter extends AbstractApiGetter<WalletJournalResponse> {

	private static final int ROW_COUNT = 1000;
	private final boolean saveHistory;

	public JournalGetter(UpdateTask updateTask, EveApiOwner owner, boolean saveHistory) {
		super(updateTask, owner, false, owner.getJournalNextUpdate(), TaskType.JOURNAL);
		this.saveHistory = saveHistory;
	}

	@Override
	protected void get(String updaterStatus) throws ApiException {
		Set<Integer> accountKeys = new HashSet<>();
		if (owner.isCorporation()) {
			for (int i = 1000; i <= 1006; i++) { //For each wallet division
				accountKeys.add(i);
			}
		} else {
			accountKeys.add(1000);
		}
		Map<Integer, List<JournalEntry>> updateList = updateList(accountKeys, NO_RETRIES, new ListHandler<Integer, List<JournalEntry>>() {
			@Override
			protected List<JournalEntry> get(String listUpdaterStatus, Integer t) throws ApiException {
				return updateIDs(new HashSet<Long>(), NO_RETRIES, new IDsHandler<JournalEntry>() {
					@Override
					protected List<JournalEntry> get(String idUpdaterStatus, Long fromID) throws ApiException {
						if (fromID == null) {
							fromID = 0L;
						}
						if (owner.isCorporation()) {
							WalletJournalResponse response = new CorpWalletJournalParser()
									.getResponse(EveApiOwner.getApiAuthorization(owner), t, fromID, ROW_COUNT);
							if (!handle(response, listUpdaterStatus + " " + idUpdaterStatus)) {
								return null;
							}
							return response.getAll();
						} else {
							WalletJournalResponse response = new CharWalletJournalParser()
									.getWalletJournalResponse(EveApiOwner.getApiAuthorization(owner), fromID, ROW_COUNT);
							if (!handle(response, listUpdaterStatus + " " + idUpdaterStatus)) {
								return null;
							}
							return response.getAll();
						}
					}

					@Override
					protected Long getID(JournalEntry response) {
						return response.getRefID();
					}
				});
			}
		});
		Set<MyJournal> journal = new HashSet<MyJournal>();
		for (Map.Entry<Integer, List<JournalEntry>> entry : updateList.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			journal.addAll(EveApiConverter.toJournal(entry.getValue(), owner, entry.getKey(), saveHistory));
			
		}
		owner.setJournal(journal);
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setJournalNextUpdate(date);
	}

	@Override
	protected long requestMask() {
		if (owner.isCorporation()) {
			return EveApiAccessMask.JOURNAL_CORP.getAccessMask();
		} else {
			return EveApiAccessMask.JOURNAL_CHAR.getAccessMask();
		}
	}
}
