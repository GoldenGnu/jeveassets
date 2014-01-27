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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;


public final class ApiIdConverter {

	private ApiIdConverter() { }

	private static final Map<String, Float> packagedVolume = new HashMap<String, Float>();

	private static void buildVolume() {
		packagedVolume.put("Assault Ship", 2500f);
		packagedVolume.put("Battlecruiser", 15000f);
		packagedVolume.put("Battleship", 50000f);
		packagedVolume.put("Black Ops", 50000f);
		packagedVolume.put("Capital Industrial Ship", 1000000f);
		packagedVolume.put("Capsule", 500f);
		packagedVolume.put("Carrier", 1000000f);
		packagedVolume.put("Combat Recon Ship", 10000f);
		packagedVolume.put("Command Ship", 15000f);
		packagedVolume.put("Covert Ops", 2500f);
		packagedVolume.put("Cruiser", 10000f);
		packagedVolume.put("Destroyer", 5000f);
		packagedVolume.put("Dreadnought", 1000000f);
		packagedVolume.put("Electronic Attack Ship", 2500f);
		packagedVolume.put("Elite Battleship", 50000f);
		packagedVolume.put("Exhumer", 3750f);
		packagedVolume.put("Force Recon Ship", 10000f);
		packagedVolume.put("Freighter", 1000000f);
		packagedVolume.put("Frigate", 2500f);
		packagedVolume.put("Heavy Assault Ship", 10000f);
		packagedVolume.put("Heavy Interdictor", 10000f);
		packagedVolume.put("Industrial", 20000f);
		packagedVolume.put("Industrial Command Ship", 500000f);
		packagedVolume.put("Interceptor", 2500f);
		packagedVolume.put("Interdictor", 5000f);
		packagedVolume.put("Jump Freighter", 1000000f);
		packagedVolume.put("Logistics", 10000f);
		packagedVolume.put("Marauder", 50000f);
		packagedVolume.put("Mining Barge", 3750f);
		packagedVolume.put("Prototype Exploration Ship", 500f);
		packagedVolume.put("Rookie ship", 2500f);
		packagedVolume.put("Shuttle", 500f);
		packagedVolume.put("Stealth Bomber", 2500f);
		packagedVolume.put("Strategic Cruiser", 5000f);
		packagedVolume.put("Supercarrier", 1000000f);
		packagedVolume.put("Titan", 10000000f);
		packagedVolume.put("Transport Ship", 20000f);
	}

	public static String flag(final int flag, final Asset parentAsset) {
		ItemFlag itemFlag = StaticData.get().getItemFlags().get(flag);
		if (itemFlag != null) {
			if (parentAsset != null && !parentAsset.getFlag().isEmpty()) {
				return parentAsset.getFlag() + " > " + itemFlag.getFlagName();
			} else {
				return itemFlag.getFlagName();
			}
		}
		return "!" + flag;
	}

	public static double getPrice(final int typeID, final boolean isBlueprintCopy) {
		return getPriceType(typeID, isBlueprintCopy, false);
	}

	private static double getPriceReprocessed(final int typeID, final boolean isBlueprintCopy) {
		return getPriceType(typeID, isBlueprintCopy, true);
	}

