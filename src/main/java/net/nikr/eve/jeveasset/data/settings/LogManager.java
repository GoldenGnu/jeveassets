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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLog;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogData.LogType;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogSource;
import net.nikr.eve.jeveasset.gui.tabs.log.LogChangeType;
import net.nikr.eve.jeveasset.gui.tabs.log.LogSource;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.local.LogsReader;
import net.nikr.eve.jeveasset.io.local.LogsWriter;


public class LogManager {
	private static Map<Date, Map<AssetLog, List<AssetLogSource>>> logs =  null;

	private LogManager() { }

	public static synchronized List<AssetLogSource> getList() {
		List<AssetLogSource> logSet = new ArrayList<>();
		for (Map<AssetLog, List<AssetLogSource>> log : getLogs().values()) {
			for (List<AssetLogSource> sources : log.values()) {
				logSet.addAll(sources);
			}
		}
		return Collections.unmodifiableList(logSet);
	}

	private static void add(Date date, Map<AssetLog, List<AssetLogSource>> newLogs) {
		getLogs().put(date, newLogs);
		save();
	}

	private static void save() {
		LogsWriter.save(logs);
	}

	private static Map<Date, Map<AssetLog, List<AssetLogSource>>> getLogs() {
		if (logs == null) {
			logs = new HashMap<>();
			LogsReader.load(logs);
		}
		return logs;
	}

	private static Date getStartDate() {
		List<Date> dates = new ArrayList<>(getLogs().keySet());
		Collections.sort(dates);
		if (!dates.isEmpty()) {
			return dates.get(0);
		} else {
			return null;
		}
	}

	public static void createLog(List<MyAsset> oldAssets, Date start, ProfileData profileData) {
		if (oldAssets.isEmpty()) {
			return;
		}
		List<MyAsset> newAssets = profileData.getAssetsList();
		Date end = new Date();
		Map<Long, AssetLog> oldMap = new HashMap<Long, AssetLog>();
		Map<Long, AssetLog> newMap = new HashMap<Long, AssetLog>();
		for (MyAsset asset : oldAssets) {
			if (asset.isGenerated()) {
				continue;
			}
			if (asset.getFlag().equals(General.get().industryJobFlag())) {
				continue;
			}
			oldMap.put(asset.getItemID(), new AssetLog(asset, end));
		}
		for (MyAsset asset : newAssets) {
			if (asset.isGenerated()) {
				continue;
			}
			if (asset.getFlag().equals(General.get().industryJobFlag())) {
				continue;
			}
			newMap.put(asset.getItemID(), new AssetLog(asset, end));
		}
		Map<AssetLog, List<AssetLogSource>> newLogs = new HashMap<>();
	//New
		//Added Assets
		Map<Long, AssetLog> added = new HashMap<Long, AssetLog>(newMap); //New Assets
		added.keySet().removeAll(oldMap.keySet()); //Removed Old Assets
		//Removed Assets
		Map<Long, AssetLog> removed = new HashMap<Long, AssetLog>(oldMap); //Old Assets
		removed.keySet().removeAll(newMap.keySet()); //Remove New Assets
		//Moved
		Map<Long, AssetLog> same = new HashMap<Long, AssetLog>(oldMap); //Old Assets
		same.keySet().retainAll(newMap.keySet()); //Assets in both New and Old (retain)
		//Moved: Same itemID
		for (Long itemID : same.keySet()) {
			AssetLog from = oldMap.get(itemID);
			AssetLog to = newMap.get(itemID);
			boolean owner = !Objects.equal(from.getOwnerID(), to.getOwnerID()) ; //New Owner
			boolean location = !Objects.equal(from.getLocationID(), to.getLocationID());  //New Location
			boolean flag =  !Objects.equal(from.getFlagID(), to.getFlagID()); //New Flag
			boolean container = !Objects.equal(from.getParentIDs(), to.getParentIDs()); //New Container
			if (location || flag || container || owner) {
				AssetLogSource assetLogSource = new AssetLogSource(from, to, LogChangeType.MOVED_FROM, 100, to.getNeed());
				to.add(assetLogSource, false);
				put(newLogs, to, assetLogSource);
			}
			if (from.getNeed() > to.getNeed()) {
				removed.put(from.getItemID(), new AssetLog(from, end, from.getNeed() - to.getNeed()));
			}
		}
		//Added Assets
		List<AssetLog> unknownAdded = new ArrayList<>();
		added(profileData, start, end, unknownAdded, newLogs, added.values());
		//Removed Assets
		List<AssetLog> unknownRemoved = new ArrayList<>();
		removed(profileData, start, end, unknownRemoved, newLogs, removed.values());
		//Moved: new itemID
		boolean loot = canBeLoot(start, end, profileData.getJournalList());
		moved(end, newLogs, unknownAdded, unknownRemoved, loot);

		add(end, newLogs);
	}

