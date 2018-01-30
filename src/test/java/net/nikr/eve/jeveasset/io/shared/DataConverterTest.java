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
package net.nikr.eve.jeveasset.io.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.nikr.eve.jeveasset.i18n.General;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class DataConverterTest extends TestUtil {

	@Test
	public void testAssetIndustryJob() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyIndustryJob industryJob = ConverterTestUtil.getMyIndustryJob(ConverterTestUtil.getEsiOwner(options), false, true, options);
			industryJob.setJobID(industryJob.getJobID() + 1);
			industryJob.setStationID(options.getLocationTypeEveApi());
			List<MyAsset> assets = DataConverter.assetIndustryJob(Collections.singletonList(industryJob), true);
			if (assets.isEmpty()) {
				assertTrue(industryJob.getState() == IndustryJobState.STATE_DELIVERED
				|| industryJob.getState() == IndustryJobState.STATE_CANCELLED
				|| industryJob.getState() == IndustryJobState.STATE_REVERTED);
			} else {
				MyAsset asset = assets.get(0);
				//Quantity
				assertEquals((Object) asset.getQuantity(), -2);
				asset.setQuantity(options.getInteger());
				assertEquals(asset.getItemFlag().getFlagName(), General.get().industryJobFlag());
				assertEquals(asset.getItemFlag().getFlagText(), General.get().industryJobFlag());
				//ItemFlag
				asset.setItemFlag(options.getItemFlag());
				ConverterTestUtil.testValues(assets.get(0), options);
			}
		}
	}

	@Test
	public void testAssetMarketOrder() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyMarketOrder marketOrder = ConverterTestUtil.getMyMarketOrder(ConverterTestUtil.getEsiOwner(options), false, true, options);
			marketOrder.setOrderID(marketOrder.getOrderID() + 1);
			marketOrder.setLocationID(options.getLocationTypeEveApi());
			List<MyAsset> assets = DataConverter.assetMarketOrder(Collections.singletonList(marketOrder), true, true);
			if (assets.isEmpty()) {
				assertFalse(marketOrder.isActive());
			} else {
				MyAsset asset = assets.get(0);
				//Singleton
				assertEquals(asset.isSingleton(), false);
				asset.setSingleton(true);
				//ItemFlag
				assertEquals(asset.getItemFlag().getFlagName(), General.get().marketOrderBuyFlag());
				assertEquals(asset.getItemFlag().getFlagText(), General.get().marketOrderBuyFlag());
				asset.setItemFlag(options.getItemFlag());
				ConverterTestUtil.testValues(asset, options);
			}
		}
	}

	@Test
	public void testAssetContracts() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyContractItem contractItem = ConverterTestUtil.getMyContractItem(ConverterTestUtil.getMyContract(false, true, options), false, true, options);
			contractItem.setRecordID(contractItem.getRecordID() + 1);
			contractItem.getContract().setStartLocationID(options.getLocationTypeEveApi());
			contractItem.getContract().setEndLocationID(options.getLocationTypeEveApi());
			final Map<Long, OwnerType> owners = new HashMap<>();
			EsiOwner owner = ConverterTestUtil.getEsiOwner(options);
			owners.put(owner.getOwnerID(), owner);
			List<MyAsset> assets = DataConverter.assetContracts(Collections.singletonList(contractItem), owners, true, true);
			if (assets.isEmpty()) {
				assertTrue((contractItem.getContract().getStatus() != RawContract.ContractStatus.OUTSTANDING
						&& contractItem.getContract().getStatus() != RawContract.ContractStatus.IN_PROGRESS)
						|| contractItem.getContract().isIgnoreContract());

			} else {
				MyAsset asset = assets.get(0);
				assertEquals(asset.getItemFlag().getFlagName(), General.get().contractIncluded());
				assertEquals(asset.getItemFlag().getFlagText(), General.get().contractIncluded());
				asset.setItemFlag(options.getItemFlag());
				ConverterTestUtil.testValues(asset, options);
			}
		}
	}

	@Test
	public void testConvertRawAccountBalance() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			List<MyAccountBalance> accountBalances = DataConverter.convertRawAccountBalance(Collections.singletonList(ConverterTestUtil.getRawAccountBalance(options)), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(accountBalances.get(0), options);
		}
	}

	@Test
	public void testToMyAccountBalance() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyAccountBalance accountBalance = DataConverter.toMyAccountBalance(ConverterTestUtil.getRawAccountBalance(options), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(accountBalance, options);
		}
	}

	@Test
	public void testConvertRawAssets() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			List<RawAsset> rawAssets = new ArrayList<RawAsset>();
			RawAsset rootRawAsset = ConverterTestUtil.getRawAsset(false, options);
			rawAssets.add(rootRawAsset);

			RawAsset childRawAsset = ConverterTestUtil.getRawAsset(false, options);
			rawAssets.add(childRawAsset);
			childRawAsset.setItemID(childRawAsset.getItemID() + 1);
			childRawAsset.setLocationID(rootRawAsset.getItemID());

			EsiOwner owner = ConverterTestUtil.getEsiOwner(options);
			List<MyAsset> assets = DataConverter.convertRawAssets(rawAssets, owner);
			if (!assets.isEmpty()) {
				assertEquals("List empty @" + options.getIndex(), 1, assets.size());
				ConverterTestUtil.testValues(assets.get(0), options);

				assertEquals("List empty @" + options.getIndex(), 1, assets.get(0).getAssets().size());
				MyAsset childMyAsset = assets.get(0).getAssets().get(0);
				childMyAsset.setItemID(childMyAsset.getItemID() - 1);
				ConverterTestUtil.testValues(childMyAsset, options);
			} else {
				assertEquals(assets.size(), 0);
				assertTrue(DataConverter.ignoreAsset(rootRawAsset, owner));
			}
		}
	}

	@Test
	public void testToMyAsset() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			RawAsset rawAsset = ConverterTestUtil.getRawAsset(false, options);
			EsiOwner owner = ConverterTestUtil.getEsiOwner(options);
			MyAsset asset = DataConverter.toMyAsset(rawAsset, owner, new ArrayList<MyAsset>());
			if (asset != null) {
				assertNotNull("Object null @" + options.getIndex(), asset);
				ConverterTestUtil.testValues(asset, options);
			} else {
				assertNull(asset);
				assertTrue(DataConverter.ignoreAsset(rawAsset, owner));
			}
		}
	}

	@Test
	public void testConvertRawContracts() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			Map<MyContract, List<MyContractItem>> contracts = DataConverter.convertRawContracts(Collections.singletonList(ConverterTestUtil.getRawContract(false, options)), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(contracts.keySet().iterator().next(), options);
		}
	}

	@Test
	public void testConvertRawContractItems() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			Map<MyContract, List<MyContractItem>> contractItems = DataConverter.convertRawContractItems(ConverterTestUtil.getMyContract(false, true, options), Collections.singletonList(ConverterTestUtil.getRawContractItem(false, options)), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(contractItems.values().iterator().next().get(0), options);
		}
	}

	@Test
	public void testToMyContract() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyContract contract = DataConverter.toMyContract(ConverterTestUtil.getRawContract(false, options));
			ConverterTestUtil.testValues(contract, options);
		}
	}

	@Test
	public void testToMyContractItem() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyContractItem contractItem = DataConverter.toMyContractItem(ConverterTestUtil.getRawContractItem(false, options), ConverterTestUtil.getMyContract(false, true, options));
			ConverterTestUtil.testValues(contractItem, options);
		}
	}

	@Test
	public void testConvertRawIndustryJobs() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			List<MyIndustryJob> industryJobs = DataConverter.convertRawIndustryJobs(Collections.singletonList(ConverterTestUtil.getRawIndustryJob(false, options)), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(industryJobs.get(0), options);
		}

	}

	@Test
	public void testToMyIndustryJob() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyIndustryJob industryJob = DataConverter.toMyIndustryJob(ConverterTestUtil.getRawIndustryJob(false, options), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(industryJob, options);
		}
	}

	@Test
	public void testConvertRawJournals() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			Set<MyJournal> journals = DataConverter.convertRawJournals(Collections.singletonList(ConverterTestUtil.getRawJournal(false, options)), ConverterTestUtil.getEsiOwner(options), false);
			ConverterTestUtil.testValues(journals.iterator().next(), options);
		}
	}

	@Test
	public void testConvertRawJournalsHistory() {
		testConvertRawJournalsHistory(true);
		testConvertRawJournalsHistory(false);
	}

	public void testConvertRawJournalsHistory(boolean saveHistory) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			EsiOwner esiOwner = ConverterTestUtil.getEsiOwner(options);
			for (long i = 1; i <= 10; i++) {
				RawJournal rawJournal = ConverterTestUtil.getRawJournal(true, options);
				MyJournal myJournal = DataConverter.toMyJournal(rawJournal, esiOwner);
				myJournal.setRefID(i);
				esiOwner.getJournal().add(myJournal);
			}
			List<RawJournal> rawJournals = new ArrayList<RawJournal>();
			for (long i = 11; i <= 20; i++) {
				RawJournal rawJournal = ConverterTestUtil.getRawJournal(true, options);
				rawJournal.setRefID(i);
				rawJournals.add(rawJournal);
			}
			esiOwner.setJournal(DataConverter.convertRawJournals(rawJournals, esiOwner, saveHistory));
			if (saveHistory) {
				assertThat(esiOwner.getJournal().size(), is(20));
			} else {
				assertThat(esiOwner.getJournal().size(), is(10));
				assertTrue(esiOwner.getJournal().iterator().next().getRefID() > 10L);
			}
		}
	}

	@Test
	public void testToMyJournal() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyJournal journal = DataConverter.toMyJournal(ConverterTestUtil.getRawJournal(false, options), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(journal, options);
		}
	}

	@Test
	public void testConvertRawMarketOrders() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			Set<MyMarketOrder> marketOrders = DataConverter.convertRawMarketOrders(Collections.singletonList(ConverterTestUtil.getRawMarketOrder(false, options)), ConverterTestUtil.getEsiOwner(options), false);
			ConverterTestUtil.testValues(marketOrders.iterator().next(), options);
		}
	}

	@Test
	public void testConvertRawMarketOrdersHistory() {
		testConvertRawMarketOrdersHistory(true);
		testConvertRawMarketOrdersHistory(false);
	}

	public void testConvertRawMarketOrdersHistory(boolean saveHistory) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			EsiOwner esiOwner = ConverterTestUtil.getEsiOwner(options);
			for (long i = 1; i <= 10; i++) {
				RawMarketOrder rawMarketOrder = ConverterTestUtil.getRawMarketOrder(true, options);
				MyMarketOrder myMarketOrder = DataConverter.toMyMarketOrder(rawMarketOrder, esiOwner);
				myMarketOrder.setOrderID(i);
				esiOwner.getMarketOrders().add(myMarketOrder);
				
			}
			List<RawMarketOrder> rawMarketOrders = new ArrayList<RawMarketOrder>();
			for (long i = 11; i <= 20; i++) {
				RawMarketOrder rawMarketOrder = ConverterTestUtil.getRawMarketOrder(true, options);
				rawMarketOrder.setOrderID(i);
				rawMarketOrders.add(rawMarketOrder);
			}
			esiOwner.setMarketOrders(DataConverter.convertRawMarketOrders(rawMarketOrders, esiOwner, saveHistory));
			if (saveHistory) {
				assertThat(esiOwner.getMarketOrders().size(), is(20));
			} else {
				assertThat(esiOwner.getMarketOrders().size(), is(10));
				assertTrue(esiOwner.getMarketOrders().iterator().next().getOrderID() > 10L);
			}
		}
	}

	@Test
	public void testToMyMarketOrder() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyMarketOrder marketOrder = DataConverter.toMyMarketOrder(ConverterTestUtil.getRawMarketOrder(false, options), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(marketOrder, options);
		}

	}

	@Test
	public void testConvertRawTransactions() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			Set<MyTransaction> transactions = DataConverter.convertRawTransactions(Collections.singletonList(ConverterTestUtil.getRawTransaction(false, options)), ConverterTestUtil.getEsiOwner(options), false);
			ConverterTestUtil.testValues(transactions.iterator().next(), options);
		}
	}

	@Test
	public void testConvertRawTransactionsHistory() {
		testConvertRawTransactionsHistory(true);
		testConvertRawTransactionsHistory(false);
	}

	public void testConvertRawTransactionsHistory(boolean saveHistory) {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			EsiOwner esiOwner = ConverterTestUtil.getEsiOwner(options);
			for (long i = 1; i <= 10; i++) {
				RawTransaction rawTransaction = ConverterTestUtil.getRawTransaction(true, options);
				MyTransaction myTransaction = DataConverter.toMyTransaction(rawTransaction, esiOwner);
				myTransaction.setTransactionID(i);
				esiOwner.getTransactions().add(myTransaction);
			}
			List<RawTransaction> rawTransactions = new ArrayList<RawTransaction>();
			for (long i = 11; i <= 20; i++) {
				RawTransaction rawTransaction = ConverterTestUtil.getRawTransaction(true, options);
				rawTransaction.setTransactionID(i);
				rawTransactions.add(rawTransaction);
			}
			esiOwner.setTransactions(DataConverter.convertRawTransactions(rawTransactions, esiOwner, saveHistory));
			if (saveHistory) {
				assertThat(esiOwner.getTransactions().size(), is(20));
			} else {
				assertThat(esiOwner.getTransactions().size(), is(10));
				assertTrue(esiOwner.getTransactions().iterator().next().getTransactionID() > 10L);
			}
		}
	}

	@Test
	public void testToMyTransaction() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyTransaction transaction = DataConverter.toMyTransaction(ConverterTestUtil.getRawTransaction(false, options), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(transaction, options);
		}
	}

}
