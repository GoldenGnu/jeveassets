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

import com.beimin.eveapi.balance.ApiAccountBalance;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.MarketstatSettings;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserPrice;
import net.nikr.log.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

		//Accounts
		NodeList accountNodes = element.getElementsByTagName("accounts");
		if (accountNodes.getLength() != 1){
			throw new XmlException("Wrong accounts element count.");
		}

		Element accountsElement = (Element) accountNodes.item(0);
		parseAccounts(accountsElement, settings.getAccounts());

		// Proxy can have 0 or 1 proxy elements; at 0, the proxy stays as null.
		NodeList proxyNodes = element.getElementsByTagName("proxy");
		if (proxyNodes.getLength() == 1) {
			Element proxyElement = (Element) proxyNodes.item(0);
			parseProxy(proxyElement, settings);
		} else if (proxyNodes.getLength() > 1) {
			throw new XmlException("Wrong proxy element count.");
		}
    
	}

	private static void parseProxy(Element proxyElement, Settings settings) throws XmlException {
		String addrName = AttributeGetters.getAttributeString(proxyElement, "address");
		String proxyType = AttributeGetters.getAttributeString(proxyElement, "type");
		Integer port = AttributeGetters.getAttributeInteger(proxyElement, "port");

		// delegate to the utility method in the Settings.
		settings.setProxy(addrName, port, proxyType);
	}

	private static void parseBposPrices(Element element, Settings settings){
		NodeList userPriceNodes = element.getElementsByTagName("bpo");
		for (int a = 0; a < userPriceNodes.getLength(); a++){
			Element currentNode = (Element) userPriceNodes.item(a);
			int id = AttributeGetters.getAttributeInteger(currentNode, "id");
			settings.getBpos().add(id);
		}
	}
	private static void parseUserPrices(Element element, Settings settings){
		NodeList userPriceNodes = element.getElementsByTagName("userprice");
		for (int a = 0; a < userPriceNodes.getLength(); a++){
			Element currentNode = (Element) userPriceNodes.item(a);
			String name = AttributeGetters.getAttributeString(currentNode, "name");
			double price = AttributeGetters.getAttributeDouble(currentNode, "price");
			int typeID = AttributeGetters.getAttributeInteger(currentNode, "typeid");
			UserPrice userPrice = new UserPrice(price, typeID, name);
			settings.getUserPrices().put(typeID, userPrice);
		}
	}

	private static void parseMarketstatSettings(Element element, Settings settings){
		int age = AttributeGetters.getAttributeInteger(element, "age");
		int quantity = AttributeGetters.getAttributeInteger(element, "quantity");
		int region = AttributeGetters.getAttributeInteger(element, "region");
		settings.setMarketstatSettings( new MarketstatSettings(region, age, quantity) );
	}

	private static void parseFlags(Element element, Settings settings){
		NodeList flagNodes = element.getElementsByTagName("flag");
		for (int a = 0; a < flagNodes.getLength(); a++){
			Element currentNode = (Element) flagNodes.item(a);
			String key = AttributeGetters.getAttributeString(currentNode, "key");
			boolean enabled = AttributeGetters.getAttributeBoolean(currentNode, "enabled");
			settings.getFlags().put(key, enabled);
		}
	}


	private static void parseColumns(Element element, Settings settings){
		NodeList columnNodes = element.getElementsByTagName("column");
		List<String> mainTableColumnNames = new Vector<String>();
		List<String> mainTableColumnVisible = new Vector<String>();
		for (int a = 0; a < columnNodes.getLength(); a++){
			Element currentNode = (Element) columnNodes.item(a);
			String name = AttributeGetters.getAttributeString(currentNode, "name");
			boolean visible = AttributeGetters.getAttributeBoolean(currentNode, "visible");
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
		String text = AttributeGetters.getAttributeString(element, "name");
		Date nextUpdate = new Date( AttributeGetters.getAttributeLong(element, "nextupdate") );
		if (text.equals("conquerable station")){
			settings.setConquerableStationsNextUpdate(nextUpdate);
		}
		if (text.equals("marketstats")){
			settings.setMarketstatsNextUpdate(nextUpdate);
		}
		if (text.equals("corporation")){
			long corpid = AttributeGetters.getAttributeLong(element, "corpid");
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
		return AttributeGetters.getAttributeString(element, "name");
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
		String text = AttributeGetters.getAttributeString(element, "text");
		String column = AttributeGetters.getAttributeString(element, "column");
		String mode = AttributeGetters.getAttributeString(element, "mode");
		boolean and = AttributeGetters.getAttributeBoolean(element, "and");
		return new AssetFilter(column, text, mode, and);
	}

	private static void parseAccounts(Element element, List<Account> accounts){
		NodeList accountNodes = element.getElementsByTagName("account");
		for (int a = 0; a < accountNodes.getLength(); a++){
			Element currentNode = (Element) accountNodes.item(a);
			Account account = parseAccount(currentNode);
			parseHumans(currentNode, account);
			accounts.add(account);
		}
	}

	private static Account parseAccount(Node node){
		int userID = AttributeGetters.getAttributeInteger(node, "userid");
		String apiKey = AttributeGetters.getAttributeString(node, "apikey");
		Date nextUpdate = new Date( AttributeGetters.getAttributeLong(node, "charactersnextupdate") );
		return new Account(userID, apiKey, nextUpdate);
	}

	private static void parseHumans(Element element, Account account){
		NodeList humanNodes =  element.getElementsByTagName("human");
		for (int a = 0; a < humanNodes.getLength(); a++){
			Element currentNode = (Element) humanNodes.item(a);
			Human human = parseHuman(currentNode, account);
			account.getHumans().add(human);
			NodeList assetNodes = currentNode.getElementsByTagName("assets");
			if (assetNodes.getLength() == 1) parseAssets(assetNodes.item(0), human.getAssets(), null);
			parseBalances(currentNode, human);
		}
	}
	private static Human parseHuman(Node node, Account account){
		String name = AttributeGetters.getAttributeString(node, "name");
		long characterID = AttributeGetters.getAttributeLong(node, "id");
		String corporation = AttributeGetters.getAttributeString(node, "corporation");
		boolean updateCorporationAssets = AttributeGetters.getAttributeBoolean(node, "corpassets");
		Date assetsNextUpdate = new Date( AttributeGetters.getAttributeLong(node, "assetsnextupdate") );
		Date balanceNextUpdate = new Date( AttributeGetters.getAttributeLong(node, "balancenextupdate") );
		boolean showAssets = true;
		if (AttributeGetters.haveAttribute(node, "show")){
			showAssets = AttributeGetters.getAttributeBoolean(node, "show");
		}
		return new Human(account, name, characterID, corporation, updateCorporationAssets, showAssets, assetsNextUpdate, balanceNextUpdate);
	}

	private static void parseBalances(Element element, Human human){
		NodeList balancesNodes = element.getElementsByTagName("balances");
		for (int a = 0; a < balancesNodes.getLength(); a++){
			Element currentBalancesNode = (Element) balancesNodes.item(a);
			boolean bCorp = AttributeGetters.getAttributeBoolean(currentBalancesNode, "corp");
			NodeList balanceNodes = currentBalancesNode.getElementsByTagName("balance");
			
			for (int b = 0; b < balanceNodes.getLength(); b++){
				Element currentNode = (Element) balanceNodes.item(b);
				ApiAccountBalance AccountBalance = parseBalance(currentNode);
				if (bCorp){
					human.getCorporationAccountBalances().add(AccountBalance);
				} else {
					human.getAccountBalances().add(AccountBalance);
				}

			}
		}
	}
	private static ApiAccountBalance parseBalance(Element element){
		ApiAccountBalance accountBalance = new ApiAccountBalance();
		int accountID = AttributeGetters.getAttributeInteger(element, "accountid");
		int accountKey = AttributeGetters.getAttributeInteger(element, "accountkey");
		double balance = AttributeGetters.getAttributeDouble(element, "balance");
		accountBalance.setAccountID(accountID);
		accountBalance.setAccountKey(accountKey);
		accountBalance.setBalance(balance);
		return accountBalance;
	}


	private static void parseAssets(Node node, List<EveAsset> assets, EveAsset parentEveAsset){
		NodeList assetsNodes = node.getChildNodes();
		EveAsset eveAsset = null;
		for (int a = 0; a < assetsNodes.getLength(); a++){
			Node currentNode = assetsNodes.item(a);
			if (currentNode.getNodeName().equals("asset")){
				eveAsset = parseEveAsset(currentNode);
				if (parentEveAsset == null){
					assets.add(eveAsset);
				} else {
					parentEveAsset.addEveAsset(eveAsset);
				}
				parseAssets(currentNode, assets, eveAsset);
			}
		}
	}
	private static EveAsset parseEveAsset(Node node){
		String name = AttributeGetters.getAttributeString(node, "name");
		String group = AttributeGetters.getAttributeString(node, "group");
		String category = AttributeGetters.getAttributeString(node, "category");
		String owner = AttributeGetters.getAttributeString(node, "owner");
		long count = AttributeGetters.getAttributeLong(node, "count");
		String location = AttributeGetters.getAttributeString(node, "location");
		String container = AttributeGetters.getAttributeString(node, "container");
		String flag = AttributeGetters.getAttributeString(node, "flag");
		double price = AttributeGetters.getAttributeDouble(node, "price");
		String meta = AttributeGetters.getAttributeString(node, "meta");
		int id = AttributeGetters.getAttributeInteger(node, "id");
		int typeID = AttributeGetters.getAttributeInteger(node, "typeid");
		boolean marketGroup = AttributeGetters.getAttributeBoolean(node, "marketgroup");
		boolean corporationAsset = AttributeGetters.getAttributeBoolean(node, "corporationasset");
		float volume = AttributeGetters.getAttributeFloat(node, "volume");
		String region = AttributeGetters.getAttributeString(node, "region");
		int locationID = AttributeGetters.getAttributeInteger(node, "locationid");
		boolean singleton = AttributeGetters.getAttributeBoolean(node, "singleton");
		return new EveAsset(name, group, category, owner, count, location, container, flag, price, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton);
	}
}
