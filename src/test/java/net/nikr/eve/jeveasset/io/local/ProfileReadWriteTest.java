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
package net.nikr.eve.jeveasset.io.local;

import ch.qos.logback.classic.Level;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.Profile;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.settings.AddedData;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptions;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptionsGetter;
import net.nikr.eve.jeveasset.io.shared.ConverterTestUtil;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;


public class ProfileReadWriteTest extends TestUtil {

	@BeforeClass
	public static void setUpClass() {
		setLoggingLevel(Level.WARN);
	}

	@AfterClass
	public static void tearDownClass() {
		setLoggingLevel(Level.INFO);
	}

	@Test
	public void testNotNull() {
		test(false);
	}

	@Test
	public void testNull() {
		test(true);
	}

	private void test(boolean setNull) {
		AddedData.load();
		
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			Profile saveProfile = new Profile();
			//ESI
			saveProfile.getEsiOwners().add(ConverterTestUtil.getEsiOwner(true, setNull, false, options));

			//Write
			saveProfile.save();

			//Read
			ProfileManager loadProfileManager = new ProfileManager();

			Profile loadProfile = new Profile();
			loadProfile.load();
			loadProfileManager.setActiveProfile(loadProfile);

			//Update dynamic data
			ProfileData profileData = new ProfileData(loadProfileManager);
			profileData.updateEventLists();

			//ESI
			assertEquals(1, loadProfile.getEsiOwners().size());
			EsiOwner esiOwner = loadProfile.getEsiOwners().get(0);
			ConverterTestUtil.testOwner(esiOwner, setNull, options);
		}
	}

}
