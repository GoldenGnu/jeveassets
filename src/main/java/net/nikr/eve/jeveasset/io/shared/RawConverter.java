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

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalExtraInfo;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderRange;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderState;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CorporationContainersLogsResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;

public class RawConverter {

	private static Map<Integer, RawJournalRefType> journalRefTypesIDs = null;

	private static synchronized void createJournalRefTypesIDs() {
		if (journalRefTypesIDs == null) {
			journalRefTypesIDs = new HashMap<Integer, RawJournalRefType>();
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

	public static ItemFlag toFlag(net.troja.eve.esi.model.CharacterAssetsResponse.LocationFlagEnum locationFlagEnum) {
		LocationFlag locationFlag = LocationFlag.valueOf(locationFlagEnum.name());
		return ApiIdConverter.getFlag(locationFlag.getID());
	}

	public static ItemFlag toFlag(net.troja.eve.esi.model.CorporationBlueprintsResponse.LocationFlagEnum locationFlagEnum) {
		LocationFlag locationFlag = LocationFlag.valueOf(locationFlagEnum.name());
		return ApiIdConverter.getFlag(locationFlag.getID());
	}

	public static ItemFlag toFlag(net.troja.eve.esi.model.CorporationAssetsResponse.LocationFlagEnum locationFlagEnum) {
		LocationFlag locationFlag = LocationFlag.valueOf(locationFlagEnum.name());
		return ApiIdConverter.getFlag(locationFlag.getID());
	}

	public static ItemFlag toFlag(net.troja.eve.esi.model.CharacterBlueprintsResponse.LocationFlagEnum locationFlagEnum) {
		LocationFlag locationFlag = LocationFlag.valueOf(locationFlagEnum.name());
		return ApiIdConverter.getFlag(locationFlag.getID());
	}

	public static ItemFlag toFlag(net.troja.eve.esi.model.CorporationContainersLogsResponse.LocationFlagEnum locationFlagEnum) {
		LocationFlag locationFlag = LocationFlag.valueOf(locationFlagEnum.name());
		return ApiIdConverter.getFlag(locationFlag.getID());
	}

	public static ItemFlag toFlag(String locationFlag) {
		LocationFlag locationFlagEnum = LocationFlag.valueOf(locationFlag.toUpperCase());
		if (locationFlagEnum == null) {
			return ApiIdConverter.getFlag(0);
		}
		return ApiIdConverter.getFlag(locationFlagEnum.getID());
	}

	public static RawAsset.LocationType toAssetLocationType(Long locationID) {
		MyLocation location = ApiIdConverter.getLocation(locationID);
		if (location.isStation()) {
			return RawAsset.LocationType.STATION;
		} else if (location.isSystem()) {
			return RawAsset.LocationType.SOLAR_SYSTEM;
		} else {
			return RawAsset.LocationType.OTHER;
		}
	}

	public static RawContainerLog.ContainerAction toContainerLogAction(CorporationContainersLogsResponse.ActionEnum actionEnum) {
		return RawContainerLog.ContainerAction.valueOf(actionEnum.name());
	}

	public static RawContainerLog.ContainerPasswordType toContainerLogPasswordType(CorporationContainersLogsResponse.PasswordTypeEnum passwordTypeEnum) {
		if (passwordTypeEnum == null) {
			return null;
		}
		return RawContainerLog.ContainerPasswordType.valueOf(passwordTypeEnum.name());
	}

	public static RawContract.ContractAvailability toContractAvailability(String value) {
		switch (value.toLowerCase()) {
			case "private":
				return RawContract.ContractAvailability.PERSONAL;
			case "public":
				return RawContract.ContractAvailability.PUBLIC;
			case "alliance":
				return RawContract.ContractAvailability.ALLIANCE;
			case "corporation":
				return RawContract.ContractAvailability.CORPORATION;
			case "personal":
				return RawContract.ContractAvailability.PERSONAL;
			default:
				throw new RuntimeException("Can't convert: " + value + " to ContractAvailability");
		}
	}

	public static RawContract.ContractAvailability toContractAvailability(com.beimin.eveapi.model.shared.ContractAvailability value) {
		switch (value) {
			case PRIVATE:
				return RawContract.ContractAvailability.PERSONAL;
			case PUBLIC:
				return RawContract.ContractAvailability.PUBLIC;
			default:
				throw new RuntimeException("Can't convert: " + value + " to ContractAvailability");
		}
	}

	public static RawContract.ContractStatus toContractStatus(String value) {
		switch (value.toUpperCase()) {
			case "CANCELLED":
				return RawContract.ContractStatus.CANCELLED;
			case "DELETED":
				return RawContract.ContractStatus.DELETED;
			case "FAILED":
				return RawContract.ContractStatus.FAILED;
			case "FINISHED":
				return RawContract.ContractStatus.FINISHED;
			case "COMPLETED":
				return RawContract.ContractStatus.FINISHED;
			case "FINISHED_CONTRACTOR":
				return RawContract.ContractStatus.FINISHED_CONTRACTOR;
			case "COMPLETEDBYCONTRACTOR":
				return RawContract.ContractStatus.FINISHED_CONTRACTOR;
			case "FINISHED_ISSUER":
				return RawContract.ContractStatus.FINISHED_ISSUER;
			case "COMPLETEDBYISSUER":
				return RawContract.ContractStatus.FINISHED_ISSUER;
			case "IN_PROGRESS":
				return RawContract.ContractStatus.IN_PROGRESS;
			case "INPROGRESS":
				return RawContract.ContractStatus.IN_PROGRESS;
			case "OUTSTANDING":
				return RawContract.ContractStatus.OUTSTANDING;
			case "REJECTED":
				return RawContract.ContractStatus.REJECTED;
			case "REVERSED":
				return RawContract.ContractStatus.REVERSED;
			default:
				throw new RuntimeException("Can't convert: " + value + " to ContractStatus");
		}
	}

	public static RawContract.ContractStatus toContractStatus(com.beimin.eveapi.model.shared.ContractStatus value) {
		switch (value) {
			case OUTSTANDING:
				return RawContract.ContractStatus.OUTSTANDING;
			case DELETED:
				return RawContract.ContractStatus.DELETED;
			case COMPLETED:
				return RawContract.ContractStatus.FINISHED;
			case FAILED:
				return RawContract.ContractStatus.FAILED;
			case COMPLETEDBYISSUER:
				return RawContract.ContractStatus.FINISHED_ISSUER;
			case COMPLETEDBYCONTRACTOR:
				return RawContract.ContractStatus.FINISHED_CONTRACTOR;
			case CANCELLED:
				return RawContract.ContractStatus.CANCELLED;
			case REJECTED:
				return RawContract.ContractStatus.REJECTED;
			case REVERSED:
				return RawContract.ContractStatus.REVERSED;
			case INPROGRESS:
				return RawContract.ContractStatus.IN_PROGRESS;
			default:
				throw new RuntimeException("Can't convert: " + value + " to ContractStatus");
		}

	}

	public static RawContract.ContractType toContractType(String value) {
		if (value == null) {
			return RawContract.ContractType.UNKNOWN;
		} else {
			switch (value.toLowerCase()) {
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
	}

	public static RawContract.ContractType toContractType(com.beimin.eveapi.model.shared.ContractType value) {
		if (value == null) {
			return RawContract.ContractType.UNKNOWN;
		} else {
			switch (value) {
				case ITEMEXCHANGE:
					return RawContract.ContractType.ITEM_EXCHANGE;
				case COURIER:
					return RawContract.ContractType.COURIER;
				case LOAN:
					return RawContract.ContractType.LOAN;
				case AUCTION:
					return RawContract.ContractType.AUCTION;
				default:
					return RawContract.ContractType.UNKNOWN;
			}
		}
	}

	public static RawIndustryJob.IndustryJobStatus toIndustryJobStatus(int value) {
		switch (value) {
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
			default:
				throw new RuntimeException("Can't convert: " + value + " to IndustryJobStatus");
		}
	}

	public static RawIndustryJob.IndustryJobStatus toIndustryJobStatus(String value) {
		return RawIndustryJob.IndustryJobStatus.fromValue(value);
	}

	public static int fromIndustryJobStatus(RawIndustryJob.IndustryJobStatus value) {
		switch (value) {
			case ACTIVE:
				return 1;
			case PAUSED:
				return 2;
			case READY:
				return 3;
			case DELIVERED:
				return 101;
			case CANCELLED:
				return 102;
			case REVERTED:
				return 103;
			default:
				throw new RuntimeException("Can't convert: " + value + " to IndustryJobStatus");
		}
	}

	public static RawJournalRefType toJournalRefType(int value) {
		createJournalRefTypesIDs();
		return journalRefTypesIDs.get(value);
	}

	public static RawJournalRefType toJournalRefType(String value) {
		CharacterWalletJournalResponse.RefTypeEnum charValue = CharacterWalletJournalResponse.RefTypeEnum.fromValue(value);
		if (charValue != null) {
			return toJournalRefType(charValue);
		}
		CorporationWalletJournalResponse.RefTypeEnum corpValue = CorporationWalletJournalResponse.RefTypeEnum.fromValue(value);
		if (corpValue != null) {
			return toJournalRefType(corpValue);
		}
		return null;
	}

	public static RawJournalRefType toJournalRefType(CharacterWalletJournalResponse.RefTypeEnum value) {
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

	public static RawJournal.JournalPartyType toJournalPartyType(String value) {
		return RawJournal.JournalPartyType.fromValue(value);
	}

	public static RawJournal.JournalPartyType toJournalPartyType(Integer value) {
		if (value == null) {
			return null;
		} else if (value == 2) {
			return RawJournal.JournalPartyType.CORPORATION;
		} else if (value >= 1373 && value <= 1386) {
			return RawJournal.JournalPartyType.CHARACTER;
		} else if (value == 16159) {
			return RawJournal.JournalPartyType.ALLIANCE;
		} else if (value > 500000 && value < 500025) {
			return RawJournal.JournalPartyType.FACTION;
		} else {
			return RawJournal.JournalPartyType.SYSTEM;
		}
	}

	public static RawJournal.JournalPartyType toJournalPartyType(CharacterWalletJournalResponse.FirstPartyTypeEnum value) {
		if (value == null) {
			return null;
		} else {
			return RawJournal.JournalPartyType.valueOf(value.name());
		}
	}

	public static RawJournal.JournalPartyType toJournalPartyType(CorporationWalletJournalResponse.FirstPartyTypeEnum value) {
		if (value == null) {
			return null;
		} else {
			return RawJournal.JournalPartyType.valueOf(value.name());
		}
	}

	public static RawJournal.JournalPartyType toJournalPartyType(CharacterWalletJournalResponse.SecondPartyTypeEnum value) {
		if (value == null) {
			return null;
		} else {
			return RawJournal.JournalPartyType.valueOf(value.name());
		}
	}

	public static RawJournal.JournalPartyType toJournalPartyType(CorporationWalletJournalResponse.SecondPartyTypeEnum value) {
		if (value == null) {
			return null;
		} else {
			return RawJournal.JournalPartyType.valueOf(value.name());
		}
	}

	public static RawMarketOrder.MarketOrderRange toMarketOrderRange(String value) {
		return MarketOrderRange.fromValue(value);
	}

	public static RawMarketOrder.MarketOrderRange toMarketOrderRange(int value) {
		switch (value) {
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
				throw new RuntimeException("Can't convert: " + value + " to MarketOrderRange");
		}
	}

	public static Long fromRawJournalExtraInfoArgID(RawJournalExtraInfo journalExtraInfo) {
		if (journalExtraInfo.getAllianceId() != null) {
			return RawConverter.toLong(journalExtraInfo.getAllianceId());
		} else if (journalExtraInfo.getCharacterId() != null) {
			return RawConverter.toLong(journalExtraInfo.getCharacterId());
		} else if (journalExtraInfo.getLocationId() != null) {
			return RawConverter.toLong(journalExtraInfo.getLocationId());
		} else if (journalExtraInfo.getNpcId() != null) {
			return RawConverter.toLong(journalExtraInfo.getNpcId());
		} else if (journalExtraInfo.getPlanetId() != null) {
			return RawConverter.toLong(journalExtraInfo.getPlanetId());
		} else if (journalExtraInfo.getSystemId() != null) {
			return RawConverter.toLong(journalExtraInfo.getSystemId());
		} else if (journalExtraInfo.getCorporationId() != null) {
			return RawConverter.toLong(journalExtraInfo.getCorporationId());
		} else {
			return null;
		}
	}

	public static String fromRawJournalExtraInfoArgName(RawJournalExtraInfo journalExtraInfo) {
		if (journalExtraInfo.getCharacterId() != null) {
			return journalExtraInfo.getCharacterId().toString();
		} else if (journalExtraInfo.getContractId() != null) {
			return journalExtraInfo.getContractId().toString();
		} else if (journalExtraInfo.getDestroyedShipTypeId() != null) {
			return journalExtraInfo.getDestroyedShipTypeId().toString();
		} else if (journalExtraInfo.getJobId() != null) {
			return journalExtraInfo.getJobId().toString();
		} else if (journalExtraInfo.getNpcName() != null) {
			return journalExtraInfo.getNpcName();
		} else if (journalExtraInfo.getSystemId() != null) {
			return journalExtraInfo.getSystemId().toString();
		} else if (journalExtraInfo.getTransactionId() != null) {
			return journalExtraInfo.getTransactionId().toString();
		} else {
			return null;
		}
	}

	public static int fromMarketOrderRange(RawMarketOrder.MarketOrderRange value) {
		switch (value) {
			case STATION:
				return -1;
			case SOLARSYSTEM:
				return 0;
			case _1:
				return 1;
			case _2:
				return 2;
			case _3:
				return 3;
			case _4:
				return 4;
			case _5:
				return 5;
			case _10:
				return 10;
			case _20:
				return 20;
			case _30:
				return 30;
			case _40:
				return 40;
			case REGION:
				return 32767;
			default:
				throw new RuntimeException("Can't convert: " + value + " to MarketOrderRange");
		}
	}

	public static RawMarketOrder.MarketOrderState toMarketOrderState(String value) {
		return MarketOrderState.fromValue(value);
	}

	public static RawMarketOrder.MarketOrderState toMarketOrderState(int value) {
		switch (value) {
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
				throw new RuntimeException("Can't convert: " + value + " to MarketOrderState");
		}
	}

	public static int fromMarketOrderState(RawMarketOrder.MarketOrderState value) {
		switch (value) {
			case OPEN:
				return 0;
			case CLOSED:
				return 1;
			case EXPIRED:
				return 2;
			case CANCELLED:
				return 3;
			case PENDING:
				return 4;
			case CHARACTER_DELETED:
				return 5;
			case UNKNOWN:
				return -100;
			default:
				throw new RuntimeException("Can't convert: " + value + " to MarketOrderState");
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

	public static Integer fromJournalPartyType(RawJournal.JournalPartyType value) {
		if (value == null) {
			return null;
		} else {
			switch (value) {
				case ALLIANCE:
					return 16159;
				case CHARACTER:
					return 1373;
				case CORPORATION:
					return 2;
				case FACTION:
					return 500001;
				case SYSTEM:
					return 30000142;
				default:
					return 0;
			}
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
		SPECIALIZEDOREHOLD("SpecializedOreHold", 134),
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
		CORPSEBAY("CrateLoot", 174), //CorpseBay
		BOOSTERBAY("BoosterBay", 176),
		SUBSYSTEMBAY("SubSystemBay", 177),
		;

		private final String value;
		private final int id;

		LocationFlag(String value, int id) {
			this.value = value;
			this.id = id;
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
