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
package net.nikr.eve.jeveasset.io.shared;

import ca.odell.glazedlists.GlazedLists;
import ch.qos.logback.classic.Level;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.nikr.eve.jeveasset.CliOptions;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.profile.Profile;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Security;
import net.nikr.eve.jeveasset.io.local.profile.ProfileDatabase;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class ProfileDatabaseConverterTest extends TestUtil {

	private static final ObjectComparator COMPARATOR = new ObjectComparator();

	@BeforeClass
	public static void setUpClass() {
		setLoggingLevel(Level.WARN);
	}

	@AfterClass
	public static void tearDownClass() {
		setLoggingLevel(Level.INFO);
	}

	@After
	public void testCleanup() {
		cleanup();
	}

	@Test
	public void testLocal() {
		//Exiting data
		CliOptions.get().setPortable(false);
		ProfileManager manager = new ProfileManager();
		manager.searchProfile();
		manager.loadActiveProfile();
		Profile oldProfile = manager.getActiveProfile();
		CliOptions.get().setPortable(true);
		manager.saveProfile();
		//Loaded data
		manager = new ProfileManager();
		manager.searchProfile();
		manager.loadActiveProfile();
		Profile newProfile = manager.getActiveProfile();

		testClass("", oldProfile, newProfile, false);
	}

	@Test
	public void testSave() {
		//Exiting data
		boolean data = true;
		boolean setNull = false;
		boolean setValues = true;
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			//Generated data
			Profile oldProfile = new Profile();
			oldProfile.getEsiOwners().add(ConverterTestUtil.getEsiOwner(data, setNull, setValues, options));
			oldProfile.save();
			//Loaded data
			Profile newProfile = new Profile();
			newProfile.load();
			//Test
			testClass("", newProfile, newProfile, false);
		}
	}

	@Test
	public void testUpdate() {
		boolean data = false;
		boolean setNull = false;
		boolean setValues = true;
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			//Empty data
			Profile oldProfile = new Profile();
			ProfileDatabase.setUpdateConnectionUrl(oldProfile);
			EsiOwner owner = ConverterTestUtil.getEsiOwner(data, setNull, setValues, options);
			owner.setScopes(options.getString());
			oldProfile.getEsiOwners().add(owner);
			oldProfile.save();

			//Contract
			Map<MyContract, List<MyContractItem>> contracts = DataConverter.convertRawContracts(Collections.singletonList(ConverterTestUtil.getRawContract(setNull, options)), owner, true);
			MyContract contract = contracts.entrySet().iterator().next().getKey();
			owner.setContracts(DataConverter.convertRawContractItems(Collections.singletonMap(contract, Collections.singletonList(ConverterTestUtil.getRawContractItem(setNull, options))), owner, true));

			//Industry Job
			owner.setIndustryJobs(DataConverter.convertRawIndustryJobs(Collections.singletonList(ConverterTestUtil.getRawIndustryJob(setNull, options)), owner, true));

			//Journal
			owner.setJournal(DataConverter.convertRawJournals(Collections.singletonList(ConverterTestUtil.getRawJournal(setNull, options)), owner, true));

			//Market Order
			owner.setMarketOrders(DataConverter.convertRawMarketOrders(Collections.singletonList(ConverterTestUtil.getRawMarketOrder(setNull, options)), owner, true));

			//Transaction
			owner.setTransactions(DataConverter.convertRawTransactions(Collections.singletonList(ConverterTestUtil.getRawTransaction(setNull, options)), owner, true));

			//Mining
			owner.setMining(DataConverter.convertRawMining(Collections.singletonList(ConverterTestUtil.getRawMining(setNull, options)), owner, true));

			//Extractions
			owner.setExtractions(DataConverter.convertRawExtraction(Collections.singletonList(ConverterTestUtil.getRawExtraction(setNull, options)), owner, true));

			ProfileDatabase.waitForUpdates();

			//Loaded data
			Profile newProfile = new Profile();
			ProfileDatabase.setUpdateConnectionUrl(newProfile);
			newProfile.load();

			testClass("", oldProfile, newProfile, false);
		}
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
		if (oldValue == null || newValue == null || Enum.class.isAssignableFrom(oldValue.getClass())) {
			assertEquals(msg, oldValue, newValue);
		} else if (List.class.isAssignableFrom(oldValue.getClass()) && List.class.isAssignableFrom(newValue.getClass())) {
			List<?> oldList = (List<?>) oldValue;
			List<?> newList = (List<?>) newValue;
			assertEquals(msg, oldList.size(), newList.size());
			Collections.sort(oldList, COMPARATOR);
			Collections.sort(newList, COMPARATOR);
			if (!oldList.isEmpty()) {
				
			}
			Iterator<?> oldIterator = oldList.iterator();
			Iterator<?> newIterator = newList.iterator();
			while (oldIterator.hasNext() && newIterator.hasNext()) {
				Object oldObject = oldIterator.next();
				Object newObject = newIterator.next();
				testClass(msg + "[List]>", oldObject, newObject, true);
			}
			assertFalse(msg, oldIterator.hasNext());
			assertFalse(msg, newIterator.hasNext());
		} else if (Collection.class.isAssignableFrom(oldValue.getClass()) && Collection.class.isAssignableFrom(newValue.getClass())) {
			Collection<?> oldCollection = (Collection<?>) oldValue;
			Collection<?> newCollection = (Collection<?>) newValue;
			assertEquals(msg, oldCollection.size(), newCollection.size());
			Iterator<?> oldIterator = oldCollection.iterator();
			Iterator<?> newIterator = newCollection.iterator();
			while (oldIterator.hasNext() && newIterator.hasNext()) {
				Object oldObject = oldIterator.next();
				Object newObject = newIterator.next();
				testClass(msg + "[Collection]>", oldObject, newObject, true);
			}
			assertFalse(msg, oldIterator.hasNext());
			assertFalse(msg, newIterator.hasNext());
		} else if (Map.class.isAssignableFrom(oldValue.getClass()) && Map.class.isAssignableFrom(newValue.getClass())) {
			Map<?,?> oldMap = new TreeMap<>((Map<?,?>) oldValue);
			Map<?,?> newMap = new TreeMap<>((Map<?,?>) newValue);
			assertEquals(msg, oldMap.size(), newMap.size());
			Iterator<?> oldIterator = oldMap.entrySet().iterator();
			Iterator<?> newIterator = newMap.entrySet().iterator();
			while (oldIterator.hasNext() && newIterator.hasNext()) {
				Object oldObject = oldIterator.next();
				Object newObject = newIterator.next();
				if (oldObject instanceof Map.Entry<?,?> && newObject instanceof Map.Entry<?,?>) {
					Map.Entry<?,?> oldEntry = (Map.Entry<?,?>) oldObject;
					Map.Entry<?,?> newEntry = (Map.Entry<?,?>) newObject;
					testClass(msg + "[Map>Key]>", oldEntry.getKey(), newEntry.getKey(), true);
					testClass(msg + "[Map>Value]>", oldEntry.getValue(), newEntry.getValue(), true);
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
						"$jacocoData".equals(fieldName)
						|| (EsiOwner.class.equals(oldClazz)
							&& ("apiClient".equals(fieldName)
							|| "marketApi".equals(fieldName)
							|| "industryApi".equals(fieldName)
							|| "characterApi".equals(fieldName)
							|| "clonesApi".equals(fieldName)
							|| "assetsApi".equals(fieldName)
							|| "walletApi".equals(fieldName)
							|| "universeApi".equals(fieldName)
							|| "contractsApi".equals(fieldName)
							|| "corporationApi".equals(fieldName)
							|| "locationApi".equals(fieldName)
							|| "planetaryInteractionApi".equals(fieldName)
							|| "userInterfaceApi".equals(fieldName)
							|| "skillsApi".equals(fieldName)))
						|| (MyAsset.class.equals(oldClazz)
							&& ("owner".equals(fieldName)
							|| "parents".equals(fieldName)
							|| "priceData".equals(fieldName)))
						|| (MyAccountBalance.class.equals(oldClazz)
							&& "owner".equals(fieldName))
						|| (MyJournal.class.equals(oldClazz)
							&& "owner".equals(fieldName))
						|| (MyTransaction.class.equals(oldClazz)
							&& "owner".equals(fieldName))
						|| (MyIndustryJob.class.equals(oldClazz)
							&& ("owner".equals(fieldName)
							|| "owned".equals(fieldName)))
						|| (MyMarketOrder.class.equals(oldClazz)
							&& ("owner".equals(fieldName)
							|| "priceData".equals(fieldName)
							|| "jButton".equals(fieldName)))
						|| (Profile.class.equals(oldClazz)
							&& "stockpileIDs".equals(fieldName))
						|| (MyLocation.class.equals(oldClazz)
							&& "CACHE".equals(fieldName))
						|| (MyContractItem.class.equals(oldClazz)
							&& "contract".equals(fieldName))
						|| (MyContract.class.equals(oldClazz)
							&& ("endLocation".equals(fieldName)
							|| "startLocation".equals(fieldName)))
						|| (RawMarketOrder.class.equals(oldClazz)
							&& "regionId".equals(fieldName))
						|| (Security.class.equals(oldClazz)
							&& "CACHE".equals(fieldName))
						|| (Percent.class.equals(oldClazz)
							&& "CACHE".equals(fieldName))
						) {
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

	private static class ObjectComparator implements Comparator<Object> {

		@Override
		public int compare(Object o1, Object o2) {
			if (o1 instanceof MyAsset && o2 instanceof MyAsset) {
				return Long.compare(((MyAsset)o1).getItemID(), ((MyAsset)o2).getItemID());
			}
			if (o1 instanceof Comparable && o2 instanceof Comparable) {
				return GlazedLists.comparableComparator().compare((Comparable) o1, (Comparable) o2);
			}
			return 0;
		}

	}
}
