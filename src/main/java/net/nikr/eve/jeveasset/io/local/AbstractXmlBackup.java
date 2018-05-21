/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.nikr.eve.jeveasset.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;


public abstract class AbstractXmlBackup {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractXmlBackup.class);

	protected boolean exist(final String filename) {
		return new File(filename).exists() //.xml
				|| getNewFile(filename).exists() //.new
				|| getBackupFile(filename).exists(); //.bac
	}
	
	protected boolean restoreBackupFile(final String filename) {
		File targetFile = new File(filename);
		renameFile(targetFile, getCorruptFile(filename)); //Backup corrupted file
		return renameFile(getBackupFile(filename), targetFile);
	}

	protected boolean restoreNewFile(final String filename) {
		File targetFile = new File(filename);
		renameFile(targetFile, getCorruptFile(filename)); //Backup corrupted file
		return renameFile(getNewFile(filename), targetFile);
	}

	protected void restoreFailed(final String filename) {
		File targetFile = new File(filename);
		renameFile(targetFile, getCorruptFile(filename)); //Backup corrupted file
	}
	
	protected void backupFile(final String filename) {
		File targetFile = new File(filename);
		//target to bac (new is safe)
		renameFile(targetFile, getBackupFile(filename));
		//new to targe (bac is safe)
		renameFile(getNewFile(filename), targetFile);
	}

	protected File getNewFile(final String filename) {
		return new File(filename.substring(0, filename.lastIndexOf(".")) + ".new");
	}

	private File getProgramBackup(final String filename) {
		return new File(filename.substring(0, filename.lastIndexOf(".")) + "_" + Program.PROGRAM_VERSION.replace(" ", "_") + "_backup.zip");
	}
	
	protected void backup(final String filename, final Element element) {
		File backupFile = getProgramBackup(filename);
		if (!backupFile.exists()) {
			ZipOutputStream out = null;
			InputStream in = null;
			try {
				File sourceFile = new File(filename);
				out = new ZipOutputStream(new FileOutputStream(backupFile));
				in = new FileInputStream(sourceFile);
				ZipEntry e = new ZipEntry(sourceFile.getName());
				out.putNextEntry(e);
				byte[] buffer = new byte[8192];
				int len;
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.closeEntry();
				LOG.info("Backup Created: " + backupFile.getName());
			} catch (IOException ex) {
				LOG.error("Failed to create backup for new program version", ex);
			} finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException ex) {
					//No problem
				}
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
					//No problem
				}
			}
		}
	}

	protected void lock(final String filename) {
		File xmlFile = new File(filename);
		FileLock.lock(xmlFile);
	}

	protected void unlock(final String filename) {
		File xmlFile = new File(filename);
		FileLock.unlock(xmlFile);
	}

	private File getBackupFile(final String filename) {
		return new File(filename.substring(0, filename.lastIndexOf(".")) + ".bac");
	}

	private File getCorruptFile(final String filename) {
		File file = null;
		int count = 0;
		while(file == null || file.exists()) {
			count++;
			file = new File(filename.substring(0, filename.lastIndexOf(".")) + ".error" + count);
		}
		return file;
	}

	private boolean renameFile(File from, File to) {
		if (!from.exists()) {
			LOG.warn("Move failed: " + from.getName() + " does not exist");
			return false;
		}
		if (to.exists() && !to.delete()) {
			LOG.warn("Move failed: failed to delete: " + to.getName());
			return false;
		}
		if (from.exists() && from.renameTo(to)) {
			LOG.info(from.getName() + " moved to: "+ to.getName());
			return true;
		} else {
			LOG.warn("Move failed: from " + from.getName() + " to " + to.getName());
			return false;
		}
	}
}
