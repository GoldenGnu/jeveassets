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
import static org.junit.Assert.*;
import org.junit.Test;


public class ImportIskPerHourTest extends TestUtil {

	private final ImportIskPerHour importIskPerHour = new ImportIskPerHour();

	private Map<String, Double> test(String text, int size) {
		return test(text, size, true);
	}

	private Map<String, Double> test(String text, int size, boolean tritanium) {
		Map<String, Double> data = importIskPerHour.doImport(text);
		assertNotNull(data);
		assertEquals(size, data.size());
		if (tritanium) {
			assertEquals(2932280, data.get("Tritanium"), 0.1);
		}
		assertEquals(data.size(), importIskPerHour.importText(text).size());
		return data;
	}

	@Test
	public void testCopyEveList() {
		String eveListFormat = "Tritanium 2932280\n" +
					"Pyerite 915005\n" +
					"Mexallon 190244\n" +
					"Crystalline Carbonide 144316\n" +
					"Isogen 63721\n" +
					"Sylramic Fibers 24033\n" +
					"Phenolic Composites 12474\n" +
					"Nocxium 8837\n" +
					"Photonic Metamaterials 4940\n" +
					"Nanotransistors 4683\n" +
					"Fullerides 4532\n" +
					"Zydrine 3364\n" +
					"Crystalline Carbonide Armor Plate 2058\n" +
					"Photon Microprocessor 2058\n" +
					"Megacyte 1618\n" +
					"Oscillator Capacitor Unit 412\n" +
					"Hypersynaptic Fibers 310\n" +
					"Construction Blocks 206\n" +
					"Ferrogel 197\n" +
					"Pulse Shield Emitter 155\n" +
					"Magnetometric Sensor Cluster 155\n" +
					"Morphite 148\n" +
					"Datacore - Gallentean Starship Engineering 72\n" +
					"Datacore - Laser Physics 72\n" +
					"Fermionic Condensates 64\n" +
					"Ion Thruster 42\n" +
					"Fusion Reactor Unit 32\n" +
					"R.A.M.- Starship Tech 9\n" +
					"Procurer 1";
		test(eveListFormat, 29);
	}

	@Test
	public void testCopyDefault() {
		String plainFormat = "Shopping List for: \n" +
					"Material - Quantity\n" +
					"10000MN Afterburner I (ME: 0, NumBPs: 1) - 1\n" +
					"Location: Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"Algos (ME: 0, NumBPs: 1) - 1\n" +
					"Location: Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"Skiff (ME: 2, NumBPs: 1) - 1\n" +
					"Location: Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"Skiff (ME: 2, NumBPs: 2) - 2\n" +
					"Location: Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"\n" +
					"Estimated Invention Materials: \n" +
					"Material - Quantity\n" +
					"Datacore - Gallentean Starship Engineering - 72\n" +
					"Datacore - Laser Physics - 72\n" +
					"\n" +
					"Total Volume of Materials: 14.40 m3\n" +
					"Total Cost of Materials: 6,049,975.68 ISK\n" +
					"\n" +
					"Build Items List: \n" +
					"Material - Quantity\n" +
					"Photon Microprocessor (ME: 0, NumBPs: 1) - 2,058\n" +
					"Crystalline Carbonide Armor Plate (ME: 0, NumBPs: 1) - 2,058\n" +
					"Oscillator Capacitor Unit (ME: 0, NumBPs: 1) - 412\n" +
					"Magnetometric Sensor Cluster (ME: 0, NumBPs: 1) - 155\n" +
					"Pulse Shield Emitter (ME: 0, NumBPs: 1) - 155\n" +
					"Ion Thruster (ME: 0, NumBPs: 1) - 42\n" +
					"Fusion Reactor Unit (ME: 0, NumBPs: 1) - 32\n" +
					"R.A.M.- Starship Tech (ME: 0, NumBPs: 1) - 9\n" +
					"Procurer (ME: 0, NumBPs: 1) - 1\n" +
					"\n" +
					"Buy Materials List: \n" +
					"Material - Quantity\n" +
					"Tritanium - 2,932,280\n" +
					"Pyerite - 915,005\n" +
					"Mexallon - 190,244\n" +
					"Crystalline Carbonide - 144,316\n" +
					"Isogen - 63,721\n" +
					"Sylramic Fibers - 24,033\n" +
					"Phenolic Composites - 12,474\n" +
					"Nocxium - 8,837\n" +
					"Photonic Metamaterials - 4,940\n" +
					"Nanotransistors - 4,683\n" +
					"Fullerides - 4,532\n" +
					"Zydrine - 3,364\n" +
					"Crystalline Carbonide Armor Plate - 2,058\n" +
					"Photon Microprocessor - 2,058\n" +
					"Megacyte - 1,618\n" +
					"Oscillator Capacitor Unit - 412\n" +
					"Hypersynaptic Fibers - 310\n" +
					"Construction Blocks - 206\n" +
					"Ferrogel - 197\n" +
					"Pulse Shield Emitter - 155\n" +
					"Magnetometric Sensor Cluster - 155\n" +
					"Morphite - 148\n" +
					"Fermionic Condensates - 64\n" +
					"Ion Thruster - 42\n" +
					"Fusion Reactor Unit - 32\n" +
					"R.A.M.- Starship Tech - 9\n" +
					"Procurer - 1\n" +
					"\n" +
					"Total Volume of Materials: 62,519.89 m3\n" +
					"Total Cost of Materials: 273,043,780.28 ISK\n" +
					"Total Volume of Built Item(s): 359,000.00 m3";
		test(plainFormat, 32);
	}

