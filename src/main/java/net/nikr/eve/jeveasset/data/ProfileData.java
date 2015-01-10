/*
 * Copyright 2009-2015 Contributors (see credits.txt)
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
import com.beimin.eveapi.model.shared.Blueprint;
import com.beimin.eveapi.model.shared.ContractType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.journal.MyJournal;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.transaction.MyTransaction;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class ProfileData {
	private final ProfileManager profileManager;
	
	private final EventList<MyContractItem> contractItemEventList = new BasicEventList<MyContractItem>();
	private final EventList<MyIndustryJob> industryJobsEventList = new BasicEventList<MyIndustryJob>();
	private final EventList<MyMarketOrder> marketOrdersEventList = new BasicEventList<MyMarketOrder>();
	private final EventList<MyJournal> journalEventList = new BasicEventList<MyJournal>();
	private final EventList<MyTransaction> transactionsEventList = new BasicEventList<MyTransaction>();
	private final EventList<MyAsset> assetsEventList = new BasicEventList<MyAsset>();
	private final EventList<MyAccountBalance> accountBalanceEventList = new BasicEventList<MyAccountBalance>();
	private Map<Integer, List<MyAsset>> uniqueAssetsDuplicates = null; //TypeID : int
	private Map<Integer, MarketPriceData> marketPriceData; //TypeID : int
	private final List<String> owners = new ArrayList<String>();
	private boolean saveSettings = false;

	public ProfileData(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public Set<Integer> getPriceTypeIDs() {
		return createPriceTypeIDs(); //always needs to be fresh :)
	}

	public EventList<MyAccountBalance> getAccountBalanceEventList() {
		return accountBalanceEventList;
	}

	public EventList<MyAsset> getAssetsEventList() {
		return assetsEventList;
	}

	public EventList<MyIndustryJob> getIndustryJobsEventList() {
		return industryJobsEventList;
	}

	public EventList<MyMarketOrder> getMarketOrdersEventList() {
		return marketOrdersEventList;
	}

	public EventList<MyJournal> getJournalEventList() {
		return journalEventList;
	}

	public EventList<MyTransaction> getTransactionsEventList() {
		return transactionsEventList;
	}

	public EventList<MyContractItem> getContractItemEventList() {
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
		for (MyAccount account : profileManager.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				//Add Assets to uniqueIds
				deepAssets(owner.getAssets(), priceTypeIDs);
				//Add Market Orders to uniqueIds
				for (MyMarketOrder marketOrder : owner.getMarketOrders()) {
					Item item = marketOrder.getItem();
					if (item.isMarketGroup()) {
						priceTypeIDs.add(item.getTypeID());
					}
				}
				//Add Transaction to uniqueIds
				for (MyTransaction transaction : owner.getTransactions().values()) {
					Item item = transaction.getItem();
					if (item.isMarketGroup()) {
						priceTypeIDs.add(item.getTypeID());
					}
				}
				//Add Industry Job to uniqueIds
				for (MyIndustryJob industryJob : owner.getIndustryJobs()) {
					//Blueprint
					Item blueprint = industryJob.getItem();
					if (blueprint.isMarketGroup()) {
						priceTypeIDs.add(blueprint.getTypeID());
					}
					//Manufacturing Output
					if (industryJob.isManufacturing() && !industryJob.isDelivered()) {
						//Output
						Item output = ApiIdConverter.getItem(industryJob.getProductTypeID());
						if (output.isMarketGroup()) {
							priceTypeIDs.add(output.getTypeID());
						}
					}
				}
				//Add Contract to uniqueIds
				for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
					for (MyContractItem contractItem : entry.getValue()) {
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

	private void deepAssets(List<MyAsset> assets, Set<Integer> priceTypeIDs) {
		for (MyAsset asset : assets) {
			//Unique Ids
			if (asset.getItem().isMarketGroup()) {
				priceTypeIDs.add(asset.getItem().getTypeID());
			}
			deepAssets(asset.getAssets(), priceTypeIDs);
		}
	}

	public boolean updateEventLists() {
		saveSettings = false;
		uniqueAssetsDuplicates = new HashMap<Integer, List<MyAsset>>();
		Set<String> uniqueOwners = new HashSet<String>();
		List<String> ownersOrders = new ArrayList<String>();
		List<String> ownersJournal = new ArrayList<String>();
		List<String> ownersTransactions = new ArrayList<String>();
		List<String> ownersJobs = new ArrayList<String>();
		List<String> ownersAssets = new ArrayList<String>();
		List<String> ownersAccountBalance = new ArrayList<String>();
		List<Long> contractIDs = new ArrayList<Long>();
		//Temp
		List<MyAsset> assets = new ArrayList<MyAsset>();
		List<MyMarketOrder> marketOrders = new ArrayList<MyMarketOrder>();
		List<MyJournal> journal = new ArrayList<MyJournal>();
		List<MyTransaction> transactions = new ArrayList<MyTransaction>();
		List<MyIndustryJob> industryJobs = new ArrayList<MyIndustryJob>();
		List<MyContractItem> contractItems = new ArrayList<MyContractItem>();
		List<MyAccountBalance> accountBalance = new ArrayList<MyAccountBalance>();
		maximumPurchaseAge();
		//Add assets
		for (MyAccount account : profileManager.getAccounts()) {
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
					addAssets(ApiConverter.assetMarketOrder(owner.getMarketOrders(), owner), assets, owner.getBlueprints());
					ownersOrders.add(owner.getName());
				}
				//Journal
				if (!owner.getJournal().isEmpty() && !ownersJournal.contains(owner.getName())) {
					//Journal
					journal.addAll(owner.getJournal().values());
					ownersJournal.add(owner.getName());
				}
				//Transactions
				if (!owner.getTransactions().isEmpty() && !ownersTransactions.contains(owner.getName())) {
					//Transactions
					for (MyTransaction transaction : owner.getTransactions().values()) {
						int index = transactions.indexOf(transaction);
						if (index >= 0) { //Dublicate
							if (owner.isCorporation()) {
								MyTransaction remove = transactions.remove(index);
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
					for (MyIndustryJob industryJob : owner.getIndustryJobs()) {
						//Update Owners
						industryJob.setInstaller(ApiIdConverter.getOwnerName(industryJob.getInstallerID()));
						//Update BPO/BPC status
						Blueprint blueprint = owner.getBlueprints().get(industryJob.getBlueprintID());
						industryJob.setBlueprint(blueprint);
					}
					//Assets
					addAssets(ApiConverter.assetIndustryJob(owner.getIndustryJobs(), owner), assets, owner.getBlueprints());
					ownersJobs.add(owner.getName());
				}
				//Contracts
				for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
					//Contracts
					MyContract contract = entry.getKey();
					//Update Owners
					contract.setAcceptor(ApiIdConverter.getOwnerName(contract.getAcceptorID()));
					contract.setAssignee(ApiIdConverter.getOwnerName(contract.getAssigneeID()));
					contract.setIssuerCorp(ApiIdConverter.getOwnerName(contract.getIssuerCorpID()));
					contract.setIssuer(ApiIdConverter.getOwnerName(contract.getIssuerID()));
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
						contractItems.add(new MyContractItem(contract));
					} else if (!entry.getValue().isEmpty()) {
						contractIDs.add(contract.getContractID());
						contractItems.addAll(entry.getValue());
					}
					//Assets
					List<MyAsset> contractAssets = ApiConverter.assetContracts(entry.getValue(), owner);
					addAssets(contractAssets, assets, owner.getBlueprints());
				}
				//Assets
				if (!owner.getAssets().isEmpty() && !ownersAssets.contains(owner.getName())) {
					addAssets(owner.getAssets(), assets, owner.getBlueprints());
					ownersAssets.add(owner.getName());
				}
				//Account Balance
				if (!owner.getAccountBalances().isEmpty() && !ownersAccountBalance.contains(owner.getName())) {
					accountBalance.addAll(owner.getAccountBalances());
					ownersAccountBalance.add(owner.getName());
				}
				//Update MarketOrders dynamic values
				for (MyMarketOrder order : owner.getMarketOrders()) {
					Item item = order.getItem();
					//Price
					double price = ApiIdConverter.getPrice(item.getTypeID(), false);
					order.setDynamicPrice(price);
				}
				//Update IndustryJobs dynamic values
				for (MyIndustryJob job : owner.getIndustryJobs()) {
					Item itemType = job.getItem();
					//Price
					double price = ApiIdConverter.getPrice(itemType.getTypeID(), true);
					job.setDynamicPrice(price);
					double outputPrice = ApiIdConverter.getPrice(job.getProductTypeID(), false);
					job.setOutputPrice(outputPrice);
				}
				//Update Contracts dynamic values
				for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
					for (MyContractItem contractItem : entry.getValue()) {
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
		return saveSettings;
	}

	private void maximumPurchaseAge() {
		//Create Market Price Data
		marketPriceData = new HashMap<Integer, MarketPriceData>();
		//Date - maximumPurchaseAge in days
		Date maxAge = new Date(System.currentTimeMillis() - (Settings.get().getMaximumPurchaseAge() * 24 * 60 * 60 * 1000L));
		for (MyAccount account : profileManager.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				for (MyMarketOrder marketOrder : owner.getMarketOrders()) {
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

	private void addAssets(final List<MyAsset> assets, List<MyAsset> addTo, Map<Long, Blueprint> blueprints) {
		for (MyAsset asset : assets) {
			//Blueprint
			Blueprint blueprint = blueprints.get(asset.getItemID());
			asset.setBlueprint(blueprint);
			//Tags
			Tags tags = Settings.get().getTags(asset.getTagID());
			asset.setTags(tags);
			//Date added
			if (Settings.get().getAssetAdded().containsKey(asset.getItemID())) {
				asset.setAdded(Settings.get().getAssetAdded().get(asset.getItemID()));
			} else {
				Date date = new Date();
				Settings.lock(); //Lock for Asset Added
				Settings.get().getAssetAdded().put(asset.getItemID(), date);
				Settings.unlock(); //Unlock for Asset Added
				saveSettings = true;
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
				asset.setName(Settings.get().getUserItemNames().get(asset.getItemID()).getValue(), true, false);
			} else if (Settings.get().getEveNames().containsKey(asset.getItemID())){
				String eveName = Settings.get().getEveNames().get(asset.getItemID());
				asset.setName(eveName + " (" + asset.getTypeName() + ")", false, true);
			} else {
				asset.setName(asset.getTypeName(), false, false);
			}
			//Contaioner
			String sContainer = "";
			for (MyAsset parentAsset : asset.getParents()) {
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
			PriceData priceData = Settings.get().getPriceData().get(asset.getItem().getTypeID());
			if (asset.getItem().isMarketGroup() && priceData != null && !priceData.isEmpty()) { //Market Price
				asset.setPriceData(priceData);
			} else { //No Price :(
				asset.setPriceData(null);
			}

			//Reprocessed price
			asset.setPriceReprocessed(ApiIdConverter.getPriceReprocessed(asset.getItem()));

			//Type Count
			if (!uniqueAssetsDuplicates.containsKey(asset.getItem().getTypeID())) {
				uniqueAssetsDuplicates.put(asset.getItem().getTypeID(), new ArrayList<MyAsset>());
			}
			List<MyAsset> dup = uniqueAssetsDuplicates.get(asset.getItem().getTypeID());
			long newCount = asset.getCount();
			if (!dup.isEmpty()) {
				newCount = newCount + dup.get(0).getTypeCount();
			}
			dup.add(asset);
			for (MyAsset assetLoop : dup) {
				assetLoop.setTypeCount(newCount);
			}
			//Packaged Volume
			float volume = ApiIdConverter.getVolume(asset.getItem().getTypeID(), !asset.isSingleton());
			asset.setVolume(volume);

			//Add asset
			addTo.add(asset);
			//Add sub-assets
			addAssets(asset.getAssets(), addTo, blueprints);
		}
	}
}
