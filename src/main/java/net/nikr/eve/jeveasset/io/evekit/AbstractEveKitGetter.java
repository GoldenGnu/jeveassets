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
package net.nikr.eve.jeveasset.io.evekit;

import enterprises.orbital.evekit.client.ApiClient;
import enterprises.orbital.evekit.client.ApiException;
import enterprises.orbital.evekit.client.api.AccessKeyApi;
import enterprises.orbital.evekit.client.api.CharacterApi;
import enterprises.orbital.evekit.client.api.CommonApi;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.io.shared.AbstractGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker.TaskCancelledException;


public abstract class AbstractEveKitGetter extends AbstractGetter<EveKitOwner, ApiClient, ApiException> {
	
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
			updateApi(new EveKitUpdater(), 0);
		} catch (ApiException ex) {
			switch (ex.getCode()) {
				case 400:
					addError(null, "INVALID ATTRIBUTE SELECTOR", "Invalid attribute selector");
					break;
				case 401:
					addError(null, "INVALID CREDENTIAL", "Access credential invalid");
					invalid = true;
					break;
				case 403:
					addError(null, "INVALID ACCESS MASK", "Not enough access privileges.\r\n(Fix: Add " + getTaskName() + " to the API Key)");
					invalid = true;
					break;
				case 404:
					addError(null, "INVALID ACCESS KEY ID", "Access key with the given ID not found");
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

	@Override
	public <R> R updateApi(Updater<R, ApiClient, ApiException> updater, int retries) throws ApiException {
		try {
			checkCancelled();
			final ApiClient client = new ApiClient();
			R r = updater.update(client);
			logInfo(updater.getStatus(), "Updated");
			String expiresHeader = getHeader(client.getResponseHeaders(), "Expires");
			if (expiresHeader != null) {
				setNextUpdateSafe(Formater.parseExpireDate(expiresHeader));
			}
			return r;
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
		return (owner.getAccessMask() & getAccessMask()) != getAccessMask();
	}

	public final boolean isInvalid() {
		return invalid;
	}

	protected final CommonApi getCommonApi(ApiClient apiClient) {
		return new CommonApi(apiClient);
	}

	protected final CharacterApi getCharacterApi(ApiClient apiClient) {
		return new CharacterApi(apiClient);
	}

	protected final AccessKeyApi getAccessKeyApi(ApiClient apiClient) {
		return new AccessKeyApi(apiClient);
	}

	public Date getLifeStart() {
		return lifeStart;
	}

	protected final String industryJobsFilter() {
		return encode("{ values: [\"1\", \"2\", \"3\"] }");
	}

	protected final String contractsFilter() {
		return encode("{ values: [\"InProgress\"] }");
	}
	

	protected final <E> String valuesFilter(Set<E> ids) {
		StringBuilder builder = new StringBuilder();
		builder.append("{ values: [");
		if (ids.isEmpty()) {
			builder.append("\"\"");
		}
		boolean firstRun = true;
		for (E id : ids) {
			if (firstRun) {
				firstRun = false;
			} else {
				builder.append(", ");
			}
			builder.append("\"");
			builder.append(id);
			builder.append("\"");
		}
		builder.append("] }");
		return encode(builder.toString());
	}

	protected final String dateFilter(int months) {
		if (months == 0) {
			return encode("{ any: true }");
		} else {
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			calendar.add(Calendar.DAY_OF_MONTH, (-months * 30));
			return encode("{ start: \"" + String.valueOf(calendar.getTime().getTime()) + "\", end: \"" + String.valueOf(Long.MAX_VALUE) + "\" }");
		}
	}

	protected final String atFilter(Long at) {
		if (at == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("{ values: [\"");
		builder.append(at);
		builder.append("\"] }");
		return encode(builder.toString());
	}

	protected final String atAny() {
		return encode("{ any: true }");
	}

	protected String encode(String plane) {
		try {
			return URLEncoder.encode(plane, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	protected abstract void get(ApiClient apiClient, Long at, boolean first) throws ApiException;
	protected abstract long getAccessMask();

	protected <K> List<K> updatePages(EveKitPagesHandler<K> handler) throws ApiException {
		if (first) {
			EveKitPageUpdater<K> updater = new EveKitPageUpdater<K>(handler, "", atAny(), null, 1);
			List<K> results = updateApi(updater, 0);
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
			while (batch == null || !batch.isEmpty()) {
				count++;
				EveKitPageUpdater<K> updater = new EveKitPageUpdater<K>(handler, count + " of ?", atFilter(at), getCid(handler, batch), Integer.MAX_VALUE);
				batch = updateApi(updater, 0);
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
		public List<K> get(ApiClient apiClient, String at, Long cid, Integer maxResults) throws ApiException;
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

	public class EveKitPageUpdater<K> implements Callable<List<K>>, Updater<List<K>, ApiClient, ApiException> {

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
		public List<K> update(ApiClient client) throws ApiException {
			return handler.get(client, at, cid, maxResults);
		}

		@Override
		public List<K> call() throws Exception {
			return updateApi(this, 0);
		}

		@Override
		public String getStatus() {
			return status;
		}

		@Override
		public int getMaxRetries() {
			return NO_RETRIES;
		}
	}

	public class EveKitUpdater implements Updater<Void, ApiClient, ApiException> {

		@Override
		public Void update(ApiClient client) throws ApiException {
			get(client, at, first);
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
