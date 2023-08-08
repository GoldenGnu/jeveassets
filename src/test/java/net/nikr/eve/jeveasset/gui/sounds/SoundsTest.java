/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.sounds;

import java.io.File;
import java.net.URISyntaxException;
import net.nikr.eve.jeveasset.TestUtil;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;


public class SoundsTest extends TestUtil {

	@Test
	public void testExists() {
		for (DefaultSound i : DefaultSound.values()) {
			try {
				File file = new File(DefaultSound.class.getResource(i.getFilename()).toURI());
				assertTrue(i.getFilename() + " not found", file.exists());
			} catch (URISyntaxException ex) {
				fail(ex.getMessage());
			}
		}
	}

	@Test
	public void testUnused() {
		try {
			File dir = new File(DefaultSound.class.getResource("DefaultSound.class").toURI()).getParentFile();
			for (String filename : dir.list()) {
				if (filename.endsWith(".class")) {
					continue;
				}
				boolean ok = false;
				//Images Class
				for (DefaultSound i : DefaultSound.values()) {
					if (filename.equals(i.getFilename())) {
						ok = true;
						break;
					}
				}
				assertTrue(filename + " is not used anywhere", ok);
			}
		} catch (URISyntaxException ex) {
			fail("Directory not found");
		}
	}

	@Test
	public void testFilenames() {
		for (DefaultSound i : DefaultSound.values()) {
			String properFilename = i.name().toLowerCase() + ".mp3";
			if (!i.getFilename().isEmpty() && !properFilename.equals(i.getFilename())) {
				fail(i.toString() + " filename should be " + properFilename + " but is " + i.getFilename());
			}
		}
	}

}