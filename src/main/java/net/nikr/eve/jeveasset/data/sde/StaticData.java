/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.sde;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.io.local.SdeFlagsReader;
import net.nikr.eve.jeveasset.io.local.SdeItemsReader;
import net.nikr.eve.jeveasset.io.local.SdeJumpsReader;
import net.nikr.eve.jeveasset.io.local.SdeLocationsReader;
import net.nikr.eve.jeveasset.io.online.SdeDownloader;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StaticData {
	private static final Logger LOG = LoggerFactory.getLogger(StaticData.class);
	private static final ReentrantReadWriteLock LOCATIONS_LOCK = new ReentrantReadWriteLock();
	//Data
	private final Map<Integer, Item> items = new HashMap<>(); //TypeID : int
	private final Map<Integer, ItemFlag> flags = new HashMap<>(); //FlagID : int
	private final Map<Long, MyLocation> locations = new HashMap<>(); //LocationID : long
	private final List<Jump> jumps = new ArrayList<>(); //LocationID : long

	private static StaticData staticData = null;

	private StaticData() {}

	public static StaticData get() {
		load();
		return staticData;
	}

	public static synchronized void load() {
		if (staticData == null) {
			staticData = new StaticData();
			if (!checkSdeVersion()) {
				throw new RuntimeException("SDE version check failed. Please ensure SDE files are downloaded and up to date.");
			}
			staticData.loadData();
		}
	}

	private static boolean checkSdeVersion() {
		String sdeDirectory = FileUtil.getPathSdeDirectory();
		File sdeDir = new File(sdeDirectory);
		File typesFile = new File(sdeDir, "types.jsonl");
		if (!typesFile.exists()) {
			LOG.info("SDE files not found. Downloading JSONL archive...");
			SplashUpdater.setText("Downloading SDE data from CCP...");
			if (!SdeDownloader.ensureRequiredSdeFiles(sdeDirectory)) {
				LOG.error("Failed to download required SDE files");
				return false;
			}
			LOG.info("SDE files downloaded successfully");
		}
		if (!sdeDir.exists() || !sdeDir.isDirectory()) {
			LOG.error("SDE directory does not exist: " + sdeDir.getAbsolutePath());
			return false;
		}
		if (!typesFile.exists()) {
			LOG.error("Required SDE file not found: types.jsonl");
			return false;
		}
		return true;
	}

	private void loadData() {
		SplashUpdater.setProgress(5);
		SdeItemsReader.load(items); //Items from SDE
		SplashUpdater.setProgress(10);
		try {
			LOCATIONS_LOCK.writeLock().lock();
			SdeLocationsReader.load(locations); //Locations from SDE
		} finally {
			LOCATIONS_LOCK.writeLock().unlock();
		}
		SplashUpdater.setProgress(15);
		SdeJumpsReader.load(jumps); //Jumps from SDE
		SplashUpdater.setProgress(20);
		SdeFlagsReader.load(flags); //Item Flags from SDE
		SplashUpdater.setProgress(25);
	}

	public Map<Integer, ItemFlag> getItemFlags() {
		return flags;
	}

	public Map<Integer, Item> getItems() {
		return items;
	}

	public List<Jump> getJumps() {
		return jumps;
	}

	public void addLocation(MyLocation location) {
		try {
			LOCATIONS_LOCK.writeLock().lock();
			locations.put(location.getLocationID(), location);
		} finally {
			LOCATIONS_LOCK.writeLock().unlock();
		}
	}

	public void removeLocation(long locationID) {
		try {
			LOCATIONS_LOCK.writeLock().lock();
			locations.remove(locationID);
		} finally {
			LOCATIONS_LOCK.writeLock().unlock();
		}
	}

	public MyLocation getLocation(long locationID) {
		try {
			LOCATIONS_LOCK.readLock().lock();
			return locations.get(locationID);
		} finally {
			LOCATIONS_LOCK.readLock().unlock();
		}
	}

	public Collection<MyLocation> getLocations() {
		try {
			LOCATIONS_LOCK.readLock().lock();
			return new ArrayList<>(locations.values()); //Copy
		} finally {
			LOCATIONS_LOCK.readLock().unlock();
		}
	}
}
