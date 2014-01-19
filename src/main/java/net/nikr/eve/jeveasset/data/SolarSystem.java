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

import uk.me.candle.eve.graph.Node;

/**
 *
 * @author Candle
 */
public class SolarSystem extends Node {
	private Location location;

	public SolarSystem(final Location location) {
		super(location.getLocation());
		this.location = location;
	}

	public String getSecurity() {
		return location.getSecurity();
	}

	public long getRegionID() {
		return location.getRegionID();
	}

	public long getLocationID() {
		return location.getLocationID();
	}

	public long getSystemID() {
		return location.getSystemID();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SolarSystem other = (SolarSystem) obj;
		if (this.location != other.location && (this.location == null || !this.location.equals(other.location))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + (this.location != null ? this.location.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return getName();
	}
}
