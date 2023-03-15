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
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;


public class EsiMarketOrdersGetter extends AbstractEsiGetter {

	private final boolean saveHistory;

	public EsiMarketOrdersGetter(UpdateTask updateTask, EsiOwner owner, boolean saveHistory) {
		super(updateTask, owner, false, owner.getMarketOrdersNextUpdate(), TaskType.MARKET_ORDERS);
		this.saveHistory = saveHistory;
	}

	@Override
	protected void update() throws ApiException {
		if (owner.isCorporation()) {
			List<CorporationOrdersResponse> marketOrders = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationOrdersResponse>() {
				@Override
				public ApiResponse<List<CorporationOrdersResponse>> get(Integer page) throws ApiException {
					return getMarketApiAuth().getCorporationsCorporationIdOrdersWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			List<CorporationOrdersHistoryResponse> marketOrdersHistory = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationOrdersHistoryResponse>() {
				@Override
				public ApiResponse<List<CorporationOrdersHistoryResponse>> get(Integer page) throws ApiException {
					return getMarketApiAuth().getCorporationsCorporationIdOrdersHistoryWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			owner.setMarketOrders(EsiConverter.toMarketOrdersCorporation(marketOrders, marketOrdersHistory, owner, saveHistory));
		} else {
			List<CharacterOrdersResponse> marketOrders = update(DEFAULT_RETRIES, new EsiHandler<List<CharacterOrdersResponse>>() {
				@Override
				public ApiResponse<List<CharacterOrdersResponse>> get() throws ApiException {
					return getMarketApiAuth().getCharactersCharacterIdOrdersWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, null);
				}
			});
			List<CharacterOrdersHistoryResponse> marketOrdersHistory = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CharacterOrdersHistoryResponse>() {
				@Override
				public ApiResponse<List<CharacterOrdersHistoryResponse>> get(Integer page) throws ApiException {
					return getMarketApiAuth().getCharactersCharacterIdOrdersHistoryWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			owner.setMarketOrders(EsiConverter.toMarketOrders(marketOrders, marketOrdersHistory, owner, saveHistory));
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setMarketOrdersNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return owner.isMarketOrders();
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		RolesEnum[] roles = {RolesEnum.DIRECTOR, RolesEnum.ACCOUNTANT, RolesEnum.TRADER};
		return roles;
	}

}
