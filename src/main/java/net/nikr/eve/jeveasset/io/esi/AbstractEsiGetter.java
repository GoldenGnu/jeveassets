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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.io.shared.AbstractGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker;
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


public abstract class AbstractEsiGetter extends AbstractGetter<EsiOwner, ApiClient, ApiException> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractEsiGetter.class);

	protected static final String DATASOURCE = "tranquility";
	protected static final String USER_AGENT = System.getProperty("http.agent");
	protected static final int UNIVERSE_BATCH_SIZE = 100;
	protected static final int LOCATIONS_BATCH_SIZE = 100;
	private static final int RETRIES = 1;
	private static Integer errorLimit = null;
	private static Date errorReset = new Date();

	public AbstractEsiGetter(UpdateTask updateTask, EsiOwner owner, boolean forceUpdate, Date nextUpdate, TaskType taskType) {
		super(updateTask, owner, forceUpdate, nextUpdate, taskType, "ESI");
	}

	@Override
	public Void call() throws Exception {
		updateApi(new EsiUpdater(), 0);
		return null;
	}

	@Override
	public <R> R updateApi(Updater<R, ApiClient, ApiException> updater, int retries) {
		//Silently ignore disabled endpoints
		if (!enabled()) {
			logInfo(updater.getStatus(), getTaskName() + " endpoint disabled");
			return null;
		}

		if (!canUpdate(updater.getStatus())) {
			return null;
		}

		ApiClient client = new ApiClient(); //Public
		if (owner != null) { //Auth
			OAuth auth = (OAuth) client.getAuthentication("evesso");
			auth.setRefreshToken(owner.getRefreshToken());
			auth.setClientId(owner.getCallbackURL().getA());
			auth.setClientSecret(owner.getCallbackURL().getB());
		}
		updateErrorReset(); //Update timeframe as needed
		if (getErrorLimit() != null && getErrorLimit() < 10) {
			try {
				Thread.sleep((getErrorReset().getTime() + 1000) - System.currentTimeMillis()); //Wait until the error window is reset
			} catch (InterruptedException ex) {
				//No problem
			}
		}
		try {
			R t = updater.update(client);
			String expiresHeader = getHeader(client.getResponseHeaders(), "expires");
			if (expiresHeader != null) {
				setNextUpdateSafe(Formater.parseExpireDate(expiresHeader));
			}
			handleErrorLimit(client.getResponseHeaders());
			logInfo(updater.getStatus(), "Updated");
			//LOG.info("	ESI: " + getTaskName() + " updated for " + getOwnerName(owner) + " (" + updater.getStatus() + ")");
			return t;
		} catch (ApiException ex) {
			if (ex.getCode() >= 500 && ex.getCode() < 600 //CCP error, Lets try again in a sec
					&& ex.getCode() != 503 //Don't retry when it may be downtime
					&& ex.getCode() != 502 //Don't retry when it may be downtime
					&& retries < RETRIES) { //Retries
				retries++;
				try {
					Thread.sleep(1000); //Wait a sec
				} catch (InterruptedException ex1) {
					//No problem
				}
				logInfo(updater.getStatus(), "Retrying "  + retries + " of " + RETRIES + ":");
				return updateApi(updater, retries);
			} else {
				addError(updater.getStatus(), ex.getCode(), "Error Code: " + ex.getCode(), ex);
			}
		} catch (Throwable ex) {
			addError(updater.getStatus(), ex.getMessage(), "Unknown Error: " + ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	protected boolean invalidAccessPrivileges() {
		return owner != null && !inScope();
	}

	protected abstract void get(ApiClient apiClient) throws ApiException;

	protected abstract boolean inScope();

	protected abstract boolean enabled();

	private void handleErrorLimit(Map<String, List<String>> responseHeaders) {
		if (responseHeaders != null) {
			setErrorLimit(getHeaderInteger(responseHeaders, "x-esi-error-limit-remain"));
			setErrorReset(getHeaderInteger(responseHeaders, "x-esi-error-limit-reset"));
		}
	}

	private synchronized static Integer getErrorLimit() {
		return errorLimit;
	}

	private synchronized static void setErrorLimit(Integer errorLimit) {
		AbstractEsiGetter.errorLimit = errorLimit;
	}

	private synchronized static Date getErrorReset() {
		return errorReset;
	}

	private synchronized static void updateErrorReset() {
		if (errorLimit != null && errorLimit < 100) {
			LOG.warn("Error limit: " + errorLimit + " (resetting in " + errorReset + "sec");
		}
		
		if (new Date().after(AbstractEsiGetter.errorReset)) {
			AbstractEsiGetter.errorReset = new Date(); //New timeframe
			AbstractEsiGetter.errorLimit = null;  //No errors in this timeframe (yet)
		}
	}

	private synchronized static void setErrorReset(Integer errorReset) {
		if (errorLimit != null) {
			AbstractEsiGetter.errorReset = new Date(System.currentTimeMillis() + (errorReset * 1000));
		}
	}

	protected SsoApi getSsoApiAuth(ApiClient apiClient) {
		return new SsoApi(apiClient);
	}

	public MarketApi getMarketApiAuth(ApiClient apiClient) {
		return new MarketApi(apiClient);
	}

	public IndustryApi getIndustryApiAuth(ApiClient apiClient) {
		return new IndustryApi(apiClient);
	}

	protected CharacterApi getCharacterApiAuth(ApiClient apiClient) {
		return new CharacterApi(apiClient);
	}

	protected AssetsApi getAssetsApiAuth(ApiClient apiClient) {
		return new AssetsApi(apiClient);
	}

	protected WalletApi getWalletApiAuth(ApiClient apiClient) {
		return new WalletApi(apiClient);
	}

	protected UniverseApi getUniverseApiAuth(ApiClient apiClient) {
		return new UniverseApi(apiClient);
	}

	public ContractsApi getContractsApiAuth(ApiClient apiClient) {
		return new ContractsApi(apiClient);
	}

	public CorporationApi getCorporationApiAuth(ApiClient apiClient) {
		return new CorporationApi(apiClient);
	}

	public UniverseApi getUniverseApiOpen(ApiClient apiClient) {
		return new UniverseApi();
	}

	public CharacterApi getCharacterApiOpen(ApiClient apiClient) {
		return new CharacterApi();
	}

	public CorporationApi getCorporationApiOpen(ApiClient apiClient) {
		return new CorporationApi();
	}

	public SovereigntyApi getSovereigntyApiOpen(ApiClient apiClient) {
		return new SovereigntyApi();
	}

	protected <K> List<K> updatePages(EsiPagesHandler<K> handler) throws ApiException {
		List<K> values = new ArrayList<K>();
		EsiPageUpdater<K> pageUpdater = new EsiPageUpdater<K>(handler, 1, "1 of ?");
		List<K> returnValue = updateApi(pageUpdater, 0);
		if (returnValue != null) {
			values.addAll(returnValue);
		}
		Integer pages = getHeaderInteger(pageUpdater.getClient().getResponseHeaders(), "x-pages"); //Get pages header
		int count = 2;
		if (pages != null && pages > 1) { //More than one page
			List<EsiPageUpdater<K>> updaters = new ArrayList<EsiPageUpdater<K>>();
			for (int i = 2; i <= pages; i++) { //Get the remaining pages (we already got page 1 so we start at page 2
				updaters.add(new EsiPageUpdater<K>(handler, i, count + " of " + pages));
				count++;
			}
			LOG.info("Starting " + updaters.size() + " pages threads");
			List<Future<List<K>>> futures = ThreadWoker.startReturn(updaters);
			for (Future<List<K>> future : futures) {
				if (future.isDone()) {
					try {
						returnValue = future.get();
						if (returnValue != null) {
							values.addAll(returnValue);
						}
						values.addAll(future.get()); //Get data from ESI
					} catch (InterruptedException ex) {
						//No problem
					} catch (ExecutionException ex) {
						//No problem
					}
				}
			}
		}
		return values;
	}

	public interface EsiPagesHandler<K> {
		public List<K> get(ApiClient apiClient, Integer page) throws ApiException;
	}

	public class EsiPageUpdater<T> implements Callable<List<T>>, Updater<List<T>, ApiClient, ApiException> {

		private final EsiPagesHandler<T> handler;
		private final int page;
		private final String status;
		private ApiClient client;

		public EsiPageUpdater(EsiPagesHandler<T> handler, int page, String status) {
			this.handler = handler;
			this.page = page;
			this.status = status;
		}

		@Override
		public List<T> update(ApiClient client) throws ApiException {
			this.client = client;
			return handler.get(client, page);
		}

		@Override
		public List<T> call() throws Exception {
			return updateApi(this, 0);
		}

		public ApiClient getClient() {
			return client;
		}

		@Override
		public String getStatus() {
			return status;
		}
	}

	public class EsiUpdater implements Updater<Void, ApiClient, ApiException> {

		@Override
		public Void update(ApiClient client) throws ApiException {
			get(client);
			return null;
		}

		@Override
		public String getStatus() {
			return "Completed";
		}
	}
}
