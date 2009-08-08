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
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class LocalAssetsReader extends AbstractXmlReader {
	public static boolean load(Settings settings){
		try {
			Element element = getDocumentElement(Settings.getPathAssets());
			parseSettings(element, settings);
		} catch (IOException ex) {
			Log.info("Assets not loaded");
			return false;
		} catch (XmlException ex) {
			Log.error("Assets not loaded: ("+Settings.getPathAssets()+")"+ex.getMessage(), ex);
		}
		Log.info("Assets loaded");
		return true;
	}
	private static void parseSettings(Element element, Settings settings) throws XmlException {
		if (!element.getNodeName().equals("assets")) {
			throw new XmlException("Wrong root element name.");
		}
		//Accounts
		NodeList accountNodes = element.getElementsByTagName("accounts");
		if (accountNodes.getLength() == 1){
			Element accountsElement = (Element) accountNodes.item(0);
			parseAccounts(accountsElement, settings.getAccounts());
		}
		
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
