/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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

import enterprises.orbital.evekit.client.ApiClient;
import enterprises.orbital.evekit.client.ApiException;
import enterprises.orbital.evekit.client.ApiResponse;
import enterprises.orbital.evekit.client.api.AccessKeyApi;
import enterprises.orbital.evekit.client.api.CharacterApi;
import enterprises.orbital.evekit.client.api.CommonApi;
import enterprises.orbital.evekit.client.api.CorporationApi;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker.TaskCancelledException;


public abstract class AbstractEveKitGetter extends AbstractGetter<EveKitOwner> {

	
	private static final ApiClient API_CLIENT = new ApiClient();
	static {
		API_CLIENT.setUserAgent(System.getProperty("http.agent"));
		API_CLIENT.setConnectTimeout(180000);
		API_CLIENT.setWriteTimeout(180000);
		API_CLIENT.setReadTimeout(180000);
	}
	private static final CommonApi COMMON_API = new CommonApi(API_CLIENT);
	private static final CharacterApi CHARACTER_API = new CharacterApi(API_CLIENT);
	private static final CorporationApi CORPORATION_API = new CorporationApi(API_CLIENT);
	private static final AccessKeyApi ACCESS_KEY_API = new AccessKeyApi(API_CLIENT);
	protected static final int DEFAULT_RETRIES = 3;
	private boolean invalid = false;
	private Date lifeStart = null;
	private final boolean first;
	private final Long at;

	public AbstractEveKitGetter(UpdateTask updateTask, EveKitOwner owner, boolean forceUpdate, Date nextUpdate, TaskType taskType, boolean first, Long at) {
		super(updateTask, owner, forceUpdate, nextUpdate, taskType, "EveKit");
		this.first = first;
		this.at = at;
	}

	@Override
	public void run() {
		//Check API cache time
		if (!canUpdate()) {
			return;
		}
		//Check if the Api Key is expired
		if (!isForceUpdate() && owner.isExpired()) {
			addError(null, "API KEY EXPIRED", "API Key expired");
			return;
		}
		try {
			update(at, first);
		} catch (ApiException ex) {
			switch (ex.getCode()) {
				case 400:
					addError(null, "INVALID ATTRIBUTE SELECTOR", "Invalid attribute selector", ex);
					break;
				case 401:
					addError(null, "INVALID CREDENTIAL", "Access credential invalid", ex);
					invalid = true;
					break;
				case 403:
					addError(null, "INVALID ACCESS MASK", "Not enough access privileges.\r\n(Fix: Add " + getTaskName() + " to the API Key)", ex);
					invalid = true;
					break;
				case 404:
					addError(null, "INVALID ACCESS KEY ID", "Access key with the given ID not found", ex);
					invalid = true;
					break;
				default:
					addError(null, ex.getMessage(), "Unknown Error Code: " + ex.getCode(), ex);
					break;
			}
		} catch (TaskCancelledException ex) {
			logInfo(null, "Cancelled");
		} catch (Throwable ex) {
			addError(null, ex.getMessage(), "Unknown Error: " + ex.getMessage(), ex);
		}
	}

	private <R> R updateApi(Updater<ApiResponse<R>, ApiException> updater) throws ApiException {
		return updateApi(updater, 0);
	}

	private <R> R updateApi(Updater<ApiResponse<R>, ApiException> updater, int retries) throws ApiException {
		try {
			checkCancelled();
			ApiResponse<R> apiResponse = updater.update();
			setExpires(apiResponse.getHeaders());
			logInfo(updater.getStatus(), "Updated");
			return apiResponse.getData();
		} catch (ApiException ex) {
			setExpires(ex.getResponseHeaders());
			logWarn(ex.getResponseBody(), ex.getMessage());
			if (retries < DEFAULT_RETRIES) {
				retries++;
				return updateApi(updater, retries);
			} else {
				throw ex;
			}
		}
	}

	@Override
	protected boolean invalidAccessPrivileges() {
		return (owner.getAccessMask() & getAccessMask()) != getAccessMask();
	}

	public final boolean isInvalid() {
		return invalid;
	}

	protected final CommonApi getCommonApi() {
		return COMMON_API;
	}

	protected final CharacterApi getCharacterApi() {
		return CHARACTER_API;
	}

	protected final CorporationApi getCorporationApi() {
		return CORPORATION_API;
	}

