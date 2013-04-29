/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

import com.beimin.eveapi.shared.KeyType;
import com.beimin.eveapi.shared.accountbalance.EveAccountBalance;
import com.beimin.eveapi.shared.contract.ContractAvailability;
import com.beimin.eveapi.shared.contract.ContractStatus;
import com.beimin.eveapi.shared.contract.ContractType;
import com.beimin.eveapi.shared.contract.EveContract;
import com.beimin.eveapi.shared.contract.items.EveContractItem;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class ProfileReader extends AbstractXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileReader.class);

	private ProfileReader() { }

	public static boolean load(final Settings settings, ProfileManager profileManager, final String filename) {
		ProfileReader reader = new ProfileReader();
		return reader.read(settings, profileManager, filename);
	}

	private boolean read(final Settings settings, ProfileManager profileManager, final String filename) {
		try {
			Element element = getDocumentElement(filename);
			parseSettings(element, profileManager, settings);
		} catch (IOException ex) {
			LOG.info("Profile not loaded");
			return false;
		} catch (XmlException ex) {
			LOG.error("Profile not loaded: (" + filename + ") " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Profile loaded");
		return true;
	}

	private void parseSettings(final Element element, ProfileManager profileManager, final Settings settings) throws XmlException {
		if (!element.getNodeName().equals("assets")) {
			throw new XmlException("Wrong root element name.");
		}
		//Accounts
		NodeList accountNodes = element.getElementsByTagName("accounts");
		if (accountNodes.getLength() == 1) {
			Element accountsElement = (Element) accountNodes.item(0);
			parseAccounts(accountsElement, profileManager.getAccounts(), settings);
		}
	}

	private void parseAccounts(final Element element, final List<Account> accounts, final Settings settings) {
		NodeList accountNodes = element.getElementsByTagName("account");
		for (int i = 0; i < accountNodes.getLength(); i++) {
			Element currentNode = (Element) accountNodes.item(i);
			Account account = parseAccount(currentNode);
			parseOwners(currentNode, account, settings);
			accounts.add(account);
		}
	}

	private Account parseAccount(final Node node) {
		int keyID;
		if (AttributeGetters.haveAttribute(node, "keyid")) {
			keyID = AttributeGetters.getInt(node, "keyid");
		} else {
			keyID = AttributeGetters.getInt(node, "userid");
		}
		String vCode;
		if (AttributeGetters.haveAttribute(node, "vcode")) {
			vCode = AttributeGetters.getString(node, "vcode");
		} else {
			vCode = AttributeGetters.getString(node, "apikey");
		}
		Date nextUpdate = new Date(AttributeGetters.getLong(node, "charactersnextupdate"));
		String name = Integer.toString(keyID);
		if (AttributeGetters.haveAttribute(node, "name")) {
			name = AttributeGetters.getString(node, "name");
		}
		int accessMask = 0;
		if (AttributeGetters.haveAttribute(node, "accessmask")) {
			accessMask = AttributeGetters.getInt(node, "accessmask");
		}
		KeyType type = null;
		if (AttributeGetters.haveAttribute(node, "type")) {
			try {
				type = KeyType.valueOf(AttributeGetters.getString(node, "type"));
			} catch (IllegalArgumentException ex) {
				type = null;
			}
		}
		Date expires = null;
		if (AttributeGetters.haveAttribute(node, "expires")) {
			long i = AttributeGetters.getLong(node, "expires");
			if (i != 0) {
				expires = new Date(i);
			}
		}
		return new Account(keyID, vCode, name, nextUpdate, accessMask, type, expires);
	}

	private void parseOwners(final Element element, final Account account, final Settings settings) {
		NodeList ownerNodes =  element.getElementsByTagName("human");
		for (int i = 0; i < ownerNodes.getLength(); i++) {
			Element currentNode = (Element) ownerNodes.item(i);
			Owner owner = parseOwner(currentNode, account);
			account.getOwners().add(owner);
			NodeList assetNodes = currentNode.getElementsByTagName("assets");
			if (assetNodes.getLength() == 1) {
				parseAssets(assetNodes.item(0), owner, owner.getAssets(), null);
			}
			parseContracts(currentNode, owner, settings);
			parseBalances(currentNode, owner);
			parseMarkerOrders(currentNode, owner);
			parseWalletTransactions(currentNode, owner);
			parseIndustryJobs(currentNode, owner);
		}
	}

	private Owner parseOwner(final Node node, final Account account) {
		String name = AttributeGetters.getString(node, "name");
		int ownerID = AttributeGetters.getInt(node, "id");
		Date assetsNextUpdate = new Date(AttributeGetters.getLong(node, "assetsnextupdate"));
		Date balanceNextUpdate = new Date(AttributeGetters.getLong(node, "balancenextupdate"));
		boolean showAssets = true;
		if (AttributeGetters.haveAttribute(node, "show")) {
			showAssets = AttributeGetters.getBoolean(node, "show");
		}
		Date marketOrdersNextUpdate = Settings.getNow();
		if (AttributeGetters.haveAttribute(node, "marketordersnextupdate")) {
			marketOrdersNextUpdate = new Date(AttributeGetters.getLong(node, "marketordersnextupdate"));
		}
		Date walletTransactionsNextUpdate = Settings.getNow();
		if (AttributeGetters.haveAttribute(node, "wallettransactionsnextupdate")) {
			walletTransactionsNextUpdate = new Date(AttributeGetters.getLong(node, "wallettransactionsnextupdate"));
		}
		Date industryJobsNextUpdate = Settings.getNow();
		if (AttributeGetters.haveAttribute(node, "industryjobsnextupdate")) {
			industryJobsNextUpdate = new Date(AttributeGetters.getLong(node, "industryjobsnextupdate"));
		}
		Date contractsNextUpdate = Settings.getNow();
		if (AttributeGetters.haveAttribute(node, "contractsnextupdate")) {
			contractsNextUpdate = new Date(AttributeGetters.getLong(node, "contractsnextupdate"));
		}

		return new Owner(account, name, ownerID, showAssets, assetsNextUpdate, balanceNextUpdate, marketOrdersNextUpdate, walletTransactionsNextUpdate, industryJobsNextUpdate, contractsNextUpdate);
	}

	private void parseContracts(final Element element, final Owner owner, final Settings settings) {
		NodeList contractsNodes = element.getElementsByTagName("contracts");
		Map<EveContract, List<EveContractItem>> eveContracts = new HashMap<EveContract, List<EveContractItem>>();
		for (int a = 0; a < contractsNodes.getLength(); a++) {
			Element contractsNode = (Element) contractsNodes.item(a);
			NodeList contractNodes = contractsNode.getElementsByTagName("contract");
			for (int b = 0; b < contractNodes.getLength(); b++) {
				Element contractNode = (Element) contractNodes.item(b);
				EveContract contract = parseContract(contractNode);
				NodeList itemNodes = contractNode.getElementsByTagName("contractitem");
				List<EveContractItem> contractItems = new ArrayList<EveContractItem>();
				for (int c = 0; c < itemNodes.getLength(); c++) {
					Element currentNode = (Element) itemNodes.item(c);
					EveContractItem contractItem = parseContractItem(currentNode);
					contractItems.add(contractItem);
				}
				eveContracts.put(contract, contractItems);
			}
		}
		owner.setContracts(ApiConverter.convertContracts(eveContracts, settings));
	}

	private EveContract parseContract(final Element element) {
		EveContract contract = new EveContract();
		long acceptorID = AttributeGetters.getLong(element, "acceptorid");
		long assigneeID = AttributeGetters.getLong(element, "assigneeid");
		ContractAvailability availability
				= ContractAvailability.valueOf(AttributeGetters.getString(element, "availability"));
		double buyout = AttributeGetters.getDouble(element, "buyout");
		double collateral = AttributeGetters.getDouble(element, "collateral");
		long contractID = AttributeGetters.getLong(element, "contractid");
		Date dateAccepted;
		if (AttributeGetters.haveAttribute(element, "dateaccepted")) {
			dateAccepted = AttributeGetters.getDate(element, "dateaccepted");
		} else {
			dateAccepted = null;
		}
		Date dateCompleted;
		if (AttributeGetters.haveAttribute(element, "datecompleted")) {
			dateCompleted = AttributeGetters.getDate(element, "datecompleted");
		} else {
			dateCompleted = null;
		}
		Date dateExpired = AttributeGetters.getDate(element, "dateexpired");
		Date dateIssued = AttributeGetters.getDate(element, "dateissued");
		int endStationID = AttributeGetters.getInt(element, "endstationid");
		long issuerCorpID = AttributeGetters.getLong(element, "issuercorpid");
		long issuerID = AttributeGetters.getLong(element, "issuerid");
		int numDays = AttributeGetters.getInt(element, "numdays");
		double price = AttributeGetters.getDouble(element, "price");
		double reward = AttributeGetters.getDouble(element, "reward");
		int startStationID = AttributeGetters.getInt(element, "startstationid");
		ContractStatus status = ContractStatus.valueOf(AttributeGetters.getString(element, "status"));
		String title = AttributeGetters.getString(element, "title");
		ContractType type = ContractType.valueOf(AttributeGetters.getString(element, "type"));
		double volume = AttributeGetters.getDouble(element, "volume");
		boolean forCorp = AttributeGetters.getBoolean(element, "forcorp");

		contract.setAcceptorID(acceptorID);
		contract.setAssigneeID(assigneeID);
		contract.setAvailability(availability);
		contract.setBuyout(buyout);
		contract.setCollateral(collateral);
		contract.setContractID(contractID);
		contract.setDateAccepted(dateAccepted);
		contract.setDateCompleted(dateCompleted);
		contract.setDateExpired(dateExpired);
		contract.setDateIssued(dateIssued);
		contract.setEndStationID(endStationID);
		contract.setForCorp(forCorp);
		contract.setIssuerCorpID(issuerCorpID);
		contract.setIssuerID(issuerID);
		contract.setNumDays(numDays);
		contract.setPrice(price);
		contract.setReward(reward);
		contract.setStartStationID(startStationID);
		contract.setStatus(status);
		contract.setTitle(title);
		contract.setType(type);
		contract.setVolume(volume);

		return contract;
	}

	private EveContractItem parseContractItem(final Element element) {
		EveContractItem contractItem = new EveContractItem();
		boolean included = AttributeGetters.getBoolean(element, "included");
		long quantity = AttributeGetters.getLong(element, "quantity");
		long recordID = AttributeGetters.getLong(element, "recordid");
		boolean singleton = AttributeGetters.getBoolean(element, "singleton");
		int typeID = AttributeGetters.getInt(element, "typeid");
		Long rawQuantity = null;
		if (AttributeGetters.haveAttribute(element, "rawquantity")) {
			rawQuantity = AttributeGetters.getLong(element, "rawquantity");
		}
		contractItem.setIncluded(included);
		contractItem.setQuantity(quantity);
		contractItem.setRecordID(recordID);
		contractItem.setSingleton(singleton);
		contractItem.setTypeID(typeID);
		contractItem.setRawQuantity(rawQuantity);

		return contractItem;
	}

	private void parseBalances(final Element element, final Owner owner) {
		NodeList balancesNodes = element.getElementsByTagName("balances");
		for (int a = 0; a < balancesNodes.getLength(); a++) {
			Element currentBalancesNode = (Element) balancesNodes.item(a);
			NodeList balanceNodes = currentBalancesNode.getElementsByTagName("balance");
			for (int b = 0; b < balanceNodes.getLength(); b++) {
				Element currentNode = (Element) balanceNodes.item(b);
				EveAccountBalance accountBalance = parseBalance(currentNode);
				owner.getAccountBalances().add(new AccountBalance(accountBalance, owner));
			}
		}
	}

	private EveAccountBalance parseBalance(final Element element) {
		EveAccountBalance accountBalance = new EveAccountBalance();
		int accountID = AttributeGetters.getInt(element, "accountid");
		int accountKey = AttributeGetters.getInt(element, "accountkey");
		double balance = AttributeGetters.getDouble(element, "balance");
		accountBalance.setAccountID(accountID);
		accountBalance.setAccountKey(accountKey);
		accountBalance.setBalance(balance);
		return accountBalance;
	}

	private void parseMarkerOrders(final Element element, final Owner owner) {
		NodeList markerOrdersNodes = element.getElementsByTagName("markerorders");
		List<ApiMarketOrder> marketOrders = new ArrayList<ApiMarketOrder>();
		for (int a = 0; a < markerOrdersNodes.getLength(); a++) {
			Element currentMarkerOrdersNode = (Element) markerOrdersNodes.item(a);
			NodeList markerOrderNodes = currentMarkerOrdersNode.getElementsByTagName("markerorder");
			for (int b = 0; b < markerOrderNodes.getLength(); b++) {
				Element currentNode = (Element) markerOrderNodes.item(b);
				ApiMarketOrder apiMarketOrder = parseMarkerOrder(currentNode);
				marketOrders.add(apiMarketOrder);
			}
		}
		owner.setMarketOrders(ApiConverter.convertMarketOrders(marketOrders, owner));
	}

	private ApiMarketOrder parseMarkerOrder(final Element element) {
		ApiMarketOrder apiMarketOrder = new ApiMarketOrder();
		long orderID = AttributeGetters.getLong(element, "orderid");
		long charID = AttributeGetters.getLong(element, "charid");
		long stationID = AttributeGetters.getLong(element, "stationid");
		int volEntered = AttributeGetters.getInt(element, "volentered");
		int volRemaining = AttributeGetters.getInt(element, "volremaining");
		int minVolume = AttributeGetters.getInt(element, "minvolume");
		int orderState = AttributeGetters.getInt(element, "orderstate");
		int typeID = AttributeGetters.getInt(element, "typeid");
		int range = AttributeGetters.getInt(element, "range");
		int accountKey = AttributeGetters.getInt(element, "accountkey");
		int duration = AttributeGetters.getInt(element, "duration");
		double escrow = AttributeGetters.getDouble(element, "escrow");
		double price = AttributeGetters.getDouble(element, "price");
		int bid = AttributeGetters.getInt(element, "bid");
		Date issued = AttributeGetters.getDate(element, "issued");
		apiMarketOrder.setOrderID(orderID);
		apiMarketOrder.setCharID(charID);
		apiMarketOrder.setStationID(stationID);
		apiMarketOrder.setVolEntered(volEntered);
		apiMarketOrder.setVolRemaining(volRemaining);
		apiMarketOrder.setMinVolume(minVolume);
		apiMarketOrder.setOrderState(orderState);
		apiMarketOrder.setTypeID(typeID);
		apiMarketOrder.setRange(range);
		apiMarketOrder.setAccountKey(accountKey);
		apiMarketOrder.setDuration(duration);
		apiMarketOrder.setEscrow(escrow);
		apiMarketOrder.setPrice(price);
		apiMarketOrder.setBid(bid);
		apiMarketOrder.setIssued(issued);
		return apiMarketOrder;
	}

	private void parseWalletTransactions(final Element element, final Owner owner) {
		NodeList walletTransactionsNodes = element.getElementsByTagName("wallettransactions");
		List<ApiWalletTransaction> walletTransactions = new ArrayList<ApiWalletTransaction>();
		for (int a = 0; a < walletTransactionsNodes.getLength(); a++) {
			Element currentWalletTransactionsNode = (Element) walletTransactionsNodes.item(a);
			NodeList walletTransactionNodes = currentWalletTransactionsNode.getElementsByTagName("wallettransaction");
			for (int b = 0; b < walletTransactionNodes.getLength(); b++) {
				Element currentNode = (Element) walletTransactionNodes.item(b);
				ApiWalletTransaction apiWalletTransaction = parseWalletTransaction(currentNode);
				walletTransactions.add(apiWalletTransaction);
			}
		}
		owner.setWalletTransactions(ApiConverter.convertWalletTransactions(walletTransactions, owner));
	}

	private ApiWalletTransaction parseWalletTransaction(final Element element) {
		ApiWalletTransaction apiWalletTransaction = new ApiWalletTransaction();
		Date transactionDateTime = AttributeGetters.getDate(element, "transactiondatetime");
		Long transactionID = AttributeGetters.getLong(element, "transactionid");
		int quantity = AttributeGetters.getInt(element, "quantity");
		String typeName = AttributeGetters.getString(element, "typename");
		int typeID = AttributeGetters.getInt(element, "typeid");
		Double price = AttributeGetters.getDouble(element, "price");
		Long clientID = AttributeGetters.getLong(element, "clientid");
		String clientName = AttributeGetters.getString(element, "clientname");
		Long characterID = null;
		if (AttributeGetters.haveAttribute(element, "characterid")) {
			characterID = AttributeGetters.getLong(element, "characterid");
		}
		String characterName = null;
		if (AttributeGetters.haveAttribute(element, "charactername")) {
			characterName = AttributeGetters.getString(element, "charactername");
		}
		int stationID = AttributeGetters.getInt(element, "stationid");
		String stationName = AttributeGetters.getString(element, "stationname");
		String transactionType = AttributeGetters.getString(element, "transactiontype");
		String transactionFor = AttributeGetters.getString(element, "transactionfor");
		
		apiWalletTransaction.setTransactionDateTime(transactionDateTime);
		apiWalletTransaction.setTransactionID(transactionID);
		apiWalletTransaction.setQuantity(quantity);
		apiWalletTransaction.setTypeName(typeName);
		apiWalletTransaction.setTypeID(typeID);
		apiWalletTransaction.setPrice(price);
		apiWalletTransaction.setClientID(clientID);
		apiWalletTransaction.setClientName(clientName);
		apiWalletTransaction.setCharacterID(characterID);
		apiWalletTransaction.setCharacterName(characterName);
		apiWalletTransaction.setStationID(stationID);
		apiWalletTransaction.setStationName(stationName);
		apiWalletTransaction.setTransactionType(transactionType);
		apiWalletTransaction.setTransactionFor(transactionFor);
		return apiWalletTransaction;
	}

	private void parseIndustryJobs(final Element element, final Owner owner) {
		NodeList industryJobsNodes = element.getElementsByTagName("industryjobs");
		List<ApiIndustryJob> industryJobs = new ArrayList<ApiIndustryJob>();
		for (int a = 0; a < industryJobsNodes.getLength(); a++) {
			Element currentIndustryJobsNode = (Element) industryJobsNodes.item(a);
			NodeList industryJobNodes = currentIndustryJobsNode.getElementsByTagName("industryjob");
			for (int b = 0; b < industryJobNodes.getLength(); b++) {
				Element currentNode = (Element) industryJobNodes.item(b);
				ApiIndustryJob apiIndustryJob = parseIndustryJobs(currentNode);
				industryJobs.add(apiIndustryJob);
			}
		}
		owner.setIndustryJobs(ApiConverter.convertIndustryJobs(industryJobs, owner));
	}

	private ApiIndustryJob parseIndustryJobs(final Element element) {
		ApiIndustryJob apiIndustryJob = new ApiIndustryJob();

		long jobID = AttributeGetters.getLong(element, "jobid");
		long containerID = AttributeGetters.getLong(element, "containerid");
		long installedItemID = AttributeGetters.getLong(element, "installeditemid");
		long installedItemLocationID = AttributeGetters.getLong(element, "installeditemlocationid");
		int installedItemQuantity = AttributeGetters.getInt(element, "installeditemquantity");
		int installedItemProductivityLevel = AttributeGetters.getInt(element, "installeditemproductivitylevel");
		int installedItemMaterialLevel = AttributeGetters.getInt(element, "installeditemmateriallevel");
		int installedItemLicensedProductionRunsRemaining = AttributeGetters.getInt(element, "installeditemlicensedproductionrunsremaining");
		long outputLocationID = AttributeGetters.getLong(element, "outputlocationid");
		long installerID = AttributeGetters.getLong(element, "installerid");
		int runs = AttributeGetters.getInt(element, "runs");
		int licensedProductionRuns = AttributeGetters.getInt(element, "licensedproductionruns");
		long installedInSolarSystemID = AttributeGetters.getLong(element, "installedinsolarsystemid");
		long containerLocationID = AttributeGetters.getLong(element, "containerlocationid");
		double materialMultiplier = AttributeGetters.getDouble(element, "materialmultiplier");
		double charMaterialMultiplier = AttributeGetters.getDouble(element, "charmaterialmultiplier");
		double timeMultiplier = AttributeGetters.getDouble(element, "timemultiplier");
		double charTimeMultiplier = AttributeGetters.getDouble(element, "chartimemultiplier");
		int installedItemTypeID = AttributeGetters.getInt(element, "installeditemtypeid");
		int outputTypeID = AttributeGetters.getInt(element, "outputtypeid");
		int containerTypeID = AttributeGetters.getInt(element, "containertypeid");
		long installedItemCopy = AttributeGetters.getLong(element, "installeditemcopy");
		boolean completed = AttributeGetters.getBoolean(element, "completed");
		boolean completedSuccessfully = AttributeGetters.getBoolean(element, "completedsuccessfully");
		int installedItemFlag = AttributeGetters.getInt(element, "installeditemflag");
		int outputFlag = AttributeGetters.getInt(element, "outputflag");
		int activityID = AttributeGetters.getInt(element, "activityid");
		int completedStatus = AttributeGetters.getInt(element, "completedstatus");
		Date installTime = AttributeGetters.getDate(element, "installtime");
		Date beginProductionTime = AttributeGetters.getDate(element, "beginproductiontime");
		Date endProductionTime = AttributeGetters.getDate(element, "endproductiontime");
		Date pauseProductionTime = AttributeGetters.getDate(element, "pauseproductiontime");
		long assemblyLineId = AttributeGetters.getLong(element, "assemblylineid");

		apiIndustryJob.setJobID(jobID);
		apiIndustryJob.setContainerID(containerID);
		apiIndustryJob.setInstalledItemID(installedItemID);
		apiIndustryJob.setInstalledItemLocationID(installedItemLocationID);
		apiIndustryJob.setInstalledItemQuantity(installedItemQuantity);
		apiIndustryJob.setInstalledItemProductivityLevel(installedItemProductivityLevel);
		apiIndustryJob.setInstalledItemMaterialLevel(installedItemMaterialLevel);
		apiIndustryJob.setInstalledItemLicensedProductionRunsRemaining(installedItemLicensedProductionRunsRemaining);
		apiIndustryJob.setOutputLocationID(outputLocationID);
		apiIndustryJob.setInstallerID(installerID);
		apiIndustryJob.setRuns(runs);
		apiIndustryJob.setLicensedProductionRuns(licensedProductionRuns);
		apiIndustryJob.setInstalledInSolarSystemID(installedInSolarSystemID);
		apiIndustryJob.setContainerLocationID(containerLocationID);
		apiIndustryJob.setMaterialMultiplier(materialMultiplier);
		apiIndustryJob.setCharMaterialMultiplier(charMaterialMultiplier);
		apiIndustryJob.setTimeMultiplier(timeMultiplier);
		apiIndustryJob.setCharTimeMultiplier(charTimeMultiplier);
		apiIndustryJob.setInstalledItemTypeID(installedItemTypeID);
		apiIndustryJob.setOutputTypeID(outputTypeID);
		apiIndustryJob.setContainerTypeID(containerTypeID);
		apiIndustryJob.setInstalledItemCopy(installedItemCopy);
		apiIndustryJob.setCompleted(completed);
		apiIndustryJob.setCompletedSuccessfully(completedSuccessfully);
		apiIndustryJob.setInstalledItemFlag(installedItemFlag);
		apiIndustryJob.setOutputFlag(outputFlag);
		apiIndustryJob.setActivityID(activityID);
		apiIndustryJob.setCompletedStatus(completedStatus);
		apiIndustryJob.setInstallTime(installTime);
		apiIndustryJob.setBeginProductionTime(beginProductionTime);
		apiIndustryJob.setEndProductionTime(endProductionTime);
		apiIndustryJob.setPauseProductionTime(pauseProductionTime);
		apiIndustryJob.setAssemblyLineID(assemblyLineId);

		return apiIndustryJob;
	}

	private void parseAssets(final Node node, final Owner owner, final List<Asset> assets, final Asset parentEveAsset) {
		NodeList assetsNodes = node.getChildNodes();
		for (int i = 0; i < assetsNodes.getLength(); i++) {
			Node currentNode = assetsNodes.item(i);
			if (currentNode.getNodeName().equals("asset")) {
				Asset eveAsset = parseEveAsset(currentNode, owner, parentEveAsset);
				if (parentEveAsset == null) {
					assets.add(eveAsset);
				} else {
					parentEveAsset.addEveAsset(eveAsset);
				}
				parseAssets(currentNode, owner, assets, eveAsset);
			}
		}
	}

	private Asset parseEveAsset(final Node node, final Owner owner, final Asset parentEveAsset) {
		long count = AttributeGetters.getLong(node, "count");

		long itemId = AttributeGetters.getLong(node, "id");
		int typeID = AttributeGetters.getInt(node, "typeid");
		long locationID = AttributeGetters.getInt(node, "locationid");
		if (locationID == 0 && parentEveAsset != null) {
			locationID = parentEveAsset.getLocation().getLocationID();
		}
		boolean singleton = AttributeGetters.getBoolean(node, "singleton");
		int rawQuantity = 0;
		if (AttributeGetters.haveAttribute(node, "rawquantity")) {
			rawQuantity = AttributeGetters.getInt(node, "rawquantity");
		}
		int flagID = 0;
		if (AttributeGetters.haveAttribute(node, "flagid")) {
			flagID = AttributeGetters.getInt(node, "flagid");
		} else { //Workaround for the old system
			String flag = AttributeGetters.getString(node, "flag");
			for (ItemFlag itemFlag : StaticData.get().getItemFlags().values()) {
				if (flag.equals(itemFlag.getFlagName())) {
					flagID = itemFlag.getFlagID();
					break;
				}
			}
		}
		return ApiConverter.createAsset(parentEveAsset, owner, count, flagID, itemId, typeID, locationID, singleton, rawQuantity, null);
	}
}
