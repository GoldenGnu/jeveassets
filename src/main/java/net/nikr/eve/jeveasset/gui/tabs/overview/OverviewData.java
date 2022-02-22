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
package net.nikr.eve.jeveasset.gui.tabs.overview;

import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab.View;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class OverviewData extends TableData {

	private final List<String> groupedLocations = new ArrayList<>();
	private int rowCount;

	public OverviewData(Program program) {
		super(program);
	}

	public OverviewData(ProfileManager profileManager, ProfileData profileData) {
		super(profileManager, profileData);
	}

	public EventList<Overview> getData(final Collection<MyAsset> assets, final String owner, final View view) {
		EventList<Overview> eventList = EventListManager.create();
		updateData(eventList, assets, owner, view);
		return eventList;
	}

	public void updateData(EventList<Overview> eventList, final Collection<MyAsset> assets, String owner, final View view) {
		if (owner == null || owner.isEmpty()) {
			owner = General.get().all();
		}
		groupedLocations.clear();
		List<Overview> locations = new ArrayList<>();
		Map<String, Overview> locationsMap = new HashMap<>();
		rowCount = 0;
		if (view == OverviewTab.View.GROUPS) { //Add all groups
			for (Map.Entry<String, OverviewGroup> entry : Settings.get().getOverviewGroups().entrySet()) {
				OverviewGroup overviewGroup = entry.getValue();
				if (!locationsMap.containsKey(overviewGroup.getName())) { //Create new overview
					Overview overview = new Overview(overviewGroup.getName(), MyLocation.create(0), 0, 0, 0, 0);
					locationsMap.put(overviewGroup.getName(), overview);
					locations.add(overview);
				}
			}
		} else { //Add all grouped locations
			for (Map.Entry<String, OverviewGroup> entry : Settings.get().getOverviewGroups().entrySet()) {
				OverviewGroup overviewGroup = entry.getValue();
				for (OverviewLocation overviewLocation : overviewGroup.getLocations()) {
					if (!groupedLocations.contains(overviewLocation.getName())) {
						groupedLocations.add(overviewLocation.getName());
					}
				}
			}
		}
		final boolean all = owner.equals(General.get().all());
		for (MyAsset asset : assets) {
			if (asset.getItem().isContainer() && Settings.get().isIgnoreSecureContainers()) {
				continue;
			}
			if (asset.getItem().getGroup().equals(Item.GROUP_STATION_SERVICES)) {
				continue;
			}
			//Filters
			if (!owner.equals(asset.getOwnerName()) && !all) {
				continue;
			}

			rowCount++;

			double reprocessedValue = asset.getValueReprocessed();
			double value = asset.getValue();
			long count = asset.getCount();
			double volume = asset.getVolumeTotal();
			if (view != View.GROUPS) { //Locations
				String locationName = "";
				MyLocation location = asset.getLocation();
				if (view == View.STATIONS && location.isPlanet()) {
					continue;
				}
				if (view == View.PLANETS && !location.isPlanet()) {
					continue;
				}
				if (!location.isEmpty()) { //Always use the default location for empty locations
					if (view == View.REGIONS) {
						locationName = asset.getLocation().getRegion();
						location = ApiIdConverter.getLocation(asset.getLocation().getRegionID());
					} else if (view == View.CONSTELLATIONS) {
						locationName = asset.getLocation().getConstellation();
						location = ApiIdConverter.getLocation(asset.getLocation().getConstellationID());
					} else if (view == View.SYSTEMS) {
						locationName = asset.getLocation().getSystem();
						location = ApiIdConverter.getLocation(asset.getLocation().getSystemID());
					} else if (view == View.PLANETS) {
						locationName = asset.getLocation().getLocation();
						location = ApiIdConverter.getLocation(asset.getLocation().getLocationID());
					} else if (view == View.STATIONS) {
						locationName = asset.getLocation().getLocation();
						location = ApiIdConverter.getLocation(asset.getLocation().getLocationID());
					}
				} else {
					locationName = location.getLocation();
				}
				if (locationsMap.containsKey(locationName)) { //Update existing overview
					Overview overview = locationsMap.get(locationName);
					overview.addCount(count);
					overview.addValue(value);
					overview.addVolume(volume);
					overview.addReprocessedValue(reprocessedValue);
				} else { //Create new overview
					Overview overview = new Overview(locationName, location, reprocessedValue, volume, count, value);
					locationsMap.put(locationName, overview);
					locations.add(overview);
				}
			} else { //Groups
				for (Map.Entry<String, OverviewGroup> entry : Settings.get().getOverviewGroups().entrySet()) {
					OverviewGroup overviewGroup = entry.getValue();
					for (OverviewLocation overviewLocation : overviewGroup.getLocations()) {
						if (overviewLocation.equalsLocation(asset)) { //Update existing overview (group)
							Overview overview = locationsMap.get(overviewGroup.getName());
							overview.addCount(count);
							overview.addValue(value);
							overview.addVolume(volume);
							overview.addReprocessedValue(reprocessedValue);
							break; //Only add once....
						}
					}
				}
			}
		}
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(locations);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	public List<String> getGroupedLocations() {
		return groupedLocations;
	}

	public int getRowCount() {
		return rowCount;
	}
}
