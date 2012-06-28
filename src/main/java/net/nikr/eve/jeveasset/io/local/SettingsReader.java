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

package net.nikr.eve.jeveasset.io.local;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.*;
import net.nikr.eve.jeveasset.data.Asset.PriceMode;
import net.nikr.eve.jeveasset.data.ExportSettings.DecimalSeperator;
import net.nikr.eve.jeveasset.data.ExportSettings.FieldDelimiter;
import net.nikr.eve.jeveasset.data.ExportSettings.LineDelimiter;
import net.nikr.eve.jeveasset.data.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.PriceDataSettings.RegionType;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserNameSettingsPanel.UserName;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel.UserPrice;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.EveAssetTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.*;
import net.nikr.eve.jeveasset.io.local.update.Update;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.me.candle.eve.pricing.options.LocationType;


public class SettingsReader extends AbstractXmlReader {

	public static final int SETTINGS_VERSION = 2;

	private static final Logger LOG = LoggerFactory.getLogger(SettingsReader.class);

	public static boolean load(final Settings settings) {
		try {
			Update updater = new Update();
			updater.performUpdates(SETTINGS_VERSION, settings.getPathSettings());

			Element element = getDocumentElement(settings.getPathSettings());
			parseSettings(element, settings);
		} catch (IOException ex) {
			LOG.info("Settings not loaded");
			return false;
		} catch (XmlException ex) {
			LOG.error("Settings parser error: (" + settings.getPathSettings() + ")" + ex.getMessage(), ex);
		}
		LOG.info("Settings loaded");
		return true;
	}

