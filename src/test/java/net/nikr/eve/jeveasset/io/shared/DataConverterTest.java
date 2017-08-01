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
package net.nikr.eve.jeveasset.io.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.OwnerType;
import net.nikr.eve.jeveasset.data.esi.EsiOwner;
import net.nikr.eve.jeveasset.data.raw.RawAsset;
import net.nikr.eve.jeveasset.data.raw.RawContract;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.gui.tabs.journal.MyJournal;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.transaction.MyTransaction;
import net.nikr.eve.jeveasset.i18n.General;
import org.junit.Test;

public class DataConverterTest extends TestUtil {

	@Test
	public void testAssetIndustryJob() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MyIndustryJob industryJob = ConverterTestUtil.getMyIndustryJob(ConverterTestUtil.getEsiOwner(options), false, true, options);
			industryJob.setBlueprintID(industryJob.getBlueprintID() + 1);
			industryJob.setStationID(options.getLocationTypeEveApi());
			List<MyAsset> assets = DataConverter.assetIndustryJob(Collections.singletonList(industryJob));
			if (assets.isEmpty()) {
				assertEquals(industryJob.getState(), IndustryJobState.STATE_DELIVERED);
			} else {
				MyAsset asset = assets.get(0);
				//Quantity
				assertEquals((Object) asset.getQuantity(), -1);
				asset.setQuantity(options.getInteger());
				assertEquals(asset.getItemFlag(), ApiIdConverter.getFlag(0));
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
			contractItem.getContract().setContractID(contractItem.getContract().getContractID() + 1);
			contractItem.getContract().setStartLocationID(options.getLocationTypeEveApi());
			contractItem.getContract().setEndLocationID(options.getLocationTypeEveApi());
			final Map<Long, OwnerType> owners = new HashMap<>();
			EsiOwner owner = ConverterTestUtil.getEsiOwner(options);
			owners.put(owner.getOwnerID(), owner);
			List<MyAsset> assets = DataConverter.assetContracts(Collections.singletonList(contractItem), owners, true, true);
			if (assets.isEmpty()) {
				assertTrue((contractItem.getContract().getStatus() != RawContract.ContractStatus.OUTSTANDING
						&& contractItem.getContract().getStatus() != RawContract.ContractStatus.IN_PROGRESS)
						|| contractItem.getContract().isCourier());

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

			List<MyAsset> assets = DataConverter.convertRawAssets(rawAssets, ConverterTestUtil.getEsiOwner(options));
			if (rootRawAsset.getItemFlag().getFlagID() != 89) {
				assertEquals("List empty @" + options.getIndex(), 1, assets.size());
				ConverterTestUtil.testValues(assets.get(0), options);

				assertEquals("List empty @" + options.getIndex(), 1, assets.get(0).getAssets().size());
				MyAsset childMyAsset = assets.get(0).getAssets().get(0);
				childMyAsset.setItemID(childMyAsset.getItemID() - 1);
				ConverterTestUtil.testValues(childMyAsset, options);
			} else {
				assertEquals(assets.size(), 0);
			}
		}
	}

	@Test
	public void testToMyAsset() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			RawAsset rawAsset = ConverterTestUtil.getRawAsset(false, options);
			MyAsset asset = DataConverter.toMyAsset(rawAsset, ConverterTestUtil.getEsiOwner(options), new ArrayList<MyAsset>());
			if (rawAsset.getItemFlag().getFlagID() != 89) {
				assertNotNull("Object null @" + options.getIndex(), asset);
				ConverterTestUtil.testValues(asset, options);
			} else {
				assertNull(asset);
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
			Set<MyJournal> journals = DataConverter.convertRawJournals(Collections.singletonList(ConverterTestUtil.getRawJournal(false, options)), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(journals.iterator().next(), options);
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
			List<MyMarketOrder> marketOrders = DataConverter.convertRawMarketOrders(Collections.singletonList(ConverterTestUtil.getRawMarketOrder(false, options)), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(marketOrders.get(0), options);
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
			Set<MyTransaction> transactions = DataConverter.convertRawTransactions(Collections.singletonList(ConverterTestUtil.getRawTransaction(false, options)), ConverterTestUtil.getEsiOwner(options));
			ConverterTestUtil.testValues(transactions.iterator().next(), options);
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
