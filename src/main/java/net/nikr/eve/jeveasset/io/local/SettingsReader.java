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

package net.nikr.eve.jeveasset.io.local;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderRange;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.AddedData;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorTheme.ColorThemeTypes;
import net.nikr.eve.jeveasset.data.settings.ExportSettings;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.ColumnSelection;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.DecimalSeparator;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.ExportFormat;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.FilterSelection;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.LineDelimiter;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingFacility;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.data.settings.MarketOrdersSettings;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceMode;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.settings.ProxyData;
import net.nikr.eve.jeveasset.data.settings.ReprocessSettings;
import net.nikr.eve.jeveasset.data.settings.RouteResult;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.Settings.SettingFlag;
import net.nikr.eve.jeveasset.data.settings.Settings.SettingsFactory;
import net.nikr.eve.jeveasset.data.settings.Settings.TransactionProfitPrice;
import net.nikr.eve.jeveasset.data.settings.StockpileGroupSettings;
import net.nikr.eve.jeveasset.data.settings.TrackerData;
import net.nikr.eve.jeveasset.data.settings.TrackerSettings;
import net.nikr.eve.jeveasset.data.settings.TrackerSettings.DisplayType;
import net.nikr.eve.jeveasset.data.settings.TrackerSettings.ShowOption;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.data.settings.tag.Tag;
import net.nikr.eve.jeveasset.data.settings.tag.TagColor;
import net.nikr.eve.jeveasset.data.settings.tag.TagID;
import net.nikr.eve.jeveasset.gui.dialogs.settings.SoundsSettingsPanel.SoundOption;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserNameSettingsPanel.UserName;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel.UserPrice;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog.Formula;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps.Jump;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.FormulaColumn;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.JumpColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.sounds.DefaultSound;
import net.nikr.eve.jeveasset.gui.sounds.FileSound;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsExtendedTableFormat;
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
import net.nikr.eve.jeveasset.gui.tabs.orders.Outbid;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import net.nikr.eve.jeveasset.gui.tabs.skills.SkillsTab;
import net.nikr.eve.jeveasset.gui.tabs.skills.SkillsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTab;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileContainer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileFlag;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerDate;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerNote;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerSkillPointFilter;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.local.update.Update;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.me.candle.eve.pricing.options.LocationType;


public final class SettingsReader extends AbstractXmlReader<Boolean> {

	public static final int SETTINGS_VERSION = 2;

	private static final Logger LOG = LoggerFactory.getLogger(SettingsReader.class);

	private enum ReaderType {
		SETTINGS, STOCKPILE, TRACKER, ROUTES
	}

	private Settings settings;
	private SettingsFactory settingsFactory;
	private List<Stockpile> stockpilesList;
	private Map<String, List<Value>> trackerDataMap;
	private Map<String, RouteResult> routes;
	private final ReaderType readerType;

	private SettingsReader(ReaderType readerType) {
		this.readerType = readerType;
	}

	public static Settings load(final SettingsFactory settingsFactory, final String filename) {
		SettingsReader reader = new SettingsReader(ReaderType.SETTINGS);
		reader.setSettingsFactory(settingsFactory);
		Update updater = new Update();
		try {
			updater.performUpdates(SETTINGS_VERSION, filename);
		} catch (XmlException ex) {
			LOG.error(ex.getMessage(), ex);
			Settings settings = settingsFactory.create();
			settings.setSettingsLoadError(true);
			return settings;
		}
		Boolean ok = reader.read("Settings", filename, XmlType.DYNAMIC_BACKUP);
		Settings settings = reader.getSettings();
		if (!ok || settings == null) {
			settings = settingsFactory.create();
		}
		if (!ok) {
			settings.setSettingsLoadError(true);
		}
		return settings;
	}

	private void setSettingsFactory(SettingsFactory settingsFactory) {
		this.settingsFactory = settingsFactory;
	}

	private Settings getSettings() {
		return settings;
	}

	private List<Stockpile> getStockpiles() {
		return stockpilesList;
	}

	private Map<String, List<Value>> getTrackerDataMap() {
		return trackerDataMap;
	}

	private Map<String, RouteResult> getRoutes() {
		return routes;
	}

	public static List<Stockpile> loadStockpile(final String filename) {
		SettingsReader reader = new SettingsReader(ReaderType.STOCKPILE);
		if (reader.read(filename, filename, XmlType.IMPORT)) {
			return reader.getStockpiles();
		} else {
			return null;
		}
	}

	public static Map<String, List<Value>> loadTracker(final String filename) {
		SettingsReader reader = new SettingsReader(ReaderType.TRACKER);
		if (reader.read(filename, filename, XmlType.IMPORT)) {
			return reader.getTrackerDataMap();
		} else {
			return null;
		}
	}

	public static Map<String, RouteResult> loadRoutes(final String filename) {
		SettingsReader reader = new SettingsReader(ReaderType.ROUTES);
		if (reader.read(filename, filename, XmlType.IMPORT)) {
			return reader.getRoutes();
		} else {
			return null;
		}
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		switch (readerType) {
			case SETTINGS:
				settings = loadSettings(element, settingsFactory.create());
				break;
			case STOCKPILE:
				stockpilesList = loadStockpile(element);
				break;
			case TRACKER:
				trackerDataMap = loadTracker(element);
				break;
			case ROUTES:
				routes = loadRoutes(element);
				break;
		}
		return true;
	}

	@Override
	protected Boolean failValue() {
		return false;
	}

	@Override
	protected Boolean doNotExistValue() {
		return true;
	}

	private Map<String, List<Value>> loadTracker(final Element element) throws XmlException {
		if (!element.getNodeName().equals("settings")) {
			throw new XmlException("Wrong root element name.");
		}
		//Tracker Data
		NodeList trackerDataNodes = element.getElementsByTagName("trackerdata");
		if (trackerDataNodes.getLength() == 1) {
			Element trackerDataElement = (Element) trackerDataNodes.item(0);
			return parseTrackerData(trackerDataElement);
		}
		return null;
	}

	private List<Stockpile> loadStockpile(final Element element) throws XmlException {
		if (!element.getNodeName().equals("settings")) {
			throw new XmlException("Wrong root element name.");
		}
		//Stockpiles
		List<Stockpile> stockpiles = new ArrayList<>();
		NodeList stockpilesNodes = element.getElementsByTagName("stockpiles");
		if (stockpilesNodes.getLength() == 1) {
			Element stockpilesElement = (Element) stockpilesNodes.item(0);
			parseStockpiles(stockpilesElement, stockpiles, null);
		}
		return stockpiles;
	}

	private Map<String, RouteResult> loadRoutes(final Element element) throws XmlException {
		if (!element.getNodeName().equals("settings")) {
			throw new XmlException("Wrong root element name.");
		}
		//Routing
		Map<String, RouteResult> map = new HashMap<>();
		Element routingElement = getNodeOptional(element, "routingsettings");
		if (routingElement != null) {
			parseRoutes(routingElement, map);
		}
		return map;
	}

