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
import java.util.Map;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.esi.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.AssetsApi;
import net.troja.eve.esi.api.SsoApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.api.WalletApi;
import net.troja.eve.esi.auth.OAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractEsiGetter {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractEsiGetter.class);

	protected final String DATASOURCE = "tranquility";
	private String error = null;
	private ApiClient client;
	private AssetsApi assetsApi;
	private WalletApi walletApi;
	private UniverseApi universeApi;

	protected AbstractEsiGetter() { }

	protected void load(EsiOwner owner) {
		LOG.info("ESI: " + getTaskName() + " updating:");
		loadAPI(null, owner, true);
	}

	protected void load(UpdateTask updateTask, List<EsiOwner> owners) {
		LOG.info("ESI: " + getTaskName() + " updating:");
		int progress = 0;
		if (updateTask != null) {
			updateTask.resetTaskProgress();
		}
		for (EsiOwner owner : owners) {
			loadAPI(updateTask, owner, false);
			if (updateTask != null) {
				if (updateTask.isCancelled()) {
					return;
				}
				progress++;
				updateTask.setTaskProgress(owners.size(), progress, 0, 100);
			}
		}
	}

	private void loadAPI(UpdateTask updateTask, EsiOwner owner, boolean forceUpdate) {
		error = null;
		createClient(owner);
		try {
			//Check if the Access Mask include this API
			if (!inScope(owner)) {
				addError("	ESI: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (NOT ENOUGH ACCESS PRIVILEGES)");
				if (updateTask != null) {
					updateTask.addError(owner.getOwnerName(), "ESI: Not enough access privileges.\r\n(Fix: Add " + getTaskName() + " to the API Key)");
				}
				return;
			}
			//Check if the Api Key is expired
			if (!forceUpdate && owner.isExpired()) {
				addError("	ESI: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (API KEY EXPIRED)");
				if (updateTask != null) {
					updateTask.addError(owner.getOwnerName(), "ESI: API Key expired");
				}
				return;
			}
			//Check API cache time
			if (!forceUpdate && !Settings.get().isUpdatable(getNextUpdate(owner), false)) {
				addError("	ESI: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (NOT ALLOWED YET)");
				if (updateTask != null) {
					updateTask.addError(owner.getOwnerName(), "ESI: Not allowed yet.\r\n(Fix: Just wait a bit)");
				}
				return;
			}
			get(owner);
			LOG.info("	ESI: " + getTaskName() + " updated for " + owner.getOwnerName());
			Map<String, List<String>> responseHeaders = client.getResponseHeaders();
			if (responseHeaders != null) {
				for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
					if (entry.getKey().toLowerCase().equals("expires")) { //Case insensitive
						List<String> expiryHeaders = entry.getValue();
						if (expiryHeaders != null && !expiryHeaders.isEmpty()) {
							setNextUpdate(owner, Formater.parseExpireDate(expiryHeaders.get(0)));
						}
					}
				}
			}
		} catch (ApiException ex) {
			switch (ex.getCode()) {
				case 403:
					addError("	ESI: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (FORBIDDEN)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "ESI: Forbidden");
					}
					break;
				case 500:
					addError("	ESI: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (INTERNAL SERVER ERROR)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "ESI: Internal server error");
					}
					break;
				case 502:
					addError("	ESI: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (SERVER OFFLINE)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "ESI: Server offline");
					}
					break;
				case 503:
					addError("	ESI: " + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (SERVER OFFLINE)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "ESI: Server offline");
					}
					break;
				default:
					addError("	ESI: " + ex.getMessage(), ex);
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "ESI: Unknown Error Code: " + ex.getCode());
					}
					break;
			}
		} catch (Throwable ex) {
			addError("	ESI: " + ex.getMessage(), ex);
			if (updateTask != null) {
				updateTask.addError(owner.getOwnerName(), "ESI: Unknown Error: " + ex.getMessage());
			}
		}
	}

	protected abstract void get(EsiOwner owner) throws ApiException;
	protected abstract String getTaskName();
	protected abstract void setNextUpdate(EsiOwner owner, Date date);
	protected abstract Date getNextUpdate(EsiOwner owner);
	protected abstract boolean inScope(EsiOwner owner);

	private ApiClient getClient(EsiOwner owner) {
		ApiClient apiClient = new ApiClient();
		OAuth auth = (OAuth) apiClient.getAuthentication("evesso");
		auth.setClientId(owner.getCallbackURL().getA());
		auth.setClientSecret(owner.getCallbackURL().getB());
		auth.setRefreshToken(owner.getRefreshToken());
		return apiClient;
	}

	private void createClient(EsiOwner owner) {
		client = getClient(owner);
		assetsApi = new AssetsApi(client);
		walletApi = new WalletApi(client);
		universeApi = new UniverseApi(client);
	}

	protected SsoApi getSsoApi(EsiOwner owner) {
		client = getClient(owner);
		return new SsoApi(client);
	}

	protected AssetsApi getAssetsApi() {
		return assetsApi;
	}

	protected WalletApi getWalletApi() {
		return  walletApi;
	}

	protected UniverseApi getUniverseApi() {
		return universeApi;
	}

	protected final void addError(String error, Throwable ex) {
		this.error = error;
		LOG.error(error, ex);
	}

	protected final void addError(String error) {
		this.error = error;
		LOG.error(error);
	}

	public final boolean hasError() {
		return error != null;
	}

	public final String getError() {
		return error;
	}
}
