/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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


public abstract class TabsContracts extends Bundle {

	public static TabsContracts get() {
		return Main.getBundleService().get(TabsContracts.class);
	}

	public TabsContracts(final Locale locale) {
		super(locale);
	}

	public abstract String auction();
	public abstract String collapse();
	public abstract String columnAccepted();
	public abstract String columnAcceptor();
	public abstract String columnAssignee();
	public abstract String columnCompleted();
	public abstract String columnEndStation();
	public abstract String columnExpired();
	public abstract String columnIncluded();
	public abstract String columnIssued();
	public abstract String columnIssuer();
	public abstract String columnIssuerCorp();
	public abstract String columnName();
	public abstract String columnQuantity();
	public abstract String columnSingleton();
	public abstract String columnStartStation();
	public abstract String columnTitle();
	public abstract String columnType();
	public abstract String columnTypeID();
	public abstract String courier();
	public abstract String expand();
	public abstract String itemExchange();
	public abstract String loan();
	public abstract String notAccepted();
	public abstract String packaged();
	public abstract String publicContract();
	public abstract String title();
	public abstract String type();
	public abstract String unknown();
	public abstract String unpackaged();
}
