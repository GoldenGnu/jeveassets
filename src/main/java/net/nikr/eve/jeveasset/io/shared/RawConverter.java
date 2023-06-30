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
package net.nikr.eve.jeveasset.io.shared;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CharacterBlueprintsResponse;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CorporationAssetsResponse;
import net.troja.eve.esi.model.CorporationBlueprintsResponse;
import net.troja.eve.esi.model.CorporationContractsResponse;
import net.troja.eve.esi.model.CorporationIndustryJobsResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;
import net.troja.eve.esi.model.MarketOrdersResponse;
import net.troja.eve.esi.model.MarketStructuresResponse;

public class RawConverter {

	private static Map<Integer, RawJournalRefType> journalRefTypesIDs = null;

	private static synchronized void createJournalRefTypesIDs() {
		if (journalRefTypesIDs == null) {
			journalRefTypesIDs = new HashMap<>();
			for (RawJournalRefType journalRefType : RawJournalRefType.values()) {
				journalRefTypesIDs.put(journalRefType.getID(), journalRefType);
			}
		}
	}

	public static boolean toBoolean(Boolean value) {
		if (value == null) {
			return false;
		} else {
			return value;
		}
	}

	public static Long toLong(Number value) {
		if (value != null) {
			return value.longValue();
		} else {
			return null;
		}
	}

	public static Long toLong(String value) {
		if (value != null) {
			try {
				return Long.valueOf(value);
			} catch (NumberFormatException ex) {
				//No problem just return null
			}
		}
		return null;
	}

	public static Integer toInteger(Number value) {
		if (value != null) {
			return value.intValue();
		} else {
			return null;
		}
	}

	public static int toInteger(Number value, int nullValue) {
		if (value != null) {
			return value.intValue();
		} else {
			return nullValue;
		}
	}

	public static Integer toInteger(String value) {
		if (value != null) {
			try {
				return Integer.valueOf(value);
			} catch (NumberFormatException ex) {
				//No problem just return null
			}
		}
		return null;
	}

	public static Float toFloat(Number value) {
		if (value != null) {
			return value.floatValue();
		} else {
			return null;
		}
	}

	public static double toDouble(Number value, double nullValue) {
		if (value != null) {
			return value.doubleValue();
		} else {
			return nullValue;
		}
	}

	public static Date toDate(OffsetDateTime dateTime) {
		if (dateTime == null) {
			return null;
		} else {
			return Date.from(dateTime.toInstant());
		}
	}

