/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.menu;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SeparatorList;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.MaterialTotal;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialSeparatorComparator;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.assertEquals;


public class JMenuInfoTest {

	private static Material summaryAll;
	private static Material summaryTotal_group1;
	private static Material summaryTotal_group2;
	private static Material summary_group1;
	private static Material summary_group2;
	private static Material all_location1;
	private static Material all_location2;
	private static Material total_location1_group1;
	private static Material total_location1_group2;
	private static Material total_location2_group1;
	private static Material total_location2_group2;
	private static Material name1_location1_group1;
	private static Material name2_location1_group2;
	private static Material name1_location2_group1;
	private static Material name2_location2_group2;
	/*
	private static Material location_location1_group1;
	private static Material location_location1_group2;
	private static Material location_location2_group1;
	private static Material location_location2_group2;
	*/
	private static final String NAME_1 = "NAME_1";
	private static final String NAME_2 = "NAME_2";
	private static final String NAME_3 = "NAME_3";
	private static final String NAME_4 = "NAME_4";
	private static final String GROUP_1 = "GROUP_1";
	private static final String GROUP_2 = "GROUP_2";
	private static final String LOCATION_1 = "LOCATION_1";
	private static final String LOCATION_2 = "LOCATION_2";
	private static final List<Material> ALL = new ArrayList<Material>();

	public JMenuInfoTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		Logger.getRootLogger().setLevel(Level.OFF);
		summaryAll = new Material(Material.MaterialType.SUMMARY_ALL, null, TabsMaterials.get().summary(), "", "");
		summaryAll.updateValue(1, 400);
		ALL.add(summaryAll);

		summaryTotal_group1 = new Material(Material.MaterialType.SUMMARY_TOTAL, null, TabsMaterials.get().summary(), "", GROUP_1);
		summaryTotal_group1.updateValue(1, 200);
		ALL.add(summaryTotal_group1);

		summaryTotal_group2 = new Material(Material.MaterialType.SUMMARY_TOTAL, null, TabsMaterials.get().summary(), "", GROUP_2);
		summaryTotal_group2.updateValue(1, 200);
		ALL.add(summaryTotal_group2);

		summary_group1 = new Material(Material.MaterialType.SUMMARY, null, TabsMaterials.get().summary(), GROUP_1, NAME_1);
		summary_group1.updateValue(1, 200);
		ALL.add(summary_group1);

		summary_group2 = new Material(Material.MaterialType.SUMMARY, null, TabsMaterials.get().summary(), GROUP_2, NAME_2);
		summary_group2.updateValue(1, 200);
		ALL.add(summary_group2);

		all_location1 = new Material(Material.MaterialType.LOCATIONS_ALL, null, LOCATION_1, "", "");
		all_location1.updateValue(1, 200);
		ALL.add(all_location1);

		all_location2 = new Material(Material.MaterialType.LOCATIONS_ALL, null, LOCATION_2, "", "");
		all_location2.updateValue(1, 200);
		ALL.add(all_location2);

		total_location1_group1 = new Material(Material.MaterialType.LOCATIONS_TOTAL, null, LOCATION_1, "", GROUP_1);
		total_location1_group1.updateValue(1, 100);
		ALL.add(total_location1_group1);

		total_location1_group2 = new Material(Material.MaterialType.LOCATIONS_TOTAL, null, LOCATION_1, "", GROUP_2);
		total_location1_group2.updateValue(1, 100);
		ALL.add(total_location1_group2);

		total_location2_group1 = new Material(Material.MaterialType.LOCATIONS_TOTAL, null, LOCATION_2, "", GROUP_1);
		total_location2_group1.updateValue(1, 100);
		ALL.add(total_location2_group1);

		total_location2_group2 = new Material(Material.MaterialType.LOCATIONS_TOTAL, null, LOCATION_2, "", GROUP_2);
		total_location2_group2.updateValue(1, 100);
		ALL.add(total_location2_group2);

		name1_location1_group1 = new Material(Material.MaterialType.LOCATIONS, null, LOCATION_1, GROUP_1, NAME_1);
		name1_location1_group1.updateValue(1, 100);
		ALL.add(name1_location1_group1);

		name2_location1_group2 = new Material(Material.MaterialType.LOCATIONS, null, LOCATION_1, GROUP_2, NAME_2);
		name2_location1_group2.updateValue(1, 100);
		ALL.add(name2_location1_group2);

		name1_location2_group1 = new Material(Material.MaterialType.LOCATIONS, null, LOCATION_2, GROUP_1, NAME_1);
		name1_location2_group1.updateValue(1, 100);
		ALL.add(name1_location2_group1);

