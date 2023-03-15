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
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CorporationWalletsResponse;


public class EsiAccountBalanceGetter extends AbstractEsiGetter {

	public EsiAccountBalanceGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getBalanceNextUpdate(), TaskType.ACCOUNT_BALANCE);
	}

	@Override
	protected void update() throws ApiException {
		if (owner.isCorporation()) {
			List<CorporationWalletsResponse> response = update(DEFAULT_RETRIES, new EsiHandler<List<CorporationWalletsResponse>>() {
				@Override
				public ApiResponse<List<CorporationWalletsResponse>> get() throws ApiException {
					ApiResponse<List<CorporationWalletsResponse>> apiResponse = getWalletApiAuth().getCorporationsCorporationIdWalletsWithHttpInfo((int)owner.getOwnerID(), DATASOURCE, null, null);
					Date modified = getHeaderDate(apiResponse.getHeaders(), "last-modified");
					if (modified != null && (owner.getBalanceLastUpdate() == null || modified.after(owner.getBalanceLastUpdate()))) {
						owner.setBalanceLastUpdate(modified);
					}
					return apiResponse;
				}
			});
			owner.setAccountBalances(EsiConverter.toAccountBalanceCorporation(response, owner));
		} else {
			Double response = update(DEFAULT_RETRIES, new EsiHandler<Double>() {
				@Override
				public ApiResponse<Double> get() throws ApiException {
					ApiResponse<Double> apiResponse = getWalletApiAuth().getCharactersCharacterIdWalletWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, null);
					Date modified = getHeaderDate(apiResponse.getHeaders(), "last-modified");
					if (modified != null && (owner.getBalanceLastUpdate() == null || modified.after(owner.getBalanceLastUpdate()))) {
						owner.setBalanceLastUpdate(modified);
					}
					return apiResponse;
				}
			});
			owner.setAccountBalances(EsiConverter.toAccountBalance(response, owner, 1000));
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setBalanceNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return owner.isAccountBalance();
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		RolesEnum[] roles = {RolesEnum.DIRECTOR, RolesEnum.ACCOUNTANT, RolesEnum.JUNIOR_ACCOUNTANT};
		return roles;
	}

}
