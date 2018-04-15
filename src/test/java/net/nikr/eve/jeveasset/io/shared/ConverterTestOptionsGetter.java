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

import com.beimin.eveapi.model.shared.KeyType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalExtraInfo;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.MarketPriceData;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel.UserPrice;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CharacterBlueprintsResponse;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterWalletJournalExtraInfoResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CorporationAssetsResponse;
import net.troja.eve.esi.model.CorporationBlueprintsResponse;
import net.troja.eve.esi.model.CorporationContainersLogsResponse;
import net.troja.eve.esi.model.CorporationContractsResponse;
import net.troja.eve.esi.model.CorporationIndustryJobsResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;
import net.troja.eve.esi.model.CorporationWalletJournalExtraInfoResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;
import static org.junit.Assert.fail;

public class ConverterTestOptionsGetter {

	private static List<ConverterTestOptions> options = null;

	public static synchronized List<ConverterTestOptions> getConverterOptions() {
		if (options == null) {
			options = new ArrayList<ConverterTestOptions>();
			int index = 0;
			while (add(options, index)) {
				index++;
			}
		}
		return options;
	}

	private static boolean add(List<ConverterTestOptions> list, int i) {
		IndexOptions indexOptions = new IndexOptions(i);
		if (indexOptions.isMaxed()) {
			return false;
		} else {
			list.add(indexOptions);
			return true;
		}
	}

	private static class IndexOptions implements ConverterTestOptions {

