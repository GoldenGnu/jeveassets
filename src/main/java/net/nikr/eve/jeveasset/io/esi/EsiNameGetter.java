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
package net.nikr.eve.jeveasset.io.esi;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.UniverseNamesResponse;


public class EsiNameGetter extends AbstractEsiGetter {

	private final Set<Integer> ids = new HashSet<Integer>();

	public EsiNameGetter(UpdateTask updateTask, List<OwnerType> ownerTypes) {
		super(updateTask, null, false, Settings.getNow(), TaskType.OWNER_ID_TO_NAME);
		Set<Long> list = new HashSet<Long>();
		for (OwnerType ownerType : ownerTypes) {
			list.add(ownerType.getOwnerID()); //Just to be sure
			for (MyIndustryJob myIndustryJob : ownerType.getIndustryJobs()) {
				list.add(myIndustryJob.getInstallerID());
			}
			for (MyContract contract : ownerType.getContracts().keySet()) {
				list.add(contract.getAcceptorID());
				list.add(contract.getAssigneeID());
				list.add(contract.getIssuerCorpID());
				list.add(contract.getIssuerID());
			}
			for (MyTransaction transaction : ownerType.getTransactions()) {
				list.add(transaction.getClientID());
			}
			for (MyJournal journal : ownerType.getJournal()) {
				if (journal.getFirstPartyID() != null) {
					list.add((long) journal.getFirstPartyID());
				}
				if (journal.getSecondPartyID() != null) {
					list.add((long) journal.getSecondPartyID());
				}
			}
		}
		for (long id : list) {
			try {
				ids.add(Math.toIntExact(id));
			} catch (ArithmeticException ex) {
				//Ignore...
			}
		}
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		List<List<Integer>> batches = splitList(ids, UNIVERSE_BATCH_SIZE);

		Map<List<Integer>, List<UniverseNamesResponse>> responses = updateList(batches, new ListHandler<List<Integer>, List<UniverseNamesResponse>>() {
			@Override
			public List<UniverseNamesResponse> get(ApiClient apiClient, List<Integer> t) throws ApiException {
				return getUniverseApiOpen(apiClient).postUniverseNames(t, DATASOURCE, System.getProperty("http.agent"), null);
			}
		});

		for (Map.Entry<List<Integer>, List<UniverseNamesResponse>> entry : responses.entrySet()) {
			for (UniverseNamesResponse lookup : entry.getValue()) {
				Settings.get().getOwners().put((long)lookup.getId(), lookup.getName());
			}
		}
	}

	@Override
	protected void setNextUpdate(Date date) { }

	@Override
	protected boolean inScope() {
		return true;
	}

	@Override
	protected boolean enabled() {
		return EsiScopes.NAMES.isEnabled();
	}

}
