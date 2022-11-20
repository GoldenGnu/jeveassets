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
package net.nikr.eve.jeveasset.data.profile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;


public class StockpileIDsTest extends TestUtil {

	private static final String TABLE_1 = "test_table_1";
	private static final String TABLE_2 = "test_table_2";
	private static final long VALUE = 1234L;
	private static final Set<Long> VALUES = Collections.singleton(VALUE);
	//Database file
	private static final String FILENAME = FileUtil.getLocalFile("testing" + File.separator + "stockpileids_testdb.db", false);
	private static StockpileIDs stockpileIDs;

	@BeforeClass
	public static void init() {
		new File(FILENAME).getParentFile().mkdirs();
		StockpileIDs.setConnectionUrl("jdbc:sqlite:" + FILENAME);
		stockpileIDs = new StockpileIDs(TABLE_1, true);
	}

	@AfterClass
	public static void out() {
		StockpileIDs.setConnectionUrl(StockpileIDs.DEFAULT_CONNECTION_URL);
		stockpileIDs.removeTable();
		new File(FILENAME).delete();
	}

	@Test
	public void testSetConnectionUrl() { }

	@Test
	public void testLoad() { }

	@Test
	public void testTableExist() { }

	@Test
	public void testRemoveTable() { }

	@Test
	public void testGetHidden() {
		assertEquals(0, stockpileIDs.getHidden().size());
		assertTrue(stockpileIDs.getHidden().isEmpty());
		stockpileIDs.hide(VALUE);
		assertEquals(1, stockpileIDs.getHidden().size());

		//Cleanup
		stockpileIDs.show(VALUE);
		assertEquals(0, stockpileIDs.getHidden().size());
	}

	@Test
	public void testIsHidden() {
		assertEquals(0, stockpileIDs.getHidden().size());
		assertFalse(stockpileIDs.isHidden(VALUE));
		stockpileIDs.hide(VALUE);
		assertTrue(stockpileIDs.isHidden(VALUE));

		//Cleanup
		stockpileIDs.show(VALUE);
		assertEquals(0, stockpileIDs.getHidden().size());
	}

	@Test
	public void testIsShown() {
		assertEquals(0, stockpileIDs.getHidden().size());
		assertTrue(stockpileIDs.isShown(VALUE));
		stockpileIDs.hide(VALUE);
		assertFalse(stockpileIDs.isShown(VALUE));

		//Cleanup
		stockpileIDs.show(VALUE);
		assertEquals(0, stockpileIDs.getHidden().size());
	}

	@Test
	public void testSetHidden() {
		assertEquals(0, stockpileIDs.getHidden().size());
		stockpileIDs.setHidden(VALUES);
		assertTrue(stockpileIDs.isHidden(VALUE));
		assertEquals(1, stockpileIDs.getHidden().size());
		stockpileIDs.setHidden(VALUES);
		assertTrue(stockpileIDs.isHidden(VALUE));
		assertEquals(1, stockpileIDs.getHidden().size());

		//Cleanup
		stockpileIDs.show(VALUE);
		assertEquals(0, stockpileIDs.getHidden().size());
	}

	@Test
	public void testHide() {
		assertEquals(0, stockpileIDs.getHidden().size());
		stockpileIDs.hide(VALUE);
		assertTrue(stockpileIDs.isHidden(VALUE));
		assertEquals(1, stockpileIDs.getHidden().size());
		stockpileIDs.hide(VALUE);
		assertTrue(stockpileIDs.isHidden(VALUE));
		assertEquals(1, stockpileIDs.getHidden().size());

		//Cleanup
		stockpileIDs.show(VALUE);
		assertEquals(0, stockpileIDs.getHidden().size());
	}

	@Test
	public void testSetShown() {
		assertEquals(0, stockpileIDs.getHidden().size());
		stockpileIDs.hide(VALUE);
		assertTrue(stockpileIDs.isHidden(VALUE));
		stockpileIDs.setNewDatabase(true);
		stockpileIDs.setShown(VALUES, Collections.singletonList(new Stockpile("some name", VALUE, new ArrayList<>(), 1.0, false)));
		stockpileIDs.setNewDatabase(false);
		assertFalse(stockpileIDs.isHidden(VALUE));
		assertEquals(0, stockpileIDs.getHidden().size());
	}

	@Test
	public void testShow() {
		assertEquals(0, stockpileIDs.getHidden().size());
		stockpileIDs.hide(VALUE);
		assertTrue(stockpileIDs.isHidden(VALUE));
		assertEquals(1, stockpileIDs.getHidden().size());
		stockpileIDs.show(VALUE);
		assertFalse(stockpileIDs.isHidden(VALUE));
		assertEquals(0, stockpileIDs.getHidden().size());
		stockpileIDs.show(VALUE);
		assertFalse(stockpileIDs.isHidden(VALUE));
		assertEquals(0, stockpileIDs.getHidden().size());
	}

	@Test
	public void testRenameTable() {
		assertEquals(0, stockpileIDs.getHidden().size());
		stockpileIDs.hide(VALUE);
		stockpileIDs.renameTable(TABLE_2);
		assertTrue(stockpileIDs.isHidden(VALUE));

		//Cleanup
		stockpileIDs.renameTable(TABLE_1);
		assertTrue(stockpileIDs.isHidden(VALUE));
		stockpileIDs.show(VALUE);
		assertFalse(stockpileIDs.isHidden(VALUE));
		assertEquals(0, stockpileIDs.getHidden().size());
	}

}
