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
package net.nikr.eve.jeveasset.data.settings;

import java.util.Date;
import java.util.Map;
import net.nikr.eve.jeveasset.io.local.settings.SettingsEveNames;
import net.nikr.eve.jeveasset.io.local.settings.SettingsManufacturingPrices;
import net.nikr.eve.jeveasset.io.local.settings.SettingsManufacturingSystems;
import net.nikr.eve.jeveasset.io.local.settings.SettingsOwnerNames;
import net.nikr.eve.jeveasset.io.local.settings.SettingsOwnersNextUpdate;


public class SQLiteSettings {

	private static SettingsManufacturingPrices settingsManufacturingPrices;
	private static SettingsManufacturingSystems settingsManufacturingSystems;
	private static SettingsEveNames settingsEveNames;
	private static SettingsOwnerNames settingsOwnerNames;
	private static SettingsOwnersNextUpdate ownerNextUpdate;

	public static void load() {
		if (settingsManufacturingPrices == null) {
			settingsManufacturingPrices = new SettingsManufacturingPrices();
		}
		if (settingsManufacturingSystems == null) {
			settingsManufacturingSystems = new SettingsManufacturingSystems();
		}
		if (settingsEveNames == null) {
			settingsEveNames = new SettingsEveNames();
		}
		if (settingsOwnerNames == null) {
			settingsOwnerNames = new SettingsOwnerNames();
		}
		if (ownerNextUpdate == null) {
			ownerNextUpdate = new SettingsOwnersNextUpdate();
		}
	}

	public static synchronized void setManufacturingSystemIndex(Map<Integer, Float> manufacturingSystems) {
		settingsManufacturingSystems.set(manufacturingSystems);
	}

	public static synchronized Float getManufacturingSystemIndex(long systemID) {
		return getManufacturingSystemIndex((int) systemID);
	}

	public static synchronized Float getManufacturingSystemIndex(int systemID) {
		return settingsManufacturingSystems.get(systemID);
	}

	public static synchronized boolean isManufacturingSystemIndexsEmpty() {
		return settingsManufacturingSystems.isEmpty();
	}

	public static synchronized void setManufacturingPrices(Map<Integer, Double> data) {
		settingsManufacturingPrices.set(data);
	}
	
	public static synchronized Double getManufacturingPrice(Integer typeID) {
		return settingsManufacturingPrices.get(typeID);
	}

	public static synchronized String getEveName(Long itemID) {
		return settingsEveNames.get(itemID);
	}

	public static synchronized void putEveName(Long itemId, String name) {
		settingsEveNames.put(itemId, name);
	}

	public static synchronized void setEveNames(Map<Long, String> data) {
		settingsEveNames.set(data);
	}

	public static synchronized void removeEveName(Long itemID) {
		settingsEveNames.delete(itemID);
	}

	public static synchronized String getOwner(Long ownerID) {
		return settingsOwnerNames.get(ownerID);
	}

	public static synchronized void clearOwners() {
		settingsOwnerNames.deleteAll();
	}

	public static synchronized void setOwners(Map<Long, String> names) {
		settingsOwnerNames.set(names);
	}

	public static synchronized void setOwnerNextUpdate(Map<Long, Date> dates) {
		ownerNextUpdate.set(dates);
	}

	public static synchronized Date getOwnerNextUpdate(Long ownerID) {
		return ownerNextUpdate.get(ownerID);
	}
}
