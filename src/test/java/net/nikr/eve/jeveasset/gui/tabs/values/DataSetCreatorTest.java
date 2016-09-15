/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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

import com.beimin.eveapi.model.shared.Contract;
import com.beimin.eveapi.model.shared.ContractItem;
import com.beimin.eveapi.model.shared.ContractStatus;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.i18n.TabsValues;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Niklas
 */
public class DataSetCreatorTest {

	private Date now = new Date();
	private Date before = getBefore();
	private Date after = getAfter();
	private DataSetCreatorTester creator = new DataSetCreatorTester();
	
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
		Map<String, Owner> owners = new HashMap<String, Owner>();
		Owner issuer = getOwner("Issuer", after, now);
		Owner acceptor = getOwner("Acceptor");
		owners.put(issuer.getName(), issuer);
		owners.put(acceptor.getName(), acceptor);
		//Contacts Items
		List<MyContractItem> contractItems = new ArrayList<MyContractItem>();
		MyContract contract = getContract(issuer, acceptor, ContractStatus.OUTSTANDING, 0, 0, 0, null, now);
		MyContractItem contractItem = getContractItem(contract, 10, true);
		contractItems.add(contractItem);
		
		creator.addContractItems(contractItems, values, owners, total, now);
		Assert.assertEquals(1, values.size());
		Assert.assertEquals(10, values.get(issuer.getName()).getContractValue(), 0.0001);
	}

	@Test
	public void testSellingAssetsIssuer() {
		//Values
		Map<String, Value> values = new HashMap<String, Value>();
		Value total = new Value(TabsValues.get().grandTotal(), now);
		//Owners
		Map<String, Owner> owners = new HashMap<String, Owner>();
		Owner issuer = getOwner("Issuer", after, now);
		Owner acceptor = getOwner("Acceptor");
		owners.put(issuer.getName(), issuer);
		owners.put(acceptor.getName(), acceptor);
		//Contacts Items
		List<MyContractItem> contractItems = new ArrayList<MyContractItem>();
		MyContract contract = getContract(issuer, acceptor, ContractStatus.OUTSTANDING, 0, 0, 0, null, now);
		MyContractItem contractItem = getContractItem(contract, 10, true);
		contractItems.add(contractItem);
		
		creator.addContractItems(contractItems, values, owners, total, now);
		Assert.assertEquals(1, values.size());
		Assert.assertEquals(10, values.get(issuer.getName()).getContractValue(), 0.0001);
	}

	private Owner getOwner(String name) {
		return getOwner(name, now, now);
	}

	private Owner getOwner(String name, Date lastAsset, Date lastBalance) {
		Owner owner = new Owner(null, name, 0);
		owner.setAssetLastUpdate(lastAsset);
		owner.setBalanceLastUpdate(lastBalance);
		return owner;
	}

	private MyContract getContract(Owner issuer, Owner acceptor, ContractStatus status, double collateral, double price, double reward, Date completed, Date issued) {
		Contract contract = new Contract();
		contract.setCollateral(collateral);
		contract.setDateCompleted(completed);
		contract.setDateIssued(issued);
		contract.setPrice(price);
		contract.setReward(reward);
		contract.setStatus(status);
		contract.setForCorp(false);
		MyContract myContract = new MyContract(contract, new MyLocation(0), new MyLocation(0));
		if (issuer != null) {
			myContract.setIssuer(issuer.getName());
		}
		if (acceptor != null) {
			myContract.setAcceptor(acceptor.getName());
		}
		return myContract;
	}

	private MyContractItem getContractItem(MyContract contract, int quantity, boolean included) {
		ContractItem item = new ContractItem();
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

		public DataSetCreatorTester() {}

		@Override
		public void addContractItems(List<MyContractItem> contractItems, Map<String, Value> values, Map<String, Owner> owners, Value total, Date date) {
			super.addContractItems(contractItems, values, owners, total, date);
		}

		@Override
		public void addContracts(List<MyContract> contractItems, Map<String, Value> values, Map<String, Owner> owners, Value total, Date date) {
			super.addContracts(contractItems, values, owners, total, date);
		}

		
	}
}
