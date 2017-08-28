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
import static org.junit.Assert.fail;

import com.beimin.eveapi.model.shared.KeyType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
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
import net.nikr.eve.jeveasset.data.api.raw.RawJournalExtraInfo;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.MarketPriceData;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CharacterBlueprintsResponse;
import net.troja.eve.esi.model.CharacterContractsItemsResponse;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CharacterContractsResponse.AvailabilityEnum;
import net.troja.eve.esi.model.CharacterContractsResponse.TypeEnum;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse.RangeEnum;
import net.troja.eve.esi.model.CharacterWalletJournalExtraInfoResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse.FirstPartyTypeEnum;
import net.troja.eve.esi.model.CharacterWalletJournalResponse.RefTypeEnum;
import net.troja.eve.esi.model.CharacterWalletJournalResponse.SecondPartyTypeEnum;
import net.troja.eve.esi.model.CharacterWalletTransactionsResponse;
import org.joda.time.DateTime;


public class ConverterTestUtil {

	public static EveApiOwner getEveApiOwner(ConverterTestOptions options) {
		return getEveApiOwner(false, false, false, options);
	}

	public static EveApiOwner getEveApiOwner(boolean data, boolean setNull, boolean setValues, ConverterTestOptions options) {
		EveApiOwner owner = new EveApiOwner(null, false);
		setValues(owner, options);
		if (data) {
			setData(owner, setNull, setValues, options);
		}
		return owner;
	}

	public static EveKitOwner getEveKitOwner(ConverterTestOptions options) {
		return getEveKitOwner(false, false, false, options);
	}

	public static EveKitOwner getEveKitOwner(boolean data, boolean setNull, boolean setValues, ConverterTestOptions options) {
		EveKitOwner owner = new EveKitOwner(null, null);
		setValues(owner, options);
		if (data) {
			setData(owner, setNull, setValues, options);
		}
		return owner;
	}

	public static EsiOwner getEsiOwner(ConverterTestOptions options) {
		return getEsiOwner(false, false, false, options);
	}

	public static EsiOwner getEsiOwner(boolean data, boolean setNull, boolean setValues, ConverterTestOptions options) {
		EsiOwner esiOwner = new EsiOwner();
		setValues(esiOwner, options);
		if (data) {
			setData(esiOwner, setNull, setValues, options);
		}
		return esiOwner;
	}

	private static void setData(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		//Account Balance
		MyAccountBalance myAccountBalance = getMyAccountBalance(owner, setValues, options);
		owner.setAccountBalances(Collections.singletonList(myAccountBalance));

		//Asset
		owner.setAssets(getMyAssets(owner, setNull, setValues, options));

		//Blueprint
		RawBlueprint rawBlueprint = getRawBlueprint(options);
		owner.setBlueprints(Collections.singletonMap(rawBlueprint.getItemID(), rawBlueprint));

		//Contract
		MyContract saveMyContract = getMyContract(setNull, setValues, options);
		MyContractItem saveMyContractItem = getMyContractItem(saveMyContract, setNull, setValues, options);
		owner.setContracts(Collections.singletonMap(saveMyContract, Collections.singletonList(saveMyContractItem)));

		//IndustryJob
		MyIndustryJob saveMyIndustryJob = getMyIndustryJob(owner, setNull, setValues, options);
		owner.setIndustryJobs(Collections.singletonList(saveMyIndustryJob));

		//Journal
		MyJournal saveMyJournal = getMyJournal(owner, setNull, setValues, options);
		owner.setJournal(Collections.singleton(saveMyJournal));

		//MarketOrder
		MyMarketOrder saveMyMarketOrder = getMyMarketOrder(owner, setNull, setValues, options);
		owner.setMarketOrders(Collections.singletonList(saveMyMarketOrder));

		//Transaction
		MyTransaction saveMyTransaction = getMyTransaction(owner, setNull, setValues, options);
		owner.setTransactions(Collections.singleton(saveMyTransaction));
	}

