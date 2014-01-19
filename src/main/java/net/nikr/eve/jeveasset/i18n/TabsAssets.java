/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.Main;
import uk.me.candle.translations.Bundle;

public abstract class TabsAssets extends Bundle {
	public static TabsAssets get() {
		return Main.getBundleService().get(TabsAssets.class);
	}

	public TabsAssets(final Locale locale) {
		super(locale);
	}

	public abstract String assets();
	public abstract String average();
	public abstract String totalCount();
	public abstract String totalReprocessed();
	public abstract String totalValue();
	public abstract String totalVolume();
	public abstract String columnName();
	public abstract String columnTags();
	public abstract String columnGroup();
	public abstract String columnCategory();
	public abstract String columnOwner();
	public abstract String columnLocation();
	public abstract String columnSecurity();
	public abstract String columnRegion();
	public abstract String columnContainer();
	public abstract String columnFlag();
	public abstract String columnPrice();
	public abstract String columnPriceSellMin();
	public abstract String columnPriceBuyMax();
	public abstract String columnPriceReprocessed();
	public abstract String columnMarketOrderLatest();
	public abstract String columnMarketOrderAverage();
	public abstract String columnMarketOrderMaximum();
	public abstract String columnMarketOrderMinimum();
	public abstract String columnPriceBase();
	public abstract String columnPriceReprocessedDifference();
	public abstract String columnPriceReprocessedPercent();
	public abstract String columnValueReprocessed();
	public abstract String columnValue();
	public abstract String columnValuePerVolume();
	public abstract String columnCount();
	public abstract String columnTypeCount();
	public abstract String columnMeta();
	public abstract String columnTech();
	public abstract String columnVolume();
	public abstract String columnVolumeTotal();
	public abstract String columnSingleton();
	public abstract String columnAdded();
	public abstract String columnItemID();
	public abstract String columnTypeID();
}
