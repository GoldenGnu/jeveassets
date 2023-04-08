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

import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DEFAULT_RETRIES;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterMiningResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;

public class EsiMiningGetter extends AbstractEsiGetter {

	private final boolean saveHistory;

	public EsiMiningGetter(UpdateTask updateTask, EsiOwner owner, boolean saveHistory) {
		super(updateTask, owner, false, owner.getMiningNextUpdate(), TaskType.MINING);
		this.saveHistory = saveHistory;
	}

	@Override
	protected void update() throws ApiException {
		if (owner.isCorporation()) {
			//Not doing corporation mining ledger, yet!
			/*
			List<CorporationMiningExtractionsResponse> extractions = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationMiningExtractionsResponse>() {
				@Override
				public ApiResponse<List<CorporationMiningExtractionsResponse>> get(Integer page) throws ApiException {
					return getIndustryApiAuth().getCorporationCorporationIdMiningExtractionsWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			//owner.setAssets(EsiConverter.toAssetsCorporation(extractions, owner));
			List<CorporationMiningObserversResponse> observers = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationMiningObserversResponse>() {
				@Override
				public ApiResponse<List<CorporationMiningObserversResponse>> get(Integer page) throws ApiException {
					return getIndustryApiAuth().getCorporationCorporationIdMiningObserversWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			Set<Long> observerIDs = new HashSet<>();
			for (CorporationMiningObserversResponse observersResponse : observers) {
				observerIDs.add(observersResponse.getObserverId());
			}
			Map<Long, List<CorporationMiningObserverResponse>> miningObservers = updatePagedMap(observerIDs, new PagedListHandler<Long, CorporationMiningObserverResponse>() {
				@Override
				protected List<CorporationMiningObserverResponse> get(Long observerID) throws ApiException {
					return updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationMiningObserverResponse>() {
						@Override
						public ApiResponse<List<CorporationMiningObserverResponse>> get(Integer page) throws ApiException {
							return getIndustryApiAuth().getCorporationCorporationIdMiningObserversObserverIdWithHttpInfo((int) owner.getOwnerID(), observerID, DATASOURCE, null, page, null);
						}
					});
				}
			});

			List<CorporationMiningObserverResponse> miningObservers = updatePagedList(observerIDs, new PagedListHandler<Long, CorporationMiningObserverResponse>() {
				@Override
				protected List<CorporationMiningObserverResponse> get(Long observerID) throws ApiException {
					return updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationMiningObserverResponse>() {
						@Override
						public ApiResponse<List<CorporationMiningObserverResponse>> get(Integer page) throws ApiException {
							return getIndustryApiAuth().getCorporationCorporationIdMiningObserversObserverIdWithHttpInfo((int) owner.getOwnerID(), observerID, DATASOURCE, null, page, null);
						}
					});
				}
			});
			*/
			//owner.setMining(EsiConverter.toMiningCorporation(miningObservers, owner, true));
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
		return null;
		//Not doing corporation mining ledger, yet!
		/*
		RolesEnum[] roles = {RolesEnum.DIRECTOR, RolesEnum.ACCOUNTANT, RolesEnum.STATION_MANAGER};
		return roles;
		*/
	}

}
