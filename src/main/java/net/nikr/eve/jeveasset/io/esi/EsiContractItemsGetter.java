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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterContractsItemsResponse;
import net.troja.eve.esi.model.CorporationContractsItemsResponse;

public class EsiContractItemsGetter extends AbstractEsiGetter {

	private final List<EsiOwner> owners;
	private static Map<Long, List<MyContract>> contracts;

	public EsiContractItemsGetter(UpdateTask updateTask, EsiOwner owner, List<EsiOwner> owners) {
		super(updateTask, owner, false, Settings.getNow(), TaskType.CONTRACT_ITEMS, NO_RETRIES);
		this.owners = owners;
	}

	public static void reset() {
		contracts = null;
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		createContracts(owners);
		if (owner.isCorporation()) {
			Map<MyContract, List<CorporationContractsItemsResponse>> responses = updateList(contracts.get(owner.getOwnerID()), DEFAULT_RETRIES, new ListHandler<MyContract, List<CorporationContractsItemsResponse>>() {
				@Override
				public List<CorporationContractsItemsResponse> get(ApiClient apiClient, MyContract t) throws ApiException {
					return getContractsApiAuth(apiClient).getCorporationsCorporationIdContractsContractIdItems(t.getContractID(), (int) owner.getOwnerID(), DATASOURCE, null, USER_AGENT, null);
				}
			});
			for (Map.Entry<MyContract, List<CorporationContractsItemsResponse>> entry : responses.entrySet()) {
				owner.setContracts(EsiConverter.toContractItemsCorporation(entry.getKey(), entry.getValue(), owner));
			}
		} else {
			Map<MyContract, List<CharacterContractsItemsResponse>> responses = updateList(contracts.get(owner.getOwnerID()), DEFAULT_RETRIES, new ListHandler<MyContract, List<CharacterContractsItemsResponse>>() {
				@Override
				public List<CharacterContractsItemsResponse> get(ApiClient apiClient, MyContract t) throws ApiException {
					return getContractsApiAuth(apiClient).getCharactersCharacterIdContractsContractIdItems((int) owner.getOwnerID(), t.getContractID(), DATASOURCE, null, USER_AGENT, null);
				}
			});
			for (Map.Entry<MyContract, List<CharacterContractsItemsResponse>> entry : responses.entrySet()) {
				owner.setContracts(EsiConverter.toContractItems(entry.getKey(), entry.getValue(), owner));
			}
		}
	}

	private static synchronized void createContracts(List<EsiOwner> owners) {
		if (contracts == null) {
			contracts = new HashMap<Long, List<MyContract>>();
			Set<MyContract> uniqueContacts = new HashSet<MyContract>();
			Map<Long, EsiOwner> uniqueOwners = new HashMap<Long, EsiOwner>();
			for (EsiOwner esiOwner : owners) {
				uniqueOwners.put(esiOwner.getOwnerID(), esiOwner);
				contracts.put(esiOwner.getOwnerID(), new ArrayList<MyContract>());
				for (Map.Entry<MyContract, List<MyContractItem>> entry : esiOwner.getContracts().entrySet()) {
					MyContract contract = entry.getKey();
					if (contract.isIgnoreContract()) {
						continue; //Ignore contracts without items
					}
					if (entry.getValue() != null && !entry.getValue().isEmpty()) { 
						continue; //Ignore contracts that have been already updated
					}
					uniqueContacts.add(contract);
				}
			}
			for (MyContract contract : uniqueContacts) {
				if (uniqueOwners.containsKey(contract.getIssuerID())) {
					contracts.get(contract.getIssuerID()).add(contract);
				} else if (uniqueOwners.containsKey(contract.getIssuerCorpID())) {
					contracts.get(contract.getIssuerCorpID()).add(contract);
				} else if (uniqueOwners.containsKey(contract.getAssigneeID())) {
					contracts.get(contract.getAssigneeID()).add(contract);
				} else if (uniqueOwners.containsKey(contract.getAcceptorID())) {
					contracts.get(contract.getAcceptorID()).add(contract);
				}
			}
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		//We will never update again...
	}

	@Override
	protected boolean inScope() {
		return owner.isContracts();
	}

}
