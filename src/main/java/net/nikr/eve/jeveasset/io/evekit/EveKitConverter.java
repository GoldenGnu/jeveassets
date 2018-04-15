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
package net.nikr.eve.jeveasset.io.evekit;

import static net.nikr.eve.jeveasset.io.shared.DataConverter.convertRawAccountBalance;

import enterprises.orbital.evekit.client.model.AccountBalance;
import enterprises.orbital.evekit.client.model.Asset;
import enterprises.orbital.evekit.client.model.Blueprint;
import enterprises.orbital.evekit.client.model.CharacterLocation;
import enterprises.orbital.evekit.client.model.CharacterShip;
import enterprises.orbital.evekit.client.model.Contract;
import enterprises.orbital.evekit.client.model.ContractItem;
import enterprises.orbital.evekit.client.model.IndustryJob;
import enterprises.orbital.evekit.client.model.MarketOrder;
import enterprises.orbital.evekit.client.model.WalletJournal;
import enterprises.orbital.evekit.client.model.WalletTransaction;
import java.util.ArrayList;
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
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.nikr.eve.jeveasset.io.shared.DataConverter;


public final class EveKitConverter extends DataConverter {

	private EveKitConverter() {
	}

	public static List<MyAccountBalance> toAccountBalance(List<AccountBalance> responses, OwnerType owner) {
		List<RawAccountBalance> accountBalances = new ArrayList<RawAccountBalance>();
		for (AccountBalance response : responses) {
			accountBalances.add(new RawAccountBalance(response));
		}
		return convertRawAccountBalance(accountBalances, owner);
	}

	public static List<MyAsset> toAssets(List<Asset> responses, OwnerType owner) {
		List<RawAsset> rawAssets = new ArrayList<RawAsset>();
		for (Asset response : responses) {
			rawAssets.add(new RawAsset(response));
		}
		return convertRawAssets(rawAssets, owner);
	}

	public static MyAsset toAssetsShip(CharacterShip shipType, CharacterLocation shipLocation, OwnerType owner) {
		return toMyAsset(new RawAsset(shipType, shipLocation), owner, new ArrayList<MyAsset>());
	}

	public static Map<Long, RawBlueprint> toBlueprints(List<Blueprint> responses) {
		Map<Long, RawBlueprint> blueprints = new HashMap<Long, RawBlueprint>();
		for (Blueprint blueprint : responses) {
			blueprints.put(blueprint.getItemID(), new RawBlueprint(blueprint));
		}
		return blueprints;
	}

	public static Map<MyContract, List<MyContractItem>> toContracts(List<Contract> responses, OwnerType owner) {
		List<RawContract> rawContracts = new ArrayList<RawContract>();
		for (Contract response : responses) {
			rawContracts.add(new RawContract(response));
		}
		return convertRawContracts(rawContracts, owner);
	}

	public static Map<MyContract, List<MyContractItem>> toContractItems(MyContract contract, List<ContractItem> responses, OwnerType owner) {
		List<RawContractItem> rawContractItems = new ArrayList<RawContractItem>();
		for (ContractItem response : responses) {
			rawContractItems.add(new RawContractItem(response));
		}
		return convertRawContractItems(contract, rawContractItems, owner);
	}

	public static List<MyIndustryJob> toIndustryJobs(List<IndustryJob> responses, OwnerType owner) {
		List<RawIndustryJob> rawIndustryJobs = new ArrayList<RawIndustryJob>();
		for (IndustryJob response : responses) {
			rawIndustryJobs.add(new RawIndustryJob(response));
		}
		return convertRawIndustryJobs(rawIndustryJobs, owner);
	}

	public static Set<MyJournal> toJournals(List<WalletJournal> responses, OwnerType owner, boolean saveHistory) {
		List<RawJournal> rawIndustryJobs = new ArrayList<RawJournal>();
		for (WalletJournal response : responses) {
			rawIndustryJobs.add(new RawJournal(response));
		}
		return convertRawJournals(rawIndustryJobs, owner, saveHistory);
	}

	public static Set<MyMarketOrder> toMarketOrders(List<MarketOrder> responses, OwnerType owner, boolean saveHistory) {
		List<RawMarketOrder> rawMarketOrders = new ArrayList<RawMarketOrder>();
		for (MarketOrder response : responses) {
			rawMarketOrders.add(new RawMarketOrder(response));
		}
		return convertRawMarketOrders(rawMarketOrders, owner, saveHistory);
	}

	public static Set<MyTransaction> toTransactions(List<WalletTransaction> responses, OwnerType owner, boolean saveHistory) {
		List<RawTransaction> rawTransactions = new ArrayList<RawTransaction>();
		for (WalletTransaction response : responses) {
			rawTransactions.add(new RawTransaction(response));
		}
		return convertRawTransactions(rawTransactions, owner, saveHistory);
	}
}
