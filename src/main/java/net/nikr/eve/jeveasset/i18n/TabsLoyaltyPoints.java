/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

public abstract class TabsLoyaltyPoints extends Bundle {

	public static TabsLoyaltyPoints get() {
		return BundleServiceFactory.getBundleService().get(TabsLoyaltyPoints.class);
	}

	public TabsLoyaltyPoints(final Locale locale) {
		super(locale);
	}

	public abstract String loyaltyPoints();
	public abstract String columnOwner();
	public abstract String columnCorporationName();
	public abstract String columnLoyaltyPoints();
	public abstract String fuzzworkLoyaltyPointsStore();
	public abstract String sell();
	public abstract String buy();
}
