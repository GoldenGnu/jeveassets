/*
 * Copyright 2009-2018 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * Original code from jWarframe (https://github.com/GoldenGnu/jwarframe)
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

package net.nikr.eve.jeveasset.io.online;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.data.settings.ProxyData;
import net.nikr.eve.jeveasset.io.shared.FileUtil;


public class Updater {
	private static final Logger LOG = Logger.getLogger(Updater.class.getName());

	private static final String UPDATE_URL = "https://eve.nikr.net/jeveassets/update/";
	private static final String PROGRAM =	 UPDATE_URL + "program/";
	private static final String DATA =		 UPDATE_URL + "data/";
	private static final String UPDATE =	 UPDATE_URL + "jupdate.jar";

	public void update(final String localProgram, String localData, ProxyData proxyData) {
		LOG.info("Checking online version");
		Getter getter = new Getter();
		final String onlineProgram = getter.get(PROGRAM+"update_version.dat");
		update("Program", onlineProgram, localProgram, PROGRAM, proxyData);
		final String onlineData = getter.get(DATA+"update_version.dat");
		if (localData == null) {
			fixData();
		} else {
			update("Static data", onlineData, localData, DATA, proxyData);
		}
	}

	public boolean checkProgramUpdate(final String localProgram) {
		LOG.info("Checking online version");
		Getter getter = new Getter();
		final String onlineProgram = getter.get(PROGRAM+"update_version.dat");
		return onlineProgram != null && !onlineProgram.equals(localProgram);
	}

	public boolean checkDataUpdate(String localData) {
		LOG.info("Checking online version");
		Getter getter = new Getter();
		final String onlineData = getter.get(DATA+"update_version.dat");
		return onlineData != null && !onlineData.equals(localData);
	}

	public void fixData() {
		int value = JOptionPane.showConfirmDialog(null, 
				"One of the data files in the data folder is corrupted or missing\r\n"
				+ "jEveAssets will not work without it\r\n"
				+ "Download the latest version with auto update?\r\n"
				,
				"Critical Error",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
		if (value == JOptionPane.OK_OPTION) {
			LOG.info("Updating data");
			boolean download = downloadUpdater();
			if (download) {
				runUpdate(DATA, null);
			} else {
				JOptionPane.showMessageDialog(null, "Auto update failed\r\nPlease, re-download jEveAssets and leave the unzipped directory intact\r\nPress OK to close jEveAssets", "Critical Error", JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Please, re-download jEveAssets and leave the unzipped directory intact\r\nRestart jEveAssets to use auto update to fix the problem\r\nPress OK to close jEveAssets", "Critical Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}

	public void fixLibs() {
		int value = JOptionPane.showConfirmDialog(null, 
				"One of the libraies in the lib folder is corrupted or missing\r\n"
				+ "jEveAssets will not work without it\r\n"
				+ "Download the latest version with auto update?\r\n"
				,
				"Critical Error",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
		if (value == JOptionPane.OK_OPTION) {
			LOG.info("Updating program");
			boolean download = downloadUpdater();
			if (download) {
				runUpdate(PROGRAM, null);
			} else {
				JOptionPane.showMessageDialog(null, "Auto update failed\r\nPlease, re-download jEveAssets and leave the unzipped directory intact\r\nPress OK to close jEveAssets", "Critical Error", JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Please, re-download jEveAssets and leave the unzipped directory intact\r\nRestart jEveAssets to use auto update to fix the problem\r\nPress OK to close jEveAssets", "Critical Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}

	public void fixMissingClasses() {
		int value = JOptionPane.showConfirmDialog(null, 
				"jEveAssets have been corrupted\r\n"
				+ "You can use auto update to fix the problem\r\n"
				+ "Download the latest version with auto update?\r\n"
				,
				"Critical Error",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
		if (value == JOptionPane.OK_OPTION) {
			LOG.info("Updating program");
			boolean download = downloadUpdater();
			if (download) {
				runUpdate(PROGRAM, null);
			} else {
				JOptionPane.showMessageDialog(null, "Auto update failed\r\nPlease, re-download jEveAssets and leave the unzipped directory intact\r\nPress OK to close jEveAssets", "Critical Error", JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Please, re-download jEveAssets and leave the unzipped directory intact\r\nRestart jEveAssets to use auto update to fix the problem\r\nPress OK to close jEveAssets", "Critical Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}

	public String getLocalData() {
		Getter getter = new Getter();
		return getter.get(new File(FileUtil.getPathDataVersion()));
	}

	private void update(String title, String online, String local, String link, ProxyData proxyData) {
		LOG.log(Level.INFO, "{0} Online: {1} Local: {2}", new Object[]{title.toUpperCase(), online, local});
		if (online != null && !online.equals(local)) {
			int value = JOptionPane.showConfirmDialog(null, 
					title + " update available\r\n"
					+ "\r\n"
					+ "Your version: " + local + "\r\n"
					+ "Latest version: " + online + "\r\n"
					+ "\r\n"
					+ "Update " + title.toLowerCase() + " now?\r\n"
					+ "\r\n"
					,
					"Auto Update",
					JOptionPane.OK_CANCEL_OPTION);
			if (value == JOptionPane.OK_OPTION) {
				LOG.log(Level.INFO, "Updating {0}", title);
				boolean download = downloadUpdater();
				if (download) {
					runUpdate(link, proxyData);
				} else {
					JOptionPane.showMessageDialog(null, "Auto update failed\r\nRestart jEveAssets to try again...", "Auto Update", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private void runUpdate(String link, ProxyData proxyData) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory(getJavaHome());
		processBuilder.command(getArgsString(link, proxyData));
		try {
			processBuilder.start();
			System.exit(0);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Failed to start jupdate.jar", ex);
		}
	}

	private File getJavaHome() {
		return new File(System.getProperty("java.home") + File.separator + "bin");
	}

	private List<String> getArgsString(String link, ProxyData proxyData) {
		List<String> list = new ArrayList<String>();
		list.add("java");
		if (proxyData != null) {
			list.addAll(proxyData.getArgs());
		} else {
			list.add("-Djava.net.useSystemProxies=true");
		}
		list.add("-jar");
		list.add(FileUtil.getPathRunUpdate());
		list.add(link);
		list.add(FileUtil.getPathRunJar());
		return list;
	}

	private static boolean downloadUpdater() {
		DataGetter dataGetter = new DataGetter();
		Getter getter = new Getter();
		String checksum = getter.get(UPDATE+".md5");
		return dataGetter.get(UPDATE, new File(FileUtil.getPathRunUpdate()), checksum);
	}

	private static class Getter {

		protected String get(File file) {
			try {
				return get(new FileReader(file));
			} catch (FileNotFoundException ex) {
				return null;
			}
		}

		protected String get(String link) {
			try {
				URL url = new URL(link);
				return get(new InputStreamReader(url.openStream()));
			} catch (MalformedURLException e) {
				return null;
			} catch (IOException ex) {
				return null;
			}
		}
		
		protected String get(Reader reader) {
			StringBuilder builder = new StringBuilder();
			try {
				BufferedReader in = new BufferedReader(reader);
				
				String str;
				while ((str = in.readLine()) != null) {
					builder.append(str);
				}
				return builder.toString();
			} catch (MalformedURLException e) {
				return null;
			} catch (IOException e) {
				return null;
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException ex) {
						//I give up...
					}
				}
			}
		}
		
	}
}
