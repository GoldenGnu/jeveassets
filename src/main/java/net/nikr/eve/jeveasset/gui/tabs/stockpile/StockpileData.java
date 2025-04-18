/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileFlag;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileStock;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.TypeIdentifier;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileData extends TableData {

	private Map<Long, String> ownersName;
	private final Map<Stockpile, Map<Integer, Set<MyContractItem>>> contractItems = new HashMap<>();
	private final Map<Stockpile, Map<Integer, Set<MyAsset>>> assets = new HashMap<>();
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

		//Update Stockpiles (StockpileItem)
		for (Stockpile stockpile : StockpileTab.getShownStockpiles(profileManager)) {
			stockpile.updateDynamicValues();
			updateStockpile(stockpile);
			stockpileItems.addAll(stockpile.getItems());
		}
		//Update Subpiles (SubpileItem)
		stockpileItems.addAll(getUpdatedSubpiles());
		//Update EventList (GUI)
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			StockpileTab.enableGroupFirstUpdate();
			eventList.addAll(stockpileItems);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
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
		for (StockpileFilter filter : stockpile.getFilters()) {
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
		for (StockpileFilter filter : stockpile.getFilters()) {
			for (StockpileFlag flag : filter.getFlags()) {
				ItemFlag itemFlag = StaticData.get().getItemFlags().get(flag.getFlagID());
				if (itemFlag != null) {
					flags.add(itemFlag);
				}
			}
		}
		stockpile.setFlagName(flags);
		//Update Tags
		stockpile.updateTags();
		//Update Items
		updateStockpileItems(stockpile, true);
	}

	private void updateStockpileItems(Stockpile stockpile, boolean updateClaims) {
		//Create lookup set of TypeIDs
		Set<Integer> typeIDs = new HashSet<>();
		addTypeIDs(typeIDs, stockpile);
		//Create lookup maps of Items
		if (!typeIDs.isEmpty()) {
			//Contract Items
			if (stockpile.isContracts()) {
				get(contractItems, stockpile).clear();
				if (stockpile.isMatchAll()) {
					Map<MyContract, List<MyContractItem>> foundItems = contractsMatchAll(profileData, stockpile, updateClaims);
					//Add
					for (List<MyContractItem> list : foundItems.values()) {
						for (MyContractItem contractItem : list) {
							add(get(contractItems, stockpile), get(contractItem.getTypeID(), contractItem.isBPC()), contractItem);
						}
					}
				} else {
					for (MyContractItem contractItem : profileData.getContractItemList()) {
						if (contractItem.getContract().isIgnoreContract()) {
							continue;
						}
						if (!contractItem.getContract().isOpen() && !contractItem.getContract().isCompletedSuccessful() ) {
							continue; //Only include open and successfully completed  contracts
						}
						Integer typeID = get(contractItem.getTypeID(), contractItem.isBPC());
						//Ignore null and wrong typeID
						if (ignore(typeIDs, typeID)) {
							continue;
						}
						//Add Contract Item
						add(get(contractItems, stockpile), typeID, contractItem);
					}
				}
			}
			//Assets
			if (stockpile.isAssets()) {
				get(assets, stockpile).clear();
				if (stockpile.isMatchAll()) {
					Map<MyAsset, List<MyAsset>> foundItems = assetsMatchAll(profileData, stockpile, updateClaims);
					//Add
					for (List<MyAsset> list : foundItems.values()) {
						for (MyAsset asset : list) {
							add(get(assets, stockpile), get(asset.getTypeID(), asset.isBPC()), asset);
						}
					}
				} else {
					for (MyAsset asset : profileData.getAssetsList()) {
						if (asset.isGenerated()) { //Skip generated assets
							continue;
						}
						Integer typeID = get(asset.getTypeID(), asset.isBPC());
						//Ignore null and wrong typeID
						if (ignore(typeIDs, typeID)) {
							continue;
						}
						//Add Asset
						add(get(assets, stockpile), typeID, asset);
					}
				}
			}
			//Market Orders
			if (stockpile.isBuyOrders() || stockpile.isSellOrders()) {
				for (MyMarketOrder marketOrder : profileData.getMarketOrdersList()) {
					if (!marketOrder.isActive()) {
						continue; //Only include active orders
					}
					Integer typeID = marketOrder.getTypeID();
					//Ignore null and wrong typeID
					if (ignore(typeIDs, typeID)) {
						continue;
					}
					//Add Market Order
					add(marketOrders, typeID, marketOrder);
				}
			}
			//Industry Jobs
			if (stockpile.isJobs()) {
				for (MyIndustryJob industryJob : profileData.getIndustryJobsList()) {
					if (!industryJob.isNotDeliveredToAssets()) {
						continue; //Only include industry jobs not delivered
					}
					//Manufacturing
					Integer productTypeID = industryJob.getProductTypeID();
					if (!ignore(typeIDs, productTypeID)) { //Ignore null and wrong typeID
						add(industryJobs, productTypeID, industryJob);
					}
					//Copying
					Integer blueprintTypeID = get(industryJob.getBlueprintTypeID(), true);  //Negative - match blueprints copies
					if (!ignore(typeIDs, blueprintTypeID)) { //Ignore null and wrong typeID
						add(industryJobs, blueprintTypeID, industryJob);
					}
				}
			}
			//Transactions
			if (stockpile.isTransactions()) {
				for (MyTransaction transaction : profileData.getTransactionsList()) {
					if (!transaction.isAfterAssets()) {
						continue; //Only include transaction made after the last asset update
					}
					Integer typeID = transaction.getTypeID();
					//Ignore null and wrong typeID
					if (typeID == null || !typeIDs.contains(typeID)) {
						continue;
					}
					//Add Transaction
					add(transactions, typeID, transaction);
				}
			}
		}
		stockpile.reset();
		if (!stockpile.isEmpty()) {
			for (StockpileItem item : stockpile.getItems()) {
				if (item.isTotal()) {
					continue; //Ignore Total
				}
				updateItem(item, stockpile);
			}
		}
		stockpile.updateTotal();
	}

	public static Map<MyContract, List<MyContractItem>> contractsMatchAll(ProfileData profileData, Stockpile stockpile, boolean updateClaims) {
		Map<MyContract, Set<TypeIdentifier>> foundIDs =  new HashMap<>();
		Map<MyContract, List<MyContractItem>> foundItems =  new HashMap<>();
		//Init found maps
		for (MyContract contract : profileData.getContractList()) {
			foundIDs.put(contract, new HashSet<>());
			foundItems.put(contract, new ArrayList<>());
		}
		//Update subpile claims
		if (updateClaims && !stockpile.getSubpiles().isEmpty()) {
			updateSubpileClaims(stockpile);
		}
		//StockpileItem map lookup
		Map<TypeIdentifier, StockpileItem> stockpileItems = stockpile.getClaimsMap();
		//Contract Items matching
		for (MyContractItem contractItem : profileData.getContractItemList()) {
			//Validate contract
			if (contractItem.getContract().isIgnoreContract()) {
				continue;
			}
			boolean found = false;
			for (TypeIdentifier type : getTypes(contractItem.getTypeID(), contractItem.isBPC())) {
				//Validate typeID
				if (ignore(stockpileItems.keySet(), type)) {
					continue; //Nothing left to do here
				}
				//Get items
				List<MyContractItem> items = foundItems.get(contractItem.getContract());
				if (items == null) {
					continue; //Happens when one or more typeIDs from the contract isn't in the stockpile
				}
				//Get contract typeIDs
				Set<TypeIdentifier> ids = foundIDs.get(contractItem.getContract());
				if (ids == null) {
					continue; //Should never happen, but, better safe than sorry
				}
				//Get StockpileItem
				StockpileItem stockpileItem = stockpileItems.get(type);
				if (stockpileItem == null) {
					continue; //Should never happen, but, better safe than sorry
				}
				if (stockpileItem.matchesContract(contractItem)) {
					items.add(contractItem);
					ids.add(type);
					break; //Can only match once
				}
			}
			if (!found) {
				foundItems.remove(contractItem.getContract()); //Contract have items not in the stockpile
			}
		}
		//Stockpile Items matching
		for (Map.Entry<MyContract, Set<TypeIdentifier>> entry : foundIDs.entrySet()) {
			//Only compare the size of the sets, as both sets only contains valid and unique ids.
			//Therefore there should be no reason to compare the actualy IDs (which is really really slow)
			if (entry.getValue().size() != stockpileItems.keySet().size()) { //Stockpile have items not in the contract
				foundItems.remove(entry.getKey());
			}
		}
		return foundItems;
	}

	public static Map<MyAsset, List<MyAsset>> assetsMatchAll(ProfileData profileData, Stockpile stockpile, boolean updateClaims) {
		Map<MyAsset, Set<TypeIdentifier>> foundIDs =  new HashMap<>();
		Map<MyAsset, List<MyAsset>> foundItems =  new HashMap<>();
		Map<MyAsset, List<MyAsset>> parents =  new HashMap<>();
		//Init found maps
		for (MyAsset asset : profileData.getAssetsList()) {
			if (asset.getAssets().isEmpty()) {
				continue;
			}
			List<MyAsset> children = new ArrayList<>();
			addAssetChildren(children, asset);
			parents.put(asset, children);
			foundIDs.put(asset, new HashSet<>());
			foundItems.put(asset, new ArrayList<>());
		}
		//Update subpile claims
		if (updateClaims && !stockpile.getSubpiles().isEmpty()) {
			updateSubpileClaims(stockpile);
		}
		//StockpileItem map lookup
		Map<TypeIdentifier, StockpileItem> stockpileItems =  stockpile.getClaimsMap();
		//Contract Items matching
		for (Map.Entry<MyAsset, List<MyAsset>> entry : parents.entrySet()) {
			MyAsset parent = entry.getKey();
			for (MyAsset child : entry.getValue()) {
				//Validate contract
				if (child.isGenerated()) {
					continue;
				}
				boolean found = false;
				for (TypeIdentifier type : getTypes(child.getTypeID(), child.isBPC())) {
					//Validate typeID
					if (ignore(stockpileItems.keySet(), type)) {
						continue; //Nothing left to do here
					}

					//Get items
					List<MyAsset> items = foundItems.get(parent);
					if (items == null) {
						continue; //Happens when one or more typeIDs from the contract isn't in the stockpile
					}
					//Get contract typeIDs
					Set<TypeIdentifier> ids = foundIDs.get(parent);
					if (ids == null) {
						continue; //Should never happen, but, better safe than sorry
					}
					//Get StockpileItem
					StockpileItem stockpileItem = stockpileItems.get(type);
					if (stockpileItem == null) {
						continue; //Should never happen, but, better safe than sorry
					}
					if (stockpileItem.matchesAsset(child)) {
						items.add(child);
						ids.add(type);
						found = true;
						break; //Can only match once
					}
				}
				if (!found) {
					foundItems.remove(parent); //Contract have items not in the stockpile
				}
			}
		}
		//Stockpile Items matching
		for (Map.Entry<MyAsset, Set<TypeIdentifier>> entry : foundIDs.entrySet()) {
			//Only compare the size of the sets, as both sets only contains valid and unique ids.
			//Therefore there should be no reason to compare the actualy IDs (which is really really slow)
			if (entry.getValue().size() != stockpileItems.keySet().size()) { //Stockpile have items not in the contract
				foundItems.remove(entry.getKey());
			}
		}
		return foundItems;
	}

	private static void addAssetChildren(List<MyAsset> assets, MyAsset asset) {
		assets.add(asset);
		for (MyAsset child : asset.getAssets()) {
			addAssetChildren(assets, child);
		}
	}

	public static Integer get(Integer typeID, boolean bpc) {
		//Ignore null
		if (typeID == null) {
			return null;
		}
		//BPC has negative value
		if (bpc) {
			typeID = -typeID;
		}
		//Return fixed TypeID
		return typeID;
	}

	public static List<TypeIdentifier> getTypes(Integer typeID, boolean bpc) {
		//Ignore null
		if (typeID == null) {
			return Collections.emptyList();
		}
		//BPC has negative value
		if (bpc) {
			typeID = -typeID;
			List<TypeIdentifier> list = new ArrayList<>();
			list.add(new TypeIdentifier(typeID, true));
			list.add(new TypeIdentifier(typeID, false));
			return list;
		} else {
			return Collections.singletonList(new TypeIdentifier(typeID, false));
		}
	}

	private static boolean ignore(Set<Integer> typeIDs, Integer typeID) {
		//Ignore null
		if (typeID == null) {
			return true;
		}
		//Ignore wrong typeID
		return !typeIDs.contains(typeID);
	}

	private static boolean ignore(Set<TypeIdentifier> typeIDs, TypeIdentifier typeID) {
		//Ignore null
		if (typeID == null) {
			return true;
		}
		//Ignore wrong typeID
		return !typeIDs.contains(typeID);
	}

	private <T> void add(Map<Integer, Set<T>> map, Integer typeID, T t) {
		if (typeID == null) {
			return; //Ignore null (should never happen: better safe than sorry)
		}
		Set<T> items = map.get(typeID);
		if (items == null) {
			items = new HashSet<>();
			map.put(typeID, items);
		}
		items.add(t);
	}

	private void addTypeIDs(Set<Integer> typeIDs, Stockpile stockpile) {
		for (StockpileItem item : stockpile.getItems()) {
			if (item.isTotal()) {
				continue;
			}
			typeIDs.add(item.getItemTypeID());
		}
		for (Stockpile subpile : stockpile.getSubpiles().keySet()) {
			addTypeIDs(typeIDs, subpile);
		}
	}

	private void updateItem(StockpileItem item, Stockpile stockpile) {
		final int TYPE_ID = item.getItemTypeID();
		double price = ApiIdConverter.getPrice(TYPE_ID, item.isBPC());
		float volume = ApiIdConverter.getVolume(item.getItem(), true);
		Double transactionAveragePrice = profileData.getTransactionAveragePrice(TYPE_ID);
		PriceData priceData = ApiIdConverter.getPriceData(TYPE_ID, item.isBPC());
		item.updateValues(price, volume, transactionAveragePrice, priceData);
		//Contract Items
		if (stockpile.isContracts()) {
			Set<MyContractItem> items = get(contractItems, stockpile).get(TYPE_ID);
			if (items != null) {
				for (MyContractItem contractItem : items) {
					item.updateContract(contractItem);
				}
			}
		}
		//Assets
		if (stockpile.isAssets()) {
			Set<MyAsset> items = get(assets, stockpile).get(TYPE_ID);
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

	private <E> Map<Integer, Set<E>> get(Map<Stockpile, Map<Integer, Set<E>>> map, Stockpile key) {
		Map<Integer, Set<E>> value = map.get(key);
		if (value == null) {
			value = new HashMap<>();
			map.put(key, value);
		}
		return value;
	}

	/**
	 * Update Subpiles for all stockpiles and return a list of the updated Subpiles.
	 * This method does not change the EventList
	 * @return
	 */
	private List<StockpileItem> getUpdatedSubpiles() {
		List<StockpileItem> added = new ArrayList<>();
		for (Stockpile stockpile : Settings.get().getStockpiles()) {
			updateSubpile(added, null, stockpile);
		}
		return added;
	}

	/**
	 * Update Subpiles for a single stockpile.
	 * This method will update the EventList (remove old, add new)
	 * This method is very ineffective when updating multiple Stockpiles:
	 * Use getUpdatedSubpiles() to update all
	 * And updateSubpile(,,) for anything > 1
	 * @param eventList
	 * @param parent
	 */
	public void updateSubpile(EventList<StockpileItem> eventList, Stockpile parent) {
		List<StockpileItem> updated = new ArrayList<>();
		List<StockpileItem> removed = new ArrayList<>();
		updateSubpile(updated, removed, parent);
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.removeAll(removed);
			StockpileTab.enableGroupFirstUpdate();
			eventList.addAll(updated);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	/**
	 * Internal: Don't use this.
	 * Update subpiles for a single stockpile. Does not modify the EventList.
	 * @param updated Updated SubpileItem's
	 * @param removed Removed SubpileItem's
	 * @param parent
	 */
	private void updateSubpile(List<StockpileItem> updated, List<StockpileItem> removed, Stockpile parent) {
		Map<Integer, StockpileItem> parentItems = new HashMap<>();
		for (StockpileItem item : parent.getItems()) {
			parentItems.put(item.getItemTypeID(), item);
		}
		//Save old items (for them to be removed)
		List<SubpileItem> subpileItems = new ArrayList<>(parent.getSubpileItems());
		//Clear old items
		parent.clearSubpileItems();
		for (SubpileItem subpileItem : subpileItems) {
			subpileItem.clearItemLinks();
		}
		//Update subs
		for (Stockpile stockpile : parent.getSubpileLinks()) {
			updateSubpile(updated, removed, stockpile);
		}
		//Add new items
		updateSubpileClaims(parent, parentItems);
		//Update stockpile items
		if (parent.isMatchAll() && (parent.isContracts() || parent.isAssets())) {
			updateStockpileItems(parent, false);
		}
		//Update items
		for (SubpileItem subpileItem : parent.getSubpileItems()) {
			updateItem(subpileItem, subpileItem.getStockpile());
		}
		parent.updateTotal();
		//Update lists
		if (removed != null) {
			removed.addAll(subpileItems);
		}
		updated.removeAll(subpileItems);
		if (profileManager.getStockpileIDs().isShown(parent.getStockpileID())) {
			String group = Settings.get().getStockpileGroupSettings().getGroup(parent);
			if (Settings.get().getStockpileGroupSettings().isGroupExpanded(group)) { //Stockpile group expanded or not in group
				updated.addAll(parent.getSubpileTableItems());
			}
		}
	}

	private static void updateSubpileClaims(Stockpile topStockpile) {
		Map<Integer, StockpileItem> parentItems = new HashMap<>();
		for (StockpileItem item : topStockpile.getItems()) {
			parentItems.put(item.getItemTypeID(), item);
		}
		updateSubpileClaims(topStockpile, parentItems);
	}

	private static void updateSubpileClaims(Stockpile topStockpile, Map<Integer, StockpileItem> topItems) {
		updateSubpileClaims(topStockpile, topStockpile, topItems, null, 0, "");
	}

	/**
	 * Internal: Don't use this.
	 * Do all the subpile calculations
	 * (this where the magic happens, 100% certified unreadable code! As required for all critical parts of this software)
	 * @param topStockpile
	 * @param parentStockpile
	 * @param topItems
	 * @param parentStock
	 * @param parentLevel
	 * @param parentPath
	 */
	private static void updateSubpileClaims(Stockpile topStockpile, Stockpile parentStockpile, Map<Integer, StockpileItem> topItems, SubpileStock parentStock, int parentLevel, String parentPath) {
		for (Map.Entry<Stockpile, Double> entry : parentStockpile.getSubpiles().entrySet()) {
			//For each subpile (stockpile)
			Stockpile currentStockpile = entry.getKey();
			Double value = entry.getValue();
			String path = parentPath + currentStockpile.getName() + "\r\n";
			int level = parentLevel + 1;
			SubpileStock subpileStock = new SubpileStock(topStockpile, currentStockpile, parentStockpile, parentStock, value, parentLevel, path);
			topStockpile.addSubpileStock(subpileStock);
			for (StockpileItem stockpileItem : currentStockpile.getItems()) {
				//For each StockpileItem
				if (stockpileItem.isTotal()) {
					continue; //Ignore Total
				}
				StockpileItem parentItem = topItems.get(stockpileItem.getItemTypeID());
				SubpileItem subpileItem = new SubpileItem(topStockpile, stockpileItem, subpileStock, parentLevel, path);
				int linkIndex = topStockpile.getSubpileItems().indexOf(subpileItem);
				if (parentItem != null) { //Add link (Advanced: Item + Link)
					subpileItem.addItemLink(parentItem, null); //Add link
				}
				if (linkIndex >= 0) { //Update item (Advanced: Link + Link = MultiLink)
					SubpileItem linkItem = topStockpile.getSubpileItems().get(linkIndex);
					linkItem.addItemLink(stockpileItem, subpileStock);
					if (level >= linkItem.getLevel()) {
						linkItem.setPath(path);
						linkItem.setLevel(level);
					}
				} else { //Add new item (Simple)
					topStockpile.addSubpileItem(subpileItem);
				}
			}
			updateSubpileClaims(topStockpile, currentStockpile, topItems, subpileStock, level, path);
		}
	}
}
