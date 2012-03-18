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
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Andrew
 */
public abstract class DialoguesUpdate extends Bundle {
	public static DialoguesUpdate get() {
		return BundleCache.get(DialoguesUpdate.class);
	}
	public static DialoguesUpdate get(Locale locale) {
		return BundleCache.get(DialoguesUpdate.class, locale);
	}
	public DialoguesUpdate(Locale locale) {
		super(locale);
	}
	public abstract String updating();
	public abstract String ok();
	public abstract String cancel();
	public abstract String cancelQuestion();
	public abstract String cancelQuestionTitle();
	public abstract String errors(String mouseTask);

	// used in UpdateDialog
	public abstract String update();
	public abstract String all();
	public abstract String marketOrders();
	public abstract String industryJobs();
	public abstract String accounts();
	public abstract String accountBlances();
	public abstract String assets();
	public abstract String priceData();
	public abstract String nextUpdate();
	public abstract String nowAll();
	public abstract String nowSome();
	public abstract String conqStations();
	public abstract String balance();

	public abstract String clickToShow(String name);
	public abstract String clickToHide(String name);
}
