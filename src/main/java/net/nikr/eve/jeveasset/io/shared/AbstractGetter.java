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
package net.nikr.eve.jeveasset.io.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker.TaskCancelledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractGetter<O extends OwnerType, C, E extends Exception> implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractGetter.class);

	protected static final int NO_RETRIES = 0;

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
		TRANSACTIONS,
		SHIP,
		CONTAINER_LOGS
	}

	private final UpdateTask updateTask;
	private final boolean forceUpdate;
	private final boolean disabled;
	private final boolean wait;
	private final String taskName;
	private final String apiName;
	protected final O owner;
	private String error = null;

	public AbstractGetter(UpdateTask updateTask, O owner, boolean forceUpdate, Date nextUpdate, TaskType taskType, String ApiName) {
		this.updateTask = updateTask;
		this.owner = owner;
		this.forceUpdate = forceUpdate;
		this.disabled = !forceUpdate && owner != null && !owner.isShowOwner();
		this.wait = !forceUpdate && !Settings.get().isUpdatable(nextUpdate, false);
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
			case SHIP: taskName = "Active Ship"; break;
			case CONTAINER_LOGS: taskName = "Container Logs"; break;
			default: taskName = "Unknown"; break;
		}
		//this.taskName = taskType;
		this.apiName = ApiName;
	}

	public void start() {
		ThreadWoker.start(updateTask, Collections.singletonList(this));
	}

	public final synchronized boolean hasError() {
		return error != null;
	}

	public final synchronized String getError() {
		return error;
	}

	public final synchronized void setError(String error) {
		this.error = error;
	}

	/**
	 * NOT THREAD SAFE!
	 * use setNextUpdateSafe(Date date)
	 * @param date 
	 */
	protected abstract void setNextUpdate(Date date);
	protected abstract boolean invalidAccessPrivileges();
	protected abstract <R> R updateApi(Updater<R, C, E> updater, int retries) throws E;
	protected abstract void throwApiException(Exception ex) throws E;

	protected final String getTaskName() {
		return taskName;
	}

	protected final boolean canUpdate() {
		//Silently ignore disabled owners
		if (disabled) {
			logInfo(null, "Owner disabled");
			return false; 
		}
		//Check API cache time
		if (wait) {
			addError(null, "NOT ALLOWED YET", "Not allowed yet.\r\n(Fix: Just wait a bit)");
			return false;
		}
		//Check if the owner have accesss to the endpoint 
		if (invalidAccessPrivileges()) {
			addError(null, "NOT ENOUGH ACCESS PRIVILEGES", "Not enough access privileges.\r\n(Fix: Add " + getTaskName() + " to the API Key)");
			return false;
		}
		return true;
	}

	protected final boolean isForceUpdate() {
		return forceUpdate;
	}

	protected final synchronized void setNextUpdateSafe(Date date) {
		setNextUpdate(date);
	}

	protected interface Updater<R, C, E extends Throwable> {
		public R update(final C client) throws E;
		public String getStatus();
		public int getMaxRetries();
	}

	protected final void pause() {
		if (updateTask != null) {
			updateTask.pause(); //Pause
		}
	}

	protected final void setProgress(final float progressEnd, final float progressNow, final int minimum, final int maximum) {
		if (updateTask != null) {
			updateTask.setTaskProgress(progressEnd, progressNow, minimum, maximum);
		}
	}

	protected final <K> List<Future<K>> startSubThreads(Collection<? extends Callable<K>> updaters) throws InterruptedException {
		return ThreadWoker.startReturn(updateTask, updaters);
	}

	protected final void checkCancelled() {
		if (updateTask != null && updateTask.isCancelled()) {
			throw new TaskCancelledException();
		}
	}

	protected final void addError(String update, String logMsg, String taskMsg) {
		addError(update, logMsg, taskMsg, null);
	}

	protected final void addError(String update, Object logMsg, Object taskMsg, Throwable ex) {
		logError(update, logMsg, taskMsg, ex);
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

	protected final void addMigrationWarning() {
		if (updateTask != null) {
			updateTask.addError("EveApi accounts can be migrated to ESI", "Add ESI accounts in the account manager:\r\nOptions > Accounts... > Add > ESI");
		}
	}

	protected final void logError(String update, Object logMsg, Object taskMsg) {
		logError(update, logMsg, taskMsg, null);
	}
	protected final void logError(String update, Object logMsg, Object taskMsg, Throwable ex) {
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

	protected final String getOwnerName(O owner) {
		if (owner != null) {
			return owner.getOwnerName();
		} else {
			return Program.PROGRAM_NAME;
		}
	}

	protected final <T> List<List<T>> splitList(Collection<T> list, final int L) {
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

	protected final Set<Long> getIDs(Map<Long, String> itemMap, OwnerType owner) {
		addItemIDs(itemMap, owner.getAssets());
		return itemMap.keySet();
	}

	private void addItemIDs(Map<Long, String> itemIDs, List<MyAsset> assets) {
		for (MyAsset asset : assets) {
			if ((asset.getItem().getGroup().equals("Audit Log Secure Container")
					|| asset.getItem().getCategory().equals("Ship"))
					&& asset.isSingleton()) {
				itemIDs.put(asset.getItemID(), asset.getItem().getTypeName());
			}
			addItemIDs(itemIDs, asset.getAssets());
		}
	}

	protected final  synchronized Integer getHeaderInteger(Map<String, List<String>> responseHeaders, String headerName) {
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

	protected final synchronized String getHeader(Map<String, List<String>> responseHeaders, String headerName) {
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

	protected final <K, V> Map<K, V> updateList(Collection<K> list, int maxRetries, ListHandler<K, V> handler) throws E {
		Map<K, V> values = new HashMap<K, V>();
		List<ListUpdater<K, V>> updaters = new ArrayList<ListUpdater<K, V>>();
		int count = 1;
		for (K k : list) {
			updaters.add(new ListUpdater<K, V>(handler, k, count + " of " + list.size(), maxRetries));
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
		} catch (InterruptedException | ExecutionException ex) {
			throwApiException(ex);
		}
		return values;
	}

	protected abstract class ListHandler<K, V> {
		protected abstract V get(C client, K k) throws E;
	}

	protected class ListUpdater<K, V> implements Updater<Map<K, V>, C, E>, Callable<Map<K, V>> {

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
		public Map<K, V> update(C client) throws E {
			V v = handler.get(client, k);
			if (v != null) {
				Map<K, V> map = new HashMap<K, V>();
				map.put(k, v);
				return map;
			} else {
				return null;
			}
		}

		public Map<K, V> go() throws E {
			return updateApi(this, 0);
		}

		@Override
		public Map<K, V> call() throws Exception {
			return updateApi(this, 0);
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

	protected final <K> List<K> updateIDs(Set<Long> existing, int maxRetries, IDsHandler<K> handler) throws E {
		List<K> list = new ArrayList<K>();
		Long fromID = null;
		boolean run = true;
		int count = 0;
		while (run) {
			count++;
			List<K> result;
			result = updateApi(new IdUpdater<K>(handler, fromID, count + " of ?", maxRetries), 0);
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
		protected abstract List<K> get(C client, Long fromID) throws E;
		protected abstract Long getID(K response);
	}

	public class IdUpdater<K> implements Updater<List<K>, C, E> {

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
		public List<K> update(C client) throws E {
			return handler.get(client, fromID);
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
}
