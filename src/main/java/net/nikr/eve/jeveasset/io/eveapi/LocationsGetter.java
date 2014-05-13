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
import com.beimin.eveapi.model.shared.Location;
import com.beimin.eveapi.response.shared.LocationsResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.MyAccount;
import net.nikr.eve.jeveasset.data.MyAccount.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;


public class LocationsGetter extends AbstractApiGetter<LocationsResponse> {

	private final int MAX_SIZE = 50;
	private Map<Long, String> itemMap = new HashMap<Long, String>();
	private Map<Long, String> eveNames = new HashMap<Long, String>();
	private List<Long> itemIDs = new ArrayList<Long>();
	private Date nextUpdate;

	public LocationsGetter() {
		super("Locations", true, false);
	}

	public void load(UpdateTask updateTask, boolean forceUpdate, List<MyAccount> accounts) {
		eveNames = new HashMap<Long, String>();
		super.loadAccounts(updateTask, forceUpdate, accounts);
		if (!hasError()) {
			Settings.get().setEveNames(eveNames);
		}
	}

	@Override
	protected int getProgressStart() {
		return 80;
	}

	private List<Long> getItems() throws ApiException {
		return itemIDs;
	}

	private void getItemID(Map<Long, String> itemIDs, List<MyAsset> assets) {
		for (MyAsset asset : assets) {
			if ((asset.getItem().getGroup().equals("Audit Log Secure Container")
					|| asset.getItem().getCategory().equals("Ship"))
					&& asset.isSingleton()) {
				itemIDs.put(asset.getItemID(), asset.getItem().getTypeName());
			}
			getItemID(itemIDs, asset.getAssets());
		}
	}

	@Override
	protected boolean load(Date nextUpdate, boolean updateCorporation, String updateName) {
		nextUpdate = getOwner().getLocationsNextUpdate();
		itemMap = new HashMap<Long, String>();
		getItemID(itemMap, getOwner().getAssets());
		List<Long> keys = new ArrayList<Long>(itemMap.keySet());
		for (int start = 0; start < keys.size(); start = Math.min(start + MAX_SIZE, keys.size())) {
			int end = Math.min(start + MAX_SIZE, keys.size());
			itemIDs = keys.subList(start, end);
			super.load(nextUpdate, updateCorporation, updateName + " (Request " + ((long)Math.ceil(start / MAX_SIZE) + 1) + " of " + ((long)Math.ceil((double)keys.size() / (double)MAX_SIZE)) + ")");
		}
		return true;
	}

	

	@Override
	protected LocationsResponse getResponse(boolean bCorp) throws ApiException {
		if (bCorp) {
			return new com.beimin.eveapi.parser.corporation.LocationsParser()
					.getResponse(Owner.getApiAuthorization(getOwner()), getItems());
		} else {
			return new com.beimin.eveapi.parser.pilot.LocationsParser()
					.getResponse(Owner.getApiAuthorization(getOwner()), getItems());
		}
	}

	@Override
	protected Date getNextUpdate() {
		return nextUpdate;
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		getOwner().setLocationsNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(LocationsResponse response) {
		Set<Location> all = response.getAll();
		for (Location apiLocation : all) {
			final long itemID = apiLocation.getItemID();
			final String eveName = apiLocation.getItemName();
			final String typeName = itemMap.get(itemID);
			if (!eveName.equals(typeName)) {
				eveNames.put(itemID, eveName);
			}
		}
	}

	@Override
	protected void updateFailed(Owner ownerFrom, Owner ownerTo) {
		
	}

	@Override
	protected long requestMask(boolean bCorp) {
		if (bCorp) {
			return AccessMask.LOCATIONS_CORP.getAccessMask();
		} else {
			return AccessMask.LOCATIONS_CHAR.getAccessMask();
		}
	}
}
