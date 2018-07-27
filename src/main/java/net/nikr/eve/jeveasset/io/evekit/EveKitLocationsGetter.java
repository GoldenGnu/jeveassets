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
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;

public class EveKitLocationsGetter extends AbstractEveKitGetter implements EveKitPagesHandler<Location> {

	private Map<Long, MyAsset> iDs;

	public EveKitLocationsGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, false, owner.getLocationsNextUpdate(), TaskType.LOCATIONS, false, null);
	}

	@Override
	protected void get(ApiClient apiClient, Long at, boolean first) throws ApiException {
		List<Location> data = updatePages(this);
		if (data == null) {
			return;
		}
		try {
			Settings.lock("Ship/Container Names");
			for (Location location : data) {
				Long itemID = location.getItemID();
				String eveName = location.getItemName();
				if (!eveName.isEmpty()) {
					Settings.get().getEveNames().put(itemID, eveName);
					MyAsset asset = iDs.get(itemID);
					if (asset.getItem().getCategory().equals("Structure")) {
						CitadelGetter.set(new Citadel(asset.getItemID(), eveName, ApiIdConverter.getLocation(asset.getLocationID())));
					}
				} else {
					Settings.get().getEveNames().remove(itemID);
				}
			}
		} finally {
			Settings.unlock("Ship/Container Names");
		}
	}

	@Override
	public List<Location> get(ApiClient apiClient, String at, Long contid, Integer maxResults) throws ApiException {
		//Get all items matching itemID
		iDs = getIDs(owner);
		return getCommonApi(apiClient).getLocations(owner.getAccessKey(), owner.getAccessCred(), at, contid, maxResults, false,
				valuesFilter(iDs.keySet()), null, null, null, null);
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
