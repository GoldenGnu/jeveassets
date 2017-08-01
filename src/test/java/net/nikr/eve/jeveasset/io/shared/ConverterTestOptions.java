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
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.MarketPriceData;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccount;
import net.nikr.eve.jeveasset.data.raw.RawAsset;
import net.nikr.eve.jeveasset.data.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.raw.RawContract;
import net.nikr.eve.jeveasset.data.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.raw.RawJournal;
import net.nikr.eve.jeveasset.data.raw.RawJournalExtraInfo;
import net.nikr.eve.jeveasset.data.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CharacterBlueprintsResponse;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterWalletJournalExtraInfoResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import org.joda.time.DateTime;

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

	public CharacterAssetsResponse.LocationTypeEnum getLocationTypeEsi();

	//LocationFlag
	public CharacterBlueprintsResponse.LocationFlagEnum getLocationFlagEsiBlueprint();

	public CharacterAssetsResponse.LocationFlagEnum getLocationFlagEsiAssets();

	public int getLocationFlagEveApi();

	//ContractAvailability
	public RawContract.ContractAvailability getContractAvailabilityRaw();

	public CharacterContractsResponse.AvailabilityEnum getContractAvailabilityEsi();

	public com.beimin.eveapi.model.shared.ContractAvailability getContractAvailabilityEveApi();

	public String getContractAvailabilityEveKit();

	//ContractStatus
	public RawContract.ContractStatus getContractStatusRaw();

	public CharacterContractsResponse.StatusEnum getContractStatusEsi();

	public com.beimin.eveapi.model.shared.ContractStatus getContractStatusEveApi();

	public String getContractStatusEveKit();

	//ContractType
	public RawContract.ContractType getContractTypeRaw();

	public com.beimin.eveapi.model.shared.ContractType getContractTypeEveApi();

	public CharacterContractsResponse.TypeEnum getContractTypeEsi();

	public String getContractTypeEveKit();

	//IndustryJobStatus
	public RawIndustryJob.IndustryJobStatus getIndustryJobStatusRaw();

	public CharacterIndustryJobsResponse.StatusEnum getIndustryJobStatusEsi();

	public int getIndustryJobStatusEveApi();

	//JournalExtraInfo
	public RawJournalExtraInfo getJournalExtraInfoRaw();

	public CharacterWalletJournalExtraInfoResponse getJournalExtraInfoEsi();

	//JournalPartyType
	public RawJournal.JournalPartyType getJournalPartyTypeRaw();

	public CharacterWalletJournalResponse.FirstPartyTypeEnum getJournalPartyTypeEsiFirst();

	public CharacterWalletJournalResponse.SecondPartyTypeEnum getJournalPartyTypeEsiSecond();

	public int getJournalPartyTypeEveApi();

	//JournalRefType
	public RawJournalRefType getJournalRefTypeRaw();

	public CharacterWalletJournalResponse.RefTypeEnum getJournalRefTypeEsi();

	public com.beimin.eveapi.model.shared.RefType getJournalRefTypeEveApi();

	//MarketOrderRange
	public RawMarketOrder.MarketOrderRange getMarketOrderRangeRaw();

	public CharacterOrdersResponse.RangeEnum getMarketOrderRangeEsi();

	public int getMarketOrderRangeEveApi();

	//MarketOrderState
	public RawMarketOrder.MarketOrderState getMarketOrderStateRaw();

	public CharacterOrdersResponse.StateEnum getMarketOrderStateEsi();

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

	default DateTime getDateTime() {
		return new DateTime(getDate());
	}

	default BigDecimal getBigDecimal() {
		return new BigDecimal(getDouble());
	}
}
