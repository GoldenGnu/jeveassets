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


public abstract class TabsReprocessed extends Bundle {

	public static TabsReprocessed get() {
		return BundleServiceFactory.getBundleService().get(TabsReprocessed.class);
	}

	public TabsReprocessed(final Locale locale) {
		super(locale);
	}

	public abstract String add();
	public abstract String batch();
	public abstract String removeAll();
	public abstract String collapse();
	public abstract String columnName();
	public abstract String columnPrice();
	public abstract String columnQuantityMax();
	public abstract String columnQuantitySkill();
	public abstract String columnTotalBatch();
	public abstract String columnTotalName();
	public abstract String columnTotalPrice();
	public abstract String columnTotalValue();
	public abstract String columnTypeID();
	public abstract String columnValueDifference();
	public abstract String columnValueMax();
	public abstract String columnValueSkill();
	public abstract String expand();
	public abstract String grandTotal();
	public abstract String info();
	public abstract String price();
	public abstract String remove();
	public abstract String set();
	public abstract String title();
	public abstract String total();
	public abstract String value();
	
}