	private static void parseSettings(final Element element, final Settings settings) throws XmlException {
		if (!element.getNodeName().equals("settings")) {
			throw new XmlException("Wrong root element name.");
		}

		//CsvExport
		NodeList stockpilesNodes = element.getElementsByTagName("stockpiles");
		if (stockpilesNodes.getLength() == 1) {
			Element stockpilesElement = (Element) stockpilesNodes.item(0);
			parseStockpiles(stockpilesElement, settings);
		}

		//Export Settings
		NodeList exportNodes = element.getElementsByTagName("csvexport");
		if (exportNodes.getLength() == 1) {
			Element exportElement = (Element) exportNodes.item(0);
			parseExportSettings(exportElement);
		}

		//Overview
		NodeList overviewNodes = element.getElementsByTagName("overview");
		if (overviewNodes.getLength() == 1) {
			Element overviewElement = (Element) overviewNodes.item(0);
			parseOverview(overviewElement, settings);
		}

		//Window
		NodeList windowNodes = element.getElementsByTagName("window");
		if (windowNodes.getLength() == 1) {
			Element windowElement = (Element) windowNodes.item(0);
			parseWindow(windowElement, settings);
		}

		//Reprocessing
		NodeList reprocessingNodes = element.getElementsByTagName("reprocessing");
		if (reprocessingNodes.getLength() == 1) {
			Element reprocessingElement = (Element) reprocessingNodes.item(0);
			parseReprocessing(reprocessingElement, settings);
		}

		//UserPrices
		NodeList userPriceNodes = element.getElementsByTagName("userprices");
		if (userPriceNodes.getLength() == 1) {
			Element userPriceElement = (Element) userPriceNodes.item(0);
			parseUserPrices(userPriceElement, settings);
		}


		//User Item Names
		NodeList userItemNameNodes = element.getElementsByTagName("itemmames");
		if (userItemNameNodes.getLength() == 1) {
			Element userItemNameElement = (Element) userItemNameNodes.item(0);
			parseUserItemNames(userItemNameElement, settings);
		}

		//PriceDataSettings
		NodeList priceDataSettingsNodes = element.getElementsByTagName("marketstat");
		if (priceDataSettingsNodes.getLength() == 1) {
			Element priceDataSettingsElement = (Element) priceDataSettingsNodes.item(0);
			parsePriceDataSettings(priceDataSettingsElement, settings);
		}

		//Flags
		NodeList flagNodes = element.getElementsByTagName("flags");
		if (flagNodes.getLength() != 1) {
			throw new XmlException("Wrong flag element count.");
		}
		Element flagsElement = (Element) flagNodes.item(0);
		parseFlags(flagsElement, settings);

		//Updates
		NodeList updateNodes = element.getElementsByTagName("updates");
		if (updateNodes.getLength() != 1) {
			throw new XmlException("Wrong updates element count.");
		}
		Element updatesElement = (Element) updateNodes.item(0);
		parseUpdates(updatesElement, settings);

		//Table Filters (Must be loaded before Asset Filters)
		NodeList tablefiltersNodes = element.getElementsByTagName("tablefilters");
		if (tablefiltersNodes.getLength() == 1) {
			Element tablefiltersElement = (Element) tablefiltersNodes.item(0);
			parseTableFilters(tablefiltersElement, settings);
		}

		//Asset Filters
		NodeList filterNodes = element.getElementsByTagName("filters");
		if (filterNodes.getLength() == 1) {
			Element filtersElement = (Element) filterNodes.item(0);
			parseAssetFilters(filtersElement, settings);
		}

		//Table Columns
		NodeList tablecolumnsNodes = element.getElementsByTagName("tablecolumns");
		if (tablecolumnsNodes.getLength() == 1) {
			Element tablecolumnsElement = (Element) tablecolumnsNodes.item(0);
			parseTableColumns(tablecolumnsElement, settings);
		}

		//Table Resize
		NodeList tableResizeNodes = element.getElementsByTagName("tableresize");
		if (tableResizeNodes.getLength() == 1) {
			Element tableResizeElement = (Element) tableResizeNodes.item(0);
			parseTableResize(tableResizeElement, settings);
		}

		// Proxy can have 0 or 1 proxy elements; at 0, the proxy stays as null.
		NodeList proxyNodes = element.getElementsByTagName("proxy");
		if (proxyNodes.getLength() == 1) {
			Element proxyElement = (Element) proxyNodes.item(0);
			parseProxy(proxyElement, settings);
		} else if (proxyNodes.getLength() > 1) {
			throw new XmlException("Wrong proxy element count.");
		}

		// API Proxy; 0 or 1 elements.
		NodeList apiProxyNodes = element.getElementsByTagName("apiProxy");
		switch (apiProxyNodes.getLength()) { // I think the 'switch' is a lot neater then the if/elseif blocks. - Candle
			case 0:
				break;
			case 1:
				Element apiProxyElement = (Element) apiProxyNodes.item(0);
				parseApiProxy(apiProxyElement, settings);
				break;
			default:
				throw new XmlException("Wrong apiProxy element count.");
		}
	}

