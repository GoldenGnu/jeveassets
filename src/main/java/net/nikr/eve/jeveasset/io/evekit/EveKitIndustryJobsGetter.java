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

	private enum Runs {
		ACTIVE_PAUSED_READY, MONTHS, ALL
	}

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
	public void load(UpdateTask updateTask, List<EveKitOwner> owners, boolean first) {
		industryJobs = new HashMap<EveKitOwner, Set<MyIndustryJob>>();
		run = Runs.ALL;
		super.load(updateTask, owners, first);
	}

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners, Long at) {
		industryJobs = new HashMap<EveKitOwner, Set<MyIndustryJob>>();
		run = Runs.ALL;
		super.load(updateTask, owners, at);
	}

	@Override
	protected List<IndustryJob> get(EveKitOwner owner, String at, Long contid) throws ApiException {
		if (run == Runs.ACTIVE_PAUSED_READY) { //Status 1,2,3 = Active, Paused, Ready
			return getCommonApi().getIndustryJobs(owner.getAccessKey(), owner.getAccessCred(), null, contid, getMaxResults(), getReverse(),
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, industryJobsFilter(), null, null, null, null, null, null, null);
		}
		if (run == Runs.MONTHS) { //months
			return getCommonApi().getIndustryJobs(owner.getAccessKey(), owner.getAccessCred(), at, contid, getMaxResults(), getReverse(),
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, dateFilter(Settings.get().getEveKitIndustryJobsHistory()), null, null);
		}
		if (run == Runs.ALL) {
			return getCommonApi().getIndustryJobs(owner.getAccessKey(), owner.getAccessCred(), at, contid, getMaxResults(), getReverse(),
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		}
		return new ArrayList<IndustryJob>();
	}

	@Override
	protected void set(EveKitOwner owner, List<IndustryJob> data) throws ApiException {
		Set<MyIndustryJob> set = industryJobs.get(owner);
		if (set == null) { //New owner
			set = new HashSet<MyIndustryJob>();
			if (loadCID(owner) != null) { //Old
				set.addAll(owner.getIndustryJobs());
			}
			industryJobs.put(owner, set);
		}
		set.addAll(EveKitConverter.toIndustryJobs(data, owner)); //New
		owner.setIndustryJobs(new ArrayList<MyIndustryJob>(set)); //All
	}

	@Override
	protected long getCID(IndustryJob obj) {
		return obj.getCid();
	}

	@Override
	protected Long getLifeStart(IndustryJob obj) {
		return obj.getLifeStart();
	}

	@Override
	protected String getTaskName() {
		return "Industry Jobs";
	}

	@Override
	protected int getProgressStart() {
		switch (run) {
			case ACTIVE_PAUSED_READY:
				return 0;
			case MONTHS:
				return 50;
			case ALL:
				return 0;
			default:
				return 0;
		}
	}

	@Override
	protected int getProgressEnd() {
		switch (run) {
			case ACTIVE_PAUSED_READY:
				return 50;
			case MONTHS:
				return 100;
			case ALL:
				return 100;
			default:
				return 100;
		}
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.INDUSTRY_JOBS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		if (run == Runs.MONTHS || run == Runs.ALL) { //Ignore first update...
			owner.setIndustryJobsNextUpdate(date);
		}
	}

	@Override
	protected Date getNextUpdate(EveKitOwner owner) {
		return owner.getIndustryJobsNextUpdate();
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

	@Override
	protected void saveCID(EveKitOwner owner, Long cid) {
		if (run == Runs.MONTHS || run == Runs.ALL) {
			owner.setIndustryJobsCID(cid);
		}
	}

	@Override
	protected Long loadCID(EveKitOwner owner) {
		if (run == Runs.MONTHS || run == Runs.ALL) {
			return owner.getIndustryJobsCID();
		} else {
			return null;
		}
	}
}