	protected final AccessKeyApi getAccessKeyApi() {
		return ACCESS_KEY_API;
	}

	public Date getLifeStart() {
		return lifeStart;
	}

	protected final String industryJobsFilter() {
		return "{ values: [1, 2, 3] }";
	}

	protected final String contractsFilter() {
		return "{ values: [\"in_progress\", \"outstanding\"] }";
	}

	protected final String marketOrdersFilter() {
		return "{ values: [\"open\"] }";
	}

	protected final <E extends Number> String valuesFilter(Set<E> ids) {
		StringBuilder builder = new StringBuilder();
		builder.append("{ values: [");
		boolean firstRun = true;
		for (E id : ids) {
			if (firstRun) {
				firstRun = false;
			} else {
				builder.append(", ");
			}
			builder.append(id);
		}
		builder.append("] }");
		return builder.toString();
	}

	protected final String dateFilter(int months) {
		if (months == 0) {
			return "{ any: true }";
		} else {
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			calendar.add(Calendar.DAY_OF_MONTH, (-months * 30));
			return "{ start: " + String.valueOf(calendar.getTime().getTime()) + ", end: " + String.valueOf(Long.MAX_VALUE) + " }";
		}
	}

	protected final String atFilter(Long at) {
		if (at == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("{ values: [");
		builder.append(at);
		builder.append("] }");
		return builder.toString();
	}

	protected final String atAny() {
		return "{ any: true }";
	}

	protected abstract void update(Long at, boolean first) throws ApiException;
	protected abstract long getAccessMask();

	protected <K> List<K> updatePages(EveKitPagesHandler<K> handler) throws ApiException {
		if (first) {
			EveKitPageUpdater<K> updater = new EveKitPageUpdater<K>(handler, "first", atAny(), null, 1);
			List<K> results = updateApi(updater);
			if (results != null && !results.isEmpty()) {
				Long l = handler.getLifeStart(results.get(0));
				if (l != null) {
					Date date = new Date(l);
					if (lifeStart == null || date.before(lifeStart)) {
						lifeStart = date;
					}
				}
			}
			return null;
		} else {
			List<K> results = new ArrayList<K>();
			List<K> batch = null;
			int count = 0;
			while (batch == null || batch.size() == 1000) {
				count++;
				EveKitPageUpdater<K> updater = new EveKitPageUpdater<K>(handler, count + " of ?", atFilter(at), getCid(handler, batch), 1000);
				batch = updateApi(updater);
				if (batch == null) {
					break;
				}
				results.addAll(batch);
			}
			handler.saveCID(getCid(handler, results));
			return results;
		}
	}

	private <K> Long getCid(EveKitPagesHandler<K> handler, List<K> batch) {
		if (batch == null || batch.isEmpty()) {
			return handler.loadCID();
		} else {
			return handler.getCID(batch.get(batch.size() - 1));
		}
	}

	public interface EveKitPagesHandler<K> {
		public ApiResponse<List<K>> get(String at, Long cid, Integer maxResults) throws ApiException;
		public long getCID(K k);
		public Long getLifeStart(K obj);
		/**
		 * NOT THREAD SAFE!
		 * We currently only use one thread, so, it's not a problem, yet
		 * @param cid 
		 */
		public void saveCID(Long cid); 
		/**
		 * NOT THREAD SAFE!
		 * We currently only use one thread, so, it's not a problem, yet
		 * @return 
		 */
		public Long loadCID(); 
	}

	public class EveKitPageUpdater<K> implements Callable<List<K>>, Updater<ApiResponse<List<K>>, ApiException> {

		private final EveKitPagesHandler<K> handler;
		private final String status;
		private final String at;
		private final Long cid;
		private final Integer maxResults;

		public EveKitPageUpdater(EveKitPagesHandler<K> handler, String status, String at, Long cid, Integer maxResults) {
			this.handler = handler;
			this.status = status;
			this.at = at;
			this.cid = cid;
			this.maxResults = maxResults;
		}

		@Override
		public ApiResponse<List<K>> update() throws ApiException {
			return handler.get(at, cid, maxResults);
		}

		@Override
		public List<K> call() throws Exception {
			return updateApi(this);
		}

		@Override
		public String getStatus() {
			return status;
		}

		@Override
		public int getMaxRetries() {
			return DEFAULT_RETRIES;
		}
	}
}
