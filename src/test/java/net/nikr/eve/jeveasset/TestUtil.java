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
package net.nikr.eve.jeveasset;

import ch.qos.logback.classic.Level;
import java.io.File;
import net.nikr.eve.jeveasset.data.profile.Profile;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;


public class TestUtil {

	@BeforeClass
	public static void initLog() {
		Main.setLogLocation(true);
		CliOptions.get().setPortable(true);
		System.setProperty("http.agent", Program.PROGRAM_USER_AGENT);
		Settings.setTestMode(true);
	}

	@AfterClass
	public static void cleanup() {
		Profile profile = new Profile();
		deleteProfileFilename(profile.getSQLiteFilename());
		deleteProfileFilename(profile.getBackupSQLiteFilename());
		deleteProfileFilename(FileUtil.getPathAssetAdded());
		deleteProfileFilename(FileUtil.getPathStockpileIDsDatabase());
		deleteProfileFilename(FileUtil.getPathTrackerData());
	}

	protected static void setLoggingLevel(Level level) {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(level);
	}

	private static void deleteProfileFilename(String filename) {
		File file = new File(filename);
		if (file.exists() && !file.delete()) {
			throw new RuntimeException("Failed to delete:" + filename);
		}
	}
}
