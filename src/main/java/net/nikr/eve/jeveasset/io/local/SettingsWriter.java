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

import java.net.Proxy;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.settings.ExportSettings;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings;
import net.nikr.eve.jeveasset.data.settings.ProxyData;
import net.nikr.eve.jeveasset.data.settings.ReprocessSettings;
import net.nikr.eve.jeveasset.data.settings.RouteResult;
import net.nikr.eve.jeveasset.data.settings.RoutingSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.Settings.SettingFlag;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.data.settings.tag.Tag;
import net.nikr.eve.jeveasset.data.settings.tag.TagID;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerDate;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerNote;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.gui.tabs.values.Value.AssetValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class SettingsWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(SettingsWriter.class);

	private SettingsWriter() { }

	public static boolean save(final Settings settings) {
		SettingsWriter writer = new SettingsWriter();
		return writer.write(settings);
	}

	public static boolean saveStockpiles(final List<Stockpile> stockpiles, final String filename) {
		SettingsWriter writer = new SettingsWriter();
		return writer.writeStockpiles(stockpiles, filename);
	}

	private boolean writeStockpiles(final List<Stockpile> stockpiles, final String filename) {
		Document xmldoc;
		try {
			xmldoc = getXmlDocument("settings");
		} catch (XmlException ex) {
			LOG.error("Stockpile not saved " + ex.getMessage(), ex);
			return false;
		}

		writeStockpiles(xmldoc, stockpiles, true);
		try {
			writeXmlFile(xmldoc, filename, false);
		} catch (XmlException ex) {
			LOG.error("Stockpile not saved " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Stockpile saved");
		return true;
	}

	private boolean write(final Settings settings) {
		Document xmldoc;
		try {
			xmldoc = getXmlDocument("settings");
		} catch (XmlException ex) {
			LOG.error("Settings not saved " + ex.getMessage(), ex);
			return false;
		}
		//Add version number
		xmldoc.getDocumentElement().setAttribute("version", String.valueOf(SettingsReader.SETTINGS_VERSION));

		writeAssetSettings(xmldoc, settings);
		writeStockpileGroups(xmldoc, settings);
		writeStockpiles(xmldoc, settings.getStockpiles(), false);
		writeOverviewGroups(xmldoc, settings.getOverviewGroups());
		writeReprocessSettings(xmldoc, settings.getReprocessSettings());
		writeWindow(xmldoc, settings);
		writeProxy(xmldoc, settings.getProxyData());
		writeApiProxy(xmldoc, settings.getApiProxy());
		writePriceDataSettings(xmldoc, settings.getPriceDataSettings());
		writeFlags(xmldoc, settings.getFlags());
		writeUserPrices(xmldoc, settings.getUserPrices());
		writeUserItemNames(xmldoc, settings.getUserItemNames());
		writeEveNames(xmldoc, settings.getEveNames());
		writeUpdates(xmldoc, settings);
		writeTableFilters(xmldoc, settings.getTableFilters());
		writeTableColumns(xmldoc, settings.getTableColumns());
		writeTableColumnsWidth(xmldoc, settings.getTableColumnsWidth());
		writeTablesResize(xmldoc, settings.getTableResize());
		writeTablesViews(xmldoc, settings.getTableViews());
		writeExportSettings(xmldoc, settings.getExportSettings());
		writeAssetAdded(xmldoc, settings.getAssetAdded());
		writeTrackerData(xmldoc, settings.getTrackerData());
		writeTrackerNotes(xmldoc, settings.getTrackerNotes());
		writeTrackerFilters(xmldoc, settings.getTrackerFilters(), settings.isTrackerSelectNew());
		writeOwners(xmldoc, settings.getOwners());
		writeTags(xmldoc, settings.getTags());
		writeRoutingSettings(xmldoc, settings.getRoutingSettings());
		writeEveKitSettings(xmldoc, settings);
		try {
			writeXmlFile(xmldoc, settings.getPathSettings(), true);
		} catch (XmlException ex) {
			LOG.error("Settings not saved " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Settings saved");
		return true;
	}

	private void writeEveKitSettings(Document xmldoc, Settings settings) {
		Element routingNode = xmldoc.createElementNS(null, "evekit");
		xmldoc.getDocumentElement().appendChild(routingNode);
		routingNode.setAttribute("transactionshistory", String.valueOf(settings.getEveKitTransactionsHistory()));
		routingNode.setAttribute("journalhistory", String.valueOf(settings.getEveKitJournalHistory()));
		routingNode.setAttribute("marketordershistory", String.valueOf(settings.getEveKitMarketOrdersHistory()));
		routingNode.setAttribute("industryjobshistory", String.valueOf(settings.getEveKitIndustryJobsHistory()));
		routingNode.setAttribute("contractshistory", String.valueOf(settings.getEveKitContractsHistory()));
	}

	private void writeRoutingSettings(Document xmldoc, RoutingSettings routingSettings) {
		Element routingNode = xmldoc.createElementNS(null, "routingsettings");
		xmldoc.getDocumentElement().appendChild(routingNode);
		routingNode.setAttribute("securitymaximum", String.valueOf(routingSettings.getSecMax()));
		routingNode.setAttribute("securityminimum", String.valueOf(routingSettings.getSecMin()));
		for (long systemID : routingSettings.getAvoid().keySet()) {
			Element systemNode = xmldoc.createElementNS(null, "routingsystem");
			systemNode.setAttributeNS(null, "id", String.valueOf(systemID));
			routingNode.appendChild(systemNode);
		}
		for (Map.Entry<String, Set<Long>> entry : routingSettings.getPresets().entrySet()) {
			Element presetNode = xmldoc.createElementNS(null, "routingpreset");
			presetNode.setAttributeNS(null, "name", entry.getKey());
			routingNode.appendChild(presetNode);
			for (Long systemID : entry.getValue()) {
				Element systemNode = xmldoc.createElementNS(null, "presetsystem");
				systemNode.setAttributeNS(null, "id", String.valueOf(systemID));
				presetNode.appendChild(systemNode);
			}
		}
		for (Map.Entry<String, RouteResult> entry : routingSettings.getRoutes().entrySet()) {
			Element routeNode = xmldoc.createElementNS(null, "route");
			RouteResult routeResult = entry.getValue();
			routeNode.setAttributeNS(null, "name", entry.getKey());
			routeNode.setAttributeNS(null, "algorithmname", routeResult.getAlgorithmName());
			routeNode.setAttributeNS(null, "algorithmtime", String.valueOf(routeResult.getAlgorithmTime()));
			routeNode.setAttributeNS(null, "jumps", String.valueOf(routeResult.getJumps()));
			routeNode.setAttributeNS(null, "waypoints", String.valueOf(routeResult.getWaypoints()));
			routingNode.appendChild(routeNode);
			for (List<SolarSystem> systems : routeResult.getRoute()) {
				Element systemsNode = xmldoc.createElementNS(null, "routesystems");
				for (SolarSystem system : systems) {
					Element systemNode = xmldoc.createElementNS(null, "routesystem");
					systemNode.setAttributeNS(null, "systemid", String.valueOf(system.getSystemID()));
					systemsNode.appendChild(systemNode);
				}
				List<SolarSystem> stations = routeResult.getStations().get(systems.get(0).getSystemID());
				if (stations != null) {
					for (SolarSystem station : stations) {
						Element stationNode = xmldoc.createElementNS(null, "routestation");
						stationNode.setAttributeNS(null, "stationid", String.valueOf(station.getLocationID()));
						systemsNode.appendChild(stationNode);
					}
				}
				routeNode.appendChild(systemsNode);
			}
		}
	}

	private void writeTags(Document xmldoc, Map<String, Tag> tags) {
		Element tagsNode = xmldoc.createElementNS(null, "tags");
		xmldoc.getDocumentElement().appendChild(tagsNode);
		for (Tag tag : tags.values()) {
			Element tagNode = xmldoc.createElementNS(null, "tag");
			tagNode.setAttributeNS(null, "name", tag.getName());
			tagNode.setAttributeNS(null, "background", tag.getColor().getBackgroundHtml());
			tagNode.setAttributeNS(null, "foreground", tag.getColor().getForegroundHtml());
			tagsNode.appendChild(tagNode);
			for (TagID tagID : tag.getIDs()) {
				Element tagIdNode = xmldoc.createElementNS(null, "tagid");
				tagIdNode.setAttributeNS(null, "tool", tagID.getTool());
				tagIdNode.setAttributeNS(null, "id", String.valueOf(tagID.getID()));
				tagNode.appendChild(tagIdNode);
			}
		}
	}

	private void writeOwners(final Document xmldoc, final Map<Long, String> owners) {
		Element trackerDataNode = xmldoc.createElementNS(null, "owners");
		xmldoc.getDocumentElement().appendChild(trackerDataNode);
		for (Map.Entry<Long, String> entry : owners.entrySet()) {
			Element ownerNode = xmldoc.createElementNS(null, "owner");
			ownerNode.setAttributeNS(null, "name", entry.getValue());
			ownerNode.setAttributeNS(null, "id", String.valueOf(entry.getKey()));
			trackerDataNode.appendChild(ownerNode);
		}
	}

	private void writeTrackerFilters(final Document xmldoc, final Map<String, Boolean> trackerFilters, boolean selectNew) {
		Element trackerDataNode = xmldoc.createElementNS(null, "trackerfilters");
		xmldoc.getDocumentElement().appendChild(trackerDataNode);
		trackerDataNode.setAttributeNS(null, "selectnew", String.valueOf(selectNew));
		for (Map.Entry<String, Boolean> entry : trackerFilters.entrySet()) {
			Element ownerNode = xmldoc.createElementNS(null, "trackerfilter");
			ownerNode.setAttributeNS(null, "id", entry.getKey());
			ownerNode.setAttributeNS(null, "selected", String.valueOf(entry.getValue()));
			trackerDataNode.appendChild(ownerNode);
		}
	}

	private void writeTrackerData(final Document xmldoc, final Map<String, List<Value>> trackerData) {
		Element trackerDataNode = xmldoc.createElementNS(null, "trackerdata");
		xmldoc.getDocumentElement().appendChild(trackerDataNode);
		for (Map.Entry<String, List<Value>> entry : trackerData.entrySet()) {
			Element ownerNode = xmldoc.createElementNS(null, "owner");
			ownerNode.setAttributeNS(null, "name", entry.getKey());
			trackerDataNode.appendChild(ownerNode);
			for (Value value : entry.getValue()) {
				Element dataNode = xmldoc.createElementNS(null, "data");
				dataNode.setAttributeNS(null, "date", String.valueOf(value.getDate().getTime()));
				dataNode.setAttributeNS(null, "assets", String.valueOf(value.getAssetsTotal()));
				dataNode.setAttributeNS(null, "escrows", String.valueOf(value.getEscrows()));
				dataNode.setAttributeNS(null, "escrowstocover", String.valueOf(value.getEscrowsToCover()));
				dataNode.setAttributeNS(null, "sellorders", String.valueOf(value.getSellOrders()));
				dataNode.setAttributeNS(null, "walletbalance", String.valueOf(value.getBalanceTotal()));
				dataNode.setAttributeNS(null, "manufacturing", String.valueOf(value.getManufacturing()));
				dataNode.setAttributeNS(null, "contractcollateral", String.valueOf(value.getContractCollateral()));
				dataNode.setAttributeNS(null, "contractvalue", String.valueOf(value.getContractValue()));
				ownerNode.appendChild(dataNode);
				for (Map.Entry<String, Double> balanceEntry : value.getBalanceFilter().entrySet()) {
					Element balanceNode = xmldoc.createElementNS(null, "balance");
					balanceNode.setAttributeNS(null, "id", balanceEntry.getKey());
					balanceNode.setAttributeNS(null, "value", String.valueOf(balanceEntry.getValue()));
					dataNode.appendChild(balanceNode);
				}
				for (Map.Entry<AssetValue, Double> assetEntry : value.getAssetsFilter().entrySet()) {
					Element assetNode = xmldoc.createElementNS(null, "asset");
					assetNode.setAttributeNS(null, "location", assetEntry.getKey().getLocation());
					if (assetEntry.getKey().getLocationID() != null) {
						assetNode.setAttributeNS(null, "locationid", String.valueOf(assetEntry.getKey().getLocationID()));
					}
					if (assetEntry.getKey().getFlag() != null) {
						assetNode.setAttributeNS(null, "flag", assetEntry.getKey().getFlag());
					}
					assetNode.setAttributeNS(null, "value", String.valueOf(assetEntry.getValue()));
					dataNode.appendChild(assetNode);
				}
			}
		}
	}

	private void writeTrackerNotes(final Document xmldoc, final Map<TrackerDate, TrackerNote> trackerNotes) {
		Element notesNode = xmldoc.createElementNS(null, "trackernotes");
		xmldoc.getDocumentElement().appendChild(notesNode);
		for (Map.Entry<TrackerDate, TrackerNote> entry : trackerNotes.entrySet()) {
			Element noteNode = xmldoc.createElementNS(null, "trackernote");
			noteNode.setAttributeNS(null, "note", entry.getValue().getNote());
			noteNode.setAttributeNS(null, "date", String.valueOf(entry.getKey().getDate().getTime()));
			notesNode.appendChild(noteNode);
		}
	}

	private void writeTableFilters(final Document xmldoc, final Map<String, Map<String, List<Filter>>> tableFilters) {
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

	private void writeTableColumns(final Document xmldoc, final Map<String, List<SimpleColumn>> tableColumns) {
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

	private void writeTableColumnsWidth(final Document xmldoc, final Map<String, Map<String, Integer>> tableColumnsWidth) {
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

	private void writeTablesResize(final Document xmldoc, final Map<String, ResizeMode> tableColumns) {
		Element tablecolumnsNode = xmldoc.createElementNS(null, "tableresize");
		xmldoc.getDocumentElement().appendChild(tablecolumnsNode);
		for (Map.Entry<String, ResizeMode> entry : tableColumns.entrySet()) {
			Element nameNode = xmldoc.createElementNS(null, "table");
			nameNode.setAttributeNS(null, "name", entry.getKey());
			nameNode.setAttributeNS(null, "resize", entry.getValue().name());
			tablecolumnsNode.appendChild(nameNode);
		}
	}

	private void writeTablesViews(final Document xmldoc, final Map<String, Map<String ,View>> tableViews) {
		Element tableviewsNode = xmldoc.createElementNS(null, "tableviews");
		xmldoc.getDocumentElement().appendChild(tableviewsNode);
		for (Map.Entry<String, Map<String ,View>> entry : tableViews.entrySet()) {
			Element nameNode = xmldoc.createElementNS(null, "viewtool");
			nameNode.setAttributeNS(null, "tool", entry.getKey());
			tableviewsNode.appendChild(nameNode);
			for (View view : entry.getValue().values()) {
				Element tableviewNode = xmldoc.createElementNS(null, "view");
				tableviewNode.setAttributeNS(null, "name", view.getName());
				nameNode.appendChild(tableviewNode);
				for (SimpleColumn column : view.getColumns()) {
					Element viewColumnNode = xmldoc.createElementNS(null, "viewcolumn");
					viewColumnNode.setAttributeNS(null, "name", column.getEnumName());
					viewColumnNode.setAttributeNS(null, "shown", String.valueOf(column.isShown()));
					tableviewNode.appendChild(viewColumnNode);
				}
			}
			
		}
	}

	private void writeAssetSettings(final Document xmldoc, final Settings settings) {
		Element parentNode = xmldoc.createElementNS(null, "assetsettings");
		xmldoc.getDocumentElement().appendChild(parentNode);
		parentNode.setAttributeNS(null, "maximumpurchaseage", String.valueOf(settings.getMaximumPurchaseAge()));
	}

	private void writeStockpileGroups(final Document xmldoc, final Settings settings) {
		Element parentNode = xmldoc.createElementNS(null, "stockpilegroups");
		xmldoc.getDocumentElement().appendChild(parentNode);
		parentNode.setAttributeNS(null, "stockpilegroup2", String.valueOf(settings.getStockpileColorGroup2()));
		parentNode.setAttributeNS(null, "stockpilegroup3", String.valueOf(settings.getStockpileColorGroup3()));
	}

	private void writeStockpiles(final Document xmldoc, final List<Stockpile> stockpiles, boolean export) {
		Element parentNode = xmldoc.createElementNS(null, "stockpiles");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Stockpile strockpile : stockpiles) {
			Element strockpileNode = xmldoc.createElementNS(null, "stockpile");
			strockpileNode.setAttributeNS(null, "name", strockpile.getName());
			strockpileNode.setAttributeNS(null, "multiplier", String.valueOf(strockpile.getMultiplier()));
			for (StockpileItem item : strockpile.getItems()) {
				if (item.getItemTypeID() != 0) { //Ignore Total
					Element itemNode = xmldoc.createElementNS(null, "item");
					if (!export) { //Risk of collision, better to generate a new one on import
						itemNode.setAttributeNS(null, "id", String.valueOf(item.getID()));
					}
					itemNode.setAttributeNS(null, "typeid", String.valueOf(item.getItemTypeID()));
					itemNode.setAttributeNS(null, "minimum", String.valueOf(item.getCountMinimum()));
					strockpileNode.appendChild(itemNode);
				}
			}
			
			for (StockpileFilter filter : strockpile.getFilters()) {
				Element locationNode = xmldoc.createElementNS(null, "stockpilefilter");
				locationNode.setAttributeNS(null, "locationid", String.valueOf(filter.getLocation().getLocationID()));
				locationNode.setAttributeNS(null, "sellingcontracts", String.valueOf(filter.isSellingContracts()));
				locationNode.setAttributeNS(null, "soldcontracts", String.valueOf(filter.isSoldContracts()));
				locationNode.setAttributeNS(null, "buyingcontracts", String.valueOf(filter.isBuyingContracts()));
				locationNode.setAttributeNS(null, "boughtcontracts", String.valueOf(filter.isBoughtContracts()));
				locationNode.setAttributeNS(null, "exclude", String.valueOf(filter.isExclude()));
				locationNode.setAttributeNS(null, "inventory", String.valueOf(filter.isAssets()));
				locationNode.setAttributeNS(null, "sellorders", String.valueOf(filter.isSellOrders()));
				locationNode.setAttributeNS(null, "buyorders", String.valueOf(filter.isBuyOrders()));
				locationNode.setAttributeNS(null, "buytransactions", String.valueOf(filter.isBuyTransactions()));
				locationNode.setAttributeNS(null, "selltransactions", String.valueOf(filter.isSellTransactions()));
				locationNode.setAttributeNS(null, "jobs", String.valueOf(filter.isJobs()));
				strockpileNode.appendChild(locationNode);
				for (Long ownerID : filter.getOwnerIDs()) {
					Element ownerNode = xmldoc.createElementNS(null, "owner");
					ownerNode.setAttributeNS(null, "ownerid", String.valueOf(ownerID));
					locationNode.appendChild(ownerNode);
				}
				for (String container : filter.getContainers()) {
					Element containerNode = xmldoc.createElementNS(null, "container");
					containerNode.setAttributeNS(null, "container", container);
					locationNode.appendChild(containerNode);
				}
				for (Integer flagID : filter.getFlagIDs()) {
					Element flagNode = xmldoc.createElementNS(null, "flag");
					flagNode.setAttributeNS(null, "flagid", String.valueOf(flagID));
					locationNode.appendChild(flagNode);
				}
			}
			parentNode.appendChild(strockpileNode);
		}
	}

	private void writeOverviewGroups(final Document xmldoc, final Map<String, OverviewGroup> overviewGroups) {
		Element parentNode = xmldoc.createElementNS(null, "overview");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<String, OverviewGroup> entry : overviewGroups.entrySet()) {
			OverviewGroup overviewGroup = entry.getValue();
			Element node = xmldoc.createElementNS(null, "group");
			node.setAttributeNS(null, "name", overviewGroup.getName());
			parentNode.appendChild(node);
			for (OverviewLocation location : overviewGroup.getLocations()) {
				Element nodeLocation = xmldoc.createElementNS(null, "location");
				nodeLocation.setAttributeNS(null, "name", location.getName());
				nodeLocation.setAttributeNS(null, "type", location.getType().name());
				node.appendChild(nodeLocation);
			}
		}
	}

	private void writeUserItemNames(final Document xmldoc, final Map<Long, UserItem<Long, String>> userPrices) {
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

	private void writeEveNames(final Document xmldoc, final Map<Long, String> eveNames) {
		Element parentNode = xmldoc.createElementNS(null, "evenames");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<Long, String> entry : eveNames.entrySet()) {
			Element node = xmldoc.createElementNS(null, "evename");
			node.setAttributeNS(null, "name", entry.getValue());
			node.setAttributeNS(null, "itemid", String.valueOf(entry.getKey()));
			parentNode.appendChild(node);
		}
	}

	private void writeReprocessSettings(final Document xmldoc, final ReprocessSettings reprocessSettings) {
		Element parentNode = xmldoc.createElementNS(null, "reprocessing");
		xmldoc.getDocumentElement().appendChild(parentNode);
		parentNode.setAttributeNS(null, "refining", String.valueOf(reprocessSettings.getReprocessingLevel()));
		parentNode.setAttributeNS(null, "efficiency", String.valueOf(reprocessSettings.getReprocessingEfficiencyLevel()));
		parentNode.setAttributeNS(null, "processing", String.valueOf(reprocessSettings.getScrapmetalProcessingLevel()));
		parentNode.setAttributeNS(null, "station", String.valueOf(reprocessSettings.getStation()));
	}

	private void writeWindow(final Document xmldoc, final Settings settings) {
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

	private void writeUserPrices(final Document xmldoc, final Map<Integer, UserItem<Integer, Double>> userPrices) {
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
	private void writePriceDataSettings(final Document xmldoc, final PriceDataSettings priceDataSettings) {
		Element parentNode = xmldoc.createElementNS(null, "marketstat");
		parentNode.setAttributeNS(null, "defaultprice", priceDataSettings.getPriceType().name());
		parentNode.setAttributeNS(null, "defaultreprocessedprice", priceDataSettings.getPriceReprocessedType().name());
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

	private void writeFlags(final Document xmldoc, final Map<SettingFlag, Boolean> flags) {
		Element parentNode = xmldoc.createElementNS(null, "flags");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<SettingFlag, Boolean> entry : flags.entrySet()) {
			Element node = xmldoc.createElementNS(null, "flag");
			node.setAttributeNS(null, "key", entry.getKey().name());
			node.setAttributeNS(null, "enabled", String.valueOf(entry.getValue()));
			parentNode.appendChild(node);
		}
	}

	private void writeUpdates(final Document xmldoc, final Settings settings) {
		Element parentNode = xmldoc.createElementNS(null, "updates");
		xmldoc.getDocumentElement().appendChild(parentNode);

		Element node;

		node = xmldoc.createElementNS(null, "update");
		node.setAttributeNS(null, "name", "conquerable station");
		node.setAttributeNS(null, "nextupdate", String.valueOf(settings.getConquerableStationsNextUpdate().getTime()));
		parentNode.appendChild(node);
	}

	private void writeApiProxy(final Document xmldoc, final String apiProxy) {
		if (apiProxy != null) {
			Element node = xmldoc.createElementNS(null, "apiProxy");
			node.setAttributeNS(null, "url", String.valueOf(apiProxy));
			xmldoc.getDocumentElement().appendChild(node);
		}
	}

	private void writeProxy(final Document xmldoc, final ProxyData proxy) {
		if (proxy.getType() != Proxy.Type.DIRECT) { // Only adds proxy tag if there is anything to save... (To prevent an error when the proxy tag doesn't have any attributes)
			Element node = xmldoc.createElementNS(null, "proxy");
			node.setAttributeNS(null, "address", proxy.getAddress());
			node.setAttributeNS(null, "port", String.valueOf(proxy.getPort()));
			node.setAttributeNS(null, "type", proxy.getType().name());
			if (proxy.isAuth()) {
				node.setAttributeNS(null, "username", proxy.getUsername());
				node.setAttributeNS(null, "password", proxy.getPassword());
			}
			xmldoc.getDocumentElement().appendChild(node);
		}
	}

	private void writeExportSettings(final Document xmldoc, final ExportSettings exportSettings) {
		Element node = xmldoc.createElementNS(null, "csvexport");
		xmldoc.getDocumentElement().appendChild(node);
		//CSV
		node.setAttributeNS(null, "decimal", exportSettings.getDecimalSeparator().name());
		node.setAttributeNS(null, "field", exportSettings.getFieldDelimiter().name());
		node.setAttributeNS(null, "line", exportSettings.getLineDelimiter().name());
		//SQL
		node.setAttributeNS(null, "sqlcreatetable", String.valueOf(exportSettings.isCreateTable()));
		node.setAttributeNS(null, "sqldroptable", String.valueOf(exportSettings.isDropTable()));
		node.setAttributeNS(null, "sqlextendedinserts", String.valueOf(exportSettings.isExtendedInserts()));
		//Html
		node.setAttributeNS(null, "htmlstyled", String.valueOf(exportSettings.isHtmlStyled()));
		node.setAttributeNS(null, "htmligb", String.valueOf(exportSettings.isHtmlIGB()));
		node.setAttributeNS(null, "htmlrepeatheader", String.valueOf(exportSettings.getHtmlRepeatHeader()));
		for (Map.Entry<String, String> entry : exportSettings.getTableNames().entrySet()) {
			Element nameNode = xmldoc.createElementNS(null, "sqltablenames");
			nameNode.setAttributeNS(null, "tool", entry.getKey());
			nameNode.setAttributeNS(null, "tablename", entry.getValue());
			node.appendChild(nameNode);
		}
		//Shared
		node.setAttributeNS(null, "exportformat", exportSettings.getExportFormat().name());
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

	private void writeAssetAdded(Document xmldoc, Map<Long, Date> assetAdded) {
		Element assetaddedNode = xmldoc.createElementNS(null, "assetadded");
		xmldoc.getDocumentElement().appendChild(assetaddedNode);
		for (Map.Entry<Long, Date> entry : assetAdded.entrySet()) {
			Element assetNode = xmldoc.createElementNS(null, "asset");
			assetNode.setAttributeNS(null, "itemid", String.valueOf(entry.getKey()));
			assetNode.setAttributeNS(null, "date", String.valueOf(entry.getValue().getTime()));
			assetaddedNode.appendChild(assetNode);
		}
	}
}
