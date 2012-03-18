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

public abstract class TabsRouting extends Bundle {
	public static TabsRouting get() {
		return BundleCache.get(TabsRouting.class);
	}

	public static TabsRouting get(Locale locale) {
		return BundleCache.get(TabsRouting.class, locale);
	}

	public TabsRouting(Locale locale) {
		super(locale);
	}

	public abstract String a(Object arg0, Object arg1);
	public abstract String add();
	public abstract String all();
	public abstract String algorithm();
	public abstract String calculate();
	public abstract String cancel();
	public abstract String error();
	public abstract String filteredAssets();
	public abstract String generating();
	public abstract String not();
	public abstract String once();
	public abstract String overviewGroup(Object arg0);
	public abstract String route();
	public abstract String route1();
	public abstract String routing();
	public abstract String routing1();
	public abstract String second(int time);
	public abstract String source();
	public abstract String there();
	public abstract String unknown(Object arg0);
	public abstract String whitespace();
	public abstract String whitespace1();
	public abstract String whitespace2(Object arg0, Object arg1);
	public abstract String whitespace3(Object arg0, Object arg1);
	public abstract String whitespace4(Object arg0, Object arg1);
	public abstract String whitespace5();
}
