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

package net.nikr.eve.jeveasset.data;


public class OverviewLocation {
	public enum LocationType {
		TYPE_STATION, TYPE_SYSTEM, TYPE_REGION,
		;
	}

	String name;
	LocationType type;

	public OverviewLocation(String name) {
		this.name = name;
		this.type = null;
	}

	public OverviewLocation(String name, LocationType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public LocationType getType() {
		return type;
	}

	public boolean isStation(){
		return type.equals(LocationType.TYPE_STATION);
	}

	public boolean isSystem(){
		return type.equals(LocationType.TYPE_SYSTEM);
	}

	public boolean isRegion(){
		return type.equals(LocationType.TYPE_REGION);
	}

	public boolean equalsLocation(Asset eveAsset){
		return (name.equals(eveAsset.getLocation()) || name.equals(eveAsset.getSystem())|| name.equals(eveAsset.getRegion()));
	}

	public boolean equalsLocation(Overview overview){
		return (name.equals(overview.getName()) || name.equals(overview.getSolarSystem())|| name.equals(overview.getRegion()));
	}

	@Override
	public boolean equals(Object obj) {
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