		//Primitive
		private static final Integer[] INTEGER = {5};
		private static final Float[] FLOAT = {5.1f};
		private static final Boolean[] BOOLEAN = {true};
		private static final Long[] LONG = {5L};
		private static final Double[] DOUBLE = {5.1};
		private static final Date[] DATE = {new Date()};
		private static final String[] STRING = {"StringValue"};
		//Data
		private static final MyLocation[] MY_LOCATION = {ApiIdConverter.getLocation(60003466)};
		private static final PriceData[] PRICE_DATA = {new PriceData()};
		private static final MarketPriceData[] MARKET_PRICE_DATA = {new MarketPriceData()};
		private static final Tags[] TAGS = {new Tags()};
		private static final Percent[] PERCENT = {new Percent(5.1)};
		private static final UserPrice[] USER_ITEM = {new UserPrice(DOUBLE[0], INTEGER[0], STRING[0])};
		//LocationType
		private static final RawAsset.LocationType[] RAW_LOCATION_TYPE = RawAsset.LocationType.values();
		private static final CharacterAssetsResponse.LocationTypeEnum[] ESI_LOCATION_TYPE_CHARACTER = CharacterAssetsResponse.LocationTypeEnum.values();
		private static final CorporationAssetsResponse.LocationTypeEnum[] ESI_LOCATION_TYPE_CORPORATION = CorporationAssetsResponse.LocationTypeEnum.values();
		private static final Long[] EVE_API_LOCATION_TYPE = {60003466L, 30000142L, 10000002L};
		//LocationFlag
		private static final List<LocationFlag> LOCATION_TYPE = createLocationTypes();
		//ContractAvailability
		private static final RawContract.ContractAvailability[] RAW_CONTRACT_AVAILABILITY = RawContract.ContractAvailability.values();
		private static final CharacterContractsResponse.AvailabilityEnum[] ESI_CONTRACTS_AVAILABILITY_CHARACTER = CharacterContractsResponse.AvailabilityEnum.values();
		private static final CorporationContractsResponse.AvailabilityEnum[] ESI_CONTRACTS_AVAILABILITY_CORPORATION = CorporationContractsResponse.AvailabilityEnum.values();
		private static final com.beimin.eveapi.model.shared.ContractAvailability[] XML_CONTRACT_AVAILABILITY = {
			com.beimin.eveapi.model.shared.ContractAvailability.PUBLIC,
			com.beimin.eveapi.model.shared.ContractAvailability.PRIVATE,
			com.beimin.eveapi.model.shared.ContractAvailability.PRIVATE,
			com.beimin.eveapi.model.shared.ContractAvailability.PRIVATE
		};
		private static final String[] EVE_KIT_CONTRACT_AVAILABILITY = {
			"public",
			"private",
			"private",
			"private"
		};
		//ContractStatus
		private static final RawContract.ContractStatus[] RAW_CONTRACT_STATUS = RawContract.ContractStatus.values();
		private static final CharacterContractsResponse.StatusEnum[] ESI_CONTRACT_STATUS_CHARACTER = CharacterContractsResponse.StatusEnum.values();
		private static final CorporationContractsResponse.StatusEnum[] ESI_CONTRACT_STATUS_CORPORATION = CorporationContractsResponse.StatusEnum.values();
		private static final com.beimin.eveapi.model.shared.ContractStatus[] XML_CONTRACT_STATUS = {
			com.beimin.eveapi.model.shared.ContractStatus.OUTSTANDING,
			com.beimin.eveapi.model.shared.ContractStatus.INPROGRESS,
			com.beimin.eveapi.model.shared.ContractStatus.COMPLETEDBYISSUER,
			com.beimin.eveapi.model.shared.ContractStatus.COMPLETEDBYCONTRACTOR,
			com.beimin.eveapi.model.shared.ContractStatus.COMPLETED,
			com.beimin.eveapi.model.shared.ContractStatus.CANCELLED,
			com.beimin.eveapi.model.shared.ContractStatus.REJECTED,
			com.beimin.eveapi.model.shared.ContractStatus.FAILED,
			com.beimin.eveapi.model.shared.ContractStatus.DELETED,
			com.beimin.eveapi.model.shared.ContractStatus.REVERSED
		};
		private static final String[] EVE_KIT_CONTRACT_STATUS = {
			"outstanding",
			"inprogress",
			"completedbyissuer",
			"completedbycontractor",
			"completed",
			"cancelled",
			"rejected",
			"failed",
			"deleted",
			"reversed"
		};
		//ContractType
		private static final RawContract.ContractType[] RAW_CONTRACT_TYPE = RawContract.ContractType.values();
		private static final CharacterContractsResponse.TypeEnum[] ESI_CONTRACT_TYPE_CHARACTER = CharacterContractsResponse.TypeEnum.values();
		private static final CorporationContractsResponse.TypeEnum[] ESI_CONTRACT_TYPE_CORPORATION = CorporationContractsResponse.TypeEnum.values();
		private static final com.beimin.eveapi.model.shared.ContractType[] XML_CONTRACT_TYPE = {
			null,
			com.beimin.eveapi.model.shared.ContractType.ITEMEXCHANGE,
			com.beimin.eveapi.model.shared.ContractType.AUCTION,
			com.beimin.eveapi.model.shared.ContractType.COURIER,
			com.beimin.eveapi.model.shared.ContractType.LOAN
		};
		private static final String[] EVE_KIT_CONTRACT_TYPE = {
			null,
			"itemexchange",
			"auction",
			"courier",
			"loan"
		};
		//IndustryJobStatus
		private static final RawIndustryJob.IndustryJobStatus[] RAW_INDUSTRY_JOB_STATUS = RawIndustryJob.IndustryJobStatus.values();
		private static final CharacterIndustryJobsResponse.StatusEnum[] ESI_INDUSTRY_JOB_STATUS_CHARACTER = CharacterIndustryJobsResponse.StatusEnum.values();
		private static final CorporationIndustryJobsResponse.StatusEnum[] ESI_INDUSTRY_JOB_STATUS_CORPORATION = CorporationIndustryJobsResponse.StatusEnum.values();
		private static final Integer[] EVE_API_INDUSTRY_JOB_STATUS = {1, 102, 101, 2, 3, 103};
		//JournalPartyType
		private static final RawJournal.JournalPartyType[] RAW_JOURNAL_PARTY_TYPE = RawJournal.JournalPartyType.values();
		private static final CharacterWalletJournalResponse.FirstPartyTypeEnum[] ESI_JOURNAL_PARTY_TYPE_FIRST_CHARACTER = CharacterWalletJournalResponse.FirstPartyTypeEnum.values();
		private static final CharacterWalletJournalResponse.SecondPartyTypeEnum[] ESI_JOURNAL_PARTY_TYPE_SECOND_CHARACTER = CharacterWalletJournalResponse.SecondPartyTypeEnum.values();
		private static final CorporationWalletJournalResponse.FirstPartyTypeEnum[] ESI_JOURNAL_PARTY_TYPE_FIRST_CORPORATION = CorporationWalletJournalResponse.FirstPartyTypeEnum.values();
		private static final CorporationWalletJournalResponse.SecondPartyTypeEnum[] ESI_JOURNAL_PARTY_TYPE_SECOND_CORPORATION = CorporationWalletJournalResponse.SecondPartyTypeEnum.values();
		private static final Integer[] EVE_API_JOURNAL_PARTY_TYPE = {1373, 2, 16159, 500001, 30000142};
		//JournalRefType
		private static final List<RefType> REF_TYPE = createRefTypes();
		//MarketOrderRange
		private static final RawMarketOrder.MarketOrderRange[] RAW_MARKET_ORDER_RANGE = RawMarketOrder.MarketOrderRange.values();
		private static final CharacterOrdersResponse.RangeEnum[] ESI_MARKET_ORDER_RANGE_CHARACTER = CharacterOrdersResponse.RangeEnum.values();
		private static final CharacterOrdersHistoryResponse.RangeEnum[] ESI_MARKET_ORDER_RANGE_CHARACTER_HISTORY = CharacterOrdersHistoryResponse.RangeEnum.values();
		private static final CorporationOrdersResponse.RangeEnum[] ESI_MARKET_ORDER_RANGE_CORPORATION = CorporationOrdersResponse.RangeEnum.values();
		private static final CorporationOrdersHistoryResponse.RangeEnum[] ESI_MARKET_ORDER_RANGE_CORPORATION_HISTORY = CorporationOrdersHistoryResponse.RangeEnum.values();
		private static final Integer[] EVE_API_MARKET_ORDER_RANGE = {1, 10, 2, 20, 3, 30, 4, 40, 5, 32767, 0, -1};
		//MarketOrderState
		private static final RawMarketOrder.MarketOrderState[] RAW_MARKET_ORDER_STATE = {
			RawMarketOrder.MarketOrderState.CANCELLED,
			RawMarketOrder.MarketOrderState.CHARACTER_DELETED,
			RawMarketOrder.MarketOrderState.CLOSED,
			RawMarketOrder.MarketOrderState.EXPIRED,
			RawMarketOrder.MarketOrderState.OPEN,
			RawMarketOrder.MarketOrderState.PENDING,
			//UNKNOWN("Unknown");  //Ignored: jEveAssets value
		};
		private static final CharacterOrdersHistoryResponse.StateEnum[] ESI_MARKET_ORDER_STATE_CHARACTER_HISTORY = CharacterOrdersHistoryResponse.StateEnum.values();
		private static final CorporationOrdersHistoryResponse.StateEnum[] ESI_MARKET_ORDER_STATE_CORPORATION_HISTORY = CorporationOrdersHistoryResponse.StateEnum.values();
		private static final Integer[] EVE_API_MARKET_ORDER_STATE = {3, 5, 1, 2, 0, 4};
		//ContainerLog
		private static final RawContainerLog.ContainerAction[] RAW_CONTAINER_ACTION = RawContainerLog.ContainerAction.values();
		private static final CorporationContainersLogsResponse.ActionEnum[] ESI_CONTAINER_ACTION = CorporationContainersLogsResponse.ActionEnum.values();
		private static final RawContainerLog.ContainerPasswordType[] RAW_CONTAINER_PASSWORD_TYPE = RawContainerLog.ContainerPasswordType.values();
		private static final CorporationContainersLogsResponse.PasswordTypeEnum[] ESI_CONTAINER_PASSWORD_TYPE = CorporationContainersLogsResponse.PasswordTypeEnum.values();
		//Owners
		private static final EsiCallbackURL[] ESI_CALLBACK_URL = EsiCallbackURL.values();
		private static final KeyType[] KEY_TYPE = {KeyType.CORPORATION};
		//Control
		private static final int MAX = createMax();

