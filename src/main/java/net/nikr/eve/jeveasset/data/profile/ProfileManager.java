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

import java.awt.Component;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.Profile.DefaultProfile;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.local.ProfileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProfileManager {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileManager.class);

	private boolean profileLoadError = false;
	private Profile activeProfile;
	private List<Profile> profiles = new ArrayList<>();

	public ProfileManager() {
		activeProfile = new DefaultProfile();
		profiles.add(activeProfile);
	}

	public void searchProfile() {
		//Find profiles
		boolean found = ProfileFinder.load(this);
		if (!found) { //No profiles found, using default
			saveProfile(); //Saving new default profile
		}
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public boolean containsProfileName(String name) {
		for (Profile profile : profiles) {
			if (profile.getName().equalsIgnoreCase(name)) { //Profile names are case insensitive
				return true;
			}
		}
		return false;
	}

	public void setProfiles(final List<Profile> profiles) {
		this.profiles = profiles;
	}

	public void setActiveProfile(final Profile activeProfile) {
		this.activeProfile = activeProfile;
	}

	public StockpileIDs getStockpileIDs() {
		return activeProfile.getStockpileIDs();
	}

	public Profile getActiveProfile() {
		return activeProfile;
	}

	public void saveProfile() {
		activeProfile.save();
	}

	public List<OwnerType> getOwnerTypes() {
		List<OwnerType> owners = new ArrayList<>();
		for (EveApiAccount account : getAccounts()) {
			owners.addAll(account.getOwners());
		}
		owners.addAll(getEveKitOwners());
		owners.addAll(getEsiOwners());
		return owners;
	}

	public List<EveKitOwner> getEveKitOwners() {
		return activeProfile.getEveKitOwners();
	}

	public List<EsiOwner> getEsiOwners() {
		return activeProfile.getEsiOwners();
	}

	public List<EveApiAccount> getAccounts() {
		return activeProfile.getAccounts();
	}

	public void clear() {
		activeProfile.clear();
	}

	public void loadActiveProfile() {
	//Load Profile
		LOG.info("Loading profile: {}", activeProfile.getName());
		profileLoadError = !activeProfile.load();
		SplashUpdater.setProgress(40);
	}

	public void showProfileLoadErrorWarning(Component parentComponent) {
		if (profileLoadError) {
			JOptionPane.showMessageDialog(parentComponent, GuiShared.get().errorLoadingProfileMsg(), GuiShared.get().errorLoadingProfileTitle(), JOptionPane.ERROR_MESSAGE);
		}
	}
}
