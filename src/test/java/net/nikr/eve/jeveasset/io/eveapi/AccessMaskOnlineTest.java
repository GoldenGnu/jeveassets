/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.calllist.Call;
import com.beimin.eveapi.model.calllist.CallList;
import com.beimin.eveapi.model.shared.KeyType;
import com.beimin.eveapi.parser.calllist.CallListParser;
import com.beimin.eveapi.response.calllist.CallListResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccessMask;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class AccessMaskOnlineTest {
	@Test
	public void testAccessMask() throws ApiException {
		CallListParser callListParser = new CallListParser();
		CallListResponse response = callListParser.getResponse();
		CallList callList = response.get();
		Map<ID, Call> calls = new HashMap<ID, Call>();
		for (Call call : callList.getCalls()) {
			calls.put(new ID(call.getAccessMask(), call.getType()), call);
		}
		test(calls, EveApiAccessMask.ACCOUNT_BALANCE, KeyType.Character , "AccountBalance");
		test(calls, EveApiAccessMask.ACCOUNT_BALANCE, KeyType.Corporation , "AccountBalance");
		test(calls, EveApiAccessMask.ASSET_LIST, KeyType.Character , "AssetList");
		test(calls, EveApiAccessMask.ASSET_LIST, KeyType.Corporation , "AssetList");
		test(calls, EveApiAccessMask.INDUSTRY_JOBS, KeyType.Character , "IndustryJobs");
		test(calls, EveApiAccessMask.INDUSTRY_JOBS, KeyType.Corporation , "IndustryJobs");
		test(calls, EveApiAccessMask.MARKET_ORDERS, KeyType.Character , "MarketOrders");
		test(calls, EveApiAccessMask.MARKET_ORDERS, KeyType.Corporation , "MarketOrders");
		test(calls, EveApiAccessMask.TRANSACTIONS_CHAR, KeyType.Character , "WalletTransactions");
		test(calls, EveApiAccessMask.TRANSACTIONS_CORP, KeyType.Corporation , "WalletTransactions");
		test(calls, EveApiAccessMask.JOURNAL_CHAR, KeyType.Character , "WalletJournal");
		test(calls, EveApiAccessMask.JOURNAL_CORP, KeyType.Corporation , "WalletJournal");
		test(calls, EveApiAccessMask.CONTRACTS_CHAR, KeyType.Character , "Contracts");
		test(calls, EveApiAccessMask.CONTRACTS_CORP, KeyType.Corporation , "Contracts");
		test(calls, EveApiAccessMask.LOCATIONS_CHAR, KeyType.Character , "Locations");
		test(calls, EveApiAccessMask.LOCATIONS_CORP, KeyType.Corporation , "Locations");
	}

	private void test(Map<ID, Call> calls, EveApiAccessMask accessMask, KeyType keyType, String expected) {
		Call call = calls.get(new ID(accessMask.getAccessMask(), keyType));
		assertNotNull(call);
		assertEquals(expected, call.getName());
	}

	private static class ID {
		private final long accessMask;
		private final KeyType type;

		public ID(long accessMask, KeyType type) {
			this.accessMask = accessMask;
			this.type = type;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 29 * hash + (int) (this.accessMask ^ (this.accessMask >>> 32));
			hash = 29 * hash + Objects.hashCode(this.type);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ID other = (ID) obj;
			if (this.accessMask != other.accessMask) {
				return false;
			}
			if (this.type != other.type) {
				return false;
			}
			return true;
		}
	}
}