	private static void parseStockpiles(final Element stockpilesElement, final Settings settings) {
		NodeList stockpileNodes = stockpilesElement.getElementsByTagName("stockpile");
		for (int a = 0; a < stockpileNodes.getLength(); a++) {
			Element stockpileNode = (Element) stockpileNodes.item(a);
			String name = AttributeGetters.getString(stockpileNode, "name");

			long ownerID = AttributeGetters.getLong(stockpileNode, "characterid");
			String container = AttributeGetters.getString(stockpileNode, "container");
			int flagID = AttributeGetters.getInt(stockpileNode, "flagid");
			long locationID = AttributeGetters.getLong(stockpileNode, "locationid");

			Location location = settings.getLocations().get(locationID);
			String station = null;
			String system = null;
			String region = null;
			if (location == null) {
				location = StockpileDialog.LOCATION_ALL;
			}
			if (location.isRegion() || location.isSystem() || location.isStation()) {
				region = ApiIdConverter.regionName(location.getLocationID(), null, settings.getLocations());
			}
			if (location.isSystem() || location.isStation()) {
				system = ApiIdConverter.systemName(location.getLocationID(), null, settings.getLocations());
			}
			if (location.isStation()) {
				station = ApiIdConverter.locationName(location.getLocationID(), null, settings.getLocations());
			} else if (location.isSystem()) {
				station = system;
			} else if (location.isRegion()) {
				station = region;
			}
			boolean inventory = AttributeGetters.getBoolean(stockpileNode, "inventory");
			boolean sellOrders = AttributeGetters.getBoolean(stockpileNode, "sellorders");
			boolean buyOrders = AttributeGetters.getBoolean(stockpileNode, "buyorders");
			boolean jobs = AttributeGetters.getBoolean(stockpileNode, "jobs");

			Stockpile stockpile = new Stockpile(name, ownerID, "", locationID, station, system, region, flagID, "", container, inventory, sellOrders, buyOrders, jobs);
			settings.getStockpiles().add(stockpile);
			NodeList itemNodes = stockpileNode.getElementsByTagName("item");
			for (int b = 0; b < itemNodes.getLength(); b++) {
				Element itemNode = (Element) itemNodes.item(b);
				int typeID = AttributeGetters.getInt(itemNode, "typeid");
				long countMinimum = AttributeGetters.getLong(itemNode, "minimum");
				if (typeID > 0) { //Ignore Total
					String itemName = ApiIdConverter.typeName(typeID, settings.getItems());
					String itemGroup = ApiIdConverter.group(typeID, settings.getItems());
					StockpileItem item = new StockpileItem(stockpile, itemName, itemGroup, typeID, countMinimum);
					stockpile.add(item);
				}
			}
		}
	}

	private static void parseOverview(final Element overviewElement, final Settings settings) {
		NodeList groupNodes = overviewElement.getElementsByTagName("group");
		for (int a = 0; a < groupNodes.getLength(); a++) {
			Element groupNode = (Element) groupNodes.item(a);
			String name = AttributeGetters.getString(groupNode, "name");
			OverviewGroup overviewGroup = new OverviewGroup(name);
			settings.getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
			NodeList locationNodes = groupNode.getElementsByTagName("location");
			for (int b = 0; b < locationNodes.getLength(); b++) {
				Element locationNode = (Element) locationNodes.item(b);
				String location = AttributeGetters.getString(locationNode, "name");
				String type = AttributeGetters.getString(locationNode, "type");
				overviewGroup.add(new OverviewLocation(location, OverviewLocation.LocationType.valueOf(type)));
			}
		}
	}

	private static void parseReprocessing(final Element windowElement, final Settings settings) {
		int refining = AttributeGetters.getInt(windowElement, "refining");
		int efficiency = AttributeGetters.getInt(windowElement, "efficiency");
		int processing = AttributeGetters.getInt(windowElement, "processing");
		int station = AttributeGetters.getInt(windowElement, "station");
		settings.setReprocessSettings(new ReprocessSettings(station, refining, efficiency, processing));
	}

	private static void parseWindow(final Element windowElement, final Settings settings) {
		int x = AttributeGetters.getInt(windowElement, "x");
		int y = AttributeGetters.getInt(windowElement, "y");
		int height = AttributeGetters.getInt(windowElement, "height");
		int width = AttributeGetters.getInt(windowElement, "width");
		boolean maximized = AttributeGetters.getBoolean(windowElement, "maximized");
		boolean autosave = AttributeGetters.getBoolean(windowElement, "autosave");
		boolean alwaysOnTop = false;
		if (AttributeGetters.haveAttribute(windowElement, "alwaysontop")) {
			alwaysOnTop = AttributeGetters.getBoolean(windowElement, "alwaysontop");
		}
		settings.setWindowLocation(new Point(x, y));
		settings.setWindowSize(new Dimension(width, height));
		settings.setWindowMaximized(maximized);
		settings.setWindowAutoSave(autosave);
		settings.setWindowAlwaysOnTop(alwaysOnTop);
	}

