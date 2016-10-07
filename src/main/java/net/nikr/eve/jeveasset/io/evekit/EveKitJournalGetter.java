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
package net.nikr.eve.jeveasset.io.evekit;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.WalletJournal;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitJournalGetter extends AbstractEveKitGetter {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected void get(EveKitOwner owner) throws ApiException {
	  List<WalletJournal> journalEntries = new ArrayList<>();
		List<WalletJournal> batch = getCommonApi().getJournalEntries(owner.getAccessKey(), owner.getAccessCred(), null, null, Integer.MAX_VALUE, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		while (!batch.isEmpty()) {
		  journalEntries.addAll(batch);
		  batch = getCommonApi().getJournalEntries(owner.getAccessKey(), owner.getAccessCred(), null, batch.get(batch.size() - 1).getCid(), Integer.MAX_VALUE, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		}
		owner.setJournal(EveKitConverter.convertJournals(journalEntries, owner));
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
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

	

}
