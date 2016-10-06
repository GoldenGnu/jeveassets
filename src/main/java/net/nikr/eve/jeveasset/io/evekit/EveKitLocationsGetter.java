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
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitLocationsGetter extends AbstractEveKitGetter {

	private final Map<Long, String> eveNames = new HashMap<Long, String>();

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		eveNames.clear();
		super.load(updateTask, owners);
		Settings.get().setEveNames(eveNames);
	}
	
	@Override
	protected void get(EveKitOwner owner) throws ApiException {
		List<Location> locations = getCommonApi().getLocations(owner.getAccessKey(), owner.getAccessCred(), null, null, Integer.MAX_VALUE, null, null, null, null, null, null);
		eveNames.putAll(EveKitConverter.convertLocations(locations));
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