	@Test
	public void testFileDefault() {
		String plainFormat = "Buy List\n" +
					"Material|Quantity|Cost Per Item|Min Sell|Max Buy|Buy Type|Total m3|Isk/m3|TotalCost\n" +
					"Tritanium|2932280.00|0.00|0.00|0.00|Unknown|29322.80|0.00|0.00\n" +
					"Pyerite|915005.00|0.00|0.00|0.00|Unknown|9150.05|0.00|0.00\n" +
					"Mexallon|190244.00|0.00|0.00|0.00|Unknown|1902.44|0.00|0.00\n" +
					"Crystalline Carbonide|144316.00|0.00|0.00|0.00|Unknown|1443.16|0.00|0.00\n" +
					"Isogen|63721.00|0.00|0.00|0.00|Unknown|637.21|0.00|0.00\n" +
					"Sylramic Fibers|24033.00|0.00|0.00|0.00|Unknown|1201.65|0.00|0.00\n" +
					"Phenolic Composites|12474.00|0.00|0.00|0.00|Unknown|2494.80|0.00|0.00\n" +
					"Nocxium|8837.00|0.00|0.00|0.00|Unknown|88.37|0.00|0.00\n" +
					"Photonic Metamaterials|4940.00|0.00|0.00|0.00|Unknown|4940.00|0.00|0.00\n" +
					"Nanotransistors|4683.00|0.00|0.00|0.00|Unknown|1170.75|0.00|0.00\n" +
					"Fullerides|4532.00|0.00|0.00|0.00|Unknown|679.80|0.00|0.00\n" +
					"Zydrine|3364.00|0.00|0.00|0.00|Unknown|33.64|0.00|0.00\n" +
					"Crystalline Carbonide Armor Plate|2058.00|0.00|0.00|0.00|Unknown|2058.00|0.00|0.00\n" +
					"Photon Microprocessor|2058.00|0.00|0.00|0.00|Unknown|2058.00|0.00|0.00\n" +
					"Megacyte|1618.00|0.00|0.00|0.00|Unknown|16.18|0.00|0.00\n" +
					"Oscillator Capacitor Unit|412.00|0.00|0.00|0.00|Unknown|412.00|0.00|0.00\n" +
					"Hypersynaptic Fibers|310.00|0.00|0.00|0.00|Unknown|186.00|0.00|0.00\n" +
					"Construction Blocks|206.00|0.00|0.00|0.00|Unknown|309.00|0.00|0.00\n" +
					"Ferrogel|197.00|0.00|0.00|0.00|Unknown|197.00|0.00|0.00\n" +
					"Pulse Shield Emitter|155.00|0.00|0.00|0.00|Unknown|155.00|0.00|0.00\n" +
					"Magnetometric Sensor Cluster|155.00|0.00|0.00|0.00|Unknown|155.00|0.00|0.00\n" +
					"Morphite|148.00|0.00|0.00|0.00|Unknown|1.48|0.00|0.00\n" +
					"Datacore - Gallentean Starship Engineering|72.00|0.00|0.00|0.00|Unknown|7.20|0.00|0.00\n" +
					"Datacore - Laser Physics|72.00|0.00|0.00|0.00|Unknown|7.20|0.00|0.00\n" +
					"Fermionic Condensates|64.00|0.00|0.00|0.00|Unknown|83.20|0.00|0.00\n" +
					"Ion Thruster|42.00|0.00|0.00|0.00|Unknown|42.00|0.00|0.00\n" +
					"Fusion Reactor Unit|32.00|0.00|0.00|0.00|Unknown|32.00|0.00|0.00\n" +
					"R.A.M.- Starship Tech|9.00|0.00|0.00|0.00|Unknown|0.36|0.00|0.00\n" +
					"Procurer|1.00|0.00|0.00|0.00|Unknown|3750.00|0.00|0.00\n" +
					"\n" +
					"Build List\n" +
					"Build Item|Quantity|ME|TE|Facility Location|Facility Type|IncludeActivityCost|IncludeActivityTime|IncludeUsageCost\n" +
					"Photon Microprocessor|2058.00|0|0|Jita IV - Moon 4 - Caldari Navy Assembly Plant|Station|False|False|True\n" +
					"Crystalline Carbonide Armor Plate|2058.00|0|0|Jita IV - Moon 4 - Caldari Navy Assembly Plant|Station|False|False|True\n" +
					"Oscillator Capacitor Unit|412.00|0|0|Jita IV - Moon 4 - Caldari Navy Assembly Plant|Station|False|False|True\n" +
					"Magnetometric Sensor Cluster|155.00|0|0|Jita IV - Moon 4 - Caldari Navy Assembly Plant|Station|False|False|True\n" +
					"Pulse Shield Emitter|155.00|0|0|Jita IV - Moon 4 - Caldari Navy Assembly Plant|Station|False|False|True\n" +
					"Ion Thruster|42.00|0|0|Jita IV - Moon 4 - Caldari Navy Assembly Plant|Station|False|False|True\n" +
					"Fusion Reactor Unit|32.00|0|0|Jita IV - Moon 4 - Caldari Navy Assembly Plant|Station|False|False|True\n" +
					"R.A.M.- Starship Tech|9.00|0|0|Jita IV - Moon 4 - Caldari Navy Assembly Plant|Station|False|False|True\n" +
					"Procurer|1.00|0|0|Jita IV - Moon 4 - Caldari Navy Assembly Plant|Station|False|False|True\n" +
					"\n" +
					"Item List\n" +
					"Item|Quantity|ME|NumBps|Build Type|Decryptor|Relic|Facility Type|Location|IgnoredInvention|IgnoredMinerals|IgnoredT1BaseItem|IncludeActivityCost|IncludeActivityTime|IncludeUsageCost\n" +
					"10000MN Afterburner I|1.00|0|1|Raw Mats|||Station|Jita IV - Moon 4 - Caldari Navy Assembly Plant|0|0|0|0|0|0\n" +
					"Algos|1.00|0|1|Raw Mats|||Station|Jita IV - Moon 4 - Caldari Navy Assembly Plant|0|0|0|0|0|0\n" +
					"Skiff|1.00|2|1|Raw Mats|None||Station|Jita IV - Moon 4 - Caldari Navy Assembly Plant|0|0|0|0|0|0\n" +
					"Skiff|2.00|2|2|Components|None||Station|Jita IV - Moon 4 - Caldari Navy Assembly Plant|0|0|0|0|0|0";
		test(plainFormat, 32);
	}

