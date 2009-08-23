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
import com.beimin.eveapi.order.ApiMarketOrder;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class LocalAssetsWriter extends AbstractXmlWriter {
	public static void save(Settings settings){
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("assets");
		} catch (XmlException ex) {
			Log.error("Assets not saved "+ex.getMessage(), ex);
		}
		writeAccounts(xmldoc, settings.getAccounts());
		try {
			writeXmlFile(xmldoc, Settings.getPathAssets());
		} catch (XmlException ex) {
			Log.error("Assets not saved "+ex.getMessage(), ex);
		}
		Log.info("Assets saved");
	}

	private static void writeAccounts(Document xmldoc, List<Account> accounts){
		Element parentNode = xmldoc.createElementNS(null, "accounts");
		xmldoc.getDocumentElement().appendChild(parentNode);

		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			Element node = xmldoc.createElementNS(null, "account");
			node.setAttributeNS(null, "userid", String.valueOf(account.getUserID()));
			node.setAttributeNS(null, "apikey", account.getApiKey());
			node.setAttributeNS(null, "charactersnextupdate", String.valueOf(account.getCharactersNextUpdate().getTime()));
			parentNode.appendChild(node);
			writeHumans(xmldoc, node, account.getHumans());

		}
	}

	private static void writeHumans(Document xmldoc, Element parentNode, List<Human> humans){
		for (int a = 0; a < humans.size(); a++){
			Human human = humans.get(a);
			Element node = xmldoc.createElementNS(null, "human");
			node.setAttributeNS(null, "id", String.valueOf(human.getCharacterID()));
			node.setAttributeNS(null, "name", human.getName());
			node.setAttributeNS(null, "corporation", human.getCorporation());
			node.setAttributeNS(null, "corpassets", String.valueOf(human.isUpdateCorporationAssets()));
			node.setAttributeNS(null, "show", String.valueOf(human.isShowAssets()));
			node.setAttributeNS(null, "assetsnextupdate", String.valueOf(human.getAssetNextUpdate().getTime()));
			node.setAttributeNS(null, "balancenextupdate", String.valueOf(human.getBalanceNextUpdate().getTime()));
			node.setAttributeNS(null, "marketordersnextupdate", String.valueOf(human.getMarketOrdersNextUpdate().getTime()));
			parentNode.appendChild(node);
			Element childNode = xmldoc.createElementNS(null, "assets");
			node.appendChild(childNode);
			writeAssets(xmldoc, childNode, human.getAssets());
			writeAccountBalances(xmldoc, node, human.getAccountBalances(), false);
			writeAccountBalances(xmldoc, node, human.getCorporationAccountBalances(), true);
			writeMarketOrders(xmldoc, node, human.getMarketOrders(), false);
			writeMarketOrders(xmldoc, node, human.getCorporationMarketOrders(), true);
		}
	}

	private static void writeAssets(Document xmldoc, Element parentNode, List<EveAsset> assets) {
		for (int a = 0; a < assets.size(); a++){
			EveAsset eveAsset = assets.get(a);
			Element node = xmldoc.createElementNS(null, "asset");
			node.setAttributeNS(null, "owner", eveAsset.getOwner());
			node.setAttributeNS(null, "count", String.valueOf(eveAsset.getCount()));
			node.setAttributeNS(null, "flag", eveAsset.getFlag());
			node.setAttributeNS(null, "id", String.valueOf(eveAsset.getId()));
			node.setAttributeNS(null, "typeid", String.valueOf(eveAsset.getTypeId()));
			node.setAttributeNS(null, "corporationasset", String.valueOf(eveAsset.isCorporationAsset()));
			node.setAttributeNS(null, "locationid", String.valueOf(eveAsset.getLocationID()));
			node.setAttributeNS(null, "singleton", String.valueOf(eveAsset.isSingleton()));
			parentNode.appendChild(node);
			writeAssets(xmldoc, node, eveAsset.getAssets());
		}
	}

	private static void writeAccountBalances(Document xmldoc, Element parentNode, List<ApiAccountBalance> accountBalances, boolean bCorp){
		Element node = xmldoc.createElementNS(null, "balances");
		if (!accountBalances.isEmpty()){
			node.setAttributeNS(null, "corp", String.valueOf(bCorp));
			parentNode.appendChild(node);
		}
		for (int a = 0; a < accountBalances.size(); a++){
			ApiAccountBalance accountBalance = accountBalances.get(a);

			Element childNode = xmldoc.createElementNS(null, "balance");
			childNode.setAttributeNS(null, "accountid", String.valueOf(accountBalance.getAccountID()));
			childNode.setAttributeNS(null, "accountkey", String.valueOf(accountBalance.getAccountKey()));
			childNode.setAttributeNS(null, "balance", String.valueOf(accountBalance.getBalance()));
			node.appendChild(childNode);
		}
	}
	private static void writeMarketOrders(Document xmldoc, Element parentNode, List<ApiMarketOrder> marketOrders, boolean bCorp){
		Element node = xmldoc.createElementNS(null, "markerorders");
		if (!marketOrders.isEmpty()){
			node.setAttributeNS(null, "corp", String.valueOf(bCorp));
			parentNode.appendChild(node);
		}
		for (int a = 0; a < marketOrders.size(); a++){
			ApiMarketOrder apiMarketOrder = marketOrders.get(a);
			Element childNode = xmldoc.createElementNS(null, "markerorder");
			childNode.setAttributeNS(null, "orderid", String.valueOf(apiMarketOrder.getOrderID()));
			childNode.setAttributeNS(null, "charid", String.valueOf(apiMarketOrder.getCharID()));
			childNode.setAttributeNS(null, "stationid", String.valueOf(apiMarketOrder.getStationID()));
			childNode.setAttributeNS(null, "volentered", String.valueOf(apiMarketOrder.getVolEntered()));
			childNode.setAttributeNS(null, "volremaining", String.valueOf(apiMarketOrder.getVolRemaining()));
			childNode.setAttributeNS(null, "minvolume", String.valueOf(apiMarketOrder.getMinVolume()));
			childNode.setAttributeNS(null, "orderstate", String.valueOf(apiMarketOrder.getOrderState()));
			childNode.setAttributeNS(null, "typeid", String.valueOf(apiMarketOrder.getTypeID()));
			childNode.setAttributeNS(null, "range", String.valueOf(apiMarketOrder.getRange()));
			childNode.setAttributeNS(null, "accountkey", String.valueOf(apiMarketOrder.getAccountKey()));
			childNode.setAttributeNS(null, "duration", String.valueOf(apiMarketOrder.getDuration()));
			childNode.setAttributeNS(null, "escrow", String.valueOf(apiMarketOrder.getEscrow()));
			childNode.setAttributeNS(null, "price", String.valueOf(apiMarketOrder.getPrice()));
			childNode.setAttributeNS(null, "bid", String.valueOf(apiMarketOrder.getBid()));
			childNode.setAttributeNS(null, "issued", String.valueOf(apiMarketOrder.getIssued()));
			node.appendChild(childNode);
		}
	}
}
