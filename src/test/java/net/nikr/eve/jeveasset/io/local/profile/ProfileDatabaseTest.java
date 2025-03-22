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
package net.nikr.eve.jeveasset.io.local.profile;

import ch.qos.logback.classic.Level;
import net.nikr.eve.jeveasset.CliOptions;
import net.nikr.eve.jeveasset.TestUtil;
import static net.nikr.eve.jeveasset.TestUtil.initLog;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.settings.AddedData;
import net.nikr.eve.jeveasset.io.local.ProfileReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class ProfileDatabaseTest extends TestUtil {

	private static final int REPS = 100;
	private static ProfileManager profileManager;
	private static boolean portable;

	@BeforeClass
	public static void setUpClass() {
		setLoggingLevel(Level.WARN);
		portable = CliOptions.get().isPortable();
		CliOptions.get().setPortable(false);

		profileManager = new ProfileManager();
		profileManager.searchProfile();
		profileManager.loadActiveProfile(); //Load

		CliOptions.get().setPortable(true);
		profileManager.saveProfile(); //Save to SQLite
		AddedData.load();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		setLoggingLevel(Level.INFO);
		CliOptions.get().setPortable(portable);
		cleanupPortableProfile();
	}

	/**
	 * SQLite test
	 * @param args
	 * @throws java.lang.InterruptedException
	 */
	public static void main(final String[] args) throws InterruptedException {
		initLog();
		setUpClass();
		ProfileDatabaseTest test = new ProfileDatabaseTest();
		test.test();
		System.exit(0);
	}

	@Test
	public void test() {
		loadXml();
		saveSQL();
		loadSQL();
		updateData();
	}

	private void saveSQL() {
		//Save to SQLite * REPS
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPS; i++) {
			profileManager.saveProfile();
		}
		log("Save SQLite: " +  + ((System.currentTimeMillis() - start) / REPS) + "ms");
	}

	private void updateData() {
		//EventList update * REPS
		ProfileData profileData = new ProfileData(profileManager);
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPS; i++) {
			profileData.updateEventLists();
		}
		log("Update data " + ((System.currentTimeMillis() - start) / REPS) + "ms");
	}

	private void loadXml() {
		//Load XML * REPS
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPS; i++) {
			ProfileReader.load(profileManager.getActiveProfile());
		}
		log("Loading XML " + ((System.currentTimeMillis() - start) / REPS) + "ms");
	}

	private void loadSQL() {
		//Load SQlite * REPS
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPS; i++) {
			profileManager.clear();
			profileManager.loadActiveProfile(); //Load from SQLite
		}
		log("Loading SQLite " + ((System.currentTimeMillis() - start) / REPS) + "ms");
	}

	private void log(String s) {
		setLoggingLevel(Level.INFO);
		System.out.println(s);
		setLoggingLevel(Level.WARN);
	}

}
