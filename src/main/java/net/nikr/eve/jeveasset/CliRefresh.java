/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.Profile;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.nikr.eve.jeveasset.io.local.profile.ProfileDatabase.Table;
import net.troja.eve.esi.ApiClientBuilder;
import net.troja.eve.esi.auth.OAuth;


public class CliRefresh {

	static final int EXIT_AUTHORIZATION_FAILURE = 1;
	static final int EXIT_PERSISTENCE_FAILURE = 2;

	private final ProfileStore profileStore;
	private final TokenRefresher tokenRefresher;
	private final PrintStream out;
	private final PrintStream err;

	public CliRefresh() {
		this(new LocalProfileStore(), new OAuthTokenRefresher(), System.out, System.err);
	}

	CliRefresh(ProfileStore profileStore, TokenRefresher tokenRefresher, PrintStream out, PrintStream err) {
		this.profileStore = profileStore;
		this.tokenRefresher = tokenRefresher;
		this.out = out;
		this.err = err;
	}

	int refresh() {
		List<Profile> profiles = new ArrayList<>();
		List<Profile> loadedProfiles = new ArrayList<>();
		List<AccountRecord> accounts = new ArrayList<>();
		RunState state = new RunState();
		try {
			try {
				List<Profile> foundProfiles = profileStore.findProfiles();
				if (foundProfiles != null) {
					profiles.addAll(foundProfiles);
				}
			} catch (RuntimeException ex) {
				state.authorizationFailure = true;
				err.println("ERROR operation=profile_discovery");
			}

			if (!state.authorizationFailure) {
				loadProfiles(profiles, loadedProfiles, state);
				accounts.addAll(getAccounts(loadedProfiles));
			}

			if (!state.authorizationFailure) {
				preflightProfiles(loadedProfiles, state);
			}

			if (!state.authorizationFailure && !state.persistenceFailure) {
				refreshAccounts(accounts, state);
			}
		} finally {
			clearProfiles(profiles, state);
		}

		finishAccounts(accounts, state);
		printResults(profiles.size(), accounts, state.persistenceFailures);
		if (state.persistenceFailure) {
			return EXIT_PERSISTENCE_FAILURE;
		} else if (state.authorizationFailure) {
			return EXIT_AUTHORIZATION_FAILURE;
		} else {
			return 0;
		}
	}

	private void loadProfiles(List<Profile> profiles, List<Profile> loadedProfiles, RunState state) {
		for (Profile profile : profiles) {
			boolean loaded = false;
			try {
				loaded = profileStore.load(profile);
			} catch (RuntimeException ex) {
				//Reported below without the potentially secret-bearing exception.
			}
			if (loaded) {
				loadedProfiles.add(profile);
			} else {
				state.authorizationFailure = true;
				err.println("ERROR profile=" + safeLabel(profile.getName()) + " operation=load");
			}
		}
	}

	private void preflightProfiles(List<Profile> profiles, RunState state) {
		for (Profile profile : profiles) {
			boolean saved = false;
			try {
				saved = profileStore.preflightOwners(profile);
			} catch (RuntimeException ex) {
				//Reported below without the potentially secret-bearing exception.
			}
			if (!saved) {
				state.persistenceFailure = true;
				state.persistenceFailures++;
				err.println("ERROR profile=" + safeLabel(profile.getName()) + " operation=preflight_save");
			}
		}
	}

	private List<AccountRecord> getAccounts(List<Profile> profiles) {
		List<AccountRecord> accounts = new ArrayList<>();
		for (Profile profile : profiles) {
			List<EsiOwner> owners = new ArrayList<>(profile.getEsiOwners());
			Collections.sort(owners, new Comparator<EsiOwner>() {
				@Override
				public int compare(EsiOwner first, EsiOwner second) {
					int compare = compareStrings(first.getOwnerName(), second.getOwnerName());
					if (compare == 0) {
						compare = compareStrings(first.getAccountID(), second.getAccountID());
					}
					return compare;
				}
			});
			for (EsiOwner owner : owners) {
				accounts.add(new AccountRecord(profile, owner));
			}
		}
		return accounts;
	}

