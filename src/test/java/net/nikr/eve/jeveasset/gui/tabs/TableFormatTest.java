/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountTableFormat;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.loadout.Loadout;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialExtenedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.overview.Overview;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedInterface;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedItem;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTotal;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableFormat;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptions;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptionsGetter;
import net.nikr.eve.jeveasset.io.shared.ConverterTestUtil;
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
			EsiOwner owner = ConverterTestUtil.getEsiOwner(options);
			Item item = new Item(INTEGER_VALUE, STRING_VALUE, STRING_VALUE, STRING_VALUE, LONG_VALUE, FLOAT_VALUE, INTEGER_VALUE, STRING_VALUE, BOOLEAN_VALUE, BOOLEAN_VALUE, INTEGER_VALUE, INTEGER_VALUE, INTEGER_VALUE);
			MyLocation location = new MyLocation(LONG_VALUE, STRING_VALUE, LONG_VALUE, STRING_VALUE, LONG_VALUE, STRING_VALUE, STRING_VALUE);
		//Primary Tools
			//Asset
			MyAsset asset = ConverterTestUtil.getMyAsset(owner, setNull, setValues, options);
			for (AssetTableFormat tableFormat : AssetTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(asset));
			}
			//Contract
			MyContract saveMyContract = ConverterTestUtil.getMyContract(setNull, setValues, options);
			MyContractItem saveMyContractItem = ConverterTestUtil.getMyContractItem(saveMyContract, setNull, setValues, options);
			for (ContractsTableFormat tableFormat : ContractsTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyContractItem));
			}
			for (ContractsExtendedTableFormat tableFormat : ContractsExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyContractItem));
			}
			//Journal
			MyJournal saveMyJournal = ConverterTestUtil.getMyJournal(owner, setNull, setValues, options);
			for (JournalTableFormat tableFormat : JournalTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyJournal));
			}
			//MarketOrder
			MyMarketOrder saveMyMarketOrder = ConverterTestUtil.getMyMarketOrder(owner, setNull, setValues, options);
			for (MarketTableFormat tableFormat : MarketTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyMarketOrder));
			}
			//Transaction
			MyTransaction saveMyTransaction = ConverterTestUtil.getMyTransaction(owner, setNull, setValues, options);
			for (TransactionTableFormat tableFormat : TransactionTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyTransaction));
			}
			//IndustryJob
			MyIndustryJob saveMyIndustryJob = ConverterTestUtil.getMyIndustryJob(owner, setNull, setValues, options);
			for (IndustryJobTableFormat tableFormat : IndustryJobTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyIndustryJob));
			}
			//Owners
			for (AccountTableFormat tableFormat : AccountTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(owner));
			}
		//Secondary Tools
			//Item
			for (ItemTableFormat tableFormat : ItemTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(item));
			}
			//Loadout
			Loadout loadout = new Loadout(item, location, owner, STRING_VALUE, STRING_VALUE, STRING_VALUE, DOUBLE_VALUE, DOUBLE_VALUE, LONG_VALUE);
			for (LoadoutTableFormat tableFormat : LoadoutTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(loadout));
			}
			for (LoadoutExtendedTableFormat tableFormat : LoadoutExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(loadout));
			}
			//Overview
			Overview overview = new Overview(STRING_VALUE, location, DOUBLE_VALUE, DOUBLE_VALUE, LONG_VALUE, DOUBLE_VALUE);
			for (OverviewTableFormat tableFormat : OverviewTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(overview));
			}
			//Reprocessed
			ReprocessedMaterial reprocessedMaterial = new ReprocessedMaterial(INTEGER_VALUE, INTEGER_VALUE, INTEGER_VALUE);
			ReprocessedTotal parent = new ReprocessedTotal(item, DOUBLE_VALUE);
			ReprocessedInterface reprocessedItem = new ReprocessedItem(parent, item, reprocessedMaterial, INTEGER_VALUE, DOUBLE_VALUE);
			for (ReprocessedTableFormat tableFormat : ReprocessedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(reprocessedItem));
			}
			for (ReprocessedExtendedTableFormat tableFormat : ReprocessedExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(reprocessedItem));
			}
			//StockpileItem
			Stockpile stockpile = new Stockpile(STRING_VALUE, new ArrayList<Stockpile.StockpileFilter>(), DOUBLE_VALUE);
			stockpile.setOwnerName(Collections.singletonList(owner.getOwnerName()));
			stockpile.setFlagName(Collections.singletonList(asset.getFlag()));
			StockpileItem stockpileItem = new StockpileItem(stockpile, item, INTEGER_VALUE, DOUBLE_VALUE);
			for (StockpileTableFormat tableFormat : StockpileTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(stockpileItem));
			}
			for (StockpileExtendedTableFormat tableFormat : StockpileExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(stockpileItem));
			}
			//Material
			Material material = new Material(Material.MaterialType.LOCATIONS, asset, STRING_VALUE, STRING_VALUE, STRING_VALUE);
			for (MaterialTableFormat tableFormat : MaterialTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(material));
			}
			for (MaterialExtenedTableFormat tableFormat : MaterialExtenedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(material));
			}
			Value value = new Value(STRING_VALUE, DATE_VALUE);
			for (ValueTableFormat tableFormat : ValueTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(value));
			}
			TreeAsset treeAsset = new TreeAsset(asset, TreeAsset.TreeType.CATEGORY, new ArrayList<TreeAsset>(), STRING_VALUE, BOOLEAN_VALUE);
			for (TreeTableFormat tableFormat : TreeTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(treeAsset));
			}
		}
	}

	private void test(EnumTableColumn<?> tableFormat, Class<?> expecteds, Object actual) {
		assertNotNull(tableFormat.getClass().getSuperclass().getSimpleName() + "->" + tableFormat.name() + " was null", actual);
		if ((Number.class.isAssignableFrom(actual.getClass()) && Number.class.isAssignableFrom(expecteds))) {
			//assertTrue(tableFormat.getClass().getSuperclass().getSimpleName() + "->" + tableFormat.name() + " expected: " + expecteds.getSimpleName() + " was: " + actual.getClass().getSimpleName(), expecteds.isAssignableFrom(actual.getClass()));
		} else if (String.class.isAssignableFrom(expecteds)) {
			//Handled by toString()
		} else {
			assertTrue(tableFormat.getClass().getSuperclass().getSimpleName() + "->" + tableFormat.name() + " expected: " + expecteds.getSimpleName() + " was: " + actual.getClass().getSimpleName(), expecteds.isAssignableFrom(actual.getClass()));
		}
	}
}
