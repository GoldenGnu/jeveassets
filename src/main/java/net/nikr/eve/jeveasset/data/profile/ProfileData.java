/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.profile;

import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContainerLog;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.Jump;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.AssetAddedData;
import net.nikr.eve.jeveasset.data.settings.LogManager;
import net.nikr.eve.jeveasset.data.settings.MarketPriceData;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.settings.types.EditablePriceType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.JumpType;
import net.nikr.eve.jeveasset.data.settings.types.LocationsType;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import uk.me.candle.eve.graph.Edge;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.distances.Jumps;

public class ProfileData {

	private final ProfileManager profileManager;

	private final EventList<MyContractItem> contractItemEventList = new EventListManager<MyContractItem>().create();
	private final EventList<MyIndustryJob> industryJobsEventList = new EventListManager<MyIndustryJob>().create();
	private final EventList<MyMarketOrder> marketOrdersEventList = new EventListManager<MyMarketOrder>().create();
	private final EventList<MyJournal> journalEventList = new EventListManager<MyJournal>().create();
	private final EventList<MyTransaction> transactionsEventList = new EventListManager<MyTransaction>().create();
	private final EventList<MyAsset> assetsEventList = new EventListManager<MyAsset>().create();
	private final EventList<MyAccountBalance> accountBalanceEventList = new EventListManager<MyAccountBalance>().create();
	private final EventList<MyContract> contractEventList = new EventListManager<MyContract>().create();
	private final List<MyContractItem> contractItemList = new ArrayList<>();
	private final List<MyIndustryJob> industryJobsList = new ArrayList<>();
	private final List<MyMarketOrder> marketOrdersList = new ArrayList<>();
	private final List<MyJournal> journalList = new ArrayList<>();
	private final List<MyTransaction> transactionsList = new ArrayList<>();
	private final List<MyAsset> assetsList = new ArrayList<>();
	private final List<MyAccountBalance> accountBalanceList = new ArrayList<>();
	private final List<MyContract> contractList = new ArrayList<>();
	private final List<MyContainerLog> containerLogsList = new ArrayList<>();
	private Map<Long, List<MyContainerLog>> containerLogs = null; //ItemID : long
	private Map<Integer, List<MyAsset>> uniqueAssetsDuplicates = null; //TypeID : int
	private Map<Integer, MarketPriceData> marketPriceData; //TypeID : int
	private Map<Integer, MarketPriceData> transactionPriceDataSell; //TypeID : int
	private Map<Integer, MarketPriceData> transactionPriceDataBuy; //TypeID : int
	private final List<String> ownerNames = new ArrayList<>();
	private final Map<Long, OwnerType> owners = new HashMap<>();
	private boolean assetAddedDataChanged = false;
	private final Graph graph;
	private final Map<Long, SolarSystem> systemCache;
	private final Map<Long, Map<Long, Integer>> distance = new HashMap<>();

