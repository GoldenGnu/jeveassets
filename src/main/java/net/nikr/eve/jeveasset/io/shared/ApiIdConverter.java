/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.Location;


public final class ApiIdConverter {

	private ApiIdConverter() { }

	public static String flag(final int flag, final Asset parentAsset, final Map<Integer, ItemFlag> flags) {
		ItemFlag itemFlag = flags.get(flag);
		if (itemFlag != null) {
			if (parentAsset != null && !parentAsset.getFlag().isEmpty()) {
				return parentAsset.getFlag() + " > " + itemFlag.getFlagName();
			} else {
				return itemFlag.getFlagName();
			}
		}
		return "!" + flag;
	}

	public static boolean locationTest(final long locationID, final Asset parentAsset, final Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			return true;
		}

		if (parentAsset != null) {
			return true;
		}
		return false;
	}

	private static Location location(long locationID, final Map<Long, Location> locations) {
		//Offices
		if (locationID >= 66000000) {
			if (locationID < 66014933) {
				locationID = locationID - 6000001;
			} else {
				locationID = locationID - 6000000;
			}
		}

		//locations.xml (staStations && mapDenormalize)
		return locations.get(locationID);
	}

	public static String locationName(final long locationID, final Asset parentAsset, final Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			return location.getName();
		}

		if (parentAsset != null) {
			return parentAsset.getLocation();
		}
		return "Error !" + String.valueOf(locationID);
	}

	public static String regionName(final long locationID, final Asset parentAsset, final Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			location = locations.get(location.getRegionID());
			if (location != null) {
				return location.getName();
			}
		}
		if (parentAsset != null) {
			return parentAsset.getRegion();
		}
		return "Error !" + String.valueOf(locationID);
	}

	public static String security(final long locationID, final Asset parentAsset, final Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			return location.getSecurity();
		}

		if (parentAsset != null) {
			return parentAsset.getSecurity();
		}
		return "Error !" + String.valueOf(locationID);
	}

	public static String systemName(final long locationID, final Asset parentAsset, final Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			location = locations.get(location.getSystemID());
			if (location != null) {
				return location.getName();
			}
		}
		if (parentAsset != null) {
			return parentAsset.getSystem();
		}
		return "Error !" + String.valueOf(locationID);
	}
	public static long systemID(final long locationID, final Asset parentAsset, final Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			return location.getSystemID();
		}
		if (parentAsset != null) {
			return parentAsset.getSolarSystemID();
		}
		return -locationID;
	}

	public static long regionID(final long locationID, final Asset parentAsset, final Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			return location.getRegionID();
		}
		if (parentAsset != null) {
			return parentAsset.getRegionID();
		}
		return -locationID;
	}

	public static float volume(final int typeID, final Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getVolume();
		}
		return -1;
	}

	public static String typeName(final int typeID, final Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getName();
		}
		return "!" + String.valueOf(typeID);
	}

	public static String ownerName(final long ownerID, final Map<Long, String> owners) {
		if (ownerID == 0) { //0 (zero) is valid, but, should return empty string
			return "";
		}
		String owner = owners.get(ownerID);
		if (owner != null) {
			return owner;
		}
		return "!" + String.valueOf(ownerID);
	}

	public static double priceBase(final int typeID, final Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getPrice();
		}
		return -1;
	}

	public static String category(final int typeID, final Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getCategory();
		}
		return "";
	}

	public static String group(final int typeID, final Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getGroup();
		}
		return "";
	}

	public static int meta(final int typeID, final Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getMeta();
		}
		return 0;
	}
	public static String tech(final int typeID, final Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getTech();
		}
		return "";
	}
	public static boolean piMaterial(final int typeID, final Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.isPiMaterial();
		}
		return false;
	}

	public static boolean marketGroup(final int typeID, final Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.isMarketGroup();
		}
		return false;
	}

	public static List<Asset> parents(final Asset parentEveAsset) {
		List<Asset> parents;
		if (parentEveAsset != null) {
			parents = new ArrayList<Asset>(parentEveAsset.getParents());
			parents.add(parentEveAsset);
		} else {
			parents = new ArrayList<Asset>();
		}
		
		return parents;
	}

	public static void addLocation(final ApiStation station, final Map<Long, Location> locations) {
		long regionID = ApiIdConverter.location(station.getSolarSystemID(), locations).getRegionID();
		String security = ApiIdConverter.security(station.getSolarSystemID(), null, locations);
		Location location = new Location(station.getStationID(), station.getStationName(), regionID, security, station.getSolarSystemID());
		locations.put(location.getLocationID(), location);
	}
}
