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
package net.nikr.eve.jeveasset.data.settings;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLog;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogData.LogType;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogSource;
import net.nikr.eve.jeveasset.gui.tabs.log.LogSourceType;
import net.nikr.eve.jeveasset.gui.tabs.log.LogData;
import net.nikr.eve.jeveasset.gui.tabs.log.LogSource;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.local.LogsReader;
import net.nikr.eve.jeveasset.io.local.LogsWriter;


public class LogManager {
	private static List<AssetLogSource> logs =  null;
	private static LogData logData = null;

	private LogManager() { }

	public static synchronized List<AssetLogSource> getList() {
		if (logs == null) {
			calculateLog();
		}
		return Collections.unmodifiableList(logs);
	}

	public static LogData getLogData() {
		if (logData == null) {
			logData = new LogData();
			LogsReader.load(logData);
		}
		return logData;
	}

	private static void save() {
		LogsWriter.save(getLogData());
	}

	public static void createLog(List<MyContract> oldContracts, List<MyIndustryJob> oldIndustryJobs, List<MyMarketOrder> oldMarketOrders, List<MyAsset> oldAssets, Date start, ProfileData profileData) {
		if (oldAssets.isEmpty()) {
			return;
		}
		if (getLogData().isEmpty()) {
			getLogData().getAddedClaims().put(start, new HashMap<>());
			getLogData().getRemovedClaims().put(start, new HashMap<>());
		}
		Date end = new Date();
	//Lookup tables
		Map<Long, AssetLog> oldAssetsMap = new HashMap<>();
		Map<Long, AssetLog> newAssetsMap = new HashMap<>();
		Map<Integer, MyContract> oldContractMap = new HashMap<>();
		Map<Integer, MyIndustryJob> oldIndustryJobsMap = new HashMap<>();
		Map<Long, MyMarketOrder> oldMarketOrdersMap = new HashMap<>();
		for (MyAsset asset : oldAssets) {
			if (asset.isGenerated()) {
				continue;
			}
			if (asset.getFlag().equals(General.get().industryJobFlag())) {
				continue;
			}
			oldAssetsMap.put(asset.getItemID(), new AssetLog(asset, end));
		}
		for (MyAsset asset : profileData.getAssetsList()) {
			if (asset.isGenerated()) {
				continue;
			}
			if (asset.getFlag().equals(General.get().industryJobFlag())) {
				continue;
			}
			newAssetsMap.put(asset.getItemID(), new AssetLog(asset, end));
		}
		
		for (MyContract contract : oldContracts) {
			oldContractMap.put(contract.getContractID(), contract);
		}
		for (MyIndustryJob myIndustryJob : oldIndustryJobs) {
			oldIndustryJobsMap.put(myIndustryJob.getJobID(), myIndustryJob);
		}
		for (MyMarketOrder myMarketOrder : oldMarketOrders) {
			oldMarketOrdersMap.put(myMarketOrder.getOrderID(), myMarketOrder);
		}
	//New
		//Added Assets
		Map<Long, AssetLog> added = new HashMap<Long, AssetLog>(newAssetsMap); //New Assets
		added.keySet().removeAll(oldAssetsMap.keySet()); //Removed Old Assets
		//Removed Assets
		Map<Long, AssetLog> removed = new HashMap<Long, AssetLog>(oldAssetsMap); //Old Assets
		removed.keySet().removeAll(newAssetsMap.keySet()); //Remove New Assets
		//Moved
		Map<Long, AssetLog> same = new HashMap<Long, AssetLog>(oldAssetsMap); //Old Assets
		same.keySet().retainAll(newAssetsMap.keySet()); //Assets in both New and Old (retain)

		Map<Integer, Set<LogSource>> addedSources = getLogData().getAddedSources();
		Map<Integer, List<AssetLog>> addedClaims = new HashMap<>();
		Map<Integer, Set<LogSource>> removedSources = getLogData().getRemovedSources();
		Map<Integer, List<AssetLog>> removedClaims = new HashMap<>();

		//Moved Claims/Soruces
		for (Long itemID : same.keySet()) {
			AssetLog from = oldAssetsMap.get(itemID);
			AssetLog to = newAssetsMap.get(itemID);
			boolean owner = !Objects.equal(from.getOwnerID(), to.getOwnerID()) ; //New Owner
			boolean location = !Objects.equal(from.getLocationID(), to.getLocationID());  //New Location
			boolean flag =  !Objects.equal(from.getFlagID(), to.getFlagID()); //New Flag
			boolean container = !Objects.equal(from.getParentIDs(), to.getParentIDs()); //New Container
			if (location || flag || container || owner) { //Moved same itemID
				putSet(addedSources, from.getTypeID(), new LogSource(LogSourceType.MOVED_FROM, from.getNeed(), from));
				added.put(to.getItemID(), to);
			}
			if (from.getNeed() > to.getNeed()) { //Removed from stack
				removed.put(from.getItemID(), new AssetLog(from, end, from.getNeed() - to.getNeed()));
			}
		}

		//Added Claims
		for (AssetLog asset : added.values()) {
			put(addedClaims, asset.getTypeID(), asset);
		}
		//Added Sources
		addedMarketOrderCancelled(addedSources, end, profileData.getMarketOrdersList(), oldMarketOrdersMap);
		addedContractsCancelled(addedSources, end, profileData.getContractItemList(), oldContractMap);
		addedIndustryJobsDelivered(addedSources, end, profileData.getIndustryJobsList(), oldIndustryJobsMap);
		addedTransactionsBought(addedSources, profileData.getTransactionsList());
		addedContracts(addedSources, profileData.getContractItemList());

		//Removed Claims
		for (AssetLog asset : removed.values()) {
			put(removedClaims, asset.getTypeID(), asset);
		}
		//Removed Sources
		removedSellMarketOrderCreated(removedSources, profileData.getMarketOrdersList());
		removedContracts(removedSources, profileData.getContractItemList());
		removedIndustryJobsCreated(removedSources, profileData.getIndustryJobsList());

		//Add claim data
		if (!addedClaims.isEmpty() || !removedClaims.isEmpty()) { //Only add claim point if something was changed
			getLogData().getAddedClaims().put(end, addedClaims);
			getLogData().getRemovedClaims().put(end, removedClaims);
		}
		save(); //Save data
		calculateLog(); //Re-calculate with new data
	}

