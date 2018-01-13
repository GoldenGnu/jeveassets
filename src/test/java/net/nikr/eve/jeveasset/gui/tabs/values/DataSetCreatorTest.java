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
package net.nikr.eve.jeveasset.gui.tabs.values;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractStatus;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.i18n.TabsValues;
import org.junit.Assert;
import org.junit.Test;


public class DataSetCreatorTest extends TestUtil {

	private final Date now = new Date();
	private final Date before = getBefore();
	private final Date after = getAfter();
	private final DataSetCreatorTester creator = new DataSetCreatorTester();

	public DataSetCreatorTest() {
	}

	@Test
	public void testCreateTrackerDataPoint() {
	}

	@Test
	public void testCreateDataSet() {
	}

	@Test
	public void testSellingAssetsAcceptor() {
		//Values
		Map<String, Value> values = new HashMap<String, Value>();
		Value total = new Value(TabsValues.get().grandTotal(), now);
		//Owners
		Map<Long, OwnerType> owners = new HashMap<Long, OwnerType>();
		EveApiOwner issuer = getOwner("Issuer", 1, after, now);
		EveApiOwner acceptor = getOwner("Acceptor", 2);
		owners.put(issuer.getOwnerID(), issuer);
		owners.put(acceptor.getOwnerID(), acceptor);
		//Contacts Items
		List<MyContractItem> contractItems = new ArrayList<MyContractItem>();
		MyContract contract = getContract(issuer, acceptor, ContractStatus.OUTSTANDING, 0.0, 0.0, 0.0, null, now);
		MyContractItem contractItem = getContractItem(contract, 10, true);
		contractItems.add(contractItem);

		creator.addContractItems(contractItems, values, owners, total, now);
		Assert.assertEquals(1, values.size());
		Assert.assertEquals(10, values.get(issuer.getOwnerName()).getContractValue(), 0.0001);
	}

	@Test
	public void testSellingAssetsIssuer() {
		//Values
		Map<String, Value> values = new HashMap<String, Value>();
		Value total = new Value(TabsValues.get().grandTotal(), now);
		//Owners
		Map<Long, OwnerType> owners = new HashMap<Long, OwnerType>();
		EveApiOwner issuer = getOwner("Issuer", 1, after, now);
		EveApiOwner acceptor = getOwner("Acceptor", 2);
		owners.put(issuer.getOwnerID(), issuer);
		owners.put(acceptor.getOwnerID(), acceptor);
		//Contacts Items
		List<MyContractItem> contractItems = new ArrayList<MyContractItem>();
		MyContract contract = getContract(issuer, acceptor, ContractStatus.OUTSTANDING, 0.0, 0.0, 0.0, null, now);
		MyContractItem contractItem = getContractItem(contract, 10, true);
		contractItems.add(contractItem);

		creator.addContractItems(contractItems, values, owners, total, now);
		Assert.assertEquals(1, values.size());
		Assert.assertEquals(10, values.get(issuer.getOwnerName()).getContractValue(), 0.0001);
	}

	private EveApiOwner getOwner(String name, long id) {
		return getOwner(name, id, now, now);
	}

	private EveApiOwner getOwner(String name, long id, Date lastAsset, Date lastBalance) {
		EveApiOwner owner = new EveApiOwner(null, name, id);
		owner.setAssetLastUpdate(lastAsset);
		owner.setBalanceLastUpdate(lastBalance);
		return owner;
	}

	private MyContract getContract(EveApiOwner issuer, EveApiOwner acceptor, ContractStatus status, Double collateral, Double price, Double reward, Date completed, Date issued) {
		RawContract contract = RawContract.create();
		contract.setCollateral(collateral);
		contract.setDateCompleted(completed);
		contract.setDateIssued(issued);
		contract.setPrice(price);
		contract.setReward(reward);
		contract.setStatus(status);
		contract.setForCorporation(false);
		contract.setType(RawContract.ContractType.ITEM_EXCHANGE);
		if (issuer != null) {
			contract.setIssuerID((int) issuer.getOwnerID());
		} else {
			contract.setIssuerID(0);
		}
		if (acceptor != null) {
			contract.setAcceptorID((int) acceptor.getOwnerID());
		} else {
			contract.setIssuerID(0);
		}
		contract.setIssuerCorporationID(0);
		contract.setAssigneeID(0);
		MyContract myContract = new MyContract(contract);
		if (issuer != null) {
			myContract.setIssuer(issuer.getOwnerName());
		}
		if (acceptor != null) {
			myContract.setAcceptor(acceptor.getOwnerName());
		}
		return myContract;
	}

	private MyContractItem getContractItem(MyContract contract, int quantity, boolean included) {
		RawContractItem item = RawContractItem.create();
		item.setQuantity(quantity);
		item.setIncluded(included);
		MyContractItem contractItem = new MyContractItem(item, contract, null);
		contractItem.setDynamicPrice(1);
		return contractItem;
	}

	private Date getAfter() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		return cal.getTime();
	}

	private Date getBefore() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR_OF_DAY, -1);
		return cal.getTime();
	}

	@Test
	public void testGetValueInner() {
	}

	private static class DataSetCreatorTester extends DataSetCreator {

		public DataSetCreatorTester() {
		}

		@Override
		public void addContractItems(List<MyContractItem> contractItems, Map<String, Value> values, Map<Long, OwnerType> owners, Value total, Date date) {
			super.addContractItems(contractItems, values, owners, total, date);
		}

		@Override
		public void addContracts(List<MyContract> contractItems, Map<String, Value> values, Map<Long, OwnerType> owners, Value total, Date date) {
			super.addContracts(contractItems, values, owners, total, date);
		}

	}
}
