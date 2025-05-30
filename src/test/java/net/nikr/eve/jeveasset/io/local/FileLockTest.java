/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ch.qos.logback.classic.Level;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.io.local.FileLock.SafeFileIO;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import org.junit.BeforeClass;
import org.junit.Test;


public class FileLockTest extends TestUtil {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setLoggingLevel(Level.OFF);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		setLoggingLevel(Level.INFO);
		//Cleanup
		File settings = new File(FileLockSettings.getPathSettingsStatic());
		File settingsBackup = new File(FileLockSettings.getPathSettingsBackup());
		File settingsVersionBackup = new File(FileLockSettings.getPathSettingsVersionBackup());
		File settingsError = new File(FileLockSettings.getPathSettingsError());
		File timeout = new File(FileLockSettings.getPathTimeout());
		settings.delete();
		settingsBackup.delete();
		settingsVersionBackup.delete();
		settingsError.delete();
		timeout.delete();
	}

	private static class LoadSettings extends Thread implements TestThread {

		private final FileLockSettings settings;

		public LoadSettings(FileLockSettings settings) {
			this.settings = settings;
		}

		@Override
		public void run() {
			SettingsReader.load(settings, settings.getPathSettings());
		}

		@Override
		public Boolean isOk() {
			return !settings.isSettingsLoadError();
		}
	}

	private static class SaveSettings extends Thread implements TestThread {

		private final FileLockSettings settings;
		private Boolean ok = null;

		public SaveSettings(FileLockSettings settings) {
			this.settings = settings;
		}

		@Override
		public void run() {
			ok = SettingsWriter.save(settings, settings.getPathSettings());
		}

		@Override
		public Boolean isOk() {
			return ok;
		}
	}

	//@Test
	public void timeoutTest() throws IOException {
		File file = new File(FileLockSettings.getPathTimeout());
		try (SafeFileIO aLock = new SafeFileIO(file); SafeFileIO  bLock =  new SafeFileIO(file);){
			aLock.getFileInputStream();
			bLock.getFileInputStream();
		}
	}

	@Test
	public void restoreBackupSettingsTest() throws IOException {
		FileLockSettings settings = new FileLockSettings();
		File file = new File (settings.getPathSettings());
		boolean saved;
		saved = SettingsWriter.save(settings, settings.getPathSettings());
		assertTrue("LockTest: Backup - Save settings failed (1 of 2)", saved);
		saved = SettingsWriter.save(settings, settings.getPathSettings());
		assertTrue("LockTest: Backup - Save settings failed (2 of 2)", saved);
		boolean deleted = file.delete();
		assertTrue("LockTest: Backup - Delete settings failed", deleted);
		boolean created = file.createNewFile();
		assertTrue("LockTest: Backup - Create settings failed", created);
		System.out.println("\"Premature end of file\": Is an expected error:");
		SettingsReader.load(settings, settings.getPathSettings());
		assertFalse("LockTest: Backup - Load settings failed", settings.isSettingsLoadError());
	}

	@Test
	public void unlockAllTest() throws IOException {
		List<File> files = new ArrayList<>();
		File items = new File(FileUtil.getPathItems());
		files.add(items);
		File flags = new File(FileUtil.getPathFlags());
		files.add(flags);
		File jumps = new File(FileUtil.getPathJumps());
		files.add(jumps);
		File locations = new File(FileUtil.getPathLocations());
		files.add(locations);
		File profile = new File(FileUtil.getPathProfilesDirectory() + File.separator + "some_test_profile.xml");
		profile.getParentFile().mkdirs();
		profile.createNewFile();
		files.add(profile);
		File conquerableStations = new File(FileUtil.getPathConquerableStations());
		files.add(conquerableStations);
		boolean emptyConquerableStations = false;
		if (!conquerableStations.exists()) {
			conquerableStations.createNewFile();
			emptyConquerableStations = true;
		}
		try (SafeFileIO itemsLock = new SafeFileIO(items);
				SafeFileIO  flagsLock =  new SafeFileIO(flags);
				SafeFileIO  jumpsLock =  new SafeFileIO(jumps);
				SafeFileIO  locationsLock =  new SafeFileIO(locations);
				SafeFileIO  profileLock =  new SafeFileIO(profile);
				SafeFileIO  conquerableStationsLock =  new SafeFileIO(conquerableStations);
				){
			itemsLock.getFileInputStream();
			flagsLock.getFileInputStream();
			jumpsLock.getFileInputStream();
			locationsLock.getFileInputStream();
			profileLock.getFileInputStream();
			conquerableStationsLock.getFileInputStream();
			//Data directory
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
		FileLockSettings settings = new FileLockSettings();
		boolean ok = SettingsWriter.save(settings, settings.getPathSettings());
		assertTrue("LockTest: Setup failed", ok);
		//Load
		TestThread load1 = new LoadSettings(settings);
		TestThread load2 = new LoadSettings(settings);
		load1.start();
		load2.start();
		try {
			load2.join();
			load1.join();
		} catch (InterruptedException ex) {
			fail("Thread Interrupted");
		}
		assertTrue(load1.isOk());
		assertTrue(load2.isOk());
		//Save
		TestThread save1 = new SaveSettings(settings);
		TestThread save2 = new SaveSettings(settings);
		save1.start();
		save2.start();
		try {
			save1.join();
			save2.join();
		} catch (InterruptedException ex) {
			fail("Thread Interrupted");
		}
		assertTrue(save1.isOk());
		assertTrue(save2.isOk());
		//Save & Load
		TestThread load = new LoadSettings(settings);
		TestThread save = new SaveSettings(settings);
		load.start();
		save.start();
		try {
			//wait
			load.join();
			save.join();
		} catch (InterruptedException ex) {
			fail("Thread Interrupted");
		}
		assertTrue(load.isOk());
		assertTrue(save.isOk());
		//Chaos! :D
		List<TestThread> threads = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			threads.add(new SaveSettings(settings));
		}
		for (int i = 0; i < 8; i++) {
			threads.add(new LoadSettings(settings));
		}
		for (TestThread thread : threads) {
			thread.start();
		}
		for (TestThread thread : threads) {
			thread.join();
		}
		for (TestThread thread : threads) {
			assertTrue(thread.isOk());
		}
	}

	public static interface TestThread {
		public Boolean isOk();
		public void start();
		public void join() throws InterruptedException;
	}
}
