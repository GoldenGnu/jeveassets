/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data.sde;

import net.nikr.eve.jeveasset.gui.shared.table.containers.Security;
import net.nikr.eve.jeveasset.i18n.General;


public class MyLocation implements Comparable<MyLocation> {
	private final long locationID; //LocationID : long
	private final String location;
	private final long stationID; //LocationID : long
	private final String station;
	private final long systemID; //LocationID : long
	private final String system;
	private final long regionID; //LocationID : long
	private final String region;
	private final String security;
	private final Security securityObject;
	private final boolean citadel;
	private final boolean empty;
	private final boolean userLocation;

	public MyLocation(long locationID) {
		if (locationID > 9000000000000000000L) {
			this.location = General.get().buggedLocation();
			this.station = General.get().buggedLocation();
			this.system = General.get().buggedLocation();
			this.region = General.get().buggedLocation();
		} else {
			this.location = General.get().emptyLocation(String.valueOf(locationID));
			this.station = General.get().emptyLocation(String.valueOf(locationID));
			this.system = General.get().emptyLocation(String.valueOf(locationID));
			this.region = General.get().emptyLocation(String.valueOf(locationID));
		}
		this.locationID = locationID;
		this.stationID = 0;
		this.systemID = 0;
		this.regionID = 0;
		this.security = "0.0";
		this.securityObject = new Security(security);
		this.citadel = false;
		this.empty = true;
		this.userLocation = false;
	}

	public MyLocation(long stationID, String station, long systemID, String system, long regionID, String region, String security) {
		this(stationID, station, systemID, system, regionID, region, security, false, false);
	}

	public MyLocation(long stationID, String station, long systemID, String system, long regionID, String region, String security, boolean citadel) {
		this(stationID, station, systemID, system, regionID, region, security, citadel, false);
	}

	public MyLocation(long stationID, String station, long systemID, String system, long regionID, String region, String security, boolean citadel, boolean userLocation) {
		this.stationID = stationID;
		this.station = station;
		this.systemID = systemID;
		this.system = system;
		this.regionID = regionID;
		this.region = region;
		this.security = security;
		this.securityObject = new Security(security);
		if (isStation()) {
			empty = false;
			this.locationID = stationID;
			this.location = station;
		} else if (isSystem()) {
			empty = false;
			this.locationID = systemID;
			this.location = system;
		} else if (isRegion()) {
			empty = false;
			this.locationID = regionID;
			this.location = region;
		} else {
			empty = true;
			this.locationID = 0;
			this.location = "";
		}
		this.citadel = citadel;
		this.userLocation = userLocation;
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
		return empty;
	}

	public boolean isCitadel() {
		return citadel;
	}

	public boolean isUserLocation() {
		return userLocation;
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
		final MyLocation other = (MyLocation) obj;
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
	public int compareTo(final MyLocation o) {
		return getLocation().compareToIgnoreCase(o.getLocation());
	}
}
