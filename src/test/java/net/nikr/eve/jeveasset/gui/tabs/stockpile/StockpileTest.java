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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.util.Collections;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class StockpileTest {
	
	public StockpileTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	@Test
	public void testSomeMethod() {
		StockpileFilter filter = new StockpileFilter(new MyLocation(0), 
				Collections.singletonList(0),
				Collections.singletonList("Container"),
				Collections.singletonList(0L),
				true, true, true, true, true, true);
		Stockpile stockpile = new Stockpile("Name", Collections.singletonList(filter), 1);
		StockpileItem item1 = new Stockpile.StockpileItem(stockpile, new Item(0), 0, 0);
		StockpileItem item2 = new Stockpile.StockpileItem(stockpile, new Item(0), 0, 0);
		StockpileTotal total1 = new StockpileTotal(stockpile);
		StockpileTotal total2 = new StockpileTotal(stockpile);
		assertEquals(item1.compareTo(item2), item2.compareTo(item2), 0);
		assertEquals(total1.compareTo(total2), total2.compareTo(total1), 0);
		assertEquals(0, total1.compareTo(total2), 0);
		assertEquals(0, item1.compareTo(item2), 0);
	}
}