	private static Item getItem(ConverterTestOptions options) {
		return new Item(options.getInteger());
	}

	public static RawAccountBalance getRawAccountBalance(ConverterTestOptions options) {
		RawAccountBalance rawAccountBalance = RawAccountBalance.create();
		setValues(rawAccountBalance, options, null);
		return rawAccountBalance;
	}

	public static MyAccountBalance getMyAccountBalance(OwnerType owner, boolean setValues, ConverterTestOptions options) {
		MyAccountBalance accountBalance = new MyAccountBalance(getRawAccountBalance(options), owner);
		if (setValues) {
			setValues(accountBalance, options, null, false);
		}
		return accountBalance;
	}

	public static RawAsset getRawAsset(boolean setNull, ConverterTestOptions options) {
		RawAsset rawAsset = RawAsset.create();
		setValues(rawAsset, options, setNull ? CharacterAssetsResponse.class : null);
		return rawAsset;
	}

	public static MyAsset getMyAsset(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		MyAsset asset = new MyAsset(getRawAsset(setNull, options), getItem(options), owner, new ArrayList<MyAsset>());
		if (setValues) {
			setValues(asset, options, null, false);
		}
		return asset;
	}

	public static List<MyAsset> getMyAssets(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		MyAsset rootAsset = getMyAsset(owner, setNull, setValues, options);
		if (setValues) {
			setValues(rootAsset, options, null, false);
		}
		MyAsset childAsset = getMyAsset(owner, setNull, setValues, options);
		if (setValues) {
			setValues(childAsset, options, null, false);
		}
		rootAsset.getAssets().add(childAsset);

		return Collections.singletonList(rootAsset);
	}

	public static RawBlueprint getRawBlueprint(ConverterTestOptions options) {
		RawBlueprint rawBlueprint = RawBlueprint.create();
		setValues(rawBlueprint, options, CharacterBlueprintsResponse.class);
		return rawBlueprint;
	}

	public static RawContract getRawContract(boolean setNull, ConverterTestOptions options) {
		RawContract rawContract = RawContract.create();
		setValues(rawContract, options, setNull ? CharacterContractsResponse.class : null);
		return rawContract;
	}

	public static MyContract getMyContract(boolean setNull, boolean setValues, ConverterTestOptions options) {
		MyContract contract = new MyContract(getRawContract(setNull, options));
		if (setValues) {
			setValues(contract, options, null, false);
		}
		return contract;
	}

	public static RawContractItem getRawContractItem(boolean setNull, ConverterTestOptions options) {
		RawContractItem rawContractItem = RawContractItem.create();
		setValues(rawContractItem, options, setNull ? CharacterContractsItemsResponse.class : null);
		return rawContractItem;
	}

	public static MyContractItem getMyContractItem(MyContract contract, boolean setNull, boolean setValues, ConverterTestOptions options) {
		MyContractItem contractItem = new MyContractItem(getRawContractItem(setNull, options), contract, getItem(options));
		if (setValues) {
			setValues(contractItem, options, null, false);
		}
		return contractItem;
	}

	public static RawIndustryJob getRawIndustryJob(boolean setNull, ConverterTestOptions options) {
		RawIndustryJob rawIndustryJob = RawIndustryJob.create();
		setValues(rawIndustryJob, options, setNull ? CharacterIndustryJobsResponse.class : null);
		return rawIndustryJob;
	}

	public static MyIndustryJob getMyIndustryJob(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		MyIndustryJob industryJob = new MyIndustryJob(getRawIndustryJob(setNull, options), getItem(options), owner, 100);
		if (setValues) {
			setValues(industryJob, options, null, false);
		}
		return industryJob;
	}

	public static RawJournal getRawJournal(boolean setNull, ConverterTestOptions options) {
		RawJournal rawJournal = RawJournal.create();
		setValues(rawJournal, options, setNull ? CharacterWalletJournalResponse.class : null);
		return rawJournal;
	}

