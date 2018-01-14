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


public class ImportEftTest extends TestUtil {

	private final ImportEft importEft = new ImportEft();

	@Test
	public void testCargo() {
		String text = "[Dominix, Cargo Test]\n" +
				"\n" +
				"Large Armor Repairer II\n" +
				"Large Armor Repairer II\n" +
				"Armor Kinetic Hardener II\n" +
				"Armor Explosive Hardener II\n" +
				"Armor Explosive Hardener II\n" +
				"Armor Explosive Hardener II\n" +
				"Capacitor Power Relay II\n" +
				"\n" +
				"100MN Afterburner II\n" +
				"Cap Recharger II\n" +
				"Cap Recharger II\n" +
				"Cap Recharger II\n" +
				"Cap Recharger II\n" +
				"\n" +
				"Dual 250mm Prototype Gauss Gun\n" +
				"Dual 250mm Prototype Gauss Gun\n" +
				"Dual 250mm Prototype Gauss Gun\n" +
				"Dual 250mm Prototype Gauss Gun\n" +
				"Drone Link Augmentor I\n" +
				"Drone Link Augmentor I\n" +
				"\n" +
				"Large Capacitor Control Circuit I\n" +
				"Large Capacitor Control Circuit I\n" +
				"Large Capacitor Control Circuit I\n" +
				"\n" +
				"\n" +
				"Hammerhead II x10\n" +
				"Ogre II x5\n" +
				"Warden II x5\n" +
				"Hobgoblin II x5\n" +
				"\n" +
				"\n" +
				"Iron Charge L x3260\n" +
				"Antimatter Charge L x4704";
		Map<String, Double> data = test(text, 16);
		assertEquals(3260, data.get("Iron Charge L"), 0.1);
	}
	@Test
	public void testDomi() {
/*
[Dominix, My Domi]

Large Armor Repairer II
Large Armor Repairer II
Armor Kinetic Hardener II
Armor Explosive Hardener II
Armor Explosive Hardener II
Armor Explosive Hardener II
Capacitor Power Relay II

100MN Afterburner II
Cap Recharger II
Cap Recharger II
Cap Recharger II
Cap Recharger II

Dual 250mm Prototype Gauss Gun
Dual 250mm Prototype Gauss Gun
Dual 250mm Prototype Gauss Gun
Dual 250mm Prototype Gauss Gun
[Empty High slot]
[Empty High slot]

Large Capacitor Control Circuit I
Large Capacitor Control Circuit I
Large Capacitor Control Circuit I


Hammerhead II x10
Ogre II x5
Hobgoblin II x5
Warden II x5
*/
		String text = "[Dominix, My Domi]\n" +
			"\n" +
			"Large Armor Repairer II\n" +
			"Large Armor Repairer II\n" +
			"Armor Kinetic Hardener II\n" +
			"Armor Explosive Hardener II\n" +
			"Armor Explosive Hardener II\n" +
			"Armor Explosive Hardener II\n" +
			"Capacitor Power Relay II\n" +
			"\n" +
			"100MN Afterburner II\n" +
			"Cap Recharger II\n" +
			"Cap Recharger II\n" +
			"Cap Recharger II\n" +
			"Cap Recharger II\n" +
			"\n" +
			"Dual 250mm Prototype Gauss Gun\n" +
			"Dual 250mm Prototype Gauss Gun\n" +
			"Dual 250mm Prototype Gauss Gun\n" +
			"Dual 250mm Prototype Gauss Gun\n" +
			"[Empty High slot]\n" +
			"[Empty High slot]\n" +
			"\n" +
			"Large Capacitor Control Circuit I\n" +
			"Large Capacitor Control Circuit I\n" +
			"Large Capacitor Control Circuit I\n" +
			"\n" +
			"\n" +
			"Hammerhead II x10\n" +
			"Ogre II x5\n" +
			"Hobgoblin II x5\n" +
			"Warden II x5";
		Map<String, Double> data = test(text, 13);
		assertEquals(10, data.get("Hammerhead II"), 0.1);
	}

	private Map<String, Double> test(String text, int size) {
		Map<String, Double> data = importEft.doImport(text);
		assertNotNull(data);
		assertEquals(size, data.size());
		assertEquals(data.size(), importEft.importText(text).size());
		return data;
	}
	
}
