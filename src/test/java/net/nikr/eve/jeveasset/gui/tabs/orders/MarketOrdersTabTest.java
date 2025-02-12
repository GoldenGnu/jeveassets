/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.TestUtil;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;


public class MarketOrdersTabTest extends TestUtil {

	public MarketOrdersTabTest() {
	}

	@Test
	public void testSignificantIncrement() {
		assertThat(MarketOrdersTab.significantIncrement(0.00), equalTo(0.00));
		assertThat(MarketOrdersTab.significantIncrement(0.01), equalTo(0.02));
		assertThat(MarketOrdersTab.significantIncrement(0.1), equalTo(0.11));
		assertThat(MarketOrdersTab.significantIncrement(1), equalTo(1.01));
		assertThat(MarketOrdersTab.significantIncrement(10), equalTo(10.01));
		assertThat(MarketOrdersTab.significantIncrement(100), equalTo(100.10));
		assertThat(MarketOrdersTab.significantIncrement(1000), equalTo(1001.00));
		assertThat(MarketOrdersTab.significantIncrement(10000), equalTo(10010.00));

		assertThat(MarketOrdersTab.significantIncrement(1001), equalTo(1002.00));
		assertThat(MarketOrdersTab.significantIncrement(10010), equalTo(10020.00));

		assertThat(MarketOrdersTab.significantIncrement(0.99), equalTo(1.00));
		assertThat(MarketOrdersTab.significantIncrement(9.99), equalTo(10.00));
		assertThat(MarketOrdersTab.significantIncrement(99.99), equalTo(100.00));
		assertThat(MarketOrdersTab.significantIncrement(999.90), equalTo(1000.00));
		assertThat(MarketOrdersTab.significantIncrement(9999), equalTo(10000.00));
		assertThat(MarketOrdersTab.significantIncrement(99990), equalTo(100000.00));

		assertThat(MarketOrdersTab.significantIncrement(2), equalTo(2.01));
		assertThat(MarketOrdersTab.significantIncrement(20), equalTo(20.01));
		assertThat(MarketOrdersTab.significantIncrement(200), equalTo(200.10));
		assertThat(MarketOrdersTab.significantIncrement(2000), equalTo(2001.00));
		assertThat(MarketOrdersTab.significantIncrement(20000), equalTo(20010.0));
	}

	@Test
	public void testsignificantDecrement() {
		assertThat(MarketOrdersTab.significantDecrement(0.00), equalTo(0.00));
		assertThat(MarketOrdersTab.significantDecrement(0.01), equalTo(0.01));
		assertThat(MarketOrdersTab.significantDecrement(0.1), equalTo(0.09));
		assertThat(MarketOrdersTab.significantDecrement(1), equalTo(0.99));
		assertThat(MarketOrdersTab.significantDecrement(10), equalTo(9.99));
		assertThat(MarketOrdersTab.significantDecrement(100), equalTo(99.99));
		assertThat(MarketOrdersTab.significantDecrement(1000), equalTo(999.90));
		assertThat(MarketOrdersTab.significantDecrement(10000), equalTo(9999.00));

		assertThat(MarketOrdersTab.significantDecrement(1001), equalTo(1000.00));
		assertThat(MarketOrdersTab.significantDecrement(10010), equalTo(10000.00));

		assertThat(MarketOrdersTab.significantDecrement(0.99), equalTo(0.98));
		assertThat(MarketOrdersTab.significantDecrement(9.99), equalTo(9.98));
		assertThat(MarketOrdersTab.significantDecrement(99.99), equalTo(99.98));
		assertThat(MarketOrdersTab.significantDecrement(999.90), equalTo(999.80));
		assertThat(MarketOrdersTab.significantDecrement(9999), equalTo(9998.0));
		assertThat(MarketOrdersTab.significantDecrement(99990), equalTo(99980.0));

		assertThat(MarketOrdersTab.significantDecrement(2), equalTo(1.99));
		assertThat(MarketOrdersTab.significantDecrement(20), equalTo(19.99));
		assertThat(MarketOrdersTab.significantDecrement(200), equalTo(199.90));
		assertThat(MarketOrdersTab.significantDecrement(2000), equalTo(1999.00));
		assertThat(MarketOrdersTab.significantDecrement(20000), equalTo(19990.0));

	}

}
