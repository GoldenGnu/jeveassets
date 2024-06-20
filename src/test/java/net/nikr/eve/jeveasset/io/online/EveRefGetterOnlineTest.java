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
package net.nikr.eve.jeveasset.io.online;

import ch.qos.logback.classic.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.CliOptions;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.io.esi.EsiItemsGetter;
import net.nikr.eve.jeveasset.io.online.EveRefGetter.EveRefActivity;
import net.nikr.eve.jeveasset.io.online.EveRefGetter.EveRefBlueprint;
import net.nikr.eve.jeveasset.io.online.EveRefGetter.EveRefType;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.BeforeClass;


public class EveRefGetterOnlineTest extends TestUtil {

	private static final long MAX_RUNS = 500;
	private static final double DELTA = 0.0000001;

	@BeforeClass
	public static void setUpClass() {
		setLoggingLevel(Level.ERROR);
		CliOptions.get().setPortable(true);
	}

	@AfterClass
	public static void tearDownClass() {
		setLoggingLevel(Level.INFO);
	}

	/**
	 * Download test
	 * @param args
	 * @throws java.lang.InterruptedException
	 */
	public static void main(final String[] args) throws InterruptedException {
		initLog();
		setUpClass();
		EveRefGetterOnlineTest test = new EveRefGetterOnlineTest();
		test.testDownloadItemsThreads();
		System.exit(0);
	}

	@Test
	public void testDownloadItemsThreads() throws InterruptedException {
		List<Download> updates = new ArrayList<>();
		Set<Integer> typeIDs = new HashSet<>();
		List<Integer> all = new ArrayList<>(StaticData.get().getItems().keySet());
		if (MAX_RUNS > 0) {
			Collections.shuffle(all); //Randomize
			typeIDs.addAll(all.subList(0, (int)Math.min(MAX_RUNS, all.size())));
		} else {
			typeIDs.addAll(all);
		}
		for (int typeID : typeIDs) {
			updates.add(new Download(typeID));
		}
		//Download
		ThreadWoker.start(null, updates, false);
		//Test
		for (Download download : updates) {
			download.test();
		}
	}

	private static class Download implements Runnable {

		private final int typeID;
		private final Item itemSDE;
		private Item itemEveRef;

		public Download(int typeID) {
			this.typeID = typeID;
			this.itemSDE = ApiIdConverter.getItem(typeID);
		}

		@Override
		public void run() {
			EsiItemsGetter esiItemsGetter = new EsiItemsGetter(typeID);
			esiItemsGetter.run();
			itemEveRef = esiItemsGetter.getItem();
		}

		public void test() {
			testItem(itemSDE, itemEveRef);
		}

	}

	@Test
	public void testMultiBlueprints() {
		test(22925);
		test(37161);
	}

	public void test(final int typeID) {
		Item itemSDE = ApiIdConverter.getItem(typeID);
		EsiItemsGetter esiItemsGetter = new EsiItemsGetter(typeID);
		esiItemsGetter.run();
		Item itemEveRef = esiItemsGetter.getItem();
		testItem(itemSDE, itemEveRef);
	}

