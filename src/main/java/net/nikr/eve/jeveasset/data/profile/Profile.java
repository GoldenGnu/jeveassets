/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.data.settings.Settings;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Profile implements Comparable<Profile> {

	private static final Logger LOG = LoggerFactory.getLogger(Profile.class);

	private String name;
	private boolean defaultProfile;
	private boolean activeProfile;
	private Set<Long> stockpileIDs = null;

	public Profile(final String name, final boolean defaultProfile, final boolean activeProfile) {
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

	public void setActiveProfile(final boolean activeProfile) {
		this.activeProfile = activeProfile;
	}

	public Set<Long> getStockpileIDs() {
		createList();
		return stockpileIDs;
	}

	public void setStockpileIDs(Set<Long> stockpileIDs) {
		this.stockpileIDs = stockpileIDs;
	}

	private void createList() {
		if (stockpileIDs == null) {
			stockpileIDs = new HashSet<>();
			for (Stockpile stockpile : Settings.get().getStockpiles()) {
				stockpileIDs.add(stockpile.getId());
			}
		}
	}

	public String getBackupFilename() {
		return getFilenameNoExtension() + ".bac";
	}

	public String getFilename() {
		return getFilenameNoExtension() + ".xml";
	}

	public File getBackupFile() {
		return new File(getFilenameNoExtension() + ".bac");
	}

	public File getFile() {
		return new File(getFilenameNoExtension() + ".xml");
	}

	private String getFilenameNoExtension() {
		String filename = getName();
		filename = filename.replace(" ", "_");
		if (defaultProfile) {
			filename = "#" + filename;
		}
		filename = FileUtil.getPathProfilesDirectory() + File.separator + filename;
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
			if (!from.equals(to)
							&& !from.renameTo(to)) {
				LOG.warn("Failed to rename profile: {}", this.getName());
			}
			if (!backFrom.equals(backTo)
							&& !backFrom.renameTo(backTo)) {
				LOG.warn("Failed to rename profile backup: {}", this.getName());
			}
		}
	}

	public void setName(final String name) {
		File from = getFile();
		File backFrom = getBackupFile();
		this.name = name;
		File to = getFile();
		File backTo = getBackupFile();
		if (!from.equals(to)) {
			from.renameTo(to);
		}
		if (!backFrom.equals(backTo)) {
			backFrom.renameTo(backTo);
		}
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
