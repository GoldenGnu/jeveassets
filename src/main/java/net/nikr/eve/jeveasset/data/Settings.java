/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

import com.beimin.eveapi.AbstractApiParser;
import com.beimin.eveapi.utils.stationlist.ApiStation;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.io.EveApiHumansReader;
import net.nikr.eve.jeveasset.io.LocalAssetsReader;
import net.nikr.eve.jeveasset.io.LocalAssetsWriter;
import net.nikr.eve.jeveasset.io.LocalConquerableStationsReader;
import net.nikr.eve.jeveasset.io.LocalItemsReader;
import net.nikr.eve.jeveasset.io.LocalSettingsReader;
import net.nikr.eve.jeveasset.io.LocalLocationReader;
import net.nikr.eve.jeveasset.io.LocalMarketstatsReader;
import net.nikr.eve.jeveasset.io.LocalSettingsWriter;
import net.nikr.log.Log;


public class Settings {

	private final static String PATH_SETTINGS = "data"+File.separator+"settings.xml";
	private final static String PATH_ITEMS = "data"+File.separator+"items.xml";
	private final static String PATH_LOCATIONS = "data"+File.separator+"locations.xml";
	private final static String PATH_MARKETSTATS = "data"+File.separator+"marketstats.xml";
	private final static String PATH_ASSETS = "data"+File.separator+"assets.xml";
	private final static String PATH_CONQUERABLE_STATIONS = "data"+File.separator+"conquerable_stations.xml";
	private final static String PATH_README = "readme.txt";
	private final static String PATH_LICENSE = "license.txt";
	private final static String PATH_CREDITS = "credits.txt";

	private final static String FLAG_AUTO_RESIZE_COLUMNS_TEXT = "FLAG_AUTO_RESIZE_COLUMNS_TEXT";
	private final static String FLAG_AUTO_RESIZE_COLUMNS_WINDOW = "FLAG_AUTO_RESIZE_COLUMNS_WINDOW";
	private final static String FLAG_FILTER_ON_ENTER = "FLAG_FILTER_ON_ENTER";
	
	//Data
	private Map<String, EveAsset> uniqueAssets = null;
	private Map<String, List<EveAsset>> uniqueAssetsDuplicates = null;
	private List<EveAsset> allAssets = null;
	private List<EveAsset> eventListAssets = null;
	private Map<String, List<AssetFilter>> assetFilters;
	private Map<Integer, Items> items;
	private Map<Integer, Marketstat> marketstats;
	private Map<Integer, Location> locations;
	private Map<Integer, ApiStation> conquerableStations;
	private Map<Long, String> corporations;
	private Map<Integer, UserPrice> userPrices;
	private List<Account> accounts;
	private List<String> tableColumnNames;
	private Map<String, String> tableColumnTooltips;
	private List<String> tableNumberColumns;
	private List<String> tableColumnVisible;
	private Date marketstatsNextUpdate;
	private Date conquerableStationsNextUpdate;
	private Map<Long, Date> corporationsNextUpdate;
	private Map<String, Boolean> flags;
	private List<Integer> bpos;
	private boolean settingsLoaded;
	private MarketstatSettings marketstatSettings;
	private Proxy proxy;
	private String apiProxy;
	//private boolean filterOnEnter;
	
