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
package net.nikr.eve.jeveasset.data.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.gui.tabs.log.LogChangeType;
import net.nikr.eve.jeveasset.gui.tabs.log.LogType;
import net.nikr.eve.jeveasset.gui.tabs.log.MyLog;
import net.nikr.eve.jeveasset.gui.tabs.log.RawLog;
import net.nikr.eve.jeveasset.gui.tabs.log.RawLog.LogData;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.local.LogsReader;
import net.nikr.eve.jeveasset.io.local.LogsWriter;


public class LogManager {
	private static Map<Date, Set<RawLog>> logs =  null;

	private LogManager() { }

	public static synchronized Set<MyLog> getList() {
		Set<RawLog> logSet = new HashSet<>();
		for (Set<RawLog> log : getLogs().values()) {
			logSet.addAll(log);
		}
		return Collections.unmodifiableSet(convert(logSet));
	}

	private static void add(Date date, Set<RawLog> c) {
		getLogs().put(date, c);
		save();
	}

	private static void save() {
		LogsWriter.save(logs);
	}

	private static Set<MyLog> convert(Collection<RawLog> logs) {
		Set<MyLog> myLogs = new HashSet<MyLog>();
		Settings.lock("Creating Log");
		for (RawLog rawLog : logs) {
			myLogs.add(new MyLog(rawLog));
		}
		Settings.unlock("Creating Log");
		return myLogs;
	}

