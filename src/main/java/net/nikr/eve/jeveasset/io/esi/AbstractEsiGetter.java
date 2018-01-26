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
package net.nikr.eve.jeveasset.io.esi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.io.shared.AbstractGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker.TaskCancelledException;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.AssetsApi;
import net.troja.eve.esi.api.CharacterApi;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.api.CorporationApi;
import net.troja.eve.esi.api.IndustryApi;
import net.troja.eve.esi.api.LocationApi;
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

	public static final String DATASOURCE = "tranquility";
	public static final String USER_AGENT = System.getProperty("http.agent");
	protected static final int UNIVERSE_BATCH_SIZE = 100;
	protected static final int LOCATIONS_BATCH_SIZE = 100;
	protected static final int DEFAULT_RETRIES = 1;
	private final int maxRetries;
	/**
	 * Errors left in in this error limit time frame (can be null)
	 */
	private static Integer errorLimit = null;
	/**
	 * Date when the error limit will be reset (never null)
	 */
	private static Date errorReset = new Date();


	public AbstractEsiGetter(UpdateTask updateTask, EsiOwner owner, boolean forceUpdate, Date nextUpdate, TaskType taskType, int maxRetries) {
		super(updateTask, owner, forceUpdate, nextUpdate, taskType, "ESI");
		this.maxRetries = maxRetries;
	}

	@Override
	public void run() {
		if (!canUpdate()) {
			return;
		}
		//Check if API key is invalid (still update when editing account AKA forceUpdate)
		if (!isForceUpdate() && owner != null && owner.isInvalid()) {
			addError(null, "REFRESH TOKEN INVALID", "Auth invalid\r\n(Fix: Options > Accounts... > Edit)");
			return;
		}
		try {
			updateApi(new EsiUpdater(maxRetries), 0);
		} catch (ApiException ex) {
			addError(null, ex.getCode(), "Error Code: " + ex.getCode(), ex);
		} catch (TaskCancelledException ex) {
			logInfo(null, "Cancelled");
		} catch (Throwable ex) {
			addError(null, ex.getMessage(), "Unknown Error: " + ex.getMessage(), ex);
		}
	}

	@Override
	public <R> R updateApi(Updater<R, ApiClient, ApiException> updater, int retries) throws ApiException {
		final ApiClient client = new ApiClient(); //Public
		if (owner != null) { //Auth
			OAuth auth = (OAuth) client.getAuthentication("evesso");
			auth.setRefreshToken(owner.getRefreshToken());
			auth.setClientId(owner.getCallbackURL().getA());
			auth.setClientSecret(owner.getCallbackURL().getB());
		}
		checkErrors(); //Update timeframe as needed
		checkCancelled();
		try {
			R t = updater.update(client);
			String expiresHeader = getHeader(client.getResponseHeaders(), "expires");
			if (expiresHeader != null) {
				setNextUpdateSafe(Formater.parseExpireDate(expiresHeader));
			}
			logInfo(updater.getStatus(), "Updated");
			if (owner != null) {
				owner.setInvalid(false);
			}
			return t;
		} catch (ApiException ex) {
			logError(updater.getStatus(), ex.getMessage(), ex.getMessage());
			if (ex.getCode() == 400 && ex.getMessage().toLowerCase().contains("invalid_token")
					&& (ex.getMessage().toLowerCase().contains("the refresh token is expired")
					|| ex.getMessage().toLowerCase().contains("token is no longer valid"))) {
				if (owner != null) {
					owner.setInvalid(true);
				}
				throw ex;
			} else if (ex.getCode() >= 500 && ex.getCode() < 600 //CCP error, Lets try again in a sec
					&& ex.getCode() != 503 //Don't retry when it may be downtime
					&& (ex.getCode() != 502 || ex.getMessage().toLowerCase().contains("no reply within 10 seconds")) //Don't retry when it may be downtime, unless it's "no reply within 10 seconds"
					&& retries < updater.getMaxRetries()) { //Retries
				retries++;
				try {
					Thread.sleep(1000); //Wait a sec
				} catch (InterruptedException ex1) {
					//No problem
				}
				logInfo(updater.getStatus(), "Retrying "  + retries + " of " + updater.getMaxRetries() + ":");
				return updateApi(updater, retries);
			} else {
				throw ex;
			}
		} finally {
			setErrorLimit(client.getResponseHeaders()); //Always save error limit header
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
		return owner != null && !inScope();
	}

	protected abstract void get(ApiClient apiClient) throws ApiException;

	protected abstract boolean inScope();

	private void setErrorLimit(Map<String, List<String>> responseHeaders) {
		if (responseHeaders != null) {
			setErrorLimit(getHeaderInteger(responseHeaders, "x-esi-error-limit-remain"));
			setErrorReset(getHeaderInteger(responseHeaders, "x-esi-error-limit-reset"));
		}
	}

	private synchronized static void setErrorLimit(Integer errorLimit) {
		if (AbstractEsiGetter.errorLimit != null && errorLimit != null) {
			AbstractEsiGetter.errorLimit = Math.min(AbstractEsiGetter.errorLimit, errorLimit);
		} else {
			AbstractEsiGetter.errorLimit = errorLimit;
		}
	}

	private synchronized static void setErrorReset(Integer errorReset) {
		if (errorReset != null) {
			AbstractEsiGetter.errorReset = new Date(System.currentTimeMillis() + (errorReset * 1000));
		}
	}

	private synchronized static void checkErrors() {
		if (errorLimit != null && errorLimit < 10) { //Error limit reached
			try {
				long wait = (errorReset.getTime() + 1000) - System.currentTimeMillis();
				LOG.warn("Error limit reached waiting: " + Formater.milliseconds(wait, false, false));
				if (wait > 0) { //Negative values throws an Exception
					Thread.sleep(wait); //Wait until the error window is reset
				}
				//Reset
				AbstractEsiGetter.errorReset = new Date(); //New timeframe
				AbstractEsiGetter.errorLimit = null;  //No errors in this timeframe (yet)
			} catch (InterruptedException ex) {
				//No problem
			}
		} else if (errorLimit != null && errorLimit < 100) { //At least one error
			LOG.warn("Error limit: " + errorLimit);
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

	public LocationApi getLocationApiAuth(ApiClient apiClient) {
		return new LocationApi(apiClient);
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

	protected final <K, V> Map<K, V> updateListSlow(Collection<K> list, boolean trackProgress, int maxRetries, ListHandlerSlow<K, V> handler) throws ApiException {
		Map<K, V> values = new HashMap<K, V>();
		int count = 1;
		for (K k : list) {
			ListUpdater<K, V> listUpdater = new ListUpdater<K, V>(handler, k, count + " of " + list.size(), maxRetries);
			try {
				if (trackProgress) {
					setProgress(list.size(), count, 0, 100);
				}
				Map<K, V> returnValue = listUpdater.go();
				if (returnValue != null) {
					values.putAll(returnValue);
				}
			} catch (ApiException ex) {
				handler.handle(ex, k);
			}
			count++;
		}
		return values;
	}

	protected abstract class ListHandlerSlow<K, V> extends ListHandler<K, V> {
		protected abstract void handle(ApiException ex, K k) throws ApiException;
	}

	protected <K> List<K> updatePages(int maxRetries, EsiPagesHandler<K> handler) throws ApiException {
		List<K> values = new ArrayList<K>();
		EsiPageUpdater<K> pageUpdater = new EsiPageUpdater<K>(handler, 1, "1 of ?", maxRetries);
		List<K> returnValue = updateApi(pageUpdater, 0);
		if (returnValue != null) {
			values.addAll(returnValue);
		}
		Integer pages = getHeaderInteger(pageUpdater.getClient().getResponseHeaders(), "x-pages"); //Get pages header
		int count = 2;
		if (pages != null && pages > 1) { //More than one page
			List<EsiPageUpdater<K>> updaters = new ArrayList<EsiPageUpdater<K>>();
			for (int i = 2; i <= pages; i++) { //Get the remaining pages (we already got page 1 so we start at page 2
				updaters.add(new EsiPageUpdater<K>(handler, i, count + " of " + pages, maxRetries));
				count++;
			}
			LOG.info("Starting " + updaters.size() + " pages threads");
			try {
				List<Future<List<K>>> futures = startSubThreads(updaters);
				for (Future<List<K>> future : futures) {
					if (future.isDone()) {
						returnValue = future.get(); //Get data from ESI
						if (returnValue != null) {
							values.addAll(returnValue);
						}
					}
				}
			} catch (InterruptedException | ExecutionException ex) {
				throwApiException(ex);
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
		private final int maxRetries;

		public EsiPageUpdater(EsiPagesHandler<T> handler, int page, String status, int maxRetries) {
			this.handler = handler;
			this.page = page;
			this.status = status;
			this.maxRetries = maxRetries;
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

		@Override
		public int getMaxRetries() {
			return maxRetries;
		}
	}

	public class EsiUpdater implements Updater<Void, ApiClient, ApiException> {

		private final int maxRetries;

		public EsiUpdater(int maxRetries) {
			this.maxRetries = maxRetries;
		}
		
		@Override
		public Void update(ApiClient client) throws ApiException {
			get(client);
			return null;
		}

		@Override
		public String getStatus() {
			return "Completed";
		}

		@Override
		public int getMaxRetries() {
			return maxRetries;
		}
	}
}
