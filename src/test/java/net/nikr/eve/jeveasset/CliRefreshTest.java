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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.CliRefresh.ProfileStore;
import net.nikr.eve.jeveasset.CliRefresh.RefreshResult;
import net.nikr.eve.jeveasset.CliRefresh.TokenRefresher;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.Profile;
import net.nikr.eve.jeveasset.data.profile.Profile.ProfileType;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class CliRefreshTest {

	private static int secretID;

	private ByteArrayOutputStream stdout;
	private ByteArrayOutputStream stderr;

	@Before
	public void setUp() {
		stdout = new ByteArrayOutputStream();
		stderr = new ByteArrayOutputStream();
	}

	@Test
	public void rotationIsPersistedBeforeNextGrantWithStableSecretSafeOutput() {
		String oldRefresh = secret("refresh-old");
		String newRefresh = secret("refresh-new");
		String unchangedRefresh = secret("refresh-unchanged");
		String firstAccess = secret("access-first");
		String secondAccess = secret("access-second");
		EsiOwner alice = owner("alice", "Alice", oldRefresh, true);
		Profile profile = profile("Default",
				owner("bob", "Bob", unchangedRefresh, false),
				alice);
		List<String> events = new ArrayList<>();
		FakeProfileStore store = new FakeProfileStore(events, profile);
		FakeTokenRefresher refresher = new FakeTokenRefresher(events);
		refresher.result("alice", RefreshResult.fromTokens(firstAccess, newRefresh));
		refresher.result("bob", RefreshResult.fromTokens(secondAccess, unchangedRefresh));

		int exitCode = run(store, refresher);

		assertNoSecrets(oldRefresh, newRefresh, unchangedRefresh, firstAccess, secondAccess);
		assertEquals(0, exitCode);
		assertEquals("OK profile=Default character=Alice" + System.lineSeparator()
				+ "OK profile=Default character=Bob" + System.lineSeparator()
				+ "SUMMARY profiles=1 accounts=2 succeeded=2 failed=0 persistence_failed=0" + System.lineSeparator(), output());
		assertEquals("", errorOutput());
		assertEquals(newRefresh, alice.getRefreshToken());
		assertFalse(alice.isInvalid());
		assertEquals(1, store.saveCount(profile));
		assertTrue(events.indexOf("remote:alice") < events.indexOf("save:Default"));
		assertTrue(events.indexOf("save:Default") < events.indexOf("remote:bob"));
		assertEquals(Arrays.asList(profile), store.clearedProfiles);
	}

	@Test
	public void rotationSurvivesRuntimeFailureAndIsNotCachedAsSuccess() {
		String oldRefresh = secret("refresh-failed-old");
		String newRefresh = secret("refresh-failed-new");
		String access = secret("access-replacement");
		String rawError = secret("raw-error");
		EsiOwner failedOwner = owner("old", "Alice", oldRefresh, true);
		Profile failedProfile = profile("First", failedOwner);
		Profile replacementProfile = profile("Second", owner("new", "Bob", newRefresh, false));
		List<String> events = new ArrayList<>();
		FakeProfileStore store = new FakeProfileStore(events, failedProfile, replacementProfile);
		FakeTokenRefresher refresher = new FakeTokenRefresher(events);
		refresher.runtimeFailureAfterRotation("old", EsiCallbackURL.LOCALHOST, newRefresh, rawError);
		refresher.result("new", RefreshResult.fromTokens(access, newRefresh));

		int exitCode = run(store, refresher);

		assertNoSecrets(oldRefresh, newRefresh, access, rawError);
		assertEquals(CliRefresh.EXIT_AUTHORIZATION_FAILURE, exitCode);
		assertEquals(Arrays.asList("old", "new"), refresher.calls);
		assertEquals(newRefresh, failedOwner.getRefreshToken());
		assertTrue(failedOwner.isInvalid());
		assertEquals(1, store.saveCount(failedProfile));
		assertEquals(0, store.saveCount(replacementProfile));
		assertEquals("FAILED profile=First character=Alice" + System.lineSeparator()
				+ "OK profile=Second character=Bob" + System.lineSeparator()
				+ "SUMMARY profiles=2 accounts=2 succeeded=1 failed=1 persistence_failed=0" + System.lineSeparator(), output());
	}

	@Test
	public void blankReplacementRestoresOriginalAndStopsRemoteRefreshes() {
		String oldRefresh = secret("refresh-old-blank");
		String secondRefresh = secret("refresh-second-blank");
		String access = secret("access-blank");
		EsiOwner first = owner("first", "Alice", oldRefresh, true);
		Profile profile = profile("Default", first, owner("second", "Bob", secondRefresh, false));
		List<String> events = new ArrayList<>();
		FakeProfileStore store = new FakeProfileStore(events, profile);
		FakeTokenRefresher refresher = new FakeTokenRefresher(events);
		refresher.result("first", RefreshResult.fromTokens(access, " "));
		refresher.result("second", RefreshResult.fromTokens(access, secondRefresh));

		int exitCode = run(store, refresher);

		assertNoSecrets(oldRefresh, secondRefresh, access);
		assertEquals(CliRefresh.EXIT_AUTHORIZATION_FAILURE, exitCode);
		assertEquals(Arrays.asList("first"), refresher.calls);
		assertEquals(oldRefresh, first.getRefreshToken());
		assertTrue(first.isInvalid());
		assertEquals(0, store.saveCount(profile));
	}

	@Test
	public void preflightFailureMakesNoRemoteRequestAndCleansUp() {
		String refresh = secret("refresh-preflight-failure");
		Profile profile = profile("Default", owner("one", "Alice", refresh, false));
		List<String> events = new ArrayList<>();
		FakeProfileStore store = new FakeProfileStore(events, profile);
		store.failPreflight(profile);
		FakeTokenRefresher refresher = new FakeTokenRefresher(events);

		int exitCode = run(store, refresher);

		assertNoSecrets(refresh);
		assertEquals(CliRefresh.EXIT_PERSISTENCE_FAILURE, exitCode);
		assertTrue(refresher.calls.isEmpty());
		assertEquals(1, store.preflightCount(profile));
		assertEquals(Arrays.asList(profile), store.clearedProfiles);
	}

	@Test
	public void unrecoverableSaveFailureRetriesLocallyAndStopsRemoteRefreshes() {
		String firstOld = secret("refresh-first-old-save-failure");
		String firstNew = secret("refresh-first-new-save-failure");
		String secondRefresh = secret("refresh-second-save-failure");
		String access = secret("access-save-failure");
		String rawError = secret("save-error");
		Profile profile = profile("Default",
				owner("first", "Alice", firstOld, false),
				owner("second", "Bob", secondRefresh, false));
		List<String> events = new ArrayList<>();
		FakeProfileStore store = new FakeProfileStore(events, profile);
		store.saveResults(profile, new RuntimeException(rawError), false);
		FakeTokenRefresher refresher = new FakeTokenRefresher(events);
		refresher.result("first", RefreshResult.fromTokens(access, firstNew));
		refresher.result("second", RefreshResult.fromTokens(access, secondRefresh));

		int exitCode = run(store, refresher);

		assertNoSecrets(firstOld, firstNew, secondRefresh, access, rawError);
		assertEquals(CliRefresh.EXIT_PERSISTENCE_FAILURE, exitCode);
		assertEquals(Arrays.asList("first"), refresher.calls);
		assertEquals(2, store.saveCount(profile));
		assertTrue(output().contains("succeeded=0 failed=2 persistence_failed=1"));
	}

	@Test
	public void grantIdentityDeduplicatesOnlyEquivalentOldAndRotatedTokens() {
		String oldRefresh = secret("refresh-cache-old");
		String newRefresh = secret("refresh-cache-new");
		String otherRefresh = secret("refresh-cache-other");
		String access = secret("access-cache");
		EsiOwner firstOwner = owner("first", "Alice", oldRefresh, false);
		EsiOwner duplicateOwner = owner("duplicate", "Bob", oldRefresh, false);
		Profile first = profile("First", firstOwner);
		Profile duplicate = profile("Duplicate", duplicateOwner);
		Profile replacement = profile("Replacement", owner("replacement", "Carol", newRefresh, false));
		Profile otherToken = profile("Other Token", owner("other", "Alice", otherRefresh, false));
		Profile otherCallback = profile("Other Callback",
				owner("callback", "Alice", oldRefresh, false, EsiCallbackURL.EVE_NIKR_NET));
		List<String> events = new ArrayList<>();
		FakeProfileStore store = new FakeProfileStore(events, first, duplicate, replacement, otherToken, otherCallback);
		FakeTokenRefresher refresher = new FakeTokenRefresher(events);
		refresher.result("first", RefreshResult.fromTokens(access, newRefresh));
		refresher.result("other", RefreshResult.fromTokens(access, otherRefresh));
		refresher.result("callback", RefreshResult.fromTokens(access, oldRefresh));

		int exitCode = run(store, refresher);

		assertNoSecrets(oldRefresh, newRefresh, otherRefresh, access);
		assertEquals(0, exitCode);
		assertEquals(Arrays.asList("first", "other", "callback"), refresher.calls);
		assertEquals(newRefresh, firstOwner.getRefreshToken());
		assertEquals(newRefresh, duplicateOwner.getRefreshToken());
		assertEquals(1, store.saveCount(first));
		assertEquals(1, store.saveCount(duplicate));
		assertEquals(0, store.saveCount(replacement));
		assertEquals(0, store.saveCount(otherToken));
		assertEquals(0, store.saveCount(otherCallback));
	}

	private int run(FakeProfileStore store, FakeTokenRefresher refresher) {
		PrintStream out = new PrintStream(stdout, true);
		PrintStream err = new PrintStream(stderr, true);
		return new CliRefresh(store, refresher, out, err).refresh();
	}

	private String output() {
		return new String(stdout.toByteArray(), StandardCharsets.UTF_8);
	}

	private String errorOutput() {
		return new String(stderr.toByteArray(), StandardCharsets.UTF_8);
	}

	private void assertNoSecrets(String... secrets) {
		String output = output();
		String errors = errorOutput();
		for (String secret : secrets) {
			assertFalse("stdout contained secret material", output.contains(secret));
			assertFalse("stderr contained secret material", errors.contains(secret));
		}
	}

	private static String secret(String label) {
		secretID++;
		return "test-private-" + label + "-" + secretID;
	}

	private static Profile profile(String name, EsiOwner... owners) {
		Profile profile = new Profile(name, false, false, ProfileType.SQLITE);
		profile.getEsiOwners().addAll(Arrays.asList(owners));
		return profile;
	}

	private static EsiOwner owner(String accountID, String character, String refreshToken, boolean invalid) {
		return owner(accountID, character, refreshToken, invalid, EsiCallbackURL.LOCALHOST);
	}

	private static EsiOwner owner(String accountID, String character, String refreshToken, boolean invalid, EsiCallbackURL callbackURL) {
		EsiOwner owner = new EsiOwner(accountID);
		owner.setOwnerName(character);
		owner.setAuth(callbackURL, refreshToken, null);
		owner.setInvalid(invalid);
		return owner;
	}

	private static class FakeTokenRefresher implements TokenRefresher {

		private final List<String> events;
		private final Map<String, Object> results = new HashMap<>();
		private final List<String> calls = new ArrayList<>();

		private FakeTokenRefresher(List<String> events) {
			this.events = events;
		}

		private void result(String accountID, RefreshResult result) {
			results.put(accountID, result);
		}

		private void runtimeFailureAfterRotation(String accountID, EsiCallbackURL callbackURL, String refreshToken, String message) {
			results.put(accountID, new RotationFailure(callbackURL, refreshToken, message));
		}

		@Override
		public RefreshResult refresh(EsiOwner owner) {
			String accountID = owner.getAccountID();
			calls.add(accountID);
			events.add("remote:" + accountID);
			Object result = results.get(accountID);
			if (result instanceof RotationFailure) {
				RotationFailure failure = (RotationFailure) result;
				owner.setAuth(failure.callbackURL, failure.refreshToken, null);
				throw failure.exception;
			} else if (result instanceof RefreshResult) {
				return (RefreshResult) result;
			} else {
				return RefreshResult.failure();
			}
		}
	}

	private static class RotationFailure {

		private final EsiCallbackURL callbackURL;
		private final String refreshToken;
		private final RuntimeException exception;

		private RotationFailure(EsiCallbackURL callbackURL, String refreshToken, String message) {
			this.callbackURL = callbackURL;
			this.refreshToken = refreshToken;
			this.exception = new RuntimeException(message);
		}
	}

	private static class FakeProfileStore implements ProfileStore {

		private final List<String> events;
		private final List<Profile> profiles;
		private final Map<Profile, Boolean> preflightResults = new HashMap<>();
		private final Map<Profile, List<Object>> saveResults = new HashMap<>();
		private final Map<Profile, Integer> saveCounts = new HashMap<>();
		private final Map<Profile, Integer> preflightCounts = new HashMap<>();
		private final List<Profile> clearedProfiles = new ArrayList<>();

		private FakeProfileStore(List<String> events, Profile... profiles) {
			this.events = events;
			this.profiles = Arrays.asList(profiles);
		}

		private void failPreflight(Profile profile) {
			preflightResults.put(profile, Boolean.FALSE);
		}

		private void saveResults(Profile profile, Object... results) {
			saveResults.put(profile, new ArrayList<>(Arrays.asList(results)));
		}

		private int saveCount(Profile profile) {
			Integer count = saveCounts.get(profile);
			return count == null ? 0 : count;
		}

		private int preflightCount(Profile profile) {
			Integer count = preflightCounts.get(profile);
			return count == null ? 0 : count;
		}

		@Override
		public List<Profile> findProfiles() {
			events.add("find");
			return new ArrayList<>(profiles);
		}

		@Override
		public boolean load(Profile profile) {
			events.add("load:" + profile.getName());
			return true;
		}

		@Override
		public boolean preflightOwners(Profile profile) {
			events.add("preflight:" + profile.getName());
			Integer count = preflightCounts.get(profile);
			preflightCounts.put(profile, count == null ? 1 : count + 1);
			Boolean result = preflightResults.get(profile);
			return result == null || result;
		}

		@Override
		public boolean saveOwners(Profile profile) {
			events.add("save:" + profile.getName());
			int count = saveCount(profile);
			saveCounts.put(profile, count + 1);
			List<Object> results = saveResults.get(profile);
			if (results == null || count >= results.size()) {
				return true;
			}
			Object result = results.get(count);
			if (result instanceof RuntimeException) {
				throw (RuntimeException) result;
			}
			return Boolean.TRUE.equals(result);
		}

		@Override
		public void clear(Profile profile) {
			events.add("clear:" + profile.getName());
			clearedProfiles.add(profile);
		}
	}
}
