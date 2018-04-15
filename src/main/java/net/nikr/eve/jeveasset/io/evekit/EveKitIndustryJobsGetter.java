/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import enterprises.orbital.evekit.client.ApiClient;
import enterprises.orbital.evekit.client.ApiException;
import enterprises.orbital.evekit.client.model.IndustryJob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;


public class EveKitIndustryJobsGetter extends AbstractEveKitGetter implements EveKitPagesHandler<IndustryJob> {

	private enum Runs {
		ACTIVE_PAUSED_READY, MONTHS, ALL
	}

	private final List<Runs> runs = new ArrayList<Runs>();
	private Runs run;

	public EveKitIndustryJobsGetter(UpdateTask updateTask, EveKitOwner owner, boolean first) {
		super(updateTask, owner, false, owner.getIndustryJobsNextUpdate(), TaskType.INDUSTRY_JOBS, first, null);
		runs.add(Runs.ALL);
	}
	public EveKitIndustryJobsGetter(UpdateTask updateTask, EveKitOwner owner, Long at) {
		super(updateTask, owner, false, owner.getIndustryJobsNextUpdate(), TaskType.INDUSTRY_JOBS, false, at);
		runs.add(Runs.ALL);
	}
	public EveKitIndustryJobsGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, false, owner.getIndustryJobsNextUpdate(), TaskType.INDUSTRY_JOBS, false, null);
		if (Settings.get().getEveKitIndustryJobsHistory() == 0) {
			runs.add(Runs.ALL);
		} else {
			runs.add(Runs.ACTIVE_PAUSED_READY);
			runs.add(Runs.MONTHS);
		}
	}

	@Override
	protected void get(ApiClient apiClient, Long at, boolean first) throws ApiException {
		ArrayList<IndustryJob> data = new ArrayList<IndustryJob>();
		for (Runs r : runs) {
			run = r;
			List<IndustryJob> list = updatePages(this);
			if (list == null) {
				return;
			}
			data.addAll(list);
		}
		Set<MyIndustryJob> set = new HashSet<MyIndustryJob>();
		if (loadCID() != null) { //Old
			set.addAll(owner.getIndustryJobs());
		}
		set.addAll(EveKitConverter.toIndustryJobs(data, owner)); //New
		owner.setIndustryJobs(new ArrayList<MyIndustryJob>(set)); //All
	}

	@Override
	public List<IndustryJob> get(ApiClient apiClient, String at, Long contid, Integer maxResults) throws ApiException {
		switch (run) {
			case ACTIVE_PAUSED_READY:
				return getCommonApi(apiClient).getIndustryJobs(owner.getAccessKey(), owner.getAccessCred(), at, contid, maxResults, false,
						null, null, null, null, null, null, null, null, null, null, null, null, null, null, industryJobsFilter(), null, null, null, null, null, null, null);
			case MONTHS:
				return getCommonApi(apiClient).getIndustryJobs(owner.getAccessKey(), owner.getAccessCred(), at, contid, maxResults, false,
						null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, dateFilter(Settings.get().getEveKitIndustryJobsHistory()), null, null);
			default: //ALL
				return getCommonApi(apiClient).getIndustryJobs(owner.getAccessKey(), owner.getAccessCred(), at, contid, maxResults, false,
						null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		}
	}

	@Override
	public long getCID(IndustryJob obj) {
		return obj.getCid();
	}

	@Override
	public Long getLifeStart(IndustryJob obj) {
		return obj.getLifeStart();
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.INDUSTRY_JOBS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(Date date) {
		if (run == Runs.MONTHS || run == Runs.ALL) { //Ignore first update...
			owner.setIndustryJobsNextUpdate(date);
		}
	}

	@Override
	public void saveCID(Long cid) {
		if (run == Runs.MONTHS || run == Runs.ALL) { //Ignore first update...
			owner.setIndustryJobsCID(cid);
		}
	}

	@Override
	public Long loadCID() {
		if (run == Runs.MONTHS || run == Runs.ALL) { //Ignore first update...
			return owner.getIndustryJobsCID();
		} else {
			return null;
		}
	}
}
