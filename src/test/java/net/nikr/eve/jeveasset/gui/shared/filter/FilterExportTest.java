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
package net.nikr.eve.jeveasset.gui.shared.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog.Formula;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps.Jump;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.FormulaColumn;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.JumpColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.SimpleColumnManager;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTab;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTab;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.mining.ExtractionsTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.ExtractionsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.mining.MiningTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.MiningTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.skills.SkillsTab;
import net.nikr.eve.jeveasset.gui.tabs.skills.SkillsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTab;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;


public class FilterExportTest extends TestUtil {

	private final String filterName = "Filter Name";

	@Test
	public void testNull() {
		FilterExport filterExport = new FilterExport("Toolname", new TestColumnManager<>());
		Map<String, List<Filter>> importFilter = filterExport.importFilter(null);
		assertNotNull(importFilter);
	}

	@Test
	public void test() {
	//Primary Tools
		//Asset
		test(AssetsTab.NAME, AssetTableFormat.values());
		//Contract
		test(ContractsTab.NAME, ContractsTableFormat.values());
		//Journal
		test(JournalTab.NAME, JournalTableFormat.values());
		//MarketOrder
		test(MarketOrdersTab.NAME, MarketTableFormat.values());
		//Transaction
		test(TransactionTab.NAME, TransactionTableFormat.values());
		//IndustryJob
		test(IndustryJobsTab.NAME, IndustryJobTableFormat.values());
		//Skills
		test(SkillsTab.NAME, SkillsTableFormat.values());
		//Mining
		test(MiningTab.NAME, MiningTableFormat.values());
		//Extractions
		test(ExtractionsTab.NAME, ExtractionsTableFormat.values());
	//Secondary Tools
		//Slots
		test(SlotsTab.NAME, SlotsTableFormat.values());
		//Tree
		test(TreeTab.NAME, TreeTableFormat.values());
		//Item
		test(ItemsTab.NAME, ItemTableFormat.values());
		//Loadout - No filters
		//test(LoadoutsTab.NAME, LoadoutTableFormat.values());
		//test(LoadoutsTab.NAME, LoadoutExtendedTableFormat.values());
		//Overview
		test(OverviewTab.NAME, OverviewTableFormat.values());
		//Reprocessed - No filters
		//test(ReprocessedTab.NAME, ReprocessedTableFormat.values());
		//test(ReprocessedTab.NAME, ReprocessedExtendedTableFormat.values());
		//Stockpile
		test(StockpileTab.NAME, StockpileTableFormat.values());
		test(StockpileTab.NAME, StockpileExtendedTableFormat.values());
		//Material - No filters
		//test(MaterialsTab.NAME, MaterialTableFormat.values());
		//test(MaterialsTab.NAME, MaterialExtendedTableFormat.values());
		//ISK
		test(ValueTableTab.NAME, ValueTableFormat.values());
	//Dialogs
		//AccountTableFormat - No filters
	}

	public <E> void test(String toolName, EnumTableColumn<E>[] columns) {
		System.out.println("Testing: " + toolName);
		FilterExport filterExport = new FilterExport(toolName, new TestColumnManager<E>());

		//Plain
		List<Filter> plainFilters = createFilters(columns);
		testFilters(filterExport, plainFilters);

		//Formula
		Filter formulaFilter = createFormulaFilter(columns);
		if (formulaFilter != null) {
			List<Filter> formulaFilters = new ArrayList<>(plainFilters);
			formulaFilters.add(formulaFilter);
			testFilters(filterExport, formulaFilters);
		}

		//Jump
		List<Filter> jumpFilters = new ArrayList<>(plainFilters);
		jumpFilters.add(createJumpFilter());
		testFilters(filterExport, jumpFilters);
	}

	public <E> void testFilters(FilterExport filterExport, List<Filter> exportFilters) {
		String exportString = filterExport.exportFilter(filterName, exportFilters);
		System.out.println(exportString);
		Map<String, List<Filter>> importFilter = filterExport.importFilter(exportString);
		assertNotNull(importFilter);
		List<Filter> importFilters = importFilter.get(filterName);
		assertEquals(exportFilters, importFilters);
	}

	public <E> Filter createFormulaFilter(EnumTableColumn<E>[] columns) {
		EnumTableColumn<E> columnA = null;
		EnumTableColumn<E> columnB = null;
		for (EnumTableColumn<E> column : columns) {
			if (Number.class.isAssignableFrom(column.getType()) || NumberValue.class.isAssignableFrom(column.getType())) {
				if (columnA == null) {
					columnA = column;
				} else {
					columnB = column;
				break;
				}
			}
		}
		if (columnA == null || columnB == null) {
			return null;
		}
		Formula formula = new Formula("Formula Column", JFormulaDialog.getHardName(columnB) + " > " + JFormulaDialog.getHardName(columnA), 0);
		FormulaColumn<E> column = new FormulaColumn<>(formula);
		return new Filter(Filter.LogicType.AND, column, Filter.CompareType.GREATER_THAN, "0");
	}

	public <E> Filter createJumpFilter() {
		Jump jump = new Jump(StaticData.get().getLocation(30000142)); //Jita
		JumpColumn<E> column = new JumpColumn<>(jump);
		return new Filter(Filter.LogicType.AND, column, Filter.CompareType.GREATER_THAN, "0");
	}

	public <E> List<Filter> createFilters(EnumTableColumn<E>[] columns) {
		List<Filter> exportFilters = new ArrayList<>();
		EnumTableColumn<E> stringColumn = null;
		EnumTableColumn<E> numberColumn = null;
		for (EnumTableColumn<E> column : columns) {
			if (stringColumn == null && column.getType().equals(String.class)) {
				exportFilters.add(new Filter(Filter.LogicType.AND, column, Filter.CompareType.CONTAINS, "text"));
				stringColumn = column;
			}
			if (numberColumn == null && column.getType().equals(Double.class)) {
				exportFilters.add(new Filter(Filter.LogicType.AND, column, Filter.CompareType.GREATER_THAN, "0"));
				numberColumn = column;
			}
			if (numberColumn != null && column.getType().equals(Double.class)) {
				exportFilters.add(new Filter(Filter.LogicType.AND, column, Filter.CompareType.GREATER_THAN_COLUMN, column.name()));
				break;
			}
		}
		return exportFilters;
	}

	private class TestColumnManager<Q> implements SimpleColumnManager<Q> {
		@Override
		public FormulaColumn<Q> addColumn(Formula formula) {
			return new FormulaColumn<>(formula);
		}

		@Override
		public JumpColumn<Q> addColumn(Jump jump) {
			return new JumpColumn<>(jump);
		}
	}
}
