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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.LibraryManager;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class LibTest extends TestUtil {

	@Test
	public void test() {
		testLibs();
		testPurge();
	}

	public void testLibs() {
		File folder = new File("target" + File.separator + "lib");
		assertTrue(folder.exists());
		File[] listOfFiles = folder.listFiles();

		Set<String> files = new HashSet<>();
		for (File file : listOfFiles) {
			files.add(file.getName());
		}

		Set<String> libs = LibraryManager.getLibFiles();
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

	public void testPurge() {
		List<File> files = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			File file = new File(FileUtil.getPathLib(i + ".jar"));
			files.add(file);
			try {
				assertTrue("Failed to create file: " + file.getName(), file.createNewFile());
			} catch (IOException ex) {
				Assert.fail("Failed to create file: " + file.getName() + " (" + ex.getMessage() + ")");
			}
		}
		for (File file : files) {
			assertTrue("File doesn't exist: " + file.getName(), file.exists());
		}
		LibraryManager.checkLibraries();
		for (File file : files) {
			assertFalse("File still exist (after purge): " + file.getName(), file.exists());
		}
		
	}
}
