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


public class EveKitLocationsGetter extends AbstractEveKitListGetter<Location> {

	private final Map<Long, String> eveNames = new HashMap<Long, String>();
	private final Map<EveKitOwner, Map<Long, String>> itemMap = new HashMap<EveKitOwner, Map<Long, String>>();

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		eveNames.clear();
		itemMap.clear();
		super.load(updateTask, owners);
		Settings.get().setEveNames(eveNames);
	}

	@Override
	protected List<Location> get(EveKitOwner owner, String at, Long contid) throws ApiException {
		//Get all items matching itemID
		return getCommonApi().getLocations(owner.getAccessKey(), owner.getAccessCred(), null, contid, getMaxResults(), getReverse(),
				valuesFilter(getIDs(owner)), null, null, null, null);
	}

	@Override
	protected void set(EveKitOwner owner, List<Location> data) throws ApiException {
		Map<Long, String> locations = EveKitConverter.convertLocations(data);
		for (Map.Entry<Long, String> entry : locations.entrySet()) {
			Long itemID = entry.getKey();
			String eveName = entry.getValue();
			String typeName = itemMap.get(owner).get(itemID);
			if (!eveName.equals(typeName)) {
				eveNames.put(itemID, eveName);
			}
		}
	}

	private Set<Long> getIDs(EveKitOwner owner) throws ApiException {
		Map<Long, String> map = itemMap.get(owner);
		if (map == null) {
			map = new HashMap<Long, String>();
			itemMap.put(owner, map);
			getItemID(map, owner.getAssets());
		}
		return map.keySet();
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
	protected Long getLifeStart(Location obj) {
		return obj.getLifeStart();
	}

	@Override
	protected String getTaskName() {
		return "Locations";
	}

	@Override
	protected int getProgressStart() {
		return 50;
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
	protected Date getNextUpdate(EveKitOwner owner) {
		return owner.getLocationsNextUpdate();
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

	@Override
	protected long getCID(Location obj) {
		return obj.getCid();
	}

	@Override
	protected void saveCID(EveKitOwner owner, Long cid) { } //Always get all data

	@Override
	protected Long loadCID(EveKitOwner owner) {
		return null; //Always get all data
	}

}
