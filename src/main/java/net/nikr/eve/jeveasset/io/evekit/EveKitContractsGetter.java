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
import enterprises.orbital.evekit.client.model.Contract;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;


public class EveKitContractsGetter extends AbstractEveKitListGetter<Contract> {

	private enum Runs { IN_PROGRESS, MONTHS, ALL} 
	
	private Runs run;
	private Map<EveKitOwner, Map<MyContract, List<MyContractItem>>> contracts;
	
	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		contracts = new HashMap<EveKitOwner, Map<MyContract, List<MyContractItem>>>();
		if (Settings.get().getEveKitContractsHistory() == 0) {
			run = Runs.ALL;
			super.load(updateTask, owners);
		} else {
			run = Runs.IN_PROGRESS;
			super.load(updateTask, owners);
			run = Runs.MONTHS;
			super.load(updateTask, owners);
		}
	}

	@Override
	protected List<Contract> get(EveKitOwner owner, Long contid) throws ApiException {
		if (run == Runs.IN_PROGRESS) { //In-Progress
			return getCommonApi().getContracts(owner.getAccessKey(), owner.getAccessCred(), null, contid, MAX_RESULTS, REVERSE,
				null, null, null, null, null, null, null, null, contractsFilter(), null, null, null, null, null, null, null, null, null, null, null, null, null);
		}
		if (run == Runs.MONTHS || run == Runs.ALL) { //months
			return getCommonApi().getContracts(owner.getAccessKey(), owner.getAccessCred(), null, contid, MAX_RESULTS, REVERSE,
					null, null, null, null, null, null, null, null, null, null, null, null, null, dateFilter(Settings.get().getEveKitContractsHistory()), null, null, null, null, null, null, null, null);
		}
		return new ArrayList<Contract>();
	}

	@Override
	protected void set(EveKitOwner owner, List<Contract> data) throws ApiException {
		Map<MyContract, List<MyContractItem>> map = contracts.get(owner);
		if (map == null) { //New owner
			map = new HashMap<MyContract, List<MyContractItem>>();
			contracts.put(owner, map);
		}
		map.putAll(EveKitConverter.convertContracts(data)); //New
		owner.setContracts(map); //All
	}

	@Override
	protected long getCid(Contract obj) {
		return obj.getCid();
	}

	@Override
	protected boolean isNow(Contract obj) {
		return obj.getLifeEnd() == Long.MAX_VALUE;
	}

	@Override
	protected String getTaskName() {
		return "Contracts";
	}

	@Override
	protected int getProgressStart() {
		switch(run) {
			case IN_PROGRESS: return 0;
			case MONTHS: return 15;
			case ALL: return 0;
			default: return 0;
		}
	}

	@Override
	protected int getProgressEnd() {
		switch(run) {
			case IN_PROGRESS: return 15;
			case MONTHS: return 30;
			case ALL: return 30;
			default: return 30;
		}
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.CONTRACTS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		owner.setContractsNextUpdate(date);
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

	@Override
	protected void saveCid(EveKitOwner owner, Long contid) { } //Always get all data

	@Override
	protected Long loadCid(EveKitOwner owner) {
		return null; //Always get all data
	}
}
