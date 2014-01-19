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
package net.nikr.eve.jeveasset.data;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.io.local.ConquerableStationsReader;
import net.nikr.eve.jeveasset.io.local.FlagsReader;
import net.nikr.eve.jeveasset.io.local.ItemsReader;
import net.nikr.eve.jeveasset.io.local.JumpsReader;
import net.nikr.eve.jeveasset.io.local.LocationsReader;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StaticData {
	//Data
	private final Map<Integer, Item> items = new HashMap<Integer, Item>(); //TypeID : int
	private final Map<Integer, ItemFlag> itemFlags = new HashMap<Integer, ItemFlag>(); //FlagID : int
	private final Map<Long, Location> locations = new HashMap<Long, Location>(); //LocationID : long
	private final List<Jump> jumps = new ArrayList<Jump>(); //LocationID : long
	//XXX - Integer locationID
	private final Map<Integer, ApiStation> conquerableStations = new HashMap<Integer, ApiStation>(); //LocationID : long

	private static StaticData staticData = null;

	private StaticData() {}

	public static StaticData get() {
		load();
		return staticData;
	}

	public static void load() {
		if (staticData == null) {
			staticData = new StaticData();
			staticData.loadData();
		}
	}

	private void loadData() {
		SplashUpdater.setProgress(5);
		ItemsReader.load(); //Items
		SplashUpdater.setProgress(10);
		LocationsReader.load(); //Locations
		SplashUpdater.setProgress(15);
		JumpsReader.load(); //Jumps
		SplashUpdater.setProgress(20);
		FlagsReader.load(); //Item Flags
		ConquerableStationsReader.load(); //Conquerable Stations
		SplashUpdater.setProgress(25);
	}

	public Map<Integer, ItemFlag> getItemFlags() {
		return itemFlags;
	}

	public Map<Integer, Item> getItems() {
		return items;
	}

	public List<Jump> getJumps() {
		return jumps;
	}

	public Map<Long, Location> getLocations() {
		return locations;
	}

	public Map<Integer, ApiStation> getConquerableStations() {
		return conquerableStations;
	}

	public void setConquerableStations(final Map<Integer, ApiStation> conquerableStations) {
		this.conquerableStations.clear();
		this.conquerableStations.putAll(conquerableStations);
		for (ApiStation station : conquerableStations.values()) {
			ApiIdConverter.addLocation(station);
		}
	}
}
