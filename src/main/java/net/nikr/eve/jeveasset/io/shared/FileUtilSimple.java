/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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


public class FileUtilSimple {

	private static final String PATH_JAR = "jeveassets.jar";
	
	public static String getPathRunJar() {
		return getLocalFile(PATH_JAR);
	}

	public static String getPathLib() {
		return getPathLib("");
	}

	public static String getPathLib(String filename) {
		return getLocalFile("lib" + File.separator + filename);
	}

	/**
	 *
	 * @param filename the name of the data file to obtain
	 * @return
	 */
	public static String getLocalFile(final String filename) {
		File file;
		File ret;
		URL location = net.nikr.eve.jeveasset.Program.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			file = new File(location.toURI());
		} catch (Exception ex) {
			file = new File(location.getPath());
		}
		ret = new File(file.getParentFile().getAbsolutePath() + File.separator + filename);
		return ret.getAbsolutePath();
	}
}
