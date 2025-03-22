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
package net.nikr.eve.jeveasset.io.shared;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.accounts.SimpleOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyExtraction;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.api.my.MySkill;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawAccountBalance;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawExtraction;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.Change;
import net.nikr.eve.jeveasset.data.api.raw.RawMining;
import net.nikr.eve.jeveasset.data.api.raw.RawSkill;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.local.profile.ProfileContracts;
import net.nikr.eve.jeveasset.io.local.profile.ProfileDatabase;
import net.nikr.eve.jeveasset.io.local.profile.ProfileIndustryJobs;
import net.nikr.eve.jeveasset.io.local.profile.ProfileJournals;
import net.nikr.eve.jeveasset.io.local.profile.ProfileMarketOrders;
import net.nikr.eve.jeveasset.io.local.profile.ProfileMining;
import net.nikr.eve.jeveasset.io.local.profile.ProfileTransactions;
import net.nikr.eve.jeveasset.io.local.profile.ProfileConnectionData;

public abstract class DataConverter {

	public static List<MyAsset> assetIndustryJob(final Collection<MyIndustryJob> industryJobs, boolean includeManufacturing, boolean includeCopying) {
		List<MyAsset> assets = new ArrayList<>();
		for (MyIndustryJob industryJob : industryJobs) {
			if (industryJob.isNotDeliveredToAssets()) {
				//Blueprint
				if (industryJob.isRemovedFromAssets()) {
					MyAsset asset = new MyAsset(industryJob, false);
					assets.add(asset);
				}
				//Manufacturing Output
				if (includeManufacturing && industryJob.isManufacturing() && industryJob.getProductTypeID() != null) {
					MyAsset product = new MyAsset(industryJob, true);
					assets.add(product);
				}
				//Copy Output
				if (includeCopying && industryJob.isCopying()) {
					for (int i = 0; i < industryJob.getRuns(); i++) {
						MyAsset product = new MyAsset(industryJob, true);
						assets.add(product);
					}
				}
			}
		}
		return assets;
	}

	public static List<MyAsset> assetMarketOrder(final Collection<MyMarketOrder> marketOrders, boolean includeSellOrders, boolean includeBuyOrders) {
		List<MyAsset> assets = new ArrayList<>();
		for (MyMarketOrder marketOrder : marketOrders) {
			if (marketOrder.isActive() && marketOrder.getVolumeRemain() > 0
					&& ((!marketOrder.isBuyOrder() && includeSellOrders)
					|| (marketOrder.isBuyOrder() && includeBuyOrders))) {
				MyAsset asset = new MyAsset(marketOrder);
				assets.add(asset);
			}
		}
		return assets;
	}

	public static List<MyAsset> assetContracts(final Collection<MyContractItem> contractItems, final Map<Long, OwnerType> owners, boolean includeSellContracts, boolean includeBuyContracts) {
		List<MyAsset> list = new ArrayList<>();
		//Only includes issuer buying and selling items
		//TODO Could add issuer bought/sold and acceptor bought/sold items to the assets list
		for (MyContractItem contractItem : contractItems) {
			SimpleOwner issuer;
			if (contractItem.getContract().isForCorp()) {
				issuer = new SimpleOwner() {
					@Override
					public long getOwnerID() {
						if (Settings.get().isAssetsContractsOwnerCorporation() ||Settings.get().isAssetsContractsOwnerBoth()) {
							return contractItem.getContract().getIssuerCorpID();
						} else {
							return contractItem.getContract().getIssuerID();
						}
					}

					@Override
					public String getOwnerName() {
						if (Settings.get().isAssetsContractsOwnerCorporation()) {
							return ApiIdConverter.getOwnerName(contractItem.getContract().getIssuerCorpID());
						} else if (Settings.get().isAssetsContractsOwnerBoth()) {
							String characterName =  ApiIdConverter.getOwnerName(contractItem.getContract().getIssuerID());
							String corporationName =  ApiIdConverter.getOwnerName(contractItem.getContract().getIssuerCorpID());
							return GuiShared.get().contractCorporationOwner(corporationName, characterName);
						} else { //Character
							return ApiIdConverter.getOwnerName(contractItem.getContract().getIssuerID());
						}
					}

					@Override
					public boolean isCorporation() {
						return Settings.get().isAssetsContractsOwnerCorporation()
								|| Settings.get().isAssetsContractsOwnerBoth();
						
					}
				};
			} else {
				issuer = owners.get(contractItem.getContract().getIssuerID());
			}
			if (contractItem.getContract().isOpen() //Not completed
					&& issuer != null //Owned
					&& contractItem.getContract().isItemContract() //Not courier
					&& ((contractItem.isIncluded() && includeSellContracts) //Sell
					|| (!contractItem.isIncluded() && includeBuyContracts))) { //Buy
				MyAsset asset = new MyAsset(contractItem, issuer);
				list.add(asset);
			}
		}
		return list;
	}

