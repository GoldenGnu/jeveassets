/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.shared;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.ProfileManager;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.local.ProfileReader;
import net.nikr.eve.jeveasset.io.local.ProfileWriter;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.SettingsWriter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;


public class FileLockTest {
	@BeforeClass
	public static void setUpClass() throws Exception {
		Logger.getRootLogger().setLevel(Level.OFF);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		Logger.getRootLogger().setLevel(Level.INFO);
		//Cleanup
		File settings = new File(FileLockSettings.getPathSettingsStatic());
		File settingsBackup = new File(FileLockSettings.getPathSettingsBackup());
		File profile = new File(FileLockSettings.getPathProfile());
		File profileBackup = new File(FileLockSettings.getPathProfileBackup());
		File timeout = new File(FileLockSettings.getPathTimeout());
		settings.delete();
		settingsBackup.delete();
		profile.delete();
		profileBackup.delete();
		timeout.delete();
	}

	private static class LoadSettings implements Runnable {

		private Settings settings;

		public LoadSettings(Settings settings) {
			this.settings = settings;
		}
		
		@Override
		public void run() {
			boolean ok = SettingsReader.load(settings);
			assertTrue("LockTest: Settings load failed", ok);
		}
	}

	private static class SaveSettings implements Runnable {

		private Settings settings;

		public SaveSettings(Settings settings) {
			this.settings = settings;
		}
		
		@Override
		public void run() {
			boolean ok = SettingsWriter.save(settings);
			assertTrue("LockTest: Settings save failed", ok);
		}
	}

	//@Test
	public void timeoutTest() throws IOException {
		File file = new File(FileLockSettings.getPathTimeout());
		FileLock.lock(file);
		FileLock.lock(file);
	}

	@Test
	public void restoreBackupProfileTest() throws IOException {
		File file = new File (FileLockSettings.getPathProfile());
		ProfileManager profileManager = new ProfileManager();
		boolean saved;
		saved = ProfileWriter.save(profileManager, FileLockSettings.getPathProfile());
		assertTrue("LockTest: Backup - Save profile failed (1 of 2)", saved);
		saved = ProfileWriter.save(profileManager, FileLockSettings.getPathProfile());
		assertTrue("LockTest: Backup - Save profile failed (2 of 2)", saved);
		boolean deleted = file.delete();
		assertTrue("LockTest: Backup - Delete profile failed", deleted);
		boolean created = file.createNewFile();
		assertTrue("LockTest: Backup - Create profile failed", created);
		System.out.println("\"Premature end of file\": Is an expected error:");
		boolean loaded = ProfileReader.load(profileManager, FileLockSettings.getPathProfile());
		assertTrue("LockTest: Backup - Load profile failed", loaded);
	}

	@Test
	public void restoreBackupSettingsTest() throws IOException {
		Settings settings = new FileLockSettings();
		File file = new File (settings.getPathSettings());
		boolean saved;
		saved = SettingsWriter.save(settings);
		assertTrue("LockTest: Backup - Save settings failed (1 of 2)", saved);
		saved = SettingsWriter.save(settings);
		assertTrue("LockTest: Backup - Save settings failed (2 of 2)", saved);
		boolean deleted = file.delete();
		assertTrue("LockTest: Backup - Delete settings failed", deleted);
		boolean created = file.createNewFile();
		assertTrue("LockTest: Backup - Create settings failed", created);
		System.out.println("\"Premature end of file\": Is an expected error:");
		boolean loaded = SettingsReader.load(settings);
		assertTrue("LockTest: Backup - Load settings failed", loaded);
	}

	@Test
	public void unlockAllTest() throws IOException {
		File profile = new File(Settings.getPathProfilesDirectory() + File.separator + "some_test_profile.xml");
		File conquerableStations = new File(Settings.getPathConquerableStations());
		boolean emptyConquerableStations = false;
		try {
			List<File> files = new ArrayList<File>();
			//Static data directory
			File items = new File(Settings.getPathItems());
			files.add(items);
			FileLock.lock(items);
			File flags = new File(Settings.getPathFlags());
			files.add(flags);
			FileLock.lock(flags);
			File jumps = new File(Settings.getPathJumps());
			files.add(jumps);
			FileLock.lock(jumps);
			File locations = new File(Settings.getPathLocations());
			files.add(locations);
			FileLock.lock(locations);
			//Profile directory
			files.add(profile);
			profile.getParentFile().mkdirs();
			profile.createNewFile();
			FileLock.lock(profile);
			//Data directory
			files.add(conquerableStations);
			if (!conquerableStations.exists()) {
				conquerableStations.createNewFile();
				emptyConquerableStations = true;
			}
			FileLock.lock(conquerableStations);
			FileLock.unlockAll();
			for (File file : files) {
				if (FileLock.isLocked(file)) {
					fail(file.getName() + " was not unlocked by unlockAll");
				}
			}
		} finally {
			profile.delete();
			if (emptyConquerableStations) {
				conquerableStations.delete();
			}
		}
	}

	@Test
	public void settingsLockTest() throws InterruptedException {
		//Setup
		Settings settings = new FileLockSettings();
		SettingsWriter.save(settings);
		//Load
		Thread load1 = new Thread(new LoadSettings(settings));
		Thread load2 = new Thread(new LoadSettings(settings));
		load1.start();
		load2.start();
		try {
			load2.join();
			load1.join();
		} catch (InterruptedException ex) {
			fail("Thread Interrupted");
		}
		//Save
		Thread save1 = new Thread(new SaveSettings(settings));
		Thread save2 = new Thread(new SaveSettings(settings));
		save1.start();
		save2.start();
		try {
			save1.join();
			save2.join();
		} catch (InterruptedException ex) {
			fail("Thread Interrupted");
		}
		//Save & Load
		Thread load = new Thread(new LoadSettings(settings));
		Thread save = new Thread(new SaveSettings(settings));
		load.start();
		save.start();
		try {
			//wait
			load.join();
			save.join();
		} catch (InterruptedException ex) {
			fail("Thread Interrupted");
		}
		//Chaos! :D
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 8; i++) {
			threads.add(new Thread(new SaveSettings(settings)));
		}
		for (int i = 0; i < 8; i++) {
			threads.add(new Thread(new LoadSettings(settings)));
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
	}

	private static class LoadProfile implements Runnable {
	
		@Override
		public void run() {
			boolean ok = ProfileReader.load(new ProfileManager(), FileLockSettings.getPathProfile());
			assertTrue("LockTest: Profile load failed", ok);
		}
	}

	private static class SaveProfile implements Runnable {
	
		@Override
		public void run() {
			boolean ok = ProfileWriter.save(new ProfileManager(), FileLockSettings.getPathProfile());
			assertTrue("LockTest: Profile save failed", ok);
		}
	}

	@Test
	public void profileLockTest() throws InterruptedException {
		//Setup
		boolean ok = ProfileWriter.save(new ProfileManager(), FileLockSettings.getPathProfile());
		assertTrue("LockTest: Setup failed", ok);
		//Chaos! :D
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 8; i++) {
			threads.add(new Thread(new LoadProfile()));
		}
		for (int i = 0; i < 8; i++) {
			threads.add(new Thread(new SaveProfile()));
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
	}
	
}
