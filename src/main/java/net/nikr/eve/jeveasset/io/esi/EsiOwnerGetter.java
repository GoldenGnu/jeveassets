/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.esi.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AccountAdder;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.auth.CharacterInfo;


public class EsiOwnerGetter extends AbstractEsiGetter implements AccountAdder{

	private boolean limited = false;
	private boolean invalidPrivileges = false;
	private boolean wrongEntry = false;
	private boolean accountImport = false;

	@Override
	public void load(EsiOwner owner) {
		limited = false;
		invalidPrivileges = false;
		wrongEntry = false;
		accountImport = true;
		super.load(owner);
	}

	@Override
	public void load(UpdateTask updateTask, List<EsiOwner> owners) {
		limited = false;
		invalidPrivileges = false;
		wrongEntry = false;
		accountImport = false;
		super.load(updateTask, owners);
	}

	@Override
	protected ApiClient get(EsiOwner owner) throws ApiException {
		CharacterInfo characterInfo = getSsoApiAuth(owner).getCharacterInfo();
		if (characterInfo.getCharacterId() != owner.getOwnerID() && owner.getOwnerID() != 0) {
			addError("Wrong Entry");
			wrongEntry = true;
			return getSsoClient();
		}
		owner.setAccountName(characterInfo.getCharacterName());
		owner.setOwnerID(characterInfo.getCharacterId());
		owner.setOwnerName(characterInfo.getCharacterName());
		owner.setCharacterOwnerHash(characterInfo.getCharacterOwnerHash());
		owner.setScopes(characterInfo.getScopes());
		owner.setIntellectualProperty(characterInfo.getIntellectualProperty());
		owner.setTokenType(characterInfo.getTokenType());

		int fails = 0;
		int max = 0;
		if (accountImport) {
			for (EsiScopes scopes : EsiScopes.values()) {
				if (!scopes.isEnabled()) {
					continue;
				}
				max++;
				if (!owner.getScopes().contains(scopes.getScope())) {
					fails++;
				}
			}
		}
		limited = (fails > 0 && fails < max);
		invalidPrivileges = (fails >= max);
		return getSsoClient();
	}

	@Override
	protected void setNextUpdate(EsiOwner owner, Date date) {
		owner.setAccountNextUpdate(date);
	}

	@Override
	protected Date getNextUpdate(EsiOwner owner) {
		return owner.getAccountNextUpdate();
	}

	@Override
	protected String getTaskName() {
		return "Account";
	}

	@Override
	protected boolean inScope(EsiOwner owner) {
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
