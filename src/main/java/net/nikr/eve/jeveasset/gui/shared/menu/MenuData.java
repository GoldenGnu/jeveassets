/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceItem;
import net.nikr.eve.jeveasset.data.settings.tag.Tag;
import net.nikr.eve.jeveasset.data.settings.types.BlueprintType;
import net.nikr.eve.jeveasset.data.settings.types.ContractPriceType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.data.settings.types.LocationsType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.data.settings.types.PriceType;
import net.nikr.eve.jeveasset.data.settings.types.TagsType;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class MenuData<T> {

	private final Set<Integer> typeIDs = new HashSet<>();
	private final Set<MyLocation> autopilotStationLocations = new HashSet<>();
	private final Set<MyLocation> systemLocations = new HashSet<>();
	private final Set<MyLocation> regionLocations = new HashSet<>();
	private final Set<MyLocation> constellationLocations = new HashSet<>();
	private final Set<MyLocation> editableCitadelLocations = new HashSet<>();
	private final Set<MyLocation> userLocations = new HashSet<>();
	private final Map<Integer, Double> prices = new HashMap<>();
	private final Set<String> typeNames = new HashSet<>();
	private final Set<String> stationsAndCitadelsNames = new HashSet<>();
	private final Set<String> stationNames = new HashSet<>();
	private final Set<String> planetNames = new HashSet<>();
	private final Set<String> systemNames = new HashSet<>();
	private final Set<String> constellationNames = new HashSet<>();
	private final Set<String> regionNames = new HashSet<>();
	private final Set<Integer> marketTypeIDs = new HashSet<>();
	private final Set<Integer> blueprintTypeIDs = new HashSet<>();
	private final Set<Integer> inventionTypeIDs = new HashSet<>();
	private final Set<Integer> bpcTypeIDs = new HashSet<>();
	private final Set<Long> ownerIDs = new HashSet<>();
	private final Map<Tag, Integer> tagCount = new HashMap<>();
	private final List<TagsType> tags = new ArrayList<>();
	private final List<MyAsset> assets = new ArrayList<>();
	private final Set<MyContract> contracts = new HashSet<>();
	private final Map<Item, Long> itemCounts = new HashMap<>();
	private final List<ContractPriceItem> contractPriceItems = new ArrayList<>();

	public MenuData() { }

	public MenuData(final List<T> items) {
		if (items == null) { //Skip null
			return;
		}

		for (T t : items) {
			if (t == null) { //Skip null
				continue;
			}

			Set<MyLocation> locations = new HashSet<>();
			if (t instanceof LocationType) {
				LocationType type = (LocationType) t;
				locations.add(type.getLocation());
			}

			if (t instanceof LocationsType) {
				LocationsType type = (LocationsType) t;
				locations.addAll(type.getLocations());
			}

			Set<Long> owners = new HashSet<>();
			if (t instanceof OwnersType) {
				OwnersType ownersType = (OwnersType) t;
				owners.addAll(ownersType.getOwners());
			}

			Item itemType = null;
			if (t instanceof ItemType) {
				ItemType type = (ItemType) t;
				itemType = type.getItem();
				Long count = itemCounts.get(itemType);
				if (count == null) {
					count = 0L;
				}
				itemCounts.put(itemType, (count + type.getItemCount()));
			}

			BlueprintType blueprint = null;
			if (t instanceof BlueprintType) {
				blueprint = (BlueprintType) t;
			}

			Double price = null;
			if (t instanceof PriceType) {
				PriceType priceType = (PriceType) t;
				price = priceType.getDynamicPrice();
			}

			TagsType tagsType = null;
			if (t instanceof TagsType) {
				tagsType = (TagsType) t;
			}

			if (t instanceof Item) {
				Item item = (Item) t;
				if (items.size() == 1) { //Always zero for multiple items
					price = ApiIdConverter.getPriceSimple(item.getTypeID(), false);
				}
				if (price == null || price == 0) {
					price = item.getPriceBase();
				}
			}

			ContractPriceItem contractPriceItem = null;
			if (t instanceof ContractPriceType) {
				contractPriceItem = ContractPriceItem.create((ContractPriceType) t);
			}

			add(itemType, locations, price, blueprint, tagsType, owners, contractPriceItem);
		}
	}

	private void add(final Item item, final Collection<MyLocation> locations, final Double price, final BlueprintType blueprintType, final TagsType tagsType, Set<Long> owners, ContractPriceItem contractPriceItem) {
		if (item != null && !item.isEmpty()) {
			//Type Name
			typeNames.add(item.getTypeName());
			//TypeID
			int typeID = item.getTypeID();
			typeIDs.add(typeID);

			if (item.isBlueprint()) {
				blueprintTypeIDs.add(item.getTypeID());
				if (item.getMeta() == 0 && item.getTypeName().contains(" I ")) {
					inventionTypeIDs.add(item.getTypeID());
				}
			} else if (item.isProduct()) {
				blueprintTypeIDs.add(item.getBlueprintTypeID());
				if (item.getMeta() == 0 && item.getTypeName().endsWith(" I")) {
					inventionTypeIDs.add(item.getBlueprintTypeID());
				}
			}
			//Market TypeID
			if (item.isMarketGroup()) {
				marketTypeIDs.add(typeID);
			}
			//Blueprint TypeID
			int blueprintTypeID;
			if (blueprintType != null && blueprintType.isBPC()) {
				blueprintTypeID = -typeID;
			} else {
				blueprintTypeID = typeID;
			}
			bpcTypeIDs.add(blueprintTypeID);
			//Price TypeID
			if (price != null) { //Not unique
				prices.put(blueprintTypeID, price);
			}
		}
		if (ContractPriceManager.get().haveContractPrice(contractPriceItem)) {
			contractPriceItems.add(contractPriceItem);
		}

		ownerIDs.addAll(owners);

		//Locations
		for (MyLocation location : locations) {
			if (location == null) {
				continue;
			}
			if ((location.isEmpty() && location.getLocationID() != 0) || location.isUserLocation()){ //Empty with locationID or user
				editableCitadelLocations.add(location);
			}
			if (location.isUserLocation()) { //User
				userLocations.add(location);
			}
			if (location.getLocationID() != 0 && (location.isStation() || location.isEmpty())) { //Any station with a locationID (not planets)
				stationsAndCitadelsNames.add(location.getStation()); //Assets Station (support citadels)
				autopilotStationLocations.add(location); //Autopilot Station (support citadels)
			}
			if (location.isEmpty()) {
				continue; //Ignore empty locations for the rest of the loop
			}
			//Staion
			if (location.isStation() && !location.isCitadel()) { //Not planet
				stationNames.add(location.getStation()); //Dotlan Station (does not support citadels)
			}
			//Planet
			if (location.isPlanet()) {
				planetNames.add(location.getLocation()); //Dotlan + Assets Planet
			}
			//System
			if (location.isStation()  || location.isPlanet() || location.isSystem()) { //Station, Planet, or System
				systemNames.add(location.getSystem()); //Dotlan + Assets System
				//Jumps
				MyLocation system = ApiIdConverter.getLocation(location.getSystemID());
				if (!system.isEmpty()) {
					systemLocations.add(system); //Jumps + Autopilot + zKillboard System 
				}
			}
			//Constellation
			if (location.isStation()  || location.isPlanet() || location.isSystem() || location.isConstellation()) {  //Station, Planet, System or Constellation
				constellationNames.add(location.getConstellation()); //Assets Constellation
				MyLocation constellation = ApiIdConverter.getLocation(location.getConstellationID());
				if (!constellation.isEmpty()) {
					constellationLocations.add(constellation); //Dotlan + zKillboard Constellation
				}
			}
			//Region
			if (location.isStation()  || location.isPlanet() || location.isSystem() || location.isConstellation() || location.isRegion()) {  //Station, Planet, System, Constellation or Region
				regionNames.add(location.getRegion()); //Dotlan + Assets Region
				MyLocation region = ApiIdConverter.getLocation(location.getRegionID());
				if (!region.isEmpty()) {
					regionLocations.add(region); //zKillboard Region
				}
			}
		}
		//Tags
		if (tagsType != null && tagsType.getTags() != null) {
			tags.add(tagsType);
			for (Tag tagKey : tagsType.getTags()) {
				Integer count = tagCount.get(tagKey);
				if (count != null) {
					count++;
				} else {
					count = 1;
				}
				tagCount.put(tagKey, count);
			}
		}
	}

	public Map<Tag, Integer> getTagCount() {
		return tagCount;
	}

	public List<TagsType> getTags() {
		return tags;
	}

	public Set<Integer> getTypeIDs() {
		return typeIDs;
	}

	public Map<Integer, Double> getPrices() {
		return prices;
	}

	public Set<String> getTypeNames() {
		return typeNames;
	}

	//JMenuAssetFilter
	public Set<String> getStationAndCitadelNames() {
		return stationsAndCitadelsNames;
	}

	//JMenuLookup
	public Set<String> getStationNames() {
		return stationNames;
	}

	//JMenuLookup + JMenuAssetFilter
	public Set<String> getPlanetNames() {
		return planetNames;
	}

	//JMenuLookup + JMenuAssetFilter
	public Set<String> getSystemNames() {
		return systemNames;
	}

	//JMenuAssetFilter
	public Set<String> getConstellationNames() {
		return constellationNames;
	}

	//JMenuLookup + JMenuAssetFilter
	public Set<String> getRegionNames() {
		return regionNames;
	}

	//JMenuLookup
	public Set<MyLocation> getRegionLocations() {
		return regionLocations;
	}

	//JMenuLookup
	public Set<MyLocation> getConstellationLocations() {
		return constellationLocations;
	}

	//JMenuJumps and JMenuUI
	public Set<MyLocation> getSystemLocations() {
		return systemLocations;
	}

	//JMenuUI
	public Set<MyLocation> getAutopilotStationLocations() {
		return autopilotStationLocations;
	}

	//JMenuLocation
	public Set<MyLocation> getUserLocations() {
		return userLocations;
	}

	//JMenuLocation
	public Set<MyLocation> getEditableCitadelLocations() {
		return editableCitadelLocations;
	}

	public Set<Integer> getMarketTypeIDs() {
		return marketTypeIDs;
	}

	public List<ContractPriceItem> getContractPriceItems() {
		return contractPriceItems;
	}

	public Set<Integer> getBpcTypeIDs() {
		return bpcTypeIDs;
	}

	public Set<Integer> getBlueprintTypeIDs() {
		return blueprintTypeIDs;
	}

	public Set<Integer> getInventionTypeIDs() {
		return inventionTypeIDs;
	}

	public Set<Long> getOwnerIDs() {
		return ownerIDs;
	}

	public <T extends MyAsset> void setAssets(List<T> assets) {
		this.assets.clear();
		this.assets.addAll(assets);
	}

	public List<MyAsset> getAssets() {
		return assets;
	}

	public Set<MyContract> getContracts() {
		return contracts;
	}

	public void setContracts(Set<MyContract> contracts) {
		this.contracts.clear();
		this.contracts.addAll(contracts);
	}

	public Map<Item, Long> getItemCounts() {
		return itemCounts;
	}

	public static class AssetMenuData <T extends MyAsset> extends MenuData<T> {

		public AssetMenuData(List<T> items) {
			super(items);
			setAssets(items);
		}
	}
}
