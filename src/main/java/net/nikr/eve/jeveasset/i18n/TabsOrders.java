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

public abstract class TabsOrders extends Bundle {
	public static TabsOrders get() {
		return Main.getBundleService().get(TabsOrders.class);
	}

	public TabsOrders(final Locale locale) {
		super(locale);
	}

	public abstract String activeBuyOrders();
	public abstract String activeSellOrders();
	public abstract String buy();
	public abstract String columnOrderType();
	public abstract String columnName();
	public abstract String columnQuantity();
	public abstract String columnPrice();
	public abstract String columnIssued();
	public abstract String columnExpires();
	public abstract String columnRange();
	public abstract String columnRemainingValue();
	public abstract String columnStatus();
	public abstract String columnMinVolume();
	public abstract String columnOwner();
	public abstract String columnLocation();
	public abstract String columnRegion();
	public abstract String market();
	public abstract String rangeStation();
	public abstract String rangeSolarSystem();
	public abstract String rangeRegion();
	public abstract String rangeJump();
	public abstract String rangeJumps(int range);
	public abstract String sell();
	public abstract String statusActive();
	public abstract String statusClosed();
	public abstract String statusFulfilled();
	public abstract String statusExpired();
	public abstract String statusPartiallyFulfilled();
	public abstract String statusCancelled();
	public abstract String statusPending();
	public abstract String statusCharacterDeleted();
	public abstract String totalSellOrders();
	public abstract String totalBuyOrders();
	public abstract String totalEscrow();
	public abstract String totalToCover();
	public abstract String whitespace(Object arg0);
}
