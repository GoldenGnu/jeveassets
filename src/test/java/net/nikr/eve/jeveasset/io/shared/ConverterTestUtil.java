/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyBlueprint;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyExtraction;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.api.my.MyShip;
import net.nikr.eve.jeveasset.data.api.my.MySkill;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawAccountBalance;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawClone;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawExtraction;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.Change;
import net.nikr.eve.jeveasset.data.api.raw.RawMining;
import net.nikr.eve.jeveasset.data.api.raw.RawSkill;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.MarketPriceData;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.gui.tabs.orders.Outbid;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.api.AssetsApi;
import net.troja.eve.esi.api.CharacterApi;
import net.troja.eve.esi.api.ClonesApi;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.api.CorporationApi;
import net.troja.eve.esi.api.IndustryApi;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.api.MarketApi;
import net.troja.eve.esi.api.PlanetaryInteractionApi;
import net.troja.eve.esi.api.SkillsApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.api.UserInterfaceApi;
import net.troja.eve.esi.api.WalletApi;
import net.troja.eve.esi.auth.SsoScopes;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CharacterBlueprintsResponse;
import net.troja.eve.esi.model.CharacterContractsItemsResponse;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterMiningResponse;
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CharacterShipResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CharacterWalletTransactionsResponse;
import net.troja.eve.esi.model.CorporationAssetsResponse;
import net.troja.eve.esi.model.CorporationBlueprintsResponse;
import net.troja.eve.esi.model.CorporationContractsResponse;
import net.troja.eve.esi.model.CorporationIndustryJobsResponse;
import net.troja.eve.esi.model.CorporationMiningExtractionsResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;
import net.troja.eve.esi.model.CorporationWalletsResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ConverterTestUtil {

	public static EsiOwner getEsiOwner(ConverterTestOptions options) {
		return getEsiOwner(false, false, false, options);
	}

	public static EsiOwner getEsiOwner(boolean data, boolean setNull, boolean setValues, ConverterTestOptions options) {
		EsiOwner esiOwner = EsiOwner.create();
		setValues(esiOwner, options);
		if (data) {
			setData(esiOwner, setNull, setValues, options);
			esiOwner.setRoles(set(RolesEnum.DIRECTOR));
			esiOwner.setScopes(SsoScopes.ESI_CHARACTERS_READ_CORPORATION_ROLES_V1);
		}
		return esiOwner;
	}

	private static void setData(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		/*
	private Long totalSkillPoints = null;
	private Integer unallocatedSkillPoints = null;
		*/
		//Account Balance
		owner.setAccountBalances(list(getMyAccountBalance(owner, setValues, options)));

		//Asset
		owner.setAssets(getMyAssets(owner, setNull, setValues, options));

		//Blueprint
		RawBlueprint rawBlueprint = getRawBlueprint(options);
		owner.setBlueprints(map(rawBlueprint.getItemID(), rawBlueprint));

		//Contract
		MyContract saveMyContract = getMyContract(setNull, setValues, options);
		owner.setContracts(map(saveMyContract, list(getMyContractItem(saveMyContract, setNull, setValues, options))));

		//IndustryJob
		owner.setIndustryJobs(set(getMyIndustryJob(owner, setNull, setValues, options)));

		//Journal
		owner.setJournal(set(getMyJournal(owner, setNull, setValues, options)));

		//MarketOrder
		owner.setMarketOrders(set(getMyMarketOrder(owner, setNull, setValues, options)));

		//Transaction
		owner.setTransactions(set(getMyTransaction(owner, setNull, setValues, options)));

		//Clones
		owner.setClones(list(getRawClone(setNull, options)));

		//Skills
		owner.setSkills(list(getMySkill(owner, setNull, setValues, options)));

		//Mining
		owner.setMining(set(getMyMining(owner, setNull, setValues, options)));

		//Extractions
		owner.setExtractions(set(getMyExtraction(owner, setNull, setValues, options)));

		//Wallet Divisions
		//owner.setWalletDivisions(walletDivisions);

		//Asset Divisions
		//owner.setAssetDivisions(assetDivisions);
	}

	public static <T> Set<T> set(T o) {
        return new HashSet<>(Collections.singleton(o));
    }

	public static <K,V> Map<K,V> map(K key, V value) {
        return new HashMap<>(Collections.singletonMap(key, value));
    }

	public static <T> List<T> list(T o) {
        return new ArrayList<>(Collections.singletonList(o));
    }

	private static Item getItem(ConverterTestOptions options) {
		return ApiIdConverter.getItem(options.getInteger());
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
		MyAsset asset = new MyAsset(getRawAsset(setNull, options), getItem(options), owner, new ArrayList<>());
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
		rootAsset.setItemID(rootAsset.getItemID() + 1);
		MyAsset childAsset = getMyAsset(owner, setNull, setValues, options);
		if (setValues) {
			setValues(childAsset, options, null, false);
		}
		rootAsset.getAssets().add(childAsset);

		return list(rootAsset);
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
		RawIndustryJob rawIndustryJob = getRawIndustryJob(setNull, options);
		Item item = getItem(options);
		Item output = ApiIdConverter.getItem(rawIndustryJob.getProductTypeID());
		MyIndustryJob industryJob = new MyIndustryJob(rawIndustryJob, item, output, owner);
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

	public static RawClone getRawClone(boolean setNull, ConverterTestOptions options) {
		RawClone rawClone = RawClone.create();
		setValues(rawClone, options, setNull ? CharacterMiningResponse.class : null);
		return rawClone;
	}

	public static RawSkill getRawSkill(boolean setNull, ConverterTestOptions options) {
		RawSkill rawSkill = RawSkill.create();
		setValues(rawSkill, options, setNull ? CharacterMiningResponse.class : null);
		return rawSkill;
	}

	public static MySkill getMySkill(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		MySkill skill = new MySkill(getRawSkill(setNull, options), getItem(options), options.getString());
		if (setValues) {
			setValues(skill, options, null, false);
		}
		return skill;
	}

	public static RawMining getRawMining(boolean setNull, ConverterTestOptions options) {
		RawMining rawMining = RawMining.create();
		setValues(rawMining, options, setNull ? CharacterMiningResponse.class : null);
		return rawMining;
	}

	public static MyMining getMyMining(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		MyMining mining = new MyMining(getRawMining(setNull, options), getItem(options), options.getMyLocation());
		if (setValues) {
			setValues(mining, options, null, false);
		}
		return mining;
	}

	public static RawExtraction getRawExtraction(boolean setNull, ConverterTestOptions options) {
		RawExtraction rawExtraction = RawExtraction.create();
		setValues(rawExtraction, options, setNull ? CorporationMiningExtractionsResponse.class : null);
		return rawExtraction;
	}

	public static MyExtraction getMyExtraction(OwnerType owner, boolean setNull, boolean setValues, ConverterTestOptions options) {
		MyExtraction mining = new MyExtraction(getRawExtraction(setNull, options), options.getMyLocation());
		if (setValues) {
			setValues(mining, options, null, false);
		}
		return mining;
	}

	public static void testOwner(OwnerType esiOwner, boolean setNull, ConverterTestOptions options) {
		//Account Balance
		assertEquals(esiOwner.getAccountBalances().size(), 1);
		MyAccountBalance loadAccountBalance = esiOwner.getAccountBalances().get(0);
		testValues(loadAccountBalance, options, null, false);

		//Asset
		if (!esiOwner.getAssets().isEmpty()) {
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
		MyIndustryJob loadMyIndustryJob = esiOwner.getIndustryJobs().iterator().next();
		testValues(loadMyIndustryJob, options, setNull ? CharacterIndustryJobsResponse.class : null, false);

		//Journal
		assertEquals(esiOwner.getJournal().size(), 1);
		MyJournal loadMyJournal = esiOwner.getJournal().iterator().next();
		testValues(loadMyJournal, options, setNull ? CharacterWalletJournalResponse.class : null, false);

		//MarketOrder
		assertEquals(esiOwner.getMarketOrders().size(), 1);
		MyMarketOrder loadMyMarketOrder = esiOwner.getMarketOrders().iterator().next();
		testValues(loadMyMarketOrder, options, setNull ? CharacterOrdersResponse.class : null, false);

		//Transactions
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
		Map<String, Boolean> optional = getOptional(esi);
		for (Field field : fields) {
			Class<?> type = field.getType();
			String name = field.getName();
			if (ignore(object, field, type)) {
				continue;
			}
			try {
				field.setAccessible(true);
				if (!overwrite && field.get(object) != null) {
					continue;
				}
				if ((Long.class.equals(type) || long.class.equals(type))
						&& ("locationID".equals(name)
						|| "locationId".equals(name)
						|| "stationId".equals(name)
						|| "startLocationId".equals(name)
						|| "endLocationId".equals(name))) {
					if (isOptional(optional, field)) {
						if (type.equals(Boolean.class) || type.equals(boolean.class)) {
							field.set(object, false);
						} else {
							field.set(object, options.getNull());
						}
					} else {
						field.set(object, options.getLocationID());
					}
				}
				field.set(object, getValue(type, isOptional(optional, field), options));
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				fail(ex.getMessage());
			}
		}
	//Account Balance
		//ESI
		if (object instanceof CorporationWalletsResponse) {
			CorporationWalletsResponse response = (CorporationWalletsResponse) object;
			response.setDivision(response.getDivision() - 999);
		}
	//Assets
		//ESI Character
		if (object instanceof CharacterAssetsResponse) {
			CharacterAssetsResponse asset = (CharacterAssetsResponse) object;
			asset.setItemId(asset.getItemId() + 1); //Workaround for itemID == locationID
		}
		//ESI Corporation
		if (object instanceof CorporationAssetsResponse) {
			CorporationAssetsResponse asset = (CorporationAssetsResponse) object;
			asset.setItemId(asset.getItemId() + 1); //Workaround for itemID == locationID
		}
		//ESI Ship
		if (object instanceof CharacterShipResponse) {
			CharacterShipResponse asset = (CharacterShipResponse) object;
			asset.setShipItemId(asset.getShipItemId()+ 1); //Workaround for itemID == locationID
		}
		//ESI Location
		if (object instanceof CharacterLocationResponse) {
			CharacterLocationResponse asset = (CharacterLocationResponse) object;
			long locationID = options.getLocationID();
			if (locationID >= 30000000 && locationID <= 32000000) { //System
				asset.setSolarSystemId((int)locationID);
				asset.setStationId(null);
				asset.setStructureId(null);
			} else if (locationID >= 60000000 && locationID <= 64000000) { //Station
				asset.setSolarSystemId(null);
				asset.setStationId((int)locationID);
				asset.setStructureId(null);
			} else { //Other
				asset.setSolarSystemId(null);
				asset.setStationId(null);
				asset.setStructureId(locationID);
			}
		}
		//Raw
		if (object instanceof RawAsset) {
			RawAsset asset = (RawAsset) object;
			asset.setItemID(asset.getItemID() + 1); //Workaround for itemID == locationID
		}
		//EsiOwner
		if (object instanceof EsiOwner) {
			EsiOwner esiOwner = (EsiOwner) object;
			esiOwner.setAuth(EsiCallbackURL.LOCALHOST, options.getString(), options.getString());
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
			if (myAsset.getAssets().isEmpty()) {
				myAsset.setItemID(myAsset.getItemID() - 1); //Workaround for itemID == locationID
			} else {
				myAsset.setItemID(myAsset.getItemID() - 2); //Workaround for itemID == locationID
			}
			myAsset.setLocationID(options.getLong());
			myAsset.setLocationFlagString(options.getString());
			myAsset.setItemFlag(options.getItemFlag());
		}
		if (object instanceof MyContract) {
			MyContract myContract = (MyContract) object;
			if (myContract.getAvailability() == RawContract.ContractAvailability.PERSONAL
					&& (options.getContractAvailabilityRaw() == RawContract.ContractAvailability.CORPORATION
					|| options.getContractAvailabilityRaw() == RawContract.ContractAvailability.ALLIANCE)) {
				myContract.setAvailability(options.getContractAvailabilityRaw());
			}
			if (myContract.getStatus() != null) {
				myContract.setStatus(options.getContractStatusRaw());
			}
		}
		if (object instanceof MyContractItem) {
			MyContractItem myContractItem = (MyContractItem) object;
			myContractItem.setItemID(options.getLong());
			myContractItem.setLicensedRuns(options.getInteger());
			myContractItem.setME(options.getInteger());
			myContractItem.setTE(options.getInteger());
		}
		if (object instanceof MyIndustryJob) {
			MyIndustryJob industryJob = (MyIndustryJob) object;
			if (industryJob.getStatus() != null) {
				industryJob.setStatus(options.getIndustryJobStatusRaw());
			}
		}
		if (object instanceof MyMarketOrder) {
			MyMarketOrder marketOrder = (MyMarketOrder) object;
			marketOrder.setWalletDivision(options.getInteger());
			if (marketOrder.getState() != null) {
				marketOrder.setState(options.getMarketOrderStateRaw());
			}
			marketOrder.setStateString(options.getString());
			marketOrder.setIssuedBy(options.getInteger());
			marketOrder.setChanged(options.getDate());
			marketOrder.addChanges(set(new Change(new Date(), 0.0, 0)));
		}
		if (object instanceof MyJournal) {
			MyJournal journal = (MyJournal) object;
			journal.setDescription(options.getString());
		}
		Map<String, Boolean> optional = getOptional(esi);
		for (Field field : getField(object, true)) {
			Class<?> type = field.getType();
			if (ignore(object, field, type)) {
				continue;
			}
			try {
				field.setAccessible(true);
				assertEquals(getString(field, object, options.getIndex()), getValue(type, isOptional(optional, field), options), field.get(object));
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				fail(ex.getMessage());
			}
		}
	}

	private static List<Field> getField(Object object, boolean superClassOnly) {
		List<Field> fields = new ArrayList<>();
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
		if (Modifier.isStatic(field.getModifiers())) { //Ignore static fields
			return true;
		}
		if (type.equals(List.class)) {
			return true;
		}
		if (type.equals(Set.class)) {
			return true;
		}
		if (type.equals(Map.class)) {
			return true;
		}
		if (type.equals(ApiClient.class)) {
			return true;
		}
		if (type.equals(MarketApi.class)) {
			return true;
		}
		if (type.equals(IndustryApi.class)) {
			return true;
		}
		if (type.equals(CharacterApi.class)) {
			return true;
		}
		if (type.equals(AssetsApi.class)) {
			return true;
		}
		if (type.equals(WalletApi.class)) {
			return true;
		}
		if (type.equals(UniverseApi.class)) {
			return true;
		}
		if (type.equals(ContractsApi.class)) {
			return true;
		}
		if (type.equals(CorporationApi.class)) {
			return true;
		}
		if (type.equals(LocationApi.class)) {
			return true;
		}
		if (type.equals(UserInterfaceApi.class)) {
			return true;
		}
		if (type.equals(PlanetaryInteractionApi.class)) {
			return true;
		}
		if (type.equals(SkillsApi.class)) {
			return true;
		}
		if (type.equals(ClonesApi.class)) {
			return true;
		}
		return false;
	}

	private static Object getValue(Class<?> type, boolean optional, ConverterTestOptions options) {
		if (options == null) {
			throw new RuntimeException("Option is null");
		}
		if (optional) {
			if (type.equals(Boolean.class) || type.equals(boolean.class)) {
				return false; //Optional boolean should default to false
			} else {
				return options.getNull(); //Optional, must handle null
			}
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
		} else if (type.equals(RawContract.ContractAvailability.class)) {
			return options.getContractAvailabilityRaw();
		} else if (type.equals(RawContract.ContractStatus.class)) {
			return options.getContractStatusRaw();
		} else if (type.equals(RawContract.ContractType.class)) {
			return options.getContractTypeRaw();
		} else if (type.equals(RawIndustryJob.IndustryJobStatus.class)) {
			return options.getIndustryJobStatusRaw();
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
			return options.getLocationFlagEsiBlueprintCharacter();
		} else if (type.equals(CorporationBlueprintsResponse.LocationFlagEnum.class)) {
			return options.getLocationFlagEsiBlueprintCorporation();
		} else if (type.equals(OffsetDateTime.class)) {
			return options.getOffsetDateTime();
		} else if (type.equals(CharacterIndustryJobsResponse.StatusEnum.class)) {
			return options.getIndustryJobStatusEsiCharacter();
		} else if (type.equals(CorporationIndustryJobsResponse.StatusEnum.class)) {
			return options.getIndustryJobStatusEsiCorporation();
		} else if (type.equals(CharacterWalletJournalResponse.RefTypeEnum.class)) {
			return options.getJournalRefTypeEsiCharacter();
		} else if (type.equals(CharacterWalletJournalResponse.ContextIdTypeEnum.class)) {
			return options.getJournalContextTypeEsiCharacter();
		} else if (type.equals(CorporationWalletJournalResponse.RefTypeEnum.class)) {
			return options.getJournalRefTypeEsiCorporation();
		} else if (type.equals(CorporationWalletJournalResponse.ContextIdTypeEnum.class)) {
			return options.getJournalContextTypeEsiCorporation();
		} else if (type.equals(ContextType.class)) {
			return options.getJournalContextTypeRaw();
		} else if (type.equals(CharacterContractsResponse.AvailabilityEnum.class)) {
			return options.getContractAvailabilityEsiCharacter();
		} else if (type.equals(CorporationContractsResponse.AvailabilityEnum.class)) {
			return options.getContractAvailabilityEsiCorporation();
		} else if (type.equals(CharacterContractsResponse.StatusEnum.class)) {
			return options.getContractStatusEsiCharacter();
		} else if (type.equals(CorporationContractsResponse.StatusEnum.class)) {
			return options.getContractStatusEsiCorporation();
		} else if (type.equals(CharacterContractsResponse.TypeEnum.class)) {
			return options.getContractTypeEsiCharacter();
		} else if (type.equals(CorporationContractsResponse.TypeEnum.class)) {
			return options.getContractTypeEsiCorporation();
		} else if (type.equals(CharacterOrdersResponse.RangeEnum.class)) {
			return options.getMarketOrderRangeEsiCharacter();
		} else if (type.equals(CorporationOrdersResponse.RangeEnum.class)) {//
			return options.getMarketOrderRangeEsiCorporation();
		} else if (type.equals(CharacterAssetsResponse.LocationFlagEnum.class)) {
			return options.getLocationFlagEsiAssetsCharacter();
		} else if (type.equals(CorporationAssetsResponse.LocationFlagEnum.class)) {
			return options.getLocationFlagEsiAssetsCorporation();
		} else if (type.equals(CharacterAssetsResponse.LocationTypeEnum.class)) {
			return options.getLocationTypeEsiCharacter();
		} else if (type.equals(CorporationAssetsResponse.LocationTypeEnum.class)) {
			return options.getLocationTypeEsiCorporation();
		} else if (type.equals(BigDecimal.class)) {
			return options.getBigDecimal();
		} else if (type.equals(EsiCallbackURL.class)) {
			return options.getEsiCallbackURL();
		} else if (type.equals(ApiClient.class)) {
			return new ApiClient();
		} else if (type.equals(CorporationOrdersHistoryResponse.RangeEnum.class)) {
			return options.getMarketOrderRangeEsiCorporationHistory();
		} else if (type.equals(CorporationOrdersHistoryResponse.StateEnum.class)) {
			return options.getMarketOrderStateEsiCorporationHistory();
		} else if (type.equals(CharacterOrdersHistoryResponse.RangeEnum.class)) {
			return options.getMarketOrderRangeEsiCharacterHistory();
		} else if (type.equals(CharacterOrdersHistoryResponse.StateEnum.class)) {
			return options.getMarketOrderStateEsiCharacterHistory();
		} else if (type.equals(Outbid.class)) {
			return options.getMarketOrdersOutbid();
		} else if (type.equals(MyShip.class)) {
			return options.getMyShip();
		} else if (type.equals(JButton.class)) {
			return options.getButton();
		} else if (type.equals(MyBlueprint.class)) {
			return options.getMyBlueprint();
		} else {
			fail("No test value for: " + type.getSimpleName());
			return null;
		}
	}

	private static String getString(Field field, Object object, int index) {
		return object.getClass().getSimpleName() + "->" + field.getName() + " @index: " + index;
	}

	private static boolean isOptional(Map<String, Boolean> optional, Field field) {
		Boolean isOptional = optional.get(field.getName().toLowerCase());
		if (isOptional == null) {
			return false;
		} else {
			return isOptional;
		}
	}

	private static Map<String, Boolean> getOptional(Class<?> esi) {
		Map<String, Boolean> optional = new HashMap<>();
		if (esi == null) {
			return optional;
		}
		for (Method method : esi.getDeclaredMethods()) {
			String methodName = method.getName();
			String methodId = esi.getSimpleName() + "->" + methodName;
			if (methodId.equals("CharacterAssetsResponse->getQuantity") //Quantity is not optional in RawAsset
					|| methodId.equals("CorporationAssetsResponse->getQuantity") //Quantity is not optional in RawAsset
					//RawJournalExtraInfo is not optional in RawJournal
					|| methodId.equals("CharacterWalletJournalResponse->getExtraInfo")
					|| methodId.equals("CorporationWalletJournalResponse->getExtraInfo")
					//escrow is not optional in RawMarketOrder
					|| methodId.equals("CharacterOrdersHistoryResponse->getEscrow")
					|| methodId.equals("CorporationOrdersHistoryResponse->getEscrow")
					|| methodId.equals("CharacterOrdersResponse->getEscrow")
					|| methodId.equals("CorporationOrdersResponse->getEscrow")
					// issuedBy workaround for optional in CorporationOrdersHistoryResponse
					|| methodId.equals("CorporationOrdersHistoryResponse->getIssuedBy")
					//isBuyOrder is not optional in RawMarketOrder
					|| methodId.equals("CharacterOrdersHistoryResponse->getIsBuyOrder")
					|| methodId.equals("CorporationOrdersHistoryResponse->getIsBuyOrder")
					|| methodId.equals("CharacterOrdersResponse->getIsBuyOrder")
					|| methodId.equals("CorporationOrdersResponse->getIsBuyOrder")
					//minVolume is not optional in RawMarketOrder
					|| methodId.equals("CharacterOrdersHistoryResponse->getMinVolume")
					|| methodId.equals("CorporationOrdersHistoryResponse->getMinVolume")
					|| methodId.equals("CharacterOrdersResponse->getMinVolume")
					|| methodId.equals("CorporationOrdersResponse->getMinVolume")
					) {
				continue;
			}
			if (methodName.startsWith("get") && methodName.endsWith("String") ) {
				optional.put(methodName.toLowerCase().replaceFirst("get", "").replace("string", ""), false);
			} else if (Enum.class.isAssignableFrom(method.getReturnType())) {
				//All enums should be treaded as optional as they can be null for new unknown values
				optional.put(methodName.toLowerCase().replaceFirst("get", "") + "enum", true);
			} else if (methodName.startsWith("get")) {
				Annotation[] annotations = method.getAnnotations();
				for (Annotation annotation : annotations) {
					if (javax.annotation.Nonnull.class.equals(annotation.annotationType())) {
						optional.put(methodName.toLowerCase().replaceFirst("get", ""), false);
						break;
					}
					if (javax.annotation.Nullable.class.equals(annotation.annotationType())) {
						optional.put(methodName.toLowerCase().replaceFirst("get", ""), true);
						break;
					}
				}
				
			}
		}
		return optional;
	}
}
