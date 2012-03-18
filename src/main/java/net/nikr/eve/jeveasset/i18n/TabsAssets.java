/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import uk.me.candle.translations.BundleCache;

public abstract class TabsAssets extends Bundle {
	public static TabsAssets get() {
		return BundleCache.get(TabsAssets.class);
	}

	public static TabsAssets get(Locale locale) {
		return BundleCache.get(TabsAssets.class, locale);
	}

	public TabsAssets(Locale locale) {
		super(locale);
	}

	public abstract String assets();
	public abstract String average();
	public abstract String average1();
	public abstract String count();
	public abstract String selection();
	public abstract String total();
	public abstract String total1();
	public abstract String total2();
	public abstract String value();
	public abstract String volume();
	public abstract String columnName();
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
	public abstract String columnPriceBase();
	public abstract String columnValueReprocessed();
	public abstract String columnValue();
	public abstract String columnCount();
	public abstract String columnTypeCount();
	public abstract String columnMeta();
	public abstract String columnVolume();
	public abstract String columnVolumeTotal();
	public abstract String columnSingleton();
	public abstract String columnItemID();
	public abstract String columnTypeID();
}
