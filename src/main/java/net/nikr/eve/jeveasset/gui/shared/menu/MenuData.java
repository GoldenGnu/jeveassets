/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.data.tag.Tag;
import net.nikr.eve.jeveasset.data.types.BlueprintType;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.LocationsType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.data.types.TagsType;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class MenuData<T> {

	private final Set<Integer> typeIDs = new HashSet<Integer>();
	private final Set<MyLocation> systemLocations = new HashSet<MyLocation>();
	private final Map<Integer, Double> prices = new HashMap<Integer, Double>();
	private final Set<String> typeNames = new HashSet<String>();
	private final Set<String> stationsAndCitadels = new HashSet<String>();
	private final Set<MyLocation> emptyStations = new HashSet<MyLocation>();
	private final Set<MyLocation> userStations = new HashSet<MyLocation>();
	private final Set<String> stations = new HashSet<String>();
	private final Set<String> systems = new HashSet<String>();
	private final Set<String> regions = new HashSet<String>();
	private final Set<Integer> marketTypeIDs = new HashSet<Integer>();
	private final Set<Integer> blueprintTypeIDs = new HashSet<Integer>();
	private final Map<Tag, Integer> tagCount = new HashMap<Tag, Integer>();
	private final List<TagsType> tags = new ArrayList<TagsType>();
	private final List<MyAsset> assets = new ArrayList<MyAsset>();

	public MenuData() { }

	public MenuData(final List<T> items) {
		if (items == null) { //Skip null
			return;
		}

		for (T t : items) {
			if (t == null) { //Skip null
				continue;
			}

			Set<MyLocation> locations = new HashSet<MyLocation>();
			if (t instanceof LocationType) {
				LocationType type = (LocationType) t;
				locations.add(type.getLocation());
			}

			if (t instanceof LocationsType) {
				LocationsType type = (LocationsType) t;
				locations.addAll(type.getLocations());
			}

			Item itemType = null;
			if (t instanceof ItemType) {
				ItemType type = (ItemType) t;
				itemType = type.getItem();
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
					price = ApiIdConverter.getPrice(item.getTypeID(), false);
				}
				if (price == null || price == 0) {
					price = (double) item.getPriceBase();
				}
			}

			add(itemType, locations, price, blueprint, tagsType);
		}
	}

	private void add(final Item item, final Collection<MyLocation> locations, final Double price, final BlueprintType blueprintType, final TagsType tagsType) {
		if (item != null && !item.isEmpty()) {
			//Type Name
			typeNames.add(item.getTypeName());
			//TypeID
			int typeID = item.getTypeID();
			typeIDs.add(typeID);
			
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
			blueprintTypeIDs.add(blueprintTypeID);
			//Price TypeID
			if (price != null) { //Not unique
				prices.put(blueprintTypeID, price);
			}
		}
		//Locations
		for (MyLocation location : locations) {
			if (location != null && !location.isEmpty()) {
				if (location.isStation()) { //Ignore citadels stations as they're not supported by dotlan
					if (!location.isCitadel()) {
						stations.add(location.getStation());
					}
					stationsAndCitadels.add(location.getStation());
					systemLocations.add(location);
				}
				if (location.isStation() || location.isSystem()) {
					systems.add(location.getSystem());
				}
				if (location.isStation() || location.isSystem() || location.isRegion()) {
					regions.add(location.getRegion());
				}
			}
			if (location != null && location.isEmpty()) {
				emptyStations.add(location);
			}
			if (location != null && location.isUserLocation()) {
				userStations.add(location);
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

	public Set<String> getStationsOnly() {
		return stations;
	}

	public Set<MyLocation> getEmptyStations() {
		return emptyStations;
	}

	public Set<MyLocation> getUserStations() {
		return userStations;
	}

	public Set<String> getStationsAndCitadels() {
		return stationsAndCitadels;
	}

	public Set<String> getSystems() {
		return systems;
	}

	public Set<MyLocation> getSystemLocations() {
		return systemLocations;
	}

	public Set<String> getRegions() {
		return regions;
	}

	public Set<Integer> getMarketTypeIDs() {
		return marketTypeIDs;
	}

	public Set<Integer> getBlueprintTypeIDs() {
		return blueprintTypeIDs;
	}

	public void setAssets(List<MyAsset> assets) {
		this.assets.clear();
		this.assets.addAll(assets);
	}

	public List<MyAsset> getAssets() {
		return assets;
	}
}
