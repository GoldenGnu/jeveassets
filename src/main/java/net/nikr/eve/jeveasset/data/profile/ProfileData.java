/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.api.my.MyBlueprint;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyExtraction;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.api.my.MySkill;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob.IndustryJobStatus;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.Change;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.sde.RouteFinder;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.AddedData;
import net.nikr.eve.jeveasset.data.settings.MarketPriceData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.settings.types.EditablePriceType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.LastTransactionType;
import net.nikr.eve.jeveasset.data.settings.types.LocationsType;
import net.nikr.eve.jeveasset.gui.dialogs.settings.SoundsSettingsPanel.SoundOption;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.gui.sounds.SoundPlayer;
import net.nikr.eve.jeveasset.gui.tabs.orders.OutbidProcesser.OutbidProcesserOutput;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.DataConverter;

public class ProfileData {

	private final ProfileManager profileManager;

	private final EventList<MyContractItem> contractItemEventList = EventListManager.create();
	private final EventList<MyIndustryJob> industryJobsEventList = EventListManager.create();
	private final EventList<MyMarketOrder> marketOrdersEventList = EventListManager.create();
	private final EventList<MyJournal> journalEventList = EventListManager.create();
	private final EventList<MyTransaction> transactionsEventList = EventListManager.create();
	private final EventList<MyAsset> assetsEventList = EventListManager.create();
	private final EventList<MyAccountBalance> accountBalanceEventList = EventListManager.create();
	private final EventList<MyContract> contractEventList = EventListManager.create();
	private final EventList<MySkill> skillsEventList = EventListManager.create();
	private final EventList<MyMining> miningEventList = EventListManager.create();
	private final EventList<MyExtraction> extractionsEventList = EventListManager.create();
	private final List<MyContractItem> contractItemList = new ArrayList<>();
	private final List<MyIndustryJob> industryJobsList = new ArrayList<>();
	private final List<MyMarketOrder> marketOrdersList = new ArrayList<>();
	private final List<MyJournal> journalList = new ArrayList<>();
	private final List<MyTransaction> transactionsList = new ArrayList<>();
	private final List<MyAsset> assetsList = new ArrayList<>();
	private final List<MyAccountBalance> accountBalanceList = new ArrayList<>();
	private final List<MyContract> contractList = new ArrayList<>();
	private final Map<String, Long> skillPointsTotal = new HashMap<>();
	private Map<Integer, List<MyAsset>> uniqueAssetsDuplicates = null; //TypeID : int
	private Map<Integer, MarketPriceData> transactionSellPriceData; //TypeID : int
	private Map<Integer, MarketPriceData> transactionBuyPriceData; //TypeID : int
	private Map<Integer, Double> transactionBuyTax; //TypeID : int
	private Map<Long, Double> transactionSellTax; //TransactionID : long
	private Map<Long, Double> marketOrdersBrokersFee; //OrderID : long
	private final List<String> ownerNames = new ArrayList<>();
	private final Map<Long, OwnerType> owners = new HashMap<>();
	private Set<Integer> staticTypeIDs = null;

