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
	Map<Integer, Location> locations = new HashMap<Integer, Location>();

	public RoutingMockSettings() {
		LocationsReader.load(this);
		JumpsReader.load(this);
	}

	@Override
	public List<Jump> getJumps() {
		return jumps;
	}

	@Override
	public Map<Integer, Location> getLocations() {
		return locations;
	}



}
