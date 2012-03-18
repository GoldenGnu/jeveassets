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

import java.util.Date;
import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class GuiShared extends Bundle {
	public static GuiShared get() {
		return BundleCache.get(GuiShared.class);
	}

	public static GuiShared get(Locale locale) {
		return BundleCache.get(GuiShared.class, locale);
	}

	public GuiShared(Locale locale) {
		super(locale);
	}

	public abstract String add();
	public abstract String addStockpileItem();
	public abstract String autoText();
	public abstract String autoWindow();
	public abstract String chruker();
	public abstract String columns();
	public abstract String copy();
	public abstract String cut();
	public abstract String disable();
	public abstract String dotlan();
	public abstract String editItem();
	public abstract String editName();
	public abstract String editPrice();
	public abstract String emptyString();
	public abstract String eveCentral();
	public abstract String eveMarketdata();
	public abstract String eveMarkets();
	public abstract String eveOnline();
	public abstract String item();
	public abstract String lookup();
	public abstract String newStockpile();
	public abstract String overwrite();
	public abstract String overwriteFile();
	public abstract String paste();
	public abstract String region();
	public abstract String reset();
	public abstract String station();
	public abstract String stockpile();
	public abstract String system();
	public abstract String today(Object arg0); 
	public abstract String weekdayAndTime(Date datetime);
	public abstract String whitespace37(Object arg0, Object arg1);
	public abstract String files(Object arg0);
	//Filters
	public abstract String saveFilter();
	public abstract String enterFilterName();
	public abstract String save();
	public abstract String cancel();
	public abstract String noFilterName();
	public abstract String overwriteFilter();
	public abstract String addField();
	public abstract String clearField();
	public abstract String loadFilter();
	public abstract String showFilters();
	public abstract String manageFilters();
	public abstract String nothingToSave();
	public abstract String filterManager();
	public abstract String managerLoad();
	public abstract String managerRename();
	public abstract String managerDelete();
	public abstract String managerDone();
	public abstract String renameFilter();
	public abstract String deleteFilter();
	public abstract String deleteFilterName(Object arg0);
	public abstract String mergeFilters();
	public abstract String managerMerge();
	public abstract String filterAll();
	public abstract String filterAnd();
	public abstract String filterOr();
	public abstract String filterContains();
	public abstract String filterContainsNot();
	public abstract String filterEquals();
	public abstract String filterEqualsNot();
	public abstract String filterGreaterThan();
	public abstract String filterLessThan();
	public abstract String filterBefore();
	public abstract String filterAfter();
	public abstract String filterEqualsDate();
	public abstract String filterEqualsNotDate();
	public abstract String filterContainsColumn();
	public abstract String filterContainsNotColumn();
	public abstract String filterEqualsColumn();
	public abstract String filterEqualsNotColumn();
	public abstract String filterGreaterThanColumn();
	public abstract String filterLessThanColumn();
	public abstract String filterBeforeColumn();
	public abstract String filterAfterColumn();
	public abstract String filterUntitled();
	public abstract String filterEmpty();
	public abstract String filterShowing(int rowCount, int size, String filterName);
	public abstract String popupMenuAddField();
	public abstract String export();
}
