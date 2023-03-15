/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CorporationIndustryJobsResponse;


public class EsiIndustryJobsGetter extends AbstractEsiGetter {

	public EsiIndustryJobsGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getIndustryJobsNextUpdate(), TaskType.INDUSTRY_JOBS);
	}

	@Override
	protected void update() throws ApiException {
		if (owner.isCorporation()) {
			List<CorporationIndustryJobsResponse> industryJobs = new ArrayList<>();
			//Completed
			List<CorporationIndustryJobsResponse> completed = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationIndustryJobsResponse>() {
				@Override
				public ApiResponse<List<CorporationIndustryJobsResponse>> get(Integer page) throws ApiException {
					return getIndustryApiAuth().getCorporationsCorporationIdIndustryJobsWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, true, page, null);
				}
			});
			industryJobs.addAll(completed);
			//Not Completed
			List<CorporationIndustryJobsResponse> incomplated = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationIndustryJobsResponse>() {
				@Override
				public ApiResponse<List<CorporationIndustryJobsResponse>> get(Integer page) throws ApiException {
					return getIndustryApiAuth().getCorporationsCorporationIdIndustryJobsWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, false, page, null);
				}
			});
			industryJobs.addAll(incomplated);
			owner.setIndustryJobs(EsiConverter.toIndustryJobsCorporation(industryJobs, owner));
		} else {
			Set<Boolean> completed = new HashSet<>();
			completed.add(true);
			completed.add(false);
			Map<Boolean, List<CharacterIndustryJobsResponse>> updateList = updateList(completed, DEFAULT_RETRIES, new ListHandler<Boolean, List<CharacterIndustryJobsResponse>>() {
				@Override
				protected ApiResponse<List<CharacterIndustryJobsResponse>> get(Boolean k) throws ApiException {
					return getIndustryApiAuth().getCharactersCharacterIdIndustryJobsWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, k, null);
				}
			});
			List<CharacterIndustryJobsResponse> industryJobs = new ArrayList<>();
			for (List<CharacterIndustryJobsResponse> list : updateList.values()) {
				industryJobs.addAll(list);
			}
			owner.setIndustryJobs(EsiConverter.toIndustryJobs(industryJobs, owner));
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setIndustryJobsNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return owner.isIndustryJobs();
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		RolesEnum[] roles = {RolesEnum.DIRECTOR, RolesEnum.FACTORY_MANAGER};
		return roles;
	}

}
