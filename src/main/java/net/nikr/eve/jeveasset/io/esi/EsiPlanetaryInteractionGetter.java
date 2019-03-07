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
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterPlanetResponse;
import net.troja.eve.esi.model.CharacterPlanetsResponse;
import net.troja.eve.esi.model.PlanetContent;
import net.troja.eve.esi.model.PlanetPin;
import net.troja.eve.esi.model.PlanetResponse;


public class EsiPlanetaryInteractionGetter extends AbstractEsiGetter {

	public EsiPlanetaryInteractionGetter(UpdateTask updateTask, EsiOwner esiOwner) {
		super(updateTask, esiOwner, false, esiOwner.getAssetNextUpdate(), TaskType.PLANETARY_INTERACTION, NO_RETRIES);
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		if (owner.isCorporation()) {
			return; //Character Endpoint
		}
		//Get PI Planets
		List<CharacterPlanetsResponse> responses = getPlanetaryInteractionApiAuth(apiClient).getCharactersCharacterIdPlanets((int) owner.getOwnerID(), DATASOURCE, null, null);
		List<Citadel> citadels = new ArrayList<>();
		for (CharacterPlanetsResponse response : responses) { //For each planet
			//Convert planet location to citadel
			PlanetResponse planet = getUniverseApiOpen(apiClient).getUniversePlanetsPlanetId(response.getPlanetId(), DATASOURCE, null);
			Citadel citadel = ApiIdConverter.getCitadel(planet);
			if (citadel != null) {
				citadels.add(citadel);
			}
			//Get planetary assets
			CharacterPlanetResponse planetResponse = getPlanetaryInteractionApiAuth(apiClient).getCharactersCharacterIdPlanetsPlanetId((int) owner.getOwnerID(), response.getPlanetId(), DATASOURCE, null, null);
			for (PlanetPin pin : planetResponse.getPins()) { //For each pin on planet
				for (PlanetContent content : pin.getContents()) { //For each type in pin
					owner.addAsset(EsiConverter.toAssetsPlanetaryInteraction(response, pin, content, owner));
				}
			}
			
		}
		CitadelGetter.set(citadels);
	}

	@Override
	protected boolean inScope() {
		if (owner.isCorporation()) {
			return true; //Overwrite the default, so, we don't get errors
		} else {
			return owner.isPlanetaryInteraction();
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		//Use the assets update times
	}

	
}
