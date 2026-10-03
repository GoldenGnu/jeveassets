/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.profile;

import ch.qos.logback.classic.Level;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.Profile.ProfileType;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.nikr.eve.jeveasset.io.local.profile.ProfileDatabase.Table;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ProfileRefreshPersistenceTest extends TestUtil {

	private static int testID;

	@BeforeClass
	public static void setUpClass() {
		setLoggingLevel(Level.OFF);
	}

	@AfterClass
	public static void tearDownClass() {
		setLoggingLevel(Level.INFO);
	}

	@Test
	public void sqlitePreflightRollsBackAndCheckedSavePersistsRotation() {
		String oldRefresh = secret("sqlite-old");
		String newRefresh = secret("sqlite-new");
		Profile profile = sqliteProfile("Refresh Persistence");
		try {
			EsiOwner owner = owner("account", "Alice", oldRefresh, true);
			profile.getEsiOwners().add(owner);
			assertTrue(profile.saveChecked());

			owner.setAuth(EsiCallbackURL.LOCALHOST, newRefresh, null);
			owner.setInvalid(false);
			assertTrue(profile.preflightTable(Table.OWNERS));

			Profile preflightReload = sqliteCopy(profile);
			assertTrue(preflightReload.load());
			assertEquals(1, preflightReload.getEsiOwners().size());
			assertTrue("preflight changed the stored refresh token", oldRefresh.equals(preflightReload.getEsiOwners().get(0).getRefreshToken()));
			assertTrue("preflight changed the stored invalid flag", preflightReload.getEsiOwners().get(0).isInvalid());

			assertTrue(profile.saveTableChecked(Table.OWNERS));
			Profile savedReload = sqliteCopy(profile);
			assertTrue(savedReload.load());
			assertEquals(1, savedReload.getEsiOwners().size());
			assertTrue("rotated refresh token was not stored", newRefresh.equals(savedReload.getEsiOwners().get(0).getRefreshToken()));
			assertFalse("valid state was not stored", savedReload.getEsiOwners().get(0).isInvalid());
		} finally {
			cleanup(profile);
		}
	}

	@Test
	public void sqlitePreflightRejectsOwnersSkippedDuringLoad() throws SQLException {
		String refresh = secret("unknown-callback");
		Profile profile = sqliteProfile("Unknown Callback");
		try {
			profile.getEsiOwners().add(owner("account", "Alice", refresh, false));
			assertTrue(profile.saveChecked());
			try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + profile.getSQLiteFilename());
					PreparedStatement statement = connection.prepareStatement("UPDATE owners SET callbackurl = ?")) {
				statement.setString(1, "FUTURE_CALLBACK");
				assertEquals(1, statement.executeUpdate());
			}

			Profile loaded = sqliteCopy(profile);
			assertTrue(loaded.load());
			assertTrue(loaded.getEsiOwners().isEmpty());
			assertFalse(loaded.preflightTable(Table.OWNERS));

			try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + profile.getSQLiteFilename());
					PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM owners");
					ResultSet result = statement.executeQuery()) {
				assertTrue(result.next());
				assertEquals(1, result.getInt(1));
			}
		} finally {
			cleanup(profile);
		}
	}

	@Test
	public void legacyXmlMigrationIsCheckedAndKeepsRotatedToken() throws IOException {
		String oldRefresh = secret("legacy-old");
		String newRefresh = secret("legacy-new");
		Profile profile = xmlProfile("Legacy Refresh");
		try {
			String xml = "<assets><esiowners><esiowner accountname=\"Account\" refreshtoken=\"" + oldRefresh
					+ "\" scopes=\"\" structuresnextupdate=\"0\" accountnextupdate=\"0\" callbackurl=\"LOCALHOST\""
					+ " name=\"Alice\" id=\"1\" invalid=\"true\"/></esiowners></assets>";
			write(profile.getXmlFilename(), xml);
			write(profile.getBackupXmlFilename(), "stale backup");
			assertTrue(profile.load());
			assertEquals(1, profile.getEsiOwners().size());
			assertTrue(profile.preflightTable(Table.OWNERS));
			assertFalse(new File(profile.getXmlFilename()).exists());
			assertTrue(new File(profile.getBackupXmlFilename()).exists());
			assertEquals(xml, read(profile.getBackupXmlFilename()));
			assertTrue(new File(profile.getSQLiteFilename()).exists());

			profile.getEsiOwners().get(0).setAuth(EsiCallbackURL.LOCALHOST, newRefresh, null);
			profile.getEsiOwners().get(0).setInvalid(false);
			assertTrue(profile.saveTableChecked(Table.OWNERS));

			Profile reloaded = sqliteCopy(profile);
			assertTrue(reloaded.load());
			assertEquals(1, reloaded.getEsiOwners().size());
			assertTrue("legacy rotation was not stored", newRefresh.equals(reloaded.getEsiOwners().get(0).getRefreshToken()));
			assertFalse("legacy invalid flag was not cleared", reloaded.getEsiOwners().get(0).isInvalid());
		} finally {
			cleanup(profile);
		}
	}

	private static Profile sqliteProfile(String name) {
		testID++;
		return new Profile(name + " " + testID, false, false, ProfileType.SQLITE);
	}

	private static Profile xmlProfile(String name) {
		testID++;
		return new Profile(name + " " + testID, false, false, ProfileType.XML);
	}

	private static Profile sqliteCopy(Profile profile) {
		return new Profile(profile.getName(), false, false, ProfileType.SQLITE);
	}

	private static EsiOwner owner(String accountID, String character, String refreshToken, boolean invalid) {
		EsiOwner owner = new EsiOwner(accountID);
		owner.setOwnerName(character);
		owner.setAuth(EsiCallbackURL.LOCALHOST, refreshToken, null);
		owner.setInvalid(invalid);
		return owner;
	}

	private static String secret(String label) {
		return "test-private-" + label + "-" + testID;
	}

	private static void write(String filename, String value) throws IOException {
		Path path = Paths.get(filename);
		Files.createDirectories(path.getParent());
		Files.write(path, value.getBytes(StandardCharsets.UTF_8));
	}

	private static String read(String filename) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
	}

	private static void cleanup(Profile profile) {
		profile.getStockpileIDs().removeTable();
		delete(getProgramBackupFilename(profile.getXmlFilename()));
		delete(profile.getXmlFilename());
		delete(profile.getBackupXmlFilename());
		delete(profile.getSQLiteFilename());
		delete(profile.getBackupSQLiteFilename());
	}

	private static String getProgramBackupFilename(String filename) {
		return filename.substring(0, filename.lastIndexOf('.'))
				+ "_" + Program.PROGRAM_VERSION.replace(" ", "_") + "_backup.zip";
	}

	private static void delete(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			assertTrue("test file cleanup failed", file.delete());
		}
	}
}