		//JournalExtraInfo
		private final RawJournalExtraInfo[] rawJournalExtraInfo;
		private final CharacterWalletJournalExtraInfoResponse[] esiJournalExtraInfoCharacter;
		private final CorporationWalletJournalExtraInfoResponse[] esiJournalExtraInfoCorporation;
		
		//Controls
		private final int index;

		public IndexOptions(int index) {
			this.index = index;
			RawJournalRefType refType = get(REF_TYPE, index).getRawJournalRefType();
			rawJournalExtraInfo = new RawJournalExtraInfo[1];
			rawJournalExtraInfo[0] = new RawJournalExtraInfo(getLong(), getLong().toString(), refType);
			esiJournalExtraInfoCharacter = new CharacterWalletJournalExtraInfoResponse[1];
			CharacterWalletJournalExtraInfoResponse journalExtraInfoResponseCharacter = new CharacterWalletJournalExtraInfoResponse();
			esiJournalExtraInfoCharacter[0] = journalExtraInfoResponseCharacter; //TODO set correct values
			esiJournalExtraInfoCorporation = new CorporationWalletJournalExtraInfoResponse[1];
			CorporationWalletJournalExtraInfoResponse journalExtraInfoResponseCorporation = new CorporationWalletJournalExtraInfoResponse();
			esiJournalExtraInfoCorporation[0] = journalExtraInfoResponseCorporation; //TODO set correct values
			if (refType.getArgName() != null) {
				switch (refType.getArgName()) {
					case CONTRACT_ID:
						journalExtraInfoResponseCharacter.setContractId(getLong().intValue());
						journalExtraInfoResponseCorporation.setContractId(getLong().intValue());
						break;
					case DESTROYED_SHIP_TYPE_ID:
						journalExtraInfoResponseCharacter.setDestroyedShipTypeId(getLong().intValue());
						journalExtraInfoResponseCorporation.setDestroyedShipTypeId(getLong().intValue());
						break;
					case JOB_ID:
						journalExtraInfoResponseCharacter.setJobId(getLong().intValue());
						journalExtraInfoResponseCorporation.setJobId(getLong().intValue());
						break;
					case NPC_NAME:
						journalExtraInfoResponseCharacter.setNpcName(String.valueOf(getLong()));
						journalExtraInfoResponseCorporation.setNpcName(String.valueOf(getLong()));
						break;
					case PLAYER_NAME:
						break;
					case STATION_NAME:
						break;
					case TRANSACTION_ID:
						journalExtraInfoResponseCharacter.setTransactionId(getLong());
						journalExtraInfoResponseCorporation.setTransactionId(getLong());
						break;
					case CORPORATION_NAME:
						break;
					case ALLIANCE_NAME:
						break;
					case PLANET_NAME:
						break;
					default:
						throw new RuntimeException("RawJournal.ArgName switch incomplete");
				}
			}
			if (refType.getArgID() != null) {
				switch (refType.getArgID()) {
					case NPC_ID:
						journalExtraInfoResponseCharacter.setNpcId(getLong().intValue());
						journalExtraInfoResponseCorporation.setNpcId(getLong().intValue());
						break;
					case PLAYER_ID:
						journalExtraInfoResponseCharacter.setCharacterId(getLong().intValue());
						journalExtraInfoResponseCorporation.setCharacterId(getLong().intValue());
						break;
					case STATION_ID:
						journalExtraInfoResponseCharacter.setLocationId(getLong());
						journalExtraInfoResponseCorporation.setLocationId(getLong());
						break;
					case SYSTEM_ID:
						journalExtraInfoResponseCharacter.setSystemId(getLong().intValue());
						journalExtraInfoResponseCorporation.setSystemId(getLong().intValue());
						break;
					case CORPORATION_ID:
						journalExtraInfoResponseCharacter.setCorporationId(getLong().intValue());
						journalExtraInfoResponseCorporation.setCorporationId(getLong().intValue());
						break;
					case ALLIANCE_ID:
						journalExtraInfoResponseCharacter.setAllianceId(getLong().intValue());
						journalExtraInfoResponseCorporation.setAllianceId(getLong().intValue());
						break;
					case PLANET_ID:
						journalExtraInfoResponseCharacter.setPlanetId(getLong().intValue());
						journalExtraInfoResponseCorporation.setPlanetId(getLong().intValue());
						break;
					default:
						throw new RuntimeException("RawJournal.ArgID switch incomplete");
				}
			}
		}

