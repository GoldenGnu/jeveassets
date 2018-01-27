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
package net.nikr.eve.jeveasset.data.raw;

import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderRange;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderState;
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;
import org.junit.Test;


public class RawMarketOrderTest extends TestUtil {

	@Test
	public void rawMarketOrderTest() {
		RawUtil.compare(RawMarketOrder.class, CharacterOrdersResponse.class);
		RawUtil.compare(RawMarketOrder.class, CharacterOrdersHistoryResponse.class);
		RawUtil.compare(RawMarketOrder.class, CorporationOrdersResponse.class);
		RawUtil.compare(RawMarketOrder.class, CorporationOrdersHistoryResponse.class);
		RawUtil.compare(MarketOrderRange.values(), CharacterOrdersResponse.RangeEnum.values(),
				CharacterOrdersHistoryResponse.RangeEnum.values(),
				CorporationOrdersResponse.RangeEnum.values(),
				CorporationOrdersHistoryResponse.RangeEnum.values());
		RawUtil.compare(MarketOrderState.values(), CharacterOrdersResponse.StateEnum.values(),
				CharacterOrdersHistoryResponse.StateEnum.values(),
				CorporationOrdersResponse.StateEnum.values(),
				CorporationOrdersHistoryResponse.StateEnum.values());
	}
}
