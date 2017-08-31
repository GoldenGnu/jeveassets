/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
import com.beimin.eveapi.parser.character.CharMarketOrdersParser;
import com.beimin.eveapi.parser.corporation.CorpMarketOrdersParser;
import com.beimin.eveapi.response.shared.MarketOrdersResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


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
					this.setTaskName("Market Orders (" + id + ")");
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
				return new CorpMarketOrdersParser()
						.getResponse(EveApiOwner.getApiAuthorization(getOwner()));
			} else {
				return new CharMarketOrdersParser()
						.getResponse(EveApiOwner.getApiAuthorization(getOwner()));
			}
		} else {
			if (bCorp) {
				return new CorpMarketOrdersParser()
						.getResponse(EveApiOwner.getApiAuthorization(getOwner()), orderID);
			} else {
				return new CharMarketOrdersParser()
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
		getOwner().setMarketOrders(EveApiConverter.toMarketOrders(response.getAll(), getOwner(), saveHistory));
		if (saveHistory) {
			//Save updated market orders
			Set<Long> updated = updatedByOwner.get(getOwner().getOwnerID());
			if (updated == null) {
				updated = new HashSet<Long>();
				updatedByOwner.put(getOwner().getOwnerID(), updated);
			}
			for (MyMarketOrder marketOrder : getOwner().getMarketOrders()) {
				updated.add(marketOrder.getOrderID());
			}
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
