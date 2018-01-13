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
package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.model.shared.AccountBalance;
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.model.shared.Blueprint;
import com.beimin.eveapi.model.shared.Contract;
import com.beimin.eveapi.model.shared.ContractItem;
import com.beimin.eveapi.model.shared.IndustryJob;
import com.beimin.eveapi.model.shared.JournalEntry;
import com.beimin.eveapi.model.shared.MarketOrder;
import com.beimin.eveapi.model.shared.WalletTransaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
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
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptions;
import net.nikr.eve.jeveasset.io.shared.ConverterTestOptionsGetter;
import net.nikr.eve.jeveasset.io.shared.ConverterTestUtil;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class EveApiConverterTest extends TestUtil {

	public EveApiConverterTest() {
	}

	@Test
	public void testToAccountBalance() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			AccountBalance accountBalance = new AccountBalance();
			ConverterTestUtil.setValues(accountBalance, options);
			List<MyAccountBalance> accountBalances = EveApiConverter.toAccountBalance(Collections.singletonList(accountBalance), ConverterTestUtil.getEveApiOwner(options));
			ConverterTestUtil.testValues(accountBalances.get(0), options);
		}
	}

	@Test
	public void testToAssets() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			List<Asset> assetsList = new ArrayList<Asset>();
			Asset rootAsset = new Asset();
			assetsList.add(rootAsset);
			ConverterTestUtil.setValues(rootAsset, options);

			Asset childAsset = new Asset();
			assetsList.add(childAsset);
			ConverterTestUtil.setValues(childAsset, options);
			childAsset.setItemID(childAsset.getItemID() + 1);
			childAsset.setLocationID(rootAsset.getItemID());

			EveApiOwner owner = ConverterTestUtil.getEveApiOwner(options);
			List<MyAsset> assets = EveApiConverter.toAssets(assetsList, owner);
			if (!assets.isEmpty()) {
				assertEquals("List empty @" + options.getIndex(), 1, assets.size());
				ConverterTestUtil.testValues(assets.get(0), options);

				assertEquals("List empty @" + options.getIndex(), 1, assets.get(0).getAssets().size());
				MyAsset childMyAsset = assets.get(0).getAssets().get(0);
				childMyAsset.setItemID(childMyAsset.getItemID() - 1);
				ConverterTestUtil.testValues(childMyAsset, options);
			} else {
				assertEquals(assets.size(), 0);
				assertTrue(DataConverter.ignoreAsset(new RawAsset(rootAsset), owner));
			}
		}
	}

	@Test
	public void testToBlueprints() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			Blueprint blueprint = new Blueprint();
			ConverterTestUtil.setValues(blueprint, options);
			Map<Long, RawBlueprint> blueprints = EveApiConverter.toBlueprints(Collections.singletonList(blueprint));
			ConverterTestUtil.testValues(blueprints.values().iterator().next(), options);
		}
	}

	@Test
	public void testToIndustryJobs() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			IndustryJob industryJob = new IndustryJob();
			ConverterTestUtil.setValues(industryJob, options);
			List<MyIndustryJob> industryJobs = EveApiConverter.toIndustryJobs(Collections.singletonList(industryJob), ConverterTestUtil.getEveApiOwner(options));
			ConverterTestUtil.testValues(industryJobs.get(0), options);
		}
	}

	@Test
	public void testToJournal() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			JournalEntry journalEntry = new JournalEntry();
			ConverterTestUtil.setValues(journalEntry, options);
			Set<MyJournal> journals = EveApiConverter.toJournal(Collections.singletonList(journalEntry), ConverterTestUtil.getEveApiOwner(options), options.getInteger(), false);
			ConverterTestUtil.testValues(journals.iterator().next(), options);
		}
	}

	@Test
	public void testToContracts() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			Contract contract = new Contract();
			ConverterTestUtil.setValues(contract, options);
			Map<MyContract, List<MyContractItem>> contracts = EveApiConverter.toContracts(Collections.singletonList(contract), ConverterTestUtil.getEveApiOwner(options));
			ConverterTestUtil.testValues(contracts.keySet().iterator().next(), options);
		}
	}

	@Test
	public void testToContractItems() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			ContractItem contractItem = new ContractItem();
			ConverterTestUtil.setValues(contractItem, options);
			Map<MyContract, List<MyContractItem>> contractItems = EveApiConverter.toContractItems(ConverterTestUtil.getMyContract(false, false, options), Collections.singletonList(contractItem), ConverterTestUtil.getEveApiOwner(options));
			ConverterTestUtil.testValues(contractItems.values().iterator().next().get(0), options);
		}
	}

	@Test
	public void testToMarketOrders() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			MarketOrder marketOrder = new MarketOrder();
			ConverterTestUtil.setValues(marketOrder, options);
			Set<MyMarketOrder> marketOrders = EveApiConverter.toMarketOrders(Collections.singletonList(marketOrder), ConverterTestUtil.getEveApiOwner(options), false);
			ConverterTestUtil.testValues(marketOrders.iterator().next(), options);
		}
	}

	@Test
	public void testToTransactions() {
		for (ConverterTestOptions options : ConverterTestOptionsGetter.getConverterOptions()) {
			WalletTransaction transaction = new WalletTransaction();
			ConverterTestUtil.setValues(transaction, options);
			Set<MyTransaction> transactions = EveApiConverter.toTransactions(Collections.singletonList(transaction), ConverterTestUtil.getEveApiOwner(options), options.getInteger(), false);
			ConverterTestUtil.testValues(transactions.iterator().next(), options);
		}
	}

}
