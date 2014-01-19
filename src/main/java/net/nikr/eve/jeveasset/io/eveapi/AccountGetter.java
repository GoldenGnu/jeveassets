/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import com.beimin.eveapi.account.apikeyinfo.ApiKeyInfoResponse;
import com.beimin.eveapi.account.characters.EveCharacter;
import com.beimin.eveapi.exception.ApiException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Account.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;


public class AccountGetter extends AbstractApiGetter<ApiKeyInfoResponse> {

	private boolean limited = false;
	private boolean full = false;
	private boolean fail = true;

	public AccountGetter() {
		super("Accounts", false, true);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final Account account) {
		super.loadAccount(updateTask, forceUpdate, account);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<Account> accounts) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected ApiKeyInfoResponse getResponse(final boolean bCorp) throws ApiException {
		return com.beimin.eveapi.account.apikeyinfo
				.ApiKeyInfoParser.getInstance()
				.getResponse(Owner.getApiAuthorization(getAccount()));
	}

	@Override
	protected Date getNextUpdate() {
		return getAccount().getAccountNextUpdate();
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getAccount().setAccountNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(final ApiKeyInfoResponse response) {
		//Changed between Char and Corp AKA should be treated as a new api
		boolean typeChanged = !getAccount().compareTypes(response.getType());

		//Update account
		getAccount().setAccessMask(response.getAccessMask());
		getAccount().setExpires(response.getExpires());
		getAccount().setType(response.getType());

		List<EveCharacter> characters = new ArrayList<EveCharacter>(response.getEveCharacters());
		List<Owner> owners = new ArrayList<Owner>();

		int fails = 0;
		int max = 0;
		if (isForceUpdate()) {
			max++;
			if (!getAccount().isAccountBalance()) {
				fails++;
			}
			max++;
			if (!getAccount().isIndustryJobs()) {
				fails++;
			}
			max++;
			if (!getAccount().isMarketOrders()) {
				fails++;
			}
			max++;
			if (!getAccount().isJournal()) {
				fails++;
			}
			max++;
			if (!getAccount().isTransactions()) {
				fails++;
			}
			max++;
			if (!getAccount().isContracts()) {
				fails++;
			}
			max++;
			if (!getAccount().isLocations()) {
				fails++;
			}
			max++;
			if (!getAccount().isAssetList()) {
				fails++;
			}
		}

		limited = (fails > 0 && fails < max);
		full = (fails == 0);
		fail = (fails >= max);

		for (EveCharacter apiCharacter : characters) {
			boolean found = false;
			for (Owner owner : getAccount().getOwners()) {
				if ((owner.getOwnerID() == apiCharacter.getCharacterID() || owner.getOwnerID() == apiCharacter.getCorporationID()) && !typeChanged) {
					owner.setName(getName(apiCharacter));
					owner.setOwnerID(getID(apiCharacter));
					owners.add(owner);
					found = true;
					break;
				}
			}
			if (!found) { //Add New
				owners.add(new Owner(getAccount(), getName(apiCharacter), getID(apiCharacter)));
			}
		}
		getAccount().setOwners(owners);
	}

	@Override
	protected void updateFailed(final Owner ownerFrom, final Owner ownerTo) { }

	@Override
	protected long requestMask(boolean bCorp) {
		return AccessMask.OPEN.getAccessMask();
	}

	private String getName(final EveCharacter apiCharacter) {
		if (getAccount().isCharacter()) {
			return apiCharacter.getName();
		} else {
			return apiCharacter.getCorporationName();
		}
	}
	private long getID(final EveCharacter apiCharacter) {
		if (getAccount().isCharacter()) {
			return apiCharacter.getCharacterID();
		} else {
			return apiCharacter.getCorporationID();
		}
	}

	public boolean isLimited() {
		return limited;
	}

	public boolean isFull() {
		return full;
	}

	public boolean isFail() {
		return fail;
	}
}
