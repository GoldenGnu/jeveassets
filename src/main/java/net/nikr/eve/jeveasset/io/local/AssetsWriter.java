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

import com.beimin.eveapi.shared.accountbalance.EveAccountBalance;
import com.beimin.eveapi.shared.contract.EveContract;
import com.beimin.eveapi.shared.contract.items.EveContractItem;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlWriter;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public final class AssetsWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(AssetsWriter.class);

	private AssetsWriter() { }

	public static void save(final Settings settings, final String filename) {
		AssetsWriter writer = new AssetsWriter();
		writer.write(settings, filename);
	}

	private void write(final Settings settings, final String filename) {
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("assets");
		} catch (XmlException ex) {
			LOG.error("Assets not saved " + ex.getMessage(), ex);
		}
		writeAccounts(xmldoc, settings.getAccounts());
		try {
			writeXmlFile(xmldoc, filename, true);
		} catch (XmlException ex) {
			LOG.error("Assets not saved " + ex.getMessage(), ex);
		}
		LOG.info("Assets saved");
	}

	private void writeAccounts(final Document xmldoc, final List<Account> accounts) {
		Element parentNode = xmldoc.createElementNS(null, "accounts");
		xmldoc.getDocumentElement().appendChild(parentNode);

		for (Account account : accounts) {
			Element node = xmldoc.createElementNS(null, "account");
			node.setAttributeNS(null, "keyid", String.valueOf(account.getKeyID()));
			node.setAttributeNS(null, "vcode", account.getVCode());
			node.setAttributeNS(null, "name", account.getName());
			node.setAttributeNS(null, "charactersnextupdate", String.valueOf(account.getAccountNextUpdate().getTime()));
			node.setAttributeNS(null, "accessmask", String.valueOf(account.getAccessMask()));
			node.setAttributeNS(null, "type", account.getType().name());
			node.setAttributeNS(null, "expires", account.getExpires() == null ? "0" : String.valueOf(account.getExpires().getTime()));
			parentNode.appendChild(node);
			writeOwners(xmldoc, node, account.getOwners());

		}
	}

	private void writeOwners(final Document xmldoc, final Element parentNode, final List<Owner> owners) {
		for (Owner owner : owners) {
			Element node = xmldoc.createElementNS(null, "human");
			node.setAttributeNS(null, "id", String.valueOf(owner.getOwnerID()));
			node.setAttributeNS(null, "name", owner.getName());
			node.setAttributeNS(null, "show", String.valueOf(owner.isShowAssets()));
			node.setAttributeNS(null, "assetsnextupdate", String.valueOf(owner.getAssetNextUpdate().getTime()));
			node.setAttributeNS(null, "balancenextupdate", String.valueOf(owner.getBalanceNextUpdate().getTime()));
			node.setAttributeNS(null, "marketordersnextupdate", String.valueOf(owner.getMarketOrdersNextUpdate().getTime()));
			node.setAttributeNS(null, "industryjobsnextupdate", String.valueOf(owner.getIndustryJobsNextUpdate().getTime()));
			node.setAttributeNS(null, "contractsnextupdate", String.valueOf(owner.getContractsNextUpdate().getTime()));
			parentNode.appendChild(node);
			Element childNode = xmldoc.createElementNS(null, "assets");
			node.appendChild(childNode);
			writeAssets(xmldoc, childNode, owner.getAssets());
			writeContractItems(xmldoc, node, owner.getContracts());
			writeAccountBalances(xmldoc, node, owner.getAccountBalances(), owner.isCorporation());
			writeMarketOrders(xmldoc, node, owner.getMarketOrders(), owner.isCorporation());
			writeIndustryJobs(xmldoc, node, owner.getIndustryJobs(), owner.isCorporation());
		}
	}

	private void writeAssets(final Document xmldoc, final Element parentNode, final List<Asset> assets) {
		for (Asset asset : assets) {
			Element node = xmldoc.createElementNS(null, "asset");
			node.setAttributeNS(null, "owner", asset.getOwner());
			node.setAttributeNS(null, "count", String.valueOf(asset.getCount()));
			node.setAttributeNS(null, "flagid", String.valueOf(asset.getFlagID()));
			node.setAttributeNS(null, "id", String.valueOf(asset.getItemID()));
			node.setAttributeNS(null, "typeid", String.valueOf(asset.getTypeID()));
			node.setAttributeNS(null, "corporationasset", String.valueOf(asset.isCorporation()));
			node.setAttributeNS(null, "locationid", String.valueOf(asset.getLocationID()));
			node.setAttributeNS(null, "singleton", String.valueOf(asset.isSingleton()));
			node.setAttributeNS(null, "rawquantity", String.valueOf(asset.getRawQuantity()));
			parentNode.appendChild(node);
			writeAssets(xmldoc, node, asset.getAssets());
		}
	}

	private void writeContractItems(Document xmldoc, Element parentNode, Map<EveContract, List<EveContractItem>> contractItems) {
		Element contractsNode = xmldoc.createElementNS(null, "contracts");
		parentNode.appendChild(contractsNode);
		for (Map.Entry<EveContract, List<EveContractItem>> entry : contractItems.entrySet()) {
			EveContract contract = entry.getKey();
			Element contractNode = xmldoc.createElementNS(null, "contract");
			contractNode.setAttributeNS(null, "acceptorid", String.valueOf(contract.getAcceptorID()));
			contractNode.setAttributeNS(null, "assigneeid", String.valueOf(contract.getAssigneeID()));
			contractNode.setAttributeNS(null, "availability", contract.getAvailability().name());
			contractNode.setAttributeNS(null, "buyout", String.valueOf(contract.getBuyout()));
			contractNode.setAttributeNS(null, "collateral", String.valueOf(contract.getCollateral()));
			contractNode.setAttributeNS(null, "contractid", String.valueOf(contract.getContractID()));
			if (contract.getDateAccepted() != null) {
				contractNode.setAttributeNS(null, "dateaccepted", String.valueOf(contract.getDateAccepted().getTime()));
			}
			if (contract.getDateCompleted() != null) {
				contractNode.setAttributeNS(null, "datecompleted", String.valueOf(contract.getDateCompleted().getTime()));
			}
			contractNode.setAttributeNS(null, "dateexpired", String.valueOf(contract.getDateExpired().getTime()));
			contractNode.setAttributeNS(null, "dateissued", String.valueOf(contract.getDateIssued().getTime()));
			contractNode.setAttributeNS(null, "endstationid", String.valueOf(contract.getEndStationID()));
			contractNode.setAttributeNS(null, "issuercorpid", String.valueOf(contract.getIssuerCorpID()));
			contractNode.setAttributeNS(null, "issuerid", String.valueOf(contract.getIssuerID()));
			contractNode.setAttributeNS(null, "numdays", String.valueOf(contract.getNumDays()));
			contractNode.setAttributeNS(null, "price", String.valueOf(contract.getPrice()));
			contractNode.setAttributeNS(null, "reward", String.valueOf(contract.getReward()));
			contractNode.setAttributeNS(null, "startstationid", String.valueOf(contract.getStartStationID()));
			contractNode.setAttributeNS(null, "status", contract.getStatus().name());
			contractNode.setAttributeNS(null, "title", String.valueOf(contract.getTitle()));
			contractNode.setAttributeNS(null, "type", contract.getType().name());
			contractNode.setAttributeNS(null, "volume", String.valueOf(contract.getVolume()));
			contractNode.setAttributeNS(null, "forcorp", String.valueOf(contract.isForCorp()));
			contractsNode.appendChild(contractNode);
			for (EveContractItem contractItem : entry.getValue()) {
				Element itemNode = xmldoc.createElementNS(null, "contractitem");
				itemNode.setAttributeNS(null, "contractid", String.valueOf(entry.getKey()));
				itemNode.setAttributeNS(null, "included", String.valueOf(contractItem.isIncluded()));
				itemNode.setAttributeNS(null, "quantity", String.valueOf(contractItem.getQuantity()));
				itemNode.setAttributeNS(null, "recordid", String.valueOf(contractItem.getRecordID()));
				itemNode.setAttributeNS(null, "singleton", String.valueOf(contractItem.isSingleton()));
				itemNode.setAttributeNS(null, "typeid", String.valueOf(contractItem.getTypeID()));
				contractNode.appendChild(itemNode);
			}
		}
	}

	private void writeAccountBalances(final Document xmldoc, final Element parentNode, final List<EveAccountBalance> accountBalances, final boolean bCorp) {
		Element node = xmldoc.createElementNS(null, "balances");
		if (!accountBalances.isEmpty()) {
			node.setAttributeNS(null, "corp", String.valueOf(bCorp));
			parentNode.appendChild(node);
		}
		for (EveAccountBalance accountBalance : accountBalances) {
			Element childNode = xmldoc.createElementNS(null, "balance");
			childNode.setAttributeNS(null, "accountid", String.valueOf(accountBalance.getAccountID()));
			childNode.setAttributeNS(null, "accountkey", String.valueOf(accountBalance.getAccountKey()));
			childNode.setAttributeNS(null, "balance", String.valueOf(accountBalance.getBalance()));
			node.appendChild(childNode);
		}
	}

	private void writeMarketOrders(final Document xmldoc, final Element parentNode, final List<ApiMarketOrder> marketOrders, final boolean bCorp) {
		Element node = xmldoc.createElementNS(null, "markerorders");
		if (!marketOrders.isEmpty()) {
			node.setAttributeNS(null, "corp", String.valueOf(bCorp));
			parentNode.appendChild(node);
		}
		for (ApiMarketOrder apiMarketOrder : marketOrders) {
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

	private void writeIndustryJobs(final Document xmldoc, final Element parentNode, final List<ApiIndustryJob> industryJobs, final boolean bCorp) {
		Element node = xmldoc.createElementNS(null, "industryjobs");
		if (!industryJobs.isEmpty()) {
			node.setAttributeNS(null, "corp", String.valueOf(bCorp));
			parentNode.appendChild(node);
		}
		for (ApiIndustryJob apiIndustryJob : industryJobs) {
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
