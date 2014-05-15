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

public abstract class TabsMaterials extends Bundle {
	public static TabsMaterials get() {
		return Main.getBundleService().get(TabsMaterials.class);
	}

	public TabsMaterials(final Locale locale) {
		super(locale);
	}

	public abstract String collapse();
	public abstract String columnCount();
	public abstract String columnGroup();
	public abstract String columnLocation();
	public abstract String columnName();
	public abstract String columnPrice();
	public abstract String columnValue();
	public abstract String expand();
	public abstract String grandTotal();
	public abstract String includePI();
	public abstract String materials();
	public abstract String no();
	public abstract String summary();
	public abstract String total();
}
