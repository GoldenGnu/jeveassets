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

public abstract class TabsValues extends Bundle {
	public static TabsValues get() {
		return Main.getBundleService().get(TabsValues.class);
	}

	public TabsValues(final Locale locale) {
		super(locale);
	}

	public abstract String columnAssets();
	public abstract String columnBestAsset();
	public abstract String columnBestModule();
	public abstract String columnBestShip();
	public abstract String columnBestShipFitted();
	public abstract String columnEscrows();
	public abstract String columnEscrowsToCover();
	public abstract String columnManufacturing();
	public abstract String columnOwner();
	public abstract String columnSellOrders();
	public abstract String columnTotal();
	public abstract String columnWalletBalance();
	public abstract String grandTotal();
	public abstract String none();
	public abstract String title();

	public abstract String oldTitle();
	public abstract String oldNoCharacter();
	public abstract String oldNoCorporation();

}
