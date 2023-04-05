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
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.nikr.eve.jeveasset.SplashUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public enum Sounds {

	ARMOR("armor.wav"),
	CAPACITOR("capacitor.wav"),
	CARGO("cargo.wav"),
	CHARACTER_SELECT("character_select.wav"),
	LOGIN("login.wav"),
	NOTIFICATION_PING("notification_ping.wav"),
	SHIELD("shield.wav"),
	SKILL("skill.wav"),
	START("start.wav"),
	STRUCTURE("structure.wav");

	private static final Logger LOG = LoggerFactory.getLogger(Sounds.class);

	private final String filename;
	private Clip clip = null;

	private Sounds(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	private boolean load() {
		if (clip == null) {
			clip = getClip(filename);
		}
		return (clip != null);
	}

	public Clip getClip() {
		load();
		return clip;
	}

	public static boolean preload() {
		int count = 0;
		boolean ok = true;
		for (Sounds i : Sounds.values()) {
			if (!i.load()) {
				ok = false;
			}
			count++;
			SplashUpdater.setSubProgress((int) (count * 100.0 / Sounds.values().length));
		}
		SplashUpdater.setSubProgress(100);
		return ok;
	}

	public static Clip getClip(String filename) {
		AudioInputStream inputStream = null;
		try {
			inputStream = AudioSystem.getAudioInputStream(Sounds.class.getResourceAsStream(filename));
			AudioFormat format = inputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip c = (Clip) AudioSystem.getLine(info);
			c.open(inputStream);
			return c;
		} catch (UnsupportedAudioFileException ex) {
			LOG.error(ex.getMessage(), ex);
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		} catch (LineUnavailableException ex) {
			LOG.error(ex.getMessage(), ex);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		return null;
	}
}
