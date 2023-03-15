/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Citadel.CitadelSource;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterAssetsNamesResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CorporationAssetsNamesResponse;


public class EsiLocationsGetter extends AbstractEsiGetter {

	public EsiLocationsGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getLocationsNextUpdate(), TaskType.LOCATIONS);
	}

	@Override
	protected void update() throws ApiException {
		Map<Long, MyAsset> iDs = getIDs(owner);
		if (owner.isCorporation()) {
			Map<List<Long>, List<CorporationAssetsNamesResponse>> responses = updateList(splitList(iDs.keySet(), LOCATIONS_BATCH_SIZE), DEFAULT_RETRIES, new ListHandler<List<Long>, List<CorporationAssetsNamesResponse>>() {
				@Override
				public ApiResponse<List<CorporationAssetsNamesResponse>> get(List<Long> t) throws ApiException {
					return getAssetsApiAuth().postCorporationsCorporationIdAssetsNamesWithHttpInfo((int) owner.getOwnerID(), t, DATASOURCE, null);
				}
			});

			try {
				Settings.lock("Ship/Container Names");
				for (Map.Entry<List<Long>, List<CorporationAssetsNamesResponse>> entry : responses.entrySet()) {
					for (CorporationAssetsNamesResponse response : entry.getValue()) {
						final long itemID = response.getItemId();
						final String eveName = response.getName();
						if (!eveName.isEmpty()) { //Set name
							Settings.get().getEveNames().put(itemID, eveName);
							MyAsset asset = iDs.get(itemID);
							if (asset.getItem().getCategory().equals(Item.CATEGORY_STRUCTURE)) {
								CitadelGetter.set(new Citadel(asset.getItemID(), eveName, asset.getLocationID(), false, true, CitadelSource.ESI_LOCATIONS));
							}
						} else { //Remove name (Empty)
							Settings.get().getEveNames().remove(itemID);
						}
					}
				}
			} finally {
				Settings.unlock("Ship/Container Names");
			}
		} else {
			Map<List<Long>, List<CharacterAssetsNamesResponse>> responses = updateList(splitList(iDs.keySet(), LOCATIONS_BATCH_SIZE), DEFAULT_RETRIES, new ListHandler<List<Long>, List<CharacterAssetsNamesResponse>>() {
				@Override
				public ApiResponse<List<CharacterAssetsNamesResponse>> get(List<Long> t) throws ApiException {
					return getAssetsApiAuth().postCharactersCharacterIdAssetsNamesWithHttpInfo((int) owner.getOwnerID(), t, DATASOURCE, null);
				}
			});
			try {
				Settings.lock("Ship/Container Names");
				for (Map.Entry<List<Long>, List<CharacterAssetsNamesResponse>> entry : responses.entrySet()) {
					for (CharacterAssetsNamesResponse response : entry.getValue()) {
						final long itemID = response.getItemId();
						final String eveName = response.getName();
						if (!eveName.isEmpty()) { //Set name
							Settings.get().getEveNames().put(itemID, eveName);
							MyAsset asset = iDs.get(itemID);
							if (asset.getItem().getCategory().equals(Item.CATEGORY_STRUCTURE)) {
								CitadelGetter.set(new Citadel(asset.getItemID(), eveName, asset.getLocationID(), false, true, CitadelSource.ESI_LOCATIONS));
							}
						} else { //Remove name (Empty)
							Settings.get().getEveNames().remove(itemID);
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
