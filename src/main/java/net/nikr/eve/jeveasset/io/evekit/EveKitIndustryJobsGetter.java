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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitIndustryJobsGetter extends AbstractEveKitGetter {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected void get(final EveKitOwner owner) throws ApiException {
	  // Industry jobs can change state, but are never removed.  So a call to getIndustryJobs here will
	  // return every industry job ever stored in EveKit because they will all be live at the current
	  // time.  To avoid that, we filter on the "startDate" attribute to only get recent orders.  We
	  // could do better by only querying from the oldest active industry job but we'd need to pass that
	  // date in.
	  //
	  // I don't actually know how long an industry job is allowed to take or be paused, so I made up
	  // a threshold of one year.
	  final long threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(360);
	  // Page to make sure we get all desired results
		List<IndustryJob> industryJobs = retrievePagedResults(new BatchRetriever<IndustryJob>() {

      @Override
      public List<IndustryJob> getNextBatch(
                                            long contid)
        throws ApiException {
        return getCommonApi().getIndustryJobs(owner.getAccessKey(), owner.getAccessCred(), null, contid, Integer.MAX_VALUE, null,
                                              null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
                                              null, null, null, null, null, null, null, null, ek_range(threshold, Long.MAX_VALUE), 
                                              null, null, null, null, null);
      }

      @Override
      public long getCid(
                         IndustryJob obj) {
        return obj.getCid();
      }
		  
		}); 
		owner.setIndustryJobs(EveKitConverter.convertIndustryJobs(industryJobs, owner));
	}

	@Override
	protected String getTaskName() {
		return "Industry Jobs";
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

}
