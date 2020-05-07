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

import java.awt.Color;
import net.nikr.eve.jeveasset.i18n.DataColors;


public enum ColorEntry {
	ASSETS_REPROCESSING_EQUAL(ColorEntryGroup.ASSETS, DataColors.get().assetsReprocessingEqual(),  Colors.LIGHT_YELLOW.getColor()),
	ASSETS_REPROCESSING_REPROCES(ColorEntryGroup.ASSETS, DataColors.get().assetsReprocessingReproces(), Colors.LIGHT_RED.getColor()),
	ASSETS_REPROCESSING_SELL(ColorEntryGroup.ASSETS, DataColors.get().assetsReprocessingSell(), Colors.LIGHT_GREEN.getColor()),
	ASSETS_REPROCESS(ColorEntryGroup.ASSETS, DataColors.get().assetsReprocess(), Colors.LIGHT_YELLOW.getColor()),
	CUSTOM_PRICE(ColorEntryGroup.CUSTOM, DataColors.get().customPrice(), Colors.LIGHT_GRAY.getColor()),
	CUSTOM_ASSET_NAME(ColorEntryGroup.CUSTOM, DataColors.get().customAssetName(), Colors.LIGHT_GRAY.getColor()),
	CUSTOM_USER_LOCATION(ColorEntryGroup.CUSTOM, DataColors.get().customUserLocation(), Colors.LIGHT_GRAY.getColor()),
	CONTRACTS_COURIER(ColorEntryGroup.CONTRACTS, DataColors.get().contractsCourier(), Colors.LIGHT_YELLOW.getColor()),
	CONTRACTS_INCLUDED(ColorEntryGroup.CONTRACTS, DataColors.get().contractsIncluded(), Colors.LIGHT_GREEN.getColor()),
	CONTRACTS_EXCLUDED(ColorEntryGroup.CONTRACTS, DataColors.get().contractsExcluded(), Colors.LIGHT_RED.getColor()),
	OVERVIEW_GROUPED_LOCATIONS(ColorEntryGroup.OVERVIEW, DataColors.get().overviewGroupedLocations(), Colors.LIGHT_GREEN.getColor()),
	STOCKPILE_TABLE_BELOW_THRESHOLD(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileTableBelowThreshold(), Colors.LIGHT_RED.getColor()),
	STOCKPILE_ICON_BELOW_THRESHOLD(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileIconBelowThreshold(), Colors.LIGHT_RED.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	STOCKPILE_TABLE_BELOW_THRESHOLD_2ND(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileTableBelowThreshold2nd(), Colors.LIGHT_YELLOW.getColor()),
	STOCKPILE_ICON_BELOW_THRESHOLD_2ND(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileIconBelowThreshold2nd(), Colors.LIGHT_YELLOW.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	STOCKPILE_TABLE_OVER_THRESHOLD(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileTableOverThreshold(), Colors.LIGHT_GREEN.getColor()),
	STOCKPILE_ICON_OVER_THRESHOLD(ColorEntryGroup.STOCKPILE, DataColors.get().stockpileIconOverThreshold(), Colors.LIGHT_GREEN.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	MARKET_ORDERS_OUTBID_NOT_BEST(ColorEntryGroup.MARKET_ORDERS, DataColors.get().marketOrdersOutbidNotBest(), Colors.LIGHT_RED.getColor()),
	MARKET_ORDERS_OUTBID_BEST(ColorEntryGroup.MARKET_ORDERS, DataColors.get().marketOrdersOutbidBest(), Colors.LIGHT_GREEN.getColor()),
	MARKET_ORDERS_OUTBID_UNKNOWN(ColorEntryGroup.MARKET_ORDERS, DataColors.get().marketOrdersOutbidUnknown(), Colors.LIGHT_GRAY.getColor()),
	MARKET_ORDERS_EXPIRED(ColorEntryGroup.MARKET_ORDERS, DataColors.get().marketOrdersExpired(), Colors.LIGHT_RED.getColor()),
	TRANSACTIONS_BOUGHT(ColorEntryGroup.TRANSACTIONS, DataColors.get().transactionsBought(), Colors.LIGHT_RED.getColor()),
	TRANSACTIONS_SOLD(ColorEntryGroup.TRANSACTIONS, DataColors.get().transactionsSold(), Colors.LIGHT_GREEN.getColor()),
	GLOBAL_BPO(ColorEntryGroup.GLOBAL, DataColors.get().globalBPO(), Colors.LIGHT_YELLOW.getColor()),
	GLOBAL_VALUE_NEGATIVE(ColorEntryGroup.GLOBAL, DataColors.get().globalValueNegative(), null, Colors.DARK_RED.getColor()),
	GLOBAL_VALUE_POSITIVE(ColorEntryGroup.GLOBAL, DataColors.get().globalValuePositive(), null, Background.NOT_EDITABLE, Colors.DARK_GREEN.getColor(), Foreground.NOT_EDITABLE),
	GLOBAL_ENTRY_INVALID(ColorEntryGroup.GLOBAL, DataColors.get().globalEntryInvalid(), Colors.LIGHT_RED.getColor()),
	GLOBAL_ENTRY_WARNING(ColorEntryGroup.GLOBAL, DataColors.get().globalEntryWarning(), Colors.LIGHT_YELLOW.getColor()),
	GLOBAL_ENTRY_VALID(ColorEntryGroup.GLOBAL, DataColors.get().globalEntryValid(), Colors.LIGHT_GREEN.getColor()),
	GLOBAL_GRAND_TOTAL(ColorEntryGroup.GLOBAL, DataColors.get().globalGrandTotal(), Colors.LIGHT_GRAY.getColor(), Background.NOT_EDITABLE, null, Foreground.NOT_EDITABLE),
	GLOBAL_SELECTED_ROW_HIGHLIGHTING(ColorEntryGroup.GLOBAL, DataColors.get().globalSelectedRowHighlighting(), Colors.LIGHT_BLUE.getColor()),
	FILTER_OR_GROUP_1(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup1(), Colors.LIGHT_GRAY.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_2(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup2(), Colors.LIGHT_GREEN.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_3(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup3(), Colors.LIGHT_YELLOW.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_4(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup4(), Colors.LIGHT_ORANGE.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_5(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup5(), Colors.LIGHT_RED.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_6(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup6(), Colors.LIGHT_MAGENTA.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	FILTER_OR_GROUP_7(ColorEntryGroup.FILTERS, DataColors.get().filterOrGroup7(), Colors.LIGHT_BLUE.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	REPROCESSED_SELL(ColorEntryGroup.REPROCESSED, DataColors.get().reprocessedSell(), Colors.LIGHT_GREEN.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	REPROCESSED_REPROCESS(ColorEntryGroup.REPROCESSED, DataColors.get().reprocessedReprocess(), Colors.LIGHT_RED.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	REPROCESSED_EQUAL(ColorEntryGroup.REPROCESSED, DataColors.get().reprocessedEqual(), Colors.LIGHT_GRAY.getColor(), Background.EDITABLE_AND_NOT_NULL, null, Foreground.NOT_EDITABLE),
	;

	private final ColorEntryGroup group;
	private final String description;
	private final Color background;
	private final Background backgroundType;
	private final Color foreground;
	private final Foreground foregroundType;

	private ColorEntry(ColorEntryGroup group, String description, Color background) {
		this(group, description, background, Background.EDITABLE_AND_NULLABLE, null, Foreground.EDITABLE_AND_NULLABLE);
	}

	private ColorEntry(ColorEntryGroup group, String description, Color background, Color foreground) {
		this(group, description, background, Background.EDITABLE_AND_NULLABLE, foreground, Foreground.EDITABLE_AND_NULLABLE);
	}

	private ColorEntry(ColorEntryGroup group, String description, Color background, Background backgroundType, Color foreground, Foreground foregroundType) {
		this.group = group;
		this.description = description;
		this.background = background;
		this.backgroundType = backgroundType;
		this.foreground = foreground;
		this.foregroundType = foregroundType;
	}

	public ColorEntryGroup getGroup() {
		return group;
	}

	public String getDescription() {
		return description;
	}

	public Color getBackground() {
		return background;
	}

	public boolean isBackgroundEditable() {
		return backgroundType == Background.EDITABLE_AND_NULLABLE
			|| backgroundType == Background.EDITABLE_AND_NOT_NULL;
	}

	public boolean isBackgroundNullable() {
		return backgroundType == Background.EDITABLE_AND_NULLABLE;
	}

	public Color getForeground() {
		return foreground;
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
