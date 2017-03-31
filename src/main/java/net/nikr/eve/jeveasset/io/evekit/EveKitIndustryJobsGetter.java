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
import enterprises.orbital.evekit.client.model.IndustryJob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;


public class EveKitIndustryJobsGetter extends AbstractEveKitListGetter<IndustryJob> {

	private enum Runs { ACTIVE_PAUSED_READY, MONTHS, ALL }
	
	private Runs run;
	private Map<EveKitOwner, Set<MyIndustryJob>> industryJobs;

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		industryJobs = new HashMap<EveKitOwner, Set<MyIndustryJob>>();
		if (Settings.get().getEveKitIndustryJobsHistory() == 0) {
			run = Runs.ALL;
			super.load(updateTask, owners);
		} else {
			run = Runs.ACTIVE_PAUSED_READY;
			super.load(updateTask, owners);
			run = Runs.MONTHS;
			super.load(updateTask, owners);
		}
	}

	@Override
	protected List<IndustryJob> get(EveKitOwner owner, Long contid) throws ApiException {
		if (run == Runs.ACTIVE_PAUSED_READY) { //Status 1,2,3 = Active, Paused, Ready
			return getCommonApi().getIndustryJobs(owner.getAccessKey(), owner.getAccessCred(), null, contid, MAX_RESULTS, REVERSE,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, industryJobsFilter(), null, null, null, null, null, null, null);
		}
		if (run == Runs.MONTHS || run == Runs.ALL) { //months
			return getCommonApi().getIndustryJobs(owner.getAccessKey(), owner.getAccessCred(), null, contid, MAX_RESULTS, REVERSE,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, dateFilter(Settings.get().getEveKitIndustryJobsHistory()), null, null);
		}
		return new ArrayList<IndustryJob>();
	}

	@Override
	protected void set(EveKitOwner owner, List<IndustryJob> data) throws ApiException {
		Set<MyIndustryJob> set = industryJobs.get(owner);
		if (set == null) { //New owner
			 set = new HashSet<MyIndustryJob>();
			if (loadCid(owner) != null) { //Old
				set.addAll(owner.getIndustryJobs());
			}
			industryJobs.put(owner, set);
		}
		set.addAll(EveKitConverter.convertIndustryJobs(data, owner)); //New
		owner.setIndustryJobs(new ArrayList<MyIndustryJob>(set)); //All
	}

	@Override
	protected long getCid(IndustryJob obj) {
		return obj.getCid();
	}

	@Override
	protected boolean isNow(IndustryJob obj) {
		return obj.getLifeEnd() == Long.MAX_VALUE;
	}

	@Override
	protected String getTaskName() {
		return "Industry Jobs";
	}

	@Override
	protected int getProgressStart() {
		switch (run) {
			case ACTIVE_PAUSED_READY: return 0;
			case MONTHS: return 50;
			case ALL: return 0;
			default: return 0;
		}
	}

	@Override
	protected int getProgressEnd() {
		switch (run) {
			case ACTIVE_PAUSED_READY: return 50;
			case MONTHS: return 100;
			case ALL: return 100;
			default: return 100;
		}
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.INDUSTRY_JOBS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		owner.setIndustryJobsNextUpdate(date);
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

	@Override
	protected void saveCid(EveKitOwner owner, Long contid) {
		switch (run) {
			case ACTIVE_PAUSED_READY: owner.setIndustryJobsActiveContID(contid); break;
			case MONTHS: owner.setIndustryJobsDateContID(contid); break;
			case ALL: owner.setIndustryJobsDateContID(contid); break;
			default: owner.setIndustryJobsDateContID(contid); break;
		}
	}

	@Override
	protected Long loadCid(EveKitOwner owner) {
		switch (run) {
			case ACTIVE_PAUSED_READY: return owner.getIndustryJobsActiveContID();
			case MONTHS: return owner.getIndustryJobsDateContID();
			case ALL: return owner.getIndustryJobsDateContID();
			default: return owner.getIndustryJobsDateContID();
		}
	}

}