	public Settings() {
		SplashUpdater.setProgress(10);
		items = new HashMap<Integer, Items>();
		locations = new HashMap<Integer, Location>();
		marketstats = new HashMap<Integer, Marketstat>();
		conquerableStations = new HashMap<Integer, ApiStation>();
		assetFilters = new HashMap<String, List<AssetFilter>>();
		accounts = new Vector<Account>();
		corporations = new HashMap<Long, String>();
		userPrices = new HashMap<Integer, UserPrice>();
		bpos = new Vector<Integer>();
		
		flags = new HashMap<String, Boolean>();
		flags.put(FLAG_AUTO_RESIZE_COLUMNS_TEXT, true);
		flags.put(FLAG_AUTO_RESIZE_COLUMNS_WINDOW, false);
		flags.put(FLAG_FILTER_ON_ENTER, false);
		//filterOnEnter = false;
		
		conquerableStationsNextUpdate = Settings.getGmtNow();
		marketstatsNextUpdate = Settings.getGmtNow();
		corporationsNextUpdate =  new HashMap<Long, Date>();  //Settings.cvtToGmt( new Date() );
		resetMainTableColumns();

		marketstatSettings = new MarketstatSettings(0, 0, 0);

		//Load data and overwite default values
		SplashUpdater.setProgress(20);
		settingsLoaded = LocalSettingsReader.load(this);
		LocalAssetsReader.load(this);
		SplashUpdater.setProgress(30);
		LocalItemsReader.load(this);
		SplashUpdater.setProgress(40);
		LocalLocationReader.load(this);
		SplashUpdater.setProgress(50);
		LocalConquerableStationsReader.load(this);
		SplashUpdater.setProgress(60);
		LocalMarketstatsReader.load(this);
		SplashUpdater.setProgress(70);
		EveApiHumansReader.load(this);
	}
	public void resetMainTableColumns(){
		//Also need to update:
		//		gui.table.EveAssetTableFormat.getColumnClass()
		//		gui.table.EveAssetTableFormat.getColumnComparator()
		//		gui.table.EveAssetTableFormat.getColumnValue()
		//		gui.table.EveAssetMatching.matches()
		//			remember to add to "All" as well...
		//		gui.dialogs.CsvExportDialog.getLine()
		//	If number column:
		//		add to mainTableNumberColumns bellow

		tableColumnNames = new Vector<String>();
		tableColumnNames.add("Name");
		tableColumnNames.add("Group");
		tableColumnNames.add("Category");
		tableColumnNames.add("Owner");
		tableColumnNames.add("Location");
		tableColumnNames.add("Region");
		tableColumnNames.add("Container");
		tableColumnNames.add("Flag");
		tableColumnNames.add("Price");
		tableColumnNames.add("Sell Min");
		tableColumnNames.add("Buy Max");
		tableColumnNames.add("Base Price");
		tableColumnNames.add("Value");
		tableColumnNames.add("Count");
		tableColumnNames.add("Type Count");
		tableColumnNames.add("Meta");
		tableColumnNames.add("Volume");
		tableColumnNames.add("ID");
		tableColumnNames.add("Type ID");

		tableColumnTooltips = new HashMap<String, String>();
		//tableColumnTooltips.put("Name", "Name");
		//tableColumnTooltips.put("Group", "Group");
		//tableColumnTooltips.put("Category", "Category");
		//tableColumnTooltips.put("Owner", "Owner");
		//tableColumnTooltips.put("Location", "Station");
		//tableColumnTooltips.put("Region", "Region");
		//tableColumnTooltips.put("Container", "Container");
		//tableColumnTooltips.put("Flag", "Flag");
		tableColumnTooltips.put("Price", "Median Sell Price (Eve-Central)");
		tableColumnTooltips.put("Sell Min", "Minimum Sell Price (Eve-Central)");
		tableColumnTooltips.put("Buy Max", "Maximum Buy Price (Eve-Central)");
		//tableColumnTooltips.put("Base Price", "Base Price");
		tableColumnTooltips.put("Value", "Value (Count*Price)");
		//tableColumnTooltips.put("Count", "Count");
		tableColumnTooltips.put("Type Count", "Type Count (all assets of this type)");
		tableColumnTooltips.put("Meta", "Meta Level");
		//tableColumnTooltips.put("Volume", "Volume");
		tableColumnTooltips.put("ID", "ID (this specific asset)");
		tableColumnTooltips.put("Type ID", "Type ID (this type of asset)");
		
		tableColumnVisible = new Vector<String>(tableColumnNames);

		tableNumberColumns = new Vector<String>();
		tableNumberColumns.add("Count");
		tableNumberColumns.add("Price");
		tableNumberColumns.add("Sell Min");
		tableNumberColumns.add("Buy Max");
		tableNumberColumns.add("Base Price");
		tableNumberColumns.add("Value");
		tableNumberColumns.add("Volume");
		tableNumberColumns.add("ID");
		tableNumberColumns.add("Type ID");
		tableNumberColumns.add("Type Count");

	}
	public void clearEveAssetList(){
		allAssets = null;
		eventListAssets = null;
		uniqueAssets = null;
	}
	public List<EveAsset> getEventListAssets(){
		updateAssetLists(false);
		return eventListAssets;
	}
	public List<EveAsset> getAllAssets(){
		updateAssetLists(true);
		return allAssets;
	}
	public Map<String, EveAsset> getUniqueAssets(){
		updateAssetLists(false);
		return uniqueAssets;
	}
	public boolean hasAssets(){
		updateAssetLists(false);
		return !uniqueAssets.isEmpty();
	}
	private void updateAssetLists(boolean getAllAssets){
		if ((allAssets == null && getAllAssets) ||  (eventListAssets == null && !getAllAssets) ){
			List<EveAsset> assetList = new Vector<EveAsset>();
			uniqueAssets = new HashMap<String, EveAsset>();
			uniqueAssetsDuplicates = new HashMap<String, List<EveAsset>>();
			for (int a = 0; a < accounts.size(); a++){
				Account account = accounts.get(a);
				List<Human> humans = account.getHumans();
				for (int b = 0; b < humans.size(); b++){
					Human human = humans.get(b);
					if (human.isShowAssets() || getAllAssets) addAssets(human.getAssets(), assetList, human.isShowAssets(), human.isUpdateCorporationAssets());
				}
			}
			if (getAllAssets){
				allAssets = assetList;
			} else {
				eventListAssets = assetList;
			}
		}
	}
	private void addAssets(List<EveAsset> currentAssets, List<EveAsset> assetList, boolean shouldShow, boolean shouldShowCorp){
		for (int a = 0; a < currentAssets.size(); a++){
			EveAsset eveAsset = currentAssets.get(a);
			if (userPrices.containsKey(eveAsset.getTypeId())){ //Add User Price
				eveAsset.setUserPrice(userPrices.get(eveAsset.getTypeId()));
			} else { //No user price, clear user price
				eveAsset.setUserPrice(null);
			}
			if (eveAsset.isMarketGroup()){ //Add Marketstarts
				eveAsset.setMarketstat(getMarketstats().get(eveAsset.getTypeId()));
			}
			if (eveAsset.isBlueprint()){
				eveAsset.setBpo(bpos.contains(eveAsset.getId()));
			} else {
				eveAsset.setBpo(false);
			}
			if (shouldShow && (shouldShowCorp && eveAsset.isCorporationAsset()) || !eveAsset.isCorporationAsset()){
				assetList.add(eveAsset);
			}
			if (!uniqueAssets.containsKey(eveAsset.getName())){
				uniqueAssets.put(eveAsset.getName(), eveAsset);
				uniqueAssetsDuplicates.put(eveAsset.getName(), new Vector<EveAsset>());
			}
			if (shouldShow) {
				List<EveAsset> dup = uniqueAssetsDuplicates.get(eveAsset.getName());
				long newCount = eveAsset.getCount();
				if (!dup.isEmpty()){
					newCount = newCount + dup.get(0).getTypeCount();
				}
				dup.add(eveAsset);
				for (int b = 0; b < dup.size(); b++){
					dup.get(b).setTypeCount(newCount);
				}
			}
			addAssets(eveAsset.getAssets(), assetList, shouldShow, shouldShowCorp);
		}
	}
	public List<Integer> getBpos() {
		return bpos;
	}
	public Date getConquerableStationsNextUpdate() {
		return conquerableStationsNextUpdate;
	}
	public void setConquerableStationsNextUpdate(Date conquerableStationNextUpdate) {
		this.conquerableStationsNextUpdate = conquerableStationNextUpdate;
	}
	public MarketstatSettings getMarketstatSettings() {
		return marketstatSettings;
	}
	public void setMarketstatSettings(MarketstatSettings marketstatSettings) {
		this.marketstatSettings = marketstatSettings;
	}
	public Map<Long, String> getCorporations() {
		return corporations;
	}
	public void setCorporations(Map<Long, String> corporations) {
		this.corporations = corporations;
	}
	public Map<Long, Date> getCorporationsNextUpdate() {
		return corporationsNextUpdate;
	}
	public void setCorporationsNextUpdate(Map<Long, Date> corporationNextUpdate) {
		this.corporationsNextUpdate = corporationNextUpdate;
	}
	public Map<Integer, UserPrice> getUserPrices() {
		return userPrices;
	}
	public void setUserPrices(Map<Integer, UserPrice> userPrices) {
		this.userPrices = userPrices;
	}
	public Date getMarketstatsNextUpdate() {
		return marketstatsNextUpdate;
	}
	public void setMarketstatsNextUpdate(Date marketstatsNextUpdate) {
		this.marketstatsNextUpdate = marketstatsNextUpdate;
	}
	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	public Map<Integer, Items> getItems() {
		return items;
	}

