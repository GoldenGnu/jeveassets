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


public class ImportEveMultibuyTest extends TestUtil {
	
	private final ImportEveMultibuy importEveMultibuy = new ImportEveMultibuy();

	@Test
	public void testSomeMethod() {
/*
100MN Afterburner II	1	-	-
Dominix	1	-	-
Capacitor Power Relay II	1	-	-
Hammerhead II	10	1.189.997,05	11.899.970,50
Drone Link Augmentor I	2	-	-
Ogre II	5	-	-
Dual 250mm Prototype Gauss Gun	4	-	-
Cap Recharger II	4	-	-
Warden II	5	-	-
Large Capacitor Control Circuit I	3	-	-
Large Armor Repairer II	2	-	-
Hobgoblin II	5	499.997,32	2.499.986,60
Armor Kinetic Hardener II	1	-	-
Armor Explosive Hardener II	3	-	-
Total:			14.399.957,10
*/
		String text = "100MN Afterburner II	1	-	-\n" +
			"Dominix	1	-	-\n" +
			"Capacitor Power Relay II	1	-	-\n" +
			"Hammerhead II	10	1.189.997,05	11.899.970,50\n" +
			"Drone Link Augmentor I	2	-	-\n" +
			"Ogre II	5	-	-\n" +
			"Dual 250mm Prototype Gauss Gun	4	-	-\n" +
			"Cap Recharger II	4	-	-\n" +
			"Warden II	5	-	-\n" +
			"Large Capacitor Control Circuit I	3	-	-\n" +
			"Large Armor Repairer II	2	-	-\n" +
			"Hobgoblin II	5	499.997,32	2.499.986,60\n" +
			"Armor Kinetic Hardener II	1	-	-\n" +
			"Armor Explosive Hardener II	3	-	-\n" +
			"Total:			14.399.957,10";
		Map<String, Double> data = test(text, 14);
		assertEquals(10, data.get("Hammerhead II"), 0.1);
	}

	private Map<String, Double> test(String text, int size) {
		Map<String, Double> data = importEveMultibuy.doImport(text);
		assertNotNull(data);
		assertEquals(size, data.size());
		assertEquals(data.size(), importEveMultibuy.importText(text).size());
		return data;
	}
	
}
