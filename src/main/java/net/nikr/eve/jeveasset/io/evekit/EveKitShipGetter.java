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
import enterprises.orbital.evekit.client.model.CharacterLocation;
import enterprises.orbital.evekit.client.model.CharacterShip;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class EveKitShipGetter extends AbstractEveKitGetter {

	public EveKitShipGetter(UpdateTask updateTask, EveKitOwner owner, Long at) {
		super(updateTask, owner, false, owner.getAssetNextUpdate(), TaskType.SHIP, false, at);
	}

	public EveKitShipGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, false, owner.getAssetNextUpdate(), TaskType.SHIP, false, null);
	}

	@Override
	protected void get(ApiClient apiClient, Long at, boolean first) throws ApiException {
		//Get Ship
		List<CharacterShip> characterShips = getCharacterApi(apiClient).getShipType(owner.getAccessKey(), owner.getAccessCred(), atFilter(at), null, null, false, null, null, null);
		//Get Location
		List<CharacterLocation> characterLocations = getCharacterApi(apiClient).getLocation(owner.getAccessKey(), owner.getAccessCred(), atFilter(at), null, null, false, null, null, null);
		//Create assets
		if (characterShips == null || characterShips.isEmpty()) {
			return;
		}
		if (characterLocations == null || characterLocations.isEmpty()) {
			return;
		}
		CharacterLocation location = characterLocations.get(0);
		CharacterShip ship = characterShips.get(0);
		MyAsset activeShip = EveKitConverter.toAssetsShip(ship, location, owner);
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
				Settings.get().getEveNames().put(ship.getShipItemID(), ship.getShipName());
			} finally {
				Settings.unlock("Active Ship Name");
			}
		} 
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.LOCATIONS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(Date date) {
		//Use the assets update times
	}
	
}
