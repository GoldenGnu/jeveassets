/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.io.shared.AbstractGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker.TaskCancelledException;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiClientBuilder;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.api.AssetsApi;
import net.troja.eve.esi.api.BookmarksApi;
import net.troja.eve.esi.api.CharacterApi;
import net.troja.eve.esi.api.ClonesApi;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.api.CorporationApi;
import net.troja.eve.esi.api.FactionWarfareApi;
import net.troja.eve.esi.api.IndustryApi;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.api.MarketApi;
import net.troja.eve.esi.api.PlanetaryInteractionApi;
import net.troja.eve.esi.api.SkillsApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.api.UserInterfaceApi;
import net.troja.eve.esi.api.WalletApi;
import net.troja.eve.esi.auth.OAuth;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractEsiGetter extends AbstractGetter<EsiOwner> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractEsiGetter.class);

	private static OkHttpClient OkHttpClient;
	private static final ApiClient PUBLIC_CLIENT = new ApiClientBuilder().okHttpClient(getHttpClient()).build();
	private static final UniverseApi UNIVERSE_API = new UniverseApi(PUBLIC_CLIENT);
	private static final CharacterApi CHARACTER_API = new CharacterApi(PUBLIC_CLIENT);
	private static final ContractsApi CONTRACTS_API = new ContractsApi(PUBLIC_CLIENT);
	private static final MarketApi MARKET_API = new MarketApi(PUBLIC_CLIENT);
	private static final IndustryApi INDUSTRY_API = new IndustryApi(PUBLIC_CLIENT);
	private static final FactionWarfareApi FACTION_WARFARE_API = new FactionWarfareApi(PUBLIC_CLIENT);
	public static final UserInterfaceApi USER_INTERFACE_API = new UserInterfaceApi(PUBLIC_CLIENT);
	public static final String DATASOURCE = "tranquility";
	protected static final int UNIVERSE_BATCH_SIZE = 100;
	protected static final int LOCATIONS_BATCH_SIZE = 100;
	protected static final int DEFAULT_RETRIES = 3;
	/**
	 * Errors left in in this error limit time frame (can be null)
	 */
	private static Integer errorLimit = null;
	/**
	 * Date when the error limit will be reset (never null)
	 */
	private static Date errorReset = new Date();


	public AbstractEsiGetter(UpdateTask updateTask, EsiOwner owner, boolean forceUpdate, Date nextUpdate, TaskType taskType) {
		super(updateTask, owner, forceUpdate(owner, taskType, forceUpdate), nextUpdate, taskType, "ESI");
	}

	public static OkHttpClient getHttpClient() {
		if (OkHttpClient == null || OkHttpClient.interceptors().size() > 100 || OkHttpClient.networkInterceptors().size() > 100) {
			OkHttpClient = new OkHttpClient.Builder()
					.readTimeout(20, TimeUnit.SECONDS)
					.writeTimeout(20, TimeUnit.SECONDS)
					.connectTimeout(20, TimeUnit.SECONDS).build();
		}
		return OkHttpClient;
	}

	private static boolean forceUpdate(EsiOwner owner, TaskType taskType, boolean forceUpdate) {
		if (forceUpdate) {
			return true;
		}
		if (taskType == TaskType.OWNER && owner != null) {
			OAuth auth = (OAuth) owner.getApiClient().getAuthentication("evesso");
			return auth.getJWT() == null; //Force update of old tokens
		}
		return false;
	}

	protected abstract RolesEnum[] getRequiredRoles();

	@Override
	public void run() {
		if (!canUpdate()) {
			//Add warning for missing corporation roles
			if (owner != null && !haveAccess()) {
				RolesEnum[] requiredRoles = getRequiredRoles();
				if (owner.isCorporation() && requiredRoles != null && requiredRoles.length > 0) {
					StringBuilder builder = new StringBuilder();
					builder.append("Require corporation role:\r\n");
					boolean first = true;
					for (RolesEnum role : requiredRoles) {
						if (first) {
							first = false;
						} else {
							builder.append(", ");
						}
						builder.append(role.getValue().replace("_", " "));
					}
					addWarning("MISSING CORPORATION ROLE", builder.toString());
				}
			}
			return;
		}
		//Check if API key is invalid (still update when editing account AKA forceUpdate)
		if (!isForceUpdate() && owner != null && owner.isInvalid()) {
			addError("INVALID AUTHORIZATION (OWNER)", "Account Authorization Invalid\r\n(Fix: Options > Accounts... > Edit the account)");
			return;
		}
		try {
			update();
		} catch (ApiException ex) {
			addError("Error Code: " + ex.getCode() + "\r\n" + ex.getResponseBody(), "Error Code: " + ex.getCode() + "\r\n" + ex.getResponseBody(), ex);
		} catch (TaskCancelledException ex) {
			logInfo(null, "Cancelled");
		} catch (InvalidAuthException ex) {
			addError("INVALID AUTHORIZATION (API)", "Account Authorization Invalid\r\n(Fix: Options > Accounts... > Edit the account)");
		} catch (Exception ex) {
			addError(ex.getMessage(), "Unknown Error: " + ex.getMessage(), ex);
		}
	}

	private <R> R updateApi(Updater<ApiResponse<R>, ApiException> updater) throws ApiException {
		return updateApi(updater, 0);
	}

	private <R> R updateApi(Updater<ApiResponse<R>, ApiException> updater, int retries) throws ApiException {
		checkErrors(); //Update timeframe as needed
		checkCancelled();
		try {
			ApiResponse<R> apiResponse = updater.update();
			if (apiResponse == null) {
				return null;
			}
			handleHeaders(apiResponse);
			logInfo(updater.getStatus(), "Updated");
			if (owner != null) {
				owner.setInvalid(false);
			}
			return apiResponse.getData();
		} catch (ApiException ex) {
			handleHeaders(ex);
			logWarn(ex.getResponseBody(), ex.getMessage());
			if (ex.getCode() == 401 && ex.getResponseBody().toLowerCase().contains("error") && ex.getResponseBody().toLowerCase().contains("authorization not provided")) {
				if (owner != null) {
					owner.setInvalid(true);
				}
				throw new InvalidAuthException();
			} else if ((ex.getCode() >= 500 && ex.getCode() < 600 //CCP error, Lets try again in a sec
					|| ex.getCode() == 0) //Other error, Lets try again in a sec
					&& ex.getCode() != 503 //Don't retry when it may be downtime
					&& (ex.getCode() != 502 || (ex.getResponseBody().toLowerCase().contains("no reply within 10 seconds") || ex.getResponseBody().toLowerCase().startsWith("<html>"))) //Don't retry when it may be downtime, unless it's "no reply within 10 seconds" or html body
					&& retries < updater.getMaxRetries()) { //Retries
				retries++;
				try {
					Thread.sleep(1000); //Wait a sec
				} catch (InterruptedException ex1) {
					//No problem
				}
				logInfo(updater.getStatus(), "Retrying " + retries + " of " + updater.getMaxRetries() + ":");
				return updateApi(updater, retries);
			} else {
				throw ex;
			}
		}
	}

	protected void handleHeaders(ApiException apiException) {
		setExpires(apiException.getResponseHeaders());
		setErrorLimit(apiException.getResponseHeaders()); //Always save error limit header
	}

	private void handleHeaders(ApiResponse<?> apiResponse) {
		setExpires(apiResponse.getHeaders());
		setErrorLimit(apiResponse.getHeaders()); //Always save error limit header
	}

	protected abstract void update() throws ApiException;

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
			AbstractEsiGetter.errorReset = new Date(System.currentTimeMillis() + (errorReset * 1000L));
		}
	}

	private synchronized static void checkErrors() {
		if (errorLimit != null && errorLimit < 10) { //Error limit reached
			try {
				long wait = (errorReset.getTime() + 1000) - System.currentTimeMillis();
				LOG.warn("Error limit reached waiting: " + Formatter.milliseconds(wait, false, false));
				if (wait > 0) { //Negative values throws an Exception
					Thread.sleep(wait); //Wait until the error window is reset
				}
				//Reset
				AbstractEsiGetter.errorReset = new Date(); //New timeframe
				AbstractEsiGetter.errorLimit = null; //No errors in this timeframe (yet)
			} catch (InterruptedException ex) {
				//No problem
			}
		} else if (errorLimit != null && errorLimit < 100) { //At least one error
			LOG.warn("Error limit: " + errorLimit);
		}
	}

	public MarketApi getMarketApiAuth() {
		return owner.getMarketApiAuth();
 	}

	public IndustryApi getIndustryApiAuth() {
		return owner.getIndustryApiAuth();
 	}

	protected CharacterApi getCharacterApiAuth() {
		return owner.getCharacterApiAuth();
 	}

	protected AssetsApi getAssetsApiAuth() {
		return owner.getAssetsApiAuth();
 	}

	protected WalletApi getWalletApiAuth() {
		return owner.getWalletApiAuth();
 	}

	protected UniverseApi getUniverseApiAuth() {
		return owner.getUniverseApiAuth();
 	}
	
	public ClonesApi getClonesApiAuth() {
		return owner.getClonesApiAuth();
	}

	public ContractsApi getContractsApiAuth() {
		return owner.getContractsApiAuth();
 	}

	public CorporationApi getCorporationApiAuth() {
		return owner.getCorporationApiAuth();
 	}

	public LocationApi getLocationApiAuth() {
		return owner.getLocationApiAuth();
 	}

	public BookmarksApi getBookmarksApiAuth() {
		return owner.getBookmarksApiAuth();
 	}

	public PlanetaryInteractionApi getPlanetaryInteractionApiAuth() {
		return owner.getPlanetaryInteractionApiAuth();
 	}

	public SkillsApi getSkillsApiAuth() {
		return owner.getSkillsApi();
 	}

	public UniverseApi getUniverseApiOpen() {
		return UNIVERSE_API;
 	}

	public CharacterApi getCharacterApiOpen() {
		return CHARACTER_API;
 	}

	public static MarketApi getMarketApiOpen() {
		return MARKET_API;
	}

	public static FactionWarfareApi getFactionWarfareApiOpen() {
		return FACTION_WARFARE_API;
	}

	public static ContractsApi getContractsApiOpen() {
		return CONTRACTS_API;
	}

	public static IndustryApi getIndustryApiOpen() {
		return INDUSTRY_API;
	}

	protected final <K, V> Map<K, V> updateListSlow(Collection<K> list, boolean trackProgress, int maxRetries, ListHandlerSlow<K, V> handler) throws ApiException {
		Map<K, V> values = new HashMap<>();
		int count = 1;
		for (K k : list) {
			ListUpdater<K, V> listUpdater = new ListUpdater<>(handler, k, count + " of " + list.size(), maxRetries);
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

	protected final <K, V> Map<K, V> updateList(Collection<K> list, int maxRetries, ListHandler<K, V> handler) throws ApiException {
		Map<K, V> values = new HashMap<>();
		List<ListUpdater<K, V>> updaters = new ArrayList<>();
		int count = 1;
		for (K k : list) {
			updaters.add(new ListUpdater<>(handler, k, count + " of " + list.size(), maxRetries));
			count++;
		}
		LOG.info("Starting " + updaters.size() + " list threads");
		try {
			List<Future<Map<K, V>>> futures = startSubThreads(updaters);
			for (Future<Map<K, V>> future : futures) {
				Map<K, V> returnValue = future.get();
				if (returnValue != null) {
					values.putAll(returnValue);
				}
			}
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		} catch (ExecutionException ex) {
			ThreadWoker.throwExecutionException(ApiException.class, ex);
		}
		return values;
	}

	protected abstract class ListHandler<K, V> {
		protected abstract ApiResponse<V> get(K k) throws ApiException;
	}

	protected class ListUpdater<K, V> implements Updater<ApiResponse<V>, ApiException>, Callable<Map<K, V>> {

		private final ListHandler<K, V> handler;
		private final K k;
		private final String status;
		private final int maxRetries;

		public ListUpdater(ListHandler<K, V> handler, K k, String status, int maxRetries) {
			this.handler = handler;
			this.k = k;
			this.status = status;
			this.maxRetries = maxRetries;
		}

		@Override
		public ApiResponse<V> update() throws ApiException {
			return handler.get(k);
		}

		public Map<K, V> go() throws ApiException {
			V v = updateApi(this);
			if (v != null) {
				Map<K, V> map = new HashMap<>();
				map.put(k, v);
				return map;
			} else {
				return null;
			}
		}

		@Override
		public Map<K, V> call() throws Exception {
			return go();
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

	protected final <K, V> List<V> updatePagedList(Collection<K> list, PagedListHandler<K, V> handler) throws ApiException {
		List<Callable<List<V>>> updaters = new ArrayList<>();
		for (K k : list) {
			updaters.add(new Callable<List<V>>() {
				@Override
				public List<V> call() throws Exception {
					return handler.get(k);
				}
			}
			);
		}
		LOG.info("Starting " + updaters.size() + " list threads");
		List<V> values = new ArrayList<>();
		try {
			List<Future<List<V>>> futures = startSubThreads(updaters);
			for (Future<List<V>> future : futures) {
				List<V> returnValue = future.get();
				if (returnValue != null) {
					values.addAll(returnValue);
				}
			}
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		} catch (ExecutionException ex) {
			ThreadWoker.throwExecutionException(ApiException.class, ex);
		}
		return values;
	}

	protected abstract class PagedListHandler<K, V> {
		protected abstract List<V> get(K k) throws ApiException;
	}

	protected final <K, V> Map<K, List<V>> updatePagedMap(Collection<K> list, PagedListHandler<K, V> handler) throws ApiException {
		List<Callable<Map<K, List<V>>>> updaters = new ArrayList<>();
		for (K k : list) {
			updaters.add(new Callable<Map<K, List<V>>>() {
				@Override
				public Map<K, List<V>> call() throws Exception {
					List<V> v = handler.get(k);
					if (v != null) {
						Map<K, List<V>> map = new HashMap<>();
						map.put(k, v);
						return map;
					} else {
						return null;
					}
				}
			}
			);
		}
		LOG.info("Starting " + updaters.size() + " list threads");
		Map<K, List<V>> values = new HashMap<>();
		try {
			List<Future<Map<K, List<V>>>> futures = startSubThreads(updaters);
			for (Future<Map<K, List<V>>> future : futures) {
				Map<K, List<V>> returnValue = future.get();
				if (returnValue != null) {
					values.putAll(returnValue);
				}
			}
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		} catch (ExecutionException ex) {
			ThreadWoker.throwExecutionException(ApiException.class, ex);
		}
		return values;
	}

	protected final <K> List<K> updateIDs(Set<Long> existing, int maxRetries, IDsHandler<K> handler) throws ApiException {
		List<K> list = new ArrayList<>();
		Long fromID = null;
		boolean run = true;
		int count = 0;
		while (run) {
			count++;
			List<K> result;
			result = updateApi(new IdUpdater<>(handler, fromID, count + " of ?", maxRetries));
			if (result == null || result.isEmpty()) { //Nothing returned: we're done
				break; //Stop updating
			}

			list.addAll(result); //Add new

			Long lastID = handler.getID(result.get(result.size() - 1)); //Get the last ID
			if (lastID.equals(fromID)) { //ID is the same as on last update: we're done
				break; //Stop updating
			}
			fromID = lastID; //Set ID for next update

			for (K t : result) { //Search for existing data
				if (existing.contains(handler.getID(t))) { //Found existing data
					run = false; //Stop updating
					break; //no need to continue
				}
			}
		}
		return list;
	}

	public abstract class IDsHandler<K> {
		protected abstract ApiResponse<List<K>> get(Long fromID) throws ApiException;
		protected abstract Long getID(K response);
	}

	public class IdUpdater<K> implements Updater<ApiResponse<List<K>>, ApiException> {

		private final IDsHandler<K> handler;
		private final Long fromID;
		private final String status;
		private final int maxRetries;

		public IdUpdater(IDsHandler<K> handler, Long fromID, String status, int maxRetries) {
			this.handler = handler;
			this.fromID = fromID;
			this.status = status;
			this.maxRetries = maxRetries;
		}

		@Override
		public ApiResponse<List<K>> update() throws ApiException {
			return handler.get(fromID);
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

	protected <K> List<K> updatePages(int maxRetries, EsiPagesHandler<K> handler) throws ApiException {
		List<K> values = new ArrayList<>();
		EsiPageUpdater<K> pageUpdater = new EsiPageUpdater<>(handler, 1, "1 of ?", maxRetries);
		List<K> returnValue = updateApi(pageUpdater);
		if (returnValue != null) {
			values.addAll(returnValue);
		}
		Integer pages = getHeaderInteger(pageUpdater.getResponse().getHeaders(), "x-pages"); //Get pages header
		int count = 2;
		if (pages != null && pages > 1) { //More than one page
			List<EsiPageUpdater<K>> updaters = new ArrayList<>();
			for (int i = 2; i <= pages; i++) { //Get the remaining pages (we already got page 1 so we start at page 2
				updaters.add(new EsiPageUpdater<>(handler, i, count + " of " + pages, maxRetries));
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
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			} catch (ExecutionException ex) {
				ThreadWoker.throwExecutionException(ApiException.class, ex);
			}
		}
		return values;
	}

	public interface EsiPagesHandler<K> {
		public ApiResponse<List<K>> get(Integer page) throws ApiException;
	}

	public class EsiPageUpdater<T> implements Callable<List<T>>, Updater<ApiResponse<List<T>>, ApiException> {

		private final EsiPagesHandler<T> handler;
		private final int page;
		private final String status;
		private final int maxRetries;
		private ApiResponse<List<T>> response;

		public EsiPageUpdater(EsiPagesHandler<T> handler, int page, String status, int maxRetries) {
			this.handler = handler;
			this.page = page;
			this.status = status;
			this.maxRetries = maxRetries;
		}

		@Override
		public ApiResponse<List<T>> update() throws ApiException {
			response = handler.get(page);
			return response;
		}

		@Override
		public List<T> call() throws Exception {
			return updateApi(this);
		}

		public ApiResponse<List<T>> getResponse() {
			return response;
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

	protected <K> K update(int maxRetries, EsiHandler<K> handler) throws ApiException {
		EsiUpdater<K> esiUpdater = new EsiUpdater<>(maxRetries, handler);
		return esiUpdater.go();
	}

	public interface EsiHandler<K> {
		public ApiResponse<K> get() throws ApiException;
	}

	public class EsiUpdater<T> implements Updater<ApiResponse<T>, ApiException> {

		private final int maxRetries;
		private final EsiHandler<T> handler;

		public EsiUpdater(int maxRetries, EsiHandler<T> handler) {
			this.maxRetries = maxRetries;
			this.handler = handler;
		}

		public T go() throws ApiException {
			return updateApi(this);
		}

		@Override
		public ApiResponse<T> update() throws ApiException {
			return handler.get();
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

	private static class InvalidAuthException extends RuntimeException {

	}
}
