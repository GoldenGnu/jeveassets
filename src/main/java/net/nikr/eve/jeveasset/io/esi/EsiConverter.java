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
package net.nikr.eve.jeveasset.io.esi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawAccountBalance;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CharacterBlueprintsResponse;
import net.troja.eve.esi.model.CharacterContractsItemsResponse;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterShipResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CharacterWalletTransactionsResponse;
import net.troja.eve.esi.model.CorporationAssetsResponse;
import net.troja.eve.esi.model.CorporationBlueprintsResponse;
import net.troja.eve.esi.model.CorporationContainersLogsResponse;
import net.troja.eve.esi.model.CorporationContractsItemsResponse;
import net.troja.eve.esi.model.CorporationContractsResponse;
import net.troja.eve.esi.model.CorporationIndustryJobsResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;
import net.troja.eve.esi.model.CorporationWalletTransactionsResponse;
import net.troja.eve.esi.model.CorporationWalletsResponse;


public class EsiConverter extends DataConverter {

	private EsiConverter() {
	}

	public static List<MyAccountBalance> toAccountBalance(Double responses, OwnerType owner, Integer accountKey) {
		return convertRawAccountBalance(Collections.singletonList(new RawAccountBalance(responses, accountKey)), owner);
	}

	public static List<MyAccountBalance> toAccountBalanceCorporation(List<CorporationWalletsResponse> responses, OwnerType owner) {
		List<RawAccountBalance> rawAccountBalances = new ArrayList<RawAccountBalance>();
		for (CorporationWalletsResponse response : responses) {
			rawAccountBalances.add(new RawAccountBalance(response));
		}
		return convertRawAccountBalance(rawAccountBalances, owner);
	}

	public static List<MyAsset> toAssets(List<CharacterAssetsResponse> responses, OwnerType owner) {
		List<RawAsset> rawAssets = new ArrayList<RawAsset>();
		for (CharacterAssetsResponse response : responses) {
			rawAssets.add(new RawAsset(response));
		}
		return convertRawAssets(rawAssets, owner);
	}

	public static List<MyAsset> toAssetsCorporation(List<CorporationAssetsResponse> responses, OwnerType owner) {
		List<RawAsset> rawAssets = new ArrayList<RawAsset>();
		for (CorporationAssetsResponse response : responses) {
			rawAssets.add(new RawAsset(response));
		}
		return convertRawAssets(rawAssets, owner);
	}

	public static MyAsset toAssetsShip(CharacterShipResponse shipType, CharacterLocationResponse shipLocation, OwnerType owner) {
		return toMyAsset(new RawAsset(shipType, shipLocation), owner, new ArrayList<MyAsset>());
	}

	public static Map<Long, RawBlueprint> toBlueprints(List<CharacterBlueprintsResponse> responses) {
		Map<Long, RawBlueprint> rawBlueprints = new HashMap<Long, RawBlueprint>();
		for (CharacterBlueprintsResponse blueprint : responses) {
			rawBlueprints.put(blueprint.getItemId(), new RawBlueprint(blueprint));
		}
		return rawBlueprints;
	}

	public static Map<Long, RawBlueprint> toBlueprintsCorporation(List<CorporationBlueprintsResponse> responses) {
		Map<Long, RawBlueprint> rawBlueprints = new HashMap<Long, RawBlueprint>();
		for (CorporationBlueprintsResponse blueprint : responses) {
			rawBlueprints.put(blueprint.getItemId(), new RawBlueprint(blueprint));
		}
		return rawBlueprints;
	}

	public static List<MyIndustryJob> toIndustryJobs(List<CharacterIndustryJobsResponse> responses, OwnerType owner) {
		List<RawIndustryJob> rawIndustryJobs = new ArrayList<RawIndustryJob>();
		for (CharacterIndustryJobsResponse response : responses) {
			rawIndustryJobs.add(new RawIndustryJob(response));
		}
		return convertRawIndustryJobs(rawIndustryJobs, owner);
	}

	public static List<MyIndustryJob> toIndustryJobsCorporation(List<CorporationIndustryJobsResponse> responses, OwnerType owner) {
		List<RawIndustryJob> rawIndustryJobs = new ArrayList<RawIndustryJob>();
		for (CorporationIndustryJobsResponse response : responses) {
			rawIndustryJobs.add(new RawIndustryJob(response));
		}
		return convertRawIndustryJobs(rawIndustryJobs, owner);
	}

	public static Set<MyJournal> toJournals(List<CharacterWalletJournalResponse> responses, OwnerType owner, Integer accountKey, boolean saveHistory) {
		List<RawJournal> rawJournals = new ArrayList<RawJournal>();
		for (CharacterWalletJournalResponse response : responses) {
			rawJournals.add(new RawJournal(response, accountKey));
		}
		return convertRawJournals(rawJournals, owner, saveHistory);
	}