	public static MyJournal getMyJournal(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		MyJournal journal = new MyJournal(getRawJournal(setNull, options), owner);
		if (setValues) {
			setValues(journal, options, null, false);
		}
		return journal;
	}

	public static RawMarketOrder getRawMarketOrder(boolean setNull, ConverterTestOptions options) {
		RawMarketOrder rawMarketOrder = RawMarketOrder.create();
		setValues(rawMarketOrder, options, setNull ? CharacterOrdersResponse.class : null);
		return rawMarketOrder;
	}

	public static MyMarketOrder getMyMarketOrder(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		MyMarketOrder marketOrder = new MyMarketOrder(getRawMarketOrder(setNull, options), getItem(options), owner);
		if (setValues) {
			setValues(marketOrder, options, null, false);
		}
		return marketOrder;
	}

	public static RawTransaction getRawTransaction(boolean setNull, ConverterTestOptions options) {
		RawTransaction rawTransaction = RawTransaction.create();
		setValues(rawTransaction, options, setNull ? CharacterWalletTransactionsResponse.class : null);
		return rawTransaction;
	}

	public static MyTransaction getMyTransaction(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		MyTransaction transaction = new MyTransaction(getRawTransaction(setNull, options), getItem(options), owner);
		if (setValues) {
			setValues(transaction, options, null, false);
		}
		return transaction;
	}

	static EveApiAccount getEveApiAccount(ConverterTestOptions options) {
		EveApiAccount account = new EveApiAccount(0, null);
		setValues(account, options);
		return account;
	}

	public static void testOwner(OwnerType esiOwner, boolean setNull, ConverterTestOptions options) {
		//Account Balance
		assertEquals(esiOwner.getAccountBalances().size(), 1);
		MyAccountBalance loadAccountBalance = esiOwner.getAccountBalances().get(0);
		testValues(loadAccountBalance, options, null, false);

		//Asset
		if (options.getItemFlag().getFlagID() != 89) {
			assertEquals("List empty @" + options.getIndex(), 1, esiOwner.getAssets().size());
			MyAsset rootMyAsset = esiOwner.getAssets().get(0);
			testValues(rootMyAsset, options, setNull ? CharacterAssetsResponse.class : null, false);
			assertEquals("List empty @" + options.getIndex(), 1, rootMyAsset.getAssets().size());
			MyAsset childMyAsset = rootMyAsset.getAssets().get(0);
			testValues(childMyAsset, options, setNull ? CharacterAssetsResponse.class : null, false);
		} else {
			assertEquals(esiOwner.getAssets().size(), 0);
		}

		//Blueprint
		assertEquals(esiOwner.getBlueprints().size(), 1);
		RawBlueprint loadRawBlueprint = esiOwner.getBlueprints().values().iterator().next();
		testValues(loadRawBlueprint, options, setNull ? CharacterBlueprintsResponse.class : null, false);

		//Contract
		assertEquals(esiOwner.getContracts().size(), 1);
		Map.Entry<MyContract, List<MyContractItem>> entry = esiOwner.getContracts().entrySet().iterator().next();
		testValues(entry.getKey(), options, setNull ? CharacterContractsResponse.class : null, false);
		testValues(entry.getValue().iterator().next(), options, setNull ? CharacterContractsItemsResponse.class : null, false);

		//IndustryJobs
		assertEquals(esiOwner.getIndustryJobs().size(), 1);
		MyIndustryJob loadMyIndustryJob = esiOwner.getIndustryJobs().get(0);
		testValues(loadMyIndustryJob, options, setNull ? CharacterIndustryJobsResponse.class : null, false);

		//Journal
		assertEquals(esiOwner.getJournal().size(), 1);
		MyJournal loadMyJournal = esiOwner.getJournal().iterator().next();
		testValues(loadMyJournal, options, setNull ? CharacterWalletJournalResponse.class : null, false);

		//MarketOrder
		assertEquals(esiOwner.getMarketOrders().size(), 1);
		MyMarketOrder loadMyMarketOrder = esiOwner.getMarketOrders().get(0);
		testValues(loadMyMarketOrder, options, setNull ? CharacterOrdersResponse.class : null, false);

		//MarketOrder
		assertEquals(esiOwner.getTransactions().size(), 1);
		MyTransaction loadMyTransaction = esiOwner.getTransactions().iterator().next();
		testValues(loadMyTransaction, options, setNull ? CharacterWalletTransactionsResponse.class : null, false);
	}

