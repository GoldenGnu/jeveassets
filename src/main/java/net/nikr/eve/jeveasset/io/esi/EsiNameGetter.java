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
package net.nikr.eve.jeveasset.io.esi;

import java.util.ArrayList;
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
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterNamesResponse;
import net.troja.eve.esi.model.UniverseNamesResponse;


public class EsiNameGetter extends AbstractEsiGetter {

	private final List<OwnerType> ownerTypes;

	public EsiNameGetter(UpdateTask updateTask, List<OwnerType> ownerTypes) {
		super(updateTask, null, false, Settings.getNow(), TaskType.OWNER_ID_TO_NAME, NO_RETRIES);
		this.ownerTypes = ownerTypes;
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		Set<Integer> ids = getOwnerIDs(ownerTypes);
		Map<List<Integer>, List<UniverseNamesResponse>> responses = updateList(splitList(ids, UNIVERSE_BATCH_SIZE), NO_RETRIES, new ListHandler<List<Integer>, List<UniverseNamesResponse>>() {
			@Override
			public List<UniverseNamesResponse> get(ApiClient apiClient, List<Integer> t) throws ApiException {
				try {
					return getUniverseApiOpen(apiClient).postUniverseNames(t, DATASOURCE, USER_AGENT, null);
				} catch (ApiException ex) {
					if (ex.getCode() == 404 && ex.getResponseBody().toLowerCase().contains("ensure all ids are valid before resolving")) {
						return null; //Ignore this error we will use another endpoint instead
					} else {
						throw ex;
					}
				}
			}
		});

		Set<Integer> retries = new HashSet<Integer>(ids);
		for (Map.Entry<List<Integer>, List<UniverseNamesResponse>> entry : responses.entrySet()) {
			for (UniverseNamesResponse lookup : entry.getValue()) {
				Settings.get().getOwners().put((long)lookup.getId(), lookup.getName());
			}
			retries.removeAll(entry.getKey());
		}
		Map<List<Integer>, List<CharacterNamesResponse>> retriesResponses = updateList(splitList(retries, UNIVERSE_BATCH_SIZE), DEFAULT_RETRIES, new ListHandler<List<Integer>, List<CharacterNamesResponse>>() {
			@Override
			public List<CharacterNamesResponse> get(ApiClient apiClient, List<Integer> k) throws ApiException {
				List<Long> list = new ArrayList<Long>();
				for (Integer i : k) {
					list.add(i.longValue());
				}
				return getCharacterApiOpen(apiClient).getCharactersNames(list, DATASOURCE, USER_AGENT, null);
			}
		});
		for (Map.Entry<List<Integer>, List<CharacterNamesResponse>> entry : retriesResponses.entrySet()) {
			for (CharacterNamesResponse lookup : entry.getValue()) {
				Settings.get().getOwners().put((long)lookup.getCharacterId(), lookup.getCharacterName());
			}
		}
	}

	private Set<Integer> getOwnerIDs(List<OwnerType> ownerTypes) {
		Set<Integer> list = new HashSet<Integer>();
		for (OwnerType ownerType : ownerTypes) {
			addOwnerID(list, ownerType.getOwnerID());
			for (MyIndustryJob myIndustryJob : ownerType.getIndustryJobs()) {
				addOwnerID(list, myIndustryJob.getInstallerID());
			}
			for (MyContract contract : ownerType.getContracts().keySet()) {
				addOwnerID(list, contract.getAcceptorID());
				addOwnerID(list, contract.getAssigneeID());
				addOwnerID(list, contract.getIssuerCorpID());
				addOwnerID(list, contract.getIssuerID());
			}
			for (MyTransaction transaction : ownerType.getTransactions()) {
				addOwnerID(list, transaction.getClientID());
			}
			for (MyJournal journal : ownerType.getJournal()) {
				addOwnerID(list, journal.getFirstPartyID());
				addOwnerID(list, journal.getSecondPartyID());
			}
		}
		return list;
	}

	private void addOwnerID(Set<Integer> list, Number number) {
		//Ignore null
		if (number == null) {
			return;
		}
		//Ignore Locations
		if (!ApiIdConverter.getLocation(number.longValue()).isEmpty()) {
			return;
		}
		int l = number.intValue();
		if (l >= 100) {
			list.add(l);
		}
	}

	@Override
	protected void setNextUpdate(Date date) { }

	@Override
	protected boolean inScope() {
		return true;
	}

}
