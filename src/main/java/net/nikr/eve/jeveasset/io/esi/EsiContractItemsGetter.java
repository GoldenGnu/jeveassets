/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import java.util.concurrent.atomic.AtomicInteger;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterContractsItemsResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CorporationContractsItemsResponse;
import net.troja.eve.esi.model.PublicContractsItemsResponse;

public class EsiContractItemsGetter extends AbstractEsiGetter {

	private final List<EsiOwner> owners;
	private static Map<Long, List<MyContract>> contracts;
	private static Map<Long, List<MyContract>> publicContracts;
	private final static AtomicInteger SIZE = new AtomicInteger(0);
	private final static AtomicInteger PROGRESS = new AtomicInteger(0);
	private final static int BATCH_SIZE = 20;

	private final boolean saveHistory;

	public EsiContractItemsGetter(UpdateTask updateTask, EsiOwner owner, List<EsiOwner> owners, boolean saveHistory) {
		super(updateTask, owner, false, Settings.getNow(), TaskType.CONTRACT_ITEMS);
		this.owners = owners;
		this.saveHistory = saveHistory;
	}

	public static void reset() {
		contracts = null;
		SIZE.set(0);
		PROGRESS.set(0);
	}

	@Override
	protected void update() throws ApiException {
		createContracts(owners);
		if (owner.isCorporation()) {
			List<List<MyContract>> updates = splitList(contracts.get(owner.getOwnerID()), BATCH_SIZE);
			Map<MyContract, List<CorporationContractsItemsResponse>> responses = new HashMap<>();
			boolean first = true;
			for (List<MyContract> list : updates) {
				if (first) {
					first = false;
				} else {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException ex) {
						throw new RuntimeException(ex);
					}
				}
				Map<MyContract, List<CorporationContractsItemsResponse>> response = updateList(list, DEFAULT_RETRIES, new ListHandler<MyContract, List<CorporationContractsItemsResponse>>() {
					@Override
					public ApiResponse<List<CorporationContractsItemsResponse>> get(MyContract t) throws ApiException {
						return getContractsApiAuth().getCorporationsCorporationIdContractsContractIdItemsWithHttpInfo(t.getContractID(), (int) owner.getOwnerID(), DATASOURCE, null, null);
					}
				});
				responses.putAll(response);
				PROGRESS.getAndAdd(list.size());
				setProgress(SIZE.get(), PROGRESS.get(), 0, 100);
			}
			owner.setContracts(EsiConverter.toContractItemsCorporation(responses, owner, saveHistory));
		} else {
			Map<MyContract, List<CharacterContractsItemsResponse>> responses = updateList(contracts.get(owner.getOwnerID()), DEFAULT_RETRIES, new ListHandler<MyContract, List<CharacterContractsItemsResponse>>() {
				@Override
				public ApiResponse<List<CharacterContractsItemsResponse>> get(MyContract t) throws ApiException {
					ApiResponse<List<CharacterContractsItemsResponse>> response = getContractsApiAuth().getCharactersCharacterIdContractsContractIdItemsWithHttpInfo((int) owner.getOwnerID(), t.getContractID(), DATASOURCE, null, null);
					PROGRESS.getAndAdd(1);
					setProgress(SIZE.get(), PROGRESS.get(), 0, 100);
					return response;
				}
			});
			owner.setContracts(EsiConverter.toContractItems(responses, owner, saveHistory));
		}
		//Public contracts (Have blueprint info Runs/Me/Te)
		Map<MyContract, List<PublicContractsItemsResponse>> responses = updatePagedMap(publicContracts.get(owner.getOwnerID()), new PagedListHandler<MyContract, PublicContractsItemsResponse>() {
			@Override
			protected List<PublicContractsItemsResponse> get(MyContract contract) throws ApiException {
				return updatePages(DEFAULT_RETRIES, new EsiPagesHandler<PublicContractsItemsResponse>() {
					@Override
					public ApiResponse<List<PublicContractsItemsResponse>> get(Integer page) throws ApiException {
						ApiResponse<List<PublicContractsItemsResponse>> response = getContractsApiOpen().getContractsPublicItemsContractIdWithHttpInfo(contract.getContractID(), DATASOURCE, null, page);
						PROGRESS.getAndAdd(1);
						setProgress(SIZE.get(), PROGRESS.get(), 0, 100);
						return response;
					}
				});
			}
		});
		owner.setContracts(EsiConverter.toContractItemsPublic(responses, owner, saveHistory));
	}

	private static synchronized void createContracts(List<EsiOwner> owners) {
		if (contracts == null) {
			contracts = new HashMap<>();
			publicContracts = new HashMap<>();
			Set<MyContract> uniqueContacts = new HashSet<>();
			Map<Long, EsiOwner> uniqueOwners = new HashMap<>();
			for (EsiOwner esiOwner : owners) {
				if (!esiOwner.isShowOwner()) {
					continue;
				}
				uniqueOwners.put(esiOwner.getOwnerID(), esiOwner);
				contracts.put(esiOwner.getOwnerID(), new ArrayList<>());
				publicContracts.put(esiOwner.getOwnerID(), new ArrayList<>());
				for (Map.Entry<MyContract, List<MyContractItem>> entry : esiOwner.getContracts().entrySet()) {
					MyContract contract = entry.getKey();
					if (contract.isIgnoreContract()) {
						continue; //Ignore contracts without items
					}
					if (entry.getValue() != null && !entry.getValue().isEmpty()) {
						continue; //Ignore contracts that have been already updated
					}
					if (esiOwner.isCorporation() && contract.isDeleted()) {
						continue; //Ignore deleted corporation contracts
					}
					if (!contract.isESI()) {
						continue; //No longer in ESI
					}
					uniqueContacts.add(contract);
					//Open public contracts
					if (contract.isPublic() && contract.isOpen()) {
						publicContracts.get(esiOwner.getOwnerID()).add(contract);
						SIZE.getAndIncrement();
					}
				}
			}
			for (MyContract contract : uniqueContacts) {
				if (uniqueOwners.containsKey(contract.getIssuerID())) {
					contracts.get(contract.getIssuerID()).add(contract);
					SIZE.getAndIncrement();
				} else if (uniqueOwners.containsKey(contract.getAssigneeID())) {
					contracts.get(contract.getAssigneeID()).add(contract);
					SIZE.getAndIncrement();
				} else if (uniqueOwners.containsKey(contract.getAcceptorID())) {
					contracts.get(contract.getAcceptorID()).add(contract);
					SIZE.getAndIncrement();
				} else if (uniqueOwners.containsKey(contract.getIssuerCorpID())) { //Last resort (Rate limited and access to less contract items)
					contracts.get(contract.getIssuerCorpID()).add(contract);
					SIZE.getAndIncrement();
				}
			}
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		//We will never update again...
	}

	@Override
	protected boolean haveAccess() {
		return owner.isContracts();
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		return null;
	}

}