	private static void parseProxy(final Element proxyElement, final Settings settings) {
		String addrName = AttributeGetters.getString(proxyElement, "address");
		String proxyType = AttributeGetters.getString(proxyElement, "type");
		Integer port = AttributeGetters.getInt(proxyElement, "port");
		if (addrName.length() > 0
						&& proxyType.length() > 0
						&& port != null
						&& port >= 0) { // check the proxy attributes are all there.

			// delegate to the utility method in the Settings.
			try {
				settings.setProxy(addrName, port, proxyType);
			} catch (IllegalArgumentException iae) { //catch none valid proxt settings
				settings.setProxy(null);
			}
		}
	}

	private static void parseUserPrices(final Element element, final Settings settings) {
		NodeList userPriceNodes = element.getElementsByTagName("userprice");
		for (int a = 0; a < userPriceNodes.getLength(); a++) {
			Element currentNode = (Element) userPriceNodes.item(a);
			String name = AttributeGetters.getString(currentNode, "name");
			double price = AttributeGetters.getDouble(currentNode, "price");
			int typeID = AttributeGetters.getInt(currentNode, "typeid");
			UserItem<Integer, Double> userPrice = new UserPrice(price, typeID, name);
			settings.getUserPrices().put(typeID, userPrice);
		}
	}

	private static void parseUserItemNames(final Element element, final Settings settings) {
		NodeList userPriceNodes = element.getElementsByTagName("itemname");
		for (int a = 0; a < userPriceNodes.getLength(); a++) {
			Element currentNode = (Element) userPriceNodes.item(a);
			String name = AttributeGetters.getString(currentNode, "name");
			String typeName = AttributeGetters.getString(currentNode, "typename");
			long itemId = AttributeGetters.getLong(currentNode, "itemid");
			UserItem<Long, String> userItemName = new UserName(name, itemId, typeName);
			settings.getUserItemNames().put(itemId, userItemName);
		}
	}

	private static void parsePriceDataSettings(final Element element, final Settings settings) {
		PriceMode priceType = Asset.getDefaultPriceType();
		if (AttributeGetters.haveAttribute(element, "defaultprice")) {
			priceType = PriceMode.valueOf(AttributeGetters.getString(element, "defaultprice"));
		}
		Asset.setPriceType(priceType);
		//null = default
		List<Long> locations = null;
		LocationType locationType = null;
		//Backward compatibility
		if (AttributeGetters.haveAttribute(element, "regiontype")) {
			RegionType regionType = RegionType.valueOf(AttributeGetters.getString(element, "regiontype"));
			locations = regionType.getRegions();
			locationType = LocationType.REGION;
		}
		if (AttributeGetters.haveAttribute(element, "locations")) {
			String string = AttributeGetters.getString(element, "locations");
			String[] split = string.split(",");
			locations = new ArrayList<Long>();
			for (String s : split) {
				try {
					locations.add(Long.valueOf(s));
				} catch (NumberFormatException ex) {
					LOG.warn("Could not parse locations long: " + s);
				}
			}
		}
		if (AttributeGetters.haveAttribute(element, "type")) {
			locationType = LocationType.valueOf(AttributeGetters.getString(element, "type"));
		}
		PriceSource priceSource = PriceDataSettings.getDefaultPriceSource();
		if (AttributeGetters.haveAttribute(element, "pricesource")) {
			try {
				priceSource = PriceSource.valueOf(AttributeGetters.getString(element, "pricesource"));
			} catch (IllegalArgumentException ex) {
				//In case a price source is removed: Use the default
			}
		}
		settings.setPriceDataSettings(new PriceDataSettings(locationType, locations, priceSource));
	}

	private static void parseFlags(final Element element, final Settings settings) {
		NodeList flagNodes = element.getElementsByTagName("flag");
		for (int a = 0; a < flagNodes.getLength(); a++) {
			Element currentNode = (Element) flagNodes.item(a);
			String key = AttributeGetters.getString(currentNode, "key");
			boolean enabled = AttributeGetters.getBoolean(currentNode, "enabled");
			settings.getFlags().put(key, enabled);
		}
	}

