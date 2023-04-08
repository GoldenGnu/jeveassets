/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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


public abstract class TabsPriceHistory extends Bundle {

	public static TabsPriceHistory get() {
		return BundleServiceFactory.getBundleService().get(TabsPriceHistory.class);
	}

	public TabsPriceHistory(final Locale locale) {
		super(locale);
	}

	public abstract String add();
	public abstract String addTitle();
	public abstract String clear();
	public abstract String deleteHistorySet();
	public abstract String deleteHistorySets(int size);
	public abstract String edit();
	public abstract String empty();
	public abstract String enterName();
	public abstract String from();
	public abstract String graphToolTip(Comparable<?> name, String isk, String date);
	public abstract String includeZero();
	public abstract String load();
	public abstract String manage();
	public abstract String manageTitle();
	public abstract String maxItemsMsg(int maxItems);
	public abstract String merge();
	public abstract String mergeMax(int maxItem);
	public abstract String overwrite();
	public abstract String remove();
	public abstract String rename();
	public abstract String save();
	public abstract String saveTitle();
	public abstract String scaleLinear();
	public abstract String scaleLogarithmic();
	public abstract String sourcejEveAssets();
	public abstract String sourcezKillboard();
	public abstract String title();
	public abstract String to();
	public abstract String updateTitle();
	public abstract String updatingMsg();
	public abstract String updatingTitle();

}
