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

package net.nikr.eve.jeveasset.tests.gui.shared;

import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.AssetFilter.Mode;
import net.nikr.eve.jeveasset.gui.shared.EveAssetMatching;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class EveAssetMatchingTest {
	
	public EveAssetMatchingTest() {
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
	 * Test of matches method, of class EveAssetMatching.
	 */
	@Test
	public void testMatches() {
		System.out.println("matches");
		Asset eveAsset = new MockAsset(0.0025f, "0.5", "1 (Tech I)", 100.0);
		EveAssetMatching instance = new EveAssetMatching();
		
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN, "5", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN, "25", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN, "025", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN, "0025", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN, ".0025", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN, "0.0025", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN, "0.002", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN, "0.00", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN, "0.0", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN, "0", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, "5", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, "25", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, "025", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, "0025", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, ".0025", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, "0.0025", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, "0.002", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, "0.00", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, "0.0", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, "0.", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_CONTAIN_NOT, "0", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_EQUALS, "0.0025", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_EQUALS_NOT, "0.0025", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_GREATER_THAN, "0.0024", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_GREATER_THAN, "0.0025", null));
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_LESS_THAN, "0.0026", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_LESS_THAN, "0.0025", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_GREATER_THAN, "NaN", null));
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_LESS_THAN, "NaN", null));
		eveAsset.setPriceReprocessed(0.0024);
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_GREATER_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(0.0025);
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_GREATER_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(0.0026);
		assertEquals(true,  instance.matches(eveAsset, "Volume", Mode.MODE_LESS_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(0.0025);
		assertEquals(false, instance.matches(eveAsset, "Volume", Mode.MODE_LESS_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(0.0026);
		assertEquals(true,  instance.matches(eveAsset, "Reprocessed", Mode.MODE_GREATER_THAN_COLUMN, "", "Volume"));
		eveAsset.setPriceReprocessed(0.0025);
		assertEquals(false, instance.matches(eveAsset, "Reprocessed", Mode.MODE_GREATER_THAN_COLUMN, "", "Volume"));
		eveAsset.setPriceReprocessed(0.0024);
		assertEquals(true,  instance.matches(eveAsset, "Reprocessed", Mode.MODE_LESS_THAN_COLUMN, "", "Volume"));
		eveAsset.setPriceReprocessed(0.0025);
		assertEquals(false, instance.matches(eveAsset, "Reprocessed", Mode.MODE_LESS_THAN_COLUMN, "", "Volume"));
		
		
		
		assertEquals(true,  instance.matches(eveAsset, "Security", Mode.MODE_CONTAIN, "5", null));
		assertEquals(true,  instance.matches(eveAsset, "Security", Mode.MODE_CONTAIN, ".5", null));
		assertEquals(true,  instance.matches(eveAsset, "Security", Mode.MODE_CONTAIN, "0.5", null));
		assertEquals(true,  instance.matches(eveAsset, "Security", Mode.MODE_CONTAIN, "0.", null));
		assertEquals(true,  instance.matches(eveAsset, "Security", Mode.MODE_CONTAIN, "0", null));
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_CONTAIN_NOT, "5", null));
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_CONTAIN_NOT, ".5", null));
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_CONTAIN_NOT, "0.5", null));
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_CONTAIN_NOT, "0.", null));
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_CONTAIN_NOT, "0", null));
		assertEquals(true,  instance.matches(eveAsset, "Security", Mode.MODE_EQUALS, "0.5", null));
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_EQUALS_NOT, "0.5", null));
		assertEquals(true,  instance.matches(eveAsset, "Security", Mode.MODE_GREATER_THAN, "0.49", null));
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_GREATER_THAN, "0.5", null));
		assertEquals(true,  instance.matches(eveAsset, "Security", Mode.MODE_LESS_THAN, "0.51", null));
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_LESS_THAN, "0.5", null));
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_GREATER_THAN, "NaN", null));
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_LESS_THAN, "NaN", null));
		eveAsset.setPriceReprocessed(0.49);
		assertEquals(true,  instance.matches(eveAsset, "Security", Mode.MODE_GREATER_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(0.5);
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_GREATER_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(0.51);
		assertEquals(true,  instance.matches(eveAsset, "Security", Mode.MODE_LESS_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(0.5);
		assertEquals(false, instance.matches(eveAsset, "Security", Mode.MODE_LESS_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(0.51);
		assertEquals(true,  instance.matches(eveAsset, "Reprocessed", Mode.MODE_GREATER_THAN_COLUMN, "", "Security"));
		eveAsset.setPriceReprocessed(0.5);
		assertEquals(false, instance.matches(eveAsset, "Reprocessed", Mode.MODE_GREATER_THAN_COLUMN, "", "Security"));
		eveAsset.setPriceReprocessed(0.49);
		assertEquals(true,  instance.matches(eveAsset, "Reprocessed", Mode.MODE_LESS_THAN_COLUMN, "", "Security"));
		eveAsset.setPriceReprocessed(0.5);
		assertEquals(false, instance.matches(eveAsset, "Reprocessed", Mode.MODE_LESS_THAN_COLUMN, "", "Security"));
		
		
		Asset securityAsset = new MockAsset(0.0025f, "0.0", "1 (Tech I)", 100.0);
		assertEquals(true,  instance.matches(securityAsset, "Security", Mode.MODE_CONTAIN, "0", null));
		assertEquals(true,  instance.matches(securityAsset, "Security", Mode.MODE_CONTAIN, ".0", null));
		assertEquals(true,  instance.matches(securityAsset, "Security", Mode.MODE_CONTAIN, "0.0", null));
		assertEquals(true,  instance.matches(securityAsset, "Security", Mode.MODE_CONTAIN, "0.", null));
		assertEquals(true,  instance.matches(securityAsset, "Security", Mode.MODE_CONTAIN, "0", null));
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_CONTAIN_NOT, "0", null));
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_CONTAIN_NOT, ".0", null));
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_CONTAIN_NOT, "0.0", null));
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_CONTAIN_NOT, "0.", null));
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_CONTAIN_NOT, "0", null));
		assertEquals(true,  instance.matches(securityAsset, "Security", Mode.MODE_EQUALS, "0.0", null));
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_EQUALS_NOT, "0.0", null));
		assertEquals(true,  instance.matches(securityAsset, "Security", Mode.MODE_GREATER_THAN, "-1", null));
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_GREATER_THAN, "0.0", null));
		assertEquals(true,  instance.matches(securityAsset, "Security", Mode.MODE_LESS_THAN, "0.01", null));
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_LESS_THAN, "0.0", null));
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_GREATER_THAN, "NaN", null));
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_LESS_THAN, "NaN", null));
		securityAsset.setPriceReprocessed(-1);
		assertEquals(true,  instance.matches(securityAsset, "Security", Mode.MODE_GREATER_THAN_COLUMN, "", "Reprocessed"));
		securityAsset.setPriceReprocessed(0.0);
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_GREATER_THAN_COLUMN, "", "Reprocessed"));
		securityAsset.setPriceReprocessed(0.01);
		assertEquals(true,  instance.matches(securityAsset, "Security", Mode.MODE_LESS_THAN_COLUMN, "", "Reprocessed"));
		securityAsset.setPriceReprocessed(0.0);
		assertEquals(false, instance.matches(securityAsset, "Security", Mode.MODE_LESS_THAN_COLUMN, "", "Reprocessed"));
		securityAsset.setPriceReprocessed(0.01);
		assertEquals(true,  instance.matches(securityAsset, "Reprocessed", Mode.MODE_GREATER_THAN_COLUMN, "", "Security"));
		securityAsset.setPriceReprocessed(0.0);
		assertEquals(false, instance.matches(securityAsset, "Reprocessed", Mode.MODE_GREATER_THAN_COLUMN, "", "Security"));
		securityAsset.setPriceReprocessed(-1);
		assertEquals(true,  instance.matches(securityAsset, "Reprocessed", Mode.MODE_LESS_THAN_COLUMN, "", "Security"));
		securityAsset.setPriceReprocessed(0.0);
		assertEquals(false, instance.matches(securityAsset, "Reprocessed", Mode.MODE_LESS_THAN_COLUMN, "", "Security"));
		
		
		
		
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN, "0", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN, ".0", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN, "0.0", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN, "00.0", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN, "100.0", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN, "100.", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN, "100", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN, "10", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN, "1", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN_NOT, "0", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN_NOT, ".0", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN_NOT, "0.0", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN_NOT, "00.0", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN_NOT, "100.0", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN_NOT, "100.", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN_NOT, "100", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN_NOT, "10", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_CONTAIN_NOT, "1", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_EQUALS, "100.0", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_EQUALS_NOT, "100.0", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_GREATER_THAN, "99.9", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_GREATER_THAN, "100.0", null));
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_LESS_THAN, "100.1", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_LESS_THAN, "100", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_GREATER_THAN, "NaN", null));
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_LESS_THAN, "NaN", null));
		eveAsset.setPriceReprocessed(99.9);
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_GREATER_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(100.0);
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_GREATER_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(100.1);
		assertEquals(true,  instance.matches(eveAsset, "Price", Mode.MODE_LESS_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(100);
		assertEquals(false, instance.matches(eveAsset, "Price", Mode.MODE_LESS_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(100.1);
		assertEquals(true,  instance.matches(eveAsset, "Reprocessed", Mode.MODE_GREATER_THAN_COLUMN, "", "Price"));
		eveAsset.setPriceReprocessed(100.0);
		assertEquals(false, instance.matches(eveAsset, "Reprocessed", Mode.MODE_GREATER_THAN_COLUMN, "", "Price"));
		eveAsset.setPriceReprocessed(99.9);
		assertEquals(true,  instance.matches(eveAsset, "Reprocessed", Mode.MODE_LESS_THAN_COLUMN, "", "Price"));
		eveAsset.setPriceReprocessed(100);
		assertEquals(false, instance.matches(eveAsset, "Reprocessed", Mode.MODE_LESS_THAN_COLUMN, "", "Price"));
		
		
		
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, ")", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, " I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "h I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "ch I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "ech I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "Tech I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "(Tech I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, " (Tech I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "1 (Tech I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "1 (Tech I", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "1 (Tech ", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "1 (Tech", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "1 (Tec", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "1 (Te", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "1 (T", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "1 (", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "1 ", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN, "1", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, ")", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, " I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "h I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "ch I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "ech I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "Tech I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "(Tech I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, " (Tech I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "1 (Tech I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "1 (Tech I", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "1 (Tech ", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "1 (Tech", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "1 (Tec", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "1 (Te", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "1 (T", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "1 (", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "1 ", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_CONTAIN_NOT, "1", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_EQUALS, "1 (Tech I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_EQUALS_NOT, "1 (Tech I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_GREATER_THAN, "0 (Tech I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_GREATER_THAN, "1 (Tech I)", null));
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_LESS_THAN, "2 (Tech I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_LESS_THAN, "1 (Tech I)", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_GREATER_THAN, "NaN", null));
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_LESS_THAN, "NaN", null));
		eveAsset.setPriceReprocessed(0);
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_GREATER_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(1);
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_GREATER_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(2);
		assertEquals(true,  instance.matches(eveAsset, "Meta", Mode.MODE_LESS_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(1);
		assertEquals(false, instance.matches(eveAsset, "Meta", Mode.MODE_LESS_THAN_COLUMN, "", "Reprocessed"));
		eveAsset.setPriceReprocessed(2);
		assertEquals(true,  instance.matches(eveAsset, "Reprocessed", Mode.MODE_GREATER_THAN_COLUMN, "", "Meta"));
		eveAsset.setPriceReprocessed(1);
		assertEquals(false, instance.matches(eveAsset, "Reprocessed", Mode.MODE_GREATER_THAN_COLUMN, "", "Meta"));
		eveAsset.setPriceReprocessed(0);
		assertEquals(true,  instance.matches(eveAsset, "Reprocessed", Mode.MODE_LESS_THAN_COLUMN, "", "Meta"));
		eveAsset.setPriceReprocessed(1);
		assertEquals(false, instance.matches(eveAsset, "Reprocessed", Mode.MODE_LESS_THAN_COLUMN, "", "Meta"));
	}

}
