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

import java.io.IOException;
import java.io.InputStream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SoundThread extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(SoundThread.class);

	private final Sound sound;
	private Player player = null;

	public SoundThread(Sound sound) {
		this.sound = sound;
	}

	@Override
	public void run() {
		try {
			InputStream is = sound.createInputStream();
			if (is == null) {
				return;
			}
			player = new Player(is);
			player.play();
		} catch (JavaLayerException ex) {
			LOG.error(ex.getMessage(), ex);
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		} finally {
			player.close();
		}
	}

	public void stopPlayback() {
		if (player != null) {
			player.close();
		}
	}
}
