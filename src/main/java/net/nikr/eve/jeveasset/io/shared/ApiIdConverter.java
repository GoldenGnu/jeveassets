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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.Location;


public class ApiIdConverter {

	private ApiIdConverter() {}

	public static String flag(int flag, Map<Integer, ItemFlag> flags) {
		ItemFlag itemFlag = flags.get(flag);
		return itemFlag.getFlagName();
	}

	public static boolean locationTest(long locationID, Asset parentAsset, Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			return true;
		}

		if (parentAsset != null) {
			return true;
		}
		return false;
	}
	
	private static Location location(long locationID, Map<Long, Location> locations) {
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

	public static String locationName(long locationID, Asset parentAsset, Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			return location.getName();
		}

		if (parentAsset != null) {
			return parentAsset.getLocation();
		}
		return "Error !" + String.valueOf(locationID);
	}

	public static String regionName(long locationID, Asset parentAsset, Map<Long, Location> locations) {
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

	public static String security(long locationID, Asset parentAsset, Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			return location.getSecurity();
		}

		if (parentAsset != null) {
			return parentAsset.getSecurity();
		}
		return "Error !" + String.valueOf(locationID);
	}

	public static String systemName(long locationID, Asset parentAsset, Map<Long, Location> locations) {
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
	public static long systemID(long locationID, Asset parentAsset, Map<Long, Location> locations) {
		Location location = location(locationID, locations);
		if (location != null) {
			return location.getSystemID();
		}
		if (parentAsset != null) {
			return parentAsset.getSolarSystemID();
		}
		return -locationID;
	}

	public static float volume(int typeID, Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getVolume();
		}
		return -1;
	}

	public static String typeName(int typeID, Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getName();
		}
		return "!" + String.valueOf(typeID);
	}

	public static double priceBase(int typeID, Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getPrice();
		}
		return -1;
	}

	public static String category(int typeID, Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getCategory();
		}
		return "";
	}

	public static String group(int typeID, Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getGroup();
		}
		return "";
	}

	public static String meta(int typeID, Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.getMeta();
		}
		return "";
	}
	public static boolean piMaterial(int typeID, Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.isPiMaterial();
		}
		return false;
	}

	public static boolean marketGroup(int typeID, Map<Integer, Item> items) {
		Item item = items.get(typeID);
		if (item != null) {
			return item.isMarketGroup();
		}
		return false;
	}

	public static List<Asset> parents(Asset parentEveAsset) {
		List<Asset> parents = new ArrayList<Asset>();
		if (parentEveAsset != null){
			for (int a = 0; a < parentEveAsset.getParents().size(); a++){
				parents.add(parentEveAsset.getParents().get(a));
			}
			parents.add(parentEveAsset);
		}
		return parents;
	}
	
	public static void addLocation(ApiStation station, Map<Long, Location> locations){
		long regionID = ApiIdConverter.location(station.getSolarSystemID(), locations).getRegionID();
		String security = ApiIdConverter.security(station.getSolarSystemID(), null, locations);
		Location location = new Location(station.getStationID(), station.getStationName(), regionID, security, station.getSolarSystemID());
		locations.put(location.getLocationID(), location);
	}
}