	public static void setValues(Object object, ConverterTestOptions options) {
		setValues(object, options, null, true);
	}

	public static void setValues(Object object, ConverterTestOptions options, Class<?> esi) {
		setValues(object, options, esi, true);
	}

	private static void setValues(Object object, ConverterTestOptions options, Class<?> esi, boolean overwrite) {
		List<Field> fields = new ArrayList<>();
		fields.addAll(Arrays.asList(object.getClass().getDeclaredFields()));
		if (object.getClass().getSuperclass() != null) {
			fields.addAll(Arrays.asList(object.getClass().getSuperclass().getDeclaredFields()));
		}
		fields.addAll(Arrays.asList(object.getClass().getDeclaredFields()));
		for (Field field : fields) {
			Class<?> type = field.getType();
			if (ignore(object, field, type)) {
				continue;
			}
			try {
				field.setAccessible(true);
				if (!overwrite && field.get(object) != null) {
					continue;
				}
				field.set(object, getValue(type, isOptional(esi, field), options));
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				fail(ex.getMessage());
			}
		}
		//Assets
		//EveAPI
		if (object instanceof com.beimin.eveapi.model.shared.Asset) {
			com.beimin.eveapi.model.shared.Asset asset = (com.beimin.eveapi.model.shared.Asset) object;
			asset.setItemID(asset.getItemID() + 1); //Workaround for itemID == locationID
			asset.setLocationID(options.getLocationTypeEveApi());
			asset.setFlag(options.getLocationFlagEveApi());
		}
		//EveKit
		if (object instanceof enterprises.orbital.evekit.client.model.Asset) {
			enterprises.orbital.evekit.client.model.Asset asset = (enterprises.orbital.evekit.client.model.Asset) object;
			asset.setItemID(asset.getItemID() + 1); //Workaround for itemID == locationID
			asset.setLocationID(options.getLocationTypeEveApi());
			asset.setFlag(options.getLocationFlagEveApi());
			asset.setContainer(0L);
		}
		//ESI
		if (object instanceof CharacterAssetsResponse) {
			CharacterAssetsResponse asset = (CharacterAssetsResponse) object;
			asset.setItemId(asset.getItemId() + 1); //Workaround for itemID == locationID
			asset.setLocationId(options.getLocationTypeEveApi());
		}
		//Raw
		if (object instanceof RawAsset) {
			RawAsset asset = (RawAsset) object;
			asset.setItemID(asset.getItemID() + 1); //Workaround for itemID == locationID
			asset.setLocationID(options.getLocationTypeEveApi());
		}
		//Contracts
		//EveKit
		if (object instanceof enterprises.orbital.evekit.client.model.Contract) {
			enterprises.orbital.evekit.client.model.Contract contract = (enterprises.orbital.evekit.client.model.Contract) object;
			contract.setAvailability(options.getContractAvailabilityEveKit());
			contract.setStatus(options.getContractStatusEveKit());
			contract.setType(options.getContractTypeEveKit());
		}
		//IndustryJobs
		//EveAPI
		if (object instanceof com.beimin.eveapi.model.shared.IndustryJob) {
			com.beimin.eveapi.model.shared.IndustryJob industryJob = (com.beimin.eveapi.model.shared.IndustryJob) object;
			industryJob.setStatus(options.getIndustryJobStatusEveApi()); //Workaround: Set valid value for status (PENDING)
		}
		//EveKit
		if (object instanceof enterprises.orbital.evekit.client.model.IndustryJob) {
			enterprises.orbital.evekit.client.model.IndustryJob industryJob = (enterprises.orbital.evekit.client.model.IndustryJob) object;
			industryJob.setStatus(options.getIndustryJobStatusEveApi()); //Workaround: Set valid value for status (PENDING)
		}
		//Journal
		//EveAPI
		if (object instanceof com.beimin.eveapi.model.shared.JournalEntry) {
			com.beimin.eveapi.model.shared.JournalEntry journalEntry = (com.beimin.eveapi.model.shared.JournalEntry) object;
			journalEntry.setRefTypeID(journalEntry.getRefType().getId());
			journalEntry.setOwner1TypeID(options.getJournalPartyTypeEveApi());
			journalEntry.setOwner2TypeID(options.getJournalPartyTypeEveApi());
			journalEntry.setArgID1(options.getLong());
			journalEntry.setArgName1(String.valueOf(options.getLong()));
		}
		//EveKit
		if (object instanceof enterprises.orbital.evekit.client.model.WalletJournal) {
			enterprises.orbital.evekit.client.model.WalletJournal journalEntry = (enterprises.orbital.evekit.client.model.WalletJournal) object;
			journalEntry.setRefTypeID(options.getJournalRefTypeRaw().getID());
			journalEntry.setOwner1TypeID(options.getJournalPartyTypeEveApi());
			journalEntry.setOwner2TypeID(options.getJournalPartyTypeEveApi());
			journalEntry.setArgID1(options.getLong());
			journalEntry.setArgName1(String.valueOf(options.getLong()));
		}
		//Market Orders
		//EveAPI
		if (object instanceof com.beimin.eveapi.model.shared.MarketOrder) {
			com.beimin.eveapi.model.shared.MarketOrder marketOrder = (com.beimin.eveapi.model.shared.MarketOrder) object;
			marketOrder.setRange(options.getMarketOrderRangeEveApi());
			marketOrder.setOrderState(options.getMarketOrderStateEveApi());
		}
		//EveKit
		if (object instanceof enterprises.orbital.evekit.client.model.MarketOrder) {
			enterprises.orbital.evekit.client.model.MarketOrder marketOrder = (enterprises.orbital.evekit.client.model.MarketOrder) object;
			marketOrder.setOrderRange(options.getMarketOrderRangeEveApi());
			marketOrder.setOrderState(options.getMarketOrderStateEveApi());
		}
		//Transactions
		//EveAPI
		if (object instanceof com.beimin.eveapi.model.shared.WalletTransaction) {
			com.beimin.eveapi.model.shared.WalletTransaction transaction = (com.beimin.eveapi.model.shared.WalletTransaction) object;
			transaction.setTransactionType("buy");
			transaction.setTransactionFor("personal");
		}
		//EveKit
		if (object instanceof enterprises.orbital.evekit.client.model.WalletTransaction) {
			enterprises.orbital.evekit.client.model.WalletTransaction transaction = (enterprises.orbital.evekit.client.model.WalletTransaction) object;
			transaction.setTransactionType("buy");
			transaction.setTransactionFor("personal");
		}
	}

