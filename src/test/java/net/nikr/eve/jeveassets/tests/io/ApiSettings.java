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
package net.nikr.eve.jeveassets.tests.io;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.io.local.ConquerableStationsReader;
import net.nikr.eve.jeveasset.io.local.FlagsReader;
import net.nikr.eve.jeveasset.io.local.ItemsReader;
import net.nikr.eve.jeveasset.io.local.LocationsReader;
import net.nikr.eve.jeveassets.tests.mocks.FakeSettings;


public class ApiSettings extends FakeSettings {
	private List<Long> bpos = new ArrayList<Long>();
	private Map<Integer, ItemFlag> itemFlags = new HashMap<Integer, ItemFlag>();
	private Map<Long, Location> locations = new HashMap<Long, Location>();
	private Map<Long, ApiStation> conquerableStations = new HashMap<Long, ApiStation>();
	private Map<Integer, Item> items = new HashMap<Integer, Item>();

	public ApiSettings() {
		LocationsReader.load(this);
		FlagsReader.load(this);
		ConquerableStationsReader.load(this);
		ItemsReader.load(this);
	}

	@Override
	public Map<Integer, Item> getItems() {
		return items;
	}

	@Override
	public Map<Integer, ItemFlag> getItemFlags() {
		return itemFlags;
	}

	@Override
	public Map<Long, ApiStation> getConquerableStations() {
		return conquerableStations;
	}

	@Override
	public Map<Long, Location> getLocations() {
		return locations;
	}

	@Override
	public List<Long> getBpos() {
		return bpos;
	}
}
