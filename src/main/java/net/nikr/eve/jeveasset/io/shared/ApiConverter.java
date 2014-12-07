/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import com.beimin.eveapi.model.shared.AccountBalance;
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.model.shared.Contract;
import com.beimin.eveapi.model.shared.ContractItem;
import com.beimin.eveapi.model.shared.ContractStatus;
import com.beimin.eveapi.model.shared.IndustryJob;
import com.beimin.eveapi.model.shared.JournalEntry;
import com.beimin.eveapi.model.shared.MarketOrder;
import com.beimin.eveapi.model.shared.WalletTransaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.MyAccountBalance;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.journal.MyJournal;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.transaction.MyTransaction;
import net.nikr.eve.jeveasset.i18n.General;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApiConverter {

	private static final Logger LOG = LoggerFactory.getLogger(ApiConverter.class);;

	private ApiConverter() { }

	public static List<MyAccountBalance> convertAccountBalance(final List<AccountBalance> eveAccountBalances, final Owner owner) {
		List<MyAccountBalance> accountBalances = new ArrayList<MyAccountBalance>();
		for (AccountBalance eveAccountBalance : eveAccountBalances) {
			accountBalances.add( new MyAccountBalance(eveAccountBalance, owner));
		}
		return accountBalances;
	}

	public static List<MyAsset> assetIndustryJob(final List<MyIndustryJob> industryJobs, final Owner owner) {
		List<MyAsset> assets = new ArrayList<MyAsset>();
		for (MyIndustryJob industryJob : industryJobs) {
			if (!industryJob.isDelivered()) {
				MyAsset asset = toAssetIndustryJob(industryJob, owner);
				assets.add(asset);
			}
		}
		return assets;
	}

	private static MyAsset toAssetIndustryJob(final MyIndustryJob industryJob, final Owner owner) {
		int typeID = industryJob.getBlueprintTypeID();
		long locationID = toLocationID(industryJob);
		long count = 1;
		long itemID = industryJob.getBlueprintID();
		int flagID = 0;
		String flag = "Industry Job";
		boolean singleton  = true;
		int rawQuantity = -2;
		return createAsset(null, owner, count, flagID, itemID, typeID, locationID, singleton, rawQuantity, flag);
	}

	public static List<MyAsset> convertAsset(final List<Asset<?>> eveAssets, final Owner owner) {
		List<MyAsset> assets = new ArrayList<MyAsset>();
		toDeepAsset(eveAssets, assets, null, owner);
		return assets;
	}
	private static void toDeepAsset(final List<Asset<?>> eveAssets, final List<MyAsset> assets, final MyAsset parentAsset, final Owner owner) {
		for (Asset<?> eveAsset : eveAssets) {
			MyAsset asset = toAsset(owner, eveAsset, parentAsset);
			if (parentAsset == null) {
				assets.add(asset);
			} else {
				parentAsset.addAsset(asset);
			}
			toDeepAsset(new ArrayList<Asset<?>>(eveAsset.getAssets()), assets, asset, owner);
		}
	}

	private static MyAsset toAsset(final Owner owner, final Asset<?> eveAsset, final MyAsset parentAsset) {
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
	public static List<MyMarketOrder> convertMarketOrders(final List<MarketOrder> apiMarketOrders, final Owner owner) {
		List<MyMarketOrder> marketOrders = new ArrayList<MyMarketOrder>();
		for (MarketOrder apiMarketOrder : apiMarketOrders) {
			marketOrders.add(toMarketOrder(apiMarketOrder, owner));
		}
		return marketOrders;
	}

	private static MyMarketOrder toMarketOrder(final MarketOrder apiMarketOrder, final Owner owner) {
		Item item = ApiIdConverter.getItem(apiMarketOrder.getTypeID());
		MyLocation location = ApiIdConverter.getLocation(apiMarketOrder.getStationID(), null);
		return new MyMarketOrder(apiMarketOrder, item, location, owner);
	}

	public static List<MyAsset> assetMarketOrder(final List<MyMarketOrder> marketOrders, final Owner owner) {
		List<MyAsset> assets = new ArrayList<MyAsset>();
		for (MyMarketOrder marketOrder : marketOrders) {
			if (marketOrder.getOrderState() == 0 && marketOrder.getVolRemaining() > 0
					&& ((marketOrder.getBid() < 1 && Settings.get().isIncludeSellOrders())
					|| (marketOrder.getBid() > 0 && Settings.get().isIncludeBuyOrders()))
					) {
				MyAsset asset = toAssetMarketOrder(marketOrder, owner);
				assets.add(asset);
			}
		}
		return assets;
	}

	private static MyAsset toAssetMarketOrder(final MyMarketOrder marketOrder, final Owner owner) {
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

	public static Map<MyContract, List<MyContractItem>> convertContracts(final Map<Contract, List<ContractItem>> eveContracts) {
		Map<MyContract, List<MyContractItem>> contracts = new HashMap<MyContract, List<MyContractItem>>();
		for (Entry<Contract, List<ContractItem>> entry : eveContracts.entrySet()) {
			MyContract contract = toContract(entry.getKey());
			List<MyContractItem> contractItems = convertContractItems(entry.getValue(), contract);
			contracts.put(contract, contractItems);
		}
		return contracts;
	}

	public static List<MyContractItem> convertContractItems(final List<ContractItem> eveContractItems, MyContract contract) {
		List<MyContractItem> contractItems = new ArrayList<MyContractItem>();
		for (ContractItem eveContractItem : eveContractItems) {
			contractItems.add(toContractItem(eveContractItem, contract));
		}
		return contractItems;
	}

	public static MyContract toContract(final Contract eveContract) {
		MyLocation endStation = ApiIdConverter.getLocation(eveContract.getEndStationID());
		MyLocation startStation = ApiIdConverter.getLocation(eveContract.getStartStationID());
		return new MyContract(eveContract, startStation, endStation);
	}

	private static MyContractItem toContractItem(final ContractItem eveContractItem, MyContract contract) {
		Item item = ApiIdConverter.getItem(eveContractItem.getTypeID());
		return new MyContractItem(eveContractItem, contract, item);
	}

	
	public static List<MyAsset> assetContracts(final List<MyContractItem> contractItems, final Owner owner) {
		List<MyAsset> list = new ArrayList<MyAsset>();
		for (MyContractItem contractItem : contractItems) {
			if (	//Not completed
					(contractItem.getContract().getStatus() == ContractStatus.INPROGRESS 
					|| contractItem.getContract().getStatus() == ContractStatus.OUTSTANDING)
					//Owned
					&& contractItem.getContract().getIssuerID() == owner.getOwnerID()
					//Sell
					&& ((contractItem.isIncluded() && Settings.get().isIncludeSellContracts())
					//Buy
					|| (!contractItem.isIncluded() && Settings.get().isIncludeBuyContracts()))
					) { 
				MyAsset asset = toAssetContract(contractItem, owner);
				list.add(asset);
			}
		}
		return list;
	}

	private static MyAsset toAssetContract(final MyContractItem contractItem, final Owner owner) {
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

	public static List<MyIndustryJob> convertIndustryJobs(final List<IndustryJob> apiIndustryJobs, final Owner owner) {
		List<MyIndustryJob> industryJobs = new ArrayList<MyIndustryJob>();
		for (IndustryJob apiIndustryJob : apiIndustryJobs) {
			industryJobs.add(toIndustryJob(apiIndustryJob, owner));
		}
		return industryJobs;
	}

	private static MyIndustryJob toIndustryJob(final IndustryJob apiIndustryJob, final Owner owner) {
		Item item = ApiIdConverter.getItem(apiIndustryJob.getBlueprintTypeID());
		long locationID = toLocationID(apiIndustryJob);
		MyLocation location = ApiIdConverter.getLocation(locationID);
		Item blueprint = ApiIdConverter.getItem(apiIndustryJob.getBlueprintTypeID());
		Item product = ApiIdConverter.getItem(blueprint.getProduct());
		return new MyIndustryJob(apiIndustryJob, item, location, owner, product.getPortion(), product.getTypeID());
	}

	private static long toLocationID(final IndustryJob apiIndustryJob) {
		boolean location = ApiIdConverter.isLocationOK(apiIndustryJob.getBlueprintLocationID());
		if (location) {
			return apiIndustryJob.getBlueprintLocationID();
		}
		location = ApiIdConverter.isLocationOK(apiIndustryJob.getOutputLocationID());
		if (location) {
			return apiIndustryJob.getOutputLocationID();
		}
		location = ApiIdConverter.isLocationOK(apiIndustryJob.getSolarSystemID());
		if (location) {
			return apiIndustryJob.getSolarSystemID();
		}
		LOG.error("Failed to find locationID for IndustryJob. InstalledItemLocationID: " + apiIndustryJob.getBlueprintLocationID() + " - ContainerLocationID: " + apiIndustryJob.getOutputLocationID());
		return -1;
	}

	public static MyAsset createAsset(final MyAsset parentEveAsset,
			Owner owner, long count, int flagID, long itemID,
			int typeID, long locationID, boolean singleton, int rawQuantity, String flag) {
		//Calculated:
		Item item = ApiIdConverter.getItem(typeID);
		MyLocation location = ApiIdConverter.getLocation(locationID, parentEveAsset);
		List<MyAsset> parents = ApiIdConverter.getParents(parentEveAsset);
		if (flag == null) {
			flag = ApiIdConverter.flag(flagID, parentEveAsset);
		}
		return new MyAsset(item, location, owner, count, parents, flag, flagID, itemID, singleton, rawQuantity);
	}

	public static Map<Long, MyJournal> convertJournals(final List<JournalEntry> apiJournals, final Owner owner, final int accountKey) {
		Map<Long, MyJournal> journals = new HashMap<Long, MyJournal>();
		for (JournalEntry apiJournal : apiJournals) {
			MyJournal journal = convertJournal(apiJournal, owner, accountKey);
			journals.put(journal.getRefID(), journal);
		}
		return journals;
	}

	public static MyJournal convertJournal(final JournalEntry apiJournal, final Owner owner, final int accountKey) {
		MyJournal journal = new MyJournal(apiJournal, owner, accountKey);
		return journal;
	}

	public static Map<Long, MyTransaction> convertTransactions(final List<WalletTransaction> apiJournals, final Owner owner, final int accountKey) {
		Map<Long, MyTransaction> transactions = new HashMap<Long, MyTransaction>();
		for (WalletTransaction apiTransaction : apiJournals) {
			MyTransaction transaction = convertTransaction(apiTransaction, owner, accountKey);
			transactions.put(transaction.getTransactionID(), transaction);
		}
		return transactions;
	}

	public static MyTransaction convertTransaction(final WalletTransaction apiTransaction, final Owner owner, final int accountKey) {
		Item item = ApiIdConverter.getItem(apiTransaction.getTypeID());
		MyLocation location = ApiIdConverter.getLocation(apiTransaction.getStationID());
		MyTransaction transaction = new MyTransaction(apiTransaction, item, location, owner, accountKey);
		return transaction;
	}
}