		private static List<LocationFlag> createLocationTypes() {
			Map<Integer, LocationFlag> locationFlags = new HashMap<Integer, LocationFlag>();
			//Character Blueprints
			for (CharacterBlueprintsResponse.LocationFlagEnum locationFlagEnum : CharacterBlueprintsResponse.LocationFlagEnum.values()) {
				ItemFlag itemFlag = RawConverter.toFlag(locationFlagEnum);
				LocationFlag locationFlag = locationFlags.get(itemFlag.getFlagID());
				if (locationFlag == null) {
					locationFlag = new LocationFlag(itemFlag);
					locationFlags.put(itemFlag.getFlagID(), locationFlag);
				}
				locationFlag.setLocationFlag(locationFlagEnum);
				locationFlag.setLocationFlag(locationFlagEnum.toString());
			}
			//Corporation Blueprints
			for (CorporationBlueprintsResponse.LocationFlagEnum locationFlagEnum : CorporationBlueprintsResponse.LocationFlagEnum.values()) {
				ItemFlag itemFlag = RawConverter.toFlag(locationFlagEnum);
				LocationFlag locationFlag = locationFlags.get(itemFlag.getFlagID());
				if (locationFlag == null) {
					locationFlag = new LocationFlag(itemFlag);
					locationFlags.put(itemFlag.getFlagID(), locationFlag);
				}
				locationFlag.setLocationFlag(locationFlagEnum);
				locationFlag.setLocationFlag(locationFlagEnum.toString());
			}
			//Character Assets
			for (CharacterAssetsResponse.LocationFlagEnum locationFlagEnum : CharacterAssetsResponse.LocationFlagEnum.values()) {
				ItemFlag itemFlag = RawConverter.toFlag(locationFlagEnum);
				LocationFlag locationFlag = locationFlags.get(itemFlag.getFlagID());
				if (locationFlag == null) {
					locationFlag = new LocationFlag(itemFlag);
					locationFlags.put(itemFlag.getFlagID(), locationFlag);
				}
				locationFlag.setLocationFlag(locationFlagEnum);
				locationFlag.setLocationFlag(locationFlagEnum.toString());
			}
			//Corporation Assets
			for (CorporationAssetsResponse.LocationFlagEnum locationFlagEnum : CorporationAssetsResponse.LocationFlagEnum.values()) {
				ItemFlag itemFlag = RawConverter.toFlag(locationFlagEnum);
				LocationFlag locationFlag = locationFlags.get(itemFlag.getFlagID());
				if (locationFlag == null) {
					locationFlag = new LocationFlag(itemFlag);
					locationFlags.put(itemFlag.getFlagID(), locationFlag);
				}
				locationFlag.setLocationFlag(locationFlagEnum);
			}
			//Corporation Containers Logs
			for (CorporationContainersLogsResponse.LocationFlagEnum locationFlagEnum : CorporationContainersLogsResponse.LocationFlagEnum.values()) {
				ItemFlag itemFlag = RawConverter.toFlag(locationFlagEnum);
				LocationFlag locationFlag = locationFlags.get(itemFlag.getFlagID());
				if (locationFlag == null) {
					locationFlag = new LocationFlag(itemFlag);
					locationFlags.put(itemFlag.getFlagID(), locationFlag);
				}
				locationFlag.setLocationFlag(locationFlagEnum);
				locationFlag.setLocationFlag(locationFlagEnum.toString());
			}
			Set<Integer> remove = new HashSet<Integer>();
			for (LocationFlag locationType : locationFlags.values()) {
				if (locationType.isEmpty()) {
					remove.add(locationType.getItemFlag().getFlagID());
				}
			}
			for (Integer index : remove) {
				locationFlags.remove(index);
			}
			return new ArrayList<LocationFlag>(locationFlags.values());
		}

		private static List<RefType> createRefTypes() {
			Map<Integer, RefType> refTypes = new HashMap<Integer, RefType>();
			//EveAPI
			for (com.beimin.eveapi.model.shared.RefType refTypeEnum : com.beimin.eveapi.model.shared.RefType.values()) {
				RawJournalRefType rawJournalRefType = RawConverter.toJournalRefType(refTypeEnum.getId());
				RefType refType = refTypes.get(rawJournalRefType.getID());
				if (refType == null) {
					refType = new RefType(rawJournalRefType);
					refTypes.put(rawJournalRefType.getID(), refType);
				}
				refType.setRefType(refTypeEnum);
			}
			//ESI Character
			for (CharacterWalletJournalResponse.RefTypeEnum refTypeEnum : CharacterWalletJournalResponse.RefTypeEnum.values()) {
				RawJournalRefType rawJournalRefType = RawConverter.toJournalRefType(refTypeEnum);
				if (rawJournalRefType == null) {
					fail(refTypeEnum.name() + " not found");
					continue;
				}
				RefType refType = refTypes.get(rawJournalRefType.getID());
				if (refType == null) {
					refType = new RefType(rawJournalRefType);
					refTypes.put(rawJournalRefType.getID(), refType);
				}
				refType.setRefType(refTypeEnum);
			}
			//ESI Corporation
			for (CorporationWalletJournalResponse.RefTypeEnum refTypeEnum : CorporationWalletJournalResponse.RefTypeEnum.values()) {
				RawJournalRefType rawJournalRefType = RawConverter.toJournalRefType(refTypeEnum);
				if (rawJournalRefType == null) {
					fail(refTypeEnum.name() + " not found");
					continue;
				}
				RefType refType = refTypes.get(rawJournalRefType.getID());
				if (refType == null) {
					refType = new RefType(rawJournalRefType);
					refTypes.put(rawJournalRefType.getID(), refType);
				}
				refType.setRefType(refTypeEnum);
			}
			Set<Integer> remove = new HashSet<Integer>();
			for (RefType refType : refTypes.values()) {
				if (refType.isEmpty()) {
					remove.add(refType.getRawJournalRefType().getID());
				}
			}
			for (Integer index : remove) {
				refTypes.remove(index);
			}
			return new ArrayList<RefType>(refTypes.values());
		}