	@Test
	public void testCopyCsv() {
		String csvFormat = "Shopping List for: \n" +
					"Material, Quantity, ME, NumBPs, Decryptor/Relic, Cost Per Item, Total Cost, Location\n" +
					"10000MN Afterburner I, 1, 0, 1, None, 29789997.31, 29789997.31, Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"Algos, 1, 0, 1, None, 1305999.39, 1305999.39, Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"Skiff, 1, 2, 1, None, 168493998.99, 168493998.99, Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"Skiff, 2, 2, 2, None, 168493998.99, 336987997.98, Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"\n" +
					"Estimated Invention Materials: \n" +
					"Material, Quantity, Cost Per Item, Total Cost, Location\n" +
					"Datacore - Gallentean Starship Engineering, 72, 43008.93, 3096642.96\n" +
					"Datacore - Laser Physics, 72, 41018.51, 2953332.72\n" +
					"\n" +
					"Total Volume of Materials:,14.4,m3\n" +
					"Total Cost of Materials:,6049975.68,ISK\n" +
					"\n" +
					"Build Items List: \n" +
					"Material, Quantity, ME, NumBPs, Cost Per Item, Total Cost, Location\n" +
					"Photon Microprocessor, 2058, 0, 1, 43749.11, 90035668.38\n" +
					"Crystalline Carbonide Armor Plate, 2058, 0, 1, 11497.94, 23662760.52\n" +
					"Oscillator Capacitor Unit, 412, 0, 1, 44884.19, 18492286.28\n" +
					"Magnetometric Sensor Cluster, 155, 0, 1, 28997, 4494535\n" +
					"Pulse Shield Emitter, 155, 0, 1, 46997.76, 7284652.8\n" +
					"Ion Thruster, 42, 0, 1, 47899.58, 2011782.36\n" +
					"Fusion Reactor Unit, 32, 0, 1, 161498.09, 5167938.88\n" +
					"R.A.M.- Starship Tech, 9, 0, 1, 299.99, 2699.91\n" +
					"Procurer, 1, 0, 1, 18494999, 18494999\n" +
					"\n" +
					"Buy Materials List: \n" +
					"Material, Quantity, Cost Per Item, Total Cost, Location\n" +
					"Tritanium, 2932280, 2, 5864560\n" +
					"Pyerite, 915005, 4, 3660020\n" +
					"Mexallon, 190244, 37.01, 7040930.44\n" +
					"Crystalline Carbonide, 144316, 106.09, 15310484.44\n" +
					"Isogen, 63721, 40, 2548840\n" +
					"Sylramic Fibers, 24033, 199.14, 4785931.62\n" +
					"Phenolic Composites, 12474, 1117.39, 13938322.86\n" +
					"Nocxium, 8837, 247.14, 2183976.18\n" +
					"Photonic Metamaterials, 4940, 6000.01, 29640049.4\n" +
					"Nanotransistors, 4683, 1260.78, 5904232.74\n" +
					"Fullerides, 4532, 350.52, 1588556.64\n" +
					"Zydrine, 3364, 521.91, 1755705.24\n" +
					"Crystalline Carbonide Armor Plate, 2058, 11497.94, 23662760.52\n" +
					"Photon Microprocessor, 2058, 43749.11, 90035668.38\n" +
					"Megacyte, 1618, 136.02, 220080.36\n" +
					"Oscillator Capacitor Unit, 412, 44884.19, 18492286.28\n" +
					"Hypersynaptic Fibers, 310, 4000, 1240000\n" +
					"Construction Blocks, 206, 8104.33, 1669491.98\n" +
					"Ferrogel, 197, 16881.57, 3325669.29\n" +
					"Pulse Shield Emitter, 155, 46997.76, 7284652.8\n" +
					"Magnetometric Sensor Cluster, 155, 28997, 4494535\n" +
					"Morphite, 148, 6700.01, 991601.48\n" +
					"Fermionic Condensates, 64, 27000.07, 1728004.48\n" +
					"Ion Thruster, 42, 47899.58, 2011782.36\n" +
					"Fusion Reactor Unit, 32, 161498.09, 5167938.88\n" +
					"R.A.M.- Starship Tech, 9, 299.99, 2699.91\n" +
					"Procurer, 1, 18494999, 18494999\n" +
					"\n" +
					"Total Volume of Materials:,62519.89,m3\n" +
					"Total Cost of Materials:,273043780.28,ISK\n" +
					"Total Volume of Built Item(s):,359000,m3";
		test(csvFormat, 32);
	}

