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
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CorporationDivisionsResponse;


public class EsiDivisionsGetter extends AbstractEsiGetter {

	public EsiDivisionsGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getAssetNextUpdate(), TaskType.DIVISIONS);
	}

	@Override
	protected void update() throws ApiException {
		if (!owner.isCorporation()) {
			return; //Character accounts can not get divisions
		}
		CorporationDivisionsResponse response = update(DEFAULT_RETRIES, new EsiHandler<CorporationDivisionsResponse>() {
			@Override
			public ApiResponse<CorporationDivisionsResponse> get() throws ApiException {
				return getCorporationApiAuth().getCorporationsCorporationIdDivisionsWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, null);
			}
		});
		owner.setAssetDivisions(EsiConverter.toAssetDivisions(response.getHangar()));
		owner.setWalletDivisions(EsiConverter.toWalletDivisions(response.getWallet()));
	}

	@Override
	protected boolean haveAccess() {
		if (owner.isCorporation()) {
			return owner.isDivisions();
		} else {
			return true; //Overwrite the default, so, we don't get errors
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		//Nope
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		RolesEnum[] roles = {RolesEnum.DIRECTOR};
		return roles;
	}

}
