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
import com.beimin.eveapi.handler.ApiError;
import com.beimin.eveapi.response.ApiResponse;
import java.util.Date;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker.TaskCancelledException;


public abstract class AbstractApiGetter<T extends ApiResponse> extends AbstractGetter<EveApiOwner, String, ApiException> {

	private static final String INVALID_ACCOUNT = "HTTP response code: 403";

	public AbstractApiGetter(UpdateTask updateTask, EveApiOwner owner, boolean forceUpdate, Date nextUpdate, TaskType taskType) {
		super(updateTask, owner, forceUpdate, nextUpdate, taskType, "EveApi");
	}

	@Override
	public void run() {
		//Ignore migrated accounts
		if (owner != null && owner.isMigrated()) {
			logInfo(null, "Skipping migrated EveApi owner: "  + getOwnerName(owner));
			return;
		}
		if (!canUpdate()) {
			return;
		}
		//Check if API key is invalid (still update when editing account AKA forceUpdate)
		if (!isForceUpdate() && isInvalid()) {
			errorInvalid();
			return;
		}
		//Check if API key is expired (still update when editing account AKA forceUpdate)
		if (!isForceUpdate() && isExpired()) {
			errorExpired();
			return;
		}
		try {
			updateApi(new EveApiUpdater(), 0);
		} catch (ApiException ex) { //Real Error
			if (ex.getMessage().contains(INVALID_ACCOUNT) && !isExpired()) { //Invalid
				errorInvalid();
			} else if (isExpired()) { //Expired
				errorExpired();
			} else {
				addError(null, "ApiException: " + ex.getMessage(), "ApiException: " + ex.getMessage(), ex);
			}
		} catch (TaskCancelledException ex) {
			logInfo(null, "Cancelled");
		} catch (Throwable ex) {
			addError(null, ex.getMessage(), "Unknown Error: " + ex.getMessage(), ex);
		}
	}

	@Override
	protected <R> R updateApi(Updater<R, String, ApiException> updater, int retries) throws ApiException {
		try {
			checkCancelled();
			return updater.update(updater.getStatus());
		} catch (ApiException ex) {
			logError(updater.getStatus(), ex.getMessage(), ex.getMessage());
			throw ex;
		}
	}

	@Override
	protected void throwApiException(Exception ex) throws ApiException {
		Throwable cause = ex.getCause();
		if (cause instanceof ApiException) {
			ApiException apiException = (ApiException) cause;
			throw apiException;
		} else if (cause instanceof RuntimeException) {
			RuntimeException runtimeException = (RuntimeException) cause;
			throw runtimeException;
		} else {
			throw new RuntimeException(cause);
		}
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

	private void errorInvalid() {
		if (owner != null) {
			owner.getParentAccount().setInvalid(true);
		}
		addError(null, "API KEY INVALID", "API Key invalid");
	}

	private void errorExpired() {
		if (owner != null) {
			owner.getParentAccount().setExpires(new Date(1));
		}
		addError(null, "API KEY EXPIRE", "API Key expired");
	}

	protected boolean handle(ApiResponse response, String updaterStatus) {
		if (!response.hasError()) { //OK
			setNextUpdateSafe(response.getCachedUntil());
			notInvalid();
			logInfo(updaterStatus, "Updated");
			return true;
		} else { //API Error
			ApiError apiError = response.getError();
			switch (apiError.getCode()) {
				case 203: //Invalid
					errorInvalid();
					break;
				case 222: //Expired 
					errorExpired();
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

		@Override
		public int getMaxRetries() {
			return NO_RETRIES;
		}
	}
}
