/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import java.util.stream.Collectors;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DEFAULT_RETRIES;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterClonesResponse;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.Clone;


public class EsiClonesGetter extends AbstractEsiGetter {

	public EsiClonesGetter(UpdateTask updateTask, EsiOwner owner, Date assetNextUpdate) {
		super(updateTask, owner, false, assetNextUpdate, TaskType.CLONES);
	}

	@Override
	protected void update() throws ApiException {
		if (owner.isCorporation()) {
			return; //Character Endpoint
		}
		//Get Jump Clones
		CharacterClonesResponse jumpClonesResponse = update(DEFAULT_RETRIES, new EsiHandler<CharacterClonesResponse>() {
			@Override
			public ApiResponse<CharacterClonesResponse> get() throws ApiException {
				return getClonesApiAuth().getCharactersCharacterIdClonesWithHttpInfo((int)owner.getOwnerID(), DATASOURCE, null, null);
			}
		});
		//Get Active Clone
		List<Integer> activeClone = update(DEFAULT_RETRIES, new EsiHandler<List<Integer>>() {
			@Override
			public ApiResponse<List<Integer>> get() throws ApiException {
				return getClonesApiAuth().getCharactersCharacterIdImplantsWithHttpInfo((int)owner.getOwnerID(), DATASOURCE, null, null);
			}
		});
		//Get Location
		CharacterLocationResponse characterLocation = update(DEFAULT_RETRIES, new EsiHandler<CharacterLocationResponse>() {
			@Override
			public ApiResponse<CharacterLocationResponse> get() throws ApiException {
				return getLocationApiAuth().getCharactersCharacterIdLocationWithHttpInfo((int)owner.getOwnerID(), DATASOURCE, null, null);
			}
		});
		Long activeCloneLocation = RawConverter.toLocationID(characterLocation);
		
		//Create assets
		List<MyAsset> implants = new ArrayList<>();
		List<Clone> jumpClones = jumpClonesResponse.getJumpClones();
		
		for (Clone clone : jumpClones){
			List<Integer> cloneImplants = clone.getImplants();
			Long cloneLocation = clone.getLocationId();
			Long cloneId = (long)clone.getJumpCloneId();
			for (Integer implant : cloneImplants){
				MyAsset implantAsset = EsiConverter.toAssetsImplant(implant, cloneLocation, cloneId, owner);
				implants.add(implantAsset);
			}
		}

		for (Integer implant : activeClone){
			Long cloneId = owner.getOwnerID();
			MyAsset activeCloneImplant = EsiConverter.toAssetsImplant(implant, activeCloneLocation, cloneId, owner);
			implants.add(activeCloneImplant);
		}
		
		
		//Clear out implants
		List<MyAsset> assets;
		synchronized (owner) {
			assets = new ArrayList<>(owner.getAssets());
		}
		List<MyAsset> existingImplants = assets.stream().filter(asset -> "Plugged in Implant".equals(asset.getFlag())).collect(Collectors.toList());
		if (!existingImplants.isEmpty()) {
			owner.removeAssets(existingImplants);
		}
		
		//Reload Implants
		for (MyAsset implant : implants){
			owner.addAsset(implant);
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		//Use the assets update times
	}

	@Override
	protected boolean haveAccess() {
		if (owner.isCorporation()) {
			return true; //Overwrite the default, so, we don't get errors
		} else {
			return owner.isClones() && owner.isImplants();
		}
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		return null;
	}

}
