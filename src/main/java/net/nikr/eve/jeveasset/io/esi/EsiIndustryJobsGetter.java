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
package net.nikr.eve.jeveasset.io.esi;

import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.esi.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;


public class EsiIndustryJobsGetter extends AbstractEsiGetter {

	@Override
	public void load(UpdateTask updateTask, List<EsiOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected ApiClient get(EsiOwner owner) throws ApiException {
		List<CharacterIndustryJobsResponse> industryJobs = getIndustryApiAuth().getCharactersCharacterIdIndustryJobs((int)owner.getOwnerID(), DATASOURCE, true, null, null, null);
		owner.setIndustryJobs(EsiConverter.convertIndustryJobs(owner, industryJobs));
		return getIndustryApiAuth().getApiClient();
	}

	@Override
	protected String getTaskName() {
		return "Industry Jobs";
	}

	@Override
	protected void setNextUpdate(EsiOwner owner, Date date) {
		owner.setIndustryJobsNextUpdate(date);
	}

	@Override
	protected Date getNextUpdate(EsiOwner owner) {
		return owner.getIndustryJobsNextUpdate();
	}

	@Override
	protected boolean inScope(EsiOwner owner) {
		return owner.isIndustryJobs();
	}
	
}