	public static Date toDate(LocalDate dateTime) {
		if (dateTime == null) {
			return null;
		} else {
			Date date = Date.from(dateTime.atStartOfDay().toInstant(ZoneOffset.UTC));
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 12);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		}
	}

	public static long toLocationID(CharacterLocationResponse shipLocation) {
		if (shipLocation.getStationId() != null) {
			return RawConverter.toLong(shipLocation.getStationId());
		} else if (shipLocation.getStructureId() != null) {
			return shipLocation.getStructureId();
		} else if (shipLocation.getSolarSystemId() != null) {
			return RawConverter.toLong(shipLocation.getSolarSystemId());
		} else {
			return 0; //Fallback
		}
	}

	public static ItemFlag toFlag(CharacterAssetsResponse.LocationFlagEnum value) {
		return toFlagEnum(value);
	}

	public static ItemFlag toFlag(CorporationBlueprintsResponse.LocationFlagEnum value) {
		return toFlagEnum(value);
	}

	public static ItemFlag toFlag(CorporationAssetsResponse.LocationFlagEnum value) {
		return toFlagEnum(value);
	}

	public static ItemFlag toFlag(CharacterBlueprintsResponse.LocationFlagEnum value) {
		return toFlagEnum(value);
	}

	private static <E extends Enum<E>> ItemFlag toFlagEnum(E value) {
		if (value == null) {
			return ApiIdConverter.getFlag(0);
		}
		try {
			LocationFlag locationFlag = LocationFlag.valueOf(value.name());
			return ApiIdConverter.getFlag(locationFlag.getID());
		} catch (IllegalArgumentException ex) {
			return ApiIdConverter.getFlag(0);
		}
	}

	public static ItemFlag toFlag(final int flagID, final String valueString) {
		ItemFlag itemFlag = StaticData.get().getItemFlags().get(flagID);
		if (itemFlag != null) {
			return itemFlag;
		}
		if (valueString != null) {
			for (LocationFlag value : LocationFlag.values()) {
				if (value.getValue().equals(valueString)) {
					return ApiIdConverter.getFlag(value.getID());
				}
			}
		}
		return ApiIdConverter.getFlag(0);
	}

	public static RawContract.ContractAvailability toContractAvailability(String valueEnum, String valueString) {
		if (valueEnum != null) {
			try {
				return RawContract.ContractAvailability.valueOf(valueEnum);
			} catch (IllegalArgumentException ex) {

			}
			switch (valueEnum.toLowerCase()) {
				case "private":
					return RawContract.ContractAvailability.PERSONAL;
				case "public":
					return RawContract.ContractAvailability.PUBLIC;
			}
		}
		if (valueString != null) {
			for (RawContract.ContractAvailability value : RawContract.ContractAvailability.values()) {
				if (value.getValue().equals(valueString)) {
					return value;
				}
			}
		}
		return null;
	}

	public static RawContract.ContractAvailability toContractAvailability(CharacterContractsResponse.AvailabilityEnum value) {
		return toContractAvailabilityEnum(value);
	}

	public static RawContract.ContractAvailability toContractAvailability(CorporationContractsResponse.AvailabilityEnum value) {
		return toContractAvailabilityEnum(value);
	}

	private static <E extends Enum<E>> RawContract.ContractAvailability toContractAvailabilityEnum(E value) {
		if (value == null) {
			return null;
		}
		try {
			return RawContract.ContractAvailability.valueOf(value.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static RawContract.ContractStatus toContractStatus(String valueEnum, String valueString) {
		if (valueEnum != null) {
			try {
				return RawContract.ContractStatus.valueOf(valueEnum);
			} catch (IllegalArgumentException ex) {

			}
			switch (valueEnum.toUpperCase()) {
				case "COMPLETED":
					return RawContract.ContractStatus.FINISHED;
				case "COMPLETEDBYCONTRACTOR":
					return RawContract.ContractStatus.FINISHED_CONTRACTOR;
				case "COMPLETEDBYISSUER":
					return RawContract.ContractStatus.FINISHED_ISSUER;
				case "INPROGRESS":
					return RawContract.ContractStatus.IN_PROGRESS;
			}
		}
		if (valueString != null) {
			for (RawContract.ContractStatus value : RawContract.ContractStatus.values()) {
				if (value.getValue().equals(valueString)) {
					return value;
				}
			}
		}
		return null;
	}

	public static RawContract.ContractStatus toContractStatus(CharacterContractsResponse.StatusEnum value) {
		return toContractStatusEnum(value);
	}

	public static RawContract.ContractStatus toContractStatus(CorporationContractsResponse.StatusEnum value) {
		return toContractStatusEnum(value);
	}

	private static <E extends Enum<E>> RawContract.ContractStatus toContractStatusEnum(E value) {
		if (value == null) {
			return null;
		}
		try {
			return RawContract.ContractStatus.valueOf(value.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static RawContract.ContractType toContractType(String valueEnum, String valueString) {
		if (valueEnum != null) {
			try {
				return RawContract.ContractType.valueOf(valueEnum);
			} catch (IllegalArgumentException ex) {

			}
			switch (valueEnum.toLowerCase()) {
				case "item_exchange":
					return RawContract.ContractType.ITEM_EXCHANGE;
				case "itemexchange":
					return RawContract.ContractType.ITEM_EXCHANGE;
				case "courier":
					return RawContract.ContractType.COURIER;
				case "loan":
					return RawContract.ContractType.LOAN;
				case "auction":
					return RawContract.ContractType.AUCTION;
				default:
					return RawContract.ContractType.UNKNOWN;
			}
		}
		if (valueString != null) {
			for (RawContract.ContractType value : RawContract.ContractType.values()) {
				if (value.getValue().equals(valueString)) {
					return value;
				}
			}
		}
		return null;
	}

	public static RawContract.ContractType toContractType(CharacterContractsResponse.TypeEnum value) {
		return toContractTypeEnum(value);
	}

	public static RawContract.ContractType toContractType(CorporationContractsResponse.TypeEnum value) {
		return toContractTypeEnum(value);
	}

	private static <E extends Enum<E>> RawContract.ContractType toContractTypeEnum(E value) {
		if (value == null) {
			return null;
		}
		try {
			return RawContract.ContractType.valueOf(value.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static RawIndustryJob.IndustryJobStatus toIndustryJobStatus(Integer valueInt, String valueEnum, String valueString) {
		if (valueEnum != null) {
			try {
				return RawIndustryJob.IndustryJobStatus.valueOf(valueEnum);
			} catch (IllegalArgumentException ex) {

			}
		}
		if (valueString != null) {
			for (RawIndustryJob.IndustryJobStatus value : RawIndustryJob.IndustryJobStatus.values()) {
				if (value.getValue().equals(valueString)) {
					return value;
				}
			}
		}
		if (valueInt != null) {
			switch (valueInt) {
				case 1:
					return RawIndustryJob.IndustryJobStatus.ACTIVE;
				case 2:
					return RawIndustryJob.IndustryJobStatus.PAUSED;
				case 3:
					return RawIndustryJob.IndustryJobStatus.READY;
				case 101:
					return RawIndustryJob.IndustryJobStatus.DELIVERED;
				case 102:
					return RawIndustryJob.IndustryJobStatus.CANCELLED;
				case 103:
					return RawIndustryJob.IndustryJobStatus.REVERTED;
			}
		}
		return null;
	}

	public static RawIndustryJob.IndustryJobStatus toIndustryJobStatus(CharacterIndustryJobsResponse.StatusEnum value) {
		return toIndustryJobStatusEnum(value);
	}

	public static RawIndustryJob.IndustryJobStatus toIndustryJobStatus(CorporationIndustryJobsResponse.StatusEnum value) {
		return toIndustryJobStatusEnum(value);
	}

	private static <E extends Enum<E>> RawIndustryJob.IndustryJobStatus toIndustryJobStatusEnum(E value) {
		if (value == null) {
			return null;
		}
		try {
			return RawIndustryJob.IndustryJobStatus.valueOf(value.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static RawJournalRefType toJournalRefType(Integer valueInt, String valueString) {
		CharacterWalletJournalResponse.RefTypeEnum charValue = CharacterWalletJournalResponse.RefTypeEnum.fromValue(valueString);
		if (charValue != null) {
			return toJournalRefType(charValue);
		}
		CorporationWalletJournalResponse.RefTypeEnum corpValue = CorporationWalletJournalResponse.RefTypeEnum.fromValue(valueString);
		if (corpValue != null) {
			return toJournalRefType(corpValue);
		}
		if (valueInt != null) {
			createJournalRefTypesIDs();
			return journalRefTypesIDs.get(valueInt);
		}
		return null;
	}

	public static RawJournalRefType toJournalRefType(CharacterWalletJournalResponse.RefTypeEnum value) {
		if (value == null) {
			return null;
		}
		try {
			return RawJournalRefType.valueOf(value.name());
		} catch (IllegalArgumentException ex) {
			switch (value) {
				case KILL_RIGHT_FEE:
					return RawJournalRefType.KILL_RIGHT;
				case RESOURCE_WARS_REWARD:
					return RawJournalRefType.RESOURCE_WARS_SITE_COMPLETION;
				case REACTION:
					return RawJournalRefType.REACTIONS;
				default: return null;
			}
		}
	}

	public static RawJournalRefType toJournalRefType(CorporationWalletJournalResponse.RefTypeEnum value) {
		if (value == null) {
			return null;
		}
		try {
			return RawJournalRefType.valueOf(value.name());
		} catch (IllegalArgumentException ex) {
			switch (value) {
				case KILL_RIGHT_FEE:
					return RawJournalRefType.KILL_RIGHT;
				case RESOURCE_WARS_REWARD:
					return RawJournalRefType.RESOURCE_WARS_SITE_COMPLETION;
				case REACTION:
					return RawJournalRefType.REACTIONS;
				default: return null;
			}
		}
	}

	public static RawJournal.ContextType toJournalContextType(CharacterWalletJournalResponse.ContextIdTypeEnum value) {
		return toJournalContextTypeEnum(value);
	}

	public static RawJournal.ContextType toJournalContextType(CorporationWalletJournalResponse.ContextIdTypeEnum value) {
		return toJournalContextTypeEnum(value);
	}

	private static <E extends Enum<E>> RawJournal.ContextType toJournalContextTypeEnum(E value) {
		if (value == null) {
			return null;
		}
		try {
			return RawJournal.ContextType.valueOf(value.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static RawJournal.ContextType toJournalContextType(String valueEnum, String valueString) {
		if (valueEnum != null) {
			try {
				return RawJournal.ContextType.valueOf(valueEnum);
			} catch (IllegalArgumentException ex) {

			}
		}
		if (valueString != null) {
			for (RawJournal.ContextType value : RawJournal.ContextType.values()) {
				if (value.getValue().equals(valueString)) {
					return value;
				}
			}
		}
		return null;
	}

	public static RawJournal.ContextType toJournalContextType(RawJournalRefType refType) {
		if (refType.getArgName() != null) {
			switch (refType.getArgName()) {
				case CONTRACT_ID:
					return RawJournal.ContextType.CONTRACT_ID;
				case DESTROYED_SHIP_TYPE_ID:
					return RawJournal.ContextType.TYPE_ID;
				case JOB_ID:
					return RawJournal.ContextType.INDUSTRY_JOB_ID;
				case TRANSACTION_ID:
					return RawJournal.ContextType.MARKET_TRANSACTION_ID;
			}
		}
		if (refType.getArgID() != null) {
			switch (refType.getArgID()) {
				case NPC_ID:
					return RawJournal.ContextType.TYPE_ID;
				case PLAYER_ID:
					return RawJournal.ContextType.CHARACTER_ID;
				case STATION_ID:
					return RawJournal.ContextType.STATION_ID;
				case SYSTEM_ID:
					return RawJournal.ContextType.SYSTEM_ID;
				case CORPORATION_ID:
					return RawJournal.ContextType.CORPORATION_ID;
				case ALLIANCE_ID:
					return RawJournal.ContextType.ALLIANCE_ID;
				case PLANET_ID:
					return RawJournal.ContextType.PLANET_ID;
			}
		}
		return null;
	}

	public static Long toJournalContextID(Long argID, String argName, RawJournalRefType refType) {
		if (refType.getArgName() != null) {
			switch (refType.getArgName()) {
				case CONTRACT_ID:
					return RawConverter.toLong(argName);
				case DESTROYED_SHIP_TYPE_ID:
					return RawConverter.toLong(argName);
				case JOB_ID:
					return RawConverter.toLong(argName);
				case TRANSACTION_ID:
					return RawConverter.toLong(argName);
			}
		}
		return argID;
	}

	public static RawMarketOrder.MarketOrderRange toMarketOrderRange(Integer valueInt, String valueEnum, String valueString) {
		if (valueEnum != null) {
			try {
				return RawMarketOrder.MarketOrderRange.valueOf(valueEnum);
			} catch (IllegalArgumentException ex) {

			}
		}
		if (valueString != null) {
			for (RawMarketOrder.MarketOrderRange value : RawMarketOrder.MarketOrderRange.values()) {
				if (value.getValue().equals(valueString)) {
					return value;
				}
			}
		}
		if (valueInt != null) {
			switch (valueInt) {
				case -1:
					return RawMarketOrder.MarketOrderRange.STATION;
				case 0:
					return RawMarketOrder.MarketOrderRange.SOLARSYSTEM;
				case 1:
					return RawMarketOrder.MarketOrderRange._1;
				case 2:
					return RawMarketOrder.MarketOrderRange._2;
				case 3:
					return RawMarketOrder.MarketOrderRange._3;
				case 4:
					return RawMarketOrder.MarketOrderRange._4;
				case 5:
					return RawMarketOrder.MarketOrderRange._5;
				case 10:
					return RawMarketOrder.MarketOrderRange._10;
				case 20:
					return RawMarketOrder.MarketOrderRange._20;
				case 30:
					return RawMarketOrder.MarketOrderRange._30;
				case 40:
					return RawMarketOrder.MarketOrderRange._40;
				case 32767:
					return RawMarketOrder.MarketOrderRange.REGION;
				default:
					throw new RuntimeException("Can't convert: " + valueInt + " to MarketOrderRange");
			}
		}
		return null;
	}

	public static RawMarketOrder.MarketOrderRange toMarketOrderRange(CharacterOrdersResponse.RangeEnum value) {
		return toMarketOrderRangeEnum(value);
	}

	public static RawMarketOrder.MarketOrderRange toMarketOrderRange(CharacterOrdersHistoryResponse.RangeEnum value) {
		return toMarketOrderRangeEnum(value);
	}

	public static RawMarketOrder.MarketOrderRange toMarketOrderRange(CorporationOrdersResponse.RangeEnum value) {
		return toMarketOrderRangeEnum(value);
	}

	public static RawMarketOrder.MarketOrderRange toMarketOrderRange(CorporationOrdersHistoryResponse.RangeEnum value) {
		return toMarketOrderRangeEnum(value);
	}

	public static RawMarketOrder.MarketOrderRange toMarketOrderRange(MarketOrdersResponse.RangeEnum value) {
		return toMarketOrderRangeEnum(value);
	}

	public static RawMarketOrder.MarketOrderRange toMarketOrderRange(MarketStructuresResponse.RangeEnum value) {
		return toMarketOrderRangeEnum(value);
	}

	private static <E extends Enum<E>> RawMarketOrder.MarketOrderRange toMarketOrderRangeEnum(E value) {
		if (value == null) {
			return null;
		}
		try {
			return RawMarketOrder.MarketOrderRange.valueOf(value.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static RawMarketOrder.MarketOrderState toMarketOrderState(Integer valueInt, String valueEnum, String valueString) {
		if (valueEnum != null) {
			try {
				return RawMarketOrder.MarketOrderState.valueOf(valueEnum);
			} catch (IllegalArgumentException ex) {

			}
		}
		if (valueString != null) {
			for (RawMarketOrder.MarketOrderState value : RawMarketOrder.MarketOrderState.values()) {
				if (value.getValue().equals(valueString)) {
					return value;
				}
			}
		}
		if (valueInt != null) {
			switch (valueInt) {
				case 0:
					return RawMarketOrder.MarketOrderState.OPEN;
				case 1:
					return RawMarketOrder.MarketOrderState.CLOSED;
				case 2:
					return RawMarketOrder.MarketOrderState.EXPIRED;
				case 3:
					return RawMarketOrder.MarketOrderState.CANCELLED;
				case 4:
					return RawMarketOrder.MarketOrderState.PENDING;
				case 5:
					return RawMarketOrder.MarketOrderState.CHARACTER_DELETED;
				case -100:
					return RawMarketOrder.MarketOrderState.UNKNOWN;
				default:
					throw new RuntimeException("Can't convert: " + valueInt + " to MarketOrderState");
			}
		}
		return null;
	}

	public static RawMarketOrder.MarketOrderState toMarketOrderState(CharacterOrdersHistoryResponse.StateEnum value) {
		return toMarketOrderStateEnum(value);
	}

	public static RawMarketOrder.MarketOrderState toMarketOrderState(CorporationOrdersHistoryResponse.StateEnum value) {
		return toMarketOrderStateEnum(value);
	}

	private static <E extends Enum<E>> RawMarketOrder.MarketOrderState toMarketOrderStateEnum(E value) {
		if (value == null) {
			return null;
		}
		try {
			return RawMarketOrder.MarketOrderState.valueOf(value.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static int fromMarketOrderIsBuyOrder(Boolean value) {
		return value ? 1 : 0;
	}

	public static Boolean toTransactionIsBuy(String value) {
		return value.toLowerCase().equals("buy");
	}

	public static Boolean toTransactionIsPersonal(String value) {
		return value.toLowerCase().equals("personal");
	}

	public static String fromTransactionIsBuy(Boolean value) {
		if (value) {
			return "buy";
		} else {
			return "sell";
		}
	}

	public static String fromTransactionIsPersonal(Boolean value) {
		if (value) {
			return "personal";
		} else {
			return "corporate";
		}
	}

	public static int toAssetQuantity(int quantity, Integer rawQuantity) {
		if (rawQuantity != null && rawQuantity < 0) {
			return rawQuantity;
		} else {
			return quantity;
		}
	}

	public enum LocationFlag {
		AUTOFIT("AutoFit", 0),
		HANGARALL("HangarAll", 0), //1000
		WALLET("Wallet", 1),
		OFFICEFOLDER("OfficeFolder", 2),
		WARDROBE("Wardrobe", 3),
		HANGAR("Hangar", 4),
		CARGO("Cargo", 5),
		IMPOUNDED("OfficeImpound", 6), //Impounded
		MODULE("Skill", 7), //Module
		SKILL("Skill", 7),
		REWARD("Reward", 8),
		LOSLOT0("LoSlot0", 11),
		LOSLOT1("LoSlot1", 12),
		LOSLOT2("LoSlot2", 13),
		LOSLOT3("LoSlot3", 14),
		LOSLOT4("LoSlot4", 15),
		LOSLOT5("LoSlot5", 16),
		LOSLOT6("LoSlot6", 17),
		LOSLOT7("LoSlot7", 18),
		MEDSLOT0("MedSlot0", 19),
		MEDSLOT1("MedSlot1", 20),
		MEDSLOT2("MedSlot2", 21),
		MEDSLOT3("MedSlot3", 22),
		MEDSLOT4("MedSlot4", 23),
		MEDSLOT5("MedSlot5", 24),
		MEDSLOT6("MedSlot6", 25),
		MEDSLOT7("MedSlot7", 26),
		HISLOT0("HiSlot0", 27),
		HISLOT1("HiSlot1", 28),
		HISLOT2("HiSlot2", 29),
		HISLOT3("HiSlot3", 30),
		HISLOT4("HiSlot4", 31),
		HISLOT5("HiSlot5", 32),
		HISLOT6("HiSlot6", 33),
		HISLOT7("HiSlot7", 34),
		ASSETSAFETY("AssetSafety", 36),
		CAPSULE("Capsule", 56),
		PILOT("Pilot", 57),
		SKILLINTRAINING("SkillInTraining", 61),
		CORPDELIVERIES("CorpMarket", 62), //CorpDeliveries
		LOCKED("Locked", 63),
		UNLOCKED("Unlocked", 64),
		BONUS("Bonus", 86),
		DRONEBAY("DroneBay", 87),
		BOOSTER("Booster", 88),
		IMPLANT("Implant", 89),
		SHIPHANGAR("ShipHangar", 90),
		SHIPOFFLINE("ShipOffline", 91),
		RIGSLOT0("RigSlot0", 92),
		RIGSLOT1("RigSlot1", 93),
		RIGSLOT2("RigSlot2", 94),
		RIGSLOT3("RigSlot3", 95),
		RIGSLOT4("RigSlot4", 96),
		RIGSLOT5("RigSlot5", 97),
		RIGSLOT6("RigSlot6", 98),
		RIGSLOT7("RigSlot7", 99),
		CORPSAG1("CorpSAG1", 115),
		CORPSAG2("CorpSAG2", 116),
		CORPSAG3("CorpSAG3", 117),
		CORPSAG4("CorpSAG4", 118),
		CORPSAG5("CorpSAG5", 119),
		CORPSAG6("CorpSAG6", 120),
		CORPSAG7("CorpSAG7", 121),
		SECONDARYSTORAGE("SecondaryStorage", 122),
		SUBSYSTEMSLOT0("SubSystemSlot0", 125),
		SUBSYSTEMSLOT1("SubSystemSlot1", 126),
		SUBSYSTEMSLOT2("SubSystemSlot2", 127),
		SUBSYSTEMSLOT3("SubSystemSlot3", 128),
		SUBSYSTEMSLOT4("SubSystemSlot4", 129),
		SUBSYSTEMSLOT5("SubSystemSlot5", 130),
		SUBSYSTEMSLOT6("SubSystemSlot6", 131),
		SUBSYSTEMSLOT7("SubSystemSlot7", 132),
		SPECIALIZEDFUELBAY("SpecializedFuelBay", 133),
		SPECIALIZEDOREHOLD("SpecializedAsteroidHold", 134),
		SPECIALIZEDGASHOLD("SpecializedGasHold", 135),
		SPECIALIZEDMINERALHOLD("SpecializedMineralHold", 136),
		SPECIALIZEDSALVAGEHOLD("SpecializedSalvageHold", 137),
		SPECIALIZEDSHIPHOLD("SpecializedShipHold", 138),
		SPECIALIZEDSMALLSHIPHOLD("SpecializedSmallShipHold", 139),
		SPECIALIZEDMEDIUMSHIPHOLD("SpecializedMediumShipHold", 140),
		SPECIALIZEDLARGESHIPHOLD("SpecializedLargeShipHold", 141),
		SPECIALIZEDINDUSTRIALSHIPHOLD("SpecializedIndustrialShipHold", 142),
		SPECIALIZEDAMMOHOLD("SpecializedAmmoHold", 143),
		STRUCTUREACTIVE("StructureActive", 144),
		STRUCTUREINACTIVE("StructureInactive", 145),
		JUNKYARDREPROCESSED("JunkyardReprocessed", 146),
		JUNKYARDTRASHED("JunkyardTrashed", 147),
		SPECIALIZEDCOMMANDCENTERHOLD("SpecializedCommandCenterHold", 148),
		SPECIALIZEDPLANETARYCOMMODITIESHOLD("SpecializedPlanetaryCommoditiesHold", 149),
		PLANETSURFACE("PlanetSurface", 150),
		SPECIALIZEDMATERIALBAY("SpecializedMaterialBay", 151),
		DUSTDATABANK("DustCharacterDatabank", 152), //DustDatabank
		DUSTBATTLE("DustCharacterBattle", 153), //DustBattle
		QUAFEBAY("QuafeBay", 154),
		FLEETHANGAR("FleetHangar", 155),
		HIDDENMODIFIERS("HiddenModifiers", 156),
		STRUCTUREOFFLINE("StructureOffline", 157),
		FIGHTERBAY("FighterBay", 158),
		FIGHTERTUBE0("FighterTube0", 159),
		FIGHTERTUBE1("FighterTube1", 160),
		FIGHTERTUBE2("FighterTube2", 161),
		FIGHTERTUBE3("FighterTube3", 162),
		FIGHTERTUBE4("FighterTube4", 163),
		SERVICESLOT0("StructureServiceSlot0", 164), //ServiceSlot0
		SERVICESLOT1("StructureServiceSlot1", 165), //ServiceSlot1
		SERVICESLOT2("StructureServiceSlot2", 166), //ServiceSlot2
		SERVICESLOT3("StructureServiceSlot3", 167), //ServiceSlot3
		SERVICESLOT4("StructureServiceSlot4", 168), //ServiceSlot4
		SERVICESLOT5("StructureServiceSlot5", 169), //ServiceSlot5
		SERVICESLOT6("StructureServiceSlot6", 170), //ServiceSlot6
		SERVICESLOT7("StructureServiceSlot7", 171), //ServiceSlot7
		STRUCTUREFUEL("StructureFuel", 172),
		DELIVERIES("Deliveries", 173),
		CRATELOOT("CrateLoot", 174),
		CORPSEBAY("CrateLoot", 174), //CorpseBay 175?
		BOOSTERBAY("BoosterBay", 176),
		SUBSYSTEMBAY("SubSystemBay", 177),
		FRIGATEESCAPEBAY("FrigateEscapeBay", 179),
		QUANTUMCOREROOM("StructureDeedBay", 180), //QuantumCoreRoom
		STRUCTUREDEEDBAY("StructureDeedBay", 180), //QuantumCoreRoom
		SPECIALIZEDICEHOLD("SpecializedIceHold", 181),
		SPECIALIZEDASTEROIDHOLD("SpecializedAsteroidHold", 182),
		MOBILEDEPOTHOLD("MobileDepot", 183),
		CORPORATIONGOALDELIVERIES("CorpProjectsHangar", 184),
		;

		private final String value;
		private final int id;

		LocationFlag(String value, int id) {
			this.value = value;
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public int getID() {
			return id;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}
}
