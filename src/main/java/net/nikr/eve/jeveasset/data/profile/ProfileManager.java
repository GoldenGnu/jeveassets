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
package net.nikr.eve.jeveasset.data.profile;

import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.io.local.ProfileReader;
import net.nikr.eve.jeveasset.io.local.ProfileWriter;
import net.nikr.eve.jeveasset.io.local.ProfileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProfileManager {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileManager.class);

	private final List<EveApiAccount> accounts = new ArrayList<EveApiAccount>();
	private final List<EveKitOwner> eveKitOwners = new ArrayList<EveKitOwner>();
	private final List<EsiOwner> esiOwners = new ArrayList<EsiOwner>();
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

	public List<EveApiAccount> getAccounts() {
		return accounts;
	}

	public List<OwnerType> getOwnerTypes() {
		List<OwnerType> owners = new ArrayList<OwnerType>();
		for (EveApiAccount account : getAccounts()) {
			owners.addAll(account.getOwners());
		}
		owners.addAll(getEveKitOwners());
		owners.addAll(getEsiOwners());
		return owners;
	}

	public List<EveKitOwner> getEveKitOwners() {
		return eveKitOwners;
	}

	public List<EsiOwner> getEsiOwners() {
		return esiOwners;
	}

	public void loadActiveProfile() {
	//Load Profile
		LOG.info("Loading profile: {}", activeProfile.getName());
		accounts.clear();
		eveKitOwners.clear();
		esiOwners.clear();
		ProfileReader.load(this, activeProfile.getFilename()); //Assets (Must be loaded before the price data)
		SplashUpdater.setProgress(40);
	//Price data (update as needed)
		SplashUpdater.setProgress(45);
	}
}
