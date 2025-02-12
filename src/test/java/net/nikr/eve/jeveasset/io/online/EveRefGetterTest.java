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
package net.nikr.eve.jeveasset.io.online;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import net.nikr.eve.jeveasset.TestUtil;
import static net.nikr.eve.jeveasset.TestUtil.initLog;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.io.online.EveRefGetter.EveRefActivity;
import net.nikr.eve.jeveasset.io.online.EveRefGetter.EveRefBlueprint;
import net.nikr.eve.jeveasset.io.online.EveRefGetter.EveRefType;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.BeforeClass;


public class EveRefGetterTest extends TestUtil {

	private static final Gson GSON = new GsonBuilder().create();
	private static final Long LONG_VALUE = 10L;
	private static final Integer INTEGER_VALUE = 10;
	private static final Double DOUBLE_VALUE = 10.0;
	private static final String STRING_VALUE = "string";
	private static final boolean BOOLEAN_VALUE = true;
	private static final String MAP_VALUE = "additionalProp1";
	private static final int MAP_LENGTH = 3;
	private static final int ARRAY_LENGTH = 1;
	private static final double DELTA = 0.0000001;

	@BeforeClass
	public static void setUpClass() {
		setLoggingLevel(Level.ERROR);
	}

	@AfterClass
	public static void tearDownClass() {
		setLoggingLevel(Level.INFO);
	}

	@Test
	public void testBlueprintModel() throws URISyntaxException, FileNotFoundException {
		URL resource = EveRefGetterTest.class.getResource("/everefblueprint.json");
		File file  = new File(resource.toURI());
		FileReader input = new FileReader(file);
		EveRefBlueprint blueprint = GSON.fromJson(input, new TypeToken<EveRefBlueprint>() {}.getType());

		assertEquals(LONG_VALUE, blueprint.getMaxProductionLimit());
		assertEquals(LONG_VALUE, blueprint.getBlueprintTypeID());

		EveRefActivity activity = blueprint.getActivities().get(MAP_VALUE);
		assertEquals(LONG_VALUE, activity.getTime());
		assertEquals(MAP_LENGTH, activity.getMaterials().size());
		assertEquals(INTEGER_VALUE, activity.getMaterials().get(MAP_VALUE).getQuantity());
		assertEquals(INTEGER_VALUE, activity.getMaterials().get(MAP_VALUE).getTypeID());
		assertEquals(MAP_LENGTH, activity.getProducts().size());
		assertEquals(DOUBLE_VALUE, activity.getProducts().get(MAP_VALUE).getProbability(), DELTA);
		assertEquals(INTEGER_VALUE, activity.getProducts().get(MAP_VALUE).getQuantity());
		assertEquals(INTEGER_VALUE, activity.getProducts().get(MAP_VALUE).getTypeID());
		assertEquals(LONG_VALUE, activity.getRequiredSkills().get(MAP_VALUE));
	}

