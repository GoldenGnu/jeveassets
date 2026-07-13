/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
import java.util.Map;
import net.nikr.eve.jeveasset.io.local.settings.SettingsEveNames;
import net.nikr.eve.jeveasset.io.local.settings.SettingsManufacturingPrices;
import net.nikr.eve.jeveasset.io.local.settings.SettingsManufacturingSystems;
import net.nikr.eve.jeveasset.io.local.settings.SettingsOwnerNames;
import net.nikr.eve.jeveasset.io.local.settings.SettingsOwnersNextUpdate;


public class SQLiteSettings {

	private static final SettingsManufacturingPrices MANUFACTURING_PRICES = new SettingsManufacturingPrices();
	private static final SettingsManufacturingSystems MANUFACTURING_SYSTEMS = new SettingsManufacturingSystems();
	private static final SettingsEveNames EVE_NAMES = new SettingsEveNames();
	private static final SettingsOwnerNames OWNER_NAMES = new SettingsOwnerNames();
	private static final SettingsOwnersNextUpdate OWNER_NEXT_UPDATE = new SettingsOwnersNextUpdate();
	private static boolean save = false;

	public static boolean isSave() {
		return save;
	}

	public static synchronized void setManufacturingSystemIndex(Map<Integer, Float> manufacturingSystems) {
		MANUFACTURING_SYSTEMS.set(manufacturingSystems);
	}

	public static synchronized void migrateManufacturingSystemIndex(Map<Integer, Float> manufacturingSystems) {
		MANUFACTURING_SYSTEMS.set(manufacturingSystems);
		save = true;
	}

	public static synchronized Float getManufacturingSystemIndex(long systemID) {
		return getManufacturingSystemIndex((int) systemID);
	}

	public static synchronized Float getManufacturingSystemIndex(int systemID) {
		return MANUFACTURING_SYSTEMS.get(systemID);
	}

	public static synchronized boolean isManufacturingSystemIndexsEmpty() {
		return MANUFACTURING_SYSTEMS.isEmpty();
	}

	public static synchronized void setManufacturingPrices(Map<Integer, Double> data) {
		MANUFACTURING_PRICES.set(data);
	}

	public static synchronized void migrateManufacturingPrices(Map<Integer, Double> data) {
		MANUFACTURING_PRICES.set(data);
		save = true;
	}
	
	public static synchronized Double getManufacturingPrice(Integer typeID) {
		return MANUFACTURING_PRICES.get(typeID);
	}

	public static synchronized String getEveName(Long itemID) {
		return EVE_NAMES.get(itemID);
	}

	public static synchronized void putEveName(Long itemId, String name) {
		EVE_NAMES.put(itemId, name);
	}

	public static synchronized void setEveNames(Map<Long, String> data) {
		EVE_NAMES.set(data);
	}

	public static synchronized void migrateEveNames(Map<Long, String> data) {
		EVE_NAMES.set(data);
		save = true;
	}

	public static synchronized void removeEveName(Long itemID) {
		EVE_NAMES.delete(itemID);
	}

	public static synchronized String getOwner(Long ownerID) {
		return OWNER_NAMES.get(ownerID);
	}

	public static synchronized void clearOwners() {
		OWNER_NAMES.deleteAll();
	}

	public static synchronized void setOwners(Map<Long, String> names) {
		OWNER_NAMES.set(names);
	}

	public static synchronized void migrateOwners(Map<Long, String> names) {
		OWNER_NAMES.set(names);
		save = true;
	}

	public static synchronized void setOwnerNextUpdate(Map<Long, Date> dates) {
		OWNER_NEXT_UPDATE.set(dates);
	}

	public static synchronized void migrateOwnerNextUpdate(Map<Long, Date> dates) {
		OWNER_NEXT_UPDATE.set(dates);
		save = true;
	}

	public static synchronized Date getOwnerNextUpdate(Long ownerID) {
		return OWNER_NEXT_UPDATE.get(ownerID);
	}
}
