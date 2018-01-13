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

import java.util.Date;
import java.util.EnumSet;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AccountAdder;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.auth.CharacterInfo;
import net.troja.eve.esi.model.CharacterResponse;
import net.troja.eve.esi.model.CharacterRolesResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CorporationResponse;


public class EsiOwnerGetter extends AbstractEsiGetter implements AccountAdder{

	private boolean limited = false;
	private boolean invalidPrivileges = false;
	private boolean wrongEntry = false;

	public EsiOwnerGetter(EsiOwner owner, boolean forceUpdate) {
		super(null, owner, forceUpdate, owner.getAccountNextUpdate(), TaskType.OWNER, NO_RETRIES);
	}

	public EsiOwnerGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getAccountNextUpdate(), TaskType.OWNER, NO_RETRIES);
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		CharacterInfo characterInfo = getSsoApiAuth(apiClient).getCharacterInfo();
		Set<RolesEnum> roles = EnumSet.noneOf(RolesEnum.class);
		Integer characterID = characterInfo.getCharacterId();
		Integer corporationID = 0;
		String corporationName = "";
		boolean isCorporation = EsiScopes.CORPORATION_ROLES.isInScope(characterInfo.getScopes());
		if (isCorporation) { //Corporation
			//CharacterID to CorporationID
			CharacterResponse character = getCharacterApiOpen(apiClient).getCharactersCharacterId(characterID, DATASOURCE, USER_AGENT, null);
			corporationID = character.getCorporationId();
			//CorporationID to CorporationName
			CorporationResponse corporation = getCorporationApiOpen(apiClient).getCorporationsCorporationId(corporationID, DATASOURCE, USER_AGENT, null);
			corporationName = corporation.getName();
			//Updated Character Roles
			CharacterRolesResponse characterRolesResponse = getCharacterApiAuth(apiClient).getCharactersCharacterIdRoles(characterID, DATASOURCE, null, USER_AGENT, null);
			roles.addAll(characterRolesResponse.getRoles());
		}
		if (((!isCorporation && characterID != owner.getOwnerID())
				|| (isCorporation && corporationID != owner.getOwnerID()))
				&& owner.getOwnerID() != 0) {
			addError(null, "Wrong Entry", null);
			wrongEntry = true;
			return;
		}
		owner.setCharacterOwnerHash(characterInfo.getCharacterOwnerHash());
		owner.setScopes(characterInfo.getScopes());
		owner.setIntellectualProperty(characterInfo.getIntellectualProperty());
		owner.setTokenType(characterInfo.getTokenType());
		owner.setRoles(roles);
		if (owner.isCorporation()) {
			owner.setOwnerID(corporationID);
			owner.setOwnerName(corporationName);
		} else {
			owner.setOwnerID(characterInfo.getCharacterId());
			owner.setOwnerName(characterInfo.getCharacterName());
		}

		int fails = 0;
		int max = 0;
		for (EsiScopes scope : EsiScopes.values()) {
			if (!owner.isCorporation() && !scope.isCharacterScope()) {
				continue;
			}
			if (owner.isCorporation() && !scope.isCorporationScope()) {
				continue;
			}
			max++;
			if (!scope.isInScope(owner.getScopes())) {
				fails++;
			}
		}
		limited = (fails > 0 && fails < max);
		invalidPrivileges = (fails >= max);
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setAccountNextUpdate(date);
	}

	@Override
	protected boolean inScope() {
		return true; //Always update accounts
	}

	@Override
	public boolean isLimited() {
		return limited;
	}

	@Override
	public boolean isInvalidPrivileges() {
		return invalidPrivileges;
	}

	@Override
	public boolean isInvalid() {
		return false;
	}

	@Override
	public boolean isWrongEntry() {
		return wrongEntry;
	}
}