	public void setItems(Map<Integer, Items> items) {
		this.items = items;
	}

	public Map<Integer, Location> getLocations() {
		return locations;
	}

	public void setLocations(Map<Integer, Location> locations) {
		this.locations = locations;
	}

	public Map<Integer, Marketstat> getMarketstats() {
		return marketstats;
	}

	public void setMarketstats(Map<Integer, Marketstat> marketstats) {
		this.marketstats = marketstats;
	}

	public Map<Integer, ApiStation> getConquerableStations() {
		return conquerableStations;
	}

	public void setConquerableStations(Map<Integer, ApiStation> conquerableStations) {
		this.conquerableStations = conquerableStations;
	}

	public Map<String, List<AssetFilter>> getAssetFilters() {
		return assetFilters;
	}

	public void setAssetFilters(Map<String, List<AssetFilter>> assetFilters) {
		this.assetFilters = assetFilters;
	}

	public void setTableColumnVisible(List<String> mainTableColumnVisible) {
		this.tableColumnVisible = mainTableColumnVisible;
	}

	public List<String> getTableColumnVisible() {
		return tableColumnVisible;
	}

	public void setTableColumnNames(List<String> mainTableColumnNames) {
		this.tableColumnNames = mainTableColumnNames;
	}

	public List<String> getTableColumnNames(){
		return tableColumnNames;
	}

