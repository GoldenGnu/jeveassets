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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.shared.accountbalance.EveAccountBalance;
import com.beimin.eveapi.shared.assetlist.EveAsset;
import com.beimin.eveapi.shared.contract.ContractStatus;
import com.beimin.eveapi.shared.contract.EveContract;
import com.beimin.eveapi.shared.contract.items.EveContractItem;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import com.beimin.eveapi.shared.wallet.journal.ApiJournalEntry;
import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.Contract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.journal.Journal;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.transaction.Transaction;
import net.nikr.eve.jeveasset.i18n.General;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApiConverter {

	private static final Logger LOG = LoggerFactory.getLogger(ApiConverter.class);;

	private ApiConverter() { }

	public static List<AccountBalance> convertAccountBalance(final List<EveAccountBalance> eveAccountBalances, final Owner owner) {
		List<AccountBalance> accountBalances = new ArrayList<AccountBalance>();
		for (EveAccountBalance eveAccountBalance : eveAccountBalances) {
			accountBalances.add( new AccountBalance(eveAccountBalance, owner));
		}
		return accountBalances;
	}

	public static List<Asset> assetIndustryJob(final List<IndustryJob> industryJobs, final Owner owner) {
		List<Asset> assets = new ArrayList<Asset>();
		for (IndustryJob industryJob : industryJobs) {
			if (!industryJob.isCompleted()) {
				Asset asset = toAssetIndustryJob(industryJob, owner);
				assets.add(asset);
			}
		}
		return assets;
	}

	private static Asset toAssetIndustryJob(final IndustryJob industryJob, final Owner owner) {
		int typeID = industryJob.getInstalledItemTypeID();
		long locationID = toLocationID(industryJob);
		long count = industryJob.getInstalledItemQuantity();
		long itemID = industryJob.getInstalledItemID();
		int flagID = industryJob.getInstalledItemFlag();
		boolean singleton  = false;
		int rawQuantity;
		if (industryJob.getInstalledItemCopy() == 0) { //0 = BPO | 1 = PBC
			rawQuantity = -1; //-1 = BPO
		} else {
			rawQuantity = -2; //-2 = BPC
		}

		return createAsset(null, owner, count, flagID, itemID, typeID, locationID, singleton, rawQuantity, null);
	}

	public static List<Asset> convertAsset(final List<EveAsset<?>> eveAssets, final Owner owner) {
		List<Asset> assets = new ArrayList<Asset>();
		toDeepAsset(eveAssets, assets, null, owner);
		return assets;
	}
	private static void toDeepAsset(final List<EveAsset<?>> eveAssets, final List<Asset> assets, final Asset parentAsset, final Owner owner) {
		for (EveAsset<?> eveAsset : eveAssets) {
			Asset asset = toAsset(owner, eveAsset, parentAsset);
			if (parentAsset == null) {
				assets.add(asset);
			} else {
				parentAsset.addAsset(asset);
			}
			toDeepAsset(new ArrayList<EveAsset<?>>(eveAsset.getAssets()), assets, asset, owner);
		}
	}

	private static Asset toAsset(final Owner owner, final EveAsset<?> eveAsset, final Asset parentAsset) {
		long count = eveAsset.getQuantity();
		int flagID = eveAsset.getFlag();
		long itemID = eveAsset.getItemID();
		int typeID = eveAsset.getTypeID();
		long locationID;
		if (eveAsset.getLocationID() != null) { //Top level
			locationID = eveAsset.getLocationID();
		} else if (parentAsset != null) { //Sub level
			locationID = parentAsset.getLocation().getLocationID();
		} else { //Fail (fallback)
			locationID = 0;
		}
		boolean singleton  = eveAsset.getSingleton();
		int rawQuantity = eveAsset.getRawQuantity();

		return createAsset(parentAsset, owner, count, flagID, itemID, typeID, locationID, singleton, rawQuantity, null);

	}
	public static List<MarketOrder> convertMarketOrders(final List<ApiMarketOrder> apiMarketOrders, final Owner owner) {
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		for (ApiMarketOrder apiMarketOrder : apiMarketOrders) {
			marketOrders.add(toMarketOrder(apiMarketOrder, owner));
		}
		return marketOrders;
	}

	private static MarketOrder toMarketOrder(final ApiMarketOrder apiMarketOrder, final Owner owner) {
		Item item = ApiIdConverter.getItem(apiMarketOrder.getTypeID());
		Location location = ApiIdConverter.getLocation(apiMarketOrder.getStationID(), null);
		return new MarketOrder(apiMarketOrder, item, location, owner);
	}

	public static List<Asset> assetMarketOrder(final List<MarketOrder> marketOrders, final Owner owner) {
		List<Asset> assets = new ArrayList<Asset>();
		for (MarketOrder marketOrder : marketOrders) {
			if (marketOrder.getOrderState() == 0 && marketOrder.getVolRemaining() > 0
					&& ((marketOrder.getBid() < 1 && Settings.get().isIncludeSellOrders())
					|| (marketOrder.getBid() > 0 && Settings.get().isIncludeBuyOrders()))
					) {
				Asset asset = toAssetMarketOrder(marketOrder, owner);
				assets.add(asset);
			}
		}
		return assets;
	}

	private static Asset toAssetMarketOrder(final MarketOrder marketOrder, final Owner owner) {
		int typeID = marketOrder.getTypeID();
		long locationID = marketOrder.getStationID();
		long count = marketOrder.getVolRemaining();
		long itemID = marketOrder.getOrderID();
		String flag;
		if (marketOrder.getBid() < 1) { //Sell
			flag = General.get().marketOrderSellFlag();
		} else { //Buy
			flag = General.get().marketOrderBuyFlag();
		}
		int flagID = 0;
		boolean singleton = false;
		int rawQuantity = 0;

		return createAsset(null, owner, count, flagID, itemID, typeID, locationID, singleton, rawQuantity, flag);
	}

	public static Map<Contract, List<ContractItem>> convertContracts(final Map<EveContract, List<EveContractItem>> eveContracts) {
		Map<Contract, List<ContractItem>> contracts = new HashMap<Contract, List<ContractItem>>();
		for (Entry<EveContract, List<EveContractItem>> entry : eveContracts.entrySet()) {
			Contract contract = toContract(entry.getKey());
			List<ContractItem> contractItems = convertContractItems(entry.getValue(), contract);
			contracts.put(contract, contractItems);
		}
		return contracts;
	}

	public static List<ContractItem> convertContractItems(final List<EveContractItem> eveContractItems, Contract contract) {
		List<ContractItem> contractItems = new ArrayList<ContractItem>();
		for (EveContractItem eveContractItem : eveContractItems) {
			contractItems.add(toContractItem(eveContractItem, contract));
		}
		return contractItems;
	}

	public static Contract toContract(final EveContract eveContract) {
		String acceptor = ApiIdConverter.getOwnerName(eveContract.getAcceptorID());
		String assignee = ApiIdConverter.getOwnerName(eveContract.getAssigneeID());
		String issuerCorp = ApiIdConverter.getOwnerName(eveContract.getIssuerCorpID());
		String issuer = ApiIdConverter.getOwnerName(eveContract.getIssuerID());
		Location endStation = ApiIdConverter.getLocation(eveContract.getEndStationID());
		Location startStation = ApiIdConverter.getLocation(eveContract.getStartStationID());
		return new Contract(eveContract, acceptor, assignee, issuerCorp, issuer, startStation, endStation);
	}

	private static ContractItem toContractItem(final EveContractItem eveContractItem, Contract contract) {
		Item item = ApiIdConverter.getItem(eveContractItem.getTypeID());
		return new ContractItem(eveContractItem, contract, item);
	}

	
	public static List<Asset> assetContracts(final List<ContractItem> contractItems, final Owner owner) {
		List<Asset> list = new ArrayList<Asset>();
		if (!Settings.get().isIncludeContracts()) {
			return list;
		}
		for (ContractItem contractItem : contractItems) {
			if ((contractItem.getContract().getStatus() == ContractStatus.INPROGRESS
					|| contractItem.getContract().getStatus() == ContractStatus.OUTSTANDING)) {
				Asset asset = toAssetContract(contractItem, owner);
				list.add(asset);
			}
		}
		return list;
	}

	private static Asset toAssetContract(final ContractItem contractItem, final Owner owner) {
		long count = contractItem.getQuantity();
		int flagID = 0;
		String flag;
		if (contractItem.isIncluded()) { //Sell
			flag = General.get().contractIncluded();
		} else { //Buy
			flag = General.get().contractExcluded();
		}
		long itemID = contractItem.getRecordID();
		int typeID = contractItem.getTypeID();
		long locationID = contractItem.getContract().getStartStationID();
		boolean singleton  = contractItem.isSingleton();
		//XXX - Workaround invalid singleton values in ContractItems
		if (contractItem.getItem().isBlueprint() && !singleton) {
			singleton = true;
		}
		int rawQuantity;
		if (contractItem.getRawQuantity() == null) {
			rawQuantity = 0;
		} else if (contractItem.getRawQuantity() == -1) {
			rawQuantity = -1;
		} else if (contractItem.getRawQuantity() == -2) {
			rawQuantity = -2;
		} else {
			rawQuantity = 0;
		}

		return createAsset(null, owner, count, flagID, itemID, typeID, locationID, singleton, rawQuantity, flag);
	}

	public static List<IndustryJob> convertIndustryJobs(final List<ApiIndustryJob> apiIndustryJobs, final Owner owner) {
		List<IndustryJob> industryJobs = new ArrayList<IndustryJob>();
		for (ApiIndustryJob apiIndustryJob : apiIndustryJobs) {
			industryJobs.add(toIndustryJob(apiIndustryJob, owner));
		}
		return industryJobs;
	}

	private static IndustryJob toIndustryJob(final ApiIndustryJob apiIndustryJob, final Owner owner) {
		Item item = ApiIdConverter.getItem(apiIndustryJob.getInstalledItemTypeID());
		long locationID = toLocationID(apiIndustryJob);
		Location location = ApiIdConverter.getLocation(locationID);
		Item output = ApiIdConverter.getItem(apiIndustryJob.getOutputTypeID());
		return new IndustryJob(apiIndustryJob, item, location, owner, output.getPortion());
	}

	private static long toLocationID(final ApiIndustryJob apiIndustryJob) {
		boolean location = ApiIdConverter.isLocationOK(apiIndustryJob.getInstalledItemLocationID());
		if (location) {
			return apiIndustryJob.getInstalledItemLocationID();
		}
		location = ApiIdConverter.isLocationOK(apiIndustryJob.getContainerLocationID());
		if (location) {
			return apiIndustryJob.getContainerLocationID();
		}
		LOG.error("Failed to find locationID for IndustryJob. InstalledItemLocationID: " + apiIndustryJob.getInstalledItemLocationID() + " - ContainerLocationID: " + apiIndustryJob.getContainerLocationID());
		return -1;
	}

	public static Asset createAsset(final Asset parentEveAsset,
			Owner owner, long count, int flagID, long itemID,
			int typeID, long locationID, boolean singleton, int rawQuantity, String flag) {
		//Calculated:
		Item item = ApiIdConverter.getItem(typeID);
		Location location = ApiIdConverter.getLocation(locationID, parentEveAsset);
		List<Asset> parents = ApiIdConverter.getParents(parentEveAsset);
		if (flag == null) {
			flag = ApiIdConverter.flag(flagID, parentEveAsset);
		}
		return new Asset(item, location, owner, count, parents, flag, flagID, itemID, singleton, rawQuantity);
	}

	public static Map<Long, Journal> convertJournals(final List<ApiJournalEntry> apiJournals, final Owner owner, final int accountKey) {
		Map<Long, Journal> journals = new HashMap<Long, Journal>();
		for (ApiJournalEntry apiJournal : apiJournals) {
			Journal journal = convertJournal(apiJournal, owner, accountKey);
			journals.put(journal.getRefID(), journal);
		}
		return journals;
	}

	public static Journal convertJournal(final ApiJournalEntry apiJournal, final Owner owner, final int accountKey) {
		Journal journal = new Journal(apiJournal, owner, accountKey);
		return journal;
	}

	public static Map<Long, Transaction> convertTransactions(final List<ApiWalletTransaction> apiJournals, final Owner owner, final int accountKey) {
		Map<Long, Transaction> transactions = new HashMap<Long, Transaction>();
		for (ApiWalletTransaction apiTransaction : apiJournals) {
			Transaction transaction = convertTransaction(apiTransaction, owner, accountKey);
			transactions.put(transaction.getTransactionID(), transaction);
		}
		return transactions;
	}

	public static Transaction convertTransaction(final ApiWalletTransaction apiTransaction, final Owner owner, final int accountKey) {
		Item item = ApiIdConverter.getItem(apiTransaction.getTypeID());
		Location location = ApiIdConverter.getLocation(apiTransaction.getStationID());
		Transaction transaction = new Transaction(apiTransaction, item, location, owner, accountKey);
		return transaction;
	}
}
