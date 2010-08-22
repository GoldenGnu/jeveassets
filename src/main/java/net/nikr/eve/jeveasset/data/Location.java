/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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


public class Location {
	private int id;
	private String name;
	private int region;
	private String security;
	private int solarSystemID;

	public Location(int id, String name, int region, String security, int solarSystemID) {
		this.id = id;
		this.name = name;
		this.region = region;
		this.security = security;
		this.solarSystemID = solarSystemID;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getRegion() {
		return region;
	}

	public String getSecurity() {
		return security;
	}

	@Override
	public String toString(){
		return name;
	}

	public int getSolarSystemID() {
		return solarSystemID == 0 ? id : solarSystemID;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Location other = (Location) obj;
		if (this.id != other.id) {
			return false;
		}
		if (this.solarSystemID != other.solarSystemID) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + this.id;
		hash = 29 * hash + this.solarSystemID;
		return hash;
	}
}
