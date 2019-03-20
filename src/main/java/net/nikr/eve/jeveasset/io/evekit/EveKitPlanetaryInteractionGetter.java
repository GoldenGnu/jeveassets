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

import enterprises.orbital.evekit.client.ApiException;
import enterprises.orbital.evekit.client.ApiResponse;
import enterprises.orbital.evekit.client.model.PlanetaryPin;
import enterprises.orbital.evekit.client.model.PlanetaryPinContent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.online.FuzzworkMapGetter;
import net.nikr.eve.jeveasset.io.online.FuzzworkMapGetter.Planet;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class EveKitPlanetaryInteractionGetter extends AbstractEveKitGetter implements EveKitPagesHandler<PlanetaryPin> {

	public EveKitPlanetaryInteractionGetter(UpdateTask updateTask, EveKitOwner eveKitOwner) {
		super(updateTask, eveKitOwner, false, eveKitOwner.getAssetNextUpdate(), TaskType.PLANETARY_INTERACTION, false, null);
	}

	@Override
	protected void update(Long at, boolean first) throws ApiException {
		if (owner.isCorporation()) {
			return; //Character Endpoint
		}
		List<PlanetaryPin> pins = updatePages(this);
		//Convert planet locations to citadels
		Set<Integer> planetIDs = new HashSet<>();
		for (PlanetaryPin pin : pins) {
			planetIDs.add(pin.getPlanetID());
		}
		List<Planet> planets = FuzzworkMapGetter.getPlanets(planetIDs);
		List<Citadel> citadels = new ArrayList<>();
		for (Planet planet : planets) {
			Citadel citadel = ApiIdConverter.getCitadel(planet);
			if (citadel != null) {
				citadels.add(citadel);
			}
		}
		CitadelGetter.set(citadels);
		//Get planetary assets
		for (PlanetaryPin pin : pins) {
			for (PlanetaryPinContent content : pin.getContents()) {
				owner.addAsset(EveKitConverter.toAssetsPlanetaryInteraction(content, pin, owner));
			}
		}
	}

	@Override
	public ApiResponse<List<PlanetaryPin>> get(String at, Long cid, Integer maxResults) throws ApiException {
		return getCharacterApi().getPlanetaryPinsWithHttpInfo(owner.getAccessKey(), owner.getAccessCred(), at, null, null, false,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Override
	public long getCID(PlanetaryPin k) {
		return k.getCid();
	}

	@Override
	public Long getLifeStart(PlanetaryPin obj) {
		return obj.getLifeStart();
	}

	@Override
	public void saveCID(Long cid) {
		//Do nothing
	}

	@Override
	public Long loadCID() {
		return null;
	}

	@Override
	protected boolean haveAccess() {
		return EveKitAccessMask.ASSET_LIST.isInMask(owner.getAccessMask());
	}

	@Override
	protected void setNextUpdate(Date date) {
		//Use the assets update times
	}

	
}
