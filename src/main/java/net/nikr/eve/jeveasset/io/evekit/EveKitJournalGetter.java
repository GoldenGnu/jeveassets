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
package net.nikr.eve.jeveasset.io.evekit;


import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.WalletJournal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.journal.MyJournal;


public class EveKitJournalGetter extends AbstractEveKitListGetter<WalletJournal> {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected List<WalletJournal> get(EveKitOwner owner, String at, Long contid) throws ApiException {
		//months
		return getCommonApi().getJournalEntries(owner.getAccessKey(), owner.getAccessCred(), null, contid, getMaxResults(), getReverse(),
				null, null, dateFilter(Settings.get().getEveKitJournalHistory()), null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Override
	protected void set(EveKitOwner owner, List<WalletJournal> data) throws ApiException {
		Set<MyJournal> set = new HashSet<MyJournal>();
		if (loadCID(owner) != null) { //Old
			set.addAll(owner.getJournal());
		}
		set.addAll(EveKitConverter.convertJournals(data, owner)); //New
		owner.setJournal(set); //All
	}

	@Override
	protected long getCID(WalletJournal obj) {
		return obj.getCid();
	}

	@Override
	protected Long getLifeStart(WalletJournal obj) {
		return obj.getLifeStart();
	}

	@Override
	protected String getTaskName() {
		return "Journal";
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.JOURNAL.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		owner.setJournalNextUpdate(date);
	}

	@Override
	protected Date getNextUpdate(EveKitOwner owner) {
		return owner.getJournalNextUpdate();
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

	@Override
	protected void saveCID(EveKitOwner owner, Long contid) {
		owner.setJournalCID(contid);
	}

	@Override
	protected Long loadCID(EveKitOwner owner) {
		return owner.getJournalCID();
	}
}