	protected static List<MyAccountBalance> convertRawAccountBalance(List<RawAccountBalance> rawAccountBalances, OwnerType owner) {
		List<MyAccountBalance> accountBalances = new ArrayList<>();
		for (RawAccountBalance rawAccountBalance : rawAccountBalances) { //Lookup by ItemID
			accountBalances.add(toMyAccountBalance(rawAccountBalance, owner));
		}
		return accountBalances;
	}

	public static MyAccountBalance toMyAccountBalance(RawAccountBalance rawAccountBalance, OwnerType owner) {
		return new MyAccountBalance(rawAccountBalance, owner);
	}

	public static List<MyAsset> toRawAssets(List<RawAsset> rawAssets, OwnerType owner) {
		return convertRawAssets(rawAssets, owner);
	}

	protected static List<MyAsset> convertRawAssets(List<RawAsset> rawAssets, OwnerType owner) {
		List<MyAsset> assets = new ArrayList<>();

		Map<Long, RawAsset> lookup = new HashMap<>();
		Map<Long, List<RawAsset>> childMap = new HashMap<>();
		List<RawAsset> root = new ArrayList<>();
		for (RawAsset rawAsset : rawAssets) { //Lookup by ItemID
			lookup.put(rawAsset.getItemID(), rawAsset);
			childMap.put(rawAsset.getItemID(), new ArrayList<>());
		}
		for (RawAsset rawAsset : rawAssets) { //Create child map
			RawAsset parent = lookup.get(rawAsset.getLocationID());
			if (parent != null) { //Is Child Asset
				childMap.get(parent.getItemID()).add(rawAsset);
			} else { //Is Root Asset
				root.add(rawAsset);
			}
		}
		for (RawAsset rawAsset : root) {
			MyAsset asset = deepAsset(rawAsset, owner, childMap, null);
			if (asset != null) {
				assets.add(asset);
			}
		}
		return assets;
	}

	private static MyAsset deepAsset(RawAsset rawAsset, OwnerType owner, Map<Long, List<RawAsset>> childMap, MyAsset parent) {
		List<MyAsset> parents = new ArrayList<>();
		if (parent != null) {
			parents.addAll(parent.getParents());
			parents.add(parent);
		}
		MyAsset asset = toMyAsset(rawAsset, owner, parents);
		if (asset != null) {
			for (RawAsset child : childMap.get(rawAsset.getItemID())) {
				MyAsset childAsset = deepAsset(child, owner, childMap, asset);
				if (childAsset != null) {
					asset.addAsset(childAsset);
				}
			}
		}
		return asset;
	}

	public static MyAsset toMyAsset(RawAsset rawAsset, OwnerType owner, List<MyAsset> parents) {
		Item item = ApiIdConverter.getItemUpdate(rawAsset.getTypeID());
		if (!parents.isEmpty()) { //Update locationID from ItemID to locationID
			MyAsset rootAsset = parents.get(0);
			rawAsset.setLocationID(rootAsset.getLocationID());
		}
		//Ignore some stuff
		if (ignoreAsset(rawAsset, owner)) {
			return null;
		}
		return new MyAsset(rawAsset, item, owner, parents);
	}

	public static boolean ignoreAsset(RawAsset rawAsset, OwnerType owner) {
		return rawAsset.getItemFlag().getFlagID() == 7 //Skill
				|| rawAsset.getItemFlag().getFlagID() == 61 //Skill In Training
				|| rawAsset.getItemFlag().getFlagID() == 88 //Booster
				|| rawAsset.getItemFlag().getFlagID() == 89 //Implant
				|| rawAsset.getLocationID() == owner.getOwnerID(); //Other stuff
	}

