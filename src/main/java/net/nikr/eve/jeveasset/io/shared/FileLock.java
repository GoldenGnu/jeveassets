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
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.i18n.General;
import org.slf4j.LoggerFactory;

public class FileLock {

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FileLock.class);
	private static final Object SYNC_LOCK = new Object();
	private static final int MAX_TRIES = 12; //1 minute
	private static final int DELAY = 5000;
	private static final List<File> LOCKS = new ArrayList<File>();
	private static boolean SAFE = false;

	private static void saferShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				unlockLocked();
			}
		});
	}

	private static void unlockLocked() {
		LOG.info("Unlocking " + LOCKS.size() + " files");
		while (!LOCKS.isEmpty()) {
			unlock(LOCKS.get(0));
		}
	}

	private static void add(File file) {
		if (!SAFE) {
			SAFE = true;
			saferShutdown();

		}
		LOCKS.add(file);
	}

	private static void remove(File file) {
		LOCKS.remove(file);
	}

	public static void unlockAll() {
		LOG.info("Unlocking all files");
		File folder;
		folder = new File(Settings.getPathProfilesDirectory());
		unlockFiles(folder.listFiles());
		folder = new File(Settings.getPathStaticDataDirectory());
		unlockFiles(folder.listFiles());
		folder = new File(Settings.getPathDataDirectory());
		unlockFiles(folder.listFiles());
	}

	private static void unlockFiles(File[] files) {
		if (files == null) { //We can not be sure directory has been created...
			return;
		}
		for (File file : files) {
			if (file.getName().endsWith(".LOCK")) {
				file.delete();
			}
		}
	}

	public static void lock(File file) {
		lock(file, 0, DELAY);
	}

	private static void lock(File file, int tries, int delay) {
		try {
			tryLockFile(file);
		} catch (Exception ex) {
			tries++;
			if (tries > MAX_TRIES) {
				JOptionPane.showMessageDialog(null, getMessage(file), General.get().fileLockTitle(), JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			waitForUnlock(file, tries, delay);
			lock(file, tries, delay);
		}
	}

	private static synchronized void tryLockFile(File file) throws Exception {
		if (isLocked(file)) {
			throw new IOException();
		}
		boolean ok = convertFile(file).createNewFile();
		if (!ok) {
			throw new IOException();
		}
		add(file);
	}

	public static synchronized void unlock(File file) {
		convertFile(file).delete();
		remove(file);
		synchronized (SYNC_LOCK) {
			SYNC_LOCK.notify();
		}
	}

	protected static boolean isLocked(File file) {
		return convertFile(file).exists();
	}

	private static File convertFile(File file) {
		return new File(file.getAbsolutePath() + ".LOCK");
	}

	private static void waitForUnlock(File file, int tries, int delay) {
		LOG.info("Waiting for lock: " + file.getName() + " (" + tries + " of " + MAX_TRIES + ")");
		try {
			synchronized (SYNC_LOCK) {
				SYNC_LOCK.wait(delay);
			}
		} catch (InterruptedException ex) {
		}
	}

	private static JEditorPane getMessage(File file) {
		JLabel jLabel = new JLabel();
		JEditorPane jEditorPane = new JEditorPane("text/html", "");
		jEditorPane.setEditable(false);
		jEditorPane.setFocusable(false);
		jEditorPane.setOpaque(false);
		jEditorPane.setText("<html><body style=\"font-family: " + jLabel.getFont().getName() + "; font-size: " + jLabel.getFont().getSize() + "pt\">"
				+ General.get().fileLockMsg(file.getName())
				+ "</body></html>");
		jEditorPane.addHyperlinkListener(DesktopUtil.getHyperlinkListener(null));
		return jEditorPane;
	}
}
