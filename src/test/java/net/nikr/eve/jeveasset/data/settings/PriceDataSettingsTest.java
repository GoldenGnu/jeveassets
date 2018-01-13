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

import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import java.util.List;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.RegionType;
import org.junit.*;

import static org.junit.Assert.*;


public class PriceDataSettingsTest extends TestUtil {

	@Test
	public void testGetRegion() {
		for (RegionType regionType : RegionType.values()) {
			List<Long> regions = regionType.getRegions();
			if (regions.size() == 1) { //Single location
				MyLocation location = StaticData.get().getLocations().get(regions.get(0));
				String locationName = regionType.toString();
				if (locationName.contains("(")) {
					locationName = locationName.substring(0, locationName.indexOf("(")).trim();
				}
				assertEquals(location.getLocation(), locationName);
			} else { //Multiple locations
				for (Long regionID : regionType.getRegions()) {
					MyLocation location = StaticData.get().getLocations().get(regionID);
					assertNotNull(location);
				}
			}
		}
	}
}
