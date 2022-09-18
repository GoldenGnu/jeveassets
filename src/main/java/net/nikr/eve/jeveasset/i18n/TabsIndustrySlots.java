/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

public abstract class TabsIndustrySlots extends Bundle {

	public static TabsIndustrySlots get() {
		return BundleServiceFactory.getBundleService().get(TabsIndustrySlots.class);
	}

	public TabsIndustrySlots(final Locale locale) {
		super(locale);
	}

	public abstract String grandTotal();
	public abstract String manufacturing();
	public abstract String reactions();
	public abstract String research();
	public abstract String title();
	public abstract String columnOwner();
	public abstract String columnManufacturingDone();
	public abstract String columnManufacturingFree();
	public abstract String columnManufacturingActive();
	public abstract String columnManufacturingMax();
	public abstract String columnResearchDone();
	public abstract String columnResearchFree();
	public abstract String columnResearchActive();
	public abstract String columnResearchMax();
	public abstract String columnReactionsFree();
	public abstract String columnReactionsDone();
	public abstract String columnReactionsActive();
	public abstract String columnReactionsMax();
	public abstract String columnCurrentShip();
	public abstract String columnCurrentStation();
	public abstract String columnCurrentSystem();
	public abstract String columnCurrentConstellation();
	public abstract String columnCurrentRegion();
}