	@Test
	public void testFileCsv() {
		String csvFormat = "Buy List\n" +
					"Material,Quantity,Cost Per Item,Min Sell,Max Buy,Buy Type,Total m3,Isk/m3,TotalCost\n" +
					"Tritanium,2932280.00,0.00,0.00,0.00,Unknown,29322.80,0.00,0.00\n" +
					"Pyerite,915005.00,0.00,0.00,0.00,Unknown,9150.05,0.00,0.00\n" +
					"Mexallon,190244.00,0.00,0.00,0.00,Unknown,1902.44,0.00,0.00\n" +
					"Crystalline Carbonide,144316.00,0.00,0.00,0.00,Unknown,1443.16,0.00,0.00\n" +
					"Isogen,63721.00,0.00,0.00,0.00,Unknown,637.21,0.00,0.00\n" +
					"Sylramic Fibers,24033.00,0.00,0.00,0.00,Unknown,1201.65,0.00,0.00\n" +
					"Phenolic Composites,12474.00,0.00,0.00,0.00,Unknown,2494.80,0.00,0.00\n" +
					"Nocxium,8837.00,0.00,0.00,0.00,Unknown,88.37,0.00,0.00\n" +
					"Photonic Metamaterials,4940.00,0.00,0.00,0.00,Unknown,4940.00,0.00,0.00\n" +
					"Nanotransistors,4683.00,0.00,0.00,0.00,Unknown,1170.75,0.00,0.00\n" +
					"Fullerides,4532.00,0.00,0.00,0.00,Unknown,679.80,0.00,0.00\n" +
					"Zydrine,3364.00,0.00,0.00,0.00,Unknown,33.64,0.00,0.00\n" +
					"Crystalline Carbonide Armor Plate,2058.00,0.00,0.00,0.00,Unknown,2058.00,0.00,0.00\n" +
					"Photon Microprocessor,2058.00,0.00,0.00,0.00,Unknown,2058.00,0.00,0.00\n" +
					"Megacyte,1618.00,0.00,0.00,0.00,Unknown,16.18,0.00,0.00\n" +
					"Oscillator Capacitor Unit,412.00,0.00,0.00,0.00,Unknown,412.00,0.00,0.00\n" +
					"Hypersynaptic Fibers,310.00,0.00,0.00,0.00,Unknown,186.00,0.00,0.00\n" +
					"Construction Blocks,206.00,0.00,0.00,0.00,Unknown,309.00,0.00,0.00\n" +
					"Ferrogel,197.00,0.00,0.00,0.00,Unknown,197.00,0.00,0.00\n" +
					"Pulse Shield Emitter,155.00,0.00,0.00,0.00,Unknown,155.00,0.00,0.00\n" +
					"Magnetometric Sensor Cluster,155.00,0.00,0.00,0.00,Unknown,155.00,0.00,0.00\n" +
					"Morphite,148.00,0.00,0.00,0.00,Unknown,1.48,0.00,0.00\n" +
					"Datacore - Gallentean Starship Engineering,72.00,0.00,0.00,0.00,Unknown,7.20,0.00,0.00\n" +
					"Datacore - Laser Physics,72.00,0.00,0.00,0.00,Unknown,7.20,0.00,0.00\n" +
					"Fermionic Condensates,64.00,0.00,0.00,0.00,Unknown,83.20,0.00,0.00\n" +
					"Ion Thruster,42.00,0.00,0.00,0.00,Unknown,42.00,0.00,0.00\n" +
					"Fusion Reactor Unit,32.00,0.00,0.00,0.00,Unknown,32.00,0.00,0.00\n" +
					"R.A.M.- Starship Tech,9.00,0.00,0.00,0.00,Unknown,0.36,0.00,0.00\n" +
					"Procurer,1.00,0.00,0.00,0.00,Unknown,3750.00,0.00,0.00\n" +
					"\n" +
					"Build List\n" +
					"Build Item,Quantity,ME,TE,Facility Location,Facility Type,IncludeActivityCost,IncludeActivityTime,IncludeUsageCost\n" +
					"Photon Microprocessor,2058.00,0,0,Jita IV - Moon 4 - Caldari Navy Assembly Plant,Station,False,False,True\n" +
					"Crystalline Carbonide Armor Plate,2058.00,0,0,Jita IV - Moon 4 - Caldari Navy Assembly Plant,Station,False,False,True\n" +
					"Oscillator Capacitor Unit,412.00,0,0,Jita IV - Moon 4 - Caldari Navy Assembly Plant,Station,False,False,True\n" +
					"Magnetometric Sensor Cluster,155.00,0,0,Jita IV - Moon 4 - Caldari Navy Assembly Plant,Station,False,False,True\n" +
					"Pulse Shield Emitter,155.00,0,0,Jita IV - Moon 4 - Caldari Navy Assembly Plant,Station,False,False,True\n" +
					"Ion Thruster,42.00,0,0,Jita IV - Moon 4 - Caldari Navy Assembly Plant,Station,False,False,True\n" +
					"Fusion Reactor Unit,32.00,0,0,Jita IV - Moon 4 - Caldari Navy Assembly Plant,Station,False,False,True\n" +
					"R.A.M.- Starship Tech,9.00,0,0,Jita IV - Moon 4 - Caldari Navy Assembly Plant,Station,False,False,True\n" +
					"Procurer,1.00,0,0,Jita IV - Moon 4 - Caldari Navy Assembly Plant,Station,False,False,True\n" +
					"\n" +
					"Item List\n" +
					"Item,Quantity,ME,NumBps,Build Type,Decryptor,Relic,Facility Type,Location,IgnoredInvention,IgnoredMinerals,IgnoredT1BaseItem,IncludeActivityCost,IncludeActivityTime,IncludeUsageCost\n" +
					"10000MN Afterburner I,1.00,0,1,Raw Mats,,,Station,Jita IV - Moon 4 - Caldari Navy Assembly Plant,0,0,0,0,0,0\n" +
					"Algos,1.00,0,1,Raw Mats,,,Station,Jita IV - Moon 4 - Caldari Navy Assembly Plant,0,0,0,0,0,0\n" +
					"Skiff,1.00,2,1,Raw Mats,None,,Station,Jita IV - Moon 4 - Caldari Navy Assembly Plant,0,0,0,0,0,0\n" +
					"Skiff,2.00,2,2,Components,None,,Station,Jita IV - Moon 4 - Caldari Navy Assembly Plant,0,0,0,0,0,0";
		test(csvFormat, 32);
	}

