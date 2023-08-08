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

package net.nikr.eve.jeveasset.data.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderRange;
import net.nikr.eve.jeveasset.data.settings.tag.Tag;
import net.nikr.eve.jeveasset.data.settings.tag.TagID;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.dialogs.settings.SoundsSettingsPanel.SoundOption;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog.Formula;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps.Jump;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.sounds.Sound;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.ExtractionsTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.ExtractionsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.orders.Outbid;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import net.nikr.eve.jeveasset.i18n.TabsJobs;
import net.nikr.eve.jeveasset.i18n.TabsMining;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.i18n.TabsTransaction;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.SettingsWriter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings {

	private static final Logger LOG = LoggerFactory.getLogger(Settings.class);

	public static enum SettingFlag {
		FLAG_IGNORE_SECURE_CONTAINERS,
		FLAG_FILTER_ON_ENTER,
		FLAG_REPROCESS_COLORS,
		FLAG_INCLUDE_SELL_ORDERS,
		FLAG_INCLUDE_BUY_ORDERS,
		FLAG_INCLUDE_SELL_CONTRACTS,
		FLAG_INCLUDE_BUY_CONTRACTS,
		FLAG_INCLUDE_MANUFACTURING,
		FLAG_INCLUDE_COPYING,
		FLAG_HIGHLIGHT_SELECTED_ROWS,
		FLAG_STOCKPILE_FOCUS_TAB,
		FLAG_STOCKPILE_HALF_COLORS,
		FLAG_BLUEPRINT_BASE_PRICE_TECH_1,
		FLAG_BLUEPRINT_BASE_PRICE_TECH_2,
		FLAG_TRANSACTION_HISTORY,
		FLAG_JOURNAL_HISTORY,
		FLAG_MARKET_ORDER_HISTORY,
		FLAG_ASKED_CHECK_ALL_TRACKER,
		FLAG_TRACKER_USE_ASSET_PRICE_FOR_SELL_ORDERS,
		FLAG_FOCUS_EVE_ONLINE_ON_ESI_UI_CALLS,
		FLAG_SAVE_TOOLS_ON_EXIT,
		FLAG_SAVE_CONTRACT_HISTORY,
		FLAG_SAVE_MINING_HISTORY,
		FLAG_MANUFACTURING_DEFAULT,
		FLAG_EASY_CHART_COLORS
	}

	public static enum TransactionProfitPrice {
		LASTEST() {
			@Override
			public String getText() {
				return DialoguesSettings.get().transactionsPriceLatest();
			}
		},
		AVERAGE() {
			@Override
			public String getText() {
				return DialoguesSettings.get().transactionsPriceAverage();
			}
		},
		MINIMUM() {
			@Override
			public String getText() {
				return DialoguesSettings.get().transactionsPriceMinimum();
			}
		},
		MAXIMUM() {
			@Override
			public String getText() {
				return DialoguesSettings.get().transactionsPriceMaximum();
			}
		};

		@Override
		public String toString() {
			return getText();
		}

		protected abstract String getText();
	}

	private static final SettingsLock LOCK = new SettingsLock();
	private static Settings settings;
	private static boolean testMode = false;

//External
	//Price						Saved by PriceDataGetter.process() in pricedata.dat (on api update)
	private Map<Integer, PriceData> priceDatas = new HashMap<>(); //TypeID : int
//API Data
	//Api id to owner name		Saved by TaskDialog.update() (on API update)
	private final Map<Long, Date> ownersNextUpdate = new HashMap<>();
	private final Map<Long, String> owners = new HashMap<>();
//!! - Values
	//OK - Custom Price			Saved by JUserListPanel.edit()/delete() + SettingsDialog.save()
	//Lock OK
	private Map<Integer, UserItem<Integer, Double>> userPrices = new HashMap<>(); //TypeID : int
	//OK - Custom Item Name		Saved by JUserListPanel.edit()/delete() + SettingsDialog.save()
	//Lock OK
	private Map<Long, UserItem<Long, String>> userNames = new HashMap<>(); //ItemID : long
	//Eve Item Name				Saved by TaskDialog.update() (on API update)
	//Lock ???
	private Map<Long, String> eveNames = new HashMap<>();
//!! - Stockpile				Saved by StockpileTab.removeItems() / addStockpile() / removeStockpile()
	//							Could be more selective...
	//Lock FAIL!!!
	private final List<Stockpile> stockpiles = new ArrayList<>();
	private int stockpileColorGroup2 = 100;
	private int stockpileColorGroup3 = 0;
	private final StockpileGroupSettings stockpileGroupSettings = new StockpileGroupSettings();
//Routing						Saved by ???
	//Lock ???
	private final RoutingSettings routingSettings = new RoutingSettings();
//Overview						Saved by JOverviewMenu.ListenerClass.NEW/DELETE/RENAME
	//Lock OK
	private final Map<String, OverviewGroup> overviewGroups = new HashMap<>();
//Export						Saved in ExportDialog.saveSettings()
	//Lock OK
	private final CopySettings copySettings = new CopySettings();
	private final Map<String, ExportSettings> exportSettings = new HashMap<>();
//Tracker						Saved by TaskDialog.update() (on API update)
	private final TrackerSettings trackerSettings = new TrackerSettings();
//Price History
	private final Map<String, Set<Integer>> priceHistorySets = new HashMap<>();
//Runtime flags					Is not saved to file
	private boolean settingsLoadError = false;
//Settings Dialog:				Saved by SettingsDialog.save()
	//Lock OK
	//Mixed boolean flags
	private final Map<SettingFlag, Boolean> flags = new EnumMap<>(SettingFlag.class);
	//Price
	private PriceDataSettings priceDataSettings = new PriceDataSettings();
	//Proxy (API)
	private ProxyData proxyData = new ProxyData();
	//FIXME - - > Settings: Create windows settings
	//Window
	//							Saved by MainWindow.ListenerClass.componentMoved() (on change)
	private Point windowLocation = new Point(0, 0);
	//							Saved by MainWindow.ListenerClass.componentResized() (on change)
	private Dimension windowSize = new Dimension(800, 600);
	//							Saved by MainWindow.ListenerClass.componentMoved() (on change)
	private boolean windowMaximized = false;
	//							Saved by SettingsDialog.save()
	private boolean windowAutoSave = true;
	private boolean windowAlwaysOnTop = false;
	//Assets
	private int maximumPurchaseAge = 0;
	private int transactionProfitMargin = 0;
	private TransactionProfitPrice transactionProfitPrice = TransactionProfitPrice.LASTEST;
	//Reprocess price
	private ReprocessSettings reprocessSettings = new ReprocessSettings();
	//Public Market Orders Last Update
	private Date publicMarketOrdersLastUpdate = null;
	//Public Market Orders Next Update
	private Date publicMarketOrdersNextUpdate = getNow();
	//Faction Warfare System Owners
	private Map<Long, String> factionWarfareSystemOwners = new HashMap<>();
	//Faction Warfare Next Update
	private Date factionWarfareNextUpdate = getNow();
	//Market Orders Outbid
	private Map<Long, Outbid> marketOrdersOutbid = new HashMap<>();
	//SellOrderRange
	private MarketOrderRange outbidOrderRange = MarketOrderRange.REGION;
	//Expire Warning Days
	private final MarketOrdersSettings marketOrdersSettings = new MarketOrdersSettings();
	//Cache
	private Boolean filterOnEnter = null; //Filter tools
	private Boolean highlightSelectedRows = null; //Assets
	private Boolean reprocessColors = null; //Assets
	private Boolean stockpileHalfColors = null; //Stockpile
//Table settings
	//Filters					Saved by ExportFilterControl.saveSettings()
	//Lock OK
	private final Map<String, Map<String, List<Filter>>> tableFilters = new HashMap<>();
	//Columns					Saved by EnumTableFormatAdaptor.getMenu() - Reset
	//									 EditColumnsDialog.save() - Edit Columns
	//									 JAutoColumnTable.ListenerClass.mouseReleased() - Moved
	//									 ViewManager.loadView() - Load View
	//Lock OK
	private final Map<String, Map<String, List<Filter>>> defaultTableFilters = new HashMap<>();
	private final Map<String, List<Filter>> currentTableFilters = new HashMap<>();
	private final Map<String, Boolean> currentTableFiltersShown = new HashMap<>();
	private final Map<String, List<SimpleColumn>> tableColumns = new HashMap<>();
	//Column Width				Saved by JAutoColumnTable.saveColumnsWidth()
	//Lock OK
	private final Map<String, Map<String, Integer>> tableColumnsWidth = new HashMap<>();
	//Resize Mode				Saved by EnumTableFormatAdaptor.getMenu()
	//Lock OK
	private final Map<String, ResizeMode> tableResize = new HashMap<>();
	//Views						Saved by EnumTableFormatAdaptor.getMenu() - New
	//									 ViewManager.rename() - Rename
	//									 ViewManager.delete() - Delete
	//Lock OK
	private final Map<String, Map<String, View>> tableViews = new HashMap<>();
	//Formula Columns
	private final Map<String, List<Formula>> tableFormulas = new HashMap<>();
	//Jump Columns
	private final Map<String, List<Jump>> tableJumps = new HashMap<>();
//Tags						Saved by JMenuTags.addTag()/removeTag() + SettingsDialog.save()
	//Lock OK
	private final Map<String, Tag> tags = new HashMap<>();
	private final Map<TagID, Tags> tagIds = new HashMap<>();
//Changed
	private final Map<String, Date> tableChanged = new HashMap<>();
//Tools
	private final List<String> showTools = new ArrayList<>();
//Colors
	private final ColorSettings colorSettings = new ColorSettings();
//Sounds
	private final Map<SoundOption, Sound> soundSettings = new EnumMap<>(SoundOption.class);
//Manufacturing
	private final ManufacturingSettings manufacturingSettings = new ManufacturingSettings();

	protected Settings() {
		//Settings
		flags.put(SettingFlag.FLAG_FILTER_ON_ENTER, false); //Cached
		flags.put(SettingFlag.FLAG_HIGHLIGHT_SELECTED_ROWS, true); //Cached
		flags.put(SettingFlag.FLAG_REPROCESS_COLORS, false); //Cached
		flags.put(SettingFlag.FLAG_IGNORE_SECURE_CONTAINERS, true);
		flags.put(SettingFlag.FLAG_STOCKPILE_FOCUS_TAB, true);
		flags.put(SettingFlag.FLAG_STOCKPILE_HALF_COLORS, false); //Cached
		flags.put(SettingFlag.FLAG_INCLUDE_SELL_ORDERS, true);
		flags.put(SettingFlag.FLAG_INCLUDE_BUY_ORDERS, false);
		flags.put(SettingFlag.FLAG_INCLUDE_SELL_CONTRACTS, false);
		flags.put(SettingFlag.FLAG_INCLUDE_BUY_CONTRACTS, false);
		flags.put(SettingFlag.FLAG_INCLUDE_MANUFACTURING, false);
		flags.put(SettingFlag.FLAG_INCLUDE_COPYING, false);
		flags.put(SettingFlag.FLAG_BLUEPRINT_BASE_PRICE_TECH_1, true);
		flags.put(SettingFlag.FLAG_BLUEPRINT_BASE_PRICE_TECH_2, false);
		flags.put(SettingFlag.FLAG_TRANSACTION_HISTORY, true);
		flags.put(SettingFlag.FLAG_JOURNAL_HISTORY, true);
		flags.put(SettingFlag.FLAG_MARKET_ORDER_HISTORY, true);
		flags.put(SettingFlag.FLAG_ASKED_CHECK_ALL_TRACKER, false);
		flags.put(SettingFlag.FLAG_TRACKER_USE_ASSET_PRICE_FOR_SELL_ORDERS, false);
		flags.put(SettingFlag.FLAG_FOCUS_EVE_ONLINE_ON_ESI_UI_CALLS, true);
		flags.put(SettingFlag.FLAG_SAVE_TOOLS_ON_EXIT, false);
		flags.put(SettingFlag.FLAG_SAVE_CONTRACT_HISTORY, true);
		flags.put(SettingFlag.FLAG_SAVE_MINING_HISTORY, true);
		flags.put(SettingFlag.FLAG_MANUFACTURING_DEFAULT, true);
		flags.put(SettingFlag.FLAG_EASY_CHART_COLORS, false);
		cacheFlags();
		//Default Filters
		List<Filter> filter;
		//Market Orders: Default Filters
		Map<String, List<Filter>> marketOrdersDefaultFilters = new HashMap<>();
		filter = new ArrayList<>();
		filter.add(new Filter(Filter.LogicType.AND, MarketTableFormat.ORDER_TYPE, Filter.CompareType.EQUALS, TabsOrders.get().buy()));
		filter.add(new Filter(Filter.LogicType.AND, MarketTableFormat.STATUS, Filter.CompareType.EQUALS, TabsOrders.get().statusActive()));
		marketOrdersDefaultFilters.put(TabsOrders.get().activeBuyOrders(), filter);
		filter = new ArrayList<>();
		filter.add(new Filter(Filter.LogicType.AND, MarketTableFormat.ORDER_TYPE, Filter.CompareType.EQUALS, TabsOrders.get().sell()));
		filter.add(new Filter(Filter.LogicType.AND, MarketTableFormat.STATUS, Filter.CompareType.EQUALS, TabsOrders.get().statusActive()));
		marketOrdersDefaultFilters.put(TabsOrders.get().activeSellOrders(), filter);
		defaultTableFilters.put(MarketOrdersTab.NAME, marketOrdersDefaultFilters);
		//Transactions: Default Filters
		Map<String, List<Filter>> transactionsDefaultFilters = new HashMap<>();
		filter = new ArrayList<>();
		filter.add(new Filter(Filter.LogicType.AND, TransactionTableFormat.TYPE, Filter.CompareType.EQUALS, TabsTransaction.get().buy()));
		transactionsDefaultFilters.put(TabsTransaction.get().buy(), filter);
		filter = new ArrayList<>();
		filter.add(new Filter(Filter.LogicType.AND, TransactionTableFormat.TYPE, Filter.CompareType.EQUALS, TabsTransaction.get().sell()));
		transactionsDefaultFilters.put(TabsTransaction.get().sell(), filter);
		defaultTableFilters.put(TransactionTab.NAME, transactionsDefaultFilters);
		//Industry Jobs: Default Filters
		Map<String, List<Filter>> industryJobsTabDefaultFilters = new HashMap<>();
		filter = new ArrayList<>();
		filter.add(new Filter(Filter.LogicType.AND, IndustryJobTableFormat.STATE, Filter.CompareType.EQUALS_NOT, MyIndustryJob.IndustryJobState.STATE_DELIVERED.toString()));
		filter.add(new Filter(Filter.LogicType.AND, IndustryJobTableFormat.STATE, Filter.CompareType.EQUALS_NOT, MyIndustryJob.IndustryJobState.STATE_CANCELLED.toString()));
		filter.add(new Filter(Filter.LogicType.AND, IndustryJobTableFormat.STATE, Filter.CompareType.EQUALS_NOT, MyIndustryJob.IndustryJobState.STATE_REVERTED.toString()));
		industryJobsTabDefaultFilters.put(TabsJobs.get().active(), filter);
		filter = new ArrayList<>();
		filter.add(new Filter(Filter.LogicType.OR, IndustryJobTableFormat.STATE, Filter.CompareType.EQUALS, MyIndustryJob.IndustryJobState.STATE_DELIVERED.toString()));
		filter.add(new Filter(Filter.LogicType.OR, IndustryJobTableFormat.STATE, Filter.CompareType.EQUALS, MyIndustryJob.IndustryJobState.STATE_CANCELLED.toString()));
		filter.add(new Filter(Filter.LogicType.OR, IndustryJobTableFormat.STATE, Filter.CompareType.EQUALS, MyIndustryJob.IndustryJobState.STATE_REVERTED.toString()));
		industryJobsTabDefaultFilters.put(TabsJobs.get().completed(), filter);
		defaultTableFilters.put(IndustryJobsTab.NAME, industryJobsTabDefaultFilters);
		//Extractions Default Filters
		Map<String, List<Filter>> extractionsDefaultFilters = new HashMap<>();
		filter = new ArrayList<>();
		filter.add(new Filter(Filter.LogicType.OR, ExtractionsTableFormat.ARRIVAL, Filter.CompareType.NEXT_DAYS, "2"));
		filter.add(new Filter(Filter.LogicType.OR, ExtractionsTableFormat.DECAY, Filter.CompareType.LAST_DAYS, "2"));
		extractionsDefaultFilters.put(TabsMining.get().extractionsActiveSoon(), filter);
		defaultTableFilters.put(ExtractionsTab.NAME, extractionsDefaultFilters);
	}

	public static Settings get() {
		load();
		return settings;
	}

	public static void setTestMode(boolean testMode) {
		Settings.testMode = testMode;
	}

	public static boolean isTestMode() {
		return testMode;
	}

	public static void lock(String msg) {
		LOCK.lock(msg);
	}

	public static void unlock(String msg) {
		LOCK.unlock(msg);
	}

	public static boolean ignoreSave() {
		return LOCK.ignoreSave();
	}

	public static void waitForEmptySaveQueue() {
		LOCK.waitForEmptySaveQueue();
	}

	public static void saveStart() {
		LOCK.saveStart();
	}

	public static void saveEnd() {
		LOCK.saveEnd();
	}

	public synchronized static void load() {
		if (settings == null) {
			SplashUpdater.setProgress(30);
			settings = SettingsReader.load(new EmptySettingsFactory(), FileUtil.getPathSettings());
			SplashUpdater.setProgress(35);
		}
	}

	/***
	 *
	 * @return
	 */
	public Map<String, ExportSettings> getExportSettings() {
		return exportSettings;
	}

	/***
	 *
	 * @param toolName
	 * @return
	 */
	public ExportSettings getExportSettings(String toolName) {
		if (!exportSettings.containsKey(toolName)) {
			exportSettings.put(toolName, new ExportSettings(toolName));
		}
		return exportSettings.get(toolName);
	}

	public CopySettings getCopySettings() {
		return copySettings;
	}

	public static void saveSettings() {
		LOCK.lock("Save Settings");
		try {
			SettingsWriter.save(settings, FileUtil.getPathSettings());
		} finally {
			LOCK.unlock("Save Settings");
		}
	}

	public TrackerSettings getTrackerSettings() {
		return trackerSettings;
	}

	public Map<String, Set<Integer>> getPriceHistorySets() {
		return priceHistorySets;
	}

	public PriceDataSettings getPriceDataSettings() {
		return priceDataSettings;
	}

	public void setPriceDataSettings(final PriceDataSettings priceDataSettings) {
		this.priceDataSettings = priceDataSettings;
	}

	public Map<Integer, UserItem<Integer, Double>> getUserPrices() {
		return userPrices;
	}

	public void setUserPrices(final Map<Integer, UserItem<Integer, Double>> userPrices) {
		this.userPrices = userPrices;
	}

	public Map<Long, UserItem<Long, String>> getUserItemNames() {
		return userNames;
	}

	public void setUserItemNames(final Map<Long, UserItem<Long, String>> userItemNames) {
		this.userNames = userItemNames;
	}

	public void setPriceData(final Map<Integer, PriceData> priceData) {
		this.priceDatas = priceData;
	}

	public Map<Long, String> getEveNames() {
		return eveNames;
	}

	public void setEveNames(Map<Long, String> eveNames) {
		this.eveNames = eveNames;
	}

	public Map<Integer, PriceData> getPriceData() {
		return priceDatas;
	}

	public Map<SettingFlag, Boolean> getFlags() {
		return flags;
	}

	public final void cacheFlags() {
		highlightSelectedRows = flags.get(SettingFlag.FLAG_HIGHLIGHT_SELECTED_ROWS);
		filterOnEnter = flags.get(SettingFlag.FLAG_FILTER_ON_ENTER);
		reprocessColors = flags.get(SettingFlag.FLAG_REPROCESS_COLORS);
		stockpileHalfColors = flags.get(SettingFlag.FLAG_STOCKPILE_HALF_COLORS);
	}

	public ReprocessSettings getReprocessSettings() {
		return reprocessSettings;
	}

	public void setReprocessSettings(final ReprocessSettings reprocessSettings) {
		this.reprocessSettings = reprocessSettings;
	}

	public RoutingSettings getRoutingSettings() {
		return routingSettings;
	}

	public Map<String, List<Jump>> getTableJumps() {
		return tableJumps;
	}

	public List<Jump> getTableJumps(String toolName) {
		List<Jump> jumps = tableJumps.get(toolName);
		if (jumps == null) {
			jumps = new ArrayList<>();
			tableJumps.put(toolName, jumps);
		}
		return jumps;
	}

	public Map<SoundOption, Sound> getSoundSettings() {
		return soundSettings;
	}

	public Map<String, Date> getTableChanged() {
		return tableChanged;
	}

	public Date getTableChanged(String toolName) {
		Date date = tableChanged.get(toolName);
		if (date == null) {
			date = new Date();
			tableChanged.put(toolName, date);
		}
		return date;
	}

	public ProxyData getProxyData() {
		return proxyData;
	}

	public void setProxyData(ProxyData proxyData) {
		this.proxyData = proxyData;
	}

	public Map<Long, Date> getOwnersNextUpdate() {
		return ownersNextUpdate;
	}

	public Map<Long, String> getOwners() {
		return owners;
	}

	public Map<String, Map<String, List<Filter>>> getTableFilters() {
		return tableFilters;
	}

	public Map<String, List<Filter>> getTableFilters(final String key) {
		if (!tableFilters.containsKey(key)) {
			tableFilters.put(key, new HashMap<>());
		}
		return tableFilters.get(key);
	}

	public Map<String, List<Filter>> getDefaultTableFilters(final String key) {
		if (!defaultTableFilters.containsKey(key)) {
			defaultTableFilters.put(key, new HashMap<>());
		}
		return defaultTableFilters.get(key);
	}

	/***
	 * @return Current table filters, list may be empty but should never be null.
	 */
	public Map<String, List<Filter>> getCurrentTableFilters() {
		return currentTableFilters;
	}

	/***
	 * @param tableName Table to look up.
	 * @return The value of the filter if {@code key} is found. If {@code key} is not found it will be added with an
	 * empty list and then returned.
	 */
	public List<Filter> getCurrentTableFilters(final String tableName) {
		if (!currentTableFilters.containsKey(tableName)) {
			currentTableFilters.put(tableName, new ArrayList<>());
		}
		return currentTableFilters.get(tableName);
	}

	/***
	 * @return current table filters visibility state, list may be empty but should never be null.
	 */
	public Map<String, Boolean> getCurrentTableFiltersShown() {
		return currentTableFiltersShown;
	}

	/***
	 * @param tableName Table to look up.
	 * @return The value of the filter visibility state if key is found. If key is not found it will be added as visible
	 * and then returned.
	 */
	public boolean getCurrentTableFiltersShown(final String tableName) {
		if (!currentTableFiltersShown.containsKey(tableName)) {
			currentTableFiltersShown.put(tableName, true);
		}
		return currentTableFiltersShown.get(tableName);
	}

	public Map<String, List<SimpleColumn>> getTableColumns() {
		return tableColumns;
	}

	public Map<String, Map<String, Integer>> getTableColumnsWidth() {
		return tableColumnsWidth;
	}

	public Map<String, ResizeMode> getTableResize() {
		return tableResize;
	}

	public Map<String, Map<String, View>> getTableViews() {
		return tableViews;
	}

	public Map<String, View> getTableViews(String name) {
		Map<String, View> views = tableViews.get(name);
		if (views == null) {
			views = new TreeMap<>(new CaseInsensitiveComparator());
			tableViews.put(name, views);
		}
		return views;
	}

	public Map<String, List<Formula>> getTableFormulas() {
		return tableFormulas;
	}

	public List<Formula> getTableFormulas(String name) {
		List<Formula> formula = tableFormulas.get(name);
		if (formula == null) {
			formula = new ArrayList<>();
			tableFormulas.put(name, formula);
		}
		return formula;
	}

	public Map<String, Tag> getTags() {
		return tags;
	}

	public Tags getTags(TagID tagID) {
		Tags set = tagIds.get(tagID);
		if (set == null) {
			set = new Tags();
			tagIds.put(tagID, set);
		}
		return set;
	}

	public Map<Long, String> getFactionWarfareSystemOwners() {
		return factionWarfareSystemOwners;
	}

	public void setFactionWarfareSystemOwners(Map<Long, String> factionWarfareSystemOwners) {
		this.factionWarfareSystemOwners = factionWarfareSystemOwners;
	}

	public Date getFactionWarfareNextUpdate() {
		return factionWarfareNextUpdate;
	}

	public void setFactionWarfareNextUpdate(Date factionWarfareNextUpdate) {
		this.factionWarfareNextUpdate = factionWarfareNextUpdate;
	}

	public ManufacturingSettings getManufacturingSettings() {
		return manufacturingSettings;
	}

	public Date getPublicMarketOrdersNextUpdate() {
		return publicMarketOrdersNextUpdate;
	}

	public void setPublicMarketOrdersNextUpdate(Date publicMarketOrdersNextUpdate) {
		this.publicMarketOrdersNextUpdate = publicMarketOrdersNextUpdate;
	}

	public Date getPublicMarketOrdersLastUpdate() {
		return publicMarketOrdersLastUpdate;
	}

	public void setPublicMarketOrdersLastUpdate(Date publicMarketOrdersLastUpdate) {
		this.publicMarketOrdersLastUpdate = publicMarketOrdersLastUpdate;
	}

	public MarketOrderRange getOutbidOrderRange() {
		return outbidOrderRange;
	}

	public void setOutbidOrderRange(MarketOrderRange sellOrderOutbidRange) {
		this.outbidOrderRange = sellOrderOutbidRange;
	}

	public Map<Long, Outbid> getMarketOrdersOutbid() {
		return marketOrdersOutbid;
	}

	public void setMarketOrdersOutbid(Map<Long, Outbid> marketOrdersOutbid) {
		this.marketOrdersOutbid = marketOrdersOutbid;
	}

	public int getMaximumPurchaseAge() {
		return maximumPurchaseAge;
	}

	public void setMaximumPurchaseAge(final int maximumPurchaseAge) {
		this.maximumPurchaseAge = maximumPurchaseAge;
	}

	public TransactionProfitPrice getTransactionProfitPrice() {
		return transactionProfitPrice;
	}

	public void setTransactionProfitPrice(TransactionProfitPrice transactionProfitPrice) {
		this.transactionProfitPrice = transactionProfitPrice;
	}

	public int getTransactionProfitMargin() {
		return transactionProfitMargin;
	}

	public void setTransactionProfitMargin(int transactionProfitMargin) {
		this.transactionProfitMargin = transactionProfitMargin;
	}

	public boolean isFilterOnEnter() {
		if (filterOnEnter == null) {
			filterOnEnter = flags.get(SettingFlag.FLAG_FILTER_ON_ENTER);
		}
		return filterOnEnter;
	}

	public void setFilterOnEnter(final boolean filterOnEnter) {
		flags.put(SettingFlag.FLAG_FILTER_ON_ENTER, filterOnEnter); //Save & Load
		this.filterOnEnter = filterOnEnter;
	}

	public boolean isHighlightSelectedRows() { //High volume call - Map.get is too slow, use cache
		return highlightSelectedRows;
	}

	public void setHighlightSelectedRows(final boolean highlightSelectedRows) {
		flags.put(SettingFlag.FLAG_HIGHLIGHT_SELECTED_ROWS, highlightSelectedRows);
		this.highlightSelectedRows = highlightSelectedRows;
	}

	public boolean isIgnoreSecureContainers() {
		return flags.get(SettingFlag.FLAG_IGNORE_SECURE_CONTAINERS);
	}

	public void setIgnoreSecureContainers(final boolean ignoreSecureContainers) {
		flags.put(SettingFlag.FLAG_IGNORE_SECURE_CONTAINERS, ignoreSecureContainers);
	}

	public boolean isReprocessColors() { //High volume call - Map.get is too slow, use cache
		return reprocessColors;
	}

	public void setReprocessColors(final boolean reprocessColors) {
		flags.put(SettingFlag.FLAG_REPROCESS_COLORS, reprocessColors);
		this.reprocessColors = reprocessColors;
	}

	public boolean isStockpileFocusTab() {
		return flags.get(SettingFlag.FLAG_STOCKPILE_FOCUS_TAB);
	}

	public void setStockpileFocusTab(final boolean stockpileFocusOnAdd) {
		flags.put(SettingFlag.FLAG_STOCKPILE_FOCUS_TAB, stockpileFocusOnAdd);
	}

	public boolean isStockpileHalfColors() {
		return stockpileHalfColors;
	}

	public void setStockpileHalfColors(final boolean stockpileHalfColors) {
		flags.put(SettingFlag.FLAG_STOCKPILE_HALF_COLORS, stockpileHalfColors);
		this.stockpileHalfColors = stockpileHalfColors;
	}

	public boolean isIncludeSellOrders() {
		return flags.get(SettingFlag.FLAG_INCLUDE_SELL_ORDERS);
	}

	public void setIncludeSellOrders(final boolean includeSellOrders) {
		flags.put(SettingFlag.FLAG_INCLUDE_SELL_ORDERS, includeSellOrders);
	}

	public boolean isIncludeBuyOrders() {
		return flags.get(SettingFlag.FLAG_INCLUDE_BUY_ORDERS);
	}

	public void setIncludeBuyOrders(final boolean includeBuyOrders) {
		flags.put(SettingFlag.FLAG_INCLUDE_BUY_ORDERS, includeBuyOrders);
	}

	public boolean isIncludeBuyContracts() {
		return flags.get(SettingFlag.FLAG_INCLUDE_BUY_CONTRACTS);
	}

	public void setIncludeBuyContracts(final boolean includeBuyContracts) {
		flags.put(SettingFlag.FLAG_INCLUDE_BUY_CONTRACTS, includeBuyContracts);
	}

	public boolean isIncludeSellContracts() {
		return flags.get(SettingFlag.FLAG_INCLUDE_SELL_CONTRACTS);
	}

	public void setIncludeSellContracts(final boolean includeSellContracts) {
		flags.put(SettingFlag.FLAG_INCLUDE_SELL_CONTRACTS, includeSellContracts);
	}

	public boolean isIncludeManufacturing() {
		return flags.get(SettingFlag.FLAG_INCLUDE_MANUFACTURING);
	}

	public boolean setIncludeManufacturing(final boolean includeManufacturing) {
		return flags.put(SettingFlag.FLAG_INCLUDE_MANUFACTURING, includeManufacturing);
	}

	public boolean isIncludeCopying() {
		return flags.get(SettingFlag.FLAG_INCLUDE_COPYING);
	}

	public boolean setIncludeCopying(final boolean includeCopying) {
		return flags.put(SettingFlag.FLAG_INCLUDE_COPYING, includeCopying);
	}

	public boolean isBlueprintBasePriceTech1() {
		return flags.get(SettingFlag.FLAG_BLUEPRINT_BASE_PRICE_TECH_1);
	}

	public void setBlueprintBasePriceTech1(final boolean blueprintsTech1) {
		flags.put(SettingFlag.FLAG_BLUEPRINT_BASE_PRICE_TECH_1, blueprintsTech1);
	}

	public boolean isBlueprintBasePriceTech2() {
		return flags.get(SettingFlag.FLAG_BLUEPRINT_BASE_PRICE_TECH_2);
	}

	public void setBlueprintBasePriceTech2(final boolean blueprintsTech2) {
		flags.put(SettingFlag.FLAG_BLUEPRINT_BASE_PRICE_TECH_2, blueprintsTech2);
	}

	public boolean isManufacturingDefault() {
		return flags.get(SettingFlag.FLAG_MANUFACTURING_DEFAULT);
	}

	public void setManufacturingDefault(final boolean manufacturingDefault) {
		flags.put(SettingFlag.FLAG_MANUFACTURING_DEFAULT, manufacturingDefault);
	}

	public boolean isEasyChartColors() {
		return flags.get(SettingFlag.FLAG_EASY_CHART_COLORS);
	}

	public void setEasyChartColors(final boolean easyChartColors) {
		flags.put(SettingFlag.FLAG_EASY_CHART_COLORS, easyChartColors);
	}

	public boolean isTransactionHistory() {
		return flags.get(SettingFlag.FLAG_TRANSACTION_HISTORY);
	}

	public void setTransactionHistory(final boolean transactionHistory) {
		flags.put(SettingFlag.FLAG_TRANSACTION_HISTORY, transactionHistory);
	}

	public boolean isJournalHistory() {
		return flags.get(SettingFlag.FLAG_JOURNAL_HISTORY);
	}

	public void setJournalHistory(final boolean journalHistory) {
		flags.put(SettingFlag.FLAG_JOURNAL_HISTORY, journalHistory);
	}
	public boolean isMarketOrderHistory() {
		return flags.get(SettingFlag.FLAG_MARKET_ORDER_HISTORY);
	}

	public void setMarketOrderHistory(final boolean marketOrderHistory) {
		flags.put(SettingFlag.FLAG_MARKET_ORDER_HISTORY, marketOrderHistory);
	}

	public boolean isContractHistory() {
		return flags.get(SettingFlag.FLAG_SAVE_CONTRACT_HISTORY);
	}

	public void setContractHistory(final boolean contractHistory) {
		flags.put(SettingFlag.FLAG_SAVE_CONTRACT_HISTORY, contractHistory);
	}

	public boolean isMiningHistory() {
		return flags.get(SettingFlag.FLAG_SAVE_MINING_HISTORY);
	}

	public void setMiningHistory(final boolean contractHistory) {
		flags.put(SettingFlag.FLAG_SAVE_MINING_HISTORY, contractHistory);
	}

	public MarketOrdersSettings getMarketOrdersSettings() {
		return marketOrdersSettings;
	}

	public boolean isAskedCheckAllTracker() {
		return flags.get(SettingFlag.FLAG_ASKED_CHECK_ALL_TRACKER);
	}

	public void setAskedCheckAllTracker(final boolean checkAllTracker) {
		flags.put(SettingFlag.FLAG_ASKED_CHECK_ALL_TRACKER, checkAllTracker);
	}

	public boolean isTrackerUseAssetPriceForSellOrders() {
		return flags.get(SettingFlag.FLAG_TRACKER_USE_ASSET_PRICE_FOR_SELL_ORDERS);
	}

	public void setTrackerUseAssetPriceForSellOrders(final boolean checkAllTracker) {
		flags.put(SettingFlag.FLAG_TRACKER_USE_ASSET_PRICE_FOR_SELL_ORDERS, checkAllTracker);
	}

	public boolean isFocusEveOnlineOnEsiUiCalls() {
		return flags.get(SettingFlag.FLAG_FOCUS_EVE_ONLINE_ON_ESI_UI_CALLS);
	}

	public void setFocusEveOnlineOnEsiUiCalls(final boolean focusEveOnlineOnEsiUiCalls) {
		flags.put(SettingFlag.FLAG_FOCUS_EVE_ONLINE_ON_ESI_UI_CALLS, focusEveOnlineOnEsiUiCalls);
	}

	public boolean isSaveToolsOnExit() {
		return flags.get(SettingFlag.FLAG_SAVE_TOOLS_ON_EXIT);
	}

	public void setSaveToolsOnExit(final boolean saveToolsOnExit) {
		flags.put(SettingFlag.FLAG_SAVE_TOOLS_ON_EXIT, saveToolsOnExit);
	}

	public List<String> getShowTools() {
		return showTools;
	}

	public ColorSettings getColorSettings() {
		return colorSettings;
	}

	public List<Stockpile> getStockpiles() {
		return stockpiles;
	}

	public StockpileGroupSettings getStockpileGroupSettings() {
		return stockpileGroupSettings;
	}

	public int getStockpileColorGroup2() {
		return stockpileColorGroup2;
	}

	public void setStockpileColorGroup2(int stockpileColorGroup1) {
		this.stockpileColorGroup2 = stockpileColorGroup1;
	}

	public int getStockpileColorGroup3() {
		return stockpileColorGroup3;
	}

	public void setStockpileColorGroup3(int stockpileColorGroup2) {
		this.stockpileColorGroup3 = stockpileColorGroup2;
	}

	//Window
	public Point getWindowLocation() {
		return windowLocation;
	}

	public void setWindowLocation(final Point windowLocation) {
		this.windowLocation = windowLocation;
	}

	public boolean isWindowMaximized() {
		return windowMaximized;
	}

	public void setWindowMaximized(final boolean windowMaximized) {
		this.windowMaximized = windowMaximized;
	}

	public Dimension getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(final Dimension windowSize) {
		this.windowSize = windowSize;
	}

	public boolean isWindowAutoSave() {
		return windowAutoSave;
	}

	public void setWindowAutoSave(final boolean windowAutoSave) {
		this.windowAutoSave = windowAutoSave;
	}

	public boolean isWindowAlwaysOnTop() {
		return windowAlwaysOnTop;
	}

	public void setWindowAlwaysOnTop(final boolean windowAlwaysOnTop) {
		this.windowAlwaysOnTop = windowAlwaysOnTop;
	}

	public boolean isSettingsLoadError() {
		return settingsLoadError;
	}

	public void setSettingsLoadError(boolean settingsLoadError) {
		this.settingsLoadError = settingsLoadError;
	}

	public Map<String, OverviewGroup> getOverviewGroups() {
		return overviewGroups;
	}

	public static Date getNow() {
		return new Date();
	}

	public static DateFormat getSettingsDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}

	private static class SettingsLock {

		private boolean locked = false;
		private final SettingsQueue settingsQueue = new SettingsQueue();

		public boolean ignoreSave() {
			return settingsQueue.ignoreSave();
		}

		public void saveStart() {
			settingsQueue.saveStart();
		}

		public void saveEnd() {
			settingsQueue.saveEnd();
		}

		public void waitForEmptySaveQueue() {
			settingsQueue.waitForEmptySaveQueue();
		}

		public synchronized void lock(String msg) {
			while (locked) {
				try {
					wait();
				} catch (InterruptedException ex) {

				}
			}
			locked = true;
			LOG.debug("Settings Locked: " + msg);
		}

		public synchronized void unlock(String msg) {
			locked = false;
			LOG.debug("Settings Unlocked: " + msg);
			notify();
		}
	}

	private static class SettingsQueue {

		private short savesQueue = 0;

		public synchronized boolean ignoreSave() {
			LOG.debug("Save Queue: " + savesQueue + " ignore: " + (savesQueue > 1));
			return savesQueue > 1;
		}

		public synchronized void saveStart() {
			this.savesQueue++;
			notifyAll();
		}

		public synchronized void saveEnd() {
			this.savesQueue--;
			notifyAll();
		}

		public synchronized void waitForEmptySaveQueue() {
			while (savesQueue > 0) {
				try {
					wait();
				} catch (InterruptedException ex) {

				}
			}
		}
	}

	private static class EmptySettingsFactory implements SettingsFactory {
		@Override
		public Settings create() {
			return new Settings();
		}
	}

	public static interface SettingsFactory {
		public Settings create();
	}
}
