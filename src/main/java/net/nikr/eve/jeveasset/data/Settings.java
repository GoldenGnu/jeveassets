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
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.io.local.*;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Settings{

	private final static Logger LOG = LoggerFactory.getLogger(Settings.class);

	private final static String PATH_SETTINGS = "data"+File.separator+"settings.xml";
	private final static String PATH_ITEMS = "data"+File.separator+"items.xml";
	private final static String PATH_JUMPS = "data"+File.separator+"jumps.xml";
	private final static String PATH_LOCATIONS = "data"+File.separator+"locations.xml";
	private final static String PATH_FLAGS = "data"+File.separator+"flags.xml";
	private final static String PATH_DATA_VERSION = "data"+File.separator+"data.xml";
	private final static String PATH_PRICE_DATA = "data"+File.separator+"pricedata.dat";
	private final static String PATH_ASSETS = "data"+File.separator+"assets.xml";
	private final static String PATH_CONQUERABLE_STATIONS = "data"+File.separator+"conquerable_stations.xml";
	private final static String PATH_README = "readme.txt";
	private final static String PATH_LICENSE = "license.txt";
	private final static String PATH_CREDITS = "credits.txt";
	private final static String PATH_CHANGELOG = "changelog.txt";
	private final static String PATH_PROFILES = "profiles";

	private final static String FLAG_IGNORE_SECURE_CONTAINERS = "FLAG_IGNORE_SECURE_CONTAINERS";
	private final static String FLAG_FILTER_ON_ENTER = "FLAG_FILTER_ON_ENTER";
	private final static String FLAG_REPROCESS_COLORS = "FLAG_REPROCESS_COLORS";
	private final static String FLAG_HIGHLIGHT_SELECTED_ROWS = "FLAG_HIGHLIGHT_SELECTED_ROWS";
	private final static String FLAG_AUTO_UPDATE = "FLAG_AUTO_UPDATE";
	private final static String FLAG_UPDATE_DEV = "FLAG_UPDATE_DEV";
	private final static String FLAG_STOCKPILE_FOCUS_TAB = "FLAG_STOCKPILE_FOCUS_TAB";
	private final static String FLAG_STOCKPILE_HALF_COLORS = "FLAG_STOCKPILE_HALF_COLORS";

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
	private final Map<Integer, PriceData> priceFactionData = new HashMap<Integer, PriceData>(); //TypeID : int
	private Map<Integer, UserItem<Integer,Double>> userPrices; //TypeID : int
	private Map<Long, UserItem<Long, String>> userNames; //ItemID : long
	private List<Asset> eventListAssets = null;
	private final List<Stockpile> stockpiles = new ArrayList<Stockpile>();
	private List<Account> accounts;
	private final Map<String, Float> packagedVolume = new HashMap<String, Float>();
	private Date conquerableStationsNextUpdate;
	private Map<String, Boolean> flags;
	private List<Profile> profiles;
	private boolean settingsLoaded;
	private PriceDataSettings priceDataSettings;
	private Proxy proxy;
	private String apiProxy;
	private Point windowLocation;
	private Dimension windowSize;
	private boolean windowMaximized;
	private boolean windowAutoSave;
	private Profile activeProfile;
	private Map<String, OverviewGroup> overviewGroups;
	private ReprocessSettings reprocessSettings;
	private Galaxy model;
	private PriceDataGetter priceDataGetter = new PriceDataGetter(this);
	private static CsvSettings csvSettings = new CsvSettings();
	
	private Map<String, Map<String, List<Filter>>> tableFilters = new HashMap<String, Map<String, List<Filter>>>();
	private Map<String, List<SimpleColumn>> tableColumns = new HashMap<String, List<SimpleColumn>>();
	
	public Settings() {
		SplashUpdater.setProgress(5);
		priceDatas = new HashMap<Integer, PriceData>();
		accounts = new ArrayList<Account>();
		profiles = new ArrayList<Profile>();

		//Settings
		userPrices = new HashMap<Integer, UserItem<Integer,Double>>();
		userNames = new HashMap<Long, UserItem<Long,String>>();
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

		priceDataSettings = new PriceDataSettings(0, PriceDataSettings.SOURCE_EVE_CENTRAL, PriceDataSettings.FactionPrice.PRICES_C0RPORATION);

		windowLocation = new Point(0, 0);
		windowSize = new Dimension(800, 600);
		windowMaximized = false;
		windowAutoSave = true;
		loadSettings();
		model = new Galaxy(this.locations, this.jumps);
		constructEveApiConnector();
	}

	public static CsvSettings getCsvSettings() {
		return csvSettings;
	}

	/**
	 *
	 * @param load does nothing except change the method signature.
	 */
	protected Settings(boolean load) { }

	public Galaxy getGalaxyModel() {
		return model;
	}
	
	public void saveSettings(){
		SettingsWriter.save(this);
		saveAssets();
	}

	private void loadSettings(){
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

	public void loadActiveProfile(){
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

	public void saveAssets(){
		AssetsWriter.save(this, activeProfile.getFilename());
	}

	public PriceDataGetter getPriceDataGetter(){
		return priceDataGetter;
	}

	public static void setPortable(boolean portable) {
		Settings.portable = portable;
	}

	public static boolean isPortable() {
		return portable;
	}

	public void clearEveAssetList(){
		eventListAssets = null;
		uniqueIds = null;
		uniqueAssetsDuplicates = null;
	}
	public List<Asset> getEventListAssets(){
		updateAssetLists();
		return eventListAssets;
	}
	public List<Integer> getUniqueIds(){
		updateAssetLists();
		return uniqueIds;
	}
	
	public boolean hasAssets(){
		updateAssetLists();
		return !uniqueIds.isEmpty();
	}
	private void updateAssetLists(){
		if (eventListAssets == null || uniqueIds == null || uniqueAssetsDuplicates == null){
			eventListAssets = new ArrayList<Asset>();
			uniqueIds = new ArrayList<Integer>();
			uniqueAssetsDuplicates = new HashMap<Integer, List<Asset>>();
			List<String> ownersOrders = new ArrayList<String>();
			List<String> ownersJobs = new ArrayList<String>();
			List<String> ownersAssets = new ArrayList<String>();
			for (Account account : accounts){
				for (Human human : account.getHumans()){
					//Market Orders
					if (!human.getMarketOrders().isEmpty() && !ownersOrders.contains(human.getName())){
						List<Asset> marketOrdersAssets = ApiConverter.apiMarketOrder(human.getMarketOrders(), human, this);
						addAssets(marketOrdersAssets, human.isShowAssets());
						if (human.isShowAssets()) ownersOrders.add(human.getName());
					}
					//Industry Jobs
					if (!human.getIndustryJobs().isEmpty() && !ownersJobs.contains(human.getName())){
						List<Asset> industryJobAssets = ApiConverter.apiIndustryJob(human.getIndustryJobs(), human, this);
						addAssets(industryJobAssets, human.isShowAssets());
						if (human.isShowAssets()) ownersJobs.add(human.getName());
					}
					//Assets (Must be after Industry Jobs, for bpos to be marked)
					if (!human.getAssets().isEmpty() && !ownersAssets.contains(human.getName())){
						addAssets(human.getAssets(), human.isShowAssets());
						if (human.isShowAssets()) ownersAssets.add(human.getName());
					}
					//Add StockpileItems to uniqueIds
					for (Stockpile stockpile: this.getStockpiles()){
						for (StockpileItem item : stockpile.getItems()){
							boolean marketGroup = ApiIdConverter.marketGroup(item.getTypeID(), this.getItems());
							if (marketGroup && !uniqueIds.contains(item.getTypeID())){
								uniqueIds.add(item.getTypeID());
							}
						}
					}
					//Add MarketOrders to uniqueIds
					for (ApiMarketOrder order : human.getMarketOrders()){
						boolean marketGroup = ApiIdConverter.marketGroup(order.getTypeID(), this.getItems());
						if (marketGroup && !uniqueIds.contains(order.getTypeID())){
							uniqueIds.add(order.getTypeID());
						}
					}
					//Add IndustryJobs to uniqueIds
					for (ApiIndustryJob job : human.getIndustryJobs()){
						boolean marketGroup = ApiIdConverter.marketGroup(job.getInstalledItemTypeID(), this.getItems());
						if (marketGroup && !uniqueIds.contains(job.getInstalledItemTypeID())){
							uniqueIds.add(job.getInstalledItemTypeID());
						}
					}
					
				}
			}
		}
	}
	private void addAssets(List<Asset> currentAssets, boolean shouldShow){
		for (Asset eveAsset : currentAssets){
			if (shouldShow){
				//User price
				if (eveAsset.isBlueprint() && !eveAsset.isBpo()) { //Blueprint Copy
					eveAsset.setUserPrice(userPrices.get(-eveAsset.getTypeID()));
				} else { //All other
					eveAsset.setUserPrice(userPrices.get(eveAsset.getTypeID()));
				}
				
				//User Item Names
				if (userNames.containsKey(eveAsset.getItemID())){
					eveAsset.setName(userNames.get(eveAsset.getItemID()).getValue());
				} else {
					eveAsset.setName(eveAsset.getTypeName());
				}
				//Contaioner
				String sContainer = "";
				for (Asset parentEveAsset : eveAsset.getParents()){
					if (!sContainer.isEmpty()) sContainer = sContainer + ">";
					if (!parentEveAsset.isUserName()){
						sContainer = sContainer + parentEveAsset.getName() + " #" + parentEveAsset.getItemID();
					} else {
						sContainer = sContainer + parentEveAsset.getName();
					}
				}
				eveAsset.setContainer(sContainer);

				//Price data
				if (eveAsset.isMarketGroup() && priceDatas.containsKey(eveAsset.getTypeID()) && !priceDatas.get(eveAsset.getTypeID()).isEmpty()){ //Market Price
					eveAsset.setPriceData(priceDatas.get(eveAsset.getTypeID()));
				} else if (priceFactionData.containsKey(eveAsset.getTypeID()) && (getPriceDataSettings().getFactionPrice() == PriceDataSettings.FactionPrice.PRICES_C0RPORATION)){ //Faction Price
					eveAsset.setPriceData(priceFactionData.get(eveAsset.getTypeID()));
				} else { //No Price :(
					eveAsset.setPriceData(null);
				}
				
				//Reprocessed price
				eveAsset.setPriceReprocessed(0);
				if (getItems().containsKey(eveAsset.getTypeID())){
					List<ReprocessedMaterial> reprocessedMaterials = getItems().get(eveAsset.getTypeID()).getReprocessedMaterial();
					double priceReprocessed = 0;
					int portionSize = 0;
					for (ReprocessedMaterial material : reprocessedMaterials){
						//Calculate reprocessed price
						portionSize = material.getPortionSize();
						if (priceDatas.containsKey(material.getTypeID())){
							PriceData priceData = priceDatas.get(material.getTypeID());
							double price = 0;
							if (userPrices.containsKey(material.getTypeID())){
								price = userPrices.get(material.getTypeID()).getValue();
							} else {
								price = Asset.getDefaultPrice(priceData);
							}
							priceReprocessed = priceReprocessed + (price * this.getReprocessSettings().getLeft(material.getQuantity()));
						}
						//Unique Ids
						if (!uniqueIds.contains(material.getTypeID())){
							uniqueIds.add(material.getTypeID());
						}
					}
					if (priceReprocessed > 0 && portionSize > 0){
						priceReprocessed = priceReprocessed / portionSize;
					}
					eveAsset.setPriceReprocessed(priceReprocessed);
				}

				//Type Count
				if (!uniqueAssetsDuplicates.containsKey(eveAsset.getTypeID())){
					uniqueAssetsDuplicates.put(eveAsset.getTypeID(), new ArrayList<Asset>());
				}
				if (shouldShow) {
					List<Asset> dup = uniqueAssetsDuplicates.get(eveAsset.getTypeID());
					long newCount = eveAsset.getCount();
					if (!dup.isEmpty()){
						newCount = newCount + dup.get(0).getTypeCount();
					}
					dup.add(eveAsset);
					for (int b = 0; b < dup.size(); b++){
						dup.get(b).setTypeCount(newCount);
					}
				}
				//Packaged Volume
				if (!eveAsset.isSingleton() && packagedVolume.containsKey(eveAsset.getGroup())){
					eveAsset.setVolume(packagedVolume.get(eveAsset.getGroup()));
				}

				//Add asset
				eventListAssets.add(eveAsset);
			}
			//Unique Ids
			if (eveAsset.isMarketGroup() && !uniqueIds.contains(eveAsset.getTypeID())){
				uniqueIds.add(eveAsset.getTypeID());
			}
			//Add sub-assets
			addAssets(eveAsset.getAssets(), shouldShow);
		}
	}
	
	public double getPrice(int typeID, boolean isBlueprintCopy){
		UserItem<Integer,Double> userPrice = null;
		if (isBlueprintCopy) { //Blueprint Copy
			userPrice = userPrices.get(-typeID);
		} else { //All other
			userPrice = userPrices.get(typeID);
		}
		if (userPrice != null) return userPrice.getValue();
		//Price data
		PriceData priceData = null;
		if (priceDatas.containsKey(typeID) && !priceDatas.get(typeID).isEmpty()){ //Market Price
			priceData = priceDatas.get(typeID);
		} else if (priceFactionData.containsKey(typeID) && (getPriceDataSettings().getFactionPrice() == PriceDataSettings.FactionPrice.PRICES_C0RPORATION)){ //Faction Price
			priceData = priceFactionData.get(typeID);
		}
		return Asset.getDefaultPrice(priceData);
	}
	
	public float getVolume(int typeID, boolean packaged) {
		Item item = getItems().get(typeID);
		if (item != null){
			if (packaged && packagedVolume.containsKey(item.getGroup())){
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
	public void setConquerableStationsNextUpdate(Date conquerableStationNextUpdate) {
		this.conquerableStationsNextUpdate = conquerableStationNextUpdate;
	}
	public PriceDataSettings getPriceDataSettings() {
		return priceDataSettings;
	}
	public void setPriceDataSettings(PriceDataSettings priceDataSettings) {
		this.priceDataSettings = priceDataSettings;
	}
	public Date getPriceDataNextUpdate(){
		return priceDataGetter.getNextUpdate();
	}
	public Map<Integer, UserItem<Integer,Double>> getUserPrices() {
		return userPrices;
	}
	public void setUserPrices(Map<Integer, UserItem<Integer,Double>> userPrices) {
		this.userPrices = userPrices;
	}
	public Map<Long, UserItem<Long,String>> getUserItemNames() {
		return userNames;
	}
	public void setUserItemNames(Map<Long, UserItem<Long,String>> userItemNames) {
		this.userNames = userItemNames;
	}
	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public Map<Integer, PriceData> getPriceFactionData() {
		return priceFactionData;
	}

	public void setPriceData(Map<Integer, PriceData> priceData) {
		this.priceDatas = priceData;
	}
	
	public Map<String, Boolean> getFlags() {
		return flags;
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

	public void setActiveProfile(Profile activeProfile) {
		this.activeProfile = activeProfile;
	}

	public Profile getActiveProfile() {
		return activeProfile;
	}

	public ReprocessSettings getReprocessSettings() {
		return reprocessSettings;
	}

	public void setReprocessSettings(ReprocessSettings reprocessSettings) {
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
	public void setProxy(Proxy proxy) {
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
	public void setProxy(String host, int port, String type) throws IllegalArgumentException {
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
	public void setProxy(String host, int port, Proxy.Type type) throws IllegalArgumentException {
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

	public boolean isForceUpdate(){
		return (apiProxy != null);
	}

	public String getApiProxy() {
		return apiProxy;
	}

	/**
	 * 
	 * @param apiProxy pass null to disable any API proxy, and use the default: http://api.eve-online.com
	 */
	public void setApiProxy(String apiProxy) {
		this.apiProxy = apiProxy;
		constructEveApiConnector();
	}

	/**
	 * build the API Connector and set it in the library.
	 */
	private void constructEveApiConnector() {
		ApiConnector connector = new ApiConnector(); //Default
		if (apiProxy != null) connector = new ApiConnector(getApiProxy()); //API Proxy
		if (proxy != null) connector = new ProxyConnector(getProxy(), connector); //Real Proxy
		EveApi.setConnector(connector);
	}

	public Map<Long, ApiStation> getConquerableStations() {
		return conquerableStations;
	}

	public void setConquerableStations(Map<Long, ApiStation> conquerableStations) {
		this.conquerableStations = conquerableStations;
		for (ApiStation station : conquerableStations.values()){
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
	
	public Map<String, List<Filter>> getTableFilters(String key){
		if (!tableFilters.containsKey(key)){
			tableFilters.put(key, new HashMap<String, List<Filter>>());
		}
		return tableFilters.get(key);
	}

	public Map<String, List<SimpleColumn>> getTableColumns() {
		return tableColumns;
	}
	public List<SimpleColumn> getTableColumns(String key){
		if (!tableColumns.containsKey(key)){
			tableColumns.put(key, new ArrayList<SimpleColumn>());
		}
		return tableColumns.get(key);
	}
	public boolean isFilterOnEnter() {
		return flags.get(FLAG_FILTER_ON_ENTER);
	}
	public void setFilterOnEnter(boolean filterOnEnter) {
		flags.put(FLAG_FILTER_ON_ENTER, filterOnEnter);
	}
	public boolean isHighlightSelectedRows() {
		return flags.get(FLAG_HIGHLIGHT_SELECTED_ROWS);
	}
	public void setHighlightSelectedRows(boolean filterOnEnter) {
		flags.put(FLAG_HIGHLIGHT_SELECTED_ROWS, filterOnEnter);
	}

	public boolean isAutoUpdate() {
		return flags.get(FLAG_AUTO_UPDATE);
	}
	public void setAutoUpdate(boolean updateStable) {
		flags.put(FLAG_AUTO_UPDATE, updateStable);
	}
	public boolean isUpdateDev() {
		return flags.get(FLAG_UPDATE_DEV);
	}
	public void setUpdateDev(boolean updateDev) {
		flags.put(FLAG_UPDATE_DEV, updateDev);
	}
	public boolean isIgnoreSecureContainers() {
		return flags.get(FLAG_IGNORE_SECURE_CONTAINERS);
	}
	public void setIgnoreSecureContainers(boolean ignoreSecureContainers) {
		flags.put(FLAG_IGNORE_SECURE_CONTAINERS, ignoreSecureContainers);
	}
	public boolean isReprocessColors() {
		return flags.get(FLAG_REPROCESS_COLORS);
	}
	public void setReprocessColors(boolean reprocessColors) {
		flags.put(FLAG_REPROCESS_COLORS, reprocessColors);
	}
	public boolean isStockpileFocusTab() {
		return flags.get(FLAG_STOCKPILE_FOCUS_TAB);
	}
	public void setStockpileFocusTab(boolean stockpileFocusOnAdd) {
		flags.put(FLAG_STOCKPILE_FOCUS_TAB, stockpileFocusOnAdd);
	}
	public boolean isStockpileHalfColors() {
		return flags.get(FLAG_STOCKPILE_HALF_COLORS);
	}
	public void setStockpileHalfColors(boolean stockpileHalfColors) {
		flags.put(FLAG_STOCKPILE_HALF_COLORS, stockpileHalfColors);
	}
	public List<Stockpile> getStockpiles() {
		return stockpiles;
	}
	//Window
	public Point getWindowLocation() {
		return windowLocation;
	}

	public void setWindowLocation(Point windowLocation) {
		this.windowLocation = windowLocation;
	}

	public boolean isWindowMaximized() {
		return windowMaximized;
	}

	public void setWindowMaximized(boolean windowMaximized) {
		this.windowMaximized = windowMaximized;
	}

	public Dimension getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Dimension windowSize) {
		this.windowSize = windowSize;
	}

	public boolean isWindowAutoSave() {
		return windowAutoSave;
	}

	public void setWindowAutoSave(boolean windowAutoSave) {
		this.windowAutoSave = windowAutoSave;
	}
	
	public boolean isSettingsLoaded() {
		return settingsLoaded;
	}

	public Map<String, OverviewGroup> getOverviewGroups() {
		return overviewGroups;
	}

	public static String getPathSettings(){
		return getLocalFile(Settings.PATH_SETTINGS, !portable);
	}
	public static String getPathConquerableStations(){
		return getLocalFile(Settings.PATH_CONQUERABLE_STATIONS, !portable);
	}
	public static String getPathJumps(){
		return getLocalFile(Settings.PATH_JUMPS, false);
	}
	public static String getPathFlags(){
		return getLocalFile(Settings.PATH_FLAGS, false);
	}
	public static String getPathPriceData(){
		return getLocalFile(Settings.PATH_PRICE_DATA, !portable);
	}
	public static String getPathAssetsOld(){
		return getLocalFile(Settings.PATH_ASSETS, !portable);
	}
	public static String getPathProfilesDirectory(){
		return getLocalFile(Settings.PATH_PROFILES, !portable);
	}
	public static String getPathItems(){
		return getLocalFile(Settings.PATH_ITEMS, false);
	}
	public static String getPathLocations(){
		return getLocalFile(Settings.PATH_LOCATIONS, false);
	}
	public static String getPathDataVersion(){
		return getLocalFile(Settings.PATH_DATA_VERSION, false);
	}
	public static String getPathReadme(){
		return getLocalFile(Settings.PATH_README, false);
	}
	public static String getPathLicense(){
		return getLocalFile(Settings.PATH_LICENSE, false);
	}
	public static String getPathCredits(){
		return getLocalFile(Settings.PATH_CREDITS, false);
	}
	public static String getPathChangeLog(){
		return getLocalFile(Settings.PATH_CHANGELOG, false);
	}

	public static String getUserDirectory(){
		File userDir = new File(System.getProperty("user.home", "."));
		return userDir.getAbsolutePath()+File.separator;
	}

  /**
   *
   * @param filename the name of the data file to obtain
   * @param dynamic true if the file is expecting to be written to, false for things like the items and locations.
   * @return
   */
	private static String getLocalFile(String filename, boolean dynamic){
		LOG.debug("Looking for file: {} dynamic: {}", filename, dynamic);
		try {
			File file = null;
			File ret = null;
			if (dynamic) {
				File userDir = new File(System.getProperty("user.home", "."));
				if (Program.onMac()) { // preferences are stored in user.home/Library/Preferences
					file = new File(userDir, "Library/Preferences/JEveAssets");
				} else {
					file = new File(userDir.getAbsolutePath()+File.separator+".jeveassets");	
				}
				ret = new File(file.getAbsolutePath()+File.separator+filename);
				File parent = ret.getParentFile();
				if (!parent.exists()
								&& !parent.mkdirs()) {
					LOG.error("failed to create directories for " + parent.getAbsolutePath());
				}
			} else {
				file = new File(net.nikr.eve.jeveasset.Program.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
				ret = new File(file.getAbsolutePath()+File.separator+filename);
			}
			LOG.debug("Found file at: {}", ret.getAbsolutePath());
			return ret.getAbsolutePath();
		} catch (URISyntaxException ex) {
			LOG.error("Failed to get program directory: Please email the latest error.txt in the logs directory to niklaskr@gmail.com", ex);
		}
		return null;
	}

	public static Date getGmtNow() {
		return getGmt( new Date() );
	}

	public static Date getGmt(Date date) {
		TimeZone tz = TimeZone.getDefault();
		Date ret = new Date( date.getTime() - tz.getRawOffset() );

		// if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
		if ( tz.inDaylightTime( ret )) {
			Date dstDate = new Date( ret.getTime() - tz.getDSTSavings() );

			// check to make sure we have not crossed back into standard time
			// this happens when we are on the cusp of DST (7pm the day before the change for PDT)
			if ( tz.inDaylightTime( dstDate )) {
				ret = dstDate;
			}
		}
		return ret;
	}

	public static DateFormat getSettingsDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}

	public boolean isUpdatable(Date date){
		return isUpdatable(date, true);
	}

	public boolean isUpdatable(Date date, boolean ignoreOnProxy){
		return ( (Settings.getGmtNow().after(date)
				|| Settings.getGmtNow().equals(date)
				|| Program.isForceUpdate()
				|| (getApiProxy() != null && ignoreOnProxy) )
				&& !Program.isForceNoUpdate());
	}
}
