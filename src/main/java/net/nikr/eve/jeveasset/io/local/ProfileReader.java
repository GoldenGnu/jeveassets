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

import com.beimin.eveapi.model.shared.KeyType;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
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
import net.nikr.eve.jeveasset.data.api.raw.RawAccountBalance;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog.ContainerAction;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog.ContainerPasswordType;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractAvailability;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractStatus;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractType;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalExtraInfo;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class ProfileReader extends AbstractXmlReader<Boolean> {

	private ProfileManager profileManager;

	public static boolean load(ProfileManager profileManager, final String filename) {
		ProfileReader reader = new ProfileReader();
		reader.profileManager = profileManager;
		return reader.read(filename, filename, XmlType.DYNAMIC_BACKUP);
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		parseSettings(element, profileManager);
		return true;
	}

	@Override
	protected Boolean failValue() {
		return false;
	}

	@Override
	protected Boolean doNotExistValue() {
		return true;
	}

	private void parseSettings(final Element element, ProfileManager profileManager) throws XmlException {
		if (!element.getNodeName().equals("assets")) {
			throw new XmlException("Wrong root element name.");
		}
		//Eve XML Api
		NodeList accountNodes = element.getElementsByTagName("accounts");
		if (accountNodes.getLength() == 1) {
			Element accountsElement = (Element) accountNodes.item(0);
			parseAccounts(accountsElement, profileManager.getAccounts());
		}
		//EveKit
		NodeList eveKitOwnersNodes = element.getElementsByTagName("evekitowners");
		if (eveKitOwnersNodes.getLength() == 1) {
			Element eveKitOwnersElement = (Element) eveKitOwnersNodes.item(0);
			parseEveKitOwners(eveKitOwnersElement, profileManager.getEveKitOwners());
		}
		//Esi
		NodeList esiOwnersNodes = element.getElementsByTagName("esiowners");
		if (esiOwnersNodes.getLength() == 1) {
			Element esiElement = (Element) esiOwnersNodes.item(0);
			parseEsiOwners(esiElement, profileManager.getEsiOwners());
		}
	}

	private void parseEsiOwners(final Element element, final List<EsiOwner> esiOwners) throws XmlException {
		NodeList ownerNodes = element.getElementsByTagName("esiowner");
		for (int i = 0; i < ownerNodes.getLength(); i++) {
			Element currentNode = (Element) ownerNodes.item(i);
			String accountName = AttributeGetters.getString(currentNode, "accountname");
			String refreshToken = AttributeGetters.getString(currentNode, "refreshtoken");
			String scopes = AttributeGetters.getString(currentNode, "scopes");
			String tokenType = AttributeGetters.getString(currentNode, "tokentype");
			String characterOwnerHash = AttributeGetters.getString(currentNode, "characterownerhash");
			String intellectualProperty = AttributeGetters.getString(currentNode, "intellectualproperty");
			Date structuresNextUpdate = AttributeGetters.getDate(currentNode, "structuresnextupdate");
			Date accountNextUpdate = AttributeGetters.getDate(currentNode, "accountnextupdate");
			EsiCallbackURL callbackURL = EsiCallbackURL.valueOf(AttributeGetters.getString(currentNode, "callbackurl"));
			Set<RolesEnum> roles = EnumSet.noneOf(RolesEnum.class);
			if (AttributeGetters.haveAttribute(currentNode, "characterroles")) {
				for (String role : AttributeGetters.getString(currentNode, "characterroles").split(",")) {
					try {
						roles.add(RolesEnum.valueOf(role));
					} catch (IllegalArgumentException ex) {
						
					}
				}
			}
			EsiOwner owner = new EsiOwner();
			owner.setRoles(roles);
			owner.setAccountName(accountName);
			owner.setRefreshToken(refreshToken);
			owner.setScopes(scopes);
			owner.setTokenType(tokenType);
			owner.setCharacterOwnerHash(characterOwnerHash);
			owner.setIntellectualProperty(intellectualProperty);
			owner.setStructuresNextUpdate(structuresNextUpdate);
			owner.setAccountNextUpdate(accountNextUpdate);
			owner.setCallbackURL(callbackURL);

			parseOwnerType(currentNode, owner);
			esiOwners.add(owner);
		}
	}

	private void parseEveKitOwners(final Element element, final List<EveKitOwner> eveKitOwners) throws XmlException {
		NodeList ownerNodes = element.getElementsByTagName("evekitowner");
		for (int i = 0; i < ownerNodes.getLength(); i++) {
			Element currentNode = (Element) ownerNodes.item(i);
			int accessKey = AttributeGetters.getInt(currentNode, "accesskey");
			String accessCred = AttributeGetters.getString(currentNode, "accesscred");
			Date expire = AttributeGetters.getDateOptional(currentNode, "expire");
			long accessmask = AttributeGetters.getLong(currentNode, "accessmask");
			boolean corporation = AttributeGetters.getBoolean(currentNode, "corporation");
			Date limit = AttributeGetters.getDateOptional(currentNode, "limit");
			String accountName = AttributeGetters.getString(currentNode, "accountname");
			//ContID
			Long journalCID = AttributeGetters.getLongOptional(currentNode, "journalcid");
			Long transactionsCID = AttributeGetters.getLongOptional(currentNode, "transactionscid");
			Long contractsCID = AttributeGetters.getLongOptional(currentNode, "contractscid");
			Long industryJobsCID = AttributeGetters.getLongOptional(currentNode, "industryjobscid");
			Long marketOrdersCID = AttributeGetters.getLongOptional(currentNode, "marketorderscid");
			Date accountNextUpdate = AttributeGetters.getDateOptional(currentNode, "accountnextupdate");
			EveKitOwner owner = new EveKitOwner(accessKey, accessCred, expire, accessmask, corporation, limit, accountName);
			owner.setJournalCID(journalCID);
			owner.setTransactionsCID(transactionsCID);
			owner.setContractsCID(contractsCID);
			owner.setIndustryJobsCID(industryJobsCID);
			owner.setMarketOrdersCID(marketOrdersCID);
			owner.setAccountNextUpdate(accountNextUpdate);
			parseOwnerType(currentNode, owner);
			eveKitOwners.add(owner);
		}
	}

	private void parseAccounts(final Element element, final List<EveApiAccount> accounts) throws XmlException {
		NodeList accountNodes = element.getElementsByTagName("account");
		for (int i = 0; i < accountNodes.getLength(); i++) {
			Element currentNode = (Element) accountNodes.item(i);
			EveApiAccount account = parseAccount(currentNode);
			parseOwners(currentNode, account);
			accounts.add(account);
		}
	}

	private EveApiAccount parseAccount(final Node node) throws XmlException {
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
		Date nextUpdate = AttributeGetters.getDate(node, "charactersnextupdate");
		String name = Integer.toString(keyID);
		if (AttributeGetters.haveAttribute(node, "name")) {
			name = AttributeGetters.getString(node, "name");
		}
		long accessMask = 0;
		if (AttributeGetters.haveAttribute(node, "accessmask")) {
			accessMask = AttributeGetters.getLong(node, "accessmask");
		}
		KeyType type = null;
		if (AttributeGetters.haveAttribute(node, "type")) {
			type = KeyType.valueOf(AttributeGetters.getString(node, "type").toUpperCase());
		}
		Date expires = null;
		if (AttributeGetters.haveAttribute(node, "expires")) {
			long i = AttributeGetters.getLong(node, "expires");
			if (i != 0) {
				expires = new Date(i);
			}
		}
		boolean invalid = false;
		if (AttributeGetters.haveAttribute(node, "invalid")) {
			invalid = AttributeGetters.getBoolean(node, "invalid");
		}
		return new EveApiAccount(keyID, vCode, name, nextUpdate, accessMask, type, expires, invalid);
	}

	private void parseOwners(final Element element, final EveApiAccount account) throws XmlException {
		NodeList ownerNodes = element.getElementsByTagName("human");
		for (int i = 0; i < ownerNodes.getLength(); i++) {
			Element currentNode = (Element) ownerNodes.item(i);
			boolean migrated = false;
			if (AttributeGetters.haveAttribute(currentNode, "migrated")) {
				migrated = AttributeGetters.getBoolean(currentNode, "migrated");
			}
			EveApiOwner owner = new EveApiOwner(account, migrated);
			parseOwnerType(currentNode, owner);
			account.getOwners().add(owner);
		}
	}

	private void parseOwnerType(final Element node, OwnerType owner) throws XmlException {
		String ownerName = AttributeGetters.getString(node, "name");
		long ownerID = AttributeGetters.getLong(node, "id");
		Date assetsNextUpdate = AttributeGetters.getDateNotNull(node, "assetsnextupdate");
		Date assetsLastUpdate = AttributeGetters.getDateOptional(node, "assetslastupdate");
		Date balanceNextUpdate = AttributeGetters.getDateNotNull(node, "balancenextupdate");
		Date balanceLastUpdate = AttributeGetters.getDateOptional(node, "balancelastupdate");
		boolean showOwner = true;
		if (AttributeGetters.haveAttribute(node, "show")) {
			showOwner = AttributeGetters.getBoolean(node, "show");
		}
		Date marketOrdersNextUpdate = AttributeGetters.getDateNotNull(node, "marketordersnextupdate");
		Date journalNextUpdate = AttributeGetters.getDateNotNull(node, "journalnextupdate");
		Date transactionsNextUpdate = AttributeGetters.getDateNotNull(node, "wallettransactionsnextupdate");
		Date industryJobsNextUpdate = AttributeGetters.getDateNotNull(node, "industryjobsnextupdate");
		Date contractsNextUpdate = AttributeGetters.getDateNotNull(node, "contractsnextupdate");
		Date locationsNextUpdate = AttributeGetters.getDateNotNull(node, "locationsnextupdate");
		Date blueprintsNextUpdate = AttributeGetters.getDateNotNull(node, "blueprintsnextupdate");
		Date containerLogsNextUpdate = AttributeGetters.getDateNotNull(node, "containerlogsnextupdate");
		owner.setOwnerName(ownerName);
		owner.setOwnerID(ownerID);
		owner.setAssetNextUpdate(assetsNextUpdate);
		owner.setAssetLastUpdate(assetsLastUpdate);
		owner.setBalanceNextUpdate(balanceNextUpdate);
		owner.setBalanceLastUpdate(balanceLastUpdate);
		owner.setShowOwner(showOwner);
		owner.setMarketOrdersNextUpdate(marketOrdersNextUpdate);
		owner.setJournalNextUpdate(journalNextUpdate);
		owner.setTransactionsNextUpdate(transactionsNextUpdate);
		owner.setIndustryJobsNextUpdate(industryJobsNextUpdate);
		owner.setContractsNextUpdate(contractsNextUpdate);
		owner.setLocationsNextUpdate(locationsNextUpdate);
		owner.setBlueprintsNextUpdate(blueprintsNextUpdate);
		owner.setContainerLogsNextUpdate(containerLogsNextUpdate);

		NodeList assetNodes = node.getElementsByTagName("assets");
		if (assetNodes.getLength() == 1) {
			parseAssets(assetNodes.item(0), owner, owner.getAssets(), null);
		}
		parseContracts(node, owner);
		parseBalances(node, owner);
		parseMarketOrders(node, owner);
		parseJournals(node, owner);
		parseTransactions(node, owner);
		parseIndustryJobs(node, owner);
		parseBlueprints(node, owner);
		parseContainerLogs(node, owner);
	}

	private void parseContracts(final Element element, final OwnerType owner) throws XmlException {
		NodeList contractsNodes = element.getElementsByTagName("contracts");
		Map<MyContract, List<MyContractItem>> contracts = new HashMap<MyContract, List<MyContractItem>>();
		for (int a = 0; a < contractsNodes.getLength(); a++) {
			Element contractsNode = (Element) contractsNodes.item(a);
			NodeList contractNodes = contractsNode.getElementsByTagName("contract");
			for (int b = 0; b < contractNodes.getLength(); b++) {
				Element contractNode = (Element) contractNodes.item(b);
				RawContract rawContract = parseContract(contractNode);
				MyContract contract = DataConverter.toMyContract(rawContract);
				NodeList itemNodes = contractNode.getElementsByTagName("contractitem");
				List<MyContractItem> contractItems = new ArrayList<MyContractItem>();
				for (int c = 0; c < itemNodes.getLength(); c++) {
					Element currentNode = (Element) itemNodes.item(c);
					RawContractItem rawContractItem = parseContractItem(currentNode);
					MyContractItem contractItem = DataConverter.toMyContractItem(rawContractItem, contract);
					contractItems.add(contractItem);
				}

				contracts.put(contract, contractItems);
			}
		}
		owner.setContracts(contracts);
	}

	private RawContract parseContract(final Element element) throws XmlException {
		RawContract contract = RawContract.create();
		Integer acceptorID = AttributeGetters.getInt(element, "acceptorid");
		Integer assigneeID = AttributeGetters.getInt(element, "assigneeid");
		ContractAvailability availability = RawConverter.toContractAvailability(AttributeGetters.getString(element, "availability"));
		Double buyout = AttributeGetters.getDoubleOptional(element, "buyout");
		Double collateral = AttributeGetters.getDoubleOptional(element, "collateral");
		Integer contractID = AttributeGetters.getInt(element, "contractid");
		Date dateAccepted = AttributeGetters.getDateOptional(element, "dateaccepted");
		Date dateCompleted = AttributeGetters.getDateOptional(element, "datecompleted");
		Date dateExpired = AttributeGetters.getDate(element, "dateexpired");
		Date dateIssued = AttributeGetters.getDate(element, "dateissued");
		Long endLocationID = AttributeGetters.getLongOptional(element, "endstationid");
		Integer issuerCorporationID = AttributeGetters.getInt(element, "issuercorpid");
		Integer issuerID = AttributeGetters.getInt(element, "issuerid");
		Integer daysToComplete = AttributeGetters.getIntOptional(element, "numdays");
		Double price = AttributeGetters.getDoubleOptional(element, "price");
		Double reward = AttributeGetters.getDoubleOptional(element, "reward");
		Long startLocationID = AttributeGetters.getLongOptional(element, "startstationid");
		ContractStatus status = RawConverter.toContractStatus(AttributeGetters.getString(element, "status"));
		String title = AttributeGetters.getStringOptional(element, "title");
		ContractType type = RawConverter.toContractType(AttributeGetters.getString(element, "type"));
		Double volume = AttributeGetters.getDoubleOptional(element, "volume");
		boolean forCorporation = AttributeGetters.getBoolean(element, "forcorp");

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
		contract.setDaysToComplete(daysToComplete);
		contract.setEndLocationID(endLocationID);
		contract.setForCorporation(forCorporation);
		contract.setIssuerCorporationID(issuerCorporationID);
		contract.setIssuerID(issuerID);
		contract.setPrice(price);
		contract.setReward(reward);
		contract.setStartLocationID(startLocationID);
		contract.setStatus(status);
		contract.setTitle(title);
		contract.setType(type);
		contract.setVolume(volume);

		return contract;
	}

	private RawContractItem parseContractItem(final Element element) throws XmlException {
		RawContractItem contractItem = RawContractItem.create();
		boolean included = AttributeGetters.getBoolean(element, "included");
		Integer quantity = AttributeGetters.getInt(element, "quantity");
		long recordID = AttributeGetters.getLong(element, "recordid");
		boolean singleton = AttributeGetters.getBoolean(element, "singleton");
		int typeID = AttributeGetters.getInt(element, "typeid");
		Integer rawQuantity = AttributeGetters.getIntOptional(element, "rawquantity");
		contractItem.setIncluded(included);
		contractItem.setQuantity(quantity);
		contractItem.setRecordID(recordID);
		contractItem.setSingleton(singleton);
		contractItem.setTypeID(typeID);
		contractItem.setRawQuantity(rawQuantity);

		return contractItem;
	}

	private void parseBalances(final Element element, final OwnerType owner) throws XmlException {
		List<MyAccountBalance> accountBalances = new ArrayList<MyAccountBalance>();
		NodeList balancesNodes = element.getElementsByTagName("balances");
		for (int a = 0; a < balancesNodes.getLength(); a++) {
			Element currentBalancesNode = (Element) balancesNodes.item(a);
			NodeList balanceNodes = currentBalancesNode.getElementsByTagName("balance");
			for (int b = 0; b < balanceNodes.getLength(); b++) {
				Element currentNode = (Element) balanceNodes.item(b);
				RawAccountBalance rawAccountBalance = parseBalance(currentNode);
				MyAccountBalance accountBalance = DataConverter.toMyAccountBalance(rawAccountBalance, owner);
				accountBalances.add(accountBalance);
			}
		}
		owner.setAccountBalances(accountBalances);
	}

	private RawAccountBalance parseBalance(final Element element) throws XmlException {
		RawAccountBalance accountBalance = RawAccountBalance.create();
		int accountKey = AttributeGetters.getInt(element, "accountkey");
		Double balance = AttributeGetters.getDouble(element, "balance");
		accountBalance.setAccountKey(accountKey);
		accountBalance.setBalance(balance);
		return accountBalance;
	}

	private void parseMarketOrders(final Element element, final OwnerType owner) throws XmlException {
		NodeList marketOrdersNodes = element.getElementsByTagName("markerorders");
		Set<MyMarketOrder> marketOrders = new HashSet<MyMarketOrder>();
		for (int a = 0; a < marketOrdersNodes.getLength(); a++) {
			Element currentMarketOrdersNode = (Element) marketOrdersNodes.item(a);
			NodeList marketOrderNodes = currentMarketOrdersNode.getElementsByTagName("markerorder");
			for (int b = 0; b < marketOrderNodes.getLength(); b++) {
				Element currentNode = (Element) marketOrderNodes.item(b);
				RawMarketOrder rawMarketOrder = parseMarketOrder(currentNode, owner);
				MyMarketOrder marketOrder = DataConverter.toMyMarketOrder(rawMarketOrder, owner);
				marketOrders.add(marketOrder);
			}
		}
		owner.setMarketOrders(marketOrders);
	}

	private RawMarketOrder parseMarketOrder(final Element element, final OwnerType owner) throws XmlException {
		RawMarketOrder apiMarketOrder = RawMarketOrder.create();
		long orderID = AttributeGetters.getLong(element, "orderid");
		long locationID = AttributeGetters.getLong(element, "stationid");
		int volEntered = AttributeGetters.getInt(element, "volentered");
		int volRemaining = AttributeGetters.getInt(element, "volremaining");
		int minVolume = AttributeGetters.getInt(element, "minvolume");
		int state = AttributeGetters.getInt(element, "orderstate");
		int typeID = AttributeGetters.getInt(element, "typeid");
		int range = AttributeGetters.getInt(element, "range");
		int accountID = AttributeGetters.getInt(element, "accountkey");
		int duration = AttributeGetters.getInt(element, "duration");
		Double escrow = AttributeGetters.getDouble(element, "escrow");
		Double price = AttributeGetters.getDouble(element, "price");
		int bid = AttributeGetters.getInt(element, "bid");
		Date issued = AttributeGetters.getDate(element, "issued");
		boolean corp = owner.isCorporation();
		if (AttributeGetters.haveAttribute(element, "corp")) {
			corp = AttributeGetters.getBoolean(element, "corp");
		}
		apiMarketOrder.setWalletDivision(accountID);
		apiMarketOrder.setDuration(duration);
		apiMarketOrder.setEscrow(escrow);
		apiMarketOrder.setBuyOrder(bid > 0);
		apiMarketOrder.setCorp(corp);
		apiMarketOrder.setIssued(issued);
		apiMarketOrder.setLocationID(locationID);
		apiMarketOrder.setMinVolume(minVolume);
		apiMarketOrder.setOrderID(orderID);
		apiMarketOrder.setPrice(price);
		apiMarketOrder.setRange(RawConverter.toMarketOrderRange(range));
		apiMarketOrder.setRegionID((int) ApiIdConverter.getLocation(locationID).getRegionID());
		apiMarketOrder.setState(RawConverter.toMarketOrderState(state));
		apiMarketOrder.setTypeID(typeID);
		apiMarketOrder.setVolumeRemain(volRemaining);
		apiMarketOrder.setVolumeTotal(volEntered);
		return apiMarketOrder;
	}

	private void parseJournals(final Element element, final OwnerType owner) throws XmlException {
		NodeList journalsNodes = element.getElementsByTagName("journals");
		Set<MyJournal> journals = new HashSet<MyJournal>();
		for (int a = 0; a < journalsNodes.getLength(); a++) {
			Element currentAalletJournalsNode = (Element) journalsNodes.item(a);
			NodeList journalNodes = currentAalletJournalsNode.getElementsByTagName("journal");
			for (int b = 0; b < journalNodes.getLength(); b++) {
				Element currentNode = (Element) journalNodes.item(b);
				RawJournal rawJournal = parseJournal(currentNode);
				MyJournal journal = DataConverter.toMyJournal(rawJournal, owner);
				journals.add(journal);
			}
		}
		owner.setJournal(journals);
	}

	private RawJournal parseJournal(final Element element) throws XmlException {
		//Base
		RawJournal rawJournal = RawJournal.create();
		Double amount = AttributeGetters.getDoubleOptional(element, "amount");
		Long argID = AttributeGetters.getLongOptional(element, "argid1");
		String argName = AttributeGetters.getStringOptional(element, "argname1");
		Double balance = AttributeGetters.getDoubleOptional(element, "balance");
		Date date = AttributeGetters.getDate(element, "date");
		Integer firstPartyID = AttributeGetters.getIntOptional(element, "ownerid1");
		Integer secondPartyID = AttributeGetters.getIntOptional(element, "ownerid2");
		String reason = AttributeGetters.getStringOptional(element, "reason");
		long refID = AttributeGetters.getLong(element, "refid");
		int refTypeID = AttributeGetters.getInt(element, "reftypeid");
		Double taxAmount = AttributeGetters.getDoubleOptional(element, "taxamount");
		Integer taxReceiverID = AttributeGetters.getIntOptional(element, "taxreceiverid");
		//New
		Integer firstPartyTypeID = AttributeGetters.getIntOptional(element, "owner1typeid");
		Integer secondPartyTypeID = AttributeGetters.getIntOptional(element, "owner2typeid");
		//Extra
		int accountKey = AttributeGetters.getInt(element, "accountkey");

		rawJournal.setAmount(amount);
		rawJournal.setBalance(balance);
		rawJournal.setDate(date);
		rawJournal.setFirstPartyID(firstPartyID);
		rawJournal.setFirstPartyType(RawConverter.toJournalPartyType(firstPartyTypeID));
		rawJournal.setReason(reason);
		rawJournal.setRefID(refID);
		rawJournal.setRefType(RawConverter.toJournalRefType(refTypeID));
		rawJournal.setSecondPartyID(secondPartyID);
		rawJournal.setSecondPartyType(RawConverter.toJournalPartyType(secondPartyTypeID));
		rawJournal.setTax(taxAmount);
		rawJournal.setTaxReceiverId(taxReceiverID);
		rawJournal.setExtraInfo(new RawJournalExtraInfo(argID, argName, RawConverter.toJournalRefType(refTypeID)));
		rawJournal.setAccountKey(accountKey);
		return rawJournal;
	}

	private void parseTransactions(final Element element, final OwnerType owner) throws XmlException {
		NodeList transactionsNodes = element.getElementsByTagName("wallettransactions");
		Set<MyTransaction> transactions = new HashSet<MyTransaction>();
		for (int a = 0; a < transactionsNodes.getLength(); a++) {
			Element currentTransactionsNode = (Element) transactionsNodes.item(a);
			NodeList transactionNodes = currentTransactionsNode.getElementsByTagName("wallettransaction");
			for (int b = 0; b < transactionNodes.getLength(); b++) {
				Element currentNode = (Element) transactionNodes.item(b);
				RawTransaction rawTransaction = parseTransaction(currentNode);
				MyTransaction transaction = DataConverter.toMyTransaction(rawTransaction, owner);
				transactions.add(transaction);
			}
		}
		owner.setTransactions(transactions);
	}

	private RawTransaction parseTransaction(final Element element) throws XmlException {
		RawTransaction rawTransaction = RawTransaction.create();
		Date date = AttributeGetters.getDate(element, "transactiondatetime");
		Long transactionID = AttributeGetters.getLong(element, "transactionid");
		int quantity = AttributeGetters.getInt(element, "quantity");
		int typeID = AttributeGetters.getInt(element, "typeid");
		Double price = AttributeGetters.getDouble(element, "price");
		Integer clientID = AttributeGetters.getInt(element, "clientid");
		long locationID = AttributeGetters.getLong(element, "stationid");
		String transactionType = AttributeGetters.getString(element, "transactiontype");
		String transactionFor = AttributeGetters.getString(element, "transactionfor");

		//New
		Long journalRefID;
		if (AttributeGetters.haveAttribute(element, "journaltransactionid")) {
			journalRefID = AttributeGetters.getLong(element, "journaltransactionid");
		} else {
			journalRefID = 0L; //Legacy support
		}

		//Extra
		int accountKey = 1000;
		if (AttributeGetters.haveAttribute(element, "accountkey")) {
			accountKey = AttributeGetters.getInt(element, "accountkey");
		}
		rawTransaction.setClientID(clientID);
		rawTransaction.setDate(date);
		rawTransaction.setBuy(RawConverter.toTransactionIsBuy(transactionType));
		rawTransaction.setPersonal(RawConverter.toTransactionIsPersonal(transactionFor));
		rawTransaction.setJournalRefID(journalRefID);
		rawTransaction.setLocationID(locationID);
		rawTransaction.setQuantity(quantity);
		rawTransaction.setTransactionID(transactionID);
		rawTransaction.setTypeID(typeID);
		rawTransaction.setUnitPrice(price);
		rawTransaction.setAccountKey(accountKey);
		return rawTransaction;
	}

	private void parseIndustryJobs(final Element element, final OwnerType owner) throws XmlException {
		NodeList industryJobsNodes = element.getElementsByTagName("industryjobs");
		List<MyIndustryJob> industryJobs = new ArrayList<MyIndustryJob>();
		for (int a = 0; a < industryJobsNodes.getLength(); a++) {
			Element currentIndustryJobsNode = (Element) industryJobsNodes.item(a);
			NodeList industryJobNodes = currentIndustryJobsNode.getElementsByTagName("industryjob");
			for (int b = 0; b < industryJobNodes.getLength(); b++) {
				Element currentNode = (Element) industryJobNodes.item(b);
				if (AttributeGetters.haveAttribute(currentNode, "blueprintid")) {
					RawIndustryJob rawIndustryJob = parseIndustryJob(currentNode);
					MyIndustryJob industryJob = DataConverter.toMyIndustryJob(rawIndustryJob, owner);
					industryJobs.add(industryJob);
				}
			}
		}
		owner.setIndustryJobs(industryJobs);
	}

	private RawIndustryJob parseIndustryJob(final Element element) throws XmlException {
		RawIndustryJob rawIndustryJob = RawIndustryJob.create();
		Integer jobID = AttributeGetters.getInt(element, "jobid");
		Integer installerID = AttributeGetters.getInt(element, "installerid");
		long facilityID = AttributeGetters.getLong(element, "facilityid");
		long stationID = AttributeGetters.getLong(element, "stationid");
		int activityID = AttributeGetters.getInt(element, "activityid");
		long blueprintID = AttributeGetters.getLong(element, "blueprintid");
		int blueprintTypeID = AttributeGetters.getInt(element, "blueprinttypeid");
		long blueprintLocationID = AttributeGetters.getLong(element, "blueprintlocationid");
		long outputLocationID = AttributeGetters.getLong(element, "outputlocationid");
		int runs = AttributeGetters.getInt(element, "runs");
		Double cost = AttributeGetters.getDoubleOptional(element, "cost");
		Integer licensedRuns = AttributeGetters.getIntOptional(element, "licensedruns");
		Float probability = AttributeGetters.getFloatOptional(element, "probability");
		Integer productTypeID = AttributeGetters.getIntOptional(element, "producttypeid");
		int status = AttributeGetters.getInt(element, "status");
		int duration = AttributeGetters.getInt(element, "timeinseconds");
		Date startDate = AttributeGetters.getDate(element, "startdate");
		Date endDate = AttributeGetters.getDate(element, "enddate");
		Date pauseDate = AttributeGetters.getDateOptional(element, "pausedate");
		Date completedDate = AttributeGetters.getDateOptional(element, "completeddate");
		Integer completedCharacterID = AttributeGetters.getIntOptional(element, "completedcharacterid");
		Integer successfulRuns = AttributeGetters.getIntOptional(element, "successfulruns");

		rawIndustryJob.setActivityID(activityID);
		rawIndustryJob.setBlueprintID(blueprintID);
		rawIndustryJob.setBlueprintLocationID(blueprintLocationID);
		rawIndustryJob.setBlueprintTypeID(blueprintTypeID);
		rawIndustryJob.setCompletedCharacterID(completedCharacterID);
		rawIndustryJob.setCompletedDate(completedDate);
		rawIndustryJob.setCost(cost);
		rawIndustryJob.setDuration(duration);
		rawIndustryJob.setEndDate(endDate);
		rawIndustryJob.setFacilityID(facilityID);
		rawIndustryJob.setInstallerID(installerID);
		rawIndustryJob.setJobID(jobID);
		rawIndustryJob.setLicensedRuns(licensedRuns);
		rawIndustryJob.setOutputLocationID(outputLocationID);
		rawIndustryJob.setPauseDate(pauseDate);
		rawIndustryJob.setProbability(probability);
		rawIndustryJob.setProductTypeID(productTypeID);
		rawIndustryJob.setRuns(runs);
		rawIndustryJob.setStartDate(startDate);
		rawIndustryJob.setStationID(stationID);
		rawIndustryJob.setStatus(RawConverter.toIndustryJobStatus(status));
		rawIndustryJob.setSuccessfulRuns(successfulRuns);
		return rawIndustryJob;
	}

	private void parseAssets(final Node node, final OwnerType owner, final List<MyAsset> assets, final MyAsset parentAsset) throws XmlException {
		NodeList assetsNodes = node.getChildNodes();
		for (int i = 0; i < assetsNodes.getLength(); i++) {
			Node currentNode = assetsNodes.item(i);
			if (currentNode.getNodeName().equals("asset")) {
				RawAsset rawAsset = parseAsset(currentNode, parentAsset);
				List<MyAsset> parents = new ArrayList<MyAsset>();
				if (parentAsset != null) { //Child
					parents.addAll(parentAsset.getParents());
					parents.add(parentAsset);
				}
				MyAsset asset = DataConverter.toMyAsset(rawAsset, owner, parents);
				if (asset == null) {
					continue;
				}
				if (parentAsset == null) { //Root
					assets.add(asset);
				} else { //Child
					parentAsset.addAsset(asset);
				}
				parseAssets(currentNode, owner, assets, asset);
			}
		}
	}

	private RawAsset parseAsset(final Node node, final MyAsset parentAsset) throws XmlException {
		RawAsset rawAsset = RawAsset.create();
		Integer count = AttributeGetters.getInt(node, "count");

		long itemId = AttributeGetters.getLong(node, "id");
		int typeID = AttributeGetters.getInt(node, "typeid");
		long locationID = AttributeGetters.getLong(node, "locationid");
		if (locationID == 0 && parentAsset != null) {
			locationID = parentAsset.getLocationID();
		}
		boolean singleton = AttributeGetters.getBoolean(node, "singleton");
		Integer rawQuantity;
		if (AttributeGetters.haveAttribute(node, "rawquantity")) {
			rawQuantity = AttributeGetters.getInt(node, "rawquantity");
		} else {
			rawQuantity = null; //Legacy support
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
		rawAsset.setItemFlag(ApiIdConverter.getFlag(flagID));
		rawAsset.setItemID(itemId);
		rawAsset.setLocationID(locationID);
		rawAsset.setLocationType(RawConverter.toAssetLocationType(locationID));
		rawAsset.setQuantity(RawConverter.toAssetQuantity(count, rawQuantity));
		rawAsset.setSingleton(singleton);
		rawAsset.setTypeID(typeID);
		return rawAsset;
	}

	private void parseBlueprints(final Element element, final OwnerType owners) throws XmlException {
		Map<Long, RawBlueprint> blueprints = new HashMap<Long, RawBlueprint>();
		NodeList blueprintsNodes = element.getElementsByTagName("blueprints");
		for (int a = 0; a < blueprintsNodes.getLength(); a++) {
			Element currentBlueprintsNode = (Element) blueprintsNodes.item(a);
			NodeList blueprintNodes = currentBlueprintsNode.getElementsByTagName("blueprint");
			for (int b = 0; b < blueprintNodes.getLength(); b++) {
				Element currentNode = (Element) blueprintNodes.item(b);
				RawBlueprint blueprint = parseBlueprint(currentNode);
				blueprints.put(blueprint.getItemID(), blueprint);
			}
		}
		owners.setBlueprints(blueprints);
	}

	private RawBlueprint parseBlueprint(final Node node) throws XmlException {
		RawBlueprint blueprint = RawBlueprint.create();
		long itemID = AttributeGetters.getLong(node, "itemid");
		long locationID = AttributeGetters.getLong(node, "locationid");
		int typeID = AttributeGetters.getInt(node, "typeid");
		int flagID = AttributeGetters.getInt(node, "flagid");
		int quantity = AttributeGetters.getInt(node, "quantity");
		int timeEfficiency = AttributeGetters.getInt(node, "timeefficiency");
		int materialEfficiency = AttributeGetters.getInt(node, "materialefficiency");
		int runs = AttributeGetters.getInt(node, "runs");

		blueprint.setItemID(itemID);
		blueprint.setItemFlag(ApiIdConverter.getFlag(flagID));
		blueprint.setLocationID(locationID);
		blueprint.setMaterialEfficiency(materialEfficiency);
		blueprint.setQuantity(quantity);
		blueprint.setRuns(runs);
		blueprint.setTimeEfficiency(timeEfficiency);
		blueprint.setTypeID(typeID);
		return blueprint;
	}

	private void parseContainerLogs(final Element element, final OwnerType owners) throws XmlException {
		List<RawContainerLog> containerLogs = new ArrayList<RawContainerLog>();
		NodeList containerLogsNodes = element.getElementsByTagName("containerlogs");
		for (int a = 0; a < containerLogsNodes.getLength(); a++) {
			Element containerLogsNode = (Element) containerLogsNodes.item(a);
			NodeList containerLogNodes = containerLogsNode.getElementsByTagName("containerlog");
			for (int b = 0; b < containerLogNodes.getLength(); b++) {
				Element containerLogNode = (Element) containerLogNodes.item(a);
				ContainerAction action = ContainerAction.valueOf(AttributeGetters.getString(containerLogNode, "action"));
				Integer characterID = AttributeGetters.getInt(containerLogNode, "characterid");
				Long containerID = AttributeGetters.getLong(containerLogNode, "containerid");
				Integer containerTypeID = AttributeGetters.getInt(containerLogNode, "containertypeid");
				Integer flagID = AttributeGetters.getInt(containerLogNode, "flagid");
				Long locationID = AttributeGetters.getLong(containerLogNode, "locationid");
				Date loggedAt = AttributeGetters.getDateNotNull(containerLogNode, "loggedat");
				Integer newConfigBitmask = AttributeGetters.getIntOptional(containerLogNode, "newconfigbitmask");
				Integer oldConfigBitmask = AttributeGetters.getIntOptional(containerLogNode, "oldconfigbitmask");
				String passwordTypeValue = AttributeGetters.getStringOptional(containerLogNode, "passwordtype");
				ContainerPasswordType passwordType;
				if (passwordTypeValue == null) {
					passwordType = null;
				} else {
					passwordType = ContainerPasswordType.valueOf(passwordTypeValue);
				}
				Integer quantity = AttributeGetters.getIntOptional(containerLogNode, "quantity");
				Integer typeID = AttributeGetters.getIntOptional(containerLogNode, "typeid");

				RawContainerLog containerLog = RawContainerLog.create();
				containerLog.setAction(action);
				containerLog.setCharacterID(characterID);
				containerLog.setContainerID(containerID);
				containerLog.setContainerTypeID(containerTypeID);
				containerLog.setItemFlag(ApiIdConverter.getFlag(flagID));
				containerLog.setLocationID(locationID);
				containerLog.setLoggedAt(loggedAt);
				containerLog.setNewConfigBitmask(newConfigBitmask);
				containerLog.setOldConfigBitmask(oldConfigBitmask);
				containerLog.setPasswordType(passwordType);
				containerLog.setQuantity(quantity);
				containerLog.setTypeID(typeID);

				containerLogs.add(containerLog);
			}
		}
		owners.setContainerLogs(containerLogs);
	}
}
