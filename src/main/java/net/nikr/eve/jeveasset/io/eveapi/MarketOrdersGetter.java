/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import com.beimin.eveapi.shared.marketorders.MarketOrdersResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Account.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrder;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class MarketOrdersGetter extends AbstractApiGetter<MarketOrdersResponse> {

	public MarketOrdersGetter() {
		super("Market Orders", true, false);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<Account> accounts) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected MarketOrdersResponse getResponse(final boolean bCorp) throws ApiException {
		if (bCorp) {
			return com.beimin.eveapi.corporation
					.marketorders.MarketOrdersParser.getInstance()
					.getResponse(Owner.getApiAuthorization(getOwner()));
		} else {
			return com.beimin.eveapi.character
					.marketorders.MarketOrdersParser.getInstance()
					.getResponse(Owner.getApiAuthorization(getOwner()));
		}
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getMarketOrdersNextUpdate();
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getOwner().setMarketOrdersNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(final MarketOrdersResponse response) {
		List<MarketOrder> marketOrders = ApiConverter.convertMarketOrders(new ArrayList<ApiMarketOrder>(response.getAll()), getOwner());
		getOwner().setMarketOrders(marketOrders);
	}

	@Override
	protected void updateFailed(final Owner ownerFrom, final Owner ownerTo) {
		ownerTo.setMarketOrders(ownerFrom.getMarketOrders());
		ownerTo.setMarketOrdersNextUpdate(ownerFrom.getMarketOrdersNextUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		return AccessMask.MARKET_ORDERS.getAccessMask();
	}
}
