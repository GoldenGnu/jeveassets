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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import net.nikr.eve.jeveasset.i18n.TabsRouting;


public class RouteResult {
	private final List<List<SolarSystem>> route;
	private final Map<Long, List<SolarSystem>> stations;
	private final int waypoints;
	private final String algorithmName;
	private final long algorithmTime;
	private final int jumps;
	private final String avoid;
	private final String security;

	public RouteResult(List<List<SolarSystem>> route, Map<Long, List<SolarSystem>> stations, int waypoints, String algorithmName, long algorithmTime, int jumps, String avoid, String security) {
		this.route = route;
		this.stations = stations;
		this.waypoints = waypoints;
		this.algorithmName = algorithmName;
		this.algorithmTime = algorithmTime;
		this.jumps = jumps;
		if (avoid != null) {
			this.avoid = avoid;
		} else {
			this.avoid = TabsRouting.get().resultUnknownValue();
		}
		if (security != null) {
			this.security = security;
		} else {
			this.security = TabsRouting.get().resultUnknownValue();
		}
	}

	public List<List<SolarSystem>> getRoute() {
		return route;
	}

	public Map<Long, List<SolarSystem>> getStations() {
		return stations;
	}

	public int getWaypoints() {
		return waypoints;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public long getAlgorithmTime() {
		return algorithmTime;
	}

	public int getJumps() {
		return jumps;
	}

	public String getAvoid() {
		return avoid;
	}

	public String getSecurity() {
		return security;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + Objects.hashCode(this.route);
		hash = 97 * hash + Objects.hashCode(this.stations);
		hash = 97 * hash + this.waypoints;
		hash = 97 * hash + Objects.hashCode(this.algorithmName);
		hash = 97 * hash + (int) (this.algorithmTime ^ (this.algorithmTime >>> 32));
		hash = 97 * hash + this.jumps;
		hash = 97 * hash + Objects.hashCode(this.avoid);
		hash = 97 * hash + Objects.hashCode(this.security);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RouteResult other = (RouteResult) obj;
		if (this.waypoints != other.waypoints) {
			return false;
		}
		if (this.algorithmTime != other.algorithmTime) {
			return false;
		}
		if (this.jumps != other.jumps) {
			return false;
		}
		if (!Objects.equals(this.algorithmName, other.algorithmName)) {
			return false;
		}
		if (!Objects.equals(this.avoid, other.avoid)) {
			return false;
		}
		if (!Objects.equals(this.security, other.security)) {
			return false;
		}
		if (!Objects.equals(this.route, other.route)) {
			return false;
		}
		if (!Objects.equals(this.stations, other.stations)) {
			return false;
		}
		return true;
	}

}
