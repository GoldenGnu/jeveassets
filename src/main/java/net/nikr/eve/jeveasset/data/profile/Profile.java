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

package net.nikr.eve.jeveasset.data.profile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.io.local.profile.ProfileDatabase;
import net.nikr.eve.jeveasset.io.local.ProfileReader;
import net.nikr.eve.jeveasset.io.local.profile.ProfileDatabase.Table;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Profile implements Comparable<Profile> {

	private static final Logger LOG = LoggerFactory.getLogger(Profile.class);
	private static final String XML = "xml";
	private static final String XML_BAC = "bac";
	private static final String XML_BACKUP = "xmlbackup";
	private static final String SQLITE = "db";
	private static final String SQLITE_BACKUP = "zip";

	public static enum ProfileType {
		XML, SQLITE
	}

	private String name;
	private boolean defaultProfile;
	private boolean activeProfile;
	private ProfileType type;
	private final StockpileIDs stockpileIDs;
	private final List<EsiOwner> esiOwners = new ArrayList<>();

	public Profile() {
		this("Default", true, true, ProfileType.SQLITE);
	}

	public Profile(final String name, final boolean defaultProfile, final boolean activeProfile, final ProfileType type) {
		this.name = name;
		this.defaultProfile = defaultProfile;
		this.activeProfile = activeProfile;
		this.stockpileIDs = new StockpileIDs(name);
		this.type = type;
	}

	public boolean load() {
		clear(); //Clear the profile before loading
		stockpileIDs.load(); //Load stockpileIDs
		if (type == ProfileType.XML) {
			return ProfileReader.load(this); //Assets (Must be loaded before the price data)
		} else if (type == ProfileType.SQLITE) {
			return ProfileDatabase.load(this);
		} else {
			return false;
		}
	}

	public void saveTable(Table table) {
		if (type == ProfileType.XML) {
			save(); //Full save
		} else {
			ProfileDatabase.save(this, table);
		}
	}

	public void save() {
		boolean save = ProfileDatabase.save(this);
		if (save && type == ProfileType.XML) {
			type = ProfileType.SQLITE; //Migrated to SQLite
			File file = new File(getXmlFilename());
			File backup = new File(getBackupXmlFilename());
			file.renameTo(backup);
		}
	}

	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	public boolean isActiveProfile() {
		return activeProfile;
	}

	public void setActiveProfile(final boolean activeProfile) {
		this.activeProfile = activeProfile;
	}

	public StockpileIDs getStockpileIDs() {
		return stockpileIDs;
	}

	public List<EsiOwner> getEsiOwners() {
		return esiOwners;
	}

	public void clear() {
		esiOwners.clear();
	}

	public String getXmlFilename() {
		return getFilenameExtension(XML);
	}

	public String getBackupXmlFilename() {
		return getFilenameExtension(XML_BACKUP);
	}

	public String getSQLiteFilename() {
		return getFilenameExtension(SQLITE);
	}

	public String getBackupSQLiteFilename() {
		String filename = getName() + "_" + Program.PROGRAM_VERSION + "_dbbackup";
		filename = filename.replace(" ", "_"); //Remove spaces
		filename = filename + "." + SQLITE_BACKUP; //Add extension
		if (defaultProfile) {
			filename = "#" + filename; //Mark active profile
		}
		
		filename = FileUtil.getPathProfile(filename);
		return filename;
	}

	private File getBackupFile() {
		switch (type) {
			case XML: return new File(getFilenameExtension(XML_BAC));
			case SQLITE: return null;
			default: return null;
		}
	}

	private File getFile() {
		switch (type) {
			case XML: return new File(getFilenameExtension(XML));
			case SQLITE: return new File(getFilenameExtension(SQLITE));
			default: return null;
		}
	}

	private String getFilenameExtension(String extension) {
		String filename = getName();
		filename = filename.replace(" ", "_"); //Remove spaces
		filename = filename + "." + extension; //Add extension
		if (defaultProfile) {
			filename = "#" + filename; //Mark active profile
		}
		filename = FileUtil.getPathProfile(filename);
		return filename;
	}

	public String getName() {
		return name;
	}

	public void setDefaultProfile(final boolean defaultProfile) {
		if (this.defaultProfile != defaultProfile) {
			File from = getFile();
			File backFrom = getBackupFile();
			this.defaultProfile = defaultProfile;
			File to = getFile();
			File backTo = getBackupFile();
			if (from != null && to != null && !from.equals(to) && !from.renameTo(to)) {
				LOG.warn("Failed to rename profile: {}", getName());
			}
			if (backFrom != null && backTo != null && !backFrom.equals(backTo) && !backFrom.renameTo(backTo)) {
				LOG.warn("Failed to rename profile backup: {}", getName());
			}
		}
	}

	public void setName(final String name) {
		File from = getFile();
		File backFrom = getBackupFile();
		this.name = name;
		File to = getFile();
		File backTo = getBackupFile();
		if (from != null && to != null && !from.equals(to) && !from.renameTo(to)) {
			LOG.warn("Failed to rename profile: {}", getName());
		}
		if (backFrom != null && backTo != null && !backFrom.equals(backTo) && !backFrom.renameTo(backTo)) {
			LOG.warn("Failed to rename profile backup: {}", getName());
		}
		stockpileIDs.renameTable(name);
	}

	public void delete() {
		File file = getFile();
		if (file != null && !file.delete()) {
			LOG.warn("Failed to delete profile: {}", getName());
		}
		File backupFile = getBackupFile();
		if (backupFile != null && !backupFile.delete()) {
			LOG.warn("Failed to delete profile backup: {}", getName());
		}
		stockpileIDs.removeTable();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Profile other = (Profile) obj;
		if (this.name == null && other.name == null) {
			return true; //Both null
		} else if (this.name == null || other.name == null) {
			return false; //One null
		} else {
			return this.name.equalsIgnoreCase(other.name);
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + (this.name != null ? this.name.toLowerCase().hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		String temp = name;
		if (defaultProfile) {
			temp = temp + " (default)";
		}
		//if (activeProfile) temp = temp+" (active)";
		return temp;
	}

	@Override
	public int compareTo(final Profile o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}
}
