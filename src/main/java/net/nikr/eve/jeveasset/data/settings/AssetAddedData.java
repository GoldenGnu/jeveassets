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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.io.local.AssetAddedReader;
import net.nikr.eve.jeveasset.io.local.AssetAddedWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AssetAddedData {
	private static final Logger LOG = LoggerFactory.getLogger(AssetAddedData.class);
	private static final Map<Long, Date> ASSET_ADDED = new HashMap<Long, Date>();
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
		AssetAddedReader.load();
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

	public static Map<Long, Date> get() {
		if (Program.isDebug() && LOCK.getReadHoldCount() == 0 && LOCK.getWriteHoldCount() == 0) {
			throw new RuntimeException("Asset Added not read locked");
		}
		return ASSET_ADDED;
	}

	public static Date getAdd(Long itemID, Date added) {
		try {
			LOCK.readLock().lock();
			Date date = ASSET_ADDED.get(itemID);
			if (date == null || date.after(added)) { //Add or Update
				date = added;
				ASSET_ADDED.put(itemID, date);
			}
			return date;
		} finally {
			LOCK.readLock().unlock();
		}
	}

	public static boolean containsKey(Long itemID) {
		try {
			LOCK.readLock().lock();
			return ASSET_ADDED.containsKey(itemID);
		} finally {
			LOCK.readLock().unlock();
		}
	}

	public static void put(Long itemID, Date date) {
		try {
			LOCK.writeLock().lock();
			ASSET_ADDED.put(itemID, date);
		} finally {
			LOCK.writeLock().unlock();
		}
	}

	public static void set(Map<Long, Date> assetAdded) {
		try {
			LOCK.writeLock().lock();
			ASSET_ADDED.clear();
			ASSET_ADDED.putAll(assetAdded);
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

			LOG.info("Saving asset added data: " + msg);
			AssetAddedData.readLock();
			AssetAddedWriter.save();
			AssetAddedData.readUnlock();
			saveQueueRemove();
			LOG.debug("Asset added data saved in: " + (System.currentTimeMillis() - before) + "ms");
		}
	}
}