	private static double getPriceType(final int typeID, final boolean isBlueprintCopy, boolean reprocessed) {
		UserItem<Integer, Double> userPrice;
		if (isBlueprintCopy) { //Blueprint Copy
			userPrice = Settings.get().getUserPrices().get(-typeID);
		} else { //All other
			userPrice = Settings.get().getUserPrices().get(typeID);
		}
		if (userPrice != null) {
			return userPrice.getValue();
		}

		//Blueprint Copy (Default Zero)
		if (isBlueprintCopy) {
			return 0;
		}

		//Blueprints Base Price
		Item item = getItem(typeID);
		//Tech 1
		if (item.isBlueprint()) {
			if (Settings.get().isBlueprintBasePriceTech1() && !item.getTypeName().toLowerCase().contains("ii")) {
				return item.getPriceBase();
			}
			//Tech 2
			if (Settings.get().isBlueprintBasePriceTech2() && item.getTypeName().toLowerCase().contains("ii")) {
				return item.getPriceBase();
			}
		}

		//Price data
		PriceData priceData = Settings.get().getPriceData().get(typeID);
		if (priceData != null && priceData.isEmpty()) {
			priceData = null;
		}
		if (reprocessed) {
			return Settings.get().getPriceDataSettings().getDefaultPriceReprocessed(priceData);
		} else {
			return Settings.get().getPriceDataSettings().getDefaultPrice(priceData);
		}
	}

	public static double getPriceReprocessed(Item item) {
		double priceReprocessed = 0;
		int portionSize = 0;
		for (ReprocessedMaterial material : item.getReprocessedMaterial()) {
			//Calculate reprocessed price
			portionSize = material.getPortionSize();
			double price = ApiIdConverter.getPriceReprocessed(material.getTypeID(), false);
			priceReprocessed = priceReprocessed + (price * Settings.get().getReprocessSettings().getLeft(material.getQuantity()));
		}
		if (priceReprocessed > 0 && portionSize > 0) {
			priceReprocessed = priceReprocessed / portionSize;
		}
		return priceReprocessed;
	}

	public static float getVolume(final int typeID, final boolean packaged) {
		Item item = StaticData.get().getItems().get(typeID);
		if (item != null) {
			if (packagedVolume.isEmpty()) {
				buildVolume();
			}
			if (packaged && packagedVolume.containsKey(item.getGroup())) {
				return packagedVolume.get(item.getGroup());
			} else {
				return item.getVolume();
			}
		}
		return 0;
	}

	public static boolean isLocationOK(final long locationID) {
		return isLocationOK(locationID, null);
	}

	public static boolean isLocationOK(final long locationID, final Asset parentAsset) {
		Location location = getLocation(locationID, parentAsset);
		return location != null && !location.isEmpty();
	}

	public static Item getItem(final int typeID) {
		Item item = StaticData.get().getItems().get(typeID);
		if (item != null) {
			return item;
		} else {
			return new Item(typeID);
		}
	}

	public static String getOwnerName(final long ownerID) {
		if (ownerID == 0) { //0 (zero) is valid, but, should return empty string
			return "";
		}
		String owner = Settings.get().getOwners().get(ownerID);
		if (owner != null) {
			return owner;
		}
		return "!" + String.valueOf(ownerID);
	}

	public static List<Asset> getParents(final Asset parentAsset) {
		List<Asset> parents;
		if (parentAsset != null) {
			parents = new ArrayList<Asset>(parentAsset.getParents());
			parents.add(parentAsset);
		} else {
			parents = new ArrayList<Asset>();
		}
		
		return parents;
	}

	public static Location getLocation(long locationID) {
		return getLocation(locationID, null);
	}

	public static Location getLocation(long locationID, final Asset parentAsset) {
		//Offices
		if (locationID >= 66000000) {
			if (locationID < 66014933) {
				locationID = locationID - 6000001;
			} else {
				locationID = locationID - 6000000;
			}
		}
		Location location = StaticData.get().getLocations().get(locationID);
		if (location != null) {
			return location;
		}
		if (parentAsset != null) {
			location = parentAsset.getLocation();
			if (location != null) {
				return location;
			}
		}
		return new Location(locationID);
	}

	public static void addLocation(final ApiStation station) {
		Location system = getLocation(station.getSolarSystemID());
		Location location = new Location(station.getStationID(),
				station.getStationName(),
				system.getSystemID(),
				system.getSystem(),
				system.getRegionID(),
				system.getRegion(),
				system.getSecurity());
		StaticData.get().getLocations().put(location.getLocationID(), location);
	}
}
