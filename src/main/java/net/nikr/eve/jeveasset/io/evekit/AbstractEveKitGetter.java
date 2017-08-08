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

import enterprises.orbital.evekit.client.api.AccessKeyApi;
import enterprises.orbital.evekit.client.api.CommonApi;
import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractEveKitGetter {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractEveKitGetter.class);

	private final CommonApi commonApi = new CommonApi();
	private final AccessKeyApi accessKeyApi = new AccessKeyApi();
	
	private String error = null;
	private boolean invalid = false;

	protected void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		load(updateTask, owners, null, false);
	}

	protected void load(UpdateTask updateTask, List<EveKitOwner> owners, Long at) {
		load(updateTask, owners, at, false);
	}

	protected void load(UpdateTask updateTask, List<EveKitOwner> owners, boolean first) {
		load(updateTask, owners, null, first);
	}

	private void load(UpdateTask updateTask, List<EveKitOwner> owners, Long at, boolean first) {
		LOG.info("EveKit: " + getTaskName() + " updating:");
		error = null;
		invalid = false;
		int progress = 0;
		if (updateTask != null && getProgressStart() == 0) {
			updateTask.resetTaskProgress();
		}
		for (EveKitOwner owner : owners) {
			if (owner.isShowOwner()) { //Ignore not shown owners
				loadApi(updateTask, owner, at, first, false);
			}
			if (updateTask != null) {
				if (updateTask.isCancelled()) {
					addError("Cancelled");
					return;
				}
				progress++;
				updateTask.setTaskProgress(owners.size(), progress, getProgressStart(), getProgressEnd());
			}
		}
	}

	protected void load(UpdateTask updateTask, EveKitOwner owner) {
		LOG.info("EveKit: " + getTaskName() + " updating:");
		error = null;
		invalid = false;
		loadApi(updateTask, owner, null, false, true);
	}

	private boolean loadApi(UpdateTask updateTask, EveKitOwner owner, Long at, boolean first, boolean forceUpdate) {
		try {
			//Check if the Access Mask include this API
			if ((owner.getAccessMask() & getAccessMask()) != getAccessMask()) {
				addError("	EveKit: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (NOT ENOUGH ACCESS PRIVILEGES)");
				if (updateTask != null) {
					updateTask.addError(owner.getOwnerName(), "EveKit: Not enough access privileges.\r\n(Fix: Add " + getTaskName() + " to the API Key)");
				}
				return false;
			}
			//Check if the Api Key is expired
			if (owner.isExpired()) {
				addError("	EveKit: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (API KEY EXPIRED)");
				if (updateTask != null) {
					updateTask.addError(owner.getOwnerName(), "EveKit: API Key expired");
				}
				return false;
			}
			//Check API cache time
			if (!forceUpdate && !Settings.get().isUpdatable(getNextUpdate(owner), false)) {
				addError("	EveKit: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (NOT ALLOWED YET)");
				if (updateTask != null) {
					updateTask.addError(owner.getOwnerName(), "EveKit: Not allowed yet.\r\n(Fix: Just wait a bit)");
				}
				return false;
			}
			get(owner, at, first);
			LOG.info("	EveKit: " + getTaskName() + " updated for " + owner.getOwnerName());
			List<String> expiryHeaders = getApiClient().getResponseHeaders().get("Expires");
			if (expiryHeaders != null && !expiryHeaders.isEmpty()) {
				setNextUpdate(owner, Formater.parseExpireDate(expiryHeaders.get(0)));
			}
			return true;
		} catch (ApiException ex) {
			switch (ex.getCode()) {
				case 400:
					addError("	EveKit: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (INVALID ATTRIBUTE SELECTOR)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "EveKit: Invalid attribute selector");
					}
					break;
				case 401:
					addError("	EveKit: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (INVALID CREDENTIAL)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "EveKit: Access credential invalid");
					}
					invalid = true;
					break;
				case 403:
					addError("	EveKit: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (INVALID ACCESS MASK)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "EveKit: Not enough access privileges.\r\n(Fix: Add " + getTaskName() + " to the API Key)");
					}
					invalid = true;
					break;
				case 404:
					addError("	EveKit: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (INVALID ACCESS KEY ID)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "EveKit: Access key with the given ID not found");
					}
					invalid = true;
					break;
				default:
					addError("EveKit: " + ex.getMessage(), ex);
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "EveKit: Unknown Error Code: " + ex.getCode());
					}
					break;
			}
			return false;
		} catch (Throwable ex) {
			addError("EveKit Unknown Error: " + ex.getMessage(), ex);
			if (updateTask != null) {
				updateTask.addError(owner.getOwnerName(), "EveKit: Unknown Error: " + ex.getMessage());
			}
			return false;
		}
	}

	public final boolean hasError() {
		return error != null;
	}

	public final String getError() {
		return error;
	}

	public final boolean isInvalid() {
		return invalid;
	}

	protected int getProgressStart() {
		return 0;
	}

	protected int getProgressEnd() {
		return 100;
	}

	protected final void addError(String error, Throwable ex) {
		this.error = error;
		LOG.error(error, ex);
	}

	protected final void addError(String error) {
		this.error = error;
		LOG.error(error);
	}

	protected final CommonApi getCommonApi() {
		return commonApi;
	}

	protected final AccessKeyApi getAccessKeyApi() {
		return accessKeyApi;
	}

	protected abstract void get(EveKitOwner owner, Long at, boolean first) throws ApiException;
	protected abstract String getTaskName();
	protected abstract long getAccessMask();
	protected abstract void setNextUpdate(EveKitOwner owner, Date date);
	protected abstract Date getNextUpdate(EveKitOwner owner);
	protected abstract ApiClient getApiClient();

}
