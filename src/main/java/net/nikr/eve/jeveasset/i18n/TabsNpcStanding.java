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

public abstract class TabsNpcStanding extends Bundle {

	public static TabsNpcStanding get() {
		return BundleServiceFactory.getBundleService().get(TabsNpcStanding.class);
	}

	public TabsNpcStanding(final Locale locale) {
		super(locale);
	}

	public abstract String npcStanding();
	public abstract String columnOwner();
	public abstract String columnAgent();
	public abstract String columnCorporation();
	public abstract String columnFaction();
	public abstract String columnType();
	public abstract String columnStanding();
	public abstract String columnID();
	public abstract String typeAgent();
	public abstract String typeCorporation();
	public abstract String typeFaction();
}
