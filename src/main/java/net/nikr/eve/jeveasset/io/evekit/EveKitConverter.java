/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.evekit;



import com.beimin.eveapi.model.shared.ContractAvailability;
import com.beimin.eveapi.model.shared.ContractStatus;
import com.beimin.eveapi.model.shared.ContractType;
import com.beimin.eveapi.model.shared.JournalEntry;
import enterprises.orbital.evekit.client.model.AccountBalance;
import enterprises.orbital.evekit.client.model.Asset;
import enterprises.orbital.evekit.client.model.Blueprint;
import enterprises.orbital.evekit.client.model.Contract;
import enterprises.orbital.evekit.client.model.ContractItem;
import enterprises.orbital.evekit.client.model.IndustryJob;
import enterprises.orbital.evekit.client.model.Location;
import enterprises.orbital.evekit.client.model.MarketOrder;
import enterprises.orbital.evekit.client.model.WalletJournal;
import enterprises.orbital.evekit.client.model.WalletTransaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.MyAccountBalance;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.journal.MyJournal;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.transaction.MyTransaction;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EveKitConverter {

	private static final Logger LOG = LoggerFactory.getLogger(EveKitConverter.class);

	private EveKitConverter() { }

	static Map<Long, String> convertLocations(List<Location> locations) {
		Map<Long, String> names = new HashMap<Long, String>();
		for (Location location : locations) {
			names.put(location.getItemID(), location.getItemName());
		}
		return names;
	}

	static Set<MyTransaction> convertTransactions(List<WalletTransaction> eveKitTransactions, EveKitOwner owner) {
		Map<Integer, List<com.beimin.eveapi.model.shared.WalletTransaction>> journals = new HashMap<Integer, List<com.beimin.eveapi.model.shared.WalletTransaction>>();
		for (WalletTransaction transaction : eveKitTransactions) {
			List<com.beimin.eveapi.model.shared.WalletTransaction> list = journals.get(transaction.getAccountKey());
			if (list == null) {
				list = new ArrayList<com.beimin.eveapi.model.shared.WalletTransaction>();
				journals.put(transaction.getAccountKey(), list);
			}
			list.add(toTransaction(transaction, owner));
		}
		Set<MyTransaction> myTransactions = new HashSet<MyTransaction>();
		for (Map.Entry<Integer, List<com.beimin.eveapi.model.shared.WalletTransaction>> entry : journals.entrySet()) {
			myTransactions.addAll(ApiConverter.convertTransactions(entry.getValue(), owner, entry.getKey()));
		}
		return myTransactions;
	}

	private static com.beimin.eveapi.model.shared.WalletTransaction toTransaction(WalletTransaction eveKitTransaction, EveKitOwner owner) {
		com.beimin.eveapi.model.shared.WalletTransaction transaction = new com.beimin.eveapi.model.shared.WalletTransaction();
		transaction.setTransactionDateTime(new Date(eveKitTransaction.getDate()));
		transaction.setTransactionID(eveKitTransaction.getTransactionID());
		transaction.setQuantity(eveKitTransaction.getQuantity());
		transaction.setTypeName(eveKitTransaction.getTypeName());
		transaction.setTypeID(eveKitTransaction.getTypeID());
		transaction.setPrice(eveKitTransaction.getPrice().doubleValue()); //BigDecimal to double
		transaction.setClientID(eveKitTransaction.getClientID());
		transaction.setClientName(eveKitTransaction.getClientName());
		transaction.setCharacterID(owner.getOwnerID()); //Note: this attribute is no longer in the API
		transaction.setCharacterName(owner.getOwnerName()); //Note: this attribute is no longer in the API
		transaction.setStationID(eveKitTransaction.getStationID());
		transaction.setStationName(eveKitTransaction.getStationName());
		transaction.setTransactionType(eveKitTransaction.getTransactionType());
		transaction.setTransactionFor(eveKitTransaction.getTransactionFor());
		transaction.setJournalTransactionID(eveKitTransaction.getJournalTransactionID());
		transaction.setClientTypeID(eveKitTransaction.getClientTypeID());
		return transaction;
	}

	static List<MyMarketOrder> convertMarketOrders(List<MarketOrder> eveKitMarketOrders, EveKitOwner owner) {
		List<com.beimin.eveapi.model.shared.MarketOrder> marketOrders = new ArrayList<com.beimin.eveapi.model.shared.MarketOrder>();
		for (MarketOrder marketOrder : eveKitMarketOrders) {
			marketOrders.add(toMarketOrder(marketOrder));
		}
		return ApiConverter.convertMarketOrders(marketOrders, owner);
	}

	private static com.beimin.eveapi.model.shared.MarketOrder toMarketOrder(MarketOrder eveKitMarketOrder) {
		com.beimin.eveapi.model.shared.MarketOrder marketOrder = new com.beimin.eveapi.model.shared.MarketOrder();
		marketOrder.setOrderID(eveKitMarketOrder.getOrderID());
		marketOrder.setCharID(eveKitMarketOrder.getCharID());
		marketOrder.setStationID(eveKitMarketOrder.getStationID());
		marketOrder.setVolEntered(eveKitMarketOrder.getVolEntered());
		marketOrder.setVolRemaining(eveKitMarketOrder.getVolRemaining());
		marketOrder.setMinVolume(eveKitMarketOrder.getMinVolume());
		marketOrder.setOrderState(eveKitMarketOrder.getOrderState());
		marketOrder.setTypeID(eveKitMarketOrder.getTypeID());
		marketOrder.setRange(eveKitMarketOrder.getOrderRange());
		marketOrder.setAccountKey(eveKitMarketOrder.getAccountKey());
		marketOrder.setDuration(eveKitMarketOrder.getDuration());
		marketOrder.setEscrow(eveKitMarketOrder.getEscrow().doubleValue()); //BigDecimal to double
		marketOrder.setPrice(eveKitMarketOrder.getPrice().doubleValue()); //BigDecimal to double
		marketOrder.setBid(eveKitMarketOrder.getBid() ? 1 : 0);
		marketOrder.setIssued(new Date(eveKitMarketOrder.getIssued()));
		return marketOrder;
	}

	static Set<MyJournal> convertJournals(List<WalletJournal> eveKitJournals, EveKitOwner owner) {
		Map<Integer, List<com.beimin.eveapi.model.shared.JournalEntry>> journals = new HashMap<Integer, List<com.beimin.eveapi.model.shared.JournalEntry>>();
		for (WalletJournal journal : eveKitJournals) {
			List<JournalEntry> list = journals.get(journal.getAccountKey());
			if (list == null) {
				list = new ArrayList<JournalEntry>();
				journals.put(journal.getAccountKey(), list);
			}
			list.add(toJournal(journal));
		}
		Set<MyJournal> myJournals = new HashSet<MyJournal>();
		for (Map.Entry<Integer, List<com.beimin.eveapi.model.shared.JournalEntry>> entry : journals.entrySet()) {
			myJournals.addAll(ApiConverter.convertJournals(entry.getValue(), owner, entry.getKey()));
		}
		return myJournals;
	}

	private static com.beimin.eveapi.model.shared.JournalEntry toJournal(WalletJournal eveKitJournal) {
		com.beimin.eveapi.model.shared.JournalEntry journal = new com.beimin.eveapi.model.shared.JournalEntry();
		journal.setDate(new Date(eveKitJournal.getDate()));
		journal.setRefID(eveKitJournal.getRefID());
		journal.setRefTypeID(eveKitJournal.getRefTypeID());
		journal.setOwnerName1(eveKitJournal.getOwnerName1());
		journal.setOwnerID1(eveKitJournal.getOwnerID1());
		journal.setOwnerName2(eveKitJournal.getOwnerName2());
		journal.setOwnerID2(eveKitJournal.getOwnerID2());
		journal.setArgName1(eveKitJournal.getArgName1());
		journal.setArgID1(eveKitJournal.getArgID1());
		journal.setAmount(journal.getAmount());
		journal.setBalance(journal.getBalance());
		journal.setReason(journal.getReason());
		journal.setTaxReceiverID(journal.getTaxReceiverID());
		journal.setTaxAmount(journal.getTaxAmount());
		journal.setOwner1TypeID(journal.getOwner1TypeID());
		journal.setOwner2TypeID(journal.getOwner2TypeID());
		return journal;
	}

	static List<MyIndustryJob> convertIndustryJobs(List<IndustryJob> eveKitindustryJobs, EveKitOwner owner) {
		List<com.beimin.eveapi.model.shared.IndustryJob> industryJobs = new ArrayList<com.beimin.eveapi.model.shared.IndustryJob>();
		for (IndustryJob industryJob : eveKitindustryJobs) {
			industryJobs.add(toIndustryJob(industryJob));
		}
		return ApiConverter.convertIndustryJobs(industryJobs, owner);
	}

	private static com.beimin.eveapi.model.shared.IndustryJob toIndustryJob(IndustryJob eveKitIndustryJob) {
		com.beimin.eveapi.model.shared.IndustryJob industryJob = new com.beimin.eveapi.model.shared.IndustryJob();
		industryJob.setJobID(eveKitIndustryJob.getJobID());
		industryJob.setInstallerID(eveKitIndustryJob.getInstallerID());
		industryJob.setInstallerName(eveKitIndustryJob.getInstallerName());
		industryJob.setFacilityID(eveKitIndustryJob.getFacilityID());
		industryJob.setSolarSystemID(eveKitIndustryJob.getSolarSystemID());
		industryJob.setStationID(eveKitIndustryJob.getStationID());
		industryJob.setActivityID(eveKitIndustryJob.getActivityID());
		industryJob.setBlueprintID(eveKitIndustryJob.getBlueprintID());
		industryJob.setBlueprintTypeID(eveKitIndustryJob.getBlueprintTypeID());
		industryJob.setBlueprintTypeName(eveKitIndustryJob.getBlueprintTypeName());
		industryJob.setBlueprintLocationID(eveKitIndustryJob.getBlueprintLocationID());
		industryJob.setOutputLocationID(eveKitIndustryJob.getOutputLocationID());
		industryJob.setRuns(eveKitIndustryJob.getRuns());
		industryJob.setCost(eveKitIndustryJob.getCost().doubleValue()); //BigDecimal to double
		industryJob.setTeamID(eveKitIndustryJob.getTeamID());
		industryJob.setLicensedRuns(eveKitIndustryJob.getLicensedRuns());
		industryJob.setProbability(eveKitIndustryJob.getProbability());
		industryJob.setProductTypeID(eveKitIndustryJob.getProductTypeID());
		industryJob.setProductTypeName(eveKitIndustryJob.getProductTypeName());
		industryJob.setStatus(eveKitIndustryJob.getStatus());
		industryJob.setTimeInSeconds(eveKitIndustryJob.getTimeInSeconds());
		industryJob.setCompletedDate(new Date(eveKitIndustryJob.getCompletedDate()));
		industryJob.setStartDate(new Date(eveKitIndustryJob.getStartDate()));
		industryJob.setEndDate(new Date(eveKitIndustryJob.getEndDate()));
		industryJob.setPauseDate(new Date(eveKitIndustryJob.getPauseDate()));
		industryJob.setCompletedCharacterID(eveKitIndustryJob.getCompletedCharacterID());
		return industryJob;
	}

	static Map<Long, com.beimin.eveapi.model.shared.Blueprint> convertBlueprints(List<Blueprint> eveKitBlueprints) {
		Map<Long, com.beimin.eveapi.model.shared.Blueprint> blueprints = new HashMap<Long, com.beimin.eveapi.model.shared.Blueprint>();
		for (Blueprint blueprint : eveKitBlueprints) {
			blueprints.put(blueprint.getItemID(), toBlueprint(blueprint));
		}
		return blueprints;
	}

	private static com.beimin.eveapi.model.shared.Blueprint toBlueprint(Blueprint eveKitBlueprint) {
		com.beimin.eveapi.model.shared.Blueprint blueprint = new com.beimin.eveapi.model.shared.Blueprint();
		blueprint.setItemID(eveKitBlueprint.getItemID());
		blueprint.setLocationID(eveKitBlueprint.getLocationID());
		blueprint.setTypeID(eveKitBlueprint.getTypeID());
		blueprint.setTypeName(eveKitBlueprint.getTypeName());
		blueprint.setFlagID(eveKitBlueprint.getFlagID());
		blueprint.setQuantity(eveKitBlueprint.getQuantity());
		blueprint.setTimeEfficiency(eveKitBlueprint.getTimeEfficiency());
		blueprint.setMaterialEfficiency(eveKitBlueprint.getMaterialEfficiency());
		blueprint.setRuns(eveKitBlueprint.getRuns());
		return blueprint;
	}

	static Map<MyContract, List<MyContractItem>> convertContracts(List<Contract> eveKitContracts) {
		Map<com.beimin.eveapi.model.shared.Contract, List<com.beimin.eveapi.model.shared.ContractItem>> contracts = new HashMap<com.beimin.eveapi.model.shared.Contract, List<com.beimin.eveapi.model.shared.ContractItem>>();
		for (Contract contract : eveKitContracts) {
			contracts.put(toContract(contract), new ArrayList<com.beimin.eveapi.model.shared.ContractItem>());
		}
		return ApiConverter.convertContracts(contracts);
	}

	private static com.beimin.eveapi.model.shared.Contract toContract(Contract eveKitContract) {
		com.beimin.eveapi.model.shared.Contract contract = new com.beimin.eveapi.model.shared.Contract();
		contract.setContractID(eveKitContract.getContractID());
		contract.setIssuerID(eveKitContract.getIssuerID());
		contract.setIssuerCorpID(eveKitContract.getIssuerCorpID());
		contract.setAssigneeID(eveKitContract.getAssigneeID());
		contract.setAcceptorID(eveKitContract.getAcceptorID());
		contract.setStartStationID(eveKitContract.getStartStationID());
		contract.setEndStationID(eveKitContract.getEndStationID());
		contract.setType(ContractType.valueOf(eveKitContract.getType().toUpperCase()));
		contract.setStatus(ContractStatus.valueOf(eveKitContract.getStatus().toUpperCase()));
		contract.setTitle(eveKitContract.getTitle());
		contract.setForCorp(eveKitContract.getForCorp());
		contract.setAvailability(ContractAvailability.valueOf(eveKitContract.getAvailability().toUpperCase()));
		contract.setDateIssued(getDate(eveKitContract.getDateIssued()));
		contract.setDateExpired(getDate(eveKitContract.getDateExpired()));
		contract.setDateAccepted(getDate(eveKitContract.getDateAccepted(), true));
		contract.setDateCompleted(getDate(eveKitContract.getDateCompleted(), true));
		contract.setNumDays(eveKitContract.getNumDays());
		contract.setPrice(eveKitContract.getPrice().doubleValue()); //BigDecimal to double
		contract.setReward(eveKitContract.getReward().doubleValue()); //BigDecimal to double
		contract.setCollateral(eveKitContract.getCollateral().doubleValue()); //BigDecimal to double
		contract.setBuyout(eveKitContract.getBuyout().doubleValue()); //BigDecimal to double
		contract.setVolume(eveKitContract.getVolume());
		return contract;
	}

	static void convertContractItems(Map<MyContract, List<MyContractItem>> contracts, List<ContractItem> eveKitContractItems) {
		//Create MyContract lookup table
		Map<Long, MyContract> contractsLookup = new HashMap<Long, MyContract>();
		for (MyContract contract : contracts.keySet()) {
			contractsLookup.put(contract.getContractID(), contract);
		}
		//Convert EveKit>ContractItem to EveApi>ContractItem
		Map<Long, List<com.beimin.eveapi.model.shared.ContractItem>> contractItems = new HashMap<Long, List<com.beimin.eveapi.model.shared.ContractItem>>();
		for (ContractItem contractItem : eveKitContractItems) {
			List<com.beimin.eveapi.model.shared.ContractItem> list = contractItems.get(contractItem.getContractID());
			if (list == null) {
				list = new ArrayList<com.beimin.eveapi.model.shared.ContractItem>();
				contractItems.put(contractItem.getContractID(), list);
			}
			list.add(toContractItem(contractItem));
		}
		//Convert EveApi>ContractItem to MyContractItem
		for (Map.Entry<Long, List<com.beimin.eveapi.model.shared.ContractItem>> entry : contractItems.entrySet()) {
			MyContract myContract = contractsLookup.get(entry.getKey());
			List<MyContractItem> myContractItems = ApiConverter.convertContractItems(entry.getValue(), myContract);
			contracts.put(myContract, myContractItems);
		}
	}

	private static com.beimin.eveapi.model.shared.ContractItem toContractItem(ContractItem eveKitContractItem) {
		com.beimin.eveapi.model.shared.ContractItem contractItem = new com.beimin.eveapi.model.shared.ContractItem();
		contractItem.setRecordID(eveKitContractItem.getRecordID());
		contractItem.setTypeID(eveKitContractItem.getTypeID());
		contractItem.setQuantity(eveKitContractItem.getQuantity());
		contractItem.setSingleton(eveKitContractItem.getSingleton());
		contractItem.setIncluded(eveKitContractItem.getIncluded());
		contractItem.setRawQuantity(eveKitContractItem.getRawQuantity());
		return contractItem;
	}

	static List<MyAsset> convertAssets(final List<Asset> eveKitAssets, final EveKitOwner owner) {
		//List of top level assets
		List<com.beimin.eveapi.model.shared.Asset> eveApiAssets = new ArrayList<com.beimin.eveapi.model.shared.Asset>();
		//Lookup table for EveApi>Asset
		Map<Long, com.beimin.eveapi.model.shared.Asset> eveApiAssetsLoopup = new HashMap<Long, com.beimin.eveapi.model.shared.Asset>();
		//Lookup table for EveKit>Asset
		Map<Long, Asset> eveKitAssetsLoopup = new HashMap<Long, Asset>();
		for (Asset asset : eveKitAssets) { //Create EveKit>Asset loopup table
			eveKitAssetsLoopup.put(asset.getItemID(), asset);
		}
		for (Asset asset : eveKitAssets) { //Convert EveKit>Asset to  EveApi>Asset
			deepAsset(eveApiAssets, eveApiAssetsLoopup, eveKitAssetsLoopup, asset);
		}
		return ApiConverter.convertAsset(eveApiAssets, owner);
	}

	private static com.beimin.eveapi.model.shared.Asset deepAsset(List<com.beimin.eveapi.model.shared.Asset> eveApiAssets, Map<Long, com.beimin.eveapi.model.shared.Asset> assetsLoopup, Map<Long, Asset> eveKitAssetsLoopup, Asset eveKitAsset) {
		com.beimin.eveapi.model.shared.Asset asset = assetsLoopup.get(eveKitAsset.getItemID()); //EveApi>Asset exist?
		if (asset == null) { //EveApi>Asset not converted
			com.beimin.eveapi.model.shared.Asset parentAsset = null;
			if (eveKitAsset.getContainer() > 0) { //Have Parent Asset
				parentAsset = assetsLoopup.get(eveKitAsset.getContainer()); //Parent EveApi>Asset exist?
				if (parentAsset == null) { //Parent EveApi>Asset not converted -> Go deeper -> Convert Parent(s) to EveApi>Asset
					parentAsset = deepAsset(eveApiAssets, assetsLoopup, eveKitAssetsLoopup, eveKitAssetsLoopup.get(eveKitAsset.getContainer()));
				}
			}
			//Convert EveKit>Asset to EveApi>Asset
			asset = toAsset(eveKitAsset, parentAsset);
			//Add  EveApi>Asset to loopup table
			assetsLoopup.put(asset.getItemID(), asset);
			if (parentAsset == null) { //If top level EveApi>Asset add it to the list
				eveApiAssets.add(asset);
			}
		}
		return asset;
	}

	private static com.beimin.eveapi.model.shared.Asset toAsset(final Asset asset, final com.beimin.eveapi.model.shared.Asset parentAsset) {
		com.beimin.eveapi.model.shared.Asset eveApiAsset = new com.beimin.eveapi.model.shared.Asset();
		eveApiAsset.setItemID(asset.getItemID());
		eveApiAsset.setLocationID(asset.getLocationID());
		eveApiAsset.setTypeID(asset.getTypeID());
		eveApiAsset.setQuantity(asset.getQuantity().intValue()); //Long to Int
		eveApiAsset.setRawQuantity(asset.getRawQuantity().intValue()); //Long to Int
		eveApiAsset.setFlag(asset.getFlag());
		eveApiAsset.setSingleton(asset.getSingleton());
		if (parentAsset != null) {
			parentAsset.getAssets().add(eveApiAsset);
		}
		return eveApiAsset;
	}

	static List<MyAccountBalance> convertAccountBalance(final List<AccountBalance> eveKitAccountBalances, final EveKitOwner owner) {
		List<com.beimin.eveapi.model.shared.AccountBalance> accountBalances = new ArrayList<com.beimin.eveapi.model.shared.AccountBalance>();
		for (AccountBalance accountBalance : eveKitAccountBalances) {
			accountBalances.add(toAccountBalance(accountBalance));
		}
		return ApiConverter.convertAccountBalance(accountBalances, owner);
	}

	private static com.beimin.eveapi.model.shared.AccountBalance toAccountBalance(final AccountBalance accountBalance) {
		com.beimin.eveapi.model.shared.AccountBalance eveApiAccountBalance = new com.beimin.eveapi.model.shared.AccountBalance();
		eveApiAccountBalance.setAccountID(accountBalance.getAccountID());
		eveApiAccountBalance.setAccountKey(accountBalance.getAccountKey());
		eveApiAccountBalance.setBalance(accountBalance.getBalance().doubleValue()); //BigDecimal to double
		return eveApiAccountBalance;
	}

	private static Date getDate(Long date) {
		return getDate(date, false);
	}

	private static Date getDate(Long date, boolean useNull) {
		if (date == null || date == -1) {
			if (useNull) {
				return null;
			} else {
				return new Date(-62135769600000L);
			}
		} else {
			return new Date(date);
		}
	}
}