	private static Map<Date, Set<RawLog>> getLogs() {
		if (logs == null) {
			logs = new HashMap<Date, Set<RawLog>>();
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
		Map<Long, LogAsset> oldMap = new HashMap<Long, LogAsset>();
		Map<Long, LogAsset> newMap = new HashMap<Long, LogAsset>();
		for (MyAsset asset : oldAssets) {
			if (asset.isGenerated()) {
				continue;
			}
			if (asset.getFlag().equals(General.get().industryJobFlag())) {
				continue;
			}
			oldMap.put(asset.getItemID(), new LogAsset(asset));
		}
		for (MyAsset asset : newAssets) {
			if (asset.isGenerated()) {
				continue;
			}
			if (asset.getFlag().equals(General.get().industryJobFlag())) {
				continue;
			}
			newMap.put(asset.getItemID(), new LogAsset(asset));
		}
		Set<RawLog> newLogs = new HashSet<RawLog>();
	//New
		//Added Assets
		Map<Long, LogAsset> added = new HashMap<Long, LogAsset>(newMap); //New Assets
		added.keySet().removeAll(oldMap.keySet()); //Removed Old Assets
		List<LogAsset> unknownAdded = new ArrayList<>();
		added(profileData, start, end, unknownAdded, newLogs, added.values());

		//Removed Assets
		Map<Long, LogAsset> removed = new HashMap<Long, LogAsset>(oldMap); //Old Assets
		removed.keySet().removeAll(newMap.keySet()); //Remove New Assets
		List<LogAsset> unknownRemoved = new ArrayList<>();
		removed(profileData, start, end, unknownRemoved, newLogs, removed.values());

		//Moved
		Map<Long, LogAsset> same = new HashMap<Long, LogAsset>(oldMap); //Old Assets
		same.keySet().retainAll(newMap.keySet()); //Assets in both New and Old (retain)
		//Moved: Same itemID
		for (Long itemID : same.keySet()) {
			LogAsset oldAsset = oldMap.get(itemID);
			LogAsset newAsset = newMap.get(itemID);
			List<Long> oldParents = new ArrayList<>();
			List<Long> newParents = new ArrayList<>();
			for (MyAsset asset : oldAsset.getParents()) {
				oldParents.add(asset.getItemID());
			}
			for (MyAsset asset : newAsset.getParents()) {
				newParents.add(asset.getItemID());
			}
			boolean owner = oldAsset.getOwnerID() != newAsset.getOwnerID(); //New Owner
			boolean location = oldAsset.getLocationID() != newAsset.getLocationID();  //New Location
			boolean flag =  !oldAsset.getFlagID().equals(newAsset.getFlagID()); //New Flag
			boolean container = !oldParents.equals(newParents); //New Container
			if (location || flag || container || owner) {
				LogData oldData = new LogData(oldAsset);
				LogData newData = new LogData(newAsset);
				newLogs.add(new RawLog(end, oldAsset.getItemID(), oldAsset.getTypeID(), oldAsset.getCount(), oldData, newData, LogData.changed(end, oldData, newData, 100, LogChangeType.MOVED_UNKNOWN)));
			} else if (oldAsset.getCount() > newAsset.getCount()) {
				unknownRemoved.add(new LogAsset(oldAsset, oldAsset.getCount() - newAsset.getCount()));
			}
		}
		//Moved: new itemID
		boolean loot = canBeLoot(start, end, profileData.getJournalList());
		moved(end, newLogs, unknownAdded, unknownRemoved, loot);

		add(end, newLogs);
	}

	private static void moved(Date end, Set<RawLog> newLogs, Collection<LogAsset> added, Collection<LogAsset> removed, boolean loot) {
		//Add Claims
		Set<Integer> typeIDs = new HashSet<>();
		Map<Integer, List<LogAsset>> claims = new HashMap<>();
		for (LogAsset asset : added) {
			typeIDs.add(asset.getTypeID());
			put(claims, asset.getTypeID(), asset);
		}
		//Add Sources
		Map<Integer, List<SourceAsset>> sources = new HashMap<>();
		for (LogAsset asset : removed) {
			int typeID = asset.getTypeID();
			if (!typeIDs.contains(typeID)) { //TypeID does not match - Remain Unknown
				Map<LogChangeType, Set<LogType>> logTypes = Collections.singletonMap(LogChangeType.REMOVED_UNKNOWN, Collections.singleton(new LogType(end, LogChangeType.REMOVED_UNKNOWN, 0)));
				newLogs.add(new RawLog(end, asset.getItemID(), asset.getTypeID(), asset.getCount(), new LogData(asset), null, logTypes));
				continue;
			}
			put(sources, typeID, new SourceAsset(asset, end));
		}
		//Resolve claims
		for (Map.Entry<Integer, List<LogAsset>> entry : claims.entrySet()) {
			List<SourceAsset> soruceList = sources.get(entry.getKey());
			if (soruceList == null) {
				continue;
			}
			for (LogAsset claim : entry.getValue()) {
				for (Source source : soruceList) {
					source.addClaim(claim);
				}
			}
			for (Source source : soruceList) {
				source.claim();
			}
		}
		for (List<LogAsset> list : claims.values()) {
			for (LogAsset claim : list) {
				Map<LogChangeType, Set<LogType>> logTypes = claim.getLogTypes();
				LogAsset fromAsset = null;
				for (LogTypeAsset logTypeAsset : claim.getLogTypeAssets()) {
					logTypes.putAll(LogData.changed(end, new LogData(logTypeAsset.getAsset()), new LogData(claim), logTypeAsset.getPercent(), LogChangeType.MOVED_SAME));
					fromAsset = logTypeAsset.getAsset();
				}
				if (logTypes.isEmpty()) {
					if (loot && (claim.getItem().getCategory().equals("Drone") 
							|| claim.getItem().getCategory().equals("Commodity")
							|| claim.getItem().getCategory().equals("Module")
							|| claim.getItem().getCategory().equals("Charge"))) {
						logTypes.put(LogChangeType.ADDED_LOOT, Collections.singleton(new LogType(end, LogChangeType.ADDED_LOOT, 25)));
					} else {
						logTypes.put(LogChangeType.ADDED_UNKNOWN, Collections.singleton(new LogType(end, LogChangeType.ADDED_UNKNOWN, 0)));
					}
				}
				newLogs.add(new RawLog(end, claim.getItemID(), claim.getTypeID(), claim.getCount(), fromAsset == null ? null : new LogData(fromAsset), new LogData(claim), logTypes));
			}
		}
		for (List<SourceAsset> sourceAssets : sources.values()) {
			for (SourceAsset sourceAsset : sourceAssets) {
				if (sourceAsset.getAvailable() > 0) {
					LogAsset asset = sourceAsset.getAsset();
					Map<LogChangeType, Set<LogType>> logTypes = Collections.singletonMap(LogChangeType.REMOVED_UNKNOWN, Collections.singleton(new LogType(end, LogChangeType.REMOVED_UNKNOWN, 0)));
					newLogs.add(new RawLog(end, asset.getItemID(), asset.getTypeID(), asset.getCount(), new LogData(asset), null, logTypes));
				}
			}
		}
	}

	private static void added(ProfileData profileData, Date start, Date end, List<LogAsset> unknown, Set<RawLog> newLogs, Collection<LogAsset> added) {
		//Add Claims
		Set<Integer> typeIDs = new HashSet<>();
		Map<Integer, List<LogAsset>> claims = new HashMap<>();
		for (LogAsset asset : added) {
			typeIDs.add(asset.getTypeID());
			put(claims, asset.getTypeID(), asset);
		}
		//Add Sources
		Map<Integer, List<Source>> sources = new HashMap<>();
		addedTransactionsBought(sources, start, end, profileData.getTransactionsList(), typeIDs);
		addedContractAccepted(sources, start, end, profileData.getContractItemList(), typeIDs);
		addedIndustryJobsDelivered(sources, start, end, profileData.getIndustryJobsList(), typeIDs);
		//Resolve claims
		for (Map.Entry<Integer, List<LogAsset>> entry : claims.entrySet()) {
			List<Source> soruceList = sources.get(entry.getKey());
			if (soruceList == null) {
				continue;
			}
			for (LogAsset claim : entry.getValue()) {
				for (Source source : soruceList) {
					source.addClaim(claim);
				}
			}
			for (Source source : soruceList) {
				source.claim();
			}
		}
		for (List<LogAsset> list : claims.values()) {
			for (LogAsset claim : list) {
				Map<LogChangeType, Set<LogType>> logTypes = claim.getLogTypes();
				if (logTypes.isEmpty() || claim.getNeed() > 0) {
					unknown.add(claim);
					continue;
				}
				newLogs.add(new RawLog(end, claim.getItemID(), claim.getTypeID(), claim.getCount(), null, new LogData(claim), logTypes));
			}
		}
	}

	private static void removed(ProfileData profileData, Date start, Date end, List<LogAsset> unknown, Set<RawLog> newLogs, Collection<LogAsset> removed) {
		//Add Claims
		Set<Integer> typeIDs = new HashSet<>();
		Map<Integer, List<LogAsset>> claims = new HashMap<>();
		for (LogAsset asset : removed) {
			typeIDs.add(asset.getTypeID());
			put(claims, asset.getTypeID(), asset);
		}
		//Add Sources
		Map<Integer, List<Source>> removedSources = new HashMap<>();
		removedSellMarketOrderCreated(removedSources, start, end, profileData.getMarketOrdersList(), typeIDs);
		removedContractCreated(removedSources, start, end, profileData.getContractItemList(), typeIDs);
		removedContractAccepted(removedSources, start, end, profileData.getContractItemList(), typeIDs);
		removedIndustryJobsCreated(removedSources, start, end, profileData.getIndustryJobsList(), typeIDs);
		//Resolve claims
		for (Map.Entry<Integer, List<LogAsset>> entry : claims.entrySet()) {
			List<Source> soruceList = removedSources.get(entry.getKey());
			if (soruceList == null) {
				continue;
			}
			for (LogAsset claim : entry.getValue()) {
				for (Source source : soruceList) {
					source.addClaim(claim);
				}
			}
			for (Source source : soruceList) {
				source.claim();
			}
		}
		//Create Logs from Claims
		for (List<LogAsset> list : claims.values()) {
			for (LogAsset claim : list) {
				Map<LogChangeType, Set<LogType>> logTypes = claim.getLogTypes();
				if (logTypes.isEmpty() || claim.getNeed() > 0) {
					unknown.add(claim);
					continue;
				}
				newLogs.add(new RawLog(end, claim.getItemID(), claim.getTypeID(), claim.getCount(), new LogData(claim), null, logTypes));
			}
		}
	}

	private static <K, V> void put(Map<K, List<V>> map, K k, V v) {
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

	private static void removedSellMarketOrderCreated(Map<Integer, List<Source>> sources, Date start, Date end, List<MyMarketOrder> marketOrders, Set<Integer> typeIDs) {
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
			put(sources, typeID, new Source(ownerID, locationID, typeID, quantity, date, changeType));
		}
	}

	private static void removedContractCreated(Map<Integer, List<Source>> sources, Date start, Date end, List<MyContractItem> contractItems, Set<Integer> typeIDs) {
		for (MyContractItem contractItem : contractItems) {
			Date date = contractItem.getContract().getDateIssued();
			int typeID = contractItem.getTypeID();
			if (start != null && date.before(start)) { //Outside Date range
				continue;
			}
			if (date.after(end)) { //Outside Date range
				continue;
			}
			if (contractItem.getContract().isIgnoreContract()) { //Wrong contract type
				continue;
			}
			if (!contractItem.isIncluded()) { //Ignore items being bought
				continue;
			}
			if (!typeIDs.contains(typeID)) { //TypeID does not match
				continue;
			}
			long ownerID = contractItem.getContract().getIssuerID();
			long locationID = contractItem.getContract().getStartLocationID();
			int quantity = contractItem.getQuantity();
			LogChangeType changeType = LogChangeType.REMOVED_CONTRACT_CREATED;
			put(sources, typeID, new Source(ownerID, locationID, typeID, quantity, date, changeType));
		}
	}

	private static void removedContractAccepted(Map<Integer, List<Source>> sources, Date start, Date end, List<MyContractItem> contractItems, Set<Integer> typeIDs) {
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
			if (contractItem.isIncluded()) { //Ignore items bought
				continue;
			}
			if (!typeIDs.contains(typeID)) { //TypeID does not match
				continue;
			}
			long ownerID = contractItem.getContract().getAcceptorID();
			long locationID = contractItem.getContract().getStartLocationID();
			int quantity = contractItem.getQuantity();
			LogChangeType changeType = LogChangeType.REMOVED_CONTRACT_ACCEPTED;
			put(sources, typeID, new Source(ownerID, locationID, typeID, quantity, date, changeType));
		}
	}