	private void refreshAccounts(List<AccountRecord> accounts, RunState state) {
		Map<GrantKey, List<AccountRecord>> grants = new LinkedHashMap<>();
		for (AccountRecord account : accounts) {
			if (account.callbackURL == null || !isNonBlank(account.originalRefreshToken)) {
				account.status = AccountStatus.FAILED;
				state.authorizationFailure = true;
				continue;
			}
			GrantKey key = new GrantKey(account.callbackURL, account.originalRefreshToken);
			List<AccountRecord> grant = grants.get(key);
			if (grant == null) {
				grant = new ArrayList<>();
				grants.put(key, grant);
			}
			grant.add(account);
		}

		Map<GrantKey, RefreshResult> cache = new HashMap<>();
		for (Map.Entry<GrantKey, List<AccountRecord>> entry : grants.entrySet()) {
			if (state.stopRemoteRefreshes) {
				break;
			}
			GrantKey originalKey = entry.getKey();
			List<AccountRecord> grant = entry.getValue();
			RefreshResult result;
			if (cache.containsKey(originalKey)) {
				result = cache.get(originalKey);
			} else {
				result = refreshGrant(grant.get(0));
				cache.put(originalKey, result);
				if (result.isSuccessful() && isNonBlank(result.refreshToken)) {
					cache.put(originalKey.withRefreshToken(result.refreshToken), result);
				}
			}
			applyResult(grant, result, state);
		}
	}

	private RefreshResult refreshGrant(AccountRecord representative) {
		RefreshResult result;
		try {
			result = tokenRefresher.refresh(representative.owner);
		} catch (RuntimeException ex) {
			result = RefreshResult.failureWithRefreshToken(readRefreshToken(representative.owner));
		}
		if (result == null) {
			result = RefreshResult.failure();
		}
		if (!result.refreshTokenObserved) {
			result = result.withRefreshToken(readRefreshToken(representative.owner));
		}
		return result;
	}

	private void applyResult(List<AccountRecord> grant, RefreshResult result, RunState state) {
		String refreshToken = result.refreshToken;
		if (!isNonBlank(refreshToken)) {
			restoreGrant(grant);
			setStatus(grant, AccountStatus.FAILED);
			state.authorizationFailure = true;
			state.stopRemoteRefreshes = true;
			return;
		}

		boolean success = result.isSuccessful();
		if (!success) {
			state.authorizationFailure = true;
		}
		try {
			for (AccountRecord account : grant) {
				account.owner.setAuth(account.callbackURL, refreshToken, null);
				if (success) {
					account.owner.setInvalid(false);
				} else {
					account.owner.setInvalid(account.originalInvalid);
				}
			}
		} catch (RuntimeException ex) {
			restoreGrant(grant);
			setStatus(grant, AccountStatus.FAILED);
			state.persistenceFailure = true;
			state.persistenceFailures++;
			state.stopRemoteRefreshes = true;
			return;
		}

		setStatus(grant, success ? AccountStatus.SUCCEEDED : AccountStatus.FAILED);
		Map<Profile, List<AccountRecord>> affectedProfiles = getAffectedProfiles(grant, refreshToken, success);
		for (Map.Entry<Profile, List<AccountRecord>> entry : affectedProfiles.entrySet()) {
			if (!saveOwners(entry.getKey())) {
				setStatus(entry.getValue(), AccountStatus.FAILED);
				state.persistenceFailure = true;
				state.persistenceFailures++;
				state.stopRemoteRefreshes = true;
				err.println("ERROR profile=" + safeLabel(entry.getKey().getName()) + " operation=save");
			}
		}
	}

