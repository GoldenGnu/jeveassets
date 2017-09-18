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

import com.beimin.eveapi.model.shared.KeyType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
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
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterWalletJournalExtraInfoResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;

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
		private final Integer[] integer = {5};
		private final Float[] floats = {5.1f};
		private final Boolean[] booleans = {true};
		private final Long[] longs = {5L};
		private final Double[] doubles = {5.1};
		private final Date[] date = {new Date()};
		private final String[] string = {"StringValue"};
		//Data
		private final MyLocation[] myLocation = {ApiIdConverter.getLocation(60003466)};
		private final PriceData[] priceData = {new PriceData()};
		private final MarketPriceData[] marketPriceData = {new MarketPriceData()};
		private final Tags[] tags = {new Tags()};
		private final Percent[] percent = {new Percent(5.1)};
		private final UserPrice[] userItem = {new UserPrice(getDouble(), getInteger(), getString())};
		//LocationType
		private final RawAsset.LocationType[] rawLocationType = RawAsset.LocationType.values();
		private final CharacterAssetsResponse.LocationTypeEnum[] esiLocationType = CharacterAssetsResponse.LocationTypeEnum.values();
		private final Long[] eveApiLocationType = {60003466L, 30000142L, 10000002L};
		//LocationFlag
		private final ItemFlag[] itemFlag = {
			ApiIdConverter.getFlag(36), //ASSETSAFETY,
			ApiIdConverter.getFlag(RawConverter.LocationFlag.AUTOFIT.getID()), //AUTOFIT
			ApiIdConverter.getFlag(5), //CARGO
			ApiIdConverter.getFlag(RawConverter.LocationFlag.CORPSEBAY.getID()), //CORPSEBAY
			ApiIdConverter.getFlag(173), //DELIVERIES,
			ApiIdConverter.getFlag(87), //DRONEBAY
			ApiIdConverter.getFlag(158), //FIGHTERBAY,
			ApiIdConverter.getFlag(159), //FIGHTERTUBE0,
			ApiIdConverter.getFlag(160), //FIGHTERTUBE1,
			ApiIdConverter.getFlag(161), //FIGHTERTUBE2,
			ApiIdConverter.getFlag(162), //FIGHTERTUBE3,
			ApiIdConverter.getFlag(163), //FIGHTERTUBE4,
			ApiIdConverter.getFlag(155), //FLEETHANGAR
			ApiIdConverter.getFlag(4), //HANGAR,
			ApiIdConverter.getFlag(RawConverter.LocationFlag.HANGARALL.getID()), //HANGARALL,
			ApiIdConverter.getFlag(27), //HISLOT0,
			ApiIdConverter.getFlag(28), //HISLOT1,
			ApiIdConverter.getFlag(29), //HISLOT2,
			ApiIdConverter.getFlag(30), //HISLOT3,
			ApiIdConverter.getFlag(31), //HISLOT4,
			ApiIdConverter.getFlag(32), //HISLOT5,
			ApiIdConverter.getFlag(33), //HISLOT6,
			ApiIdConverter.getFlag(34), //HISLOT7,
			ApiIdConverter.getFlag(156), //HIDDENMODIFIERS,
			ApiIdConverter.getFlag(89), //IMPLANT,
			ApiIdConverter.getFlag(11), //LOSLOT0,
			ApiIdConverter.getFlag(12), //LOSLOT1,
			ApiIdConverter.getFlag(13), //LOSLOT2,
			ApiIdConverter.getFlag(14), //LOSLOT3,
			ApiIdConverter.getFlag(15), //LOSLOT4,
			ApiIdConverter.getFlag(16), //LOSLOT5,
			ApiIdConverter.getFlag(17), //LOSLOT6,
			ApiIdConverter.getFlag(18), //LOSLOT7,
			ApiIdConverter.getFlag(63), //LOCKED,
			ApiIdConverter.getFlag(19), //MEDSLOT0,
			ApiIdConverter.getFlag(20), //MEDSLOT1,
			ApiIdConverter.getFlag(21), //MEDSLOT2,
			ApiIdConverter.getFlag(22), //MEDSLOT3,
			ApiIdConverter.getFlag(23), //MEDSLOT4,
			ApiIdConverter.getFlag(24), //MEDSLOT5,
			ApiIdConverter.getFlag(25), //MEDSLOT6,
			ApiIdConverter.getFlag(26), //MEDSLOT7,
			ApiIdConverter.getFlag(RawConverter.LocationFlag.MODULE.getID()), //MODULE
			ApiIdConverter.getFlag(154), //QUAFEBAY,
			ApiIdConverter.getFlag(92), //RIGSLOT0,
			ApiIdConverter.getFlag(93), //RIGSLOT1,
			ApiIdConverter.getFlag(94), //RIGSLOT2,
			ApiIdConverter.getFlag(95), //RIGSLOT3,
			ApiIdConverter.getFlag(96), //RIGSLOT4,
			ApiIdConverter.getFlag(97), //RIGSLOT5,
			ApiIdConverter.getFlag(98), //RIGSLOT6,
			ApiIdConverter.getFlag(99), //RIGSLOT7,
			ApiIdConverter.getFlag(90), //SHIPHANGAR,
			ApiIdConverter.getFlag(143), //SPECIALIZEDAMMOHOLD,
			ApiIdConverter.getFlag(148), //SPECIALIZEDCOMMANDCENTERHOLD,
			ApiIdConverter.getFlag(133), //SPECIALIZEDFUELBAY,
			ApiIdConverter.getFlag(135), //SPECIALIZEDGASHOLD,
			ApiIdConverter.getFlag(142), //SPECIALIZEDINDUSTRIALSHIPHOLD,
			ApiIdConverter.getFlag(141), //SPECIALIZEDLARGESHIPHOLD,
			ApiIdConverter.getFlag(151), //SPECIALIZEDMATERIALBAY,
			ApiIdConverter.getFlag(140), //SPECIALIZEDMEDIUMSHIPHOLD,
			ApiIdConverter.getFlag(136), //SPECIALIZEDMINERALHOLD,
			ApiIdConverter.getFlag(134), //SPECIALIZEDOREHOLD,
			ApiIdConverter.getFlag(149), //SPECIALIZEDPLANETARYCOMMODITIESHOLD,
			ApiIdConverter.getFlag(137), //SPECIALIZEDSALVAGEHOLD,
			ApiIdConverter.getFlag(138), //SPECIALIZEDSHIPHOLD,
			ApiIdConverter.getFlag(139), //SPECIALIZEDSMALLSHIPHOLD,
			ApiIdConverter.getFlag(177), //SUBSYSTEMBAY
			ApiIdConverter.getFlag(125), //SUBSYSTEMSLOT0,
			ApiIdConverter.getFlag(126), //SUBSYSTEMSLOT1,
			ApiIdConverter.getFlag(127), //SUBSYSTEMSLOT2,
			ApiIdConverter.getFlag(128), //SUBSYSTEMSLOT3,
			ApiIdConverter.getFlag(129), //SUBSYSTEMSLOT4,
			ApiIdConverter.getFlag(130), //SUBSYSTEMSLOT5,
			ApiIdConverter.getFlag(131), //SUBSYSTEMSLOT6,
			ApiIdConverter.getFlag(132), //SUBSYSTEMSLOT7,
			ApiIdConverter.getFlag(64), //UNLOCKED,
			ApiIdConverter.getFlag(3), //WARDROBE
		};
		private final CharacterBlueprintsResponse.LocationFlagEnum[] locationFlagEsiBlueprints = CharacterBlueprintsResponse.LocationFlagEnum.values();
		private final CharacterAssetsResponse.LocationFlagEnum[] locationFlagEsiAssets = CharacterAssetsResponse.LocationFlagEnum.values();
		private final Integer[] locationFlagEveApi = {
			36, //ASSETSAFETY,
			RawConverter.LocationFlag.AUTOFIT.getID(), //AUTOFIT
			5, //CARGO
			RawConverter.LocationFlag.CORPSEBAY.getID(), //CORPSEBAY
			173, //DELIVERIES,
			87, //DRONEBAY
			158, //FIGHTERBAY,
			159, //FIGHTERTUBE0,
			160, //FIGHTERTUBE1,
			161, //FIGHTERTUBE2,
			162, //FIGHTERTUBE3,
			163, //FIGHTERTUBE4,
			155, //FLEETHANGAR
			4, //HANGAR,
			RawConverter.LocationFlag.HANGARALL.getID(), //HANGARALL,
			27, //HISLOT0,
			28, //HISLOT1,
			29, //HISLOT2,
			30, //HISLOT3,
			31, //HISLOT4,
			32, //HISLOT5,
			33, //HISLOT6, ---Index 31
			34, //HISLOT7,
			156, //HIDDENMODIFIERS,
			89, //IMPLANT,
			11, //LOSLOT0,
			12, //LOSLOT1,
			13, //LOSLOT2,
			14, //LOSLOT3,
			15, //LOSLOT4,
			16, //LOSLOT5,
			17, //LOSLOT6,
			18, //LOSLOT7,
			63, //LOCKED,
			19, //MEDSLOT0,
			20, //MEDSLOT1,
			21, //MEDSLOT2,
			22, //MEDSLOT3,
			23, //MEDSLOT4,
			24, //MEDSLOT5,
			25, //MEDSLOT6,
			26, //MEDSLOT7,
			RawConverter.LocationFlag.MODULE.getID(), //MODULE
			154, //QUAFEBAY,
			92, //RIGSLOT0,
			93, //RIGSLOT1,
			94, //RIGSLOT2,
			95, //RIGSLOT3,
			96, //RIGSLOT4,
			97, //RIGSLOT5,
			98, //RIGSLOT6,
			99, //RIGSLOT7,
			90, //SHIPHANGAR,
			143, //SPECIALIZEDAMMOHOLD,
			148, //SPECIALIZEDCOMMANDCENTERHOLD,
			133, //SPECIALIZEDFUELBAY,
			135, //SPECIALIZEDGASHOLD,
			142, //SPECIALIZEDINDUSTRIALSHIPHOLD,
			141, //SPECIALIZEDLARGESHIPHOLD,
			151, //SPECIALIZEDMATERIALBAY,
			140, //SPECIALIZEDMEDIUMSHIPHOLD,
			136, //SPECIALIZEDMINERALHOLD,
			134, //SPECIALIZEDOREHOLD,
			149, //SPECIALIZEDPLANETARYCOMMODITIESHOLD,
			137, //SPECIALIZEDSALVAGEHOLD,
			138, //SPECIALIZEDSHIPHOLD,
			139, //SPECIALIZEDSMALLSHIPHOLD,
			177, //SUBSYSTEMBAY
			125, //SUBSYSTEMSLOT0,
			126, //SUBSYSTEMSLOT1,
			127, //SUBSYSTEMSLOT2,
			128, //SUBSYSTEMSLOT3,
			129, //SUBSYSTEMSLOT4,
			130, //SUBSYSTEMSLOT5,
			131, //SUBSYSTEMSLOT6,
			132, //SUBSYSTEMSLOT7,
			64, //UNLOCKED,
			3, //WARDROBE
		};
		//ContractAvailability
		private final RawContract.ContractAvailability[] rawContractAvailabilitys = RawContract.ContractAvailability.values();
		private final CharacterContractsResponse.AvailabilityEnum[] esiContractsAvailability = CharacterContractsResponse.AvailabilityEnum.values();
		private final com.beimin.eveapi.model.shared.ContractAvailability[] xmlContractAvailability = {
			com.beimin.eveapi.model.shared.ContractAvailability.PUBLIC,
			com.beimin.eveapi.model.shared.ContractAvailability.PRIVATE,
			com.beimin.eveapi.model.shared.ContractAvailability.PRIVATE,
			com.beimin.eveapi.model.shared.ContractAvailability.PRIVATE
		};
		private final String[] eveKitContractAvailability = {
			"public",
			"private",
			"private",
			"private"
		};
		//ContractStatus
		private final RawContract.ContractStatus[] rawContractStatuses = RawContract.ContractStatus.values();
		private final CharacterContractsResponse.StatusEnum[] esiContractStatus = CharacterContractsResponse.StatusEnum.values();
		private final com.beimin.eveapi.model.shared.ContractStatus[] xmlContractStatus = {
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
		private final String[] eveKitContractStatus = {
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
		private final RawContract.ContractType[] rawContractTypes = RawContract.ContractType.values();
		private final CharacterContractsResponse.TypeEnum[] esiContractType = CharacterContractsResponse.TypeEnum.values();
		private final com.beimin.eveapi.model.shared.ContractType[] xmlContractType = {
			null,
			com.beimin.eveapi.model.shared.ContractType.ITEMEXCHANGE,
			com.beimin.eveapi.model.shared.ContractType.AUCTION,
			com.beimin.eveapi.model.shared.ContractType.COURIER,
			com.beimin.eveapi.model.shared.ContractType.LOAN
		};
		private final String[] eveKitContractType = {
			null,
			"itemexchange",
			"auction",
			"courier",
			"loan"
		};
		//IndustryJobStatus
		private final RawIndustryJob.IndustryJobStatus[] rawIndustryJobStatus = RawIndustryJob.IndustryJobStatus.values();
		private final CharacterIndustryJobsResponse.StatusEnum[] esiIndustryJobStatus = CharacterIndustryJobsResponse.StatusEnum.values();
		private final Integer[] eveApiIndustryJobStatus = {1, 2, 3, 101, 102, 103};
		//JournalPartyType
		private final RawJournal.JournalPartyType[] rawJournalPartyType = RawJournal.JournalPartyType.values();
		private final CharacterWalletJournalResponse.FirstPartyTypeEnum[] esiJournalPartyTypeFirst = CharacterWalletJournalResponse.FirstPartyTypeEnum.values();
		private final CharacterWalletJournalResponse.SecondPartyTypeEnum[] esiJournalPartyTypeSecond = CharacterWalletJournalResponse.SecondPartyTypeEnum.values();
		private final Integer[] eveApiJournalPartyType = {1373, 2, 16159, 1};
		//JournalExtraInfo
		private final RawJournalExtraInfo[] rawJournalExtraInfo;
		private final CharacterWalletJournalExtraInfoResponse[] esiJournalExtraInfo;

		//JournalRefType
		private final RawJournalRefType[] rawJournalRefType = {
			RawJournalRefType.PLAYER_TRADING,
			RawJournalRefType.MARKET_TRANSACTION,
			RawJournalRefType.PLAYER_DONATION,
			RawJournalRefType.OFFICE_RENTAL_FEE,
			RawJournalRefType.BOUNTY_PRIZE,
			RawJournalRefType.INSURANCE,
			RawJournalRefType.MISSION_REWARD,
			RawJournalRefType.AGENT_MISSION_TIME_BONUS_REWARD,
			RawJournalRefType.CSPA,
			RawJournalRefType.CORPORATION_ACCOUNT_WITHDRAWAL,
			RawJournalRefType.CORPORATION_LOGO_CHANGE_COST,
			RawJournalRefType.MARKET_ESCROW,
			RawJournalRefType.BROKERS_FEE,
			RawJournalRefType.ALLIANCE_MAINTAINANCE_FEE,
			RawJournalRefType.TRANSACTION_TAX,
			RawJournalRefType.JUMP_CLONE_INSTALLATION_FEE,
			RawJournalRefType.MANUFACTURING,
			RawJournalRefType.CONTRACT_BROKERS_FEE,
			RawJournalRefType.BOUNTY_PRIZES,
			RawJournalRefType.MEDAL_CREATION,
			RawJournalRefType.MEDAL_ISSUED,
			RawJournalRefType.PLANETARY_IMPORT_TAX,
			RawJournalRefType.PLANETARY_EXPORT_TAX,
			RawJournalRefType.CORPORATE_REWARD_PAYOUT,
			RawJournalRefType.INDUSTRY_JOB_TAX,
			RawJournalRefType.PROJECT_DISCOVERY_REWARD,
			RawJournalRefType.REPROCESSING_TAX,
			RawJournalRefType.JUMP_CLONE_ACTIVATION_FEE,
			RawJournalRefType.UNDEFINED
		};
		private final CharacterWalletJournalResponse.RefTypeEnum[] esiJournalRefType = CharacterWalletJournalResponse.RefTypeEnum.values();
		private final com.beimin.eveapi.model.shared.RefType[] xmlJournalRefType = {
			com.beimin.eveapi.model.shared.RefType.PLAYER_TRADING,
			com.beimin.eveapi.model.shared.RefType.MARKET_TRANSACTION,
			com.beimin.eveapi.model.shared.RefType.PLAYER_DONATION,
			com.beimin.eveapi.model.shared.RefType.OFFICE_RENTAL_FEE,
			com.beimin.eveapi.model.shared.RefType.BOUNTY_PRIZE,
			com.beimin.eveapi.model.shared.RefType.INSURANCE,
			com.beimin.eveapi.model.shared.RefType.MISSION_REWARD,
			com.beimin.eveapi.model.shared.RefType.AGENT_MISSION_TIME_BONUS_REWARD,
			com.beimin.eveapi.model.shared.RefType.CSPA,
			com.beimin.eveapi.model.shared.RefType.CORPORATION_ACCOUNT_WITHDRAWAL,
			com.beimin.eveapi.model.shared.RefType.CORPORATION_LOGO_CHANGE_COST,
			com.beimin.eveapi.model.shared.RefType.MARKET_ESCROW,
			com.beimin.eveapi.model.shared.RefType.BROKERS_FEE,
			com.beimin.eveapi.model.shared.RefType.ALLIANCE_MAINTAINANCE_FEE,
			com.beimin.eveapi.model.shared.RefType.TRANSACTION_TAX,
			com.beimin.eveapi.model.shared.RefType.JUMP_CLONE_INSTALLATION_FEE,
			com.beimin.eveapi.model.shared.RefType.MANUFACTURING,
			com.beimin.eveapi.model.shared.RefType.CONTRACT_BROKERS_FEE,
			com.beimin.eveapi.model.shared.RefType.BOUNTY_PRIZES,
			com.beimin.eveapi.model.shared.RefType.MEDAL_CREATION,
			com.beimin.eveapi.model.shared.RefType.MEDAL_ISSUED,
			com.beimin.eveapi.model.shared.RefType.PLANETARY_IMPORT_TAX,
			com.beimin.eveapi.model.shared.RefType.PLANETARY_EXPORT_TAX,
			com.beimin.eveapi.model.shared.RefType.CORPORATE_REWARD_PAYOUT,
			com.beimin.eveapi.model.shared.RefType.INDUSTRY_JOB_TAX,
			com.beimin.eveapi.model.shared.RefType.PROJECT_DISCOVERY_REWARD,
			com.beimin.eveapi.model.shared.RefType.REPROCESSING_TAX,
			com.beimin.eveapi.model.shared.RefType.JUMP_CLONE_ACTIVATION_FEE,
			com.beimin.eveapi.model.shared.RefType.UNDEFINED
		};
		//MarketOrderRange
		private final RawMarketOrder.MarketOrderRange[] rawMarketOrderRange = RawMarketOrder.MarketOrderRange.values();
		private final CharacterOrdersResponse.RangeEnum[] esiMarketOrderRange = CharacterOrdersResponse.RangeEnum.values();
		private final Integer[] eveApiMarketOrderRange = {-1, 32767, 0, 1, 2, 3, 4, 5, 10, 20, 30, 40};
		//MarketOrderState
		private final RawMarketOrder.MarketOrderState[] rawMarketOrderState = RawMarketOrder.MarketOrderState.values();
		private final CharacterOrdersResponse.StateEnum[] esiMarketOrderState = CharacterOrdersResponse.StateEnum.values();
		private final Integer[] eveApiMarketOrderState = {0, 1, 2, 3, 4, 5};
		//Owners
		private final EsiCallbackURL[] esiCallbackURLs = EsiCallbackURL.values();
		private final KeyType[] keyTypes = {KeyType.CORPORATION};

		//Controls
		private final int index;
		private final int max;

		public IndexOptions(int index) {
			this.index = index;
			int tempMax = 0;
			rawJournalExtraInfo = new RawJournalExtraInfo[1];
			rawJournalExtraInfo[0] = new RawJournalExtraInfo(getLong(), getLong().toString(), get(rawJournalRefType, index));
			esiJournalExtraInfo = new CharacterWalletJournalExtraInfoResponse[1];
			CharacterWalletJournalExtraInfoResponse journalExtraInfoResponse = new CharacterWalletJournalExtraInfoResponse();
			RawJournalRefType refType = get(rawJournalRefType, index);
			if (refType.getArgName() != null) {
				switch (refType.getArgName()) {
					case CONTRACT_ID:
						journalExtraInfoResponse.setContractId(getLong().intValue());
						break;
					case DESTROYED_SHIP_TYPE_ID:
						journalExtraInfoResponse.setDestroyedShipTypeId(getLong().intValue());
						break;
					case JOB_ID:
						journalExtraInfoResponse.setJobId(getLong().intValue());
						break;
					case NPC_NAME:
						journalExtraInfoResponse.setNpcName(String.valueOf(getLong()));
						break;
					case PLAYER_NAME:
						break;
					case STATION_NAME:
						break;
					case TRANSACTION_ID:
						journalExtraInfoResponse.setTransactionId(getLong());
						break;
					case CORPORATION_NAME:
						break;
					case ALLIANCE_NAME:
						break;
					case PLANET_NAME:
						break;
				}
			}
			if (refType.getArgID() != null) {
				switch (refType.getArgID()) {
					case NPC_ID:
						journalExtraInfoResponse.setNpcId(getLong().intValue());
						break;
					case PLAYER_ID:
						journalExtraInfoResponse.setCharacterId(getLong().intValue());
						break;
					case STATION_ID:
						journalExtraInfoResponse.setLocationId(getLong());
						break;
					case SYSTEM_ID:
						journalExtraInfoResponse.setSystemId(getLong().intValue());
						break;
					case CORPORATION_ID:
						journalExtraInfoResponse.setCorporationId(getLong().intValue());
						break;
					case ALLIANCE_ID:
						journalExtraInfoResponse.setAllianceId(getLong().intValue());
						break;
					case PLANET_ID:
						journalExtraInfoResponse.setPlanetId(getLong().intValue());
						break;
				}
			}
			//TODO set correct values
			esiJournalExtraInfo[0] = journalExtraInfoResponse;
			tempMax = Math.max(tempMax, integer.length);
			tempMax = Math.max(tempMax, floats.length);
			tempMax = Math.max(tempMax, booleans.length);
			tempMax = Math.max(tempMax, longs.length);
			tempMax = Math.max(tempMax, doubles.length);
			tempMax = Math.max(tempMax, date.length);
			tempMax = Math.max(tempMax, string.length);

			tempMax = Math.max(tempMax, myLocation.length);
			tempMax = Math.max(tempMax, priceData.length);
			tempMax = Math.max(tempMax, marketPriceData.length);
			tempMax = Math.max(tempMax, tags.length);
			tempMax = Math.max(tempMax, percent.length);
			tempMax = Math.max(tempMax, userItem.length);
			//LocationType
			tempMax = Math.max(tempMax, rawLocationType.length);
			tempMax = Math.max(tempMax, esiLocationType.length);
			tempMax = Math.max(tempMax, eveApiLocationType.length);
			//LocationFlag
			tempMax = Math.max(tempMax, itemFlag.length);
			tempMax = Math.max(tempMax, locationFlagEsiBlueprints.length);
			tempMax = Math.max(tempMax, locationFlagEsiAssets.length);
			tempMax = Math.max(tempMax, locationFlagEveApi.length);
			//ContractAvailability
			tempMax = Math.max(tempMax, rawContractAvailabilitys.length);
			tempMax = Math.max(tempMax, esiContractsAvailability.length);
			tempMax = Math.max(tempMax, xmlContractAvailability.length);
			tempMax = Math.max(tempMax, eveKitContractAvailability.length);
			//ContractStatus
			tempMax = Math.max(tempMax, rawContractStatuses.length);
			tempMax = Math.max(tempMax, esiContractStatus.length);
			tempMax = Math.max(tempMax, xmlContractStatus.length);
			tempMax = Math.max(tempMax, eveKitContractStatus.length);
			//ContractType
			tempMax = Math.max(tempMax, rawContractTypes.length);
			tempMax = Math.max(tempMax, esiContractType.length);
			tempMax = Math.max(tempMax, xmlContractType.length);
			tempMax = Math.max(tempMax, eveKitContractType.length);
			//IndustryJobStatus
			tempMax = Math.max(tempMax, rawIndustryJobStatus.length);
			tempMax = Math.max(tempMax, esiIndustryJobStatus.length);
			tempMax = Math.max(tempMax, eveApiIndustryJobStatus.length);
			//JournalExtraInfo
			tempMax = Math.max(tempMax, rawJournalExtraInfo.length);
			tempMax = Math.max(tempMax, esiJournalExtraInfo.length);
			//JournalPartyType
			tempMax = Math.max(tempMax, rawJournalPartyType.length);
			tempMax = Math.max(tempMax, esiJournalPartyTypeFirst.length);
			tempMax = Math.max(tempMax, esiJournalPartyTypeSecond.length);
			tempMax = Math.max(tempMax, eveApiJournalPartyType.length);
			//JournalRefType
			tempMax = Math.max(tempMax, rawJournalRefType.length);
			tempMax = Math.max(tempMax, esiJournalRefType.length);
			tempMax = Math.max(tempMax, xmlJournalRefType.length);
			//MarketOrderRange
			tempMax = Math.max(tempMax, rawMarketOrderRange.length);
			tempMax = Math.max(tempMax, esiMarketOrderRange.length);
			tempMax = Math.max(tempMax, eveApiMarketOrderRange.length);
			//MarketOrderState
			tempMax = Math.max(tempMax, rawMarketOrderState.length);
			tempMax = Math.max(tempMax, esiMarketOrderState.length);
			tempMax = Math.max(tempMax, eveApiMarketOrderState.length);
			//Owners
			tempMax = Math.max(tempMax, esiCallbackURLs.length);
			tempMax = Math.max(tempMax, keyTypes.length);
			this.max = tempMax;
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

		public boolean isMaxed() {
			return index >= max;
		}

		@Override
		public Integer getInteger() {
			return get(integer, index);
		}

		@Override
		public Float getFloat() {
			return get(floats, index);
		}

		@Override
		public Boolean getBoolean() {
			return get(booleans, index);
		}

		@Override
		public final Long getLong() {
			return get(longs, index);
		}

		@Override
		public Double getDouble() {
			return get(doubles, index);
		}

		@Override
		public Date getDate() {
			return get(date, index);
		}

		@Override
		public String getString() {
			return get(string, index);
		}

		@Override
		public MyLocation getMyLocation() {
			return get(myLocation, index);
		}

		@Override
		public PriceData getPriceData() {
			return get(priceData, index);
		}

		@Override
		public UserItem<Integer, Double> getUserPrice() {
			return get(userItem, index);
		}

		@Override
		public MarketPriceData getMarketPriceData() {
			return get(marketPriceData, index);
		}

		@Override
		public Tags getTags() {
			return get(tags, index);
		}

		@Override
		public RawBlueprint getRawBlueprint() {
			return ConverterTestUtil.getRawBlueprint(this);
		}

		@Override
		public Percent getPercent() {
			return get(percent, index);
		}

//LocationType
		@Override
		public Long getLocationTypeEveApi() {
			return get(eveApiLocationType, index);
		}

		@Override
		public RawAsset.LocationType getLocationTypeRaw() {
			return get(rawLocationType, index);
		}

		@Override
		public CharacterAssetsResponse.LocationTypeEnum getLocationTypeEsi() {
			return get(esiLocationType, index);
		}

//LocationFlag
		@Override
		public CharacterBlueprintsResponse.LocationFlagEnum getLocationFlagEsiBlueprint() {
			return get(locationFlagEsiBlueprints, index);
		}

		@Override
		public CharacterAssetsResponse.LocationFlagEnum getLocationFlagEsiAssets() {
			return get(locationFlagEsiAssets, index);
		}

		@Override
		public int getLocationFlagEveApi() {
			return get(locationFlagEveApi, index);
		}

		@Override
		public ItemFlag getItemFlag() {
			return get(itemFlag, index);
		}

//ContractAvailability
		@Override
		public RawContract.ContractAvailability getContractAvailabilityRaw() {
			return get(rawContractAvailabilitys, index);
		}

		@Override
		public CharacterContractsResponse.AvailabilityEnum getContractAvailabilityEsi() {
			return get(esiContractsAvailability, index);
		}

		@Override
		public com.beimin.eveapi.model.shared.ContractAvailability getContractAvailabilityEveApi() {
			return get(xmlContractAvailability, index);
		}

		@Override
		public String getContractAvailabilityEveKit() {
			return get(eveKitContractAvailability, index);
		}

//ContractStatus
		@Override
		public RawContract.ContractStatus getContractStatusRaw() {
			return get(rawContractStatuses, index);
		}

		@Override
		public CharacterContractsResponse.StatusEnum getContractStatusEsi() {
			return get(esiContractStatus, index);
		}

		@Override
		public com.beimin.eveapi.model.shared.ContractStatus getContractStatusEveApi() {
			return get(xmlContractStatus, index);
		}

		@Override
		public String getContractStatusEveKit() {
			return get(eveKitContractStatus, index);
		}

//ContractType
		@Override
		public RawContract.ContractType getContractTypeRaw() {
			return get(rawContractTypes, index);
		}

		@Override
		public com.beimin.eveapi.model.shared.ContractType getContractTypeEveApi() {
			return get(xmlContractType, index);
		}

		@Override
		public CharacterContractsResponse.TypeEnum getContractTypeEsi() {
			return get(esiContractType, index);
		}

		@Override
		public String getContractTypeEveKit() {
			return get(eveKitContractType, index);
		}

//IndustryJobStatus
		@Override
		public RawIndustryJob.IndustryJobStatus getIndustryJobStatusRaw() {
			return get(rawIndustryJobStatus, index);
		}

		@Override
		public CharacterIndustryJobsResponse.StatusEnum getIndustryJobStatusEsi() {
			return get(esiIndustryJobStatus, index);
		}

		@Override
		public int getIndustryJobStatusEveApi() {
			return get(eveApiIndustryJobStatus, index);
		}

//JournalExtraInfo
		@Override
		public RawJournalExtraInfo getJournalExtraInfoRaw() {
			return get(rawJournalExtraInfo, index);
		}

		@Override
		public CharacterWalletJournalExtraInfoResponse getJournalExtraInfoEsi() {
			return get(esiJournalExtraInfo, index);
		}

//JournalPartyType
		@Override
		public RawJournal.JournalPartyType getJournalPartyTypeRaw() {
			return get(rawJournalPartyType, index);
		}

		@Override
		public CharacterWalletJournalResponse.FirstPartyTypeEnum getJournalPartyTypeEsiFirst() {
			return get(esiJournalPartyTypeFirst, index);
		}

		@Override
		public CharacterWalletJournalResponse.SecondPartyTypeEnum getJournalPartyTypeEsiSecond() {
			return get(esiJournalPartyTypeSecond, index);
		}

		@Override
		public int getJournalPartyTypeEveApi() {
			return get(eveApiJournalPartyType, index);
		}

//JournalRefType
		@Override
		public RawJournalRefType getJournalRefTypeRaw() {
			return get(rawJournalRefType, index);
		}

		@Override
		public CharacterWalletJournalResponse.RefTypeEnum getJournalRefTypeEsi() {
			return get(esiJournalRefType, index);
		}

		@Override
		public com.beimin.eveapi.model.shared.RefType getJournalRefTypeEveApi() {
			return get(xmlJournalRefType, index);
		}

//MarketOrderRange
		@Override
		public RawMarketOrder.MarketOrderRange getMarketOrderRangeRaw() {
			return get(rawMarketOrderRange, index);
		}

		@Override
		public CharacterOrdersResponse.RangeEnum getMarketOrderRangeEsi() {
			return get(esiMarketOrderRange, index);
		}

		@Override
		public int getMarketOrderRangeEveApi() {
			return get(eveApiMarketOrderRange, index);
		}

//MarketOrderState
		@Override
		public RawMarketOrder.MarketOrderState getMarketOrderStateRaw() {
			return get(rawMarketOrderState, index);
		}

		@Override
		public CharacterOrdersResponse.StateEnum getMarketOrderStateEsi() {
			return get(esiMarketOrderState, index);
		}

		@Override
		public int getMarketOrderStateEveApi() {
			return get(eveApiMarketOrderState, index);
		}

		@Override
		public EsiCallbackURL getEsiCallbackURL() {
			return get(esiCallbackURLs, index);
		}

		@Override
		public KeyType getKeyType() {
			return get(keyTypes, index);
		}

		@Override
		public EveApiAccount getEveApiAccount() {
			return ConverterTestUtil.getEveApiAccount(this);
		}
	}
}
