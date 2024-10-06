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


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.io.shared.RawConverter.LocationFlag;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CorporationContainersLogsResponse;
import net.troja.eve.esi.model.CorporationContractsResponse;
import net.troja.eve.esi.model.CorporationIndustryJobsResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;
import net.troja.eve.esi.model.MarketOrdersResponse;
import net.troja.eve.esi.model.MarketStructuresResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class RawConverterTest extends TestUtil {

	@Test
	public void testToDate() {
		Date from = new Date();
		OffsetDateTime offsetDateTime = from.toInstant().atOffset(ZoneOffset.UTC);
		Date to = RawConverter.toDate(offsetDateTime);
		assertEquals(from, to);
	}

	@Test
	public void testToFlag_CharacterAssetsResponseLocationFlagEnum() {
		for (net.troja.eve.esi.model.CharacterAssetsResponse.LocationFlagEnum locationFlagEnum : net.troja.eve.esi.model.CharacterAssetsResponse.LocationFlagEnum.values()) {
			ItemFlag itemFlag = RawConverter.toFlag(locationFlagEnum);
			assertNotNull(itemFlag);
			assertTrue(locationFlagEnum.name() + " (" + locationFlagEnum.toString() + ") != " + itemFlag.getFlagName(),
					itemFlag.getFlagID() == 0
					|| locationFlagEnum.toString().toLowerCase().equals(itemFlag.getFlagText().toLowerCase().replace(" ", ""))
					|| locationFlagEnum.toString().toLowerCase().equals(itemFlag.getFlagName().toLowerCase())
					|| (locationFlagEnum.toString().equals("CorpseBay") && itemFlag.getFlagName().equals("CrateLoot"))
					|| (locationFlagEnum.toString().equals("Module") && itemFlag.getFlagName().equals("Skill"))
					|| (locationFlagEnum.toString().equals("SpecializedOreHold") && itemFlag.getFlagName().equals("SpecializedAsteroidHold"))
					|| (locationFlagEnum.toString().equals("CorporationGoalDeliveries") && itemFlag.getFlagName().equals("CorpProjectsHangar"))
					|| (locationFlagEnum.toString().equals("MobileDepotHold") && itemFlag.getFlagName().equals("MobileDepot"))
					|| (locationFlagEnum.toString().equals("InfrastructureHangar") && itemFlag.getFlagName().equals("ColonyResourcesHold"))
			);

		}
	}

	@Test
	public void testToFlag_CorporationAssetsResponseLocationFlagEnum() {
		for (net.troja.eve.esi.model.CorporationAssetsResponse.LocationFlagEnum locationFlagEnum : net.troja.eve.esi.model.CorporationAssetsResponse.LocationFlagEnum.values()) {
			ItemFlag itemFlag = RawConverter.toFlag(locationFlagEnum);
			assertNotNull(itemFlag);
			assertTrue(locationFlagEnum.name() + " (" + locationFlagEnum.toString() + ") != " + itemFlag.getFlagName(),
					itemFlag.getFlagID() == 0
					|| locationFlagEnum.toString().toLowerCase().equals(itemFlag.getFlagText().toLowerCase().replace(" ", ""))
					|| locationFlagEnum.toString().toLowerCase().equals(itemFlag.getFlagName().toLowerCase())
					|| (locationFlagEnum.toString().equals("CorpDeliveries") && itemFlag.getFlagName().equals("CorpMarket"))
					|| (locationFlagEnum.toString().equals("Impounded") && itemFlag.getFlagName().equals("OfficeImpound"))
					|| (locationFlagEnum.toString().equals("ServiceSlot0") && itemFlag.getFlagName().equals("StructureServiceSlot0"))
					|| (locationFlagEnum.toString().equals("ServiceSlot1") && itemFlag.getFlagName().equals("StructureServiceSlot1"))
					|| (locationFlagEnum.toString().equals("ServiceSlot2") && itemFlag.getFlagName().equals("StructureServiceSlot2"))
					|| (locationFlagEnum.toString().equals("ServiceSlot3") && itemFlag.getFlagName().equals("StructureServiceSlot3"))
					|| (locationFlagEnum.toString().equals("ServiceSlot4") && itemFlag.getFlagName().equals("StructureServiceSlot4"))
					|| (locationFlagEnum.toString().equals("ServiceSlot5") && itemFlag.getFlagName().equals("StructureServiceSlot5"))
					|| (locationFlagEnum.toString().equals("ServiceSlot6") && itemFlag.getFlagName().equals("StructureServiceSlot6"))
					|| (locationFlagEnum.toString().equals("ServiceSlot7") && itemFlag.getFlagName().equals("StructureServiceSlot7"))
					|| (locationFlagEnum.toString().equals("DustBattle") && itemFlag.getFlagName().equals("DustCharacterBattle"))
					|| (locationFlagEnum.toString().equals("DustDatabank") && itemFlag.getFlagName().equals("DustCharacterDatabank"))
					|| (locationFlagEnum.toString().equals("HiddenModifers") && itemFlag.getFlagName().equals("HiddenModifiers"))
					|| (locationFlagEnum.toString().equals("QuantumCoreRoom") && itemFlag.getFlagName().equals("StructureDeedBay"))
					|| (locationFlagEnum.toString().equals("SpecializedOreHold") && itemFlag.getFlagName().equals("SpecializedAsteroidHold"))
					|| (locationFlagEnum.toString().equals("CorporationGoalDeliveries") && itemFlag.getFlagName().equals("CorpProjectsHangar"))
					|| (locationFlagEnum.toString().equals("MobileDepotHold") && itemFlag.getFlagName().equals("MobileDepot"))
					|| (locationFlagEnum.toString().equals("InfrastructureHangar") && itemFlag.getFlagName().equals("ColonyResourcesHold"))
			);
		}
	}

	@Test
	public void testToFlag_CharacterBlueprintsResponseLocationFlagEnum() {
		for (net.troja.eve.esi.model.CharacterBlueprintsResponse.LocationFlagEnum locationFlagEnum : net.troja.eve.esi.model.CharacterBlueprintsResponse.LocationFlagEnum.values()) {
			ItemFlag itemFlag = RawConverter.toFlag(locationFlagEnum);
			assertNotNull(itemFlag);
			assertTrue(locationFlagEnum.name() + " (" + locationFlagEnum.toString() + ") != " + itemFlag.getFlagName(),
					itemFlag.getFlagID() == 0
					|| locationFlagEnum.toString().toLowerCase().equals(itemFlag.getFlagText().toLowerCase().replace(" ", ""))
					|| locationFlagEnum.toString().toLowerCase().equals(itemFlag.getFlagName().toLowerCase())
					|| (locationFlagEnum.toString().equals("CorpseBay") && itemFlag.getFlagName().equals("CrateLoot"))
					|| (locationFlagEnum.toString().equals("Module") && itemFlag.getFlagName().equals("Skill"))
					|| (locationFlagEnum.toString().equals("SpecializedOreHold") && itemFlag.getFlagName().equals("SpecializedAsteroidHold"))
			);
		}
	}

	@Test
	public void testToFlag_CorporationBlueprintsResponseLocationFlagEnum() {
		for (net.troja.eve.esi.model.CorporationBlueprintsResponse.LocationFlagEnum locationFlagEnum : net.troja.eve.esi.model.CorporationBlueprintsResponse.LocationFlagEnum.values()) {
			ItemFlag itemFlag = RawConverter.toFlag(locationFlagEnum);
			assertNotNull(itemFlag);
			assertTrue(locationFlagEnum.name() + " (" + locationFlagEnum.toString() + ") != " + itemFlag.getFlagName(),
					itemFlag.getFlagID() == 0
					|| locationFlagEnum.toString().toLowerCase().equals(itemFlag.getFlagText().toLowerCase().replace(" ", ""))
					|| locationFlagEnum.toString().toLowerCase().equals(itemFlag.getFlagName().toLowerCase())
					|| (locationFlagEnum.toString().equals("CorpDeliveries") && itemFlag.getFlagName().equals("CorpMarket"))
					|| (locationFlagEnum.toString().equals("Impounded") && itemFlag.getFlagName().equals("OfficeImpound"))
					|| (locationFlagEnum.toString().equals("ServiceSlot0") && itemFlag.getFlagName().equals("StructureServiceSlot0"))
					|| (locationFlagEnum.toString().equals("ServiceSlot1") && itemFlag.getFlagName().equals("StructureServiceSlot1"))
					|| (locationFlagEnum.toString().equals("ServiceSlot2") && itemFlag.getFlagName().equals("StructureServiceSlot2"))
					|| (locationFlagEnum.toString().equals("ServiceSlot3") && itemFlag.getFlagName().equals("StructureServiceSlot3"))
					|| (locationFlagEnum.toString().equals("ServiceSlot4") && itemFlag.getFlagName().equals("StructureServiceSlot4"))
					|| (locationFlagEnum.toString().equals("ServiceSlot5") && itemFlag.getFlagName().equals("StructureServiceSlot5"))
					|| (locationFlagEnum.toString().equals("ServiceSlot6") && itemFlag.getFlagName().equals("StructureServiceSlot6"))
					|| (locationFlagEnum.toString().equals("ServiceSlot7") && itemFlag.getFlagName().equals("StructureServiceSlot7"))
					|| (locationFlagEnum.toString().equals("DustBattle") && itemFlag.getFlagName().equals("DustCharacterBattle"))
					|| (locationFlagEnum.toString().equals("DustDatabank") && itemFlag.getFlagName().equals("DustCharacterDatabank"))
					|| (locationFlagEnum.toString().equals("HiddenModifers") && itemFlag.getFlagName().equals("HiddenModifiers"))
					|| (locationFlagEnum.toString().equals("QuantumCoreRoom") && itemFlag.getFlagName().equals("StructureDeedBay"))
					|| (locationFlagEnum.toString().equals("SpecializedOreHold") && itemFlag.getFlagName().equals("SpecializedAsteroidHold"))
			);
		}
	}

	@Test
	public void testToFlag_CorporationContainersLogsResponseLocationFlagEnum() {
		for (net.troja.eve.esi.model.CorporationContainersLogsResponse.LocationFlagEnum locationFlagEnum : net.troja.eve.esi.model.CorporationContainersLogsResponse.LocationFlagEnum.values()) {
			ItemFlag itemFlag = RawConverter.toFlag(locationFlagEnum);
			assertNotNull(itemFlag);
			assertTrue(locationFlagEnum.name() + " (" + locationFlagEnum.toString() + ") != " + itemFlag.getFlagName(),
					itemFlag.getFlagID() == 0
					|| locationFlagEnum.toString().toLowerCase().equals(itemFlag.getFlagText().toLowerCase().replace(" ", ""))
					|| locationFlagEnum.toString().toLowerCase().equals(itemFlag.getFlagName().toLowerCase())
					|| (locationFlagEnum.toString().equals("CorpDeliveries") && itemFlag.getFlagName().equals("CorpMarket"))
					|| (locationFlagEnum.toString().equals("Impounded") && itemFlag.getFlagName().equals("OfficeImpound"))
					|| (locationFlagEnum.toString().equals("ServiceSlot0") && itemFlag.getFlagName().equals("StructureServiceSlot0"))
					|| (locationFlagEnum.toString().equals("ServiceSlot1") && itemFlag.getFlagName().equals("StructureServiceSlot1"))
					|| (locationFlagEnum.toString().equals("ServiceSlot2") && itemFlag.getFlagName().equals("StructureServiceSlot2"))
					|| (locationFlagEnum.toString().equals("ServiceSlot3") && itemFlag.getFlagName().equals("StructureServiceSlot3"))
					|| (locationFlagEnum.toString().equals("ServiceSlot4") && itemFlag.getFlagName().equals("StructureServiceSlot4"))
					|| (locationFlagEnum.toString().equals("ServiceSlot5") && itemFlag.getFlagName().equals("StructureServiceSlot5"))
					|| (locationFlagEnum.toString().equals("ServiceSlot6") && itemFlag.getFlagName().equals("StructureServiceSlot6"))
					|| (locationFlagEnum.toString().equals("ServiceSlot7") && itemFlag.getFlagName().equals("StructureServiceSlot7"))
					|| (locationFlagEnum.toString().equals("DustBattle") && itemFlag.getFlagName().equals("DustCharacterBattle"))
					|| (locationFlagEnum.toString().equals("DustDatabank") && itemFlag.getFlagName().equals("DustCharacterDatabank"))
					|| (locationFlagEnum.toString().equals("HiddenModifers") && itemFlag.getFlagName().equals("HiddenModifiers"))
					|| (locationFlagEnum.toString().equals("QuantumCoreRoom") && itemFlag.getFlagName().equals("StructureDeedBay"))
					|| (locationFlagEnum.toString().equals("SpecializedOreHold") && itemFlag.getFlagName().equals("SpecializedAsteroidHold"))
			);
		}
	}

	@Test
	public void testToContractAvailability_String_String() {
		//Enum
		for (RawContract.ContractAvailability value : RawContract.ContractAvailability.values()) {
			assertEquals(value, RawConverter.toContractAvailability(value.name(), null));
		}
		//String
		for (RawContract.ContractAvailability value : RawContract.ContractAvailability.values()) {
			assertEquals(value, RawConverter.toContractAvailability(null, value.getValue()));
		}
		//EveAPI
		Map<String, RawContract.ContractAvailability> map = new HashMap<>();
		map.put("public", RawContract.ContractAvailability.PUBLIC);
		map.put("private", RawContract.ContractAvailability.PERSONAL);
		for (Map.Entry<String, RawContract.ContractAvailability> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContractAvailability(entry.getKey(), null));
		}
	}

	@Test
	public void testToContractAvailability_CharacterContractsResponseAvailabilityEnum() {
		//Enum
		for (CharacterContractsResponse.AvailabilityEnum value : CharacterContractsResponse.AvailabilityEnum.values()) {
			assertEquals(value.name(), RawConverter.toContractAvailability(value).name());
		}
	}

	@Test
	public void testToContractAvailability_CorporationContractsResponseAvailabilityEnum() {
		//Enum
		for (CorporationContractsResponse.AvailabilityEnum value : CorporationContractsResponse.AvailabilityEnum.values()) {
			assertEquals(value.name(), RawConverter.toContractAvailability(value).name());
		}
	}

	@Test
	public void testToContractStatus_String_String() {
		//Enum
		for (RawContract.ContractStatus value : RawContract.ContractStatus.values()) {
			assertEquals(value, RawConverter.toContractStatus(value.name(), null));
		}
		//String
		for (RawContract.ContractStatus value : RawContract.ContractStatus.values()) {
			assertEquals(value, RawConverter.toContractStatus(null, value.getValue()));
		}
		//EveAPI
		Map<String, RawContract.ContractStatus> map = new HashMap<>();
		map.put("COMPLETED", RawContract.ContractStatus.FINISHED);
		map.put("COMPLETEDBYCONTRACTOR", RawContract.ContractStatus.FINISHED_CONTRACTOR);
		map.put("COMPLETEDBYISSUER", RawContract.ContractStatus.FINISHED_ISSUER);
		map.put("INPROGRESS", RawContract.ContractStatus.IN_PROGRESS);
		for (Map.Entry<String, RawContract.ContractStatus> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContractStatus(entry.getKey(), null));
		}
	}

	@Test
	public void testToContractStatus_CharacterContractsResponseStatusEnum() {
		//Enum
		for (CharacterContractsResponse.StatusEnum value : CharacterContractsResponse.StatusEnum.values()) {
			assertEquals(value.name(), RawConverter.toContractStatus(value).name());
		}
	}

	@Test
	public void testToContractStatus_CorporationContractsResponseStatusEnum() {
		//Enum
		for (CorporationContractsResponse.StatusEnum value : CorporationContractsResponse.StatusEnum.values()) {
			assertEquals(value.name(), RawConverter.toContractStatus(value).name());
		}
	}

	@Test
	public void testToContractType_String_String() {
		//Enum
		for (RawContract.ContractType value : RawContract.ContractType.values()) {
			assertEquals(value, RawConverter.toContractType(value.name(), null));
		}
		//String
		for (RawContract.ContractType value : RawContract.ContractType.values()) {
			assertEquals(value, RawConverter.toContractType(null, value.getValue()));
		}
		//EveAPI
		Map<String, RawContract.ContractType> map = new HashMap<>();
		map.put("Auction", RawContract.ContractType.AUCTION);
		map.put("Courier", RawContract.ContractType.COURIER);
		map.put("ItemExchange", RawContract.ContractType.ITEM_EXCHANGE);
		map.put("Loan", RawContract.ContractType.LOAN);
		for (Map.Entry<String, RawContract.ContractType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContractType(entry.getKey(), null));
		}
	}

	@Test
	public void testToContractType_CharacterContractsResponseTypeEnum() {
		//Enum
		for (CharacterContractsResponse.TypeEnum value : CharacterContractsResponse.TypeEnum.values()) {
			assertEquals(value.name(), RawConverter.toContractType(value).name());
		}
	}

	@Test
	public void testToContractType_CorporationContractsResponseTypeEnum() {
		//Enum
		for (CorporationContractsResponse.TypeEnum value : CorporationContractsResponse.TypeEnum.values()) {
			assertEquals(value.name(), RawConverter.toContractType(value).name());
		}
	}

	@Test
	public void testToIndustryJobStatus_3args() {
		//Enum
		for (RawIndustryJob.IndustryJobStatus value : RawIndustryJob.IndustryJobStatus.values()) {
			assertEquals(value, RawConverter.toIndustryJobStatus(null, value.name(), null));
		}
		//String
		for (RawIndustryJob.IndustryJobStatus value : RawIndustryJob.IndustryJobStatus.values()) {
			assertEquals(value, RawConverter.toIndustryJobStatus(null, null, value.getValue()));
		}
		//EveAPI
		Map<Integer, RawIndustryJob.IndustryJobStatus> map = new HashMap<>();
		map.put(1, RawIndustryJob.IndustryJobStatus.ACTIVE);
		map.put(2, RawIndustryJob.IndustryJobStatus.PAUSED);
		map.put(3, RawIndustryJob.IndustryJobStatus.READY);
		map.put(101, RawIndustryJob.IndustryJobStatus.DELIVERED);
		map.put(102, RawIndustryJob.IndustryJobStatus.CANCELLED);
		map.put(103, RawIndustryJob.IndustryJobStatus.REVERTED);
		map.put(-100, RawIndustryJob.IndustryJobStatus.ARCHIVED);
		assertEquals(map.size(), RawIndustryJob.IndustryJobStatus.values().length);
		for (Map.Entry<Integer, RawIndustryJob.IndustryJobStatus> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toIndustryJobStatus(entry.getKey(), null, null));
		}
	}

	@Test
	public void testToIndustryJobStatus_CharacterIndustryJobsResponseStatusEnum() {
		//Enum
		for (CharacterIndustryJobsResponse.StatusEnum value : CharacterIndustryJobsResponse.StatusEnum.values()) {
			assertEquals(value.name(), RawConverter.toIndustryJobStatus(value).name());
		}
	}

	@Test
	public void testToIndustryJobStatus_CorporationIndustryJobsResponseStatusEnum() {
		//Enum
		for (CorporationIndustryJobsResponse.StatusEnum value : CorporationIndustryJobsResponse.StatusEnum.values()) {
			assertEquals(value.name(), RawConverter.toIndustryJobStatus(value).name());
		}
	}

	@Test
	public void testToJournalRefType_Integer_String() {
		for (RawJournalRefType refType : RawJournalRefType.values()) {
			RawJournalRefType type = RawConverter.toJournalRefType(refType.getID(), null);
			assertEquals(type.name(), refType.name());
		}
		for (CharacterWalletJournalResponse.RefTypeEnum refType : CharacterWalletJournalResponse.RefTypeEnum.values()) {
			RawJournalRefType type = RawConverter.toJournalRefType(null, refType.toString());
			if (type == RawJournalRefType.KILL_RIGHT && refType == CharacterWalletJournalResponse.RefTypeEnum.KILL_RIGHT_FEE) {
				continue;
			}
			if (type == RawJournalRefType.RESOURCE_WARS_SITE_COMPLETION && refType == CharacterWalletJournalResponse.RefTypeEnum.RESOURCE_WARS_REWARD) {
				continue;
			}
			if (type == RawJournalRefType.REACTIONS && refType == CharacterWalletJournalResponse.RefTypeEnum.REACTION) {
				continue;
			}
			assertEquals(type.name(), refType.name());
		}
		for (CorporationWalletJournalResponse.RefTypeEnum refType : CorporationWalletJournalResponse.RefTypeEnum.values()) {
			RawJournalRefType type = RawConverter.toJournalRefType(null, refType.toString());
			if (type == RawJournalRefType.KILL_RIGHT && refType == CorporationWalletJournalResponse.RefTypeEnum.KILL_RIGHT_FEE) {
				continue;
			}
			if (type == RawJournalRefType.RESOURCE_WARS_SITE_COMPLETION && refType == CorporationWalletJournalResponse.RefTypeEnum.RESOURCE_WARS_REWARD) {
				continue;
			}
			if (type == RawJournalRefType.REACTIONS && refType == CorporationWalletJournalResponse.RefTypeEnum.REACTION) {
				continue;
			}
			assertEquals(type.name(), refType.name());
		}
	}

	@Test
	public void testToJournalRefType_CharacterWalletJournalResponseRefTypeEnum() {
		assertEquals(146, CharacterWalletJournalResponse.RefTypeEnum.values().length);
		int undefined = 0;
		for (CharacterWalletJournalResponse.RefTypeEnum refType : CharacterWalletJournalResponse.RefTypeEnum.values()) {
			RawJournalRefType rawJournalRefType = RawConverter.toJournalRefType(refType);
			assertNotNull("No value for: " + refType.name(), rawJournalRefType);
			if (rawJournalRefType == RawJournalRefType.UNDEFINED) {
				undefined++;
			}
		}
		assertEquals(0, undefined);
	}

	@Test
	public void testToJournalRefType_CorporationWalletJournalResponseRefTypeEnum() {
		assertEquals(146, CorporationWalletJournalResponse.RefTypeEnum.values().length);
		int undefined = 0;
		for (CorporationWalletJournalResponse.RefTypeEnum refType : CorporationWalletJournalResponse.RefTypeEnum.values()) {
			RawJournalRefType rawJournalRefType = RawConverter.toJournalRefType(refType);
			assertNotNull("No value for: " + refType.name(), rawJournalRefType);
			if (rawJournalRefType == RawJournalRefType.UNDEFINED) {
				undefined++;
			}
		}
		assertEquals(0, undefined);
	}

	@Test
	public void testToJournalContextType_CharacterWalletJournalResponseContextIdTypeEnum() {
		Map<CharacterWalletJournalResponse.ContextIdTypeEnum, ContextType> map = new EnumMap<>(CharacterWalletJournalResponse.ContextIdTypeEnum.class);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.ALLIANCE_ID, ContextType.ALLIANCE_ID);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.CHARACTER_ID, ContextType.CHARACTER_ID);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.CONTRACT_ID, ContextType.CONTRACT_ID);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.CORPORATION_ID, ContextType.CORPORATION_ID);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.EVE_SYSTEM, ContextType.EVE_SYSTEM);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.INDUSTRY_JOB_ID, ContextType.INDUSTRY_JOB_ID);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.MARKET_TRANSACTION_ID, ContextType.MARKET_TRANSACTION_ID);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.PLANET_ID, ContextType.PLANET_ID);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.STATION_ID, ContextType.STATION_ID);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.STRUCTURE_ID, ContextType.STRUCTURE_ID);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.SYSTEM_ID, ContextType.SYSTEM_ID);
		map.put(CharacterWalletJournalResponse.ContextIdTypeEnum.TYPE_ID, ContextType.TYPE_ID);
		assertEquals(map.size(), CharacterWalletJournalResponse.ContextIdTypeEnum.values().length);
		for (Map.Entry<CharacterWalletJournalResponse.ContextIdTypeEnum, ContextType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toJournalContextType(entry.getKey()));
		}
	}

	@Test
	public void testToJournalContextType_CorporationWalletJournalResponseContextIdTypeEnum() {
		Map<CorporationWalletJournalResponse.ContextIdTypeEnum, ContextType> map = new EnumMap<>(CorporationWalletJournalResponse.ContextIdTypeEnum.class);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.ALLIANCE_ID, ContextType.ALLIANCE_ID);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.CHARACTER_ID, ContextType.CHARACTER_ID);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.CONTRACT_ID, ContextType.CONTRACT_ID);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.CORPORATION_ID, ContextType.CORPORATION_ID);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.EVE_SYSTEM, ContextType.EVE_SYSTEM);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.INDUSTRY_JOB_ID, ContextType.INDUSTRY_JOB_ID);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.MARKET_TRANSACTION_ID, ContextType.MARKET_TRANSACTION_ID);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.PLANET_ID, ContextType.PLANET_ID);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.STATION_ID, ContextType.STATION_ID);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.STRUCTURE_ID, ContextType.STRUCTURE_ID);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.SYSTEM_ID, ContextType.SYSTEM_ID);
		map.put(CorporationWalletJournalResponse.ContextIdTypeEnum.TYPE_ID, ContextType.TYPE_ID);
		assertEquals(map.size(), CorporationWalletJournalResponse.ContextIdTypeEnum.values().length);
		for (Map.Entry<CorporationWalletJournalResponse.ContextIdTypeEnum, ContextType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toJournalContextType(entry.getKey()));
		}
	}

	@Test
	public void testToJournalContextType_String_String() {
		//Enum
		for (ContextType value : ContextType.values()) {
			assertEquals(value, RawConverter.toJournalContextType(value.name(), null));
		}
		//String
		for (ContextType value : ContextType.values()) {
			assertEquals(value, RawConverter.toJournalContextType(null, value.getValue()));
		}
	}

	@Test
	public void testToJournalContextType_RawJournalRefType() {
		Map<RawJournalRefType, ContextType> map = new EnumMap<>(RawJournalRefType.class);
		map.put(RawJournalRefType.ALLIANCE_MAINTAINANCE_FEE, ContextType.ALLIANCE_ID);
		map.put(RawJournalRefType.MISSION_REWARD, ContextType.CHARACTER_ID);
		map.put(RawJournalRefType.CONTRACT_AUCTION_BID, ContextType.CONTRACT_ID);
		map.put(RawJournalRefType.CORPORATION_LOGO_CHANGE_COST, ContextType.CORPORATION_ID);
		//map.put(RawJournalRefType., ContextType.EVE_SYSTEM);
		map.put(RawJournalRefType.MANUFACTURING, ContextType.INDUSTRY_JOB_ID);
		map.put(RawJournalRefType.MARKET_TRANSACTION, ContextType.MARKET_TRANSACTION_ID);
		map.put(RawJournalRefType.PLANETARY_IMPORT_TAX, ContextType.PLANET_ID);
		map.put(RawJournalRefType.INDUSTRY_JOB_TAX, ContextType.STATION_ID);
		//map.put(RawJournalRefType., ContextType.STRUCTURE_ID);
		map.put(RawJournalRefType.BOUNTY_PRIZES, ContextType.SYSTEM_ID);
		map.put(RawJournalRefType.BOUNTY_PRIZE, ContextType.TYPE_ID);
		assertEquals(map.size(), ContextType.values().length - 2);
		for (Map.Entry<RawJournalRefType, ContextType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toJournalContextType(entry.getKey()));
		}
	}

	@Test
	public void testToJournalContextID_3args() {
		Map<RawJournalRefType, Long> map = new EnumMap<>(RawJournalRefType.class);
		map.put(RawJournalRefType.ALLIANCE_MAINTAINANCE_FEE, 1L);
		map.put(RawJournalRefType.MISSION_REWARD, 1L);
		map.put(RawJournalRefType.CONTRACT_AUCTION_BID, 1L);
		map.put(RawJournalRefType.CORPORATION_LOGO_CHANGE_COST, 1L);
		//map.put(RawJournalRefType., ContextType.EVE_SYSTEM);
		map.put(RawJournalRefType.MANUFACTURING, 1L);
		map.put(RawJournalRefType.MARKET_TRANSACTION, 1L);
		map.put(RawJournalRefType.PLANETARY_IMPORT_TAX, 1L);
		map.put(RawJournalRefType.INDUSTRY_JOB_TAX, 1L);
		//map.put(RawJournalRefType., ContextType.STRUCTURE_ID);
		map.put(RawJournalRefType.BOUNTY_PRIZES, 1L);
		map.put(RawJournalRefType.BOUNTY_PRIZE, 1L);
		assertEquals(map.size(), ContextType.values().length - 2);
		for (Map.Entry<RawJournalRefType, Long> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toJournalContextID(1L, "1", entry.getKey()));
		}
	}

	@Test
	public void testToMarketOrderRange_3args() {
		//Enum
		for (RawMarketOrder.MarketOrderRange value : RawMarketOrder.MarketOrderRange.values()) {
			assertEquals(value, RawConverter.toMarketOrderRange(null, value.name(), null));
		}
		//String
		for (RawMarketOrder.MarketOrderRange value : RawMarketOrder.MarketOrderRange.values()) {
			assertEquals(value, RawConverter.toMarketOrderRange(null, null, value.getValue()));
		}
		Map<Integer, RawMarketOrder.MarketOrderRange> map = new HashMap<>();
		map.put(-1, RawMarketOrder.MarketOrderRange.STATION);
		map.put(0, RawMarketOrder.MarketOrderRange.SOLARSYSTEM);
		map.put(1, RawMarketOrder.MarketOrderRange._1);
		map.put(2, RawMarketOrder.MarketOrderRange._2);
		map.put(3, RawMarketOrder.MarketOrderRange._3);
		map.put(4, RawMarketOrder.MarketOrderRange._4);
		map.put(5, RawMarketOrder.MarketOrderRange._5);
		map.put(10, RawMarketOrder.MarketOrderRange._10);
		map.put(20, RawMarketOrder.MarketOrderRange._20);
		map.put(30, RawMarketOrder.MarketOrderRange._30);
		map.put(40, RawMarketOrder.MarketOrderRange._40);
		map.put(32767, RawMarketOrder.MarketOrderRange.REGION);
		assertEquals(map.size(), RawMarketOrder.MarketOrderRange.values().length);
		for (Map.Entry<Integer, RawMarketOrder.MarketOrderRange> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toMarketOrderRange(entry.getKey(), null, null));
		}
	}

	@Test
	public void testToMarketOrderRange_CharacterOrdersResponseRangeEnum() {
		//Enum
		for (CharacterOrdersResponse.RangeEnum value : CharacterOrdersResponse.RangeEnum.values()) {
			assertEquals(value.name(), RawConverter.toMarketOrderRange(value).name());
		}
	}

	@Test
	public void testToMarketOrderRange_CharacterOrdersHistoryResponseRangeEnum() {
		//Enum
		for (CharacterOrdersHistoryResponse.RangeEnum value : CharacterOrdersHistoryResponse.RangeEnum.values()) {
			assertEquals(value.name(), RawConverter.toMarketOrderRange(value).name());
		}
	}

	@Test
	public void testToMarketOrderRange_CorporationOrdersResponseRangeEnum() {
		//Enum
		for (CorporationOrdersResponse.RangeEnum value : CorporationOrdersResponse.RangeEnum.values()) {
			assertEquals(value.name(), RawConverter.toMarketOrderRange(value).name());
		}
	}

	@Test
	public void testToMarketOrderRange_CorporationOrdersHistoryResponseRangeEnum() {
		//Enum
		for (CorporationOrdersHistoryResponse.RangeEnum value : CorporationOrdersHistoryResponse.RangeEnum.values()) {
			assertEquals(value.name(), RawConverter.toMarketOrderRange(value).name());
		}
	}

	@Test
	public void testToMarketOrderRange_MarketOrdersResponseRangeEnum() {
		//Enum
		for (MarketOrdersResponse.RangeEnum value : MarketOrdersResponse.RangeEnum.values()) {
			assertEquals(value.name(), RawConverter.toMarketOrderRange(value).name());
		}
	}

	@Test
	public void testToMarketOrderRange_MarketStructuresResponseRangeEnum() {
		//Enum
		for (MarketStructuresResponse.RangeEnum value : MarketStructuresResponse.RangeEnum.values()) {
			assertEquals(value.name(), RawConverter.toMarketOrderRange(value).name());
		}
	}

	@Test
	public void testToMarketOrderState_3args() {
		//Enum
		for (RawMarketOrder.MarketOrderState value : RawMarketOrder.MarketOrderState.values()) {
			assertEquals(value, RawConverter.toMarketOrderState(null, value.name(), null));
		}
		//String
		for (RawMarketOrder.MarketOrderState value : RawMarketOrder.MarketOrderState.values()) {
			assertEquals(value, RawConverter.toMarketOrderState(null, null, value.getValue()));
		}
		Map<Integer, RawMarketOrder.MarketOrderState> map = new HashMap<>();
		map.put(0, RawMarketOrder.MarketOrderState.OPEN);
		map.put(1, RawMarketOrder.MarketOrderState.CLOSED);
		map.put(2, RawMarketOrder.MarketOrderState.EXPIRED);
		map.put(3, RawMarketOrder.MarketOrderState.CANCELLED);
		map.put(4, RawMarketOrder.MarketOrderState.PENDING);
		map.put(5, RawMarketOrder.MarketOrderState.CHARACTER_DELETED);
		map.put(-100, RawMarketOrder.MarketOrderState.UNKNOWN);
		assertEquals(map.size(), RawMarketOrder.MarketOrderState.values().length);
		for (Map.Entry<Integer, RawMarketOrder.MarketOrderState> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toMarketOrderState(entry.getKey(), null, null));
		}
	}

	@Test
	public void testToMarketOrderState_CharacterOrdersHistoryResponseStateEnum() {
		//Enum
		for (CharacterOrdersHistoryResponse.StateEnum value : CharacterOrdersHistoryResponse.StateEnum.values()) {
			assertEquals(value.name(), RawConverter.toMarketOrderState(value).name());
		}
	}

	@Test
	public void testToMarketOrderState_CorporationOrdersHistoryResponseStateEnum() {
		//Enum
		for (CorporationOrdersHistoryResponse.StateEnum value : CorporationOrdersHistoryResponse.StateEnum.values()) {
			assertEquals(value.name(), RawConverter.toMarketOrderState(value).name());
		}
	}

	@Test
	public void testFromMarketOrderIsBuyOrder() {
		Map<Boolean, Integer> map = new HashMap<>();
		map.put(true, 1);
		map.put(false, 0);
		for (Map.Entry<Boolean, Integer> entry : map.entrySet()) {
			assertEquals(entry.getValue(), (Integer) RawConverter.fromMarketOrderIsBuyOrder(entry.getKey()));
		}
	}

	@Test
	public void testToTransactionIsBuy() {
		Map<String, Boolean> map = new HashMap<>();
		map.put("buy", true);
		map.put("sell", false);
		for (Map.Entry<String, Boolean> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toTransactionIsBuy(entry.getKey()));
		}
	}

	@Test
	public void testToTransactionIsPersonal() {
		Map<String, Boolean> map = new HashMap<>();
		map.put("personal", true);
		map.put("corporate", false);
		for (Map.Entry<String, Boolean> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toTransactionIsPersonal(entry.getKey()));
		}
	}

	@Test
	public void testFromTransactionIsBuy() {
		Map<Boolean, String> map = new HashMap<>();
		map.put(true, "buy");
		map.put(false, "sell");
		for (Map.Entry<Boolean, String> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.fromTransactionIsBuy(entry.getKey()));
		}
	}

	@Test
	public void testFromTransactionIsPersonal() {
		Map<Boolean, String> map = new HashMap<>();
		map.put(true, "personal");
		map.put(false, "corporate");
		for (Map.Entry<Boolean, String> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.fromTransactionIsPersonal(entry.getKey()));
		}
	}

	@Test
	public void testToAssetQuantity() {
		assertEquals(1, RawConverter.toAssetQuantity(1, null));
		assertEquals(10, RawConverter.toAssetQuantity(10, null));
		assertEquals(10, RawConverter.toAssetQuantity(10, 0));
		assertEquals(-1, RawConverter.toAssetQuantity(10, -1));
		assertEquals(-2, RawConverter.toAssetQuantity(10, -2));
	}

	@Test
	public void testLocationFlag() {
		for (LocationFlag locationFlag : LocationFlag.values()) {
			ItemFlag itemFlag = StaticData.get().getItemFlags().get(locationFlag.getID());
			assertNotNull(locationFlag);
			assertTrue(locationFlag.name() + " != " + itemFlag.getFlagName(),
					itemFlag.getFlagID() == 0
					|| locationFlag.toString().toLowerCase().equals(itemFlag.getFlagText().toLowerCase().replace(" ", ""))
					|| locationFlag.toString().toLowerCase().equals(itemFlag.getFlagName().toLowerCase()));
		}
	}

	@Test
	public void testToLong_Number() {
		assertEquals(RawConverter.toLong(1L), (Long) 1L);
		assertEquals(RawConverter.toLong(0), (Long) 0L);
		assertEquals(RawConverter.toLong(0.0), (Long) 0L);
		assertEquals(RawConverter.toLong(0.0f), (Long) 0L);
		assertEquals((long) RawConverter.toLong(Long.MAX_VALUE), Long.MAX_VALUE);
		assertEquals((long) RawConverter.toLong(Long.MIN_VALUE), Long.MIN_VALUE);
		assertEquals((long) RawConverter.toLong(Integer.MAX_VALUE), (long) Integer.MAX_VALUE);
		assertEquals((long) RawConverter.toLong(Integer.MIN_VALUE), (long) Integer.MIN_VALUE);
		assertEquals((long) RawConverter.toLong(Double.MAX_VALUE), (long) Double.MAX_VALUE);
		assertEquals((long) RawConverter.toLong(Double.MIN_VALUE), (long) Double.MIN_VALUE);
		assertEquals((long) RawConverter.toLong(Float.MAX_VALUE), (long) Float.MAX_VALUE);
		assertEquals((long) RawConverter.toLong(Float.MIN_VALUE), (long) Float.MIN_VALUE);
	}

	@Test
	public void testToLong_String() {
		assertEquals(RawConverter.toLong(String.valueOf(1L)), (Long) 1L);
		assertEquals(RawConverter.toLong(String.valueOf(0)), (Long) 0L);
		assertEquals((long) RawConverter.toLong(String.valueOf(Long.MAX_VALUE)), Long.MAX_VALUE);
		assertEquals((long) RawConverter.toLong(String.valueOf(Long.MIN_VALUE)), Long.MIN_VALUE);
		assertEquals((long) RawConverter.toLong(String.valueOf(Integer.MAX_VALUE)), (long) Integer.MAX_VALUE);
		assertEquals((long) RawConverter.toLong(String.valueOf(Integer.MIN_VALUE)), (long) Integer.MIN_VALUE);
		assertEquals(RawConverter.toLong((String)null), null);
	}

	@Test
	public void testToInteger_Number() {
		assertEquals((int) RawConverter.toInteger(1L), 1);
		assertEquals((int) RawConverter.toInteger(0), 0);
		assertEquals((int) RawConverter.toInteger(0.0), 0);
		assertEquals((int) RawConverter.toInteger(0.0f), 0);
		assertEquals((int) RawConverter.toInteger(Long.MAX_VALUE), (int) Long.MAX_VALUE);
		assertEquals((int) RawConverter.toInteger(Long.MIN_VALUE), (int) Long.MIN_VALUE);
		assertEquals((int) RawConverter.toInteger(Integer.MAX_VALUE), Integer.MAX_VALUE);
		assertEquals((int) RawConverter.toInteger(Integer.MIN_VALUE), Integer.MIN_VALUE);
		assertEquals((int) RawConverter.toInteger(Double.MAX_VALUE), (int) Double.MAX_VALUE);
		assertEquals((int) RawConverter.toInteger(Double.MIN_VALUE), (int) Double.MIN_VALUE);
		assertEquals((int) RawConverter.toInteger(Float.MAX_VALUE), (int) Float.MAX_VALUE);
		assertEquals((int) RawConverter.toInteger(Float.MIN_VALUE), (int) Float.MIN_VALUE);
	}

	@Test
	public void testToInteger_Number_int() {
		assertEquals(RawConverter.toInteger(1L, 0), 1);
		assertEquals(RawConverter.toInteger(0, 1), 0);
		assertEquals(RawConverter.toInteger(0.0, 0), 0);
		assertEquals(RawConverter.toInteger(0.0f, 0), 0);
		assertEquals(RawConverter.toInteger(Long.MAX_VALUE, 0), (int) Long.MAX_VALUE);
		assertEquals(RawConverter.toInteger(Long.MIN_VALUE, 0), (int) Long.MIN_VALUE);
		assertEquals(RawConverter.toInteger(Integer.MAX_VALUE, 0), Integer.MAX_VALUE);
		assertEquals(RawConverter.toInteger(Integer.MIN_VALUE, 0), Integer.MIN_VALUE);
		assertEquals(RawConverter.toInteger(Double.MAX_VALUE, 0), (int) Double.MAX_VALUE);
		assertEquals(RawConverter.toInteger(Double.MIN_VALUE, 0), (int) Double.MIN_VALUE);
		assertEquals(RawConverter.toInteger(Float.MAX_VALUE, 0), (int) Float.MAX_VALUE);
		assertEquals(RawConverter.toInteger(Float.MIN_VALUE, 0), (int) Float.MIN_VALUE);
		assertEquals(RawConverter.toInteger(null, 0), 0);
		assertEquals(RawConverter.toInteger(null, 1), 1);
	}

	@Test
	public void testToInteger_String() {
		assertEquals((int) RawConverter.toInteger(String.valueOf(1L)), 1);
		assertEquals((int) RawConverter.toInteger(String.valueOf(0)), 0);
		assertEquals((int) RawConverter.toInteger(String.valueOf(Integer.MAX_VALUE)), Integer.MAX_VALUE);
		assertEquals((int) RawConverter.toInteger(String.valueOf(Integer.MIN_VALUE)), Integer.MIN_VALUE);
		assertEquals(RawConverter.toInteger((String)null), null);
	}

	@Test
	public void testToFloat() {
		float delta = 0;
		assertEquals(RawConverter.toFloat(Long.MAX_VALUE), Long.MAX_VALUE, delta);
		assertEquals(RawConverter.toFloat(Long.MIN_VALUE), Long.MIN_VALUE, delta);
		assertEquals(RawConverter.toFloat(Integer.MAX_VALUE), Integer.MAX_VALUE, delta);
		assertEquals(RawConverter.toFloat(Integer.MIN_VALUE), Integer.MIN_VALUE, delta);
		assertEquals((float) RawConverter.toFloat(Double.MAX_VALUE), (float) Double.MAX_VALUE, delta);
		assertEquals((float) RawConverter.toFloat(Double.MIN_VALUE), (float) Double.MIN_VALUE, delta);
		assertEquals(RawConverter.toFloat(Float.MAX_VALUE), Float.MAX_VALUE, delta);
		assertEquals(RawConverter.toFloat(Float.MIN_VALUE), Float.MIN_VALUE, delta);
	}

	@Test
	public void testToDouble() {
		float delta = 0;
		assertEquals(RawConverter.toDouble(Long.MAX_VALUE, 0), Long.MAX_VALUE, delta);
		assertEquals(RawConverter.toDouble(Long.MIN_VALUE, 0), Long.MIN_VALUE, delta);
		assertEquals(RawConverter.toDouble(Integer.MAX_VALUE, 0), Integer.MAX_VALUE, delta);
		assertEquals(RawConverter.toDouble(Integer.MIN_VALUE, 0), Integer.MIN_VALUE, delta);
		assertEquals(RawConverter.toDouble(Double.MAX_VALUE, 0), Double.MAX_VALUE, delta);
		assertEquals(RawConverter.toDouble(Double.MIN_VALUE, 0), Double.MIN_VALUE, delta);
		assertEquals(RawConverter.toDouble(Float.MAX_VALUE, 0), Float.MAX_VALUE, delta);
		assertEquals(RawConverter.toDouble(Float.MIN_VALUE, 0), Float.MIN_VALUE, delta);
		assertEquals(RawConverter.toDouble(null, 0), 0, delta);
		assertEquals(RawConverter.toDouble(null, 1), 1, delta);
	}

	@Test
	public void testToBoolean() {
		assertEquals(RawConverter.toBoolean(null), false);
		assertEquals(RawConverter.toBoolean(false), false);
		assertEquals(RawConverter.toBoolean(true), true);
	}

	@Test
	public void testToContainerLogAction() {
		Map<CorporationContainersLogsResponse.ActionEnum, RawContainerLog.ContainerAction> map = new HashMap<>();
		map.put(CorporationContainersLogsResponse.ActionEnum.ADD, RawContainerLog.ContainerAction.ADD);
		map.put(CorporationContainersLogsResponse.ActionEnum.ASSEMBLE, RawContainerLog.ContainerAction.ASSEMBLE);
		map.put(CorporationContainersLogsResponse.ActionEnum.CONFIGURE, RawContainerLog.ContainerAction.CONFIGURE);
		map.put(CorporationContainersLogsResponse.ActionEnum.ENTER_PASSWORD, RawContainerLog.ContainerAction.ENTER_PASSWORD);
		map.put(CorporationContainersLogsResponse.ActionEnum.LOCK, RawContainerLog.ContainerAction.LOCK);
		map.put(CorporationContainersLogsResponse.ActionEnum.MOVE, RawContainerLog.ContainerAction.MOVE);
		map.put(CorporationContainersLogsResponse.ActionEnum.REPACKAGE, RawContainerLog.ContainerAction.REPACKAGE);
		map.put(CorporationContainersLogsResponse.ActionEnum.SET_NAME, RawContainerLog.ContainerAction.SET_NAME);
		map.put(CorporationContainersLogsResponse.ActionEnum.SET_PASSWORD, RawContainerLog.ContainerAction.SET_PASSWORD);
		map.put(CorporationContainersLogsResponse.ActionEnum.UNLOCK, RawContainerLog.ContainerAction.UNLOCK);
		assertEquals(map.size(), CorporationContainersLogsResponse.ActionEnum.values().length);
		for (Map.Entry<CorporationContainersLogsResponse.ActionEnum, RawContainerLog.ContainerAction> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContainerLogAction(entry.getKey()));
		}
	}

	@Test
	public void testToContainerLogPasswordType() {
		Map<CorporationContainersLogsResponse.PasswordTypeEnum, RawContainerLog.ContainerPasswordType> map = new HashMap<>();
		map.put(CorporationContainersLogsResponse.PasswordTypeEnum.CONFIG, RawContainerLog.ContainerPasswordType.CONFIG);
		map.put(CorporationContainersLogsResponse.PasswordTypeEnum.GENERAL, RawContainerLog.ContainerPasswordType.GENERAL);
		assertEquals(map.size(), CorporationContainersLogsResponse.PasswordTypeEnum.values().length);
		for (Map.Entry<CorporationContainersLogsResponse.PasswordTypeEnum, RawContainerLog.ContainerPasswordType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContainerLogPasswordType(entry.getKey()));
		}
	}
}
