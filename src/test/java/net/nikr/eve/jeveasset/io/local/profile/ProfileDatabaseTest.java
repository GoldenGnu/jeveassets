/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.local.profile;

import ch.qos.logback.classic.Level;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.nikr.eve.jeveasset.CliOptions;
import net.nikr.eve.jeveasset.TestUtil;
import static net.nikr.eve.jeveasset.TestUtil.initLog;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.profile.Profile;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.AddedData;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Security;
import net.nikr.eve.jeveasset.io.local.ProfileReader;
import net.nikr.eve.jeveasset.io.local.ProfileWriter;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;


public class ProfileDatabaseTest extends TestUtil {

	private static final int REPS = 100;
	private static ProfileManager profileManager;
	private static Profile profile;
	private static boolean portable;

	@BeforeClass
	public static void setUpClass() {
		setLoggingLevel(Level.WARN);
		portable = CliOptions.get().isPortable();
		CliOptions.get().setPortable(false);

		profileManager = new ProfileManager();
		profileManager.searchProfile();
		profileManager.loadActiveProfile(); //Load
		profile = profileManager.getActiveProfile();

		CliOptions.get().setPortable(true);
		profileManager.saveProfile(); //Save to SQLite
		AddedData.load();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		setLoggingLevel(Level.INFO);
		new File(profileManager.getActiveProfile().getSQLiteFilename()).delete();
		new File(profileManager.getActiveProfile().getXmlFilename()).delete();
		new File(profileManager.getActiveProfile().getBackupXmlFilename()).delete();
		CliOptions.get().setPortable(portable);
	}

	/**
	 * SQLite test
	 * @param args
	 * @throws java.lang.InterruptedException
	 */
	public static void main(final String[] args) throws InterruptedException {
		initLog();
		setUpClass();
		ProfileDatabaseTest test = new ProfileDatabaseTest();
		test.testLoad();
		test.testSave();
		System.exit(0);
	}

	@Test
	public void testSave() {
		saveXml();
		saveSQL();
		updateData();
	}

	@Test
	public void testLoad() {
		Profile oldProfile = loadXml();
		Profile newProfile = loadSQL();
		testClass("", oldProfile, newProfile, false);
		updateData();
	}