	public ProfileData(ProfileManager profileManager) {
		this.profileManager = profileManager;
		// build the graph.
		// filter the solarsystems based on the settings.
		graph = new Graph(new Jumps());
		int count = 0;
		systemCache = new HashMap<>();
		for (Jump jump : StaticData.get().getJumps()) { // this way we exclude the locations that are unreachable.
			count++;
			SplashUpdater.setSubProgress((int) (count * 100.0 / StaticData.get().getJumps().size()));

			SolarSystem from = systemCache.get(jump.getFrom().getSystemID());
			SolarSystem to = systemCache.get(jump.getTo().getSystemID());
			if (from == null) {
				from = SolarSystem.create(systemCache, jump.getFrom());
			}
			if (to == null) {
				to = SolarSystem.create(systemCache, jump.getTo());
			}
			graph.addEdge(new Edge(from, to));
		}
		SplashUpdater.setSubProgress(100);
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

	public EventList<MyContract> getContractEventList() {
		return contractEventList;
	}

	public EventList<MyContractItem> getContractItemEventList() {
		return contractItemEventList;
	}

	public List<MyContractItem> getContractItemList() {
		return contractItemList;
	}

	public List<MyIndustryJob> getIndustryJobsList() {
		return industryJobsList;
	}

	public List<MyMarketOrder> getMarketOrdersList() {
		return marketOrdersList;
	}

	public List<MyJournal> getJournalList() {
		return journalList;
	}

	public List<MyTransaction> getTransactionsList() {
		return transactionsList;
	}

	public List<MyAsset> getAssetsList() {
		return assetsList;
	}

	public List<MyAccountBalance> getAccountBalanceList() {
		return accountBalanceList;
	}

	public List<MyContract> getContractList() {
		return contractList;
	}

	public List<MyContainerLog> getContainerLogsList() {
		return containerLogsList;
	}

	public List<String> getOwnerNames(boolean all) {
		List<String> sortedOwners = new ArrayList<>(ownerNames);
		if (all) {
			sortedOwners.add(0, General.get().all());
		}
		return sortedOwners;
	}

	public Map<Long, OwnerType> getOwners() {
		return new HashMap<>(owners);
	}

	public void updateJumps(Collection<JumpType> jumpTypes, Class<?> clazz) {
		for (JumpType jumpType : jumpTypes) {
			jumpType.clearJumps(); //Clear old
			long systemID = jumpType.getLocation().getSystemID();
			if (systemID <= 0) {
				continue;
			}
			for (MyLocation jumpLocation : Settings.get().getJumpLocations(clazz)) {
				long jumpSystemID = jumpLocation.getSystemID();
				if (systemID != jumpSystemID) {
					Map<Long, Integer> distances = distance.get(jumpSystemID);
					if (distances == null) {
						distances = new HashMap<>();
						distance.put(jumpSystemID, distances);
					}
					Integer jumps = distances.get(systemID);
					if (jumps == null) {
						SolarSystem from = systemCache.get(systemID);
						SolarSystem to = systemCache.get(jumpSystemID);
						if (from != null && to != null) {
							jumps = graph.distanceBetween(from, to);
						} else {
							jumps = -1;
						}
						distances.put(systemID, jumps);
					}
					jumpType.addJump(jumpSystemID, jumps);
				} else {
					jumpType.addJump(jumpSystemID, 0);
				}
			}
		}
	}

	private Set<Integer> createPriceTypeIDs() {
		Set<Integer> priceTypeIDs = new HashSet<>();
		for (OwnerType owner : profileManager.getOwnerTypes()) {
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
			for (MyTransaction transaction : owner.getTransactions()) {
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

	public void updateLocations(Set<Long> locationIDs) {
		if (locationIDs == null || locationIDs.isEmpty()) {
			return;
		}
		//Update Contracts locations
		try {
			contractEventList.getReadWriteLock().readLock().lock();
			for (MyContract contract : contractEventList) {
				//Update Locations
				contract.setStartLocation(ApiIdConverter.getLocation(contract.getStartLocationID()));
				contract.setEndLocation(ApiIdConverter.getLocation(contract.getEndLocationID()));
			}
		} finally {
			contractEventList.getReadWriteLock().readLock().unlock();
		}
		updateLocation(transactionsEventList, locationIDs);
		updateLocation(marketOrdersEventList, locationIDs);
		updateLocation(assetsEventList, locationIDs);
		updateLocation(industryJobsEventList, locationIDs);
		updateLocations(contractItemEventList, locationIDs);
		updateLocations(contractEventList, locationIDs);
	}

	public void updateNames(Set<Long> itemIDs) {
		if (itemIDs == null || itemIDs.isEmpty()) {
			return;
		}
		updateNames(assetsEventList, itemIDs);
	}

	public void updatePrice(Set<Integer> typeIDs) {
		if (typeIDs == null || typeIDs.isEmpty()) {
			return;
		}
		updatePrices(marketOrdersEventList, typeIDs);
		updatePrices(contractItemEventList, typeIDs);
		updateAssetPrices(assetsEventList, typeIDs);
		updateIndustryJobPrices(industryJobsEventList, typeIDs);
	}

	public boolean updateEventLists() {
		return updateEventLists(new Date());
	}

	public boolean updateEventLists(Date assetAddedData) {
		assetAddedDataChanged = false;
		uniqueAssetsDuplicates = new HashMap<>();
		containerLogs = new HashMap<>();
		containerLogsList.clear();
		Set<String> uniqueOwnerNames = new HashSet<>();
		Map<Long, OwnerType> uniqueOwners = new HashMap<>();
		//Temp
		List<MyAsset> assets = new ArrayList<>();
		List<MyAccountBalance> accountBalance = new ArrayList<>();
		Map<Long, OwnerType> assetsMap = new HashMap<>();
		Map<Long, OwnerType> accountBalanceMap = new HashMap<>();
		Set<MyMarketOrder> marketOrders = new HashSet<>();
		Set<MyMarketOrder> charMarketOrders = new HashSet<>();
		Set<MyJournal> journals = new HashSet<>();
		Set<MyTransaction> transactions = new HashSet<>();
		Set<MyIndustryJob> industryJobs = new HashSet<>();
		Set<MyContractItem> contractItems = new HashSet<>();
		Set<MyContract> contracts = new HashSet<>();
		Map<Long, OwnerType> blueprintsMap = new HashMap<>();
		Map<Long, RawBlueprint> blueprints = new HashMap<>();

		maximumPurchaseAge();
		calcTransactionsPriceData();
		for (OwnerType owner : profileManager.getOwnerTypes()) {
			if (!owner.isShowOwner()) {
				continue;
			}
			uniqueOwnerNames.add(owner.getOwnerName());
			uniqueOwners.put(owner.getOwnerID(), owner);
		}
		//Add Market Orders/Journal/Transactions/Industry Jobs/Contracts/Contract Items/Blueprints/Assets/Account Balance
		for (OwnerType owner : profileManager.getOwnerTypes()) {
			if (!owner.isShowOwner()) {
				continue;
			}
			//Marker Orders
			//If owner is corporation overwrite the character orders (to use the "right" owner)
			if (owner.isCorporation()) {
				marketOrders.addAll(owner.getMarketOrders());
			} else {
				charMarketOrders.addAll(owner.getMarketOrders());
			}
			//Journal
			journals.addAll(owner.getJournal());
			//Transactions
			transactions.addAll(owner.getTransactions());
			//Industry Jobs
			industryJobs.addAll(owner.getIndustryJobs());
			//Contracts & Contract Items
			for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
				MyContract contract = entry.getKey();
				if (entry.getValue().isEmpty()
						&& contract.isCourierContract()
						&& ( //XXX - Workaround for alien contracts
						uniqueOwners.containsKey(contract.getAcceptorID())
						|| uniqueOwners.containsKey(contract.getAssigneeID())
						|| uniqueOwners.containsKey(contract.getIssuerID())
						|| (contract.isForCorp() && uniqueOwners.containsKey(contract.getIssuerCorpID())))) {
					//Add contracts and ContractItems
					contracts.add(contract);
					contractItems.add(new MyContractItem(contract));
				} else if (!entry.getValue().isEmpty()) {
					//Add contracts and ContractItems
					contracts.add(contract);
					contractItems.addAll(entry.getValue());
				}
			}
			//Blueprints (Newest)
			if (!owner.getBlueprints().isEmpty()) {
				OwnerType ownerType = blueprintsMap.get(owner.getOwnerID());
				if (ownerType == null || (owner.getBlueprintsNextUpdate() != null && ownerType.getBalanceNextUpdate() != null && owner.getBlueprintsNextUpdate().after(ownerType.getBalanceNextUpdate()))) {
					blueprintsMap.put(owner.getOwnerID(), owner);
 				}
			}
			//Assets (Newest)
			if (!owner.getAssets().isEmpty()) {
				OwnerType ownerType = assetsMap.get(owner.getOwnerID());
				if (ownerType == null || (owner.getAssetNextUpdate() != null && ownerType.getAssetNextUpdate() != null && owner.getAssetNextUpdate().after(ownerType.getAssetNextUpdate()))) {
					assetsMap.put(owner.getOwnerID(), owner);
 				}
			}
			//Account Balance (Newest)
			if (!owner.getAccountBalances().isEmpty()) {
				OwnerType ownerType = accountBalanceMap.get(owner.getOwnerID());
				if (ownerType == null || (owner.getBalanceNextUpdate() != null && ownerType.getBalanceNextUpdate() != null && owner.getBalanceNextUpdate().after(ownerType.getBalanceNextUpdate()))) {
					accountBalanceMap.put(owner.getOwnerID(), owner);
 				}
			}
			//Container Logs
			containerLogsList.addAll(owner.getContainerLogs());
			for (MyContainerLog containerLog : owner.getContainerLogs()) {
				LogManager.put(containerLogs, containerLog.getContainerID(), containerLog);
			}
		}

		//Fill accountBalance
		for (OwnerType owner : accountBalanceMap.values()) {
			accountBalance.addAll(owner.getAccountBalances());
 		}

		//Fill blueprints
		for (OwnerType owner : blueprintsMap.values()) {
			blueprints.putAll(owner.getBlueprints());
 		}

		for (MyMarketOrder marketOrder : charMarketOrders) {
			if (!marketOrder.isCorp()) { //Remove non-corporation orders
				marketOrders.remove(marketOrder);
			}
			marketOrders.add(marketOrder);
		}

		//Update MarketOrders dynamic values
		for (MyMarketOrder order : marketOrders) {
			//Last Transaction
			if (order.isBuyOrder()) { //Buy
				order.setLastTransaction(transactionPriceDataSell.get(order.getTypeID()));
			} else { //Sell
				order.setLastTransaction(transactionPriceDataBuy.get(order.getTypeID()));
			}
			order.setIssuedByName(ApiIdConverter.getOwnerName(order.getIssuedBy()));
		}

		//Update IndustryJobs dynamic values
		for (MyIndustryJob industryJob : industryJobs) {
			//Update Owners
			industryJob.setInstaller(ApiIdConverter.getOwnerName(industryJob.getInstallerID()));
			//Update BPO/BPC status
			RawBlueprint blueprint = blueprints.get(industryJob.getBlueprintID());
			industryJob.setBlueprint(blueprint);
			//Price
			updatePrice(industryJob);
		}
		//Update Contracts dynamic values
		for (MyContract contract : contracts) {
			OwnerType issuer = uniqueOwners.get(contract.getIssuerID());
			OwnerType acceptor = uniqueOwners.get(contract.getAcceptorID());
			if (issuer != null) {
				contract.setIssuerAfterAssets(issuer.getAssetLastUpdate());
			}
			if (acceptor != null) {
				contract.setAcceptorAfterAssets(acceptor.getAssetLastUpdate());
			}
			//Update Locations
			contract.setStartLocation(ApiIdConverter.getLocation(contract.getStartLocationID()));
			contract.setEndLocation(ApiIdConverter.getLocation(contract.getEndLocationID()));
			//Update Owners
			contract.setAcceptor(ApiIdConverter.getOwnerName(contract.getAcceptorID()));
			contract.setAssignee(ApiIdConverter.getOwnerName(contract.getAssigneeID()));
			contract.setIssuerCorp(ApiIdConverter.getOwnerName(contract.getIssuerCorpID()));
			contract.setIssuer(ApiIdConverter.getOwnerName(contract.getIssuerID()));
		}

		//Update Transaction dynamic values
		for (MyTransaction transaction : transactions) {
			transaction.setClientName(ApiIdConverter.getOwnerName(transaction.getClientID()));
		}
		//Update Journal dynamic values
		for (MyJournal journal : journals) {
			journal.setFirstPartyName(ApiIdConverter.getOwnerName(journal.getFirstPartyID()));
			journal.setSecondPartyName(ApiIdConverter.getOwnerName(journal.getSecondPartyID()));
		}

		//Update Items dynamic values
		for (Item item : StaticData.get().getItems().values()) {
			item.setPriceReprocessed(ApiIdConverter.getPriceReprocessed(item));
		}

		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				//Add Market Orders to Assets
				addAssets(DataConverter.assetMarketOrder(marketOrders, Settings.get().isIncludeSellOrders(), Settings.get().isIncludeBuyOrders()), assets, blueprints, assetAddedData);

				//Add Industry Jobs to Assets
				addAssets(DataConverter.assetIndustryJob(industryJobs, Settings.get().isIncludeManufacturing()), assets, blueprints, assetAddedData);

				//Add Contract Items to Assets
				addAssets(DataConverter.assetContracts(contractItems, uniqueOwners, Settings.get().isIncludeSellContracts(), Settings.get().isIncludeBuyContracts()), assets, blueprints, assetAddedData);

				//Add Assets to Assets
				for (OwnerType owner : assetsMap.values()) {
					addAssets(owner.getAssets(), assets, blueprints, assetAddedData);
				}
			}
		});

		//Update Locations
		List<EditableLocationType> editableLocationTypes = new ArrayList<>();
		editableLocationTypes.addAll(assets);
		editableLocationTypes.addAll(marketOrders);
		editableLocationTypes.addAll(transactions);
		editableLocationTypes.addAll(industryJobs);
		for (EditableLocationType editableLocationType : editableLocationTypes) {
			editableLocationType.setLocation(ApiIdConverter.getLocation(editableLocationType.getLocationID()));
		}
		//Update Prices
		List<EditablePriceType> editablePriceTypes = new ArrayList<>();
		editablePriceTypes.addAll(marketOrders);
		editablePriceTypes.addAll(contractItems);
		for (EditablePriceType editablePriceType : editablePriceTypes) {
			editablePriceType.setDynamicPrice(ApiIdConverter.getPrice(editablePriceType.getItem().getTypeID(), editablePriceType.isBPC()));
		}
		//Update Jumps (Must be updated after locations!)
		updateJumps(new ArrayList<>(assets), MyAsset.class);

		assetsList.clear();
		assetsList.addAll(assets);
		marketOrdersList.clear();
		marketOrdersList.addAll(marketOrders);
		journalList.clear();
		journalList.addAll(journals);
		transactionsList.clear();
		transactionsList.addAll(transactions);
		industryJobsList.clear();
		industryJobsList.addAll(industryJobs);
		contractItemList.clear();
		contractItemList.addAll(contractItems);
		contractList.clear();
		contractList.addAll(contracts);
		accountBalanceList.clear();
		accountBalanceList.addAll(accountBalance);
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					assetsEventList.getReadWriteLock().writeLock().lock();
					assetsEventList.clear();
					assetsEventList.addAll(assets);
				} finally {
					assetsEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					marketOrdersEventList.getReadWriteLock().writeLock().lock();
					marketOrdersEventList.clear();
					marketOrdersEventList.addAll(marketOrders);
				} finally {
					marketOrdersEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					journalEventList.getReadWriteLock().writeLock().lock();
					journalEventList.clear();
					journalEventList.addAll(journals);
				} finally {
					journalEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					transactionsEventList.getReadWriteLock().writeLock().lock();
					transactionsEventList.clear();
					transactionsEventList.addAll(transactions);
				} finally {
					transactionsEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					industryJobsEventList.getReadWriteLock().writeLock().lock();
					industryJobsEventList.clear();
					industryJobsEventList.addAll(industryJobs);
				} finally {
					industryJobsEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					contractItemEventList.getReadWriteLock().writeLock().lock();
					contractItemEventList.clear();
					contractItemEventList.addAll(contractItems);
				} finally {
					contractItemEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					contractEventList.getReadWriteLock().writeLock().lock();
					contractEventList.clear();
					contractEventList.addAll(contracts);
				} finally {
					contractEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					accountBalanceEventList.getReadWriteLock().writeLock().lock();
					accountBalanceEventList.clear();
					accountBalanceEventList.addAll(accountBalance);
				} finally {
					accountBalanceEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
		//Sort Owners
		ownerNames.clear();
		ownerNames.addAll(uniqueOwnerNames);
		Collections.sort(ownerNames, new CaseInsensitiveComparator());
		owners.clear();
		owners.putAll(uniqueOwners);
		return assetAddedDataChanged;
	}

