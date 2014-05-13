/*
 * Copyright 2014 Niklas Kyster Rasmussen
 *
 * This file is part of jWarframe.
 *
 * jWarframe is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jWarframe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jWarframe; if not, write to the Free Software
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
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Updater {
	private static final Logger LOG = LoggerFactory.getLogger(Updater.class);

	private static final String PROGRAM_UPDATE_URL = "http://eve.nikr.net/jeveassets/update/program/";
	private static final String DATA_UPDATE_URL =    "http://eve.nikr.net/jeveassets/update/data/";

	public void update() {
		LOG.info("Checking online version");
		Getter getter = new Getter();
		String programVersion = getter.get(PROGRAM_UPDATE_URL);
		LOG.info("PROGRAM Online: " + programVersion + " Local: " + Program.PROGRAM_VERSION);
		if (programVersion != null && !programVersion.equals(Program.PROGRAM_VERSION)) {
			int value = JOptionPane.showConfirmDialog(null, 
					"Update jEveAssets now?",
					"Program Update Available",
					JOptionPane.OK_CANCEL_OPTION);
			if (value == JOptionPane.OK_OPTION) {
				LOG.info("Updating program");
				runUpdate(PROGRAM_UPDATE_URL);
			}
		}
		String dataVersion = getter.get(DATA_UPDATE_URL);
		LOG.info("DATA Online: " + dataVersion + " Local: " + getLocalData());
		if (dataVersion != null && !dataVersion.equals(getLocalData())) {
			int value = JOptionPane.showConfirmDialog(null, 
					"Update static data now?",
					"Data Update Available",
					JOptionPane.OK_CANCEL_OPTION);
			if (value == JOptionPane.OK_OPTION) {
				LOG.info("Updating program");
				runUpdate(DATA_UPDATE_URL);
			}
		}
	}

	public String getLocalData() {
		Getter getter = new Getter();
		return getter.get(new File(Settings.getPathDataVersion()));
	}

	private void runUpdate(String link) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory(getJavaHome());
		processBuilder.command(getArgsString(link));
		try {
			processBuilder.start();
			System.exit(0);
		} catch (IOException ex) {
			LOG.error("Failed to start jupdate.jat", ex);
		}
	}

	private File getJavaHome() {
		return new File(System.getProperty("java.home") + File.separator + "bin");
	}

	private List<String> getArgsString(String link) {
		List<String> list = new ArrayList<String>();
		list.add("java");
		list.add("-jar");
		list.add(Settings.getPathRunUpdate());
		list.add(link);
		System.out.println(Settings.getPathRunJar());
		list.add(Settings.getPathRunJar());
		return list;
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
				URL url = new URL(link+"update_version.dat");
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
