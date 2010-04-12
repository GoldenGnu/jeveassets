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

package net.nikr.eve.jeveasset.data;

import java.io.File;


public class Profile implements Comparable<Profile>{

	private String name;
	private boolean defaultProfile;
	private boolean activeProfile;

	public Profile(String name, boolean defaultProfile, boolean activeProfile) {
		this.name = name;
		this.defaultProfile = defaultProfile;
		this.activeProfile = activeProfile;
	}

	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	public boolean isActiveProfile() {
		return activeProfile;
	}

	public void setActiveProfile(boolean activeProfile) {
		this.activeProfile = activeProfile;
	}

	public String getBackupFilename(){
		return getFilenameNoExtension()+".bac";
	}

	public String getFilename() {
		return getFilenameNoExtension()+".xml";
	}

	public File getBackupFile(){
		return new File(getFilenameNoExtension()+".bac");
	}

	public File getFile() {
		return new File(getFilenameNoExtension()+".xml");
	}

	private String getFilenameNoExtension(){
		String filename = getName();
		filename = filename.replace(" ", "_");
		if (defaultProfile){
			filename = "#"+filename;
		}
		filename = Settings.getPathProfilesDirectory()+File.separator+filename;
		return filename;
	}

	public String getName() {
		return name;
	}

	public void setDefaultProfile(boolean defaultProfile) {
		if (this.defaultProfile != defaultProfile){
			File from = getFile();
			File backFrom = getBackupFile();
			this.defaultProfile = defaultProfile;
			File to = getFile();
			File backTo = getBackupFile();
			if (!from.equals(to)){
				from.renameTo(to);
			}
			if (!backFrom.equals(backTo)){
				backFrom.renameTo(backTo);
			}
		}
	}

	public void setName(String name) {
		File from = getFile();
		File backFrom = getBackupFile();
		this.name = name;
		File to = getFile();
		File backTo = getBackupFile();
		if (!from.equals(to)){
			from.renameTo(to);
		}
		if (!backFrom.equals(backTo)){
			backFrom.renameTo(backTo);
		}
	}

	@Override
	public String toString(){
		String temp = name;
		if (defaultProfile) temp = temp+" (default)";
		//if (activeProfile) temp = temp+" (active)";
		return temp;
	}

	@Override
	public int compareTo(Profile o) {
		return this.getName().compareTo(o.getName());
	}
}