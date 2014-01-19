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

public abstract class TabsLoadout extends Bundle {
	public static TabsLoadout get() {
		return Main.getBundleService().get(TabsLoadout.class);
	}

	public TabsLoadout(final Locale locale) {
		super(locale);
	}

	public abstract String cancel();
	public abstract String collapse();
	public abstract String columnLocation();
	public abstract String columnName();
	public abstract String columnOwner();
	public abstract String columnSlot();
	public abstract String columnValue();
	public abstract String description();
	public abstract String empty();
	public abstract String expand();
	public abstract String export();
	public abstract String exportEveXml();
	public abstract String exportEveXmlAll();
	public abstract String exportEveXmlSelected();
	public abstract String exportSqlCsvHtml();
	public abstract String flagCargo();
	public abstract String flagDroneBay();
	public abstract String flagHighSlot();
	public abstract String flagLowSlot();
	public abstract String flagMediumSlot();
	public abstract String flagOther();
	public abstract String flagRigSlot();
	public abstract String flagSubSystem();
	public abstract String flagTotalValue();
	public abstract String name();
	public abstract String name1();
	public abstract String no();
	public abstract String no1();
	public abstract String oK();
	public abstract String owner();
	public abstract String ship();
	public abstract String ship1();
	public abstract String totalAll();
	public abstract String totalModules();
	public abstract String totalShip();
	public abstract String whitespace10(Object arg0);
}