		name2_location2_group2 = new Material(Material.MaterialType.LOCATIONS, null, LOCATION_2, GROUP_2, NAME_2);
		name2_location2_group2.updateValue(1, 100);
		ALL.add(name2_location2_group2);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of calcMaterialTotal method, of class JMenuInfo.
	 */
	@Test
	public void testCalcMaterialTotal() {
		List<Material> selected;
		MaterialTotal result;

		selected = new ArrayList<Material>();
		selected.add(summaryAll);
		selected.add(summaryTotal_group1);
		selected.add(summaryTotal_group2);
		selected.add(summary_group1);
		selected.add(summary_group2);
		selected.add(all_location1);
		selected.add(all_location2);
		selected.add(total_location1_group1);
		selected.add(total_location1_group2);
		selected.add(total_location2_group1);
		selected.add(total_location2_group2);
		selected.add(name1_location1_group1);
		selected.add(name2_location1_group2);
		selected.add(name1_location2_group1);
		selected.add(name2_location2_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(400, result.getTotalValue(), 0);

		EventList<Material> materialEventList = new BasicEventList<Material>();
		materialEventList.add(summaryAll);
		materialEventList.add(summaryTotal_group1);
		materialEventList.add(summaryTotal_group2);
		materialEventList.add(summary_group1);
		materialEventList.add(summary_group2);
		materialEventList.add(all_location1);
		materialEventList.add(all_location2);
		materialEventList.add(total_location1_group1);
		materialEventList.add(total_location1_group2);
		materialEventList.add(total_location2_group1);
		materialEventList.add(total_location2_group2);
		materialEventList.add(name1_location1_group1);
		materialEventList.add(name2_location1_group2);
		materialEventList.add(name1_location2_group1);
		materialEventList.add(name2_location2_group2);
		SeparatorList<Material> separatorList = new SeparatorList<Material>(materialEventList, new MaterialSeparatorComparator(), 1, Integer.MAX_VALUE);
		result = JMenuInfo.calcMaterialTotal(separatorList, ALL);
		assertEquals(400, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summaryTotal_group1);
		selected.add(summaryTotal_group2);
		selected.add(summary_group1);
		selected.add(summary_group2);
		selected.add(all_location1);
		selected.add(all_location2);
		selected.add(total_location1_group1);
		selected.add(total_location1_group2);
		selected.add(total_location2_group1);
		selected.add(total_location2_group2);
		selected.add(name1_location1_group1);
		selected.add(name2_location1_group2);
		selected.add(name1_location2_group1);
		selected.add(name2_location2_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(400, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summary_group1);
		selected.add(summary_group2);
		selected.add(all_location1);
		selected.add(all_location2);
		selected.add(total_location1_group1);
		selected.add(total_location1_group2);
		selected.add(total_location2_group1);
		selected.add(total_location2_group2);
		selected.add(name1_location1_group1);
		selected.add(name2_location1_group2);
		selected.add(name1_location2_group1);
		selected.add(name2_location2_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(400, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(all_location1);
		selected.add(all_location2);
		selected.add(total_location1_group1);
		selected.add(total_location1_group2);
		selected.add(total_location2_group1);
		selected.add(total_location2_group2);
		selected.add(name1_location1_group1);
		selected.add(name2_location1_group2);
		selected.add(name1_location2_group1);
		selected.add(name2_location2_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(400, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(total_location1_group1);
		selected.add(total_location1_group2);
		selected.add(total_location2_group1);
		selected.add(total_location2_group2);
		selected.add(name1_location1_group1);
		selected.add(name2_location1_group2);
		selected.add(name1_location2_group1);
		selected.add(name2_location2_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(400, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(name1_location1_group1);
		selected.add(name2_location1_group2);
		selected.add(name1_location2_group1);
		selected.add(name2_location2_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(400, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summary_group1);
		selected.add(all_location1);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(300, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summaryTotal_group1);
		selected.add(all_location1);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(300, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summary_group1);
		selected.add(total_location1_group1);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(200, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summary_group1);
		selected.add(total_location1_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(300, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summaryTotal_group1);
		selected.add(total_location1_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(300, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summary_group1);
		selected.add(name1_location1_group1);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(200, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summary_group1);
		selected.add(name2_location1_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(300, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summary_group1);
		selected.add(name1_location2_group1);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(200, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summary_group1);
		selected.add(name2_location2_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(300, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summaryTotal_group1);
		selected.add(name1_location1_group1);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(200, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summaryTotal_group1);
		selected.add(name2_location1_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(300, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summaryTotal_group1);
		selected.add(name1_location2_group1);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(200, result.getTotalValue(), 0);

		selected = new ArrayList<Material>();
		selected.add(summaryTotal_group1);
		selected.add(name2_location2_group2);
		result = JMenuInfo.calcMaterialTotal(selected, ALL);
		assertEquals(300, result.getTotalValue(), 0);
	}
}