	private Map<Profile, List<AccountRecord>> getAffectedProfiles(List<AccountRecord> grant, String refreshToken, boolean success) {
		Map<Profile, List<AccountRecord>> profiles = new LinkedHashMap<>();
		for (AccountRecord account : grant) {
			boolean tokenChanged = !account.originalRefreshToken.equals(refreshToken);
			boolean invalidChanged = success && account.originalInvalid;
			if (!tokenChanged && !invalidChanged) {
				continue;
			}
			List<AccountRecord> affected = profiles.get(account.profile);
			if (affected == null) {
				affected = new ArrayList<>();
				profiles.put(account.profile, affected);
			}
			affected.add(account);
		}
		return profiles;
	}

	private boolean saveOwners(Profile profile) {
		for (int attempt = 0; attempt < 2; attempt++) {
			try {
				if (profileStore.saveOwners(profile)) {
					return true;
				}
			} catch (RuntimeException ex) {
				//Retry once without reporting the potentially secret-bearing exception.
			}
		}
		return false;
	}

	private void restoreGrant(List<AccountRecord> grant) {
		for (AccountRecord account : grant) {
			try {
				account.owner.setAuth(account.callbackURL, account.originalRefreshToken, null);
				account.owner.setInvalid(account.originalInvalid);
			} catch (RuntimeException ex) {
				//The profile is cleared before returning from the command.
			}
		}
	}

	private void finishAccounts(List<AccountRecord> accounts, RunState state) {
		for (AccountRecord account : accounts) {
			if (account.status == null) {
				account.status = AccountStatus.FAILED;
				state.authorizationFailure = true;
			}
		}
	}

	private void clearProfiles(List<Profile> profiles, RunState state) {
		for (Profile profile : profiles) {
			try {
				profileStore.clear(profile);
			} catch (RuntimeException ex) {
				state.authorizationFailure = true;
				err.println("ERROR profile=" + safeLabel(profile.getName()) + " operation=cleanup");
			}
		}
	}

	private void printResults(int profileCount, List<AccountRecord> accounts, int persistenceFailures) {
		int succeeded = 0;
		for (AccountRecord account : accounts) {
			if (account.status == AccountStatus.SUCCEEDED) {
				succeeded++;
				out.println("OK profile=" + safeLabel(account.profile.getName()) + " character=" + safeLabel(account.owner.getOwnerName()));
			} else {
				out.println("FAILED profile=" + safeLabel(account.profile.getName()) + " character=" + safeLabel(account.owner.getOwnerName()));
			}
		}
		out.println("SUMMARY profiles=" + profileCount
				+ " accounts=" + accounts.size()
				+ " succeeded=" + succeeded
				+ " failed=" + (accounts.size() - succeeded)
				+ " persistence_failed=" + persistenceFailures);
	}

	private static void setStatus(List<AccountRecord> accounts, AccountStatus status) {
		for (AccountRecord account : accounts) {
			account.status = status;
		}
	}

	private static String readRefreshToken(EsiOwner owner) {
		try {
			return owner.getRefreshToken();
		} catch (RuntimeException ex) {
			return null;
		}
	}

	private static boolean isNonBlank(String value) {
		return value != null && !value.trim().isEmpty();
	}

	private static int compareStrings(String first, String second) {
		if (first == null && second == null) {
			return 0;
		} else if (first == null) {
			return -1;
		} else if (second == null) {
			return 1;
		} else {
			return first.compareToIgnoreCase(second);
		}
	}

	private static String safeLabel(String value) {
		if (value == null || value.trim().isEmpty()) {
			return "<unknown>";
		}
		return value.replace('\r', ' ').replace('\n', ' ').replace('\t', ' ');
	}

	interface TokenRefresher {
		RefreshResult refresh(EsiOwner owner);
	}

	interface ProfileStore {
		List<Profile> findProfiles();
		boolean load(Profile profile);
		boolean preflightOwners(Profile profile);
		boolean saveOwners(Profile profile);
		void clear(Profile profile);
	}

	static final class RefreshResult {

		private final boolean accessTokenValid;
		private final boolean refreshTokenObserved;
		private final String refreshToken;

		static RefreshResult fromTokens(String accessToken, String refreshToken) {
			return new RefreshResult(isNonBlank(accessToken), true, refreshToken);
		}

		static RefreshResult failure() {
			return new RefreshResult(false, false, null);
		}