	@Test
	public void testCopySSV() {
		String ssvFormat = "Shopping List for: \n" +
					"Material; Quantity; ME; NumBPs; Decryptor/Relic; Cost Per Item; Total Cost; Location\n" +
					"10000MN Afterburner I; 1; 0; 1; None; 29789997,31; 29789997,31; Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"Algos; 1; 0; 1; None; 1305999,39; 1305999,39; Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"Skiff; 1; 2; 1; None; 168493998,99; 168493998,99; Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"Skiff; 2; 2; 2; None; 168493998,99; 336987997,98; Jita IV - Moon 4 - Caldari Navy Assembly Plant\n" +
					"\n" +
					"Estimated Invention Materials: \n" +
					"Material; Quantity; Cost Per Item; Total Cost; Location\n" +
					"Datacore - Gallentean Starship Engineering; 72; 43008,93; 3096642,96\n" +
					"Datacore - Laser Physics; 72; 41018,51; 2953332,72\n" +
					"\n" +
					"Total Volume of Materials:;14,4;m3\n" +
					"Total Cost of Materials:;6049975,68;ISK\n" +
					"\n" +
					"Build Items List: \n" +
					"Material; Quantity; ME; NumBPs; Cost Per Item; Total Cost; Location\n" +
					"Photon Microprocessor; 2058; 0; 1; 43749,11; 90035668,38\n" +
					"Crystalline Carbonide Armor Plate; 2058; 0; 1; 11497,94; 23662760,52\n" +
					"Oscillator Capacitor Unit; 412; 0; 1; 44884,19; 18492286,28\n" +
					"Magnetometric Sensor Cluster; 155; 0; 1; 28997; 4494535\n" +
					"Pulse Shield Emitter; 155; 0; 1; 46997,76; 7284652,8\n" +
					"Ion Thruster; 42; 0; 1; 47899,58; 2011782,36\n" +
					"Fusion Reactor Unit; 32; 0; 1; 161498,09; 5167938,88\n" +
					"R.A.M.- Starship Tech; 9; 0; 1; 299,99; 2699,91\n" +
					"Procurer; 1; 0; 1; 18494999; 18494999\n" +
					"\n" +
					"Buy Materials List: \n" +
					"Material; Quantity; Cost Per Item; Total Cost; Location\n" +
					"Tritanium; 2932280; 2; 5864560\n" +
					"Pyerite; 915005; 4; 3660020\n" +
					"Mexallon; 190244; 37,01; 7040930,44\n" +
					"Crystalline Carbonide; 144316; 106,09; 15310484,44\n" +
					"Isogen; 63721; 40; 2548840\n" +
					"Sylramic Fibers; 24033; 199,14; 4785931,62\n" +
					"Phenolic Composites; 12474; 1117,39; 13938322,86\n" +
					"Nocxium; 8837; 247,14; 2183976,18\n" +
					"Photonic Metamaterials; 4940; 6000,01; 29640049,4\n" +
					"Nanotransistors; 4683; 1260,78; 5904232,74\n" +
					"Fullerides; 4532; 350,52; 1588556,64\n" +
					"Zydrine; 3364; 521,91; 1755705,24\n" +
					"Crystalline Carbonide Armor Plate; 2058; 11497,94; 23662760,52\n" +
					"Photon Microprocessor; 2058; 43749,11; 90035668,38\n" +
					"Megacyte; 1618; 136,02; 220080,36\n" +
					"Oscillator Capacitor Unit; 412; 44884,19; 18492286,28\n" +
					"Hypersynaptic Fibers; 310; 4000; 1240000\n" +
					"Construction Blocks; 206; 8104,33; 1669491,98\n" +
					"Ferrogel; 197; 16881,57; 3325669,29\n" +
					"Pulse Shield Emitter; 155; 46997,76; 7284652,8\n" +
					"Magnetometric Sensor Cluster; 155; 28997; 4494535\n" +
					"Morphite; 148; 6700,01; 991601,48\n" +
					"Fermionic Condensates; 64; 27000,07; 1728004,48\n" +
					"Ion Thruster; 42; 47899,58; 2011782,36\n" +
					"Fusion Reactor Unit; 32; 161498,09; 5167938,88\n" +
					"R.A.M.- Starship Tech; 9; 299,99; 2699,91\n" +
					"Procurer; 1; 18494999; 18494999\n" +
					"\n" +
					"Total Volume of Materials:;62519,89;m3\n" +
					"Total Cost of Materials:;273043780,28;ISK\n" +
					"Total Volume of Built Item(s):;359000;m3";
		test(ssvFormat, 32);
	}

