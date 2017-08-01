/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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

import static org.junit.Assert.assertTrue;

import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.MyAccountBalance;
import net.nikr.eve.jeveasset.data.esi.EsiOwner;
import net.nikr.eve.jeveasset.data.raw.RawBlueprint;
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountTableFormat;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.journal.MyJournal;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.transaction.MyTransaction;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptions;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptionsGetter;
import net.nikr.eve.jeveasset.io.shared.ConverterTestUtil;
import org.junit.Test;

public class TableFormatTest extends TestUtil {

	@Test
	public void testTableFormatClass() {
		boolean setValues = true;
		boolean setNull = false;
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			EsiOwner owner = ConverterTestUtil.getEsiOwner(options);
			//Account Balance
			MyAccountBalance accountBalance = ConverterTestUtil.getMyAccountBalance(owner, setValues, options);

			//Asset
			MyAsset asset = ConverterTestUtil.getMyAsset(owner, setNull, setValues, options);

			//Blueprint
			RawBlueprint rawBlueprint = ConverterTestUtil.getRawBlueprint(options);

			//Contract
			MyContract saveMyContract = ConverterTestUtil.getMyContract(setNull, setValues, options);
			MyContractItem saveMyContractItem = ConverterTestUtil.getMyContractItem(saveMyContract, setNull, setValues, options);

			//IndustryJob
			MyIndustryJob saveMyIndustryJob = ConverterTestUtil.getMyIndustryJob(owner, setNull, setValues, options);

			//Journal
			MyJournal saveMyJournal = ConverterTestUtil.getMyJournal(owner, setNull, setValues, options);

			//MarketOrder
			MyMarketOrder saveMyMarketOrder = ConverterTestUtil.getMyMarketOrder(owner, setNull, setValues, options);

			//Transaction
			MyTransaction saveMyTransaction = ConverterTestUtil.getMyTransaction(owner, setNull, setValues, options);

			//Primary Tools
			for (AssetTableFormat tableFormat : AssetTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(asset));
			}
			for (ContractsTableFormat tableFormat : ContractsTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyContractItem));
			}
			for (ContractsExtendedTableFormat tableFormat : ContractsExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyContractItem));
			}
			for (JournalTableFormat tableFormat : JournalTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyJournal));
			}
			for (MarketTableFormat tableFormat : MarketTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyMarketOrder));
			}
			for (TransactionTableFormat tableFormat : TransactionTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyTransaction));
			}
			for (IndustryJobTableFormat tableFormat : IndustryJobTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyIndustryJob));
			}
			//Owners
			for (AccountTableFormat tableFormat : AccountTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(owner));
			}
			//Secondary Tools
			/*
			for (ItemTableFormat tableFormat : ItemTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(saveMyJournal));
			}
			for (LoadoutTableFormat tableFormat : LoadoutTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue());
			}
			for (LoadoutExtendedTableFormat tableFormat : LoadoutExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue());
			}
			for (OverviewTableFormat tableFormat : OverviewTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(from));
			}
			for (ReprocessedTableFormat tableFormat : ReprocessedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(from));
			}
			for (ReprocessedExtendedTableFormat tableFormat : ReprocessedExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(from));
			}
			for (StockpileTableFormat tableFormat : StockpileTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(from));
			}
			for (StockpileExtendedTableFormat tableFormat : StockpileExtendedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(from));
			}
			for (MaterialTableFormat tableFormat : MaterialTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(from));
			}
			for (MaterialExtenedTableFormat tableFormat : MaterialExtenedTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(from));
			}
			for (ValueTableFormat tableFormat : ValueTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(from));
			}
			for (TreeTableFormat tableFormat : TreeTableFormat.values()) {
				test(tableFormat, tableFormat.getType(), tableFormat.getColumnValue(from));
			}
			 */
		}
	}

	private void test(EnumTableColumn<?> tableFormat, Class<?> expecteds, Object actual) {
		if ((Number.class.isAssignableFrom(actual.getClass()) && Number.class.isAssignableFrom(expecteds)) || String.class.isAssignableFrom(expecteds)) {
			//Resovled by Glazed Lists
		} else {
			assertTrue(tableFormat.getClass().getSuperclass().getSimpleName() + "->" + tableFormat.name() + " expected: " + expecteds.getSimpleName() + " was: " + actual.getClass().getSimpleName(), expecteds.isAssignableFrom(actual.getClass()));
		}
	}
}
