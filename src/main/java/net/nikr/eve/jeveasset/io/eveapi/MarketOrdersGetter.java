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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.MarketOrder;
import com.beimin.eveapi.response.shared.MarketOrdersResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccount;
import net.nikr.eve.jeveasset.data.eveapi.EveApiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class MarketOrdersGetter extends AbstractApiGetter<MarketOrdersResponse> {

	private boolean saveHistory;
	private final Map<Long, Set<Long>> updatedByOwner = new HashMap<Long, Set<Long>>();
	private Long orderID;
	private boolean ingoreNextUpdate;
	
	public MarketOrdersGetter() {
		super("Market Orders", true, false);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<EveApiAccount> accounts, final boolean saveHistory) {
		this.saveHistory = saveHistory;
		updatedByOwner.clear();
		ingoreNextUpdate = false;
		orderID = null;
		//Default update - this will be enough if we don't save the history
		super.loadAccounts(updateTask, forceUpdate, accounts);
		
		//If we save history we need to update none-completed orders in the history
		if (saveHistory) { 
			//Ignore nextUpdate value
			ingoreNextUpdate = true;
			int size = 0;
			Map<EveApiOwner, Set<Long>> orderIDsByOwner = new HashMap<EveApiOwner, Set<Long>>();
			//Find orders that needs updating
			for (EveApiAccount account : accounts) {
				for (EveApiOwner owner : account.getOwners()) {
					Set<Long> updated = updatedByOwner.get(owner.getOwnerID());
					if (updated == null) {
						updated = new HashSet<Long>();
					}
					for (MyMarketOrder myMarketOrder : owner.getMarketOrders()) {
						if (updated.contains(myMarketOrder.getOrderID())) {
							continue; //Already updated
						}
						if (!myMarketOrder.isActive()) {
							continue; //Already completed
						}
						Set<Long> orderIDs = orderIDsByOwner.get(owner);
						if (orderIDs == null) {
							orderIDs = new HashSet<Long>();
							orderIDsByOwner.put(owner, orderIDs);
						}
						size++;
						orderIDs.add(myMarketOrder.getOrderID());
					}
				}
			}
			int count = 0;
			//Update the needed orders
			for (Map.Entry<EveApiOwner, Set<Long>> entry : orderIDsByOwner.entrySet()) {
				for (long id : entry.getValue()) {
					//Set orderID to update
					orderID = id;
					//Set task name
					this.setTaskName("Market Orders ("+id+")");
					//Update from the API
					super.loadOwner(updateTask, forceUpdate, entry.getKey());
					count++;
					if (updateTask != null) {
						updateTask.setTaskProgress(size, count, 0, 100);
					}
				}
			}
		}
	}

	@Override
	protected MarketOrdersResponse getResponse(final boolean bCorp) throws ApiException {
		if (orderID == null) {
			if (bCorp) {
				return new com.beimin.eveapi.parser.corporation.MarketOrdersParser()
						.getResponse(EveApiOwner.getApiAuthorization(getOwner()));
			} else {
				return new com.beimin.eveapi.parser.pilot.MarketOrdersParser()
						.getResponse(EveApiOwner.getApiAuthorization(getOwner()));
			}
		} else {
			if (bCorp) {
				return new com.beimin.eveapi.parser.corporation.MarketOrdersParser()
						.getResponse(EveApiOwner.getApiAuthorization(getOwner()), orderID);
			} else {
				return new com.beimin.eveapi.parser.pilot.MarketOrdersParser()
						.getResponse(EveApiOwner.getApiAuthorization(getOwner()), orderID);
			}
		}
	}

	@Override
	protected Date getNextUpdate() {
		if (ingoreNextUpdate) {
			return new Date();
		} else {
			return getOwner().getMarketOrdersNextUpdate();
		}
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getOwner().setMarketOrdersNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(final MarketOrdersResponse response) {
		List<MyMarketOrder> marketOrders = ApiConverter.convertMarketOrders(new ArrayList<MarketOrder>(response.getAll()), getOwner());
		if (saveHistory) {
			Set<MyMarketOrder> marketOrdersUnique = new HashSet<MyMarketOrder>();
			marketOrdersUnique.addAll(marketOrders); //Add new
			marketOrdersUnique.addAll(getOwner().getMarketOrders()); //Add old (Keep new, if equal to old)
			getOwner().setMarketOrders(new ArrayList<MyMarketOrder>(marketOrdersUnique));
			//Save updated market orders
			Set<Long> updated = updatedByOwner.get(getOwner().getOwnerID());
			if (updated == null) {
				updated = new HashSet<Long>();
				updatedByOwner.put(getOwner().getOwnerID(), updated);
			}	
			for (MyMarketOrder marketOrder : marketOrders) {
				updated.add(marketOrder.getOrderID());
			}
		} else {
			getOwner().setMarketOrders(marketOrders);
		}
	}

	@Override
	protected void updateFailed(final EveApiOwner ownerFrom, final EveApiOwner ownerTo) {
		ownerTo.setMarketOrders(ownerFrom.getMarketOrders());
		ownerTo.setMarketOrdersNextUpdate(ownerFrom.getMarketOrdersNextUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		return EveApiAccessMask.MARKET_ORDERS.getAccessMask();
	}
}
