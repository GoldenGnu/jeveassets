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
package net.nikr.eve.jeveasset.gui.tabs.values;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.TrackerData;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class AssetValue implements Comparable<AssetValue> {
	private final static Map<String, AssetValue> CACHE = new HashMap<>();
	private static final String UNKNOWN_LOCATION = General.get().emptyLocation("(\\d+)").replace("[", "\\[").replace("]", "\\]");
	private static final String CITADEL_MATCH = "\\[Citadel #(\\d+)\\]";
	private static final String CITADEL_REPLACE = General.get().emptyLocation("$1").replace("[", "\\[").replace("]", "\\]");

	private final String flag;
	private String location;
	private Long locationID;
	private String id;

	public static AssetValue create(String id) {
		return get(new AssetValue(id));
	}

	public static AssetValue create(String location, String flag, Long locationID) {
		return get(new AssetValue(location, flag, locationID));
	}

	public static void updateData() {
		try {
			TrackerData.writeLock();
			for (AssetValue assetValue : CACHE.values()) {
				assetValue.update();
			}
		} finally {
			TrackerData.writeUnlock();
		}
		TrackerData.save("Asset values updated", true);
	}

	private static AssetValue get(final AssetValue add) {
		AssetValue cached = CACHE.get(add.getKey());
		if (cached != null) {
			return cached;
		} else {
			CACHE.put(add.getKey(), add);
			return add;
		}
	}
	
	private AssetValue(String id) {
		String[] ids = id.split(" > ");
		if (ids.length == 2) {
			location = ids[0]; //Location
			flag = ids[1]; //Flag
		} else {
			location = id;
			flag = null; //Never used
		}
		locationID = null;
		update();
	}

	private AssetValue(String location, String flag, Long locationID) {
		this.location = location;
		this.locationID = locationID;
		this.flag = flag;
		update();
	}

	public String getLocation() {
		return location;
	}

	public String getFlag() {
		return flag;
	}

	public Long getLocationID() {
		return locationID;
	}

	public String getID() {
		return id;
	}

	private void update() {
		if (locationID == null) {
			locationID = updateLocationID(location);
		}
		this.location = updateLocationName(location, locationID);
		if (flag != null) {
			id = location + " > " + flag;
		} else {
			id =location;
		}
	}

	private Long updateLocationID(String name) {
		//Unknown Locations
		Long locationIDvalue = resolveUnknownLocationID(name, UNKNOWN_LOCATION);
		//Citadel
		if (locationIDvalue == null) {
			locationIDvalue = resolveUnknownLocationID(name.replace(",", ""), CITADEL_MATCH);
		}
		//Existing Locations
		if (locationIDvalue == null) {
			locationIDvalue = resolveExistingLocationID(name);
		}
		return locationIDvalue;
	}

	private String updateLocationName(String locationValue, Long locationIDvalue) {
		if (locationIDvalue == null) {
			return locationValue;
		} else {
			 MyLocation myLocation = ApiIdConverter.getLocation(locationIDvalue);
			 if (!myLocation.isEmpty()) {
				 return myLocation.getLocation();
			 } else {
				 if (locationValue.replace(",", "").matches(CITADEL_MATCH)) {
					 return locationValue.replace(",", "").replaceAll(CITADEL_MATCH, CITADEL_REPLACE);
				 } else {
					 return locationValue;
				 }
			 }
		}
	}

	private Long resolveExistingLocationID(String locationValue) {
		for (MyLocation myLocation : StaticData.get().getLocations().values()) {
			if (myLocation.getLocation().equals(locationValue)) {
				return myLocation.getLocationID();
			}
		}
		return null;
	}

	private Long resolveUnknownLocationID(String locationValue, String match) {
		String number = locationValue.replaceAll(match, "$1"); //Try to resovle unknown location
		try {
			return Long.valueOf(number);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	public String getKey() {
		return this.location + " " + this.flag;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + Objects.hashCode(this.location);
		hash = 29 * hash + Objects.hashCode(this.flag);
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
		final AssetValue other = (AssetValue) obj;
		if (!Objects.equals(this.location, other.location)) {
			return false;
		}
		if (!Objects.equals(this.flag, other.flag)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(AssetValue o) {
		return this.getID().compareTo(o.getID());
	}
}
