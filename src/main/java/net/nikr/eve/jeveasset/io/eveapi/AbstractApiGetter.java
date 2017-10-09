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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.handler.ApiError;
import com.beimin.eveapi.response.ApiResponse;
import java.util.Date;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractGetter;


public abstract class AbstractApiGetter<T extends ApiResponse> extends AbstractGetter<EveApiOwner, String, ApiException> {

	private static final String INVALID_ACCOUNT = "HTTP response code: 403";

	public AbstractApiGetter(UpdateTask updateTask, EveApiOwner owner, boolean forceUpdate, Date nextUpdate, TaskType taskType) {
		super(updateTask, owner, forceUpdate, nextUpdate, taskType, "EveApi");
	}

	@Override
	public Void call() throws Exception {
		updateApi(new EveApiUpdater(), 0);
		return null;
	}

	@Override
	protected <R> R updateApi(Updater<R, String, ApiException> updater, int retries) {
		//Check if API key is invalid (still update when editing account AKA forceUpdate)
		if (!canUpdate(updater.getStatus())) {
			return null;
		}
		if (isInvalid()) {
			errorInvalid(updater.getStatus());
			return null;
		}
		//Check if API key is expired (still update when editing account AKA forceUpdate)
		if (isExpired()) {
			errorExpired(updater.getStatus());
			return null;
		}

		try {
			return updater.update(null);
		} catch (ApiException ex) { //Real Error
			if (ex.getMessage().contains(INVALID_ACCOUNT) && !isExpired()) { //Invalid
				errorInvalid(updater.getStatus());
			} else if (isExpired()) { //Expired
				errorExpired(updater.getStatus());
			} else {
				addError(updater.getStatus(), "ApiException: " + ex.getMessage(), "ApiException: " + ex.getMessage(), ex);
			}
		}
		return null;
	}

	@Override
	protected boolean invalidAccessPrivileges() {
		return (getAccessMask() & requestMask()) != requestMask();
	}

	private long getAccessMask() {
		if (owner != null) {
			return owner.getParentAccount().getAccessMask();
		} else {
			return 0;
		}
	}
	private boolean isExpired() {
		if (owner != null) {
			return owner.getParentAccount().isExpired();
		} else {
			return false; //Eve
		}
	}

	public boolean isInvalid() {
		if (owner != null) {
			return owner.getParentAccount().isInvalid();
		} else {
			return false; //Eve
		}
	}

	private void notInvalid() {
		if (owner != null) {
			owner.getParentAccount().setInvalid(false);
		}
	}

	private void errorInvalid(String updaterStatus) {
		if (owner != null) {
			owner.getParentAccount().setInvalid(true);
		}
		addError(updaterStatus, "API KEY INVALID", "API Key invalid");
	}

	private void errorExpired(String updaterStatus) {
		if (owner != null) {
			owner.getParentAccount().setExpires(new Date(1));
		}
		addError(updaterStatus, "API KEY EXPIRE", "API Key expired");
	}

	protected boolean handle(ApiResponse response, String updaterStatus) {
		if (!response.hasError()) { //OK
			setNextUpdate(response.getCachedUntil());
			notInvalid();
			logInfo(updaterStatus, "Updated");
			return true;
		} else { //API Error
			ApiError apiError = response.getError();
			switch (apiError.getCode()) {
				case 203: //Invalid
					errorInvalid(null);
					break;
				case 222: //Expired 
					errorExpired(null);
					break;
				default:
					addError(null, "API Error: " + apiError.getCode() + " :: " + apiError.getError(), "API Error: " + apiError.getCode() + " :: " + apiError.getError());
					break;
			}
		}
		return false;
	}

	protected abstract void get(String updaterStatus) throws ApiException;
	protected abstract long requestMask();

	public class EveApiUpdater implements Updater<Void, String, ApiException> {

		@Override
		public Void update(String updaterStatus) throws ApiException {
			get(getStatus());
			return null;
		}

		@Override
		public String getStatus() {
			return "Completed";
		}
	}
}
