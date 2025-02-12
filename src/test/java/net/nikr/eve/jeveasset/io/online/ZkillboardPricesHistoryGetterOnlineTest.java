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
package net.nikr.eve.jeveasset.io.online;

import java.util.Map;
import net.nikr.eve.jeveasset.TestUtil;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;


public class ZkillboardPricesHistoryGetterOnlineTest extends TestUtil {

	@Test
	public void testGetPrice() {
		Map<String, Double> priceHistory;

		priceHistory = ZkillboardPricesHistoryGetter.getPriceHistory(34);
		assertNotNull(priceHistory);

		priceHistory = ZkillboardPricesHistoryGetter.getPriceHistory(638);
		assertNotNull(priceHistory);

		try {
			Thread.sleep(1100);
		} catch (InterruptedException ex) {
			fail(ex.getMessage());
		}

		priceHistory = ZkillboardPricesHistoryGetter.getPriceHistory(35);
		assertNotNull(priceHistory);
	}
}
