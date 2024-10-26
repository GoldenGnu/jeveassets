/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data.settings;

import java.util.Date;
import net.nikr.eve.jeveasset.TestUtil;

import static org.junit.Assert.*;

import org.junit.Test;


public class MarketPriceDataTest extends TestUtil {

	@Test
	public void testUpdate() {
		//Set
		MarketPriceData data = new MarketPriceData();
		data.update(75, 1, new Date(1));
		data.update(25, 1, new Date(2));
		data.update(50, 1, new Date(3));
		assertEquals(50, data.getAverage(), 0);
		assertEquals(75, data.getMaximum(), 0);
		assertEquals(25, data.getMinimum(), 0);
		assertEquals(50, data.getLatest(), 0);

		//Empty
		data = new MarketPriceData();
		assertEquals(0, data.getAverage(), 0);
		assertEquals(0, data.getMaximum(), 0);
		assertEquals(0, data.getMinimum(), 0);
		assertEquals(0, data.getLatest(), 0);

		//Set
		data = new MarketPriceData();
		data.update(10, 300 , new Date(1));
		data.update(20, 100, new Date(2));
		assertEquals(12.5, data.getAverage(), 0);
		assertEquals(20, data.getMaximum(), 0);
		assertEquals(10, data.getMinimum(), 0);
		assertEquals(20, data.getLatest(), 0);
	}
}
