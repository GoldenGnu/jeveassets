/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Region extends GalacticObject {

	Set<SolarSystem> systems;

	Region(String id, String name) {
		super(id, name);
		this.systems = new TreeSet<SolarSystem>();
	}

	public boolean isWSpace() {
		return "Unknown".equals(getName());
	}

	public Set<SolarSystem> getSolarSystems() {
		return Collections.unmodifiableSet(systems);
	}

	public Map<String, SolarSystem> getSolarSystemsAsMap() {
		Map<String, SolarSystem> systemMap = new HashMap<String, SolarSystem>();
		for (SolarSystem s : systems) {
			systemMap.put(s.getId(), s);
		}
		return Collections.unmodifiableMap(systemMap);
	}
	
	public int getSolarSystemCount() {
		return systems.size();
	}

	public String toStringFull() {
		return getName() + " (" + getSolarSystemCount() + " sytems)";
	}

	// <editor-fold defaultstate="collapsed" desc="Package level model construction methods">
	void addSystem(String id, String name, String security) {
		systems.add(new SolarSystem(id, name, security, this));
	}

	void addStationToSystem(String id, String name, String systemID) {
		for (SolarSystem s : systems) {
			if (s.getId().equals(systemID))
				s.addStation(id, name);
		}
	}
	// </editor-fold>
}