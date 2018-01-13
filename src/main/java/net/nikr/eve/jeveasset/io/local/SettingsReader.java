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

package net.nikr.eve.jeveasset.io.local;

import java.awt.Dimension;
import java.awt.Point;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.DecimalSeparator;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.ExportFormat;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.FieldDelimiter;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.LineDelimiter;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceMode;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.RegionType;
import net.nikr.eve.jeveasset.data.settings.ProxyData;
import net.nikr.eve.jeveasset.data.settings.ReprocessSettings;
import net.nikr.eve.jeveasset.data.settings.RouteResult;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.Settings.SettingFlag;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.data.settings.tag.Tag;
import net.nikr.eve.jeveasset.data.settings.tag.TagColor;
import net.nikr.eve.jeveasset.data.settings.tag.TagID;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserNameSettingsPanel.UserName;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel.UserPrice;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
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
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerDate;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerNote;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.gui.tabs.values.Value.AssetValue;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.local.update.Update;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.me.candle.eve.pricing.options.LocationType;


public final class SettingsReader extends AbstractXmlReader<Boolean> {

	public static final int SETTINGS_VERSION = 2;

	private static final Logger LOG = LoggerFactory.getLogger(SettingsReader.class);

	private Settings settings;
	private List<Stockpile> stockpilesList;

	private SettingsReader() { }

	public static boolean load(final Settings settings) {
		SettingsReader reader = new SettingsReader();
		reader.settings = settings;
		Update updater = new Update();
		try {
			updater.performUpdates(SETTINGS_VERSION, settings.getPathSettings());
		} catch (XmlException ex) {
			return false; //Failed update!
		}
		return reader.read("Settings", settings.getPathSettings(), XmlType.DYNAMIC_BACKUP);
	}