	public void updateNames(EventList<MyAsset> eventList, Set<Long> itemIDs) {
		if (itemIDs == null || itemIDs.isEmpty()) {
			return;
		}
		List<MyAsset> found = new ArrayList<>();
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (MyAsset asset : eventList) {
				if (itemIDs.contains(asset.getItemID())) {
					found.add(asset); //Save for update
					updateName(asset); //Update Name
					updateContainerChildren(found, asset.getAssets()); //Update Container
				}
				for (MyAsset parent : asset.getParents()) { //Offices
					if (parent.getTypeID() != 27) {
						continue;
					}
					if (itemIDs.contains(parent.getItemID())) {
						updateName(parent); //Update Name
						updateContainerChildren(found, parent.getAssets()); //Update Container
					}
				}
			}
			if (found.isEmpty()) {
				
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		updateList(eventList, found);
	}

	public static <T extends ItemType & EditablePriceType> void updatePrices(EventList<T> eventList, Set<Integer> typeIDs) {
		if (typeIDs == null || typeIDs.isEmpty()) {
			return;
		}
		List<T> found = new ArrayList<>();
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (T t : eventList) {
				if (typeIDs.contains(t.getItem().getTypeID())) {
					found.add(t); //Save for update
					t.setDynamicPrice(ApiIdConverter.getPrice(t.getItem().getTypeID(), t.isBPC())); //Update data
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		updateList(eventList, found);
	}

	private void updateIndustryJobPrices(EventList<MyIndustryJob> eventList, Set<Integer> typeIDs) {
		if (typeIDs == null || typeIDs.isEmpty()) {
			return;
		}
		List<MyIndustryJob> found = new ArrayList<>();
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (MyIndustryJob industryJob : eventList) {
				if (typeIDs.contains(industryJob.getItem().getTypeID()) || typeIDs.contains(industryJob.getProductTypeID())) {
					found.add(industryJob); //Save for update
					updatePrice(industryJob); //Update data
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		updateList(eventList, found);
	}

	private void updateAssetPrices(EventList<MyAsset> eventList, Set<Integer> typeIDs) {
		if (typeIDs == null || typeIDs.isEmpty()) {
			return;
		}
		List<MyAsset> found = new ArrayList<>();
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (MyAsset asset : eventList) {
				//Reprocessed price
				boolean reprocessed = false;
				for (ReprocessedMaterial material : asset.getItem().getReprocessedMaterial()) {
					if (typeIDs.contains(material.getTypeID())) {
						reprocessed = true;
						break;
					}
				}
				if (reprocessed) {
					asset.setPriceReprocessed(ApiIdConverter.getPriceReprocessed(asset.getItem())); //Update data
				}
				//Dynamic Price
				boolean dynamic = typeIDs.contains(asset.getItem().getTypeID());
				if (dynamic) {
					updatePrice(asset); //Update data
				}
				//Update
				if (reprocessed || dynamic) { //If changed
					found.add(asset); //Save for update
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		updateList(eventList, found);
	}

	public static <T extends EditableLocationType> void updateLocation(EventList<T> eventList, Set<Long> locationIDs) {
		if (locationIDs == null || locationIDs.isEmpty()) {
			return;
		}
		List<T> found = new ArrayList<>();
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (T t : eventList) {
				if (locationIDs.contains(t.getLocationID())) {
					found.add(t); //Save for update
					t.setLocation(ApiIdConverter.getLocation(t.getLocationID())); //Update data
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		updateList(eventList, found);
	}

	private <T extends LocationsType> void updateLocations(EventList<T> eventList, Set<Long> locationIDs) {
		if (locationIDs == null || locationIDs.isEmpty()) {
			return;
		}
		List<T> found = new ArrayList<>();
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (T t : eventList) {
				for (MyLocation location : t.getLocations()) {
					if (locationIDs.contains(location.getLocationID())) {
						found.add(t); //Save for update
						break; //Item already added
					}
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		updateList(eventList, found);
	}

	private static <T> void updateList(EventList<T> eventList, List<T> found) {
		if (found.isEmpty()) {
			return;
		}
		if (found.size() > 2) {
			Program.ensureEDT(new Runnable() {
				@Override
				public void run() {
					try {
						eventList.getReadWriteLock().writeLock().lock();
						List<T> cache = new ArrayList<>(eventList);
						eventList.clear();
						eventList.addAll(cache);
					} finally {
						eventList.getReadWriteLock().writeLock().unlock();
					}
				}
			});
		} else {
			Program.ensureEDT(new Runnable() {
				@Override
				public void run() {
					try {
						eventList.getReadWriteLock().writeLock().lock();
						eventList.removeAll(found);
						eventList.addAll(found);
					} finally {
						eventList.getReadWriteLock().writeLock().unlock();
					}
				}
			});
		}
	}

	private void maximumPurchaseAge() {
		//Create Market Price Data
		marketPriceData = new HashMap<>();
		//Date - maximumPurchaseAge in days
		Date maxAge = new Date(System.currentTimeMillis() - (Settings.get().getMaximumPurchaseAge() * 24 * 60 * 60 * 1000L));
		for (OwnerType owner : profileManager.getOwnerTypes()) {
			for (MyMarketOrder marketOrder : owner.getMarketOrders()) {
				if (marketOrder.isBuyOrder() //Buy orders only
						//at least one bought
						&& !Objects.equals(marketOrder.getVolumeRemain(), marketOrder.getVolumeTotal())
						//Date in range or unlimited
						&& (marketOrder.getIssued().after(maxAge) || Settings.get().getMaximumPurchaseAge() == 0)) {
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

	private void calcTransactionsPriceData() {
		//Create Transaction Price Data
		transactionPriceDataSell = new HashMap<>();
		transactionPriceDataBuy = new HashMap<>();
		//Date - maximumPurchaseAge in days
		for (OwnerType owner : profileManager.getOwnerTypes()) {
			for (MyTransaction transaction : owner.getTransactions()) {
				if (transaction.isSell()) { //Sell
					createTransactionsPriceData(transactionPriceDataSell, transaction);
				} else { //Buy
					createTransactionsPriceData(transactionPriceDataBuy, transaction);
				}

			}
		}
	}

	private void createTransactionsPriceData(Map<Integer, MarketPriceData> transactionPriceData, MyTransaction transaction) {
		int typeID = transaction.getTypeID();
		if (!transactionPriceData.containsKey(typeID)) {
			transactionPriceData.put(typeID, new MarketPriceData());
		}
		MarketPriceData data = transactionPriceData.get(typeID);
		data.update(transaction.getPrice(), transaction.getDate());
	}

	private void addAssets(final List<MyAsset> assets, List<MyAsset> addTo, Map<Long, RawBlueprint> blueprints, Date assetAddedData) {
		for (MyAsset asset : assets) {
			//XXX Ignore 9e18 locations: https://github.com/ccpgames/esi-issues/issues/684
			if (asset.getLocationID() > 9000000000000000000L) {
				continue;
			}
			//XXX Ignore deleted PI structures: https://github.com/esi/esi-issues/issues/943
			if (asset.getLocationID() > 40000000 && asset.getLocationID() < 50000000) {
				continue;
			}
			//Handle Asset Structures
			if (asset.getItem().getCategory().equals("Structure")) {
				for (MyAsset childAsset:  asset.getAssets()) {
					updateStructureAssets(childAsset, asset);
				}
			}
			//Blueprint
			RawBlueprint blueprint = blueprints.get(asset.getItemID());
			asset.setBlueprint(blueprint);
			//Tags
			Tags tags = Settings.get().getTags(asset.getTagID());
			asset.setTags(tags);
			//Date added
			if (!assetAddedDataChanged && !AssetAddedData.containsKey(asset.getItemID())) {
				assetAddedDataChanged = true;
			}
			asset.setAdded(AssetAddedData.getAdd(asset.getItemID(), assetAddedData));
			//Price
			updatePrice(asset);
			//Reprocessed price
			asset.setPriceReprocessed(ApiIdConverter.getPriceReprocessed(asset.getItem()));
			//Market price
			asset.setMarketPriceData(marketPriceData.get(asset.getItem().getTypeID()));
			//User Item Names
			updateName(asset);
			//Contaioner
			updateContainer(asset);
			//Price data
			PriceData priceData = Settings.get().getPriceData().get(asset.getItem().getTypeID());
			if (asset.getItem().isMarketGroup() && priceData != null && !priceData.isEmpty()) { //Market Price
				asset.setPriceData(priceData);
			} else { //No Price :(
				asset.setPriceData(null);
			}
			//Type Count
			int typeID;
			if (asset.isBPC()) {
				typeID = -asset.getItem().getTypeID();
			} else {
				typeID = asset.getItem().getTypeID();
			}
			List<MyAsset> dup = uniqueAssetsDuplicates.get(typeID);
			if (dup == null) {
				dup = new ArrayList<>();
				uniqueAssetsDuplicates.put(typeID, dup);
			}
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
			if (asset.getTypeID() != 27) { //Ignore offices
				addTo.add(asset);
			} else { //Office
				asset.setLocation(ApiIdConverter.getLocation(asset.getLocationID()));
			}
			//Add sub-assets
			addAssets(asset.getAssets(), addTo, blueprints, assetAddedData);
		}
	}

	private static void updatePrice(MyIndustryJob industryJob) {
		industryJob.setOutputPrice(ApiIdConverter.getPrice(industryJob.getProductTypeID(), false));
		industryJob.setDynamicPrice(ApiIdConverter.getPrice(industryJob.getItem().getTypeID(), industryJob.isBPC()));
	}

	private static void updatePrice(MyAsset asset) {
		//User price
		if (asset.getItem().isBlueprint() && !asset.isBPO()) { //Blueprint Copy
			asset.setUserPrice(Settings.get().getUserPrices().get(-asset.getItem().getTypeID()));
		} else { //All other
			asset.setUserPrice(Settings.get().getUserPrices().get(asset.getItem().getTypeID()));
		}
		//Dynamic Price
		asset.setDynamicPrice(ApiIdConverter.getPrice(asset.getItem().getTypeID(), asset.isBPC())); //Update data
	}

	private void updateName(MyAsset asset) {
		if (Settings.get().getUserItemNames().containsKey(asset.getItemID())) {
			asset.setName(Settings.get().getUserItemNames().get(asset.getItemID()).getValue(), true, false);
		} else if (Settings.get().getEveNames().containsKey(asset.getItemID())) {
			String eveName = Settings.get().getEveNames().get(asset.getItemID());
			asset.setName(eveName + " (" + asset.getTypeName() + ")", false, true);
		} else {
			asset.setName(asset.getTypeName(), false, false);
		}
	}

	private void updateContainerChildren(List<MyAsset> found, List<MyAsset> assets) {
		for (MyAsset asset : assets) {
			found.add(asset);
			updateContainer(asset);
			updateContainerChildren(found, asset.getAssets());
		}
	}

	private void updateContainer(MyAsset asset) {
		String sContainer = "";
		List<Long> parentIds = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		if (asset.getParents().isEmpty()) {
			builder.append(General.get().none());
		} else {
			boolean first = true;
			for (MyAsset parentAsset : asset.getParents()) {
				if (first) {
					first = false;
				} else {
					builder.append(" > ");
				}
				builder.append(containerName(parentAsset));
				parentIds.add(parentAsset.getItemID());
 			}
		}
		asset.setContainer(builder.toString().intern());
		List<MyContainerLog> containers = containerLogs.get(asset.getItemID());
		if (containers != null) {
			for (MyContainerLog containerLog : containers) {
				containerLog.setContainer(sContainer);
				containerLog.setParentIDs(parentIds);
			}
		}
		asset.setContainer(sContainer.intern());
	}

	private void updateStructureAssets(final MyAsset asset, final MyAsset structure) {
		final Long locationID;
		if (asset.isCorporation() && !asset.getParents().isEmpty() && asset.getParents().get(asset.getParents().size() - 1).equals(structure) && asset.getTypeID() != 27) {
			locationID = structure.getLocationID();
		} else {
			asset.getParents().remove(structure);
			locationID = structure.getItemID();
		}
		asset.setLocationID(locationID);
		asset.setLocation(ApiIdConverter.getLocation(locationID));
		asset.setLocationType(RawConverter.toAssetLocationType(locationID));
		for (MyAsset subAsset : asset.getAssets()) { //Update child assets
			updateStructureAssets(subAsset, structure);
		}
	}

	public static String containerName(MyAsset asset) {
		if (!asset.isUserName()) {
			return asset.getName() + " #" + asset.getItemID();
		} else {
			return asset.getName();
		}
	}
}
