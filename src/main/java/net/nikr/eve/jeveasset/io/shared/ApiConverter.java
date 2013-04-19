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
import com.beimin.eveapi.shared.contract.EveContract;
import com.beimin.eveapi.shared.contract.items.EveContractItem;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.tabs.contracts.Contract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractItem;
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

	public static List<Asset> assetIndustryJob(final List<IndustryJob> industryJobs, final Owner owner, final Settings settings) {
		List<Asset> eveAssets = new ArrayList<Asset>();
		for (IndustryJob industryJob : industryJobs) {
			if (!industryJob.isCompleted()) {
				Asset eveAsset = toAssetIndustryJob(industryJob, owner, settings);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static Asset toAssetIndustryJob(final IndustryJob industryJob, final Owner owner, final Settings settings) {
		int typeID = industryJob.getInstalledItemTypeID();
		long locationID = toLocationId(industryJob, settings);
		long count = industryJob.getInstalledItemQuantity();
		long id = industryJob.getInstalledItemID();
		int flagID = industryJob.getInstalledItemFlag();
		boolean singleton  = false;
		int rawQuantity;
		if (industryJob.getInstalledItemCopy() == 0) {
			rawQuantity = 0; //0 = BPO
		} else {
			rawQuantity = -2; //-2 = BPC
		}

		return createAsset(settings, null, owner, count, flagID, id, typeID, locationID, singleton, rawQuantity, null);
	}

	public static List<Asset> convertAsset(final List<EveAsset<?>> assets, final Owner owner, final Settings settings) {
		List<Asset> eveAssets = new ArrayList<Asset>();
		toDeepAsset(assets, eveAssets, null, owner, settings);
		return eveAssets;
	}
	private static void toDeepAsset(final List<EveAsset<?>> eveAssets, final List<Asset> assets, final Asset parentEveAsset, final Owner owner, final Settings settings) {
		for (EveAsset<?> asset : eveAssets) {
			Asset eveAsset = toAsset(owner, asset, parentEveAsset, settings);
			if (parentEveAsset == null) {
				assets.add(eveAsset);
			} else {
				parentEveAsset.addEveAsset(eveAsset);
			}
			toDeepAsset(new ArrayList<EveAsset<?>>(asset.getAssets()), assets, eveAsset, owner, settings);
		}
	}

	private static Asset toAsset(final Owner owner, final EveAsset<?> apiAsset, final Asset parentEveAsset, final Settings settings) {
		long count = apiAsset.getQuantity();
		int flagID = apiAsset.getFlag();
		long itemId = apiAsset.getItemID();
		int typeID = apiAsset.getTypeID();
		long locationID;
		if (apiAsset.getLocationID() != null) { //Top level
			locationID = apiAsset.getLocationID();
		} else if (parentEveAsset != null) { //Sub level
			locationID = parentEveAsset.getLocation().getLocationID();
		} else { //Fail (fallback)
			locationID = 0;
		}
		boolean singleton  = apiAsset.getSingleton();
		int rawQuantity = apiAsset.getRawQuantity();

		return createAsset(settings, parentEveAsset, owner, count, flagID, itemId, typeID, locationID, singleton, rawQuantity, null);

	}
	public static List<MarketOrder> convertMarketOrders(final List<ApiMarketOrder> apiMarketOrders, final Owner owner, final Settings settings) {
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		for (ApiMarketOrder apiMarketOrder : apiMarketOrders) {
			marketOrders.add(toMarketOrder(apiMarketOrder, owner, settings));
		}
		return marketOrders;
	}

	private static MarketOrder toMarketOrder(final ApiMarketOrder apiMarketOrder, final Owner owner, final Settings settings) {
		Item item = ApiIdConverter.getItem(apiMarketOrder.getTypeID(), settings.getItems());
		Location location = ApiIdConverter.getLocation(apiMarketOrder.getStationID(), null, settings.getLocations());
		return new MarketOrder(apiMarketOrder, item, location, owner);
	}

	public static List<Asset> assetMarketOrder(final List<MarketOrder> marketOrders, final Owner owner, final Settings settings) {
		List<Asset> eveAssets = new ArrayList<Asset>();
		for (MarketOrder marketOrder : marketOrders) {
			if (marketOrder.getOrderState() == 0 && marketOrder.getVolRemaining() > 0
					&& ((marketOrder.getBid() < 1 && settings.isIncludeSellOrders())
					|| (marketOrder.getBid() > 0 && settings.isIncludeBuyOrders()))
					) {
				Asset eveAsset = toAssetMarketOrder(marketOrder, owner, settings);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static Asset toAssetMarketOrder(final MarketOrder marketOrder, final Owner owner, final Settings settings) {
		int typeID = marketOrder.getTypeID();
		long locationID = marketOrder.getStationID();
		long count = marketOrder.getVolRemaining();
		long itemId = marketOrder.getOrderID();
		String flag;
		if (marketOrder.getBid() < 1) { //Sell
			flag = General.get().marketOrderSellFlag();
		} else { //Buy
			flag = General.get().marketOrderBuyFlag();
		}
		int flagID = 0;
		boolean singleton  = true;
		int rawQuantity = 0;

		return createAsset(settings, null, owner, count, flagID, itemId, typeID, locationID, singleton, rawQuantity, flag);
	}

	public static Map<Contract, List<ContractItem>> convertContracts(final Map<EveContract, List<EveContractItem>> eveContracts, final Settings settings) {
		Map<Contract, List<ContractItem>> contracts = new HashMap<Contract, List<ContractItem>>();
		for (Entry<EveContract, List<EveContractItem>> entry : eveContracts.entrySet()) {
			Contract contract = toContract(entry.getKey(), settings);
			List<ContractItem> contractItems = convertContractItems(entry.getValue(), contract, settings);
			contracts.put(contract, contractItems);
		}
		return contracts;
	}

	public static List<ContractItem> convertContractItems(final List<EveContractItem> eveContractItems, Contract contract, final Settings settings) {
		List<ContractItem> contractItems = new ArrayList<ContractItem>();
		for (EveContractItem eveContractItem : eveContractItems) {
			contractItems.add(toContractItem(eveContractItem, contract, settings));
		}
		return contractItems;
	}

	public static Contract toContract(final EveContract eveContract, final Settings settings) {
		String acceptor = ApiIdConverter.getOwnerName(eveContract.getAcceptorID(), settings.getOwners());
		String assignee = ApiIdConverter.getOwnerName(eveContract.getAssigneeID(), settings.getOwners());
		String issuerCorp = ApiIdConverter.getOwnerName(eveContract.getIssuerCorpID(), settings.getOwners());
		String issuer = ApiIdConverter.getOwnerName(eveContract.getIssuerID(), settings.getOwners());
		Location endStation = ApiIdConverter.getLocation(eveContract.getEndStationID(), null, settings.getLocations());
		Location startStation = ApiIdConverter.getLocation(eveContract.getStartStationID(), null, settings.getLocations());
		return new Contract(eveContract, acceptor, assignee, issuerCorp, issuer, startStation, endStation);
	}

	private static ContractItem toContractItem(final EveContractItem eveContractItem, Contract contract, final Settings settings) {
		Item item = ApiIdConverter.getItem(eveContractItem.getTypeID(), settings.getItems());
		return new ContractItem(eveContractItem, contract, item);
	}

	
	public static List<Asset> assetContracts(final List<ContractItem> contractItems, final Owner owner, final Settings settings) {
		List<Asset> list = new ArrayList<Asset>();
		if (!settings.isIncludeContracts()) {
			return list;
		}
		for (ContractItem contractItem : contractItems) {
			Asset asset = toAssetContract(contractItem, owner, settings);
			list.add(asset);
		}
		return list;
	}

	private static Asset toAssetContract(final ContractItem contractItem, final Owner owner, final Settings settings) {
		long count = contractItem.getQuantity();
		int flagID = 0;
		String flag;
		if (contractItem.isIncluded()) { //Sell
			flag = General.get().contractIncluded();
		} else { //Buy
			flag = General.get().contractExcluded();
		}
		long itemId = 0;
		int typeID = contractItem.getTypeID();
		long locationID = contractItem.getContract().getStartStationID();
		boolean singleton  = contractItem.isSingleton();
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

		return createAsset(settings, null, owner, count, flagID, itemId, typeID, locationID, singleton, rawQuantity, flag);
	}

	public static List<IndustryJob> convertIndustryJobs(final List<ApiIndustryJob> apiIndustryJobs, final Owner owner, final Settings settings) {
		List<IndustryJob> industryJobs = new ArrayList<IndustryJob>();
		for (ApiIndustryJob apiIndustryJob : apiIndustryJobs) {
			industryJobs.add(toIndustryJob(apiIndustryJob, owner, settings));
		}
		return industryJobs;
	}

	private static IndustryJob toIndustryJob(final ApiIndustryJob apiIndustryJob, final Owner owner, final Settings settings) {
		Item item = ApiIdConverter.getItem(apiIndustryJob.getInstalledItemTypeID(), settings.getItems());
		long locationID = toLocationId(apiIndustryJob, settings);
		Location location = ApiIdConverter.getLocation(locationID, null, settings.getLocations());
		return new IndustryJob(apiIndustryJob, item, location, owner);
	}

	private static long toLocationId(final ApiIndustryJob apiIndustryJob, final Settings settings) {
		boolean location = ApiIdConverter.isLocationOK(apiIndustryJob.getInstalledItemLocationID(), null, settings.getLocations());
		if (location) {
			return apiIndustryJob.getInstalledItemLocationID();
		}
		location = ApiIdConverter.isLocationOK(apiIndustryJob.getContainerLocationID(), null, settings.getLocations());
		if (location) {
			return apiIndustryJob.getContainerLocationID();
		}
		LOG.error("Failed to find locationID for IndustryJob. InstalledItemLocationID: " + apiIndustryJob.getInstalledItemLocationID() + " - ContainerLocationID: " + apiIndustryJob.getContainerLocationID());
		return -1;
	}

	public static Asset createAsset(final Settings settings, final Asset parentEveAsset,
			Owner owner, long count, int flagID, long itemId,
			int typeID, long locationID, boolean singleton, int rawQuantity, String flag) {
		//Calculated:
		Item item = ApiIdConverter.getItem(typeID, settings.getItems());
		Location location = ApiIdConverter.getLocation(locationID, parentEveAsset, settings.getLocations());
		List<Asset> parents = ApiIdConverter.getParents(parentEveAsset);
		if (flag == null) {
			flag = ApiIdConverter.flag(flagID, parentEveAsset, settings.getItemFlags());
		}
		return new Asset(item, location, owner, count, parents, flag, flagID, itemId, singleton, rawQuantity);
	}

	public static List<WalletTransaction> convertWalletTransactions(final List<ApiWalletTransaction> apiWalletTransactions, final Owner owner, final Settings settings) {
		List<WalletTransaction> walletTransactions = new ArrayList<WalletTransaction>();
		for (ApiWalletTransaction apiWalletTransaction : apiWalletTransactions) {
			walletTransactions.add(toWalletTransaction(owner, apiWalletTransaction, settings));
		}
		return walletTransactions;
	}

	private static WalletTransaction toWalletTransaction(final Owner owner, final ApiWalletTransaction apiWalletTransaction, final Settings settings) {
		Item item = ApiIdConverter.getItem(apiWalletTransaction.getTypeID(), settings.getItems());
		Location location = ApiIdConverter.getLocation(apiWalletTransaction.getStationID(), null, settings.getLocations());
		return new WalletTransaction(apiWalletTransaction, item, location, owner);
	}
}
