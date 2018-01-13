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
package net.nikr.eve.jeveasset.lib;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import net.nikr.eve.jeveasset.Main;
import net.nikr.eve.jeveasset.TestUtil;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class LibTest extends TestUtil {

	@Test
	public void testLibs() {
		File folder = new File("target" + File.separator + "lib");
		assertTrue(folder.exists());
		File[] listOfFiles = folder.listFiles();

		Set<String> files = new HashSet<String>();
		for (File file : listOfFiles) {
			files.add(file.getName());
		}

		Set<String> libs = Main.getLibFiles();
		for (String file : files) {
			if (file.endsWith(".md5")) {
				continue;
			}
			assertTrue("Libs missing: " + file, libs.contains(file));
		}
		for (String lib : libs) {
			assertTrue("Libs dosn't exist: " + lib, files.contains(lib));
		}
	}
}
