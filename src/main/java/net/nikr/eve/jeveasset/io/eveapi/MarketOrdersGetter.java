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
package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.MarketOrder;
import com.beimin.eveapi.parser.character.CharMarketOrdersParser;
import com.beimin.eveapi.parser.corporation.CorpMarketOrdersParser;
import com.beimin.eveapi.response.shared.MarketOrdersResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class MarketOrdersGetter extends AbstractApiGetter<MarketOrdersResponse> {

	private final boolean saveHistory;

	public MarketOrdersGetter(UpdateTask updateTask, EveApiOwner owner,  boolean saveHistory) {
		super(updateTask, owner, false, owner.getMarketOrdersNextUpdate(), TaskType.MARKET_ORDERS);
		this.saveHistory = saveHistory;
	}

	@Override
	protected void get(String updaterStatus) throws ApiException {
		MarketOrdersResponse response;
		if (owner.isCorporation()) {
			response = new CorpMarketOrdersParser()
				.getResponse(EveApiOwner.getApiAuthorization(owner));
		} else {
			response = new CharMarketOrdersParser()
					.getResponse(EveApiOwner.getApiAuthorization(owner));
		}
		if (!handle(response, updaterStatus)) {
			return;
		}
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>(response.getAll());
		if (saveHistory) { //update old orders
			Set<Long> updated = new HashSet<Long>();
			for (MarketOrder marketOrder : marketOrders) {
				updated.add(marketOrder.getOrderID());
			}
			Set<Long> orderIDs = new HashSet<Long>();
			for (MyMarketOrder myMarketOrder : owner.getMarketOrders()) {
				if (updated.contains(myMarketOrder.getOrderID())) {
					continue; //Already updated
				}
				if (!myMarketOrder.isActive()) {
					continue; //Already completed
				}
				orderIDs.add(myMarketOrder.getOrderID());
			}
			Map<Long, MarketOrdersResponse> updateList = updateList(orderIDs, NO_RETRIES, new ListHandler<Long, MarketOrdersResponse>() {
				@Override
				protected MarketOrdersResponse get(String updaterStatus, Long k) throws ApiException {
					if (owner.isCorporation()) {
						return new CorpMarketOrdersParser()
								.getResponse(EveApiOwner.getApiAuthorization(owner), k);
					} else {
						return new CharMarketOrdersParser()
								.getResponse(EveApiOwner.getApiAuthorization(owner), k);
					}
				}
			});
			for (MarketOrdersResponse ordersResponse : updateList.values()) {
				if (!handle(response, updaterStatus)) {
					continue;
				}
				marketOrders.addAll(ordersResponse.getAll());
			}
		}
		owner.setMarketOrders(EveApiConverter.toMarketOrders(marketOrders , owner, saveHistory));
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		owner.setMarketOrdersNextUpdate(nextUpdate);
	}

	@Override
	protected long requestMask() {
		return EveApiAccessMask.MARKET_ORDERS.getAccessMask();
	}
}
