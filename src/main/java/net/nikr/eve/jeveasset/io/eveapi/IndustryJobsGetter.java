/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.industryjobs.IndustryJobsResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Account.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class IndustryJobsGetter extends AbstractApiGetter<IndustryJobsResponse> {

	public IndustryJobsGetter() {
		super("Industry Jobs", true, false);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<Account> accounts) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected IndustryJobsResponse getResponse(final boolean bCorp) throws ApiException {
		if (bCorp) {
			return com.beimin.eveapi.corporation
					.industryjobs.IndustryJobsParser.getInstance()
					.getResponse(Owner.getApiAuthorization(getOwner()));
		} else {
			return com.beimin.eveapi.character
					.industryjobs.IndustryJobsParser.getInstance()
					.getResponse(Owner.getApiAuthorization(getOwner()));
			}
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getIndustryJobsNextUpdate();
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getOwner().setIndustryJobsNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(final IndustryJobsResponse response) {
		List<IndustryJob> industryJobs = ApiConverter.convertIndustryJobs(new ArrayList<ApiIndustryJob>(response.getAll()), getOwner());
		getOwner().setIndustryJobs(industryJobs);
	}

	@Override
	protected void updateFailed(final Owner ownerFrom, final Owner ownerTo) {
		ownerTo.setIndustryJobs(ownerFrom.getIndustryJobs());
		ownerTo.setIndustryJobsNextUpdate(ownerFrom.getIndustryJobsNextUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		return AccessMask.INDUSTRY_JOBS.getAccessMask();
	}
}
