/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractStatus;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractType;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;


public class DataSetCreatorTest extends TestUtil {

	private static enum AssetDate {
		UPDATED, NOT_UPDATED
	}

	private static enum BalanceDate {
		UPDATED, NOT_UPDATED
	}

	private static enum Included {
		TRUE, FALSE, BOTH
	}

	private static enum Completed {
		TRUE, FALSE, BOTH
	}

	private static final double NONE = 0;
	private static final double COLLATERAL = 100;
	private static final double PRICE = 1000;
	private static final double REWARD = 10000;
	private static final double ITEM = 100000;
	private static final long ISSUER_ID = 1;
	private static final long ISSUER_CORPORATION_ID = 2;
	private static final long ACCEPTOR_ID = 3;
	private static final long ACCEPTOR_CORPORATION_ID = 4;

	private final Date now = new Date();
	private final Date before = getBefore();
	private final Date after = getAfter();
	private final DataSetCreatorTester creator = new DataSetCreatorTester();

	@Test
	public void testContract() {
		List<TestMatch> matchs = new ArrayList<>();
	//Contract Collateral (Courier)
		//issuer
		matchs.add(new TestMatch(1, issuer(AssetDate.UPDATED, null), acceptor(null, null), Included.BOTH, Completed.BOTH, COLLATERAL, NONE, contractType(ContractType.COURIER), contractStatus(ContractStatus.IN_PROGRESS, ContractStatus.OUTSTANDING), contractStatus()));
		matchs.add(new TestMatch(1, issuer(AssetDate.NOT_UPDATED, null), acceptor(null, null), Included.BOTH, Completed.TRUE, COLLATERAL, NONE, contractType(ContractType.COURIER), contractStatus(), contractStatus()));
		//acceptor
		matchs.add(new TestMatch(2, issuer(null, null), acceptor(null, BalanceDate.UPDATED), Included.BOTH, Completed.BOTH, COLLATERAL, NONE, contractType(ContractType.COURIER), contractStatus(ContractStatus.IN_PROGRESS), contractStatus()));
	//Contract ISK
		//issuer
		matchs.add(new TestMatch(3, issuer(null, BalanceDate.UPDATED), acceptor(null, null), Included.BOTH, Completed.BOTH, NONE, REWARD, contractType(), contractStatus(ContractStatus.OUTSTANDING), contractStatus()));
		matchs.add(new TestMatch(3, issuer(null, BalanceDate.NOT_UPDATED), acceptor(null, null), Included.BOTH, Completed.TRUE, NONE, (PRICE-REWARD), contractType(), contractStatus(), contractStatus(ContractStatus.OUTSTANDING)));
		//acceptor
		matchs.add(new TestMatch(4, issuer(null, null), acceptor(null, BalanceDate.NOT_UPDATED), Included.BOTH, Completed.TRUE, NONE, (REWARD + -PRICE), contractType(), contractStatus(), contractStatus()));
	//Contract Items
		//issuer
		matchs.add(new TestMatch(5, issuer(AssetDate.UPDATED, null), acceptor(null, null), Included.TRUE, Completed.BOTH, NONE, ITEM, contractType(ContractType.AUCTION, ContractType.ITEM_EXCHANGE), contractStatus(ContractStatus.OUTSTANDING), contractStatus()));
		matchs.add(new TestMatch(5, issuer(AssetDate.NOT_UPDATED, null), acceptor(null, null), Included.TRUE, Completed.TRUE, NONE, -ITEM, contractType(ContractType.AUCTION, ContractType.ITEM_EXCHANGE), contractStatus(), contractStatus(ContractStatus.OUTSTANDING)));
		matchs.add(new TestMatch(5, issuer(AssetDate.NOT_UPDATED, null), acceptor(null, null), Included.FALSE, Completed.TRUE, NONE, ITEM, contractType(ContractType.AUCTION, ContractType.ITEM_EXCHANGE), contractStatus(), contractStatus(ContractStatus.OUTSTANDING)));
		//acceptor
		matchs.add(new TestMatch(6, issuer(null, null), acceptor(AssetDate.NOT_UPDATED, null), Included.TRUE, Completed.TRUE, NONE, ITEM, contractType(ContractType.AUCTION, ContractType.ITEM_EXCHANGE), contractStatus(), contractStatus()));
		matchs.add(new TestMatch(6, issuer(null, null), acceptor(AssetDate.NOT_UPDATED, null), Included.FALSE, Completed.TRUE, NONE, -ITEM, contractType(ContractType.AUCTION, ContractType.ITEM_EXCHANGE), contractStatus(), contractStatus()));
		boolean[] includedValues = {true, false};
		boolean[] completedValues = {true, false};
		int iteration = 0;
		for (ContractStatus status : ContractStatus.values()) {
			for (ContractType type : ContractType.values()) {
				for (AssetDate issuerAssetDate : AssetDate.values()) {
					for (AssetDate acceptorAssetDate : AssetDate.values()) {
						for (BalanceDate issuerBalanceDate : BalanceDate.values()) {
							for (BalanceDate acceptorBalanceDate : BalanceDate.values()) {
								for (boolean included : includedValues) {
									for (boolean completed : completedValues) {
										iteration++;
										test(iteration, matchs, new TestData(issuer(issuerAssetDate, issuerBalanceDate), acceptor(acceptorAssetDate, acceptorBalanceDate), included, completed, type, status));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void test(int iteration, final List<TestMatch> matchs, final TestData test) {
		OwnerType issuer = new EsiOwner();
		issuer.setOwnerID(ISSUER_ID);
		issuer.setOwnerName("issuer");
		//issuer.setCorporationID(ISSUER_CORPORATION_ID);
		issuer.setCorporationName("issuer corporation");
		setOwnerData(issuer, test.getIssuer());
		OwnerType acceptor = new EsiOwner();
		acceptor.setOwnerID(ACCEPTOR_ID);
		acceptor.setOwnerName("acceptor");
		//acceptor.setCorporationID(ACCEPTOR_CORPORATION_ID);
		acceptor.setCorporationName("acceptor corporation");
		setOwnerData(acceptor, test.getAcceptor());

		Map<Long, OwnerType> owners = new HashMap<>();
		owners.put(issuer.getOwnerID(), issuer);
		owners.put(acceptor.getOwnerID(), acceptor);

		Map<String, Value> values = new HashMap<>();
		List<MyContractItem> contractItems = new ArrayList<>();
		List<MyContract> contracts = new ArrayList<>();
		MyContract contract = getContract(test, issuer, acceptor);
		contracts.add(contract);
		contractItems.add(getContractItem(contract, test.isIncluded()));
		Value total = new Value(now);
		creator.addContractItems(contractItems, values, owners, total, now);
		creator.addContracts(contracts, values, owners, total, now);

		double collateral = 0;
		double value = 0;
		for (TestMatch match : test.match(matchs)) {
			collateral = collateral + match.getCollateral();
			value = value + match.getValue();
		}
		assertThat(print(iteration, test, total), total.getContractCollateral(), equalTo(collateral));
		assertThat(print(iteration, test, total), total.getContractValue(), equalTo(value));
	}

	private String print(final int iteration, final TestData test, final Value total) {
		StringBuilder builder = new StringBuilder();
		builder.append("\r\niteration: ");
		builder.append(iteration);
		builder.append("\r\nTestData\r\n	Status: ");
		builder.append(test.getContractStatus());
		builder.append("\r\n	Type: ");
		builder.append(test.getContractType());
		builder.append("\r\n	Complated: ");
		builder.append(test.isComplated());
		builder.append("\r\n	Included: ");
		builder.append(test.isIncluded());
		builder.append("\r\n	Issue:\r\n		AssetDate.");
		builder.append(test.getIssuer().getAssetDate());
		builder.append("\r\n		BalanceDate.");
		builder.append(test.getIssuer().getBalanceDate());
		builder.append("\r\n	Acceptor:\r\n		AssetDate.");
		builder.append(test.getAcceptor().getAssetDate());
		builder.append("\r\n		BalanceDate.");
		builder.append(test.getAcceptor().getBalanceDate());
		builder.append("\r\nTotal\r\n	Collateral: ");
		builder.append(total.getContractCollateral());
		builder.append("\r\n	Value: ");
		builder.append(total.getContractValue());
		builder.append("\r\n");
		return builder.toString();
	}

	private Issuer issuer(AssetDate assetDate, BalanceDate balanceDate) {
		return new Issuer(assetDate, balanceDate);
	}

	private Acceptor acceptor(AssetDate assetDate, BalanceDate balanceDate) {
		return new Acceptor(assetDate, balanceDate);
	}

	public Set<ContractStatus> contractStatus(ContractStatus ... data) {
		return new HashSet<>(Arrays.asList(data));
	}

	public Set<ContractType> contractType(ContractType ... data) {
		return new HashSet<>(Arrays.asList(data));
	}

	private void setOwnerData(OwnerType ownerType, OwnerData<?> ownerData) {
		if (ownerData.getAssetDate() == null) {
			ownerType.setAssetLastUpdate(now);
		} else switch (ownerData.getAssetDate()) {
			case UPDATED:
				ownerType.setAssetLastUpdate(after);
				break;
			case NOT_UPDATED:
				ownerType.setAssetLastUpdate(before);
				break;
			default:
				throw new RuntimeException("AssetLastUpdate not set");

		}
		if (ownerData.getBalanceDate() == null) {
			ownerType.setBalanceLastUpdate(now);
		} else switch (ownerData.getBalanceDate()) {
			case UPDATED:
				ownerType.setBalanceLastUpdate(after);
				break;
			case NOT_UPDATED:
				ownerType.setBalanceLastUpdate(before);
				break;
			default:
				throw new RuntimeException("BalanceLastUpdate not set");

		}
	}

	private MyContract getContract(TestData data, OwnerType issuer, OwnerType acceptor) {
		RawContract contract = RawContract.create();
		if (data.isComplated()) {
			contract.setDateCompleted(now);
		} else {
			contract.setDateCompleted(null);
		}
		contract.setDateExpired(after); //Required, never null
		contract.setDateIssued(now);
		contract.setCollateral(COLLATERAL);
		contract.setPrice(PRICE);
		contract.setReward(REWARD);
		contract.setStatus(data.getContractStatus());
		contract.setForCorporation(false);
		contract.setType(data.getContractType());
		contract.setIssuerID((int) ISSUER_ID);
		contract.setIssuerCorporationID((int) ISSUER_CORPORATION_ID);
		contract.setAcceptorID((int) ACCEPTOR_ID);

		contract.setAssigneeID(0);
		MyContract myContract = new MyContract(contract);
		myContract.setIssuer(issuer.getOwnerName());
		myContract.setIssuerCorp(issuer.getCorporationName());
		myContract.setAcceptor(acceptor.getOwnerName());
		return myContract;
	}

	private MyContractItem getContractItem(MyContract contract, boolean included) {
		RawContractItem item = RawContractItem.create();
		item.setQuantity(1);
		item.setIncluded(included);
		MyContractItem contractItem = new MyContractItem(item, contract, null);
		contractItem.setDynamicPrice(ITEM);
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

	private static class DataSetCreatorTester extends DataSetCreator {

		public DataSetCreatorTester() {
		}

		@Override
		public void addContractItems(List<MyContractItem> contractItems, Map<String, Value> values, Map<Long, OwnerType> owners, Value total, Date date) {
			super.addContractItems(contractItems, values, owners, total, date);
		}

		@Override
		public void addContracts(List<MyContract> contracts, Map<String, Value> values, Map<Long, OwnerType> owners, Value total, Date date) {
			super.addContracts(contracts, values, owners, total, date);
		}
	}

	private static class TestMatch {
		private final int group;
		private final Issuer issuer;
		private final Acceptor acceptor;
		private final Included included;
		private final Completed completed;
		private final double collateral;
		private final double value;
		private final Set<ContractType> contractType;
		private final Set<ContractStatus> contractStatus;
		private final Set<ContractStatus> notContractStatus;

		public TestMatch(int group, Issuer issuer, Acceptor acceptor, Included included, Completed completed, double collateral, double value, Set<ContractType> contractType, Set<ContractStatus> contractStatus, Set<ContractStatus> notContractStatus) {
			this.group = group;
			this.issuer = issuer;
			this.acceptor = acceptor;
			this.included = included;
			this.completed = completed;
			this.collateral = collateral;
			this.value = value;
			this.contractType = contractType;
			this.contractStatus = contractStatus;
			this.notContractStatus = notContractStatus;
		}

		public int getGroup() {
			return group;
		}

		public Issuer getIssuer() {
			return issuer;
		}

		public Acceptor getAcceptor() {
			return acceptor;
		}

		public Included getIncluded() {
			return included;
		}

		public Completed getCompleted() {
			return completed;
		}

		public double getCollateral() {
			return collateral;
		}

		public double getValue() {
			return value;
		}

		public Set<ContractType> getContractType() {
			return contractType;
		}

		public Set<ContractStatus> getContractStatus() {
			return contractStatus;
		}

		public Set<ContractStatus> getNotContractStatus() {
			return notContractStatus;
		}
	}

	private static class TestData {
		private final Issuer issuer;
		private final Acceptor acceptor;
		private final boolean included;
		private final boolean completed;
		private final ContractType contractType;
		private final ContractStatus contractStatus;

		public TestData(Issuer issuer, Acceptor acceptor, boolean included, boolean completed, ContractType contractType, ContractStatus contractStatus) {
			this.issuer = issuer;
			this.acceptor = acceptor;
			this.included = included;
			this.completed = completed;
			this.contractType = contractType;
			this.contractStatus = contractStatus;
		}

		public Issuer getIssuer() {
			return issuer;
		}

		public Acceptor getAcceptor() {
			return acceptor;
		}

		public boolean isIncluded() {
			return included;
		}

		public boolean isComplated() {
			return completed;
		}

		public ContractType getContractType() {
			return contractType;
		}

		public ContractStatus getContractStatus() {
			return contractStatus;
		}

		public List<TestMatch> match(final List<TestMatch> matchs) {
			final List<TestMatch> ok = new ArrayList<>();
			Set<Integer> found = new HashSet<>();
			for (TestMatch match : matchs) {
				if (found.contains(match.getGroup())) {
					continue;
				}
				if (!match.getAcceptor().match(acceptor)) {
					continue;
				}
				if (!match.getIssuer().match(issuer)) {
					continue;
				}
				switch(match.getIncluded()) {
					case TRUE:
						if (!included) {
							continue;
						}
						break;
					case FALSE:
						if (included) {
							continue;
						}
						break;
				}
				switch(match.getCompleted()) {
					case TRUE:
						if (!completed) {
							continue;
						}
						break;
					case FALSE:
						if (completed) {
							continue;
						}
						break;
				}
				if (!match.getContractType().isEmpty() && !match.getContractType().contains(contractType)) {
					continue;
				}
				if (!match.getContractStatus().isEmpty() && !match.getContractStatus().contains(contractStatus)) {
					continue;
				}
				if (!match.getNotContractStatus().isEmpty() && match.getNotContractStatus().contains(contractStatus)) {
					continue;
				}
				found.add(match.getGroup());
				ok.add(match);
			}
			return ok;
		}
	}

	private static class Acceptor implements OwnerData<Acceptor> {
		protected final AssetDate assetDate;
		protected final BalanceDate balanceDate;

		public Acceptor(AssetDate assetDate, BalanceDate balanceDate) {
			this.assetDate = assetDate;
			this.balanceDate = balanceDate;
		}

		@Override
		public AssetDate getAssetDate() {
			return assetDate;
		}

		@Override
		public BalanceDate getBalanceDate() {
			return balanceDate;
		}

		@Override
		public boolean match(Acceptor acceptor) {
			return match(this, acceptor);
		}
	}

	private static class Issuer implements OwnerData<Issuer> {

		protected final AssetDate assetDate;
		protected final BalanceDate balanceDate;

		public Issuer(AssetDate assetDate, BalanceDate balanceDate) {
			this.assetDate = assetDate;
			this.balanceDate = balanceDate;
		}

		@Override
		public AssetDate getAssetDate() {
			return assetDate;
		}

		@Override
		public BalanceDate getBalanceDate() {
			return balanceDate;
		}

		@Override
		public boolean match(Issuer issuer) {
			return match(this, issuer);
		}
	}

	private static interface OwnerData<T extends OwnerData<?>> {
		public AssetDate getAssetDate();
		public BalanceDate getBalanceDate();
		public boolean match(T ownerData);
		default boolean match(T o1, T o2) {
			return match(o1.getAssetDate(), o2.getAssetDate()) && match(o1.getBalanceDate(), o2.getBalanceDate());
		}
		default boolean match(AssetDate o1, AssetDate o2) {
			if (o1 == null || o2 == null) {
				return true;
			} else {
				return o1 == o2;
			}
		}
		default boolean match(BalanceDate o1, BalanceDate o2) {
			if (o1 == null || o2 == null) {
				return true;
			} else {
				return o1 == o2;
			}
		}
	}
}