		private static int createMax() {
			int tempMax = 0;
			tempMax = Math.max(tempMax, INTEGER.length);
			tempMax = Math.max(tempMax, FLOAT.length);
			tempMax = Math.max(tempMax, BOOLEAN.length);
			tempMax = Math.max(tempMax, LONG.length);
			tempMax = Math.max(tempMax, DOUBLE.length);
			tempMax = Math.max(tempMax, DATE.length);
			tempMax = Math.max(tempMax, STRING.length);

			tempMax = Math.max(tempMax, MY_LOCATION.length);
			tempMax = Math.max(tempMax, PRICE_DATA.length);
			tempMax = Math.max(tempMax, MARKET_PRICE_DATA.length);
			tempMax = Math.max(tempMax, TAGS.length);
			tempMax = Math.max(tempMax, PERCENT.length);
			tempMax = Math.max(tempMax, USER_ITEM.length);
			//LocationType
			tempMax = Math.max(tempMax, RAW_LOCATION_TYPE.length);
			tempMax = Math.max(tempMax, ESI_LOCATION_TYPE_CHARACTER.length);
			tempMax = Math.max(tempMax, ESI_LOCATION_TYPE_CORPORATION.length);
			tempMax = Math.max(tempMax, EVE_API_LOCATION_TYPE.length);
			//LocationFlag
			tempMax = Math.max(tempMax, LOCATION_TYPE.size());
			//ContractAvailability
			tempMax = Math.max(tempMax, RAW_CONTRACT_AVAILABILITY.length);
			tempMax = Math.max(tempMax, ESI_CONTRACTS_AVAILABILITY_CHARACTER.length);
			tempMax = Math.max(tempMax, ESI_CONTRACTS_AVAILABILITY_CORPORATION.length);
			tempMax = Math.max(tempMax, XML_CONTRACT_AVAILABILITY.length);
			tempMax = Math.max(tempMax, EVE_KIT_CONTRACT_AVAILABILITY.length);
			//ContractStatus
			tempMax = Math.max(tempMax, RAW_CONTRACT_STATUS.length);
			tempMax = Math.max(tempMax, ESI_CONTRACT_STATUS_CHARACTER.length);
			tempMax = Math.max(tempMax, ESI_CONTRACT_STATUS_CORPORATION.length);
			tempMax = Math.max(tempMax, XML_CONTRACT_STATUS.length);
			tempMax = Math.max(tempMax, EVE_KIT_CONTRACT_STATUS.length);
			//ContractType
			tempMax = Math.max(tempMax, RAW_CONTRACT_TYPE.length);
			tempMax = Math.max(tempMax, ESI_CONTRACT_TYPE_CHARACTER.length);
			tempMax = Math.max(tempMax, ESI_CONTRACT_TYPE_CORPORATION.length);
			tempMax = Math.max(tempMax, XML_CONTRACT_TYPE.length);
			tempMax = Math.max(tempMax, EVE_KIT_CONTRACT_TYPE.length);
			//IndustryJobStatus
			tempMax = Math.max(tempMax, RAW_INDUSTRY_JOB_STATUS.length);
			tempMax = Math.max(tempMax, ESI_INDUSTRY_JOB_STATUS_CHARACTER.length);
			tempMax = Math.max(tempMax, ESI_INDUSTRY_JOB_STATUS_CORPORATION.length);
			tempMax = Math.max(tempMax, EVE_API_INDUSTRY_JOB_STATUS.length);
			//JournalExtraInfo
			//tempMax = Math.max(tempMax, rawJournalExtraInfo.length);
			//tempMax = Math.max(tempMax, esiJournalExtraInfoCharacter.length);
			//JournalPartyType
			tempMax = Math.max(tempMax, RAW_JOURNAL_PARTY_TYPE.length);
			tempMax = Math.max(tempMax, ESI_JOURNAL_PARTY_TYPE_FIRST_CHARACTER.length);
			tempMax = Math.max(tempMax, ESI_JOURNAL_PARTY_TYPE_SECOND_CHARACTER.length);
			tempMax = Math.max(tempMax, ESI_JOURNAL_PARTY_TYPE_FIRST_CORPORATION.length);
			tempMax = Math.max(tempMax, ESI_JOURNAL_PARTY_TYPE_SECOND_CORPORATION.length);
			tempMax = Math.max(tempMax, EVE_API_JOURNAL_PARTY_TYPE.length);
			//JournalRefType
			tempMax = Math.max(tempMax, REF_TYPE.size());
			//MarketOrderRange
			tempMax = Math.max(tempMax, RAW_MARKET_ORDER_RANGE.length);
			tempMax = Math.max(tempMax, ESI_MARKET_ORDER_RANGE_CHARACTER.length);
			tempMax = Math.max(tempMax, ESI_MARKET_ORDER_RANGE_CORPORATION.length);
			tempMax = Math.max(tempMax, EVE_API_MARKET_ORDER_RANGE.length);
			//MarketOrderState
			tempMax = Math.max(tempMax, RAW_MARKET_ORDER_STATE.length);
			tempMax = Math.max(tempMax, EVE_API_MARKET_ORDER_STATE.length);
			//Owners
			tempMax = Math.max(tempMax, ESI_CALLBACK_URL.length);
			tempMax = Math.max(tempMax, KEY_TYPE.length);
			return tempMax;
		}

		@Override
		public int getIndex() {
			return index;
		}

		private <E> E get(E[] array, int index) {
			if (index < array.length) {
				return array[index];
			} else {
				return array[0];
			}
		}
		private <E> E get(List<E> list, Integer index) {
			if (index < list.size()) {
				return list.get(index);
			} else {
				return list.get(0);
			}
		}

		public boolean isMaxed() {
			return index >= MAX;
		}

		@Override
		public Integer getInteger() {
			return get(INTEGER, index);
		}

		@Override
		public Float getFloat() {
			return get(FLOAT, index);
		}

		@Override
		public Boolean getBoolean() {
			return get(BOOLEAN, index);
		}

		@Override
		public final Long getLong() {
			return get(LONG, index);
		}

		@Override
		public Double getDouble() {
			return get(DOUBLE, index);
		}

		@Override
		public Date getDate() {
			return get(DATE, index);
		}

		@Override
		public String getString() {
			return get(STRING, index);
		}

