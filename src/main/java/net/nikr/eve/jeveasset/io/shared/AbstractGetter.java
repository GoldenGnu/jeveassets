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
package net.nikr.eve.jeveasset.io.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractGetter<O extends OwnerType, C, E extends Exception> implements Callable<Void> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractGetter.class);

	protected enum TaskType {
		ASSETS,
		ACCOUNT_BALANCE,
		BLUEPRINTS,
		CONQUERABLE_STATIONS,
		CONTRACT_ITEMS,
		CONTRACTS,
		INDUSTRY_JOBS,
		JOURNAL,
		LOCATIONS,
		MARKET_ORDERS,
		OWNER_ID_TO_NAME,
		OWNER,
		STRUCTURES,
		TRANSACTIONS
	}

	private final UpdateTask updateTask;
	private final boolean forceUpdate;
	private final Date nextUpdate;
	private final String taskName;
	private final String apiName;
	protected final O owner;
	private String error = null;

	public AbstractGetter(UpdateTask updateTask, O owner, boolean forceUpdate, Date nextUpdate, TaskType taskType, String ApiName) {
		this.updateTask = updateTask;
		this.owner = owner;
		this.forceUpdate = forceUpdate;
		this.nextUpdate = nextUpdate;
		switch (taskType) {
			case ACCOUNT_BALANCE: taskName = "Account Balance"; break;
			case ASSETS: taskName = "Assets"; break;
			case BLUEPRINTS: taskName = "Blueprints"; break;
			case CONQUERABLE_STATIONS: taskName = "Conquerable Stations"; break;
			case CONTRACT_ITEMS: taskName = "Contract Items"; break;
			case CONTRACTS: taskName = "Contracts"; break;
			case INDUSTRY_JOBS: taskName = "Industry Jobs"; break;
			case JOURNAL: taskName = "Journal"; break;
			case LOCATIONS: taskName = "Locations"; break;
			case MARKET_ORDERS: taskName = "Market Orders"; break;
			case OWNER: taskName = "Account"; break;
			case OWNER_ID_TO_NAME:  taskName = "IDs to Names"; break;
			case TRANSACTIONS: taskName = "Transactions"; break;
			case STRUCTURES: taskName = "Structures"; break;
			default: taskName = "Unknown"; break;
		}
		//this.taskName = taskType;
		this.apiName = ApiName;
	}

	public void run() {
		ThreadWoker.start(updateTask, Collections.singletonList(this));
	}

	public synchronized final boolean hasError() {
		return error != null;
	}

	public synchronized final String getError() {
		return error;
	}

	public synchronized final void setError(String error) {
		this.error = error;
	}

	protected final String getTaskName() {
		return taskName;
	}

	protected boolean canUpdate(String updaterStatus) {
		//Silently ignore disabled owners
		if (owner != null && (!owner.isShowOwner() && !forceUpdate)) {
			logInfo(updaterStatus, "Owner disabled");
			return false; 
		}
		//Check API cache time
		if (!forceUpdate && owner != null && !Settings.get().isUpdatable(nextUpdate, false)) {
			addError(updaterStatus, "NOT ALLOWED YET", "Not allowed yet.\r\n(Fix: Just wait a bit)");
			return false;
		}
		//Check if the owner have accesss to the endpoint 
		if (invalidAccessPrivileges()) {
			addError(updaterStatus, "NOT ENOUGH ACCESS PRIVILEGES", "Not enough access privileges.\r\n(Fix: Add " + getTaskName() + " to the API Key)");
			return false;
		}
		return true;
	}

	protected synchronized void setNextUpdateSafe(Date date) {
		setNextUpdate(date);
	}

	/**
	 * NOT THREAD SAFE!
	 * use setNextUpdateSafe(Date date)
	 * @param date 
	 */
	protected abstract void setNextUpdate(Date date);
	protected abstract boolean invalidAccessPrivileges();
	protected abstract <R> R updateApi(Updater<R, C, E> updater, int retries);

	protected final void addError(String update, String logMsg, String taskMsg) {
		addError(update, logMsg, taskMsg, null);
	}

	protected final void addError(String update, Object logMsg, Object taskMsg, Throwable ex) {
		StringBuilder builder = new StringBuilder();
		builder.append(apiName);
		builder.append(" ");
		builder.append(taskName);
		builder.append(" failed to update for: ");
		builder.append(getOwnerName(owner));
		if (update != null) {
			builder.append(" (");
			builder.append(update);
			builder.append(")");
		}
		if (logMsg != null) {
			builder.append(" ERROR: ");
			builder.append(logMsg);
		}
		String e = builder.toString();
		setError(e);
		if (ex != null) {
			LOG.error(e, ex);
		} else {
			LOG.error(e);
		}
		if (updateTask != null && taskMsg != null) {
			StringBuilder ownerBuilder = new StringBuilder();
			ownerBuilder.append(apiName);
			ownerBuilder.append(" > ");
			ownerBuilder.append(taskName);
			ownerBuilder.append(" > ");
			ownerBuilder.append(getOwnerName(owner));
			updateTask.addError(ownerBuilder.toString(), taskMsg.toString());
		}
	}

	protected final void logInfo(String update, String msg) {
		StringBuilder builder = new StringBuilder();
		if (msg != null) {
			builder.append(msg);
			builder.append(":");
		}
		builder.append(" ");
		builder.append(apiName);
		builder.append(" > ");
		builder.append(taskName);
		builder.append(" > ");
		builder.append(getOwnerName(owner));
		if (update != null) {
			builder.append(" (");
			builder.append(update);
			builder.append(")");
		}
		LOG.info(builder.toString());
	}

	

	protected String getOwnerName(O owner) {
		if (owner != null) {
			return owner.getOwnerName();
		} else {
			return Program.PROGRAM_NAME;
		}
	}

	protected <T> List<List<T>> splitList(Collection<T> list, final int L) {
		return splitList(new ArrayList<T>(list), L);
	}

	private <T> List<List<T>> splitList(List<T> list, final int L) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int N = list.size();
		for (int i = 0; i < N; i += L) {
			parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + L))));
		}
		return parts;
	}

	protected synchronized Integer getHeaderInteger(Map<String, List<String>> responseHeaders, String headerName) {
		String errorResetHeader = getHeader(responseHeaders, headerName);
		if (errorResetHeader != null) {
			try {
				return Integer.valueOf(errorResetHeader);
			} catch (NumberFormatException ex) {
				//No problem
			}
		}
		return null;
	}

	protected synchronized String getHeader(Map<String, List<String>> responseHeaders, String headerName) {
		if (responseHeaders != null) {
			Map<String, List<String>> caseInsensitiveHeaders = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
			caseInsensitiveHeaders.putAll(responseHeaders);
			List<String> headers = caseInsensitiveHeaders.get(headerName.toLowerCase());
			if (headers != null && !headers.isEmpty()) {
				String header = headers.get(0);
				if (header != null && !header.isEmpty()) {
					return header;
				}
			}
		}
		return null;
	}

	protected interface Updater<R, C, E extends Throwable> {
		public R update(final C client) throws E;
		public String getStatus();
	}

	protected <K, V> Map<K, V> updateList(Collection<K> list, ListHandler<K, V> handler) throws E {
		Map<K, V> values = new HashMap<K, V>();
		List<ListUpdater<K, V>> updaters = new ArrayList<ListUpdater<K, V>>();
		int count = 1;
		for (K k : list) {
			updaters.add(new ListUpdater<K, V>(handler, k, count + " of " + list.size()));
			count++;
		}
		LOG.info("Starting " + updaters.size() + " list threads");
		List<Future<Map<K, V>>> futures = ThreadWoker.startReturn(updaters);
		for (Future<Map<K, V>> future : futures) {
			try {
				Map<K, V> returnValue = future.get();
				if (returnValue != null) {
					values.putAll(returnValue);
				}
			} catch (InterruptedException ex) {
				//No problem
			} catch (ExecutionException ex) {
				//No problem
			}
		}
		return values;
	}


	protected abstract class ListHandler<K, V> {
		public abstract V get(C client, K t) throws E;
	}

	private class ListUpdater<K, V> implements Updater<Map<K, V>, C, E>, Callable<Map<K, V>> {

		private final ListHandler<K, V> handler;
		private final K k;
		private final String status;

		public ListUpdater(ListHandler<K, V> handler, K k, String status) {
			this.handler = handler;
			this.k = k;
			this.status = status;
		}

		@Override
		public Map<K, V> update(C client) throws E {
			Map<K, V> map = new HashMap<K, V>();
			V v = handler.get(client, k);
			if (v != null) {
				map.put(k, v);
			}
			return map;
		}

		@Override
		public Map<K, V> call() throws Exception {
			return updateApi(this, 0);
		}

		@Override
		public String getStatus() {
			return status;
		}
	}
}
