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

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.MarketstatSettings;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserPrice;
import net.nikr.log.Log;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class LocalSettingsReader extends AbstractXmlReader {

	public static boolean load(Settings settings){
		try {
			Element element = getDocumentElement(Settings.getPathSettings());
			parseSettings(element, settings);
		} catch (IOException ex) {
			Log.info("Settings not loaded");
			return false;
		} catch (XmlException ex) {
			Log.error("Settings not loaded: ("+Settings.getPathSettings()+")"+ex.getMessage(), ex);
		}
		Log.info("Settings loaded");
		return true;
	}

	private static void parseSettings(Element element, Settings settings) throws XmlException {
		if (!element.getNodeName().equals("settings")) {
			throw new XmlException("Wrong root element name.");
		}
		//Window
		NodeList windowNodes = element.getElementsByTagName("window");
		if (windowNodes.getLength() == 1){
			Element windowElement = (Element) windowNodes.item(0);
			parseWindow(windowElement, settings);
		}


		//BPOs
		NodeList bposNodes = element.getElementsByTagName("bpos");
		if (bposNodes.getLength() == 1){
			Element bposElement = (Element) bposNodes.item(0);
			parseBposPrices(bposElement, settings);
		}

		//UserPrices
		NodeList userPriceNodes = element.getElementsByTagName("userprices");
		if (userPriceNodes.getLength() == 1){
			Element userPriceElement = (Element) userPriceNodes.item(0);
			parseUserPrices(userPriceElement, settings);
		}

		//MarketstatSettings
		NodeList marketstatSettingsNodes = element.getElementsByTagName("marketstat");
		if (marketstatSettingsNodes.getLength() == 1){
			Element marketstatSettingsElement = (Element) marketstatSettingsNodes.item(0);
			parseMarketstatSettings(marketstatSettingsElement, settings);
		}
		

		//Flags
		NodeList flagNodes = element.getElementsByTagName("flags");
		if (flagNodes.getLength() != 1){
			throw new XmlException("Wrong flag element count.");
		}
		Element flagsElement = (Element) flagNodes.item(0);
		parseFlags(flagsElement, settings);

		//Columns
		NodeList columnNodes = element.getElementsByTagName("columns");
		if (columnNodes.getLength() != 1){
			throw new XmlException("Wrong columns element count.");
		}
		Element columnsElement = (Element) columnNodes.item(0);
		parseColumns(columnsElement, settings);

		//Updates
		NodeList updateNodes = element.getElementsByTagName("updates");
		if (updateNodes.getLength() != 1){
			throw new XmlException("Wrong updates element count.");
		}
		Element updatesElement = (Element) updateNodes.item(0);
		parseUpdates(updatesElement, settings);

		//Filters
		NodeList filterNodes = element.getElementsByTagName("filters");
		if (filterNodes.getLength() != 1){
			throw new XmlException("Wrong filters element count.");
		}
		Element filtersElement = (Element) filterNodes.item(0);
		parseFilters(filtersElement, settings.getAssetFilters());
		
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

	private static void parseWindow(Element windowElement, Settings settings) throws XmlException {
		int x = AttributeGetters.getInt(windowElement, "x");
		int y = AttributeGetters.getInt(windowElement, "y");
		int height = AttributeGetters.getInt(windowElement, "height");
		int width = AttributeGetters.getInt(windowElement, "width");
		boolean maximized = AttributeGetters.getBoolean(windowElement, "maximized");
		boolean autosave = AttributeGetters.getBoolean(windowElement, "autosave");
		settings.setWindowLocation( new Point(x, y));
		settings.setWindowSize( new Dimension(width, height));
		settings.setWindowMaximized(maximized);
		settings.setWindowAutoSave(autosave);
	}

	private static void parseProxy(Element proxyElement, Settings settings) throws XmlException {
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

	private static void parseBposPrices(Element element, Settings settings){
		NodeList userPriceNodes = element.getElementsByTagName("bpo");
		for (int a = 0; a < userPriceNodes.getLength(); a++){
			Element currentNode = (Element) userPriceNodes.item(a);
			int id = AttributeGetters.getInt(currentNode, "id");
			settings.getBpos().add(id);
		}
	}
	private static void parseUserPrices(Element element, Settings settings){
		NodeList userPriceNodes = element.getElementsByTagName("userprice");
		for (int a = 0; a < userPriceNodes.getLength(); a++){
			Element currentNode = (Element) userPriceNodes.item(a);
			String name = AttributeGetters.getString(currentNode, "name");
			double price = AttributeGetters.getDouble(currentNode, "price");
			int typeID = AttributeGetters.getInt(currentNode, "typeid");
			UserPrice userPrice = new UserPrice(price, typeID, name);
			settings.getUserPrices().put(typeID, userPrice);
		}
	}

	private static void parseMarketstatSettings(Element element, Settings settings){
		int age = AttributeGetters.getInt(element, "age");
		int quantity = AttributeGetters.getInt(element, "quantity");
		int region = AttributeGetters.getInt(element, "region");
		String defaultPrice = EveAsset.PRICE_SELL_MEDIAN;
		if (AttributeGetters.haveAttribute(element, "defaultprice")){
			defaultPrice = AttributeGetters.getString(element, "defaultprice");
		}
		settings.setMarketstatSettings( new MarketstatSettings(region, age, quantity, defaultPrice) );
	}

	private static void parseFlags(Element element, Settings settings){
		NodeList flagNodes = element.getElementsByTagName("flag");
		for (int a = 0; a < flagNodes.getLength(); a++){
			Element currentNode = (Element) flagNodes.item(a);
			String key = AttributeGetters.getString(currentNode, "key");
			boolean enabled = AttributeGetters.getBoolean(currentNode, "enabled");
			settings.getFlags().put(key, enabled);
		}
	}

	private static void parseColumns(Element element, Settings settings){
		NodeList columnNodes = element.getElementsByTagName("column");
		List<String> mainTableColumnNames = new Vector<String>();
		List<String> mainTableColumnVisible = new Vector<String>();
		for (int a = 0; a < columnNodes.getLength(); a++){
			Element currentNode = (Element) columnNodes.item(a);
			String name = AttributeGetters.getString(currentNode, "name");
			boolean visible = AttributeGetters.getBoolean(currentNode, "visible");
			mainTableColumnNames.add(name);
			if (visible) mainTableColumnVisible.add(name);
		}
		//Add new columns, at the end... (Might not be the defaut location)
		List<String> mainTableColumnNamesOriginal = settings.getTableColumnNames();
		for (int a = 0; a < mainTableColumnNamesOriginal.size(); a++){
			if (!mainTableColumnNames.contains(mainTableColumnNamesOriginal.get(a))){
				mainTableColumnNames.add(mainTableColumnNamesOriginal.get(a));
				mainTableColumnVisible.add(mainTableColumnNamesOriginal.get(a));
			}
		}

		settings.setTableColumnNames(mainTableColumnNames);
		settings.setTableColumnVisible(mainTableColumnVisible);
	}

	private static void parseUpdates(Element element, Settings settings){
		NodeList updateNodes = element.getElementsByTagName("update");
		for (int a = 0; a < updateNodes.getLength(); a++){
			Element currentNode = (Element) updateNodes.item(a);
			parseUpdate(currentNode, settings);
		}
	}
	private static void parseUpdate(Element element, Settings settings){
		String text = AttributeGetters.getString(element, "name");
		Date nextUpdate = new Date( AttributeGetters.getLong(element, "nextupdate") );
		if (text.equals("conquerable station")){
			settings.setConquerableStationsNextUpdate(nextUpdate);
		}
		if (text.equals("marketstats")){
			settings.setMarketstatsNextUpdate(nextUpdate);
		}
		if (text.equals("corporation")){
			long corpid = AttributeGetters.getLong(element, "corpid");
			settings.getCorporationsNextUpdate().put(corpid, nextUpdate);
		}
	}

	private static void parseFilters(Element element, Map<String, List<AssetFilter>> assetFilters){
		NodeList filterNodes = element.getElementsByTagName("filter");
		for (int a = 0; a < filterNodes.getLength(); a++){
			Element currentNode = (Element) filterNodes.item(a);
			String name = parseFilter(currentNode);
			assetFilters.put(name, parseFilterRows(currentNode));
		}
	}

	private static String parseFilter(Element element){
		return AttributeGetters.getString(element, "name");
	}

	private static List<AssetFilter> parseFilterRows(Element element){
		List<AssetFilter> assetFilters = new Vector<AssetFilter>();
		NodeList rowNodes = element.getElementsByTagName("row");
		for (int a = 0; a < rowNodes.getLength(); a++){
			Element currentNode = (Element) rowNodes.item(a);
			AssetFilter assetFilter = parseAssetFilter(currentNode);
			assetFilters.add(assetFilter);
		}
		return assetFilters;
	}

	private static AssetFilter parseAssetFilter(Element element){
		String text = AttributeGetters.getString(element, "text");
		String column = AttributeGetters.getString(element, "column");
		String mode = AttributeGetters.getString(element, "mode");
		boolean and = AttributeGetters.getBoolean(element, "and");
		String columnMatch = null;
		if (AttributeGetters.haveAttribute(element, "columnmatch")){
			columnMatch = AttributeGetters.getString(element, "columnmatch");
		}
		return new AssetFilter(column, text, mode, and, columnMatch);
	}

	private static void parseApiProxy(Element apiProxyElement, Settings settings) {
		String proxyURL = AttributeGetters.getString(apiProxyElement, "url");
		settings.setApiProxy(proxyURL);
	}
}
