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
import enterprises.orbital.evekit.client.model.WalletJournal;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;


public class EveKitJournalGetter extends AbstractEveKitGetter implements EveKitPagesHandler<WalletJournal> {

	public EveKitJournalGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, false, owner.getJournalNextUpdate(), TaskType.JOURNAL, false, null);
	}

	@Override
	protected void get(ApiClient apiClient, Long at, boolean first) throws ApiException {
		List<WalletJournal> data = updatePages(this);
		if (data == null) {
			return;
		}
		owner.setJournal(EveKitConverter.toJournals(data, owner, loadCID() != null));
	}

	

	@Override
	public List<WalletJournal> get(ApiClient apiClient, String at, Long contid, Integer maxResults) throws ApiException {
		//months
		return getCommonApi(apiClient).getJournalEntries(owner.getAccessKey(), owner.getAccessCred(), at, contid, maxResults, false,
				null, null, dateFilter(Settings.get().getEveKitJournalHistory()), null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Override
	public long getCID(WalletJournal obj) {
		return obj.getCid();
	}

	@Override
	public Long getLifeStart(WalletJournal obj) {
		return obj.getLifeStart();
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.JOURNAL.getAccessMask();
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setJournalNextUpdate(date);
	}

	@Override
	public void saveCID(Long contid) {
		owner.setJournalCID(contid);
	}

	@Override
	public Long loadCID() {
		return owner.getJournalCID();
	}
}
