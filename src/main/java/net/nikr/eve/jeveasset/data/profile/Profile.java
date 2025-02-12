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
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.io.local.ProfileReader;
import net.nikr.eve.jeveasset.io.local.ProfileWriter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Profile implements Comparable<Profile> {

	private static final Logger LOG = LoggerFactory.getLogger(Profile.class);

	private String name;
	private boolean defaultProfile;
	private boolean activeProfile;
	private final StockpileIDs stockpileIDs;
	private final List<EveApiAccount> accounts = new ArrayList<>();
	private final List<EveKitOwner> eveKitOwners = new ArrayList<>();
	private final List<EsiOwner> esiOwners = new ArrayList<>();

	private Profile() {
		this("Default", true, true);
	}

	public Profile(final String name, final boolean defaultProfile, final boolean activeProfile) {
		this.name = name;
		this.defaultProfile = defaultProfile;
		this.activeProfile = activeProfile;
		this.stockpileIDs = new StockpileIDs(name);
	}

	public boolean load() {
		clear(); //Clear the profile before loading
		stockpileIDs.load(); //Load stockpileIDs
		return ProfileReader.load(this); //Assets (Must be loaded before the price data)
	}

	public void save() {
		ProfileWriter.save(this);
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

	public List<EveApiAccount> getAccounts() {
		return accounts;
	}

	public List<EveKitOwner> getEveKitOwners() {
		return eveKitOwners;
	}

	public List<EsiOwner> getEsiOwners() {
		return esiOwners;
	}

	public void clear() {
		accounts.clear();
		eveKitOwners.clear();
		esiOwners.clear();
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
		stockpileIDs.renameTable(name);
	}

	public void delete() {
		getFile().delete();
		getBackupFile().delete();
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

	public static class DefaultProfile extends Profile {

		public DefaultProfile() {
			super();
		}
	}
}
