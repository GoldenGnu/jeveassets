/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CharacterStandingsResponse;
import net.troja.eve.esi.model.CorporationStandingsResponse;


public class EsiNpcStandingGetter extends AbstractEsiGetter {

	public EsiNpcStandingGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getNpcStandingNextUpdate(), TaskType.NPC_STANDING);
	}

	@Override
	protected void update() throws ApiException {
		if (owner.isCorporation()) {
			List<CorporationStandingsResponse> response = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationStandingsResponse>() {
				@Override
				public ApiResponse<List<CorporationStandingsResponse>> get(Integer page) throws ApiException {
					return getCorporationApiAuth().getCorporationsCorporationIdStandingsWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			owner.setNpcStanding(EsiConverter.toNpcStandingCorporation(response, owner));
		} else {
			List<CharacterStandingsResponse> response = update(DEFAULT_RETRIES, new EsiHandler<List<CharacterStandingsResponse>>() {
				@Override
				public ApiResponse<List<CharacterStandingsResponse>> get() throws ApiException {
					return getCharacterApiAuth().getCharactersCharacterIdStandingsWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, null);
				}
			});
			owner.setNpcStanding(EsiConverter.toNpcStanding(response, owner));
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setNpcStandingNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return owner.isNpcStanding();
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		return null;
	}

}
