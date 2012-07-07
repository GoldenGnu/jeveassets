/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.model.Galaxy;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.io.local.*;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
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
	private static final String FLAG_HIGHLIGHT_SELECTED_ROWS = "FLAG_HIGHLIGHT_SELECTED_ROWS";
	private static final String FLAG_AUTO_UPDATE = "FLAG_AUTO_UPDATE";
	private static final String FLAG_UPDATE_DEV = "FLAG_UPDATE_DEV";
	private static final String FLAG_STOCKPILE_FOCUS_TAB = "FLAG_STOCKPILE_FOCUS_TAB";
	private static final String FLAG_STOCKPILE_HALF_COLORS = "FLAG_STOCKPILE_HALF_COLORS";

	private static boolean portable = false;

	//Data
	private Map<Integer, Item> items = new HashMap<Integer, Item>(); //TypeID : int
	private Map<Integer, ItemFlag> itemFlags = new HashMap<Integer, ItemFlag>(); //FlagID : int
	private Map<Long, Location> locations = new HashMap<Long, Location>(); //LocationID : long
	private List<Jump> jumps = new ArrayList<Jump>(); //LocationID : long
	private Map<Long, ApiStation> conquerableStations = new HashMap<Long, ApiStation>(); //LocationID : long
	private List<Integer> uniqueIds = null; //TypeID : int
	private Map<Integer, List<Asset>> uniqueAssetsDuplicates = null; //TypeID : int
	private Map<Integer, PriceData> priceDatas; //TypeID : int
	private Map<Integer, UserItem<Integer, Double>> userPrices; //TypeID : int
	private Map<Long, UserItem<Long, String>> userNames; //ItemID : long
	private List<Asset> eventListAssets = null;
	private final List<Stockpile> stockpiles = new ArrayList<Stockpile>();
	private List<Account> accounts;
	private final Map<String, Float> packagedVolume = new HashMap<String, Float>();
	private Date conquerableStationsNextUpdate;
	private Map<String, Boolean> flags;
	private List<Profile> profiles;
	private boolean settingsLoaded;
	private PriceDataSettings priceDataSettings = new PriceDataSettings();
	private Proxy proxy;
	private String apiProxy;
	private Point windowLocation;
	private Dimension windowSize;
	private boolean windowMaximized;
	private boolean windowAutoSave;
	private boolean windowAlwaysOnTop;
	private Profile activeProfile;
	private Map<String, OverviewGroup> overviewGroups;
	private ReprocessSettings reprocessSettings;
	private Galaxy model;
	private PriceDataGetter priceDataGetter = new PriceDataGetter(this);
	private static ExportSettings exportSettings = new ExportSettings();
	private static boolean filterOnEnter = false;

	private Map<String, Map<String, List<Filter>>> tableFilters = new HashMap<String, Map<String, List<Filter>>>();
	private Map<String, List<SimpleColumn>> tableColumns = new HashMap<String, List<SimpleColumn>>();
	private Map<String, Map<String, Integer>> tableColumnsWidth = new HashMap<String, Map<String, Integer>>();
	private Map<String, ResizeMode> tableResize = new HashMap<String, ResizeMode>();

	public Settings() {
		SplashUpdater.setProgress(5);
		priceDatas = new HashMap<Integer, PriceData>();
		accounts = new ArrayList<Account>();
		profiles = new ArrayList<Profile>();

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

		packagedVolume.put("Assault Ship", 2500f);
		packagedVolume.put("Battlecruiser", 15000f);
		packagedVolume.put("Battleship", 50000f);
		packagedVolume.put("Black Ops", 50000f);
		packagedVolume.put("Capital Industrial Ship", 1000000f);
		packagedVolume.put("Capsule", 500f);
		packagedVolume.put("Carrier", 1000000f);
		packagedVolume.put("Combat Recon Ship", 10000f);
		packagedVolume.put("Command Ship", 15000f);
		packagedVolume.put("Covert Ops", 2500f);
		packagedVolume.put("Cruiser", 10000f);
		packagedVolume.put("Destroyer", 5000f);
		packagedVolume.put("Dreadnought", 1000000f);
		packagedVolume.put("Electronic Attack Ship", 2500f);
		packagedVolume.put("Elite Battleship", 50000f);
		packagedVolume.put("Exhumer", 3750f);
		packagedVolume.put("Force Recon Ship", 10000f);
		packagedVolume.put("Freighter", 1000000f);
		packagedVolume.put("Frigate", 2500f);
		packagedVolume.put("Heavy Assault Ship", 10000f);
		packagedVolume.put("Heavy Interdictor", 10000f);
		packagedVolume.put("Industrial", 20000f);
		packagedVolume.put("Industrial Command Ship", 500000f);
		packagedVolume.put("Interceptor", 2500f);
		packagedVolume.put("Interdictor", 5000f);
		packagedVolume.put("Jump Freighter", 1000000f);
		packagedVolume.put("Logistics", 10000f);
		packagedVolume.put("Marauder", 50000f);
		packagedVolume.put("Mining Barge", 3750f);
		packagedVolume.put("Prototype Exploration Ship", 500f);
		packagedVolume.put("Rookie ship", 2500f);
		packagedVolume.put("Shuttle", 500f);
		packagedVolume.put("Stealth Bomber", 2500f);
		packagedVolume.put("Strategic Cruiser", 5000f);
		packagedVolume.put("Supercarrier", 1000000f);
		packagedVolume.put("Titan", 10000000f);
		packagedVolume.put("Transport Ship", 20000f);

		reprocessSettings = new ReprocessSettings();

		activeProfile = new Profile("Default", true, true);
		profiles.add(activeProfile);

		conquerableStationsNextUpdate = Settings.getGmtNow();

		windowLocation = new Point(0, 0);
		windowSize = new Dimension(800, 600);
		windowMaximized = false;
		windowAutoSave = true;
		loadSettings();
		model = new Galaxy(this.locations, this.jumps);
		constructEveApiConnector();
	}

	public static ExportSettings getExportSettings() {
		return exportSettings;
	}

	/**
	 *
	 * @param load does nothing except change the method signature.
	 */
	protected Settings(final boolean load) { }

	public Galaxy getGalaxyModel() {
		return model;
	}

	public void saveSettings() {
		SettingsWriter.save(this);
		saveAssets();
	}

	private void loadSettings() {
	//Load static data
		SplashUpdater.setProgress(10);
		ItemsReader.load(this); //Items (Must be loaded before Assets)
		SplashUpdater.setProgress(15);
		LocationsReader.load(this); //Locations (Must be loaded before Assets)
		SplashUpdater.setProgress(20);
		JumpsReader.load(this); //Jumps
		SplashUpdater.setProgress(25);
		FlagsReader.load(this); //Item Flags (Must be loaded before Assets)
		ConquerableStationsReader.load(this); //Conquerable Stations (Must be loaded before Assets)
		SplashUpdater.setProgress(30);
	//Load data and overwite default values
		settingsLoaded = SettingsReader.load(this);
	//Find profiles
		ProfileReader.load(this);
		SplashUpdater.setProgress(35);
		constructEveApiConnector();
	}

	public void loadActiveProfile() {
	//Load Assets
		LOG.info("Loading profile: {}", activeProfile.getName());
		accounts = new ArrayList<Account>();
		AssetsReader.load(this, activeProfile.getFilename()); //Assets (Must be loaded before the price data)
		SplashUpdater.setProgress(40);
	//Price data (update as needed)
		clearEveAssetList(); //Must be cleared to update uniqueIds
		priceDataGetter.load(); //Price Data - Must be loaded last
		SplashUpdater.setProgress(45);
		constructEveApiConnector();
	}

	public void saveAssets() {
		AssetsWriter.save(this, activeProfile.getFilename());
	}

	public PriceDataGetter getPriceDataGetter() {
		return priceDataGetter;
	}

	public static void setPortable(final boolean portable) {
		Settings.portable = portable;
	}

	public static boolean isPortable() {
		return portable;
	}

	public void clearEveAssetList() {
		eventListAssets = null;
		uniqueIds = null;
		uniqueAssetsDuplicates = null;
	}
	public List<Asset> getEventListAssets() {
		updateAssetLists();
		return eventListAssets;
	}
	public List<Integer> getUniqueIds() {
		updateAssetLists();
		return uniqueIds;
	}

	public boolean hasAssets() {
		updateAssetLists();
		return !uniqueIds.isEmpty();
	}
	private void updateAssetLists() {
		if (eventListAssets == null || uniqueIds == null || uniqueAssetsDuplicates == null) {
			eventListAssets = new ArrayList<Asset>();
			uniqueIds = new ArrayList<Integer>();
			uniqueAssetsDuplicates = new HashMap<Integer, List<Asset>>();
			List<String> ownersOrders = new ArrayList<String>();
			List<String> ownersJobs = new ArrayList<String>();
			List<String> ownersAssets = new ArrayList<String>();
			for (Account account : accounts) {
				for (Human human : account.getHumans()) {
					//Market Orders
					if (!human.getMarketOrders().isEmpty() && !ownersOrders.contains(human.getName())) {
						List<Asset> marketOrdersAssets = ApiConverter.apiMarketOrder(human.getMarketOrders(), human, this);
						addAssets(marketOrdersAssets, human.isShowAssets());
						if (human.isShowAssets()) {
							ownersOrders.add(human.getName());
						}
					}
					//Industry Jobs
					if (!human.getIndustryJobs().isEmpty() && !ownersJobs.contains(human.getName())) {
						List<Asset> industryJobAssets = ApiConverter.apiIndustryJob(human.getIndustryJobs(), human, this);
						addAssets(industryJobAssets, human.isShowAssets());
						if (human.isShowAssets()) {
							ownersJobs.add(human.getName());
						}
					}
					//Assets (Must be after Industry Jobs, for bpos to be marked)
					if (!human.getAssets().isEmpty() && !ownersAssets.contains(human.getName())) {
						addAssets(human.getAssets(), human.isShowAssets());
						if (human.isShowAssets()) {
							ownersAssets.add(human.getName());
						}
					}
					//Add StockpileItems to uniqueIds
					for (Stockpile stockpile : this.getStockpiles()) {
						for (StockpileItem item : stockpile.getItems()) {
							boolean marketGroup = ApiIdConverter.marketGroup(item.getTypeID(), this.getItems());
							if (marketGroup && !uniqueIds.contains(item.getTypeID())) {
								uniqueIds.add(item.getTypeID());
							}
						}
					}
					//Add MarketOrders to uniqueIds
					for (ApiMarketOrder order : human.getMarketOrders()) {
						boolean marketGroup = ApiIdConverter.marketGroup(order.getTypeID(), this.getItems());
						if (marketGroup && !uniqueIds.contains(order.getTypeID())) {
							uniqueIds.add(order.getTypeID());
						}
					}
					//Add IndustryJobs to uniqueIds
					for (ApiIndustryJob job : human.getIndustryJobs()) {
						boolean marketGroup = ApiIdConverter.marketGroup(job.getInstalledItemTypeID(), this.getItems());
						if (marketGroup && !uniqueIds.contains(job.getInstalledItemTypeID())) {
							uniqueIds.add(job.getInstalledItemTypeID());
						}
					}
				}
			}
		}
	}
	private void addAssets(final List<Asset> currentAssets, final boolean shouldShow) {
		for (Asset eveAsset : currentAssets) {
			if (shouldShow) {
				//User price
				if (eveAsset.isBlueprint() && !eveAsset.isBpo()) { //Blueprint Copy
					eveAsset.setUserPrice(userPrices.get(-eveAsset.getTypeID()));
				} else { //All other
					eveAsset.setUserPrice(userPrices.get(eveAsset.getTypeID()));
				}

				//User Item Names
				if (userNames.containsKey(eveAsset.getItemID())) {
					eveAsset.setName(userNames.get(eveAsset.getItemID()).getValue());
				} else {
					eveAsset.setName(eveAsset.getTypeName());
				}
				//Contaioner
				String sContainer = "";
				for (Asset parentEveAsset : eveAsset.getParents()) {
					if (!sContainer.isEmpty()) {
						sContainer = sContainer + ">";
					}
					if (!parentEveAsset.isUserName()) {
						sContainer = sContainer + parentEveAsset.getName() + " #" + parentEveAsset.getItemID();
					} else {
						sContainer = sContainer + parentEveAsset.getName();
					}
				}
				eveAsset.setContainer(sContainer);

				//Price data
				if (eveAsset.isMarketGroup() && priceDatas.containsKey(eveAsset.getTypeID()) && !priceDatas.get(eveAsset.getTypeID()).isEmpty()) { //Market Price
					eveAsset.setPriceData(priceDatas.get(eveAsset.getTypeID()));
				} else { //No Price :(
					eveAsset.setPriceData(null);
				}

				//Reprocessed price
				eveAsset.setPriceReprocessed(0);
				if (getItems().containsKey(eveAsset.getTypeID())) {
					List<ReprocessedMaterial> reprocessedMaterials = getItems().get(eveAsset.getTypeID()).getReprocessedMaterial();
					double priceReprocessed = 0;
					int portionSize = 0;
					for (ReprocessedMaterial material : reprocessedMaterials) {
						//Calculate reprocessed price
						portionSize = material.getPortionSize();
						if (priceDatas.containsKey(material.getTypeID())) {
							PriceData priceData = priceDatas.get(material.getTypeID());
							double price;
							if (userPrices.containsKey(material.getTypeID())) {
								price = userPrices.get(material.getTypeID()).getValue();
							} else {
								price = Asset.getDefaultPrice(priceData);
							}
							priceReprocessed = priceReprocessed + (price * this.getReprocessSettings().getLeft(material.getQuantity()));
						}
						//Unique Ids
						if (!uniqueIds.contains(material.getTypeID())) {
							uniqueIds.add(material.getTypeID());
						}
					}
					if (priceReprocessed > 0 && portionSize > 0) {
						priceReprocessed = priceReprocessed / portionSize;
					}
					eveAsset.setPriceReprocessed(priceReprocessed);
				}

				//Type Count
				if (!uniqueAssetsDuplicates.containsKey(eveAsset.getTypeID())) {
					uniqueAssetsDuplicates.put(eveAsset.getTypeID(), new ArrayList<Asset>());
				}
				if (shouldShow) {
					List<Asset> dup = uniqueAssetsDuplicates.get(eveAsset.getTypeID());
					long newCount = eveAsset.getCount();
					if (!dup.isEmpty()) {
						newCount = newCount + dup.get(0).getTypeCount();
					}
					dup.add(eveAsset);
					for (int b = 0; b < dup.size(); b++) {
						dup.get(b).setTypeCount(newCount);
					}
				}
				//Packaged Volume
				if (!eveAsset.isSingleton() && packagedVolume.containsKey(eveAsset.getGroup())) {
					eveAsset.setVolume(packagedVolume.get(eveAsset.getGroup()));
				}

				//Add asset
				eventListAssets.add(eveAsset);
			}
			//Unique Ids
			if (eveAsset.isMarketGroup() && !uniqueIds.contains(eveAsset.getTypeID())) {
				uniqueIds.add(eveAsset.getTypeID());
			}
			//Add sub-assets
			addAssets(eveAsset.getAssets(), shouldShow);
		}
	}

	public double getPrice(final int typeID, final boolean isBlueprintCopy) {
		UserItem<Integer, Double> userPrice;
		if (isBlueprintCopy) { //Blueprint Copy
			userPrice = userPrices.get(-typeID);
		} else { //All other
			userPrice = userPrices.get(typeID);
		}
		if (userPrice != null) {
			return userPrice.getValue();
		}

		//Blueprint Copy (Default Zero)
		if (isBlueprintCopy) {
			return 0;
		}

		//Price data
		PriceData priceData = null;
		if (priceDatas.containsKey(typeID) && !priceDatas.get(typeID).isEmpty()) { //Market Price
			priceData = priceDatas.get(typeID);
		}
		return Asset.getDefaultPrice(priceData);
	}

	public float getVolume(final int typeID, final boolean packaged) {
		Item item = getItems().get(typeID);
		if (item != null) {
			if (packaged && packagedVolume.containsKey(item.getGroup())) {
				return packagedVolume.get(item.getGroup());
			} else {
				return item.getVolume();
			}
		}
		return 0;
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
	public Date getPriceDataNextUpdate() {
		return priceDataGetter.getNextUpdate();
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
	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(final List<Account> accounts) {
		this.accounts = accounts;
	}

	public void setPriceData(final Map<Integer, PriceData> priceData) {
		this.priceDatas = priceData;
	}

	public Map<String, Boolean> getFlags() {
		return flags;
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(final List<Profile> profiles) {
		this.profiles = profiles;
	}

	public void setActiveProfile(final Profile activeProfile) {
		this.activeProfile = activeProfile;
	}

	public Profile getActiveProfile() {
		return activeProfile;
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

	public Map<Long, ApiStation> getConquerableStations() {
		return conquerableStations;
	}

	public void setConquerableStations(final Map<Long, ApiStation> conquerableStations) {
		this.conquerableStations = conquerableStations;
		for (ApiStation station : conquerableStations.values()) {
			ApiIdConverter.addLocation(station, getLocations());
		}
	}

	public Map<Integer, ItemFlag> getItemFlags() {
		return itemFlags;
	}

	public Map<Integer, Item> getItems() {
		return items;
	}

	public List<Jump> getJumps() {
		return jumps;
	}

	public Map<Long, Location> getLocations() {
		return locations;
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

	public Map<String, List<SimpleColumn>> getTableColumns() {
		return tableColumns;
	}

	public Map<String, Map<String, Integer>> getTableColumnsWidth() {
		return tableColumnsWidth;
	}

	public Map<String, ResizeMode> getTableResize() {
		return tableResize;
	}

	public static boolean isFilterOnEnter() {
		return Settings.filterOnEnter; //Static
	}
	public void setFilterOnEnter(final boolean filterOnEnter) {
		Settings.filterOnEnter = filterOnEnter; //Static
		flags.put(FLAG_FILTER_ON_ENTER, filterOnEnter); //Save & Load
	}
	public boolean isHighlightSelectedRows() {
		return flags.get(FLAG_HIGHLIGHT_SELECTED_ROWS);
	}
	public void setHighlightSelectedRows(final boolean highlightSelectedRows) {
		flags.put(FLAG_HIGHLIGHT_SELECTED_ROWS, highlightSelectedRows);
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
	public boolean isReprocessColors() {
		return flags.get(FLAG_REPROCESS_COLORS);
	}
	public void setReprocessColors(final boolean reprocessColors) {
		flags.put(FLAG_REPROCESS_COLORS, reprocessColors);
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

	public static Date getGmtNow() {
		return getGmt(new Date());
	}

	public static Date getGmt(final Date date) {
		TimeZone tz = TimeZone.getDefault();
		Date ret = new Date(date.getTime() - tz.getRawOffset());

		// if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
		if (tz.inDaylightTime(ret)) {
			Date dstDate = new Date(ret.getTime() - tz.getDSTSavings());

			// check to make sure we have not crossed back into standard time
			// this happens when we are on the cusp of DST (7pm the day before the change for PDT)
			if (tz.inDaylightTime(dstDate)) {
				ret = dstDate;
			}
		}
		return ret;
	}

	public static DateFormat getSettingsDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}

	public boolean isUpdatable(final Date date) {
		return isUpdatable(date, true);
	}

	public boolean isUpdatable(final Date date, final boolean ignoreOnProxy) {
		return ((Settings.getGmtNow().after(date)
				|| Settings.getGmtNow().equals(date)
				|| Program.isForceUpdate()
				|| (getApiProxy() != null && ignoreOnProxy))
				&& !Program.isForceNoUpdate());
	}
}
