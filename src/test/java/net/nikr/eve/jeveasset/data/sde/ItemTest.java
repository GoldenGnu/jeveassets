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
package net.nikr.eve.jeveasset.data.sde;

import net.nikr.eve.jeveasset.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;


public class ItemTest extends TestUtil {

	@Test
	public void test() {
		//PI
		boolean categoryPlanetaryIndustry = false;
		boolean categoryPlanetaryCommodities = false;
		boolean categoryPlanetaryResources = false;
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
		//Blueprint
		boolean categoryBlueprint = false;
		long blueprint = 0;
		long reaction = 0;
		long total = 0;
		//Ship
		boolean categoryShip = false;
		//Asteroid
		boolean categoryAsteroid = false;
		//Module
		boolean categoryModule = false;
		//Structure
		boolean categoryStructure = false;
		//Material
		boolean categoryMaterial = false;
		//Deployable
		boolean categoryDeployable = false;
		//Biomass
		boolean groupBiomass = false;
		//Station Services
		boolean groupStationServices = false;
		//Compressed Gas
		boolean groupCompressedGas = false;
		//Harvestable Cloud
		boolean groupHarvestableCloud = false;

		for (Item item : StaticData.get().getItems().values()) {
			//PI
			if (item.getCategory().equals(Item.CATEGORY_PLANETARY_INDUSTRY)) {
				categoryPlanetaryIndustry = true;
			}
			if (item.getCategory().equals(Item.CATEGORY_PLANETARY_COMMODITIES)) {
				categoryPlanetaryCommodities = true;
			}
			if (item.getCategory().equals(Item.CATEGORY_PLANETARY_RESOURCES)) {
				categoryPlanetaryResources = true;
			}
			if (item.getGroup().equals(Item.GROUP_COMMAND_CENTERS)) {
				groupCommandCenters = true;
			}
			if (item.getGroup().equals(Item.GROUP_EXTRACTOR_CONTROL_UNITS)) {
				groupExtractorControlUnits = true;
			}
			if (item.getGroup().equals(Item.GROUP_PROCESSORS)) {
				groupProcessors = true;
			}
			if (item.getGroup().equals(Item.GROUP_SPACEPORTS)) {
				groupSpaceports = true;
			}
			if (item.getGroup().equals(Item.GROUP_STORAGE_FACILITIES)) {
				groupStorageFacilities = true;
			}
			//Containers
			if (item.getGroup().equals(Item.GROUP_AUDIT_LOG_SECURE_CONTAINER)) {
				groupAuditLogSecureContainer = true;
			}
			if (item.getGroup().equals(Item.GROUP_FREIGHT_CONTAINER)) {
				groupFreightContainer = true;
			}
			if (item.getGroup().equals(Item.GROUP_CARGO_CONTAINER)) {
				groupCargoContainer = true;
			}
			if (item.getGroup().equals(Item.GROUP_SECURE_CARGO_CONTAINER)) {
				groupSecureCargoContainer = true;
			}
			//Blueprint
			if (item.getCategory().equals(Item.CATEGORY_BLUEPRINT)) {
				categoryBlueprint = true;
				total++;
				if (item.isBlueprint()) {
					blueprint++;
				}
				if (item.isFormula()) {
					reaction++;
				}
				if (!item.isFormula() && !item.isBlueprint()) {
					System.out.println(item.getTypeName() + " :: " + item.getGroup());
				}
			}
			//Ship
			if (item.isShip()) { //CATEGORY_SHIP
				categoryShip = true;
			}
			//Asteroid
			if (item.isOre()) { //CATEGORY_ASTEROID
				categoryAsteroid = true;
			}
			//Module
			if (item.getCategory().equals(Item.CATEGORY_MODULE)) {
				categoryModule = true;
			}
			//Structure
			if (item.getCategory().equals(Item.CATEGORY_STRUCTURE)) {
				categoryStructure = true;
			}
			//Material
			if (item.getCategory().equals(Item.CATEGORY_MATERIAL)) {
				categoryMaterial = true;
			}
			//Deployable
			if (item.getCategory().equals(Item.CATEGORY_DEPLOYABLE)) {
				categoryDeployable = true;
			}
			//Biomass
			if (item.getGroup().equals(Item.GROUP_BIOMASS)) {
				groupBiomass = true;
			}
			//Station Services
			if (item.getGroup().equals(Item.GROUP_STATION_SERVICES)) {
				groupStationServices = true;
			}
			//Compressed Gas
			if (item.getGroup().equals(Item.GROUP_COMPRESSED_GAS)) {
				groupCompressedGas = true;
			}
			//Harvestable Cloud
			if (item.getGroup().equals(Item.GROUP_HARVESTABLE_CLOUD)) {
				groupHarvestableCloud = true;
			}
		}
		assertEquals(total, blueprint + reaction);
		//PI
		assertTrue("no category: Planetary Industry", categoryPlanetaryIndustry);
		assertTrue("no category: Planetary Commodities", categoryPlanetaryCommodities);
		assertTrue("no category: Planetary Resources", categoryPlanetaryResources);
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
		//Blueprint
		assertTrue("no category: Ship", categoryBlueprint);
		//Ship
		assertTrue("no category: Ship", categoryShip);
		//Asteroid
		assertTrue("no category: Asteroid", categoryAsteroid);
		//Module
		assertTrue("no category: Module", categoryModule);
		//Structure
		assertTrue("no category: Structure", categoryStructure);
		//Material
		assertTrue("no category: Material", categoryMaterial);
		//Deployable
		assertTrue("no category: Deployable", categoryDeployable);
		//Biomass
		assertTrue("no group: Biomass", groupBiomass);
		//Station Services
		assertTrue("no group: Station Services", groupStationServices);
	//Gas
		//Compressed Gas
		assertTrue("no group: Compressed Gas", groupCompressedGas);
		//Harvestable Cloud
		assertTrue("no group: Harvestable Cloud", groupHarvestableCloud);
	}

}
