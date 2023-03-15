/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

import java.io.File;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.profile.Profile;
import net.nikr.eve.jeveasset.data.profile.Profile.DefaultProfile;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.settings.AddedData;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptions;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptionsGetter;
import net.nikr.eve.jeveasset.io.shared.ConverterTestUtil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class ProfileReadWriteTest extends TestUtil {

	private static final String FILENAME = "target" + File.separator + "profile_read_write_test.xml";

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
			Profile saveProfile = new DefaultProfile();
			//ESI
			saveProfile.getEsiOwners().add(ConverterTestUtil.getEsiOwner(true, setNull, false, options));
			//EveAPI
			EveApiOwner saveEveApiOwner = ConverterTestUtil.getEveApiOwner(true, setNull, false, options);
			saveEveApiOwner.getParentAccount().getOwners().add(saveEveApiOwner);
			saveProfile.getAccounts().add(saveEveApiOwner.getParentAccount());
			//EveKit
			saveProfile.getEveKitOwners().add(ConverterTestUtil.getEveKitOwner(true, setNull, false, options));

			//Write
			ProfileWriter.save(saveProfile, FILENAME);

			//Read
			ProfileManager loadProfile = new ProfileManager();
			ProfileReader.load(loadProfile.getActiveProfile(), FILENAME);

			//Update dynamic data
			ProfileData profileData = new ProfileData(loadProfile);
			profileData.updateEventLists();

			//ESI
			assertEquals(1, loadProfile.getEsiOwners().size());
			EsiOwner esiOwner = loadProfile.getEsiOwners().get(0);
			ConverterTestUtil.testOwner(esiOwner, setNull, options);
			//EveAPI
			assertEquals(1, loadProfile.getAccounts().size());
			EveApiAccount loadEveApiAccount = loadProfile.getAccounts().get(0);
			ConverterTestUtil.testValues(loadEveApiAccount, options);
			assertEquals(1, loadEveApiAccount.getOwners().size());
			EveApiOwner eveApiOwner = loadEveApiAccount.getOwners().get(0);
			ConverterTestUtil.testOwner(eveApiOwner, setNull, options);
			//EveKit
			assertEquals(1, loadProfile.getEveKitOwners().size());
			EveKitOwner eveKitOwner = loadProfile.getEveKitOwners().get(0);
			ConverterTestUtil.testOwner(eveKitOwner, setNull, options);

			//Clean up
			File file = new File(FILENAME);
			assertTrue(file.delete());

			File backupFile = new File(FILENAME.substring(0, FILENAME.lastIndexOf(".")) + "_" + Program.PROGRAM_VERSION.replace(" ", "_") + "_backup.zip");
			assertTrue(backupFile.delete());
		}
	}

}