	private static void removedIndustryJobsCreated(Map<Integer, List<Source>> sources, Date start, Date end, List<MyIndustryJob> industryJobs, Set<Integer> typeIDs) {
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
			put(sources, typeID, new Source(ownerID, locationID, typeID, quantity, date, changeType));
		}
	}

	private static void addedTransactionsBought(Map<Integer, List<Source>> sources, Date start, Date end, List<MyTransaction> transactions, Set<Integer> typeIDs) {
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
			put(sources, typeID, new Source(ownerID, locationID, typeID, quantity, date, changeType));
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

	private static void addedContractAccepted(Map<Integer, List<Source>> sources, Date start, Date end, List<MyContractItem> contractItems, Set<Integer> typeIDs) {
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
			if (!contractItem.isIncluded()) { //Ignore sold items
				continue;
			}
			if (!typeIDs.contains(typeID)) { //TypeID does not match
				continue;
			}
			long ownerID = contractItem.getContract().getAcceptorID();
			long locationID = contractItem.getContract().getStartLocationID();
			int quantity = contractItem.getQuantity();
			LogChangeType changeType = LogChangeType.ADDED_CONTRACT_ACCEPTED;
			put(sources, typeID, new Source(ownerID, locationID, typeID, quantity, date, changeType));
		}
	}

	private static void addedIndustryJobsDelivered(Map<Integer, List<Source>> sources, Date start, Date end, List<MyIndustryJob> industryJobs, Set<Integer> typeIDs) {
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
			put(sources, blueprintTypeID, new Source(ownerID, blueprintLocationID, blueprintTypeID, blueprintQuantity, date, changeType));
			if (industryJob.isManufacturing() && industryJob.getState() == IndustryJobState.STATE_DELIVERED) {
				long productLocationID = industryJob.getOutputLocationID();
				int productQuantity = industryJob.getOutputCount();
				put(sources, productTypeID, new Source(ownerID, productLocationID, productTypeID, productQuantity, date, changeType));
			}
		}
	}

	private static class LogTypeAsset {
		private final SourceAsset asset;
		private final int percent;

		public LogTypeAsset(SourceAsset asset, int percent) {
			this.asset = asset;
			this.percent = percent;
		}

		public LogAsset getAsset() {
			return asset.getAsset();
		}

		public SourceAsset getSourceAsset() {
			return asset;
		}

		public int getPercent() {
			return percent;
		}
	}

	private static class SourceAsset extends Source {

		private final LogAsset asset;
		public SourceAsset(LogAsset asset, Date date) {
			super(asset.getOwnerID(), asset.getLocationID(), asset.getTypeID(), (int) asset.getCount(), date, LogChangeType.MOVED_UNKNOWN);
			this.asset = asset;
		}

		public LogAsset getAsset() {
			return asset;
		}
	}

	private static class Source  {
		private final long ownerID;
		private final long locationID;
		private final int typeID;
		private final int quantity;
		private final Date date;
		private final LogChangeType changeType;
		private final Map<Match, List<LogAsset>> claims = new TreeMap<>();
		private int available;

		public Source(long ownerID, long locationID, int typeID, int quantity, Date date, LogChangeType changeType) {
			this.ownerID = ownerID;
			this.locationID = locationID;
			this.typeID = typeID;
			this.quantity = quantity;
			this.available = quantity
;			this.date = date;
			this.changeType = changeType;
		}

		public long getOwnerID() {
			return ownerID;
		}

		public long getLocationID() {
			return locationID;
		}

		public int getTypeID() {
			return typeID;
		}

		public int getQuantity() {
			return quantity;
		}

		public Date getDate() {
			return date;
		}

		public LogChangeType getChangeType() {
			return changeType;
		}

		public void takeAll() {
			available = 0;
		}

		public void take(int missing) {
			available = available - missing;
		}

		public int getAvailable() {
			return available;
		}

		private int match(LogAsset claim) {
			int match = 25; //Match TypeID
			if (claim.getLocationID() == getLocationID()) {
				match = match + 50;
			}
			if (claim.getCount() == getQuantity()) {
				match = match + 25;
			}
			if (match == 100) {
				match--; //99% is max
			}
			return match;
		}

		public void addClaim(LogAsset claim) {
			if (claim.getOwnerID() != getOwnerID()) {
				return; //Wrong owner
			}
			Match match = new Match(match(claim));
			List<LogAsset> claimList = claims.get(match);
			if (claimList == null) {
				claimList = new ArrayList<LogAsset>();
				claims.put(match, claimList);
			}
			claimList.add(claim);
		}

		public void claim() {
			for (Map.Entry<Match, List<LogAsset>> entry : claims.entrySet()) {
				List<LogAsset> claimList = entry.getValue();
				Match match = entry.getKey();
				Collections.sort(claimList, new ClaimComparator(getAvailable())); //Sort by need
				for (LogAsset claim : claimList) {
					if (getAvailable() == 0) {
						return; //Nothing left...
					}
					if (claim.getNeed() >= getAvailable()) { //Add all
						claim.addCount(this, match.getPercent(), getAvailable());
						takeAll();
						return; //Nothing left...
					} else { //Add part of the count
						int missing = claim.getNeed();
						claim.addCount(this, match.getPercent(), missing);
						take(missing);
					}
				}
			}
		}
	}

	private static class ClaimComparator implements Comparator<LogAsset> {

		private final int target;

		public ClaimComparator(int target) {
			this.target = target;
		}

		@Override
		public int compare(LogAsset o1, LogAsset o2) {
			int t1 = target - o1.getNeed();
			int t2 = target - o2.getNeed();
			if (t1 < t2) {
				return 1;
			} else if  (t1 > t2){
				return -1;
			} else {
				return 0;
			}
		}
		
	}

	private static class Match implements Comparable<Match> {
		private final int percent;

		public Match(int percent) {
			this.percent = percent;
		}

		public int getPercent() {
			return percent;
		}

		@Override
		public int compareTo(Match o) {
			return o.percent - this.percent;
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 83 * hash + this.percent;
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
			final Match other = (Match) obj;
			if (this.percent != other.percent) {
				return false;
			}
			return true;
		}
	}

	public static class LogAsset {
		private final long ownerID;
		private final long locationID;
		private final Integer typeID;
		private final long count;
		private final Long itemID;
		private final Integer flagID;
		private final String container = "";
		private final List<MyAsset> parents;
		private final Item item;
		private int needed;
		private final Map<LogChangeType, Set<LogType>> logType = new HashMap<>();
		private final List<LogTypeAsset> logTypeAssets = new ArrayList<>();

		public LogAsset(MyAsset asset) {
			this.ownerID = asset.getOwnerID();
			this.locationID = asset.getLocationID();
			this.typeID = asset.getTypeID();
			this.count = asset.getCount();
			this.itemID = asset.getItemID();
			this.flagID = asset.getFlagID();
			this.parents = asset.getParents();
			this.item = asset.getItem();
			this.needed = (int) asset.getCount();
		}

		public LogAsset(LogAsset asset, long count) {
			this.ownerID = asset.getOwnerID();
			this.locationID = asset.getLocationID();
			this.typeID = asset.getTypeID();
			this.count = count;
			this.itemID = asset.getItemID();
			this.flagID = asset.getFlagID();
			this.parents = asset.getParents();
			this.item = asset.getItem();
			this.needed = (int) count;
		}

		public long getOwnerID() {
			return ownerID;
		}

		public long getLocationID() {
			return locationID;
		}

		public Integer getTypeID() {
			return typeID;
		}

		public long getCount() {
			return count;
		}

		public Long getItemID() {
			return itemID;
		}

		public Integer getFlagID() {
			return flagID;
		}

		public String getContainer() {
			return container;
		}

		public List<MyAsset> getParents() {
			return parents;
		}

		public Item getItem() {
			return item;
		}

		public Map<LogChangeType, Set<LogType>> getLogTypes() {
			return logType;
		}

		public List<LogTypeAsset> getLogTypeAssets() {
			return logTypeAssets;
		}

		public void addCount(Source source, int percent, int count) {
			putSet(logType, source.getChangeType(), new LogType(source.getDate(), source.getChangeType(), percent));
			needed = needed - count;
			if (source instanceof SourceAsset) {
				logTypeAssets.add(new LogTypeAsset((SourceAsset)source, percent));
			}
		}

		private int getNeed() { //Claim optimization
			return needed;
		}
	}
}
