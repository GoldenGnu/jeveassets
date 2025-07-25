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

public abstract class TabsTree extends Bundle {

	public static TabsTree get() {
		return BundleServiceFactory.getBundleService().get(TabsTree.class);
	}

	public TabsTree(final Locale locale) {
		super(locale);
	}

	public abstract String categories();
	public abstract String collapse();
	public abstract String expand();
	public abstract String locationAssetSafety();
	public abstract String locationClones();
	public abstract String locationContracts();
	public abstract String locationDeliveries();
	public abstract String locationItemHangar();
	public abstract String locationMarketOrders();
	public abstract String locationShipHangar();
	public abstract String locations();
	public abstract String title();
}
