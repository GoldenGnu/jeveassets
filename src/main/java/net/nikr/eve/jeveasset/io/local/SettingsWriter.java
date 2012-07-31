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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlWriter;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class SettingsWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(SettingsWriter.class);

	public static void save(final Settings settings) {
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("settings");
		} catch (XmlException ex) {
			LOG.error("Settings not saved " + ex.getMessage(), ex);
		}
		//Add version number
		xmldoc.getDocumentElement().setAttribute("version", String.valueOf(SettingsReader.SETTINGS_VERSION));

		writeAssetSettings(xmldoc, settings);
		writeStockpiles(xmldoc, settings.getStockpiles());
		writeOverviewGroups(xmldoc, settings.getOverviewGroups());
		writeReprocessSettings(xmldoc, settings.getReprocessSettings());
		writeWindow(xmldoc, settings);
		writeProxy(xmldoc, settings.getProxy());
		writeApiProxy(xmldoc, settings.getApiProxy());
		writePriceDataSettings(xmldoc, settings.getPriceDataSettings());
		writeFlags(xmldoc, settings.getFlags());
		writeUserPrices(xmldoc, settings.getUserPrices());
		writeUserItemNames(xmldoc, settings.getUserItemNames());
		writeUpdates(xmldoc, settings);
		writeTableFilters(xmldoc, settings.getTableFilters());
		writeTableColumns(xmldoc, settings.getTableColumns());
		writeTableColumnsWidth(xmldoc, settings.getTableColumnsWidth());
		writeTablesResize(xmldoc, settings.getTableResize());
		writeExportSettings(xmldoc, Settings.getExportSettings());
		try {
			writeXmlFile(xmldoc, settings.getPathSettings(), true);
		} catch (XmlException ex) {
			LOG.error("Settings not saved " + ex.getMessage(), ex);
		}
		LOG.info("Settings saved");
	}

	private static void writeTableFilters(final Document xmldoc, final Map<String, Map<String, List<Filter>>> tableFilters) {
		Element tablefiltersNode = xmldoc.createElementNS(null, "tablefilters");
		xmldoc.getDocumentElement().appendChild(tablefiltersNode);
		for (Map.Entry<String, Map<String, List<Filter>>> entry : tableFilters.entrySet()) {
			Element nameNode = xmldoc.createElementNS(null, "table");
			nameNode.setAttributeNS(null, "name", entry.getKey());
			tablefiltersNode.appendChild(nameNode);
			for (Map.Entry<String, List<Filter>> filters : entry.getValue().entrySet()) {
				Element filterNode = xmldoc.createElementNS(null, "filter");
				filterNode.setAttributeNS(null, "name", filters.getKey());
				nameNode.appendChild(filterNode);
				for (Filter filter :  filters.getValue()) {
					Element childNode = xmldoc.createElementNS(null, "row");
					childNode.setAttributeNS(null, "text", filter.getText());
					childNode.setAttributeNS(null, "column",  filter.getColumn().name());
					childNode.setAttributeNS(null, "compare", filter.getCompareType().name());
					childNode.setAttributeNS(null, "logic", filter.getLogic().name());
					filterNode.appendChild(childNode);
				}
			}
		}
	}

	private static void writeTableColumns(final Document xmldoc, final Map<String, List<SimpleColumn>> tableColumns) {
		Element tablecolumnsNode = xmldoc.createElementNS(null, "tablecolumns");
		xmldoc.getDocumentElement().appendChild(tablecolumnsNode);
		for (Map.Entry<String, List<SimpleColumn>> entry : tableColumns.entrySet()) {
			Element nameNode = xmldoc.createElementNS(null, "table");
			nameNode.setAttributeNS(null, "name", entry.getKey());
			tablecolumnsNode.appendChild(nameNode);
			for (SimpleColumn column : entry.getValue()) {
				Element node = xmldoc.createElementNS(null, "column");
				node.setAttributeNS(null, "name", column.getEnumName());
				node.setAttributeNS(null, "shown", String.valueOf(column.isShown()));
				nameNode.appendChild(node);
			}
		}
	}

	private static void writeTableColumnsWidth(final Document xmldoc, final Map<String, Map<String, Integer>> tableColumnsWidth) {
		Element tablecolumnsNode = xmldoc.createElementNS(null, "tablecolumnswidth");
		xmldoc.getDocumentElement().appendChild(tablecolumnsNode);
		for (Map.Entry<String, Map<String, Integer>> table : tableColumnsWidth.entrySet()) {
			Element nameNode = xmldoc.createElementNS(null, "table");
			nameNode.setAttributeNS(null, "name", table.getKey());
			tablecolumnsNode.appendChild(nameNode);
			for (Map.Entry<String, Integer> column : table.getValue().entrySet()) {
				Element node = xmldoc.createElementNS(null, "column");
				node.setAttributeNS(null, "column", String.valueOf(column.getKey()));
				node.setAttributeNS(null, "width", String.valueOf(column.getValue()));
				nameNode.appendChild(node);
			}
		}
	}

	private static void writeTablesResize(final Document xmldoc, final Map<String, ResizeMode> tableColumns) {
		Element tablecolumnsNode = xmldoc.createElementNS(null, "tableresize");
		xmldoc.getDocumentElement().appendChild(tablecolumnsNode);
		for (Map.Entry<String, ResizeMode> entry : tableColumns.entrySet()) {
			Element nameNode = xmldoc.createElementNS(null, "table");
			nameNode.setAttributeNS(null, "name", entry.getKey());
			nameNode.setAttributeNS(null, "resize", entry.getValue().name());
			tablecolumnsNode.appendChild(nameNode);
		}
	}

	private static void writeAssetSettings(final Document xmldoc, final Settings settings) {
		Element parentNode = xmldoc.createElementNS(null, "assetsettings");
		xmldoc.getDocumentElement().appendChild(parentNode);
		parentNode.setAttributeNS(null, "maximumpurchaseage", String.valueOf(settings.getMaximumPurchaseAge()));
	}

	private static void writeStockpiles(final Document xmldoc, final List<Stockpile> stockpiles) {
		Element parentNode = xmldoc.createElementNS(null, "stockpiles");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Stockpile strockpile : stockpiles) {
			Element strockpileNode = xmldoc.createElementNS(null, "stockpile");
			strockpileNode.setAttributeNS(null, "name", strockpile.getName());
			strockpileNode.setAttributeNS(null, "characterid", String.valueOf(strockpile.getOwnerID()));
			strockpileNode.setAttributeNS(null, "container", strockpile.getContainer());
			strockpileNode.setAttributeNS(null, "flagid", String.valueOf(strockpile.getFlagID()));
			strockpileNode.setAttributeNS(null, "locationid", String.valueOf(strockpile.getLocationID()));
			strockpileNode.setAttributeNS(null, "inventory", String.valueOf(strockpile.isInventory()));
			strockpileNode.setAttributeNS(null, "sellorders", String.valueOf(strockpile.isSellOrders()));
			strockpileNode.setAttributeNS(null, "buyorders", String.valueOf(strockpile.isBuyOrders()));
			strockpileNode.setAttributeNS(null, "jobs", String.valueOf(strockpile.isJobs()));
			for (StockpileItem item : strockpile.getItems()) {
				if (item.getItemTypeID() != 0) { //Ignore Total
					Element itemNode = xmldoc.createElementNS(null, "item");
					itemNode.setAttributeNS(null, "typeid", String.valueOf(item.getItemTypeID()));
					itemNode.setAttributeNS(null, "minimum", String.valueOf(item.getCountMinimum()));
					strockpileNode.appendChild(itemNode);
				}
			}
			parentNode.appendChild(strockpileNode);
		}
	}

	private static void writeOverviewGroups(final Document xmldoc, final Map<String, OverviewGroup> overviewGroups) {
		Element parentNode = xmldoc.createElementNS(null, "overview");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<String, OverviewGroup> entry : overviewGroups.entrySet()) {
			OverviewGroup overviewGroup = entry.getValue();
			Element node = xmldoc.createElementNS(null, "group");
			node.setAttributeNS(null, "name", overviewGroup.getName());
			parentNode.appendChild(node);
			for (int a = 0; a < overviewGroup.getLocations().size(); a++) {
				Element nodeLocation = xmldoc.createElementNS(null, "location");
				nodeLocation.setAttributeNS(null, "name", overviewGroup.getLocations().get(a).getName());
				nodeLocation.setAttributeNS(null, "type", overviewGroup.getLocations().get(a).getType().name());
				node.appendChild(nodeLocation);
			}
		}
	}

	private static void writeUserItemNames(final Document xmldoc, final Map<Long, UserItem<Long, String>> userPrices) {
		Element parentNode = xmldoc.createElementNS(null, "itemmames");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<Long, UserItem<Long, String>> entry : userPrices.entrySet()) {
			UserItem<Long, String> userItemName = entry.getValue();
			Element node = xmldoc.createElementNS(null, "itemname");
			node.setAttributeNS(null, "name", userItemName.getValue());
			node.setAttributeNS(null, "typename", userItemName.getName());
			node.setAttributeNS(null, "itemid", String.valueOf(userItemName.getKey()));
			parentNode.appendChild(node);
		}
	}

	private static void writeReprocessSettings(final Document xmldoc, final ReprocessSettings reprocessSettings) {
		Element parentNode = xmldoc.createElementNS(null, "reprocessing");
		xmldoc.getDocumentElement().appendChild(parentNode);
		parentNode.setAttributeNS(null, "refining", String.valueOf(reprocessSettings.getRefiningLevel()));
		parentNode.setAttributeNS(null, "efficiency", String.valueOf(reprocessSettings.getRefineryEfficiencyLevel()));
		parentNode.setAttributeNS(null, "processing", String.valueOf(reprocessSettings.getScrapmetalProcessingLevel()));
		parentNode.setAttributeNS(null, "station", String.valueOf(reprocessSettings.getStation()));
	}

	private static void writeWindow(final Document xmldoc, final Settings settings) {
		Element parentNode = xmldoc.createElementNS(null, "window");
		xmldoc.getDocumentElement().appendChild(parentNode);
		parentNode.setAttributeNS(null, "x", String.valueOf(settings.getWindowLocation().x));
		parentNode.setAttributeNS(null, "y", String.valueOf(settings.getWindowLocation().y));
		parentNode.setAttributeNS(null, "height", String.valueOf(settings.getWindowSize().height));
		parentNode.setAttributeNS(null, "width", String.valueOf(settings.getWindowSize().width));
		parentNode.setAttributeNS(null, "maximized", String.valueOf(settings.isWindowMaximized()));
		parentNode.setAttributeNS(null, "autosave", String.valueOf(settings.isWindowAutoSave()));
		parentNode.setAttributeNS(null, "alwaysontop", String.valueOf(settings.isWindowAlwaysOnTop()));
	}

	private static void writeUserPrices(final Document xmldoc, final Map<Integer, UserItem<Integer, Double>> userPrices) {
		Element parentNode = xmldoc.createElementNS(null, "userprices");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<Integer, UserItem<Integer, Double>> entry : userPrices.entrySet()) {
			UserItem<Integer, Double> userPrice = entry.getValue();
			Element node = xmldoc.createElementNS(null, "userprice");
			node.setAttributeNS(null, "name", userPrice.getName());
			node.setAttributeNS(null, "price", String.valueOf(userPrice.getValue()));
			node.setAttributeNS(null, "typeid", String.valueOf(userPrice.getKey()));
			parentNode.appendChild(node);
		}
	}
	private static void writePriceDataSettings(final Document xmldoc, final PriceDataSettings priceDataSettings) {
		Element parentNode = xmldoc.createElementNS(null, "marketstat");
		parentNode.setAttributeNS(null, "defaultprice", Asset.getPriceType().name());
		parentNode.setAttributeNS(null, "pricesource", priceDataSettings.getSource().name());
		StringBuilder builder = new StringBuilder();
		for (long location : priceDataSettings.getLocations()) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(location);
		}
		parentNode.setAttributeNS(null, "locations", builder.toString());
		parentNode.setAttributeNS(null, "type", priceDataSettings.getLocationType().name());
		xmldoc.getDocumentElement().appendChild(parentNode);
	}

	private static void writeFlags(final Document xmldoc, final Map<String, Boolean> flags) {
		Element parentNode = xmldoc.createElementNS(null, "flags");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<String, Boolean> entry : flags.entrySet()) {
			Element node = xmldoc.createElementNS(null, "flag");
			node.setAttributeNS(null, "key", entry.getKey());
			node.setAttributeNS(null, "enabled", String.valueOf(entry.getValue()));
			parentNode.appendChild(node);
		}
	}

	private static void writeUpdates(final Document xmldoc, final Settings settings) {
		Element parentNode = xmldoc.createElementNS(null, "updates");
		xmldoc.getDocumentElement().appendChild(parentNode);

		Element node;

		node = xmldoc.createElementNS(null, "update");
		node.setAttributeNS(null, "name", "conquerable station");
		node.setAttributeNS(null, "nextupdate", String.valueOf(settings.getConquerableStationsNextUpdate().getTime()));
		parentNode.appendChild(node);
	}

	private static void writeApiProxy(final Document xmldoc, final String apiProxy) {
		if (apiProxy != null) {
			Element node = xmldoc.createElementNS(null, "apiProxy");
			node.setAttributeNS(null, "url", String.valueOf(apiProxy));
			xmldoc.getDocumentElement().appendChild(node);
		}
	}

	private static void writeProxy(final Document xmldoc, final Proxy proxy) {
		if (proxy != null && !proxy.type().equals(Proxy.Type.DIRECT)) { // Only adds proxy tag if there is anything to save... (To prevent an error when the proxy tag doesn't have any attributes)
			Element node = xmldoc.createElementNS(null, "proxy");
			if (proxy.address() instanceof InetSocketAddress) {
				InetSocketAddress addr = (InetSocketAddress) proxy.address();
				node.setAttributeNS(null, "address", String.valueOf(addr.getHostName()));
				node.setAttributeNS(null, "port", String.valueOf(addr.getPort()));
				node.setAttributeNS(null, "type", String.valueOf(proxy.type()));
			}
			xmldoc.getDocumentElement().appendChild(node);
		}
	}

	private static void writeExportSettings(final Document xmldoc, final ExportSettings exportSettings) {
		Element node = xmldoc.createElementNS(null, "csvexport");
		xmldoc.getDocumentElement().appendChild(node);
		//CSV
		node.setAttributeNS(null, "decimal", exportSettings.getDecimalSeperator().name());
		node.setAttributeNS(null, "field", exportSettings.getFieldDelimiter().name());
		node.setAttributeNS(null, "line", exportSettings.getLineDelimiter().name());
		//SQL
		node.setAttributeNS(null, "sqlcreatetable", String.valueOf(exportSettings.isCreateTable()));
		node.setAttributeNS(null, "sqldroptable", String.valueOf(exportSettings.isDropTable()));
		node.setAttributeNS(null, "sqlextendedinserts", String.valueOf(exportSettings.isExtendedInserts()));
		for (Map.Entry<String, String> entry : exportSettings.getTableNames().entrySet()) {
			Element nameNode = xmldoc.createElementNS(null, "sqltablenames");
			nameNode.setAttributeNS(null, "tool", entry.getKey());
			nameNode.setAttributeNS(null, "tablename", entry.getValue());
			node.appendChild(nameNode);
		}
		//Shared
		for (Map.Entry<String, String> entry : exportSettings.getFilenames().entrySet()) {
			Element nameNode = xmldoc.createElementNS(null, "filenames");
			nameNode.setAttributeNS(null, "tool", entry.getKey());
			nameNode.setAttributeNS(null, "filename", entry.getValue());
			node.appendChild(nameNode);
		}
		for (Map.Entry<String, List<String>> entry : exportSettings.getTableExportColumns()) {
			Element nameNode = xmldoc.createElementNS(null, "table");
			nameNode.setAttributeNS(null, "name", entry.getKey());
			node.appendChild(nameNode);
			for (String column : entry.getValue()) {
				Element columnNode = xmldoc.createElementNS(null, "column");
				columnNode.setAttributeNS(null, "name", column);
				nameNode.appendChild(columnNode);
			}
		}
	}
}