	private static void calculateLog() {
		Date start = null;
		logs = new ArrayList<>();
		LogData data = getLogData();
		for (Date end : data.getAddedClaims().keySet()) {
			//Added
			List<AssetLog> addedUnknown = new ArrayList<>();
			calc(start, end, data.getAddedSources(), data.getAddedClaims().get(end), addedUnknown, logs);
			//Removed
			List<AssetLog> removedUnknown = new ArrayList<>();
			calc(start, end, data.getRemovedSources(), data.getRemovedClaims().get(end), removedUnknown, logs);
			//boolean loot = canBeLoot(start, end, profileData.getJournalList());
			calcUnresolved(addedUnknown, removedUnknown, logs, true);

			start = end; //Next
		}
	}

	private static void calcUnresolved(List<AssetLog> added, List<AssetLog> removed, List<AssetLogSource> newLogs, boolean loot) {
		//Add Claims
		Set<Integer> typeIDs = new HashSet<>();
		Map<Integer, List<AssetLog>> claims = new HashMap<>();
		for (AssetLog asset : added) {
			typeIDs.add(asset.getTypeID());
			put(claims, asset.getTypeID(), asset);
		}
		//Add Sources
		Map<Integer, List<LogSource>> sources = new HashMap<>();
		for (AssetLog asset : removed) {
			int typeID = asset.getTypeID();
			if (!typeIDs.contains(typeID)) { //TypeID does not match - Remain Unknown
				AssetLogSource soruce = new AssetLogSource(asset, asset, LogSourceType.REMOVED_UNKNOWN, 0, asset.getNeed());
				asset.add(soruce, true);
				newLogs.add(soruce);
				continue;
			}
			put(sources, typeID, new LogSource(LogSourceType.UNKNOWN, asset.getNeed(), asset));
		}
		//Resolve claims
		for (Map.Entry<Integer, List<AssetLog>> entry : claims.entrySet()) {
			List<LogSource> soruceList = sources.get(entry.getKey());
			if (soruceList == null) {
				continue;
			}
			for (AssetLog claim : entry.getValue()) {
				for (LogSource source : soruceList) {
					source.addClaim(claim);
				}
			}
			for (LogSource source : soruceList) {
				source.claim();
			}
		}
		for (List<AssetLog> list : claims.values()) {
			for (AssetLog claim : list) {
				if (claim.getSources().isEmpty() || claim.getNeed() > 0) {
					if (loot && (claim.getItem().getCategory().equals("Drone") 
							|| claim.getItem().getCategory().equals("Commodity")
							|| claim.getItem().getCategory().equals("Module")
							|| claim.getItem().getCategory().equals("Charge"))) {
						claim.add(new AssetLogSource(claim, claim, LogSourceType.ADDED_LOOT, 25, claim.getNeed()), true);
					} else {
						claim.add(new AssetLogSource(claim, claim, LogSourceType.ADDED_UNKNOWN, 0, claim.getNeed()), true);
					}
				}
				newLogs.addAll(claim.getSources());
			}
		}
		for (List<LogSource> sourceAssets : sources.values()) {
			for (LogSource source : sourceAssets) {
				AssetLog assetLog = source.getAssetLog();
				if (assetLog.getSources().isEmpty() || source.getAvailable() > 0) {
					assetLog.add(new AssetLogSource(source, assetLog, LogSourceType.REMOVED_UNKNOWN, 0, source.getAvailable()), true);
				}
				newLogs.addAll(assetLog.getSources());
			}
		}
	}

