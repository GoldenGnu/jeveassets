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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;

public class RouteAvoidSettings {
	private double secMin;
	private double secMax;
	private final Map<Long, SolarSystem> avoid = new HashMap<>();
	private final Map<String, Set<Long>> presets = new HashMap<>();

	public RouteAvoidSettings() {
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

	public void update(RouteAvoidSettings avoidSettings) {
		secMin = avoidSettings.getSecMin();
		secMax = avoidSettings.getSecMax();
		avoid.clear();
		avoid.putAll(avoidSettings.getAvoid());
		presets.clear();
		presets.putAll(avoidSettings.getPresets());
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 53 * hash + (int) (Double.doubleToLongBits(this.secMin) ^ (Double.doubleToLongBits(this.secMin) >>> 32));
		hash = 53 * hash + (int) (Double.doubleToLongBits(this.secMax) ^ (Double.doubleToLongBits(this.secMax) >>> 32));
		hash = 53 * hash + Objects.hashCode(this.avoid);
		hash = 53 * hash + Objects.hashCode(this.presets);
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
		final RouteAvoidSettings other = (RouteAvoidSettings) obj;
		if (Double.doubleToLongBits(this.secMin) != Double.doubleToLongBits(other.secMin)) {
			return false;
		}
		if (Double.doubleToLongBits(this.secMax) != Double.doubleToLongBits(other.secMax)) {
			return false;
		}
		if (!Objects.equals(this.avoid, other.avoid)) {
			return false;
		}
		return Objects.equals(this.presets, other.presets);
	}
}
