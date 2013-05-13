/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data;

import com.beimin.eveapi.EveApi;
import com.beimin.eveapi.connectors.ApiConnector;
import com.beimin.eveapi.connectors.ProxyConnector;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerData;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerOwner;
import net.nikr.eve.jeveasset.io.local.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Settings {

	private static final Logger LOG = LoggerFactory.getLogger(Settings.class);

	private static final String PATH_SETTINGS = "data" + File.separator + "settings.xml";
	private static final String PATH_ITEMS = "data" + File.separator + "items.xml";
	private static final String PATH_JUMPS = "data" + File.separator + "jumps.xml";
	private static final String PATH_LOCATIONS = "data" + File.separator + "locations.xml";
	private static final String PATH_FLAGS = "data" + File.separator + "flags.xml";
	private static final String PATH_DATA_VERSION = "data" + File.separator + "data.xml";
	private static final String PATH_PRICE_DATA = "data" + File.separator + "pricedata.dat";
	private static final String PATH_ASSETS = "data" + File.separator + "assets.xml";
	private static final String PATH_CONQUERABLE_STATIONS = "data" + File.separator + "conquerable_stations.xml";
	private static final String PATH_README = "readme.txt";
	private static final String PATH_LICENSE = "license.txt";
	private static final String PATH_CREDITS = "credits.txt";
	private static final String PATH_CHANGELOG = "changelog.txt";
	private static final String PATH_PROFILES = "profiles";

	private static final String FLAG_IGNORE_SECURE_CONTAINERS = "FLAG_IGNORE_SECURE_CONTAINERS";
	private static final String FLAG_FILTER_ON_ENTER = "FLAG_FILTER_ON_ENTER";
	private static final String FLAG_REPROCESS_COLORS = "FLAG_REPROCESS_COLORS";
	private static final String FLAG_INCLUDE_SELL_ORDERS = "FLAG_INCLUDE_SELL_ORDERS";
	private static final String FLAG_INCLUDE_BUY_ORDERS = "FLAG_INCLUDE_BUY_ORDERS";
	private static final String FLAG_INCLUDE_CONTRACTS = "FLAG_INCLUDE_CONTRACTS";
	private static final String FLAG_HIGHLIGHT_SELECTED_ROWS = "FLAG_HIGHLIGHT_SELECTED_ROWS";
	private static final String FLAG_AUTO_UPDATE = "FLAG_AUTO_UPDATE";
	private static final String FLAG_UPDATE_DEV = "FLAG_UPDATE_DEV";
	private static final String FLAG_STOCKPILE_FOCUS_TAB = "FLAG_STOCKPILE_FOCUS_TAB";
	private static final String FLAG_STOCKPILE_HALF_COLORS = "FLAG_STOCKPILE_HALF_COLORS";

	private static Settings settings;

	private static boolean portable = false;
	private Map<Integer, PriceData> priceDatas; //TypeID : int
	private Map<Integer, UserItem<Integer, Double>> userPrices; //TypeID : int
	private Map<Long, UserItem<Long, String>> userNames; //ItemID : long
	private final Map<Long, Date> assetAdded = new HashMap<Long, Date>();
	private final List<Stockpile> stockpiles = new ArrayList<Stockpile>();
	private Date conquerableStationsNextUpdate;
	private Map<String, Boolean> flags;
	private boolean settingsLoaded;
	private PriceDataSettings priceDataSettings = new PriceDataSettings();
	private Proxy proxy;
	private String apiProxy;
	private Point windowLocation;
	private Dimension windowSize;
	private boolean windowMaximized;
	private boolean windowAutoSave;
	private boolean windowAlwaysOnTop;
	private Boolean highlightSelectedRows = null;
	private Boolean reprocessColors = null;
	private int maximumPurchaseAge = 0;
	private Map<String, OverviewGroup> overviewGroups;
	private ReprocessSettings reprocessSettings;
	private ExportSettings exportSettings = new ExportSettings();
	private boolean filterOnEnter = false;
	private Map<TrackerOwner, List<TrackerData>> trackerData = new HashMap<TrackerOwner, List<TrackerData>>(); //ownerID :: long
	private Map<Long, String> owners = new HashMap<Long, String>();
	private Map<String, Map<String, List<Filter>>> tableFilters = new HashMap<String, Map<String, List<Filter>>>();
	private Map<String, List<SimpleColumn>> tableColumns = new HashMap<String, List<SimpleColumn>>();
	private Map<String, Map<String, Integer>> tableColumnsWidth = new HashMap<String, Map<String, Integer>>();
	private Map<String, ResizeMode> tableResize = new HashMap<String, ResizeMode>();

	private Settings() {
		SplashUpdater.setProgress(30);
		priceDatas = new HashMap<Integer, PriceData>();

		//Settings
		userPrices = new HashMap<Integer, UserItem<Integer, Double>>();
		userNames = new HashMap<Long, UserItem<Long, String>>();
		overviewGroups = new HashMap<String, OverviewGroup>();

		flags = new HashMap<String, Boolean>();
		flags.put(FLAG_FILTER_ON_ENTER, false);
		flags.put(FLAG_HIGHLIGHT_SELECTED_ROWS, true);
		flags.put(FLAG_AUTO_UPDATE, true);
		flags.put(FLAG_UPDATE_DEV, false);
		flags.put(FLAG_REPROCESS_COLORS, false);
		flags.put(FLAG_IGNORE_SECURE_CONTAINERS, true);
		flags.put(FLAG_STOCKPILE_FOCUS_TAB, true);
		flags.put(FLAG_STOCKPILE_HALF_COLORS, false);
		flags.put(FLAG_INCLUDE_SELL_ORDERS, true);
		flags.put(FLAG_INCLUDE_BUY_ORDERS, false);
		flags.put(FLAG_INCLUDE_CONTRACTS, false);

		reprocessSettings = new ReprocessSettings();

		conquerableStationsNextUpdate = Settings.getNow();

		windowLocation = new Point(0, 0);
		windowSize = new Dimension(800, 600);
		windowMaximized = false;
		windowAutoSave = true;
	}

	public static Settings get() {
		load();
		return settings;
	}

	public static void load() {
		if (settings == null) {
			settings = new Settings();
			settings.loadSettings();
			settings.constructEveApiConnector();
		}
	}

	public ExportSettings getExportSettings() {
		return exportSettings;
	}

	/**
	 *
	 * @param load does nothing except change the method signature.
	 */
	protected Settings(final boolean load) { }


	public void saveSettings() {
		SettingsWriter.save(this);
	}

	private void loadSettings() {
	//Load data and overwite default values
		settingsLoaded = SettingsReader.load(this);
		SplashUpdater.setProgress(35);
		constructEveApiConnector();
	}

	public static void setPortable(final boolean portable) {
		Settings.portable = portable;
	}

	public static boolean isPortable() {
		return portable;
	}

	public Map<TrackerOwner, List<TrackerData>> getTrackerData() {
		return trackerData;
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

	public Map<Integer, PriceData> getPriceData() {
		return priceDatas;
	}

	public Map<String, Boolean> getFlags() {
		return flags;
	}

	public ReprocessSettings getReprocessSettings() {
		return reprocessSettings;
	}

	public void setReprocessSettings(final ReprocessSettings reprocessSettings) {
		this.reprocessSettings = reprocessSettings;
	}

	//@NotNull
	public Proxy getProxy() {
		if (proxy == null) {
			return Proxy.NO_PROXY;
		} else {
			return proxy;
		}
	}

  /**
   *
   * @param proxy passing 'null' removes proxying.
   */
	public void setProxy(final Proxy proxy) {
		this.proxy = proxy;
		// pass the new proxy onto the API framework.
		constructEveApiConnector();
	}

  /**
   * handles converting "basic" types to a Proxy type.
   * @param host
   * @param port
   * @param type
   * @throws IllegalArgumentException
   */
	public void setProxy(final String host, final int port, final String type) {
		// Convert the proxy type. not using the "valueof()" method so that they can be case-insensitive.
		Proxy.Type proxyType = Proxy.Type.DIRECT;
		if ("http".equalsIgnoreCase(type)) {
			proxyType = Proxy.Type.HTTP;
		} else if ("socks".equalsIgnoreCase(type)) {
			proxyType = Proxy.Type.SOCKS;
		} else if ("direct".equalsIgnoreCase(type)) {
			setProxy(Proxy.NO_PROXY);
		}

		setProxy(host, port, proxyType);
	}

  /**
   * handles converting "basic" types to a Proxy type.
   * @param host
   * @param port
   * @param type
   * @throws IllegalArgumentException
   */
	public void setProxy(final String host, final int port, final Proxy.Type type) {
		// Convert it into something we can use.
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(host);
		} catch (UnknownHostException uhe) {
			throw new IllegalArgumentException("unknown host: " + host, uhe);
		}

		SocketAddress proxyAddress = new InetSocketAddress(addr, port);

		setProxy(new Proxy(type, proxyAddress));
	}

	public boolean isForceUpdate() {
		return (apiProxy != null);
	}

	public String getApiProxy() {
		return apiProxy;
	}

	/**
	 * Set API Proxy.
	 * @param apiProxy pass null to disable any API proxy, and use the default: http://api.eve-online.com
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
		if (proxy != null) { //Real Proxy
			connector = new ProxyConnector(getProxy(), connector);
		}
		EveApi.setConnector(connector);
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

	public int getMaximumPurchaseAge() {
		return maximumPurchaseAge;
	}

	public void setMaximumPurchaseAge(final int maximumPurchaseAge) {
		this.maximumPurchaseAge = maximumPurchaseAge;
	}

	public boolean isFilterOnEnter() {
		return filterOnEnter;
	}
	public void setFilterOnEnter(final boolean filterOnEnter) {
		this.filterOnEnter = filterOnEnter;
		flags.put(FLAG_FILTER_ON_ENTER, filterOnEnter); //Save & Load
	}
	public boolean isHighlightSelectedRows() { //High volume call - Map.get is too slow, use cache
		if (highlightSelectedRows == null) {
			highlightSelectedRows = flags.get(FLAG_HIGHLIGHT_SELECTED_ROWS);
		}
		return highlightSelectedRows;
	}
	public void setHighlightSelectedRows(final boolean highlightSelectedRows) {
		flags.put(FLAG_HIGHLIGHT_SELECTED_ROWS, highlightSelectedRows);
		this.highlightSelectedRows = highlightSelectedRows;
	}

	public boolean isAutoUpdate() {
		return flags.get(FLAG_AUTO_UPDATE);
	}
	public void setAutoUpdate(final boolean updateStable) {
		flags.put(FLAG_AUTO_UPDATE, updateStable);
	}
	public boolean isUpdateDev() {
		return flags.get(FLAG_UPDATE_DEV);
	}
	public void setUpdateDev(final boolean updateDev) {
		flags.put(FLAG_UPDATE_DEV, updateDev);
	}
	public boolean isIgnoreSecureContainers() {
		return flags.get(FLAG_IGNORE_SECURE_CONTAINERS);
	}
	public void setIgnoreSecureContainers(final boolean ignoreSecureContainers) {
		flags.put(FLAG_IGNORE_SECURE_CONTAINERS, ignoreSecureContainers);
	}
	public boolean isReprocessColors() { //High volume call - Map.get is too slow, use cache
		if (reprocessColors == null) {
			reprocessColors = flags.get(FLAG_REPROCESS_COLORS);
		}
		return reprocessColors;
	}
	public void setReprocessColors(final boolean reprocessColors) {
		flags.put(FLAG_REPROCESS_COLORS, reprocessColors);
		this.reprocessColors = reprocessColors;
	}
	public boolean isStockpileFocusTab() {
		return flags.get(FLAG_STOCKPILE_FOCUS_TAB);
	}
	public void setStockpileFocusTab(final boolean stockpileFocusOnAdd) {
		flags.put(FLAG_STOCKPILE_FOCUS_TAB, stockpileFocusOnAdd);
	}
	public boolean isStockpileHalfColors() {
		return flags.get(FLAG_STOCKPILE_HALF_COLORS);
	}
	public void setStockpileHalfColors(final boolean stockpileHalfColors) {
		flags.put(FLAG_STOCKPILE_HALF_COLORS, stockpileHalfColors);
	}
	public boolean isIncludeSellOrders() {
		return flags.get(FLAG_INCLUDE_SELL_ORDERS);
	}
	public void setIncludeSellOrders(final boolean includeSellOrders) {
		flags.put(FLAG_INCLUDE_SELL_ORDERS, includeSellOrders);
	}
	public boolean isIncludeBuyOrders() {
		return flags.get(FLAG_INCLUDE_BUY_ORDERS);
	}
	public void setIncludeBuyOrders(final boolean includeBuyOrders) {
		flags.put(FLAG_INCLUDE_BUY_ORDERS, includeBuyOrders);
	}
	public boolean isIncludeContracts() {
		return flags.get(FLAG_INCLUDE_CONTRACTS);
	}
	public void setIncludeContracts(final boolean includeBuyOrders) {
		flags.put(FLAG_INCLUDE_CONTRACTS, includeBuyOrders);
	}
	public List<Stockpile> getStockpiles() {
		return stockpiles;
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

	public boolean isSettingsLoaded() {
		return settingsLoaded;
	}

	public Map<String, OverviewGroup> getOverviewGroups() {
		return overviewGroups;
	}

	public String getPathSettings() {
		return getLocalFile(Settings.PATH_SETTINGS, !portable);
	}
	public static String getPathConquerableStations() {
		return getLocalFile(Settings.PATH_CONQUERABLE_STATIONS, !portable);
	}
	public static String getPathJumps() {
		return getLocalFile(Settings.PATH_JUMPS, false);
	}
	public static String getPathFlags() {
		return getLocalFile(Settings.PATH_FLAGS, false);
	}
	public static String getPathPriceData() {
		return getLocalFile(Settings.PATH_PRICE_DATA, !portable);
	}
	public static String getPathAssetsOld() {
		return getLocalFile(Settings.PATH_ASSETS, !portable);
	}
	public static String getPathProfilesDirectory() {
		return getLocalFile(Settings.PATH_PROFILES, !portable);
	}
	public static String getPathItems() {
		return getLocalFile(Settings.PATH_ITEMS, false);
	}
	public static String getPathLocations() {
		return getLocalFile(Settings.PATH_LOCATIONS, false);
	}
	public static String getPathDataVersion() {
		return getLocalFile(Settings.PATH_DATA_VERSION, false);
	}
	public static String getPathReadme() {
		return getLocalFile(Settings.PATH_README, false);
	}
	public static String getPathLicense() {
		return getLocalFile(Settings.PATH_LICENSE, false);
	}
	public static String getPathCredits() {
		return getLocalFile(Settings.PATH_CREDITS, false);
	}
	public static String getPathChangeLog() {
		return getLocalFile(Settings.PATH_CHANGELOG, false);
	}

	public static String getUserDirectory() {
		File userDir = new File(System.getProperty("user.home", "."));
		return userDir.getAbsolutePath() + File.separator;
	}

  /**
   *
   * @param filename the name of the data file to obtain
   * @param dynamic true if the file is expecting to be written to, false for things like the items and locations.
   * @return
   */
	private static String getLocalFile(final String filename, final boolean dynamic) {
		LOG.debug("Looking for file: {} dynamic: {}", filename, dynamic);
		try {
			File file;
			File ret;
			if (dynamic) {
				File userDir = new File(System.getProperty("user.home", "."));
				if (Program.onMac()) { // preferences are stored in user.home/Library/Preferences
					file = new File(userDir, "Library/Preferences/JEveAssets");
				} else {
					file = new File(userDir.getAbsolutePath() + File.separator + ".jeveassets");
				}
				ret = new File(file.getAbsolutePath() + File.separator + filename);
				File parent = ret.getParentFile();
				if (!parent.exists()
								&& !parent.mkdirs()) {
					LOG.error("failed to create directories for " + parent.getAbsolutePath());
				}
			} else {
				file = new File(net.nikr.eve.jeveasset.Program.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
				ret = new File(file.getAbsolutePath() + File.separator + filename);
			}
			LOG.debug("Found file at: {}", ret.getAbsolutePath());
			return ret.getAbsolutePath();
		} catch (URISyntaxException ex) {
			LOG.error("Failed to get program directory: Please email the latest error.txt in the logs directory to niklaskr@gmail.com", ex);
		}
		return null;
	}

	public static Date getNow() {
		return new Date();
	}

	public static DateFormat getSettingsDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}

	public boolean isUpdatable(final Date date) {
		return isUpdatable(date, true);
	}

	public boolean isUpdatable(final Date date, final boolean ignoreOnProxy) {
		return ((Settings.getNow().after(date)
				|| Settings.getNow().equals(date)
				|| Program.isForceUpdate()
				|| (getApiProxy() != null && ignoreOnProxy))
				&& !Program.isForceNoUpdate());
	}
}
