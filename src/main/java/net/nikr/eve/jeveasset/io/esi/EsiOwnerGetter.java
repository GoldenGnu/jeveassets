/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import java.util.EnumSet;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import net.nikr.eve.jeveasset.io.shared.AccountAdder;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.auth.JWT;
import net.troja.eve.esi.auth.OAuth;
import net.troja.eve.esi.model.CharacterResponse;
import net.troja.eve.esi.model.CharacterRolesResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CorporationResponse;


public class EsiOwnerGetter extends AbstractEsiGetter implements AccountAdder{

	private boolean wrongEntry = false;

	public EsiOwnerGetter(EsiOwner owner, boolean forceUpdate) {
		super(null, owner, forceUpdate, owner.getAccountNextUpdate(), TaskType.OWNER);
	}

	public EsiOwnerGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, owner.getCorporationName() == null, owner.getAccountNextUpdate(), TaskType.OWNER);
	}

	@Override
	protected void update() throws ApiException {
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
		//Character
		CharacterResponse character = update(DEFAULT_RETRIES, new EsiHandler<CharacterResponse>() {
			@Override
			public ApiResponse<CharacterResponse> get() throws ApiException {
				return getCharacterApiOpen().getCharactersCharacterIdWithHttpInfo(characterID, DATASOURCE, null);
			}
		});
		Integer corporationID = character.getCorporationId();
		//CorporationID to CorporationName
		CorporationResponse corporation = update(DEFAULT_RETRIES, new EsiHandler<CorporationResponse>() {
			@Override
			public ApiResponse<CorporationResponse> get() throws ApiException {
				return getCorporationApiOpen().getCorporationsCorporationIdWithHttpInfo(corporationID, DATASOURCE, null);
			}
		});
		boolean isCorporation = EsiScopes.CORPORATION_ROLES.isInScope(payload.getScopes());
		if (isCorporation) { //Corporation
			//Updated Character Roles
			CharacterRolesResponse characterRolesResponse = getCharacterApiAuth().getCharactersCharacterIdRoles(characterID, DATASOURCE, null, null);
			roles.addAll(characterRolesResponse.getRoles());
		}
		if (((!isCorporation && characterID != owner.getOwnerID())
				|| (isCorporation && corporationID != owner.getOwnerID()))
				&& owner.getOwnerID() != 0) {
			addError(null, "Wrong Entry", null);
			wrongEntry = true;
			return;
		}
		owner.setScopes(payload.getScopes());
		owner.setRoles(roles);
		owner.setCorporationName(corporation.getName());
		if (owner.isCorporation()) {
			owner.setOwnerID(corporationID);
			owner.setOwnerName(corporation.getName());
		} else {
			owner.setOwnerID(payload.getCharacterID());
			owner.setOwnerName(character.getName());
		}
		if (isPrivilegesLimited()) {
			addWarning("LIMITED ACCOUNT", "Limited account data access\r\n(Fix: Options > Accounts... > Edit)");
			setError(null);
		}
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