	private static void testItem(Item itemSDE, Item itemEveRef) {
		String msg = "TypeID: " + itemSDE.getTypeID();
		assertNotNull(msg, itemSDE);
		assertNotNull(msg, itemEveRef);
		assertEquals(msg, itemSDE.getTypeID(), itemEveRef.getTypeID());
		if (itemEveRef.getPriceBase() == 0 || itemEveRef.getPriceBase() == -1) {
			assertTrue(msg, itemSDE.getPriceBase() == 0 || itemSDE.getPriceBase() == -1);
		} else {
			assertEquals(msg, itemSDE.getPriceBase(), itemEveRef.getPriceBase(), DELTA);
		}
		assertEquals(msg, itemSDE.getVolume(), itemEveRef.getVolume(), DELTA);
		assertEquals(msg, itemSDE.getVolumePackaged(), itemEveRef.getVolumePackaged(), DELTA);
		assertEquals(msg, itemSDE.getCapacity(), itemEveRef.getCapacity(), DELTA);
		assertEquals(msg, itemSDE.getMeta(), itemEveRef.getMeta());
		if (itemSDE.getTypeID() != 54383) {
			assertEquals(msg, itemSDE.isMarketGroup(), itemEveRef.isMarketGroup());
		}
		assertEquals(msg, itemSDE.isPiMaterial(), itemEveRef.isPiMaterial());
		assertEquals(msg, itemSDE.getPortion(), itemEveRef.getPortion());
		assertEquals(msg, itemSDE.getProductTypeID(), itemEveRef.getProductTypeID());
		testLists(msg, itemSDE.getBlueprintTypeIDs(), itemEveRef.getBlueprintTypeIDs(), new Tester<Integer>() {
			@Override
			public void test(Integer sde, Integer eveRef) {
				assertEquals(msg, sde, eveRef);
			}
		});
		assertEquals(msg, itemSDE.getProductQuantity(), itemEveRef.getProductQuantity());
		assertEquals(msg, itemSDE.isBlueprint(), itemEveRef.isBlueprint());
		assertEquals(msg, itemSDE.isFormula(), itemEveRef.isFormula());
		/*
		//Don't test dynamic values
		assertEquals(msg, itemSDE.getPriceReprocessed(), itemEveRef.getPriceReprocessed(), DELTA);
		assertEquals(msg, itemSDE.getPriceReprocessedMax(), itemEveRef.getPriceReprocessedMax(), DELTA);
		assertEquals(msg, itemSDE.getPriceManufacturing(), itemEveRef.getPriceManufacturing(), DELTA);
		*/
		if (itemSDE.getTypeName().endsWith(".type")) {
			System.out.println("Ignorering: " + itemSDE.getTypeName());
		} else {
			assertEquals(msg, itemSDE.getTypeName(), itemEveRef.getTypeName());
		}
		assertEquals(msg, itemSDE.getGroup(), itemEveRef.getGroup());
		assertEquals(msg, itemSDE.getCategory(), itemEveRef.getCategory());
		assertEquals(msg, itemSDE.getTech(), itemEveRef.getTech());
		assertEquals(msg, null, itemSDE.getVersion());
		assertEquals(msg, itemEveRef.getVersion(), EsiItemsGetter.ESI_ITEM_VERSION);
		assertEquals(msg, itemSDE.getSlot(), itemEveRef.getSlot());
		assertEquals(msg, itemSDE.getChargeSize(), itemEveRef.getChargeSize());
		testLists(msg, itemSDE.getReprocessedMaterial(), itemEveRef.getReprocessedMaterial(), new Comparator<ReprocessedMaterial>() {
			@Override
			public int compare(ReprocessedMaterial o1, ReprocessedMaterial o2) {
				return Integer.compare(o1.getTypeID(), o2.getTypeID());
			}
		} , new Tester<ReprocessedMaterial>() {
			@Override
			public void test(ReprocessedMaterial sde, ReprocessedMaterial eveRef) {
				assertEquals(msg, sde.getTypeID(), eveRef.getTypeID());
				assertEquals(msg, sde.getQuantity(), eveRef.getQuantity());
				assertEquals(msg, sde.getPortionSize(), eveRef.getPortionSize());
			}
		});
		testLists(msg, itemSDE.getManufacturingMaterials(), itemEveRef.getManufacturingMaterials(), new Comparator<IndustryMaterial>() {
			@Override
			public int compare(IndustryMaterial o1, IndustryMaterial o2) {
				return Integer.compare(o1.getTypeID(), o2.getTypeID());
			}
		} , new Tester<IndustryMaterial>() {
			@Override
			public void test(IndustryMaterial sde, IndustryMaterial eveRef) {
				assertEquals(msg, sde.getTypeID(), eveRef.getTypeID());
				assertEquals(msg, sde.getQuantity(), eveRef.getQuantity());
			}
		});
		testLists(msg, itemSDE.getReactionMaterials(), itemEveRef.getReactionMaterials(), new Comparator<IndustryMaterial>() {
			@Override
			public int compare(IndustryMaterial o1, IndustryMaterial o2) {
				return Integer.compare(o1.getTypeID(), o2.getTypeID());
			}
		} , new Tester<IndustryMaterial>() {
			@Override
			public void test(IndustryMaterial sde, IndustryMaterial eveRef) {
				assertEquals(msg, sde.getTypeID(), eveRef.getTypeID());
				assertEquals(msg, sde.getQuantity(), eveRef.getQuantity());
			}
		});
	}

