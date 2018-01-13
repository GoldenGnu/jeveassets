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
package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.model.eve.Station;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.troja.eve.esi.model.SovereigntyStructuresResponse;
import net.troja.eve.esi.model.StructureResponse;

public final class ApiIdConverter {

	private ApiIdConverter() { }

	private static final Map<String, Float> PACKAGED_VOLUME = new HashMap<String, Float>();

	private static void buildVolume() {
		PACKAGED_VOLUME.put("Assault Ship", 2500f);
		PACKAGED_VOLUME.put("Battlecruiser", 15000f);
		PACKAGED_VOLUME.put("Battleship", 50000f);
		PACKAGED_VOLUME.put("Black Ops", 50000f);
		PACKAGED_VOLUME.put("Capital Industrial Ship", 1000000f);
		PACKAGED_VOLUME.put("Capsule", 500f);
		PACKAGED_VOLUME.put("Carrier", 1000000f);
		PACKAGED_VOLUME.put("Combat Recon Ship", 10000f);
		PACKAGED_VOLUME.put("Command Ship", 15000f);
		PACKAGED_VOLUME.put("Covert Ops", 2500f);
		PACKAGED_VOLUME.put("Cruiser", 10000f);
		PACKAGED_VOLUME.put("Destroyer", 5000f);
		PACKAGED_VOLUME.put("Dreadnought", 1000000f);
		PACKAGED_VOLUME.put("Electronic Attack Ship", 2500f);
		PACKAGED_VOLUME.put("Elite Battleship", 50000f);
		PACKAGED_VOLUME.put("Exhumer", 3750f);
		PACKAGED_VOLUME.put("Force Recon Ship", 10000f);
		PACKAGED_VOLUME.put("Freighter", 1000000f);
		PACKAGED_VOLUME.put("Frigate", 2500f);
		PACKAGED_VOLUME.put("Heavy Assault Ship", 10000f);
		PACKAGED_VOLUME.put("Heavy Interdictor", 10000f);
		PACKAGED_VOLUME.put("Industrial", 20000f);
		PACKAGED_VOLUME.put("Industrial Command Ship", 500000f);
		PACKAGED_VOLUME.put("Interceptor", 2500f);
		PACKAGED_VOLUME.put("Interdictor", 5000f);
		PACKAGED_VOLUME.put("Jump Freighter", 1000000f);
		PACKAGED_VOLUME.put("Logistics", 10000f);
		PACKAGED_VOLUME.put("Marauder", 50000f);
		PACKAGED_VOLUME.put("Mining Barge", 3750f);
		PACKAGED_VOLUME.put("Prototype Exploration Ship", 500f);
		PACKAGED_VOLUME.put("Rookie ship", 2500f);
		PACKAGED_VOLUME.put("Shuttle", 500f);
		PACKAGED_VOLUME.put("Stealth Bomber", 2500f);
		PACKAGED_VOLUME.put("Strategic Cruiser", 5000f);
		PACKAGED_VOLUME.put("Supercarrier", 1000000f);
		PACKAGED_VOLUME.put("Titan", 10000000f);
		PACKAGED_VOLUME.put("Transport Ship", 20000f);
	}

	public static String flag(final int flag, final MyAsset parentAsset) {
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

	public static ItemFlag getFlag(final int flag) {
		ItemFlag itemFlag = StaticData.get().getItemFlags().get(flag);
		if (itemFlag != null) {
			return itemFlag;
		} else {
			return new ItemFlag(flag, "Unknown", "Unknown");
		}
	}

	public static double getPrice(final Integer typeID, final boolean isBlueprintCopy) {
		return getPriceType(typeID, isBlueprintCopy, false);
	}

	private static double getPriceReprocessed(final Integer typeID, final boolean isBlueprintCopy) {
		return getPriceType(typeID, isBlueprintCopy, true);
	}

	private static double getPriceType(final Integer typeID, final boolean isBlueprintCopy, boolean reprocessed) {
		if (typeID == null) {
			return 0;
		}
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
			priceReprocessed = priceReprocessed + (price * Settings.get().getReprocessSettings().getLeft(material.getQuantity(), item.isOre()));
		}
		if (priceReprocessed > 0 && portionSize > 0) {
			priceReprocessed = priceReprocessed / portionSize;
		}
		return priceReprocessed;
	}