	private Settings loadSettings(final Element element, final Settings settings) throws XmlException {
		if (!element.getNodeName().equals("settings")) {
			throw new XmlException("Wrong root element name.");
		}

		//Manufacturing Prices
		Element manufacturingElement = getNodeOptional(element, "manufacturing");
		if (manufacturingElement != null) {
			parseManufacturingPriceSettings(manufacturingElement, settings);
		}

		//Price History
		Element priceHistoryElement = getNodeOptional(element, "pricehistory");
		if (priceHistoryElement != null) {
			parsePriceHistorySettings(priceHistoryElement, settings);
		}

		//Faction Warfare System Owners
		Element factionWarfareSystemOwnersElement = getNodeOptional(element, "factionwarfaresystemowners");
		if (factionWarfareSystemOwnersElement != null) {
			parseFactionWarfareSystemOwners(factionWarfareSystemOwnersElement, settings);
		}

		//Color Settings
		Element colorSettingsElement = getNodeOptional(element, "colorsettings");
		if (colorSettingsElement != null) {
			parseColorSettings(colorSettingsElement, settings);
		}

		//Sound Settings
		Element soundSettingsElement = getNodeOptional(element, "soundsettings");
		if (soundSettingsElement != null) {
			parseSoundSettings(soundSettingsElement, settings);
		}

		//Tracker Settings
		Element trackerSettingsElement = getNodeOptional(element, "trackersettings");
		if (trackerSettingsElement != null) {
			parseTrackerSettings(trackerSettingsElement, settings);
		}

		//Show Tools
		Element showToolsElement = getNodeOptional(element, "showtools");
		if (showToolsElement != null) {
			parseShowToolsNodes(showToolsElement, settings);
		}

		//Outbid
		Element marketOrderOutbidElement = getNodeOptional(element, "marketorderoutbid");
		if (marketOrderOutbidElement != null) {
			parseMarketOrderOutbidNodes(marketOrderOutbidElement, settings);
		}

		//Routing
		Element routingElement = getNodeOptional(element, "routingsettings");
		if (routingElement != null) {
			parseRoutingSettings(routingElement, settings);
		}

		//Tags - Must be loaded before stockpiles (and everything else that uses tags)
		Element tagsElement = getNodeOptional(element, "tags");
		if (tagsElement != null) {
			parseTags(tagsElement, settings);
		}

		//Owners
		Element ownersElement = getNodeOptional(element, "owners");
		if (ownersElement != null) {
			parseOwners(ownersElement, settings);
		}

		//Tracker Data
		Element trackerDataElement = getNodeOptional(element, "trackerdata");
		if (trackerDataElement != null) {
			Map<String, List<Value>> trackerData = parseTrackerData(trackerDataElement);
			TrackerData.set(trackerData);
		}

		//Tracker Data
		Element trackerNoteElement = getNodeOptional(element, "trackernotes");
		if (trackerNoteElement != null) {
			parseTrackerNotes(trackerNoteElement, settings);
		}

		//Tracker Filters
		Element trackerFilterElement = getNodeOptional(element, "trackerfilters");
		if (trackerFilterElement != null) {
			parseTrackerFilters(trackerFilterElement, settings);
		}

		//Asset Settings
		Element assetSettingsElement = getNodeOptional(element, "assetsettings");
		if (assetSettingsElement != null) {
			parseAssetSettings(assetSettingsElement, settings);
		}

		//Stockpiles
		Element stockpilesElement = getNodeOptional(element, "stockpiles");
		if (stockpilesElement != null) {
			parseStockpiles(stockpilesElement, settings.getStockpiles(), settings.getStockpileGroupSettings());
		}

		//Stockpile Groups
		Element stockpileGroupsElement = getNodeOptional(element, "stockpilegroups");
		if (stockpileGroupsElement != null) {
			parseStockpileGroups(stockpileGroupsElement, settings);
		}

		//Export Settings
		//Legacy support for 6.8.0 and later
		//TODO: Remove support at some future date
		Element exportElementLegacy = getNodeOptional(element, "csvexport");
		if (exportElementLegacy != null) {
			parseExportSettingsLegacy(exportElementLegacy, settings);
		}

		//Export Settings
		Element exportElement = getNodeOptional(element, "exports");
		if (exportElement != null) {
			parseExportSettings(exportElement, settings);
		}

		//Overview
		Element overviewElement = getNodeOptional(element, "overview");
		if (overviewElement != null) {
			parseOverview(overviewElement, settings);
		}

		//Window
		Element windowElement = getNodeOptional(element, "window");
		if (windowElement != null) {
			parseWindow(windowElement, settings);
		}

		//Reprocessing
		Element reprocessingElement = getNodeOptional(element, "reprocessing");
		if (reprocessingElement != null) {
			parseReprocessing(reprocessingElement, settings);
		}

		//UserPrices
		Element userPriceElement = getNode(element, "userprices");
		parseUserPrices(userPriceElement, settings);

		//User Item Names
		Element userItemNameElement = getNodeOptional(element, "itemmames");
		if (userItemNameElement != null) {
			parseUserItemNames(userItemNameElement, settings);
		}

		//Eve Item Names
		Element eveNameElement = getNodeOptional(element, "evenames");
		if (eveNameElement != null) {
			parseEveNames(eveNameElement, settings);
		}

		//PriceDataSettings
		Element priceDataSettingsElement = getNode(element, "marketstat");
		parsePriceDataSettings(priceDataSettingsElement, settings);

		//MarketOrdersSettings
		Element marketOrdersSettingsElement = getNodeOptional(element, "marketorderssettings");
		if (marketOrdersSettingsElement != null) {
			parseMarketOrdersSettings(marketOrdersSettingsElement, settings);
		}

		//Flags
		Element flagsElement = getNode(element, "flags");
		parseFlags(flagsElement, settings);

		//Table Changes
		Element tableChangesElement = getNodeOptional(element, "tablechanges");
		if (tableChangesElement != null) {
			parseTableChanges(tableChangesElement, settings);
		}

		//Table Formulas (Must be loaded before filters)
		Element tableFormulasElement = getNodeOptional(element, "tableformulas");
		if (tableFormulasElement != null) {
			parseTableFormulas(tableFormulasElement, settings);
		}

		//Table Jumps (Must be loaded before filters)
		Element tableJumpsElement = getNodeOptional(element, "tablejumps");
		if (tableJumpsElement != null) {
			parseTableJumps(tableJumpsElement, settings);
		}

		//Table Filters (Must be loaded before Asset Filters)
		Element tablefiltersElement = getNodeOptional(element, "tablefilters");
		if (tablefiltersElement != null) {
			parseTableFilters(tablefiltersElement, settings);
		}

		//Current Table Filters (Must be loaded before Asset Filters)
		Element currenttablefiltersElement = getNodeOptional(element, "currenttablefilters");
		if (currenttablefiltersElement != null) {
			parseCurrentTableFilters(currenttablefiltersElement, settings);
		}

		//Asset Filters
		Element filtersElement = getNodeOptional(element, "filters");
		if (filtersElement != null) {
			parseAssetFilters(filtersElement, settings);
		}

		//Table Columns
		Element tablecolumnsElement = getNodeOptional(element, "tablecolumns");
		if (tablecolumnsElement != null) {
			parseTableColumns(tablecolumnsElement, settings);
		}

		//Table Columns Width
		Element tableColumnsWidthElement = getNodeOptional(element, "tablecolumnswidth");
		if (tableColumnsWidthElement != null) {
			parseTableColumnsWidth(tableColumnsWidthElement, settings);
		}

		//Table Resize
		Element tableResizeElement = getNodeOptional(element, "tableresize");
		if (tableResizeElement != null) {
			parseTableResize(tableResizeElement, settings);
		}

		//Table Views
		Element tableViewsElement = getNodeOptional(element, "tableviews");
		if (tableViewsElement != null) {
			parseTableViews(tableViewsElement, settings);
		}

		//Asset added
		Element assetaddedElement = getNodeOptional(element, "assetadded");
		if (assetaddedElement != null) {
			parseAssetAdded(assetaddedElement);
		}

		// Proxy can have 0 or 1 proxy elements; at 0, the proxy stays as null.
		Element proxyElement = getNodeOptional(element, "proxy");
		if (proxyElement != null) {
			parseProxy(proxyElement, settings);
		}
		return settings;
	}

	private void parseOwners(final Element element, final Settings settings) throws XmlException {
		long ONE_DAY = 1000 * 60 * 60 * 24;
		NodeList ownerNodeList = element.getElementsByTagName("owner");
		int count = 1;
		for (int i = 0; i < ownerNodeList.getLength(); i++) {
			//Read Owner
			Element ownerNode = (Element) ownerNodeList.item(i);
			String ownerName = getString(ownerNode, "name");
			long ownerID = getLong(ownerNode, "id");
			Date date = getDateOptional(ownerNode, "date");
			if (date == null) { //1-30 days from now
				date = new Date(System.currentTimeMillis() + (ONE_DAY * count));
				count++;
				if (count > 30) {
					count = 1;
				}
			}
			settings.getOwners().put(ownerID, ownerName);
			settings.getOwnersNextUpdate().put(ownerID, date);
		}
	}

