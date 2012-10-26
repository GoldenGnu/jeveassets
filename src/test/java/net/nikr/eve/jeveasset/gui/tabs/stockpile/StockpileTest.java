/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.util.Arrays;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Niklas
 */
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
		Stockpile stockpile = new Stockpile("Name", 0, "Owner", 0, "Location", "System", "Region", 0, "Flag", "Container", true, true, true, true);
		StockpileItem item1 = new Stockpile.StockpileItem(stockpile, "Name", "Group", 0, 0);
		StockpileItem item2 = new Stockpile.StockpileItem(stockpile, "Name", "Group", 0, 0);
		StockpileTotal total1 = new StockpileTotal(stockpile);
		StockpileTotal total2 = new StockpileTotal(stockpile);
		assertEquals(item1.compareTo(item2), item2.compareTo(item2), 0);
		assertEquals(total1.compareTo(total2), total2.compareTo(total1), 0);
		assertEquals(0, total1.compareTo(total2), 0);
		assertEquals(0, item1.compareTo(item2), 0);
	}
}