	private static void calc(Date start, Date end, Map<Integer, Set<LogSource>> sources, Map<Integer, List<AssetLog>> claims, List<AssetLog> unknown, List<AssetLogSource> newLogs) {
		//Resolve claims
		for (List<AssetLog> list : claims.values()) {
			for (AssetLog assetLog : list) {
				assetLog.reset();
			}
		}
		for (Set<LogSource> list : sources.values()) {
			for (LogSource source : list) {
				source.reset();
			}
		}
		for (Map.Entry<Integer, List<AssetLog>> entry : claims.entrySet()) {
			Set<LogSource> soruceList = sources.get(entry.getKey());
			if (soruceList == null) {
				continue;
			}
			for (AssetLog claim : entry.getValue()) {
				if (!isValidDate(start, end, claim.getDate())) {
					continue;
				}
				for (LogSource source : soruceList) {
					if (!isValidDate(start, end, source.getDate())) {
						continue;
					}
					source.addClaim(claim);
				}
			}
			for (LogSource source : soruceList) {
				source.claim();
			}
		}
		for (List<AssetLog> list : claims.values()) {
			for (AssetLog claim : list) {
				if (claim.getSources().isEmpty() || claim.getNeed() > 0) {
					unknown.add(claim);
					continue;
				}
				newLogs.addAll(claim.getSources());
			}
		}
	}

	private static boolean isValidDate(Date start, Date end, Date date) {
		if (start != null && date.before(start)) { //Outside Date range
			return false;
		}
		if (date.after(end)) { //Outside Date range
			return false;
		}
		return true;
	}

	public static <K, V> void put(Map<K, List<V>> map, K k, V v) {
		List<V> list = map.get(k);
		if (list == null) {
			list = new ArrayList<V>();
			map.put(k, list);
		}
		list.add(v);
	}

	public static <K, V> void putSet(Map<K, Set<V>> map, K k, V v) {
		Set<V> list = map.get(k);
		if (list == null) {
			list = new HashSet<V>();
			map.put(k, list);
		}
		list.add(v);
	}

	private static boolean canBeLoot(Date start, Date end, List<MyJournal> journals) {
		for (MyJournal journal : journals) {
			Date date = journal.getDate();
			if (start != null && date.before(start)) { //Outside Date range
				continue;
			}
			if (date.after(end)) { //Outside Date range
				continue;
			}
			if (journal.getRefType() == RawJournalRefType.BOUNTY_PRIZES || journal.getRefType() == RawJournalRefType.BOUNTY_PRIZE) {
				return true;
			}
		}
		return false;
	}

	private static void removedSellMarketOrderCreated(Map<Integer, Set<LogSource>> sources, List<MyMarketOrder> marketOrders) {
		for (MyMarketOrder marketOrder : marketOrders) {
			Date date = marketOrder.getIssued();
			int typeID = marketOrder.getTypeID();
			if (marketOrder.isBuyOrder()) { //Ignore Buy Orders
				continue;
			}
			long ownerID = marketOrder.getOwnerID();
			long locationID = marketOrder.getLocationID();
			int quantity = marketOrder.getVolumeTotal();
			LogSourceType sourceType = LogSourceType.REMOVED_MARKET_ORDER_CREATED;
			LogType logType = LogType.MARKET_ORDER;
			long id = marketOrder.getOrderID();
			putSet(sources, typeID, new LogSource(sourceType, quantity, typeID, date, ownerID, locationID, logType, id));
		}
	}

