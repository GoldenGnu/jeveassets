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

import java.io.File;
import java.net.Proxy;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderRange;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.CopySettings;
import net.nikr.eve.jeveasset.data.settings.ExportSettings;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings;
import net.nikr.eve.jeveasset.data.settings.MarketOrdersSettings;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings;
import net.nikr.eve.jeveasset.data.settings.ProxyData;
import net.nikr.eve.jeveasset.data.settings.ReprocessSettings;
import net.nikr.eve.jeveasset.data.settings.RouteResult;
import net.nikr.eve.jeveasset.data.settings.RoutingSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.Settings.SettingFlag;
import net.nikr.eve.jeveasset.data.settings.TrackerData;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.data.settings.tag.Tag;
import net.nikr.eve.jeveasset.data.settings.tag.TagID;
import net.nikr.eve.jeveasset.gui.dialogs.settings.SoundsSettingsPanel.SoundOption;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog.Formula;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps.Jump;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.sounds.Sound;
import net.nikr.eve.jeveasset.gui.tabs.orders.Outbid;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileContainer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileFlag;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerDate;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerNote;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerSkillPointFilter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class SettingsWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(SettingsWriter.class);

	private SettingsWriter() { }

	public static boolean save(final Settings settings, final String filename) {
		if (!new File(FileUtil.getPathTrackerData()).exists()) { //Make sure the tracker data is saved
			TrackerData.save("Saving Settings", true);
		}
		SettingsWriter writer = new SettingsWriter();
		return writer.write(settings, filename);
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

		writeStockpiles(xmldoc, stockpiles, new HashMap<>(), true);
		try {
			writeXmlFile(xmldoc, filename, false);
		} catch (XmlException ex) {
			LOG.error("Stockpile not saved " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Stockpile saved");
		return true;
	}

	public static boolean saveRoutes(final Map<String, RouteResult> routes, final String filename) {
		SettingsWriter writer = new SettingsWriter();
		return writer.writeRoutes(routes, filename);
	}

	private boolean writeRoutes(final Map<String, RouteResult> routes, final String filename) {
		Document xmldoc;
		try {
			xmldoc = getXmlDocument("settings");
		} catch (XmlException ex) {
			LOG.error("Stockpile not saved " + ex.getMessage(), ex);
			return false;
		}
		Element routingNode = xmldoc.createElementNS(null, "routingsettings");
		xmldoc.getDocumentElement().appendChild(routingNode);
		writeRoutes(xmldoc, routingNode, routes);
		try {
			writeXmlFile(xmldoc, filename, false);
		} catch (XmlException ex) {
			LOG.error("Stockpile not saved " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Stockpile saved");
		return true;
	}

	private boolean write(final Settings settings, final String filename) {
		Document xmldoc;
		try {
			xmldoc = getXmlDocument("settings");
		} catch (XmlException ex) {
			LOG.error("Settings not saved " + ex.getMessage(), ex);
			return false;
		}
		//Add version number
		setAttribute(xmldoc.getDocumentElement(), "version", SettingsReader.SETTINGS_VERSION);

		writeAssetSettings(xmldoc, settings);
		writeStockpileGroups(xmldoc, settings);
		writeStockpiles(xmldoc, settings.getStockpiles(), settings.getStockpileGroupSettings().getStockpileGroups(), false);
		writeOverviewGroups(xmldoc, settings.getOverviewGroups());
		writeReprocessSettings(xmldoc, settings.getReprocessSettings());
		writeWindow(xmldoc, settings);
		writeProxy(xmldoc, settings.getProxyData());
		writePriceDataSettings(xmldoc, settings.getPriceDataSettings());
		writeFlags(xmldoc, settings.getFlags());
		writeUserPrices(xmldoc, settings.getUserPrices());
		writeUserItemNames(xmldoc, settings.getUserItemNames());
		writeEveNames(xmldoc, settings.getEveNames());
		writeTableFilters(xmldoc, settings.getTableFilters());
		writeCurrentTableFilters(xmldoc, settings.getCurrentTableFilters(), settings.getCurrentTableFiltersShown());
		writeTableColumns(xmldoc, settings.getTableColumns());
		writeTableColumnsWidth(xmldoc, settings.getTableColumnsWidth());
		writeTableResize(xmldoc, settings.getTableResize());
		writeTableViews(xmldoc, settings.getTableViews());
		writeTableJumps(xmldoc, settings.getTableJumps());
		writeTableFormulas(xmldoc, settings.getTableFormulas());
		writeTableChanges(xmldoc, settings.getTableChanged());
		writeExportSettings(xmldoc, settings.getExportSettings(), settings.getCopySettings());
		writeTrackerNotes(xmldoc, settings.getTrackerSettings().getNotes());
		writeTrackerFilters(xmldoc, settings.getTrackerSettings().getFilters(), settings.getTrackerSettings().isSelectNew(), settings.getTrackerSettings().getSkillPointFilters());
		writeTrackerSettings(xmldoc, settings);
		writeOwners(xmldoc, settings.getOwners(), settings.getOwnersNextUpdate());
		writeTags(xmldoc, settings.getTags());
		writeRoutingSettings(xmldoc, settings.getRoutingSettings());
		writeMarketOrderOutbid(xmldoc, settings.getPublicMarketOrdersNextUpdate(), settings.getPublicMarketOrdersLastUpdate(), settings.getOutbidOrderRange(), settings.getMarketOrdersOutbid());
		writeMarketOrdersSettings(xmldoc, settings.getMarketOrdersSettings());
		writeShowTool(xmldoc, settings.getShowTools(), settings.isSaveToolsOnExit());
		writeColorSettings(xmldoc, settings.getColorSettings());
		writeSoundSettings(xmldoc, settings.getSoundSettings());
		writeFactionWarfareSystemOwners(xmldoc, settings);
		writePriceHistorySettings(xmldoc, settings);
		writeManufacturingPriceSettings(xmldoc, settings.getManufacturingSettings());
		try {
			writeXmlFile(xmldoc, filename, true);
		} catch (XmlException ex) {
			LOG.error("Settings not saved " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Settings saved");
		return true;
	}

	private void writeManufacturingPriceSettings(Document xmldoc, ManufacturingSettings settings) {
		Element manufacturingNode = xmldoc.createElementNS(null, "manufacturing");
		setAttributeOptional(manufacturingNode, "nextupdate", settings.getNextUpdate());
		setAttributeOptional(manufacturingNode, "facility", settings.getFacility());
		setAttributeOptional(manufacturingNode, "rigs", settings.getRigs());
		setAttributeOptional(manufacturingNode, "security", settings.getSecurity());
		setAttributeOptional(manufacturingNode, "systemid", settings.getSystemID());
		setAttributeOptional(manufacturingNode, "me", settings.getMaterialEfficiency());
		setAttributeOptional(manufacturingNode, "tax", settings.getTax());
		xmldoc.getDocumentElement().appendChild(manufacturingNode);
		for (Map.Entry<Integer, Double> entry : settings.getPrices().entrySet()) {
			Element priceNode = xmldoc.createElementNS(null, "price");
			setAttribute(priceNode, "typeid", entry.getKey());
			setAttributeOptional(priceNode, "price", entry.getValue());
			manufacturingNode.appendChild(priceNode);
		}
		for (Map.Entry<Integer, Float> entry : settings.getSystems().entrySet()) {
			Element systemNode = xmldoc.createElementNS(null, "system");
			setAttribute(systemNode, "systemid", entry.getKey());
			setAttributeOptional(systemNode, "index", entry.getValue());
			manufacturingNode.appendChild(systemNode);
		}
	}

	private void writePriceHistorySettings(Document xmldoc, Settings settings) {
		Element priceHistoryNode = xmldoc.createElementNS(null, "pricehistory");
		xmldoc.getDocumentElement().appendChild(priceHistoryNode);
		for (Map.Entry<String, Set<Integer>> entry : settings.getPriceHistorySets().entrySet()) {
			Element setNode = xmldoc.createElementNS(null, "set");
			setAttribute(setNode, "name", entry.getKey());
			setAttributeOptional(setNode, "ids", entry.getValue());
			priceHistoryNode.appendChild(setNode);
		}
	}

	private void writeFactionWarfareSystemOwners(Document xmldoc, Settings settings) {
		Element factionWarfareSystemOwnersNode = xmldoc.createElementNS(null, "factionwarfaresystemowners");
		xmldoc.getDocumentElement().appendChild(factionWarfareSystemOwnersNode);
		setAttribute(factionWarfareSystemOwnersNode, "factionwarfarenextupdate", settings.getFactionWarfareNextUpdate());
		for (Map.Entry<Long, String> entry : settings.getFactionWarfareSystemOwners().entrySet()) {
			Element systemNode = xmldoc.createElementNS(null, "system");
			setAttribute(systemNode, "system", entry.getKey());
			setAttributeOptional(systemNode, "faction", entry.getValue());
			factionWarfareSystemOwnersNode.appendChild(systemNode);
		}
	}

	private void writeColorSettings(Document xmldoc, ColorSettings colorSettings) {
		Element colorSettingsNode = xmldoc.createElementNS(null, "colorsettings");
		xmldoc.getDocumentElement().appendChild(colorSettingsNode);
		setAttributeOptional(colorSettingsNode, "theme", colorSettings.getColorTheme().getType());
		setAttribute(colorSettingsNode, "lookandfeel", colorSettings.getLookAndFeelClass());
		for (ColorEntry colorEntry : ColorEntry.values()) {
			Element colorNode = xmldoc.createElementNS(null, "color");
			setAttribute(colorNode, "name", colorEntry);
			setAttributeOptional(colorNode, "background", colorSettings.getBackground(colorEntry));
			setAttributeOptional(colorNode, "foreground", colorSettings.getForeground(colorEntry));
			colorSettingsNode.appendChild(colorNode);
		}
	}

	private void writeSoundSettings(Document xmldoc, Map<SoundOption, Sound> soundSettings) {
		Element soundSettingsNode = xmldoc.createElementNS(null, "soundsettings");
		xmldoc.getDocumentElement().appendChild(soundSettingsNode);
		for (Map.Entry<SoundOption, Sound> entry : soundSettings.entrySet()) {
			Element soundNode = xmldoc.createElementNS(null, "sound");
			setAttribute(soundNode, "option", entry.getKey());
			setAttribute(soundNode, "sound", entry.getValue().getID());
			soundSettingsNode.appendChild(soundNode);
		}
	}

	private void writeShowTool(Document xmldoc, List<String> showTools, boolean saveToolsOnExit) {
		Element showToolsNode = xmldoc.createElementNS(null, "showtools");
		xmldoc.getDocumentElement().appendChild(showToolsNode);
		setAttribute(showToolsNode, "saveonexit", saveToolsOnExit);
		setAttribute(showToolsNode, "show", showTools);
	}

	private void writeMarketOrderOutbid(Document xmldoc, Date publicMarketOrdersNextUpdate, Date publicMarketOrdersLastUpdate, MarketOrderRange outbidOrderRange, Map<Long, Outbid> marketOrdersOutbid) {
		Element marketOrderOutbidNode = xmldoc.createElementNS(null, "marketorderoutbid");
		xmldoc.getDocumentElement().appendChild(marketOrderOutbidNode);
		setAttribute(marketOrderOutbidNode, "nextupdate", publicMarketOrdersNextUpdate);
		setAttributeOptional(marketOrderOutbidNode, "lastupdate", publicMarketOrdersLastUpdate);
		setAttribute(marketOrderOutbidNode, "outbidorderrange", outbidOrderRange);
		for (Map.Entry<Long, Outbid> entry : marketOrdersOutbid.entrySet()) {
			Element outbidNode = xmldoc.createElementNS(null, "outbid");
			setAttribute(outbidNode, "id", entry.getKey());
			setAttribute(outbidNode, "price", entry.getValue().getPrice());
			setAttribute(outbidNode, "count", entry.getValue().getCount());
			marketOrderOutbidNode.appendChild(outbidNode);
		}
	}

	private void writeRoutingSettings(Document xmldoc, RoutingSettings routingSettings) {
		Element routingNode = xmldoc.createElementNS(null, "routingsettings");
		xmldoc.getDocumentElement().appendChild(routingNode);
		setAttribute(routingNode, "securitymaximum", routingSettings.getSecMax());
		setAttribute(routingNode, "securityminimum", routingSettings.getSecMin());
		for (long systemID : routingSettings.getAvoid().keySet()) {
			Element systemNode = xmldoc.createElementNS(null, "routingsystem");
			setAttribute(systemNode, "id", systemID);
			routingNode.appendChild(systemNode);
		}
		for (Map.Entry<String, Set<Long>> entry : routingSettings.getPresets().entrySet()) {
			Element presetNode = xmldoc.createElementNS(null, "routingpreset");
			setAttribute(presetNode, "name", entry.getKey());
			routingNode.appendChild(presetNode);
			for (Long systemID : entry.getValue()) {
				Element systemNode = xmldoc.createElementNS(null, "presetsystem");
				setAttribute(systemNode, "id", systemID);
				presetNode.appendChild(systemNode);
			}
		}
		writeRoutes(xmldoc, routingNode, routingSettings.getRoutes());
	}

	private void writeRoutes(Document xmldoc, Element routingNode, Map<String, RouteResult> routes) {
		for (Map.Entry<String, RouteResult> entry : routes.entrySet()) {
			Element routeNode = xmldoc.createElementNS(null, "route");
			RouteResult routeResult = entry.getValue();
			setAttribute(routeNode, "name", entry.getKey());
			setAttribute(routeNode, "waypoints", routeResult.getWaypoints());
			setAttribute(routeNode, "algorithmname", routeResult.getAlgorithmName());
			setAttribute(routeNode, "algorithmtime",routeResult.getAlgorithmTime());
			setAttribute(routeNode, "jumps", routeResult.getJumps());
			setAttribute(routeNode, "avoid", routeResult.getAvoid());
			setAttribute(routeNode, "security", routeResult.getSecurity());
			routingNode.appendChild(routeNode);
			for (List<SolarSystem> systems : routeResult.getRoute()) {
				Element systemsNode = xmldoc.createElementNS(null, "routesystems");
				for (SolarSystem system : systems) {
					Element systemNode = xmldoc.createElementNS(null, "routesystem");
					setAttribute(systemNode, "systemid", system.getSystemID());
					systemsNode.appendChild(systemNode);
				}
				List<SolarSystem> stations = routeResult.getStations().get(systems.get(0).getSystemID());
				if (stations != null) {
					for (SolarSystem station : stations) {
						Element stationNode = xmldoc.createElementNS(null, "routestation");
						setAttribute(stationNode, "stationid", station.getLocationID());
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
			setAttribute(tagNode, "name", tag.getName());
			setAttribute(tagNode, "background", tag.getColor().getBackgroundHtml());
			setAttribute(tagNode, "foreground", tag.getColor().getForegroundHtml());
			tagsNode.appendChild(tagNode);
			for (TagID tagID : tag.getIDs()) {
				Element tagIdNode = xmldoc.createElementNS(null, "tagid");
				setAttribute(tagIdNode, "tool", tagID.getTool());
				setAttribute(tagIdNode, "id", tagID.getID());
				tagNode.appendChild(tagIdNode);
			}
		}
	}

	private void writeOwners(final Document xmldoc, final Map<Long, String> owners, final Map<Long, Date> ownersNextUpdate) {
		Element trackerDataNode = xmldoc.createElementNS(null, "owners");
		xmldoc.getDocumentElement().appendChild(trackerDataNode);
		for (Map.Entry<Long, String> entry : owners.entrySet()) {
			Element ownerNode = xmldoc.createElementNS(null, "owner");
			setAttribute(ownerNode, "name", entry.getValue());
			setAttribute(ownerNode, "id", entry.getKey());
			setAttributeOptional(ownerNode, "date", ownersNextUpdate.get(entry.getKey()));
			trackerDataNode.appendChild(ownerNode);
		}
	}

	private void writeTrackerFilters(final Document xmldoc, final Map<String, Boolean> trackerFilters, boolean selectNew, Map<String, TrackerSkillPointFilter> trackerSkillPointFilters) {
		Element trackerDataNode = xmldoc.createElementNS(null, "trackerfilters");
		xmldoc.getDocumentElement().appendChild(trackerDataNode);
		setAttribute(trackerDataNode, "selectnew", selectNew);
		for (Map.Entry<String, Boolean> entry : trackerFilters.entrySet()) {
			Element ownerNode = xmldoc.createElementNS(null, "trackerfilter");
			setAttribute(ownerNode, "id", entry.getKey());
			setAttribute(ownerNode, "selected", entry.getValue());
			trackerDataNode.appendChild(ownerNode);
		}
		for (Map.Entry<String, TrackerSkillPointFilter> entry : trackerSkillPointFilters.entrySet()) {
			Element ownerNode = xmldoc.createElementNS(null, "skillpointfilters");
			TrackerSkillPointFilter filter = entry.getValue();
			setAttribute(ownerNode, "id", entry.getKey());
			setAttribute(ownerNode, "selected", filter.isEnabled());
			setAttribute(ownerNode, "mimimum", filter.getMinimum());
			trackerDataNode.appendChild(ownerNode);
		}
	}

	private void writeTrackerSettings(final Document xmldoc, Settings settings) {
		Element trackerSettingsNode = xmldoc.createElementNS(null, "trackersettings");
		xmldoc.getDocumentElement().appendChild(trackerSettingsNode);
		setAttribute(trackerSettingsNode, "allprofiles", settings.getTrackerSettings().isAllProfiles());
		setAttribute(trackerSettingsNode, "charactercorporations", settings.getTrackerSettings().isCharacterCorporations());
		setAttributeOptional(trackerSettingsNode, "selectedowners", settings.getTrackerSettings().getSelectedOwners());
		setAttributeOptional(trackerSettingsNode, "fromdate", settings.getTrackerSettings().getFromDate());
		setAttributeOptional(trackerSettingsNode, "todate", settings.getTrackerSettings().getToDate());
		setAttribute(trackerSettingsNode, "displaytype", settings.getTrackerSettings().getDisplayType());
		setAttribute(trackerSettingsNode, "includezero", settings.getTrackerSettings().isIncludeZero());
		setAttribute(trackerSettingsNode, "showoptions", settings.getTrackerSettings().getShowOptions());
	}

	private void writeTrackerNotes(final Document xmldoc, final Map<TrackerDate, TrackerNote> trackerNotes) {
		Element notesNode = xmldoc.createElementNS(null, "trackernotes");
		xmldoc.getDocumentElement().appendChild(notesNode);
		for (Map.Entry<TrackerDate, TrackerNote> entry : trackerNotes.entrySet()) {
			Element noteNode = xmldoc.createElementNS(null, "trackernote");
			setAttribute(noteNode, "note", entry.getValue().getNote());
			setAttribute(noteNode, "date", entry.getKey().getDate());
			notesNode.appendChild(noteNode);
		}
	}

	/***
	 * Write setting for table filters to the xml settings document 'tablefilters' element.
	 *
	 * @param xmldoc Settings document to write to.
	 * @param tableFilters Saved filters to be written to the document zero to many for each table.
	 */
	private void writeTableFilters(final Document xmldoc, final Map<String, Map<String, List<Filter>>> tableFilters) {
		Element tableFiltersNode = xmldoc.createElementNS(null, "tablefilters");
		xmldoc.getDocumentElement().appendChild(tableFiltersNode);
		for (Map.Entry<String, Map<String, List<Filter>>> entry : tableFilters.entrySet()) {
			Element tableNode = xmldoc.createElementNS(null, "table");
			setAttribute(tableNode, "name", entry.getKey());
			tableFiltersNode.appendChild(tableNode);
			for (Map.Entry<String, List<Filter>> filters : entry.getValue().entrySet()) {
				Element filterNode = xmldoc.createElementNS(null, "filter");
				setAttribute(filterNode, "name", filters.getKey());
				tableNode.appendChild(filterNode);
				writeFilters(xmldoc, filterNode, filters);
			}
		}
	}

	/***
	 * Write setting for current table filters to the xml settings document 'currnettablefilters' element.
	 *
	 * @param xmldoc Settings document to write to.
	 * @param tableFilters Current filters to be written to the document one per table.
	 * @param tableFiltersShow Current filters visibility state to be written to the document one per table.
	 */
	private void writeCurrentTableFilters(final Document xmldoc, final Map<String, List<Filter>> tableFilters, final Map<String, Boolean> tableFiltersShow) {
		Element currentTableFiltersNode = xmldoc.createElementNS(null, "currenttablefilters");
		xmldoc.getDocumentElement().appendChild(currentTableFiltersNode);
		for (Map.Entry<String, List<Filter>> filters : tableFilters.entrySet()) {
			Element tableNode = xmldoc.createElementNS(null, "table");
			setAttribute(tableNode, "name", filters.getKey());
			currentTableFiltersNode.appendChild(tableNode);
			Element filterNode = xmldoc.createElementNS(null, "filter");
			setAttribute(filterNode, "show", tableFiltersShow.getOrDefault(filters.getKey(), true));
			tableNode.appendChild(filterNode);
			writeFilters(xmldoc, filterNode, filters);
		}
	}

	/***
	 * Write settings for individual filters rows to the xml settings document.
	 *
	 * @param xmldoc Settings document to write to.
	 * @param parentNode Node of the xml document to write the filter to.
	 * @param filters Filter to be written to the document row by row.
	 */
	private void writeFilters(final Document xmldoc, final Element parentNode, final Map.Entry<String, List<Filter>> filters) {
		for (Filter filter : filters.getValue()) {
			Element rowNode = xmldoc.createElementNS(null, "row");
			setAttribute(rowNode, "group", filter.getGroup());
			setAttribute(rowNode, "text", filter.getText());
			setAttribute(rowNode, "column", filter.getColumn().name());
			setAttribute(rowNode, "compare", filter.getCompareType());
			setAttribute(rowNode, "logic", filter.getLogic());
			setAttribute(rowNode, "enabled", filter.isEnabled());
			parentNode.appendChild(rowNode);
		}
	}

	private void writeTableColumns(final Document xmldoc, final Map<String, List<SimpleColumn>> tableColumns) {
		Element tableColumnsNode = xmldoc.createElementNS(null, "tablecolumns");
		xmldoc.getDocumentElement().appendChild(tableColumnsNode);
		for (Map.Entry<String, List<SimpleColumn>> entry : tableColumns.entrySet()) {
			Element tableNode = xmldoc.createElementNS(null, "table");
			setAttribute(tableNode, "name", entry.getKey());
			tableColumnsNode.appendChild(tableNode);
			for (SimpleColumn column : entry.getValue()) {
				Element columnNode = xmldoc.createElementNS(null, "column");
				setAttribute(columnNode, "name", column.getEnumName());
				setAttribute(columnNode, "shown", column.isShown());
				tableNode.appendChild(columnNode);
			}
		}
	}

	private void writeTableColumnsWidth(final Document xmldoc, final Map<String, Map<String, Integer>> tableColumnsWidth) {
		Element tableColumnsWidthNode = xmldoc.createElementNS(null, "tablecolumnswidth");
		xmldoc.getDocumentElement().appendChild(tableColumnsWidthNode);
		for (Map.Entry<String, Map<String, Integer>> table : tableColumnsWidth.entrySet()) {
			Element tableNode = xmldoc.createElementNS(null, "table");
			setAttribute(tableNode, "name", table.getKey());
			tableColumnsWidthNode.appendChild(tableNode);
			for (Map.Entry<String, Integer> column : table.getValue().entrySet()) {
				Element columnNode = xmldoc.createElementNS(null, "column");
				setAttribute(columnNode, "column", column.getKey());
				setAttribute(columnNode, "width", column.getValue());
				tableNode.appendChild(columnNode);
			}
		}
	}

	private void writeTableResize(final Document xmldoc, final Map<String, ResizeMode> tableResize) {
		Element tableResizeNode = xmldoc.createElementNS(null, "tableresize");
		xmldoc.getDocumentElement().appendChild(tableResizeNode);
		for (Map.Entry<String, ResizeMode> entry : tableResize.entrySet()) {
			Element tableNode = xmldoc.createElementNS(null, "table");
			setAttribute(tableNode, "name", entry.getKey());
			setAttribute(tableNode, "resize", entry.getValue());
			tableResizeNode.appendChild(tableNode);
		}
	}

	private void writeTableViews(final Document xmldoc, final Map<String, Map<String ,View>> tableViews) {
		Element tableViewsNode = xmldoc.createElementNS(null, "tableviews");
		xmldoc.getDocumentElement().appendChild(tableViewsNode);
		for (Map.Entry<String, Map<String ,View>> entry : tableViews.entrySet()) {
			Element viewToolNode = xmldoc.createElementNS(null, "viewtool");
			setAttribute(viewToolNode, "tool", entry.getKey());
			tableViewsNode.appendChild(viewToolNode);
			for (View view : entry.getValue().values()) {
				Element viewNode = xmldoc.createElementNS(null, "view");
				setAttribute(viewNode, "name", view.getName());
				viewToolNode.appendChild(viewNode);
				for (SimpleColumn column : view.getColumns()) {
					Element viewColumnNode = xmldoc.createElementNS(null, "viewcolumn");
					setAttribute(viewColumnNode, "name", column.getEnumName());
					setAttribute(viewColumnNode, "shown", column.isShown());
					viewNode.appendChild(viewColumnNode);
				}
			}
		}
	}

	private void writeTableFormulas(final Document xmldoc, final Map<String, List<Formula>> formulas) {
		Element tableFormulasNode = xmldoc.createElementNS(null, "tableformulas");
		xmldoc.getDocumentElement().appendChild(tableFormulasNode);
		for (Map.Entry<String, List<Formula>> entry : formulas.entrySet()) {
			Element formulasNode = xmldoc.createElementNS(null, "formulas");
			setAttribute(formulasNode, "tool", entry.getKey());
			tableFormulasNode.appendChild(formulasNode);
			for (Formula formula : entry.getValue()) {
				Element formulaNode = xmldoc.createElementNS(null, "formula");
				setAttribute(formulaNode, "name", formula.getColumnName());
				setAttribute(formulaNode, "expression", formula.getOriginalExpression());
				setAttributeOptional(formulaNode, "index", formula.getIndex());
				formulasNode.appendChild(formulaNode);
			}
		}
	}

	private void writeTableChanges(final Document xmldoc, final Map<String, Date> changes) {
		Element tableChangesNode = xmldoc.createElementNS(null, "tablechanges");
		xmldoc.getDocumentElement().appendChild(tableChangesNode);
		for (Map.Entry<String, Date> entry : changes.entrySet()) {
			Element changesNode = xmldoc.createElementNS(null, "changes");
			setAttribute(changesNode, "tool", entry.getKey());
			setAttribute(changesNode, "date", entry.getValue());
			tableChangesNode.appendChild(changesNode);
		}
	}

	private void writeTableJumps(final Document xmldoc, final Map<String, List<Jump>> jumps) {
		Element tableJumpsNode = xmldoc.createElementNS(null, "tablejumps");
		xmldoc.getDocumentElement().appendChild(tableJumpsNode);
		for (Map.Entry<String, List<Jump>> entry : jumps.entrySet()) {
			Element jumpsNode = xmldoc.createElementNS(null, "jumps");
			setAttribute(jumpsNode, "tool", entry.getKey());
			tableJumpsNode.appendChild(jumpsNode);
			for (Jump jump : entry.getValue()) {
				Element jumpNode = xmldoc.createElementNS(null, "jump");
				setAttribute(jumpNode, "systemid", jump.getSystemID());
				setAttributeOptional(jumpNode, "index", jump.getIndex());
				jumpsNode.appendChild(jumpNode);
			}
		}
	}

	private void writeAssetSettings(final Document xmldoc, final Settings settings) {
		Element parentNode = xmldoc.createElementNS(null, "assetsettings");
		xmldoc.getDocumentElement().appendChild(parentNode);
		setAttribute(parentNode, "maximumpurchaseage", settings.getMaximumPurchaseAge());
		setAttribute(parentNode, "transactionprofitprice", settings.getTransactionProfitPrice());
		setAttribute(parentNode, "transactionprofitmargin", settings.getTransactionProfitMargin());
	}

	private void writeStockpileGroups(final Document xmldoc, final Settings settings) {
		Element parentNode = xmldoc.createElementNS(null, "stockpilegroups");
		xmldoc.getDocumentElement().appendChild(parentNode);
		setAttribute(parentNode, "stockpilegroup2", settings.getStockpileColorGroup2());
		setAttribute(parentNode, "stockpilegroup3", settings.getStockpileColorGroup3());
	}

	/**
	 * -!- `!´ IMPORTANT `!´ -!-
	 * StockpileDataWriter and StockpileDataReader needs to be updated too - on any changes!!!
	 */
	private void writeStockpiles(final Document xmldoc, final List<Stockpile> stockpiles, Map<Stockpile, String> groups, boolean export) {
		Element parentNode = xmldoc.createElementNS(null, "stockpiles");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Stockpile stockpile : stockpiles) {
			//STOCKPILE
			Element stockpileNode = xmldoc.createElementNS(null, "stockpile");
			setAttribute(stockpileNode, "name", stockpile.getName());
			if (!export) { //Risk of collision, better to generate a new one on import
				setAttribute(stockpileNode, "id", stockpile.getStockpileID());
			}
			setAttribute(stockpileNode, "multiplier", stockpile.getMultiplier());
			String group = groups.get(stockpile);
			if (group != null && !group.isEmpty()) {
				setAttribute(stockpileNode, "stockpilegroup", group);
			}
			setAttribute(stockpileNode, "contractsmatchall", stockpile.isContractsMatchAll());
			//ITEMS
			for (StockpileItem item : stockpile.getItems()) {
				if (item.isTotal()) {
					continue; //Ignore Total
				}
				Element itemNode = xmldoc.createElementNS(null, "item");
				if (!export) { //Risk of collision, better to generate a new one on import
					setAttribute(itemNode, "id", item.getID());
				}
				setAttribute(itemNode, "typeid", item.getItemTypeID());
				setAttribute(itemNode, "minimum", item.getCountMinimum());
				setAttribute(itemNode, "runs", item.isRuns());
				stockpileNode.appendChild(itemNode);
			}
			//SUBPILES
			for (Map.Entry<Stockpile, Double> entry : stockpile.getSubpiles().entrySet()) {
				Element subpileNode = xmldoc.createElementNS(null, "subpile");
				subpileNode.setAttributeNS(null, "name", entry.getKey().getName());
				subpileNode.setAttributeNS(null, "minimum", String.valueOf(entry.getValue()));
				stockpileNode.appendChild(subpileNode);
			}
			//FILTERS
			for (StockpileFilter filter : stockpile.getFilters()) {
				Element filterNode = xmldoc.createElementNS(null, "stockpilefilter");
				setAttribute(filterNode, "locationid", filter.getLocation().getLocationID());
				setAttribute(filterNode, "sellingcontracts", filter.isSellingContracts());
				setAttribute(filterNode, "soldcontracts", filter.isSoldContracts());
				setAttribute(filterNode, "buyingcontracts", filter.isBuyingContracts());
				setAttribute(filterNode, "boughtcontracts", filter.isBoughtContracts());
				setAttribute(filterNode, "exclude", filter.isExclude());
				setAttributeOptional(filterNode, "singleton", filter.isSingleton());
				setAttributeOptional(filterNode, "jobsdaysless", filter.getJobsDaysLess());
				setAttributeOptional(filterNode, "jobsdaysmore", filter.getJobsDaysMore());
				setAttribute(filterNode, "inventory", filter.isAssets());
				setAttribute(filterNode, "sellorders", filter.isSellOrders());
				setAttribute(filterNode, "buyorders", filter.isBuyOrders());
				setAttribute(filterNode, "buytransactions", filter.isBuyTransactions());
				setAttribute(filterNode, "selltransactions", filter.isSellTransactions());
				setAttribute(filterNode, "jobs", filter.isJobs());
				stockpileNode.appendChild(filterNode);
				for (Long ownerID : filter.getOwnerIDs()) {
					Element ownerNode = xmldoc.createElementNS(null, "owner");
					setAttribute(ownerNode, "ownerid", ownerID);
					filterNode.appendChild(ownerNode);
				}
				for (StockpileContainer container : filter.getContainers()) {
					Element containerNode = xmldoc.createElementNS(null, "container");
					setAttribute(containerNode, "container", container.getContainer());
					setAttribute(containerNode, "includecontainer", container.isIncludeSubs());
					filterNode.appendChild(containerNode);
				}
				for (StockpileFlag flag : filter.getFlags()) {
					Element flagNode = xmldoc.createElementNS(null, "flag");
					setAttribute(flagNode, "flagid", flag.getFlagID());
					setAttribute(flagNode, "includecontainer", flag.isIncludeSubs());
					filterNode.appendChild(flagNode);
				}
			}
			parentNode.appendChild(stockpileNode);
		}
	}

	private void writeOverviewGroups(final Document xmldoc, final Map<String, OverviewGroup> overviewGroups) {
		Element parentNode = xmldoc.createElementNS(null, "overview");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<String, OverviewGroup> entry : overviewGroups.entrySet()) {
			OverviewGroup overviewGroup = entry.getValue();
			Element node = xmldoc.createElementNS(null, "group");
			setAttribute(node, "name", overviewGroup.getName());
			parentNode.appendChild(node);
			for (OverviewLocation location : overviewGroup.getLocations()) {
				Element nodeLocation = xmldoc.createElementNS(null, "location");
				setAttribute(nodeLocation, "name", location.getName());
				setAttribute(nodeLocation, "type", location.getType());
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
			setAttribute(node, "name", userItemName.getValue());
			setAttribute(node, "typename", userItemName.getName());
			setAttribute(node, "itemid", userItemName.getKey());
			parentNode.appendChild(node);
		}
	}

	private void writeEveNames(final Document xmldoc, final Map<Long, String> eveNames) {
		Element parentNode = xmldoc.createElementNS(null, "evenames");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<Long, String> entry : eveNames.entrySet()) {
			Element node = xmldoc.createElementNS(null, "evename");
			setAttribute(node, "name", entry.getValue());
			setAttribute(node, "itemid", entry.getKey());
			parentNode.appendChild(node);
		}
	}

	private void writeReprocessSettings(final Document xmldoc, final ReprocessSettings reprocessSettings) {
		Element parentNode = xmldoc.createElementNS(null, "reprocessing");
		xmldoc.getDocumentElement().appendChild(parentNode);
		setAttribute(parentNode, "refining", reprocessSettings.getReprocessingLevel());
		setAttribute(parentNode, "efficiency", reprocessSettings.getReprocessingEfficiencyLevel());
		setAttribute(parentNode, "ore", reprocessSettings.getOreProcessingLevel());
		setAttribute(parentNode, "scrapmetal", reprocessSettings.getScrapmetalProcessingLevel());
		setAttribute(parentNode, "station", reprocessSettings.getStation());
	}

	private void writeWindow(final Document xmldoc, final Settings settings) {
		Element parentNode = xmldoc.createElementNS(null, "window");
		xmldoc.getDocumentElement().appendChild(parentNode);
		setAttribute(parentNode, "x", settings.getWindowLocation().x);
		setAttribute(parentNode, "y", settings.getWindowLocation().y);
		setAttribute(parentNode, "height", settings.getWindowSize().height);
		setAttribute(parentNode, "width", settings.getWindowSize().width);
		setAttribute(parentNode, "maximized", settings.isWindowMaximized());
		setAttribute(parentNode, "autosave", settings.isWindowAutoSave());
		setAttribute(parentNode, "alwaysontop", settings.isWindowAlwaysOnTop());
	}

	private void writeUserPrices(final Document xmldoc, final Map<Integer, UserItem<Integer, Double>> userPrices) {
		Element parentNode = xmldoc.createElementNS(null, "userprices");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<Integer, UserItem<Integer, Double>> entry : userPrices.entrySet()) {
			UserItem<Integer, Double> userPrice = entry.getValue();
			Element node = xmldoc.createElementNS(null, "userprice");
			setAttribute(node, "name", userPrice.getName());
			setAttribute(node, "price", userPrice.getValue());
			setAttribute(node, "typeid", userPrice.getKey());
			parentNode.appendChild(node);
		}
	}

	private void writePriceDataSettings(final Document xmldoc, final PriceDataSettings priceDataSettings) {
		Element parentNode = xmldoc.createElementNS(null, "marketstat");
		setAttribute(parentNode, "defaultprice", priceDataSettings.getPriceType());
		setAttribute(parentNode, "defaultreprocessedprice", priceDataSettings.getPriceReprocessedType());
		setAttribute(parentNode, "defaultmanufacturingprice", priceDataSettings.getPriceReprocessedType());
		setAttribute(parentNode, "pricesource", priceDataSettings.getSource());
		setAttribute(parentNode, "locationid", priceDataSettings.getLocationID());
		setAttribute(parentNode, "type", priceDataSettings.getLocationType());
		setAttributeOptional(parentNode, "janicekey", priceDataSettings.getJaniceKey());
		xmldoc.getDocumentElement().appendChild(parentNode);
	}

	private void writeMarketOrdersSettings(final Document xmldoc, final MarketOrdersSettings marketOrdersSettings) {
		Element parentNode = xmldoc.createElementNS(null, "marketorderssettings");
		setAttribute(parentNode, "expirewarndays", marketOrdersSettings.getExpireWarnDays());
		setAttribute(parentNode, "remainingwarnpercent", marketOrdersSettings.getRemainingWarnPercent());
		xmldoc.getDocumentElement().appendChild(parentNode);
	}

	private void writeFlags(final Document xmldoc, final Map<SettingFlag, Boolean> flags) {
		Element parentNode = xmldoc.createElementNS(null, "flags");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<SettingFlag, Boolean> entry : flags.entrySet()) {
			Element node = xmldoc.createElementNS(null, "flag");
			setAttribute(node, "key", entry.getKey());
			setAttribute(node, "enabled", entry.getValue());
			parentNode.appendChild(node);
		}
	}

	private void writeProxy(final Document xmldoc, final ProxyData proxy) {
		if (proxy.getType() != Proxy.Type.DIRECT) { // Only adds proxy tag if there is anything to save... (To prevent an error when the proxy tag doesn't have any attributes)
			Element node = xmldoc.createElementNS(null, "proxy");
			setAttribute(node, "address", proxy.getAddress());
			setAttribute(node, "port", proxy.getPort());
			setAttribute(node, "type", proxy.getType());
			if (proxy.isAuth()) {
				setAttribute(node, "username", proxy.getUsername());
				setAttribute(node, "password", proxy.getPassword());
			}
			xmldoc.getDocumentElement().appendChild(node);
		}
	}

	private void writeExportSettings(final Document xmldoc, final Map<String, ExportSettings> exportSettings, final CopySettings copySettings) {
		Element node = xmldoc.createElementNS(null, "exports");
		//Copy
		setAttribute(node, "copy", copySettings.getCopyDecimalSeparator());

		for(Map.Entry<String, ExportSettings> exportSetting : exportSettings.entrySet()) {
			//Common
			Element exportNode = xmldoc.createElementNS(null, "export");
			setAttribute(exportNode, "name", exportSetting.getKey());
			setAttributeOptional(exportNode, "exportformat", exportSetting.getValue().getExportFormat());
			setAttributeOptional(exportNode, "filename", exportSetting.getValue().getFilename());
			setAttributeOptional(exportNode, "columnselection", exportSetting.getValue().getColumnSelection());
			setAttributeOptional(exportNode, "viewname", exportSetting.getValue().getViewName());
			setAttributeOptional(exportNode, "filterselection", exportSetting.getValue().getFilterSelection());
			setAttributeOptional(exportNode, "filtername", exportSetting.getValue().getFilterName());
			node.appendChild(exportNode);

			if (!exportSetting.getValue().getTableExportColumns().isEmpty()) {
				Element tableNode = xmldoc.createElementNS(null, "table");
				for (String column : exportSetting.getValue().getTableExportColumns()) {
					Element columnNode = xmldoc.createElementNS(null, "column");
					setAttribute(columnNode, "name", column);
					tableNode.appendChild(columnNode);
				}
				exportNode.appendChild(tableNode);
			}

			//CSV
			Element csvNode = xmldoc.createElementNS(null, "csv");
			setAttribute(csvNode, "decimal", exportSetting.getValue().getDecimalSeparator());
			setAttribute(csvNode, "line", exportSetting.getValue().getCsvLineDelimiter());
			exportNode.appendChild(csvNode);

			//SQL
			Element sqlNode = xmldoc.createElementNS(null, "sql");
			setAttribute(sqlNode, "tablename", exportSetting.getValue().getSqlTableName());
			setAttribute(sqlNode, "createtable", exportSetting.getValue().isSqlCreateTable());
			setAttribute(sqlNode, "droptable", exportSetting.getValue().isSqlDropTable());
			setAttribute(sqlNode, "extendedinserts", exportSetting.getValue().isSqlExtendedInserts());
			exportNode.appendChild(sqlNode);

			//Html
			Element htmlNode = xmldoc.createElementNS(null, "html");
			setAttribute(htmlNode, "styled", exportSetting.getValue().isHtmlStyled());
			setAttribute(htmlNode, "igb", exportSetting.getValue().isHtmlIGB());
			setAttribute(htmlNode, "repeatheader", exportSetting.getValue().getHtmlRepeatHeader());
			exportNode.appendChild(htmlNode);

		}
		xmldoc.getDocumentElement().appendChild(node);
	}
}
