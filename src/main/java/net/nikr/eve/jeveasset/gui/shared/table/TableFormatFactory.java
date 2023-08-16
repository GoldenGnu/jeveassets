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
package net.nikr.eve.jeveasset.gui.shared.table;

import java.util.Arrays;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyExtraction;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.api.my.MySkill;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.ColorSettings.ColorRow;
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountTableFormat;
import net.nikr.eve.jeveasset.gui.dialogs.settings.ColorsTableFormat;
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
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.skills.SkillsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.slots.Slots;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerSkillPointFilter;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerSkillPointsFilterTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableFormat;


public class TableFormatFactory {
	public static EnumTableFormatAdaptor<AccountTableFormat, OwnerType> accountTableFormat() {
		return new EnumTableFormatAdaptor<>(AccountTableFormat.class);
	}

	public static EnumTableFormatAdaptor<AssetTableFormat, MyAsset> assetTableFormat() {
		return new EnumTableFormatAdaptor<>(AssetTableFormat.class);
	}

	public static EnumTableFormatAdaptor<ColorsTableFormat, ColorRow> colorsTableFormat() {
		return new EnumTableFormatAdaptor<>(ColorsTableFormat.class);
	}

	//Extended
	public static EnumTableFormatAdaptor<ContractsTableFormat, MyContractItem> contractsTableFormat() {
		return new EnumTableFormatAdaptor<>(ContractsTableFormat.class);
	}

	public static EnumTableFormatAdaptor<IndustryJobTableFormat, MyIndustryJob> industryJobTableFormat() {
		return new EnumTableFormatAdaptor<>(IndustryJobTableFormat.class);
	}

	public static EnumTableFormatAdaptor<SlotsTableFormat, Slots> slotTableFormat() {
		return new EnumTableFormatAdaptor<>(SlotsTableFormat.class);
	}

	public static EnumTableFormatAdaptor<ItemTableFormat, Item> itemTableFormat() {
		return new EnumTableFormatAdaptor<>(ItemTableFormat.class);
	}

	public static EnumTableFormatAdaptor<JournalTableFormat, MyJournal> journalTableFormat() {
		return new EnumTableFormatAdaptor<>(JournalTableFormat.class);
	}

	//Extended
	public static EnumTableFormatAdaptor<LoadoutTableFormat, Loadout> loadoutTableFormat() {
		return new EnumTableFormatAdaptor<>(LoadoutTableFormat.class, Arrays.asList(LoadoutExtendedTableFormat.values()));
	}

	public static EnumTableFormatAdaptor<MarketTableFormat, MyMarketOrder> marketTableFormat() {
		return new EnumTableFormatAdaptor<>(MarketTableFormat.class);
	}

	//Extended
	public static EnumTableFormatAdaptor<MaterialTableFormat, Material> materialTableFormat() {
		return new EnumTableFormatAdaptor<>(MaterialTableFormat.class, Arrays.asList(MaterialExtendedTableFormat.values()));
	}

	public static EnumTableFormatAdaptor<OverviewTableFormat, Overview> overviewTableFormat() {
		return new EnumTableFormatAdaptor<>(OverviewTableFormat.class);
	}

	//Extended
	public static EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface> reprocessedTableFormat() {
		return new EnumTableFormatAdaptor<>(ReprocessedTableFormat.class, Arrays.asList(ReprocessedExtendedTableFormat.values()));
	}

	//Extended
	public static EnumTableFormatAdaptor<StockpileTableFormat, Stockpile.StockpileItem> stockpileTableFormat() {
		return new EnumTableFormatAdaptor<>(StockpileTableFormat.class, Arrays.asList(StockpileExtendedTableFormat.values()));
	}

	public static EnumTableFormatAdaptor<TrackerSkillPointsFilterTableFormat, TrackerSkillPointFilter> trackerSkillPointsFilterTableFormat() {
		return new EnumTableFormatAdaptor<>(TrackerSkillPointsFilterTableFormat.class);
	}

	public static EnumTableFormatAdaptor<TransactionTableFormat, MyTransaction> transactionTableFormat() {
		return new EnumTableFormatAdaptor<>(TransactionTableFormat.class);
	}

	public static EnumTableFormatAdaptor<TreeTableFormat, TreeAsset> treeTableFormat() {
		return new EnumTableFormatAdaptor<>(TreeTableFormat.class);
	}

	public static EnumTableFormatAdaptor<ValueTableFormat, Value> valueTableFormat() {
		return new EnumTableFormatAdaptor<>(ValueTableFormat.class);
	}

	public static EnumTableFormatAdaptor<SkillsTableFormat, MySkill> skillsTableFormat() {
		return new EnumTableFormatAdaptor<>(SkillsTableFormat.class);
	}

	public static EnumTableFormatAdaptor<MiningTableFormat, MyMining> miningTableFormat() {
		return new EnumTableFormatAdaptor<>(MiningTableFormat.class);
	}

	public static EnumTableFormatAdaptor<ExtractionsTableFormat, MyExtraction> extractionsTableFormat() {
		return new EnumTableFormatAdaptor<>(ExtractionsTableFormat.class);
	}
}
