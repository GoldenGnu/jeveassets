/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileData extends TableData {

	private Map<Long, String> ownersName;
	private final Map<Integer, Set<MyContractItem>> contractItems = new HashMap<>();
	private final Map<Integer, Set<MyAsset>> assets = new HashMap<>();
	private final Map<Integer, Set<MyMarketOrder>> marketOrders = new HashMap<>();
	private final Map<Integer, Set<MyIndustryJob>> industryJobs = new HashMap<>();
	private final Map<Integer, Set<MyTransaction>> transactions = new HashMap<>();

	public StockpileData(Program program) {
		super(program);
	}

	public StockpileData(ProfileManager profileManager, ProfileData profileData) {
		super(profileManager, profileData);
	}

	public EventList<StockpileItem> getData() {
		EventList<StockpileItem> eventList = EventListManager.create();
		updateData(eventList);
		return eventList;
	}

	public void updateData(EventList<StockpileItem> eventList) {
		//Items
		List<StockpileItem> stockpileItems = new ArrayList<>();

		updateOwners();

		contractItems.clear();
		assets.clear();
		marketOrders.clear();
		industryJobs.clear();
		transactions.clear();

		for (Stockpile stockpile : StockpileTab.getShownStockpiles(profileManager)) {
			stockpile.updateDynamicValues();
			stockpileItems.addAll(stockpile.getItems());
			updateStockpile(stockpile);
		}
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(stockpileItems);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}

		for (Stockpile stockpile : Settings.get().getStockpiles()) {
			updateSubpile(eventList, stockpile);
		}
	}

	public void updateOwners() {
		//Owners Look-Up
		ownersName = new HashMap<>();
		for (OwnerType owner : profileManager.getOwnerTypes()) {
			ownersName.put(owner.getOwnerID(), owner.getOwnerName());
		}
	}

	public void updateStockpile(Stockpile stockpile) {
		//Update owner name
		Set<String> owners = new HashSet<>();
		for (Stockpile.StockpileFilter filter : stockpile.getFilters()) {
			for (Long ownerID : filter.getOwnerIDs()) {
				String owner = ownersName.get(ownerID);
				if (owner != null) {
					owners.add(owner);
				}
			}
		}
		stockpile.setOwnerName(new ArrayList<>(owners));
		//Update Item flag name
		Set<ItemFlag> flags = new HashSet<>();
		for (Stockpile.StockpileFilter filter : stockpile.getFilters()) {
			for (Integer flagID : filter.getFlagIDs()) {
				ItemFlag flag = StaticData.get().getItemFlags().get(flagID);
				if (flag != null) {
					flags.add(flag);
				}
			}
		}
	//Create lookup set of TypeIDs
		Set<Integer> typeIDs = new HashSet<>();
		for (Stockpile.StockpileItem item : stockpile.getItems()) {
			typeIDs.add(item.getItemTypeID());
		}
		addTypeIDs(typeIDs, stockpile);
	//Create lookup maps of Items
		//ContractItems
		if (stockpile.isContracts()) {
			for (MyContractItem contractItem : profileData.getContractItemList()) {
				if (contractItem.getContract().isIgnoreContract()) {
					continue;
				}
				int typeID = contractItem.isBPC() ? -contractItem.getTypeID() : contractItem.getTypeID(); //BPC has negative value
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				Set<MyContractItem> items = contractItems.get(typeID);
				if (items == null) {
					items = new HashSet<>();
					contractItems.put(typeID, items);
				}
				items.add(contractItem);
			}
		}
		//Inventory AKA Assets
		if (stockpile.isAssets()) {
			for (MyAsset asset : profileData.getAssetsList()) {
				if (asset.isGenerated()) { //Skip generated assets
					continue;
				}
				int typeID = asset.isBPC() ? -asset.getTypeID() : asset.getTypeID(); //BPC has negative value
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				Set<MyAsset> items = assets.get(typeID);
				if (items == null) {
					items = new HashSet<>();
					assets.put(typeID, items);
				}
				items.add(asset);
			}
		}
		//Market Orders
		if (stockpile.isBuyOrders() || stockpile.isSellOrders()) {
			for (MyMarketOrder marketOrder : profileData.getMarketOrdersList()) {
				int typeID = marketOrder.getItem().getTypeID();
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				Set<MyMarketOrder> items = marketOrders.get(typeID);
				if (items == null) {
					items = new HashSet<>();
					marketOrders.put(typeID, items);
				}
				items.add(marketOrder);
			}
		}
		//Industry Job
		if (stockpile.isJobs()) {
			for (MyIndustryJob industryJob : profileData.getIndustryJobsList()) {
				Integer productTypeID = industryJob.getProductTypeID();
				if (productTypeID != null && typeIDs.contains(productTypeID)) {
					Set<MyIndustryJob> items = industryJobs.get(productTypeID);
					if (items == null) {
						items = new HashSet<>();
						industryJobs.put(productTypeID, items);
					}
					items.add(industryJob);
				}
				int blueprintTypeID = -industryJob.getBlueprintTypeID(); //Negative - match blueprints copies
				if (typeIDs.contains(blueprintTypeID)) {
					Set<MyIndustryJob> items = industryJobs.get(blueprintTypeID);
					if (items == null) {
						items = new HashSet<>();
						industryJobs.put(blueprintTypeID, items);
					}
					items.add(industryJob);
				}
			}
		}
		//Transactions
		if (stockpile.isTransactions()) {
			for (MyTransaction transaction : profileData.getTransactionsList()) {
				int typeID = transaction.getItem().getTypeID();
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				Set<MyTransaction> items = transactions.get(typeID);
				if (items == null) {
					items = new HashSet<>();
					transactions.put(typeID, items);
				}
				items.add(transaction);
			}
		}
		stockpile.setFlagName(flags);
		stockpile.reset();
		if (!stockpile.isEmpty()) {
			for (Stockpile.StockpileItem item : stockpile.getItems()) {
				if (item instanceof Stockpile.StockpileTotal) {
					continue;
				}
				updateItem(item, stockpile);
			}
		}
		stockpile.updateTotal();
		stockpile.updateTags();
	}

	private void addTypeIDs(Set<Integer> typeIDs, Stockpile stockpile) {
		for (Stockpile.StockpileItem item : stockpile.getItems()) {
			typeIDs.add(item.getItemTypeID());
		}
		for (Stockpile subpile : stockpile.getSubpiles().keySet()) {
			addTypeIDs(typeIDs, subpile);
		}
	}

	private void updateItem(Stockpile.StockpileItem item, Stockpile stockpile) {
		final int TYPE_ID = item.getItemTypeID();
		double price = ApiIdConverter.getPrice(TYPE_ID, item.isBPC(), item);
		float volume = ApiIdConverter.getVolume(item.getItem(), true);
		Double transactionAveragePrice = profileData.getTransactionAveragePrice(TYPE_ID);
		item.updateValues(price, volume, transactionAveragePrice);
		//ContractItems
		if (stockpile.isContracts()) {
			Set<MyContractItem> items = contractItems.get(TYPE_ID);
			if (items != null) {
				for (MyContractItem contractItem : items) {
					item.updateContract(contractItem);
				}
			}
		}
		//Inventory AKA Assets
		if (stockpile.isAssets()) {
			Set<MyAsset> items = assets.get(TYPE_ID);
			if (items != null) {
				for (MyAsset asset : items) {
					item.updateAsset(asset);
				}
			}
		}
		//Market Orders
		if (stockpile.isBuyOrders() || stockpile.isSellOrders()) {
			Set<MyMarketOrder> items = marketOrders.get(TYPE_ID);
			if (items != null) {
				for (MyMarketOrder marketOrder : items) {
					item.updateMarketOrder(marketOrder);
				}
			}
		}
		//Industry Job
		if (stockpile.isJobs()) {
			Set<MyIndustryJob> items = industryJobs.get(TYPE_ID);
			if (items != null) {
				for (MyIndustryJob industryJob : items) {
					item.updateIndustryJob(industryJob);
				}
			}
		}
		//Transactions
		if (stockpile.isTransactions()) {
			Set<MyTransaction> items = transactions.get(TYPE_ID);
			if (items != null) {
				for (MyTransaction transaction : items) {
					item.updateTransaction(transaction);
				}
			}
		}
	}

	public void updateSubpile(EventList<StockpileItem> eventList, Stockpile parent) {
		Map<Integer, Stockpile.StockpileItem> parentItems = new HashMap<>();
		for (Stockpile.StockpileItem item : parent.getItems()) {
			parentItems.put(item.getItemTypeID(), item);
		}
		//Save old items (for them to be removed)
		List<Stockpile.SubpileItem> subpileItems = new ArrayList<>(parent.getSubpileItems());
		//Clear old items
		parent.getSubpileItems().clear();
		for (Stockpile.SubpileItem subpileItem : subpileItems) {
			subpileItem.clearItemLinks();
		}
		//Update subs
		for (Stockpile stockpile : parent.getSubpileLinks()) {
			updateSubpile(eventList, stockpile);
		}
		//Add new items
		updateSubpile(parent, parent, parentItems, null, 0, "");
		//Update items
		for (Stockpile.SubpileItem subpileItem : parent.getSubpileItems()) {
			updateItem(subpileItem, subpileItem.getStockpile());
		}
		parent.updateTotal();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.removeAll(subpileItems);
			if (profileManager.getStockpileIDs().isShown(parent.getId())) {
				eventList.addAll(parent.getSubpileItems());
			}
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	private void updateSubpile(Stockpile topStockpile, Stockpile parentStockpile, Map<Integer, Stockpile.StockpileItem> topItems, Stockpile.SubpileStock parentStock, int parentLevel, String parentPath) {
		for (Map.Entry<Stockpile, Double> entry : parentStockpile.getSubpiles().entrySet()) {
			//For each subpile (stockpile)
			Stockpile currentStockpile = entry.getKey();
			Double value = entry.getValue();
			String path = parentPath + currentStockpile.getName() + "\r\n";
			int level = parentLevel + 1;
			Stockpile.SubpileStock subpileStock = new Stockpile.SubpileStock(topStockpile, currentStockpile, parentStockpile, parentStock, value, parentLevel, path);
			topStockpile.getSubpileItems().add(subpileStock);
			for (Stockpile.StockpileItem stockpileItem : currentStockpile.getItems()) {
				//For each StockpileItem
				if (stockpileItem.getTypeID() != 0) {
					Stockpile.StockpileItem parentItem = topItems.get(stockpileItem.getItemTypeID());
					Stockpile.SubpileItem subpileItem = new Stockpile.SubpileItem(topStockpile, stockpileItem, subpileStock, parentLevel, path);
					int linkIndex = topStockpile.getSubpileItems().indexOf(subpileItem);
					if (parentItem != null) { //Add link (Advanced: Item + Link)
						subpileItem.addItemLink(parentItem, null); //Add link
					}
					if (linkIndex >= 0) { //Update item (Advanced: Link + Link = MultiLink)
						Stockpile.SubpileItem linkItem = topStockpile.getSubpileItems().get(linkIndex);
						linkItem.addItemLink(stockpileItem, subpileStock);
						if (level >= linkItem.getLevel()) {
							linkItem.setPath(path);
							linkItem.setLevel(level);
						}
					} else { //Add new item (Simple)
						topStockpile.getSubpileItems().add(subpileItem);
					}
				}
			}
			updateSubpile(topStockpile, currentStockpile, topItems, subpileStock, level, path);
		}
	}
}
