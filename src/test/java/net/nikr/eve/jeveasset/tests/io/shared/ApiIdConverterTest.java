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

package net.nikr.eve.jeveasset.tests.io.shared;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class ApiIdConverterTest {
	
	private MockSettings settings = new MockSettings();
	
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
		for (Location o1 : settings.getLocations().values()){
		//LocationID
			//Name - region | system | station
			assertEquals(o1.getName(), ApiIdConverter.locationName(o1.getLocationID(), null, settings.getLocations()));
			//System
			assertEquals(settings.getLocations().get(o1.getSystemID()).getName(), ApiIdConverter.systemName(o1.getLocationID(), null, settings.getLocations()));
			//System ID
			assertEquals(settings.getLocations().get(o1.getSystemID()).getSystemID(), ApiIdConverter.systemID(o1.getLocationID(), null, settings.getLocations()));
			//Region
			assertEquals(settings.getLocations().get(o1.getRegionID()).getName(), ApiIdConverter.regionName(o1.getLocationID(), null, settings.getLocations()));
		//SystemID
			//Name - region | system | station
			//assertEquals(o1.getName(), ApiIdConverter.locationName(o1.getLocationID(), null, settings.getSystemID()));
			//System
			assertEquals(settings.getLocations().get(o1.getSystemID()).getName(), ApiIdConverter.systemName(o1.getSystemID(), null, settings.getLocations()));
			//System ID
			assertEquals(settings.getLocations().get(o1.getSystemID()).getSystemID(), ApiIdConverter.systemID(o1.getSystemID(), null, settings.getLocations()));
			//Region
			assertEquals(settings.getLocations().get(o1.getRegionID()).getName(), ApiIdConverter.regionName(o1.getSystemID(), null, settings.getLocations()));
			
		//RegionID
			//Name - region | system | station
			//assertEquals(o1.getName(), ApiIdConverter.locationName(o1.getRegionID(), null, settings.getLocations()));
			//System
			//assertEquals(settings.getLocations().get(o1.getSystemID()).getName(), ApiIdConverter.systemName(o1.getRegionID(), null, settings.getLocations()));
			//System ID
			//assertEquals(settings.getLocations().get(o1.getSystemID()).getSystemID(), ApiIdConverter.systemID(o1.getRegionID(), null, settings.getLocations()));
			//Region
			assertEquals(settings.getLocations().get(o1.getRegionID()).getName(), ApiIdConverter.regionName(o1.getRegionID(), null, settings.getLocations()));
		}
		for (ApiStation apiStation : settings.getConquerableStations().values()){
			String system = ApiIdConverter.systemName(apiStation.getSolarSystemID(), null, settings.getLocations());
			assertTrue("Station name: "+apiStation.getStationName()+" System name: "+system,  apiStation.getStationName().contains(system) || apiStation.getStationName().equals("C C P S U C K S"));
		}
	}
}
