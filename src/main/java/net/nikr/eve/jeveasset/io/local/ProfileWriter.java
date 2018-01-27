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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public final class ProfileWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileWriter.class);

	private ProfileWriter() { }

	public static boolean save(final ProfileManager profileManager, final String filename) {
		ProfileWriter writer = new ProfileWriter();
		return writer.write(profileManager, filename);
	}

	private boolean write(final ProfileManager profileManager, final String filename) {
		Document xmldoc;
		try {
			xmldoc = getXmlDocument("assets");
		} catch (XmlException ex) {
			LOG.error("Profile not saved " + ex.getMessage(), ex);
			return false;
		}
		writeAccounts(xmldoc, profileManager.getAccounts());
		writeEveKitOwners(xmldoc, profileManager.getEveKitOwners());
		writeEsiOwners(xmldoc, profileManager.getEsiOwners());
		try {
			writeXmlFile(xmldoc, filename, true);
		} catch (XmlException ex) {
			LOG.error("Profile not saved " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Profile saved");
		return true;
	}

	private void writeEsiOwners(final Document xmldoc, final List<EsiOwner> esiOwners) {
		Element parentNode = xmldoc.createElement("esiowners");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (EsiOwner owner : esiOwners) {
			Element node = xmldoc.createElement("esiowner");
			setAttribute(node, "accountname", owner.getAccountName());
			setAttribute(node, "refreshtoken", owner.getRefreshToken());
			setAttribute(node, "scopes", String.join(" ", owner.getScopes()));
			setAttribute(node, "tokentype", owner.getTokenType());
			setAttribute(node, "characterownerhash", owner.getCharacterOwnerHash());
			setAttribute(node, "intellectualproperty", owner.getIntellectualProperty());
			setAttribute(node, "structuresnextupdate", owner.getStructuresNextUpdate());
			setAttribute(node, "accountnextupdate", owner.getAccountNextUpdate());
			setAttribute(node, "callbackurl", owner.getCallbackURL());
			Set<String> roles = new HashSet<String>();
			for (RolesEnum role : owner.getRoles()) {
				roles.add(role.name());
			}
			setAttribute(node, "characterroles", String.join(",", roles));
			writeTypeOwner(xmldoc, node, owner);
			parentNode.appendChild(node);
		}
	}

	private void writeEveKitOwners(final Document xmldoc, final List<EveKitOwner> eveKitOwners) {
		Element parentNode = xmldoc.createElement("evekitowners");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (EveKitOwner owner : eveKitOwners) {
			Element node = xmldoc.createElement("evekitowner");
			setAttribute(node, "accesskey", owner.getAccessKey());
			setAttribute(node, "accesscred", owner.getAccessCred());
			setAttributeOptional(node, "expire", owner.getExpire());
			setAttribute(node, "accessmask", owner.getAccessMask());
			setAttribute(node, "corporation", owner.isCorporation());
			setAttributeOptional(node, "limit", owner.getLimit());
			setAttribute(node, "accountname", owner.getAccountName());
			//ContID
			setAttributeOptional(node, "journalcid", owner.getJournalCID());
			setAttributeOptional(node, "transactionscid", owner.getTransactionsCID());
			setAttributeOptional(node, "contractscid", owner.getContractsCID());
			setAttributeOptional(node, "industryjobscid", owner.getIndustryJobsCID());
			setAttributeOptional(node, "marketorderscid", owner.getMarketOrdersCID());
			setAttributeOptional(node, "accountnextupdate", owner.getAccountNextUpdate());
			writeTypeOwner(xmldoc, node, owner);
			parentNode.appendChild(node);
		}
	}

	private void writeAccounts(final Document xmldoc, final List<EveApiAccount> accounts) {
		Element parentNode = xmldoc.createElement("accounts");
		xmldoc.getDocumentElement().appendChild(parentNode);

		for (EveApiAccount account : accounts) {
			Element node = xmldoc.createElement("account");
			setAttribute(node, "keyid", account.getKeyID());
			setAttribute(node, "vcode", account.getVCode());
			setAttribute(node, "name", account.getName());
			setAttribute(node, "charactersnextupdate", account.getAccountNextUpdate());
			setAttribute(node, "accessmask", account.getAccessMask());
			setAttributeOptional(node, "type", account.getType());
			setAttribute(node, "expires", account.getExpires() == null ? "0" : account.getExpires());
			setAttribute(node, "invalid", account.isInvalid());
			parentNode.appendChild(node);
			writeOwners(xmldoc, node, account.getOwners());
		}
	}

	private void writeOwners(final Document xmldoc, final Element parentNode, final List<EveApiOwner> owners) {
		for (EveApiOwner owner : owners) {
			Element node = xmldoc.createElement("human");
			setAttribute(node, "migrated", owner.isMigrated());
			writeTypeOwner(xmldoc, node, owner);
			parentNode.appendChild(node);
		}
	}

	private void writeTypeOwner(final Document xmldoc, final Element node, final OwnerType owner) {
		setAttribute(node, "id", owner.getOwnerID());
		setAttribute(node, "name", owner.getOwnerName());
		setAttribute(node, "show", owner.isShowOwner());
		setAttributeOptional(node, "assetslastupdate", owner.getAssetLastUpdate());
		setAttribute(node, "assetsnextupdate", owner.getAssetNextUpdate());
		setAttributeOptional(node, "balancelastupdate", owner.getBalanceLastUpdate());
		setAttribute(node, "balancenextupdate", owner.getBalanceNextUpdate());
		setAttribute(node, "marketordersnextupdate", owner.getMarketOrdersNextUpdate());
		setAttribute(node, "journalnextupdate", owner.getJournalNextUpdate());
		setAttribute(node, "wallettransactionsnextupdate", owner.getTransactionsNextUpdate());
		setAttribute(node, "industryjobsnextupdate", owner.getIndustryJobsNextUpdate());
		setAttribute(node, "contractsnextupdate", owner.getContractsNextUpdate());
		setAttribute(node, "locationsnextupdate", owner.getLocationsNextUpdate());
		setAttribute(node, "blueprintsnextupdate", owner.getBlueprintsNextUpdate());
		setAttribute(node, "containerlogsnextupdate", owner.getContainerLogsNextUpdate());

		Element childNode = xmldoc.createElement("assets");
		node.appendChild(childNode);
		writeAssets(xmldoc, childNode, owner.getAssets());
		writeContractItems(xmldoc, node, owner.getContracts());
		writeAccountBalances(xmldoc, node, owner.getAccountBalances(), owner.isCorporation());
		writeMarketOrders(xmldoc, node, owner.getMarketOrders(), owner.isCorporation());
		writeJournals(xmldoc, node, owner.getJournal(), owner.isCorporation());
		writeTransactions(xmldoc, node, owner.getTransactions(), owner.isCorporation());
		writeIndustryJobs(xmldoc, node, owner.getIndustryJobs(), owner.isCorporation());
		writeBlueprints(xmldoc, node, owner.getBlueprints(), owner.isCorporation());
		writeContainerLogs(xmldoc, node, owner.getContainerLogs(), owner.isCorporation());
	}

	private void writeAssets(final Document xmldoc, final Element parentNode, final List<MyAsset> assets) {
		for (MyAsset asset : assets) {
			Element node = xmldoc.createElement("asset");
			Integer quantity = asset.getQuantity();
			int count;
			Integer rawQuantity;
			if (quantity == null || quantity <= 0) {
				count = 1;
				rawQuantity = quantity; //Possible values: null, -1, -2
			} else {
				count = quantity;
				rawQuantity = null;
			}
			setAttribute(node, "count", count);
			setAttribute(node, "flagid", asset.getFlagID());
			setAttribute(node, "id", asset.getItemID());
			setAttribute(node, "typeid", asset.getItem().getTypeID());
			setAttribute(node, "locationid", asset.getLocationID());
			setAttribute(node, "singleton", asset.isSingleton());
			setAttributeOptional(node, "rawquantity", rawQuantity);
			parentNode.appendChild(node);
			writeAssets(xmldoc, node, asset.getAssets());
		}
	}

	private void writeContractItems(Document xmldoc, Element parentNode, Map<MyContract, List<MyContractItem>> contractItems) {
		Element contractsNode = xmldoc.createElement("contracts");
		parentNode.appendChild(contractsNode);
		for (Map.Entry<MyContract, List<MyContractItem>> entry : contractItems.entrySet()) {
			MyContract contract = entry.getKey();
			Element contractNode = xmldoc.createElement("contract");
			setAttribute(contractNode, "acceptorid", contract.getAcceptorID());
			setAttribute(contractNode, "assigneeid", contract.getAssigneeID());
			setAttribute(contractNode, "availability", contract.getAvailability());
			setAttributeOptional(contractNode, "buyout", contract.getBuyout());
			setAttributeOptional(contractNode, "collateral", contract.getCollateral());
			setAttribute(contractNode, "contractid", contract.getContractID());
			setAttributeOptional(contractNode, "dateaccepted", contract.getDateAccepted());
			setAttributeOptional(contractNode, "datecompleted", contract.getDateCompleted());
			setAttribute(contractNode, "dateexpired", contract.getDateExpired());
			setAttribute(contractNode, "dateissued", contract.getDateIssued());
			setAttributeOptional(contractNode, "endstationid", contract.getEndLocationID());
			setAttribute(contractNode, "issuercorpid", contract.getIssuerCorpID());
			setAttribute(contractNode, "issuerid", contract.getIssuerID());
			setAttributeOptional(contractNode, "numdays", contract.getDaysToComplete());
			setAttributeOptional(contractNode, "price", contract.getPrice());
			setAttributeOptional(contractNode, "reward", contract.getReward());
			setAttributeOptional(contractNode, "startstationid", contract.getStartLocationID());
			setAttribute(contractNode, "status", contract.getStatus());
			setAttributeOptional(contractNode, "title", contract.getTitle());
			setAttribute(contractNode, "type", contract.getType());
			setAttributeOptional(contractNode, "volume", contract.getVolume());
			setAttribute(contractNode, "forcorp", contract.isForCorp());
			contractsNode.appendChild(contractNode);
			for (MyContractItem contractItem : entry.getValue()) {
				Element itemNode = xmldoc.createElement("contractitem");
				setAttribute(itemNode, "included", contractItem.isIncluded());
				setAttribute(itemNode, "quantity", contractItem.getQuantity());
				setAttribute(itemNode, "recordid", contractItem.getRecordID());
				setAttribute(itemNode, "singleton", contractItem.isSingleton());
				setAttribute(itemNode, "typeid", contractItem.getTypeID());
				setAttributeOptional(itemNode, "rawquantity", contractItem.getRawQuantity());
				contractNode.appendChild(itemNode);
			}
		}
	}

	private void writeAccountBalances(final Document xmldoc, final Element parentNode, final List<MyAccountBalance> accountBalances, final boolean bCorp) {
		Element node = xmldoc.createElement("balances");
		if (!accountBalances.isEmpty()) {
			setAttribute(node, "corp", bCorp);
			parentNode.appendChild(node);
		}
		for (MyAccountBalance accountBalance : accountBalances) {
			Element childNode = xmldoc.createElement("balance");
			setAttribute(childNode, "accountkey", accountBalance.getAccountKey());
			setAttribute(childNode, "balance", accountBalance.getBalance());
			node.appendChild(childNode);
		}
	}

	private void writeMarketOrders(final Document xmldoc, final Element parentNode, final Set<MyMarketOrder> marketOrders, final boolean bCorp) {
		Element node = xmldoc.createElement("markerorders");
		if (!marketOrders.isEmpty()) {
			setAttribute(node, "corp", bCorp);
			parentNode.appendChild(node);
		}
		for (MyMarketOrder marketOrder : marketOrders) {
			Element childNode = xmldoc.createElement("markerorder");
			setAttribute(childNode, "orderid", marketOrder.getOrderID());
			setAttribute(childNode, "stationid", marketOrder.getLocationID());
			setAttribute(childNode, "volentered", marketOrder.getVolumeTotal());
			setAttribute(childNode, "volremaining", marketOrder.getVolumeRemain());
			setAttribute(childNode, "minvolume", marketOrder.getMinVolume());
			setAttribute(childNode, "orderstate", RawConverter.fromMarketOrderState(marketOrder.getState()));
			setAttribute(childNode, "typeid", marketOrder.getTypeID());
			setAttribute(childNode, "range", RawConverter.fromMarketOrderRange(marketOrder.getRange()));
			setAttribute(childNode, "accountkey", marketOrder.getWalletDivision());
			setAttribute(childNode, "duration", marketOrder.getDuration());
			setAttribute(childNode, "escrow", marketOrder.getEscrow());
			setAttribute(childNode, "price", marketOrder.getPrice());
			setAttribute(childNode, "bid", RawConverter.fromMarketOrderIsBuyOrder(marketOrder.isBuyOrder()));
			setAttribute(childNode, "issued", marketOrder.getIssued());
			setAttribute(childNode, "corp", marketOrder.isCorp());
			node.appendChild(childNode);
		}
	}

	private void writeJournals(final Document xmldoc, final Element parentNode, final Set<MyJournal> journals, final boolean bCorp) {
		Element node = xmldoc.createElement("journals");
		if (!journals.isEmpty()) {
			setAttribute(node, "corp", bCorp);
			parentNode.appendChild(node);
		}
		for (MyJournal journal : journals) {
			Element childNode = xmldoc.createElement("journal");
			//Base
			setAttributeOptional(childNode, "amount", journal.getAmount());
			setAttributeOptional(childNode, "argid1", RawConverter.fromRawJournalExtraInfoArgID(journal.getExtraInfo()));
			setAttributeOptional(childNode, "argname1", RawConverter.fromRawJournalExtraInfoArgName(journal.getExtraInfo()));
			setAttributeOptional(childNode, "balance", journal.getBalance());
			setAttribute(childNode, "date", journal.getDate());
			setAttributeOptional(childNode, "ownerid1", journal.getFirstPartyID());
			setAttributeOptional(childNode, "ownerid2", journal.getSecondPartyID());
			setAttributeOptional(childNode, "reason", journal.getReason());
			setAttribute(childNode, "refid", journal.getRefID());
			setAttribute(childNode, "reftypeid", journal.getRefType().getID());
			setAttributeOptional(childNode, "taxamount", journal.getTaxAmount());
			setAttributeOptional(childNode, "taxreceiverid", journal.getTaxReceiverId());
			//New
			setAttributeOptional(childNode, "owner1typeid", RawConverter.fromJournalPartyType(journal.getFirstPartyType()));
			setAttributeOptional(childNode, "owner2typeid", RawConverter.fromJournalPartyType(journal.getSecondPartyType()));
			//Extra
			setAttribute(childNode, "accountkey", journal.getAccountKey());
			node.appendChild(childNode);
		}
	}

	private void writeTransactions(final Document xmldoc, final Element parentNode, final Set<MyTransaction> transactions, final boolean bCorp) {
		Element node = xmldoc.createElement("wallettransactions");
		if (!transactions.isEmpty()) {
			setAttribute(node, "corp", bCorp);
			parentNode.appendChild(node);
		}
		for (MyTransaction transaction : transactions) {
			Element childNode = xmldoc.createElement("wallettransaction");
			setAttribute(childNode, "transactiondatetime", transaction.getDate());
			setAttribute(childNode, "transactionid", transaction.getTransactionID());
			setAttribute(childNode, "quantity", transaction.getQuantity());
			setAttribute(childNode, "typeid", transaction.getTypeID());
			setAttribute(childNode, "price", transaction.getPrice());
			setAttribute(childNode, "clientid", transaction.getClientID());
			setAttribute(childNode, "clientname", transaction.getClientName());
			setAttribute(childNode, "stationid", transaction.getLocationID());
			setAttribute(childNode, "transactiontype", RawConverter.fromTransactionIsBuy(transaction.isBuy()));
			setAttribute(childNode, "transactionfor", RawConverter.fromTransactionIsPersonal(transaction.isPersonal()));
			//New
			setAttribute(childNode, "journaltransactionid", transaction.getTransactionID());
			setAttribute(childNode, "clienttypeid", transaction.getClientID());
			//Extra
			setAttribute(childNode, "accountkey", transaction.getAccountKey());
			node.appendChild(childNode);
		}
	}

	private void writeIndustryJobs(final Document xmldoc, final Element parentNode, final List<MyIndustryJob> industryJobs, final boolean bCorp) {
		Element node = xmldoc.createElement("industryjobs");
		if (!industryJobs.isEmpty()) {
			setAttribute(node, "corp", bCorp);
			parentNode.appendChild(node);
		}
		for (MyIndustryJob industryJob : industryJobs) {
			Element childNode = xmldoc.createElement("industryjob");
			setAttribute(childNode, "jobid", industryJob.getJobID());
			setAttribute(childNode, "installerid", industryJob.getInstallerID());
			setAttribute(childNode, "facilityid", industryJob.getFacilityID());
			setAttribute(childNode, "stationid", industryJob.getStationID());
			setAttribute(childNode, "activityid", industryJob.getActivityID());
			setAttribute(childNode, "blueprintid", industryJob.getBlueprintID());
			setAttribute(childNode, "blueprinttypeid", industryJob.getBlueprintTypeID());
			setAttribute(childNode, "blueprintlocationid", industryJob.getBlueprintLocationID());
			setAttribute(childNode, "outputlocationid", industryJob.getOutputLocationID());
			setAttribute(childNode, "runs", industryJob.getRuns());
			setAttributeOptional(childNode, "cost", industryJob.getCost());
			setAttributeOptional(childNode, "licensedruns", industryJob.getLicensedRuns());
			setAttributeOptional(childNode, "probability", industryJob.getProbability());
			setAttributeOptional(childNode, "producttypeid", industryJob.getProductTypeID());
			setAttribute(childNode, "status", RawConverter.fromIndustryJobStatus(industryJob.getStatus()));
			setAttribute(childNode, "timeinseconds", industryJob.getDuration());
			setAttribute(childNode, "startdate", industryJob.getStartDate());
			setAttribute(childNode, "enddate", industryJob.getEndDate());
			setAttributeOptional(childNode, "pausedate", industryJob.getPauseDate());
			setAttributeOptional(childNode, "completeddate", industryJob.getCompletedDate());
			setAttributeOptional(childNode, "completedcharacterid", industryJob.getCompletedCharacterID());
			setAttributeOptional(childNode, "successfulruns", industryJob.getSuccessfulRuns());
			node.appendChild(childNode);
		}
	}

	private void writeBlueprints(final Document xmldoc, final Element parentNode, final Map<Long, RawBlueprint> blueprints, final boolean bCorp) {
		Element node = xmldoc.createElement("blueprints");
		if (!blueprints.isEmpty()) {
			setAttribute(node, "corp", bCorp);
			parentNode.appendChild(node);
		}
		for (RawBlueprint blueprint : blueprints.values()) {
			Element childNode = xmldoc.createElement("blueprint");
			setAttribute(childNode, "itemid", blueprint.getItemID());
			setAttribute(childNode, "locationid", blueprint.getLocationID());
			setAttribute(childNode, "typeid", blueprint.getTypeID());
			setAttribute(childNode, "flagid", blueprint.getFlagID());
			setAttribute(childNode, "quantity", blueprint.getQuantity());
			setAttribute(childNode, "timeefficiency", blueprint.getTimeEfficiency());
			setAttribute(childNode, "materialefficiency", blueprint.getMaterialEfficiency());
			setAttribute(childNode, "runs", blueprint.getRuns());
			node.appendChild(childNode);
		}
	}

	private void writeContainerLogs(final Document xmldoc, final Element parentNode, final List<RawContainerLog> containerLogs, final boolean bCorp) {
		Element node = xmldoc.createElement("containerlogs");
		for (RawContainerLog containerLog : containerLogs) {
			Element childNode = xmldoc.createElement("containerlog");
			setAttribute(childNode, "action", containerLog.getAction());
			setAttribute(childNode, "characterid", containerLog.getCharacterID());
			setAttribute(childNode, "containerid", containerLog.getContainerID());
			setAttribute(childNode, "containertypeid", containerLog.getContainerTypeID());
			setAttribute(childNode, "flagid", containerLog.getFlagID());
			setAttribute(childNode, "locationid", containerLog.getLocationID());
			setAttribute(childNode, "loggedat", containerLog.getLoggedAt());
			setAttributeOptional(childNode, "newconfigbitmask", containerLog.getNewConfigBitmask());
			setAttributeOptional(childNode, "oldconfigbitmask", containerLog.getOldConfigBitmask());
			setAttributeOptional(childNode, "passwordtype", containerLog.getPasswordType());
			setAttributeOptional(childNode, "quantity", containerLog.getQuantity());
			setAttributeOptional(childNode, "typeid", containerLog.getTypeID());
			node.appendChild(childNode);
		}
		parentNode.appendChild(node);
	}
}
