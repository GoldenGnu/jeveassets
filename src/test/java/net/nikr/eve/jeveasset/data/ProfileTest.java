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

package net.nikr.eve.jeveasset.data;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import net.nikr.eve.jeveasset.io.local.ProfileReader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;


public class ProfileTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Logger.getRootLogger().setLevel(Level.OFF);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	private String getFilename(String name) throws URISyntaxException {
		URL resource = ProfileTest.class.getResource("/" + name + "/assets.xml");
		if (resource == null) {
			resource = ProfileTest.class.getResource("/" + name + "/#Default.xml");
		}
		return new File(resource.toURI()).getAbsolutePath();
	}

	private void test(String name) throws URISyntaxException {
		test(name, false);
	}

	private void test(String name, boolean supportContracts) throws URISyntaxException {
		test(name, supportContracts, false, false);
	}

	private void test(String name, boolean supportContracts, boolean supportTransactions) throws URISyntaxException {
		test(name, supportContracts, supportTransactions, false);
	}

	private void test(String name, boolean supportContracts, boolean supportTransactions, boolean supportJournal) throws URISyntaxException {
		ProfileManager profileManager = new ProfileManager();
		boolean load = ProfileReader.load(profileManager, getFilename(name));
		assertEquals(name+" fail to load", load, true);
		assertEquals(name+" had no accounts", profileManager.getAccounts().isEmpty(), false);
		boolean marketOrders = false;
		boolean industryJobs = false;
		boolean contracts = false;
		boolean transactions = false;
		boolean journal = false;
		for (Account account : profileManager.getAccounts()) {
			if (!account.getName().equals("") && !account.getName().equals("-1")) {
				fail(name+" Name is not safe expected:<[]> but was:<[" + account.getName() + "]>");
			}
			assertEquals(name+" KeyID is not safe", -1, account.getKeyID());
			assertEquals(name+" VCode is not safe", "", account.getVCode());
			assertEquals(name+" had no owners", account.getOwners().isEmpty(), false);
			for (Owner owner : account.getOwners()) {
				marketOrders = marketOrders || !owner.getMarketOrders().isEmpty();
				industryJobs = industryJobs || !owner.getIndustryJobs().isEmpty();
				transactions = transactions || !owner.getTransactions().isEmpty();
				contracts = contracts || !owner.getContracts().isEmpty();
				journal = journal || !owner.getJournal().isEmpty();
				assertEquals(name+" had no assets", true, !owner.getAssets().isEmpty());
				assertEquals(name+" had no account balances", true, !owner.getAccountBalances().isEmpty());
			}
		}
		assertEquals(name+" had no market orders", true, marketOrders);
		assertEquals(name+" had no industry jobs", true, industryJobs);
		if (supportContracts) {
			assertEquals(name+" had no contracts", true, contracts);
		}
		if (supportTransactions) {
			assertEquals(name+" had no transactions", true, transactions);
		}
		if (supportJournal) {
			assertEquals(name+" had no journal", true, journal);
		}
	}

	@Test
	public void backwardCompatibility() throws URISyntaxException {
		test("data-1-1-0");
		test("data-1-2-0");
		test("data-1-2-1");
		test("data-1-2-2");
		test("data-1-2-3");
		test("data-1-3-0");
		test("data-1-4-0");
		test("data-1-4-1");
		test("data-1-5-0");
		test("data-1-6-0");
		test("data-1-6-1");
		test("data-1-6-2");
		test("data-1-6-3");
		test("data-1-6-4");
		test("data-1-7-0");
		test("data-1-7-1");
		test("data-1-7-2");
		test("data-1-7-3");
		test("data-1-8-0");
		test("data-1-8-1");
		test("data-1-9-0");
		test("data-1-9-1");
		test("data-1-9-2");
		test("data-2-0-0");
		test("data-2-1-0");
		test("data-2-1-1");
		test("data-2-1-2");
		test("data-2-2-0");
		test("data-2-3-0");
		test("data-2-4-0");
		test("data-2-5-0", true);
		test("data-2-6-0", true, true);
		test("data-2-7-0", true, true, true);
	}
}