	public static float getVolume(final int typeID, final boolean packaged) {
		Item item = StaticData.get().getItems().get(typeID);
		if (item != null) {
			if (PACKAGED_VOLUME.isEmpty()) {
				buildVolume();
			}
			if (packaged && PACKAGED_VOLUME.containsKey(item.getGroup())) {
				return PACKAGED_VOLUME.get(item.getGroup());
			} else {
				return item.getVolume();
			}
		}
		return 0;
	}

	public static Item getItem(final int typeID) {
		Item item = StaticData.get().getItems().get(typeID);
		if (item != null) {
			return item;
		} else {
			return new Item(typeID);
		}
	}

	public static String getOwnerName(final Integer ownerID) {
		if (ownerID == null) {
			return "";
		} else {
			return getOwnerName(Long.valueOf(ownerID));
		}
	}

	public static String getOwnerName(final Long ownerID) {
		if (ownerID == null || ownerID == 0) { //0 (zero) is valid, but, should return empty string
			return "";
		}
		String owner = Settings.get().getOwners().get(ownerID);
		if (owner != null) {
			return owner;
		} else { // OwnerIDs from the journal can be a system ID
			MyLocation location = getLocation(ownerID);
			if (!location.isEmpty()) {
				return location.getLocation();
			}
		}
		return "!" + String.valueOf(ownerID);
	}

	public static List<MyAsset> getParents(final MyAsset parentAsset) {
		List<MyAsset> parents;
		if (parentAsset != null) {
			parents = new ArrayList<MyAsset>(parentAsset.getParents());
			parents.add(parentAsset);
		} else {
			parents = new ArrayList<MyAsset>();
		}

		return parents;
	}

	public static boolean isLocationOK(final Long locationID) {
		return !getLocation(locationID, null).isEmpty();
	}

	public static MyLocation getLocation(Integer locationID) {
		if (locationID == null) {
			return new MyLocation(0);
		} else {
			return getLocation(Long.valueOf(locationID), null);
		}
	}

	public static MyLocation getLocation(Long locationID) {
		return getLocation(locationID, null);
	}

	public static MyLocation getLocation(final Long locationID, final MyAsset parentAsset) {
		if (locationID == null) {
			return new MyLocation(0);
		}
		//Offices
		long fixedLocationID = locationID;
		if (fixedLocationID >= 66000000) {
			if (fixedLocationID < 66014933) {
				fixedLocationID = fixedLocationID - 6000001;
			} else {
				fixedLocationID = fixedLocationID - 6000000;
			}
		}
		MyLocation location = StaticData.get().getLocations().get(fixedLocationID);
		if (location != null) {
			return location;
		}
		if (parentAsset != null) {
			location = parentAsset.getLocation();
			if (location != null) {
				return location;
			}
		}
		location = CitadelGetter.get(locationID).getLocation();
		if (location != null) {
			return location;
		}
		return new MyLocation(locationID);
	}

	public static void addLocation(final Citadel citadel, long locationID) {
		MyLocation location = citadel.getLocation();
		if (location != null) {
			StaticData.get().getLocations().put(location.getLocationID(), location);
		}
	}

	public static void removeLocation(final long locationID) {
		StaticData.get().getLocations().remove(locationID);
	}

	public static void addLocation(final Station station) {
		MyLocation system = getLocation(station.getSolarSystemID());
		MyLocation location = new MyLocation(station.getStationID(),
				station.getStationName(),
				system.getSystemID(),
				system.getSystem(),
				system.getRegionID(),
				system.getRegion(),
				system.getSecurity());
		StaticData.get().getLocations().put(location.getLocationID(), location);
	}

	public static Citadel getCitadel(final SovereigntyStructuresResponse station, String name) {
		MyLocation system = getLocation(station.getSolarSystemId());
		return new Citadel(station.getStructureId(), name, system.getSystemID(), system.getSystem(), system.getRegionID(), system.getRegion(), false, false);
	}

	public static Citadel getCitadel(final StructureResponse response, final long locationID) {
		MyLocation system = getLocation(response.getSolarSystemId());
		return new Citadel(locationID, response.getName(), response.getSolarSystemId(), system.getSystem(), system.getRegionID(), system.getRegion(), false, true);
	}
}
