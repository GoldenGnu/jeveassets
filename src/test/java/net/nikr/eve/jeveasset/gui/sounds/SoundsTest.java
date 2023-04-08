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
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import net.nikr.eve.jeveasset.TestUtil;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;


public class SoundsTest extends TestUtil {

	@Test
	public void testExists() {
		for (Sounds i : Sounds.values()) {
			try {
				File file = new File(Sounds.class.getResource(i.getFilename()).toURI());
				assertTrue(i.getFilename() + " not found", file.exists());
			} catch (URISyntaxException ex) {
				fail(ex.getMessage());
			}
		}
	}

	@Test
	public void testPreload() {
		Sounds.preload();
	}

	@Test
	public void testUnused() {
		try {
			File dir = new File(Sounds.class.getResource("Sounds.class").toURI()).getParentFile();
			String[] children = dir.list();

			if (children != null) {
				for (String filename : children) {
					if (filename.equals("Sounds.class")
							|| filename.equals("SoundPlayer$1.class")
							|| filename.equals("SoundPlayer.class")
							) {
						continue;
					}
					boolean ok = false;
					//Images Class
					for (Sounds i : Sounds.values()) {
						if (filename.equals(i.getFilename())) {
							ok = true;
							break;
						}
					}
					//loading 01-08
					for (int a = 0; a < 8; a++) {
						if (filename.equals("loading0" + (a + 1) + ".png")) {
							ok = true;
							break;
						}
					}
					//working 01-24
					for (int a = 0; a < 24; a++) {
						String number;
						if ((a + 1) < 10) {
							number = "0" + (a + 1);
						} else {
							number = "" + (a + 1);
						}
						if (filename.equals("working" + number + ".png")) {
							ok = true;
							break;
						}
					}
					assertTrue(filename + " is not used anywhere", ok);
				}
			}
		} catch (URISyntaxException ex) {
			fail("Directory not found");
		}
	}

	@Test
	public void testFilenames() {
		for (Sounds i : Sounds.values()) {
			String properFilename = i.toString().toLowerCase() + ".wav";
			if (!properFilename.equals(i.getFilename())) {
				fail(i.toString() + " filename should be " + properFilename + " but is " + i.getFilename());
			}
		}
	}

}