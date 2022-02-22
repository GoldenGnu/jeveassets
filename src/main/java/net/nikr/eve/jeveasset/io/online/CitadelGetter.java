/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.online;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.CitadelSettings;
import net.nikr.eve.jeveasset.io.local.CitadelReader;
import net.nikr.eve.jeveasset.io.local.CitadelWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CitadelGetter {

	private static final Logger LOG = LoggerFactory.getLogger(CitadelGetter.class);

	private static CitadelGetter citadelGetter;
	private CitadelSettings citadelSettings = new CitadelSettings();

	protected CitadelGetter() { }

	public synchronized static Citadel get(long locationID) {
		return getCitadelGetter().getCitadel(locationID);
	}

	public synchronized static Iterable<Map.Entry<Long, Citadel>> getAll() {
		return getCitadelGetter().getCitadelAll();
	}

	public synchronized static void remove(long locationID) {
		getCitadelGetter().removeCitadel(locationID);
	}

	public synchronized static void remove(Set<Long> locationIDs) {
		getCitadelGetter().removeCitadels(locationIDs);
	}

	public synchronized static void set(Citadel citadel) {
		getCitadelGetter().setCitadel(citadel);
	}

	public synchronized static void set(Collection<Citadel> citadels) {
		getCitadelGetter().setCitadels(citadels);
	}

	private static CitadelGetter getCitadelGetter() {
		if (citadelGetter == null) {
			citadelGetter = new CitadelGetter();
			citadelGetter.loadXml();
		}
		return citadelGetter;
	}

	private void saveXml() {
		CitadelWriter.save(citadelSettings);
	}

	private void loadXml() {
		citadelSettings = CitadelReader.load();
	}
	
	protected static interface Setter<T> {
		public void setETag(CitadelSettings citadelSettings, String eTag);
		public void setData(CitadelSettings citadelSettings, Map<Long, T> results);
	}

	private void removeCitadel(long locationID) {
		citadelSettings.remove(locationID);
		saveXml();
	}

	private void removeCitadels(Set<Long> locationIDs) {
		for (long locationID : locationIDs) {
			citadelSettings.remove(locationID);
		}
		if (!locationIDs.isEmpty()) {
			saveXml();
		}
	}

	private void setCitadel(Citadel citadel) {
		citadelSettings.put(citadel.getLocationID(), citadel);
		saveXml();
	}

	private void setCitadels(Collection<Citadel> citadels) {
		for (Citadel citadel : citadels) {
			citadelSettings.put(citadel.getLocationID(), citadel);
		}
		if (!citadels.isEmpty()) {
			saveXml();
		}
	}

	private Citadel getCitadel(long locationID) {
		Citadel citadel = citadelSettings.get(locationID);
		if (citadel == null) { //Location not found in cache -> add placeholder for future updates
			citadel = new Citadel(locationID);
		}
		return citadel;
	}

	private Iterable<Map.Entry<Long, Citadel>> getCitadelAll() {
		return citadelSettings.getCache();
	}
}
