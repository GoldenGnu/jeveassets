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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;

public class RoutingSettings {
	private final RouteAvoidSettings avoidSettings = new RouteAvoidSettings();
	private final Map<String, RouteResult> routes = new TreeMap<>();

	public Map<String, RouteResult> getRoutes() {
		return routes;
	}

	public RouteAvoidSettings getAvoidSettings() {
		return avoidSettings;
	}

	public double getSecMin() {
		return avoidSettings.getSecMin();
	}

	public void setSecMin(double secMin) {
		avoidSettings.setSecMin(secMin);
	}

	public double getSecMax() {
		return avoidSettings.getSecMax();
	}

	public void setSecMax(double secMax) {
		avoidSettings.setSecMax(secMax);
	}

	public Map<Long, SolarSystem> getAvoid() {
		return avoidSettings.getAvoid();
	}

	public Map<String, Set<Long>> getPresets() {
		return avoidSettings.getPresets();
	}
}