	@Test
	public void testItemModel() throws FileNotFoundException, URISyntaxException {
		URL resource = EveRefGetterTest.class.getResource("/evereftype.json");
		File file  = new File(resource.toURI());
		FileReader input = new FileReader(file);
		EveRefType type = GSON.fromJson(input, new TypeToken<EveRefType>() {}.getType());

		assertEquals(LONG_VALUE, type.getTypeID());
		assertEquals(DOUBLE_VALUE, type.getBasePrice());
		assertEquals(DOUBLE_VALUE, type.getCapacity());
		assertEquals(MAP_LENGTH, type.getDescription().size());
		assertEquals(STRING_VALUE, type.getDescription().get(MAP_VALUE));
		assertNotNull(type.getDogmaAttributes());
		assertEquals(MAP_LENGTH, type.getDogmaAttributes().size());
		assertNotNull(type.getDogmaAttributes().get(MAP_VALUE));
		assertEquals(LONG_VALUE, type.getDogmaAttributes().get(MAP_VALUE).getAttributeID());
		assertEquals(DOUBLE_VALUE, type.getDogmaAttributes().get(MAP_VALUE).getValue(), DELTA);
		assertNotNull(type.getDogmaEffects());
		assertEquals(MAP_LENGTH, type.getDogmaEffects().size());
		assertNotNull(type.getDogmaEffects().get(MAP_VALUE));
		assertEquals(LONG_VALUE, type.getDogmaEffects().get(MAP_VALUE).getEffectID());
		assertEquals(BOOLEAN_VALUE, type.getDogmaEffects().get(MAP_VALUE).isDefault());
		assertEquals(LONG_VALUE, type.getFactionID());
		assertEquals(LONG_VALUE, type.getGraphicID());
		assertEquals(LONG_VALUE, type.getGroupID());
		assertEquals(LONG_VALUE, type.getIconID());
		assertEquals(LONG_VALUE, type.getMarketGroupID());
		assertEquals(DOUBLE_VALUE, type.getMass());
		assertEquals(MAP_LENGTH, type.getMasteries().size());
		assertEquals(ARRAY_LENGTH, type.getMasteries().get(MAP_VALUE).size());
		assertEquals(LONG_VALUE, type.getMasteries().get(MAP_VALUE).get(0));
		assertEquals(INTEGER_VALUE, type.getMetaGroupID());
		assertEquals(MAP_LENGTH, type.getName().size());
		assertEquals(STRING_VALUE, type.getName().get(MAP_VALUE));
		assertEquals(DOUBLE_VALUE, type.getPackagedVolume());
		assertEquals(INTEGER_VALUE, type.getPortionSize());
		assertEquals(BOOLEAN_VALUE, type.isPublished());
		assertEquals(LONG_VALUE, type.getRaceID());
		assertEquals(DOUBLE_VALUE, type.getRadius());
		assertEquals(STRING_VALUE, type.getSofFactionName());
		assertEquals(LONG_VALUE, type.getSofMaterialSetID());
		assertEquals(LONG_VALUE, type.getSoundID());
		assertNotNull(type.getTraits());
		assertNotNull(type.getTraits().getMiscBonuses());
		assertEquals(MAP_LENGTH, type.getTraits().getMiscBonuses().size());
		assertNotNull(type.getTraits().getMiscBonuses().get(MAP_VALUE));
		assertEquals(DOUBLE_VALUE, type.getTraits().getMiscBonuses().get(MAP_VALUE).getBonus(), DELTA);
		assertNotNull(type.getTraits().getMiscBonuses().get(MAP_VALUE).getBonusText());
		assertEquals(STRING_VALUE, type.getTraits().getMiscBonuses().get(MAP_VALUE).getBonusText().get(MAP_VALUE));
		assertEquals(LONG_VALUE, type.getTraits().getMiscBonuses().get(MAP_VALUE).getImportance());
		assertEquals(LONG_VALUE, type.getTraits().getMiscBonuses().get(MAP_VALUE).getUnitID());
		assertEquals(BOOLEAN_VALUE, type.getTraits().getMiscBonuses().get(MAP_VALUE).isPositive());
		assertEquals(MAP_LENGTH, type.getTraits().getRoleBonuses().size());
		assertNotNull(type.getTraits().getRoleBonuses().get(MAP_VALUE));
		assertEquals(DOUBLE_VALUE, type.getTraits().getRoleBonuses().get(MAP_VALUE).getBonus(), DELTA);
		assertNotNull(type.getTraits().getRoleBonuses().get(MAP_VALUE).getBonusText());
		assertEquals(STRING_VALUE, type.getTraits().getRoleBonuses().get(MAP_VALUE).getBonusText().get(MAP_VALUE));
		assertEquals(LONG_VALUE, type.getTraits().getRoleBonuses().get(MAP_VALUE).getImportance());
		assertEquals(LONG_VALUE, type.getTraits().getRoleBonuses().get(MAP_VALUE).getUnitID());
		assertEquals(BOOLEAN_VALUE, type.getTraits().getRoleBonuses().get(MAP_VALUE).isPositive());
		assertNotNull(type.getTraits().getTypes());  //Map 1
		assertEquals(MAP_LENGTH, type.getTraits().getTypes().size());
		assertNotNull(type.getTraits().getTypes().get(MAP_VALUE)); //Map 2
		assertEquals(MAP_LENGTH, type.getTraits().getTypes().get(MAP_VALUE).size());
		assertNotNull(type.getTraits().getTypes().get(MAP_VALUE).get(MAP_VALUE)); //RoleBonus
		assertNotNull(type.getTraits().getTypes().get(MAP_VALUE).get(MAP_VALUE).getBonus());
		assertEquals(DOUBLE_VALUE, type.getTraits().getTypes().get(MAP_VALUE).get(MAP_VALUE).getBonus(), DELTA);
		assertNotNull(type.getTraits().getTypes().get(MAP_VALUE).get(MAP_VALUE).getBonusText());
		assertEquals(STRING_VALUE, type.getTraits().getTypes().get(MAP_VALUE).get(MAP_VALUE).getBonusText().get(MAP_VALUE));
		assertEquals(LONG_VALUE, type.getTraits().getTypes().get(MAP_VALUE).get(MAP_VALUE).getImportance());
		assertEquals(LONG_VALUE, type.getTraits().getTypes().get(MAP_VALUE).get(MAP_VALUE).getUnitID());
		assertEquals(BOOLEAN_VALUE, type.getTraits().getTypes().get(MAP_VALUE).get(MAP_VALUE).isPositive());
		assertEquals(LONG_VALUE, type.getVariationParentTypeID());
		assertEquals(DOUBLE_VALUE, type.getVolume());
		assertNotNull(type.getRequiredSkills());
		assertEquals(MAP_LENGTH, type.getRequiredSkills().size());
		assertEquals(LONG_VALUE, type.getRequiredSkills().get(MAP_VALUE));
		assertEquals(ARRAY_LENGTH, type.getApplicableMutaplasmidTypeIDS().size());
		assertEquals(LONG_VALUE, type.getApplicableMutaplasmidTypeIDS().get(0));
		assertEquals(ARRAY_LENGTH, type.getCreatingMutaplasmidTypeIDS().size());
		assertEquals(LONG_VALUE, type.getCreatingMutaplasmidTypeIDS().get(0));
		assertNotNull(type.getTypeVariations());
		assertEquals(MAP_LENGTH, type.getTypeVariations().size());
		assertEquals(ARRAY_LENGTH, type.getTypeVariations().get(MAP_VALUE).size());
		assertEquals(LONG_VALUE, type.getTypeVariations().get(MAP_VALUE).get(0));
		assertNotNull(type.getOreVariations());
		assertEquals(MAP_LENGTH, type.getOreVariations().size());
		assertEquals(ARRAY_LENGTH, type.getOreVariations().get(MAP_VALUE).size());
		assertEquals(LONG_VALUE, type.getOreVariations().get(MAP_VALUE).get(0));
		assertEquals(BOOLEAN_VALUE, type.isOre());
		assertNotNull(type.getProducedByBlueprints());
		assertEquals(MAP_LENGTH, type.getProducedByBlueprints().size());
		assertNotNull(type.getProducedByBlueprints().get(MAP_VALUE));
		assertEquals(STRING_VALUE, type.getProducedByBlueprints().get(MAP_VALUE).getBlueprintActivity());
		assertEquals(INTEGER_VALUE, type.getProducedByBlueprints().get(MAP_VALUE).getBlueprintTypeID());
		assertNotNull(type.getTypeMaterials());
		assertEquals(MAP_LENGTH, type.getTypeMaterials().size());
		assertNotNull(type.getTypeMaterials().get(MAP_VALUE));
		assertEquals(INTEGER_VALUE, type.getTypeMaterials().get(MAP_VALUE).getMaterialTypeID());
		assertEquals(INTEGER_VALUE, type.getTypeMaterials().get(MAP_VALUE).getQuantity());
		assertEquals(ARRAY_LENGTH, type.getCanFitTypes().size());
		assertEquals(LONG_VALUE, type.getCanFitTypes().get(0));
		assertEquals(ARRAY_LENGTH, type.getCanBeFittedWithTypes().size());
		assertEquals(LONG_VALUE, type.getCanBeFittedWithTypes().get(0));
		assertEquals(BOOLEAN_VALUE, type.isSkill());
		assertEquals(BOOLEAN_VALUE, type.isMutaplasmid());
		assertEquals(BOOLEAN_VALUE, type.isDynamicItem());
		assertEquals(BOOLEAN_VALUE, type.isBlueprint());
	}

	/**
	 * Null test
	 * @param args
	 */
	public static void main(final String[] args) {
		initLog();
		EveRefGetterTest test = new EveRefGetterTest();
		test.testNullTest();
		System.exit(0);
	}

	@Test
	public void testNullTest() {
		Item item = new Item(0);
		assertNotNull(EveRefGetter.getItem(item, null, null));
		EveRefType type = new EveRefType();
		assertNotNull(EveRefGetter.getItem(item, type, null));
		assertNull(type.isBlueprint());
		setField(type, "blueprint", true);
		assertTrue(type.isBlueprint());
		assertNotNull(EveRefGetter.getItem(item, type, null));
		EveRefBlueprint blueprint = new EveRefBlueprint();
		assertNotNull(EveRefGetter.getItem(item, type, blueprint));
	}

	private static void setField(Object cc, String field, Object value) {
		try {
			Field f1 = cc.getClass().getDeclaredField(field);
			f1.setAccessible(true);
			f1.set(cc, value);
			f1.setAccessible(false);
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

}
