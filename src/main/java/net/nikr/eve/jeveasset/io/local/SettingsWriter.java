/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import java.util.Map.Entry;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.TableSettings;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.OverviewGroup;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.PriceDataSettings;
import net.nikr.eve.jeveasset.data.ReprocessSettings;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlWriter;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class SettingsWriter extends AbstractXmlWriter {

	private final static Logger LOG = LoggerFactory.getLogger(SettingsWriter.class);

	public static void save(Settings settings){
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("settings");
		} catch (XmlException ex) {
			LOG.error("Settings not saved "+ex.getMessage(), ex);
		}
		//Add version number
		xmldoc.getDocumentElement().setAttribute("version", String.valueOf(SettingsReader.SETTINGS_VERSION));

		writePriceFactionData(xmldoc, settings.getPriceFactionData());
		writeOverviewGroups(xmldoc, settings.getOverviewGroups());
		writeReprocessSettings(xmldoc, settings.getReprocessSettings());
		writeWindow(xmldoc, settings);
		writeBpos(xmldoc, settings.getBpos());
		writeProxy(xmldoc, settings.getProxy());
		writeApiProxy(xmldoc, settings.getApiProxy());
		writePriceDataSettings(xmldoc, settings.getPriceDataSettings());
		writeFlags(xmldoc, settings.getFlags());
		writeUserPrices(xmldoc, settings.getUserPrices());
		writeUserItemNames(xmldoc, settings.getUserItemNames());
		writeTableSettings(xmldoc, settings.getTableSettings());
		writeUpdates(xmldoc, settings);
		writeFilters(xmldoc, settings.getAssetFilters());
		try {
			writeXmlFile(xmldoc, Settings.getPathSettings());
		} catch (XmlException ex) {
			LOG.error("Settings not saved "+ex.getMessage(), ex);
		}
		LOG.info("Settings saved");
	}

	private static void writePriceFactionData(Document xmldoc, Map<Integer, PriceData> priceFactionData){
		Element parentNode = xmldoc.createElementNS(null, "factionprices");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<Integer, PriceData> entry : priceFactionData.entrySet()){
			PriceData priceData = entry.getValue();
			Element node = xmldoc.createElementNS(null, "factionprice");
			node.setAttributeNS(null, "typeID", String.valueOf(entry.getKey()));
			node.setAttributeNS(null, "avg", String.valueOf(priceData.getBuyAvg()));
			node.setAttributeNS(null, "median", String.valueOf(priceData.getBuyMedian()));
			node.setAttributeNS(null, "lo", String.valueOf(priceData.getBuyMin()));
			node.setAttributeNS(null, "hi", String.valueOf(priceData.getBuyMax()));
			parentNode.appendChild(node);
		}

	}
	private static void writeOverviewGroups(Document xmldoc, Map<String, OverviewGroup> overviewGroups){
		Element parentNode = xmldoc.createElementNS(null, "overview");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<String, OverviewGroup> entry : overviewGroups.entrySet()){
			OverviewGroup overviewGroup = entry.getValue();
			Element node = xmldoc.createElementNS(null, "group");
			node.setAttributeNS(null, "name", overviewGroup.getName());
			parentNode.appendChild(node);
			for (int a = 0; a < overviewGroup.getLocations().size(); a++){
				Element nodeLocation = xmldoc.createElementNS(null, "location");
				nodeLocation.setAttributeNS(null, "name", overviewGroup.getLocations().get(a).getName());
				nodeLocation.setAttributeNS(null, "type", overviewGroup.getLocations().get(a).getType().name());
				node.appendChild(nodeLocation);
			}
		}
	}

	private static void writeUserItemNames(Document xmldoc, Map<Long, UserItem<Long,String>> userPrices){
		Element parentNode = xmldoc.createElementNS(null, "itemmames");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<Long, UserItem<Long,String>> entry : userPrices.entrySet()){
			UserItem<Long,String> userItemName = entry.getValue();
			Element node = xmldoc.createElementNS(null, "itemname");
			node.setAttributeNS(null, "name", userItemName.getValue());
			node.setAttributeNS(null, "typename", userItemName.getName());
			node.setAttributeNS(null, "itemid", String.valueOf(userItemName.getKey()));
			parentNode.appendChild(node);
		}
	}

	private static void writeReprocessSettings(Document xmldoc, ReprocessSettings reprocessSettings){
		Element parentNode = xmldoc.createElementNS(null, "reprocessing");
		xmldoc.getDocumentElement().appendChild(parentNode);
		parentNode.setAttributeNS(null, "refining", String.valueOf(reprocessSettings.getRefiningLevel()));
		parentNode.setAttributeNS(null, "efficiency", String.valueOf(reprocessSettings.getRefineryEfficiencyLevel()));
		parentNode.setAttributeNS(null, "processing", String.valueOf(reprocessSettings.getScrapmetalProcessingLevel()));
		parentNode.setAttributeNS(null, "station", String.valueOf(reprocessSettings.getStation()));
	}

	private static void writeWindow(Document xmldoc, Settings settings){
		Element parentNode = xmldoc.createElementNS(null, "window");
		xmldoc.getDocumentElement().appendChild(parentNode);
		parentNode.setAttributeNS(null, "x", String.valueOf(settings.getWindowLocation().x));
		parentNode.setAttributeNS(null, "y", String.valueOf(settings.getWindowLocation().y));
		parentNode.setAttributeNS(null, "height", String.valueOf(settings.getWindowSize().height));
		parentNode.setAttributeNS(null, "width", String.valueOf(settings.getWindowSize().width));
		parentNode.setAttributeNS(null, "maximized", String.valueOf(settings.isWindowMaximized()));
		parentNode.setAttributeNS(null, "autosave", String.valueOf(settings.isWindowAutoSave()));
	}


	private static void writeBpos(Document xmldoc, List<Long> bpos){
		Element parentNode = xmldoc.createElementNS(null, "bpos");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (int a = 0; a < bpos.size(); a++){
			long id = bpos.get(a);
			Element node = xmldoc.createElementNS(null, "bpo");
			node.setAttributeNS(null, "id", String.valueOf(id));
			parentNode.appendChild(node);
		}
	}
	private static void writeUserPrices(Document xmldoc, Map<Integer, UserItem<Integer,Double>> userPrices){
		Element parentNode = xmldoc.createElementNS(null, "userprices");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<Integer, UserItem<Integer,Double>> entry : userPrices.entrySet()){
			UserItem<Integer,Double> userPrice = entry.getValue();
			Element node = xmldoc.createElementNS(null, "userprice");
			node.setAttributeNS(null, "name", userPrice.getName());
			node.setAttributeNS(null, "price", String.valueOf(userPrice.getValue()));
			node.setAttributeNS(null, "typeid", String.valueOf(userPrice.getKey()));
			parentNode.appendChild(node);
		}
	}
	private static void writePriceDataSettings(Document xmldoc, PriceDataSettings priceDataSettings){
		Element parentNode = xmldoc.createElementNS(null, "marketstat");
		parentNode.setAttributeNS(null, "region", String.valueOf(priceDataSettings.getRegion()));
		parentNode.setAttributeNS(null, "defaultprice", EveAsset.getPriceType().name());
		parentNode.setAttributeNS(null, "source", priceDataSettings.getSource());
		xmldoc.getDocumentElement().appendChild(parentNode);
	}

	private static void writeFlags(Document xmldoc, Map<String, Boolean> flags){
		Element parentNode = xmldoc.createElementNS(null, "flags");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<String, Boolean> entry : flags.entrySet()){
			Element node = xmldoc.createElementNS(null, "flag");
			node.setAttributeNS(null, "key", entry.getKey());
			node.setAttributeNS(null, "enabled", String.valueOf(entry.getValue()));
			parentNode.appendChild(node);
		}
	}

	public static void writeTableSettings(Document xmldoc, Map<String, TableSettings> tableSettings){
		Element parentNode = xmldoc.createElementNS(null, "tables");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Entry<String, TableSettings> entry : tableSettings.entrySet()){
			Element tableNode = xmldoc.createElementNS(null, "table");
			tableNode.setAttributeNS(null, "name", entry.getKey());
			tableNode.setAttributeNS(null, "resize", entry.getValue().getMode().toString());
			parentNode.appendChild(tableNode);
			for (String column : entry.getValue().getTableColumnNames()){
				boolean visible = entry.getValue().getTableColumnVisible().contains(column);
				Element node = xmldoc.createElementNS(null, "column");
				node.setAttributeNS(null, "name", column);
				node.setAttributeNS(null, "visible", String.valueOf(visible));
				tableNode.appendChild(node);
			}
		}
	}

	private static void writeUpdates(Document xmldoc, Settings settings){
		Element parentNode = xmldoc.createElementNS(null, "updates");
		xmldoc.getDocumentElement().appendChild(parentNode);

		Element node;

		node = xmldoc.createElementNS(null, "update");
		node.setAttributeNS(null, "name", "conquerable station");
		node.setAttributeNS(null, "nextupdate", String.valueOf(settings.getConquerableStationsNextUpdate().getTime()));
		parentNode.appendChild(node);
	}

	private static void writeFilters(Document xmldoc, Map<String, List<AssetFilter>> assetFilters){
		Element parentNode = xmldoc.createElementNS(null, "filters");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<String, List<AssetFilter>> entry : assetFilters.entrySet()){
			Element node = xmldoc.createElementNS(null, "filter");
			node.setAttributeNS(null, "name", entry.getKey());
			parentNode.appendChild(node);

			List<AssetFilter> assetFilterFilters = entry.getValue();
			for (int a = 0; a < assetFilterFilters.size(); a++){
				AssetFilter assetFilter = assetFilterFilters.get(a);

				Element childNode = xmldoc.createElementNS(null, "row");
				childNode.setAttributeNS(null, "text", assetFilter.getText());
				childNode.setAttributeNS(null, "column", assetFilter.getColumn());
				childNode.setAttributeNS(null, "mode", assetFilter.getMode().name());
				childNode.setAttributeNS(null, "and", String.valueOf(assetFilter.isAnd()));
				if (assetFilter.getColumnMatch() != null){
					childNode.setAttributeNS(null, "columnmatch", assetFilter.getColumnMatch());
				}
				node.appendChild(childNode);
			}
		}
	}

	private static void writeApiProxy(Document xmldoc, String apiProxy) {
		if (apiProxy != null) {
			Element node = xmldoc.createElementNS(null, "apiProxy");
			node.setAttributeNS(null, "url", String.valueOf(apiProxy));
			xmldoc.getDocumentElement().appendChild(node);
		}
	}

	private static void writeProxy(Document xmldoc, Proxy proxy) {
		if (proxy != null && !proxy.type().equals(Proxy.Type.DIRECT)) { // Only adds proxy tag if there is anything to save... (To prevent an error when the proxy tag doesn't have any attributes)
			Element node = xmldoc.createElementNS(null, "proxy");
			if (proxy.address() instanceof InetSocketAddress) {
				InetSocketAddress addr = (InetSocketAddress)proxy.address();
				node.setAttributeNS(null, "address", String.valueOf(addr.getHostName()));
				node.setAttributeNS(null, "port", String.valueOf(addr.getPort()));
				node.setAttributeNS(null, "type", String.valueOf(proxy.type()));
			}
			xmldoc.getDocumentElement().appendChild(node);
		}
	}
}
