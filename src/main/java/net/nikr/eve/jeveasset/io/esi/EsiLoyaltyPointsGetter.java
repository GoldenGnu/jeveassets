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
import net.troja.eve.esi.model.CharacterLoyaltyPointsResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;


public class EsiLoyaltyPointsGetter extends AbstractEsiGetter {

	public EsiLoyaltyPointsGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getLoyaltyPointsNextUpdate(), TaskType.LOYALTY_POINTS);
	}

	@Override
	protected void update() throws ApiException {
		List<CharacterLoyaltyPointsResponse> response = update(DEFAULT_RETRIES, new EsiHandler<List<CharacterLoyaltyPointsResponse>>() {
			@Override
			public ApiResponse<List<CharacterLoyaltyPointsResponse>> get() throws ApiException {
				return getLoyaltyApiAuth().getCharactersCharacterIdLoyaltyPointsWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, null);
			}
		});
		owner.setLoyaltyPoints(EsiConverter.toLoyaltyPoints(response, owner));
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setLoyaltyPointsNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return owner.isLoyaltyPoints();
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		return null;
	}

}
