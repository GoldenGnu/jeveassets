/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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
import net.nikr.log.Log;


public class ProfileReader {

	public static boolean load(Settings settings){
		backwardCompatibility();
		List<Profile> profiles = new ArrayList<Profile>();
		File profilesDirectory = new File(Settings.getPathProfilesDirectory());
		FileFilter fileFilter = new XmlFileFilter();
		
		File[] files = profilesDirectory.listFiles(fileFilter);
		if (files != null){
			for (File file : files){
				String name = file.getName();
				
				Profile profile = new Profile(formatName(name), defaultProfile(name), activeProfile(name));
				if (profile.isDefaultProfile()){
					Log.info("Default profile found: "+formatName(name));
					profiles.add(0, profile);
					settings.setActiveProfile(profile);
				} else {
					Log.info("Profile found: "+formatName(name));
					profiles.add(profile);
				}
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
				Log.info("Created profiles directory");
			} else {
				Log.error("Failed to make profiles directory");
			}
		}
		//Move assets.xml to new location
		File assets = new File(Settings.getPathAssetsOld());
		if (assets.exists()){
			if (assets.renameTo(new File(Settings.getPathProfilesDirectory(), "#Default.xml"))){
				Log.info("Moved assets.xml to new location");
			} else {
				Log.error("Failed to move assets.xml to new location");
			}
		}
		//Move assets.bac to new location
		String filename = Settings.getPathAssetsOld();
		int end = filename.lastIndexOf(".");
		filename = filename.substring(0, end)+".bac";
		File backup = new File(filename);
		if (backup.exists()){
			if (backup.renameTo(new File(Settings.getPathProfilesDirectory(), "#Default.bac"))){
				Log.info("Moved assets.xml to new location");
			} else {
				Log.error("Failed to move assets.xml to new location");
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
