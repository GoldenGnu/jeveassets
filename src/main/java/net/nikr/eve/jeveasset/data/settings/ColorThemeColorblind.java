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

import java.util.Map;
import net.nikr.eve.jeveasset.i18n.DataColors;


public class ColorThemeColorblind extends ColorTheme {

	protected ColorThemeColorblind() {}

	@Override
	public String getName() {
		return DataColors.get().colorThemeColorblind();
	}

	@Override
	public ColorThemeTypes getType() {
		return ColorThemeTypes.COLORBLIND;
	}

	@Override
	protected void createColors(Map<ColorEntry, ColorTheme.ColorThemeEntry> colors) {
		colors.put(ColorEntry.ASSETS_REPROCESSING_EQUAL, new ColorTheme.ColorThemeEntry(Colors.LIGHT_YELLOW_STRONG));
		colors.put(ColorEntry.ASSETS_REPROCESSING_REPROCES, new ColorTheme.ColorThemeEntry(Colors.LIGHT_RED_STRONG));
		colors.put(ColorEntry.ASSETS_REPROCESSING_SELL, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GREEN_STRONG));
		colors.put(ColorEntry.ASSETS_REPROCESS, new ColorTheme.ColorThemeEntry(Colors.LIGHT_YELLOW_STRONG));
		colors.put(ColorEntry.CUSTOM_PRICE, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GRAY_STRONG));
		colors.put(ColorEntry.CUSTOM_ASSET_NAME, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GRAY_STRONG));
		colors.put(ColorEntry.CUSTOM_USER_LOCATION, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GRAY_STRONG));
		colors.put(ColorEntry.CONTRACTS_COURIER, new ColorTheme.ColorThemeEntry(Colors.LIGHT_YELLOW_STRONG));
		colors.put(ColorEntry.CONTRACTS_INCLUDED, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GREEN_STRONG));
		colors.put(ColorEntry.CONTRACTS_EXCLUDED, new ColorTheme.ColorThemeEntry(Colors.LIGHT_RED_STRONG));
		colors.put(ColorEntry.OVERVIEW_GROUPED_LOCATIONS, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GREEN_STRONG));
		colors.put(ColorEntry.STOCKPILE_TABLE_BELOW_THRESHOLD, new ColorTheme.ColorThemeEntry(Colors.LIGHT_RED_STRONG));
		colors.put(ColorEntry.STOCKPILE_ICON_BELOW_THRESHOLD, new ColorTheme.ColorThemeEntry(Colors.LIGHT_RED_STRONG));
		colors.put(ColorEntry.STOCKPILE_TABLE_BELOW_THRESHOLD_2ND, new ColorTheme.ColorThemeEntry(Colors.LIGHT_YELLOW_STRONG));
		colors.put(ColorEntry.STOCKPILE_ICON_BELOW_THRESHOLD_2ND, new ColorTheme.ColorThemeEntry(Colors.LIGHT_YELLOW_STRONG));
		colors.put(ColorEntry.STOCKPILE_TABLE_OVER_THRESHOLD, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GREEN_STRONG));
		colors.put(ColorEntry.STOCKPILE_ICON_OVER_THRESHOLD, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GREEN_STRONG));
		colors.put(ColorEntry.MARKET_ORDERS_OUTBID_NOT_BEST, new ColorTheme.ColorThemeEntry(Colors.LIGHT_RED_STRONG));
		colors.put(ColorEntry.MARKET_ORDERS_OUTBID_BEST, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GREEN_STRONG));
		colors.put(ColorEntry.MARKET_ORDERS_OUTBID_UNKNOWN, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GRAY_STRONG));
		colors.put(ColorEntry.MARKET_ORDERS_EXPIRED, new ColorTheme.ColorThemeEntry(Colors.LIGHT_RED_STRONG));
		colors.put(ColorEntry.TRANSACTIONS_BOUGHT, new ColorTheme.ColorThemeEntry(Colors.LIGHT_RED_STRONG));
		colors.put(ColorEntry.TRANSACTIONS_SOLD, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GREEN_STRONG));
		colors.put(ColorEntry.GLOBAL_BPO, new ColorTheme.ColorThemeEntry(Colors.LIGHT_YELLOW_STRONG));
		colors.put(ColorEntry.GLOBAL_VALUE_NEGATIVE, new ColorTheme.ColorThemeEntry(null, Colors.DARK_RED));
		colors.put(ColorEntry.GLOBAL_VALUE_POSITIVE, new ColorTheme.ColorThemeEntry(null, Colors.DARK_GREEN));
		colors.put(ColorEntry.GLOBAL_ENTRY_INVALID, new ColorTheme.ColorThemeEntry(Colors.LIGHT_RED_STRONG));
		colors.put(ColorEntry.GLOBAL_ENTRY_WARNING, new ColorTheme.ColorThemeEntry(Colors.LIGHT_YELLOW_STRONG));
		colors.put(ColorEntry.GLOBAL_ENTRY_VALID, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GREEN_STRONG));
		colors.put(ColorEntry.GLOBAL_GRAND_TOTAL, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GRAY_STRONG));
		colors.put(ColorEntry.GLOBAL_SELECTED_ROW_HIGHLIGHTING, new ColorTheme.ColorThemeEntry(Colors.LIGHT_BLUE_STRONG));
		colors.put(ColorEntry.FILTER_OR_GROUP_1, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GRAY_STRONG));
		colors.put(ColorEntry.FILTER_OR_GROUP_2, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GREEN_STRONG));
		colors.put(ColorEntry.FILTER_OR_GROUP_3, new ColorTheme.ColorThemeEntry(Colors.LIGHT_YELLOW_STRONG));
		colors.put(ColorEntry.FILTER_OR_GROUP_4, new ColorTheme.ColorThemeEntry(Colors.LIGHT_ORANGE_STRONG));
		colors.put(ColorEntry.FILTER_OR_GROUP_5, new ColorTheme.ColorThemeEntry(Colors.LIGHT_RED_STRONG));
		colors.put(ColorEntry.FILTER_OR_GROUP_6, new ColorTheme.ColorThemeEntry(Colors.LIGHT_MAGENTA_STRONG));
		colors.put(ColorEntry.FILTER_OR_GROUP_7, new ColorTheme.ColorThemeEntry(Colors.LIGHT_BLUE_STRONG));
		colors.put(ColorEntry.REPROCESSED_SELL, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GREEN_STRONG));
		colors.put(ColorEntry.REPROCESSED_REPROCESS, new ColorTheme.ColorThemeEntry(Colors.LIGHT_RED_STRONG));
		colors.put(ColorEntry.REPROCESSED_EQUAL, new ColorTheme.ColorThemeEntry(Colors.LIGHT_GRAY_STRONG));
	}
}
