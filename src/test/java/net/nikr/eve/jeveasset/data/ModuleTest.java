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

package net.nikr.eve.jeveasset.data;

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.gui.tabs.loadout.Loadout.FlagType;
import org.junit.*;
import static org.junit.Assert.assertTrue;

public class ModuleTest {

	public ModuleTest() {
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
	 * Test of FlagType enum, of class Module.
	 */
	@Test
	public void testFlags() {
		List<ItemFlag> flags = new ArrayList<ItemFlag>(StaticData.get().getItemFlags().values());

		for (FlagType type : FlagType.values()) {
			if (type == FlagType.TOTAL_VALUE) {
				continue;
			}
			if (type == FlagType.OTHER) {
				continue;
			}
			boolean found = false;
			for (ItemFlag flag : flags) {
				if (flag.getFlagName().contains(type.getFlag())) {
					found = true;
					break;
				}
			}
			assertTrue(type.name() + " flag value (" + type.getFlag() + ") is no longer valid", found);
		}
	}
}