		@Override
		public MyLocation getMyLocation() {
			return get(MY_LOCATION, index);
		}

		@Override
		public PriceData getPriceData() {
			return get(PRICE_DATA, index);
		}

		@Override
		public UserItem<Integer, Double> getUserPrice() {
			return get(USER_ITEM, index);
		}

		@Override
		public MarketPriceData getMarketPriceData() {
			return get(MARKET_PRICE_DATA, index);
		}

		@Override
		public Tags getTags() {
			return get(TAGS, index);
		}

		@Override
		public RawBlueprint getRawBlueprint() {
			return ConverterTestUtil.getRawBlueprint(this);
		}

		@Override
		public Percent getPercent() {
			return get(PERCENT, index);
		}

//LocationType
		@Override
		public Long getLocationTypeEveApi() {
			return get(EVE_API_LOCATION_TYPE, index);
		}

		@Override
		public RawAsset.LocationType getLocationTypeRaw() {
			return get(RAW_LOCATION_TYPE, index);
		}

		@Override
		public CharacterAssetsResponse.LocationTypeEnum getLocationTypeEsiCharacter() {
			return get(ESI_LOCATION_TYPE_CHARACTER, index);
		}

		@Override
		public CorporationAssetsResponse.LocationTypeEnum getLocationTypeEsiCorporation() {
			return get(ESI_LOCATION_TYPE_CORPORATION, index);
		}

//LocationFlag
		@Override
		public CharacterBlueprintsResponse.LocationFlagEnum getLocationFlagEsiBlueprintCharacter() {
			return get(LOCATION_TYPE, index).getLocationFlagEsiBlueprintsCharacter();
		}

		@Override
		public CorporationBlueprintsResponse.LocationFlagEnum getLocationFlagEsiBlueprintCorporation() {
			return get(LOCATION_TYPE, index).getLocationFlagEsiBlueprintsCorporation();
		}

		@Override
		public CharacterAssetsResponse.LocationFlagEnum getLocationFlagEsiAssetsCharacter() {
			return get(LOCATION_TYPE, index).getLocationFlagEsiAssetsCharacter();
		}

		@Override
		public CorporationAssetsResponse.LocationFlagEnum getLocationFlagEsiAssetsCorporation() {
			return get(LOCATION_TYPE, index).getLocationFlagEsiAssetsCorporation();
		}

		@Override
		public CorporationContainersLogsResponse.LocationFlagEnum getLocationFlagEsiContainersLogsCorporation() {
			return get(LOCATION_TYPE, index).getLocationFlagEsiContainersLogsCorporation();
		}

		@Override
		public int getLocationFlagEveApi() {
			return get(LOCATION_TYPE, index).getItemFlag().getFlagID();
		}

		@Override
		public String getLocationFlagEveKit() {
			return get(LOCATION_TYPE, index).getLocationFlagEveKit();
		}

		@Override
		public ItemFlag getItemFlag() {
			return get(LOCATION_TYPE, index).getItemFlag();
		}

//ContractAvailability
		@Override
		public RawContract.ContractAvailability getContractAvailabilityRaw() {
			return get(RAW_CONTRACT_AVAILABILITY, index);
		}

		@Override
		public CharacterContractsResponse.AvailabilityEnum getContractAvailabilityEsiCharacter() {
			return get(ESI_CONTRACTS_AVAILABILITY_CHARACTER, index);
		}

		@Override
		public CorporationContractsResponse.AvailabilityEnum getContractAvailabilityEsiCorporation() {
			return get(ESI_CONTRACTS_AVAILABILITY_CORPORATION, index);
		}

		@Override
		public com.beimin.eveapi.model.shared.ContractAvailability getContractAvailabilityEveApi() {
			return get(XML_CONTRACT_AVAILABILITY, index);
		}

		@Override
		public String getContractAvailabilityEveKit() {
			return get(EVE_KIT_CONTRACT_AVAILABILITY, index);
		}

//ContractStatus
		@Override
		public RawContract.ContractStatus getContractStatusRaw() {
			return get(RAW_CONTRACT_STATUS, index);
		}

		@Override
		public CharacterContractsResponse.StatusEnum getContractStatusEsiCharacter() {
			return get(ESI_CONTRACT_STATUS_CHARACTER, index);
		}

		@Override
		public CorporationContractsResponse.StatusEnum getContractStatusEsiCorporation() {
			return get(ESI_CONTRACT_STATUS_CORPORATION, index);
		}

		@Override
		public com.beimin.eveapi.model.shared.ContractStatus getContractStatusEveApi() {
			return get(XML_CONTRACT_STATUS, index);
		}

		@Override
		public String getContractStatusEveKit() {
			return get(EVE_KIT_CONTRACT_STATUS, index);
		}

//ContractType
		@Override
		public RawContract.ContractType getContractTypeRaw() {
			return get(RAW_CONTRACT_TYPE, index);
		}

		@Override
		public com.beimin.eveapi.model.shared.ContractType getContractTypeEveApi() {
			return get(XML_CONTRACT_TYPE, index);
		}

		@Override
		public CharacterContractsResponse.TypeEnum getContractTypeEsiCharacter() {
			return get(ESI_CONTRACT_TYPE_CHARACTER, index);
		}

		@Override
		public CorporationContractsResponse.TypeEnum getContractTypeEsiCorporation() {
			return get(ESI_CONTRACT_TYPE_CORPORATION, index);
		}

		@Override
		public String getContractTypeEveKit() {
			return get(EVE_KIT_CONTRACT_TYPE, index);
		}

//IndustryJobStatus
		@Override
		public RawIndustryJob.IndustryJobStatus getIndustryJobStatusRaw() {
			return get(RAW_INDUSTRY_JOB_STATUS, index);
		}