	private static <T> void testLists(String msg, List<T> sde, List<T> eveRef, Comparator<T> comparator, Tester<T> tester) {
		Collections.sort(eveRef, comparator);
		Collections.sort(sde, comparator);
		testSortedLists(msg, sde, eveRef, tester);
	}

	private static <T extends Comparable<? super T>> void testLists(String msg, Collection<T> sde, Collection<T> eveRef, Tester<T> tester) {
		testLists(msg, new ArrayList<>(sde), new ArrayList<>(eveRef), tester);
	}

	private static <T extends Comparable<? super T>> void testLists(String msg, List<T> sde, List<T> eveRef, Tester<T> tester) {
		assertEquals(sde.size(), eveRef.size());
		Collections.sort(eveRef);
		Collections.sort(sde);
		testSortedLists(msg, sde, eveRef, tester);
	}

	private static <T> void testSortedLists(String msg, List<T> sde, List<T> eveRef, Tester<T> tester) {
		assertEquals(msg, sde.size(), eveRef.size());
		for (int i = 0; i < sde.size() && i < eveRef.size(); i++) {
			tester.test(sde.get(i), eveRef.get(i));
		}
	}

	private static interface Tester<T> {
		public void test(T sde, T eveRef);
	}

	@Test
	public void testGetBlueprint() {
		EveRefBlueprint blueprint = EveRefGetter.getBlueprint(999);
		assertEquals((Long)10L, blueprint.getMaxProductionLimit());
		assertEquals((Long)999L, blueprint.getBlueprintTypeID());

		EveRefActivity copying = blueprint.getCopying();
		assertEquals((Long)14400L, copying.getTime());

		EveRefActivity invention = blueprint.getInvention();
		assertEquals(2, invention.getMaterials().size());
		assertEquals((Integer)32, invention.getMaterials().get("20410").getQuantity());
		assertEquals((Integer)20410, invention.getMaterials().get("20410").getTypeID());
		assertEquals(1, invention.getProducts().size());
		assertEquals(0.22, invention.getProducts().get("22431").getProbability(), 0.0001);
		assertEquals((Integer)1, invention.getProducts().get("22431").getQuantity());
		assertEquals((Integer)22431, invention.getProducts().get("22431").getTypeID());
		assertEquals((Long)192000L, invention.getTime());
		assertEquals((Long)1L, invention.getRequiredSkills().get("11450"));
		assertEquals((Long)1L, invention.getRequiredSkills().get("11452"));
		assertEquals((Long)1L, invention.getRequiredSkills().get("23121"));
		
		EveRefActivity manufacturing = blueprint.getManufacturing();
		assertEquals(10, manufacturing.getMaterials().size());
		assertEquals((Integer)8000000, manufacturing.getMaterials().get("34").getQuantity());
		assertEquals((Integer)34, manufacturing.getMaterials().get("34").getTypeID());
		assertEquals(1, manufacturing.getProducts().size());
		assertEquals((Integer)1, manufacturing.getProducts().get("645").getQuantity());
		assertEquals((Integer)645, manufacturing.getProducts().get("645").getTypeID());
		assertEquals((Long)18000L, manufacturing.getTime());
		assertEquals((Long)1L, manufacturing.getRequiredSkills().get("3380"));

		EveRefActivity me = blueprint.getResearchMaterial();
		assertEquals((Long)6300L, me.getTime());
		EveRefActivity te = blueprint.getResearchTime();
		assertEquals((Long)6300L, te.getTime());
	}

