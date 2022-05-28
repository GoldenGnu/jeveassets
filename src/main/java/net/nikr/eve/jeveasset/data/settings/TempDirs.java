/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.settings;

import java.io.File;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public enum TempDirs {
	DEFAULT(System.getProperty("java.io.tmpdir")), //Default
	USER_HOME(System.getProperty("user.home")), //User Home Directory
	USER_DIR(System.getProperty("user.dir")), //jEveAssets Working Directory
	DATA_DIR(FileUtil.getPathDataDirectory()), //jEveAssets Data Directory
	PROGRAM_DIR(FileUtil.getLocalFile("", false)), //jEveAssets Program Directory
	;

	private static final Logger LOG = LoggerFactory.getLogger(TempDirs.class);
	private static boolean fixed = false;
	private final String dir;
	private final File file;

	private TempDirs(String dir) {
		this.dir = dir;
		this.file = new File(dir);
	}

	public boolean isValid() {
		return file.exists() && file.isDirectory() && file.canRead() && file.canWrite() && file.canExecute();
	}

	public String getDir() {
		return dir;
	}

	public void setTmpDir() {
		System.setProperty("java.io.tmpdir", dir);
	}

	public static void fixTempDir() {
		if (fixed) {
			return;
		}
		for (TempDirs tempDirs : TempDirs.values()) {
			if (tempDirs.isValid()) {
				tempDirs.setTmpDir();
				LOG.info("Using " + tempDirs.name() + " for java.io.tmpdir (" + tempDirs.getDir() + ")");
				fixed = true;
				break;
			}
		}
	}
}
