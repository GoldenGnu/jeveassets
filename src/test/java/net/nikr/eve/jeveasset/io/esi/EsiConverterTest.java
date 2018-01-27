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
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptions;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptionsGetter;
import net.nikr.eve.jeveasset.io.shared.ConverterTestUtil;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class EsiConverterTest extends TestUtil {

	@Test
	public void testToAccountBalance() {
		testToAccountBalance(null);
	}

	public void testToAccountBalance(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			List<MyAccountBalance> accountBalances = EsiConverter.toAccountBalance(options.getDouble(), ConverterTestUtil.getEsiOwner(options), options.getInteger());
			ConverterTestUtil.testValues(accountBalances.get(0), options, esi);
		}
	}

	@Test
	public void testToAccountBalanceCorporation() {
		testToAccountBalanceCorporation(null);
	}

	@Test
	public void testToAccountBalanceCorporationOptional() {
		testToAccountBalanceCorporation(CorporationWalletsResponse.class);
	}

	public void testToAccountBalanceCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CorporationWalletsResponse walletsResponse = new CorporationWalletsResponse();
			ConverterTestUtil.setValues(walletsResponse, options, esi);
			List<MyAccountBalance> accountBalances = EsiConverter.toAccountBalanceCorporation(Collections.singletonList(walletsResponse), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(accountBalances.get(0), options, esi);
		}
	}

	@Test
	public void testToAssets() {
		testToAssets(null);
	}

	@Test
	public void testToAssetsOptional() {
		testToAssets(CharacterAssetsResponse.class);
	}

	public void testToAssets(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			List<CharacterAssetsResponse> assetsResponses = new ArrayList<CharacterAssetsResponse>();

			CharacterAssetsResponse rootAssetsResponse = new CharacterAssetsResponse();
			assetsResponses.add(rootAssetsResponse);
			ConverterTestUtil.setValues(rootAssetsResponse, options, esi);

			CharacterAssetsResponse childAssetsResponse = new CharacterAssetsResponse();
			assetsResponses.add(childAssetsResponse);
			ConverterTestUtil.setValues(childAssetsResponse, options, esi);
			childAssetsResponse.setItemId(childAssetsResponse.getItemId() + 1);
			childAssetsResponse.setLocationId(rootAssetsResponse.getItemId());

			EsiOwner owner = ConverterTestUtil.getEsiOwner(options);
			List<MyAsset> assets = EsiConverter.toAssets(assetsResponses, owner);
			if (!assets.isEmpty()) {
				assertEquals("List empty @" + options.getIndex(), 1, assets.size());
				ConverterTestUtil.testValues(assets.get(0), options, esi);

				assertEquals("List empty @" + options.getIndex(), 1, assets.get(0).getAssets().size());
				MyAsset childAsset = assets.get(0).getAssets().get(0);
				childAsset.setItemID(childAsset.getItemID() - 1);
				ConverterTestUtil.testValues(childAsset, options, esi);
			} else {
				assertEquals(assets.size(), 0);
				assertTrue(DataConverter.ignoreAsset(new RawAsset(rootAssetsResponse), owner));
			}
		}
	}

	@Test
	public void testToAssetsCorporation() {
		testToAssetsCorporation(null);
	}

	@Test
	public void testToAssetsCorporationOptional() {
		testToAssetsCorporation(CorporationAssetsResponse.class);
	}

	public void testToAssetsCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			List<CorporationAssetsResponse> assetsResponses = new ArrayList<CorporationAssetsResponse>();

			CorporationAssetsResponse rootAssetsResponse = new CorporationAssetsResponse();
			assetsResponses.add(rootAssetsResponse);
			ConverterTestUtil.setValues(rootAssetsResponse, options, esi);

			CorporationAssetsResponse childAssetsResponse = new CorporationAssetsResponse();
			assetsResponses.add(childAssetsResponse);
			ConverterTestUtil.setValues(childAssetsResponse, options, esi);
			childAssetsResponse.setItemId(childAssetsResponse.getItemId() + 1);
			childAssetsResponse.setLocationId(rootAssetsResponse.getItemId());

			EsiOwner owner = ConverterTestUtil.getEsiOwner(options);
			List<MyAsset> assets = EsiConverter.toAssetsCorporation(assetsResponses, owner);
			if (!assets.isEmpty()) {
				assertEquals("List empty @" + options.getIndex(), 1, assets.size());
				ConverterTestUtil.testValues(assets.get(0), options, esi);

				assertEquals("List empty @" + options.getIndex(), 1, assets.get(0).getAssets().size());
				MyAsset childAsset = assets.get(0).getAssets().get(0);
				childAsset.setItemID(childAsset.getItemID() - 1);
				ConverterTestUtil.testValues(childAsset, options, esi);
			} else {
				assertEquals(assets.size(), 0);
				assertTrue(DataConverter.ignoreAsset(new RawAsset(rootAssetsResponse), owner));
			}
		}
	}

	@Test
	public void testToBlueprints() {
		testBlueprints(null);
	}

	@Test
	public void testToBlueprintsOptional() {
		testBlueprints(CharacterBlueprintsResponse.class);
	}

	private void testBlueprints(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CharacterBlueprintsResponse blueprintsResponse = new CharacterBlueprintsResponse();
			ConverterTestUtil.setValues(blueprintsResponse, options, esi);
			Map<Long, RawBlueprint> blueprints = EsiConverter.toBlueprints(Collections.singletonList(blueprintsResponse));
			ConverterTestUtil.testValues(blueprints.values().iterator().next(), options, esi);
		}
	}

	@Test
	public void testToBlueprintsCorporation() {
		testToBlueprintsCorporation(null);
	}

	@Test
	public void testToBlueprintsCorporationOptional() {
		testToBlueprintsCorporation(CorporationBlueprintsResponse.class);
	}

	public void testToBlueprintsCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CorporationBlueprintsResponse blueprintsResponse = new CorporationBlueprintsResponse();
			ConverterTestUtil.setValues(blueprintsResponse, options, esi);
			Map<Long, RawBlueprint> blueprints = EsiConverter.toBlueprintsCorporation(Collections.singletonList(blueprintsResponse));
			ConverterTestUtil.testValues(blueprints.values().iterator().next(), options, esi);
		}
	}

	@Test
	public void testToIndustryJobs() {
		testToIndustryJobs(null);
	}

	@Test
	public void testToIndustryJobsOptional() {
		testToIndustryJobs(CharacterIndustryJobsResponse.class);
	}

	private void testToIndustryJobs(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CharacterIndustryJobsResponse industryJobsResponse = new CharacterIndustryJobsResponse();
			ConverterTestUtil.setValues(industryJobsResponse, options, esi);
			List<MyIndustryJob> industryJobs = EsiConverter.toIndustryJobs(Collections.singletonList(industryJobsResponse), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(industryJobs.get(0), options, esi);
		}
	}
	@Test
	public void testToIndustryJobsCorporation() {
		testToIndustryJobsCorporation(null);
	}

	@Test
	public void testToIndustryJobsCorporationOptional() {
		testToIndustryJobsCorporation(CorporationIndustryJobsResponse.class);
	}

	private void testToIndustryJobsCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CorporationIndustryJobsResponse industryJobsResponse = new CorporationIndustryJobsResponse();
			ConverterTestUtil.setValues(industryJobsResponse, options, esi);
			List<MyIndustryJob> industryJobs = EsiConverter.toIndustryJobsCorporation(Collections.singletonList(industryJobsResponse), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(industryJobs.get(0), options, esi);
		}
	}

	@Test
	public void testToJournals() {
		testToJournals(null);
	}

	@Test
	public void testToJournalsOptional() {
		testToJournals(CharacterWalletJournalResponse.class);
	}

	private void testToJournals(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CharacterWalletJournalResponse journalResponse = new CharacterWalletJournalResponse();
			ConverterTestUtil.setValues(journalResponse, options, esi);
			Set<MyJournal> journals = EsiConverter.toJournals(Collections.singletonList(journalResponse), ConverterTestUtil.getEsiOwner(options), options.getInteger(), false);
			ConverterTestUtil.testValues(journals.iterator().next(), options, esi);
		}
	}

	@Test
	public void testToJournalsCorporation() {
		testToJournalsCorporation(null);
	}

	@Test
	public void testToJournalsCorporationOptional() {
		testToJournalsCorporation(CorporationWalletJournalResponse.class);
	}

	public void testToJournalsCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CorporationWalletJournalResponse journalResponse = new CorporationWalletJournalResponse();
			ConverterTestUtil.setValues(journalResponse, options, esi);
			Set<MyJournal> journals = EsiConverter.toJournalsCorporation(Collections.singletonList(journalResponse), ConverterTestUtil.getEsiOwner(options), options.getInteger(), false);
			ConverterTestUtil.testValues(journals.iterator().next(), options, esi);
		}
	}

	@Test
	public void testToContracts() {
		testToContracts(null);
	}

	@Test
	public void testToContractsOptional() {
		testToContracts(CharacterContractsResponse.class);
	}

	public void testToContracts(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CharacterContractsResponse contractsResponse = new CharacterContractsResponse();
			ConverterTestUtil.setValues(contractsResponse, options, esi);
			Map<MyContract, List<MyContractItem>> contracts = EsiConverter.toContracts(Collections.singletonList(contractsResponse), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(contracts.keySet().iterator().next(), options, esi);
		}
	}

	@Test
	public void testToContractsCorporation() {
		testToContractsCorporation(null);
	}

	@Test
	public void testToContractsCorporationOptional() {
		testToContractsCorporation(CorporationContractsResponse.class);
	}

	public void testToContractsCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CorporationContractsResponse contractsResponse = new CorporationContractsResponse();
			ConverterTestUtil.setValues(contractsResponse, options, esi);
			Map<MyContract, List<MyContractItem>> contracts = EsiConverter.toContractsCorporation(Collections.singletonList(contractsResponse), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(contracts.keySet().iterator().next(), options, esi);
		}
	}

	@Test
	public void testToContractItems() {
		testToContractItems(null);
	}

	@Test
	public void testToContractItemsOptional() {
		testToContractItems(CharacterContractsItemsResponse.class);
	}

	public void testToContractItems(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CharacterContractsItemsResponse contractsItemsResponse = new CharacterContractsItemsResponse();
			ConverterTestUtil.setValues(contractsItemsResponse, options, esi);
			Map<MyContract, List<MyContractItem>> contractItems = EsiConverter.toContractItems(ConverterTestUtil.getMyContract(false, true, options), Collections.singletonList(contractsItemsResponse), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(contractItems.values().iterator().next().get(0), options, esi);
		}
	}

	@Test
	public void testToContractItemsCorporation() {
		testToContractItemsCorporation(null);
	}

	@Test
	public void testToContractItemsCorporationOptional() {
		testToContractItemsCorporation(CorporationContractsItemsResponse.class);
	}

	public void testToContractItemsCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CorporationContractsItemsResponse contractsItemsResponse = new CorporationContractsItemsResponse();
			ConverterTestUtil.setValues(contractsItemsResponse, options, esi);
			Map<MyContract, List<MyContractItem>> contractItems = EsiConverter.toContractItemsCorporation(ConverterTestUtil.getMyContract(false, true, options), Collections.singletonList(contractsItemsResponse), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(contractItems.values().iterator().next().get(0), options, esi);
		}
	}

	@Test
	public void testToMarketOrders() {
		testToMarketOrders(null);
	}

	@Test
	public void testToMarketOrdersOptional() {
		testToMarketOrders(CharacterOrdersResponse.class);
	}

	public void testToMarketOrders(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CharacterOrdersResponse ordersResponse = new CharacterOrdersResponse();
			ConverterTestUtil.setValues(ordersResponse, options, esi);
			Set<MyMarketOrder> marketOrders = EsiConverter.toMarketOrders(Collections.singletonList(ordersResponse), new ArrayList<CharacterOrdersHistoryResponse>(), ConverterTestUtil.getEsiOwner(options), false);
			ConverterTestUtil.testValues(marketOrders.iterator().next(), options, esi);
		}
	}

	@Test
	public void testToMarketOrdersHistory() {
		testToMarketOrdersHistory(null);
	}

	@Test
	public void testToMarketOrdersHistoryOptional() {
		testToMarketOrdersHistory(CharacterOrdersHistoryResponse.class);
	}

	public void testToMarketOrdersHistory(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CharacterOrdersHistoryResponse ordersHistoryResponse = new CharacterOrdersHistoryResponse();
			ConverterTestUtil.setValues(ordersHistoryResponse, options, esi);
			Set<MyMarketOrder> marketOrders = EsiConverter.toMarketOrders(new ArrayList<CharacterOrdersResponse>(), Collections.singletonList(ordersHistoryResponse), ConverterTestUtil.getEsiOwner(options), false);
			ConverterTestUtil.testValues(marketOrders.iterator().next(), options, esi);
		}
	}

	@Test
	public void testToMarketOrdersCorporation() {
		testToMarketOrdersCorporation(null);
	}

	@Test
	public void testToMarketOrdersCorporationOptional() {
		testToMarketOrdersCorporation(CorporationOrdersResponse.class);
	}

	public void testToMarketOrdersCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CorporationOrdersResponse ordersResponse = new CorporationOrdersResponse();
			ConverterTestUtil.setValues(ordersResponse, options, esi);
			Set<MyMarketOrder> marketOrders = EsiConverter.toMarketOrdersCorporation(Collections.singletonList(ordersResponse), new ArrayList<CorporationOrdersHistoryResponse>(), ConverterTestUtil.getEsiOwner(options), false);
			ConverterTestUtil.testValues(marketOrders.iterator().next(), options, esi);
		}
	}

	@Test
	public void testToMarketOrdersHistoryCorporation() {
		testToMarketOrdersHistoryCorporation(null);
	}

	@Test
	public void testToMarketOrdersHistoryCorporationOptional() {
		testToMarketOrdersHistoryCorporation(CorporationOrdersHistoryResponse.class);
	}

	public void testToMarketOrdersHistoryCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CorporationOrdersHistoryResponse ordersHistoryResponse = new CorporationOrdersHistoryResponse();
			ConverterTestUtil.setValues(ordersHistoryResponse, options, esi);
			Set<MyMarketOrder> marketOrders = EsiConverter.toMarketOrdersCorporation(new ArrayList<CorporationOrdersResponse>(), Collections.singletonList(ordersHistoryResponse), ConverterTestUtil.getEsiOwner(options), false);
			ConverterTestUtil.testValues(marketOrders.iterator().next(), options, esi);
		}
	}

	@Test
	public void testToTransaction() {
		testToTransaction(null);
	}

	@Test
	public void testToTransactionOptional() {
		testToTransaction(CharacterWalletTransactionsResponse.class);
	}

	public void testToTransaction(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CharacterWalletTransactionsResponse transactionsResponse = new CharacterWalletTransactionsResponse();
			ConverterTestUtil.setValues(transactionsResponse, options, esi);
			Set<MyTransaction> transactions = EsiConverter.toTransaction(Collections.singletonList(transactionsResponse), ConverterTestUtil.getEsiOwner(options), options.getInteger(), false);
			ConverterTestUtil.testValues(transactions.iterator().next(), options, esi);
		}
	}

	@Test
	public void testToTransactionCorporation() {
		testToTransactionCorporation(null);
	}

	@Test
	public void testToTransactionCorporationOptional() {
		testToTransactionCorporation(CorporationWalletTransactionsResponse.class);
	}

	public void testToTransactionCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CorporationWalletTransactionsResponse transactionsResponse = new CorporationWalletTransactionsResponse();
			ConverterTestUtil.setValues(transactionsResponse, options, esi);
			Set<MyTransaction> transactions = EsiConverter.toTransactionCorporation(Collections.singletonList(transactionsResponse), ConverterTestUtil.getEsiOwner(options), options.getInteger(), false);
			MyTransaction myTransaction = transactions.iterator().next();
			myTransaction.setPersonal(true);
			ConverterTestUtil.testValues(transactions.iterator().next(), options, esi);
		}
	}
	
	@Test
	public void testToAssetsShip() {
		testToAssetsShip(null, null);
	}

	@Test
	public void testToAssetsShipOptional() {
		testToAssetsShip(CharacterShipResponse.class, CharacterLocationResponse.class);
	}

	public void testToAssetsShip(Class<?> esiShip, Class<?> esiLocation) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CharacterShipResponse shipType = new CharacterShipResponse();
			ConverterTestUtil.setValues(shipType, options, esiShip);
			CharacterLocationResponse shipLocation = new CharacterLocationResponse();
			ConverterTestUtil.setValues(shipLocation, options, esiLocation);
			MyAsset asset = EsiConverter.toAssetsShip(shipType, shipLocation, ConverterTestUtil.getEsiOwner(options));
			asset.setQuantity(options.getInteger()); //Always 1 -> set to 5 to pass test
			asset.setItemFlag(options.getItemFlag()); //Always "None" -> set to option value to pass test
			ConverterTestUtil.testValues(asset, options, null);
		}
	}

	@Test
	public void testToContainersLogCorporation() {
		testToContainersLogCorporation(null);
	}

	@Test
	public void testToContainersLogCorporationOptional() {
		testToContainersLogCorporation(CorporationContainersLogsResponse.class);
	}

	public void testToContainersLogCorporation(Class<?> esi) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			CorporationContainersLogsResponse containersLogsResponse = new CorporationContainersLogsResponse();
			ConverterTestUtil.setValues(containersLogsResponse, options, esi);
			List<RawContainerLog> containerLogs = EsiConverter.toContainersLogCorporation(Collections.singletonList(containersLogsResponse), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(containerLogs.iterator().next(), options, esi);
		}
	}
}
