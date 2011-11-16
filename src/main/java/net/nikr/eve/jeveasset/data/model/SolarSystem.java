/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import java.util.Set;
import java.util.TreeSet;

public class SolarSystem  extends GalacticObject {

	Region region;
	private String security;
	Set<SolarSystem> jumps;
	Set<Station> stations;

	public enum SecurityStatus {HIGH_SEC, LOW_SEC, NULL_SEC}
	
	SolarSystem(String id, String name, String security, Region region) {
		super(id, name);
		this.security = security;
		this.region = region;
		this.jumps = new TreeSet<SolarSystem>();
		this.stations = new TreeSet<Station>();
	}

	public String getSecurity() {
		return this.security;
	}

	public Region getRegion() {
		return region;
	}

	public boolean isWSpace() {
		return region.isWSpace();
	}

	public SecurityStatus getSecurityStatus() {
		String sec = this.getSecurity();
		if (sec.equals("0.0")) {
			return SecurityStatus.NULL_SEC;
		}
		if (sec.equals("1.0")) {
			return SecurityStatus.HIGH_SEC;
		}
		if (sec.charAt(2) <= '4') {
			return SecurityStatus.LOW_SEC;
		}
		return SecurityStatus.HIGH_SEC;
	}

	public Set<Station> getStations() {
		return Collections.unmodifiableSet(stations);
	}

	public Set<SolarSystem> getJumps() {
		return Collections.unmodifiableSet(jumps);
	}
	
	public int getStationCount() {
		return stations.size();
	}
	
	public int getJumpCount() {
		return jumps.size();
	}
	
	public String toStringFull() {
		return getName() + " (" + getSecurity() + " sec, " +
				getStationCount() + " station" + (getStationCount() == 1 ? ", " : "s, ") +
				getJumpCount() + " stargate" + (getJumpCount() == 1 ? ")" : "s)");
	}

	// <editor-fold defaultstate="collapsed" desc="Package level model construction methods">



	void addJump(SolarSystem system) {
		jumps.add(system);
	}

	void addStation(String id, String name) {
		stations.add(new Station(id, name, this));
	}
	// </editor-fold>
}
