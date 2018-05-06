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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount.KeyType;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType;
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
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CorporationAssetsResponse;
import net.troja.eve.esi.model.CorporationBlueprintsResponse;
import net.troja.eve.esi.model.CorporationContractsResponse;
import net.troja.eve.esi.model.CorporationIndustryJobsResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;
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

	public String getLocationFlagEveKit();

	public int getLocationFlagEveApi();

	//ContractAvailability
	public RawContract.ContractAvailability getContractAvailabilityRaw();

	public CharacterContractsResponse.AvailabilityEnum getContractAvailabilityEsiCharacter();

	public CorporationContractsResponse.AvailabilityEnum getContractAvailabilityEsiCorporation();

	public String getContractAvailabilityEveKit();

	//ContractStatus
	public RawContract.ContractStatus getContractStatusRaw();

	public CharacterContractsResponse.StatusEnum getContractStatusEsiCharacter();

	public CorporationContractsResponse.StatusEnum getContractStatusEsiCorporation();

	public String getContractStatusEveKit();

	//ContractType
	public RawContract.ContractType getContractTypeRaw();

	public CharacterContractsResponse.TypeEnum getContractTypeEsiCharacter();

	public CorporationContractsResponse.TypeEnum getContractTypeEsiCorporation();

	public String getContractTypeEveKit();

	//IndustryJobStatus
	public RawIndustryJob.IndustryJobStatus getIndustryJobStatusRaw();

	public CharacterIndustryJobsResponse.StatusEnum getIndustryJobStatusEsiCharacter();

	public CorporationIndustryJobsResponse.StatusEnum getIndustryJobStatusEsiCorporation();

	public int getIndustryJobStatusEveApi();

	public String getIndustryJobStatusEveKit();

	//JournalContextType
	public ContextType getJournalContextTypeRaw();

	public CharacterWalletJournalResponse.ContextIdTypeEnum getJournalContextTypeEsiCharacter();
	
	public CorporationWalletJournalResponse.ContextIdTypeEnum getJournalContextTypeEsiCorporation();

	//JournalRefType
	public RawJournalRefType getJournalRefTypeRaw();

	public CharacterWalletJournalResponse.RefTypeEnum getJournalRefTypeEsiCharacter();
	
	public CorporationWalletJournalResponse.RefTypeEnum getJournalRefTypeEsiCorporation();

	//MarketOrderRange
	public RawMarketOrder.MarketOrderRange getMarketOrderRangeRaw();

	public CharacterOrdersResponse.RangeEnum getMarketOrderRangeEsiCharacter();

	public CharacterOrdersHistoryResponse.RangeEnum getMarketOrderRangeEsiCharacterHistory();

	public CorporationOrdersResponse.RangeEnum getMarketOrderRangeEsiCorporation();

	public CorporationOrdersHistoryResponse.RangeEnum getMarketOrderRangeEsiCorporationHistory();

	public int getMarketOrderRangeEveApi();

	//MarketOrderState
	public RawMarketOrder.MarketOrderState getMarketOrderStateRaw();

	public CharacterOrdersHistoryResponse.StateEnum getMarketOrderStateEsiCharacterHistory();

	public CorporationOrdersHistoryResponse.StateEnum getMarketOrderStateEsiCorporationHistory();

	public int getMarketOrderStateEveApi();

	public int getIndex();

	//Owner
	public EsiCallbackURL getEsiCallbackURL();

	public KeyType getKeyType();

	public EveApiAccount getEveApiAccount();

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
