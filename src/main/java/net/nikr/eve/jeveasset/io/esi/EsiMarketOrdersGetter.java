/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;


public class EsiMarketOrdersGetter extends AbstractEsiGetter {

	private final boolean saveHistory;

	public EsiMarketOrdersGetter(UpdateTask updateTask, EsiOwner owner, boolean saveHistory) {
		super(updateTask, owner, false, owner.getMarketOrdersNextUpdate(), TaskType.MARKET_ORDERS, owner.isCorporation() ? NO_RETRIES : DEFAULT_RETRIES);
		this.saveHistory = saveHistory;
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		if (owner.isCorporation()) {
			List<CorporationOrdersResponse> marketOrders = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationOrdersResponse>() {
				@Override
				public List<CorporationOrdersResponse> get(ApiClient apiClient, Integer page) throws ApiException {
					return getMarketApiAuth(apiClient).getCorporationsCorporationIdOrders((int) owner.getOwnerID(), DATASOURCE, page, null, USER_AGENT, null);
				}
			});
			List<CorporationOrdersHistoryResponse> marketOrdersHistory = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationOrdersHistoryResponse>() {
				@Override
				public List<CorporationOrdersHistoryResponse> get(ApiClient apiClient, Integer page) throws ApiException {
					return getMarketApiAuth(apiClient).getCorporationsCorporationIdOrdersHistory((int) owner.getOwnerID(), DATASOURCE, page, null, USER_AGENT, null);
				}
			});
			owner.setMarketOrders(EsiConverter.toMarketOrdersCorporation(marketOrders, marketOrdersHistory, owner, saveHistory));
		} else {
			List<CharacterOrdersResponse> marketOrders = getMarketApiAuth(apiClient).getCharactersCharacterIdOrders((int) owner.getOwnerID(), DATASOURCE, null, USER_AGENT, null);
			List<CharacterOrdersHistoryResponse> marketOrdersHistory = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CharacterOrdersHistoryResponse>() {
				@Override
				public List<CharacterOrdersHistoryResponse> get(ApiClient apiClient, Integer page) throws ApiException {
					return getMarketApiAuth(apiClient).getCharactersCharacterIdOrdersHistory((int) owner.getOwnerID(), DATASOURCE, page, null, USER_AGENT, null);
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
	protected boolean inScope() {
		return owner.isMarketOrders();
	}

}
