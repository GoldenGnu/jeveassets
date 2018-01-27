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
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
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

public interface ConverterTestOptions {

	public Integer getInteger();

	public Float getFloat();

	public Boolean getBoolean();

	public Long getLong();

	public Double getDouble();

	public Date getDate();

	public String getString();

	public ItemFlag getItemFlag();

	public PriceData getPriceData();

	public UserItem<Integer, Double> getUserPrice();

	public MarketPriceData getMarketPriceData();

	public Tags getTags();

	public RawBlueprint getRawBlueprint();

	public Percent getPercent();

	//LocationType
	public MyLocation getMyLocation();

	public Long getLocationTypeEveApi();

	public RawAsset.LocationType getLocationTypeRaw();

	public CharacterAssetsResponse.LocationTypeEnum getLocationTypeEsiCharacter();

	public CorporationAssetsResponse.LocationTypeEnum getLocationTypeEsiCorporation();

	//LocationFlag
	public CharacterBlueprintsResponse.LocationFlagEnum getLocationFlagEsiBlueprintCharacter();

	public CorporationBlueprintsResponse.LocationFlagEnum getLocationFlagEsiBlueprintCorporation();

	public CharacterAssetsResponse.LocationFlagEnum getLocationFlagEsiAssetsCharacter();

	public CorporationAssetsResponse.LocationFlagEnum getLocationFlagEsiAssetsCorporation();

	public CorporationContainersLogsResponse.LocationFlagEnum getLocationFlagEsiContainersLogsCorporation();

	public int getLocationFlagEveApi();

	//ContractAvailability
	public RawContract.ContractAvailability getContractAvailabilityRaw();

	public CharacterContractsResponse.AvailabilityEnum getContractAvailabilityEsiCharacter();

	public CorporationContractsResponse.AvailabilityEnum getContractAvailabilityEsiCorporation();

	public com.beimin.eveapi.model.shared.ContractAvailability getContractAvailabilityEveApi();

	public String getContractAvailabilityEveKit();

	//ContractStatus
	public RawContract.ContractStatus getContractStatusRaw();

	public CharacterContractsResponse.StatusEnum getContractStatusEsiCharacter();

	public CorporationContractsResponse.StatusEnum getContractStatusEsiCorporation();

	public com.beimin.eveapi.model.shared.ContractStatus getContractStatusEveApi();

	public String getContractStatusEveKit();

	//ContractType
	public RawContract.ContractType getContractTypeRaw();

	public com.beimin.eveapi.model.shared.ContractType getContractTypeEveApi();

	public CharacterContractsResponse.TypeEnum getContractTypeEsiCharacter();

	public CorporationContractsResponse.TypeEnum getContractTypeEsiCorporation();

	public String getContractTypeEveKit();

	//IndustryJobStatus
	public RawIndustryJob.IndustryJobStatus getIndustryJobStatusRaw();

	public CharacterIndustryJobsResponse.StatusEnum getIndustryJobStatusEsiCharacter();

	public CorporationIndustryJobsResponse.StatusEnum getIndustryJobStatusEsiCorporation();

	public int getIndustryJobStatusEveApi();

	//JournalExtraInfo
	public RawJournalExtraInfo getJournalExtraInfoRaw();

	public CharacterWalletJournalExtraInfoResponse getJournalExtraInfoEsiCharacter();
	
	public CorporationWalletJournalExtraInfoResponse getJournalExtraInfoEsiCorporation();

	//JournalPartyType
	public RawJournal.JournalPartyType getJournalPartyTypeRaw();

	public CharacterWalletJournalResponse.FirstPartyTypeEnum getJournalPartyTypeEsiFirstCharacter();

	public CharacterWalletJournalResponse.SecondPartyTypeEnum getJournalPartyTypeEsiSecondCharacter();

	public CorporationWalletJournalResponse.FirstPartyTypeEnum getJournalPartyTypeEsiFirstCorporation();

	public CorporationWalletJournalResponse.SecondPartyTypeEnum getJournalPartyTypeEsiSecondCorporation();

	public int getJournalPartyTypeEveApi();

	//JournalRefType
	public RawJournalRefType getJournalRefTypeRaw();

	public CharacterWalletJournalResponse.RefTypeEnum getJournalRefTypeEsiCharacter();
	
	public CorporationWalletJournalResponse.RefTypeEnum getJournalRefTypeEsiCorporation();

	public com.beimin.eveapi.model.shared.RefType getJournalRefTypeEveApi();

	//MarketOrderRange
	public RawMarketOrder.MarketOrderRange getMarketOrderRangeRaw();

	public CharacterOrdersResponse.RangeEnum getMarketOrderRangeEsiCharacter();

	public CharacterOrdersHistoryResponse.RangeEnum getMarketOrderRangeEsiCharacterHistory();

	public CorporationOrdersResponse.RangeEnum getMarketOrderRangeEsiCorporation();

	public CorporationOrdersHistoryResponse.RangeEnum getMarketOrderRangeEsiCorporationHistory();

	public int getMarketOrderRangeEveApi();

	//MarketOrderState
	public RawMarketOrder.MarketOrderState getMarketOrderStateRaw();

	public CharacterOrdersResponse.StateEnum getMarketOrderStateEsiCharacter();

	public CharacterOrdersHistoryResponse.StateEnum getMarketOrderStateEsiCharacterHistory();

	public CorporationOrdersResponse.StateEnum getMarketOrderStateEsiCorporation();

	public CorporationOrdersHistoryResponse.StateEnum getMarketOrderStateEsiCorporationHistory();

	public int getMarketOrderStateEveApi();

	public int getIndex();

	//ContainerLog

	public RawContainerLog.ContainerAction getContainerActionRaw();

	public CorporationContainersLogsResponse.ActionEnum getContainerActionEsi();

	public RawContainerLog.ContainerPasswordType getContainerPasswordTypeRaw();

	public CorporationContainersLogsResponse.PasswordTypeEnum getContainerPasswordTypeEsi();

	//Owner
	public EsiCallbackURL getEsiCallbackURL();

	public KeyType getKeyType();

	public EveApiAccount getEveApiAccount();

	//Default
	default OffsetDateTime getOffsetDateTime() {
		return getDate().toInstant().atOffset(ZoneOffset.UTC);
	}

	default Object getNull() {
		return null;
	}

	default BigDecimal getBigDecimal() {
		return new BigDecimal(getDouble());
	}
}
