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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.model.eve.Station;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.StaticData;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ApiIdConverterTest {

	public ApiIdConverterTest() {
	}

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

	/**
	 * Test of location method, of class ApiIdConverter.
	 */
	@Test
	public void testLocation() {
		for (MyLocation o1 : StaticData.get().getLocations().values()) {
			MyLocation location = ApiIdConverter.getLocation(o1.getLocationID());
			assertEquals(o1.getLocation(), location.getLocation());
			assertEquals(o1.getLocationID(), location.getLocationID());
			if (o1.isRegion()) {
				assertEquals(o1.getStation(), location.getStation());
				assertEquals(o1.getStationID(), location.getStationID());
				assertEquals(o1.getStation(), "");
				assertEquals(o1.getStationID(), 0);
				assertEquals(o1.getSystem(), location.getSystem());
				assertEquals(o1.getSystemID(), location.getSystemID());
				assertEquals(o1.getSystem(), "");
				assertEquals(o1.getSystemID(), 0);
				assertEquals(o1.getRegion(), location.getRegion());
				assertEquals(o1.getRegionID(), location.getRegionID());
				assertFalse(o1.getRegion().equals(""));
				assertFalse(o1.getRegionID() == 0);
			} else if (o1.isSystem()) {
				assertEquals(o1.getStation(), location.getStation());
				assertEquals(o1.getStationID(), location.getStationID());
				assertEquals(o1.getStation(), "");
				assertEquals(o1.getStationID(), 0);
				assertEquals(o1.getSystem(), location.getSystem());
				assertEquals(o1.getSystemID(), location.getSystemID());
				assertFalse(o1.getSystem().equals(""));
				assertFalse(o1.getSystemID() == 0);
				assertEquals(o1.getRegion(), location.getRegion());
				assertEquals(o1.getRegionID(), location.getRegionID());
				assertFalse(o1.getRegion().equals(""));
				assertFalse(o1.getRegionID() == 0);
			} else if (o1.isStation()) {
				assertEquals(o1.getStation(), location.getStation());
				assertEquals(o1.getStationID(), location.getStationID());
				assertFalse(o1.getStation().equals(""));
				assertFalse(o1.getStationID() == 0);
				assertEquals(o1.getSystem(), location.getSystem());
				assertEquals(o1.getSystemID(), location.getSystemID());
				assertFalse(o1.getSystem().equals(""));
				assertFalse(o1.getSystemID() == 0);
				assertEquals(o1.getRegion(), location.getRegion());
				assertEquals(o1.getRegionID(), location.getRegionID());
				assertFalse(o1.getRegion().equals(""));
				assertFalse(o1.getRegionID() == 0);
			}
		}
		for (Station apiStation : StaticData.get().getConquerableStations().values()) {
			String system = ApiIdConverter.getLocation(apiStation.getSolarSystemID()).getSystem();
			assertTrue("Station name: " + apiStation.getStationName() + " System name: " + system,  apiStation.getStationName().contains(system) || apiStation.getStationName().equals("C C P S U C K S"));
		}
	}
}
