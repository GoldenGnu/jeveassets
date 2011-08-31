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

import com.beimin.eveapi.shared.accountbalance.ApiAccountBalance;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlWriter;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class AssetsWriter extends AbstractXmlWriter {

	private final static Logger LOG = LoggerFactory.getLogger(AssetsWriter.class);

	public static void save(Settings settings, String filename){
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("assets");
		} catch (XmlException ex) {
			LOG.error("Assets not saved "+ex.getMessage(), ex);
		}
		writeAccounts(xmldoc, settings.getAccounts());
		try {
			writeXmlFile(xmldoc, filename);
		} catch (XmlException ex) {
			LOG.error("Assets not saved "+ex.getMessage(), ex);
		}
		LOG.info("Assets saved");
	}

	private static void writeAccounts(Document xmldoc, List<Account> accounts){
		Element parentNode = xmldoc.createElementNS(null, "accounts");
		xmldoc.getDocumentElement().appendChild(parentNode);

		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			Element node = xmldoc.createElementNS(null, "account");
			node.setAttributeNS(null, "keyid", String.valueOf(account.getKeyID()));
			node.setAttributeNS(null, "vcode", account.getVCode());
			node.setAttributeNS(null, "name", account.getName());
			node.setAttributeNS(null, "charactersnextupdate", String.valueOf(account.getCharactersNextUpdate().getTime()));
			node.setAttributeNS(null, "accessmask", String.valueOf(account.getAccessMask()));
			node.setAttributeNS(null, "type", account.getType());
			node.setAttributeNS(null, "expires", account.getExpires() == null ? "0" : String.valueOf(account.getExpires().getTime()));
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
			node.setAttributeNS(null, "show", String.valueOf(human.isShowAssets()));
			node.setAttributeNS(null, "assetsnextupdate", String.valueOf(human.getAssetNextUpdate().getTime()));
			node.setAttributeNS(null, "balancenextupdate", String.valueOf(human.getBalanceNextUpdate().getTime()));
			node.setAttributeNS(null, "marketordersnextupdate", String.valueOf(human.getMarketOrdersNextUpdate().getTime()));
			node.setAttributeNS(null, "industryjobsnextupdate", String.valueOf(human.getIndustryJobsNextUpdate().getTime()));
			parentNode.appendChild(node);
			Element childNode = xmldoc.createElementNS(null, "assets");
			node.appendChild(childNode);
			writeAssets(xmldoc, childNode, human.getAssets());
			writeAccountBalances(xmldoc, node, human.getAccountBalances(), human.isCorporation());
			writeMarketOrders(xmldoc, node, human.getMarketOrders(), human.isCorporation());
			writeIndustryJobs(xmldoc, node, human.getIndustryJobs(), human.isCorporation());
		}
	}

	private static void writeAssets(Document xmldoc, Element parentNode, List<EveAsset> assets) {
		for (int a = 0; a < assets.size(); a++){
			EveAsset eveAsset = assets.get(a);
			Element node = xmldoc.createElementNS(null, "asset");
			node.setAttributeNS(null, "owner", eveAsset.getOwner());
			node.setAttributeNS(null, "count", String.valueOf(eveAsset.getCount()));
			node.setAttributeNS(null, "flag", eveAsset.getFlag());
			node.setAttributeNS(null, "id", String.valueOf(eveAsset.getItemID()));
			node.setAttributeNS(null, "typeid", String.valueOf(eveAsset.getTypeID()));
			node.setAttributeNS(null, "corporationasset", String.valueOf(eveAsset.isCorporation()));
			node.setAttributeNS(null, "locationid", String.valueOf(eveAsset.getLocationID()));
			node.setAttributeNS(null, "singleton", String.valueOf(eveAsset.isSingleton()));
			node.setAttributeNS(null, "rawquantity", String.valueOf(eveAsset.getRawQuantity()));
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
			childNode.setAttributeNS(null, "issued", String.valueOf(apiMarketOrder.getIssued().getTime()));
			node.appendChild(childNode);
		}
	}

	private static void writeIndustryJobs(Document xmldoc, Element parentNode, List<ApiIndustryJob> industryJobs, boolean bCorp){
		Element node = xmldoc.createElementNS(null, "industryjobs");
		if (!industryJobs.isEmpty()){
			node.setAttributeNS(null, "corp", String.valueOf(bCorp));
			parentNode.appendChild(node);
		}
		for (int a = 0; a < industryJobs.size(); a++){
			ApiIndustryJob apiIndustryJob = industryJobs.get(a);
			Element childNode = xmldoc.createElementNS(null, "industryjob");

			childNode.setAttributeNS(null, "jobid", String.valueOf(apiIndustryJob.getJobID()));
			childNode.setAttributeNS(null, "containerid", String.valueOf(apiIndustryJob.getContainerID()));
			childNode.setAttributeNS(null, "installeditemid", String.valueOf(apiIndustryJob.getInstalledItemID()));
			childNode.setAttributeNS(null, "installeditemlocationid", String.valueOf(apiIndustryJob.getInstalledItemLocationID()));
			childNode.setAttributeNS(null, "installeditemquantity", String.valueOf(apiIndustryJob.getInstalledItemQuantity()));
			childNode.setAttributeNS(null, "installeditemproductivitylevel", String.valueOf(apiIndustryJob.getInstalledItemProductivityLevel()));
			childNode.setAttributeNS(null, "installeditemmateriallevel", String.valueOf(apiIndustryJob.getInstalledItemMaterialLevel()));
			childNode.setAttributeNS(null, "installeditemlicensedproductionrunsremaining", String.valueOf(apiIndustryJob.getInstalledItemLicensedProductionRunsRemaining()));
			childNode.setAttributeNS(null, "outputlocationid", String.valueOf(apiIndustryJob.getOutputLocationID()));
			childNode.setAttributeNS(null, "installerid", String.valueOf(apiIndustryJob.getInstallerID()));
			childNode.setAttributeNS(null, "runs", String.valueOf(apiIndustryJob.getRuns()));
			childNode.setAttributeNS(null, "licensedproductionruns", String.valueOf(apiIndustryJob.getLicensedProductionRuns()));
			childNode.setAttributeNS(null, "installedinsolarsystemid", String.valueOf(apiIndustryJob.getInstalledInSolarSystemID()));
			childNode.setAttributeNS(null, "containerlocationid", String.valueOf(apiIndustryJob.getContainerLocationID()));
			childNode.setAttributeNS(null, "materialmultiplier", String.valueOf(apiIndustryJob.getMaterialMultiplier()));
			childNode.setAttributeNS(null, "charmaterialmultiplier", String.valueOf(apiIndustryJob.getCharMaterialMultiplier()));
			childNode.setAttributeNS(null, "timemultiplier", String.valueOf(apiIndustryJob.getTimeMultiplier()));
			childNode.setAttributeNS(null, "chartimemultiplier", String.valueOf(apiIndustryJob.getCharTimeMultiplier()));
			childNode.setAttributeNS(null, "installeditemtypeid", String.valueOf(apiIndustryJob.getInstalledItemTypeID()));
			childNode.setAttributeNS(null, "outputtypeid", String.valueOf(apiIndustryJob.getOutputTypeID()));
			childNode.setAttributeNS(null, "containertypeid", String.valueOf(apiIndustryJob.getContainerTypeID()));
			childNode.setAttributeNS(null, "installeditemcopy", String.valueOf(apiIndustryJob.getInstalledItemCopy()));
			childNode.setAttributeNS(null, "completed", String.valueOf(apiIndustryJob.isCompleted()));
			childNode.setAttributeNS(null, "completedsuccessfully", String.valueOf(apiIndustryJob.isCompletedSuccessfully()));
			childNode.setAttributeNS(null, "installeditemflag", String.valueOf(apiIndustryJob.getInstalledItemFlag()));
			childNode.setAttributeNS(null, "outputflag", String.valueOf(apiIndustryJob.getOutputFlag()));
			childNode.setAttributeNS(null, "activityid", String.valueOf(apiIndustryJob.getActivityID()));
			childNode.setAttributeNS(null, "completedstatus", String.valueOf(apiIndustryJob.getCompletedStatus()));
			childNode.setAttributeNS(null, "installtime", String.valueOf(apiIndustryJob.getInstallTime().getTime()));
			childNode.setAttributeNS(null, "beginproductiontime", String.valueOf(apiIndustryJob.getBeginProductionTime().getTime()));
			childNode.setAttributeNS(null, "endproductiontime", String.valueOf(apiIndustryJob.getEndProductionTime().getTime()));
			childNode.setAttributeNS(null, "pauseproductiontime", String.valueOf(apiIndustryJob.getPauseProductionTime().getTime()));
			childNode.setAttributeNS(null, "assemblylineid", String.valueOf(apiIndustryJob.getAssemblyLineID()));
			node.appendChild(childNode);
		}
	}
}
