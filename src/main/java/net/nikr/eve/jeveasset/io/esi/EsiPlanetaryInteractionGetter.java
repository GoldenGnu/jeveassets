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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterPlanetResponse;
import net.troja.eve.esi.model.CharacterPlanetsResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.PlanetPin;
import net.troja.eve.esi.model.PlanetResponse;


public class EsiPlanetaryInteractionGetter extends AbstractEsiGetter {

	public EsiPlanetaryInteractionGetter(UpdateTask updateTask, EsiOwner esiOwner, Date assetNextUpdate) {
		super(updateTask, esiOwner, false, assetNextUpdate, TaskType.PLANETARY_INTERACTION);
	}

	@Override
	protected void update() throws ApiException {
		if (owner.isCorporation()) {
			return; //Character Endpoint
		}
		//Get PI Planets
		List<CharacterPlanetsResponse> responses = update(DEFAULT_RETRIES, new EsiHandler<List<CharacterPlanetsResponse>>() {
			@Override
			public ApiResponse<List<CharacterPlanetsResponse>> get() throws ApiException {
				return getPlanetaryInteractionApiAuth().getCharactersCharacterIdPlanetsWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, null);
			}
		});
		List<Citadel> citadels = new ArrayList<>();
		for (CharacterPlanetsResponse response : responses) { //For each planet
			//Convert planet location to citadel
			PlanetResponse planet = update(DEFAULT_RETRIES, new EsiHandler<PlanetResponse>() {
				@Override
				public ApiResponse<PlanetResponse> get() throws ApiException {
					return getUniverseApiOpen().getUniversePlanetsPlanetIdWithHttpInfo(response.getPlanetId(), DATASOURCE, null);
				}
			});
			Citadel citadel = ApiIdConverter.getCitadel(planet);
			if (citadel != null) {
				citadels.add(citadel);
			}
			//Get planetary assets
			CharacterPlanetResponse planetResponse = update(DEFAULT_RETRIES, new EsiHandler<CharacterPlanetResponse>() {
				@Override
				public ApiResponse<CharacterPlanetResponse> get() throws ApiException {
					return getPlanetaryInteractionApiAuth().getCharactersCharacterIdPlanetsPlanetIdWithHttpInfo((int) owner.getOwnerID(), response.getPlanetId(), DATASOURCE, null);
				}
			});
			for (PlanetPin pin : planetResponse.getPins()) { //For each pin on planet
				if (pin.getContents() == null) {
					continue;
				}
				owner.addAsset(EsiConverter.toAssetsPlanetaryInteraction(response, pin, owner));
			}
		}
		CitadelGetter.set(citadels);
	}

	@Override
	protected boolean haveAccess() {
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

	@Override
	protected RolesEnum[] getRequiredRoles() {
		return null;
	}

}