	public ProfileData(ProfileManager profileManager) {
		this.profileManager = profileManager;
		RouteFinder.load();
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

	public EventList<MySkill> getSkillsEventList() {
		return skillsEventList;
	}

	public EventList<MyMining> getMiningEventList() {
		return miningEventList;
	}

	public EventList<MyExtraction> getExtractionsEventList() {
		return extractionsEventList;
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

	public Map<String, Long> getSkillPointsTotal() {
		return skillPointsTotal;
	}

	public List<String> getOwnerNames(boolean all) {
		synchronized (ownerNames) { //synchronized as ownerNames are modified by updateEventLists
			List<String> sortedOwners = new ArrayList<>(ownerNames);
			if (all) {
				sortedOwners.add(0, General.get().all());
			}
			return sortedOwners;
		}
	}

	public Map<Long, OwnerType> getOwners() {
		synchronized (owners) { //synchronized as owners are modified by updateEventLists
			return new HashMap<>(owners);
		}
	}

	private Set<Integer> createPriceTypeIDs() {
		Set<Integer> priceTypeIDs = new HashSet<>();
		priceTypeIDs.add(40519); //Skill Extractor
		priceTypeIDs.add(40520); //Large Skill Injector
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
				if (industryJob.isManufacturing() && industryJob.isNotDeliveredToAssets() && industryJob.getProductTypeID() != null) {
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
			//Add Mining to uniqueIds
			for (MyMining mining : owner.getMining()) {
				Item item = mining.getItem();
				if (item.isMarketGroup()) {
					priceTypeIDs.add(item.getTypeID());
				}
			}
		}
		//Add StockpileItems to uniqueIds
		for (Stockpile stockpile : Settings.get().getStockpiles()) {
			for (StockpileItem stockpileItem : stockpile.getItems()) {
				if (stockpileItem.getItem().isMarketGroup()) {
					priceTypeIDs.add(stockpileItem.getTypeID());
				}
			}
		}
		//Add reprocessed and manufacturing items to the price queue
		if (staticTypeIDs == null) {
			staticTypeIDs = new HashSet<>();
			for (Item item : StaticData.get().getItems().values()) {
				for (ReprocessedMaterial reprocessedMaterial : item.getReprocessedMaterial()) {
					int typeID = reprocessedMaterial.getTypeID();
					Item reprocessedItem = ApiIdConverter.getItem(typeID);
					if (reprocessedItem.isMarketGroup()) {
						staticTypeIDs.add(typeID);
					}
				}
				for (IndustryMaterial industryMaterial : item.getManufacturingMaterials()) {
					int typeID = industryMaterial.getTypeID();
					Item reprocessedItem = ApiIdConverter.getItem(typeID);
					if (reprocessedItem.isMarketGroup()) {
						staticTypeIDs.add(typeID);
					}
				}
			}
		}
		priceTypeIDs.addAll(staticTypeIDs);
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

	public void updateMarketOrders(OutbidProcesserOutput output) {
		Date addedDate = new Date();
		Map<Long, Date> marketOrdersAdded = AddedData.getMarketOrders().getAll();
		synchronized (owners) { //synchronized as owners are modified by updateEventLists
			for (OwnerType ownerType : owners.values()) {
				for (MyMarketOrder order : ownerType.getMarketOrders()) { // getMarketOrders() is thread safe
					order.setOutbid(output.getOutbids().get(order.getOrderID()));
					boolean updated = order.addChanges(output.getUpdates().get(order.getOrderID()));
					if (updated) { //If Market Order have been updated
						order.setChanged(AddedData.getMarketOrders().getPut(marketOrdersAdded, order.getOrderID(), addedDate));
					}
				}
			}
		}
		updateOutbidOwned(marketOrdersList);
		AddedData.getMarketOrders().commitQueue();
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					marketOrdersEventList.getReadWriteLock().writeLock().lock();
					List<MyMarketOrder> cache = new ArrayList<>(marketOrdersEventList);
					marketOrdersEventList.clear();
					marketOrdersEventList.addAll(cache);
				} finally {
					marketOrdersEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
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
		//Update Items dynamic values
		for (Item item : StaticData.get().getItems().values()) {
			double before = item.getPriceReprocessed();
			double price = ApiIdConverter.getPriceReprocessed(item);
			item.setPriceReprocessed(price);
			double beforeMax = item.getPriceReprocessedMax();
			double priceMax = ApiIdConverter.getPriceReprocessedMax(item);
			item.setPriceReprocessedMax(priceMax);
			double beforeManufacturing = item.getPriceManufacturing();
			double priceManufacturing = ApiIdConverter.getPriceManufacturing(item);
			item.setPriceManufacturing(priceManufacturing);
			if (before != price || beforeMax != priceMax || beforeManufacturing != priceManufacturing) {
				typeIDs.add(item.getTypeID());
			}
		}
		updatePrices(marketOrdersEventList, typeIDs);
		updatePrices(contractItemEventList, typeIDs);
		updatePrices(miningEventList, typeIDs);
		updatePrices(assetsEventList, typeIDs);
		updateIndustryJobPrices(industryJobsEventList, typeIDs);
	}

	public void updateEventLists() {
		updateEventLists(new Date());
	}

	public synchronized void updateEventLists(Date addedDate) {
		uniqueAssetsDuplicates = new HashMap<>();
		Set<String> uniqueOwnerNames = new HashSet<>();
		Map<Long, OwnerType> uniqueOwners = new HashMap<>();
		//Temp
		List<MyAsset> assets = new ArrayList<>();
		List<MyAccountBalance> accountBalance = new ArrayList<>();
		Map<Long, OwnerType> assetsMap = new HashMap<>();
		Map<Long, OwnerType> accountBalanceMap = new HashMap<>();
		Map<Long, MyIndustryJob> copyIndustryJobs = new HashMap<>();
		Set<MyMarketOrder> marketOrders = new HashSet<>();
		Set<MyMarketOrder> charMarketOrders = new HashSet<>();
		Set<MyJournal> journals = new HashSet<>();
		Set<MyTransaction> transactions = new HashSet<>();
		Set<MyTransaction> charTransactions = new HashSet<>();
		Set<MyIndustryJob> industryJobs = new HashSet<>();
		Set<MyContractItem> contractItems = new HashSet<>();
		Set<MyContract> contracts = new HashSet<>();
		Set<MySkill> skills = new HashSet<>();
		Set<MyMining> minings = new HashSet<>();
		Set<MyExtraction> extractions = new HashSet<>();
		Map<Long, OwnerType> blueprintsMap = new HashMap<>();
		Map<Long, MyBlueprint> blueprints = new HashMap<>();
		Map<String, Long> skillPointsTotalCache = new HashMap<>();

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
			if (owner.isCorporation()) {
				transactions.addAll(owner.getTransactions());
			} else {
				charTransactions.addAll(owner.getTransactions());
			}
			//Industry Jobs > MyBlueprint
			industryJobs.addAll(owner.getIndustryJobs());
			for (MyIndustryJob myIndustryJob : owner.getIndustryJobs()) {
				blueprints.put(myIndustryJob.getBlueprintID(), new MyBlueprint(myIndustryJob));
				if (myIndustryJob.isCopying()) {
					blueprints.put(myIndustryJob.getJobID().longValue(), new MyBlueprint(myIndustryJob));
					copyIndustryJobs.put(myIndustryJob.getBlueprintID(), myIndustryJob);
				}
			}
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
				for (MyContractItem contractItem : entry.getValue()) {
					MyBlueprint blueprint = contractItem.getBlueprint();
					Long itemID = contractItem.getItemID();
					if (blueprint != null) {
						if (itemID != null) {
							blueprints.put(itemID, blueprint);
						}
						blueprints.put(contractItem.getRecordID(), blueprint);
					}
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
			//Skills
			skills.addAll(owner.getSkills());
			if (owner.getTotalSkillPoints() != null) {
				if (owner.getUnallocatedSkillPoints() != null) {
					skillPointsTotalCache.put(owner.getOwnerName(), owner.getTotalSkillPoints() + owner.getUnallocatedSkillPoints());
				} else {
					skillPointsTotalCache.put(owner.getOwnerName(), owner.getTotalSkillPoints());
				}
			}
			//Mining
			minings.addAll(owner.getMining());
			//Extractions
			extractions.addAll(owner.getExtractions());
		}

		//Fill accountBalance
		for (OwnerType owner : accountBalanceMap.values()) {
			accountBalance.addAll(owner.getAccountBalances());
		}

		//RawBlueprint > MyBlueprint
		for (OwnerType owner : blueprintsMap.values()) {
			for (Map.Entry<Long, RawBlueprint> entry : owner.getBlueprints().entrySet()) {
				blueprints.put(entry.getKey(), new MyBlueprint(entry.getValue())); //Best source - overwrite other sources
				//Copy Industry Jobs
				MyIndustryJob industryJob = copyIndustryJobs.get(entry.getKey());
				if (industryJob != null) {
					blueprints.put(industryJob.getJobID().longValue(), new MyBlueprint(industryJob.getLicensedRuns(), entry.getValue().getMaterialEfficiency(), entry.getValue().getTimeEfficiency()));
				}
			}
		}
		//Prioritize corp market orders over char
		for (MyMarketOrder marketOrder : charMarketOrders) {
			if (!marketOrder.isCorp()) { //Remove non-corporation orders
				marketOrders.remove(marketOrder);
			}
			marketOrders.add(marketOrder);
		}
		//Prioritize corp transactions over char
		for (MyTransaction transaction : charTransactions) {
			if (!transaction.isCorporation()) { //Remove non-corporation orders
				transactions.remove(transaction);
			}
			transactions.add(transaction);
		}
		//Update MarketOrders dynamic values
		Map<Long, Date> marketOrdersAdded = AddedData.getMarketOrders().getAll();
		for (MyMarketOrder order : marketOrders) {
			//Last Transaction
			if (order.isBuyOrder()) { //Buy
				setLastTransaction(order, order.getTypeID(), order.isBuyOrder(), order.getPrice(), null);
			} else { //Sell
				setLastTransaction(order, order.getTypeID() , order.isBuyOrder(), order.getPrice(), null);
			}
			order.setIssuedByName(ApiIdConverter.getOwnerName(order.getIssuedBy()));
			order.setBrokersFee(marketOrdersBrokersFee.get(order.getOrderID()));
			order.setOutbid(Settings.get().getMarketOrdersOutbid().get(order.getOrderID()));
			//Update Owned
			Integer issuedBy = order.getIssuedBy();
			if (order.isCorporation() && issuedBy != null) {
				order.setOwned(uniqueOwners.containsKey((long) issuedBy));
			}
			//Price Data
			order.setPriceData(ApiIdConverter.getPriceData(order.getTypeID(), false));
			//Changed date
			if (order.isUpdateChanged()) { //Update!
				order.setChanged(AddedData.getMarketOrders().getPut(marketOrdersAdded, order.getOrderID(), addedDate));
			} else {
				Date changed;
				if (!marketOrdersAdded.containsKey(order.getOrderID())) { //New (use issued as a best guess)
					changed = order.getIssued();
				} else { //Updating
					changed = addedDate;
				}
				order.setChanged(AddedData.getMarketOrders().getAdd(marketOrdersAdded, order.getOrderID(), changed));
			}
		}
		updateOutbidOwned(marketOrders);
		AddedData.getMarketOrders().commitQueue();
		//Update IndustryJobs dynamic values
		for (MyIndustryJob industryJob : industryJobs) {
			//Update Owners
			industryJob.setInstaller(ApiIdConverter.getOwnerName(industryJob.getInstallerID()));
			industryJob.setCompletedCharacter(ApiIdConverter.getOwnerName(industryJob.getCompletedCharacterID()));
			//Update Owned
			if (industryJob.getOwner().isCorporation()) {
				industryJob.setOwned(uniqueOwners.containsKey(industryJob.getInstallerID()));
			}
			//Update BPO/BPC status
			industryJob.setBlueprint(blueprints.get(industryJob.getBlueprintID()));
			//Price
			updatePrice(industryJob);
			//Queue Sound
			if (industryJob.getStatus() == IndustryJobStatus.ACTIVE) {
				SoundPlayer.playAt(industryJob.getEndDate(), SoundOption.INDUSTRY_JOB_COMPLETED);
			}
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
			//Update Owned
			if (contract.isForCorp()) {
				contract.setOwned(uniqueOwners.containsKey(contract.getIssuerID()));
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
		Map<Long, Date> transactionsAdded = AddedData.getTransactions().getAll();
		for (MyTransaction transaction : transactions) {
			//Client Name
			transaction.setClientName(ApiIdConverter.getOwnerName(transaction.getClientID()));
			//Tax
			if (transaction.isBuy()) { //Buy
				transaction.setTax(null); //Seller pays the tax
			} else { //Sell
				transaction.setTax(transactionSellTax.get(transaction.getTransactionID()));
			}
			//Transaction Profit
			if (transaction.isBuy()) { //Buy
				setLastTransaction(transaction, transaction.getTypeID(), transaction.isBuy(), transaction.getPrice(), transactionBuyTax.get(transaction.getTypeID()));
			} else { //Sell
				double tax = 0;
				if (transaction.getTax() != null) {
					tax = transaction.getTax() / transaction.getItemCount();
				}
				setLastTransaction(transaction, transaction.getTypeID(), transaction.isBuy(), transaction.getPrice(), tax);
			}
			//Date added
			transaction.setAdded(AddedData.getTransactions().getAdd(transactionsAdded, transaction.getTransactionID(), addedDate));
		}
		AddedData.getTransactions().commitQueue();
		//Update Journal dynamic values
		Map<Long, Date> journalsAdded = AddedData.getJournals().getAll();
		for (MyJournal journal : journals) {
			//Names
			journal.setFirstPartyName(ApiIdConverter.getOwnerName(journal.getFirstPartyID()));
			journal.setSecondPartyName(ApiIdConverter.getOwnerName(journal.getSecondPartyID()));
			//Date added
			journal.setAdded(AddedData.getJournals().getAdd(journalsAdded, journal.getRefID(), addedDate));
			//Context
			journal.setContext(ApiIdConverter.getContext(journal));
		}
		AddedData.getJournals().commitQueue();

		//Update Mining dynamic values
		for (MyMining mining : minings) {
			mining.setCharacterName(ApiIdConverter.getOwnerName(mining.getCharacterID()));
			if (mining.getCorporationID() != null && mining.getCorporationName() == null) {
				mining.setCorporationName(ApiIdConverter.getOwnerName(mining.getCorporationID()));
			}
		}

		//Update Items dynamic values
		for (Item item : StaticData.get().getItems().values()) {
			item.setPriceReprocessed(ApiIdConverter.getPriceReprocessed(item));
			item.setPriceReprocessedMax(ApiIdConverter.getPriceReprocessedMax(item));
			item.setPriceManufacturing(ApiIdConverter.getPriceManufacturing(item));
		}

		Map<Long, Date> assetAdded = AddedData.getAssets().getAll();
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				//Add Market Orders to Assets
				addAssets(DataConverter.assetMarketOrder(marketOrders, Settings.get().isIncludeSellOrders(), Settings.get().isIncludeBuyOrders()), assets, blueprints, assetAdded, addedDate);

				//Add Industry Jobs to Assets
				addAssets(DataConverter.assetIndustryJob(industryJobs, Settings.get().isIncludeManufacturing(), Settings.get().isIncludeCopying()), assets, blueprints, assetAdded, addedDate);

				//Add Contract Items to Assets
				addAssets(DataConverter.assetContracts(contractItems, uniqueOwners, Settings.get().isIncludeSellContracts(), Settings.get().isIncludeBuyContracts()), assets, blueprints, assetAdded, addedDate);

				//Add Assets to Assets
				for (OwnerType owner : assetsMap.values()) {
					addAssets(owner.getAssets(), assets, blueprints, assetAdded, addedDate);
				}
			}
		});
		AddedData.getAssets().commitQueue();

		//Update Locations
		List<EditableLocationType> editableLocationTypes = new ArrayList<>();
		editableLocationTypes.addAll(assets);
		editableLocationTypes.addAll(marketOrders);
		editableLocationTypes.addAll(transactions);
		editableLocationTypes.addAll(industryJobs);
		editableLocationTypes.addAll(minings);
		editableLocationTypes.addAll(extractions);
		for (EditableLocationType editableLocationType : editableLocationTypes) {
			editableLocationType.setLocation(ApiIdConverter.getLocation(editableLocationType.getLocationID()));
		}
		//Update Prices
		List<EditablePriceType> editablePriceTypes = new ArrayList<>();
		editablePriceTypes.addAll(marketOrders);
		editablePriceTypes.addAll(contractItems);
		editablePriceTypes.addAll(minings);
		for (EditablePriceType editablePriceType : editablePriceTypes) {
			updatePrice(editablePriceType);
		}

		//Owners - Before EventList update - in case owners are referanced in any ListEventListeners
		synchronized (ownerNames) { //synchronized as ownerNames are modified (here) by updateEventLists
			ownerNames.clear();
			ownerNames.addAll(uniqueOwnerNames);
		}
		Collections.sort(ownerNames, new CaseInsensitiveComparator());
		synchronized (owners) { //synchronized as owners are modified (here) by updateEventLists
			owners.clear();
			owners.putAll(uniqueOwners);
		}
		//Update Lists
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
		skillPointsTotal.clear();
		skillPointsTotal.putAll(skillPointsTotalCache);
		//Update EventLists
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
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					skillsEventList.getReadWriteLock().writeLock().lock();
					skillsEventList.clear();
					skillsEventList.addAll(skills);
				} finally {
					skillsEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					miningEventList.getReadWriteLock().writeLock().lock();
					miningEventList.clear();
					miningEventList.addAll(minings);
				} finally {
					miningEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				try {
					extractionsEventList.getReadWriteLock().writeLock().lock();
					extractionsEventList.clear();
					extractionsEventList.addAll(extractions);
				} finally {
					extractionsEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		});
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
				if (typeIDs.contains(getTypeID(t.isBPC(), t.getItem().getTypeID()))) {
					found.add(t); //Save for update
					updatePrice(t);
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		updateList(eventList, found);
	}

	private void updateOutbidOwned(Collection<MyMarketOrder> marketOrders) {
		Map<Integer, Set<Long>> lowestBuy = new HashMap<>();
		Map<Integer, Set<Long>> lowestSell = new HashMap<>();
		for (MyMarketOrder order : marketOrders) { //Find lowest
			if (order.isActive() && order.haveOutbid() && !order.isOutbid()) { //Lowest
				Map<Integer, Set<Long>> lowest;
				if (order.isBuyOrder()) {
					lowest = lowestBuy;
				} else {
					lowest = lowestSell;
				}
				Set<Long> orderIDs = lowest.get(order.getTypeID());
				if (orderIDs == null) {
					orderIDs = new HashSet<>();
					lowest.put(order.getTypeID(), orderIDs);
				}
				orderIDs.add(order.getOrderID());
			}
		}
		for (MyMarketOrder order : marketOrders) { //Set owned
			if (!order.isActive()) {
				order.setOutbidOwned(false);
				continue;
			}
			Map<Integer, Set<Long>> lowest;
			if (order.isBuyOrder()) {
				lowest = lowestBuy;
			} else {
				lowest = lowestSell;
			}
			Set<Long> orderIDs = lowest.get(order.getTypeID());
			order.setOutbidOwned(orderIDs != null && !orderIDs.contains(order.getOrderID())); //Not null and not one of the lowest orders
		}
	}

	private void updateIndustryJobPrices(EventList<MyIndustryJob> eventList, Set<Integer> typeIDs) {
		if (typeIDs == null || typeIDs.isEmpty()) {
			return;
		}
		List<MyIndustryJob> found = new ArrayList<>();
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (MyIndustryJob industryJob : eventList) {
				Integer productTypeID = industryJob.getProductTypeID();
				if (typeIDs.contains(getTypeID(industryJob.isBPC(), industryJob.getItem().getTypeID()))
						|| (productTypeID != null && typeIDs.contains(getTypeID(industryJob.isCopying(), productTypeID)))) {
					found.add(industryJob); //Save for update
					updatePrice(industryJob); //Update data
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		updateList(eventList, found);
	}

	private static int getTypeID(boolean bpc, int typeID) {
		if (bpc) {
			return -typeID;
		} else {
			return typeID;
		}
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

	private void calcTransactionsPriceData() {
		//Create Transaction Price Data
		transactionBuyTax = new HashMap<>();
		transactionSellPriceData = new HashMap<>();
		transactionBuyPriceData = new HashMap<>();
		transactionSellTax = new HashMap<>();
		marketOrdersBrokersFee = new HashMap<>();
		Date lastTaxDate = null;
		Date maxAge = new Date(System.currentTimeMillis() - ((long)Settings.get().getMaximumPurchaseAge() * 24L * 60L * 60L * 1000L));
		for (OwnerType owner : profileManager.getOwnerTypes()) {
			//Journal
			Map<Date, List<Double>> taxes = new HashMap<>();
			Map<Date, List<Double>> fees = new HashMap<>();
			for (MyJournal journal : owner.getJournal()) {
				if (journal.getRefType() == RawJournalRefType.TRANSACTION_TAX) {
					List<Double> list = taxes.get(journal.getDate());
					if (list == null) {
						list = new ArrayList<>();
						taxes.put(journal.getDate(), list);
					}
					list.add(journal.getAmount());
				}
				if (journal.getRefType() == RawJournalRefType.BROKERS_FEE) {
					List<Double> list = fees.get(journal.getDate());
					if (list == null) {
						list = new ArrayList<>();
						fees.put(journal.getDate(), list);
					}
					list.add(journal.getAmount());
				}
			}
			//Transactions
			Map<Date, List<MyTransaction>> transactions = new HashMap<>();
			for (MyTransaction transaction : owner.getTransactions()) {
				if (transaction.isSell()) {
					List<MyTransaction> list = transactions.get(transaction.getDate());
					if (list == null) {
						list = new ArrayList<>();
						transactions.put(transaction.getDate(), list);
					}
					list.add(transaction);
				}
				if (transaction.getDate().before(maxAge) && Settings.get().getMaximumPurchaseAge() != 0) {
					continue; //Date out of range and not unlimited
				}
				if (transaction.isSell()) { //Sell
					createTransactionsPriceData(transactionSellPriceData, transaction);
				} else { //Buy
					createTransactionsPriceData(transactionBuyPriceData, transaction);
				}
			}
			//Tax
			for (Map.Entry<Date, List<MyTransaction>> entry : transactions.entrySet()) {
				List<Double> list = taxes.get(entry.getKey());
				if (list == null) {
					continue;
				}
				List<Match<MyTransaction>> matchs = new ArrayList<>();
				for (MyTransaction transaction : entry.getValue()) {
					double expected = transaction.getQuantity() * transaction.getPrice() / 100.0 * 5.0; //5%
					for (Double tax : list) {
						double diff = Math.abs(tax + expected); //Tax is negative and expected is possitive: Add them together for diff
						matchs.add(new Match<>(transaction, tax, diff));
					}
				}
				Collections.sort(matchs);
				Set<MyTransaction> found = new HashSet<>();
				for (Match<MyTransaction> match : matchs) {
					MyTransaction transaction = match.get();
					if (!found.contains(transaction) && found.size() <= list.size()) {
						found.add(transaction);
						double tax = match.getAmount();
						transactionSellTax.put(transaction.getTransactionID(), tax);
						if ((lastTaxDate == null || lastTaxDate.before(transaction.getDate()))) {
							transactionBuyTax.put(transaction.getTypeID(), tax / transaction.getItemCount());
							lastTaxDate = transaction.getDate();
						}
					}
				}
			}
			//Market Orders
			Map<Date, List<MyMarketOrder>> marketOrders = new HashMap<>();
			for (MyMarketOrder marketOrder : owner.getMarketOrders()) {
				for (Change change : marketOrder.getChanges()) {
					Date date = change.getDate();
					List<MyMarketOrder> list = marketOrders.get(date);
					if (list == null) {
						list = new ArrayList<>();
						marketOrders.put(date, list);
					}
					list.add(marketOrder);
				}
			}
			//Fees
			for (Map.Entry<Date, List<MyMarketOrder>> entry : marketOrders.entrySet()) {
				List<Double> list = fees.get(entry.getKey());
				if (list == null) {
					continue;
				}
				List<Match<MyMarketOrder>> matchs = new ArrayList<>();
				for (MyMarketOrder marketOrder : entry.getValue()) {
					double expected = Math.max(marketOrder.getVolumeTotal() * marketOrder.getPrice() / 100.0 * 5.0, 100); //5% or 100isk
					for (Double fee : list) {
						double diff = Math.abs(fee + expected); //Fee is negative and expected is possitive: Add them together for diff
						matchs.add(new Match<>(marketOrder, fee, diff));
					}
				}
				Collections.sort(matchs);
				Set<MyMarketOrder> found = new HashSet<>();
				for (Match<MyMarketOrder> match : matchs) {
					MyMarketOrder marketOrder = match.get();
					if (!found.contains(marketOrder) && found.size() <= list.size()) {
						found.add(marketOrder);
						Double fee = marketOrdersBrokersFee.get(marketOrder.getOrderID());
						if (fee == null) {
							fee = 0.0;
						}
						fee = fee + match.getAmount();
						marketOrdersBrokersFee.put(marketOrder.getOrderID(), fee);
					}
				}
			}
		}
	}

	private void createTransactionsPriceData(Map<Integer, MarketPriceData> transactionPriceData, MyTransaction transaction) {
		int typeID = transaction.getTypeID();
		MarketPriceData data = transactionPriceData.get(typeID);
		if (data == null) {
			data = new MarketPriceData();
			transactionPriceData.put(typeID, data);
		}
		data.update(transaction.getPrice(), transaction.getItemCount(), transaction.getDate());
	}

	public Double getTransactionAveragePrice(int typeID) {
		MarketPriceData buy = transactionBuyPriceData.get(typeID);
		MarketPriceData sell = transactionSellPriceData.get(typeID);
		if (buy != null && sell != null) {
			return MarketPriceData.getAverage(buy, sell);
		} else if (buy != null) {
			return buy.getAverage();
		} else if (sell != null) {
			return sell.getAverage();
		} else {
			return null;
		}
	}

	private void setLastTransaction(LastTransactionType item, int typeID, boolean buy, double price, Double tax) {
		if (tax == null) {
			tax = 0.0;
		}
		MarketPriceData marketPriceData;
		if (buy) { //Buy
			marketPriceData = transactionSellPriceData.get(typeID);
		} else { //Sell
			marketPriceData = transactionBuyPriceData.get(typeID);
		}
		if (marketPriceData != null) {
			double transactionPrice;
			switch (Settings.get().getTransactionProfitPrice()) {
				case AVERAGE:
					transactionPrice = marketPriceData.getAverage();
					break;
				case LASTEST:
					transactionPrice = marketPriceData.getLatest();
					break;
				case MAXIMUM:
					transactionPrice = marketPriceData.getMaximum();
					break;
				case MINIMUM:
					transactionPrice = marketPriceData.getMinimum();
					break;
				default:
					transactionPrice = marketPriceData.getLatest();
			}
			if (buy) { //Buy
				transactionPrice = transactionPrice + tax;
				item.setTransactionPrice(transactionPrice);
				item.setTransactionProfit(transactionPrice - price);
				item.setTransactionProfitPercent(Percent.create(transactionPrice / price));
			} else { //Sell
				price = price + tax;
				item.setTransactionPrice(transactionPrice);
				item.setTransactionProfit(price - (transactionPrice));
				item.setTransactionProfitPercent(Percent.create(price / (transactionPrice)));
			}
		} else {
			item.setTransactionPrice(0);
			item.setTransactionProfit(0);
			item.setTransactionProfitPercent(Percent.create(0));
		}
	}

	private void addAssets(final List<MyAsset> assets, List<MyAsset> addTo, Map<Long, MyBlueprint> blueprints, Map<Long, Date> assetAdded, Date assetAddedDate) {
		for (MyAsset asset : assets) {
			//XXX Ignore 9e18 locations: https://github.com/ccpgames/esi-issues/issues/684
			if (asset.getLocationID() > 9000000000000000000L) {
				continue;
			}
			//Handle Asset Structures
			if (asset.getItem().getCategory().equals(Item.CATEGORY_STRUCTURE)) {
				for (MyAsset childAsset: asset.getAssets()) {
					updateStructureAssets(childAsset, asset);
				}
			}
			//Blueprint
			asset.setBlueprint(blueprints.get(asset.getItemID()));
			//Tags
			Tags tags = Settings.get().getTags(asset.getTagID());
			asset.setTags(tags);
			//Date added
			asset.setAdded(AddedData.getAssets().getAdd(assetAdded, asset.getItemID(), assetAddedDate));
			//Price
			updatePrice(asset);
			//Market price
			asset.setMarketPriceData(transactionBuyPriceData.get(asset.getItem().getTypeID()));
			//User Item Names
			updateName(asset);
			//Contaioner
			updateContainer(asset);
			//Price data
			asset.setPriceData(ApiIdConverter.getPriceData(asset.getItem().getTypeID(), asset.isBPC()));
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
			//Add asset
			if (asset.getTypeID() != 27) { //Ignore offices
				addTo.add(asset);
			} else { //Office
				asset.setLocation(ApiIdConverter.getLocation(asset.getLocationID()));
			}
			//Add sub-assets
			addAssets(asset.getAssets(), addTo, blueprints, assetAdded, assetAddedDate);
		}
	}

	private static void updatePrice(EditablePriceType editablePriceType) {
		editablePriceType.setDynamicPrice(ApiIdConverter.getPrice(editablePriceType.getItem().getTypeID(), editablePriceType.isBPC()));
	}

	private static void updatePrice(MyIndustryJob industryJob) {
		industryJob.setDynamicPrice(ApiIdConverter.getPrice(industryJob.getItem().getTypeID(), industryJob.isBPC()));
		industryJob.setOutputPrice(ApiIdConverter.getPrice(industryJob.getProductTypeID(), industryJob.isCopying()));
	}

	private static void updatePrice(MyAsset asset) {
		//User price
		if (asset.getItem().isBlueprint() && !asset.isBPO()) { //Blueprint Copy
			asset.setUserPrice(Settings.get().getUserPrices().get(-asset.getItem().getTypeID()));
		} else { //All other
			asset.setUserPrice(Settings.get().getUserPrices().get(asset.getItem().getTypeID()));
		}
		//Dynamic Price
		asset.setDynamicPrice(ApiIdConverter.getPrice(asset.getItem().getTypeID(), asset.isBPC()));
	}

	private void updateName(MyAsset asset) {
		asset.setName(Settings.get().getUserItemNames().get(asset.getItemID()), Settings.get().getEveNames().get(asset.getItemID()));
	}

	private void updateContainerChildren(List<MyAsset> found, List<MyAsset> assets) {
		for (MyAsset asset : assets) {
			found.add(asset);
			updateContainer(asset);
			updateContainerChildren(found, asset.getAssets());
		}
	}

	private void updateContainer(MyAsset asset) {
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
			}
		}
		asset.setContainer(builder.toString().intern());
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

	private static class Match<T> implements Comparable<Match<T>>{
		private final T t;
		private final double amount;
		private final double diff;

		public Match(T t, double amount, double diff) {
			this.t = t;
			this.amount = amount;
			this.diff = diff;
		}

		public T get() {
			return t;
		}

		public double getAmount() {
			return amount;
		}

		public Double getDiff() {
			return diff;
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 41 * hash + Objects.hashCode(this.t);
			hash = 41 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
			hash = 41 * hash + (int) (Double.doubleToLongBits(this.diff) ^ (Double.doubleToLongBits(this.diff) >>> 32));
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Match<?> other = (Match<?>) obj;
			if (Double.doubleToLongBits(this.amount) != Double.doubleToLongBits(other.amount)) {
				return false;
			}
			if (Double.doubleToLongBits(this.diff) != Double.doubleToLongBits(other.diff)) {
				return false;
			}
			if (!Objects.equals(this.t, other.t)) {
				return false;
			}
			return true;
		}

		@Override
		public int compareTo(Match<T> match) {
			return this.getDiff().compareTo(match.getDiff());
		}
	}
}
