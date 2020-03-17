/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.orders;

import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Test;
import static org.junit.Assert.*;


public class MarketOrdersTabTest {
	
	public MarketOrdersTabTest() {
	}

	@Test
	public void testSignificantIncrement() {
		assertThat(MarketOrdersTab.significantIncrement(1000), equalTo(1001.0));
		assertThat(MarketOrdersTab.significantIncrement(10000), equalTo(10010.0));
		assertThat(MarketOrdersTab.significantIncrement(1001), equalTo(1002.0));
		assertThat(MarketOrdersTab.significantIncrement(10010), equalTo(10020.0));
		assertThat(MarketOrdersTab.significantIncrement(9999), equalTo(10000.0));
		assertThat(MarketOrdersTab.significantIncrement(99990), equalTo(100000.0));
		
		
		assertThat(MarketOrdersTab.significantDecrement(480.0), equalTo(479.9));
		assertThat(MarketOrdersTab.significantDecrement(1000), equalTo(999.0));
		assertThat(MarketOrdersTab.significantDecrement(10000), equalTo(9990.0));
		assertThat(MarketOrdersTab.significantDecrement(1002), equalTo(1001.0));
		assertThat(MarketOrdersTab.significantDecrement(10020), equalTo(10010.0));
	}
	
}