	private static void parseUpdates(final Element element, final Settings settings) {
		NodeList updateNodes = element.getElementsByTagName("update");
		for (int a = 0; a < updateNodes.getLength(); a++) {
			Element currentNode = (Element) updateNodes.item(a);
			parseUpdate(currentNode, settings);
		}
	}
	private static void parseUpdate(final Element element, final Settings settings) {
		String text = AttributeGetters.getString(element, "name");
		Date nextUpdate = new Date(AttributeGetters.getLong(element, "nextupdate"));
		if (text.equals("conquerable station")) {
			settings.setConquerableStationsNextUpdate(nextUpdate);
		}
	}

	private static void parseTableColumns(final Element element, final Settings settings) {
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			List<SimpleColumn> columns = new ArrayList<SimpleColumn>();
			Element tableNode = (Element) tableNodeList.item(a);
			String tableName = AttributeGetters.getString(tableNode, "name");
			//Ignore old tables
			if (tableName.equals("marketorderssell") || tableName.equals("marketordersbuy")) {
				continue;
			}
			NodeList columnNodeList = tableNode.getElementsByTagName("column");
			for (int b = 0; b < columnNodeList.getLength(); b++) {
				Element columnNode = (Element) columnNodeList.item(b);
				String name = AttributeGetters.getString(columnNode, "name");
				boolean shown = AttributeGetters.getBoolean(columnNode, "shown");
				columns.add(new SimpleColumn(name, shown));
			}
			settings.getTableColumns().put(tableName, columns);
		}
	}
	private static void parseTableResize(final Element element, final Settings settings) {
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			Element tableNode = (Element) tableNodeList.item(a);
			String tableName = AttributeGetters.getString(tableNode, "name");
			ResizeMode resizeMode = ResizeMode.valueOf(AttributeGetters.getString(tableNode, "resize"));
			settings.getTableResize().put(tableName, resizeMode);
		}
	}

	private static void parseTableFilters(final Element element, final Settings settings) {
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			Element tableNode = (Element) tableNodeList.item(a);
			String tableName = AttributeGetters.getString(tableNode, "name");
			NodeList filterNodeList = tableNode.getElementsByTagName("filter");
			Map<String, List<Filter>> filters = new HashMap<String, List<Filter>>();
			for (int b = 0; b < filterNodeList.getLength(); b++) {
				Element filterNode = (Element) filterNodeList.item(b);
				String filterName = AttributeGetters.getString(filterNode, "name");
				List<Filter> filter = new ArrayList<Filter>();
				NodeList rowNodes = filterNode.getElementsByTagName("row");
				for (int c = 0; c < rowNodes.getLength(); c++) {
					Element rowNode = (Element) rowNodes.item(c);
					String text = AttributeGetters.getString(rowNode, "text");
					String columnString = AttributeGetters.getString(rowNode, "column");
					Enum column =  getColumn(columnString, tableName);
					String compare = AttributeGetters.getString(rowNode, "compare");
					String logic = AttributeGetters.getString(rowNode, "logic");
					filter.add(new Filter(logic, column, compare, text));
				}
				filters.put(filterName, filter);
			}
			settings.getTableFilters().put(tableName, filters);
		}
	}

	private static Enum getColumn(final String column, final String tableName) {
		try {
			if (tableName.equals(StockpileTab.NAME)) {
				return StockpileExtendedTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		try {
			if (tableName.equals(StockpileTab.NAME)) {
				return StockpileTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		try {
			if (tableName.equals(IndustryJobsTab.NAME)) {
				return IndustryJobTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		try {
			if (tableName.equals(MarketOrdersTab.NAME)) {
				return MarketTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		try {
			if (tableName.equals(AssetsTab.NAME)) {
				return EveAssetTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		try {
			if (tableName.equals(ItemsTab.NAME)) {
				return ItemTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		try { //All
			return Filter.ExtraColumns.valueOf(column);
		} catch (IllegalArgumentException exception) {

		}
		throw new RuntimeException("Fail to load filter column: " + column);
	}

	private static void parseAssetFilters(final Element filtersElement, final Settings settings) {
		NodeList filterNodeList = filtersElement.getElementsByTagName("filter");
		for (int a = 0; a < filterNodeList.getLength(); a++) {
			Element filterNode = (Element) filterNodeList.item(a);
			String filterName = AttributeGetters.getString(filterNode, "name");

			List<Filter> filters = new ArrayList<Filter>();

			NodeList rowNodeList = filterNode.getElementsByTagName("row");
			for (int b = 0; b < rowNodeList.getLength(); b++) {
				Element rowNode = (Element) rowNodeList.item(b);
				LogicType logic = convertLogic(AttributeGetters.getBoolean(rowNode, "and"));
				Enum column = convertColumn(AttributeGetters.getString(rowNode, "column"));
				CompareType compare = convertMode(AttributeGetters.getString(rowNode, "mode"));
				String text;
				if (AttributeGetters.haveAttribute(rowNode, "columnmatch")) {
					text =  convertColumn(AttributeGetters.getString(rowNode, "columnmatch")).name();
				} else {
					text = AttributeGetters.getString(rowNode, "text");
				}
				Filter filter = new Filter(logic, column, compare, text);
				filters.add(filter);
			}
			settings.getTableFilters(AssetsTab.NAME).put(filterName, filters);
		}
	}

	private static LogicType convertLogic(final boolean logic) {
		if (logic) {
			return LogicType.AND;
		} else {
			return LogicType.OR;
		}
	}

	private static Enum convertColumn(final String column) {
		if (column.equals("Name")) { return EveAssetTableFormat.NAME; }
		if (column.equals("Group")) { return EveAssetTableFormat.GROUP; }
		if (column.equals("Category")) { return EveAssetTableFormat.CATEGORY; }
		if (column.equals("Owner")) { return EveAssetTableFormat.OWNER; }
		if (column.equals("Count")) { return EveAssetTableFormat.COUNT; }
		if (column.equals("Location")) { return EveAssetTableFormat.LOCATION; }
		if (column.equals("Container")) { return EveAssetTableFormat.CONTAINER; }
		if (column.equals("Flag")) { return EveAssetTableFormat.FLAG; }
		if (column.equals("Price")) { return EveAssetTableFormat.PRICE; }
		if (column.equals("Sell Min")) { return EveAssetTableFormat.PRICE_SELL_MIN; }
		if (column.equals("Buy Max")) { return EveAssetTableFormat.PRICE_BUY_MAX; }
		if (column.equals("Base Price")) { return EveAssetTableFormat.PRICE_BASE; }
		if (column.equals("Value")) { return EveAssetTableFormat.VALUE; }
		if (column.equals("Meta")) { return EveAssetTableFormat.META; }
		if (column.equals("ID")) { return EveAssetTableFormat.ITEM_ID; }
		if (column.equals("Volume")) { return EveAssetTableFormat.VOLUME; }
		if (column.equals("Type ID")) { return EveAssetTableFormat.TYPE_ID; }
		if (column.equals("Region")) { return EveAssetTableFormat.REGION; }
		if (column.equals("Type Count")) { return EveAssetTableFormat.COUNT_TYPE; }
		if (column.equals("Security")) { return EveAssetTableFormat.SECURITY; }
		if (column.equals("Reprocessed")) { return EveAssetTableFormat.PRICE_REPROCESSED; }
		if (column.equals("Reprocessed Value")) { return EveAssetTableFormat.VALUE_REPROCESSED; }
		if (column.equals("Singleton")) { return EveAssetTableFormat.SINGLETON; }
		if (column.equals("Total Volume")) { return EveAssetTableFormat.VOLUME_TOTAL; }
		return Filter.ExtraColumns.ALL; //Fallback
	}

	private static CompareType convertMode(final String compareMixed) {
		String compare = compareMixed.toUpperCase();
		if (compare.equals("MODE_EQUALS")) { return CompareType.EQUALS; }
		if (compare.equals("MODE_CONTAIN")) { return CompareType.CONTAINS; }
		if (compare.equals("MODE_CONTAIN_NOT")) { return CompareType.CONTAINS_NOT; }
		if (compare.equals("MODE_EQUALS_NOT")) { return CompareType.EQUALS_NOT; }
		if (compare.equals("MODE_GREATER_THAN")) { return CompareType.GREATER_THAN; }
		if (compare.equals("MODE_LESS_THAN")) { return CompareType.LESS_THAN; }
		if (compare.equals("MODE_GREATER_THAN_COLUMN")) { return CompareType.GREATER_THAN_COLUMN; }
		if (compare.equals("MODE_LESS_THAN_COLUMN")) { return CompareType.LESS_THAN_COLUMN; }
		return CompareType.CONTAINS;
	}

	private static void parseApiProxy(final Element apiProxyElement, final Settings settings) {
		String proxyURL = AttributeGetters.getString(apiProxyElement, "url");
		settings.setApiProxy(proxyURL);
	}

	private static void parseExportSettings(final Element element) {
		//CSV
		DecimalSeperator decimal = DecimalSeperator.valueOf(AttributeGetters.getString(element, "decimal"));
		FieldDelimiter field = FieldDelimiter.valueOf(AttributeGetters.getString(element, "field"));
		LineDelimiter line = LineDelimiter.valueOf(AttributeGetters.getString(element, "line"));
		Settings.getExportSettings().setDecimalSeperator(decimal);
		Settings.getExportSettings().setFieldDelimiter(field);
		Settings.getExportSettings().setLineDelimiter(line);
		//SQL
		if (AttributeGetters.haveAttribute(element, "sqlcreatetable")) {
			boolean createTable = AttributeGetters.getBoolean(element, "sqlcreatetable");
			Settings.getExportSettings().setCreateTable(createTable);
		}
		if (AttributeGetters.haveAttribute(element, "sqldroptable")) {
			boolean dropTable = AttributeGetters.getBoolean(element, "sqldroptable");
			Settings.getExportSettings().setDropTable(dropTable);
		}
		if (AttributeGetters.haveAttribute(element, "sqlextendedinserts")) {
			boolean extendedInserts = AttributeGetters.getBoolean(element, "sqlextendedinserts");
			Settings.getExportSettings().setExtendedInserts(extendedInserts);
		}
		NodeList tableNamesNodeList = element.getElementsByTagName("sqltablenames");
		for (int a = 0; a < tableNamesNodeList.getLength(); a++) {
			Element tableNameNode = (Element) tableNamesNodeList.item(a);
			String tool = AttributeGetters.getString(tableNameNode, "tool");
			String tableName = AttributeGetters.getString(tableNameNode, "tablename");
			Settings.getExportSettings().putTableName(tool, tableName);
		}
		//Shared
		NodeList fileNamesNodeList = element.getElementsByTagName("filenames");
		for (int a = 0; a < fileNamesNodeList.getLength(); a++) {
			Element tableNameNode = (Element) fileNamesNodeList.item(a);
			String tool = AttributeGetters.getString(tableNameNode, "tool");
			String fileName = AttributeGetters.getString(tableNameNode, "filename");
			Settings.getExportSettings().putFilename(tool, fileName);
		}
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			List<String> columns = new ArrayList<String>();
			Element tableNode = (Element) tableNodeList.item(a);
			String tableName = AttributeGetters.getString(tableNode, "name");
			NodeList columnNodeList = tableNode.getElementsByTagName("column");
			for (int b = 0; b < columnNodeList.getLength(); b++) {
				Element columnNode = (Element) columnNodeList.item(b);
				String name = AttributeGetters.getString(columnNode, "name");
				columns.add(name);
			}
			Settings.getExportSettings().putTableExportColumns(tableName, columns);
		}
	}
}
