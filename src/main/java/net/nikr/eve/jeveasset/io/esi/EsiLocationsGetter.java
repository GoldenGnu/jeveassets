/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.esi;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Citadel.CitadelSource;
import net.nikr.eve.jeveasset.data.settings.SQLiteSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.AssetsNamesResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;


public class EsiLocationsGetter extends AbstractEsiGetter {

	public EsiLocationsGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getLocationsNextUpdate(), TaskType.LOCATIONS);
	}

	@Override
	protected void update() throws ApiException {
		Map<Long, MyAsset> iDs = getIDs(owner);
		if (owner.isCorporation()) {
			Map<Set<Long>, List<AssetsNamesResponse>> responses = updateList(splitSet(iDs.keySet(), LOCATIONS_BATCH_SIZE), DEFAULT_RETRIES, new ListHandler<Set<Long>, List<AssetsNamesResponse>>() {
				@Override
				public ApiResponse<List<AssetsNamesResponse>> get(Set<Long> t) throws ApiException {
					return getAssetsApiAuth().postCorporationAssetsNamesWithHttpInfo(owner.getOwnerID(), COMPATIBILITY_DATE, t, null, null, null);
				}
			});

			try {
				Settings.lock("Ship/Container Names");
				for (Map.Entry<Set<Long>, List<AssetsNamesResponse>> entry : responses.entrySet()) {
					for (AssetsNamesResponse response : entry.getValue()) {
						final long itemID = response.getItemId();
						final String eveName = response.getName();
						if (!eveName.isEmpty()) { //Set name
							SQLiteSettings.putEveName(itemID, eveName);
							MyAsset asset = iDs.get(itemID);
							if (asset.getItem().getCategory().equals(Item.CATEGORY_STRUCTURE)) {
								CitadelGetter.set(new Citadel(asset.getItemID(), eveName, asset.getLocationID(), false, true, CitadelSource.ESI_LOCATIONS));
							}
						} else { //Remove name (Empty)
							SQLiteSettings.removeEveName(itemID);
						}
					}
				}
			} finally {
				Settings.unlock("Ship/Container Names");
			}
		} else {
			Map<Set<Long>, List<AssetsNamesResponse>> responses = updateList(splitSet(iDs.keySet(), LOCATIONS_BATCH_SIZE), DEFAULT_RETRIES, new ListHandler<Set<Long>, List<AssetsNamesResponse>>() {
				@Override
				public ApiResponse<List<AssetsNamesResponse>> get(Set<Long> t) throws ApiException {
					//((int) , t, DATASOURCE, null);
					return getAssetsApiAuth().postCharacterAssetsNamesWithHttpInfo(owner.getOwnerID(), COMPATIBILITY_DATE, t, null, null, null);
				}
			});
			try {
				Settings.lock("Ship/Container Names");
				for (Map.Entry<Set<Long>, List<AssetsNamesResponse>> entry : responses.entrySet()) {
					for (AssetsNamesResponse response : entry.getValue()) {
						final long itemID = response.getItemId();
						final String eveName = response.getName();
						if (!eveName.isEmpty()) { //Set name
							SQLiteSettings.putEveName(itemID, eveName);
							MyAsset asset = iDs.get(itemID);
							if (asset.getItem().getCategory().equals(Item.CATEGORY_STRUCTURE)) {
								CitadelGetter.set(new Citadel(asset.getItemID(), eveName, asset.getLocationID(), false, true, CitadelSource.ESI_LOCATIONS));
							}
						} else { //Remove name (Empty)
							SQLiteSettings.removeEveName(itemID);
						}
					}
				}
			} finally {
				Settings.unlock("Ship/Container Names");
			}
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setLocationsNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return owner.isLocations();
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		RolesEnum[] roles = {RolesEnum.DIRECTOR};
		return roles;
	}

}
