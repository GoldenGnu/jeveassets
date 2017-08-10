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
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class EveKitContractItemsGetter extends AbstractEveKitListGetter<ContractItem> {

	private final Map<EveKitOwner, Set<Integer>> ids = new HashMap<EveKitOwner, Set<Integer>>();
	private final Map<EveKitOwner, Map<Integer, MyContract>> contracts = new HashMap<EveKitOwner, Map<Integer, MyContract>>();

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		ids.clear();
		super.load(updateTask, owners);
	}

	@Override
	protected List<ContractItem> get(EveKitOwner owner, String at, Long contid) throws ApiException {
		//Get all items matching contractID
		return getCommonApi().getContractItems(owner.getAccessKey(), owner.getAccessCred(), null, contid, getMaxResults(), getReverse(),
				valuesFilter(getIDs(owner)), null, null, null, null, null, null);
	}

	@Override
	protected void set(EveKitOwner owner, List<ContractItem> data) throws ApiException {
		Map<Long, List<ContractItem>> map = new HashMap<Long, List<ContractItem>>();
		for (ContractItem contractItem : data) {
			List<ContractItem> list = map.get(contractItem.getContractID());
			if (list == null) {
				list = new ArrayList<ContractItem>();
				map.put(contractItem.getContractID(), list);
			}
			list.add(contractItem);
		}
		for (Map.Entry<Long, List<ContractItem>> entry : map.entrySet()) {
			owner.setContracts(EveKitConverter.toContractItems(contracts.get(owner).get(RawConverter.toInteger(entry.getKey())), entry.getValue(), owner));
		}

	}

	protected Set<Integer> getIDs(EveKitOwner owner) throws ApiException {
		Set<Integer> set = ids.get(owner);
		if (set == null) {
			set = new HashSet<Integer>();
			Map<Integer, MyContract> map = new HashMap<Integer, MyContract>();
			ids.put(owner, set);
			contracts.put(owner, map);
			for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
				if (!entry.getKey().isCourier() //Do not get courier contracts
						&& entry.getValue().isEmpty()) { //Only get items once (Contract items can not be changed)
					set.add(entry.getKey().getContractID());
					map.put(entry.getKey().getContractID(), entry.getKey());
				}
			}
		}
		return set;
	}

	@Override
	protected Long getLifeStart(ContractItem obj) {
		return obj.getLifeStart();
	}

	@Override
	protected String getTaskName() {
		return "Contract Items";
	}

	@Override
	protected int getProgressStart() {
		return 30;
	}

	@Override
	protected int getProgressEnd() {
		return 90;
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.CONTRACTS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
	} //Only relevent for the Contracts API (Not contract items)

	@Override
	protected Date getNextUpdate(EveKitOwner owner) {
		return Settings.getNow();
	} //Only relevent for the Contracts API (Not contract items)

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

	@Override
	protected long getCID(ContractItem obj) {
		return obj.getCid();
	}

	@Override
	protected void saveCID(EveKitOwner owner, Long cid) {
	} //Always get all data

	@Override
	protected Long loadCID(EveKitOwner owner) {
		return null; //Always get all data
	}

}
