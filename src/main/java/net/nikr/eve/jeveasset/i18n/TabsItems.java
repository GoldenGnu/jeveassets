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

public abstract class TabsItems extends Bundle {
	public static TabsItems get() {
		return Main.getBundleService().get(TabsItems.class);
	}

	public TabsItems(final Locale locale) {
		super(locale);
	}

	public abstract String items();
	public abstract String columnName();
	public abstract String columnGroup();
	public abstract String columnCategory();
	public abstract String columnPriceBase();
	public abstract String columnPriceReprocessed();
	public abstract String columnMeta();
	public abstract String columnTech();
	public abstract String columnVolume();
	public abstract String columnTypeID();
}

