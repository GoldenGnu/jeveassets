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


import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.ContractItem;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;


public class EveKitContractItemsGetter extends AbstractEveKitIdGetter<ContractItem> {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}
	
	@Override
	protected List<ContractItem> get(EveKitOwner owner, long id) throws ApiException {
		//Get all items matching contractID
		return getCommonApi().getContractItems(owner.getAccessKey(), owner.getAccessCred(), null, null, MAX_RESULTS, REVERSE,
				valueFilter(id), null, null, null, null, null, null);
	}

	@Override
	protected void set(EveKitOwner owner, List<ContractItem> data) throws ApiException {
		EveKitConverter.convertContractItems(owner.getContracts(), data);
	}

	@Override
	protected Set<Long> getIDs(EveKitOwner owner) throws ApiException {
		Set<Long> ids = new HashSet<Long>();
		for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
			if (!entry.getKey().isCourier() //Do not get courier contracts
					&& entry.getValue().isEmpty()) //Only get items once (Contract items can not be changed)
			ids.add(entry.getKey().getContractID());
		}
		return ids;
	}

	@Override
	protected boolean isNow(ContractItem obj) {
		return obj.getLifeEnd() == Long.MAX_VALUE;
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
	protected void setNextUpdate(EveKitOwner owner, Date date) { } //Only relevent for the Contracts API (Not contract items)

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

}
