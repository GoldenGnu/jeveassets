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

package net.nikr.eve.jeveasset.gui.tabs.overview;

import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;


public class OverviewLocation {
	public enum LocationType {
		TYPE_STATION, TYPE_SYSTEM, TYPE_REGION;
	}

	private String name;
	private LocationType type;

	public OverviewLocation(final String name) {
		this(name, null);
	}

	public OverviewLocation(final String name, final LocationType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public LocationType getType() {
		return type;
	}

	public boolean isStation() {
		return type.equals(LocationType.TYPE_STATION);
	}

	public boolean isSystem() {
		return type.equals(LocationType.TYPE_SYSTEM);
	}

	public boolean isRegion() {
		return type.equals(LocationType.TYPE_REGION);
	}

	public boolean equalsLocation(final Asset asset) {
		return (name.equals(asset.getLocation().getLocation()) || name.equals(asset.getLocation().getSystem()) || name.equals(asset.getLocation().getRegion()));
	}

	public boolean equalsLocation(final Overview overview) {
		return (name.equals(overview.getName()) || name.equals(overview.getLocation().getSystem()) || name.equals(overview.getLocation().getRegion()));
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final OverviewLocation other = (OverviewLocation) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}
}