	@Test
	public void testFileSSV() {
		String ssvFormat = "Buy List\n" +
					"Material;Quantity;Cost Per Item;Min Sell;Max Buy;Buy Type;Total m3;Isk/m3;TotalCost\n" +
					"Tritanium;2.932.280;0,00;0,00;0,00;Unknown;29.322,80;0,00;0,00\n" +
					"Pyerite;915.005;0,00;0,00;0,00;Unknown;9.150,05;0,00;0,00\n" +
					"Mexallon;190.244;0,00;0,00;0,00;Unknown;1.902,44;0,00;0,00\n" +
					"Crystalline Carbonide;144.316;0,00;0,00;0,00;Unknown;1.443,16;0,00;0,00\n" +
					"Isogen;63.721;0,00;0,00;0,00;Unknown;637,21;0,00;0,00\n" +
					"Sylramic Fibers;24.033;0,00;0,00;0,00;Unknown;1.201,65;0,00;0,00\n" +
					"Phenolic Composites;12.474;0,00;0,00;0,00;Unknown;2.494,80;0,00;0,00\n" +
					"Nocxium;8.837;0,00;0,00;0,00;Unknown;88,37;0,00;0,00\n" +
					"Photonic Metamaterials;4.940;0,00;0,00;0,00;Unknown;4.940,00;0,00;0,00\n" +
					"Nanotransistors;4.683;0,00;0,00;0,00;Unknown;1.170,75;0,00;0,00\n" +
					"Fullerides;4.532;0,00;0,00;0,00;Unknown;679,80;0,00;0,00\n" +
					"Zydrine;3.364;0,00;0,00;0,00;Unknown;33,64;0,00;0,00\n" +
					"Crystalline Carbonide Armor Plate;2.058;0,00;0,00;0,00;Unknown;2.058,00;0,00;0,00\n" +
					"Photon Microprocessor;2.058;0,00;0,00;0,00;Unknown;2.058,00;0,00;0,00\n" +
					"Megacyte;1.618;0,00;0,00;0,00;Unknown;16,18;0,00;0,00\n" +
					"Oscillator Capacitor Unit;412;0,00;0,00;0,00;Unknown;412,00;0,00;0,00\n" +
					"Hypersynaptic Fibers;310;0,00;0,00;0,00;Unknown;186,00;0,00;0,00\n" +
					"Construction Blocks;206;0,00;0,00;0,00;Unknown;309,00;0,00;0,00\n" +
					"Ferrogel;197;0,00;0,00;0,00;Unknown;197,00;0,00;0,00\n" +
					"Pulse Shield Emitter;155;0,00;0,00;0,00;Unknown;155,00;0,00;0,00\n" +
					"Magnetometric Sensor Cluster;155;0,00;0,00;0,00;Unknown;155,00;0,00;0,00\n" +
					"Morphite;148;0,00;0,00;0,00;Unknown;1,48;0,00;0,00\n" +
					"Datacore - Gallentean Starship Engineering;72;0,00;0,00;0,00;Unknown;7,20;0,00;0,00\n" +
					"Datacore - Laser Physics;72;0,00;0,00;0,00;Unknown;7,20;0,00;0,00\n" +
					"Fermionic Condensates;64;0,00;0,00;0,00;Unknown;83,20;0,00;0,00\n" +
					"Ion Thruster;42;0,00;0,00;0,00;Unknown;42,00;0,00;0,00\n" +
					"Fusion Reactor Unit;32;0,00;0,00;0,00;Unknown;32,00;0,00;0,00\n" +
					"R.A.M.- Starship Tech;9;0,00;0,00;0,00;Unknown;0,36;0,00;0,00\n" +
					"Procurer;1;0,00;0,00;0,00;Unknown;3.750,00;0,00;0,00\n" +
					"\n" +
					"Build List\n" +
					"Build Item;Quantity;ME;TE;Facility Location; Facility Type;IncludeActivityCost;IncludeActivityTime;IncludeUsageCost\n" +
					"Photon Microprocessor;2.058;0;0;Jita IV - Moon 4 - Caldari Navy Assembly Plant;Station;False;False;True\n" +
					"Crystalline Carbonide Armor Plate;2.058;0;0;Jita IV - Moon 4 - Caldari Navy Assembly Plant;Station;False;False;True\n" +
					"Oscillator Capacitor Unit;412;0;0;Jita IV - Moon 4 - Caldari Navy Assembly Plant;Station;False;False;True\n" +
					"Magnetometric Sensor Cluster;155;0;0;Jita IV - Moon 4 - Caldari Navy Assembly Plant;Station;False;False;True\n" +
					"Pulse Shield Emitter;155;0;0;Jita IV - Moon 4 - Caldari Navy Assembly Plant;Station;False;False;True\n" +
					"Ion Thruster;42;0;0;Jita IV - Moon 4 - Caldari Navy Assembly Plant;Station;False;False;True\n" +
					"Fusion Reactor Unit;32;0;0;Jita IV - Moon 4 - Caldari Navy Assembly Plant;Station;False;False;True\n" +
					"R.A.M.- Starship Tech;9;0;0;Jita IV - Moon 4 - Caldari Navy Assembly Plant;Station;False;False;True\n" +
					"Procurer;1;0;0;Jita IV - Moon 4 - Caldari Navy Assembly Plant;Station;False;False;True\n" +
					"\n" +
					"Item List\n" +
					"Item;Quantity;ME;NumBps;Build Type;Decryptor;Relic;Facility Type;Location;IgnoredInvention;IgnoredMinerals;IgnoredT1BaseItem;IncludeActivityCost;IncludeActivityTime;IncludeUsageCost\n" +
					"10000MN Afterburner I;1;0;1;Raw Mats;;;Station;Jita IV - Moon 4 - Caldari Navy Assembly Plant;0;0;0;0;0;0\n" +
					"Algos;1;0;1;Raw Mats;;;Station;Jita IV - Moon 4 - Caldari Navy Assembly Plant;0;0;0;0;0;0\n" +
					"Skiff;1;2;1;Raw Mats;None;;Station;Jita IV - Moon 4 - Caldari Navy Assembly Plant;0;0;0;0;0;0\n" +
					"Skiff;2;2;2;Components;None;;Station;Jita IV - Moon 4 - Caldari Navy Assembly Plant;0;0;0;0;0;0";
		test(ssvFormat, 32);
	}

	@Test
	public void testUserInput() {
		String ssvFormat = "Buy Materials List:\n" +
					"Material, Quantity, Cost Per Item, Total Cost\n" +
					"Drone Transceiver, 1520, 270001.07, 410401626.4\n" +
					"Telemetry Processor, 1520, 25010, 38015200\n" +
					"Micro Circuit, 1520, 8201.02, 12465550.4\n" +
					"Trigger Unit, 1520, 200603.29, 304917000.8\n" +
					"Current Pump, 1520, 32004, 48646080\n" +
					"Lorentz Fluid, 600, 103941.74, 62365044\n" +
					"Power Conduit, 600, 663535.68, 398121408\n" +
					"Nanite Compound, 32, 341239, 10919648\n" +
					"Single-crystal Superalloy I-beam, 32, 112135.02, 3588320.64\n" +
					"Intact Armor Plates, 4, 3041000.02, 12164000.08\n" +
					"R.A.M.- Ammunition Tech, 2, 193.89, 387.78";
		Map<String, Double> data = test(ssvFormat, 11, false);
		assertEquals(1520, data.get("Drone Transceiver"), 0.1);
	}
	
}
