/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.PriceDataSettings.RegionType;
import net.nikr.eve.jeveasset.io.local.LocationsReader;
import net.nikr.eve.jeveasset.tests.mocks.FakeSettings;
import org.junit.*;
import static org.junit.Assert.*;


public class PriceDataSettingsTest {

	public PriceDataSettingsTest() { }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGetRegion() {
		Settings settings = new PriceDataSettingsMock();
		for (RegionType regionType : RegionType.values()) {
			List<Long> regions = regionType.getRegions();
			if (regions.size() == 1) { //Single location
				Location location = settings.getLocations().get(regions.get(0));
				String locationName = regionType.toString();
				if (locationName.contains("(")) {
					locationName = locationName.substring(0, locationName.indexOf("(")).trim();
				}
				assertEquals(location.getName(), locationName);
			} else { //Multiple locations
				for (Long regionID : regionType.getRegions()) {
					Location location = settings.getLocations().get(regionID);
					assertNotNull(location);
				}
			}
		}
	}

	public static class PriceDataSettingsMock extends FakeSettings {

	private Map<Long, Location> locations = new HashMap<Long, Location>();

	public PriceDataSettingsMock() {
		LocationsReader.load(this);
	}

	@Override
	public Map<Long, Location> getLocations() {
		return locations;
	}
}

}