	public static List<Stockpile> loadStockpile(final String filename) {
		SettingsReader reader = new SettingsReader();
		if (reader.read(filename, filename, XmlType.IMPORT)) {
			return reader.stockpilesList;
		} else {
			return null;
		}
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		if (settings == null) {
			stockpilesList = parseStockpile(element);
		} else {
			parseSettings(element, settings);
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

	private List<Stockpile> parseStockpile(final Element element) throws XmlException {
		if (!element.getNodeName().equals("settings")) {
			throw new XmlException("Wrong root element name.");
		}
		//Stockpiles
		List<Stockpile> stockpiles = new ArrayList<Stockpile>();
		NodeList stockpilesNodes = element.getElementsByTagName("stockpiles");
		if (stockpilesNodes.getLength() == 1) {
			Element stockpilesElement = (Element) stockpilesNodes.item(0);
			parseStockpiles(stockpilesElement, stockpiles);
			Collections.sort(Settings.get().getStockpiles());
		}
		return stockpiles;
	}

	private void parseSettings(final Element element, final Settings settings) throws XmlException {
		if (!element.getNodeName().equals("settings")) {
			throw new XmlException("Wrong root element name.");
		}

		//EveKit
		NodeList evekitNodes = element.getElementsByTagName("evekit");
		if (evekitNodes.getLength() == 1) {
			Element evekitElement = (Element) evekitNodes.item(0);
			parseEveKitSettings(evekitElement, settings);
		}

		//Routing
		NodeList routingNodes = element.getElementsByTagName("routingsettings");
		if (routingNodes.getLength() == 1) {
			Element routingElement = (Element) routingNodes.item(0);
			parseRoutingSettings(routingElement, settings);
		}

		//Tags - Must be loaded before stockpiles (and everything else that uses tags)
		NodeList tagsNodes = element.getElementsByTagName("tags");
		if (tagsNodes.getLength() == 1) {
			Element tagsElement = (Element) tagsNodes.item(0);
			parseTags(tagsElement, settings);
		}

		//Owners
		NodeList ownersNodes = element.getElementsByTagName("owners");
		if (ownersNodes.getLength() == 1) {
			Element ownersElement = (Element) ownersNodes.item(0);
			parseOwners(ownersElement, settings);
		}

		//Tracker Data
		NodeList trackerDataNodes = element.getElementsByTagName("trackerdata");
		if (trackerDataNodes.getLength() == 1) {
			Element trackerDataElement = (Element) trackerDataNodes.item(0);
			parseTrackerData(trackerDataElement, settings);
		}

		//Tracker Data
		NodeList trackerNotesNodes = element.getElementsByTagName("trackernotes");
		if (trackerNotesNodes.getLength() == 1) {
			Element trackerNoteElement = (Element) trackerNotesNodes.item(0);
			parseTrackerNotes(trackerNoteElement, settings);
		}

		//Tracker Filters
		NodeList trackerFiltersNodes = element.getElementsByTagName("trackerfilters");
		if (trackerFiltersNodes.getLength() == 1) {
			Element trackerFilterElement = (Element) trackerFiltersNodes.item(0);
			parseTrackerFilters(trackerFilterElement, settings);
		}

		//Asset Settings
		NodeList assetSettingsNodes = element.getElementsByTagName("assetsettings");
		if (assetSettingsNodes.getLength() == 1) {
			Element assetSettingsElement = (Element) assetSettingsNodes.item(0);
			parseAssetSettings(assetSettingsElement, settings);
		}

		//Stockpiles
		NodeList stockpilesNodes = element.getElementsByTagName("stockpiles");
		if (stockpilesNodes.getLength() == 1) {
			Element stockpilesElement = (Element) stockpilesNodes.item(0);
			parseStockpiles(stockpilesElement, settings.getStockpiles());
			Collections.sort(Settings.get().getStockpiles());
		}

		//Stockpile Groups
		NodeList stockpileGroupsNodes = element.getElementsByTagName("stockpilegroups");
		if (stockpileGroupsNodes.getLength() == 1) {
			Element stockpileGroupsElement = (Element) stockpileGroupsNodes.item(0);
			parseStockpileGroups(stockpileGroupsElement, settings);
		}

		//Export Settings
		NodeList exportNodes = element.getElementsByTagName("csvexport");
		if (exportNodes.getLength() == 1) {
			Element exportElement = (Element) exportNodes.item(0);
			parseExportSettings(exportElement, settings);
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

		//Eve Item Names
		NodeList eveNameNodes = element.getElementsByTagName("evenames");
		if (eveNameNodes.getLength() == 1) {
			Element eveNameElement = (Element) eveNameNodes.item(0);
			parseEveNames(eveNameElement, settings);
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

		//Table Columns Width
		NodeList tableColumnsWidthNodes = element.getElementsByTagName("tablecolumnswidth");
		if (tableColumnsWidthNodes.getLength() == 1) {
			Element tableColumnsWidthElement = (Element) tableColumnsWidthNodes.item(0);
			parseTableColumnsWidth(tableColumnsWidthElement, settings);
		}

		//Table Resize
		NodeList tableResizeNodes = element.getElementsByTagName("tableresize");
		if (tableResizeNodes.getLength() == 1) {
			Element tableResizeElement = (Element) tableResizeNodes.item(0);
			parseTableResize(tableResizeElement, settings);
		}

		//Table Views
		NodeList tableViewsNodes = element.getElementsByTagName("tableviews");
		if (tableViewsNodes.getLength() == 1) {
			Element tableViewsElement = (Element) tableViewsNodes.item(0);
			parseTableViews(tableViewsElement, settings);
		}

		//Asset added
		NodeList assetaddedNodes = element.getElementsByTagName("assetadded");
		if (assetaddedNodes.getLength() == 1) {
			Element assetaddedElement = (Element) assetaddedNodes.item(0);
			parseAssetAdded(assetaddedElement, settings);
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

	private void parseOwners(final Element element, final Settings settings) throws XmlException {
		NodeList ownerNodeList = element.getElementsByTagName("owner");
		for (int i = 0; i < ownerNodeList.getLength(); i++) {
			//Read Owner
			Element ownerNode = (Element) ownerNodeList.item(i);
			String ownerName = AttributeGetters.getString(ownerNode, "name");
			long ownerID = AttributeGetters.getLong(ownerNode, "id");
			settings.getOwners().put(ownerID, ownerName);
		}
	}
	private void parseTrackerData(final Element element, final Settings settings) throws XmlException {
		NodeList tableNodeList = element.getElementsByTagName("owner");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			//Read Owner
			Element ownerNode = (Element) tableNodeList.item(a);
			String owner = AttributeGetters.getString(ownerNode, "name");
			//Ignore grand total, not used anymore
			if (owner.isEmpty()) {
				continue;
			}
			//Add new Owner
			settings.getTrackerData().put(owner, new ArrayList<Value>());
			//Data
			NodeList dataNodeList = ownerNode.getElementsByTagName("data");
			for (int b = 0; b < dataNodeList.getLength(); b++) {
				//Read data
				Element dataNode = (Element) dataNodeList.item(b);
				Date date = AttributeGetters.getDate(dataNode, "date");
				double assetsTotal = AttributeGetters.getDouble(dataNode, "assets");
				double escrows = AttributeGetters.getDouble(dataNode, "escrows");
				double escrowstocover = AttributeGetters.getDouble(dataNode, "escrowstocover");
				double sellorders = AttributeGetters.getDouble(dataNode, "sellorders");
				double balanceTotal = AttributeGetters.getDouble(dataNode, "walletbalance");
				double manufacturing = 0.0;
				if (AttributeGetters.haveAttribute(dataNode, "manufacturing")){
					manufacturing = AttributeGetters.getDouble(dataNode, "manufacturing");
				}
				double contractCollateral = 0.0;
				if (AttributeGetters.haveAttribute(dataNode, "contractcollateral")){
					contractCollateral = AttributeGetters.getDouble(dataNode, "contractcollateral");
				}
				double contractValue = 0.0;
				if (AttributeGetters.haveAttribute(dataNode, "contractvalue")){
					contractValue = AttributeGetters.getDouble(dataNode, "contractvalue");
				}
				//Add data
				Value value = new Value(date);
				//Balance
				NodeList balanceNodeList = dataNode.getElementsByTagName("balance");
				for (int c = 0; c < balanceNodeList.getLength(); c++) { //New data
					Element balanceNode = (Element) balanceNodeList.item(c);
					String id = AttributeGetters.getString(balanceNode, "id");
					double balance = AttributeGetters.getDouble(balanceNode, "value");
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
					double assets = AttributeGetters.getDouble(assetNode, "value");
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
				settings.getTrackerData().get(owner).add(value);
			}
			//Remove empty owners
			if (settings.getTrackerData().get(owner).isEmpty()) {
				settings.getTrackerData().remove(owner);
			}
		}
	}

	private AssetValue parseAssetValue(Element node) throws XmlException {
		if (AttributeGetters.haveAttribute(node, "id")) {
			String id = AttributeGetters.getString(node, "id");
			return new AssetValue(id);
		} else {
			String location = AttributeGetters.getString(node, "location");
			Long locationID = AttributeGetters.getLongOptional(node, "locationid");
			String flag = AttributeGetters.getStringOptional(node, "flag");
			return new AssetValue(location, flag, locationID);
		}
	}

	private void parseTrackerNotes(final Element element, final Settings settings) throws XmlException {
		NodeList noteNodeList = element.getElementsByTagName("trackernote");
		for (int a = 0; a < noteNodeList.getLength(); a++) {
			//Read Owner
			Element noteNode = (Element) noteNodeList.item(a);
			String note = AttributeGetters.getString(noteNode, "note");
			Date date = AttributeGetters.getDate(noteNode, "date");
			settings.getTrackerNotes().put(new TrackerDate(date), new TrackerNote(note));
		}
	}

	private void parseTrackerFilters(final Element element, final Settings settings) throws XmlException {
		NodeList tableNodeList = element.getElementsByTagName("trackerfilter");
		boolean selectNew = AttributeGetters.getBoolean(element, "selectnew");
		settings.setTrackerSelectNew(selectNew);
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			Element trackerFilterNode = (Element) tableNodeList.item(a);
			String id = AttributeGetters.getString(trackerFilterNode, "id");
			boolean selected = AttributeGetters.getBoolean(trackerFilterNode, "selected");
			settings.getTrackerFilters().put(id, selected);
		}
	}

	private void parseAssetSettings(final Element assetSettingsElement, final Settings settings) throws XmlException {
		int maximumPurchaseAge = AttributeGetters.getInt(assetSettingsElement, "maximumpurchaseage");
		settings.setMaximumPurchaseAge(maximumPurchaseAge);
	}

	private void parseStockpileGroups(final Element stockpilesElement, final Settings settings) throws XmlException {
		int group2 = AttributeGetters.getInt(stockpilesElement, "stockpilegroup2");
		int group3 = AttributeGetters.getInt(stockpilesElement, "stockpilegroup3");
		if (group2 <= 0) {
			group2 = 100;
		}
		settings.setStockpileColorGroup2(group2);
		settings.setStockpileColorGroup3(group3);
	}

	private void parseStockpiles(final Element stockpilesElement, final List<Stockpile> stockpiles) throws XmlException {
		NodeList stockpileNodes = stockpilesElement.getElementsByTagName("stockpile");
		for (int a = 0; a < stockpileNodes.getLength(); a++) {
			Element stockpileNode = (Element) stockpileNodes.item(a);
			String name = AttributeGetters.getString(stockpileNode, "name");

		//LEGACY
			//Owners
			List<Long> ownerIDs = new ArrayList<Long>();
			if (AttributeGetters.haveAttribute(stockpileNode, "characterid")) {
				long ownerID = AttributeGetters.getLong(stockpileNode, "characterid");
				if (ownerID > 0) {
					ownerIDs.add(ownerID);
				}
			}
			//Containers
			List<String> containers = new ArrayList<String>();
			if (AttributeGetters.haveAttribute(stockpileNode, "container")) {
				String container = AttributeGetters.getString(stockpileNode, "container");
				if (!container.equals(General.get().all())) {
					containers.add(container);
				}
			}
			//Flags
			List<Integer> flagIDs = new ArrayList<Integer>();
			if (AttributeGetters.haveAttribute(stockpileNode, "flagid")) {
				int flagID = AttributeGetters.getInt(stockpileNode, "flagid");
				if (flagID > 0) {
					flagIDs.add(flagID);
				}
			}
			//Locations
			MyLocation location = null;
			if (AttributeGetters.haveAttribute(stockpileNode, "locationid")) {
				long locationID = AttributeGetters.getLong(stockpileNode, "locationid");
				location = ApiIdConverter.getLocation(locationID);
			}
			boolean exclude = false;
			//Include
			Boolean inventory = null;
			if (AttributeGetters.haveAttribute(stockpileNode, "inventory")) {
				inventory = AttributeGetters.getBoolean(stockpileNode, "inventory");
			}
			Boolean sellOrders = null;
			if (AttributeGetters.haveAttribute(stockpileNode, "sellorders")) {
				sellOrders = AttributeGetters.getBoolean(stockpileNode, "sellorders");
			}
			Boolean buyOrders = null;
			if (AttributeGetters.haveAttribute(stockpileNode, "buyorders")) {
				buyOrders = AttributeGetters.getBoolean(stockpileNode, "buyorders");
			}
			Boolean jobs = null;
			if (AttributeGetters.haveAttribute(stockpileNode, "jobs")) {
				jobs = AttributeGetters.getBoolean(stockpileNode, "jobs");
			}
			List<StockpileFilter> filters = new ArrayList<StockpileFilter>();
			if (inventory != null && sellOrders != null && buyOrders != null && jobs != null) {
				StockpileFilter filter = new StockpileFilter(location, flagIDs, containers, ownerIDs, exclude, inventory, sellOrders, buyOrders, jobs, false, false, false, false, false, false);
				filters.add(filter);
			}
		//NEW
			NodeList filterNodes = stockpileNode.getElementsByTagName("stockpilefilter");
			for (int b = 0; b < filterNodes.getLength(); b++) {
				Element filterNode = (Element) filterNodes.item(b);
				//Include
				boolean filterExclude = false;
				if (AttributeGetters.haveAttribute(filterNode, "exclude")) {
					filterExclude = AttributeGetters.getBoolean(filterNode, "exclude");
				}
				boolean filterSellingContracts = false;
				if (AttributeGetters.haveAttribute(filterNode, "sellingcontracts")) {
					filterSellingContracts = AttributeGetters.getBoolean(filterNode, "sellingcontracts");
				}
				boolean filterSoldBuy = false;
				if (AttributeGetters.haveAttribute(filterNode, "soldcontracts")) {
					filterSoldBuy = AttributeGetters.getBoolean(filterNode, "soldcontracts");
				}
				boolean filterBuyingContracts = false;
				if (AttributeGetters.haveAttribute(filterNode, "buyingcontracts")) {
					filterBuyingContracts = AttributeGetters.getBoolean(filterNode, "buyingcontracts");
				}
				boolean filterBoughtContracts = false;
				if (AttributeGetters.haveAttribute(filterNode, "boughtcontracts")) {
					filterBoughtContracts = AttributeGetters.getBoolean(filterNode, "boughtcontracts");
				}
				boolean filterInventory = AttributeGetters.getBoolean(filterNode, "inventory");
				boolean filterSellOrders = AttributeGetters.getBoolean(filterNode, "sellorders");
				boolean filterBuyOrders = AttributeGetters.getBoolean(filterNode, "buyorders");
				boolean filterBuyTransactions = false;
				if (AttributeGetters.haveAttribute(filterNode, "buytransactions")) {
					filterBuyTransactions = AttributeGetters.getBoolean(filterNode, "buytransactions");
				}
				boolean filterSellTransactions = false;
				if (AttributeGetters.haveAttribute(filterNode, "selltransactions")) {
					filterSellTransactions = AttributeGetters.getBoolean(filterNode, "selltransactions");
				}
				boolean filterJobs = AttributeGetters.getBoolean(filterNode, "jobs");
				//Location
				long locationID = AttributeGetters.getLong(filterNode, "locationid");
				location = ApiIdConverter.getLocation(locationID);
				//Owners
				List<Long> filterOwnerIDs = new ArrayList<Long>();
				NodeList ownerNodes = filterNode.getElementsByTagName("owner");
				for (int c = 0; c < ownerNodes.getLength(); c++) {
					Element ownerNode = (Element) ownerNodes.item(c);
					long filterOwnerID = AttributeGetters.getLong(ownerNode, "ownerid");
					filterOwnerIDs.add(filterOwnerID);
				}
				//Containers
				List<String> filterContainers = new ArrayList<String>();
				NodeList containerNodes = filterNode.getElementsByTagName("container");
				for (int c = 0; c < containerNodes.getLength(); c++) {
					Element containerNode = (Element) containerNodes.item(c);
					String filterContainer = AttributeGetters.getString(containerNode, "container");
					filterContainers.add(filterContainer);
				}
				//Flags
				List<Integer> filterFlagIDs = new ArrayList<Integer>();
				NodeList flagNodes = filterNode.getElementsByTagName("flag");
				for (int c = 0; c < flagNodes.getLength(); c++) {
					Element flagNode = (Element) flagNodes.item(c);
					int filterFlagID = AttributeGetters.getInt(flagNode, "flagid");
					filterFlagIDs.add(filterFlagID);
				}
				StockpileFilter stockpileFilter = new StockpileFilter(location, filterFlagIDs, filterContainers, filterOwnerIDs, filterExclude, filterInventory, filterSellOrders, filterBuyOrders, filterJobs, filterBuyTransactions, filterSellTransactions, filterSellingContracts, filterSoldBuy, filterBuyingContracts, filterBoughtContracts);
				filters.add(stockpileFilter);
			}
		//MULTIPLIER
			double multiplier = 1;
			if (AttributeGetters.haveAttribute(stockpileNode, "multiplier")){
				multiplier = AttributeGetters.getDouble(stockpileNode, "multiplier");
			}
		
			Stockpile stockpile = new Stockpile(name, filters, multiplier);
			stockpiles.add(stockpile);
		//ITEMS
			NodeList itemNodes = stockpileNode.getElementsByTagName("item");
			for (int b = 0; b < itemNodes.getLength(); b++) {
				Element itemNode = (Element) itemNodes.item(b);
				long id;
				if (AttributeGetters.haveAttribute(itemNode, "id")) {
					id = AttributeGetters.getLong(itemNode, "id");
				} else {
					id = StockpileItem.getNewID();
				}
				int typeID = AttributeGetters.getInt(itemNode, "typeid");
				double countMinimum = AttributeGetters.getDouble(itemNode, "minimum");
				if (typeID != 0) { //Ignore Total
					Item item = ApiIdConverter.getItem(Math.abs(typeID));
					StockpileItem stockpileItem = new StockpileItem(stockpile, item, typeID, countMinimum, id);
					stockpile.add(stockpileItem);
				}
			}
		}
	}

	private void parseEveKitSettings(Element eveKitElement, Settings settings) throws XmlException {
		int transactionsHistory = 3;
		if (AttributeGetters.haveAttribute(eveKitElement, "transactionshistory")) {
			transactionsHistory = AttributeGetters.getInt(eveKitElement, "transactionshistory");
		}
		int journalHistory = 3;
		if (AttributeGetters.haveAttribute(eveKitElement, "journalhistory")) {
			journalHistory = AttributeGetters.getInt(eveKitElement, "journalhistory");
		}
		int marketOrdersHistory = 3;
		if (AttributeGetters.haveAttribute(eveKitElement, "marketordershistory")) {
			marketOrdersHistory = AttributeGetters.getInt(eveKitElement, "marketordershistory");
		}
		int industryJobsHistory = 3;
		if (AttributeGetters.haveAttribute(eveKitElement, "industryjobshistory")) {
			industryJobsHistory = AttributeGetters.getInt(eveKitElement, "industryjobshistory");
		}
		int contractsHistory = 3;
		if (AttributeGetters.haveAttribute(eveKitElement, "contractshistory")) {
			contractsHistory = AttributeGetters.getInt(eveKitElement, "contractshistory");
		}
		settings.setEveKitTransactionsHistory(transactionsHistory);
		settings.setEveKitJournalHistory(journalHistory);
		settings.setEveKitMarketOrdersHistory(marketOrdersHistory);
		settings.setEveKitIndustryJobsHistory(industryJobsHistory);
		settings.setEveKitContractsHistory(contractsHistory);
		
	}

	private void parseRoutingSettings(Element routingElement, Settings settings) throws XmlException {
		double secMax = AttributeGetters.getDouble(routingElement, "securitymaximum");
		double secMin = AttributeGetters.getDouble(routingElement, "securityminimum");
		settings.getRoutingSettings().setSecMax(secMax);
		settings.getRoutingSettings().setSecMin(secMin);
		NodeList systemNodes = routingElement.getElementsByTagName("routingsystem");
		for (int a = 0; a < systemNodes.getLength(); a++) {
			Element systemNode = (Element) systemNodes.item(a);
			Long systemID = AttributeGetters.getLong(systemNode, "id");
			MyLocation location = ApiIdConverter.getLocation(systemID);
			settings.getRoutingSettings().getAvoid().put(systemID, new SolarSystem(location));
		}
		NodeList presetNodes = routingElement.getElementsByTagName("routingpreset");
		for (int a = 0; a < presetNodes.getLength(); a++) {
			Element presetNode = (Element) presetNodes.item(a);
			String name = AttributeGetters.getString(presetNode, "name");
			Set<Long> systemIDs = new HashSet<Long>();
			NodeList presetSystemNodes = presetNode.getElementsByTagName("presetsystem");
			for (int b = 0; b < presetSystemNodes.getLength(); b++) {
				Element systemNode = (Element) presetSystemNodes.item(b);
				Long systemID = AttributeGetters.getLong(systemNode, "id");
				systemIDs.add(systemID);
			}
			Settings.get().getRoutingSettings().getPresets().put(name, systemIDs);
		}
		NodeList routeNodes = routingElement.getElementsByTagName("route");
		for (int a = 0; a < routeNodes.getLength(); a++) {
			Element routeNode = (Element) routeNodes.item(a);
			String name = AttributeGetters.getString(routeNode, "name");
			String algorithmName = AttributeGetters.getString(routeNode, "algorithmname");
			long algorithmTime = AttributeGetters.getLong(routeNode, "algorithmtime");
			int jumps = AttributeGetters.getInt(routeNode, "jumps");
			int waypoints = AttributeGetters.getInt(routeNode, "waypoints");
			NodeList routeSystemsNodes = routeNode.getElementsByTagName("routesystems");
			List<List<SolarSystem>> route = new ArrayList<List<SolarSystem>>();
			Map<Long, List<SolarSystem>> stationsMap = new HashMap<Long, List<SolarSystem>>();
			for (int b = 0; b < routeSystemsNodes.getLength(); b++) {
				Element routeStartSystemNode = (Element) routeSystemsNodes.item(b);
				NodeList routeSystemNodes = routeStartSystemNode.getElementsByTagName("routesystem");
				List<SolarSystem> systems = new ArrayList<SolarSystem>();
				for (int c = 0; c < routeSystemNodes.getLength(); c++) {
					Element routeSystemNode = (Element) routeSystemNodes.item(c);
					long systemID = AttributeGetters.getLong(routeSystemNode, "systemid");
					SolarSystem system = new SolarSystem(ApiIdConverter.getLocation(systemID));
					systems.add(system);
				}
				route.add(systems);
				NodeList routeStationNodes = routeStartSystemNode.getElementsByTagName("routestation");
				for (int c = 0; c < routeStationNodes.getLength(); c++) {
					Element routeStationNode = (Element) routeStationNodes.item(c);
					long stationID = AttributeGetters.getLong(routeStationNode, "stationid");
					SolarSystem station = new SolarSystem(ApiIdConverter.getLocation(stationID));
					List<SolarSystem> stationsList = stationsMap.get(systems.get(0).getSystemID());
					if (stationsList == null) {
						stationsList = new ArrayList<>();
						stationsMap.put(systems.get(0).getSystemID(), stationsList);
					}
					stationsList.add(station);
				}
			}
			settings.getRoutingSettings().getRoutes().put(name, new RouteResult(route, stationsMap, waypoints, algorithmName, algorithmTime, jumps));
		}
	}

	private void parseTags(Element tagsElement, Settings settings) throws XmlException {
		NodeList tagNodes = tagsElement.getElementsByTagName("tag");
		for (int a = 0; a < tagNodes.getLength(); a++) {
			Element tagNode = (Element) tagNodes.item(a);
			String name = AttributeGetters.getString(tagNode, "name");
			String background = AttributeGetters.getString(tagNode, "background");
			String foreground = AttributeGetters.getString(tagNode, "foreground");

			TagColor color = new TagColor(background, foreground);
			Tag tag = new Tag(name, color);
			settings.getTags().put(tag.getName(), tag);

			NodeList idNodes = tagNode.getElementsByTagName("tagid");
			for (int b = 0; b < idNodes.getLength(); b++) {
				Element idNode = (Element) idNodes.item(b);
				String tool = AttributeGetters.getString(idNode, "tool");
				Long id = AttributeGetters.getLong(idNode, "id");

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

	private void parseReprocessing(final Element windowElement, final Settings settings) throws XmlException {
		int reprocessing = AttributeGetters.getInt(windowElement, "refining");
		int reprocessingEfficiency = AttributeGetters.getInt(windowElement, "efficiency");
		int scrapmetalProcessing = AttributeGetters.getInt(windowElement, "processing");
		int station = AttributeGetters.getInt(windowElement, "station");
		settings.setReprocessSettings(new ReprocessSettings(station, reprocessing, reprocessingEfficiency, scrapmetalProcessing));
	}

	private void parseWindow(final Element windowElement, final Settings settings) throws XmlException {
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

	private void parseProxy(final Element proxyElement, final Settings settings) throws XmlException {
		Proxy.Type type;
		try {
			type = Proxy.Type.valueOf(AttributeGetters.getString(proxyElement, "type"));
		} catch (IllegalArgumentException  ex) {
			type = null;
		}
		String address = AttributeGetters.getString(proxyElement, "address");
		int port = AttributeGetters.getInt(proxyElement, "port");
		String username = null;
		if (AttributeGetters.haveAttribute(proxyElement, "username")) {
			username = AttributeGetters.getString(proxyElement, "username");
		}
		String password = null;
		if (AttributeGetters.haveAttribute(proxyElement, "password")) {
			password = AttributeGetters.getString(proxyElement, "password");
		}
		if (type != null && type != Proxy.Type.DIRECT && !address.isEmpty() && port != 0) { // check the proxy attributes are all there.
			settings.setProxyData(new ProxyData(address, type, port, username, password));
		}
	}

	private void parseUserPrices(final Element element, final Settings settings) throws XmlException {
		NodeList userPriceNodes = element.getElementsByTagName("userprice");
		for (int i = 0; i < userPriceNodes.getLength(); i++) {
			Element currentNode = (Element) userPriceNodes.item(i);
			String name = AttributeGetters.getString(currentNode, "name");
			double price = AttributeGetters.getDouble(currentNode, "price");
			int typeID = AttributeGetters.getInt(currentNode, "typeid");
			UserItem<Integer, Double> userPrice = new UserPrice(price, typeID, name);
			settings.getUserPrices().put(typeID, userPrice);
		}
	}

	private void parseUserItemNames(final Element element, final Settings settings) throws XmlException {
		NodeList userPriceNodes = element.getElementsByTagName("itemname");
		for (int i = 0; i < userPriceNodes.getLength(); i++) {
			Element currentNode = (Element) userPriceNodes.item(i);
			String name = AttributeGetters.getString(currentNode, "name");
			String typeName = AttributeGetters.getString(currentNode, "typename");
			long itemId = AttributeGetters.getLong(currentNode, "itemid");
			UserItem<Long, String> userItemName = new UserName(name, itemId, typeName);
			settings.getUserItemNames().put(itemId, userItemName);
		}
	}

	private void parseEveNames(final Element element, final Settings settings) throws XmlException {
		NodeList eveNameNodes = element.getElementsByTagName("evename");
		for (int i = 0; i < eveNameNodes.getLength(); i++) {
			Element currentNode = (Element) eveNameNodes.item(i);
			String name = AttributeGetters.getString(currentNode, "name");
			long itemId = AttributeGetters.getLong(currentNode, "itemid");
			settings.getEveNames().put(itemId, name);
		}
	}

	private void parsePriceDataSettings(final Element element, final Settings settings) throws XmlException {
		PriceMode priceType = settings.getPriceDataSettings().getPriceType(); //Default
		if (AttributeGetters.haveAttribute(element, "defaultprice")) {
			priceType = PriceMode.valueOf(AttributeGetters.getString(element, "defaultprice"));
		}

		PriceMode priceReprocessedType = settings.getPriceDataSettings().getPriceReprocessedType(); //Default
		if (AttributeGetters.haveAttribute(element, "defaultreprocessedprice")) {
			priceReprocessedType = PriceMode.valueOf(AttributeGetters.getString(element, "defaultreprocessedprice"));
		}

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
		//Validate
		if (!priceSource.isValid(locationType, locations)) {
			locationType = priceSource.getDefaultLocationType();
			locations = priceSource.getDefaultLocations();
		}	
		settings.setPriceDataSettings(new PriceDataSettings(locationType, locations, priceSource, priceType, priceReprocessedType));
	}

	private void parseFlags(final Element element, final Settings settings) throws XmlException {
		NodeList flagNodes = element.getElementsByTagName("flag");
		for (int i = 0; i < flagNodes.getLength(); i++) {
			Element currentNode = (Element) flagNodes.item(i);
			String key = AttributeGetters.getString(currentNode, "key");
			boolean enabled = AttributeGetters.getBoolean(currentNode, "enabled");
			try {
				if (key.equals("FLAG_INCLUDE_CONTRACTS")) {
					settings.getFlags().put(SettingFlag.FLAG_INCLUDE_SELL_CONTRACTS, enabled);
					settings.getFlags().put(SettingFlag.FLAG_INCLUDE_BUY_CONTRACTS, enabled);
				}
				SettingFlag settingFlag = SettingFlag.valueOf(key);
				settings.getFlags().put(settingFlag, enabled);
			} catch (IllegalArgumentException ex) {
				LOG.warn("Removing Setting Flag:" + key);
			}
		}
		settings.cacheFlags();
	}

	private void parseUpdates(final Element element, final Settings settings) throws XmlException {
		NodeList updateNodes = element.getElementsByTagName("update");
		for (int i = 0; i < updateNodes.getLength(); i++) {
			Element currentNode = (Element) updateNodes.item(i);
			parseUpdate(currentNode, settings);
		}
	}
	private void parseUpdate(final Element element, final Settings settings) throws XmlException {
		String text = AttributeGetters.getString(element, "name");
		Date nextUpdate = AttributeGetters.getDate(element, "nextupdate");
		if (text.equals("conquerable station")) {
			settings.setConquerableStationsNextUpdate(nextUpdate);
		}
	}

	private void parseTableColumns(final Element element, final Settings settings) throws XmlException {
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

	private void parseTableColumnsWidth(final Element element, final Settings settings) throws XmlException {
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int a = 0; a < tableNodeList.getLength(); a++) {
			Map<String, Integer> columns = new HashMap<String, Integer>();
			Element tableNode = (Element) tableNodeList.item(a);
			String tableName = AttributeGetters.getString(tableNode, "name");
			NodeList columnNodeList = tableNode.getElementsByTagName("column");
			for (int b = 0; b < columnNodeList.getLength(); b++) {
				Element columnNode = (Element) columnNodeList.item(b);
				int width = AttributeGetters.getInt(columnNode, "width");
				String column = AttributeGetters.getString(columnNode, "column");
				columns.put(column, width);
			}
			settings.getTableColumnsWidth().put(tableName, columns);
		}
	}

	private void parseTableResize(final Element element, final Settings settings) throws XmlException {
		NodeList tableNodeList = element.getElementsByTagName("table");
		for (int i = 0; i < tableNodeList.getLength(); i++) {
			Element tableNode = (Element) tableNodeList.item(i);
			String tableName = AttributeGetters.getString(tableNode, "name");
			ResizeMode resizeMode = ResizeMode.valueOf(AttributeGetters.getString(tableNode, "resize"));
			settings.getTableResize().put(tableName, resizeMode);
		}
	}

	private void parseTableViews(final Element element, final Settings settings) throws XmlException {
		NodeList viewToolNodeList = element.getElementsByTagName("viewtool");
		for (int a = 0; a < viewToolNodeList.getLength(); a++) {
			Element viewToolNode = (Element) viewToolNodeList.item(a);
			String toolName = AttributeGetters.getString(viewToolNode, "tool");
			Map<String, View> views = new TreeMap<String, View>(new CaseInsensitiveComparator());
			settings.getTableViews().put(toolName, views);
			NodeList viewNodeList = viewToolNode.getElementsByTagName("view");
			for (int b = 0; b < viewNodeList.getLength(); b++) {
				Element viewNode = (Element) viewNodeList.item(b);
				String viewName = AttributeGetters.getString(viewNode, "name");
				View view = new View(viewName);
				views.put(view.getName(), view);
				NodeList viewColumnList = viewNode.getElementsByTagName("viewcolumn");
				for (int c = 0; c < viewColumnList.getLength(); c++) {
					Element viewColumnNode = (Element) viewColumnList.item(c);
					String name = AttributeGetters.getString(viewColumnNode, "name");
					boolean shown = AttributeGetters.getBoolean(viewColumnNode, "shown");
					view.getColumns().add(new SimpleColumn(name, shown));
				}
			}
		}
	}

	private void parseTableFilters(final Element element, final Settings settings) throws XmlException {
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
					EnumTableColumn<?> column =  getColumn(columnString, tableName);
					if (column != null) {
						String compare = AttributeGetters.getString(rowNode, "compare");
						String logic = AttributeGetters.getString(rowNode, "logic");
						filter.add(new Filter(logic, column, compare, text));
					} else {
						LOG.warn(columnString + " column removed from filter");
					}
				}
				if (!filter.isEmpty()) {
					filters.put(filterName, filter);
				} else {
					LOG.warn(filterName + " filter removed (Empty)");
				}
			}
			settings.getTableFilters().put(tableName, filters);
		}
	}

	public static EnumTableColumn<?> getColumn(final String column, final String tableName) {
		//Stockpile
		try {
			if (tableName.equals(StockpileTab.NAME)) {
				return StockpileExtendedTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Stockpile (Extra)
		try {
			if (tableName.equals(StockpileTab.NAME)) {
				return StockpileTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Industry Jobs
		try {
			if (tableName.equals(IndustryJobsTab.NAME)) {
				return IndustryJobTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Market Orders
		try {
			if (tableName.equals(MarketOrdersTab.NAME)) {
				return MarketTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Journal
		try {
			if (tableName.equals(JournalTab.NAME)) {
				return JournalTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Transaction
		try {
			if (tableName.equals(TransactionTab.NAME)) {
				return TransactionTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Assets
		try {
			if (tableName.equals(AssetsTab.NAME)) {
				return AssetTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Items
		try {
			if (tableName.equals(ItemsTab.NAME)) {
				return ItemTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Contracts
		try {
			if (tableName.equals(ContractsTab.NAME)) {
				return ContractsTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Contracts (Extra)
		try {
			if (tableName.equals(ContractsTab.NAME)) {
				return ContractsExtendedTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Values (Extra)
		try {
			if (tableName.equals(ValueTableTab.NAME)) {
				return ValueTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//Values (Extra)
		try {
			if (tableName.equals(TreeTab.NAME)) {
				return TreeTableFormat.valueOf(column);
			}
		} catch (IllegalArgumentException exception) {

		}
		//All
		if (column.equals("ALL")) {
			return AllColumn.ALL;
		}
		return null;
	}

	private void parseAssetFilters(final Element filtersElement, final Settings settings) throws XmlException {
		NodeList filterNodeList = filtersElement.getElementsByTagName("filter");
		for (int a = 0; a < filterNodeList.getLength(); a++) {
			Element filterNode = (Element) filterNodeList.item(a);
			String filterName = AttributeGetters.getString(filterNode, "name");

			List<Filter> filters = new ArrayList<Filter>();

			NodeList rowNodeList = filterNode.getElementsByTagName("row");
			for (int b = 0; b < rowNodeList.getLength(); b++) {
				Element rowNode = (Element) rowNodeList.item(b);
				LogicType logic = convertLogic(AttributeGetters.getBoolean(rowNode, "and"));
				EnumTableColumn<?> column = convertColumn(AttributeGetters.getString(rowNode, "column"));
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

	private void parseApiProxy(final Element apiProxyElement, final Settings settings) throws XmlException {
		String proxyURL = AttributeGetters.getString(apiProxyElement, "url");
		settings.setApiProxy(proxyURL);
	}

	private void parseExportSettings(final Element element, final Settings settings) throws XmlException {
		//CSV
		DecimalSeparator decimal = DecimalSeparator.valueOf(AttributeGetters.getString(element, "decimal"));
		FieldDelimiter field = FieldDelimiter.valueOf(AttributeGetters.getString(element, "field"));
		LineDelimiter line = LineDelimiter.valueOf(AttributeGetters.getString(element, "line"));
		settings.getExportSettings().setDecimalSeparator(decimal);
		settings.getExportSettings().setFieldDelimiter(field);
		settings.getExportSettings().setLineDelimiter(line);
		//SQL
		if (AttributeGetters.haveAttribute(element, "sqlcreatetable")) {
			boolean createTable = AttributeGetters.getBoolean(element, "sqlcreatetable");
			settings.getExportSettings().setCreateTable(createTable);
		}
		if (AttributeGetters.haveAttribute(element, "sqldroptable")) {
			boolean dropTable = AttributeGetters.getBoolean(element, "sqldroptable");
			settings.getExportSettings().setDropTable(dropTable);
		}
		if (AttributeGetters.haveAttribute(element, "sqlextendedinserts")) {
			boolean extendedInserts = AttributeGetters.getBoolean(element, "sqlextendedinserts");
			settings.getExportSettings().setExtendedInserts(extendedInserts);
		}
		if (AttributeGetters.haveAttribute(element, "htmlstyled")) {
			boolean htmlStyled = AttributeGetters.getBoolean(element, "htmlstyled");
			settings.getExportSettings().setHtmlStyled(htmlStyled);
		}
		if (AttributeGetters.haveAttribute(element, "htmligb")) {
			boolean htmlIGB = AttributeGetters.getBoolean(element, "htmligb");
			settings.getExportSettings().setHtmlIGB(htmlIGB);
		}
		if (AttributeGetters.haveAttribute(element, "htmlrepeatheader")) {
			int htmlRepeatHeader = AttributeGetters.getInt(element, "htmlrepeatheader");
			settings.getExportSettings().setHtmlRepeatHeader(htmlRepeatHeader);
		}
		if (AttributeGetters.haveAttribute(element, "exportformat")) {
			ExportFormat exportFormat = ExportFormat.valueOf(AttributeGetters.getString(element, "exportformat"));
			settings.getExportSettings().setExportFormat(exportFormat);
		}
		NodeList tableNamesNodeList = element.getElementsByTagName("sqltablenames");
		for (int a = 0; a < tableNamesNodeList.getLength(); a++) {
			Element tableNameNode = (Element) tableNamesNodeList.item(a);
			String tool = AttributeGetters.getString(tableNameNode, "tool");
			String tableName = AttributeGetters.getString(tableNameNode, "tablename");
			settings.getExportSettings().putTableName(tool, tableName);
		}
		//Shared
		NodeList fileNamesNodeList = element.getElementsByTagName("filenames");
		for (int a = 0; a < fileNamesNodeList.getLength(); a++) {
			Element tableNameNode = (Element) fileNamesNodeList.item(a);
			String tool = AttributeGetters.getString(tableNameNode, "tool");
			String fileName = AttributeGetters.getString(tableNameNode, "filename");
			settings.getExportSettings().putFilename(tool, fileName);
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
			settings.getExportSettings().putTableExportColumns(tableName, columns);
		}
	}

	private void parseAssetAdded(final Element element, final Settings settings) throws XmlException {
		NodeList assetNodes = element.getElementsByTagName("asset");
		for (int i = 0; i < assetNodes.getLength(); i++) {
			Element currentNode = (Element) assetNodes.item(i);
			Long itemID = AttributeGetters.getLong(currentNode, "itemid");
			Date date = AttributeGetters.getDate(currentNode, "date");
			settings.getAssetAdded().put(itemID, date);
		}
	}
}
