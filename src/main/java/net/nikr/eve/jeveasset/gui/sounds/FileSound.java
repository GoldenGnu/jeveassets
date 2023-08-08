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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


public class FileSound implements Sound {

	private final File file;
	private SoundThread thread = null;

	public FileSound(File file) {
		this.file = file;
	}

	@Override
	public String getID() {
		return file.getName();
	}

	@Override
	public boolean exist() {
		return file.exists();
	}

	@Override
	public InputStream createInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public void play() {
		thread = new SoundThread(this);
		thread.start();
	}

	@Override
	public void stop() {
		if (thread != null) {
			thread.stopPlayback();
			thread = null;
		}
	}

	@Override
	public String toString() {
		return file.getName();
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + Objects.hashCode(this.file);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FileSound other = (FileSound) obj;
		return Objects.equals(this.file, other.file);
	}
}
