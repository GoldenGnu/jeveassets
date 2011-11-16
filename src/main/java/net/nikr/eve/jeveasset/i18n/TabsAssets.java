/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

	public abstract String addFilter();
	public abstract String addField();
	public abstract String addSystem();
	public abstract String assets();
	public abstract String average();
	public abstract String average1();
	public abstract String can();
	public abstract String cancel();
	public abstract String clear();
	public abstract String count();
	public abstract String delete();
	public abstract String delete2(Object arg0);
	public abstract String delete3();
	public abstract String done();
	public abstract String empty();
	public abstract String enter();
	public abstract String filter();
	public abstract String load();
	public abstract String load1();
	public abstract String manage();
	public abstract String nOfyAssets(int rowCount, int size, String filterName);
	public abstract String nothing();
	public abstract String overwrite();
	public abstract String overwrite1();
	public abstract String overwrite2();
	public abstract String rename();
	public abstract String rename1();
	public abstract String save();
	public abstract String save1();
	public abstract String selection();
	public abstract String total();
	public abstract String total1();
	public abstract String total2();
	public abstract String value();
	public abstract String volume();
	public abstract String untitled();
	public abstract String you();
}
