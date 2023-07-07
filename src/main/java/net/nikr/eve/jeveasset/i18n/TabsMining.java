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


public abstract class TabsMining extends Bundle {

	public static TabsMining get() {
		return BundleServiceFactory.getBundleService().get(TabsMining.class);
	}

	public TabsMining(final Locale locale) {
		super(locale);
	}

	public abstract String average();
	public abstract String character();
	public abstract String corporation();
	public abstract String count();
	public abstract String extractions();
	public abstract String extractionsActiveSoon();
	public abstract String from();
	public abstract String grandTotal();
	public abstract String graphToolTip(Comparable<?> name, String isk, String date);
	public abstract String groupBasic(String group);
	public abstract String groupName(String group, String type);
	public abstract String groupTotal(String group);
	public abstract String includeZero();
	public abstract String miningGraph();
	public abstract String miningLog();
	public abstract String scaleLinear();
	public abstract String scaleLogarithmic();
	public abstract String show();
	public abstract String to();
	public abstract String totalCount();
	public abstract String totalReprocessed();
	public abstract String totalValue();
	public abstract String totalVolume();
	public abstract String valueOre();
	public abstract String valueReprocessed();
	public abstract String valueReprocessedMax();
	public abstract String volume();
	public abstract String columnDate();
	public abstract String columnName();
	public abstract String columnGroup();
	public abstract String columnCategory();
	public abstract String columnOwner();
	public abstract String columnLocation();
	public abstract String columnCount();
	public abstract String columnPriceOre();
	public abstract String columnPriceReprocessed();
	public abstract String columnPriceReprocessedToolTip();
	public abstract String columnPriceReprocessedMax();
	public abstract String columnPriceReprocessedMaxToolTip();
	public abstract String columnValueOre();
	public abstract String columnValueReprocessed();
	public abstract String columnValueReprocessedToolTip();
	public abstract String columnValueReprocessedMax();
	public abstract String columnValueReprocessedMaxToolTip();
	public abstract String columnVolume();
	public abstract String columnValuePerVolumeOre();
	public abstract String columnValuePerVolumeReprocessed();
	public abstract String columnValuePerVolumeReprocessedToolTip();
	public abstract String columnValuePerVolumeReprocessedMax();
	public abstract String columnValuePerVolumeReprocessedMaxToolTip();
	public abstract String columnCorporation();
	public abstract String columnForCorporation();
	public abstract String columnExtractionStartTime();
	public abstract String columnMoon();
	public abstract String columnChunkArrivalTime();
	public abstract String columnNaturalDecayTime();
}