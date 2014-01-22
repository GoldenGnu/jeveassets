/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoutingSettings {
	private double secMin;
	private double secMax;
	private final Map<Long, SolarSystem> avoid = new HashMap<Long, SolarSystem>();
	private final Map<String, Set<Long>> presets = new HashMap<String, Set<Long>>();

	public RoutingSettings() {
		secMin = 0.0;
		secMax = 1.0;
	}

	public double getSecMin() {
		return secMin;
	}

	public void setSecMin(double secMin) {
		this.secMin = secMin;
	}

	public double getSecMax() {
		return secMax;
	}

	public void setSecMax(double secMax) {
		this.secMax = secMax;
	}

	public Map<Long, SolarSystem> getAvoid() {
		return avoid;
	}

	public Map<String, Set<Long>> getPresets() {
		return presets;
	}
}