		@Override
		public CharacterIndustryJobsResponse.StatusEnum getIndustryJobStatusEsiCharacter() {
			return get(ESI_INDUSTRY_JOB_STATUS_CHARACTER, index);
		}

		@Override
		public CorporationIndustryJobsResponse.StatusEnum getIndustryJobStatusEsiCorporation() {
			return get(ESI_INDUSTRY_JOB_STATUS_CORPORATION, index);
		}

		@Override
		public int getIndustryJobStatusEveApi() {
			return get(EVE_API_INDUSTRY_JOB_STATUS, index);
		}

		@Override
		public String getIndustryJobStatusEveKit() {
			return get(ESI_INDUSTRY_JOB_STATUS_CHARACTER, index).toString();
		}

//JournalExtraInfo
		@Override
		public RawJournalExtraInfo getJournalExtraInfoRaw() {
			return get(rawJournalExtraInfo, index);
		}

		@Override
		public CharacterWalletJournalExtraInfoResponse getJournalExtraInfoEsiCharacter() {
			return get(esiJournalExtraInfoCharacter, index);
		}

		@Override
		public CorporationWalletJournalExtraInfoResponse getJournalExtraInfoEsiCorporation() {
			return get(esiJournalExtraInfoCorporation, index);
		}

//JournalPartyType
		@Override
		public RawJournal.JournalPartyType getJournalPartyTypeRaw() {
			return get(RAW_JOURNAL_PARTY_TYPE, index);
		}

		@Override
		public CharacterWalletJournalResponse.FirstPartyTypeEnum getJournalPartyTypeEsiFirstCharacter() {
			return get(ESI_JOURNAL_PARTY_TYPE_FIRST_CHARACTER, index);
		}

		@Override
		public CharacterWalletJournalResponse.SecondPartyTypeEnum getJournalPartyTypeEsiSecondCharacter() {
			return get(ESI_JOURNAL_PARTY_TYPE_SECOND_CHARACTER, index);
		}

		@Override
		public CorporationWalletJournalResponse.FirstPartyTypeEnum getJournalPartyTypeEsiFirstCorporation() {
			return get(ESI_JOURNAL_PARTY_TYPE_FIRST_CORPORATION, index);
		}

		@Override
		public CorporationWalletJournalResponse.SecondPartyTypeEnum getJournalPartyTypeEsiSecondCorporation() {
			return get(ESI_JOURNAL_PARTY_TYPE_SECOND_CORPORATION, index);
		}

		@Override
		public int getJournalPartyTypeEveApi() {
			return get(EVE_API_JOURNAL_PARTY_TYPE, index);
		}

//JournalRefType
		@Override
		public RawJournalRefType getJournalRefTypeRaw() {
			return get(REF_TYPE, index).getRawJournalRefType();
		}

		@Override
		public CharacterWalletJournalResponse.RefTypeEnum getJournalRefTypeEsiCharacter() {
			return get(REF_TYPE, index).getEsiJournalRefTypeCharacter();
		}

		@Override
		public CorporationWalletJournalResponse.RefTypeEnum getJournalRefTypeEsiCorporation() {
			return get(REF_TYPE, index).getEsiJournalRefTypeCorporation();
		}

		@Override
		public com.beimin.eveapi.model.shared.RefType getJournalRefTypeEveApi() {
			return get(REF_TYPE, index).getXmlJournalRefType();
		}

//MarketOrderRange
		@Override
		public RawMarketOrder.MarketOrderRange getMarketOrderRangeRaw() {
			return get(RAW_MARKET_ORDER_RANGE, index);
		}

		@Override
		public CharacterOrdersResponse.RangeEnum getMarketOrderRangeEsiCharacter() {
			return get(ESI_MARKET_ORDER_RANGE_CHARACTER, index);
		}

		@Override
		public CharacterOrdersHistoryResponse.RangeEnum getMarketOrderRangeEsiCharacterHistory() {
			return get(ESI_MARKET_ORDER_RANGE_CHARACTER_HISTORY, index);
		}

		@Override
		public CorporationOrdersResponse.RangeEnum getMarketOrderRangeEsiCorporation() {
			return get(ESI_MARKET_ORDER_RANGE_CORPORATION, index);
		}

		@Override
		public CorporationOrdersHistoryResponse.RangeEnum getMarketOrderRangeEsiCorporationHistory() {
			return get(ESI_MARKET_ORDER_RANGE_CORPORATION_HISTORY, index);
		}

		@Override
		public int getMarketOrderRangeEveApi() {
			return get(EVE_API_MARKET_ORDER_RANGE, index);
		}

//MarketOrderState
		@Override
		public RawMarketOrder.MarketOrderState getMarketOrderStateRaw() {
			return get(RAW_MARKET_ORDER_STATE, index);
		}

		@Override
		public CharacterOrdersHistoryResponse.StateEnum getMarketOrderStateEsiCharacterHistory() {
			return get(ESI_MARKET_ORDER_STATE_CHARACTER_HISTORY, index);
		}

		@Override
		public CorporationOrdersHistoryResponse.StateEnum getMarketOrderStateEsiCorporationHistory() {
			return get(ESI_MARKET_ORDER_STATE_CORPORATION_HISTORY, index);
		}

		@Override
		public int getMarketOrderStateEveApi() {
			return get(EVE_API_MARKET_ORDER_STATE, index);
		}

//ContainerLog
		@Override
		public RawContainerLog.ContainerAction getContainerActionRaw() {
			return get(RAW_CONTAINER_ACTION, index);
		}

		@Override
		public CorporationContainersLogsResponse.ActionEnum getContainerActionEsi() {
			return get(ESI_CONTAINER_ACTION, index);
		}

		@Override
		public RawContainerLog.ContainerPasswordType getContainerPasswordTypeRaw() {
			return get(RAW_CONTAINER_PASSWORD_TYPE, index);
		}