	public static void testValues(Object object, ConverterTestOptions options) {
		testValues(object, options, null, true);
	}

	public static void testValues(Object object, ConverterTestOptions options, Class<?> esi) {
		testValues(object, options, esi, true);
	}

	public static void testValues(Object object, ConverterTestOptions options, Class<?> esi, boolean superClassOnly) {
		if (object instanceof MyAsset) {
			MyAsset myAsset = (MyAsset) object;
			myAsset.setItemID(myAsset.getItemID() - 1); //Workaround for itemID == locationID
			myAsset.setLocationID(options.getLong());
		}
		if (object instanceof MyContract) {
			MyContract myContract = (MyContract) object;
			if (myContract.getAvailability() == RawContract.ContractAvailability.PERSONAL
					&& (options.getContractAvailabilityRaw() == RawContract.ContractAvailability.CORPORATION
					|| options.getContractAvailabilityRaw() == RawContract.ContractAvailability.ALLIANCE)) {
				myContract.setAvailability(options.getContractAvailabilityRaw());
			}
		}
		for (Field field : getField(object, true)) {
			Class<?> type = field.getType();
			if (ignore(object, field, type)) {
				continue;
			}
			try {
				field.setAccessible(true);
				assertEquals(getString(field, object, options.getIndex()), getValue(type, isOptional(esi, field), options), field.get(object));
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				fail(ex.getMessage());
			}
		}
	}