	@Test
	public void testGetType() {
		EveRefType type = EveRefGetter.getType(645);

		assertEquals((Long)645L, type.getTypeID());
		assertEquals((Double)153900000.0, type.getBasePrice());
		assertEquals((Double)750.0, type.getCapacity());
		assertEquals(8, type.getDescription().size());
		assertNotNull(type.getDescription().get("en"));
		assertNotNull(type.getDogmaAttributes());
		assertEquals(92, type.getDogmaAttributes().size());
		assertNotNull(type.getDogmaAttributes().get("3"));
		assertEquals((Long)3L, type.getDogmaAttributes().get("3").getAttributeID());
		assertEquals((Double)0.0, type.getDogmaAttributes().get("3").getValue(), DELTA);
		assertNotNull(type.getDogmaEffects());
		assertEquals(5, type.getDogmaEffects().size());
		assertNotNull(type.getDogmaEffects().get("2186"));
		assertEquals((Long)2186L, type.getDogmaEffects().get("2186").getEffectID());
		assertEquals(false, type.getDogmaEffects().get("2186").isDefault());
		assertEquals((Long)500004L, type.getFactionID());
		assertEquals((Long)318L, type.getGraphicID());
		assertEquals((Long)27L, type.getGroupID());
		//assertEquals(LONG_VALUE, type.getIconID());
		assertEquals((Long)81L, type.getMarketGroupID());
		assertEquals((Double)100250000.0, type.getMass());
		assertEquals(5, type.getMasteries().size());
		assertEquals(6, type.getMasteries().get("0").size());
		assertEquals((Long)96L, type.getMasteries().get("0").get(0));
		assertEquals((Integer)1, type.getMetaGroupID());
		assertEquals(8, type.getName().size());
		assertEquals("Dominix", type.getName().get("en"));
		assertEquals((Double)50000.0, type.getPackagedVolume());
		assertEquals((Integer)1, type.getPortionSize());
		assertEquals(true, type.isPublished());
		assertEquals((Long)8L, type.getRaceID());
		assertEquals((Double)250.0, type.getRadius());
		assertEquals("gallentebase", type.getSofFactionName());
		//assertEquals((Long)0L, type.getSofMaterialSetID());
		assertEquals((Long)20072L, type.getSoundID());
		assertNotNull(type.getTraits());
		assertNotNull(type.getTraits().getMiscBonuses());
		assertEquals(0, type.getTraits().getMiscBonuses().size());
		/*
		assertNotNull(type.getTraits().getMiscBonuses().get(MAP_VALUE));
		assertEquals(DOUBLE_VALUE, type.getTraits().getMiscBonuses().get(MAP_VALUE).getBonus(), DELTA);
		assertNotNull(type.getTraits().getMiscBonuses().get(MAP_VALUE).getBonusText());
		assertEquals(STRING_VALUE, type.getTraits().getMiscBonuses().get(MAP_VALUE).getBonusText().get(MAP_VALUE));
		assertEquals(LONG_VALUE, type.getTraits().getMiscBonuses().get(MAP_VALUE).getImportance());
		assertEquals(LONG_VALUE, type.getTraits().getMiscBonuses().get(MAP_VALUE).getUnitID());
		assertEquals(BOOLEAN_VALUE, type.getTraits().getMiscBonuses().get(MAP_VALUE).isPositive());
		*/
		assertEquals(3, type.getTraits().getRoleBonuses().size());	
		assertNotNull(type.getTraits().getRoleBonuses().get("1"));
		assertEquals((Double)100.0, type.getTraits().getRoleBonuses().get("1").getBonus(), DELTA);
		assertNotNull(type.getTraits().getRoleBonuses().get("1").getBonusText());
		assertNotNull(type.getTraits().getRoleBonuses().get("1").getBonusText().get("en"));
		assertEquals((Long)1L, type.getTraits().getRoleBonuses().get("1").getImportance());
		assertEquals((Long)105L, type.getTraits().getRoleBonuses().get("1").getUnitID());
		//assertEquals(BOOLEAN_VALUE, type.getTraits().getRoleBonuses().get("1").isPositive());
		assertNotNull(type.getTraits().getTypes());  //Map 1
		assertEquals(1, type.getTraits().getTypes().size());
		assertNotNull(type.getTraits().getTypes().get("3336")); //Map 2
		assertEquals(2, type.getTraits().getTypes().get("3336").size());
		assertNotNull(type.getTraits().getTypes().get("3336").get("1")); //RoleBonus
		assertNotNull(type.getTraits().getTypes().get("3336").get("1").getBonus());
		assertEquals((Double)10.0, type.getTraits().getTypes().get("3336").get("1").getBonus(), DELTA);
		assertNotNull(type.getTraits().getTypes().get("3336").get("1").getBonusText());
		assertNotNull(type.getTraits().getTypes().get("3336").get("1").getBonusText().get("en"));
		assertEquals((Long)1L, type.getTraits().getTypes().get("3336").get("1").getImportance());
		assertEquals((Long)105L, type.getTraits().getTypes().get("3336").get("1").getUnitID());
		//assertEquals(BOOLEAN_VALUE, type.getTraits().getTypes().get("3336").get("1").isPositive());
		//assertEquals(LONG_VALUE, type.getVariationParentTypeID());
		assertEquals((Double)454500.0, type.getVolume());
		assertNotNull(type.getRequiredSkills());
		assertEquals(1, type.getRequiredSkills().size());
		assertEquals((Long)1L, type.getRequiredSkills().get("3336"));
		//assertEquals(ARRAY_LENGTH, type.getApplicableMutaplasmidTypeIDS().size());
		//assertEquals(LONG_VALUE, type.getApplicableMutaplasmidTypeIDS().get(0));
		//assertEquals(ARRAY_LENGTH, type.getCreatingMutaplasmidTypeIDS().size());
		//assertEquals(LONG_VALUE, type.getCreatingMutaplasmidTypeIDS().get(0));
		assertNotNull(type.getTypeVariations());
		assertEquals(3, type.getTypeVariations().size());
		assertEquals(1, type.getTypeVariations().get("1").size());
		assertEquals((Long)645L, type.getTypeVariations().get("1").get(0));
		/*
		assertNotNull(type.getOreVariations());
		assertEquals(MAP_LENGTH, type.getOreVariations().size());
		assertEquals(ARRAY_LENGTH, type.getOreVariations().get(MAP_VALUE).size());
		assertEquals(LONG_VALUE, type.getOreVariations().get(MAP_VALUE).get(0));
		*/
		//assertEquals(BOOLEAN_VALUE, type.isOre());
		assertNotNull(type.getProducedByBlueprints());
		assertEquals(1, type.getProducedByBlueprints().size());
		assertNotNull(type.getProducedByBlueprints().get("999"));
		assertEquals("manufacturing", type.getProducedByBlueprints().get("999").getBlueprintActivity());
		assertEquals((Integer)999, type.getProducedByBlueprints().get("999").getBlueprintTypeID());
		assertNotNull(type.getTypeMaterials());
		assertEquals(7, type.getTypeMaterials().size());
		assertNotNull(type.getTypeMaterials().get("34"));
		assertEquals((Integer)34, type.getTypeMaterials().get("34").getMaterialTypeID());
		assertEquals((Integer)8000000, type.getTypeMaterials().get("34").getQuantity());
		//assertEquals(ARRAY_LENGTH, type.getCanFitTypes().size());
		//assertEquals(LONG_VALUE, type.getCanFitTypes().get(0));
		//assertEquals(ARRAY_LENGTH, type.getCanBeFittedWithTypes().size());
		//assertEquals(LONG_VALUE, type.getCanBeFittedWithTypes().get(0));
		//assertEquals(BOOLEAN_VALUE, type.isSkill());
		//assertEquals(BOOLEAN_VALUE, type.isMutaplasmid());
		//assertEquals(BOOLEAN_VALUE, type.isDynamicItem());
		//assertEquals(BOOLEAN_VALUE, type.isBlueprint());
	}

}