		@Override
		public CorporationContainersLogsResponse.PasswordTypeEnum getContainerPasswordTypeEsi() {
			return get(ESI_CONTAINER_PASSWORD_TYPE, index);
		}
//Owner
		@Override
		public EsiCallbackURL getEsiCallbackURL() {
			return get(ESI_CALLBACK_URL, index);
		}

		@Override
		public KeyType getKeyType() {
			return get(KEY_TYPE, index);
		}

		@Override
		public EveApiAccount getEveApiAccount() {
			return ConverterTestUtil.getEveApiAccount(this);
		}
	}

	private static class LocationFlag {
		private String locationFlagEveKit;
		private CharacterBlueprintsResponse.LocationFlagEnum locationFlagEsiBlueprintsCharacter;
		private CorporationBlueprintsResponse.LocationFlagEnum locationFlagEsiBlueprintsCorporation;
		private CharacterAssetsResponse.LocationFlagEnum locationFlagEsiAssetsCharacter;
		private CorporationAssetsResponse.LocationFlagEnum locationFlagEsiAssetsCorporation;
		private CorporationContainersLogsResponse.LocationFlagEnum locationFlagEsiContainersLogsCorporation;
		private final ItemFlag itemFlag;

		public LocationFlag(ItemFlag itemFlag) {
			this.itemFlag = itemFlag;
		}

		public boolean isEmpty() {
			return locationFlagEsiBlueprintsCharacter == null
					|| locationFlagEsiBlueprintsCorporation == null
					|| locationFlagEsiAssetsCharacter == null
					|| locationFlagEsiAssetsCorporation == null
					|| locationFlagEveKit == null;
		}

		public ItemFlag getItemFlag() {
			return itemFlag;
		}

		public CharacterBlueprintsResponse.LocationFlagEnum getLocationFlagEsiBlueprintsCharacter() {
			return locationFlagEsiBlueprintsCharacter;
		}

		public CorporationBlueprintsResponse.LocationFlagEnum getLocationFlagEsiBlueprintsCorporation() {
			return locationFlagEsiBlueprintsCorporation;
		}

		public CharacterAssetsResponse.LocationFlagEnum getLocationFlagEsiAssetsCharacter() {
			return locationFlagEsiAssetsCharacter;
		}

		public CorporationAssetsResponse.LocationFlagEnum getLocationFlagEsiAssetsCorporation() {
			return locationFlagEsiAssetsCorporation;
		}

		public CorporationContainersLogsResponse.LocationFlagEnum getLocationFlagEsiContainersLogsCorporation() {
			return locationFlagEsiContainersLogsCorporation;
		}

		public String getLocationFlagEveKit() {
			return locationFlagEveKit;
		}

		public void setLocationFlag(CharacterAssetsResponse.LocationFlagEnum locationFlagEsiAssetsCharacter) {
			this.locationFlagEsiAssetsCharacter = locationFlagEsiAssetsCharacter;
		}

		public void setLocationFlag(CorporationAssetsResponse.LocationFlagEnum locationFlagEsiAssetsCorporation) {
			this.locationFlagEsiAssetsCorporation = locationFlagEsiAssetsCorporation;
		}

		public void setLocationFlag(CorporationBlueprintsResponse.LocationFlagEnum locationFlagEsiBlueprintsCorporation) {
			this.locationFlagEsiBlueprintsCorporation = locationFlagEsiBlueprintsCorporation;
		}

		public void setLocationFlag(CharacterBlueprintsResponse.LocationFlagEnum locationFlagEsiBlueprintsCharacter) {
			this.locationFlagEsiBlueprintsCharacter = locationFlagEsiBlueprintsCharacter;
		}

		public void setLocationFlag(CorporationContainersLogsResponse.LocationFlagEnum locationFlagEsiContainersLogsCorporation) {
			this.locationFlagEsiContainersLogsCorporation = locationFlagEsiContainersLogsCorporation;
		}

		public void setLocationFlag(String locationFlagEveKit) {
			this.locationFlagEveKit = locationFlagEveKit;
		}
	}

	private static class RefType {
		private CharacterWalletJournalResponse.RefTypeEnum EsiJournalRefTypeCharacter;
		private CorporationWalletJournalResponse.RefTypeEnum EsiJournalRefTypeCorporation;
		private com.beimin.eveapi.model.shared.RefType XmlJournalRefType;

		private final RawJournalRefType rawJournalRefType;

		public RefType(RawJournalRefType rawJournalRefType) {
			this.rawJournalRefType = rawJournalRefType;
		}

		public boolean isEmpty() {
			return EsiJournalRefTypeCharacter == null
					|| EsiJournalRefTypeCorporation == null
					|| XmlJournalRefType == null;
		}

		public RawJournalRefType getRawJournalRefType() {
			return rawJournalRefType;
		}

		public CharacterWalletJournalResponse.RefTypeEnum getEsiJournalRefTypeCharacter() {
			return EsiJournalRefTypeCharacter;
		}

		public CorporationWalletJournalResponse.RefTypeEnum getEsiJournalRefTypeCorporation() {
			return EsiJournalRefTypeCorporation;
		}

		public com.beimin.eveapi.model.shared.RefType getXmlJournalRefType() {
			return XmlJournalRefType;
		}

		public void setRefType(CharacterWalletJournalResponse.RefTypeEnum EsiJournalRefTypeCharacter) {
			this.EsiJournalRefTypeCharacter = EsiJournalRefTypeCharacter;
		}

		public void setRefType(CorporationWalletJournalResponse.RefTypeEnum EsiJournalRefTypeCorporation) {
			this.EsiJournalRefTypeCorporation = EsiJournalRefTypeCorporation;
		}

		public void setRefType(com.beimin.eveapi.model.shared.RefType XmlJournalRefType) {
			this.XmlJournalRefType = XmlJournalRefType;
		}
	}
}
