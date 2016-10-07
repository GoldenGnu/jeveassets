/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.evekit;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.MarketOrder;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitMarketOrdersGetter extends AbstractEveKitGetter  {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected void get(EveKitOwner owner) throws ApiException {
    List<MarketOrder> marketOrders = new ArrayList<>();
    // All EveKit results are ordered by the "cached id" (cid field).  Page through setting continuation ID to the last cid until we no longer receive results.
    List<MarketOrder> nextOrders = getCommonApi().getMarketOrders(owner.getAccessKey(), owner.getAccessCred(), null, null, Integer.MAX_VALUE, null, null, null,
                                                                  null, null, null, null, null, null, null, null, null, null, null, null, null);
    while (!nextOrders.isEmpty()) {
      marketOrders.addAll(nextOrders);
      nextOrders = getCommonApi().getMarketOrders(owner.getAccessKey(), owner.getAccessCred(), null, nextOrders.get(nextOrders.size() - 1).getCid(),
                                                  Integer.MAX_VALUE, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                                                  null);
    }
    owner.setMarketOrders(EveKitConverter.convertMarketOrders(marketOrders, owner));
	}

	@Override
	protected String getTaskName() {
		return "Market Orders";
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.MARKET_ORDERS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		owner.setMarketOrdersNextUpdate(date);
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

}