	private static List<Field> getField(Object object, boolean superClassOnly) {
		List<Field> fields = new ArrayList<Field>();
		if (superClassOnly) {
			fields.addAll(Arrays.asList(object.getClass().getSuperclass().getDeclaredFields()));
		} else {
			fields.addAll(Arrays.asList(object.getClass().getSuperclass().getDeclaredFields()));
			fields.addAll(Arrays.asList(object.getClass().getDeclaredFields()));
		}
		return fields;
	}

	private static boolean ignore(Object object, Field field, Class<?> type) {
		if (object.getClass().equals(MyMarketOrder.class) && field.getName().equals("regionId")) {
			return true;
		}
		if (field.getName().equals("serialVersionUID")) {
			return true;
		}
		if (type.equals(List.class)) {
			return true;
		}
		if (type.equals(Set.class)) {
			return true;
		}
		return type.equals(Map.class);
	}

	private static Object getValue(Class<?> type, boolean optional, ConverterTestOptions options) {
		if (options == null) {
			throw new RuntimeException("Option is null");
		}
		if (optional) {
			return options.getNull(); //Optional, must handle null
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			return options.getInteger();
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			return options.getFloat();
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			return options.getBoolean();
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return options.getLong();
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			return options.getDouble();
		} else if (type.equals(String.class)) {
			return options.getString();
		} else if (type.equals(Date.class)) {
			return options.getDate();
		} else if (type.equals(ItemFlag.class)) {
			return options.getItemFlag();
		} else if (type.equals(RawAsset.LocationType.class)) {
			return options.getLocationTypeRaw();
		} else if (type.equals(RawContract.ContractAvailability.class)) {
			return options.getContractAvailabilityRaw();
		} else if (type.equals(RawContract.ContractStatus.class)) {
			return options.getContractStatusRaw();
		} else if (type.equals(RawContract.ContractType.class)) {
			return options.getContractTypeRaw();
		} else if (type.equals(RawIndustryJob.IndustryJobStatus.class)) {
			return options.getIndustryJobStatusRaw();
		} else if (type.equals(RawJournalExtraInfo.class)) {
			return options.getJournalExtraInfoRaw();
		} else if (type.equals(RawJournal.JournalPartyType.class)) {
			return options.getJournalPartyTypeRaw();
		} else if (type.equals(RawJournalRefType.class)) {
			return options.getJournalRefTypeRaw();
		} else if (type.equals(RawMarketOrder.MarketOrderRange.class)) {
			return options.getMarketOrderRangeRaw();
		} else if (type.equals(RawMarketOrder.MarketOrderState.class)) {
			return options.getMarketOrderStateRaw();
		} else if (type.equals(MyLocation.class)) {
			return options.getMyLocation();
		} else if (type.equals(PriceData.class)) {
			return options.getPriceData();
		} else if (type.equals(UserItem.class)) {
			return options.getUserPrice();
		} else if (type.equals(MarketPriceData.class)) {
			return options.getMarketPriceData();
		} else if (type.equals(Tags.class)) {
			return options.getTags();
		} else if (type.equals(RawBlueprint.class)) {
			return options.getRawBlueprint();
		} else if (type.equals(Percent.class)) {
			return options.getPercent();
		} else if (type.equals(CharacterBlueprintsResponse.LocationFlagEnum.class)) {
			return options.getLocationFlagEsiBlueprint();
		} else if (type.equals(OffsetDateTime.class)) {
			return options.getOffsetDateTime();
		} else if (type.equals(CharacterIndustryJobsResponse.StatusEnum.class)) {
			return options.getIndustryJobStatusEsi();
		} else if (type.equals(CharacterWalletJournalExtraInfoResponse.class)) {
			return options.getJournalExtraInfoEsi();
		} else if (type.equals(FirstPartyTypeEnum.class)) {
			return options.getJournalPartyTypeEsiFirst();
		} else if (type.equals(RefTypeEnum.class)) {
			return options.getJournalRefTypeEsi();
		} else if (type.equals(SecondPartyTypeEnum.class)) {
			return options.getJournalPartyTypeEsiSecond();
		} else if (type.equals(AvailabilityEnum.class)) {
			return options.getContractAvailabilityEsi();
		} else if (type.equals(CharacterContractsResponse.StatusEnum.class)) {
			return options.getContractStatusEsi();
		} else if (type.equals(TypeEnum.class)) {
			return options.getContractTypeEsi();
		} else if (type.equals(RangeEnum.class)) {
			return options.getMarketOrderRangeEsi();
		} else if (type.equals(CharacterOrdersResponse.StateEnum.class)) {
			return options.getMarketOrderStateEsi();
		} else if (type.equals(CharacterAssetsResponse.LocationFlagEnum.class)) {
			return options.getLocationFlagEsiAssets();
		} else if (type.equals(CharacterAssetsResponse.LocationTypeEnum.class)) {
			return options.getLocationTypeEsi();
		} else if (type.equals(com.beimin.eveapi.model.shared.RefType.class)) {
			return options.getJournalRefTypeEveApi();
		} else if (type.equals(com.beimin.eveapi.model.shared.ContractType.class)) {
			return options.getContractTypeEveApi();
		} else if (type.equals(com.beimin.eveapi.model.shared.ContractStatus.class)) {
			return options.getContractStatusEveApi();
		} else if (type.equals(com.beimin.eveapi.model.shared.ContractAvailability.class)) {
			return options.getContractAvailabilityEveApi();
		} else if (type.equals(BigDecimal.class)) {
			return options.getBigDecimal();
		} else if (type.equals(DateTime.class)) {
			return options.getDateTime();
		} else if (type.equals(EsiCallbackURL.class)) {
			return options.getEsiCallbackURL();
		} else if (type.equals(KeyType.class)) {
			return options.getKeyType();
		} else if (type.equals(EveApiAccount.class)) {
			return options.getEveApiAccount();
		} else if (type.equals(ApiClient.class)) {
			return new ApiClient();
		} else {
			fail("No test value for: " + type.getSimpleName());
			return null;
		}
	}

	private static String getString(Field field, Object object, int index) {
		return object.getClass().getSimpleName() + "->" + field.getName() + " @index: " + index;
	}

	private static boolean isOptional(Class<?> esi, Field field) {
		Map<String, Boolean> optional = new HashMap<String, Boolean>();
		if (esi == null) {
			return false;
		}
		for (Method method : esi.getDeclaredMethods()) {
			String methodName = method.getName();
			String methodId = esi.getSimpleName() + "->" + methodName;
			if (methodId.equals("CharacterAssetsResponse->getQuantity")
					|| methodId.equals("CharacterContractsResponse->getStartLocationId")
					|| methodId.equals("CharacterWalletJournalResponse->getExtraInfo")) {
				continue;
			}
			if (methodName.startsWith("get")) {
				Annotation[] annotations = method.getAnnotations();
				assertEquals(1, annotations.length);
				optional.put(methodName.toLowerCase().replaceFirst("get", ""), annotations[0].toString().contains("required=false"));
			}
		}
		Boolean isOptional = optional.get(field.getName().toLowerCase());
		return (isOptional != null && isOptional);
	}
}
