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

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.settings.SoundsSettingsPanel.SoundOption;


public class SoundPlayer {

	private static ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
	private static boolean play = false;

	private SoundPlayer() { }

	public static void load() {
		SoundPlayer.play = true;
	}

	public static void play(SoundOption option) {
		play(Settings.get().getSoundSettings().get(option));
	}

	public static void play(Sound sound) {
		if (!play) {
			return;
		}
		if (sound != null) {
			sound.play();
		}
	}

	public synchronized static void playAt(Date date, SoundOption option) {
		if (date == null || date.before(Settings.getNow())) {
			return; //In the past
		}
		long diff = Math.abs(date.getTime() - Settings.getNow().getTime());
		long ms = TimeUnit.MILLISECONDS.convert(diff, TimeUnit.MILLISECONDS);
		EXECUTOR.schedule(new Runnable(){
			@Override
			public void run() {
				play(option);
			}
		}, ms, TimeUnit.MILLISECONDS);
	}

	public synchronized static void cancelAll() {
		EXECUTOR.shutdownNow();
		EXECUTOR = Executors.newSingleThreadScheduledExecutor();
	}

	public static void stop(Sound sound) {
		if (sound != null) {
			sound.stop();
		}
	}

}
