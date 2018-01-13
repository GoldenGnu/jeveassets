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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.Location;
import com.beimin.eveapi.parser.character.CharLocationsParser;
import com.beimin.eveapi.parser.corporation.CorpLocationsParser;
import com.beimin.eveapi.response.shared.LocationsResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class LocationsGetter extends AbstractApiGetter<LocationsResponse> {

	private final int MAX_SIZE = 50;
	private final Map<Long, String> itemMap = new HashMap<Long, String>();

	public LocationsGetter(UpdateTask updateTask, EveApiOwner owner) {
		super(updateTask, owner, false, owner.getLocationsNextUpdate(), TaskType.LOCATIONS);
	}

	@Override
	protected void get(String updaterStatus) throws ApiException {
		Map<List<Long>, LocationsResponse> updateList = updateList(splitList(getIDs(itemMap, owner), MAX_SIZE), NO_RETRIES, new ListHandler<List<Long>, LocationsResponse>() {
			@Override
			public LocationsResponse get(String updaterStatus, List<Long> t) throws ApiException {
				if (owner.isCorporation()) {
					return new CorpLocationsParser()
							.getResponse(EveApiOwner.getApiAuthorization(owner), t);
				} else {
					return new CharLocationsParser()
							.getResponse(EveApiOwner.getApiAuthorization(owner), t);
				}
			}
		});
		for (LocationsResponse response : updateList.values()) {
			if (!handle(response, updaterStatus)) {
				return;
			}
			for (Location location : response.getAll()) {
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
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		owner.setLocationsNextUpdate(nextUpdate);
	}

	@Override
	protected long requestMask() {
		if (owner.isCorporation()) {
			return EveApiAccessMask.LOCATIONS_CORP.getAccessMask();
		} else {
			return EveApiAccessMask.LOCATIONS_CHAR.getAccessMask();
		}
	}
}
