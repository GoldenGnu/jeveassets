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
package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.util.Map;
import net.nikr.eve.jeveasset.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;


public class ImportShoppingListTest extends TestUtil {
	
	private final ImportShoppingList importShoppingList = new ImportShoppingList();

	@Test
	public void testDomi() {
/*
My Domi

1x 100MN Afterburner II
1x Dominix
1x Capacitor Power Relay II
10x Hammerhead II
5x Ogre II
4x Dual 250mm Prototype Gauss Gun
4x Cap Recharger II
5x Warden II
2x Large Armor Repairer II
5x Hobgoblin II
1x Armor Kinetic Hardener II
3x Large Capacitor Control Circuit I
3x Armor Explosive Hardener II

Total m3 to be hauled: 50.665,00
Estimated market value: 225.404.694,17 isk
*/
		String text = "My Domi\n" +
			"\n" +
			"1x 100MN Afterburner II\n" +
			"1x Dominix\n" +
			"1x Capacitor Power Relay II\n" +
			"10x Hammerhead II\n" +
			"5x Ogre II\n" +
			"4x Dual 250mm Prototype Gauss Gun\n" +
			"4x Cap Recharger II\n" +
			"5x Warden II\n" +
			"2x Large Armor Repairer II\n" +
			"5x Hobgoblin II\n" +
			"1x Armor Kinetic Hardener II\n" +
			"3x Large Capacitor Control Circuit I\n" +
			"3x Armor Explosive Hardener II\n" +
			"\n" +
			"Total m3 to be hauled: 50.665,00\n" +
			"Estimated market value: 225.404.694,17 isk";
		Map<String, Double> data = test(text, 13);
		assertEquals(10, data.get("Hammerhead II"), 0.1);
	}

	private Map<String, Double> test(String text, int size) {
		Map<String, Double> data = importShoppingList.doImport(text);
		assertNotNull(data);
		assertEquals(size, data.size());
		assertEquals(data.size(), importShoppingList.importText(text).size());
		return data;
	}
	
}