	private static void moved(Date end, Map<AssetLog, List<AssetLogSource>> newLogs, Collection<AssetLog> added, Collection<AssetLog> removed, boolean loot) {
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
				AssetLogSource soruce = new AssetLogSource(asset, asset, LogChangeType.REMOVED_UNKNOWN, 0, asset.getNeed());
				asset.add(soruce, true);
				put(newLogs, asset, soruce);
				continue;
			}
			put(sources, typeID, new LogSource(LogChangeType.UNKNOWN, asset.getNeed(), asset));
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
						claim.add(new AssetLogSource(claim, claim, LogChangeType.ADDED_LOOT, 25, claim.getNeed()), true);
					} else {
						claim.add(new AssetLogSource(claim, claim, LogChangeType.ADDED_UNKNOWN, 0, claim.getNeed()), true);
					}
				}
				newLogs.put(claim, claim.getSources());
			}
		}
		for (List<LogSource> sourceAssets : sources.values()) {
			for (LogSource source : sourceAssets) {
				AssetLog assetLog = source.getAssetLog();
				if (assetLog.getSources().isEmpty() || source.getAvailable() > 0) {
					assetLog.add(new AssetLogSource(source, assetLog, LogChangeType.REMOVED_UNKNOWN, 0, source.getAvailable()), true);
				}
				newLogs.put(assetLog, assetLog.getSources());
			}
		}
	}

	private static void added(ProfileData profileData, Date start, Date end, List<AssetLog> unknown, Map<AssetLog, List<AssetLogSource>> newLogs, Collection<AssetLog> added) {
		//Add Claims
		Set<Integer> typeIDs = new HashSet<>();
		Map<Integer, List<AssetLog>> claims = new HashMap<>();
		for (AssetLog asset : added) {
			typeIDs.add(asset.getTypeID());
			put(claims, asset.getTypeID(), asset);
		}
		//Add Sources
		Map<Integer, List<LogSource>> sources = new HashMap<>();
		addedTransactionsBought(sources, start, end, profileData.getTransactionsList(), typeIDs);
		addedContracts(sources, start, end, profileData.getContractItemList(), typeIDs);
		addedIndustryJobsDelivered(sources, start, end, profileData.getIndustryJobsList(), typeIDs);
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
					unknown.add(claim);
					continue;
				}
				newLogs.put(claim, claim.getSources());
			}
		}
	}

	private static void removed(ProfileData profileData, Date start, Date end, List<AssetLog> unknown, Map<AssetLog, List<AssetLogSource>> newLogs, Collection<AssetLog> removed) {
		//Add Claims
		Set<Integer> typeIDs = new HashSet<>();
		Map<Integer, List<AssetLog>> claims = new HashMap<>();
		for (AssetLog asset : removed) {
			typeIDs.add(asset.getTypeID());
			put(claims, asset.getTypeID(), asset);
		}
		//Add Sources
		Map<Integer, List<LogSource>> removedSources = new HashMap<>();
		removedSellMarketOrderCreated(removedSources, start, end, profileData.getMarketOrdersList(), typeIDs);
		removedContracts(removedSources, start, end, profileData.getContractItemList(), typeIDs);
		removedIndustryJobsCreated(removedSources, start, end, profileData.getIndustryJobsList(), typeIDs);
		//Resolve claims
		for (Map.Entry<Integer, List<AssetLog>> entry : claims.entrySet()) {
			List<LogSource> soruceList = removedSources.get(entry.getKey());
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
		//Create Logs from Claims
		for (List<AssetLog> list : claims.values()) {
			for (AssetLog claim : list) {
				if (claim.getSources().isEmpty() || claim.getNeed() > 0) {
					unknown.add(claim);
					continue;
				}
				newLogs.put(claim, claim.getSources());
			}
		}
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

	private static void removedSellMarketOrderCreated(Map<Integer, List<LogSource>> sources, Date start, Date end, List<MyMarketOrder> marketOrders, Set<Integer> typeIDs) {
		for (MyMarketOrder marketOrder : marketOrders) {
			Date date = marketOrder.getIssued();
			int typeID = marketOrder.getTypeID();
			if (start != null && date.before(start)) { //Outside Date range
				continue;
			}
			if (date.after(end)) { //Outside Date range
				continue;
			}
			if (marketOrder.isBuyOrder()) { //Ignore Buy Orders
				continue;
			}
			if (!typeIDs.contains(typeID)) { //TypeID does not match
				continue;
			}
			long ownerID = marketOrder.getOwnerID();
			long locationID = marketOrder.getLocationID();
			int quantity = marketOrder.getVolEntered();
			LogChangeType changeType = LogChangeType.REMOVED_MARKET_ORDER_CREATED;
			LogType logType = LogType.MARKET_ORDER;
			long id = marketOrder.getOrderID();
			put(sources, typeID, new LogSource(changeType, quantity, typeID, date, ownerID, locationID, logType, id));
		}
	}

	private static void removedContracts(Map<Integer, List<LogSource>> sources, Date start, Date end, List<MyContractItem> contractItems, Set<Integer> typeIDs) {
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
			if (start != null && date.before(start)) { //Outside Date range
				continue;
			}
			if (date.after(end)) { //Outside Date range
				continue;
			}
			if (contractItem.getContract().isIgnoreContract()) { //Wrong contract type
				continue;
			}
			if (!typeIDs.contains(typeID)) { //TypeID does not match
				continue;
			}
			if (contractItem.isIncluded()) { //Item being sold by the issuer
				long ownerID = contractItem.getContract().getIssuerID();
				long locationID = contractItem.getContract().getStartLocationID();
				int quantity = contractItem.getQuantity();
				LogChangeType changeType = LogChangeType.REMOVED_CONTRACT_CREATED;
				LogType logType = LogType.CONTRACT;
				long id = contractItem.getRecordID();
				put(sources, typeID, new LogSource(changeType, quantity, typeID, date, ownerID, locationID, logType, id));
			} else { //Item being sold by the acceptor
				long ownerID = contractItem.getContract().getAcceptorID();
				long locationID = contractItem.getContract().getStartLocationID();
				int quantity = contractItem.getQuantity();
				LogChangeType changeType = LogChangeType.REMOVED_CONTRACT_ACCEPTED;
				LogType logType = LogType.CONTRACT;
				long id = contractItem.getRecordID();
				put(sources, typeID, new LogSource(changeType, quantity, typeID, date, ownerID, locationID, logType, id));
			}
		}
	}

	private static void removedIndustryJobsCreated(Map<Integer, List<LogSource>> sources, Date start, Date end, List<MyIndustryJob> industryJobs, Set<Integer> typeIDs) {
		for (MyIndustryJob industryJob : industryJobs) {
			Date date = industryJob.getStartDate();
			int typeID = industryJob.getBlueprintTypeID();
			if (start != null && date.before(start)) { //Outside Date range
				continue;
			}
			if (date.after(end)) { //Outside Date range
				continue;
			}
			if (industryJob.isDelivered()) { //Ignore delivered jobs
				continue;
			}
			if (!typeIDs.contains(typeID)) { //TypeID does not match
				continue;
			}
			long ownerID = industryJob.getOwnerID();
			long locationID = industryJob.getBlueprintLocationID();
			int quantity = 1;
			LogChangeType changeType = LogChangeType.REMOVED_INDUSTRY_JOB_CREATED;
			LogType logType = LogType.INDUSTRY_JOB;
			long id = industryJob.getJobID();
			put(sources, typeID, new LogSource(changeType, quantity, typeID, date, ownerID, locationID, logType, id));
		}
	}

	private static void addedTransactionsBought(Map<Integer, List<LogSource>> sources, Date start, Date end, List<MyTransaction> transactions, Set<Integer> typeIDs) {
		for (MyTransaction transaction : transactions) {
			Date date = transaction.getDate();
			int typeID = transaction.getTypeID();
			if (start != null && date.before(start)) { //Outside Date range
				continue;
			}
			if (date.after(end)) { //Outside Date range
				continue;
			}
			if (transaction.isSell()) { //Ignore Sell Transactions
				continue;
			}
			if (!typeIDs.contains(typeID)) { //TypeID does not match
				continue;
			}
			long ownerID = transaction.getOwnerID();
			long locationID = transaction.getLocationID();
			int quantity = transaction.getQuantity();
			LogChangeType changeType = LogChangeType.ADDED_TRANSACTIONS_BOUGHT;
			LogType logType = LogType.TRANSACTION;
			long id = transaction.getTransactionID();
			put(sources, typeID, new LogSource(changeType, quantity, typeID, date, ownerID, locationID, logType, id));
		}
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

	private static void addedContracts(Map<Integer, List<LogSource>> sources, Date start, Date end, List<MyContractItem> contractItems, Set<Integer> typeIDs) {
		for (MyContractItem contractItem : contractItems) {
			Date date = contractItem.getContract().getDateCompleted();
			int typeID = contractItem.getTypeID();
			if (date == null) { //Not Completed
				continue;
			}
			if (start != null && date.before(start)) { //Outside Date range
				continue;
			}
			if (date.after(end)) { //Outside Date range
				continue;
			}
			if (contractItem.getContract().isIgnoreContract()) { //Wrong contract type
				continue;
			}
			if (!typeIDs.contains(typeID)) { //TypeID does not match
				continue;
			}
			if (contractItem.isIncluded()) { //Item being bought by the acceptor
				long ownerID = contractItem.getContract().getAcceptorID();
				long locationID = contractItem.getContract().getStartLocationID();
				int quantity = contractItem.getQuantity();
				LogChangeType changeType = LogChangeType.ADDED_CONTRACT_ACCEPTED;
				LogType logType = LogType.CONTRACT;
				long id = contractItem.getRecordID();
				put(sources, typeID, new LogSource(changeType, quantity, typeID, date, ownerID, locationID, logType, id));
			} else { //Item being bought by the issuer
				long ownerID;
				if (contractItem.getContract().isForCorp()) {
					ownerID = contractItem.getContract().getIssuerCorpID();
				} else {
					ownerID = contractItem.getContract().getIssuerID();
				}
				long locationID = contractItem.getContract().getStartLocationID();
				int quantity = contractItem.getQuantity();
				LogChangeType changeType = LogChangeType.ADDED_CONTRACT_ACCEPTED;
				LogType logType = LogType.CONTRACT;
				long id = contractItem.getRecordID();
				put(sources, typeID, new LogSource(changeType, quantity, typeID, date, ownerID, locationID, logType, id));
			}
			
		}
	}

	private static void addedIndustryJobsDelivered(Map<Integer, List<LogSource>> sources, Date start, Date end, List<MyIndustryJob> industryJobs, Set<Integer> typeIDs) {
		for (MyIndustryJob industryJob : industryJobs) {
			Date date = industryJob.getCompletedDate();
			int blueprintTypeID = industryJob.getBlueprintTypeID();
			int productTypeID = industryJob.getProductTypeID();
			if (date == null) { //Not completed yet
				continue;
			}
			if (start != null && date.before(start)) { //Outside Date range
				continue;
			}
			if (date.after(end)) { //Outside Date range
				continue;
			}
			if (!industryJob.isDelivered()) { //Not delivered AKA not in assets yet
				continue;
			}
			if (!typeIDs.contains(blueprintTypeID) && !typeIDs.contains(productTypeID)) { //TypeID does not match
				continue;
			}
			long ownerID = industryJob.getOwnerID();
			long blueprintLocationID = industryJob.getBlueprintLocationID();
			int blueprintQuantity = 1;
			LogChangeType changeType = LogChangeType.ADDED_INDUSTRY_JOB_DELIVERED;
			LogType logType = LogType.INDUSTRY_JOB;
			long id = industryJob.getJobID();
			put(sources, blueprintTypeID, new LogSource(changeType, blueprintQuantity, blueprintTypeID, date, ownerID, blueprintLocationID, logType, id));
			if (industryJob.isManufacturing() && industryJob.getState() == IndustryJobState.STATE_DELIVERED) {
				long productLocationID = industryJob.getOutputLocationID();
				int productQuantity = industryJob.getOutputCount();
				LogType productLogType = LogType.INDUSTRY_JOB;
				long productID = industryJob.getJobID();
				put(sources, productTypeID, new LogSource(changeType, productQuantity, productTypeID, date, ownerID, productLocationID, productLogType, productID));
			}
		}
	}
}
