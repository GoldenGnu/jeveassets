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

import net.nikr.eve.jeveasset.gui.shared.table.containers.Security;


public class Location implements Comparable<Location> {
	private long locationID; //LocationID : long
	private String location;
	private long stationID; //LocationID : long
	private String station;
	private long systemID; //LocationID : long
	private String system;
	private long regionID; //LocationID : long
	private String region;
	private String security;
	private Security securityObject;

	public Location(long locationID) {
		this(0, "", 0, "", 0, "", "0.0");
		this.locationID = locationID;
	}

	public Location(long stationID, String station, long systemID, String system, long regionID, String region, String security) {
		this.stationID = stationID;
		this.station = station;
		this.systemID = systemID;
		this.system = system;
		this.regionID = regionID;
		this.region = region;
		this.security = security;
		this.securityObject = new Security(security);
		if (isStation()) {
			this.locationID = stationID;
			this.location = station;
		} else if (isSystem()) {
			this.locationID = systemID;
			this.location = system;
		} else if (isRegion()) {
			this.locationID = regionID;
			this.location = region;
		} else {
			this.locationID = 0;
			this.location = "";
		}
	}

	public String getLocation() {
		return location;
	}

	public long getLocationID() {
		return locationID;
	}

	public String getStation() {
		return station;
	}

	public long getStationID() {
		return stationID;
	}

	public String getSystem() {
		return system;
	}

	public long getSystemID() {
		return systemID;
	}

	public String getRegion() {
		return region;
	}

	public long getRegionID() {
		return regionID;
	}

	public String getSecurity() {
		return security;
	}

	public Security getSecurityObject() {
		return securityObject;
	}

	public final boolean isStation() {
		return (getStationID() != 0 && getSystemID() != 0 && getRegionID() != 0);
	}

	public final boolean isSystem() {
		return (getStationID() == 0 && getSystemID() != 0 && getRegionID() != 0);
	}

	public final boolean isRegion() {
		return (getStationID() == 0 && getSystemID() == 0 && getRegionID() != 0);
	}

	public boolean isEmpty() {
		return location.isEmpty();
	}

	@Override
	public String toString() {
		return location;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Location other = (Location) obj;
		if (this.locationID != other.locationID) {
			return false;
		}
		if (this.systemID != other.systemID) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + (int) (this.locationID ^ (this.locationID >>> 32));
		hash = 71 * hash + (int) (this.systemID ^ (this.systemID >>> 32));
		return hash;
	}

	@Override
	public int compareTo(final Location o) {
		return getLocation().compareToIgnoreCase(o.getLocation());
	}
}
