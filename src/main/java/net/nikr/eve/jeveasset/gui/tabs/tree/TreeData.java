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
package net.nikr.eve.jeveasset.gui.tabs.tree;

import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsTree;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class TreeData extends TableData {

	private final Set<TreeAsset> locationsExport = new TreeSet<>(new TreeTab.AssetTreeComparator());
	private final Set<TreeAsset> locations = new TreeSet<>(new TreeTab.AssetTreeComparator());
	private final Set<TreeAsset> categoriesExport = new TreeSet<>(new TreeTab.AssetTreeComparator());
	private final Set<TreeAsset> categories = new TreeSet<>(new TreeTab.AssetTreeComparator());

	public TreeData(Program program) {
		super(program);
	}

	public TreeData(ProfileManager profileManager, ProfileData profileData) {
		super(profileManager, profileData);
	}

	public void updateData() {
		locations.clear();
		categories.clear();
		locationsExport.clear();
		categoriesExport.clear();

		Map<Flag, Set<String>> flagsNames = new HashMap<>();
		Flag shipHangar = new Flag(TabsTree.get().locationShipHangar(), Images.LOC_HANGAR_SHIPS.getIcon());
		flagsNames.put(new Flag(TabsTree.get().locationAssetSafety(), Images.LOC_SAFTY.getIcon()), Collections.singleton(ApiIdConverter.getFlag(36).getFlagName())); //FlagName AssetSafety  (Asset Safety)
		flagsNames.put(new Flag(TabsTree.get().locationItemHangar(), Images.LOC_HANGAR_ITEMS.getIcon()), Collections.singleton(ApiIdConverter.getFlag(4).getFlagName())); //FlagName Hangar
		Set<String> deliveries = new HashSet<>();
		deliveries.add(ApiIdConverter.getFlag(173).getFlagName()); //FlagName Deliveries 
		deliveries.add(ApiIdConverter.getFlag(62).getFlagName()); //FlagName CorpMarket (Corporation Deliveries)
		flagsNames.put(new Flag(TabsTree.get().locationDeliveries(), Images.LOC_DELIVERIES.getIcon()), deliveries);
		Set<String> industryJobs = new HashSet<>();
		industryJobs.add(General.get().industryJobFlag());
		industryJobs.add(MyIndustryJob.IndustryActivity.ACTIVITY_MANUFACTURING.toString()); //industry job manufacturing
		industryJobs.add(MyIndustryJob.IndustryActivity.ACTIVITY_REACTIONS.toString()); //industry job reactions
		flagsNames.put(new Flag(General.get().industryJobFlag(), Images.LOC_INDUSTRY.getIcon()), industryJobs);
		Set<String> contracts = new HashSet<>();
		contracts.add(General.get().contractExcluded());
		contracts.add(General.get().contractIncluded());
		flagsNames.put(new Flag(TabsTree.get().locationContracts(), Images.LOC_CONTRACTS.getIcon()), contracts);
		Set<String> marketOrders = new HashSet<>();
		marketOrders.add(General.get().marketOrderBuyFlag());
		marketOrders.add(General.get().marketOrderSellFlag());
		flagsNames.put(new Flag(TabsTree.get().locationMarketOrders(), Images.LOC_MARKET.getIcon()), marketOrders);
		Set<String> clones = new HashSet<>();
		clones.add(ApiIdConverter.getFlag(89).getFlagName());
		flagsNames.put(new Flag(TabsTree.get().locationClones(), Images.LOC_CLONEBAY.getIcon()), clones);

		MyLocation emptyLocation = new MyLocation(0, "", 0, "", 0, "", 0, "", "");

		Map<String, TreeAsset> categoryCache = new HashMap<>();
		Map<String, TreeAsset> locationCache = new HashMap<>();
		for (MyAsset asset : profileData.getAssetsList()) {
		//LOCATION
			List<TreeAsset> locationTree = new ArrayList<>();
			MyLocation location = asset.getLocation();

			//Region
			String regionKey = location.getRegion();
			TreeAsset regionAsset = locationCache.get(location.getRegion());
			if (regionAsset == null) {
				regionAsset = new TreeAsset(ApiIdConverter.getLocation(location.getRegionID()), location.getRegion(), regionKey, Images.LOC_REGION.getIcon(), locationTree);
				locationCache.put(regionKey, regionAsset);
				locationsExport.add(regionAsset);
			}
			locationTree.add(regionAsset);

			//System
			String systemKey = location.getRegion() + location.getSystem();
			TreeAsset systemAsset = locationCache.get(systemKey);
			if (systemAsset == null) {
				systemAsset = new TreeAsset(ApiIdConverter.getLocation(location.getSystemID()), location.getSystem(), systemKey, Images.LOC_SYSTEM.getIcon(), locationTree);
				locationCache.put(systemKey, systemAsset);
				locationsExport.add(systemAsset);
			}
			locationTree.add(systemAsset);

			String fullLocation = location.getRegion()+location.getSystem();
			//Station
			if (location.isStation() || location.isPlanet()) { //Station or Planet
				String stationKey = location.getRegion() + location.getSystem() + location.getLocation();
				TreeAsset stationAsset = locationCache.get(stationKey);
				if (stationAsset == null) {
					if (asset.getLocation().isPlanet()) {
						stationAsset = new TreeAsset(asset.getLocation(), location.getLocation(), stationKey, Images.LOC_PLANET.getIcon(), locationTree);
					} else {
						stationAsset = new TreeAsset(asset.getLocation(), location.getLocation(), stationKey, Images.LOC_STATION.getIcon(), locationTree);
					}
					locationCache.put(stationKey, stationAsset);
					locationsExport.add(stationAsset);
				}
				locationTree.add(stationAsset);
				fullLocation = location.getRegion()+location.getSystem()+location.getLocation();
			}

			//Add parent item(s)
			String parentKey = fullLocation;
			List<MyAsset> list = new ArrayList<>(asset.getParents()); //Copy
			if (asset.getAssets().isEmpty()) {
				list.add(asset);
			}
			if (!list.isEmpty()) {
				for (MyAsset parentAsset : list) {
					//Office
					MyAsset parent = parentAsset.getParent();
					if (parent != null && parent.getTypeID() == 27) { //Office divisions
						String cacheKey = parentAsset.getFlagName() + " #" + parent.getItemID();
						TreeAsset divisionAsset = locationCache.get(cacheKey);
						if (divisionAsset == null) {
							divisionAsset = new TreeAsset(location, parentAsset.getFlagName(), parentKey + cacheKey, Images.LOC_DIVISION.getIcon(), locationTree);
							locationCache.put(cacheKey, divisionAsset);
							locationsExport.add(divisionAsset);
						}
						parentKey = parentKey + cacheKey;
						locationTree.add(divisionAsset);
					}
					//Flags
					if (parent == null) {
						for (Map.Entry<Flag, Set<String>> entry: flagsNames.entrySet()) {
							if (entry.getValue().contains(parentAsset.getFlag())) {
								final Flag flag;
								if (entry.getKey().getName().equals("Item Hangar") && parentAsset.getItem().isShip()) {
									flag = shipHangar;
								} else {
									flag = entry.getKey();
								}
								String cacheKey = flag.getName() + "#" + parentAsset.getLocationID();
								TreeAsset hangarAsset = locationCache.get(cacheKey);
								if (hangarAsset == null) {
									hangarAsset = new TreeAsset(location, flag.getName(), parentKey + cacheKey, flag.getIcon(), locationTree);
									locationCache.put(cacheKey, hangarAsset);
									locationsExport.add(hangarAsset);
								}
								parentKey = parentKey + cacheKey;
								locationTree.add(hangarAsset);
							}
						}
					}
					//Item
					String cacheKey = parentAsset.getName() + " #" + parentAsset.getItemID();
					TreeAsset parentTreeAsset = locationCache.get(cacheKey);
					if (parentTreeAsset == null) {
						parentTreeAsset = new TreeAsset(parentAsset, TreeAsset.TreeType.LOCATION, locationTree, parentKey, !parentAsset.getAssets().isEmpty());
						locationCache.put(cacheKey, parentTreeAsset);
						locations.add(parentTreeAsset);
						locationsExport.add(parentTreeAsset);
					}
					parentKey = parentKey + parentAsset.getName() + " #" + parentAsset.getItemID();
					locationTree.add(parentTreeAsset);
				}
			}

		//CATEGORY
			List<TreeAsset> categoryTree = new ArrayList<>();

			//Category
			String categoryKey = asset.getItem().getCategory();
			TreeAsset categoryAsset = categoryCache.get(categoryKey);
			if (categoryAsset == null) {
				categoryAsset = new TreeAsset(emptyLocation, asset.getItem().getCategory(), categoryKey, null, categoryTree, 1);
				categoryCache.put(categoryKey, categoryAsset);
			}
			categoryTree.add(categoryAsset);
			categoriesExport.add(categoryAsset);

			//Group
			String groupKey = categoryKey + asset.getItem().getGroup();
			TreeAsset groupAsset = categoryCache.get(groupKey);
			if (groupAsset == null) {
				groupAsset = new TreeAsset(emptyLocation, asset.getItem().getGroup(), groupKey, null, categoryTree, 1);
				categoryCache.put(groupKey, groupAsset);
			}
			categoryTree.add(groupAsset);
			categoriesExport.add(groupAsset);

			//Item
			TreeAsset category = new TreeAsset(asset, TreeAsset.TreeType.CATEGORY, categoryTree, groupKey, false);
			categories.add(category);
			categoriesExport.add(category);
		}
	}

	public Set<TreeAsset> getLocationsExport() {
		return locationsExport;
	}

	public Set<TreeAsset> getLocations() {
		return locations;
	}

	public Set<TreeAsset> getCategoriesExport() {
		return categoriesExport;
	}

	public Set<TreeAsset> getCategories() {
		return categories;
	}

	public EventList<TreeAsset> getDataCategories() {
		updateData();
		return EventListManager.create(categoriesExport);
	}

	public EventList<TreeAsset> getDataLocations() {
		updateData();
		return EventListManager.create(locationsExport);
	}

	private static class Flag implements Comparable<Flag> {

		private final String name;
		private final Icon icon;

		public Flag(String name, Icon icon) {
			this.name = name;
			this.icon = icon;
		}

		public String getName() {
			return name;
		}

		public Icon getIcon() {
			return icon;
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 61 * hash + Objects.hashCode(this.name);
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
			final Flag other = (Flag) obj;
			if (!Objects.equals(this.name, other.name)) {
				return false;
			}
			return true;
		}

		@Override
		public int compareTo(Flag o) {
			return this.name.compareTo(o.name);
		}
	}
}
