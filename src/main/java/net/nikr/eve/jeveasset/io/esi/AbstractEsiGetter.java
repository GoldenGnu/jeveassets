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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.AssetsApi;
import net.troja.eve.esi.api.CharacterApi;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.api.CorporationApi;
import net.troja.eve.esi.api.IndustryApi;
import net.troja.eve.esi.api.MarketApi;
import net.troja.eve.esi.api.SovereigntyApi;
import net.troja.eve.esi.api.SsoApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.api.WalletApi;
import net.troja.eve.esi.auth.OAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractEsiGetter {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractEsiGetter.class);

	protected final String DATASOURCE = "tranquility";
	protected final int UNIVERSE_BATCH_SIZE = 100;
	private String error = null;
	private final ApiClient clientAuth;
	private final AssetsApi assetsApiAuth;
	private final WalletApi walletApiAuth;
	private final UniverseApi universeApiAuth;
	private final CharacterApi characterApiAuth;
	private final IndustryApi industryApiAuth;
	private final MarketApi marketApiAuth;
	private final ContractsApi contractsApiAuth;
	private final SsoApi ssoApiAuth;
	private final ApiClient clientOpen;
	private final UniverseApi universeApiOpen;
	private final SovereigntyApi sovereigntyApiOpen;
	private final CharacterApi characterApiOpen;
	private final CorporationApi corporationApiOpen;

	protected AbstractEsiGetter() {
		clientAuth = new ApiClient();
		assetsApiAuth = new AssetsApi(clientAuth);
		walletApiAuth = new WalletApi(clientAuth);
		universeApiAuth = new UniverseApi(clientAuth);
		characterApiAuth = new CharacterApi(clientAuth);
		industryApiAuth = new IndustryApi(clientAuth);
		marketApiAuth = new MarketApi(clientAuth);
		contractsApiAuth = new ContractsApi(clientAuth);
		ssoApiAuth = new SsoApi(clientAuth);
		clientOpen = new ApiClient();
		universeApiOpen = new UniverseApi(clientOpen);
		sovereigntyApiOpen = new SovereigntyApi(clientOpen);
		characterApiOpen = new CharacterApi(clientOpen);
		corporationApiOpen = new CorporationApi(clientOpen);
	}

	protected void load(UpdateTask updateTask) {
		LOG.info("ESI: " + getTaskName() + " updating:");
		loadAPI(updateTask, null, false);
	}

	protected void load(EsiOwner owner) {
		LOG.info("ESI: " + getTaskName() + " updating:");
		loadAPI(null, owner, true);
	}

	protected void load(UpdateTask updateTask, List<EsiOwner> owners) {
		LOG.info("ESI: " + getTaskName() + " updating:");
		int progress = 0;
		if (updateTask != null && getProgressStart() == 0) {
			updateTask.resetTaskProgress();
		}
		for (EsiOwner owner : owners) {
			if (owner.isShowOwner()) { //Ignore not shown owners
				loadAPI(updateTask, owner, false);
			}
			if (updateTask != null) {
				if (updateTask.isCancelled()) {
					updateTask.addError(owner.getOwnerName(), "ESI: Cancelled");
				}
				progress++;
				updateTask.setTaskProgress(owners.size(), progress, getProgressStart(), getProgressEnd());
			}
		}
	}

	protected int getProgressStart() {
		return 0;
	}

	protected int getProgressEnd() {
		return 100;
	}

	private void loadAPI(UpdateTask updateTask, EsiOwner owner, boolean forceUpdate) {
		error = null;
		ApiClient client = client(owner);
		try {
			//Siliently ignore disabled scopes
			if (!enabled(owner)) {
				return;
			}
			//Check if the Access Mask include this API
			if (owner != null && !inScope(owner)) {
				addError("	ESI: " + getTaskName() + " failed to update for: " + getOwnerName(owner) + " (NOT ENOUGH ACCESS PRIVILEGES)");
				if (updateTask != null) {
					updateTask.addError(getOwnerName(owner), "ESI: Not enough access privileges.\r\n(Fix: Add " + getTaskName() + " to the API Key)");
				}
				return;
			}
			//Check API cache time
			if (!forceUpdate && owner != null && !Settings.get().isUpdatable(getNextUpdate(owner), false)) {
				addError("	ESI: " + getTaskName() + " failed to update for: " + getOwnerName(owner) + " (NOT ALLOWED YET)");
				if (updateTask != null) {
					updateTask.addError(getOwnerName(owner), "ESI: Not allowed yet.\r\n(Fix: Just wait a bit)");
				}
				return;
			}
			get(owner);
			LOG.info("	ESI: " + getTaskName() + " updated for " + getOwnerName(owner));
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
			handleErrorLimit(client);
			switch (ex.getCode()) {
				case 403:
					addError("	ESI: " + getTaskName() + " failed to update for: " + getOwnerName(owner) + " (403)");
					if (updateTask != null) {
						updateTask.addError(getOwnerName(owner), "ESI: Forbidden");
					}
					break;
				case 500:
					addError("	ESI: " + getTaskName() + " failed to update for: " + getOwnerName(owner) + " (500)");
					if (updateTask != null) {
						updateTask.addError(getOwnerName(owner), "ESI: Internal server error");
					}
					break;
				case 502:
					addError("	ESI: " + getTaskName() + " failed to update for: " + getOwnerName(owner) + " (502)");
					if (updateTask != null) {
						updateTask.addError(getOwnerName(owner), "ESI: Server offline");
					}
					break;
				case 503:
					addError("	ESI: " + getTaskName() + " failed to update for: " + getOwnerName(owner) + " (503)");
					if (updateTask != null) {
						updateTask.addError(getOwnerName(owner), "ESI: Server offline");
					}
					break;
				default:
					addError("ESI: " + ex.getMessage(), ex);
					if (updateTask != null) {
						updateTask.addError(getOwnerName(owner), "ESI: Unknown Error Code: " + ex.getCode());
					}
					break;
			}
		} catch (Throwable ex) {
			addError("	ESI: " + ex.getMessage(), ex);
			if (updateTask != null) {
				updateTask.addError(getOwnerName(owner), "ESI: Unknown Error: " + ex.getMessage());
			}
		}
	}

	protected <T> List<T> getList(EsiOwner owner, Set<Long> existing, EsiListHandler<T> handler) throws ApiException {
		List<T> list = new ArrayList<T>();
		Long fromID = null;
		boolean run = true;
		while (run) {
			List<T> result = handler.get(owner, fromID); //Get data from ESI
			if (result.isEmpty()) { //Nothing returned: we're done
				break; //Stop updating
			}

			list.addAll(result); //Add new

			Long lastID = handler.getID(result.get(result.size() - 1)); //Get the last ID
			if (lastID.equals(fromID)) { //ID is the same as on last update: we're done
				break; //Stop updating
			}
			fromID = lastID; //Set ID for next update

			for (T t : result) { //Search for existing data
				if (existing.contains(handler.getID(t))) { //Found existing data
					run = false; //Stop updating
					break; //no need to continue
				}
			}
		}
		return list;
	}

	protected interface EsiListHandler<T> {
		public List<T> get(EsiOwner owner, Long fromID) throws ApiException;
		public Long getID(T response);
	}

	protected abstract void get(EsiOwner owner) throws ApiException;

	protected abstract String getTaskName();

	protected abstract void setNextUpdate(EsiOwner owner, Date date);

	protected abstract Date getNextUpdate(EsiOwner owner);

	protected abstract boolean inScope(EsiOwner owner);

	protected abstract boolean enabled(EsiOwner owner);

	private String getOwnerName(EsiOwner owner) {
		if (owner != null) {
			return owner.getOwnerName();
		} else {
			return Program.PROGRAM_NAME;
		}
	}

	private void handleErrorLimit(ApiClient client) {
		Map<String, List<String>> responseHeaders = client.getResponseHeaders();
		if (responseHeaders != null) {
			Integer errorLimit = null;
			Integer errorReset = null;
			for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
				if (entry.getKey().toLowerCase().equals("x-esi-error-limit-remain")) { //Case insensitive
					List<String> errorLimitHeaders = entry.getValue();
					if (errorLimitHeaders != null && !errorLimitHeaders.isEmpty()) {
						try {
							errorLimit = Integer.valueOf(errorLimitHeaders.get(0));
						} catch (NumberFormatException ex) {
							//No problem
						}
					}
				}
				if (entry.getKey().toLowerCase().equals("x-esi-error-limit-reset")) { //Case insensitive
					List<String> errorLimitHeaders = entry.getValue();
					if (errorLimitHeaders != null && !errorLimitHeaders.isEmpty()) {
						try {
							errorReset = Integer.valueOf(errorLimitHeaders.get(0));
						} catch (NumberFormatException ex) {
							//No problem
						}
					}
				}
			}
			if (errorLimit != null && errorReset != null) {
				if (errorLimit < 10) {
					try {
						Thread.sleep((errorReset + 1) * 1000); //Wait until the error window is reset
					} catch (InterruptedException ex) {
						//No problem
					}
				}
			}
		}
	}

	private ApiClient client(EsiOwner owner) {
		if (owner == null) {
			return clientOpen;
		}
		OAuth auth = (OAuth) clientAuth.getAuthentication("evesso");
		auth.setRefreshToken(owner.getRefreshToken());
		auth.setClientId(owner.getCallbackURL().getA());
		auth.setClientSecret(owner.getCallbackURL().getB());
		return clientAuth;
	}

	protected <T> List<List<T>> splitList(Collection<T> list, final int L) {
		return splitList(new ArrayList<T>(list), L);
	}

	private <T> List<List<T>> splitList(List<T> list, final int L) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int N = list.size();
		for (int i = 0; i < N; i += L) {
			parts.add(new ArrayList<T>(
					list.subList(i, Math.min(N, i + L)))
			);
		}
		return parts;
	}

	protected boolean oneByOneNameRange(long id) {
		return (id >= 100000000L && id <= 2099999999L) || (id >= 2100000000 && id <= 2112000000);
	}

	protected SsoApi getSsoApiAuth() {
		return ssoApiAuth;
	}

	public MarketApi getMarketApiAuth() {
		return marketApiAuth;
	}

	public IndustryApi getIndustryApiAuth() {
		return industryApiAuth;
	}

	protected CharacterApi getCharacterApiAuth() {
		return characterApiAuth;
	}

	protected AssetsApi getAssetsApiAuth() {
		return assetsApiAuth;
	}

	protected WalletApi getWalletApiAuth() {
		return walletApiAuth;
	}

	protected UniverseApi getUniverseApiAuth() {
		return universeApiAuth;
	}

	public ContractsApi getContractsApiAuth() {
		return contractsApiAuth;
	}

	public UniverseApi getUniverseApiOpen() {
		return universeApiOpen;
	}

	public CharacterApi getCharacterApiOpen() {
		return characterApiOpen;
	}

	public CorporationApi getCorporationApiOpen() {
		return corporationApiOpen;
	}

	public SovereigntyApi getSovereigntyApiOpen() {
		return sovereigntyApiOpen;
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
