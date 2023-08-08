/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Updatable;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.UniverseNamesResponse;


public class EsiNameGetter extends AbstractEsiGetter {

	private static final long ONE_DAY = 1000 * 60 * 60 * 24;
	private final List<OwnerType> ownerTypes;

	public EsiNameGetter(UpdateTask updateTask, List<OwnerType> ownerTypes) {
		super(updateTask, null, false, Settings.getNow(), TaskType.OWNER_ID_TO_NAME);
		this.ownerTypes = ownerTypes;
	}

	@Override
	protected void update() throws ApiException {
		Set<Integer> ids = getOwnerIDs(ownerTypes);
		Map<List<Integer>, List<UniverseNamesResponse>> responses = updateList(splitList(ids, UNIVERSE_BATCH_SIZE), DEFAULT_RETRIES, new ListHandler<List<Integer>, List<UniverseNamesResponse>>() {
			@Override
			public ApiResponse<List<UniverseNamesResponse>> get(List<Integer> t) throws ApiException {
				try {
					return getUniverseApiOpen().postUniverseNamesWithHttpInfo(t, DATASOURCE);
				} catch (ApiException ex) {
					if (ex.getCode() == 404 && ex.getResponseBody().toLowerCase().contains("ensure all ids are valid before resolving")) {
						handleHeaders(ex);
						return null; //Ignore this error we will use another endpoint instead
					} else {
						throw ex;
					}
				}
			}
		});

		Set<Integer> retries = new HashSet<>(ids);
		for (Map.Entry<List<Integer>, List<UniverseNamesResponse>> entry : responses.entrySet()) {
			for (UniverseNamesResponse lookup : entry.getValue()) {
				Settings.get().getOwners().put((long)lookup.getId(), lookup.getName());
			}
			retries.removeAll(entry.getKey());
		}
		Map<List<Integer>, List<UniverseNamesResponse>> retryResponses = updateList(splitList(retries, 1), DEFAULT_RETRIES, new ListHandler<List<Integer>, List<UniverseNamesResponse>>() {
			@Override
			public ApiResponse<List<UniverseNamesResponse>> get(List<Integer> t) throws ApiException {
				try {
					return getUniverseApiOpen().postUniverseNamesWithHttpInfo(t, DATASOURCE);
				} catch (ApiException ex) {
					if (ex.getCode() == 404 && ex.getResponseBody().toLowerCase().contains("ensure all ids are valid before resolving")) {
						handleHeaders(ex);
						return null; //Ignore this error we will use another endpoint instead
					} else {
						throw ex;
					}
				}
			}
		});
		int count = 30;
		for (Map.Entry<List<Integer>, List<UniverseNamesResponse>> entry : retryResponses.entrySet()) {
			for (UniverseNamesResponse lookup : entry.getValue()) {
				Settings.get().getOwners().put((long)lookup.getId(), lookup.getName());
				Date date = new Date(System.currentTimeMillis() + (ONE_DAY * count));
				count--;
				if (count < 1) {
					count = 30;
				}
				Settings.get().getOwnersNextUpdate().put((long)lookup.getId(), date);
			}
		}
	}

	private Set<Integer> getOwnerIDs(List<OwnerType> ownerTypes) {
		Set<Integer> list = new HashSet<>();
		for (OwnerType ownerType : ownerTypes) {
			addOwnerID(list, ownerType.getOwnerID());
			for (MyIndustryJob myIndustryJob : ownerType.getIndustryJobs()) {
				addOwnerID(list, myIndustryJob.getInstallerID());
				addOwnerID(list, myIndustryJob.getCompletedCharacterID());
			}
			for (MyMarketOrder marketOrder : ownerType.getMarketOrders()) {
				addOwnerID(list, marketOrder.getIssuedBy());
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
				ContextType contextType = journal.getContextType();
				if (contextType == ContextType.ALLIANCE_ID
						|| contextType == ContextType.CHARACTER_ID
						|| contextType == ContextType.CORPORATION_ID
						) {
					addOwnerID(list, journal.getContextID());
				}
			}
			for (MyMining mining : ownerType.getMining()) {
				addOwnerID(list, mining.getCharacterID());
				addOwnerID(list, mining.getCorporationID());
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
		//Next Update
		Date nextUpdate = Settings.get().getOwnersNextUpdate().get(number.longValue());
		if (nextUpdate != null && !Updatable.isUpdatable(nextUpdate)) {
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
	protected boolean haveAccess() {
		return true;
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		return null;
	}

}
