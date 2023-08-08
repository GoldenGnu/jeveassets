/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import java.util.concurrent.Future;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.Updatable;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker.TaskCancelledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractGetter<O extends OwnerType> implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractGetter.class);

	protected static final int NO_RETRIES = 0;

	protected enum TaskType {
		ACCOUNT_BALANCE("Account Balance"),
		ASSETS("Assets"),
		BLUEPRINTS("Blueprints"),
		BOOKMARKS("Bookmarks"),
		CONTRACTS("Contracts"),
		CONTRACT_ITEMS("Contract Items"),
		CONTRACT_PRICES("Contract Prices"),
		DIVISIONS("Division Names"),
		INDUSTRY_JOBS("Industry Jobs"),
		ITEM_TYPES("Item Types"),
		JOURNAL("Journal"),
		LOCATIONS("Locations"),
		MARKET_ORDERS("Market Orders"),
		MINING("Mining"),
		PUBLIC_MARKET_ORDERS("Public Market Orders"),
		OWNER("Account"),
		OWNER_ID_TO_NAME("IDs to Names"),
		FACTION_WARFARE("Faction Warfare"),
		PLANETARY_INTERACTION("Planetary Assets"),
		SHIP("Active Ship"),
		STRUCTURES("Structures"),
		TRANSACTIONS("Transactions"),
		SKILLS("Skills"),
		MANUFACTURING_PRICES("Manufacturing Prices"),
		;

		private final String taskName;

		private TaskType(String taskName) {
			this.taskName = taskName;
		}

		public String getTaskName() {
			return taskName;
		}
	}

	private final UpdateTask updateTask;
	private final boolean forceUpdate;
	private final boolean disabled;
	private final boolean wait;
	private final String taskName;
	private final String apiName;
	protected final O owner;
	private String error = null;

	public AbstractGetter(UpdateTask updateTask, O owner, boolean forceUpdate, Date nextUpdate, TaskType taskType, String apiName) {
		this.updateTask = updateTask;
		this.owner = owner;
		this.forceUpdate = forceUpdate;
		this.disabled = !forceUpdate && owner != null && !owner.isShowOwner();
		this.wait = !forceUpdate && !Updatable.isUpdatable(nextUpdate);
		if (taskType == null) {
			taskName = "Unknown";
		} else {
			taskName = taskType.getTaskName();
		}
		//this.taskName = taskType;
		this.apiName = apiName;
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
	protected abstract boolean haveAccess();

	protected synchronized void setExpires(Map<String, List<String>> headers) {
		Date expires = getHeaderDate(headers, "expires");
		if (expires != null) {
			setNextUpdate(expires);
		}
	}

	protected final boolean canUpdate() {
		//Silently ignore disabled owners
		if (disabled) {
			logInfo(null, "Owner disabled");
			return false;
		}
		//Check API cache time
		if (wait) {
			addInfo("NOT ALLOWED YET", "Update skipped: Waiting for cache to expire.\r\n(Updatable again after cache expires)");
			return false;
		}
		//Check if the owner have access to the endpoint
		if (owner != null && !haveAccess()) {
			//Silent
			return false;
		}
		return true;
	}

	protected final boolean isForceUpdate() {
		return forceUpdate;
	}

	protected interface Updater<R, E extends Throwable> {
		public R update() throws E;
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

	protected final <K> List<Future<K>> startSubThreads(Collection<? extends Callable<K>> updaters, boolean updateProgress) throws InterruptedException {
		return ThreadWoker.startReturn(updateTask, updaters, updateProgress);
	}

	protected final void checkCancelled() {
		if (updateTask != null && updateTask.isCancelled()) {
			throw new TaskCancelledException();
		}
	}

	protected final void addError(String logMsg, String taskMsg) {
		addError(logMsg, taskMsg, null);
	}

	protected final void addError(Object logMsg, Object taskMsg, Throwable ex) {
		logError(logMsg, taskMsg, ex);
		if (updateTask != null && taskMsg != null) {
			updateTask.addError(buildLogID(), taskMsg.toString());
		}
	}

	protected final void addWarning(String logMsg, String taskMsg) {
		addWarning(logMsg, taskMsg, null);
	}

	protected final void addWarning(Object logMsg, Object taskMsg, Throwable ex) {
		logWarn(logMsg, taskMsg, ex);
		if (updateTask != null && taskMsg != null) {
			updateTask.addWarning(buildLogID(), taskMsg.toString());
		}
	}

	protected final void addInfo(String logMsg, String taskMsg) {
		addInfo(logMsg, taskMsg, null);
	}

	protected final void addInfo(Object logMsg, Object taskMsg, Throwable ex) {
		logInfo(logMsg, taskMsg, ex);
		if (updateTask != null && taskMsg != null) {
			updateTask.addInfo(buildLogID(), taskMsg.toString());
		}
	}

	private String buildLogID() {
		StringBuilder ownerBuilder = new StringBuilder();
		ownerBuilder.append(apiName);
		ownerBuilder.append(" > ");
		ownerBuilder.append(taskName);
		if (owner != null) {
			ownerBuilder.append(" > ");
			ownerBuilder.append(owner.getOwnerName());
		}
		return ownerBuilder.toString();
	}

	protected final void logError(Object logMsg, Object taskMsg) {
		logError(logMsg, taskMsg, null);
	}

	protected final void logError(Object logMsg, Object taskMsg, Throwable ex) {
		String e = getLog(logMsg);
		setError(e);
		if (ex != null) {
			LOG.error(e, ex);
		} else {
			LOG.error(e);
		}
	}

	protected final void logWarn(Object logMsg, Object taskMsg) {
		logWarn(logMsg, taskMsg, null);
	}

	protected final void logWarn(Object logMsg, Object taskMsg, Throwable ex) {
		String e = getLog(logMsg);
		if (ex != null) {
			LOG.warn(e, ex);
		} else {
			LOG.warn(e);
		}
	}

	protected final void logInfo(Object logMsg, Object taskMsg) {
		logInfo(logMsg, taskMsg, null);
	}

	protected final void logInfo(Object logMsg, Object taskMsg, Throwable ex) {
		String e = getLog(logMsg);
		if (ex != null) {
			LOG.info(e, ex);
		} else {
			LOG.info(e);
		}
	}

	protected final String getLog(Object logMsg) {
		StringBuilder builder = new StringBuilder();
		builder.append(apiName);
		builder.append(" ");
		builder.append(taskName);
		builder.append(" failed to update for: ");
		if (owner != null) {
			builder.append(" > ");
			builder.append(owner.getOwnerName());
		}
		if (logMsg != null) {
			builder.append(" ERROR: ");
			builder.append(logMsg);
		}
		return builder.toString();
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
		if (owner != null) {
			builder.append(" > ");
			builder.append(owner.getOwnerName());
		}
		if (update != null) {
			builder.append(" (");
			builder.append(update);
			builder.append(")");
		}
		LOG.info(builder.toString());
	}

	protected final <T> List<List<T>> splitList(Collection<T> list, final int L) {
		return splitList(new ArrayList<>(list), L);
	}

	private <T> List<List<T>> splitList(List<T> list, final int L) {
		List<List<T>> parts = new ArrayList<>();
		final int N = list.size();
		for (int i = 0; i < N; i += L) {
			parts.add(new ArrayList<>(list.subList(i, Math.min(N, i + L))));
		}
		return parts;
	}

	protected final Map<Long, MyAsset> getIDs(OwnerType owner) {
		Map<Long, MyAsset> itemMap = new HashMap<>();
		ArrayList<MyAsset> assets;
		synchronized(owner) {
			assets = new ArrayList<>(owner.getAssets());
		}
		addItemIDs(itemMap, assets);
		return itemMap;
	}

	private void addItemIDs(Map<Long, MyAsset> itemIDs, List<MyAsset> assets) {
		for (MyAsset asset : assets) {
			if ((asset.getItem().isContainer()
					|| asset.getItem().getGroup().equals(Item.GROUP_BIOMASS)
					|| asset.getItem().getCategory().equals(Item.CATEGORY_DEPLOYABLE)
					|| asset.getItem().isShip()
					|| asset.getItem().getCategory().equals(Item.CATEGORY_STRUCTURE))
					&& asset.isSingleton()) {
				itemIDs.put(asset.getItemID(), asset);
			}
			addItemIDs(itemIDs, asset.getAssets());
		}
	}

	protected static final Integer getHeaderInteger(Map<String, List<String>> responseHeaders, String headerName) {
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

	protected static final Date getHeaderDate(Map<String, List<String>> responseHeaders, String headerName) {
		String header = getHeader(responseHeaders, headerName);
		if (header != null) {
			return Formatter.parseExpireDate(header);
		}
		return null;
	}

	protected static final Date getHeaderExpires(Map<String, List<String>> responseHeaders) {
		String header = getHeader(responseHeaders, "expires");
		if (header != null) {
			return Formatter.parseExpireDate(header);
		}
		return null;
	}

	protected static final String getHeader(Map<String, List<String>> responseHeaders, String headerName) {
		if (responseHeaders != null) {
			Map<String, List<String>> caseInsensitiveHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
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
}
