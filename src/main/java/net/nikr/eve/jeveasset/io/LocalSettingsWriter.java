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

package net.nikr.eve.jeveasset.io;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.MarketstatSettings;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserPrice;
import net.nikr.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class LocalSettingsWriter extends AbstractXmlWriter {

	public static void save(Settings settings){
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("settings");
		} catch (XmlException ex) {
			Log.error("Settings not saved "+ex.getMessage(), ex);
		}
		writeBpos(xmldoc, settings.getBpos());
		writeProxy(xmldoc, settings.getProxy());
		writeApiProxy(xmldoc, settings.getApiProxy());
		writeMarketstatSettings(xmldoc, settings.getMarketstatSettings());
		writeFlags(xmldoc, settings.getFlags());
		writeUserPrices(xmldoc, settings.getUserPrices());
		writeColumns(xmldoc, settings.getTableColumnNames(), settings.getTableColumnVisible());
		writeUpdates(xmldoc, settings);
		writeFilters(xmldoc, settings.getAssetFilters());
		try {
			writeXmlFile(xmldoc, Settings.getPathSettings());
		} catch (XmlException ex) {
			Log.error("Settings not saved "+ex.getMessage(), ex);
		}
		Log.info("Settings saved");
	}
	private static void writeBpos(Document xmldoc, List<Integer> bpos){
		Element parentNode = xmldoc.createElementNS(null, "bpos");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (int a = 0; a < bpos.size(); a++){
			int id = bpos.get(a);
			Element node = xmldoc.createElementNS(null, "bpo");
			node.setAttributeNS(null, "id", String.valueOf(id));
			parentNode.appendChild(node);
		}
	}
	private static void writeUserPrices(Document xmldoc, Map<Integer, UserPrice> userPrices){
		Element parentNode = xmldoc.createElementNS(null, "userprices");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Map.Entry<Integer, UserPrice> entry : userPrices.entrySet()){
			UserPrice userPrice = entry.getValue();
			Element node = xmldoc.createElementNS(null, "userprice");
			node.setAttributeNS(null, "name", userPrice.getName());
			node.setAttributeNS(null, "price", String.valueOf(userPrice.getPrice()));
			node.setAttributeNS(null, "typeid", String.valueOf(userPrice.getTypeID()));
			parentNode.appendChild(node);
		}

	}
	private static void writeMarketstatSettings(Document xmldoc, MarketstatSettings marketstatSettings){
		Element parentNode = xmldoc.createElementNS(null, "marketstat");
		parentNode.setAttributeNS(null, "age", String.valueOf(marketstatSettings.getAge()));
		parentNode.setAttributeNS(null, "quantity", String.valueOf(marketstatSettings.getQuantity()));
		parentNode.setAttributeNS(null, "region", String.valueOf(marketstatSettings.getRegion()));
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

	private static void writeColumns(Document xmldoc, List<String> mainTableColumnNames, List<String> mainTableColumnVisible){
		Element parentNode = xmldoc.createElementNS(null, "columns");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (int a = 0; a < mainTableColumnNames.size(); a++){
			String column = mainTableColumnNames.get(a);
			boolean visible = mainTableColumnVisible.contains(column);
			Element node = xmldoc.createElementNS(null, "column");
			node.setAttributeNS(null, "name", column);
			node.setAttributeNS(null, "visible", String.valueOf(visible));
			parentNode.appendChild(node);
		}
	}

	private static void writeUpdates(Document xmldoc, Settings settings){
		Element parentNode = xmldoc.createElementNS(null, "updates");
		xmldoc.getDocumentElement().appendChild(parentNode);

		Element node;

		node = xmldoc.createElementNS(null, "update");
		node.setAttributeNS(null, "name", "marketstats");
		node.setAttributeNS(null, "nextupdate", String.valueOf(settings.getMarketstatsNextUpdate().getTime()));
		parentNode.appendChild(node);

		node = xmldoc.createElementNS(null, "update");
		node.setAttributeNS(null, "name", "conquerable station");
		node.setAttributeNS(null, "nextupdate", String.valueOf(settings.getConquerableStationsNextUpdate().getTime()));
		parentNode.appendChild(node);

		Map<Long, Date> corporationNextUpdate = settings.getCorporationsNextUpdate();
		for (Map.Entry<Long, Date> entry : corporationNextUpdate.entrySet()){
			node = xmldoc.createElementNS(null, "update");
			node.setAttributeNS(null, "name", "corporation");
			node.setAttributeNS(null, "corpid", String.valueOf(entry.getKey()));
			node.setAttributeNS(null, "nextupdate", String.valueOf(entry.getValue().getTime()));
			parentNode.appendChild(node);
		}
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
				childNode.setAttributeNS(null, "mode", assetFilter.getMode());
				childNode.setAttributeNS(null, "and", String.valueOf(assetFilter.isAnd()));
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
