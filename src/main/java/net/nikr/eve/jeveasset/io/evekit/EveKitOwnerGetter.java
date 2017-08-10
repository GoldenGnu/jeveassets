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
package net.nikr.eve.jeveasset.io.evekit;


import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.KeyInfo;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AccountAdder;


public class EveKitOwnerGetter extends AbstractEveKitGetter implements AccountAdder {

	private boolean limited = false;
	private boolean invalidPrivileges = false;
	private boolean wrongEntry = false;

	@Override
	public void load(UpdateTask updateTask, EveKitOwner owner) {
		limited = false;
		invalidPrivileges = false;
		wrongEntry = false;
		super.load(updateTask, owner);
	}

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		limited = false;
		invalidPrivileges = false;
		wrongEntry = false;
		super.load(updateTask, owners);
	}

	@Override
	protected void get(EveKitOwner owner, Long at, boolean first) throws ApiException {
		KeyInfo keyInfo = getAccessKeyApi().getKeyInfo(owner.getAccessKey(), owner.getAccessCred());
		if (owner.getOwnerID() != keyInfo.getEntityID() && owner.getOwnerID() != 0) {
			addError("Wrong Entry");
			wrongEntry = true;
			return;
		}
		owner.setOwnerID(keyInfo.getEntityID());
		owner.setOwnerName(keyInfo.getEntityName());
		owner.setCorporation(keyInfo.getKeyType().equals("corporation"));
		owner.setAccessMask(keyInfo.getMask());
		Long expiry = keyInfo.getExpiry();
		if (expiry > 0) {
			owner.setExpire(new Date(expiry));
		} else {
			owner.setExpire(null);
		}
		Long limit = keyInfo.getLimit();
		if (limit > 0) {
			owner.setLimit(new Date(limit));
		} else {
			owner.setLimit(null);
		}
		int fails = 0;
		int max = 0;
		max++;
		if (!owner.isAccountBalance()) {
			fails++;
		}
		max++;
		if (!owner.isIndustryJobs()) {
			fails++;
		}
		max++;
		if (!owner.isMarketOrders()) {
			fails++;
		}
		max++;
		if (!owner.isJournal()) {
			fails++;
		}
		max++;
		if (!owner.isTransactions()) {
			fails++;
		}
		max++;
		if (!owner.isContracts()) {
			fails++;
		}
		max++;
		if (!owner.isLocations()) {
			fails++;
		}
		max++;
		if (!owner.isAssetList()) {
			fails++;
		}

		limited = (fails > 0 && fails < max);
		invalidPrivileges = (fails >= max);
	}

	@Override
	protected String getTaskName() {
		return "Account";
	}

	@Override
	protected long getAccessMask() {
		return 0;
	}

	@Override
	public boolean isLimited() {
		return  limited;
	}

	@Override
	public boolean isInvalidPrivileges() {
		return invalidPrivileges;
	}

	@Override
	public boolean isWrongEntry() {
		return wrongEntry;
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		owner.setAccountNextUpdate(date);
	}

	@Override
	protected Date getNextUpdate(EveKitOwner owner) {
		return owner.getAccountNextUpdate();
	}

	@Override
	protected ApiClient getApiClient() {
		return getAccessKeyApi().getApiClient();
	}

}
