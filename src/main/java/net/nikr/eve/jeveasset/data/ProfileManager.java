/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.io.local.ProfileReader;
import net.nikr.eve.jeveasset.io.local.ProfileWriter;
import net.nikr.eve.jeveasset.io.local.ProfileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProfileManager {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileManager.class);

	private List<Account> accounts = new ArrayList<Account>();
	private Profile activeProfile;
	private List<Profile> profiles = new ArrayList<Profile>();

	public ProfileManager() {
		activeProfile = new Profile("Default", true, true);
		profiles.add(activeProfile);
	}

	public void searchProfile() {
		//Find profiles
		ProfileFinder.load(this);
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(final List<Profile> profiles) {
		this.profiles = profiles;
	}

	public void setActiveProfile(final Profile activeProfile) {
		this.activeProfile = activeProfile;
	}

	public Profile getActiveProfile() {
		return activeProfile;
	}

	public void saveProfile() {
		ProfileWriter.save(this, activeProfile.getFilename());
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(final List<Account> accounts) {
		this.accounts = accounts;
	}

	public void loadActiveProfile() {
	//Load Profile
		LOG.info("Loading profile: {}", activeProfile.getName());
		accounts = new ArrayList<Account>();
		ProfileReader.load(this, activeProfile.getFilename()); //Assets (Must be loaded before the price data)
		SplashUpdater.setProgress(40);
	//Price data (update as needed)
		SplashUpdater.setProgress(45);
	}
}
