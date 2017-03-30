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
import enterprises.orbital.evekit.client.model.Location;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;


public class EveKitLocationsGetter extends AbstractEveKitIdGetter<Location> {

	private final Map<Long, String> eveNames = new HashMap<Long, String>();
	private final Map<Long, String> itemMap = new HashMap<Long, String>();

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		eveNames.clear();
		super.load(updateTask, owners);
		Settings.get().setEveNames(eveNames);
	}

	@Override
	protected List<Location> get(EveKitOwner owner, long id) throws ApiException {
		//Get all items matching itemID
		return getCommonApi().getLocations(owner.getAccessKey(), owner.getAccessCred(), null, null, MAX_RESULTS, REVERSE,
				valuesFilter(id), null, null, null, null);
	}

	@Override
	protected void set(EveKitOwner owner, List<Location> data) throws ApiException {
		Map<Long, String> locations = EveKitConverter.convertLocations(data);
		for (Map.Entry<Long, String> entry : locations.entrySet()) {
			Long itemID = entry.getKey();
			String eveName = entry.getValue();
			String typeName = itemMap.get(itemID);
			if (!eveName.equals(typeName)) {
				eveNames.put(itemID, eveName);
			}
		}
	}

	@Override
	protected Set<Long> getIDs(EveKitOwner owner) throws ApiException {
		itemMap.clear();
		getItemID(itemMap, owner.getAssets());
		return itemMap.keySet();
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
	protected boolean isNow(Location obj) {
		return obj.getLifeEnd() == Long.MAX_VALUE;
	}

	@Override
	protected String getTaskName() {
		return "Locations";
	}

	@Override
	protected int getProgressStart() {
		return 80;
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.LOCATIONS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		owner.setLocationsNextUpdate(date);
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

}
