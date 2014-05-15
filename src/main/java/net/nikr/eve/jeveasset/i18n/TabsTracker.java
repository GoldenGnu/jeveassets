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


public abstract class TabsTracker extends Bundle {
	public static TabsTracker get() {
		return Main.getBundleService().get(TabsTracker.class);
	}

	public TabsTracker(final Locale locale) {
		super(locale);
	}

	public abstract String all();
	public abstract String assets();
	public abstract String autoZoom();
	public abstract String cancel();
	public abstract String clear();
	public abstract String date();
	public abstract String delete();
	public abstract String deleteSelected();
	public abstract String edit();
	public abstract String empty();
	public abstract String error();
	public abstract String escrows();
	public abstract String escrowsToCover();
	public abstract String from();
	public abstract String grandTotal();
	public abstract String help();
	public abstract String invalid();
	public abstract String isk();
	public abstract String manufacturing();
	public abstract String month1();
	public abstract String months3();
	public abstract String months6();
	public abstract String noDataFound();
	public abstract String ok();
	public abstract String quickDate();
	public abstract String sellOrders();
	public abstract String title();
	public abstract String to();
	public abstract String today();
	public abstract String total();
	public abstract String walletBalance();
	public abstract String year1();
	public abstract String years2();
	
}
