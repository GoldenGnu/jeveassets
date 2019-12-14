/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.io.online.Updater;
import net.nikr.eve.jeveasset.io.shared.FileUtil;


public class LibraryManager {

	private static Set<String> files = null;

	public static void checkLibraries() {
		checkMissing();
		purge();
	}

	private static void purge() {
		File lib = new File(FileUtil.getPathLib());
		List<File> delete = new ArrayList<>();
		int libsFiles = 0;
		for (File file : lib.listFiles()) {
			if (getLibFiles().contains(file.getName())) {
				libsFiles++;
				continue;
			}
			if (!getLibFiles().contains(file.getName()) && file.getName().endsWith(".jar") && !file.isDirectory()) {
				delete.add(file);
			}
		}
		if (libsFiles != getLibFiles().size()) {
			return; //Wrong directory?
		}
		for (File file : delete) {
			file.delete();
		}
	}

	private static void checkMissing() {
		File jar = new File(FileUtil.getPathRunJar());
		boolean temp = false;
		//Check if trying to run from inside zip file (Windows only)
		if (jar.getAbsolutePath().contains(".zip") && jar.getAbsolutePath().contains(System.getProperty("java.io.tmpdir")) && System.getProperty("os.name").startsWith("Windows")) {
			temp = true;
		}
		boolean missing = false;
		//Check if all libraries are pressent
		for (String filename : getLibFiles()) {
			File file = new File(FileUtil.getPathLib(filename));
			if (!file.exists()) {
				missing = true;
				break;
			}
		}
		if (temp && missing) { //Running from zip file...
			JOptionPane.showMessageDialog(null, "You need to unzip jEveAssets to run it\r\nIt will not work from inside the zip file", "Critical Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} else if (missing) { //Missing libraries
			Updater updater = new Updater();
			updater.fixLibs();
		}
	}

	public static synchronized Set<String> getLibFiles() {
		if (files == null) { //Lazy init
			files = new HashSet<>();
			files.add("asm-7.1.jar");
			files.add("dom4j-2.1.1.jar");
			files.add("glazedlists-1.11.0.jar");
			files.add("graph-1.5.0.jar");
			files.add("guava-r09.jar");
			files.add("jaxen-1.1.6.jar");
			files.add("javax.activation-1.2.0.jar");
			files.add("guava-r09.jar");
			files.add("LGoodDatePicker-10.2.3.jar");
			files.add("jfreechart-1.5.0.jar");
			files.add("osxadapter-1.1.0.jar");
			files.add("pricing-2.1.1.jar");
			files.add("routing-1.5.0.jar");
			files.add("slf4j-api-1.7.25.jar");
			files.add("log4j-over-slf4j-1.7.25.jar");
			files.add("jcl-over-slf4j-1.7.25.jar");
			files.add("jul-to-slf4j-1.7.25.jar");
			files.add("logback-core-1.2.3.jar");
			files.add("logback-classic-1.2.3.jar");
			files.add("super-csv-2.4.0.jar");
			files.add("translations-3.0.0.jar");
			files.add("swagger-annotations-1.5.22.jar");
			files.add("hamcrest-core-1.3.jar");
			files.add("eve-esi-3.3.0.jar");
			files.add("hamcrest-core-1.3.jar");
			files.add("jaxb-api-2.3.0.jar");
			files.add("sqlite-jdbc-3.23.1.jar");
			files.add("okhttp-3.14.0.jar");
			files.add("gson-fire-1.8.3.jar");
			files.add("gson-2.8.5.jar");
			files.add("okio-1.17.2.jar");
			files.add("logging-interceptor-3.14.0.jar");
			files.add("org.apache.oltu.oauth2.common-1.0.1.jar");
			files.add("org.apache.oltu.oauth2.client-1.0.1.jar");
			files.add("json-20140107.jar");
			files.add("commons-codec-1.9.jar");
			files.add("contracts-pricing-2.0.0.jar");
			files.add("commons-lang3-3.8.1.jar");
		}
		return files;
	}
}