	protected static Map<MyContract, List<MyContractItem>> convertRawContracts(List<RawContract> rawContracts, OwnerType owner, boolean saveHistory) {
		Map<MyContract, List<MyContractItem>> contracts = new HashMap<>();
		for (RawContract rawContract : rawContracts) {
			MyContract contract = toMyContract(rawContract);
			List<MyContractItem> contractItems = owner.getContracts().get(contract); //Load ContractItems
			if (contractItems == null) { //New
				contractItems = new ArrayList<>();
			} else { //Old, update contract items
				for (MyContractItem contractItem : contractItems) {
					contractItem.setContract(contract);
				}
			}
			contracts.put(contract, contractItems);
		}
		if (saveHistory) {
			ProfileDatabase.update(new ProfileConnectionData<MyContract>(new HashSet<>(contracts.keySet())) {
				@Override
				public boolean update(Connection connection, Collection<MyContract> data) {
					return ProfileContracts.updateContracts(connection, owner.getOwnerID(), data);
				}
			});
			for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
				MyContract contract = entry.getKey();
				if (!contracts.containsKey(contract)) {
					contract.archive();
					contracts.put(contract, entry.getValue());
				}
			}
		}
		return contracts;
	}

	protected static Map<MyContract, List<MyContractItem>> convertRawContractItems(Map<MyContract, List<RawContractItem>> rawContractItems, OwnerType owner, boolean saveHistory) {
		Map<MyContract, List<MyContractItem>> contracts = new HashMap<>();
		for (Map.Entry<MyContract, List<RawContractItem>> entry : rawContractItems.entrySet()) {
			List<MyContractItem> contractItems = new ArrayList<>();
			for (RawContractItem response : entry.getValue()) {
				contractItems.add(toMyContractItem(response, entry.getKey()));
			}
			contracts.put(entry.getKey(), contractItems);
		}
		if (saveHistory) {
			ProfileDatabase.update(new ProfileConnectionData<List<MyContractItem>>(contracts.values()) {
				@Override
				public boolean update(Connection connection, Collection<List<MyContractItem>> data) {
					return ProfileContracts.updateContractItems(connection, data);
				}
			});
		}
		return contracts;
	}

	public static MyContract toMyContract(RawContract rawContract) {
		return new MyContract(rawContract);
	}

	public static MyContractItem toMyContractItem(RawContractItem rawContractItem, MyContract contract) {
		Item item = ApiIdConverter.getItemUpdate(rawContractItem.getTypeID());
		return new MyContractItem(rawContractItem, contract, item);
	}

	protected static Set<MyIndustryJob> convertRawIndustryJobs(List<RawIndustryJob> rawIndustryJobs, OwnerType owner, boolean saveHistory) {
		Set<MyIndustryJob> industryJobs = new HashSet<>();
		for (RawIndustryJob rawIndustryJob : rawIndustryJobs) {
			industryJobs.add(toMyIndustryJob(rawIndustryJob, owner));
		}
		if (saveHistory) {
			ProfileDatabase.update(new ProfileConnectionData<MyIndustryJob>(industryJobs) {
				@Override
				public boolean update(Connection connection, Collection<MyIndustryJob> data) {
					return ProfileIndustryJobs.updateIndustryJobs(connection, owner.getOwnerID(), data);
				}
			});
			for (MyIndustryJob industryJob : owner.getIndustryJobs()) {
				industryJob.archive();
				industryJobs.add(industryJob);
			}
		}
		return industryJobs;
	}

	public static MyIndustryJob toMyIndustryJob(RawIndustryJob rawIndustryJob, OwnerType owner) {
		Item item = ApiIdConverter.getItemUpdate(rawIndustryJob.getBlueprintTypeID());
		Item output = ApiIdConverter.getItemUpdate(rawIndustryJob.getProductTypeID());
		return new MyIndustryJob(rawIndustryJob, item, output, owner);
	}

	protected static Set<MyJournal> convertRawJournals(List<RawJournal> rawJournals, OwnerType owner, boolean saveHistory) {
		Set<MyJournal> journals = new HashSet<>();
		for (RawJournal rawJournal : rawJournals) {
			journals.add(toMyJournal(rawJournal, owner));
		}
		if (saveHistory) {
			ProfileDatabase.update(new ProfileConnectionData<MyJournal>(journals) {
				@Override
				public boolean update(Connection connection, Collection<MyJournal> data) {
					return ProfileJournals.updateJournals(connection, owner.getOwnerID(), data);
				}
			});
			journals.addAll(owner.getJournal());
		}
		return journals;
	}

	public static MyJournal toMyJournal(RawJournal rawJournal, OwnerType owner) {
		return new MyJournal(rawJournal, owner);
	}

	protected static Set<MyMarketOrder> convertRawMarketOrders(List<RawMarketOrder> rawMarketOrders, OwnerType owner, boolean saveHistory) {
		Set<MyMarketOrder> marketOrders = new HashSet<>();
		Map<Long, Set<Change>> changed = new HashMap<>();
		for (MyMarketOrder marketOrder : owner.getMarketOrders()) {
			changed.put(marketOrder.getOrderID(), marketOrder.getChanges());
		}
		for (RawMarketOrder rawMarketOrder : rawMarketOrders) {
			MyMarketOrder marketOrder = toMyMarketOrder(rawMarketOrder, owner);
			marketOrders.add(marketOrder);
			marketOrder.addChanges(changed.get(marketOrder.getOrderID()));
		}
		if (saveHistory) {
			ProfileDatabase.update(new ProfileConnectionData<MyMarketOrder>(marketOrders) {
				@Override
				public boolean update(Connection connection, Collection<MyMarketOrder> data) {
					return ProfileMarketOrders.updateMarketOrders(connection, owner.getOwnerID(), data);
				}
			});
			for (MyMarketOrder marketOrder : owner.getMarketOrders()) {
				marketOrder.archive();
				marketOrders.add(marketOrder);
			}
		}
		return marketOrders;
	}

	public static MyMarketOrder toMyMarketOrder(RawMarketOrder rawMarketOrder, OwnerType owner) {
		Item item = ApiIdConverter.getItemUpdate(rawMarketOrder.getTypeID());
		return new MyMarketOrder(rawMarketOrder, item, owner);
	}

	protected static Set<MyTransaction> convertRawTransactions(List<RawTransaction> rawTransactions, OwnerType owner, boolean saveHistory) {
		Set<MyTransaction> transactions = new HashSet<>();
		for (RawTransaction rawTransaction : rawTransactions) {
			transactions.add(toMyTransaction(rawTransaction, owner));
		}
		if (saveHistory) {
			ProfileDatabase.update(new ProfileConnectionData<MyTransaction>(transactions) {
				@Override
				public boolean update(Connection connection, Collection<MyTransaction> data) {
					return ProfileTransactions.updateTransactions(connection, owner.getOwnerID(), data);
				}
			});
			transactions.addAll(owner.getTransactions());
		}
		return transactions;
	}

	public static MyTransaction toMyTransaction(RawTransaction rawTransaction, OwnerType owner) {
		Item item = ApiIdConverter.getItemUpdate(rawTransaction.getTypeID());
		return new MyTransaction(rawTransaction, item, owner);
	}

	protected static List<MySkill> convertRawSkills(List<RawSkill> rawSkills, OwnerType owner) {
		List<MySkill> skills = new ArrayList<>();
		for (RawSkill rawSkill : rawSkills) {
			skills.add(toMySkill(rawSkill, owner));
		}
		return skills;
	}

	public static MySkill toMySkill(RawSkill rawSkill, OwnerType owner) {
		Item item = ApiIdConverter.getItemUpdate(rawSkill.getTypeID());
		return new MySkill(rawSkill, item, owner.getOwnerName());
	}

	protected static Set<MyMining> convertRawMining(List<RawMining> rawMinings, OwnerType owner, boolean saveHistory) {
		Set<MyMining> minings = new HashSet<>();
		for (RawMining rawMining : rawMinings) {
			minings.add(toMyMining(rawMining));
		}
		if (saveHistory) {
			ProfileDatabase.update(new ProfileConnectionData<MyMining>(minings) {
				@Override
				public boolean update(Connection connection, Collection<MyMining> data) {
					return ProfileMining.updateMinings(connection, owner.getOwnerID(), data);
				}
			});
			minings.addAll(owner.getMining());
		}
		return minings;
	}

	public static MyMining toMyMining(RawMining rawMining) {
		Item item = ApiIdConverter.getItemUpdate(rawMining.getTypeID());
		MyLocation location = ApiIdConverter.getLocation(rawMining.getLocationID());
		return new MyMining(rawMining, item, location);
	}

	protected static Set<MyExtraction> convertRawExtraction(List<RawExtraction> rawExtractions, OwnerType owner, boolean saveHistory) {
		Set<MyExtraction> extractions = new HashSet<>();
		for (RawExtraction rawMining : rawExtractions) {
			extractions.add(toMyExtraction(rawMining));
		}
		if (saveHistory) {
			ProfileDatabase.update(new ProfileConnectionData<MyExtraction>(new ArrayList<>(extractions)) {
				@Override
				public boolean update(Connection connection, Collection<MyExtraction> data) {
					return ProfileMining.updateExtractions(connection, owner.getOwnerID(), data);
				}
			});
			extractions.addAll(owner.getExtractions());
		}
		return extractions;
	}

	public static MyExtraction toMyExtraction(RawExtraction rawExtraction) {
		MyLocation moon = ApiIdConverter.getLocation(rawExtraction.getMoonID());
		return new MyExtraction(rawExtraction, moon);
	}
}
