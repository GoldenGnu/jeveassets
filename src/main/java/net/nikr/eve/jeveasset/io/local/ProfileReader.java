/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.Profile;
import net.nikr.eve.jeveasset.data.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProfileReader {

	private final static Logger LOG = LoggerFactory.getLogger(ProfileReader.class);
	
	private ProfileReader() {}

	public static boolean load(Settings settings){
		backwardCompatibility();
		List<Profile> profiles = new ArrayList<Profile>();
		File profilesDirectory = new File(Settings.getPathProfilesDirectory());
		FileFilter fileFilter = new XmlFileFilter();
		
		File[] files = profilesDirectory.listFiles(fileFilter);
		if (files != null){
			boolean defaultProfileFound = false;
			for (File file : files){
				String name = file.getName();
				Profile profile = new Profile(formatName(name), defaultProfile(name), activeProfile(name));
				if (profile.isDefaultProfile() && !defaultProfileFound){
					LOG.info("Default profile found: {}", formatName(name));
					defaultProfileFound = true;
					profiles.add(0, profile);
					settings.setActiveProfile(profile);
				}  else if (profile.isDefaultProfile() && defaultProfileFound){
					LOG.warn("Default profile found (again): {}", formatName(name));
					profile.setDefaultProfile(false);
					profile.setActiveProfile(false);
					profiles.add(profile);
				} else {
					LOG.info("Profile found: {}", formatName(name));
					profiles.add(profile);
				}
			}
			if (!defaultProfileFound && !profiles.isEmpty()){
				LOG.warn("No default profile found: Using first available");
				profiles.get(0).setDefaultProfile(true);
				profiles.get(0).setActiveProfile(true);
				settings.setActiveProfile(profiles.get(0));
			} else if (!defaultProfileFound && profiles.isEmpty()){
				LOG.info("No default profile found: Using default settings");
			}
			if (!profiles.isEmpty()) settings.setProfiles(profiles);
			return true;
		} else {
			return false;
		}
	}

	private static void backwardCompatibility(){
		//Create profiles directory
		File dir = new File(Settings.getPathProfilesDirectory());
		if (!dir.exists()){
			if (dir.mkdirs()){
				LOG.info("Created profiles directory");
			} else {
				LOG.error("Failed to make profiles directory");
			}
		}
		//Move assets.xml to new location
		File assets = new File(Settings.getPathAssetsOld());
		if (assets.exists()){
			if (assets.renameTo(new File(Settings.getPathProfilesDirectory(), "#Default.xml"))){
				LOG.info("Moved assets.xml to new location");
			} else {
				LOG.error("Failed to move assets.xml to new location");
			}
		}
		//Move assets.bac to new location
		String filename = Settings.getPathAssetsOld();
		int end = filename.lastIndexOf(".");
		filename = filename.substring(0, end)+".bac";
		File backup = new File(filename);
		if (backup.exists()){
			if (backup.renameTo(new File(Settings.getPathProfilesDirectory(), "#Default.bac"))){
				LOG.info("Moved assets.xml to new location");
			} else {
				LOG.error("Failed to move assets.xml to new location");
			}
		}
	}

	private static String formatName(String name){
		if (name.contains(".")){
			int end = name.lastIndexOf(".");
			name = name.substring(0, end);
		}
		name = name.replace("_", " ");
		name = name.replace("#", "");
		return name;
	}

	private static boolean defaultProfile(String name){
		return name.startsWith("#");
	}
	private static boolean activeProfile(String name){
		return name.startsWith("#");
	}

	private static class XmlFileFilter implements  FileFilter {
		@Override
		public boolean accept(File file) {
			return !file.isDirectory() && file.getName().endsWith(".xml");
		}
	}
}
