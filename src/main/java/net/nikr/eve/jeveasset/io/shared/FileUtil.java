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
package net.nikr.eve.jeveasset.io.shared;

import java.io.File;
import java.net.URL;


public class FileUtil {
	private static final String PATH_DATA_VERSION = "data" + File.separator + "data.dat";
	private static final String PATH_JAR = "jeveassets.jar";

	public static boolean onMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	}

	public static String getPathDataVersion() {
		return FileUtil.getLocalFile(PATH_DATA_VERSION, false);
	}

	public static String getPathRunJar() {
		return FileUtil.getLocalFile(PATH_JAR, false);
	}

	public static String getPathLib(String filename) {
		return FileUtil.getLocalFile("lib" + File.separator + filename, false);
	}

	public static String getPathRunUpdate() {
		File userDir = new File(System.getProperty("user.home", "."));
		File file = new File(userDir.getAbsolutePath() + File.separator + ".jupdate" + File.separator + "jupdate.jar");
		File parentDir = file.getParentFile();
		if (!parentDir.exists() && !parentDir.mkdirs()) {
			throw new RuntimeException("Failed to create .jUpdate directory");
		}
		return file.getAbsolutePath();
	}

	/**
	 *
	 * @param filename the name of the data file to obtain
	 * @param dynamic true if the file is expecting to be written to, false for
	 * things like the items and locations.
	 * @return
	 */
	public static String getLocalFile(final String filename, final boolean dynamic) {
		File file;
		File ret;
		if (dynamic) {
			File userDir = new File(System.getProperty("user.home", "."));
			if (onMac()) { // preferences are stored in user.home/Library/Preferences
				file = new File(userDir, "Library" + File.separator + "Preferences" + File.separator + "JEveAssets");
			} else {
				file = new File(userDir.getAbsolutePath() + File.separator + ".jeveassets");
			}
			ret = new File(file.getAbsolutePath() + File.separator + filename);
			File parent = ret.getParentFile();
			if (!parent.exists()
					&& !parent.mkdirs()) {
				throw new RuntimeException("failed to create directories for " + parent.getAbsolutePath());
			}
		} else {
			URL location = net.nikr.eve.jeveasset.Program.class.getProtectionDomain().getCodeSource().getLocation();
			try {
				file = new File(location.toURI());
			} catch (Exception ex) {
				file = new File(location.getPath());
			}
			ret = new File(file.getParentFile().getAbsolutePath() + File.separator + filename);
		}
		return ret.getAbsolutePath();
	}
}
