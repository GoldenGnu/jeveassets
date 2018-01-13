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
package net.nikr.eve.jeveasset.io.evekit;

import enterprises.orbital.evekit.client.ApiClient;
import enterprises.orbital.evekit.client.ApiException;
import enterprises.orbital.evekit.client.model.Location;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;

public class EveKitLocationsGetter extends AbstractEveKitGetter implements EveKitPagesHandler<Location> {

	private final Map<Long, String> itemMap = new HashMap<Long, String>();

	public EveKitLocationsGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, false, owner.getLocationsNextUpdate(), TaskType.LOCATIONS, false, null);
	}

	@Override
	protected void get(ApiClient apiClient, Long at, boolean first) throws ApiException {
		List<Location> data = updatePages(this);
		if (data == null) {
			return;
		}
		for (Location location : data) {
			Long itemID = location.getItemID();
			String eveName = location.getItemName();
			String typeName = itemMap.get(itemID);
			if (!eveName.equals(typeName)) {
				Settings.get().getEveNames().put(itemID, eveName);
			} else {
				Settings.get().getEveNames().remove(itemID);
			}
		}
	}

	@Override
	public List<Location> get(ApiClient apiClient, String at, Long contid, Integer maxResults) throws ApiException {
		//Get all items matching itemID
		return getCommonApi(apiClient).getLocations(owner.getAccessKey(), owner.getAccessCred(), at, contid, maxResults, false,
				valuesFilter(getIDs(itemMap, owner)), null, null, null, null);
	}

	@Override
	public Long getLifeStart(Location obj) {
		return obj.getLifeStart();
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.LOCATIONS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setLocationsNextUpdate(date);
	}

	@Override
	public long getCID(Location obj) {
		return obj.getCid();
	}

	@Override
	public void saveCID(Long cid) {	} //Always get all data

	@Override
	public Long loadCID() {
		return null; //Always get all data
	}

}
