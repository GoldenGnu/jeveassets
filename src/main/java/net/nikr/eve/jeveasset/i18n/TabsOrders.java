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

public abstract class TabsOrders extends Bundle {
	public static TabsOrders get() {
		return BundleCache.get(TabsOrders.class);
	}

	public static TabsOrders get(Locale locale) {
		return BundleCache.get(TabsOrders.class, locale);
	}

	public TabsOrders(Locale locale) {
		super(locale);
	}

	public abstract String buy();
	public abstract String buy1();
	public abstract String character();
	public abstract String columnName();
	public abstract String columnQuantity();
	public abstract String columnPrice();
	public abstract String columnIssued();
	public abstract String columnExpiresIn();
	public abstract String columnRange();
	public abstract String columnRemainingValue();
	public abstract String columnStatus();
	public abstract String columnMinVolume();
	public abstract String columnOwner();
	public abstract String columnLocation();
	public abstract String columnRegion();
	public abstract String market();
	public abstract String no();
	public abstract String sell();
	public abstract String sell1();
	public abstract String state();
	public abstract String whitespace(Object arg0);
}
