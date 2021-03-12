/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;

public class Citadel {

	public static enum CitadelSource {
		ESI_STRUCTURES(3), //Certified fresh
		ESI_LOCATIONS(3), //Good source
		ESI_PLANET(3), //Planet source
		FUZZWORK_PLANET(3), //Planet source
		ZKILL(2), //Average source
		USER(2), //User set this data (unless we have top-notch info, don't overwrite)
		HAMMERTIME(1), //Better than nothing source
		OLD(1), //Unknown source - keep if?
		ESI_BOOKMARKS(0), //Unknown location name, but, known system and region
		EVEKIT_BOOKMARKS(0), //Unknown location name, but, known system and region
		EMPTY(-1) //100% Unknown location
		;

		private final int priority;

		private CitadelSource(int priority) {
			this.priority = priority;
		}

		public int getPriority() {
			return priority;
		}

	}

	private long locationID;
	private String location;
	private long systemID;
	private MyLocation system;
	private boolean userLocation;
	private boolean citadel;
	private CitadelSource source;
	private MyLocation myLocation;

	/**
	 * Empty location
	 * @param id locationID
	 */
	public Citadel(long id) {
		this(id, "", 0, false, true, CitadelSource.EMPTY);
	}

	/**
	 * 
	 * @param locationID
	 * @param location
	 * @param system
	 * @param userLocation
	 * @param citadel
	 * @param source 
	 */
	public Citadel(long locationID, String location, long systemID, boolean userLocation, boolean citadel, CitadelSource source) {
		this.locationID = locationID;
		this.location = location;
		this.systemID = systemID;
		this.userLocation = userLocation;
		this.citadel = citadel;
		this.source = source;
		updateLocation();
	}

	public void update(Citadel citadel) {
		this.locationID = citadel.locationID;
		this.location = citadel.location;
		this.systemID = citadel.systemID;
		this.userLocation = citadel.userLocation;
		this.citadel = citadel.citadel;
		this.source = citadel.source;
		updateLocation();
	}

	private void updateLocation() {
		system = StaticData.get().getLocation(systemID);
		if (system == null) {
			system = MyLocation.create(locationID);
		}
		if (!isEmpty()) { //Location is valid -> return locations
			if (userLocation) {
				myLocation =  MyLocation.create(locationID, system.getSystem() + " - " + location, systemID, system.getSystem(), system.getConstellationID(), system.getConstellation(), system.getRegionID(), system.getRegion(), system.getSecurity(), citadel, userLocation);
			} else {
				myLocation = MyLocation.create(locationID, location, systemID, system.getSystem(), system.getConstellationID(), system.getConstellation(), system.getRegionID(), system.getRegion(), system.getSecurity(), citadel, userLocation);
			}
		} else { //Location not valid -> return fallback location
			myLocation = null;
		}
	}

	public long getLocationID() {
		return locationID;
	}

	public String getLocation() {
		return location;
	}

	public long getSystemID() {
		return systemID;
	}

	public boolean isEmpty() {
		return systemID != 0 && !system.isEmpty();
	}

	public boolean isUserLocation() {
		return userLocation;
	}

	public boolean isCitadel() {
		return citadel;
	}

	public CitadelSource getSource() {
		return source;
	}

	public MyLocation toLocation() {
		return myLocation;
	}

	@Override
	public String toString() {
		return myLocation.toString();
	}

}
