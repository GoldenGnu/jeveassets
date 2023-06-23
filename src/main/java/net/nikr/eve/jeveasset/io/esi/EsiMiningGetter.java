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
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DEFAULT_RETRIES;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterMiningResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CorporationMiningExtractionsResponse;
import net.troja.eve.esi.model.CorporationMiningObserverResponse;
import net.troja.eve.esi.model.CorporationMiningObserversResponse;
import net.troja.eve.esi.model.MoonResponse;

public class EsiMiningGetter extends AbstractEsiGetter {

	private final boolean saveHistory;

	public EsiMiningGetter(UpdateTask updateTask, EsiOwner owner, boolean saveHistory) {
		super(updateTask, owner, false, owner.getMiningNextUpdate(), TaskType.MINING);
		this.saveHistory = saveHistory;
	}

	@Override
	protected void update() throws ApiException {
		if (owner.isCorporation()) {
			//Moon Extractions
			List<CorporationMiningExtractionsResponse> extractions = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationMiningExtractionsResponse>() {
				@Override
				public ApiResponse<List<CorporationMiningExtractionsResponse>> get(Integer page) throws ApiException {
					return getIndustryApiAuth().getCorporationCorporationIdMiningExtractionsWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			//Moon Locations
			Set<Integer> moonIDs = new HashSet<>();
			for (CorporationMiningExtractionsResponse response : extractions) { //For each planet
				Integer planetID = response.getMoonId();
				MyLocation location = ApiIdConverter.getLocation(planetID);
				if (location.isEmpty()) {
					moonIDs.add(planetID);
				}
			}
			Map<Integer, MoonResponse> locationResponses = updateList(moonIDs, DEFAULT_RETRIES, new ListHandler<Integer, MoonResponse>() {
				@Override
				protected ApiResponse<MoonResponse> get(Integer planetID) throws ApiException {
					return getUniverseApiOpen().getUniverseMoonsMoonIdWithHttpInfo(planetID, DATASOURCE, null);
				}
			});
			List<Citadel> citadels = new ArrayList<>();
			for (MoonResponse moon : locationResponses.values()) {
				Citadel citadel = ApiIdConverter.getCitadel(moon);
				if (citadel != null) {
					citadels.add(citadel);
				}
			}
			CitadelGetter.set(citadels);
			owner.setExtractions(EsiConverter.toExtraction(extractions, owner, saveHistory)); //Must be after the moon location update
			//Mining Ledger
			List<CorporationMiningObserversResponse> observers = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationMiningObserversResponse>() {
				@Override
				public ApiResponse<List<CorporationMiningObserversResponse>> get(Integer page) throws ApiException {
					return getIndustryApiAuth().getCorporationCorporationIdMiningObserversWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			Map<CorporationMiningObserversResponse, List<CorporationMiningObserverResponse>> miningObservers = updatePagedMap(observers, new PagedListHandler<CorporationMiningObserversResponse, CorporationMiningObserverResponse>() {
				@Override
				protected List<CorporationMiningObserverResponse> get(CorporationMiningObserversResponse observer) throws ApiException {
					return updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationMiningObserverResponse>() {
						@Override
						public ApiResponse<List<CorporationMiningObserverResponse>> get(Integer page) throws ApiException {
							return getIndustryApiAuth().getCorporationCorporationIdMiningObserversObserverIdWithHttpInfo((int) owner.getOwnerID(), observer.getObserverId(), DATASOURCE, null, page, null);
						}
					});
				}
			});
			owner.setMining(EsiConverter.toMining(miningObservers, owner, true));
		} else {
			List<CharacterMiningResponse> responses = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CharacterMiningResponse>() {
				@Override
				public ApiResponse<List<CharacterMiningResponse>> get(Integer page) throws ApiException {
					return getIndustryApiAuth().getCharactersCharacterIdMiningWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			owner.setMining(EsiConverter.toMining(responses, owner, saveHistory));
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setMiningNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return owner.isMining();
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		RolesEnum[] roles = {RolesEnum.DIRECTOR, RolesEnum.ACCOUNTANT, RolesEnum.STATION_MANAGER};
		return roles;

	}

}
