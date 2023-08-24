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
package net.nikr.eve.jeveasset;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.ExportSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.filter.ExportTableData;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterLogicalMatcher;
import net.nikr.eve.jeveasset.gui.shared.filter.SimpleTableFormat;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTab;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTab;
import net.nikr.eve.jeveasset.gui.tabs.loadout.Loadout;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutData;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialsData;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialsTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.Overview;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewData;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab.View;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedData;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsData;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileData;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeData;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.values.IskData;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CliExport {

	private static final Logger LOG = LoggerFactory.getLogger(CliExport.class);

	public static enum ExportTool {
		ASSETS("Assets", CliOptions.ASSETS, AssetsTab.NAME),
		CONTRACTS("Contracts", CliOptions.CONTRACTS, ContractsTab.NAME),
		INDUSTRY_JOBS("Industry Jobs", CliOptions.INDUSTRY_JOBS, IndustryJobsTab.NAME),
		SLOTS("Slots", CliOptions.SLOTS, SlotsTab.NAME),
		ISK("Isk", CliOptions.ISK, ValueTableTab.NAME),
		ITEMS("Items", CliOptions.ITEMS, ItemsTab.NAME),
		JOURNAL("Journal", CliOptions.JOURNAL, JournalTab.NAME),
		LOADOUTS("Ship Fittings", CliOptions.LOADOUTS, LoadoutsTab.NAME),
		MATERIALS("Materials", CliOptions.MATERIALS, MaterialsTab.NAME),
		MARKET_ORDERS("Market Orders", CliOptions.MARKET_ORDERS, MarketOrdersTab.NAME),
		MINING("Mining", CliOptions.MARKET_ORDERS, MarketOrdersTab.NAME),
		OVERVIEW_PLANETS("Overview Planets", CliOptions.OVERVIEW, OverviewTab.NAME),
		OVERVIEW_STATIONS("Overview Stations", CliOptions.OVERVIEW, OverviewTab.NAME),
		OVERVIEW_SYSTEMS("Overview Systems", CliOptions.OVERVIEW, OverviewTab.NAME),
		OVERVIEW_CONSTELLATIONS("Overview Constellations", CliOptions.OVERVIEW, OverviewTab.NAME),
		OVERVIEW_REGIONS("Overview Regions", CliOptions.OVERVIEW, OverviewTab.NAME),
		OVERVIEW_GROUPS("Overview Groups", CliOptions.OVERVIEW, OverviewTab.NAME),
		REPROCESSED("Reprocessed", CliOptions.REPROCESSED, ReprocessedTab.NAME),
		ROUTING("Routing", CliOptions.ROUTING, RoutingTab.NAME),
		SKILLS("Skills", CliOptions.SKILLS, ValueTableTab.NAME),
		STOCKPILE("Stockpile", CliOptions.STOCKPILE, StockpileTab.NAME),
		TRACKER("Tracker", CliOptions.TRACKER, TrackerTab.NAME),
		TRANSACTIONS("Transactions", CliOptions.TRANSACTIONS, TransactionTab.NAME),
		TREE_LOCATION("Tree Location", CliOptions.TREE_LOCATION, TreeTab.NAME),
		TREE_CATEGORY("Tree Category", CliOptions.TREE_CATEGORY, TreeTab.NAME),
		;

		private final String name;
		private final String exportName;
		private final String toolName;

		private ExportTool(String name, String exportName, String toolName) {
			this.name = name;
			this.exportName = exportName;
			this.toolName = toolName;
		}

		public String getFilename() {
			return name.replace(" ", "_").toLowerCase();
		}

		public String getName() {
			return name;
		}

		public String getExportName() {
			return exportName;
		}

		public String getToolName() {
			return toolName;
		}
	}

	int export() {
		PriceDataGetter priceDataGetter = new PriceDataGetter();
		priceDataGetter.load();

		ProfileManager profileManager = new ProfileManager();
		profileManager.searchProfile();
		profileManager.loadActiveProfile();

		ProfileData profileData = new ProfileData(profileManager);
		profileData.updateEventLists();

		int fails = 0;
		for (Map.Entry<ExportTool, List<ExportSettings>> entry : CliOptions.get().getExportSettings().entrySet()) {
			ExportTool tool = entry.getKey();
			String toolName = tool.getToolName();
			boolean ok;
			for (ExportSettings exportSettings : entry.getValue()) {
				switch (tool) {
					case ASSETS:
						ok = export(profileData.getAssetsEventList(), TableFormatFactory.assetTableFormat(), toolName, exportSettings);
						break;
					case CONTRACTS:
						ok = export(profileData.getContractItemEventList(), TableFormatFactory.contractsTableFormat(), toolName, exportSettings);
						break;
					case INDUSTRY_JOBS:
						ok = export(profileData.getIndustryJobsEventList(), TableFormatFactory.industryJobTableFormat(), toolName, exportSettings);
						break;
					case SLOTS:
						ok = export(new SlotsData(profileManager, profileData).getData(), TableFormatFactory.slotTableFormat(), toolName, exportSettings);
						break;
					case ISK:
						ok = export(new IskData(profileManager, profileData).getData(), TableFormatFactory.valueTableFormat(), toolName, exportSettings);
						break;
					case ITEMS:
						ok = export(EventListManager.create(StaticData.get().getItems().values()), TableFormatFactory.itemTableFormat(), toolName, exportSettings);
						break;
					case JOURNAL:
						ok = export(profileData.getJournalEventList(), TableFormatFactory.journalTableFormat(), toolName, exportSettings);
						break;
					case LOADOUTS:
						ok = exportLoadout(profileManager, profileData, exportSettings, toolName);
						break;
					case MARKET_ORDERS:
						ok = export(profileData.getMarketOrdersEventList(), TableFormatFactory.marketTableFormat(), toolName, exportSettings);
						break;
					case MATERIALS:
						ok = export(new MaterialsData(profileManager, profileData).getData(CliOptions.get().getMaterialsOwner(), CliOptions.get().isMaterialsOre(), CliOptions.get().isMaterialsPI()), TableFormatFactory.materialTableFormat(), toolName, exportSettings);
						break;
					case MINING:
						ok = export(profileData.getMiningEventList(), TableFormatFactory.miningTableFormat(), toolName, exportSettings);
						break;
					case OVERVIEW_STATIONS:
						ok = exportOverview(profileManager, profileData, exportSettings, toolName, View.STATIONS);
						break;
					case OVERVIEW_PLANETS:
						ok = exportOverview(profileManager, profileData, exportSettings, toolName, View.PLANETS);
						break;
					case OVERVIEW_SYSTEMS:
						ok = exportOverview(profileManager, profileData, exportSettings, toolName, View.SYSTEMS);
						break;
					case OVERVIEW_CONSTELLATIONS:
						ok = exportOverview(profileManager, profileData, exportSettings, toolName, View.CONSTELLATIONS);
						break;
					case OVERVIEW_REGIONS:
						ok = exportOverview(profileManager, profileData, exportSettings, toolName, View.REGIONS);
						break;
					case OVERVIEW_GROUPS:
						ok = exportOverview(profileManager, profileData, exportSettings, toolName, View.GROUPS);
						break;
					case REPROCESSED:
						ok = exportReprocessed(profileManager, profileData, exportSettings, toolName);
						break;
					case ROUTING: //ToDo: Not a table tool
						ok = false;
						break;
					case SKILLS:
						ok = export(profileData.getSkillsEventList(), TableFormatFactory.skillsTableFormat(), toolName, exportSettings);
						break;
					case STOCKPILE:
						ok = export(new StockpileData(profileManager, profileData).getData(), TableFormatFactory.stockpileTableFormat(), toolName, exportSettings);
						break;
					case TRACKER: //ToDo: Not a table tool
						ok = false;
						break;
					case TRANSACTIONS:
						ok = export(profileData.getTransactionsEventList(), TableFormatFactory.transactionTableFormat(), toolName, exportSettings);
						break;
					case TREE_LOCATION:
						ok = export(new TreeData(profileManager, profileData).getDataLocations(), TableFormatFactory.treeTableFormat(), toolName, exportSettings);
						break;
					case TREE_CATEGORY:
						ok = export(new TreeData(profileManager, profileData).getDataCategories(), TableFormatFactory.treeTableFormat(), toolName, exportSettings);
						break;
					default:
						ok = false;
				}
				if (!ok) {
					LOG.error(tool.getName() + " export failed");
					fails++;
				} else {
					LOG.info(tool.getName() + " data exported");
				}
			}
		}
		StringBuilder builder = new StringBuilder();
		if (CliOptions.get().getExportSettings().size() == fails) {
			builder.append("Failed to export data");
		} else {
			builder.append("Data exported");
			if (fails == 0) {
				builder.append(" successfully");
			} else {
				builder.append(" with ");
				builder.append(fails);
				builder.append(" error");
				if (fails > 1) {
					builder.append("s");
				}
			}
			builder.append(" to ");
			builder.append(CliOptions.get().getOutputDirectory());
		}
		if (fails == 0) {
			LOG.info(builder.toString());
		} else {
			LOG.warn(builder.toString());
		}
		return -fails; //Return negative exit code
	}

	private boolean exportLoadout(ProfileManager profileManager, ProfileData profileData, ExportSettings exportSettings, String toolName) {
		Set<String> loadoutsNames = CliOptions.get().getLoadoutsNames();
		Set<Integer> loadoutsIDs = CliOptions.get().getLoadoutsIDs();
		EventList<Loadout> eventList = new LoadoutData(profileManager, profileData).getData();
		if ((loadoutsNames != null && !loadoutsNames.isEmpty())
			|| (loadoutsIDs != null && !loadoutsIDs.isEmpty())) {
			FilterList<Loadout> filterList = new FilterList<>(eventList, new LoadoutMatcher(loadoutsNames, loadoutsIDs));
			return export(filterList, TableFormatFactory.loadoutTableFormat(), toolName, exportSettings);
		} else {
			return export(eventList, TableFormatFactory.loadoutTableFormat(), toolName, exportSettings);
		}
	}

	private boolean exportReprocessed(ProfileManager profileManager, ProfileData profileData, ExportSettings exportSettings, String toolName) {
		Map<Item, Long> items = new HashMap<>();
		Set<Integer> typeIDs = CliOptions.get().getReprocessedIDs();
		if (typeIDs != null) {
			//IDs to Items
			for (Integer typeID : typeIDs) {
				Item item = StaticData.get().getItems().get(typeID);
				if (item != null) {
					items.put(item, 1L);
				}
			}
		}
		Set<String> typeNames = CliOptions.get().getReprocessedNames();
		if (typeNames != null && !typeNames.isEmpty()) {
			if (typeNames.size() > 1) {
				//Build {name, item} Cache
				Map<String, Item> itemsNyName = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
				for (Item item : StaticData.get().getItems().values()) {
					itemsNyName.put(item.getTypeName(), item);
				}
				//Names to Items
				for (String typeName : typeNames) {
					Item item = itemsNyName.get(typeName);
					if (item != null) {
						items.put(item, 1L);
					}
				}
			} else {
				String typeName = typeNames.iterator().next();
				for (Item item : StaticData.get().getItems().values()) {
					if (typeName.equalsIgnoreCase(item.getTypeName())) {
						items.put(item, 1L);
						break;
					}
				}
			}
		}
		return export(new ReprocessedData(profileManager, profileData).getData(items), TableFormatFactory.reprocessedTableFormat(), toolName, exportSettings);
	}

	private boolean exportOverview(ProfileManager profileManager, ProfileData profileData, ExportSettings exportSettings, String toolName, View view) {
		String owner = ""; //ToDo: Owner is not settable (yet?)
		String filterName = exportSettings.getFilterName();
		//Assets Filter
		List<Filter> filter;
		if (filterName == null) {
			filter = new ArrayList<>();
		} else if (filterName.isEmpty()) {
			filter = Settings.get().getCurrentTableFilters(ExportTool.ASSETS.getToolName());
		} else {
			filter = Settings.get().getTableFilters(ExportTool.ASSETS.getToolName()).get(filterName);
			if (filter == null) {
				LOG.error(toolName + ": No such saved filter (" + filterName + ")");
				return false;
			}
		}
		//Assets Filtered
		FilterList<MyAsset> filterList = new FilterList<>(profileData.getAssetsEventList(), new FilterLogicalMatcher<>(TableFormatFactory.assetTableFormat(), null, filter));
		//Overview
		EnumTableFormatAdaptor<OverviewTableFormat, Overview> tableFormat = TableFormatFactory.overviewTableFormat();
		OverviewTab.updateShownColumns(tableFormat, view);
		return ExportTableData.exportEmpty(new OverviewData(profileManager, profileData).getData(filterList, owner, view), tableFormat, toolName, exportSettings);
	}

	private <T extends Enum<T> & EnumTableColumn<Q>, Q> boolean export(EventList<Q> data, final SimpleTableFormat<Q> tableFormat, String toolName, ExportSettings exportSettings) {
		return ExportTableData.exportAutoNoCache(data, tableFormat, toolName, exportSettings);
	}

	private static class LoadoutMatcher implements Matcher<Loadout> {
		private final Set<String> loadoutsNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		private final Set<Integer> loadoutsIDs;

		public LoadoutMatcher(Set<String> loadoutsNames, Set<Integer> loadoutsIDs) {
			if (loadoutsNames != null) {
				this.loadoutsNames.addAll(loadoutsNames);
			}
			if (loadoutsIDs != null) {
				this.loadoutsIDs = loadoutsIDs;
			} else {
				this.loadoutsIDs = new HashSet<>();
			}
		}

		@Override
		public boolean matches(Loadout item) {
			if (loadoutsNames.contains(item.getShipItemName())) {
				return true;
			} else if (loadoutsNames.contains(item.getShipTypeName())) {
				return true;
			} else if (loadoutsIDs.contains(item.getShipTypeID())) {
				return true;
			}
			return false;
		}
	}

}
