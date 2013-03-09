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
	private final EventList<Asset> assetsEventList = new BasicEventList<Asset>();
	private final EventList<AccountBalance> accountBalanceEventList = new BasicEventList<AccountBalance>();
	private Set<Integer> priceTypeIDs = null; //TypeID : int
	private Map<Integer, List<Asset>> uniqueAssetsDuplicates = null; //TypeID : int
	private Map<Integer, MarketPriceData> marketPriceData; //TypeID : int
	private List<String> owners = new ArrayList<String>();

	public ProfileData(Settings settings, ProfileManager profileManager) {
		this.settings = settings;
		this.profileManager = profileManager;
	}

	public Set<Integer> getPriceTypeIDs() {
		return priceTypeIDs;
	}

	public boolean hasAssets() {
		return !priceTypeIDs.isEmpty();
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

	public void updateEventLists() {
		priceTypeIDs = new HashSet<Integer>();
		uniqueAssetsDuplicates = new HashMap<Integer, List<Asset>>();
		owners = new ArrayList<String>();
		List<String> ownersOrders = new ArrayList<String>();
		List<String> ownersJobs = new ArrayList<String>();
		List<String> ownersContracts = new ArrayList<String>();
		List<String> ownersAssets = new ArrayList<String>();
		List<String> ownersAccountBalance = new ArrayList<String>();
		List<Long> contractIDs = new ArrayList<Long>();
		//Temp
		List<Asset> assets = new ArrayList<Asset>();
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		List<IndustryJob> industryJobs = new ArrayList<IndustryJob>();
		List<ContractItem> contractItems = new ArrayList<ContractItem>();
		List<AccountBalance> accountBalance = new ArrayList<AccountBalance>();
		maximumPurchaseAge();
		//Add assets
		for (Account account : profileManager.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				if (owner.isShowAssets()) {
					owners.add(owner.getName());
				}
				//Market Orders
				if (!owner.getMarketOrders().isEmpty() && !ownersOrders.contains(owner.getName())) {
					//Market Orders
					if (owner.isShowAssets()) {
						marketOrders.addAll(owner.getMarketOrders());
					}
					//Assets
					List<Asset> marketOrdersAssets = ApiConverter.assetMarketOrder(owner.getMarketOrders(), owner, settings);
					addAssets(marketOrdersAssets, assets, owner.isShowAssets());
					if (owner.isShowAssets()) {
						ownersOrders.add(owner.getName());
					}
				}
				//Industry Jobs
				if (!owner.getIndustryJobs().isEmpty() && !ownersJobs.contains(owner.getName())) {
					//Industry Jobs
					if (owner.isShowAssets()) {
						industryJobs.addAll(owner.getIndustryJobs());
					}
					//Assets
					List<Asset> industryJobAssets = ApiConverter.assetIndustryJob(owner.getIndustryJobs(), owner, settings);
					addAssets(industryJobAssets, assets, owner.isShowAssets());
					if (owner.isShowAssets()) {
						ownersJobs.add(owner.getName());
					}
				}
				//Contracts
				if (!owner.getContracts().isEmpty() && !ownersContracts.contains(owner.getName()) && settings.isIncludeContracts()) {
					//Contracts
					for (List<ContractItem> items : owner.getContracts().values()) {
						contractItems.addAll(items);
					}
					//Assets
					List<Asset> contractAssets = ApiConverter.assetContracts(owner.getContracts(), contractIDs, owner, settings);
					addAssets(contractAssets, assets, owner.isShowAssets());
					if (owner.isShowAssets()) {
						ownersContracts.add(owner.getName());
					}
				}
				//Assets (Must be after Industry Jobs, for bpos to be marked)
				if (!owner.getAssets().isEmpty() && !ownersAssets.contains(owner.getName())) {
					addAssets(owner.getAssets(), assets, owner.isShowAssets());
					if (owner.isShowAssets()) {
						ownersAssets.add(owner.getName());
					}
				}
				//Account Balance
				if (!owner.getAccountBalances().isEmpty() && !ownersAccountBalance.contains(owner.getName())) {
					if (owner.isShowAssets()) {
						accountBalance.addAll(owner.getAccountBalances());
						ownersAccountBalance.add(owner.getName());
					}
				}
				//Add StockpileItems to uniqueIds
				for (Stockpile stockpile : settings.getStockpiles()) {
					for (StockpileItem item : stockpile.getItems()) {
						int typeID = item.getTypeID();
						boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
						if (marketGroup) {
							priceTypeIDs.add(typeID);
						}
					}
				}
				//Add MarketOrders to uniqueIds
				for (MarketOrder order : owner.getMarketOrders()) {
					int typeID = order.getTypeID();
					boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
					if (marketGroup) {
						priceTypeIDs.add(typeID);
					}
				}
				//Add IndustryJobs to uniqueIds
				for (IndustryJob job : owner.getIndustryJobs()) {
					int typeID = job.getInstalledItemTypeID();
					boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
					if (marketGroup) {
						priceTypeIDs.add(typeID);
					}
				}
				//Add Contracts to uniqueIds
				for (Map.Entry<Contract, List<ContractItem>> entry : owner.getContracts().entrySet()) {
					for (ContractItem contractItem : entry.getValue()) {
						int typeID = contractItem.getTypeID();
						boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
						if (marketGroup) {
							priceTypeIDs.add(typeID);
						}
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

	private void addAssets(final List<Asset> assets, List<Asset> addTo, final boolean shouldShow) {
		for (Asset asset : assets) {
			if (shouldShow) {
				//Date added
				if (settings.getAssetAdded().containsKey(asset.getItemID())) {
					asset.setAdded(settings.getAssetAdded().get(asset.getItemID()));
				} else {
					Date date = new Date();
					settings.getAssetAdded().put(asset.getItemID(), date);
					asset.setAdded(date);
				}
				//User price
				if (asset.isBlueprint() && !asset.isBpo()) { //Blueprint Copy
					asset.setUserPrice(settings.getUserPrices().get(-asset.getTypeID()));
				} else { //All other
					asset.setUserPrice(settings.getUserPrices().get(asset.getTypeID()));
				}
				//Market price
				asset.setMarketPriceData(marketPriceData.get(asset.getTypeID()));
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
				if (asset.isMarketGroup() && settings.getPriceData().containsKey(asset.getTypeID()) && !settings.getPriceData().get(asset.getTypeID()).isEmpty()) { //Market Price
					asset.setPriceData(settings.getPriceData().get(asset.getTypeID()));
				} else { //No Price :(
					asset.setPriceData(null);
				}

				//Reprocessed price
				asset.setPriceReprocessed(0);
				if (settings.getItems().containsKey(asset.getTypeID())) {
					List<ReprocessedMaterial> reprocessedMaterials = settings.getItems().get(asset.getTypeID()).getReprocessedMaterial();
					double priceReprocessed = 0;
					int portionSize = 0;
					for (ReprocessedMaterial material : reprocessedMaterials) {
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
						//Unique Ids
						boolean marketGroup = ApiIdConverter.marketGroup(material.getTypeID(), settings.getItems());
						if (marketGroup && !priceTypeIDs.contains(material.getTypeID())) {
							priceTypeIDs.add(material.getTypeID());
						}
					}
					if (priceReprocessed > 0 && portionSize > 0) {
						priceReprocessed = priceReprocessed / portionSize;
					}
					asset.setPriceReprocessed(priceReprocessed);
				}

				//Type Count
				if (!uniqueAssetsDuplicates.containsKey(asset.getTypeID())) {
					uniqueAssetsDuplicates.put(asset.getTypeID(), new ArrayList<Asset>());
				}
				if (shouldShow) {
					List<Asset> dup = uniqueAssetsDuplicates.get(asset.getTypeID());
					long newCount = asset.getCount();
					if (!dup.isEmpty()) {
						newCount = newCount + dup.get(0).getTypeCount();
					}
					dup.add(asset);
					for (Asset assetLoop : dup) {
						assetLoop.setTypeCount(newCount);
					}
				}
				//Packaged Volume
				if (!asset.isSingleton() && settings.getPackagedVolume().containsKey(asset.getGroup())) {
					asset.setVolume(settings.getPackagedVolume().get(asset.getGroup()));
				}

				//Add asset
				addTo.add(asset);
			}
			//Unique Ids
			if (asset.isMarketGroup()) {
				priceTypeIDs.add(asset.getTypeID());
			}
			//Add sub-assets
			addAssets(asset.getAssets(), addTo, shouldShow);
		}
	}
}