	public static Set<MyJournal> toJournalsCorporation(List<CorporationWalletJournalResponse> responses, OwnerType owner, Integer accountKey, boolean saveHistory) {
		List<RawJournal> rawJournals = new ArrayList<RawJournal>();
		for (CorporationWalletJournalResponse response : responses) {
			rawJournals.add(new RawJournal(response, accountKey));
		}
		return convertRawJournals(rawJournals, owner, saveHistory);
	}

	public static Map<MyContract, List<MyContractItem>> toContracts(List<CharacterContractsResponse> responses, OwnerType owner) {
		List<RawContract> rawContracts = new ArrayList<RawContract>();
		for (CharacterContractsResponse response : responses) {
			rawContracts.add(new RawContract(response));
		}
		return convertRawContracts(rawContracts, owner);
	}

	public static Map<MyContract, List<MyContractItem>> toContractsCorporation(List<CorporationContractsResponse> responses, OwnerType owner) {
		List<RawContract> rawContracts = new ArrayList<RawContract>();
		for (CorporationContractsResponse response : responses) {
			rawContracts.add(new RawContract(response));
		}
		return convertRawContracts(rawContracts, owner);
	}

	public static Map<MyContract, List<MyContractItem>> toContractItems(MyContract contract, List<CharacterContractsItemsResponse> responses, OwnerType owner) {
		List<RawContractItem> rawContractItems = new ArrayList<RawContractItem>();
		for (CharacterContractsItemsResponse response : responses) {
			rawContractItems.add(new RawContractItem(response));
		}
		return convertRawContractItems(contract, rawContractItems, owner);
	}

	public static Map<MyContract, List<MyContractItem>> toContractItemsCorporation(MyContract contract, List<CorporationContractsItemsResponse> responses, OwnerType owner) {
		List<RawContractItem> rawContractItems = new ArrayList<RawContractItem>();
		for (CorporationContractsItemsResponse response : responses) {
			rawContractItems.add(new RawContractItem(response));
		}
		return convertRawContractItems(contract, rawContractItems, owner);
	}

	public static Set<MyMarketOrder> toMarketOrders(List<CharacterOrdersResponse> responses, List<CharacterOrdersHistoryResponse> responsesHistory, OwnerType owner, boolean saveHistory) {
		List<RawMarketOrder> rawMarketOrders = new ArrayList<RawMarketOrder>();
		for (CharacterOrdersResponse response : responses) {
			rawMarketOrders.add(new RawMarketOrder(response));
		}
		for (CharacterOrdersHistoryResponse response : responsesHistory) {
			rawMarketOrders.add(new RawMarketOrder(response));
		}
		return convertRawMarketOrders(rawMarketOrders, owner, saveHistory);
	}

	public static Set<MyMarketOrder> toMarketOrdersCorporation(List<CorporationOrdersResponse> responses, List<CorporationOrdersHistoryResponse> responsesHistory, OwnerType owner, boolean saveHistory) {
		List<RawMarketOrder> rawMarketOrders = new ArrayList<RawMarketOrder>();
		for (CorporationOrdersResponse response : responses) {
			rawMarketOrders.add(new RawMarketOrder(response));
		}
		for (CorporationOrdersHistoryResponse response : responsesHistory) {
			rawMarketOrders.add(new RawMarketOrder(response));
		}
		return convertRawMarketOrders(rawMarketOrders, owner, saveHistory);
	}

	public static Set<MyTransaction> toTransaction(List<CharacterWalletTransactionsResponse> responses, OwnerType owner, Integer accountKey, boolean saveHistory) {
		List<RawTransaction> rawTransactions = new ArrayList<RawTransaction>();
		for (CharacterWalletTransactionsResponse response : responses) {
			rawTransactions.add(new RawTransaction(response, accountKey));
		}
		return convertRawTransactions(rawTransactions, owner, saveHistory);
	}

	public static Set<MyTransaction> toTransactionCorporation(List<CorporationWalletTransactionsResponse> responses, OwnerType owner, Integer accountKey, boolean saveHistory) {
		List<RawTransaction> rawTransactions = new ArrayList<RawTransaction>();
		for (CorporationWalletTransactionsResponse response : responses) {
			rawTransactions.add(new RawTransaction(response, accountKey));
		}
		return convertRawTransactions(rawTransactions, owner, saveHistory);
	}

	public static List<RawContainerLog> toContainersLogCorporation(List<CorporationContainersLogsResponse> responses, OwnerType owner) {
		List<RawContainerLog> rawContainersLogs = new ArrayList<RawContainerLog>();
		for (CorporationContainersLogsResponse response : responses) {
			rawContainersLogs.add(new RawContainerLog(response, owner));
		}
		return rawContainersLogs;
	}
}
