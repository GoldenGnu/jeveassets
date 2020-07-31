/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data.settings;

import net.nikr.eve.jeveasset.i18n.DataColors;


public enum ColorEntry {
	ASSETS_REPROCESSING_EQUAL(ColorEntryGroup.ASSETS, DataColors.get().assetsReprocessingEqual()),
	ASSETS_REPROCESSING_REPROCES(ColorEntryGroup.ASSETS, DataColors.get().assetsReprocessingReproces()),
	ASSETS_REPROCESSING_SELL(ColorEntryGroup.ASSETS, DataColors.get().assetsReprocessingSell()),
	ASSETS_REPROCESS(ColorEntryGroup.ASSETS, DataColors.get().assetsReprocess()),
	CUSTOM_PRICE(ColorEntryGroup.CUSTOM, DataColors.get().customPrice()),
	CUSTOM_ASSET_NAME(ColorEntryGroup.CUSTOM, DataColors.get().customAssetName()),
	CUSTOM_USER_LOCATION(ColorEntryGroup.CUSTOM, DataColors.get().customUserLocation()),
	CONTRACTS_COURIER(ColorEntryGroup.CONTRACTS, DataColors.get().contractsCourier()),
	CONTRACTS_INCLUDED(ColorEntryGroup.CONTRACTS, DataColors.get().contractsIncluded()),
	CONTRACTS_EXCLUDED(ColorEntryGroup.CONTRACTS, DataColors.get().contractsExcluded()),
	OVERVIEW_GROUPED_LOCATIONS(ColorEntryGroup.OVERVIEW, DataColors.get().overviewGroupedLocations()),
	STOCKPILE_TABLE_BELOW_THRESHOLD(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileTableBelowThreshold()),
	STOCKPILE_ICON_BELOW_THRESHOLD(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileIconBelowThreshold(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	STOCKPILE_TABLE_BELOW_THRESHOLD_2ND(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileTableBelowThreshold2nd()),
	STOCKPILE_ICON_BELOW_THRESHOLD_2ND(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileIconBelowThreshold2nd(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	STOCKPILE_TABLE_OVER_THRESHOLD(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileTableOverThreshold()),
	STOCKPILE_ICON_OVER_THRESHOLD(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileIconOverThreshold(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	MARKET_ORDERS_OUTBID_NOT_BEST(ColorEntryGroup.MARKET_ORDERS, DataColors.get().marketOrdersOutbidNotBest()),
	MARKET_ORDERS_OUTBID_BEST(ColorEntryGroup.MARKET_ORDERS, DataColors.get().marketOrdersOutbidBest()),
	MARKET_ORDERS_OUTBID_UNKNOWN(ColorEntryGroup.MARKET_ORDERS, DataColors.get().marketOrdersOutbidUnknown()),
	MARKET_ORDERS_EXPIRED(ColorEntryGroup.MARKET_ORDERS, DataColors.get().marketOrdersExpired()),
	INDUSTRY_SLOTS_FREE(ColorEntryGroup.INDUSTRY_SLOTS, DataColors.get().industrySlotsFree()),
	INDUSTRY_SLOTS_DONE(ColorEntryGroup.INDUSTRY_SLOTS, DataColors.get().industrySlotsDone()),
	INDUSTRY_SLOTS_FULL(ColorEntryGroup.INDUSTRY_SLOTS, DataColors.get().industrySlotsFull()),
	TRANSACTIONS_BOUGHT(ColorEntryGroup.TRANSACTIONS, DataColors.get().transactionsBought()),
	TRANSACTIONS_SOLD(ColorEntryGroup.TRANSACTIONS, DataColors.get().transactionsSold()),
	GLOBAL_BPC(ColorEntryGroup.GLOBAL, DataColors.get().globalBPC()),
	GLOBAL_BPO(ColorEntryGroup.GLOBAL, DataColors.get().globalBPO()),
	GLOBAL_VALUE_NEGATIVE(ColorEntryGroup.GLOBAL, DataColors.get().globalValueNegative()),
	GLOBAL_VALUE_POSITIVE(ColorEntryGroup.GLOBAL, DataColors.get().globalValuePositive(), Background.NOT_EDITABLE, Foreground.NOT_EDITABLE),
	GLOBAL_ENTRY_INVALID(ColorEntryGroup.GLOBAL, DataColors.get().globalEntryInvalid()),
	GLOBAL_ENTRY_WARNING(ColorEntryGroup.GLOBAL, DataColors.get().globalEntryWarning()),
	GLOBAL_ENTRY_VALID(ColorEntryGroup.GLOBAL, DataColors.get().globalEntryValid()),
	GLOBAL_GRAND_TOTAL(ColorEntryGroup.GLOBAL, DataColors.get().globalGrandTotal(), Background.NOT_EDITABLE, Foreground.NOT_EDITABLE),
	GLOBAL_SELECTED_ROW_HIGHLIGHTING(ColorEntryGroup.GLOBAL, DataColors.get().globalSelectedRowHighlighting()),
	FILTER_OR_GROUP_1(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup1(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_2(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup2(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_3(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup3(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_4(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup4(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_5(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup5(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_6(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup6(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_7(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup7(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	REPROCESSED_SELL(ColorEntryGroup.REPROCESSED, DataColors.get().reprocessedSell(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	REPROCESSED_REPROCESS(ColorEntryGroup.REPROCESSED, DataColors.get().reprocessedReprocess(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	REPROCESSED_EQUAL(ColorEntryGroup.REPROCESSED, DataColors.get().reprocessedEqual(), Background.EDITABLE_AND_NOT_NULL, Foreground.NOT_EDITABLE),
	;

	private final ColorEntryGroup group;
	private final String description;
	private final Background backgroundType;
	private final Foreground foregroundType;

	private ColorEntry(ColorEntryGroup group, String description) {
		this(group, description, Background.EDITABLE_AND_NULLABLE, Foreground.EDITABLE_AND_NULLABLE);
	}

	private ColorEntry(ColorEntryGroup group, String description, Background backgroundType, Foreground foregroundType) {
		this.group = group;
		this.description = description;
		this.backgroundType = backgroundType;
		this.foregroundType = foregroundType;
	}

	public ColorEntryGroup getGroup() {
		return group;
	}

	public String getDescription() {
		return description;
	}

	public boolean isBackgroundEditable() {
		return backgroundType == Background.EDITABLE_AND_NULLABLE
			|| backgroundType == Background.EDITABLE_AND_NOT_NULL;
	}

	public boolean isBackgroundNullable() {
		return backgroundType == Background.EDITABLE_AND_NULLABLE;
	}

	public boolean isForegroundEditable() {
		return foregroundType == Foreground.EDITABLE_AND_NULLABLE
			|| foregroundType == Foreground.EDITABLE_AND_NOT_NULL;
	}

	public boolean isForegroundNullable() {
		return foregroundType == Foreground.EDITABLE_AND_NULLABLE;
	}

	private static enum Background {
		EDITABLE_AND_NULLABLE, EDITABLE_AND_NOT_NULL, NOT_EDITABLE
	}
	private static enum Foreground {
		EDITABLE_AND_NULLABLE, EDITABLE_AND_NOT_NULL, NOT_EDITABLE
	}
	public static enum ColorEntryGroup {
		ASSETS(DataColors.get().groupAssets()),
		CUSTOM(DataColors.get().groupCustom()),
		CONTRACTS(DataColors.get().groupContracts()),
		OVERVIEW(DataColors.get().groupOverview()),
		MARKET_ORDERS(DataColors.get().groupMarketOrders()),
		INDUSTRY_SLOTS(DataColors.get().groupIndustrySlots()),
		TRANSACTIONS(DataColors.get().groupTransactions()),
		GLOBAL(DataColors.get().groupGlobal()),
		FILTERS(DataColors.get().groupFilters()),
		REPROCESSED(DataColors.get().groupReprocessed()),
		STOCKPILE(DataColors.get().groupStockpile());
	
		private final String name;
		private ColorEntryGroup(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
