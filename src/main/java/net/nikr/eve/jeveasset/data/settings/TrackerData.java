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
package net.nikr.eve.jeveasset.data.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.io.local.TrackerDataReader;
import net.nikr.eve.jeveasset.io.local.TrackerDataWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrackerData {
	
	private static final Logger LOG = LoggerFactory.getLogger(TrackerData.class);
	private static final Map<String, List<Value>> TRACKER_DATA = new HashMap<String, List<Value>>(); //ownerID :: long
	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	private static final Object SAVE_QUEUE_SYNC = new Object();
	private static Integer SAVE_QUEUE = 0;
	

	public static void readLock() {
		LOCK.readLock().lock();
	}

	public static void readUnlock() {
		LOCK.readLock().unlock();
	}

	public static void writeLock() {
		LOCK.writeLock().lock();
	}

	public static void writeUnlock() {
		LOCK.writeLock().unlock();
	}

	public static void waitForEmptySaveQueue() {
		while (!saveQueueEmpty()) {
			synchronized(SAVE_QUEUE_SYNC) {
				try {
					SAVE_QUEUE_SYNC.wait();
				} catch (InterruptedException ex) {
					//No problem
				}
			}
		}
	}

	public static void load() {
		Map<String, List<Value>> trackerData = TrackerDataReader.load();
		TrackerData.set(trackerData);
	}

	public static void save(String msg) {
		save(msg, false);
	}

	public static void save(String msg, boolean wait) {
		if (saveQueueIgnore()) {
			return;
		}
		saveQueueAdd();
		Save save = new Save(msg);
		save.start();
		if (wait) {
			try {
				save.join();
			} catch (InterruptedException ex) {
				//No problem
			}
		}
	}

	public static Map<String, List<Value>> get() {
		if (Program.isDebug() && LOCK.getReadHoldCount() == 0 && LOCK.getWriteHoldCount() == 0) {
			throw new RuntimeException("Tracker Data not read locked");
		}
		return TRACKER_DATA;
	}

	public static void add(String owner, Value add) {
		try {
			LOCK.writeLock().lock();
			List<Value> list = TRACKER_DATA.get(owner);
			if (list == null) {
				list = new ArrayList<>();
				TRACKER_DATA.put(owner, list);
			}
			list.add(add);
		} finally {
			LOCK.writeLock().unlock();
		}
	}

	public static void addAll(Map<String, List<Value>> trackerData) {
		try {
			LOCK.writeLock().lock();
			TRACKER_DATA.putAll(trackerData);
		} finally {
			LOCK.writeLock().unlock();
		}
	}

	public static void set(Map<String, List<Value>> trackerData) {
		if (trackerData == null) {
			return;
		}
		try {
			LOCK.writeLock().lock();
			TRACKER_DATA.clear();
			TRACKER_DATA.putAll(trackerData);
		} finally {
			LOCK.writeLock().unlock();
		}
	}

	public static void remove(String owner, Value remove) {
		try {
			LOCK.writeLock().lock();
			List<Value> values = TRACKER_DATA.get(owner);
			if (values != null) {
				values.remove(remove);
				if (values.isEmpty()) { //Remove empty list
					TRACKER_DATA.remove(owner);
				}
			}
		} finally {
			LOCK.writeLock().unlock();
		}
	}

	public static void removeAll(String owner, Collection<Value> remove) {
		try {
			LOCK.writeLock().lock();
			List<Value> values = TRACKER_DATA.get(owner);
			if (values != null) {
				values.removeAll(remove);
				if (values.isEmpty()) { //Remove empty list
					TRACKER_DATA.remove(owner);
				}
			}
		} finally {
			LOCK.writeLock().unlock();
		}
	}

	private synchronized static boolean saveQueueIgnore() {
		return SAVE_QUEUE > 1;
	}

	private synchronized static void saveQueueAdd() {
		SAVE_QUEUE++;
		synchronized(SAVE_QUEUE_SYNC) {
			SAVE_QUEUE_SYNC.notifyAll();
		}
	}

	private synchronized static void saveQueueRemove() {
		SAVE_QUEUE--;
		synchronized(SAVE_QUEUE_SYNC) {
			SAVE_QUEUE_SYNC.notifyAll();
		}
	}

	private synchronized static boolean saveQueueEmpty() {
		return SAVE_QUEUE == 0;
	}

	private static class Save extends Thread {
		private final String msg;

		public Save(String msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			long before = System.currentTimeMillis();

			LOG.info("Saving tracker data: " + msg);
			TrackerData.readLock();
			TrackerDataWriter.save();
			TrackerData.readUnlock();
			saveQueueRemove();
			LOG.debug("Tracker data saved in: " + (System.currentTimeMillis() - before) + "ms");
		}
	}
}
