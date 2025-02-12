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

package net.nikr.eve.jeveasset.data.sde;

import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Security;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import uk.me.candle.eve.pricing.options.NamedPriceLocation;


public class MyLocation implements Comparable<MyLocation>, NamedPriceLocation {

	private final static Map<Long, MyLocation> CACHE = new HashMap<>();

	private final long locationID; //LocationID : long
	private String location;
	private long stationID; //LocationID : long
	private String station;
	private long systemID; //LocationID : long
	private String system;
	private long constellationID; //LocationID : long
	private String constellation;
	private long regionID; //LocationID : long
	private String region;
	private String security;
	private Security securityObject;
	private boolean citadel;
	private boolean empty;
	private boolean userLocation;

	public static void reset(long locationID) {
		MyLocation cached = CACHE.get(locationID);
		if (cached != null) {
			cached.updateLocation(new MyLocation(locationID));
		}
	}

	public static MyLocation create(long locationID) {
		MyLocation cached = CACHE.get(locationID);
		if (cached == null) {
			cached = new MyLocation(locationID);
			CACHE.put(locationID, cached);
		}
		return cached;
	}

	public static MyLocation create(long stationID, String station, long systemID, String system, long constellationID, String constellation, long regionID, String region, String security, boolean citadel, boolean userLocation) {
		final MyLocation newLocation = new MyLocation(stationID, station, systemID, system, constellationID, constellation, regionID, region, security, citadel, userLocation);
		MyLocation cached = CACHE.get(newLocation.getLocationID());
		if (cached == null) { //New
			cached = newLocation;
			CACHE.put(newLocation.getLocationID(), newLocation);
		} else { //Update
			cached.updateLocation(newLocation);
		}
		return cached;
	}

	private MyLocation(long locationID) {
		if (locationID == 2004) {
			this.location = General.get().assetSafety();
			this.station = General.get().assetSafety();
			this.system = General.get().assetSafety();
			this.constellation = General.get().assetSafety();
			this.region = General.get().assetSafety();
			this.empty = false;
		} else {
			this.location = General.get().emptyLocation(String.valueOf(locationID));
			this.station = General.get().emptyLocation(String.valueOf(locationID));
			this.system = General.get().emptyLocation(String.valueOf(locationID));
			this.constellation = General.get().emptyLocation(String.valueOf(locationID));
			this.region = General.get().emptyLocation(String.valueOf(locationID));
			this.empty = true;
		}
		this.locationID = locationID;
		this.stationID = 0;
		this.systemID = 0;
		this.constellationID = 0;
		this.regionID = 0;
		this.security = "0.0";
		this.securityObject = Security.create(security);
		this.citadel = false;
		this.userLocation = false;
	}

	public MyLocation(long stationID, String station, long systemID, String system, long constellationID, String constellation, long regionID, String region, String security) {
		this(stationID, station, systemID, system, constellationID, constellation, regionID, region, security, false, false);
	}

	private MyLocation(long stationID, String station, long systemID, String system, long constellationID, String constellation, long regionID, String region, String security, boolean citadel, boolean userLocation) {
		this.stationID = stationID;
		this.station = station;
		this.systemID = systemID;
		this.system = system.intern();
		this.constellationID = constellationID;
		this.constellation = constellation.intern();
		this.regionID = regionID;
		this.region = region.intern();
		this.security = security.intern();
		this.securityObject = Security.create(security);
		if (isStation() || isPlanet()) { //Station or Planet
			empty = false;
			this.locationID = stationID;
			this.location = station;
		} else if (isSystem()) {
			empty = false;
			this.locationID = systemID;
			this.location = system.intern();
		} else if (isConstellation()) {
			empty = false;
			this.locationID = constellationID;
			this.location = constellation.intern();
		} else if (isRegion()) {
			empty = false;
			this.locationID = regionID;
			this.location = region.intern();
		} else {
			empty = true;
			this.locationID = 0;
			this.location = "";
		}
		this.citadel = citadel;
		this.userLocation = userLocation;
	}

	private void updateLocation(final MyLocation newLocation) {
		this.stationID = newLocation.stationID;
		this.station = newLocation.station;
		this.systemID = newLocation.systemID;
		this.system = newLocation.system.intern();
		this.constellationID = newLocation.constellationID;
		this.constellation = newLocation.constellation.intern();
		this.regionID = newLocation.regionID;
		this.region = newLocation.region.intern();
		this.security = newLocation.security.intern();
		this.securityObject = Security.create(newLocation.security);
		this.location = newLocation.location.intern();
		this.empty = newLocation.empty;
		this.citadel = newLocation.citadel;
		this.userLocation = newLocation.userLocation;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
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

	public long getConstellationID() {
		return constellationID;
	}

	public String getConstellation() {
		return constellation;
	}

	public String getRegion() {
		return region;
	}

	@Override
	public long getRegionID() {
		return regionID;
	}

	public String getSecurity() {
		return security;
	}

	public Security getSecurityObject() {
		return securityObject;
	}

	/**
	 * Return true if this location is a Station
	 * Will return false if this location is a Planet, System, Constellation, Region or Unknown/Empty locations
	 * @return
	 */
	public final boolean isStation() {
		return getStationID() != 0 && getSystemID() != 0 && !isConstellation() && getRegionID() != 0 && (locationID < 40000000 || locationID > 50000000);
	}

	/**
	 * Return true if this location is a Planet
	 * Will return false if this location is a Station, System, Constellation, Region or Unknown/Empty locations
	 * @return
	 */
	public final boolean isPlanet() {
		return getStationID() != 0 && getSystemID() != 0 && !isConstellation() && getRegionID() != 0 && locationID >= 40000000 && locationID <= 50000000;
	}

	/**
	 * Return true if this location is a System
	 * Will return false if this location is a Station, Planet, Constellation, Region or Unknown/Empty locations
	 * @return
	 */
	public final boolean isSystem() {
		return getStationID() == 0 && getSystemID() != 0 && !isConstellation() && getRegionID() != 0;
	}
	/**
	 * Return true if this location is a Constellation
	 * Will return false if this location is a Station, Planet, System, Region or Unknown/Empty locations
	 * @return
	 */
	public final boolean isConstellation() {
		return getStationID() == 0 && getSystemID() == 0 && getConstellationID() != 0 && getRegionID() != 0;
	}
	/**
	 * Return true if this location is a Region
	 * Will return false if this location is a Station, Planet, System, Constellation or Unknown/Empty locations
	 * @return
	 */
	public final boolean isRegion() {
		return getStationID() == 0 && getSystemID() == 0 && !isConstellation() && getRegionID() != 0;
	}

	public String getFactionWarfareSystemOwner() {
		if (Settings.get().getFactionWarfareSystemOwners().isEmpty()) {
			return GuiShared.get().unknownFaction();
		}
		String faction = Settings.get().getFactionWarfareSystemOwners().get(getSystemID());
		if (faction != null) {
			return faction;
		} else {
			return GuiShared.get().none();
		}
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
