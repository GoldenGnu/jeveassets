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

public abstract class TabsRouting extends Bundle {
	public static TabsRouting get() {
		return Main.getBundleService().get(TabsRouting.class);
	}

	public TabsRouting(final Locale locale) {
		super(locale);
	}

	public abstract String add();
	public abstract String addSystem();
	public abstract String addSystemCancel();
	public abstract String addSystemOK();
	public abstract String addSystemSelect();
	public abstract String addSystemTitle();
	public abstract String algorithm();
	public abstract String allowed(Object arg0, Object arg1);
	public abstract String calculate();
	public abstract String cancel();
	public abstract String emptyResult();
	public abstract String error();
	public abstract String filteredAssets();
	public abstract String noSystems();
	public abstract String noSystemsTitle();
	public abstract String ok();
	public abstract String overviewGroup(Object arg0);
	public abstract String remove();
	public abstract String resultArrow();
	public abstract String resultOverwrite();
	public abstract String resultTabFull();
	public abstract String resultTabInfo();
	public abstract String resultTabShort();
	public abstract String resultText(String name, int jumps, int waypoints, int time);
	public abstract String routingThread();
	public abstract String routingTitle();
	public abstract String total(Object arg0, Object arg1);
	public abstract String source();
	public abstract String startEmptyAuto(Object arg0);
	public abstract String startEmpty();
	public abstract String startSystem();
	
	
	
	
	
	
	
}
