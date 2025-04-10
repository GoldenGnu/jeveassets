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

package net.nikr.eve.jeveasset.io.shared;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
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
	 * Tested against Isk Per Hour and Fuzzwork 2025-04-03
	 */
	@Test
	public void testManufacturingQuantity() {
		System.out.println("	--- Default ---");
		int me = 0;
		ManufacturingFacility facility = ManufacturingFacility.ENGINEERING_COMPLEX_XLARGE;
		ManufacturingRigs rigs = ManufacturingRigs.NONE;
		ManufacturingSecurity security = ManufacturingSecurity.LOWSEC;
		double runs = 1;
		Map<Integer, Double> expected = new HashMap<>();
		expected.put(34, 5_148_000.0); //Tritanium
		expected.put(35, 2_574_000.0); //Pyerite
		expected.put(36,   386_100.0); //Mexallon
		expected.put(37,   128_700.0); //Isogen
		expected.put(38,    15_444.0); //Nocxium
		expected.put(39,     3_861.0); //Zydrine
		expected.put(40,     1_931.0); //Megacyte
		expected.put(57478,    149.0); //Auto-Integrity Preservation Seal
		expected.put(57486,     75.0); //Life Support Backup Unit
		expected.put(57479,      1.0); //Core Temperature Regulator
		testManufacturingQuantity(expected, me, facility, rigs, security, runs);
	}

	/**
	 * Tested against Isk Per Hour and Fuzzwork 2024-10-06
	 */
	@Test
	public void testManufacturingQuantityMe() {
		System.out.println("	--- ME ---");
		int me = 10;
		ManufacturingFacility facility = ManufacturingFacility.ENGINEERING_COMPLEX_XLARGE;
		ManufacturingRigs rigs = ManufacturingRigs.NONE;
		ManufacturingSecurity security = ManufacturingSecurity.HIGHSEC;
		double runs = 1;
		Map<Integer, Double> expected = new HashMap<>();
		expected.put(34, 4_633_200.0); //Tritanium
		expected.put(35, 2_316_600.0); //Pyerite
		expected.put(36,   347_490.0); //Mexallon
		expected.put(37,   115_830.0); //Isogen
		expected.put(38,    13_900.0); //Nocxium
		expected.put(39,     3_475.0); //Zydrine
		expected.put(40,     1_738.0); //Megacyte
		expected.put(57478,    134.0); //Auto-Integrity Preservation Seal
		expected.put(57486,     67.0); //Life Support Backup Unit
		expected.put(57479,      1.0); //Core Temperature Regulator
		testManufacturingQuantity(expected, me, facility, rigs, security, runs);
	}

	/**
	 * Tested against Isk Per Hour and Fuzzwork 2025-04-03
	 */
	@Test
	public void testManufacturingQuantityRuns() {
		System.out.println("	--- Runs Eng---");
		int me = 10;
		ManufacturingFacility facility = ManufacturingFacility.ENGINEERING_COMPLEX_XLARGE;
		ManufacturingRigs rigs = ManufacturingRigs.NONE;
		ManufacturingSecurity security = ManufacturingSecurity.HIGHSEC;
		double runs = 200;
		Map<Integer, Double> expected = new HashMap<>();
		expected.put(34, 926_640_000.0); //Tritanium
		expected.put(35, 463_320_000.0); //Pyerite
		expected.put(36,  69_498_000.0); //Mexallon
		expected.put(37,  23_166_000.0); //Isogen
		expected.put(38,   2_779_920.0); //Nocxium
		expected.put(39,     694_980.0); //Zydrine
		expected.put(40,     347_490.0); //Megacyte
		expected.put(57478,   26_730.0); //Auto-Integrity Preservation Seal
		expected.put(57486,   13_365.0); //Life Support Backup Unit
		expected.put(57479,      200.0); //Core Temperature Regulator
		testManufacturingQuantity(expected, me, facility, rigs, security, runs);
	}

	/**
	 * Tested against in-game values 2025-04-03
	 */
	@Test
	public void testManufacturingQuantityRunsStation() {
		System.out.println("	--- Runs Station ---");
		int me = 10;
		ManufacturingFacility facility = ManufacturingFacility.STATION;
		ManufacturingRigs rigs = ManufacturingRigs.NONE;
		ManufacturingSecurity security = ManufacturingSecurity.HIGHSEC;
		double runs = 200;
		Map<Integer, Double> expected = new HashMap<>();
		expected.put(34, 936_000_000.0); //Tritanium
		expected.put(35, 468_000_000.0); //Pyerite
		expected.put(36,  70_200_000.0); //Mexallon
		expected.put(37,  23_400_000.0); //Isogen
		expected.put(38,   2_808_000.0); //Nocxium
		expected.put(39,     702_000.0); //Zydrine
		expected.put(40,     351_000.0); //Megacyte
		expected.put(57478,   27_000.0); //Auto-Integrity Preservation Seal
		expected.put(57486,   13_500.0); //Life Support Backup Unit
		expected.put(57479,      200.0); //Core Temperature Regulator
		testManufacturingQuantity(expected, me, facility, rigs, security, runs);
	}

	public void testManufacturingQuantity(Map<Integer, Double> expected, int me, ManufacturingFacility facility, ManufacturingRigs rigs, ManufacturingSecurity security, double runs) {
		Item blueprint = ApiIdConverter.getItem(999); //Dominix Blueprint
		for (IndustryMaterial material : blueprint.getManufacturingMaterials()) {
			double quantity = ApiIdConverter.getManufacturingQuantity(material.getQuantity(), me, facility, rigs, security, runs, true);
			System.out.println("	id=" + material.getTypeID() + " q=" + material.getQuantity() + " qmod=" + Formatter.compareFormat(quantity));
			assertEquals(expected.get(material.getTypeID()), quantity, 0.001);
			//System.out.println("expected.put(" + material.getTypeID() + ", " + Formatter.compareFormat(quantity) + ");");
		}
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
