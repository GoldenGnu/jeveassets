/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

public abstract class TabsTransaction extends Bundle {

	public static TabsTransaction get() {
		return BundleServiceFactory.getBundleService().get(TabsTransaction.class);
	}

	public TabsTransaction(final Locale locale) {
		super(locale);
	}

	public abstract String bothAvg();
	public abstract String bothCount();
	public abstract String bothTitle();
	public abstract String bothTotal();
	public abstract String buy();
	public abstract String buyAvg();
	public abstract String buyCount();
	public abstract String buyTitle();
	public abstract String buyTotal();
	public abstract String corporation();
	public abstract String personal();
	public abstract String sell();
	public abstract String sellAvg();
	public abstract String sellCount();
	public abstract String sellTitle();
	public abstract String sellTotal();
	public abstract String title();
	public abstract String columnTransactionType();
	public abstract String columnName();
	public abstract String columnQuantity();
	public abstract String columnPrice();
	public abstract String columnClientName();
	public abstract String columnStationName();
	public abstract String columnRegion();
	public abstract String columnTransactionFor();
	public abstract String columnValue();
	public abstract String columnTransactionDate();
	public abstract String columnOwner();
	public abstract String columnLocation();
	public abstract String columnAccountKey();

}