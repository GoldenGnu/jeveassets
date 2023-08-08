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
package net.nikr.eve.jeveasset.data.settings;

import java.util.Map;
import net.nikr.eve.jeveasset.i18n.DataColors;


public class ColorThemeDefault extends ColorTheme {

	protected ColorThemeDefault() { }

	@Override
	public String getName() {
		return DataColors.get().colorThemeDefault();
	}

	@Override
	public ColorThemeTypes getType() {
		return ColorThemeTypes.DEFAULT;
	}

	@Override
	protected void createColors(Map<ColorEntry, ColorThemeEntry> colors) {
		colors.put(ColorEntry.ASSETS_NEW, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.ASSETS_REPROCESSING_EQUAL, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.ASSETS_REPROCESSING_REPROCES, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.ASSETS_REPROCESSING_SELL, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.ASSETS_REPROCESS, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.CUSTOM_PRICE, new ColorThemeEntry(Colors.LIGHT_GRAY));
		colors.put(ColorEntry.CUSTOM_ASSET_NAME, new ColorThemeEntry(Colors.LIGHT_GRAY));
		colors.put(ColorEntry.CUSTOM_USER_LOCATION, new ColorThemeEntry(Colors.LIGHT_GRAY));
		colors.put(ColorEntry.CONTRACTS_COURIER, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.CONTRACTS_INCLUDED, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.CONTRACTS_EXCLUDED, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.EXTRACTIONS_DAYS, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.EXTRACTIONS_WEEK, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.EXTRACTIONS_WEEKS, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.OVERVIEW_GROUPED_LOCATIONS, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.STOCKPILE_TABLE_BELOW_THRESHOLD, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.STOCKPILE_ICON_BELOW_THRESHOLD, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.STOCKPILE_TABLE_BELOW_THRESHOLD_2ND, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.STOCKPILE_ICON_BELOW_THRESHOLD_2ND, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.STOCKPILE_TABLE_OVER_THRESHOLD, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.STOCKPILE_ICON_OVER_THRESHOLD, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.MARKET_ORDERS_OUTBID_NOT_BEST, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.MARKET_ORDERS_OUTBID_BEST, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.MARKET_ORDERS_OUTBID_UNKNOWN, new ColorThemeEntry(Colors.LIGHT_GRAY));
		colors.put(ColorEntry.MARKET_ORDERS_EXPIRED, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.MARKET_ORDERS_NEAR_EXPIRED, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.MARKET_ORDERS_NEAR_FILLED, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.MARKET_ORDERS_NEW, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.INDUSTRY_JOBS_DELIVERED, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.INDUSTRY_JOBS_DONE, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.INDUSTRY_JOBS_ACTIVITY_MANUFACTURING, new ColorThemeEntry(Colors.LIGHT_ORANGE));
		colors.put(ColorEntry.INDUSTRY_JOBS_ACTIVITY_RESEARCHING_TECHNOLOGY, new ColorThemeEntry(Colors.LIGHT_BLUE));
		colors.put(ColorEntry.INDUSTRY_JOBS_ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY, new ColorThemeEntry(Colors.LIGHT_BLUE));
		colors.put(ColorEntry.INDUSTRY_JOBS_ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY, new ColorThemeEntry(Colors.LIGHT_BLUE));
		colors.put(ColorEntry.INDUSTRY_JOBS_ACTIVITY_COPYING, new ColorThemeEntry(Colors.LIGHT_BLUE));
		colors.put(ColorEntry.INDUSTRY_JOBS_ACTIVITY_DUPLICATING, new ColorThemeEntry(Colors.LIGHT_BLUE));
		colors.put(ColorEntry.INDUSTRY_JOBS_ACTIVITY_REVERSE_ENGINEERING, new ColorThemeEntry(Colors.LIGHT_BLUE));
		colors.put(ColorEntry.INDUSTRY_JOBS_ACTIVITY_REVERSE_INVENTION, new ColorThemeEntry(Colors.LIGHT_BLUE));
		colors.put(ColorEntry.INDUSTRY_JOBS_ACTIVITY_REACTIONS, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.INDUSTRY_SLOTS_FREE, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.INDUSTRY_SLOTS_DONE, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.INDUSTRY_SLOTS_FULL, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.JOURNAL_NEW, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.TRANSACTIONS_BOUGHT, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.TRANSACTIONS_SOLD, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.TRANSACTIONS_NEW, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.GLOBAL_BPC, new ColorThemeEntry(Colors.LIGHT_MAGENTA));
		colors.put(ColorEntry.GLOBAL_BPO, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.GLOBAL_VALUE_NEGATIVE, new ColorThemeEntry(null, Colors.FOREGROUND_RED));
		colors.put(ColorEntry.GLOBAL_VALUE_POSITIVE, new ColorThemeEntry(null, Colors.FOREGROUND_GREEN));
		colors.put(ColorEntry.GLOBAL_ENTRY_INVALID, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.GLOBAL_ENTRY_WARNING, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.GLOBAL_ENTRY_VALID, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.GLOBAL_GRAND_TOTAL, new ColorThemeEntry(Colors.LIGHT_GRAY));
		colors.put(ColorEntry.GLOBAL_SELECTED_ROW_HIGHLIGHTING, new ColorThemeEntry(Colors.LIGHT_BLUE));
		colors.put(ColorEntry.FILTER_OR_GROUP_1, new ColorThemeEntry(Colors.LIGHT_GRAY));
		colors.put(ColorEntry.FILTER_OR_GROUP_2, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.FILTER_OR_GROUP_3, new ColorThemeEntry(Colors.LIGHT_YELLOW));
		colors.put(ColorEntry.FILTER_OR_GROUP_4, new ColorThemeEntry(Colors.LIGHT_ORANGE));
		colors.put(ColorEntry.FILTER_OR_GROUP_5, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.FILTER_OR_GROUP_6, new ColorThemeEntry(Colors.LIGHT_MAGENTA));
		colors.put(ColorEntry.FILTER_OR_GROUP_7, new ColorThemeEntry(Colors.LIGHT_BLUE));
		colors.put(ColorEntry.REPROCESSED_SELL, new ColorThemeEntry(Colors.LIGHT_GREEN));
		colors.put(ColorEntry.REPROCESSED_REPROCESS, new ColorThemeEntry(Colors.LIGHT_RED));
		colors.put(ColorEntry.REPROCESSED_EQUAL, new ColorThemeEntry(Colors.LIGHT_GRAY));
	}
}
