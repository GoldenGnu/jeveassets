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

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.eve.RefType;
import com.beimin.eveapi.response.eve.RefTypesResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalExtraInfo;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.io.shared.RawConverter.LocationFlag;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CorporationContainersLogsResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
			);
		}
	}

	@Test
	public void testToAssetLocationType() {
		Map<Long, RawAsset.LocationType> map = new HashMap<Long, RawAsset.LocationType>();
		map.put(10000002L, RawAsset.LocationType.OTHER);
		map.put(30000142L, RawAsset.LocationType.SOLAR_SYSTEM);
		map.put(60003466L, RawAsset.LocationType.STATION);
		assertEquals(map.size(), RawAsset.LocationType.values().length);
		for (Map.Entry<Long, RawAsset.LocationType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toAssetLocationType(entry.getKey()));
		}
	}

	@Test
	public void testToContractAvailability_String() {
		Map<String, RawContract.ContractAvailability> map = new HashMap<String, RawContract.ContractAvailability>();
		map.put(RawContract.ContractAvailability.ALLIANCE.name(), RawContract.ContractAvailability.ALLIANCE);
		map.put(RawContract.ContractAvailability.CORPORATION.name(), RawContract.ContractAvailability.CORPORATION);
		map.put(RawContract.ContractAvailability.PERSONAL.name(), RawContract.ContractAvailability.PERSONAL);
		map.put(RawContract.ContractAvailability.PUBLIC.name(), RawContract.ContractAvailability.PUBLIC);
		map.put("public", RawContract.ContractAvailability.PUBLIC);
		map.put("private", RawContract.ContractAvailability.PERSONAL);
		assertEquals(map.size(), RawContract.ContractAvailability.values().length + 2);
		for (Map.Entry<String, RawContract.ContractAvailability> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContractAvailability(entry.getKey()));
		}
	}

	@Test
	public void testToContractAvailability_ContractAvailability() {
		Map<com.beimin.eveapi.model.shared.ContractAvailability, RawContract.ContractAvailability> map = new EnumMap<com.beimin.eveapi.model.shared.ContractAvailability, RawContract.ContractAvailability>(com.beimin.eveapi.model.shared.ContractAvailability.class);
		map.put(com.beimin.eveapi.model.shared.ContractAvailability.PUBLIC, RawContract.ContractAvailability.PUBLIC);
		map.put(com.beimin.eveapi.model.shared.ContractAvailability.PRIVATE, RawContract.ContractAvailability.PERSONAL);
		assertEquals(map.size(), com.beimin.eveapi.model.shared.ContractAvailability.values().length);
		for (Map.Entry<com.beimin.eveapi.model.shared.ContractAvailability, RawContract.ContractAvailability> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContractAvailability(entry.getKey()));
		}
	}

	@Test
	public void testToContractStatus_String() {
		Map<String, RawContract.ContractStatus> map = new HashMap<String, RawContract.ContractStatus>();
		map.put("CANCELLED", RawContract.ContractStatus.CANCELLED);
		map.put("DELETED", RawContract.ContractStatus.DELETED);
		map.put("FAILED", RawContract.ContractStatus.FAILED);
		map.put("FINISHED", RawContract.ContractStatus.FINISHED);
		map.put("FINISHED_CONTRACTOR", RawContract.ContractStatus.FINISHED_CONTRACTOR);
		map.put("FINISHED_ISSUER", RawContract.ContractStatus.FINISHED_ISSUER);
		map.put("INPROGRESS", RawContract.ContractStatus.IN_PROGRESS);
		map.put("OUTSTANDING", RawContract.ContractStatus.OUTSTANDING);
		map.put("REJECTED", RawContract.ContractStatus.REJECTED);
		map.put("REVERSED", RawContract.ContractStatus.REVERSED);
		//EveAPI ContractStatus
		map.put("COMPLETED", RawContract.ContractStatus.FINISHED);
		map.put("COMPLETEDBYCONTRACTOR", RawContract.ContractStatus.FINISHED_CONTRACTOR);
		map.put("COMPLETEDBYISSUER", RawContract.ContractStatus.FINISHED_ISSUER);
		map.put("IN_PROGRESS", RawContract.ContractStatus.IN_PROGRESS);
		assertEquals(map.size(), RawContract.ContractStatus.values().length + 4);
		for (Map.Entry<String, RawContract.ContractStatus> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContractStatus(entry.getKey()));
		}
	}

	@Test
	public void testToContractStatus_ContractStatus() {
		Map<com.beimin.eveapi.model.shared.ContractStatus, RawContract.ContractStatus> map = new EnumMap<com.beimin.eveapi.model.shared.ContractStatus, RawContract.ContractStatus>(com.beimin.eveapi.model.shared.ContractStatus.class);
		map.put(com.beimin.eveapi.model.shared.ContractStatus.CANCELLED, RawContract.ContractStatus.CANCELLED);
		map.put(com.beimin.eveapi.model.shared.ContractStatus.DELETED, RawContract.ContractStatus.DELETED);
		map.put(com.beimin.eveapi.model.shared.ContractStatus.FAILED, RawContract.ContractStatus.FAILED);
		map.put(com.beimin.eveapi.model.shared.ContractStatus.COMPLETED, RawContract.ContractStatus.FINISHED);
		map.put(com.beimin.eveapi.model.shared.ContractStatus.COMPLETEDBYCONTRACTOR, RawContract.ContractStatus.FINISHED_CONTRACTOR);
		map.put(com.beimin.eveapi.model.shared.ContractStatus.COMPLETEDBYISSUER, RawContract.ContractStatus.FINISHED_ISSUER);
		map.put(com.beimin.eveapi.model.shared.ContractStatus.INPROGRESS, RawContract.ContractStatus.IN_PROGRESS);
		map.put(com.beimin.eveapi.model.shared.ContractStatus.OUTSTANDING, RawContract.ContractStatus.OUTSTANDING);
		map.put(com.beimin.eveapi.model.shared.ContractStatus.REJECTED, RawContract.ContractStatus.REJECTED);
		map.put(com.beimin.eveapi.model.shared.ContractStatus.REVERSED, RawContract.ContractStatus.REVERSED);
		assertEquals(map.size(), com.beimin.eveapi.model.shared.ContractStatus.values().length);
		for (Map.Entry<com.beimin.eveapi.model.shared.ContractStatus, RawContract.ContractStatus> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContractStatus(entry.getKey()));
		}
	}

	@Test
	public void testToContractType_String() {
		Map<String, RawContract.ContractType> map = new HashMap<String, RawContract.ContractType>();
		map.put(RawContract.ContractType.AUCTION.name(), RawContract.ContractType.AUCTION);
		map.put(RawContract.ContractType.COURIER.name(), RawContract.ContractType.COURIER);
		map.put(RawContract.ContractType.ITEM_EXCHANGE.name(), RawContract.ContractType.ITEM_EXCHANGE);
		map.put(RawContract.ContractType.LOAN.name(), RawContract.ContractType.LOAN);
		map.put(RawContract.ContractType.UNKNOWN.name(), RawContract.ContractType.UNKNOWN);
		map.put("Auction", RawContract.ContractType.AUCTION);
		map.put("Courier", RawContract.ContractType.COURIER);
		map.put("ItemExchange", RawContract.ContractType.ITEM_EXCHANGE);
		map.put("Loan", RawContract.ContractType.LOAN);
		assertEquals(map.size(), RawContract.ContractType.values().length + 4);
		for (Map.Entry<String, RawContract.ContractType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContractType(entry.getKey()));
		}
	}

	@Test
	public void testToContractType_ContractType() {
		Map<com.beimin.eveapi.model.shared.ContractType, RawContract.ContractType> map = new EnumMap<com.beimin.eveapi.model.shared.ContractType, RawContract.ContractType>(com.beimin.eveapi.model.shared.ContractType.class);
		map.put(com.beimin.eveapi.model.shared.ContractType.AUCTION, RawContract.ContractType.AUCTION);
		map.put(com.beimin.eveapi.model.shared.ContractType.COURIER, RawContract.ContractType.COURIER);
		map.put(com.beimin.eveapi.model.shared.ContractType.ITEMEXCHANGE, RawContract.ContractType.ITEM_EXCHANGE);
		map.put(com.beimin.eveapi.model.shared.ContractType.LOAN, RawContract.ContractType.LOAN);
		assertEquals(map.size(), com.beimin.eveapi.model.shared.ContractType.values().length);
		for (Map.Entry<com.beimin.eveapi.model.shared.ContractType, RawContract.ContractType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toContractType(entry.getKey()));
		}
	}

	@Test
	public void testToIndustryJobStatus() {
		Map<Integer, RawIndustryJob.IndustryJobStatus> map = new HashMap<Integer, RawIndustryJob.IndustryJobStatus>();
		map.put(1, RawIndustryJob.IndustryJobStatus.ACTIVE);
		map.put(2, RawIndustryJob.IndustryJobStatus.PAUSED);
		map.put(3, RawIndustryJob.IndustryJobStatus.READY);
		map.put(101, RawIndustryJob.IndustryJobStatus.DELIVERED);
		map.put(102, RawIndustryJob.IndustryJobStatus.CANCELLED);
		map.put(103, RawIndustryJob.IndustryJobStatus.REVERTED);
		assertEquals(map.size(), RawIndustryJob.IndustryJobStatus.values().length);
		for (Map.Entry<Integer, RawIndustryJob.IndustryJobStatus> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toIndustryJobStatus(entry.getKey()));
		}
	}

	@Test
	public void testFromIndustryJobStatus() {
		Map<RawIndustryJob.IndustryJobStatus, Integer> map = new EnumMap<RawIndustryJob.IndustryJobStatus, Integer>(RawIndustryJob.IndustryJobStatus.class);
		map.put(RawIndustryJob.IndustryJobStatus.ACTIVE, 1);
		map.put(RawIndustryJob.IndustryJobStatus.PAUSED, 2);
		map.put(RawIndustryJob.IndustryJobStatus.READY, 3);
		map.put(RawIndustryJob.IndustryJobStatus.DELIVERED, 101);
		map.put(RawIndustryJob.IndustryJobStatus.CANCELLED, 102);
		map.put(RawIndustryJob.IndustryJobStatus.REVERTED, 103);
		assertEquals(map.size(), RawIndustryJob.IndustryJobStatus.values().length);
		for (Map.Entry<RawIndustryJob.IndustryJobStatus, Integer> entry : map.entrySet()) {
			assertEquals(entry.getValue(), (Integer) RawConverter.fromIndustryJobStatus(entry.getKey()));
		}
	}

	@Test
	public void testToJournalRefType_int() {
		for (com.beimin.eveapi.model.shared.RefType refType : com.beimin.eveapi.model.shared.RefType.values()) {
			RawJournalRefType type = RawConverter.toJournalRefType(refType.getId());
			assertEquals(type.name(), refType.name());
		}
	}

	@Test
	public void testToJournalRefType_CharacterWalletJournalResponseRefTypeEnum() {
		assertEquals(117, CharacterWalletJournalResponse.RefTypeEnum.values().length);
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
		assertEquals(117, CorporationWalletJournalResponse.RefTypeEnum.values().length);
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
	public void testToJournalPartyType_Integer() {
		Map<Integer, RawJournal.JournalPartyType> map = new HashMap<Integer, RawJournal.JournalPartyType>();
		map.put(2, RawJournal.JournalPartyType.CORPORATION);
		for (int i = 1373; i <= 1386; i++) {
			map.put(i, RawJournal.JournalPartyType.CHARACTER);
		}
		map.put(16159, RawJournal.JournalPartyType.ALLIANCE);
		map.put(500001, RawJournal.JournalPartyType.FACTION);
		map.put(30000142, RawJournal.JournalPartyType.SYSTEM);
		assertEquals(5, RawJournal.JournalPartyType.values().length);
		for (Map.Entry<Integer, RawJournal.JournalPartyType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toJournalPartyType(entry.getKey()));
		}
	}

	@Test
	public void testToJournalPartyType_CharacterWalletJournalResponseFirstPartyTypeEnum() {
		Map<CharacterWalletJournalResponse.FirstPartyTypeEnum, RawJournal.JournalPartyType> map = new EnumMap<CharacterWalletJournalResponse.FirstPartyTypeEnum, RawJournal.JournalPartyType>(CharacterWalletJournalResponse.FirstPartyTypeEnum.class);
		map.put(CharacterWalletJournalResponse.FirstPartyTypeEnum.ALLIANCE, RawJournal.JournalPartyType.ALLIANCE);
		map.put(CharacterWalletJournalResponse.FirstPartyTypeEnum.CHARACTER, RawJournal.JournalPartyType.CHARACTER);
		map.put(CharacterWalletJournalResponse.FirstPartyTypeEnum.CORPORATION, RawJournal.JournalPartyType.CORPORATION);
		map.put(CharacterWalletJournalResponse.FirstPartyTypeEnum.FACTION, RawJournal.JournalPartyType.FACTION);
		map.put(CharacterWalletJournalResponse.FirstPartyTypeEnum.SYSTEM, RawJournal.JournalPartyType.SYSTEM);
		assertEquals(map.size(), CharacterWalletJournalResponse.FirstPartyTypeEnum.values().length);
		for (Map.Entry<CharacterWalletJournalResponse.FirstPartyTypeEnum, RawJournal.JournalPartyType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toJournalPartyType(entry.getKey()));
		}
	}

	@Test
	public void testToJournalPartyType_CharacterWalletJournalResponseSecondPartyTypeEnum() {
		Map<CharacterWalletJournalResponse.SecondPartyTypeEnum, RawJournal.JournalPartyType> map = new EnumMap<CharacterWalletJournalResponse.SecondPartyTypeEnum, RawJournal.JournalPartyType>(CharacterWalletJournalResponse.SecondPartyTypeEnum.class);
		map.put(CharacterWalletJournalResponse.SecondPartyTypeEnum.ALLIANCE, RawJournal.JournalPartyType.ALLIANCE);
		map.put(CharacterWalletJournalResponse.SecondPartyTypeEnum.CHARACTER, RawJournal.JournalPartyType.CHARACTER);
		map.put(CharacterWalletJournalResponse.SecondPartyTypeEnum.CORPORATION, RawJournal.JournalPartyType.CORPORATION);
		map.put(CharacterWalletJournalResponse.SecondPartyTypeEnum.FACTION, RawJournal.JournalPartyType.FACTION);
		map.put(CharacterWalletJournalResponse.SecondPartyTypeEnum.SYSTEM, RawJournal.JournalPartyType.SYSTEM);
		assertEquals(map.size(), CharacterWalletJournalResponse.SecondPartyTypeEnum.values().length);
		for (Map.Entry<CharacterWalletJournalResponse.SecondPartyTypeEnum, RawJournal.JournalPartyType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toJournalPartyType(entry.getKey()));
		}
	}

	@Test
	public void testToJournalPartyType_CorporationWalletJournalResponseFirstPartyTypeEnum() {
		Map<CorporationWalletJournalResponse.FirstPartyTypeEnum, RawJournal.JournalPartyType> map = new EnumMap<CorporationWalletJournalResponse.FirstPartyTypeEnum, RawJournal.JournalPartyType>(CorporationWalletJournalResponse.FirstPartyTypeEnum.class);
		map.put(CorporationWalletJournalResponse.FirstPartyTypeEnum.ALLIANCE, RawJournal.JournalPartyType.ALLIANCE);
		map.put(CorporationWalletJournalResponse.FirstPartyTypeEnum.CHARACTER, RawJournal.JournalPartyType.CHARACTER);
		map.put(CorporationWalletJournalResponse.FirstPartyTypeEnum.CORPORATION, RawJournal.JournalPartyType.CORPORATION);
		map.put(CorporationWalletJournalResponse.FirstPartyTypeEnum.FACTION, RawJournal.JournalPartyType.FACTION);
		map.put(CorporationWalletJournalResponse.FirstPartyTypeEnum.SYSTEM, RawJournal.JournalPartyType.SYSTEM);
		assertEquals(map.size(), CorporationWalletJournalResponse.FirstPartyTypeEnum.values().length);
		for (Map.Entry<CorporationWalletJournalResponse.FirstPartyTypeEnum, RawJournal.JournalPartyType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toJournalPartyType(entry.getKey()));
		}
	}

	@Test
	public void testToJournalPartyType_CorporationWalletJournalResponseSecondPartyTypeEnum() {
		Map<CorporationWalletJournalResponse.SecondPartyTypeEnum, RawJournal.JournalPartyType> map = new EnumMap<CorporationWalletJournalResponse.SecondPartyTypeEnum, RawJournal.JournalPartyType>(CorporationWalletJournalResponse.SecondPartyTypeEnum.class);
		map.put(CorporationWalletJournalResponse.SecondPartyTypeEnum.ALLIANCE, RawJournal.JournalPartyType.ALLIANCE);
		map.put(CorporationWalletJournalResponse.SecondPartyTypeEnum.CHARACTER, RawJournal.JournalPartyType.CHARACTER);
		map.put(CorporationWalletJournalResponse.SecondPartyTypeEnum.CORPORATION, RawJournal.JournalPartyType.CORPORATION);
		map.put(CorporationWalletJournalResponse.SecondPartyTypeEnum.FACTION, RawJournal.JournalPartyType.FACTION);
		map.put(CorporationWalletJournalResponse.SecondPartyTypeEnum.SYSTEM, RawJournal.JournalPartyType.SYSTEM);
		assertEquals(map.size(), CorporationWalletJournalResponse.SecondPartyTypeEnum.values().length);
		for (Map.Entry<CorporationWalletJournalResponse.SecondPartyTypeEnum, RawJournal.JournalPartyType> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toJournalPartyType(entry.getKey()));
		}
	}

	@Test
	public void testFromJournalPartyType() {
		Map<RawJournal.JournalPartyType, Integer> map = new EnumMap<RawJournal.JournalPartyType, Integer>(RawJournal.JournalPartyType.class);
		map.put(RawJournal.JournalPartyType.ALLIANCE, 16159);
		map.put(RawJournal.JournalPartyType.CHARACTER, 1373);
		map.put(RawJournal.JournalPartyType.CORPORATION, 2);
		map.put(RawJournal.JournalPartyType.FACTION, 500001);
		map.put(RawJournal.JournalPartyType.SYSTEM, 30000142);
		assertEquals(map.size(), RawJournal.JournalPartyType.values().length);
		for (Map.Entry<RawJournal.JournalPartyType, Integer> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.fromJournalPartyType(entry.getKey()));
		}
	}

	@Test
	public void testToMarketOrderRange() {
		Map<Integer, RawMarketOrder.MarketOrderRange> map = new HashMap<Integer, RawMarketOrder.MarketOrderRange>();
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
			assertEquals(entry.getValue(), RawConverter.toMarketOrderRange(entry.getKey()));
		}
	}

	@Test
	public void testFromRawJournalExtraInfoArgID() {
		String stringValue = String.valueOf(Integer.MAX_VALUE);
		Long longValue = Long.valueOf(Integer.MAX_VALUE);
		for (RawJournalRefType journalRefType : RawJournalRefType.values()) {
			if (journalRefType.getArgID() != null) {
				assertEquals("ArgID: " + journalRefType.name() + "->" + journalRefType.getArgID().name(), longValue, RawConverter.fromRawJournalExtraInfoArgID(new RawJournalExtraInfo(longValue, stringValue, journalRefType)));
			}
		}
	}

	@Test
	public void testFromRawJournalExtraInfoArgName() {
		String stringValue = String.valueOf(Integer.MAX_VALUE);
		long longValue = Integer.MAX_VALUE;
		for (RawJournalRefType journalRefType : RawJournalRefType.values()) {
			if (journalRefType.getArgName() != null
					&& journalRefType.getArgName() != RawJournal.ArgName.STATION_NAME
					&& journalRefType.getArgName() != RawJournal.ArgName.CORPORATION_NAME
					&& journalRefType.getArgName() != RawJournal.ArgName.ALLIANCE_NAME
					&& journalRefType.getArgName() != RawJournal.ArgName.PLANET_NAME) {
				assertEquals("ArgName: " + journalRefType.name() + "->" + journalRefType.getArgName().name(), stringValue, RawConverter.fromRawJournalExtraInfoArgName(new RawJournalExtraInfo(longValue, stringValue, journalRefType)));
			}
		}
	}

	@Test
	public void testFromMarketOrderRange() {
		Map<RawMarketOrder.MarketOrderRange, Integer> map = new EnumMap<RawMarketOrder.MarketOrderRange, Integer>(RawMarketOrder.MarketOrderRange.class);
		map.put(RawMarketOrder.MarketOrderRange.STATION, -1);
		map.put(RawMarketOrder.MarketOrderRange.SOLARSYSTEM, 0);
		map.put(RawMarketOrder.MarketOrderRange._1, 1);
		map.put(RawMarketOrder.MarketOrderRange._2, 2);
		map.put(RawMarketOrder.MarketOrderRange._3, 3);
		map.put(RawMarketOrder.MarketOrderRange._4, 4);
		map.put(RawMarketOrder.MarketOrderRange._5, 5);
		map.put(RawMarketOrder.MarketOrderRange._10, 10);
		map.put(RawMarketOrder.MarketOrderRange._20, 20);
		map.put(RawMarketOrder.MarketOrderRange._30, 30);
		map.put(RawMarketOrder.MarketOrderRange._40, 40);
		map.put(RawMarketOrder.MarketOrderRange.REGION, 32767);
		assertEquals(map.size(), RawMarketOrder.MarketOrderRange.values().length);
		for (Map.Entry<RawMarketOrder.MarketOrderRange, Integer> entry : map.entrySet()) {
			assertEquals(entry.getValue(), (Integer) RawConverter.fromMarketOrderRange(entry.getKey()));
		}
	}

	@Test
	public void testToMarketOrderState() {
		Map<Integer, RawMarketOrder.MarketOrderState> map = new HashMap<Integer, RawMarketOrder.MarketOrderState>();
		map.put(0, RawMarketOrder.MarketOrderState.OPEN);
		map.put(1, RawMarketOrder.MarketOrderState.CLOSED);
		map.put(2, RawMarketOrder.MarketOrderState.EXPIRED);
		map.put(3, RawMarketOrder.MarketOrderState.CANCELLED);
		map.put(4, RawMarketOrder.MarketOrderState.PENDING);
		map.put(5, RawMarketOrder.MarketOrderState.CHARACTER_DELETED);
		map.put(-100, RawMarketOrder.MarketOrderState.UNKNOWN);
		assertEquals(map.size(), RawMarketOrder.MarketOrderState.values().length);
		for (Map.Entry<Integer, RawMarketOrder.MarketOrderState> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toMarketOrderState(entry.getKey()));
		}
	}

	@Test
	public void testFromMarketOrderState() {
		Map<RawMarketOrder.MarketOrderState, Integer> map = new EnumMap<RawMarketOrder.MarketOrderState, Integer>(RawMarketOrder.MarketOrderState.class);
		map.put(RawMarketOrder.MarketOrderState.OPEN, 0);
		map.put(RawMarketOrder.MarketOrderState.CLOSED, 1);
		map.put(RawMarketOrder.MarketOrderState.EXPIRED, 2);
		map.put(RawMarketOrder.MarketOrderState.CANCELLED, 3);
		map.put(RawMarketOrder.MarketOrderState.PENDING, 4);
		map.put(RawMarketOrder.MarketOrderState.CHARACTER_DELETED, 5);
		map.put(RawMarketOrder.MarketOrderState.UNKNOWN, -100);
		assertEquals(map.size(), RawMarketOrder.MarketOrderState.values().length);
		for (Map.Entry<RawMarketOrder.MarketOrderState, Integer> entry : map.entrySet()) {
			assertEquals(entry.getValue(), (Integer) RawConverter.fromMarketOrderState(entry.getKey()));
		}
	}

	@Test
	public void testFromMarketOrderIsBuyOrder() {
		Map<Boolean, Integer> map = new HashMap<Boolean, Integer>();
		map.put(true, 1);
		map.put(false, 0);
		for (Map.Entry<Boolean, Integer> entry : map.entrySet()) {
			assertEquals(entry.getValue(), (Integer) RawConverter.fromMarketOrderIsBuyOrder(entry.getKey()));
		}
	}

	@Test
	public void testToTransactionIsBuy() {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("buy", true);
		map.put("sell", false);
		for (Map.Entry<String, Boolean> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toTransactionIsBuy(entry.getKey()));
		}
	}

	@Test
	public void testToTransactionIsPersonal() {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("personal", true);
		map.put("corporate", false);
		for (Map.Entry<String, Boolean> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.toTransactionIsPersonal(entry.getKey()));
		}
	}

	@Test
	public void testFromTransactionIsBuy() {
		Map<Boolean, String> map = new HashMap<Boolean, String>();
		map.put(true, "buy");
		map.put(false, "sell");
		for (Map.Entry<Boolean, String> entry : map.entrySet()) {
			assertEquals(entry.getValue(), RawConverter.fromTransactionIsBuy(entry.getKey()));
		}
	}

	@Test
	public void testFromTransactionIsPersonal() {
		Map<Boolean, String> map = new HashMap<Boolean, String>();
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
	public void testEnum() {
		try {
			Map<Integer, com.beimin.eveapi.model.eve.RefType> refTypes = new HashMap<Integer, com.beimin.eveapi.model.eve.RefType>();
			Map<Integer, RawJournalRefType> journalRefTypes = new HashMap<Integer, RawJournalRefType>();
			Set<Integer> ids = new HashSet<Integer>();
			RefTypesResponse response = new com.beimin.eveapi.parser.eve.RefTypesParser().getResponse();
			for (com.beimin.eveapi.model.eve.RefType apiRefType : response.getAll()) {
				refTypes.put(apiRefType.getRefTypeID(), apiRefType);
				ids.add(apiRefType.getRefTypeID());
			}
			for (RawJournalRefType journalRefType : RawJournalRefType.values()) {
				journalRefTypes.put(journalRefType.getID(), journalRefType);
				ids.add(journalRefType.getID());
			}
			for (Integer id : ids) {
				RefType refType = refTypes.get(id);
				assertNotNull(refType);
				RawJournalRefType journalRefType = journalRefTypes.get(id);
				assertNotNull(journalRefType);
				assertEquals(refType.getRefTypeName(), journalRefType.toString());
			}
		} catch (ApiException ex) {
			fail("Fail to get RefTypes");
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
	public void testToDouble_Number() {
		float delta = 0;
		assertEquals(RawConverter.toDouble(Long.MAX_VALUE), Long.MAX_VALUE, delta);
		assertEquals(RawConverter.toDouble(Long.MIN_VALUE), Long.MIN_VALUE, delta);
		assertEquals(RawConverter.toDouble(Integer.MAX_VALUE), Integer.MAX_VALUE, delta);
		assertEquals(RawConverter.toDouble(Integer.MIN_VALUE), Integer.MIN_VALUE, delta);
		assertEquals(RawConverter.toDouble(Double.MAX_VALUE), Double.MAX_VALUE, delta);
		assertEquals(RawConverter.toDouble(Double.MIN_VALUE), Double.MIN_VALUE, delta);
		assertEquals(RawConverter.toDouble(Float.MAX_VALUE), Float.MAX_VALUE, delta);
		assertEquals(RawConverter.toDouble(Float.MIN_VALUE), Float.MIN_VALUE, delta);
	}

	@Test
	public void testToDouble_Number_double() {
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
