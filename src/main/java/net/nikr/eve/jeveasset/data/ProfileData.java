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
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.Contract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.journal.Journal;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.transaction.Transaction;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class ProfileData {
	private ProfileManager profileManager;
	
	private final EventList<ContractItem> contractItemEventList = new BasicEventList<ContractItem>();
	private final EventList<IndustryJob> industryJobsEventList = new BasicEventList<IndustryJob>();
	private final EventList<MarketOrder> marketOrdersEventList = new BasicEventList<MarketOrder>();
	private final EventList<Journal> journalEventList = new BasicEventList<Journal>();
	private final EventList<Transaction> transactionsEventList = new BasicEventList<Transaction>();
	private final EventList<Asset> assetsEventList = new BasicEventList<Asset>();
	private final EventList<AccountBalance> accountBalanceEventList = new BasicEventList<AccountBalance>();
	private Map<Integer, List<Asset>> uniqueAssetsDuplicates = null; //TypeID : int
	private Map<Integer, MarketPriceData> marketPriceData; //TypeID : int
	private final List<String> owners = new ArrayList<String>();

	public ProfileData(ProfileManager profileManager) {
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

	public EventList<Journal> getJournalEventList() {
		return journalEventList;
	}

	public EventList<Transaction> getTransactionsEventList() {
		return transactionsEventList;
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
				//Add Transaction to uniqueIds
				for (Transaction transaction : owner.getTransactions()) {
					Item item = transaction.getItem();
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
			}
		}
		//Add StockpileItems to uniqueIds
		for (Stockpile stockpile : Settings.get().getStockpiles()) {
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
		List<String> ownersJournal = new ArrayList<String>();
		List<String> ownersTransactions = new ArrayList<String>();
		List<String> ownersJobs = new ArrayList<String>();
		List<String> ownersAssets = new ArrayList<String>();
		List<String> ownersAccountBalance = new ArrayList<String>();
		List<Long> contractIDs = new ArrayList<Long>();
		//Temp
		List<Asset> assets = new ArrayList<Asset>();
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		List<Journal> journal = new ArrayList<Journal>();
		List<Transaction> transactions = new ArrayList<Transaction>();
		List<IndustryJob> industryJobs = new ArrayList<IndustryJob>();
		List<ContractItem> contractItems = new ArrayList<ContractItem>();
		List<AccountBalance> accountBalance = new ArrayList<AccountBalance>();
		maximumPurchaseAge();
		//Add assets
		for (Account account : profileManager.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				if (owner.isShowOwner()) {
					uniqueOwners.add(owner.getName());
				} else {
					continue;
				}
				//Market Orders
				if (!owner.getMarketOrders().isEmpty() && !ownersOrders.contains(owner.getName())) {
					//Market Orders
					marketOrders.addAll(owner.getMarketOrders());
					//Assets
					addAssets(ApiConverter.assetMarketOrder(owner.getMarketOrders(), owner), assets);
					ownersOrders.add(owner.getName());
				}
				//Journal
				if (!owner.getJournal().isEmpty() && !ownersJournal.contains(owner.getName())) {
					//Journal
					journal.addAll(owner.getJournal());
					ownersJournal.add(owner.getName());
				}
				//Transactions
				if (!owner.getTransactions().isEmpty() && !ownersTransactions.contains(owner.getName())) {
					//Transactions
					for (Transaction transaction : owner.getTransactions()) {
						int index = transactions.indexOf(transaction);
						if (index >= 0) { //Dublicate
							if (owner.isCorporation()) {
								Transaction remove = transactions.remove(index);
								transaction.setOwnerCharacter(remove.getOwnerName());
								transactions.add(transaction);
							} else {
								transactions.get(index).setOwnerCharacter(transaction.getOwnerName());
							}
						} else { //New
							transactions.add(transaction);
						}
					}
					//Assets
					//FIXME - - > Transactions Assets:
					//Add items added after last asset update
					//Remove item sold after last asset update
					//addAssets(ApiConverter.assetMarketOrder(owner.getMarketOrders(), owner, settings), assets);
					ownersTransactions.add(owner.getName());
				}
				//Industry Jobs
				if (!owner.getIndustryJobs().isEmpty() && !ownersJobs.contains(owner.getName())) {
					//Industry Jobs
					industryJobs.addAll(owner.getIndustryJobs());
					//Assets
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
					List<Asset> contractAssets = ApiConverter.assetContracts(entry.getValue(), owner);
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
					double price = ApiIdConverter.getPrice(item.getTypeID(), false);
					order.setDynamicPrice(price);
				}
				//Update IndustryJobs dynamic values
				for (IndustryJob job : owner.getIndustryJobs()) {
					Item itemType = job.getItem();
					//Price
					double price = ApiIdConverter.getPrice(itemType.getTypeID(), job.isBPC());
					job.setDynamicPrice(price);
				}
				//Update Contracts dynamic values
				for (Map.Entry<Contract, List<ContractItem>> entry : owner.getContracts().entrySet()) {
					for (ContractItem contractItem : entry.getValue()) {
						Item item = contractItem.getItem();
						//Price
						double price = ApiIdConverter.getPrice(item.getTypeID(), contractItem.isBPC());
						contractItem.setDynamicPrice(price);
					}
				}
			}
		}
		//Update Items dynamic values
		for (Item item : StaticData.get().getItems().values()) {
			item.setPriceReprocessed(ApiIdConverter.getPriceReprocessed(item));
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
			journalEventList.getReadWriteLock().writeLock().lock();
			journalEventList.clear();
			journalEventList.addAll(journal);
		} finally {
			journalEventList.getReadWriteLock().writeLock().unlock();
		}
		try {
			transactionsEventList.getReadWriteLock().writeLock().lock();
			transactionsEventList.clear();
			transactionsEventList.addAll(transactions);
		} finally {
			transactionsEventList.getReadWriteLock().writeLock().unlock();
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
		Date maxAge = new Date(System.currentTimeMillis() - (Settings.get().getMaximumPurchaseAge() * 24 * 60 * 60 * 1000L));
		for (Account account : profileManager.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				for (MarketOrder marketOrder : owner.getMarketOrders()) {
					if (marketOrder.getBid() > 0 //Buy orders only
							//at least one bought
							&& marketOrder.getVolRemaining() != marketOrder.getVolEntered()
							//Date in range or unlimited
							&& (marketOrder.getIssued().after(maxAge) || Settings.get().getMaximumPurchaseAge() == 0)
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
			if (Settings.get().getAssetAdded().containsKey(asset.getItemID())) {
				asset.setAdded(Settings.get().getAssetAdded().get(asset.getItemID()));
			} else {
				Date date = new Date();
				Settings.get().getAssetAdded().put(asset.getItemID(), date);
				asset.setAdded(date);
			}
			//User price
			if (asset.getItem().isBlueprint() && !asset.isBPO()) { //Blueprint Copy
				asset.setUserPrice(Settings.get().getUserPrices().get(-asset.getItem().getTypeID()));
			} else { //All other
				asset.setUserPrice(Settings.get().getUserPrices().get(asset.getItem().getTypeID()));
			}
			//Dynamic Price
			double dynamicPrice = ApiIdConverter.getPrice(asset.getItem().getTypeID(), asset.isBPC());
			asset.setDynamicPrice(dynamicPrice);
			//Market price
			asset.setMarketPriceData(marketPriceData.get(asset.getItem().getTypeID()));
			//User Item Names
			if (Settings.get().getUserItemNames().containsKey(asset.getItemID())) {
				asset.setName(Settings.get().getUserItemNames().get(asset.getItemID()).getValue());
			} else {
				asset.setName(asset.getTypeName());
			}
			//Contaioner
			String sContainer = "";
			for (Asset parentAsset : asset.getParents()) {
				if (!sContainer.isEmpty()) {
					sContainer = sContainer + " > ";
				}
				if (!parentAsset.isUserName()) {
					sContainer = sContainer + parentAsset.getName() + " #" + parentAsset.getItemID();
				} else {
					sContainer = sContainer + parentAsset.getName();
				}
			}
			if (sContainer.isEmpty()) {
				sContainer = General.get().none();
			}
			asset.setContainer(sContainer);

			//Price data
			if (asset.getItem().isMarketGroup() && Settings.get().getPriceData().containsKey(asset.getItem().getTypeID()) && !Settings.get().getPriceData().get(asset.getItem().getTypeID()).isEmpty()) { //Market Price
				asset.setPriceData(Settings.get().getPriceData().get(asset.getItem().getTypeID()));
			} else { //No Price :(
				asset.setPriceData(null);
			}

			//Reprocessed price
			asset.setPriceReprocessed(ApiIdConverter.getPriceReprocessed(asset.getItem()));

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
