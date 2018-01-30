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
package net.nikr.eve.jeveasset.data.settings;

import net.nikr.eve.jeveasset.data.sde.MyLocation;
import com.beimin.eveapi.connectors.ApiConnector;
import com.beimin.eveapi.parser.shared.AbstractApiParser;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.settings.tag.Tag;
import net.nikr.eve.jeveasset.data.settings.tag.TagID;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerDate;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerNote;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.SettingsWriter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings {

	private static final Logger LOG = LoggerFactory.getLogger(Settings.class);

	private static final String PATH_SETTINGS = "data" + File.separator + "settings.xml";
	private static final String PATH_ITEMS = "data" + File.separator + "items.xml";
	private static final String PATH_JUMPS = "data" + File.separator + "jumps.xml";
	private static final String PATH_LOCATIONS = "data" + File.separator + "locations.xml";
	private static final String PATH_FLAGS = "data" + File.separator + "flags.xml";
	private static final String PATH_PRICE_DATA = "data" + File.separator + "pricedata.dat";
	private static final String PATH_ASSETS = "data" + File.separator + "assets.xml";
	private static final String PATH_CONQUERABLE_STATIONS = "data" + File.separator + "conquerable_stations.xml";
	private static final String PATH_LOGS = "data" + File.separator + "asset_logs.xml";
	private static final String PATH_CITADEL = "data" + File.separator + "citadel.xml";
	private static final String PATH_README = "readme.txt";
	private static final String PATH_LICENSE = "license.txt";
	private static final String PATH_CREDITS = "credits.txt";
	private static final String PATH_CHANGELOG = "changelog.txt";
	
	private static final String PATH_PROFILES = "profiles";
	private static final String PATH_DATA = "data";

	public static enum SettingFlag {
		FLAG_IGNORE_SECURE_CONTAINERS,
		FLAG_FILTER_ON_ENTER,
		FLAG_REPROCESS_COLORS,
		FLAG_INCLUDE_SELL_ORDERS,
		FLAG_INCLUDE_BUY_ORDERS,
		FLAG_INCLUDE_SELL_CONTRACTS,
		FLAG_INCLUDE_BUY_CONTRACTS,
		FLAG_INCLUDE_MANUFACTURING,
		FLAG_HIGHLIGHT_SELECTED_ROWS,
		FLAG_STOCKPILE_FOCUS_TAB,
		FLAG_STOCKPILE_HALF_COLORS,
		FLAG_BLUEPRINT_BASE_PRICE_TECH_1,
		FLAG_BLUEPRINT_BASE_PRICE_TECH_2,
		FLAG_TRANSACTION_HISTORY,
		FLAG_JOURNAL_HISTORY,
		FLAG_MARKET_ORDER_HISTORY,
		FLAG_STRONG_COLORS
	}

	private static final SettingsLock LOCK = new SettingsLock();
	private static Settings settings;
	private static boolean testMode = false;

	private int eveKitTransactionsHistory = 3;
	private int eveKitJournalHistory = 3;
	private int eveKitMarketOrdersHistory = 3;
	private int eveKitIndustryJobsHistory = 3;
	private int eveKitContractsHistory = 3;
//External
	//Price						Saved by PriceDataGetter.process() in pricedata.dat (on api update)
	private Map<Integer, PriceData> priceDatas = new HashMap<Integer, PriceData>(); //TypeID : int
//API Data
	//Api id to owner name		Saved by TaskDialog.update() (on API update)
	private final Map<Long, String> owners = new HashMap<Long, String>();
	//Stations Next Update		Saved by TaskDialog.update() (on API update)
	private Date conquerableStationsNextUpdate = Settings.getNow();
//!! - Values
	//OK - Custom Price			Saved by JUserListPanel.edit()/delete() + SettingsDialog.save()
	//Lock OK
	private Map<Integer, UserItem<Integer, Double>> userPrices = new HashMap<Integer, UserItem<Integer, Double>>(); //TypeID : int
	//OK - Custom Item Name		Saved by JUserListPanel.edit()/delete() + SettingsDialog.save()
	//Lock OK
	private Map<Long, UserItem<Long, String>> userNames = new HashMap<Long, UserItem<Long, String>>(); //ItemID : long
	//Eve Item Name				Saved by TaskDialog.update() (on API update)
	//Lock ???
	private Map<Long, String> eveNames = new HashMap<Long, String>();
	//!! - Assets				Saved by Program.updateEventLists() if needed
	//Lock OK
	private final Map<Long, Date> assetAdded = new HashMap<Long, Date>();
//!! - Stockpile				Saved by StockpileTab.removeItems() / addStockpile() / removeStockpile()
	//							Could be more selective...
	//Lock FAIL!!!
	private final List<Stockpile> stockpiles = new ArrayList<Stockpile>();
	private int stockpileColorGroup2 = 100;
	private int stockpileColorGroup3 = 0;
//Routing						Saved by ???
	//Lock ???
	private final RoutingSettings routingSettings = new RoutingSettings();
//Overview						Saved by JOverviewMenu.ListenerClass.NEW/DELETE/RENAME
	//Lock OK
	private final Map<String, OverviewGroup> overviewGroups = new HashMap<String, OverviewGroup>();
//Export						Saved in ExportDialog.saveSettings()
	//Lock OK
	private final ExportSettings exportSettings = new ExportSettings();
//Tracker						Saved by TaskDialog.update() (on API update)
	private final Map<String, List<Value>> trackerData = new HashMap<String, List<Value>>(); //ownerID :: long
	private final Map<TrackerDate, TrackerNote> trackerNotes = new HashMap<TrackerDate, TrackerNote>();
	private final Map<String, Boolean> trackerFilters = new HashMap<String, Boolean>();
	private boolean trackerSelectNew = true;
//Runtime flags					Is not saved to file
	private boolean settingsLoadError;
//Settings Dialog:				Saved by SettingsDialog.save()
	//Lock OK
	//Mixed boolean flags
	private final Map<SettingFlag, Boolean> flags = new EnumMap<SettingFlag, Boolean>(SettingFlag.class);
	//Price
	private PriceDataSettings priceDataSettings = new PriceDataSettings();
	//Proxy (API)
	private String apiProxy;
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
	//Reprocess price
	private ReprocessSettings reprocessSettings = new ReprocessSettings();
	//Cache
	private Boolean filterOnEnter = null; //Filter tools
	private Boolean highlightSelectedRows = null;  //Assets
	private Boolean reprocessColors = null;  //Assets
	private Boolean stockpileHalfColors = null; //Stockpile
//Table settings
	//Filters					Saved by ExportFilterControl.saveSettings()
	//Lock OK
	private final Map<String, Map<String, List<Filter>>> tableFilters = new HashMap<String, Map<String, List<Filter>>>();
	//Columns					Saved by EnumTableFormatAdaptor.getMenu() - Reset
	//									 EditColumnsDialog.save() - Edit Columns
	//									 JAutoColumnTable.ListenerClass.mouseReleased() - Moved
	//									 ViewManager.loadView() - Load View
	//Lock OK
	private final Map<String, List<SimpleColumn>> tableColumns = new HashMap<String, List<SimpleColumn>>();
	//Column Width				Saved by JAutoColumnTable.saveColumnsWidth()
	//Lock OK
	private final Map<String, Map<String, Integer>> tableColumnsWidth = new HashMap<String, Map<String, Integer>>();
	//Resize Mode				Saved by EnumTableFormatAdaptor.getMenu()
	//Lock OK
	private final Map<String, ResizeMode> tableResize = new HashMap<String, ResizeMode>();
	//Views						Saved by EnumTableFormatAdaptor.getMenu() - New
	//									 ViewManager.rename() - Rename
	//									 ViewManager.delete() - Delete
	//Lock OK
	private final Map<String, Map<String, View>> tableViews = new HashMap<String, Map<String, View>>();
//Tags						Saved by JMenuTags.addTag()/removeTag() + SettingsDialog.save()
	//Lock OK
	private final Map<String, Tag> tags = new HashMap<String, Tag>();
	private final Map<TagID, Tags> tagIds = new HashMap<TagID, Tags>();
//Jumps
	private final Map<Class<?>, List<MyLocation>> jumpLocations = new HashMap<Class<?>, List<MyLocation>>();

	protected Settings() {
		SplashUpdater.setProgress(30);

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
		flags.put(SettingFlag.FLAG_BLUEPRINT_BASE_PRICE_TECH_1, true);
		flags.put(SettingFlag.FLAG_BLUEPRINT_BASE_PRICE_TECH_2, false);
		flags.put(SettingFlag.FLAG_TRANSACTION_HISTORY, true);
		flags.put(SettingFlag.FLAG_JOURNAL_HISTORY, true);
		flags.put(SettingFlag.FLAG_MARKET_ORDER_HISTORY, true);
		flags.put(SettingFlag.FLAG_STRONG_COLORS, false);
		cacheFlags();
	}

	public static Settings get() {
		load();
		return settings;
	}

	public static void setTestMode(boolean testMode) {
		Settings.testMode = testMode;
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
			settings = new Settings();
			autoImportSettings();
			settings.loadSettings();
		}
	}

	private static void autoImportSettings() {
		if (Program.PROGRAM_DEV_BUILD && !testMode) { //Need import
			Program.setPortable(false);
			Path settingsFrom = Paths.get(settings.getPathSettings());
			Path citadelFrom = Paths.get(Settings.getPathCitadel());
			Path priceFrom = Paths.get(Settings.getPathPriceData());
			Path profilesFrom = Paths.get(Settings.getPathProfilesDirectory());
			Program.setPortable(true);
			Path settingsTo = Paths.get(Settings.get().getPathSettings());
			Path citadelTo = Paths.get(Settings.getPathCitadel());
			Path priceTo = Paths.get(Settings.getPathPriceData());
			Path profilesTo = Paths.get(Settings.getPathProfilesDirectory());
			if (Files.exists(settingsFrom) && !Files.exists(settingsTo)) {
				LOG.info("Importing settings");
				try {
					Files.copy(settingsFrom, settingsTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(citadelFrom) && !Files.exists(citadelTo)) {
				LOG.info("Importing citadels");
				try {
					Files.copy(citadelFrom, citadelTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(priceFrom) && !Files.exists(priceTo)) {
				LOG.info("Importing prices");
				try {
					Files.copy(priceFrom, priceTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(profilesFrom) && !Files.exists(profilesTo)) {
				PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.xml");
				try {
					LOG.info("Importing profiles");
					Files.walkFileTree(profilesFrom, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
							if (dir.equals(profilesFrom)) {
								Files.createDirectories(profilesTo.resolve(profilesFrom.relativize(dir)));
								return FileVisitResult.CONTINUE;
							} else {
								return FileVisitResult.SKIP_SUBTREE;
							}
						}

						@Override
						public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
							if (matcher.matches(file.getFileName())) {
								Files.copy(file, profilesTo.resolve(profilesFrom.relativize(file)));
							}
							return FileVisitResult.CONTINUE;
						}
					});
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
		}
	}

	public ExportSettings getExportSettings() {
		return exportSettings;
	}

	public static void saveSettings() {
		LOCK.lock("Save Settings");
		try {
			SettingsWriter.save(settings);
		} finally {
			LOCK.unlock("Save Settings");
		}
	}

	private void loadSettings() {
		//Load data and overwite default values
		settingsLoadError = !SettingsReader.load(this);
		SplashUpdater.setProgress(35);
		constructEveApiConnector();
	}

	public Map<String, List<Value>> getTrackerData() {
		return trackerData;
	}

	public Map<TrackerDate, TrackerNote> getTrackerNotes() {
		return trackerNotes;
	}

	public Map<String, Boolean> getTrackerFilters() {
		return trackerFilters;
	}

	public boolean isTrackerSelectNew() {
		return trackerSelectNew;
	}

	public void setTrackerSelectNew(boolean trackerSelectNew) {
		this.trackerSelectNew = trackerSelectNew;
	}

	public Date getConquerableStationsNextUpdate() {
		return conquerableStationsNextUpdate;
	}

	public void setConquerableStationsNextUpdate(final Date conquerableStationNextUpdate) {
		this.conquerableStationsNextUpdate = conquerableStationNextUpdate;
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

	public List<MyLocation> getJumpLocations(Class<?> clazz) {
		List<MyLocation> locations = jumpLocations.get(clazz);
		if (locations == null) {
			locations = new ArrayList<MyLocation>();
			jumpLocations.put(clazz, locations);
		}
		return locations;
	}

	public void addJumpLocation(Class<?> clazz, MyLocation location) {
		getJumpLocations(clazz).add(location);
	}
	public void removeJumpLocation(Class<?> clazz, MyLocation location) {
		getJumpLocations(clazz).remove(location);
	}
	public void clearJumpLocations(Class<?> clazz) {
		getJumpLocations(clazz).clear();
	}

	public boolean isForceUpdate() {
		return (apiProxy != null);
	}

	public String getApiProxy() {
		return apiProxy;
	}

	public ProxyData getProxyData() {
		return proxyData;
	}

	public void setProxyData(ProxyData proxyData) {
		this.proxyData = proxyData;
	}

	/**
	 * Set API Proxy.
	 *
	 * @param apiProxy pass null to disable any API proxy, and use the default:
	 * http://api.eve-online.com
	 */
	public void setApiProxy(final String apiProxy) {
		this.apiProxy = apiProxy;
		constructEveApiConnector();
	}

	/**
	 * build the API Connector and set it in the library.
	 */
	private void constructEveApiConnector() {
		ApiConnector connector = new ApiConnector(); //Default
		if (apiProxy != null) { //API Proxy
			connector = new ApiConnector(getApiProxy());
		}
		AbstractApiParser.setConnector(connector);
	}

	public Map<Long, String> getOwners() {
		return owners;
	}

	public Map<String, Map<String, List<Filter>>> getTableFilters() {
		return tableFilters;
	}

	public Map<String, List<Filter>> getTableFilters(final String key) {
		if (!tableFilters.containsKey(key)) {
			tableFilters.put(key, new HashMap<String, List<Filter>>());
		}
		return tableFilters.get(key);
	}

	public Map<Long, Date> getAssetAdded() {
		return assetAdded;
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
			views = new TreeMap<String, View>(new CaseInsensitiveComparator());
			tableViews.put(name, views);
		}
		return views;
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

	public int getMaximumPurchaseAge() {
		return maximumPurchaseAge;
	}

	public void setMaximumPurchaseAge(final int maximumPurchaseAge) {
		this.maximumPurchaseAge = maximumPurchaseAge;
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

	public boolean isStrongColors() {
		return flags.get(SettingFlag.FLAG_STRONG_COLORS);
	}

	public void setStrongColors(final boolean strongColors) {
		flags.put(SettingFlag.FLAG_STRONG_COLORS, strongColors);
	}

	public List<Stockpile> getStockpiles() {
		return stockpiles;
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

	public Map<String, OverviewGroup> getOverviewGroups() {
		return overviewGroups;
	}

	public int getEveKitTransactionsHistory() {
		return eveKitTransactionsHistory;
	}

	public void setEveKitTransactionsHistory(int eveKitTransactionsHistory) {
		this.eveKitTransactionsHistory = eveKitTransactionsHistory;
	}

	public int getEveKitJournalHistory() {
		return eveKitJournalHistory;
	}

	public void setEveKitJournalHistory(int eveKitJournalHistory) {
		this.eveKitJournalHistory = eveKitJournalHistory;
	}

	public int getEveKitMarketOrdersHistory() {
		return eveKitMarketOrdersHistory;
	}

	public void setEveKitMarketOrdersHistory(int eveKitMarketOrdersHistory) {
		this.eveKitMarketOrdersHistory = eveKitMarketOrdersHistory;
	}

	public int getEveKitIndustryJobsHistory() {
		return eveKitIndustryJobsHistory;
	}

	public void setEveKitIndustryJobsHistory(int eveKitIndustryJobsHistory) {
		this.eveKitIndustryJobsHistory = eveKitIndustryJobsHistory;
	}

	public int getEveKitContractsHistory() {
		return eveKitContractsHistory;
	}

	public void setEveKitContractsHistory(int eveKitContractsHistory) {
		this.eveKitContractsHistory = eveKitContractsHistory;
	}

	public String getPathSettings() {
		return FileUtil.getLocalFile(Settings.PATH_SETTINGS, !Program.isPortable());
	}

	public static String getPathConquerableStations() {
		return FileUtil.getLocalFile(Settings.PATH_CONQUERABLE_STATIONS, !Program.isPortable());
	}

	public static String getPathLogs() {
		return FileUtil.getLocalFile(Settings.PATH_LOGS, !Program.isPortable());
	}

	public static String getPathCitadel() {
		return FileUtil.getLocalFile(Settings.PATH_CITADEL, !Program.isPortable());
	}

	public static String getPathJumps() {
		return FileUtil.getLocalFile(Settings.PATH_JUMPS, false);
	}

	public static String getPathFlags() {
		return FileUtil.getLocalFile(Settings.PATH_FLAGS, false);
	}

	public static String getPathPriceData() {
		return FileUtil.getLocalFile(Settings.PATH_PRICE_DATA, !Program.isPortable());
	}

	public static String getPathAssetsOld() {
		return FileUtil.getLocalFile(Settings.PATH_ASSETS, !Program.isPortable());
	}

	public static String getPathProfilesDirectory() {
		return FileUtil.getLocalFile(Settings.PATH_PROFILES, !Program.isPortable());
	}

	public static String getPathStaticDataDirectory() {
		return FileUtil.getLocalFile(Settings.PATH_DATA, false);
	}

	public static String getPathDataDirectory() {
		return FileUtil.getLocalFile(Settings.PATH_DATA, !Program.isPortable());
	}

	public static String getPathItems() {
		return FileUtil.getLocalFile(Settings.PATH_ITEMS, false);
	}

	public static String getPathLocations() {
		return FileUtil.getLocalFile(Settings.PATH_LOCATIONS, false);
	}

	public static String getPathReadme() {
		return FileUtil.getLocalFile(Settings.PATH_README, false);
	}

	public static String getPathLicense() {
		return FileUtil.getLocalFile(Settings.PATH_LICENSE, false);
	}

	public static String getPathCredits() {
		return FileUtil.getLocalFile(Settings.PATH_CREDITS, false);
	}

	public static String getPathChangeLog() {
		return FileUtil.getLocalFile(Settings.PATH_CHANGELOG, false);
	}

	public static String getUserDirectory() {
		File userDir = new File(System.getProperty("user.home", "."));
		return userDir.getAbsolutePath() + File.separator;
	}

	public static Date getNow() {
		return new Date();
	}

	public static DateFormat getSettingsDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}

	public boolean isUpdatable(final Date date, final boolean ignoreOnProxy) {
		return ((Settings.getNow().after(date)
				|| Settings.getNow().equals(date)
				|| Program.isForceUpdate()
				|| (getApiProxy() != null && ignoreOnProxy))
				&& !Program.isForceNoUpdate());
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
}
