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
				"Kinetic Armor Hardener II\n" +
				"Explosive Armor Hardener II\n" +
				"Explosive Armor Hardener II\n" +
				"Explosive Armor Hardener II\n" +
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
Kinetic Armor Hardener II
Explosive Armor Hardener II
Explosive Armor Hardener II
Explosive Armor Hardener II
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
			"Kinetic Armor Hardener II\n" +
			"Explosive Armor Hardener II\n" +
			"Explosive Armor Hardener II\n" +
			"Explosive Armor Hardener II\n" +
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

	@Test
	public void testCharge() {
		/*
		Large Ancillary Armor Repairer, Nanite Repair Paste = 64
		Remote Sensor Booster II, Scan Resolution Script = 1
		'Shady' Sensor Booster, Targeting Range Script = 1
		Guidance Disruptor II, Missile Range Disruption Script = 1
		Small Ancillary Shield Booster, Navy Cap Booster 25 = 9
		Dual Light Beam Laser I, True Sanshas Xray S = 1
		350mm Railgun II, Federation Navy Iron Charge L = 80
		Rapid Heavy Missile Launcher II, Nova Fury Heavy Missile = 25
		Large Ancillary Remote Shield Booster, Navy Cap Booster 200 = 7
		Large Ancillary Remote Armor Repairer, Nanite Repair Paste = 64
		Festival Launcher, Copper Firework CXIV = 50
		*/
		String text = "[Abaddon, Abaddon fit]\n" +
			"\n" +
			"Large Ancillary Armor Repairer, Nanite Repair Paste\n" + //64
			"[Empty Low slot]\n" +
			"[Empty Low slot]\n" +
			"[Empty Low slot]\n" +
			"[Empty Low slot]\n" +
			"[Empty Low slot]\n" +
			"[Empty Low slot]\n" +
			"\n" +
			"Remote Sensor Booster II, Scan Resolution Script\n" + // 1
			"'Shady' Sensor Booster, Targeting Range Script\n" + //1
			"Guidance Disruptor II, Missile Range Disruption Script\n" + //1
			"Small Ancillary Shield Booster, Navy Cap Booster 25\n" +
			"\n" +
			"Dual Light Beam Laser I, True Sanshas Xray S\n" + //1
			"350mm Railgun II, Federation Navy Iron Charge L\n" + //80
			"Rapid Heavy Missile Launcher II, Nova Fury Heavy Missile\n" + //25
			"Large Ancillary Remote Shield Booster, Navy Cap Booster 200\n" + //7
			"Small Tractor Beam II\n" +
			"Large Ancillary Remote Armor Repairer, Nanite Repair Paste\n" + //64
			"Festival Launcher, Copper Firework CXIV\n" + //50
			"[Empty High slot]\n" +
			"\n" +
			"[Empty Rig slot]\n" +
			"[Empty Rig slot]\n" +
			"[Empty Rig slot]\n" +
			"\n" +
			"\n" +
			"'Augmented' Vespa x1\n" +
			"Berserker II x1\n" +
			"Gecko x1\n" +
			"Heavy Hull Maintenance Bot II x1\n" +
			"Hornet EC-300 x1\n" +
			"Hornet I x1\n" +
			"Imperial Navy Curator x1\n" +
			"\n" +
			"\n" +
			"Blood Dagger Firework x1\n" +
			"Ace of Podhunters Firework x1\n" + //Formerly known as "Easter Firework"
			"Sodium Firework x1\n" +
			"Yoiul Festival Firework x1\n" +
			"Armor EM Resistance Script x1\n" +
			"Armor Explosive Resistance Script x1\n" +
			"Janitor x1\n" +
			"Armor Kinetic Resistance Script x1\n" +
			"Armor Thermal Resistance Script x1\n" +
			"Shield Kinetic Resistance Script x1\n" +
			"Shield Thermal Resistance Script x1\n" +
			"Missile Precision Script x1\n" +
			"Scan Resolution Dampening Script x1\n" +
			"Targeting Range Dampening Script x1\n" +
			"Tracking Speed Disruption Script x1\n" +
			"Tracking Speed Script x1\n" +
			"Heavy Armor Maintenance Bot I x1\n" +
			"Medium Shield Maintenance Bot II x1\n" +
			"Exotic Dancers, Male x5, Nanite Repair Paste\r\n" + 
			"Exotic Dancers, Male, Navy Cap Booster 200";
		Map<String, Double> data = test(text, 49);
		assertEquals(6, data.get("Exotic Dancers, Male"), 0.1);
		assertEquals(128, data.get("Nanite Repair Paste"), 0.1);
		assertEquals(7, data.get("Navy Cap Booster 200"), 0.1);
		assertEquals(1, data.get("Scan Resolution Script"), 0.1);
		assertEquals(1, data.get("Targeting Range Script"), 0.1);
		assertEquals(1, data.get("Missile Range Disruption Script"), 0.1);
		assertEquals(9, data.get("Navy Cap Booster 25"), 0.1);
		assertEquals(1, data.get("True Sanshas Xray S"), 0.1);
		assertEquals(80, data.get("Federation Navy Iron Charge L"), 0.1);
		assertEquals(25, data.get("Nova Fury Heavy Missile"), 0.1);
		assertEquals(50, data.get("Copper Firework CXIV"), 0.1);
	}

	private Map<String, Double> test(String text, int size) {
		Map<String, Double> data = importEft.doImport(text);
		assertNotNull(data);
		assertEquals(size, data.size());
		assertEquals(data.size(), importEft.importText(text).size());
		return data;
	}
	
}