	private Map<String, List<Value>> parseTrackerData(final Element element) throws XmlException {
		Map<String, List<Value>> trackerData = new HashMap<>();
		NodeList tableNodeList = element.getElementsByTagName("owner");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			//Read Owner
			Element ownerNode = (Element) tableNodeList.item(a);
			String owner = getString(ownerNode, "name");
			//Ignore grand total, not used anymore
			if (owner.isEmpty()) {
				continue;
			}
			//Data
			NodeList dataNodeList = ownerNode.getElementsByTagName("data");
			for (int b = 0; b < dataNodeList.getLength(); b++) {
				//Read data
				Element dataNode = (Element) dataNodeList.item(b);
				Date date = getDate(dataNode, "date");
				double assetsTotal = getDouble(dataNode, "assets");
				double escrows = getDouble(dataNode, "escrows");
				double escrowstocover = getDouble(dataNode, "escrowstocover");
				double sellorders = getDouble(dataNode, "sellorders");
				double balanceTotal = getDouble(dataNode, "walletbalance");
				double manufacturing = getDoubleNotNull(dataNode, "manufacturing", 0.0);
				double contractCollateral = getDoubleNotNull(dataNode, "contractcollateral", 0.0);
				double contractValue = getDoubleNotNull(dataNode, "contractvalue", 0.0);
				//Add data
				Value value = new Value(date);
				//Balance
				NodeList balanceNodeList = dataNode.getElementsByTagName("balance");
				for (int c = 0; c < balanceNodeList.getLength(); c++) { //New data
					Element balanceNode = (Element) balanceNodeList.item(c);
					String id = getString(balanceNode, "id");
					double balance = getDouble(balanceNode, "value");
					value.addBalance(id, balance);
				}
				if (balanceNodeList.getLength() == 0) { //Old data
					value.setBalanceTotal(balanceTotal);
				}
				//Assets
				NodeList assetNodeList = dataNode.getElementsByTagName("asset");
				for (int c = 0; c < assetNodeList.getLength(); c++) { //New data
					Element assetNode = (Element) assetNodeList.item(c);
					AssetValue assetValue = parseAssetValue(assetNode);
					double assets = getDouble(assetNode, "value");
					value.addAssets(assetValue, assets);
				}
				if (assetNodeList.getLength() == 0) { //Old data
					value.setAssetsTotal(assetsTotal);
				}
				value.setEscrows(escrows);
				value.setEscrowsToCover(escrowstocover);
				value.setSellOrders(sellorders);
				value.setManufacturing(manufacturing);
				value.setContractCollateral(contractCollateral);
				value.setContractValue(contractValue);
				List<Value> list = trackerData.get(owner);
				if (list == null) {
					list = new ArrayList<>();
					trackerData.put(owner, list);
				}
				list.add(value);
			}
		}
		return trackerData;
	}

	private AssetValue parseAssetValue(Element node) throws XmlException {
		if (haveAttribute(node, "id")) {
			String id = getString(node, "id");
			return AssetValue.create(id);
		} else {
			String location = getString(node, "location");
			Long locationID = getLongOptional(node, "locationid");
			String flag = getStringOptional(node, "flag");
			return AssetValue.create(location, flag, locationID);
		}
	}

	private void parseTrackerNotes(final Element element, final Settings settings) throws XmlException {
		NodeList noteNodeList = element.getElementsByTagName("trackernote");
		for (int a = 0; a < noteNodeList.getLength(); a++) {
			//Read Owner
			Element noteNode = (Element) noteNodeList.item(a);
			String note = getString(noteNode, "note");
			Date date = getDate(noteNode, "date");
			settings.getTrackerSettings().getNotes().put(new TrackerDate(date), new TrackerNote(note));
		}
	}

	private void parseTrackerFilters(final Element element, final Settings settings) throws XmlException {
		NodeList tableNodeList = element.getElementsByTagName("trackerfilter");
		boolean selectNew = getBoolean(element, "selectnew");
		settings.getTrackerSettings().setSelectNew(selectNew);
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			Element trackerFilterNode = (Element) tableNodeList.item(a);
			String id = getString(trackerFilterNode, "id");
			boolean selected = getBoolean(trackerFilterNode, "selected");
			settings.getTrackerSettings().getFilters().put(id, selected);
		}
		NodeList skillPointFiltersList = element.getElementsByTagName("skillpointfilters");
		for (int a = 0; a < skillPointFiltersList.getLength(); a++) {
			Element filterNode = (Element) skillPointFiltersList.item(a);
			String id = getString(filterNode, "id");
			boolean selected = getBoolean(filterNode, "selected");
			long mimimum = getLong(filterNode, "mimimum");
			settings.getTrackerSettings().getSkillPointFilters().put(id, new TrackerSkillPointFilter(id, selected, mimimum));
		}
	}

	private void parseAssetSettings(final Element assetSettingsElement, final Settings settings) throws XmlException {
		int maximumPurchaseAge = getInt(assetSettingsElement, "maximumpurchaseage");
		TransactionProfitPrice transactionProfitPrice = TransactionProfitPrice.LASTEST;
		if (haveAttribute(assetSettingsElement, "transactionprofitprice")) {
			try {
				transactionProfitPrice = TransactionProfitPrice.valueOf(getString(assetSettingsElement, "transactionprofitprice"));
			} catch (IllegalArgumentException ex) {
				//No problem already set
			}
		}
		int transactionProfitMargin = getIntNotNull(assetSettingsElement, "transactionprofitmargin", 0);
		settings.setTransactionProfitPrice(transactionProfitPrice);
		settings.setMaximumPurchaseAge(maximumPurchaseAge);
		settings.setTransactionProfitMargin(transactionProfitMargin);
	}

	private void parseStockpileGroups(final Element stockpilesElement, final Settings settings) throws XmlException {
		int group2 = getInt(stockpilesElement, "stockpilegroup2");
		int group3 = getInt(stockpilesElement, "stockpilegroup3");
		if (group2 <= 0) {
			group2 = 100;
		}
		settings.setStockpileColorGroup2(group2);
		settings.setStockpileColorGroup3(group3);
	}

	/**
	 * -!- `!´ IMPORTANT `!´ -!-
	 * StockpileDataWriter and StockpileDataReader needs to be updated too - on any changes!!!
	 */
	private void parseStockpiles(final Element stockpilesElement, final List<Stockpile> stockpiles, StockpileGroupSettings stockpileGroupSettings) throws XmlException {
		NodeList stockpileNodes = stockpilesElement.getElementsByTagName("stockpile");
		Map<String, Stockpile> stockpileMap = new HashMap<>();
		Map<Stockpile, Map<String, Double>> subpileMap = new HashMap<>();
		for (int a = 0; a < stockpileNodes.getLength(); a++) {
			Element stockpileNode = (Element) stockpileNodes.item(a);
			String name = getString(stockpileNode, "name");
			Long stockpileID = getLongOptional(stockpileNode, "id"); //If null > get new id
		//LEGACY
			//Owners
			List<Long> ownerIDs = new ArrayList<>();
			if (haveAttribute(stockpileNode, "characterid")) {
				long ownerID = getLong(stockpileNode, "characterid");
				if (ownerID > 0) {
					ownerIDs.add(ownerID);
				}
			}
			//Containers
			List<StockpileContainer> containers = new ArrayList<>();
			if (haveAttribute(stockpileNode, "container")) {
				String container = getString(stockpileNode, "container");
				if (!container.equals(General.get().all())) {
					containers.add(new StockpileContainer(container, false));
				}
			}
			//Flags
			List<StockpileFlag> flags = new ArrayList<>();
			if (haveAttribute(stockpileNode, "flagid")) {
				int flagID = getInt(stockpileNode, "flagid");
				if (flagID > 0) {
					flags.add(new StockpileFlag(flagID, true));
				}
			}
			//Locations
			MyLocation location = null;
			if (haveAttribute(stockpileNode, "locationid")) {
				long locationID = getLong(stockpileNode, "locationid");
				location = ApiIdConverter.getLocation(locationID);
			}
			boolean exclude = false;
			//Include
			Boolean inventory = getBooleanOptional(stockpileNode, "inventory");
			Boolean sellOrders = getBooleanOptional(stockpileNode, "sellorders");
			Boolean buyOrders = getBooleanOptional(stockpileNode, "buyorders");
			Boolean jobs = getBooleanOptional(stockpileNode, "jobs");
			List<StockpileFilter> filters = new ArrayList<>();
			if (inventory != null && sellOrders != null && buyOrders != null && jobs != null) {
				StockpileFilter filter = new StockpileFilter(location, exclude, flags, containers, ownerIDs, null, null, null, inventory, sellOrders, buyOrders, jobs, false, false, false, false, false, false);
				filters.add(filter);
			}
		//NEW
			NodeList filterNodes = stockpileNode.getElementsByTagName("stockpilefilter");
			for (int b = 0; b < filterNodes.getLength(); b++) {
				Element filterNode = (Element) filterNodes.item(b);
				//Include
				boolean filterExclude = getBooleanNotNull(filterNode, "exclude", false);
				Boolean filterSingleton = getBooleanOptional(filterNode, "singleton");
				Integer filterJobsDaysLess = getIntOptional(filterNode, "jobsdaysless");
				Integer filterJobsDaysMore = getIntOptional(filterNode, "jobsdaysmore");
				boolean filterSellingContracts = getBooleanNotNull(filterNode, "sellingcontracts", false);
				boolean filterSoldBuy = getBooleanNotNull(filterNode, "soldcontracts", false);
				boolean filterBuyingContracts = getBooleanNotNull(filterNode, "buyingcontracts", false);
				boolean filterBoughtContracts = getBooleanNotNull(filterNode, "boughtcontracts", false);
				boolean filterInventory = getBoolean(filterNode, "inventory");
				boolean filterSellOrders = getBoolean(filterNode, "sellorders");
				boolean filterBuyOrders = getBoolean(filterNode, "buyorders");
				boolean filterBuyTransactions = getBooleanNotNull(filterNode, "buytransactions", false);
				boolean filterSellTransactions = getBooleanNotNull(filterNode, "selltransactions", false);
				boolean filterJobs = getBoolean(filterNode, "jobs");
				//Location
				long locationID = getLong(filterNode, "locationid");
				location = ApiIdConverter.getLocation(locationID);
				//Owners
				List<Long> filterOwnerIDs = new ArrayList<>();
				NodeList ownerNodes = filterNode.getElementsByTagName("owner");
				for (int c = 0; c < ownerNodes.getLength(); c++) {
					Element ownerNode = (Element) ownerNodes.item(c);
					long filterOwnerID = getLong(ownerNode, "ownerid");
					filterOwnerIDs.add(filterOwnerID);
				}
				//Containers
				List<StockpileContainer> filterContainers = new ArrayList<>();
				NodeList containerNodes = filterNode.getElementsByTagName("container");
				for (int c = 0; c < containerNodes.getLength(); c++) {
					Element containerNode = (Element) containerNodes.item(c);
					String filterContainer = getString(containerNode, "container");
					boolean filterIncludeSubs = getBooleanNotNull(containerNode, "includecontainer", false);
					filterContainers.add(new StockpileContainer(filterContainer, filterIncludeSubs));
				}
				//Flags
				List<StockpileFlag> filterFlags = new ArrayList<>();
				NodeList flagNodes = filterNode.getElementsByTagName("flag");
				for (int c = 0; c < flagNodes.getLength(); c++) {
					Element flagNode = (Element) flagNodes.item(c);
					int filterFlagID = getInt(flagNode, "flagid");
					boolean filterIncludeSubs = getBooleanNotNull(flagNode, "includecontainer", true);
					filterFlags.add(new StockpileFlag(filterFlagID, filterIncludeSubs));
				}
				StockpileFilter stockpileFilter = new StockpileFilter(location, filterExclude, filterFlags, filterContainers, filterOwnerIDs, filterJobsDaysLess, filterJobsDaysMore, filterSingleton, filterInventory, filterSellOrders, filterBuyOrders, filterJobs, filterBuyTransactions, filterSellTransactions, filterSellingContracts, filterSoldBuy, filterBuyingContracts, filterBoughtContracts);
				filters.add(stockpileFilter);
			}
		//SUBPILES
			NodeList subpileNodes = stockpileNode.getElementsByTagName("subpile");
			Map<String, Double> subpileNames = new HashMap<>();
			for (int b = 0; b < subpileNodes.getLength(); b++) {
				Element subpileNode = (Element) subpileNodes.item(b);
				String subpileName = getString(subpileNode, "name");
				double minimum = getDouble(subpileNode, "minimum");
				subpileNames.put(subpileName, minimum);
			}
		//MULTIPLIER
			double multiplier = getDoubleNotNull(stockpileNode, "multiplier", 1);
		//GROUP
			String group = getStringOptional(stockpileNode, "stockpilegroup"); //Null is handled by settings
		//CONTRACTS MATCH ALL
			boolean contractsMatchAll = getBooleanNotNull(stockpileNode, "contractsmatchall", false);

			Stockpile stockpile = new Stockpile(name, stockpileID, filters, multiplier, contractsMatchAll);
			if (stockpileGroupSettings != null) {
				stockpileGroupSettings.setGroup(stockpile, group);
			}
			stockpiles.add(stockpile);
			subpileMap.put(stockpile, subpileNames);
			stockpileMap.put(name, stockpile);
		//ITEMS
			NodeList itemNodes = stockpileNode.getElementsByTagName("item");
			for (int b = 0; b < itemNodes.getLength(); b++) {
				Element itemNode = (Element) itemNodes.item(b);
				long id;
				if (haveAttribute(itemNode, "id")) {
					id = getLong(itemNode, "id");
				} else {
					id = StockpileItem.getNewID();
				}
				int typeID = getInt(itemNode, "typeid");
				boolean runs = getBooleanNotNull(itemNode, "runs", false);
				double countMinimum = getDouble(itemNode, "minimum");
				if (typeID != 0) { //Ignore Total
					Item item = ApiIdConverter.getItemUpdate(Math.abs(typeID));
					StockpileItem stockpileItem = new StockpileItem(stockpile, item, typeID, countMinimum, runs, id);
					stockpile.add(stockpileItem);
				}
			}
		}
		for (Map.Entry<Stockpile, Map<String, Double>> entry : subpileMap.entrySet()) {
			for (Map.Entry<String, Double> entry1 : entry.getValue().entrySet()) {
				Stockpile stockpile = stockpileMap.get(entry1.getKey());
				if (stockpile != null) {
					entry.getKey().getSubpiles().put(stockpile, entry1.getValue());
					stockpile.addSubpileLink(entry.getKey());
				}
			}
		}
		subpileMap.clear();
		stockpileMap.clear();
		Collections.sort(stockpiles);
	}

	private void parseManufacturingPriceSettings(Element manufacturingElement, Settings settings) throws XmlException {
		ManufacturingSettings manufacturingSettings = settings.getManufacturingSettings();
		Date nextUpdate = getDate(manufacturingElement, "nextupdate");
		ManufacturingFacility facility;
		try {
			facility = ManufacturingFacility.valueOf(getString(manufacturingElement, "facility"));
		} catch (IllegalArgumentException ex) {
			facility = ManufacturingFacility.getDefault();
		}
		ManufacturingRigs rigs;
		try {
			rigs = ManufacturingRigs.valueOf(getString(manufacturingElement, "rigs"));
		} catch (IllegalArgumentException ex) {
			rigs = ManufacturingRigs.getDefault();
		}
		ManufacturingSecurity security;
		try {
			security = ManufacturingSecurity.valueOf(getString(manufacturingElement, "security"));
		} catch (IllegalArgumentException ex) {
			security = ManufacturingSecurity.getDefault();
		}
		int system = getInt(manufacturingElement, "systemid");
		int materialEfficiency = getInt(manufacturingElement, "me");
		double tax = getDouble(manufacturingElement, "tax");
		manufacturingSettings.setNextUpdate(nextUpdate);
		manufacturingSettings.setFacility(facility);
		manufacturingSettings.setRigs(rigs);
		manufacturingSettings.setSecurity(security);
		manufacturingSettings.setSystemID(system);
		manufacturingSettings.setMaterialEfficiency(materialEfficiency);
		manufacturingSettings.setTax(tax);
		//Manufacturing Adjusted Prices
		Map<Integer, Double> manufacturingPrices = new HashMap<>();
		NodeList priceNodes = manufacturingElement.getElementsByTagName("price");
		for (int a = 0; a < priceNodes.getLength(); a++) {
			Element priceNode = (Element) priceNodes.item(a);
			int typeID = getInt(priceNode, "typeid");
			double price = getDouble(priceNode, "price");
			manufacturingPrices.put(typeID, price);
		}
		manufacturingSettings.setPrices(manufacturingPrices);

		Map<Integer, Float> manufacturingSystems = new HashMap<>();
		NodeList systemNodes = manufacturingElement.getElementsByTagName("system");
		for (int a = 0; a < systemNodes.getLength(); a++) {
			Element systemNode = (Element) systemNodes.item(a);
			int systemID = getInt(systemNode, "systemid");
			float index = getFloat(systemNode, "index");
			manufacturingSystems.put(systemID, index);
		}

		manufacturingSettings.setSystems(manufacturingSystems);
	}

	private void parsePriceHistorySettings(Element priceHistoryElement, Settings settings) throws XmlException {
		NodeList priceListNodes = priceHistoryElement.getElementsByTagName("set");
		for (int a = 0; a < priceListNodes.getLength(); a++) {
			Element priceListNode = (Element) priceListNodes.item(a);
			String name = getString(priceListNode, "name");
			Set<Integer> typeIDs = new HashSet<>();
			addIntToList(priceListNode, "ids", typeIDs);
			settings.getPriceHistorySets().put(name, typeIDs);
		}
	}

	private void parseFactionWarfareSystemOwners(Element factionWarfareSystemOwnersElement, Settings settings) throws XmlException {
		Date factionWarfareNextUpdate = getDateNotNull(factionWarfareSystemOwnersElement, "factionwarfarenextupdate");
		settings.setFactionWarfareNextUpdate(factionWarfareNextUpdate);
		NodeList systemNodes = factionWarfareSystemOwnersElement.getElementsByTagName("system");
		settings.getFactionWarfareSystemOwners().clear();
		for (int a = 0; a < systemNodes.getLength(); a++) {
			Element systemNode = (Element) systemNodes.item(a);
			long systemID = getLong(systemNode, "system");
			String faction = getString(systemNode, "faction");
			settings.getFactionWarfareSystemOwners().put(systemID, faction);
		}
	}

	private void parseColorSettings(Element colorSettingsElement, Settings settings) throws XmlException {
		NodeList colorNodes = colorSettingsElement.getElementsByTagName("color");
		for (int a = 0; a < colorNodes.getLength(); a++) {
			Element colorNode = (Element) colorNodes.item(a);
			try {
				ColorEntry entry = ColorEntry.valueOf(getString(colorNode, "name"));
				Color background = getColorOptional(colorNode, "background");
				Color foreground = getColorOptional(colorNode, "foreground");
				settings.getColorSettings().setBackground(entry, background);
				settings.getColorSettings().setForeground(entry, foreground);
			} catch (IllegalArgumentException ex ) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		String theme = getStringOptional(colorSettingsElement, "theme");
		ColorThemeTypes colorThemeTypes;
		try {
			colorThemeTypes = ColorThemeTypes.valueOf(theme);
		} catch (IllegalArgumentException ex) {
			LOG.error(ex.getMessage(), ex);
			colorThemeTypes = ColorThemeTypes.DEFAULT;
		}
		settings.getColorSettings().setColorTheme(colorThemeTypes.getInstance(), false);
		String lookAndFeelClass = getStringOptional(colorSettingsElement, "lookandfeel");
		if (lookAndFeelClass != null) {
			settings.getColorSettings().setLookAndFeelClass(lookAndFeelClass);
		}
	}

	private void parseSoundSettings(Element colorSettingsElement, Settings settings) throws XmlException {
		NodeList soundNodes = colorSettingsElement.getElementsByTagName("sound");
		for (int a = 0; a < soundNodes.getLength(); a++) {
			Element soundNode = (Element) soundNodes.item(a);
			SoundOption option = SoundOption.valueOf(getString(soundNode, "option"));
			String sound = getString(soundNode, "sound");
			try {
				settings.getSoundSettings().put(option, DefaultSound.valueOf(sound));
			} catch (IllegalArgumentException ex ) {
				File file = new File(FileUtil.getPathSounds(sound));
				if (file.exists()) {
					settings.getSoundSettings().put(option, new FileSound(file));
				} else {
					settings.getSoundSettings().put(option, DefaultSound.BEEP); //Fallback
				}
			}
		}
	}

	private void parseTrackerSettings(Element trackerSettingsElement, Settings settings) throws XmlException {
		TrackerSettings trackerSettings = settings.getTrackerSettings();
		boolean allProfiles = getBoolean(trackerSettingsElement, "allprofiles");
		trackerSettings.setAllProfiles(allProfiles);

		boolean characterCorporations = getBoolean(trackerSettingsElement, "charactercorporations");
		trackerSettings.setCharacterCorporations(characterCorporations);

		List<String> selectedOwners = getStringListOptional(trackerSettingsElement, "selectedowners");
		trackerSettings.setSelectedOwners(selectedOwners);

		Date fromDate = getDateOptional(trackerSettingsElement, "fromdate");
		trackerSettings.setFromDate(fromDate);

		Date toDate = getDateOptional(trackerSettingsElement, "todate");
		trackerSettings.setToDate(toDate);

		String displayType = getStringOptional(trackerSettingsElement, "displaytype");
		if (displayType != null) {
			try {
				trackerSettings.setDisplayType(DisplayType.valueOf(displayType));
			}
			catch (IllegalArgumentException e) {
				LOG.warn("Could not parse trackersettigns displaytype: " + displayType);
			}
		}

		Boolean includeZero = getBooleanOptional(trackerSettingsElement, "includezero");
		if (includeZero != null) {
			trackerSettings.setIncludeZero(includeZero);
		}

		List<String> showOptions = getStringListOptional(trackerSettingsElement, "showoptions");
		if (showOptions != null) {
			trackerSettings.getShowOptions().clear();
			if (!showOptions.isEmpty()) {
				for (String showOption : showOptions) {
					try {
						trackerSettings.getShowOptions().add(ShowOption.valueOf(showOption));
					} catch (IllegalArgumentException e) {
						LOG.warn("Could not parse trackersettigns showoptions: " + showOption);
					}
				}
			}
		}
	}

	private void parseShowToolsNodes(Element showToolsElement, Settings settings) throws XmlException {
		boolean saveOnExit = getBoolean(showToolsElement, "saveonexit");
		List<String> showTools = getStringList(showToolsElement, "show");
		int index = showTools.indexOf("Industry Slots");
		if (index >= 0) {
			showTools.set(index, "Slots");
		}
		settings.setSaveToolsOnExit(saveOnExit);
		settings.getShowTools().addAll(showTools);
	}

	private void parseMarketOrderOutbidNodes(Element marketOrderOutbidElement, Settings settings) throws XmlException {
		Date nextUpdate = getDate(marketOrderOutbidElement, "nextupdate");
		settings.setPublicMarketOrdersNextUpdate(nextUpdate);
		Date lastUpdate = getDateOptional(marketOrderOutbidElement, "lastupdate");
		settings.setPublicMarketOrdersLastUpdate(lastUpdate);
		MarketOrderRange outbidOrderRange;
		try {
			outbidOrderRange = MarketOrderRange.valueOf(getString(marketOrderOutbidElement, "outbidorderrange"));
		} catch (IllegalArgumentException ex) {
			outbidOrderRange = MarketOrderRange.REGION;
		}
		settings.setOutbidOrderRange(outbidOrderRange);
		NodeList outbidNodes = marketOrderOutbidElement.getElementsByTagName("outbid");
		for (int a = 0; a < outbidNodes.getLength(); a++) {
			Element outbidNode = (Element) outbidNodes.item(a);
			long orderID = getLong(outbidNode, "id");
			double price = getDouble(outbidNode, "price");
			long count = getLong(outbidNode, "count");
			settings.getMarketOrdersOutbid().put(orderID, new Outbid(price, count));
		}
	}

	private void parseRoutingSettings(Element routingElement, Settings settings) throws XmlException {
		double secMax = getDouble(routingElement, "securitymaximum");
		double secMin = getDouble(routingElement, "securityminimum");
		settings.getRoutingSettings().setSecMax(secMax);
		settings.getRoutingSettings().setSecMin(secMin);
		NodeList systemNodes = routingElement.getElementsByTagName("routingsystem");
		for (int a = 0; a < systemNodes.getLength(); a++) {
			Element systemNode = (Element) systemNodes.item(a);
			long systemID = getLong(systemNode, "id");
			MyLocation location = ApiIdConverter.getLocation(systemID);
			settings.getRoutingSettings().getAvoid().put(systemID, new SolarSystem(location));
		}
		NodeList presetNodes = routingElement.getElementsByTagName("routingpreset");
		for (int a = 0; a < presetNodes.getLength(); a++) {
			Element presetNode = (Element) presetNodes.item(a);
			String name = getString(presetNode, "name");
			Set<Long> systemIDs = new HashSet<>();
			NodeList presetSystemNodes = presetNode.getElementsByTagName("presetsystem");
			for (int b = 0; b < presetSystemNodes.getLength(); b++) {
				Element systemNode = (Element) presetSystemNodes.item(b);
				long systemID = getLong(systemNode, "id");
				systemIDs.add(systemID);
			}
			settings.getRoutingSettings().getPresets().put(name, systemIDs);
		}
		parseRoutes(routingElement, settings.getRoutingSettings().getRoutes());
	}

	private void parseRoutes(Element routingElement, Map<String, RouteResult> map) throws XmlException {
		NodeList routeNodes = routingElement.getElementsByTagName("route");
		for (int a = 0; a < routeNodes.getLength(); a++) {
			Element routeNode = (Element) routeNodes.item(a);
			String name = getString(routeNode, "name");
			int waypoints = getInt(routeNode, "waypoints");
			String algorithmName = getString(routeNode, "algorithmname");
			long algorithmTime = getLong(routeNode, "algorithmtime");
			int jumps = getInt(routeNode, "jumps");
			String avoid = getStringOptional(routeNode, "avoid");
			String security = getStringOptional(routeNode, "security");
			NodeList routeSystemsNodes = routeNode.getElementsByTagName("routesystems");
			List<List<SolarSystem>> route = new ArrayList<>();
			Map<Long, List<SolarSystem>> stationsMap = new HashMap<>();
			for (int b = 0; b < routeSystemsNodes.getLength(); b++) {
				Element routeStartSystemNode = (Element) routeSystemsNodes.item(b);
				NodeList routeSystemNodes = routeStartSystemNode.getElementsByTagName("routesystem");
				List<SolarSystem> systems = new ArrayList<>();
				for (int c = 0; c < routeSystemNodes.getLength(); c++) {
					Element routeSystemNode = (Element) routeSystemNodes.item(c);
					long systemID = getLong(routeSystemNode, "systemid");
					SolarSystem system = new SolarSystem(ApiIdConverter.getLocation(systemID));
					systems.add(system);
				}
				route.add(systems);
				NodeList routeStationNodes = routeStartSystemNode.getElementsByTagName("routestation");
				for (int c = 0; c < routeStationNodes.getLength(); c++) {
					Element routeStationNode = (Element) routeStationNodes.item(c);
					long stationID = getLong(routeStationNode, "stationid");
					SolarSystem station = new SolarSystem(ApiIdConverter.getLocation(stationID));
					List<SolarSystem> stationsList = stationsMap.get(systems.get(0).getSystemID());
					if (stationsList == null) {
						stationsList = new ArrayList<>();
						stationsMap.put(systems.get(0).getSystemID(), stationsList);
					}
					stationsList.add(station);
				}
			}
			map.put(name, new RouteResult(route, stationsMap, waypoints, algorithmName, algorithmTime, jumps, avoid, security));
		}
	}

	private void parseTags(Element tagsElement, Settings settings) throws XmlException {
		NodeList tagNodes = tagsElement.getElementsByTagName("tag");
		for (int a = 0; a < tagNodes.getLength(); a++) {
			Element tagNode = (Element) tagNodes.item(a);
			String name = getString(tagNode, "name");
			String background = getString(tagNode, "background");
			String foreground = getString(tagNode, "foreground");

			TagColor color = new TagColor(background, foreground);
			Tag tag = new Tag(name, color);
			settings.getTags().put(tag.getName(), tag);

			NodeList idNodes = tagNode.getElementsByTagName("tagid");
			for (int b = 0; b < idNodes.getLength(); b++) {
				Element idNode = (Element) idNodes.item(b);
				String tool = getString(idNode, "tool");
				long id = getLong(idNode, "id");

				TagID tagID = new TagID(tool, id);
				tag.getIDs().add(tagID);
				settings.getTags(tagID).add(tag);
			}
		}
	}

	private void parseOverview(final Element overviewElement, final Settings settings) throws XmlException {
		NodeList groupNodes = overviewElement.getElementsByTagName("group");
		for (int a = 0; a < groupNodes.getLength(); a++) {
			Element groupNode = (Element) groupNodes.item(a);
			String name = getString(groupNode, "name");
			OverviewGroup overviewGroup = new OverviewGroup(name);
			settings.getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
			NodeList locationNodes = groupNode.getElementsByTagName("location");
			for (int b = 0; b < locationNodes.getLength(); b++) {
				Element locationNode = (Element) locationNodes.item(b);
				String location = getString(locationNode, "name");
				String type = getString(locationNode, "type");
				overviewGroup.add(new OverviewLocation(location, OverviewLocation.LocationType.valueOf(type)));
			}
		}
	}

	private void parseReprocessing(final Element windowElement, final Settings settings) throws XmlException {
		int reprocessing = getInt(windowElement, "refining");
		int efficiency = getInt(windowElement, "efficiency");
		Integer processing = getIntOptional(windowElement, "processing");
		Integer ore = getIntOptional(windowElement, "ore");
		Integer scrapmetal = getIntOptional(windowElement, "scrapmetal");
		if (scrapmetal == null) {
			if (processing != null) {
				scrapmetal = processing;
			} else {
				scrapmetal = 0;
			}
		}
		if (ore == null) {
			if (processing != null) {
				ore = processing;
			} else {
				ore = 0;
			}
		}
		double station = getDouble(windowElement, "station");
		settings.setReprocessSettings(new ReprocessSettings(station, reprocessing, efficiency, ore, scrapmetal));
	}

	private void parseWindow(final Element windowElement, final Settings settings) throws XmlException {
		int x = getInt(windowElement, "x");
		int y = getInt(windowElement, "y");
		int height = getInt(windowElement, "height");
		int width = getInt(windowElement, "width");
		boolean maximized = getBoolean(windowElement, "maximized");
		boolean autosave = getBoolean(windowElement, "autosave");
		boolean alwaysOnTop = getBooleanNotNull(windowElement, "alwaysontop", false);
		settings.setWindowLocation(new Point(x, y));
		settings.setWindowSize(new Dimension(width, height));
		settings.setWindowMaximized(maximized);
		settings.setWindowAutoSave(autosave);
		settings.setWindowAlwaysOnTop(alwaysOnTop);
	}

	private void parseProxy(final Element proxyElement, final Settings settings) throws XmlException {
		Proxy.Type type;
		try {
			type = Proxy.Type.valueOf(getString(proxyElement, "type"));
		} catch (IllegalArgumentException ex) {
			type = null;
		}
		String address = getString(proxyElement, "address");
		int port = getInt(proxyElement, "port");
		String username = getStringOptional(proxyElement, "username");
		String password = getStringOptional(proxyElement, "password");
		if (type != null && type != Proxy.Type.DIRECT && !address.isEmpty() && port != 0) { // check the proxy attributes are all there.
			settings.setProxyData(new ProxyData(address, type, port, username, password));
		}
	}

	private void parseUserPrices(final Element element, final Settings settings) throws XmlException {
		NodeList userPriceNodes = element.getElementsByTagName("userprice");
		for (int i = 0; i < userPriceNodes.getLength(); i++) {
			Element currentNode = (Element) userPriceNodes.item(i);
			String name = getString(currentNode, "name");
			double price = getDouble(currentNode, "price");
			int typeID = getInt(currentNode, "typeid");
			UserItem<Integer, Double> userPrice = new UserPrice(price, typeID, name);
			settings.getUserPrices().put(typeID, userPrice);
		}
	}

	private void parseUserItemNames(final Element element, final Settings settings) throws XmlException {
		NodeList userPriceNodes = element.getElementsByTagName("itemname");
		for (int i = 0; i < userPriceNodes.getLength(); i++) {
			Element currentNode = (Element) userPriceNodes.item(i);
			String name = getString(currentNode, "name");
			String typeName = getString(currentNode, "typename");
			long itemId = getLong(currentNode, "itemid");
			UserItem<Long, String> userItemName = new UserName(name, itemId, typeName);
			settings.getUserItemNames().put(itemId, userItemName);
		}
	}

	private void parseEveNames(final Element element, final Settings settings) throws XmlException {
		NodeList eveNameNodes = element.getElementsByTagName("evename");
		for (int i = 0; i < eveNameNodes.getLength(); i++) {
			Element currentNode = (Element) eveNameNodes.item(i);
			String name = getString(currentNode, "name");
			long itemId = getLong(currentNode, "itemid");
			settings.getEveNames().put(itemId, name);
		}
	}

	private void parsePriceDataSettings(final Element element, final Settings settings) throws XmlException {
		PriceMode priceType = settings.getPriceDataSettings().getPriceType(); //Default
		if (haveAttribute(element, "defaultprice")) {
			priceType = PriceMode.valueOf(getString(element, "defaultprice"));
		}

		PriceMode priceReprocessedType = settings.getPriceDataSettings().getPriceReprocessedType(); //Default
		if (haveAttribute(element, "defaultreprocessedprice")) {
			priceReprocessedType = PriceMode.valueOf(getString(element, "defaultreprocessedprice"));
		}
		PriceMode priceManufacturingType = settings.getPriceDataSettings().getPriceManufacturingType(); //Default
		if (haveAttribute(element, "defaultmanufacturingprice")) {
			priceManufacturingType = PriceMode.valueOf(getString(element, "defaultmanufacturingprice"));
		}

		//null = default
		Long locationID = null;
		LocationType locationType = null;
		//Backward compatibility
		if (haveAttribute(element, "regiontype")) {
			RegionTypeBackwardCompatibility regionType = RegionTypeBackwardCompatibility.valueOf(getString(element, "regiontype"));
			locationID = regionType.getRegion();
			locationType = LocationType.REGION;
		}
		//Backward compatibility
		if (haveAttribute(element, "locations")) {
			String string = getString(element, "locations");
			String[] split = string.split(",");
			if (split.length == 1) {
				try {
					locationID = Long.valueOf(split[0]);
				} catch (NumberFormatException ex) {
					LOG.warn("Could not parse locations long: " + split[0]);
				}
			}
		}
		if (haveAttribute(element, "locationid")) {
			locationID = getLong(element, "locationid");
		}
		if (haveAttribute(element, "type")) {
			locationType = LocationType.valueOf(getString(element, "type"));
		}
		PriceSource priceSource = PriceDataSettings.getDefaultPriceSource();
		if (haveAttribute(element, "pricesource")) {
			try {
				priceSource = PriceSource.valueOf(getString(element, "pricesource"));
			} catch (IllegalArgumentException ex) {
				//In case a price source is removed: Use the default
			}
		}
		String janiceKey = getStringOptional(element, "janicekey");
		//Validate
		if (!priceSource.isValid(locationType, locationID)) {
			locationType = priceSource.getDefaultLocationType();
			locationID = priceSource.getDefaultLocationID();
		}
		settings.setPriceDataSettings(new PriceDataSettings(locationType, locationID, priceSource, priceType, priceReprocessedType, priceManufacturingType, janiceKey));
	}

	private void parseMarketOrdersSettings(final Element element, final Settings settings) throws XmlException {
		int expireWarnDays = getIntNotNull(element, "expirewarndays", settings.getMarketOrdersSettings().getExpireWarnDays());
		int remainingWarnPercent = getIntNotNull(element, "remainingwarnpercent", settings.getMarketOrdersSettings().getRemainingWarnPercent());
		MarketOrdersSettings marketOrdersSettings = settings.getMarketOrdersSettings();
		marketOrdersSettings.setExpireWarnDays(expireWarnDays);
		marketOrdersSettings.setRemainingWarnPercent(remainingWarnPercent);
	}

	private void parseFlags(final Element element, final Settings settings) throws XmlException {
		NodeList flagNodes = element.getElementsByTagName("flag");
		for (int i = 0; i < flagNodes.getLength(); i++) {
			Element currentNode = (Element) flagNodes.item(i);
			String key = getString(currentNode, "key");
			boolean enabled = getBoolean(currentNode, "enabled");
			try {
				if (key.equals("FLAG_INCLUDE_CONTRACTS")) {
					settings.getFlags().put(SettingFlag.FLAG_INCLUDE_SELL_CONTRACTS, enabled);
					settings.getFlags().put(SettingFlag.FLAG_INCLUDE_BUY_CONTRACTS, enabled);
				}
				if (key.equals("FLAG_STRONG_COLORS")) {
					if (enabled) {
						settings.getColorSettings().setColorTheme(ColorThemeTypes.STRONG.getInstance(), true);
					} else {
						settings.getColorSettings().setColorTheme(ColorThemeTypes.DEFAULT.getInstance(), true);
					}
				}
				SettingFlag settingFlag = SettingFlag.valueOf(key);
				settings.getFlags().put(settingFlag, enabled);
			} catch (IllegalArgumentException ex) {
				LOG.warn("Removing Setting Flag:" + key);
			}
		}
		settings.cacheFlags();
	}

	private void parseTableColumns(final Element element, final Settings settings) throws XmlException {
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			List<SimpleColumn> columns = new ArrayList<>();
			Element tableNode = (Element) tableNodeList.item(a);
			String tableName = getString(tableNode, "name");
			//Ignore old tables
			if (tableName.equals("marketorderssell") || tableName.equals("marketordersbuy")) {
				continue;
			}
			NodeList columnNodeList = tableNode.getElementsByTagName("column");
			for (int b = 0; b < columnNodeList.getLength(); b++) {
				Element columnNode = (Element) columnNodeList.item(b);
				String name = getString(columnNode, "name");
				boolean shown = getBoolean(columnNode, "shown");
				columns.add(new SimpleColumn(name, shown));
			}
			settings.getTableColumns().put(tableName, columns);
		}
	}

	private void parseTableColumnsWidth(final Element element, final Settings settings) throws XmlException {
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			Map<String, Integer> columns = new HashMap<>();
			Element tableNode = (Element) tableNodeList.item(a);
			String tableName = getString(tableNode, "name");
			NodeList columnNodeList = tableNode.getElementsByTagName("column");
			for (int b = 0; b < columnNodeList.getLength(); b++) {
				Element columnNode = (Element) columnNodeList.item(b);
				int width = getInt(columnNode, "width");
				String column = getString(columnNode, "column");
				columns.put(column, width);
			}
			settings.getTableColumnsWidth().put(tableName, columns);
		}
	}

	private void parseTableResize(final Element element, final Settings settings) throws XmlException {
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int i = 0; i < tableNodeList.getLength(); i++) {
			Element tableNode = (Element) tableNodeList.item(i);
			String tableName = getString(tableNode, "name");
			ResizeMode resizeMode = ResizeMode.valueOf(getString(tableNode, "resize"));
			settings.getTableResize().put(tableName, resizeMode);
		}
	}

	private void parseTableViews(final Element element, final Settings settings) throws XmlException {
		NodeList viewToolNodeList = element.getElementsByTagName("viewtool");
		for (int a = 0; a < viewToolNodeList.getLength(); a++) {
			Element viewToolNode = (Element) viewToolNodeList.item(a);
			String toolName = getString(viewToolNode, "tool");
			Map<String, View> views = new TreeMap<>(new CaseInsensitiveComparator());
			settings.getTableViews().put(toolName, views);
			NodeList viewNodeList = viewToolNode.getElementsByTagName("view");
			for (int b = 0; b < viewNodeList.getLength(); b++) {
				Element viewNode = (Element) viewNodeList.item(b);
				String viewName = getString(viewNode, "name");
				View view = new View(viewName);
				views.put(view.getName(), view);
				NodeList viewColumnList = viewNode.getElementsByTagName("viewcolumn");
				for (int c = 0; c < viewColumnList.getLength(); c++) {
					Element viewColumnNode = (Element) viewColumnList.item(c);
					String name = getString(viewColumnNode, "name");
					boolean shown = getBoolean(viewColumnNode, "shown");
					view.getColumns().add(new SimpleColumn(name, shown));
				}
			}
		}
	}


	private void parseTableFormulas(final Element element, final Settings settings) throws XmlException {
		NodeList formulasNodeList = element.getElementsByTagName("formulas");
		for (int a = 0; a < formulasNodeList.getLength(); a++) {
			Element formulasNode = (Element) formulasNodeList.item(a);
			String toolName = getString(formulasNode, "tool");
			List<Formula> tableFormulas = settings.getTableFormulas(toolName);
			NodeList formulaNodeList = formulasNode.getElementsByTagName("formula");
			for (int b = 0; b < formulaNodeList.getLength(); b++) {
				Element formulaNode = (Element) formulaNodeList.item(b);
				String name = getString(formulaNode, "name");
				String expression = getString(formulaNode, "expression");
				Integer index = getIntOptional(formulaNode, "index");
				tableFormulas.add(new Formula(name, expression, index));
			}
		}
	}

	private void parseTableChanges(final Element element, final Settings settings) throws XmlException {
		NodeList changesList = element.getElementsByTagName("changes");
		for (int a = 0; a < changesList.getLength(); a++) {
			Element changesNode = (Element) changesList.item(a);
			String toolName = getString(changesNode, "tool");
			Date date = getDate(changesNode, "date");
			settings.getTableChanged().put(toolName, date);
		}
	}

	private void parseTableJumps(final Element element, final Settings settings) throws XmlException {
		NodeList jumpsNodeList = element.getElementsByTagName("jumps");
		for (int a = 0; a < jumpsNodeList.getLength(); a++) {
			Element jumpsNode = (Element) jumpsNodeList.item(a);
			String toolName = getString(jumpsNode, "tool");
			List<Jump> tableJumps = settings.getTableJumps(toolName);
			NodeList jumpNodeList = jumpsNode.getElementsByTagName("jump");
			for (int b = 0; b < jumpNodeList.getLength(); b++) {
				Element jumpNode = (Element) jumpNodeList.item(b);
				long systemID = getLong(jumpNode, "systemid");
				Integer index = getIntOptional(jumpNode, "index");
				MyLocation from = ApiIdConverter.getLocation(systemID);
				tableJumps.add(new Jump(from, index));
			}
		}
	}


	/***
	 * Parse the table filters elements of the settings file.
	 *
	 * @param element The 'tablefilters' element of the xml.
	 * @param settings The settings to be loaded to.
	 * @throws XmlException
	 */
	private void parseTableFilters(final Element element, final Settings settings) throws XmlException {
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			Element tableNode = (Element) tableNodeList.item(a);
			String tableName = getString(tableNode, "name");
			NodeList filterNodeList = tableNode.getElementsByTagName("filter");
			Map<String, List<Filter>> filters = new HashMap<>();
			for (int b = 0; b < filterNodeList.getLength(); b++) {
				Element filterNode = (Element) filterNodeList.item(b);
				String filterName = getString(filterNode, "name");
				List<Filter> filter = parseFilters(filterNode, tableName, settings);
				if (!filter.isEmpty()) {
					filters.put(filterName, filter);
				} else {
					LOG.warn(filterName + " filter removed (Empty)");
				}
			}
			settings.getTableFilters().put(tableName, filters);
		}
	}

	/***
	 * Parse the current table filters elements of the settings file.
	 *
	 * @param element The 'currenttablefilters' element of the xml.
	 * @param settings The settings to be loaded to.
	 * @throws XmlException
	 */
	private void parseCurrentTableFilters(final Element element, final Settings settings) throws XmlException {
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			Element tableNode = (Element) tableNodeList.item(a);
			String tableName = getString(tableNode, "name");
			NodeList filterNodeList = tableNode.getElementsByTagName("filter");
			List<Filter> filters = new ArrayList<>();

			if (filterNodeList.getLength() == 1) {
				Element filterNode = (Element) filterNodeList.item(0);

				if(haveAttribute(filterNode, "show")) {
					settings.getCurrentTableFiltersShown().put(tableName, getBoolean(filterNode, "show"));
				} else {
					settings.getCurrentTableFiltersShown().put(tableName, true);
				}

				filters = parseFilters(filterNode, tableName, settings);
			} else {
				LOG.warn(tableName + " current filter not found");
			}

			if (filters.isEmpty()) {
				LOG.warn(tableName + " current filter empty");
			}
			settings.getCurrentTableFilters().put(tableName, filters);
		}
	}

	/***
	 * Parse a filter element of the settings file. This can be used on both table and current table filters.
	 *
	 * @param filterNode The node of the filter element of the xml.
	 * @param tableName The name of the table the filter is for.
	 * @return A list of filters if the element had one. If not an empty list is returned.
	 * @throws XmlException
	 */
	private List<Filter> parseFilters(Element filterNode, String tableName, Settings settings) throws XmlException {
		List<Filter> filter = new ArrayList<>();
		NodeList rowNodes = filterNode.getElementsByTagName("row");
		for (int c = 0; c < rowNodes.getLength(); c++) {
			Element rowNode = (Element) rowNodes.item(c);
			int group = getIntNotNull(rowNode, "group", 1);
			boolean enabled = getBooleanNotNull(rowNode, "enabled", true);
			String text = getString(rowNode, "text");
			String columnString = getString(rowNode, "column");
			EnumTableColumn<?> column = getColumn(columnString, tableName, settings);
			if (column != null) {
				String compare = getString(rowNode, "compare");
				String logic = getString(rowNode, "logic");
				filter.add(new Filter(group, logic, column, compare, text, enabled));
			} else {
				LOG.warn(columnString + " column removed from filter");
			}
		}
		return filter;
	}

	public static EnumTableColumn<?> getColumn(final String column, final String toolName, Settings settings) {
		//Stockpile
		try {
			if (toolName.equals(StockpileTab.NAME)) {
				return StockpileExtendedTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Stockpile (Extra)
		try {
			if (toolName.equals(StockpileTab.NAME)) {
				return StockpileTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Industry Jobs
		try {
			if (toolName.equals(IndustryJobsTab.NAME)) {
				return IndustryJobTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Slots
		try {
			if (toolName.equals(SlotsTab.NAME)) {
				return SlotsTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Market Orders
		try {
			if (toolName.equals(MarketOrdersTab.NAME)) {
				return MarketTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Journal
		try {
			if (toolName.equals(JournalTab.NAME)) {
				return JournalTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Transaction
		try {
			if (toolName.equals(TransactionTab.NAME)) {
				return TransactionTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Assets
		try {
			if (toolName.equals(AssetsTab.NAME)) {
				return AssetTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Items
		try {
			if (toolName.equals(ItemsTab.NAME)) {
				return ItemTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Overview
		try {
			if (toolName.equals(OverviewTab.NAME)) {
				return OverviewTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Contracts
		try {
			if (toolName.equals(ContractsTab.NAME)) {
				return ContractsTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Contracts (Extra)
		try {
			if (toolName.equals(ContractsTab.NAME)) {
				return ContractsExtendedTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Isk
		try {
			if (toolName.equals(ValueTableTab.NAME)) {
				return ValueTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Tree
		try {
			if (toolName.equals(TreeTab.NAME)) {
				return TreeTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Skills
		try {
			if (toolName.equals(SkillsTab.NAME)) {
				return SkillsTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Mining
		try {
			if (toolName.equals(MiningTab.NAME)) {
				return MiningTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Extractions
		try {
			if (toolName.equals(ExtractionsTab.NAME)) {
				return ExtractionsTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		if (settings != null) {
			for (Formula formula : settings.getTableFormulas(toolName)) {
				if (formula.getColumnName().equals(column)) {
					return new FormulaColumn<>(formula);
				}
			}
			for (Jump jump : settings.getTableJumps(toolName)) {
				if (jump.getName().equals(column)) {
					return new JumpColumn<>(jump);
				}
			}
		}
		//All
		if (column.equals("ALL") || column.equals("all")) {
			return AllColumn.ALL;
		}
		return null;
	}

	private void parseAssetFilters(final Element filtersElement, final Settings settings) throws XmlException {
		NodeList filterNodeList = filtersElement.getElementsByTagName("filter");
		for (int a = 0; a < filterNodeList.getLength(); a++) {
			Element filterNode = (Element) filterNodeList.item(a);
			String filterName = getString(filterNode, "name");

			List<Filter> filters = new ArrayList<>();

			NodeList rowNodeList = filterNode.getElementsByTagName("row");
			for (int b = 0; b < rowNodeList.getLength(); b++) {
				Element rowNode = (Element) rowNodeList.item(b);
				LogicType logic = convertLogic(getBoolean(rowNode, "and"));
				EnumTableColumn<?> column = convertColumn(getString(rowNode, "column"));
				CompareType compare = convertMode(getString(rowNode, "mode"));
				String text;
				if (haveAttribute(rowNode, "columnmatch")) {
					text = convertColumn(getString(rowNode, "columnmatch")).name();
				} else {
					text = getString(rowNode, "text");
				}
				Filter filter = new Filter(logic, column, compare, text);
				filters.add(filter);
			}
			settings.getTableFilters(AssetsTab.NAME).put(filterName, filters);
		}
	}

	private LogicType convertLogic(final boolean logic) {
		if (logic) {
			return LogicType.AND;
		} else {
			return LogicType.OR;
		}
	}

	private EnumTableColumn<?> convertColumn(final String column) {
		if (column.equals("Name")) { return AssetTableFormat.NAME; }
		if (column.equals("Group")) { return AssetTableFormat.GROUP; }
		if (column.equals("Category")) { return AssetTableFormat.CATEGORY; }
		if (column.equals("Owner")) { return AssetTableFormat.OWNER; }
		if (column.equals("Count")) { return AssetTableFormat.COUNT; }
		if (column.equals("Location")) { return AssetTableFormat.LOCATION; }
		if (column.equals("Container")) { return AssetTableFormat.CONTAINER; }
		if (column.equals("Flag")) { return AssetTableFormat.FLAG; }
		if (column.equals("Price")) { return AssetTableFormat.PRICE; }
		if (column.equals("Sell Min")) { return AssetTableFormat.PRICE_SELL_MIN; }
		if (column.equals("Buy Max")) { return AssetTableFormat.PRICE_BUY_MAX; }
		if (column.equals("Base Price")) { return AssetTableFormat.PRICE_BASE; }
		if (column.equals("Value")) { return AssetTableFormat.VALUE; }
		if (column.equals("Meta")) { return AssetTableFormat.META; }
		if (column.equals("ID")) { return AssetTableFormat.ITEM_ID; }
		if (column.equals("Volume")) { return AssetTableFormat.VOLUME; }
		if (column.equals("Type ID")) { return AssetTableFormat.TYPE_ID; }
		if (column.equals("Region")) { return AssetTableFormat.REGION; }
		if (column.equals("Type Count")) { return AssetTableFormat.COUNT_TYPE; }
		if (column.equals("Security")) { return AssetTableFormat.SECURITY; }
		if (column.equals("Reprocessed")) { return AssetTableFormat.PRICE_REPROCESSED; }
		if (column.equals("Reprocessed Value")) { return AssetTableFormat.VALUE_REPROCESSED; }
		if (column.equals("Singleton")) { return AssetTableFormat.SINGLETON; }
		if (column.equals("Total Volume")) { return AssetTableFormat.VOLUME_TOTAL; }
		return AllColumn.ALL; //Fallback
	}

	private CompareType convertMode(final String compareMixed) {
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

	/***
	 * Old method to process export settings for 6.8.0 and older
	 */
	@Deprecated
	private void parseExportSettingsLegacy(final Element element, final Settings settings) throws XmlException {
		//Copy
		String copy = getStringOptional(element, "copy");
		if (copy != null) {
			settings.getCopySettings().setCopyDecimalSeparator(DecimalSeparator.valueOf(copy));
		}

		ExportFormat exportFormat = null;
		if (haveAttribute(element, "exportformat")) {
			exportFormat = ExportFormat.valueOf(getString(element, "exportformat"));
		}

		//CSV
		DecimalSeparator decimal = DecimalSeparator.valueOf(getString(element, "decimal"));
		LineDelimiter line = LineDelimiter.valueOf(getString(element, "line"));

		//SQL
		Boolean createTable = getBooleanOptional(element, "sqlcreatetable");
		Boolean dropTable = getBooleanOptional(element, "sqldroptable");
		Boolean extendedInserts = getBooleanOptional(element, "sqlextendedinserts");

		//HTML
		Boolean htmlStyled = getBooleanOptional(element, "htmlstyled");
		Boolean htmlIGB = getBooleanOptional(element, "htmligb");
		Integer htmlRepeatHeader = getIntOptional(element, "htmlrepeatheader");

		Map<String, String> tableNames = new HashMap<>();
		Map<String, String> fileNames = new HashMap<>();
		Map<String, List<String>> columnNames = new HashMap<>();

		NodeList tableNamesNodeList = element.getElementsByTagName("sqltablenames");
		for (int a = 0; a < tableNamesNodeList.getLength(); a++) {
			Element tableNameNode = (Element) tableNamesNodeList.item(a);
			String tool = getString(tableNameNode, "tool");
			String tableName = getString(tableNameNode, "tablename");
			tableNames.put(tool, tableName);
		}
		//Shared
		NodeList fileNamesNodeList = element.getElementsByTagName("filenames");
		for (int a = 0; a < fileNamesNodeList.getLength(); a++) {
			Element tableNameNode = (Element) fileNamesNodeList.item(a);
			String tool = getString(tableNameNode, "tool");
			String fileName = getString(tableNameNode, "filename");
			fileNames.put(tool, fileName);
		}
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			List<String> columns = new ArrayList<>();
			Element tableNode = (Element) tableNodeList.item(a);
			String tableName = getString(tableNode, "name");
			NodeList columnNodeList = tableNode.getElementsByTagName("column");
			for (int b = 0; b < columnNodeList.getLength(); b++) {
				Element columnNode = (Element) columnNodeList.item(b);
				String name = getString(columnNode, "name");
				columns.add(name);
			}
			columnNames.put(tableName, columns);
		}

		//List of existing tools at the time when the data format was changed 6.8.0
		List<String> toolNames = Arrays.asList("industryjobs", "overview", "marketorders", "loadouts", "stockpile",
				"reprocessed", "contracts", "industryslots", "journal", "assets", "materials", "treeassets", "value",
				"items", "transaction");
		for (String toolName : toolNames) {
			ExportSettings exportSettings = new ExportSettings(toolName);
			//Common
			if (exportFormat != null) {
				exportSettings.setExportFormat(exportFormat);
			}
			//CSV
			exportSettings.setDecimalSeparator(decimal);
			exportSettings.setCsvLineDelimiter(line);
			//SQL
			if (createTable != null) {
				exportSettings.setSqlCreateTable(createTable);
			}
			if (dropTable != null) {
				exportSettings.setSqlDropTable(dropTable);
			}
			if (extendedInserts != null) {
				exportSettings.setSqlExtendedInserts(extendedInserts);
			}
			//HTML
			if (htmlStyled != null) {
				exportSettings.setHtmlStyled(htmlStyled);
			}
			if (htmlIGB != null) {
				exportSettings.setHtmlIGB(htmlIGB);
			}
			if (htmlRepeatHeader != null) {
				exportSettings.setHtmlRepeatHeader(htmlRepeatHeader);
			}
			//Lists
			if (tableNames.containsKey(toolName)) {
				exportSettings.setSqlTableName(tableNames.get(toolName));
			}
			if (fileNames.containsKey(toolName)) {
				exportSettings.setFilename(fileNames.get(toolName));
			}
			if (columnNames.containsKey(toolName)) {
				exportSettings.getTableExportColumns().addAll(columnNames.get(toolName));
			}
			settings.getExportSettings().put(toolName, exportSettings);
		}
	}

	/***
	 *
	 * @param element
	 * @param settings
	 * @throws XmlException
	 */
	private void parseExportSettings(final Element element, final Settings settings) throws XmlException {
		//Copy
		String copy = getStringOptional(element, "copy");
		if (copy != null) {
			settings.getCopySettings().setCopyDecimalSeparator(DecimalSeparator.valueOf(copy));
		}

		NodeList tableNodeList = element.getElementsByTagName("export");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			Element exportNode = (Element) tableNodeList.item(a);
			String toolName = getString(exportNode, "name");
			ExportSettings exportSettings = parseExportSetting(exportNode, toolName);
			settings.getExportSettings().put(toolName, exportSettings);
		}
	}

	/***
	 *
	 * @param exportNode
	 * @param toolName
	 * @return
	 * @throws XmlException
	 */
	private ExportSettings parseExportSetting(final Element exportNode, final String toolName) throws XmlException {
		ExportSettings exportSetting = new ExportSettings(toolName);

		//Common
		String exportFormat = getStringOptional(exportNode, "exportformat");
		if (exportFormat != null) {
			exportSetting.setExportFormat(ExportFormat.valueOf(exportFormat));
		}

		String fileName = getString(exportNode, "filename");
		if (fileName != null) {
			exportSetting.setFilename(fileName);
		}

		String columnSelection = getStringOptional(exportNode, "columnselection");
		if (columnSelection != null) {
			exportSetting.setColumnSelection(ColumnSelection.valueOf(columnSelection));
		}

		String viewName = getStringOptional(exportNode, "viewname");
		if (viewName != null) {
			exportSetting.setViewName(viewName);
		}

		String filterSelection = getStringOptional(exportNode, "filterselection");
		if (filterSelection != null) {
			exportSetting.setFilterSelection(FilterSelection.valueOf(filterSelection));
		}

		String filterName = getStringOptional(exportNode, "filtername");
		if (filterName != null) {
			exportSetting.setFilterName(filterName);
		}

		Element tableNode = getNodeOptional(exportNode, "table");
		if (tableNode != null) {
			List<String> columns = new ArrayList<>();
			NodeList columnNodeList = tableNode.getElementsByTagName("column");
			for (int b = 0; b < columnNodeList.getLength(); b++) {
				Element columnNode = (Element) columnNodeList.item(b);
				String name = getString(columnNode, "name");
				columns.add(name);
			}
			exportSetting.putTableExportColumns(columns);
		}

		//CSV
		Element csvElement = getNodeOptional(exportNode, "csv");
		if (csvElement != null) {
			DecimalSeparator decimal = DecimalSeparator.valueOf(getString(csvElement, "decimal"));
			exportSetting.setDecimalSeparator(decimal);

			LineDelimiter line = LineDelimiter.valueOf(getString(csvElement, "line"));
			exportSetting.setCsvLineDelimiter(line);
		}

		//SQL
		Element sqlElement = getNodeOptional(exportNode, "sql");
		if (sqlElement != null) {
			String tableName = getString(sqlElement, "tablename");
			exportSetting.setSqlTableName(tableName);

			boolean createTable = getBoolean(sqlElement, "createtable");
			exportSetting.setSqlCreateTable(createTable);

			boolean dropTable = getBoolean(sqlElement, "droptable");
			exportSetting.setSqlDropTable(dropTable);

			boolean extendedInserts = getBoolean(sqlElement, "extendedinserts");
			exportSetting.setSqlExtendedInserts(extendedInserts);
		}

		//html
		Element htmlElement = getNodeOptional(exportNode, "html");
		if (htmlElement != null) {
			boolean htmlStyled = getBoolean(htmlElement, "styled");
			exportSetting.setHtmlStyled(htmlStyled);

			boolean htmlIGB = getBoolean(htmlElement, "igb");
			exportSetting.setHtmlIGB(htmlIGB);

			int htmlRepeatHeader = getInt(htmlElement, "repeatheader");
			exportSetting.setHtmlRepeatHeader(htmlRepeatHeader);
		}

		return exportSetting;
	}

	private void parseAssetAdded(final Element element) throws XmlException {
		NodeList assetNodes = element.getElementsByTagName("asset");
		Map<Long, Date> assetAdded = new HashMap<>();
		for (int i = 0; i < assetNodes.getLength(); i++) {
			Element currentNode = (Element) assetNodes.item(i);
			long itemID = getLong(currentNode, "itemid");
			Date date = getDate(currentNode, "date");
			assetAdded.put(itemID, date);
		}
		AddedData.getAssets().set(assetAdded); //Import from settings.xml
	}

	public enum RegionTypeBackwardCompatibility {
		NOT_CONFIGURABLE(null),
		EMPIRE(null),
		MARKET_HUBS(null),
		ALL_AMARR(null),
		ALL_GALLENTE(null),
		ALL_MINMATAR(null),
		ALL_CALDARI(null),
		ARIDIA(10000054L),
		DEVOID(10000036L),
		DOMAIN(10000043L),
		GENESIS(10000067L),
		KADOR(10000052L),
		KOR_AZOR(10000065L),
		TASH_MURKON(10000020L),
		THE_BLEAK_LANDS(10000038L),
		BLACK_RISE(10000069L),
		LONETREK(10000016L),
		THE_CITADEL(10000033L),
		THE_FORGE(10000002L),
		ESSENCE(10000064L),
		EVERYSHORE(10000037L),
		PLACID(10000048L),
		SINQ_LAISON(10000032L),
		SOLITUDE(10000044L),
		VERGE_VENDOR(10000068L),
		METROPOLIS(10000042L),
		HEIMATAR(10000030L),
		MOLDEN_HEATH(10000028L),
		DERELIK(10000001L),
		KHANID(10000049L)
		;

		private final Long region;

		private RegionTypeBackwardCompatibility(Long region) {
			this.region = region;
		}

		public Long getRegion() {
			return region;
		}

	}
}
