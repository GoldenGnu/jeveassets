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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.gui.shared.components.JLabelMultilineHtml;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.LoggerFactory;

public class FileLock {

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FileLock.class);
	private static final Object SYNC_LOCK = new Object();
	private static final int MAX_TRIES = 12; //1 minute
	private static final int DELAY = 5000;
	private static final List<File> LOCKS = new ArrayList<>();
	private static final List<SafeFileIO> OS_LOCKS = new ArrayList<>();
	private static boolean safe = false;

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
		if (!safe) {
			safe = true;
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
		folder = new File(FileUtil.getPathProfilesDirectory());
		unlockFiles(folder.listFiles());
		folder = new File(FileUtil.getPathStaticDataDirectory());
		unlockFiles(folder.listFiles());
		folder = new File(FileUtil.getPathDataDirectory());
		unlockFiles(folder.listFiles());
		while (!OS_LOCKS.isEmpty()) {
			try {
				OS_LOCKS.get(0).close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
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

	private static void lock(File file) {
		lock(file, 0, DELAY);
	}

	private static void lock(File file, int tries, int delay) {
		try {
			tryLockFile(file);
		} catch (Exception ex) {
			tries++;
			if (tries > MAX_TRIES) {
				JOptionPane.showMessageDialog(null, getMessage(file), General.get().fileLockTitle(), JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			} else {
				waitForUnlock(file, tries, delay);
				lock(file, tries, delay);
			}
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

	private static synchronized void unlock(File file) {
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
		JLabelMultilineHtml jEditorPane = new JLabelMultilineHtml(General.get().fileLockMsg(file.getName()));
		return jEditorPane;
	}

	public static class SafeFileIO implements Closeable {

		private final File file;
		private final boolean skipInternalLock;
		private Closeable closeable;
		private FileChannel channel;
		private java.nio.channels.FileLock lock;

		public SafeFileIO(String filename) {
			this(new File(filename));
		}

		public SafeFileIO(File file) {
			this(file, false);
		}

		public SafeFileIO(File file, boolean skipInternalLock) {
			this.file = file;
			this.skipInternalLock = skipInternalLock;
			if (!skipInternalLock) {
				FileLock.lock(file); //Lock internally - must be first
			}
			OS_LOCKS.add(this);
		}

		public OutputStreamWriter getOutputStreamWriter() throws IOException {
			return getOutputStreamWriter(null);
		}

		public OutputStreamWriter getOutputStreamWriter(final String encoding) throws IOException {
			FileOutputStream os = getFileOutputStream();
			OutputStreamWriter osw;
			if (encoding != null) {
				osw = new OutputStreamWriter(os, encoding);
			} else {
				osw = new OutputStreamWriter(os);
			}
			closeable = osw;
			return osw;
		}

		public FileOutputStream getFileOutputStream() throws IOException {
			unlock();
			FileOutputStream os = new FileOutputStream(file);
			closeable = os;
			channel = os.getChannel();
			lock = channel.lock(); //Write Lock
			return os;
		}

		public FileInputStream getFileInputStream() throws IOException {
			unlock();
			FileInputStream is = new FileInputStream(file);
			closeable = is;
			channel = is.getChannel();
			lock = channel.lock(0, Long.MAX_VALUE, true); //Read Lock
			return is;
		}

		public final void unlock() throws IOException {
			if (lock != null && lock.isValid()) {
				lock.release();
			}
			if (closeable != null) {
				closeable.close();
			}
			if (channel != null) {
				channel.close();
			}
			OS_LOCKS.remove(this);
		}

		@Override
		public final void close() throws IOException {
			unlock();
			if (!skipInternalLock) {
				FileLock.unlock(file); //Release internally - must be last
			}
		}
	}
}
