/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
import java.net.URISyntaxException;
import net.nikr.eve.jeveasset.data.Settings;


public class FileLockSettings extends Settings {

	private static final String SETTINGS = "settings_test";
	private static final String PROFILE = "profile_test";
	private static final String TIMEOUT = "timeout";
	private static final String XML = ".xml";
	private static final String BAC = ".bac";
	

	public FileLockSettings() {
		super();
	}

	@Override
	public String getPathSettings() {
		return getPath(SETTINGS+XML); 
	}

	public static String getPathSettingsBackup() {
		return getPath(SETTINGS+BAC); 
	}

	
	public static String getPathSettingsStatic() {
		return getPath(SETTINGS+XML); 
	}

	public static String getPathProfile() {
		return getPath(PROFILE+XML); 
	}

	public static String getPathProfileBackup() {
		return getPath(PROFILE+BAC); 
	}

	public static String getPathTimeout() {
		return getPath(TIMEOUT); 
	}

	private static String getPath(String filename) {
		try {
			File file;
			File ret;
			file = new File(net.nikr.eve.jeveasset.Program.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			ret = new File(file.getAbsolutePath() + File.separator + filename);
			return ret.getAbsolutePath();
		} catch (URISyntaxException ex) {
			return null;
		}
	}
	
	
}
