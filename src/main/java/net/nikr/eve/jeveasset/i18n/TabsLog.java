/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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


public abstract class TabsLog extends Bundle {

	public static TabsLog get() {
		return BundleServiceFactory.getBundleService().get(TabsLog.class);
	}

	public TabsLog(final Locale locale) {
		super(locale);
	}

	public abstract String toolTitle();
	public abstract String collapse();
	public abstract String expand();
	public abstract String columnDate();
	public abstract String columnCount();
	public abstract String columnAction();
	public abstract String columnSource();
	public abstract String columnLocation();
	public abstract String columnMatch();
}