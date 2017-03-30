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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitContractsGetter extends AbstractEveKitListGetter<Contract> {

	private enum Runs { IN_PROGRESS, LAST_3_MONTHS }
	
	private Runs run;
	private Map<EveKitOwner, Set<Contract>> contracts;
	
	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		contracts = new HashMap<EveKitOwner, Set<Contract>>();
		run = Runs.IN_PROGRESS;
		super.load(updateTask, owners);

		run = Runs.LAST_3_MONTHS;
		super.load(updateTask, owners);
	}

	@Override
	protected List<Contract> get(EveKitOwner owner, Long contid) throws ApiException {
		if (run == Runs.IN_PROGRESS) { //In-Progress
			return getCommonApi().getContracts(owner.getAccessKey(), owner.getAccessCred(), null, contid, MAX_RESULTS, REVERSE,
				null, null, null, null, null, null, null, null, contractsFilter(), null, null, null, null, null, null, null, null, null, null, null, null, null);
		}
		if (run == Runs.LAST_3_MONTHS) { //3 months
			return getCommonApi().getContracts(owner.getAccessKey(), owner.getAccessCred(), null, contid, MAX_RESULTS, REVERSE,
					null, null, null, null, null, null, null, null, null, null, null, null, null, dateFilter(), null, null, null, null, null, null, null, null);
		}
		return new ArrayList<Contract>();
	}

	@Override
	protected void set(EveKitOwner owner, List<Contract> data) throws ApiException {
		Set<Contract> set = contracts.get(owner);
		if (set == null) { //New owner
			set = new HashSet<Contract>();
			contracts.put(owner, set);
		}
		set.addAll(data); //Add newest data
		owner.setContracts(EveKitConverter.convertContracts(new ArrayList<Contract>(set)));
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
		if (run == Runs.IN_PROGRESS) { //In-Progress
			return 0;
		}
		if (run == Runs.LAST_3_MONTHS) { //Expired in that last 3 months
			return 15;
		}
		return 15;
	}

	@Override
	protected int getProgressEnd() {
		if (run == Runs.IN_PROGRESS) { //In-Progress
			return 15;
		}
		if (run == Runs.LAST_3_MONTHS) { //Expired in that last 3 months
			return 30;
		}
		return 30;
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
	protected void saveCid(EveKitOwner owner, Long contid) {
		if (run == Runs.IN_PROGRESS) { //In-Progress
			owner.setContractsInProgressContID(contid);
		}
		if (run == Runs.LAST_3_MONTHS) { //Expired in that last 3 months
			owner.setContractsDateContID(contid);
		}
	}

	@Override
	protected Long loadCid(EveKitOwner owner) {
		if (run == Runs.IN_PROGRESS) { //In-Progress
			return owner.getContractsInProgressContID();
		} else {
			return owner.getContractsDateContID();
		}
	}
}
