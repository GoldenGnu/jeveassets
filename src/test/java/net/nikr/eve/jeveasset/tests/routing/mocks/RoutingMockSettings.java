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

package net.nikr.eve.jeveassets.tests.routing.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Jump;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.io.local.JumpsReader;
import net.nikr.eve.jeveasset.io.local.LocationsReader;
import net.nikr.eve.jeveassets.tests.mocks.FakeSettings;

/**
 *
 * @author Candle
 */
public class RoutingMockSettings extends FakeSettings {

	List<Jump> jumps = new ArrayList<Jump>();
	Map<Long, Location> locations = new HashMap<Long, Location>();

	public RoutingMockSettings() {
		LocationsReader.load(this);
		JumpsReader.load(this);
	}

	@Override
	public List<Jump> getJumps() {
		return jumps;
	}

	@Override
	public Map<Long, Location> getLocations() {
		return locations;
	}



}
