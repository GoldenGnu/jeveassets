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
package net.nikr.eve.jeveasset.gui.tabs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.swing.JComponent;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountTableFormat;
import net.nikr.eve.jeveasset.gui.dialogs.settings.ColorsTableFormat;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterMatcherTest.TestEnum;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.FormulaColumn;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.JumpColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.loadout.Loadout;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.mining.ExtractionsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.mining.MiningTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.overview.Overview;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedInterface;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedItem;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTotal;
import net.nikr.eve.jeveasset.gui.tabs.skills.SkillsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerSkillPointsFilterTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableFormat;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptions;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptionsGetter;
import net.nikr.eve.jeveasset.io.shared.ConverterTestUtil;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;


public class TableFormatTest extends TestUtil {

	private static final Boolean BOOLEAN_VALUE = true;
	private static final String STRING_VALUE = "A String";
	private static final Integer INTEGER_VALUE = 5;
	private static final Long LONG_VALUE = 5L;
	private static final Float FLOAT_VALUE = 5.1f;
	private static final Double DOUBLE_VALUE = 5.1;
	private static final Date DATE_VALUE = new Date();

	@Test
	public void testTableFormatClass() {
		boolean setValues = true;
		boolean setNull = false;
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			//Shared
			Tags tags = new Tags();
			EsiOwner owner = ConverterTestUtil.getEsiOwner(options);
			Item item = new Item(INTEGER_VALUE, STRING_VALUE, STRING_VALUE, STRING_VALUE, LONG_VALUE, FLOAT_VALUE, FLOAT_VALUE, FLOAT_VALUE, INTEGER_VALUE, STRING_VALUE, BOOLEAN_VALUE, INTEGER_VALUE, INTEGER_VALUE, INTEGER_VALUE, STRING_VALUE, STRING_VALUE, STRING_VALUE);
			MyLocation location = new MyLocation(LONG_VALUE, STRING_VALUE, LONG_VALUE, STRING_VALUE, LONG_VALUE, STRING_VALUE, LONG_VALUE, STRING_VALUE, STRING_VALUE);
		//Diaglogs
			//Options
			test(ColorsTableFormat.class);
			//Accounts
			test(AccountTableFormat.class);
			for (AccountTableFormat tableFormat : AccountTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(owner));
			}
		//Primary Tools
			//Asset
			test(AssetTableFormat.class);
			MyAsset asset = ConverterTestUtil.getMyAsset(owner, setNull, setValues, options);
			for (AssetTableFormat tableFormat : AssetTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(asset));
			}
			//Contract
			test(ContractsTableFormat.class);
			MyContract saveMyContract = ConverterTestUtil.getMyContract(setNull, setValues, options);
			MyContractItem saveMyContractItem = ConverterTestUtil.getMyContractItem(saveMyContract, setNull, setValues, options);
			for (ContractsTableFormat tableFormat : ContractsTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyContractItem));
			}
			//Journal
			test(JournalTableFormat.class);
			MyJournal saveMyJournal = ConverterTestUtil.getMyJournal(owner, setNull, setValues, options);
			for (JournalTableFormat tableFormat : JournalTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyJournal));
			}
			//Market Order
			test(MarketTableFormat.class);
			MyMarketOrder saveMyMarketOrder = ConverterTestUtil.getMyMarketOrder(owner, setNull, setValues, options);
			for (MarketTableFormat tableFormat : MarketTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyMarketOrder));
			}
			//Transaction
			test(TransactionTableFormat.class);
			MyTransaction saveMyTransaction = ConverterTestUtil.getMyTransaction(owner, setNull, setValues, options);
			for (TransactionTableFormat tableFormat : TransactionTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyTransaction));
			}
			//Industry Job
			test(IndustryJobTableFormat.class);
			MyIndustryJob saveMyIndustryJob = ConverterTestUtil.getMyIndustryJob(owner, setNull, setValues, options);
			for (IndustryJobTableFormat tableFormat : IndustryJobTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyIndustryJob));
			}
			//Skills
			test(SkillsTableFormat.class);
			//Mining
			test(MiningTableFormat.class);
			//Extractions
			test(ExtractionsTableFormat.class);
		//Secondary Tools
			//Slots
			test(SlotsTableFormat.class);
			//Item
			test(ItemTableFormat.class);
			for (ItemTableFormat tableFormat : ItemTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(item));
			}
			//Loadout
			test(LoadoutTableFormat.class);
			test(LoadoutExtendedTableFormat.class);
			Loadout loadout = new Loadout(item, location, owner, STRING_VALUE, asset, STRING_VALUE, DOUBLE_VALUE, DOUBLE_VALUE, LONG_VALUE, BOOLEAN_VALUE);
			for (LoadoutTableFormat tableFormat : LoadoutTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(loadout));
			}
			for (LoadoutExtendedTableFormat tableFormat : LoadoutExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(loadout));
			}
			//Overview
			test(OverviewTableFormat.class);
			Overview overview = new Overview(STRING_VALUE, location, DOUBLE_VALUE, DOUBLE_VALUE, LONG_VALUE, DOUBLE_VALUE);
			for (OverviewTableFormat tableFormat : OverviewTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(overview));
			}
			//Reprocessed
			test(ReprocessedTableFormat.class);
			ReprocessedMaterial reprocessedMaterial = new ReprocessedMaterial(INTEGER_VALUE, INTEGER_VALUE, INTEGER_VALUE);
			ReprocessedTotal parent = new ReprocessedTotal(null, item, DOUBLE_VALUE, LONG_VALUE);
			ReprocessedInterface reprocessedItem = new ReprocessedItem(parent, item, reprocessedMaterial, BOOLEAN_VALUE, DOUBLE_VALUE);
			for (ReprocessedTableFormat tableFormat : ReprocessedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(reprocessedItem));
			}
			for (ReprocessedExtendedTableFormat tableFormat : ReprocessedExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(reprocessedItem));
			}
			//StockpileItem
			test(StockpileTableFormat.class);
			test(StockpileExtendedTableFormat.class);
			Stockpile stockpile = new Stockpile(STRING_VALUE, null, new ArrayList<>(), DOUBLE_VALUE, BOOLEAN_VALUE);
			stockpile.setOwnerName(Collections.singletonList(owner.getOwnerName()));
			stockpile.setFlagName(Collections.singleton(asset.getItemFlag()));
			StockpileItem stockpileItem = new StockpileItem(stockpile, item, INTEGER_VALUE, DOUBLE_VALUE, BOOLEAN_VALUE);
			stockpileItem.setTags(tags);
			stockpileItem.updateValues(0, 0, DOUBLE_VALUE, PriceData.EMPTY);
			for (StockpileTableFormat tableFormat : StockpileTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(stockpileItem));
			}
			for (StockpileExtendedTableFormat tableFormat : StockpileExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(stockpileItem));
			}
			//Material
			test(MaterialTableFormat.class);
			test(MaterialExtendedTableFormat.class);
			Material material = new Material(Material.MaterialType.LOCATIONS, asset, STRING_VALUE, STRING_VALUE, STRING_VALUE);
			for (MaterialTableFormat tableFormat : MaterialTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(material));
			}
			for (MaterialExtendedTableFormat tableFormat : MaterialExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(material));
			}
			//Isk
			test(ValueTableFormat.class);
			Value value = new Value(STRING_VALUE, DATE_VALUE);
			for (ValueTableFormat tableFormat : ValueTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(value));
			}
			//Tracker
			test(TrackerSkillPointsFilterTableFormat.class);
			//Tree
			test(TreeTableFormat.class);
			TreeAsset treeAsset = new TreeAsset(asset, TreeAsset.TreeType.CATEGORY, new ArrayList<>(), STRING_VALUE, true);
			TreeAsset sub = new TreeAsset(asset, TreeAsset.TreeType.CATEGORY, Collections.singletonList(treeAsset), STRING_VALUE, false);
			treeAsset.addAsset(sub);
			treeAsset.updateParents();
			sub.updateParents();
			for (TreeTableFormat tableFormat : TreeTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(treeAsset));
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(sub));
			}
		//Tables
			test(FormulaColumn.class);
			test(JumpColumn.class);
			test(AllColumn.class);
		//Tests
			test(TestEnum.class);
		}
	}

	private void test(Class<?> tableFormat) {
		try {
			assertTrue(tableFormat + " does not implement toString", tableFormat.getMethod("toString").getDeclaringClass() == tableFormat);
		} catch (NoSuchMethodException | SecurityException ex) {
			fail(ex.getMessage());
		}
	}

	private void test(EnumTableColumn<?> enumColumn, Class<?> expecteds, Object object) {
		assertNotNull(enumColumn.getClass().getSuperclass().getSimpleName() + "->" + enumColumn.name() + " was null", object);
		Class<?> actual = object.getClass();
		if ((actual.isAssignableFrom(Double.class) || actual.isAssignableFrom(Float.class)) && (expecteds.isAssignableFrom(Double.class) || expecteds.isAssignableFrom(Float.class))) {
			//No problem
		} else if ((actual.isAssignableFrom(Long.class) || actual.isAssignableFrom(Integer.class)) && (expecteds.isAssignableFrom(Long.class) || expecteds.isAssignableFrom(Integer.class))) {
			//No problem
		} else if (Number.class.isAssignableFrom(actual)) {
			fail("Unsupported number type used");
		} else if (JComponent.class.isAssignableFrom(actual)) {
			//No problem
		} else if (String.class.isAssignableFrom(actual)) {
			//No problem
		} else if (String.class.isAssignableFrom(expecteds)) {
			try {
				if (actual.getSuperclass().isEnum()) {
					actual = actual.getSuperclass();
				}
				Method method = actual.getMethod("toString");
				assertTrue(actual + " does not implement toString: " + method.getDeclaringClass() , method.getDeclaringClass().equals(actual));
			} catch (NoSuchMethodException ex) {
				fail("no toString method");
			} catch (SecurityException ex) {
				fail("no toString method");
			}
		} else {
			assertTrue(enumColumn.getClass().getSuperclass().getSimpleName() + "->" + enumColumn.name() + " expected: " + expecteds.getSimpleName() + " was: " + actual.getSimpleName(), expecteds.isAssignableFrom(actual));
			try {
				assertTrue(actual + " does not implement hashCode", actual.getMethod("hashCode").getDeclaringClass() == actual);
				assertTrue(actual + " does not implement equals", actual.getMethod("equals").getDeclaringClass() == actual);
				assertTrue(actual + " does not implement compareTo", actual.getMethod("compareTo").getDeclaringClass() == actual);
			} catch (NoSuchMethodException | SecurityException ex) {
				//fail(ex.getMessage());
			}
		}
		String text = JFormulaDialog.getHardName(enumColumn) + " * 100 / 90";
		JFormulaDialog.replaceAll(enumColumn, text);
	}
}
