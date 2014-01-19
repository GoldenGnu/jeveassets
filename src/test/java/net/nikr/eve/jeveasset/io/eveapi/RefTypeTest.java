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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.eve.reftypes.ApiRefType;
import com.beimin.eveapi.eve.reftypes.RefTypesResponse;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.wallet.RefType;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RefTypeTest {

	public RefTypeTest() { }

	@BeforeClass
	public static void setUpClass() { }

	@AfterClass
	public static void tearDownClass() { }

	@Before
	public void setUp() { }

	@After
	public void tearDown() { }

	/**
	 * Test of EVEAPI RefType enum (via the API - need to be online)
	 */
	@Test
	public void testEnum() {
		try {
			RefTypesResponse response = com.beimin.eveapi.eve.reftypes.RefTypesParser.getInstance().getResponse();
			for (ApiRefType apiRefType : response.getAll()) {
				RefType refType = RefType.forID(apiRefType.getRefTypeID());
				assertNotNull("RefType missing - ID: " + apiRefType.getRefTypeID() + " (" + apiRefType.getRefTypeName() + ")", refType);
				assertEquals("RefType ID: " + refType.getId() + " wrong name", apiRefType.getRefTypeName(), refType.getName());
			}
		} catch (ApiException ex) {
			fail("Fail to get RefTypes");
		}
	}
}