	private static void removedContracts(Map<Integer, Set<LogSource>> sources, List<MyContractItem> contractItems) {
		for (MyContractItem contractItem : contractItems) {
			Date date;
			if (contractItem.isIncluded()) { //Item being sold by the issuer (Removed on creating contract)
				date = contractItem.getContract().getDateIssued();
			} else { //Item being sold by the acceptor (Removed on completing contract) 
				date = contractItem.getContract().getDateCompleted();
			}
			int typeID = contractItem.getTypeID();
			if (date == null) { //Not Completed
				continue;
			}
			if (contractItem.getContract().isIgnoreContract()) { //Wrong contract type
				continue;
			}
			if (contractItem.isIncluded()) { //Item being sold by the issuer
				long ownerID = contractItem.getContract().getIssuerID();
				long locationID = contractItem.getContract().getStartLocationID();
				int quantity = contractItem.getQuantity();
				LogSourceType sourceType = LogSourceType.REMOVED_CONTRACT_CREATED;
				LogType logType = LogType.CONTRACT;
				long id = contractItem.getRecordID();
				putSet(sources, typeID, new LogSource(sourceType, quantity, typeID, date, ownerID, locationID, logType, id));
			} else { //Item being sold by the acceptor
				long ownerID = contractItem.getContract().getAcceptorID();
				long locationID = contractItem.getContract().getStartLocationID();
				int quantity = contractItem.getQuantity();
				LogSourceType sourceType = LogSourceType.REMOVED_CONTRACT_ACCEPTED;
				LogType logType = LogType.CONTRACT;
				long id = contractItem.getRecordID();
				putSet(sources, typeID, new LogSource(sourceType, quantity, typeID, date, ownerID, locationID, logType, id));
			}
		}
	}

	private static void removedIndustryJobsCreated(Map<Integer, Set<LogSource>> sources, List<MyIndustryJob> industryJobs) {
		for (MyIndustryJob industryJob : industryJobs) {
			Date date = industryJob.getStartDate();
			int typeID = industryJob.getBlueprintTypeID();
			if (industryJob.isDelivered()) { //Ignore delivered jobs
				continue;
			}
			long ownerID = industryJob.getOwnerID();
			long locationID = industryJob.getBlueprintLocationID();
			int quantity = 1;
			LogSourceType sourceType = LogSourceType.REMOVED_INDUSTRY_JOB_CREATED;
			LogType logType = LogType.INDUSTRY_JOB;
			long id = industryJob.getJobID();
			putSet(sources, typeID, new LogSource(sourceType, quantity, typeID, date, ownerID, locationID, logType, id));
		}
	}

	private static void addedTransactionsBought(Map<Integer, Set<LogSource>> sources, List<MyTransaction> transactions) {
		for (MyTransaction transaction : transactions) {
			Date date = transaction.getDate();
			int typeID = transaction.getTypeID();
			if (transaction.isSell()) { //Ignore Sell Transactions
				continue;
			}
			long ownerID = transaction.getOwnerID();
			long locationID = transaction.getLocationID();
			int quantity = transaction.getQuantity();
			LogSourceType sourceType = LogSourceType.ADDED_TRANSACTIONS_BOUGHT;
			LogType logType = LogType.TRANSACTION;
			long id = transaction.getTransactionID();
			putSet(sources, typeID, new LogSource(sourceType, quantity, typeID, date, ownerID, locationID, logType, id));
		}
	}

	private static void addedContracts(Map<Integer, Set<LogSource>> sources, List<MyContractItem> contractItems) {
		for (MyContractItem contractItem : contractItems) {
			Date date = contractItem.getContract().getDateCompleted();
			int typeID = contractItem.getTypeID();
			if (date == null) { //Not Completed
				continue;
			}
			if (contractItem.getContract().isIgnoreContract()) { //Wrong contract type
				continue;
			}
			if (contractItem.isIncluded()) { //Item being bought by the acceptor
				long ownerID = contractItem.getContract().getAcceptorID();
				long locationID = contractItem.getContract().getStartLocationID();
				int quantity = contractItem.getQuantity();
				LogSourceType sourceType = LogSourceType.ADDED_CONTRACT_ACCEPTED;
				LogType logType = LogType.CONTRACT;
				long id = contractItem.getRecordID();
				putSet(sources, typeID, new LogSource(sourceType, quantity, typeID, date, ownerID, locationID, logType, id));
			} else { //Item being bought by the issuer
				long ownerID;
				if (contractItem.getContract().isForCorp()) {
					ownerID = contractItem.getContract().getIssuerCorpID();
				} else {
					ownerID = contractItem.getContract().getIssuerID();
				}
				long locationID = contractItem.getContract().getStartLocationID();
				int quantity = contractItem.getQuantity();
				LogSourceType sourceType = LogSourceType.ADDED_CONTRACT_ACCEPTED;
				LogType logType = LogType.CONTRACT;
				long id = contractItem.getRecordID();
				putSet(sources, typeID, new LogSource(sourceType, quantity, typeID, date, ownerID, locationID, logType, id));
			}
			
		}
	}
	private static void addedContractsCancelled(Map<Integer, Set<LogSource>> sources, Date date, List<MyContractItem> newContractItems, Map<Integer, MyContract> oldContracts) {
		for (MyContractItem newContractItem : newContractItems) {
			MyContract oldContract = oldContracts.get(newContractItem.getContract().getContractID());
			if (oldContract == null) {
				continue;
			}
			if (oldContract.getStatus().equals(newContractItem.getContract().getStatus())) {
				continue;
			}
			if (newContractItem.getContract().getStatus() != RawContract.ContractStatus.CANCELLED 
					&& newContractItem.getContract().getStatus() != RawContract.ContractStatus.DELETED
					&& newContractItem.getContract().getStatus() != RawContract.ContractStatus.REVERSED) {
				continue;
			}
			if (!newContractItem.isIncluded()) {
				continue;
			}
			int typeID = newContractItem.getTypeID();
			long ownerID = newContractItem.getContract().getIssuerID();
			long locationID = newContractItem.getContract().getStartLocationID();
			int quantity = newContractItem.getQuantity();
			LogSourceType sourceType = LogSourceType.ADDED_CONTRACT_CANCELLED;
			LogType logType = LogType.CONTRACT;
			long id = newContractItem.getRecordID();
			putSet(sources, typeID, new LogSource(sourceType, quantity, typeID, date, ownerID, locationID, logType, id));
		}
	}

