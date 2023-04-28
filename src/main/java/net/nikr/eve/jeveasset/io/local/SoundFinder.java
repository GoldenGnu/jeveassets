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

package net.nikr.eve.jeveasset.io.local;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.gui.sounds.FileSound;
import net.nikr.eve.jeveasset.gui.sounds.Sound;
import net.nikr.eve.jeveasset.io.shared.FileUtil;


public final class SoundFinder {

	private SoundFinder() { }

	public static List<Sound> load() {
		SoundFinder reader = new SoundFinder();
		return reader.read();
	}

	private List<Sound> read() {
		List<Sound> sounds = new ArrayList<>();
		File soundsDirectory = new File(FileUtil.getPathSoundsDirectory());
		if (!soundsDirectory.exists()) {
			soundsDirectory.mkdirs();
		}
		FileFilter fileFilter = new Mp3FileFilter();

		File[] files = soundsDirectory.listFiles(fileFilter);
		if (files != null) {
			for (File file : files) {
				sounds.add(new FileSound(file));
			}
		}
		return sounds;
	}

	private class Mp3FileFilter implements FileFilter {
		@Override
		public boolean accept(final File file) {
			return !file.isDirectory() && file.getName().endsWith(".mp3");
		}
	}
}
