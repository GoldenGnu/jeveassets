/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.tree;

import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import org.junit.Test;
import static org.junit.Assert.*;


public class TreeAssetTest extends TestUtil {
	
	public TreeAssetTest() {
	}

	@Test
	public void test() {
		//PI
		boolean categoryPlanetaryIndustry = false;
		boolean groupCommandCenters = false;
		boolean groupExtractorControlUnits = false;
		boolean groupProcessors = false;
		boolean groupSpaceports = false;
		boolean groupStorageFacilities = false;
		//Containers
		boolean groupAuditLogSecureContainer = false;
		boolean groupFreightContainer = false;
		boolean groupCargoContainer = false;
		boolean groupSecureCargoContainer = false;
		//Ship
		boolean categoryShip = false;
		
		for (Item item : StaticData.get().getItems().values()) {
			//PI
			if (item.getCategory().equals("Planetary Industry")) {
				categoryPlanetaryIndustry = true;
			}
			if (item.getGroup().equals("Command Centers")) {
				groupCommandCenters = true;
			}
			if (item.getGroup().equals("Extractor Control Units")) {
				groupExtractorControlUnits = true;
			}
			if (item.getGroup().equals("Processors")) {
				groupProcessors = true;
			}
			if (item.getGroup().equals("Spaceports")) {
				groupSpaceports = true;
			}
			if (item.getGroup().equals("Storage Facilities")) {
				groupStorageFacilities = true;
			}
			//Containers
			if (item.getGroup().equals("Audit Log Secure Container")) {
				groupAuditLogSecureContainer = true;
			}
			if (item.getGroup().equals("Freight Container")) {
				groupFreightContainer = true;
			}
			if (item.getGroup().equals("Cargo Container")) {
				groupCargoContainer = true;
			}
			if (item.getGroup().equals("Secure Cargo Container")) {
				groupSecureCargoContainer = true;
			}
			//Ship
			if (item.getCategory().equals("Ship")) {
				categoryShip = true;
			}
		}
		//PI
		assertTrue("no category: Planetary Industry", categoryPlanetaryIndustry);
		assertTrue("no group: Command Centers", groupCommandCenters);
		assertTrue("no group: Extractor Control Units", groupExtractorControlUnits);
		assertTrue("no group: Processors", groupProcessors);
		assertTrue("no group: Spaceports", groupSpaceports);
		assertTrue("no group: Storage Facilities", groupStorageFacilities);
		//Containers
		assertTrue("no group: Audit Log Secure Container", groupAuditLogSecureContainer);
		assertTrue("no group: Freight Container", groupFreightContainer);
		assertTrue("no group: Cargo Container", groupCargoContainer);
		assertTrue("no group: Secure Cargo Container", groupSecureCargoContainer);
		//Ship
		assertTrue("no category: Ship", categoryShip);
	}
	
}
