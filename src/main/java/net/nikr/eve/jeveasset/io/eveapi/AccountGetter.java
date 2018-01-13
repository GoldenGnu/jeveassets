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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.response.account.ApiKeyInfoResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AccountAdder;


public class AccountGetter extends AbstractApiGetter<ApiKeyInfoResponse> implements AccountAdder  {

	private final EveApiAccount apiAccount;
	private boolean limited = false;
	private boolean invalidPrivileges = false;
	private boolean wrongEntry = false;

	public AccountGetter(EveApiAccount apiAccount, boolean forceUpdate) {
		super(null, null, forceUpdate, apiAccount.getAccountNextUpdate(), TaskType.OWNER);
		this.apiAccount = apiAccount;
	}

	public AccountGetter(UpdateTask updateTask, EveApiAccount apiAccount) {
		super(updateTask, null, false, apiAccount.getAccountNextUpdate(), TaskType.OWNER);
		this.apiAccount = apiAccount;
		for (EveApiOwner eveApiOwner : apiAccount.getOwners()) {
			if (eveApiOwner.canMigrate()) {
				addMigrationWarning();
				break;
			}
		}
	}

	@Override
	protected void get(String updaterStatus) throws ApiException {
		ApiKeyInfoResponse response = new com.beimin.eveapi.parser.account.ApiKeyInfoParser()
				.getResponse(EveApiOwner.getApiAuthorization(apiAccount));
		if (!handle(response, updaterStatus)) {
			return;
		}
		//Changed between Char and Corp AKA should be treated as a new api
		boolean typeChanged = !apiAccount.compareTypes(response.getType());

		//Update account
		apiAccount.setAccessMask(response.getAccessMask());
		apiAccount.setExpires(response.getExpires());
		apiAccount.setType(response.getType());

		List<Character> characters = new ArrayList<Character>(response.getEveCharacters());
		List<EveApiOwner> owners = new ArrayList<EveApiOwner>();

		int fails = 0;
		int max = 0;
		max++;
		if (!apiAccount.isAccountBalance()) {
			fails++;
		}
		max++;
		if (!apiAccount.isIndustryJobs()) {
			fails++;
		}
		max++;
		if (!apiAccount.isMarketOrders()) {
			fails++;
		}
		max++;
		if (!apiAccount.isJournal()) {
			fails++;
		}
		max++;
		if (!apiAccount.isTransactions()) {
			fails++;
		}
		max++;
		if (!apiAccount.isContracts()) {
			fails++;
		}
		max++;
		if (!apiAccount.isLocations()) {
			fails++;
		}
		max++;
		if (!apiAccount.isAssetList()) {
			fails++;
		}

		limited = (fails > 0 && fails < max);
		invalidPrivileges = (fails >= max);

		wrongEntry = !apiAccount.getOwners().isEmpty();
		for (Character apiCharacter : characters) {
			boolean found = false;
			for (EveApiOwner eveApiOwner : apiAccount.getOwners()) {
				if ((eveApiOwner.getOwnerID() == apiCharacter.getCharacterID() || eveApiOwner.getOwnerID() == apiCharacter.getCorporationID()) && !typeChanged) {
					eveApiOwner.setOwnerName(getName(apiCharacter));
					eveApiOwner.setOwnerID(getID(apiCharacter));
					owners.add(eveApiOwner);
					found = true;
					wrongEntry = false;
					break;
				}
			}
			if (!found) { //Add New
				owners.add(new EveApiOwner(apiAccount, getName(apiCharacter), getID(apiCharacter)));
			}
		}
		if (wrongEntry) {
			addError(null, "Wrong Entry", null);
		} else {
			apiAccount.setOwners(owners);
		}
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		apiAccount.setAccountNextUpdate(nextUpdate);
	}

	@Override
	protected long requestMask() {
		return EveApiAccessMask.OPEN.getAccessMask();
	}

	private String getName(final Character apiCharacter) {
		if (apiAccount.isCharacter()) {
			return apiCharacter.getName();
		} else {
			return apiCharacter.getCorporationName();
		}
	}
	private long getID(final Character apiCharacter) {
		if (apiAccount.isCharacter()) {
			return apiCharacter.getCharacterID();
		} else {
			return apiCharacter.getCorporationID();
		}
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
	public boolean isWrongEntry() {
		return wrongEntry;
	}
}
