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


import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.MarketOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;


public class EveKitMarketOrdersGetter extends AbstractEveKitListGetter<MarketOrder>  {

	private enum Runs { MONTHS, ALL }

	private Runs run;
	
	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		run = Runs.MONTHS;
		super.load(updateTask, owners);
	}

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners, boolean first) {
		run = Runs.ALL;
		super.load(updateTask, owners, first);
	}

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners, Long at) {
		run = Runs.ALL;
		super.load(updateTask, owners, at);
	}

	@Override
	protected List<MarketOrder> get(EveKitOwner owner, String at, Long contid) throws ApiException {
		if (run == Runs.MONTHS) { //months
			return getCommonApi().getMarketOrders(owner.getAccessKey(), owner.getAccessCred(), at, contid, getMaxResults(), getReverse(),
					null, null, null, null, null, null, dateFilter(Settings.get().getEveKitMarketOrdersHistory()), null, null, null, null, null, null, null, null);
		}
		if (run == Runs.ALL) {
			return getCommonApi().getMarketOrders(owner.getAccessKey(), owner.getAccessCred(), at, contid, getMaxResults(), getReverse(),
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		}
		return new ArrayList<MarketOrder>();
	}

	@Override
	protected void set(EveKitOwner owner, List<MarketOrder> data) throws ApiException {
		Set<MyMarketOrder> set = new HashSet<MyMarketOrder>();
		if (loadCID(owner) != null) { //Old
			set.addAll(owner.getMarketOrders());
		}
		set.addAll(EveKitConverter.convertMarketOrders(data, owner)); //New
		owner.setMarketOrders(new ArrayList<MyMarketOrder>(set)); //New
	}

	@Override
	protected long getCID(MarketOrder obj) {
		return obj.getCid();
	}

	@Override
	protected Long getLifeStart(MarketOrder obj) {
		return obj.getLifeStart();
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
	protected Date getNextUpdate(EveKitOwner owner) {
		return owner.getMarketOrdersNextUpdate();
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

	@Override
	protected void saveCID(EveKitOwner owner, Long contid) {
		owner.setMarketOrdersCID(contid);
	}

	@Override
	protected Long loadCID(EveKitOwner owner) {
		return owner.getMarketOrdersCID();
	}
}