	private void saveXml() {
		//Save to XML * REPS
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPS; i++) {
			ProfileWriter.save(profile);
		}
		log("Save XML: " +  + ((System.currentTimeMillis() - start) / REPS) + "ms");
	}

	private void saveSQL() {
		//Save to SQLite * REPS
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPS; i++) {
			profileManager.saveProfile();
		}
		log("Save SQLite: " +  + ((System.currentTimeMillis() - start) / REPS) + "ms");
	}

	private void updateData() {
		//EventList update * REPS
		ProfileData profileData = new ProfileData(profileManager);
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPS; i++) {
			profileData.updateEventLists();
		}
		log("Update data " + ((System.currentTimeMillis() - start) / REPS) + "ms");
	}

	private Profile loadXml() {
		//Load XML * REPS
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPS; i++) {
			ProfileReader.load(profileManager.getActiveProfile());
		}
		log("Loading XML " + ((System.currentTimeMillis() - start) / REPS) + "ms");
		return profileManager.getActiveProfile();
	}

	private Profile loadSQL() {
		//Load SQlite * REPS
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPS; i++) {
			profileManager.clear();
			profileManager.loadActiveProfile(); //Load from SQLite
		}
		log("Loading SQLite " + ((System.currentTimeMillis() - start) / REPS) + "ms");
		return profileManager.getActiveProfile();
	}

	private void log(String s) {
		setLoggingLevel(Level.INFO);
		System.out.println(s);
		setLoggingLevel(Level.WARN);
	}

	private void testClass(String msg, Object oldValue, Object newValue, boolean collection) {
		if (oldValue == null || newValue == null) {
			assertEquals(msg, oldValue, newValue);
			return;
		}
		testClass(msg, oldValue.getClass(), newValue.getClass(), oldValue, newValue, collection);
	}

	private void testClass(String input, Class<?> oldClazz, Class<?> newClazz, Object oldValue, Object newValue, boolean collection) {
		if (oldClazz == null || newClazz == null) {
			return;
		}
		String msg = input + oldClazz.getSimpleName() + getValue(oldValue) + getValue(newValue);
		//System.out.println(msg);
		if (oldValue == null || newValue == null || Enum.class.isAssignableFrom(oldValue.getClass())) {
			assertEquals(msg, oldValue, newValue);
		} else if (Collection.class.isAssignableFrom(oldValue.getClass())
				&& Collection.class.isAssignableFrom(newValue.getClass())) {
			Collection<?> oldCollection = (Collection<?>) oldValue;
			Collection<?> newCollection = (Collection<?>) newValue;
			assertEquals(msg, oldCollection.size(), newCollection.size());
			Iterator<?> oldIterator = oldCollection.iterator();
			Iterator<?> newIterator = newCollection.iterator();
			while (oldIterator.hasNext() && newIterator.hasNext()) {
				Object oldObject = oldIterator.next();
				Object newObject = newIterator.next();
				testClass(msg + ">>", oldObject, newObject, true);
			}
			assertFalse(msg, oldIterator.hasNext());
			assertFalse(msg, newIterator.hasNext());
		} else if (Map.class.isAssignableFrom(oldValue.getClass())
				&& Map.class.isAssignableFrom(newValue.getClass())) {
			Map<?,?> oldMap = (Map<?,?>) oldValue;
			Map<?,?> newMap = (Map<?,?>) newValue;
			assertEquals(msg, oldMap.size(), newMap.size());
			Iterator<?> oldIterator = oldMap.entrySet().iterator();
			Iterator<?> newIterator = newMap.entrySet().iterator();
			while (oldIterator.hasNext() && newIterator.hasNext()) {
				Object oldObject = oldIterator.next();
				Object newObject = newIterator.next();
				if (oldObject instanceof Map.Entry<?,?> && newObject instanceof Map.Entry<?,?>) {
					Map.Entry<?,?> oldEntry = (Map.Entry<?,?>) oldObject;
					Map.Entry<?,?> newEntry = (Map.Entry<?,?>) newObject;
					testClass(msg + ">>", oldEntry.getKey(), newEntry.getKey(), true);
					testClass(msg + ">>>", oldEntry.getValue(), newEntry.getValue(), true);
				} else {
					testClass(msg, oldObject, newObject, false);
				}
			}
			assertFalse(msg, oldIterator.hasNext());
			assertFalse(msg, newIterator.hasNext());
		} else if (oldClazz.getName().startsWith("net.nikr.eve.jeveasset") || (Object.class.equals(oldClazz) && collection)){
			for (Field oldField : oldClazz.getDeclaredFields()) {
				try {
					final String fieldName = oldField.getName();
					if ("LOG".equals(fieldName) ||
						(EsiOwner.class.equals(oldClazz)
						&& ("apiClient".equals(fieldName) //Perm
						|| "marketApi".equals(fieldName) //Perm
						|| "industryApi".equals(fieldName) //Perm
						|| "characterApi".equals(fieldName) //Perm
						|| "clonesApi".equals(fieldName) //Perm
						|| "assetsApi".equals(fieldName) //Perm
						|| "walletApi".equals(fieldName) //Perm
						|| "universeApi".equals(fieldName) //Perm
						|| "contractsApi".equals(fieldName) //Perm
						|| "corporationApi".equals(fieldName) //Perm
						|| "locationApi".equals(fieldName) //Perm
						|| "bookmarksApi".equals(fieldName) //Perm
						|| "planetaryInteractionApi".equals(fieldName) //Perm
						|| "userInterfaceApi".equals(fieldName) //Perm
						|| "skillsApi".equals(fieldName) //Perm
						))
						//|| (AbstractOwner.class.equals(oldClazz)	&& ("mining".equals(fieldName) )) //Temp
						|| (MyAsset.class.equals(oldClazz) //Temp
						&& ("owner".equals(fieldName) //Perm
						|| "parents".equals(fieldName) //Temp ? Perm ? 
						|| "priceData".equals(fieldName) //Temp?
						)) || (MyAccountBalance.class.equals(oldClazz)
						&& ("owner".equals(fieldName) //Perm
						)) || (MyJournal.class.equals(oldClazz)
						&& ("owner".equals(fieldName) //Perm
						)) || (MyTransaction.class.equals(oldClazz)
						&& ("owner".equals(fieldName) //Perm
						)) || (MyIndustryJob.class.equals(oldClazz)
						&& ("owner".equals(fieldName) //Perm
						)) || (MyMarketOrder.class.equals(oldClazz)
						&& ("owner".equals(fieldName) //Perm
						|| "priceData".equals(fieldName) //Temp?
						)) || (Profile.class.equals(oldClazz)
						&& ("stockpileIDs".equals(fieldName) //Perm
						)) || (MyLocation.class.equals(oldClazz)
						&& ("CACHE".equals(fieldName) //Perm
						)) || (Security.class.equals(oldClazz)
						&& ("CACHE".equals(fieldName) //Perm
							))) {
						continue;
					}
					Field newField = newClazz.getDeclaredField(fieldName);
					oldField.setAccessible(true);
					newField.setAccessible(true);
					Object oldObject = oldField.get(oldValue);
					Object newObject = newField.get(newValue);
					testClass(msg + "." + fieldName + ">", oldObject, newObject, false);
				} catch (NoSuchFieldException ex) {
					fail(ex.getMessage());
				} catch (SecurityException ex) {
					fail(ex.getMessage());
				} catch (IllegalArgumentException ex) {
					fail(ex.getMessage());
				} catch (IllegalAccessException ex) {
					fail(ex.getMessage());
				}
			}
			testClass(input + "=>", oldClazz.getSuperclass(), newClazz.getSuperclass(), oldValue, newValue, collection);
		} else {
			assertEquals(msg, oldValue, newValue);
		}
	}

	private String getValue(Object object) {
		if (object == null) {
			return "";
		} else if (object instanceof EsiOwner) {
			return "(" + ((EsiOwner) object).getOwnerName() + ")";
		} else if (object instanceof MyContract) {
			return "(" + ((MyContract) object).getTitle() + ")";
		} else if (object instanceof MyContractItem) {
			return "(" + ((MyContractItem) object).getContract().getTitle() + ")";
		}
		return "";
	}

}
