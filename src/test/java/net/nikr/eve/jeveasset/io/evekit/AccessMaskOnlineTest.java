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
package net.nikr.eve.jeveasset.io.evekit;

import static org.junit.Assert.assertEquals;

import enterprises.orbital.evekit.client.ApiException;
import enterprises.orbital.evekit.client.api.AccessKeyApi;
import enterprises.orbital.evekit.client.model.MaskList;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import org.junit.Test;


public class AccessMaskOnlineTest extends TestUtil {

	@Test
	public void testAccessMask() throws ApiException {
		AccessKeyApi accessKeyApi = new AccessKeyApi();
		MaskList maskList = accessKeyApi.getMaskList();
		assertEquals(maskList.getACCESSACCOUNTBALANCE(), EveKitAccessMask.ACCOUNT_BALANCE.getAccessMask());
		assertEquals(maskList.getACCESSASSETS(), EveKitAccessMask.ASSET_LIST.getAccessMask());
		assertEquals(maskList.getACCESSBLUEPRINTS(), EveKitAccessMask.BLUEPRINTS.getAccessMask());
		assertEquals(maskList.getACCESSCONTRACTS(), EveKitAccessMask.CONTRACTS.getAccessMask());
		assertEquals(maskList.getACCESSINDUSTRYJOBS(), EveKitAccessMask.INDUSTRY_JOBS.getAccessMask());
		assertEquals(maskList.getACCESSLOCATIONS(), EveKitAccessMask.LOCATIONS.getAccessMask());
		assertEquals(maskList.getACCESSMARKETORDERS(), EveKitAccessMask.MARKET_ORDERS.getAccessMask());
		assertEquals(maskList.getACCESSWALLETJOURNAL(), EveKitAccessMask.JOURNAL.getAccessMask());
		assertEquals(maskList.getACCESSWALLETTRANSACTIONS(), EveKitAccessMask.TRANSACTIONS.getAccessMask());
	}
}
