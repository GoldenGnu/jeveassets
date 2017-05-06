/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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

import ca.odell.glazedlists.EventList;
import com.beimin.eveapi.model.shared.Blueprint;
import com.beimin.eveapi.model.shared.ContractType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.api.OwnerType;
import net.nikr.eve.jeveasset.data.tag.Tags;
import net.nikr.eve.jeveasset.data.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.types.JumpType;
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
	private final List<MyContractItem> contractItemList = new ArrayList<MyContractItem>();
	private final List<MyIndustryJob> industryJobsList = new ArrayList<MyIndustryJob>();
	private final List<MyMarketOrder> marketOrdersList = new ArrayList<MyMarketOrder>();
	private final List<MyJournal> journalList = new ArrayList<MyJournal>();
	private final List<MyTransaction> transactionsList = new ArrayList<MyTransaction>();
	private final List<MyAsset> assetsList = new ArrayList<MyAsset>();
	private final List<MyAccountBalance> accountBalanceList = new ArrayList<MyAccountBalance>();
	private final List<MyContract> contractList = new ArrayList<MyContract>();
	private Map<Integer, List<MyAsset>> uniqueAssetsDuplicates = null; //TypeID : int
	private Map<Integer, MarketPriceData> marketPriceData; //TypeID : int
	private Map<Integer, MarketPriceData> transactionPriceDataSell; //TypeID : int
	private Map<Integer, MarketPriceData> transactionPriceDataBuy; //TypeID : int
	private final List<String> ownerNames = new ArrayList<String>();
	private final Map<Long, OwnerType> owners = new HashMap<Long, OwnerType>();
	private boolean saveSettings = false;
	private final Graph graph;
	private final Map<Long, SolarSystem> systemCache;
	private final Map<Long, Map<Long, Integer>> distance = new HashMap<Long, Map<Long, Integer>>();

	public ProfileData(ProfileManager profileManager) {
		this.profileManager = profileManager;
		// build the graph.
		// filter the solarsystems based on the settings.
		graph = new Graph(new Jumps());
		int count = 0;
		systemCache = new HashMap<Long, SolarSystem>();
		for (Jump jump : StaticData.get().getJumps()) { // this way we exclude the locations that are unreachable.
			count++;
			SplashUpdater.setSubProgress((int) (count * 100.0 / StaticData.get().getJumps().size()));

			SolarSystem from = systemCache.get(jump.getFrom().getSystemID());
			SolarSystem to = systemCache.get(jump.getTo().getSystemID());
			if (from == null) {
				from = new SolarSystem(jump.getFrom());
				systemCache.put(from.getSystemID(), from);
			}
			if (to == null) {
				to = new SolarSystem(jump.getTo());
				systemCache.put(to.getSystemID(), to);
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

	public List<String> getOwnerNames(boolean all) {
		List<String> sortedOwners = new ArrayList<String>(ownerNames);
		if (all) {
			sortedOwners.add(0, General.get().all());
		}
		return sortedOwners;
	}

	public Map<Long, OwnerType> getOwners() {
		return new HashMap<Long, OwnerType>(owners);
	}

	public void updateJumps(Collection<JumpType> jumpTypes, Class<?> clazz) {
		for (JumpType jumpType : jumpTypes) {
			jumpType.clearJumps(); //Clear old
			long systemID = jumpType.getLocation().getSystemID();
			if (systemID <= 0) {
				return;
			}
			for (MyLocation jumpLocation : Settings.get().getJumpLocations(clazz)) {
				long jumpSystemID = jumpLocation.getSystemID();
				if (systemID != jumpSystemID) {
					Map<Long, Integer> distances = distance.get(jumpSystemID);
					if (distances == null) {
						distances = new HashMap<Long, Integer>();
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
		Set<Integer> priceTypeIDs = new HashSet<Integer>();
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

	public boolean updateEventLists() {
		saveSettings = false;
		uniqueAssetsDuplicates = new HashMap<Integer, List<MyAsset>>();
		Set<String> uniqueOwnerNames = new HashSet<String>();
		Map<Long, OwnerType> uniqueOwners = new HashMap<Long, OwnerType>();
		//Temp
		List<MyAsset> assets = new ArrayList<MyAsset>();
		List<MyAccountBalance> accountBalance = new ArrayList<MyAccountBalance>();
		//ownerID > 
		Date blueprintsNewest = null;
		Date assetsNewest = null;
		Date accountBalanceNewest = null;
		Map<Long, List<MyAsset>> assetsMap = new HashMap<Long, List<MyAsset>>();
		Map<Long, List<MyAccountBalance>> accountBalanceMap = new HashMap<Long, List<MyAccountBalance>>();
		Set<MyMarketOrder> marketOrders = new HashSet<MyMarketOrder>();
		Set<MyJournal> journal = new HashSet<MyJournal>();
		Set<MyTransaction> transactions = new HashSet<MyTransaction>();
		Set<MyIndustryJob> industryJobs = new HashSet<MyIndustryJob>();
		Set<MyContractItem> contractItems = new HashSet<MyContractItem>();
		Set<MyContract> contracts = new HashSet<MyContract>();
		Map<Long, Map<Long, Blueprint>> blueprintsMap = new HashMap<Long, Map<Long, Blueprint>>();
		Map<Long, Blueprint> blueprints = new HashMap<Long, Blueprint>();

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
			marketOrders.addAll(owner.getMarketOrders());
			//Journal
			journal.addAll(owner.getJournal());
			//Transactions
			transactions.addAll(owner.getTransactions());
			//Industry Jobs
			industryJobs.addAll(owner.getIndustryJobs());
			//Contracts
			contracts.addAll(owner.getContracts().keySet());
			//Contract Items
			for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
				MyContract contract = entry.getKey();
				if (entry.getValue().isEmpty() 
					&& contract.getType() == ContractType.COURIER &&
					( //XXX - Workaround for alien contracts
						uniqueOwners.containsKey(contract.getAcceptorID())
						|| uniqueOwners.containsKey(contract.getAssigneeID())
						|| uniqueOwners.containsKey(contract.getIssuerID())
						|| (contract.isForCorp() && uniqueOwners.containsKey(contract.getIssuerCorpID()))
					)
					) {
					contractItems.add(new MyContractItem(contract));
				} else if (!entry.getValue().isEmpty()) {
					contractItems.addAll(entry.getValue());
				}
			}
			//Blueprints (Newest)
			if (!owner.getBlueprints().isEmpty()) {
				Map<Long, Blueprint> map = blueprintsMap.get(owner.getOwnerID());
				if (map == null || (owner.getBlueprintsNextUpdate() != null && blueprintsNewest != null && owner.getBlueprintsNextUpdate().after(blueprintsNewest))) {
					blueprintsMap.put(owner.getOwnerID(), owner.getBlueprints());
					blueprintsNewest = owner.getBlueprintsNextUpdate();
				}
			}
			//Assets (Newest)
			if (!owner.getAssets().isEmpty()) {
				List<MyAsset> list = assetsMap.get(owner.getOwnerID());
				if (list == null || (owner.getAssetNextUpdate() != null && assetsNewest != null && owner.getAssetNextUpdate().after(assetsNewest))) {
					assetsMap.put(owner.getOwnerID(), owner.getAssets());
					assetsNewest = owner.getAssetNextUpdate();
				}
			}
			//Account Balance (Newest)
			if (!owner.getAccountBalances().isEmpty()) {
				List<MyAccountBalance> list = accountBalanceMap.get(owner.getOwnerID());
				if (list == null || (owner.getBalanceNextUpdate()!= null && accountBalanceNewest != null && owner.getBalanceNextUpdate().after(accountBalanceNewest))) {
					accountBalanceMap.put(owner.getOwnerID(), owner.getAccountBalances());
					accountBalanceNewest = owner.getBalanceNextUpdate();
				}
			}
		}

		//Fill accountBalance
		for (List<MyAccountBalance> list : accountBalanceMap.values()) {
			accountBalance.addAll(list);
		}

		//Fill blueprints
		for (Map<Long, Blueprint> map : blueprintsMap.values()) {
			blueprints.putAll(map);
		}

		//Update MarketOrders dynamic values
		for (MyMarketOrder order : marketOrders) {
			Item item = order.getItem();
			//Price
			double price = ApiIdConverter.getPrice(item.getTypeID(), false);
			order.setDynamicPrice(price);
			//Last Transaction
			if (order.getBid() > 0) { //Buy
				order.setLastTransaction(transactionPriceDataSell.get(order.getTypeID()));
			} else { //Sell
				order.setLastTransaction(transactionPriceDataBuy.get(order.getTypeID()));
			}
		}

		//Update IndustryJobs dynamic values
		for (MyIndustryJob industryJob : industryJobs) {
			Item itemType = industryJob.getItem();
			//Update Owners
			industryJob.setInstaller(ApiIdConverter.getOwnerName(industryJob.getInstallerID()));
			//Update BPO/BPC status
			Blueprint blueprint = blueprints.get(industryJob.getBlueprintID());
			industryJob.setBlueprint(blueprint);
			//Price
			double price = ApiIdConverter.getPrice(itemType.getTypeID(), true);
			industryJob.setDynamicPrice(price);
			double outputPrice = ApiIdConverter.getPrice(industryJob.getProductTypeID(), false);
			industryJob.setOutputPrice(outputPrice);
		}

		//Update Contracts Items dynamic values
		for (MyContractItem contractItem : contractItems) {
			Item item = contractItem.getItem();
			//Price
			double price = ApiIdConverter.getPrice(item.getTypeID(), contractItem.isBPC());
			contractItem.setDynamicPrice(price);
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
			contract.setStartStation(ApiIdConverter.getLocation(contract.getStartStation()));
			contract.setEndStation(ApiIdConverter.getLocation(contract.getEndStation()));
			//Update Owners
			contract.setAcceptor(ApiIdConverter.getOwnerName(contract.getAcceptorID()));
			contract.setAssignee(ApiIdConverter.getOwnerName(contract.getAssigneeID()));
			contract.setIssuerCorp(ApiIdConverter.getOwnerName(contract.getIssuerCorpID()));
			contract.setIssuer(ApiIdConverter.getOwnerName(contract.getIssuerID()));
		}

		//Update Items dynamic values
		for (Item item : StaticData.get().getItems().values()) {
			item.setPriceReprocessed(ApiIdConverter.getPriceReprocessed(item));
		}

		//Add Market Orders to Assets
		addAssets(ApiConverter.assetMarketOrder(marketOrders), assets, blueprints);

		//Add Industry Jobs to Assets
		addAssets(ApiConverter.assetIndustryJob(industryJobs), assets, blueprints);

		//Add Contract Items to Assets
		addAssets(ApiConverter.assetContracts(contractItems, uniqueOwners), assets, blueprints);

		//Add Assets to Assets
		for (List<MyAsset> list : assetsMap.values()) {
			addAssets(list, assets, blueprints);
		}
		
		//Update Jumps
		updateJumps(new ArrayList<JumpType>(assets), MyAsset.class);

		//Update Locations
		List<EditableLocationType> editableLocationTypes = new ArrayList<EditableLocationType>();
		editableLocationTypes.addAll(assets);
		editableLocationTypes.addAll(marketOrders);
		editableLocationTypes.addAll(transactions);
		editableLocationTypes.addAll(industryJobs);
		for (EditableLocationType editableLocationType : editableLocationTypes) {
			editableLocationType.setLocation(ApiIdConverter.getLocation(editableLocationType.getLocation()));
		}

		assetsList.clear();
		assetsList.addAll(assets);
		marketOrdersList.clear();
		marketOrdersList.addAll(marketOrders);
		journalList.clear();
		journalList.addAll(journal);
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
			contractEventList.getReadWriteLock().writeLock().lock();
			contractEventList.clear();
			contractEventList.addAll(contracts);
		} finally {
			contractEventList.getReadWriteLock().writeLock().unlock();
		}
		try {
			accountBalanceEventList.getReadWriteLock().writeLock().lock();
			accountBalanceEventList.clear();
			accountBalanceEventList.addAll(accountBalance);
		} finally {
			accountBalanceEventList.getReadWriteLock().writeLock().unlock();
		}
		//Sort Owners
		ownerNames.clear();
		ownerNames.addAll(uniqueOwnerNames);
		Collections.sort(ownerNames, new CaseInsensitiveComparator());
		owners.clear();
		owners.putAll(uniqueOwners);
		return saveSettings;
	}

	private void maximumPurchaseAge() {
		//Create Market Price Data
		marketPriceData = new HashMap<Integer, MarketPriceData>();
		//Date - maximumPurchaseAge in days
		Date maxAge = new Date(System.currentTimeMillis() - (Settings.get().getMaximumPurchaseAge() * 24 * 60 * 60 * 1000L));
		for (OwnerType owner : profileManager.getOwnerTypes()) {
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

	private void calcTransactionsPriceData() {
		//Create Transaction Price Data
		transactionPriceDataSell = new HashMap<Integer, MarketPriceData>();
		transactionPriceDataBuy = new HashMap<Integer, MarketPriceData>();
		//Date - maximumPurchaseAge in days
		for (OwnerType owner : profileManager.getOwnerTypes()) {
			for (MyTransaction transaction : owner.getTransactions()) {
				if (transaction.getTransactionType().equals("sell")) { //Sell
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
		data.update(transaction.getPrice(), transaction.getTransactionDateTime());
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
				Settings.lock("Asset Added Date"); //Lock for Asset Added
				Settings.get().getAssetAdded().put(asset.getItemID(), date);
				Settings.unlock("Asset Added Date"); //Unlock for Asset Added
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
			int typeID;
			if (asset.isBPC()) {
				typeID = -asset.getItem().getTypeID();
			} else {
				typeID = asset.getItem().getTypeID();
			}
			List<MyAsset> dup = uniqueAssetsDuplicates.get(typeID);
			if (dup == null) {
				dup = new ArrayList<MyAsset>();
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
			addTo.add(asset);
			//Add sub-assets
			addAssets(asset.getAssets(), addTo, blueprints);
		}
	}
}