	private static void addedIndustryJobsDelivered(Map<Integer, Set<LogSource>> sources, Date date, List<MyIndustryJob> newIndustryJobs, Map<Integer, MyIndustryJob> oldIndustryJobs) {
		for (MyIndustryJob newIndustryJob : newIndustryJobs) {
			MyIndustryJob oldIndustryJob = oldIndustryJobs.get(newIndustryJob.getJobID());
			if (oldIndustryJob == null) {
				continue;
			}
			if (oldIndustryJob.getState().equals(newIndustryJob.getState())) {
				continue;
			}
			if (!newIndustryJob.isDelivered()) { //Not delivered AKA not in assets yet
				continue;
			}
			int blueprintTypeID = newIndustryJob.getBlueprintTypeID();
			int productTypeID = newIndustryJob.getProductTypeID();
			long ownerID = newIndustryJob.getOwnerID();
			long blueprintLocationID = newIndustryJob.getBlueprintLocationID();
			int blueprintQuantity = 1;
			LogSourceType sourceType = LogSourceType.ADDED_INDUSTRY_JOB_DELIVERED;
			LogType logType = LogType.INDUSTRY_JOB;
			long id = newIndustryJob.getJobID();
			putSet(sources, blueprintTypeID, new LogSource(sourceType, blueprintQuantity, blueprintTypeID, date, ownerID, blueprintLocationID, logType, id));
			if (newIndustryJob.isManufacturing() && newIndustryJob.getState() == IndustryJobState.STATE_DELIVERED) {
				long productLocationID = newIndustryJob.getOutputLocationID();
				int productQuantity = newIndustryJob.getOutputCount();
				LogType productLogType = LogType.INDUSTRY_JOB;
				long productID = newIndustryJob.getJobID();
				putSet(sources, productTypeID, new LogSource(sourceType, productQuantity, productTypeID, date, ownerID, productLocationID, productLogType, productID));
			}
		}
	}

	private static void addedMarketOrderCancelled(Map<Integer, Set<LogSource>> sources, Date date, List<MyMarketOrder> newMarketOrders, Map<Long, MyMarketOrder> oldMarketOrders) {
		for (MyMarketOrder newMarketOrder : newMarketOrders) {
			MyMarketOrder oldMarketOrder = oldMarketOrders.get(newMarketOrder.getOrderID());
			if (oldMarketOrder == null) {
				continue;
			}
			if (oldMarketOrder.getStatus().equals(newMarketOrder.getStatus())) {
				continue;
			}
			if (newMarketOrder.getStatus() != MyMarketOrder.OrderStatus.UNKNOWN) {
				continue;
			}
			int typeID = newMarketOrder.getTypeID();
			long ownerID = newMarketOrder.getOwnerID();
			long locationID = newMarketOrder.getLocationID();
			int quantity = newMarketOrder.getVolumeRemain();
			LogSourceType sourceType = LogSourceType.ADDED_MARKET_ORDER_CANCELLED;
			LogType logType = LogType.MARKET_ORDER;
			long id = newMarketOrder.getOrderID();
			putSet(sources, typeID, new LogSource(sourceType, quantity, typeID, date, ownerID, locationID, logType, id));
		}
	}
}
