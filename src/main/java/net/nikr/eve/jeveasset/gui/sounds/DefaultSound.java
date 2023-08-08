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

import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public enum DefaultSound implements Sound {
	NONE("") {
		@Override
		public void play() { }

		@Override
		public void stop() { }

		@Override
		public String getText() {
			return DialoguesSettings.get().soundsNone();
		}
	},
	ARMOR("armor.mp3") {
		@Override
		public String getText() {
			return DialoguesSettings.get().soundsEveArmor();
		}
	},
	CAPACITOR("capacitor.mp3") {
		@Override
		public String getText() {
			return DialoguesSettings.get().soundsEveCapacitor();
		}
	},
	CARGO("cargo.mp3") {
		@Override
		public String getText() {
			return DialoguesSettings.get().soundsEveCargo();
		}
	},
	CHARACTER_SELECT("character_select.mp3") {
		@Override
		public String getText() {
			return DialoguesSettings.get().soundsEveCharacterSelection();
		}
	},
	LOGIN("login.mp3") {
		@Override
		public String getText() {
			return DialoguesSettings.get().soundsEveLogin();
		}
	},
	NOTIFICATION_PING("notification_ping.mp3") {
		@Override
		public String getText() {
			return DialoguesSettings.get().soundsEveNotificationPing();
		}
	},
	SHIELD("shield.mp3") {
		@Override
		public String getText() {
			return DialoguesSettings.get().soundsEveShield();
		}
	},
	SKILL("skill.mp3") {
		@Override
		public String getText() {
			return DialoguesSettings.get().soundsEveSkill();
		}
	},
	START("start.mp3") {
		@Override
		public String getText() {
			return DialoguesSettings.get().soundsEveStart();
		}
	},
	STRUCTURE("structure.mp3") {
		@Override
		public String getText() {
			return DialoguesSettings.get().soundsEveStructure();
		}
	},
	BEEP("") {
		@Override
		public void play() {
			Toolkit.getDefaultToolkit().beep();
		}

		@Override
		public void stop() {
		}

		@Override
		public String getText() {
			return DialoguesSettings.get().soundsBeep();
		}
	},;
	

	public abstract String getText();

	private final String filename;
	private SoundThread thread = null;

	private DefaultSound(String filename) {
		this.filename = filename;
	}

	protected String getFilename() {
		return filename;
	}

	@Override
	public boolean exist() {
		return true;
	}

	@Override
	public String getID() {
		return name();
	}

	@Override
	public void play() {
		thread = new SoundThread(this);
		thread.start();
	}

	@Override
	public InputStream createInputStream() throws IOException {
		return DefaultSound.class.getResourceAsStream(filename);
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
		return getText();
	}
}
