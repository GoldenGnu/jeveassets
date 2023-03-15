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
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import net.nikr.eve.jeveasset.io.shared.AccountAdder;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.auth.JWT;
import net.troja.eve.esi.auth.OAuth;
import net.troja.eve.esi.model.CharacterAffiliationResponse;
import net.troja.eve.esi.model.CharacterRolesResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.UniverseNamesResponse;


public class EsiOwnerGetter extends AbstractEsiGetter implements AccountAdder{

	private static final long CACHE_TIMER = 1 * 60 * 60 * 1000L; // 1 hour (hours*min*sec*ms)
	private boolean wrongEntry = false;
	private final Date nextUpdate;

	public EsiOwnerGetter(EsiOwner owner, boolean forceUpdate) {
		this(null, owner, forceUpdate);
	}

	public EsiOwnerGetter(UpdateTask updateTask, EsiOwner owner) {
		this(updateTask, owner, owner.getCorporationName() == null);
	}

	private EsiOwnerGetter(UpdateTask updateTask, EsiOwner owner, boolean forceUpdate) {
		super(updateTask, owner, forceUpdate, owner.getAccountNextUpdate(), TaskType.OWNER);
		nextUpdate = new Date(new Date().getTime() + CACHE_TIMER);
	}

	@Override
	protected void update() throws ApiException {
		//characterID
		OAuth auth = (OAuth) owner.getApiClient().getAuthentication("evesso");
		JWT jwt = auth.getJWT();
		if (jwt == null) {
			addError("INVALID AUTHORIZATION (JWT)", "Account Authorization Invalid\r\n(Fix: Options > Accounts... > Edit the account)", null);
			owner.setInvalid(true);
			return;
		}
		JWT.Payload payload = jwt.getPayload();
		if (payload == null) {
			addError("INVALID AUTHORIZATION (PAYLOAD)", "Account Authorization Invalid\r\n(Fix: Options > Accounts... > Edit the account)", null);
			owner.setInvalid(true);
			return;
		}
		Set<RolesEnum> roles = EnumSet.noneOf(RolesEnum.class);
		Integer characterID = payload.getCharacterID();
		//CorporationID
		List<CharacterAffiliationResponse> affiliationResponse = update(DEFAULT_RETRIES, new EsiHandler<List<CharacterAffiliationResponse>>() {
			@Override
			public ApiResponse<List<CharacterAffiliationResponse>> get() throws ApiException {
				return getCharacterApiOpen().postCharactersAffiliationWithHttpInfo(Collections.singletonList(characterID), DATASOURCE);
			}
		});
		if (affiliationResponse.isEmpty()) {
			addError("INVALID AUTHORIZATION (AFFILIATION)", "Account Authorization Invalid\r\n(Fix: Options > Accounts... > Edit the account)", null);
			owner.setInvalid(true);
			return;
		}
		Integer corporationID = affiliationResponse.get(0).getCorporationId();
		//IDs to Names
		List<Integer> ids = new ArrayList<>();
		ids.add(characterID);
		ids.add(corporationID);
		List<UniverseNamesResponse> namesResponse = update(DEFAULT_RETRIES, new EsiHandler<List<UniverseNamesResponse>>() {
			@Override
			public ApiResponse<List<UniverseNamesResponse>> get() throws ApiException {
				return getUniverseApiOpen().postUniverseNamesWithHttpInfo(ids, DATASOURCE);
			}
		});
		String characterName = null;
		String corporationName = null;
		for (UniverseNamesResponse response : namesResponse) {
			if (characterID.equals(response.getId())) {
				characterName = response.getName();
			} else if (corporationID.equals(response.getId())) {
				corporationName = response.getName();
			}
		}
		if (characterName == null || corporationName == null) {
			addError("INVALID AUTHORIZATION (NAMES)", "Account Authorization Invalid\r\n(Fix: Options > Accounts... > Edit the account)", null);
			owner.setInvalid(true);
			return;
		}
		//Roles
		boolean isCorporation = EsiScopes.CORPORATION_ROLES.isInScope(payload.getScopes());
		if (isCorporation) { //Corporation
			//Updated Character Roles
			CharacterRolesResponse characterRolesResponse = update(DEFAULT_RETRIES, new EsiHandler<CharacterRolesResponse>() {
				@Override
				public ApiResponse<CharacterRolesResponse> get() throws ApiException {
					return getCharacterApiAuth().getCharactersCharacterIdRolesWithHttpInfo(characterID, DATASOURCE, null, null);
				}
			});
			roles.addAll(characterRolesResponse.getRoles());
		}
		if (((!isCorporation && characterID != owner.getOwnerID()) || (isCorporation && corporationID != owner.getOwnerID())) && owner.getOwnerID() != 0) {
			addError(null, "Wrong Entry", null);
			wrongEntry = true;
			return;
		}
		//Update owner
		owner.setScopes(payload.getScopes());
		owner.setRoles(roles);
		owner.setCorporationName(corporationName);
		if (owner.isCorporation()) {
			owner.setOwnerID(corporationID);
			owner.setOwnerName(corporationName);
		} else {
			owner.setOwnerID(payload.getCharacterID());
			owner.setOwnerName(characterName);
		}
		if (isPrivilegesLimited()) {
			addWarning("LIMITED ACCOUNT", "Limited account data access\r\n(Fix: Options > Accounts... > Edit)");
			setError(null);
		}
		setNextUpdate(nextUpdate);
	}

	@Override
	protected void setNextUpdate(Date date) {
		if (date.after(owner.getAccountNextUpdate())) {
			owner.setAccountNextUpdate(date);
		}
	}

	@Override
	protected boolean haveAccess() {
		return true; //Always update accounts
	}

	@Override
	public boolean isPrivilegesLimited() {
		return owner.isPrivilegesLimited();
	}

	@Override
	public boolean isPrivilegesInvalid() {
		return owner.isPrivilegesInvalid();
	}

	@Override
	public boolean isInvalid() {
		return false;
	}

	@Override
	public boolean isWrongEntry() {
		return wrongEntry;
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		return null;
	}

}
