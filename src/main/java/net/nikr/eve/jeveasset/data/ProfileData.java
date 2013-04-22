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
package net.nikr.eve.jeveasset.data;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.beimin.eveapi.shared.contract.ContractType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.tabs.contracts.Contract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class ProfileData {
	private Settings settings;
	private ProfileManager profileManager;
	
	private final EventList<ContractItem> contractItemEventList = new BasicEventList<ContractItem>();
	private final EventList<IndustryJob> industryJobsEventList = new BasicEventList<IndustryJob>();
	private final EventList<MarketOrder> marketOrdersEventList = new BasicEventList<MarketOrder>();
	private final EventList<WalletTransaction> walletTransactionsEventList = new BasicEventList<WalletTransaction>();
	private final EventList<Asset> assetsEventList = new BasicEventList<Asset>();
	private final EventList<AccountBalance> accountBalanceEventList = new BasicEventList<AccountBalance>();
	private Map<Integer, List<Asset>> uniqueAssetsDuplicates = null; //TypeID : int
	private Map<Integer, MarketPriceData> marketPriceData; //TypeID : int
	private final List<String> owners = new ArrayList<String>();

	public ProfileData(Settings settings, ProfileManager profileManager) {
		this.settings = settings;
		this.profileManager = profileManager;
	}

	public Set<Integer> getPriceTypeIDs() {
		return createPriceTypeIDs(); //always needs to be fresh :)
	}

	public EventList<AccountBalance> getAccountBalanceEventList() {
		return accountBalanceEventList;
	}

	public EventList<Asset> getAssetsEventList() {
		return assetsEventList;
	}

	public EventList<IndustryJob> getIndustryJobsEventList() {
		return industryJobsEventList;
	}

	public EventList<MarketOrder> getMarketOrdersEventList() {
		return marketOrdersEventList;
	}

	public EventList<WalletTransaction> getWalletTransactionsEventList() {
		return walletTransactionsEventList;
	}

	public EventList<ContractItem> getContractItemEventList() {
		return contractItemEventList;
	}

	public List<String> getOwners(boolean all) {
		List<String> sortedOwners = new ArrayList<String>(owners);
		if (all) {
			sortedOwners.add(0, General.get().all());
		}
		return sortedOwners;
	}

	private Set<Integer> createPriceTypeIDs() {
		Set<Integer> priceTypeIDs = new HashSet<Integer>();
		for (Account account : profileManager.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				//Add Assets to uniqueIds
				deepAssets(owner.getAssets(), priceTypeIDs);
				//Add Market Orders to uniqueIds
				for (MarketOrder marketOrder : owner.getMarketOrders()) {
					Item item = marketOrder.getItem();
					if (item.isMarketGroup()) {
						priceTypeIDs.add(item.getTypeID());
					}
				}
				//Add Wallet Transaction to uniqueIds
				for (WalletTransaction walletTransaction : owner.getWalletTransactions()) {
					Item item = walletTransaction.getItem();
					if (item.isMarketGroup()) {
						priceTypeIDs.add(item.getTypeID());
					}
				}
				//Add Industry Job to uniqueIds
				for (IndustryJob industryJob : owner.getIndustryJobs()) {
					Item itemType = industryJob.getItem();
					if (itemType.isMarketGroup()) {
						priceTypeIDs.add(itemType.getTypeID());
					}
				}
				//Add Contract to uniqueIds
				for (Map.Entry<Contract, List<ContractItem>> entry : owner.getContracts().entrySet()) {
					for (ContractItem contractItem : entry.getValue()) {
						Item item = contractItem.getItem();
						if (item.isMarketGroup()) {
							priceTypeIDs.add(item.getTypeID());
						}
					}
				}
				//Add StockpileItems to uniqueIds
				for (Stockpile stockpile : settings.getStockpiles()) {
					for (StockpileItem stockpileItem : stockpile.getItems()) {
						Item item = stockpileItem.getItem();
						if (item.isMarketGroup()) {
							priceTypeIDs.add(item.getTypeID());
						}
					}
				}
				//Add reprocessed items to price queue
				for (Item item : StaticData.get().getItems().values()) {
					for (ReprocessedMaterial reprocessedMaterial : item.getReprocessedMaterial()) {
						int typeID = reprocessedMaterial.getTypeID();
						Item reprocessedItem = StaticData.get().getItems().get(typeID);
						if (reprocessedItem != null && reprocessedItem.isMarketGroup()) {
							priceTypeIDs.add(typeID);
						}
					}
				}
			}
		}
		return priceTypeIDs;
	}

	private void deepAssets(List<Asset> assets, Set<Integer> priceTypeIDs) {
		for (Asset asset : assets) {
			//Unique Ids
			if (asset.getItem().isMarketGroup()) {
				priceTypeIDs.add(asset.getItem().getTypeID());
			}
			deepAssets(asset.getAssets(), priceTypeIDs);
		}
	}

	public void updateEventLists() {
		uniqueAssetsDuplicates = new HashMap<Integer, List<Asset>>();
		Set<String> uniqueOwners = new HashSet<String>();
		List<String> ownersOrders = new ArrayList<String>();
		List<String> ownersWallet = new ArrayList<String>();
		List<String> ownersJobs = new ArrayList<String>();
		List<String> ownersAssets = new ArrayList<String>();
		List<String> ownersAccountBalance = new ArrayList<String>();
		List<Long> contractIDs = new ArrayList<Long>();
		//Temp
		List<Asset> assets = new ArrayList<Asset>();
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		List<WalletTransaction> walletTransactions = new ArrayList<WalletTransaction>();
		List<IndustryJob> industryJobs = new ArrayList<IndustryJob>();
		List<ContractItem> contractItems = new ArrayList<ContractItem>();
		List<AccountBalance> accountBalance = new ArrayList<AccountBalance>();
		maximumPurchaseAge();
		//Add assets
		for (Account account : profileManager.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				if (owner.isShowAssets()) {
					uniqueOwners.add(owner.getName());
				} else {
					continue;
				}
				//Market Orders
				if (!owner.getMarketOrders().isEmpty() && !ownersOrders.contains(owner.getName())) {
					//Market Orders
					marketOrders.addAll(owner.getMarketOrders());
					//Assets
					addAssets(ApiConverter.assetMarketOrder(owner.getMarketOrders(), owner, settings), assets);
					ownersOrders.add(owner.getName());
				}
				//Wallet Transactions
				if (!owner.getWalletTransactions().isEmpty() && !ownersWallet.contains(owner.getName())) {
					//Wallet Transactions
					//FIXME - Corporation transactions can be added twice (one from corp key and one for char key)
					walletTransactions.addAll(owner.getWalletTransactions());
					//Assets
					//FIXME Wallet Transactions Assets
					//addAssets(ApiConverter.assetMarketOrder(owner.getMarketOrders(), owner, settings), assets);
					ownersWallet.add(owner.getName());
				}
				//Industry Jobs
				if (!owner.getIndustryJobs().isEmpty() && !ownersJobs.contains(owner.getName())) {
					//Industry Jobs
					industryJobs.addAll(owner.getIndustryJobs());
					//Assets
					//FIXME make Asset Industry Jobs optional?
					addAssets(ApiConverter.assetIndustryJob(owner.getIndustryJobs(), owner), assets);
					ownersJobs.add(owner.getName());
				}
				//Contracts
				for (Map.Entry<Contract, List<ContractItem>> entry : owner.getContracts().entrySet()) {
					//Contracts
					Contract contract = entry.getKey();
					if (contractIDs.contains(contract.getContractID())) {
						continue;
					}
					if (entry.getValue().isEmpty() 
						&& contract.getType() == ContractType.COURIER &&
						( //XXX - Workaround for alien contracts
							owner.getOwnerID() == contract.getAcceptorID()
							|| owner.getOwnerID() == contract.getAssigneeID()
							|| owner.getOwnerID() == contract.getIssuerID()
							|| (owner.getOwnerID() == contract.getIssuerCorpID() && contract.isForCorp())
						)
						) {
						contractIDs.add(contract.getContractID());
						contractItems.add(new ContractItem(contract));
					} else if (!entry.getValue().isEmpty()) {
						contractIDs.add(contract.getContractID());
						contractItems.addAll(entry.getValue());
					}
					//Assets
					List<Asset> contractAssets = ApiConverter.assetContracts(entry.getValue(), owner, settings);
					addAssets(contractAssets, assets);
				}
				//Assets
				if (!owner.getAssets().isEmpty() && !ownersAssets.contains(owner.getName())) {
					addAssets(owner.getAssets(), assets);
					ownersAssets.add(owner.getName());
				}
				//Account Balance
				if (!owner.getAccountBalances().isEmpty() && !ownersAccountBalance.contains(owner.getName())) {
					accountBalance.addAll(owner.getAccountBalances());
					ownersAccountBalance.add(owner.getName());
				}
				//Update MarketOrders dynamic values
				for (MarketOrder order : owner.getMarketOrders()) {
					Item item = order.getItem();
					//Price
					double price = ApiIdConverter.getPrice(item.getTypeID(), false, settings.getUserPrices(), settings.getPriceData());
					order.setDynamicPrice(price);
				}
				//Update IndustryJobs dynamic values
				for (IndustryJob job : owner.getIndustryJobs()) {
					Item itemType = job.getItem();
					//Price
					double price = ApiIdConverter.getPrice(itemType.getTypeID(), job.isBPC(), settings.getUserPrices(), settings.getPriceData());
					job.setDynamicPrice(price);
				}
				//Update Contracts dynamic values
				for (Map.Entry<Contract, List<ContractItem>> entry : owner.getContracts().entrySet()) {
					for (ContractItem contractItem : entry.getValue()) {
						Item item = contractItem.getItem();
						//Price
						double price = ApiIdConverter.getPrice(item.getTypeID(), contractItem.isBPC(), settings.getUserPrices(), settings.getPriceData());
						contractItem.setDynamicPrice(price);
					}
				}
			}
		}
		try {
			assetsEventList.getReadWriteLock().writeLock().lock();
			assetsEventList.clear();
			assetsEventList.addAll(assets);
		} finally {
			assetsEventList.getReadWriteLock().writeLock().unlock();
		}
		try {
			marketOrdersEventList.getReadWriteLock().writeLock().lock();
			marketOrdersEventList.clear();
			marketOrdersEventList.addAll(marketOrders);
		} finally {
			marketOrdersEventList.getReadWriteLock().writeLock().unlock();
		}
		try {
			walletTransactionsEventList.getReadWriteLock().writeLock().lock();
			walletTransactionsEventList.clear();
			walletTransactionsEventList.addAll(walletTransactions);
		} finally {
			walletTransactionsEventList.getReadWriteLock().writeLock().unlock();
		}
		try {
			industryJobsEventList.getReadWriteLock().writeLock().lock();
			industryJobsEventList.clear();
			industryJobsEventList.addAll(industryJobs);
		} finally {
			industryJobsEventList.getReadWriteLock().writeLock().unlock();
		}
		try {
			contractItemEventList.getReadWriteLock().writeLock().lock();
			contractItemEventList.clear();
			contractItemEventList.addAll(contractItems);
		} finally {
			contractItemEventList.getReadWriteLock().writeLock().unlock();
		}
		try {
			accountBalanceEventList.getReadWriteLock().writeLock().lock();
			accountBalanceEventList.clear();
			accountBalanceEventList.addAll(accountBalance);
		} finally {
			accountBalanceEventList.getReadWriteLock().writeLock().unlock();
		}
		//Sort Owners
		owners.clear();
		owners.addAll(uniqueOwners);
		Collections.sort(owners, new CaseInsensitiveComparator());
	}

	private void maximumPurchaseAge() {
		//Create Market Price Data
		marketPriceData = new HashMap<Integer, MarketPriceData>();
		//Date - maximumPurchaseAge in days
		Date maxAge = new Date(System.currentTimeMillis() - (settings.getMaximumPurchaseAge() * 24 * 60 * 60 * 1000L));
		for (Account account : profileManager.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				for (MarketOrder marketOrder : owner.getMarketOrders()) {
					if (marketOrder.getBid() > 0 //Buy orders only
							//at least one bought
							&& marketOrder.getVolRemaining() != marketOrder.getVolEntered()
							//Date in range or unlimited
							&& (marketOrder.getIssued().after(maxAge) || settings.getMaximumPurchaseAge() == 0)
							) {
						int typeID = marketOrder.getTypeID();
						if (!marketPriceData.containsKey(typeID)) {
							marketPriceData.put(typeID, new MarketPriceData());
						}
						MarketPriceData data = marketPriceData.get(typeID);
						data.update(marketOrder.getPrice(), marketOrder.getIssued());
					}
				}
			}
		}
	}

	private void addAssets(final List<Asset> assets, List<Asset> addTo) {
		for (Asset asset : assets) {
			//Date added
			if (settings.getAssetAdded().containsKey(asset.getItemID())) {
				asset.setAdded(settings.getAssetAdded().get(asset.getItemID()));
			} else {
				Date date = new Date();
				settings.getAssetAdded().put(asset.getItemID(), date);
				asset.setAdded(date);
			}
			//User price
			if (asset.getItem().isBlueprint() && !asset.isBPO()) { //Blueprint Copy
				asset.setUserPrice(settings.getUserPrices().get(-asset.getItem().getTypeID()));
			} else { //All other
				asset.setUserPrice(settings.getUserPrices().get(asset.getItem().getTypeID()));
			}
			//Market price
			asset.setMarketPriceData(marketPriceData.get(asset.getItem().getTypeID()));
			//User Item Names
			if (settings.getUserItemNames().containsKey(asset.getItemID())) {
				asset.setName(settings.getUserItemNames().get(asset.getItemID()).getValue());
			} else {
				asset.setName(asset.getTypeName());
			}
			//Contaioner
			String sContainer = "";
			for (Asset parentEveAsset : asset.getParents()) {
				if (!sContainer.isEmpty()) {
					sContainer = sContainer + ">";
				}
				if (!parentEveAsset.isUserName()) {
					sContainer = sContainer + parentEveAsset.getName() + " #" + parentEveAsset.getItemID();
				} else {
					sContainer = sContainer + parentEveAsset.getName();
				}
			}
			if (sContainer.isEmpty()) {
				sContainer = General.get().none();
			}
			asset.setContainer(sContainer);

			//Price data
			if (asset.getItem().isMarketGroup() && settings.getPriceData().containsKey(asset.getItem().getTypeID()) && !settings.getPriceData().get(asset.getItem().getTypeID()).isEmpty()) { //Market Price
				asset.setPriceData(settings.getPriceData().get(asset.getItem().getTypeID()));
			} else { //No Price :(
				asset.setPriceData(null);
			}

			//Reprocessed price
			asset.setPriceReprocessed(0);
			double priceReprocessed = 0;
			int portionSize = 0;
			for (ReprocessedMaterial material : asset.getItem().getReprocessedMaterial()) {
				//Calculate reprocessed price
				portionSize = material.getPortionSize();
				if (settings.getPriceData().containsKey(material.getTypeID())) {
					PriceData priceData = settings.getPriceData().get(material.getTypeID());
					double price;
					if (settings.getUserPrices().containsKey(material.getTypeID())) {
						price = settings.getUserPrices().get(material.getTypeID()).getValue();
					} else {
						price = Asset.getDefaultPriceReprocessed(priceData);
					}
					priceReprocessed = priceReprocessed + (price * settings.getReprocessSettings().getLeft(material.getQuantity()));
				}
			}
			if (priceReprocessed > 0 && portionSize > 0) {
				priceReprocessed = priceReprocessed / portionSize;
			}
			asset.setPriceReprocessed(priceReprocessed);

			//Type Count
			if (!uniqueAssetsDuplicates.containsKey(asset.getItem().getTypeID())) {
				uniqueAssetsDuplicates.put(asset.getItem().getTypeID(), new ArrayList<Asset>());
			}
			List<Asset> dup = uniqueAssetsDuplicates.get(asset.getItem().getTypeID());
			long newCount = asset.getCount();
			if (!dup.isEmpty()) {
				newCount = newCount + dup.get(0).getTypeCount();
			}
			dup.add(asset);
			for (Asset assetLoop : dup) {
				assetLoop.setTypeCount(newCount);
			}
			//Packaged Volume
			float volume = ApiIdConverter.getVolume(asset.getItem().getTypeID(), asset.isSingleton());
			asset.setVolume(volume);

			//Add asset
			addTo.add(asset);
			//Add sub-assets
			addAssets(asset.getAssets(), addTo);
		}
	}
}