	public List<String> getTableNumberColumns() {
		return tableNumberColumns;
	}

	public Map<String, String> getTableColumnTooltips() {
		return tableColumnTooltips;
	}
	
	public Map<String, Boolean> getFlags() {
		return flags;
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
		AbstractApiParser.setHttpProxy(proxy);
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

	public String getApiProxy() {
		return apiProxy;
	}

	/**
	 * 
	 * @param apiProxy pass null to disable any API proxy, and use the default: http://api.eve-online.com
	 */
	public void setApiProxy(String apiProxy) {
		AbstractApiParser.setEveApiURL(apiProxy);
		this.apiProxy = apiProxy;
	}

	public boolean isAutoResizeColumnsText() {
		return flags.get(FLAG_AUTO_RESIZE_COLUMNS_TEXT);
	}
	public void setAutoResizeColumnsText(boolean autoResizeColumns) {
		flags.put(FLAG_AUTO_RESIZE_COLUMNS_TEXT, autoResizeColumns);
	}
	public boolean isAutoResizeColumnsWindow() {
		return flags.get(FLAG_AUTO_RESIZE_COLUMNS_WINDOW);
	}
	public void setAutoResizeColumnsWindow(boolean autoResizeColumns) {
		flags.put(FLAG_AUTO_RESIZE_COLUMNS_WINDOW, autoResizeColumns);
	}
	public boolean isFilterOnEnter() {
		return flags.get(FLAG_FILTER_ON_ENTER);
		//return filterOnEnter;
	}
	public void setFilterOnEnter(boolean filterOnEnter) {
		flags.put(FLAG_FILTER_ON_ENTER, filterOnEnter);
	}

	public boolean isSettingsLoaded() {
		return settingsLoaded;
	}
	public void saveSettings(){
		LocalSettingsWriter.save(this);
		LocalAssetsWriter.save(this);
	}
	public static String getPathSettings(){
		return getLocalFile(Settings.PATH_SETTINGS);
	}
	public static String getPathConquerableStations(){
		return getLocalFile(Settings.PATH_CONQUERABLE_STATIONS);
	}
	public static String getPathMarketstats(){
		return getLocalFile(Settings.PATH_MARKETSTATS);
	}
	public static String getPathAssets(){
		return getLocalFile(Settings.PATH_ASSETS);
	}
	public static String getPathItems(){
		return getLocalFile(Settings.PATH_ITEMS, false);
	}
	public static String getPathLocations(){
		return getLocalFile(Settings.PATH_LOCATIONS, false);
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
	public static String getProgramDirectory(){
		return getLocalFile("");
	}

	private static String getLocalFile(String filename){
    return getLocalFile(filename, true);
  }

  /**
   *
   * @param filename the name of the data file to obtain
   * @param dynamic true if the file is expecting to be written to, false for things like the items and locations.
   * @return
   */
	private static String getLocalFile(String filename, boolean dynamic){
		try {
			File file = null;
			File ret = null;
			if (dynamic) {
				File userDir = new File(System.getProperty("user.home", "."));
				file = new File(userDir.getAbsolutePath()+File.separator+".jeveassets");
				ret = new File(file.getAbsolutePath()+File.separator+filename);
				File parent = ret.getParentFile();
				if (!parent.exists()) {
					if (!parent.mkdirs()) {
						Log.error("failed to create directories for " + parent.getAbsolutePath());
					}
				}
			} else {
				file = new File(net.nikr.eve.jeveasset.Program.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
				ret = new File(file.getAbsolutePath()+File.separator+filename);
			}
			return ret.getAbsolutePath();
		} catch (URISyntaxException ex) {
			Log.error("Failed to get program directory: Please email the latest error.txt in the logs directory to niklaskr@gmail.com", ex);
		}
		return null;
	}

	public static Date getGmtNow() {
		TimeZone tz = TimeZone.getDefault();
		Date date = new Date();
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
}
