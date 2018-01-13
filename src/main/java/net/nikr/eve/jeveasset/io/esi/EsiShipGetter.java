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
package net.nikr.eve.jeveasset.io.esi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterShipResponse;


public class EsiShipGetter extends AbstractEsiGetter {

	public EsiShipGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getAssetNextUpdate(), TaskType.SHIP, DEFAULT_RETRIES);
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		if (owner.isCorporation()) {
			return; //Character Ednpoint
		}
		LocationApi locationApi = getLocationApiAuth(apiClient);
		//Get Ship
		CharacterShipResponse shipType = locationApi.getCharactersCharacterIdShip((int)owner.getOwnerID(), DATASOURCE, null, null, null);
		//Get Location
		CharacterLocationResponse shipLocation = locationApi.getCharactersCharacterIdLocation((int)owner.getOwnerID(), DATASOURCE, null, null, null);
		//Create assets
		MyAsset activeShip = EsiConverter.toAssetsShip(shipType, shipLocation, owner);
		//Search for active ship
		boolean activeShipInAssets = false;
		for (MyAsset asset : owner.getAssets()) {
			if (asset.getItemID().equals(activeShip.getItemID())) {
				activeShipInAssets = true;
				break;
			}
		}
		//Update Assets (if active ship is not found)
		if (!activeShipInAssets) {
			List<MyAsset> activeShipChildren = new ArrayList<MyAsset>();
			for (MyAsset asset : owner.getAssets()) { //Root assets only
				if (asset.getParents().isEmpty() && activeShip.getItemID().equals(asset.getLocationID())) { //Found Child
					//Add asset to active ship
					activeShip.addAsset(asset);
					//Update locationID from ItemID to locationID
					asset.setLocationID(activeShip.getLocationID());
					asset.setLocationType(RawConverter.toAssetLocationType(activeShip.getLocationID()));
					//Add assets that needs to be removed from the root
					activeShipChildren.add(asset);
					//Set active ship as parent
					asset.getParents().add(activeShip);
				}

			}
			//Add active ship to root
			owner.getAssets().add(activeShip);
			//Remove active ship children from root
			owner.getAssets().removeAll(activeShipChildren);
			//Save ship name
			try {
				Settings.lock("Active Ship Name");
				Settings.get().getEveNames().put(shipType.getShipItemId(), shipType.getShipName());
			} finally {
				Settings.unlock("Active Ship Name");
			}
		} 
	}

	@Override
	protected boolean inScope() {
		if (owner.isCorporation()) {
			return true; //Overwrite the default, so, we don't get errors
		} else {
			return owner.isShip();
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		//Use the assets update times
	}
	
}
