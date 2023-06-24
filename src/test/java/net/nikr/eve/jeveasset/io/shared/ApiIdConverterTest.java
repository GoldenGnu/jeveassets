/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingFacility;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import org.junit.Test;


public class ApiIdConverterTest extends TestUtil {

	private static final double DOMINIX_BASE_PRICE = 130_856_251.94;

	/**
	 * Tested against Isk Per Hour 2023-06-24 
	 */
	@Test
	public void testInstallationFee() {
		Item item = ApiIdConverter.getItem(645); //Dominix
		float systemIndex = 0.001f;
		ManufacturingSettings manufacturingSettings = new ManufacturingSettings();
		manufacturingSettings.setFacility(ManufacturingFacility.STATION);
		manufacturingSettings.setRigs(ManufacturingRigs.NONE);
		manufacturingSettings.setSecurity(ManufacturingSecurity.HIGHSEC);
		manufacturingSettings.setTax(0.25);
		testInstallationFee(manufacturingSettings, item, systemIndex, 785_137.51);

		
		manufacturingSettings.setTax(0.0);

		manufacturingSettings.setFacility(ManufacturingFacility.ENGINEERING_COMPLEX_MEDIUM);
		testInstallationFee(manufacturingSettings, item, systemIndex, 454_071.91);

		manufacturingSettings.setFacility(ManufacturingFacility.ENGINEERING_COMPLEX_LARGE);
		testInstallationFee(manufacturingSettings, item, systemIndex, 452_762.63);

		manufacturingSettings.setFacility(ManufacturingFacility.ENGINEERING_COMPLEX_XLARGE);
		testInstallationFee(manufacturingSettings, item, systemIndex, 451_454.07);


		manufacturingSettings.setTax(0.25);

		manufacturingSettings.setFacility(ManufacturingFacility.ENGINEERING_COMPLEX_MEDIUM);
		testInstallationFee(manufacturingSettings, item, systemIndex, 781_211.82);

		manufacturingSettings.setFacility(ManufacturingFacility.ENGINEERING_COMPLEX_LARGE);
		testInstallationFee(manufacturingSettings, item, systemIndex, 779_903.26);

		manufacturingSettings.setFacility(ManufacturingFacility.ENGINEERING_COMPLEX_XLARGE);
		testInstallationFee(manufacturingSettings, item, systemIndex, 778_594.70);


		systemIndex = 0.2321f;
		manufacturingSettings.setFacility(ManufacturingFacility.STATION);
		testInstallationFee(manufacturingSettings, item, systemIndex, 31_026_017.35);
	}

	public void testInstallationFee(ManufacturingSettings manufacturingSettings, Item item, Float systemIndex, double expected) {
		double actual = ApiIdConverter.getManufacturingInstallationFee(manufacturingSettings, systemIndex, DOMINIX_BASE_PRICE, item);
		assertEquals(Formatter.doubleFormat(actual) + "!=" + Formatter.doubleFormat(expected), expected,actual , 1);
	}

	/**
	 * Test of location method, of class ApiIdConverter.
	 */
	@Test
	public void testLocation() {
		for (MyLocation o1 : StaticData.get().getLocations()) {
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
				assertEquals(o1.getConstellation(), location.getConstellation());
				assertEquals(o1.getConstellationID(), location.getConstellationID());
				assertEquals(o1.getConstellation(), "");
				assertEquals(o1.getConstellationID(), 0);
				assertEquals(o1.getRegion(), location.getRegion());
				assertEquals(o1.getRegionID(), location.getRegionID());
				assertFalse(o1.getRegion().equals(""));
				assertFalse(o1.getRegionID() == 0);
			} else if (o1.isConstellation()) {
				assertEquals(o1.getStation(), location.getStation());
				assertEquals(o1.getStationID(), location.getStationID());
				assertEquals(o1.getStation(), "");
				assertEquals(o1.getStationID(), 0);
				assertEquals(o1.getSystem(), location.getSystem());
				assertEquals(o1.getSystemID(), location.getSystemID());
				assertEquals(o1.getSystem(), "");
				assertEquals(o1.getSystemID(), 0);
				assertEquals(o1.getConstellation(), location.getConstellation());
				assertEquals(o1.getConstellationID(), location.getConstellationID());
				assertFalse(o1.getConstellation().equals(""));
				assertFalse(o1.getConstellationID()== 0);
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
				assertEquals(o1.getConstellation(), location.getConstellation());
				assertEquals(o1.getConstellationID(), location.getConstellationID());
				assertFalse(o1.getConstellation().equals(""));
				assertFalse(o1.getConstellationID()== 0);
				assertEquals(o1.getRegion(), location.getRegion());
				assertEquals(o1.getRegionID(), location.getRegionID());
				assertFalse(o1.getRegion().equals(""));
				assertFalse(o1.getRegionID() == 0);
			} else if (o1.isStation()) { //Not planet
				assertEquals(o1.getStation(), location.getStation());
				assertEquals(o1.getStationID(), location.getStationID());
				assertFalse(o1.getStation().equals(""));
				assertFalse(o1.getStationID() == 0);
				assertEquals(o1.getSystem(), location.getSystem());
				assertEquals(o1.getSystemID(), location.getSystemID());
				assertFalse(o1.getSystem().equals(""));
				assertFalse(o1.getSystemID() == 0);
				assertEquals(o1.getConstellation(), location.getConstellation());
				assertEquals(o1.getConstellationID(), location.getConstellationID());
				assertFalse(o1.getConstellation().equals(""));
				assertFalse(o1.getConstellationID()== 0);
				assertEquals(o1.getRegion(), location.getRegion());
				assertEquals(o1.getRegionID(), location.getRegionID());
				assertFalse(o1.getRegion().equals(""));
				assertFalse(o1.getRegionID() == 0);
			} else if (o1.isPlanet()) {
				assertEquals(o1.getStation(), location.getStation());
				assertEquals(o1.getStationID(), location.getStationID());
				assertFalse(o1.getStation().equals(""));
				assertFalse(o1.getStationID() == 0);
				assertEquals(o1.getSystem(), location.getSystem());
				assertEquals(o1.getSystemID(), location.getSystemID());
				assertFalse(o1.getSystem().equals(""));
				assertFalse(o1.getSystemID() == 0);
				assertEquals(o1.getConstellation(), location.getConstellation());
				assertEquals(o1.getConstellationID(), location.getConstellationID());
				assertFalse(o1.getConstellation().equals(""));
				assertFalse(o1.getConstellationID()== 0);
				assertEquals(o1.getRegion(), location.getRegion());
				assertEquals(o1.getRegionID(), location.getRegionID());
				assertFalse(o1.getRegion().equals(""));
				assertFalse(o1.getRegionID() == 0);
			}
		}
	}
}
