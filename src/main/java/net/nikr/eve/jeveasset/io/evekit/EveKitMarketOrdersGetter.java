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

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
	protected void get(final EveKitOwner owner) throws ApiException {
	  // Market orders change state, but are never removed.  So a call to getMarketOrders here will return every
	  // market order ever stored in EveKit because they will all be live at the current time.  To avoid that, we
	  // filter on the "issued" attribute to only get recent orders.  We could do better by only querying from
	  // the oldest active order but we'd need to pass that date in.
	  //
	  // We know that market orders can't be live more than 90 days (but issued date moves if the order is changed),
	  // so we double the max order duration to set a query threshold.
	  final long threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(180);
	  // Page to make sure we get all desired results
    List<MarketOrder> marketOrders = retrievePagedResults(new BatchRetriever<MarketOrder>() {

      @Override
      public List<MarketOrder> getNextBatch(
                                            long contid)
        throws ApiException {
        return getCommonApi().getMarketOrders(owner.getAccessKey(), owner.getAccessCred(), null, contid, Integer.MAX_VALUE, null, 
                                              null, null, null, null, null, null, ek_range(threshold, Long.MAX_VALUE), null, null, 
                                              null, null, null, null, null, null);
      }

      @Override
      public long getCid(
                         MarketOrder obj) {
        return obj.getCid();
      }
      
    });
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
