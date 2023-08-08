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

package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;

public abstract class TabsAssets extends Bundle {

	public static TabsAssets get() {
		return BundleServiceFactory.getBundleService().get(TabsAssets.class);
	}

	public TabsAssets(final Locale locale) {
		super(locale);
	}

	public abstract String assets();
	public abstract String average();
	public abstract String reprocessColors();
	public abstract String reprocessColorsToolTip();
	public abstract String totalCount();
	public abstract String totalReprocessed();
	public abstract String totalValue();
	public abstract String totalVolume();
	public abstract String clearNew();
	public abstract String columnName();
	public abstract String columnNameType();
	public abstract String columnNameCustom();
	public abstract String columnTags();
	public abstract String columnGroup();
	public abstract String columnCategory();
	public abstract String columnSlot();
	public abstract String columnChargeSize();
	public abstract String columnOwner();
	public abstract String columnLocation();
	public abstract String columnSecurity();
	public abstract String columnSystem();
	public abstract String columnConstellation();
	public abstract String columnRegion();
	public abstract String columnFactionWarfareSystemOwner();
	public abstract String columnFactionWarfareSystemOwnerToolTip();
	public abstract String columnContainer();
	public abstract String columnFlag();
	public abstract String columnPrice();
	public abstract String columnPriceToolTip();
	public abstract String columnPriceSellMin();
	public abstract String columnPriceSellMinToolTip();
	public abstract String columnPriceBuyMax();
	public abstract String columnPriceBuyMaxToolTip();
	public abstract String columnPriceReprocessed();
	public abstract String columnPriceReprocessedToolTip();
	public abstract String columnPriceManufacturing();
	public abstract String columnPriceManufacturingToolTip();
	public abstract String columnTransactionPriceLatest();
	public abstract String columnTransactionPriceLatestToolTip();
	public abstract String columnTransactionPriceAverage();
	public abstract String columnTransactionPriceAverageToolTip();
	public abstract String columnTransactionPriceMaximum();
	public abstract String columnTransactionPriceMaximumToolTip();
	public abstract String columnTransactionPriceMinimum();
	public abstract String columnTransactionPriceMinimumToolTip();
	public abstract String columnPriceBase();
	public abstract String columnPriceBaseToolTip();
	public abstract String columnPriceReprocessedDifference();
	public abstract String columnPriceReprocessedDifferenceToolTip();
	public abstract String columnPriceReprocessedPercent();
	public abstract String columnPriceReprocessedPercentToolTip();
	public abstract String columnValueReprocessed();
	public abstract String columnValueReprocessedToolTip();
	public abstract String columnValue();
	public abstract String columnValueToolTip();
	public abstract String columnValuePerVolume();
	public abstract String columnCount();
	public abstract String columnTypeCount();
	public abstract String columnTypeCountToolTip();
	public abstract String columnMeta();
	public abstract String columnTech();
	public abstract String columnVolume();
	public abstract String columnVolumeTotal();
	public abstract String columnVolumeTotalToolTip();
	public abstract String columnSingleton();
	public abstract String columnAdded();
	public abstract String columnAddedToolTip();
	public abstract String columnMaterialEfficiency();
	public abstract String columnMaterialEfficiencyToolTip();
	public abstract String columnTimeEfficiency();
	public abstract String columnTimeEfficiencyToolTip();
	public abstract String columnRuns();
	public abstract String columnRunsToolTip();
	public abstract String columnStructure();
	public abstract String columnStructureToolTip();
	public abstract String columnItemID();
	public abstract String columnItemIDToolTip();
	public abstract String columnLocationID();
	public abstract String columnLocationIDToolTip();
	public abstract String columnTypeID();
	public abstract String columnTypeIDToolTip();
}
