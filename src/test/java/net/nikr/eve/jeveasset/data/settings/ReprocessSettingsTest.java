/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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


package net.nikr.eve.jeveasset.data.settings;

import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.settings.ReprocessSettings;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.TestUtil;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ReprocessSettingsTest extends TestUtil {

	@Test
	public void testSomeMethod() {
		ReprocessSettings reprocessSettings;
		//Level 4 Material Skill At 50% Facilities
		reprocessSettings = new ReprocessSettings(50, 5, 5, 4);
		assertEquals(68.31, reprocessSettings.getPercent(true), 0);
		//Level 4 Material Skill 52% Reprocessing Array
		reprocessSettings = new ReprocessSettings(52, 5, 5, 4);
		assertEquals(71.0424, reprocessSettings.getPercent(true), 0);
		//Max Skill At 50% Facilities
		reprocessSettings = new ReprocessSettings(50, 5, 5, 5);
		assertEquals(69.575, reprocessSettings.getPercent(true), 0);
		//Max Skill At 52% Reprocessing Array
		reprocessSettings = new ReprocessSettings(52, 5, 5, 5);
		assertEquals(72.358, reprocessSettings.getPercent(true), 0);

		//Level 4 Material Skill At 50% Facilities
		reprocessSettings = new ReprocessSettings(50, 0, 0, 0);
		Item item = StaticData.get().getItems().get(238);
		for (ReprocessedMaterial reprocessedMaterial : item.getReprocessedMaterial()) {
			if (reprocessedMaterial.getTypeID() == 34) { //Tritanium
				assertEquals(889, reprocessSettings.getLeft(reprocessedMaterial.getQuantity(), false));
			}
			if (reprocessedMaterial.getTypeID() == 35) { //Pyerite
				assertEquals(63, reprocessSettings.getLeft(reprocessedMaterial.getQuantity(), false));
			}
			if (reprocessedMaterial.getTypeID() == 36) { //Mexallon
				assertEquals(44, reprocessSettings.getLeft(reprocessedMaterial.getQuantity(), false));
			}
			if (reprocessedMaterial.getTypeID() == 37) { //Isogen
				assertEquals(14, reprocessSettings.getLeft(reprocessedMaterial.getQuantity(), false));
			}
		}
		for (ReprocessedMaterial reprocessedMaterial : item.getReprocessedMaterial()) {
			if (reprocessedMaterial.getTypeID() == 34) { //Tritanium
				assertEquals(177800, reprocessSettings.getLeft(reprocessedMaterial.getQuantity() * 200, false));
			}
			if (reprocessedMaterial.getTypeID() == 35) { //Pyerite
				assertEquals(12700, reprocessSettings.getLeft(reprocessedMaterial.getQuantity() * 200, false));
			}
			if (reprocessedMaterial.getTypeID() == 36) { //Mexallon
				assertEquals(8900, reprocessSettings.getLeft(reprocessedMaterial.getQuantity() * 200, false));
			}
			if (reprocessedMaterial.getTypeID() == 37) { //Isogen
				assertEquals(2900, reprocessSettings.getLeft(reprocessedMaterial.getQuantity() * 200, false));
			}
		}
	}
	
}
