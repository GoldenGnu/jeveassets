/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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

import enterprises.orbital.evekit.client.ApiException;
import enterprises.orbital.evekit.client.ApiResponse;
import enterprises.orbital.evekit.client.model.ContractItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;


public class EveKitContractItemsGetter extends AbstractEveKitGetter implements EveKitPagesHandler<ContractItem> {

	private final Map<Integer, MyContract> contracts = new HashMap<Integer, MyContract>();
	private Set<Integer> ids;

	public EveKitContractItemsGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, true, Settings.getNow(), TaskType.CONTRACT_ITEMS, false, null);
	}

	@Override
	protected void update(Long at, boolean first) throws ApiException {
		//Get all items matching contractIDs
		ids = getIDs(owner);
		if (ids.isEmpty()) { //return  - nothing to update
			return;
		}
		List<ContractItem> data = updatePages(this);
		if (data == null) {
			return;
		}
		Map<Integer, List<ContractItem>> map = new HashMap<Integer, List<ContractItem>>();
		for (ContractItem contractItem : data) {
			List<ContractItem> list = map.get(contractItem.getContractID());
			if (list == null) {
				list = new ArrayList<ContractItem>();
				map.put(contractItem.getContractID(), list);
			}
			list.add(contractItem);
		}
		for (Map.Entry<Integer, List<ContractItem>> entry : map.entrySet()) {
			owner.setContracts(EveKitConverter.toContractItems(contracts.get(entry.getKey()), entry.getValue(), owner));
		}
	}

	@Override
	public ApiResponse<List<ContractItem>> get(String at, Long contid, Integer maxResults) throws ApiException {
		return getCommonApi().getContractItemsWithHttpInfo(owner.getAccessKey(), owner.getAccessCred(), null, contid, maxResults, false,
				valuesFilter(ids), null, null, null, null, null, null);
	}

	protected Set<Integer> getIDs(EveKitOwner owner) throws ApiException {
		Set<Integer> set = new HashSet<Integer>();
		for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
			if (entry.getKey().isItemContract() //Do not get courier contracts
					&& entry.getValue().isEmpty()) { //Only get items once (Contract items can not be changed)
				set.add(entry.getKey().getContractID());
				contracts.put(entry.getKey().getContractID(), entry.getKey());
			}
		}
		return set;
	}

	@Override
	public Long getLifeStart(ContractItem obj) {
		return obj.getLifeStart();
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.CONTRACTS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(Date date) { } //Only relevent for the Contracts API (Not contract items)

	@Override
	public long getCID(ContractItem obj) {
		return obj.getCid();
	}

	@Override
	public void saveCID(Long cid) { } //Always get all data

	@Override
	public Long loadCID() {
		return null; //Always get all data
	}

}