		static RefreshResult failureWithRefreshToken(String refreshToken) {
			return new RefreshResult(false, true, refreshToken);
		}

		private RefreshResult(boolean accessTokenValid, boolean refreshTokenObserved, String refreshToken) {
			this.accessTokenValid = accessTokenValid;
			this.refreshTokenObserved = refreshTokenObserved;
			this.refreshToken = refreshToken;
		}

		private RefreshResult withRefreshToken(String refreshToken) {
			return new RefreshResult(accessTokenValid, true, refreshToken);
		}

		boolean isSuccessful() {
			return accessTokenValid && isNonBlank(refreshToken);
		}

	}

	private static final class OAuthTokenRefresher implements TokenRefresher {

		@Override
		public RefreshResult refresh(EsiOwner owner) {
			OAuth oauth = (OAuth) owner.getApiClient().getAuthentication(ApiClientBuilder.AUTHENTICATION);
			oauth.setAccessToken(null);
			String accessToken = oauth.getAccessToken();
			String refreshToken = oauth.getRefreshToken();
			return RefreshResult.fromTokens(accessToken, refreshToken);
		}
	}

	private static final class LocalProfileStore implements ProfileStore {

		private final ProfileManager profileManager = new ProfileManager();

		@Override
		public List<Profile> findProfiles() {
			profileManager.searchProfile();
			List<Profile> profiles = new ArrayList<>(profileManager.getProfiles());
			Collections.sort(profiles);
			return profiles;
		}

		@Override
		public boolean load(Profile profile) {
			profileManager.setActiveProfile(profile);
			return profile.load();
		}

		@Override
		public boolean preflightOwners(Profile profile) {
			profileManager.setActiveProfile(profile);
			return profile.preflightTable(Table.OWNERS);
		}

		@Override
		public boolean saveOwners(Profile profile) {
			profileManager.setActiveProfile(profile);
			return profile.saveTableChecked(Table.OWNERS);
		}

		@Override
		public void clear(Profile profile) {
			profile.clear();
		}
	}

	private static final class AccountRecord {

		private final Profile profile;
		private final EsiOwner owner;
		private final EsiCallbackURL callbackURL;
		private final String originalRefreshToken;
		private final boolean originalInvalid;
		private AccountStatus status;

		private AccountRecord(Profile profile, EsiOwner owner) {
			this.profile = profile;
			this.owner = owner;
			this.callbackURL = owner.getCallbackURL();
			this.originalRefreshToken = readRefreshToken(owner);
			this.originalInvalid = owner.isInvalid();
		}
	}

	private static final class GrantKey {

		private final String clientID;
		private final String callbackURL;
		private final String refreshToken;

		private GrantKey(EsiCallbackURL callbackURL, String refreshToken) {
			this.clientID = callbackURL.getA();
			this.callbackURL = callbackURL.getUrl();
			this.refreshToken = refreshToken;
		}

		private GrantKey withRefreshToken(String newRefreshToken) {
			return new GrantKey(clientID, callbackURL, newRefreshToken);
		}

		private GrantKey(String clientID, String callbackURL, String refreshToken) {
			this.clientID = clientID;
			this.callbackURL = callbackURL;
			this.refreshToken = refreshToken;
		}

		@Override
		public int hashCode() {
			int hash = 5;
			hash = 53 * hash + Objects.hashCode(clientID);
			hash = 53 * hash + Objects.hashCode(callbackURL);
			hash = 53 * hash + Objects.hashCode(refreshToken);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			GrantKey other = (GrantKey) obj;
			return Objects.equals(clientID, other.clientID)
					&& Objects.equals(callbackURL, other.callbackURL)
					&& Objects.equals(refreshToken, other.refreshToken);
		}

	}

	private static final class RunState {
		private boolean authorizationFailure;
		private boolean persistenceFailure;
		private boolean stopRemoteRefreshes;
		private int persistenceFailures;
	}

	private static enum AccountStatus {
		SUCCEEDED,
		FAILED
	}
}
