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

	public abstract String character();
	public abstract String corporation();
	public abstract String mining();
	public abstract String columnName();
	public abstract String columnGroup();
	public abstract String columnCategory();
	public abstract String columnOwner();
	public abstract String columnLocation();
	public abstract String columnCount();
	public abstract String columnOrePrice();
	public abstract String columnOreValue();
	public abstract String columnSkillMineralsPrice();
	public abstract String columnSkillMineralsPriceToolTip();
	public abstract String columnSkillsMineralsValue();
	public abstract String columnSkillsMineralsValueToolTip();
	public abstract String columnMaxMineralsPrice();
	public abstract String columnMaxMineralsPriceToolTip();
	public abstract String columnMaxMineralsValue();
	public abstract String columnMaxMineralsValueToolTip();
	public abstract String columnCorporation();
	public abstract String columnForCorporation();
}