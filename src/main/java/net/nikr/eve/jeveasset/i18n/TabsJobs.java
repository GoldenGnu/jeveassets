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

public abstract class TabsJobs extends Bundle {
	public static TabsJobs get() {
		return BundleCache.get(TabsJobs.class);
	}

	public static TabsJobs get(Locale locale) {
		return BundleCache.get(TabsJobs.class, locale);
	}

	public TabsJobs(Locale locale) {
		super(locale);
	}

	public abstract String all();
	public abstract String industry();
	public abstract String install();
	public abstract String no();
	public abstract String whitespace(Object arg0);
	public abstract String columnState();
	public abstract String columnActivity();
	public abstract String columnName();
	public abstract String columnOwner();
	public abstract String columnLocation();
	public abstract String columnRegion();
	public abstract String columnInstallDate();
	public abstract String columnStartDate();
	public abstract String columnEndDate();
	public abstract String columnBpMe();
	public abstract String columnBpPe();
}
