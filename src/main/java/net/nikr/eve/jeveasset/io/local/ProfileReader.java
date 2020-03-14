/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount.KeyType;
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
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractAvailability;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractStatus;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractType;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.nikr.eve.jeveasset.data.profile.Profile;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class ProfileReader extends AbstractXmlReader<Boolean> {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileReader.class);

	private final ProfileManager profileManager;

	public static boolean load(ProfileManager profileManager, final String filename) {
		ProfileReader reader = new ProfileReader(profileManager);
		Boolean ok = reader.read(filename, filename, XmlType.DYNAMIC_BACKUP);
		if (!ok) {
			profileManager.clear();
		}
		return ok;
	}

	public ProfileReader(final ProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		profileManager.clear(); //Clear before load (may happen more than once)
		parseProfile(element, profileManager);
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

	private void parseProfile(final Element element, ProfileManager profileManager) throws XmlException {
		if (!element.getNodeName().equals("assets")) {
			throw new XmlException("Wrong root element name.");
		}
		//Stockpiles
		NodeList stockpilesNodes = element.getElementsByTagName("stockpiles");
		if (stockpilesNodes.getLength() == 1) {
			Element stockpilesElement = (Element) stockpilesNodes.item(0);
			parseStockpiles(stockpilesElement, profileManager.getActiveProfile());
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

	private void parseStockpiles(final Element element, final Profile profile) throws XmlException {
		NodeList stockpilesNodes = element.getElementsByTagName("stockpile");
		Set<Long> stockpileIDs = new HashSet<>();
		for (int i = 0; i < stockpilesNodes.getLength(); i++) {
			Element currentNode = (Element) stockpilesNodes.item(i);
			Long id = getLong(currentNode, "id");
			stockpileIDs.add(id);
		}
		profile.setStockpileIDs(stockpileIDs);
	}

	private void parseEsiOwners(final Element element, final List<EsiOwner> esiOwners) throws XmlException {
		NodeList ownerNodes = element.getElementsByTagName("esiowner");
		for (int i = 0; i < ownerNodes.getLength(); i++) {
			Element currentNode = (Element) ownerNodes.item(i);
			String accountName = getString(currentNode, "accountname");
			String refreshToken = getString(currentNode, "refreshtoken");
			String scopes = getString(currentNode, "scopes");
			String tokenType = getString(currentNode, "tokentype");
			String characterOwnerHash = getString(currentNode, "characterownerhash");
			String intellectualProperty = getString(currentNode, "intellectualproperty");
			Date structuresNextUpdate = getDate(currentNode, "structuresnextupdate");
			Date accountNextUpdate = getDate(currentNode, "accountnextupdate");
			EsiCallbackURL callbackURL;
			try {
				callbackURL = EsiCallbackURL.valueOf(getString(currentNode, "callbackurl"));
			} catch (IllegalArgumentException ex) {
				throw new XmlException(ex);
			}
			Set<RolesEnum> roles = EnumSet.noneOf(RolesEnum.class);
			if (haveAttribute(currentNode, "characterroles")) {
				for (String role : getString(currentNode, "characterroles").split(",")) {
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
			int accessKey = getInt(currentNode, "accesskey");
			String accessCred = getString(currentNode, "accesscred");
			Date expire = getDateOptional(currentNode, "expire");
			long accessmask = getLong(currentNode, "accessmask");
			boolean corporation = getBoolean(currentNode, "corporation");
			Date limit = getDateOptional(currentNode, "limit");
			String accountName = getString(currentNode, "accountname");
			//ContID
			Long journalCID = getLongOptional(currentNode, "journalcid");
			Long transactionsCID = getLongOptional(currentNode, "transactionscid");
			Long contractsCID = getLongOptional(currentNode, "contractscid");
			Long industryJobsCID = getLongOptional(currentNode, "industryjobscid");
			Long marketOrdersCID = getLongOptional(currentNode, "marketorderscid");
			Date accountNextUpdate = getDateOptional(currentNode, "accountnextupdate");
			boolean migrated = false;
			if (haveAttribute(currentNode, "migrated")) {
				migrated = getBoolean(currentNode, "migrated");
			}
			EveKitOwner owner = new EveKitOwner(accessKey, accessCred, expire, accessmask, corporation, limit, accountName, migrated);
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
		if (haveAttribute(node, "keyid")) {
			keyID = getInt(node, "keyid");
		} else {
			keyID = getInt(node, "userid");
		}
		String vCode;
		if (haveAttribute(node, "vcode")) {
			vCode = getString(node, "vcode");
		} else {
			vCode = getString(node, "apikey");
		}
		Date nextUpdate = getDate(node, "charactersnextupdate");
		String name = Integer.toString(keyID);
		if (haveAttribute(node, "name")) {
			name = getString(node, "name");
		}
		long accessMask = 0;
		if (haveAttribute(node, "accessmask")) {
			accessMask = getLong(node, "accessmask");
		}
		KeyType type = null;
		if (haveAttribute(node, "type")) {
			type = KeyType.valueOf(getString(node, "type").toUpperCase());
		}
		Date expires = null;
		if (haveAttribute(node, "expires")) {
			long i = getLong(node, "expires");
			if (i != 0) {
				expires = new Date(i);
			}
		}
		boolean invalid = false;
		if (haveAttribute(node, "invalid")) {
			invalid = getBoolean(node, "invalid");
		}
		return new EveApiAccount(keyID, vCode, name, nextUpdate, accessMask, type, expires, invalid);
	}

	private void parseOwners(final Element element, final EveApiAccount account) throws XmlException {
		NodeList ownerNodes = element.getElementsByTagName("human");
		for (int i = 0; i < ownerNodes.getLength(); i++) {
			Element currentNode = (Element) ownerNodes.item(i);
			boolean migrated = false;
			if (haveAttribute(currentNode, "migrated")) {
				migrated = getBoolean(currentNode, "migrated");
			}
			EveApiOwner owner = new EveApiOwner(account, migrated);
			parseOwnerType(currentNode, owner);
			account.getOwners().add(owner);
		}
	}

	private void parseOwnerType(final Element node, OwnerType owner) throws XmlException {
		String ownerName = getString(node, "name");
		String corporationName = getStringOptional(node, "corp");
		long ownerID = getLong(node, "id");
		Date assetsNextUpdate = getDateNotNull(node, "assetsnextupdate");
		Date assetsLastUpdate = getDateOptional(node, "assetslastupdate");
		Date balanceNextUpdate = getDateNotNull(node, "balancenextupdate");
		Date balanceLastUpdate = getDateOptional(node, "balancelastupdate");
		boolean showOwner = true;
		if (haveAttribute(node, "show")) {
			showOwner = getBoolean(node, "show");
		}
		Date marketOrdersNextUpdate = getDateNotNull(node, "marketordersnextupdate");
		Date journalNextUpdate = getDateNotNull(node, "journalnextupdate");
		Date transactionsNextUpdate = getDateNotNull(node, "wallettransactionsnextupdate");
		Date industryJobsNextUpdate = getDateNotNull(node, "industryjobsnextupdate");
		Date contractsNextUpdate = getDateNotNull(node, "contractsnextupdate");
		Date locationsNextUpdate = getDateNotNull(node, "locationsnextupdate");
		Date blueprintsNextUpdate = getDateNotNull(node, "blueprintsnextupdate");
		Date bookmarksNextUpdate = getDateNotNull(node, "bookmarksnextupdate");
		owner.setOwnerName(ownerName);
		owner.setCorporationName(corporationName);
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
		owner.setBookmarksNextUpdate(bookmarksNextUpdate);

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
		parseAssetDivisions(node, owner);
		parseWalletDivisions(node, owner);
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
		Integer acceptorID = getInt(element, "acceptorid");
		Integer assigneeID = getInt(element, "assigneeid");
		ContractAvailability availability = RawConverter.toContractAvailability(getString(element, "availability"));
		Double buyout = getDoubleOptional(element, "buyout");
		Double collateral = getDoubleOptional(element, "collateral");
		Integer contractID = getInt(element, "contractid");
		Date dateAccepted = getDateOptional(element, "dateaccepted");
		Date dateCompleted = getDateOptional(element, "datecompleted");
		Date dateExpired = getDate(element, "dateexpired");
		Date dateIssued = getDate(element, "dateissued");
		Long endLocationID = getLongOptional(element, "endstationid");
		Integer issuerCorporationID = getInt(element, "issuercorpid");
		Integer issuerID = getInt(element, "issuerid");
		Integer daysToComplete = getIntOptional(element, "numdays");
		Double price = getDoubleOptional(element, "price");
		Double reward = getDoubleOptional(element, "reward");
		Long startLocationID = getLongOptional(element, "startstationid");
		ContractStatus status = RawConverter.toContractStatus(getString(element, "status"));
		String title = getStringOptional(element, "title");
		ContractType type = RawConverter.toContractType(getString(element, "type"));
		Double volume = getDoubleOptional(element, "volume");
		boolean forCorporation = getBoolean(element, "forcorp");

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
		boolean included = getBoolean(element, "included");
		Integer quantity = getInt(element, "quantity");
		long recordID = getLong(element, "recordid");
		boolean singleton = getBoolean(element, "singleton");
		int typeID = getInt(element, "typeid");
		Integer rawQuantity = getIntOptional(element, "rawquantity");
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
		int accountKey = getInt(element, "accountkey");
		Double balance = getDouble(element, "balance");
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
		long orderID = getLong(element, "orderid");
		long locationID = getLong(element, "stationid");
		int volEntered = getInt(element, "volentered");
		int volRemaining = getInt(element, "volremaining");
		int minVolume = getInt(element, "minvolume");
		int state = getInt(element, "orderstate");
		int typeID = getInt(element, "typeid");
		int range = getInt(element, "range");
		int accountID = getInt(element, "accountkey");
		int duration = getInt(element, "duration");
		Double escrow = getDouble(element, "escrow");
		Double price = getDouble(element, "price");
		int bid = getInt(element, "bid");
		Date issued = getDate(element, "issued");
		Date created = getDateOptional(element, "created");
		String changed = getStringOptional(element, "changed");
		Integer issuedBy = getIntOptional(element, "issuedby");
		boolean corp = owner.isCorporation();
		if (haveAttribute(element, "corp")) {
			corp = getBoolean(element, "corp");
		}
		apiMarketOrder.setWalletDivision(accountID);
		apiMarketOrder.setDuration(duration);
		apiMarketOrder.setEscrow(escrow);
		apiMarketOrder.setBuyOrder(bid > 0);
		apiMarketOrder.setCorp(corp);
		apiMarketOrder.setIssued(issued);
		apiMarketOrder.addChanged(created);
		if (changed != null) {
			String[] array = changed.split(",");
			for (String s : array) {
				try {
					Date date = new Date(Long.valueOf(s));
					apiMarketOrder.addChanged(date);
				} catch (NumberFormatException ex) {
					//No problem....
				}
			}
		}
		apiMarketOrder.setIssuedBy(issuedBy);
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
		Double amount = getDoubleOptional(element, "amount");
		Long argID = getLongOptional(element, "argid1");
		String argName = getStringOptional(element, "argname1");
		Double balance = getDoubleOptional(element, "balance");
		Long contextID = getLongOptional(element, "contextid");
		String contextType = getStringOptional(element, "contexttype");
		Date date = getDate(element, "date");
		String description;
		if (haveAttribute(element, "description")) {
			description = getString(element, "description");
		} else {
			description = argName;
		}
		Integer firstPartyID = getIntOptional(element, "ownerid1");
		Integer secondPartyID = getIntOptional(element, "ownerid2");
		String reason = getStringOptional(element, "reason");
		long refID = getLong(element, "refid");
		int refTypeID = getInt(element, "reftypeid");
		Double taxAmount = getDoubleOptional(element, "taxamount");
		Integer taxReceiverID = getIntOptional(element, "taxreceiverid");
		//Extra
		int accountKey = getInt(element, "accountkey");

		rawJournal.setAmount(amount);
		rawJournal.setBalance(balance);
		rawJournal.setDate(date);
		rawJournal.setDescription(description);
		rawJournal.setFirstPartyID(firstPartyID);
		rawJournal.setReason(reason);
		rawJournal.setRefID(refID);
		RawJournalRefType refType = RawConverter.toJournalRefType(refTypeID);
		rawJournal.setRefType(refType);
		rawJournal.setSecondPartyID(secondPartyID);
		rawJournal.setTax(taxAmount);
		rawJournal.setTaxReceiverId(taxReceiverID);
		if (argID != null || argName != null) {
			rawJournal.setContextId(RawConverter.toJournalContextID(argID, argName, refType));
			rawJournal.setContextType(RawConverter.toJournalContextType(refType));
		} else {
			rawJournal.setContextId(contextID);
			rawJournal.setContextType(RawConverter.toJournalContextType(contextType));
		}
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
		Date date = getDate(element, "transactiondatetime");
		Long transactionID = getLong(element, "transactionid");
		int quantity = getInt(element, "quantity");
		int typeID = getInt(element, "typeid");
		Double price = getDouble(element, "price");
		Integer clientID = getInt(element, "clientid");
		long locationID = getLong(element, "stationid");
		String transactionType = getString(element, "transactiontype");
		String transactionFor = getString(element, "transactionfor");

		//New
		Long journalRefID;
		if (haveAttribute(element, "journaltransactionid")) {
			journalRefID = getLong(element, "journaltransactionid");
		} else {
			journalRefID = 0L; //Legacy support
		}

		//Extra
		int accountKey = 1000;
		if (haveAttribute(element, "accountkey")) {
			accountKey = getInt(element, "accountkey");
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
				if (haveAttribute(currentNode, "blueprintid")) {
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
		Integer jobID = getInt(element, "jobid");
		Integer installerID = getInt(element, "installerid");
		long facilityID = getLong(element, "facilityid");
		long stationID = getLong(element, "stationid");
		int activityID = getInt(element, "activityid");
		long blueprintID = getLong(element, "blueprintid");
		int blueprintTypeID = getInt(element, "blueprinttypeid");
		long blueprintLocationID = getLong(element, "blueprintlocationid");
		long outputLocationID = getLong(element, "outputlocationid");
		int runs = getInt(element, "runs");
		Double cost = getDoubleOptional(element, "cost");
		Integer licensedRuns = getIntOptional(element, "licensedruns");
		Float probability = getFloatOptional(element, "probability");
		Integer productTypeID = getIntOptional(element, "producttypeid");
		int status = getInt(element, "status");
		int duration = getInt(element, "timeinseconds");
		Date startDate = getDate(element, "startdate");
		Date endDate = getDate(element, "enddate");
		Date pauseDate = getDateOptional(element, "pausedate");
		Date completedDate = getDateOptional(element, "completeddate");
		Integer completedCharacterID = getIntOptional(element, "completedcharacterid");
		Integer successfulRuns = getIntOptional(element, "successfulruns");

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
		int count = getInt(node, "count");

		long itemId = getLong(node, "id");
		int typeID = getInt(node, "typeid");
		long locationID = getLong(node, "locationid");
		if (locationID == 0 && parentAsset != null) {
			locationID = parentAsset.getLocationID();
		}
		boolean singleton = getBoolean(node, "singleton");
		Integer rawQuantity;
		if (haveAttribute(node, "rawquantity")) {
			rawQuantity = getInt(node, "rawquantity");
		} else {
			rawQuantity = null; //Legacy support
		}
		int flagID = 0;
		if (haveAttribute(node, "flagid")) {
			flagID = getInt(node, "flagid");
		} else { //Workaround for the old system
			String flag = getString(node, "flag");
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
		long itemID = getLong(node, "itemid");
		long locationID = getLong(node, "locationid");
		int typeID = getInt(node, "typeid");
		int flagID = getInt(node, "flagid");
		int quantity = getInt(node, "quantity");
		int timeEfficiency = getInt(node, "timeefficiency");
		int materialEfficiency = getInt(node, "materialefficiency");
		int runs = getInt(node, "runs");

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

	private void parseAssetDivisions(final Element element, final OwnerType owners) throws XmlException {
		Map<Integer, String> divisions = new HashMap<>();
		NodeList divisionsNodes = element.getElementsByTagName("assetdivisions");
		for (int a = 0; a < divisionsNodes.getLength(); a++) {
			Element currentDivisionsNode = (Element) divisionsNodes.item(a);
			NodeList divisionNodes = currentDivisionsNode.getElementsByTagName("assetdivision");
			for (int b = 0; b < divisionNodes.getLength(); b++) {
				Element currentNode = (Element) divisionNodes.item(b);
				int id = getInt(currentNode, "id");
				String name = getStringOptional(currentNode, "name");
				divisions.put(id, name);
			}
		}
		owners.setAssetDivisions(divisions);
	}

	private void parseWalletDivisions(final Element element, final OwnerType owners) throws XmlException {
		Map<Integer, String> divisions = new HashMap<>();
		NodeList divisionsNodes = element.getElementsByTagName("walletdivisions");
		for (int a = 0; a < divisionsNodes.getLength(); a++) {
			Element currentDivisionsNode = (Element) divisionsNodes.item(a);
			NodeList divisionNodes = currentDivisionsNode.getElementsByTagName("walletdivision");
			for (int b = 0; b < divisionNodes.getLength(); b++) {
				Element currentNode = (Element) divisionNodes.item(b);
				int id = getInt(currentNode, "id");
				String name = getStringOptional(currentNode, "name");
				divisions.put(id, name);
			}
		}
		owners.setWalletDivisions(divisions);
	}
}
