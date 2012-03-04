/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
 * @author Candle
 */
public abstract class DialoguesCsvExport extends Bundle {
	public static DialoguesCsvExport get() {
		return BundleCache.get(DialoguesCsvExport.class);
	}

	public static DialoguesCsvExport get(Locale locale) {
		return BundleCache.get(DialoguesCsvExport.class, locale);
	}

	public DialoguesCsvExport(Locale locale) {
		super(locale);
	}
	public abstract String csvExport();
	public abstract String browse();
	public abstract String assets();
	public abstract String allAssets();
	public abstract String currentFilter();
	public abstract String savedFilter();
	public abstract String fieldTerminated();
	public abstract String comma();
	public abstract String semicolon();
	public abstract String linesTerminated();
	public abstract String decimalSeperator();
	public abstract String dot();
	public abstract String columns();
	public abstract String ok();
	public abstract String cancel();
	public abstract String defaultSettings();
	public abstract String headerNameName();
	public abstract String headerNameGroup();
	public abstract String headerNameCategory();
	public abstract String headerNameOwner();
	public abstract String headerNameCount();
	public abstract String headerNameLocation();
	public abstract String headerNameContainer();
	public abstract String headerNameFlag();
	public abstract String headerNamePrice();
	public abstract String headerNameSellMin();
	public abstract String headerNameBuyMax();
	public abstract String headerNameValue();
	public abstract String headerNameMeta();
	public abstract String headerNameItemID();
	public abstract String headerNameBasePrice();
	public abstract String headerNameVolume();
	public abstract String headerNameTypeID();
	public abstract String headerNameRegion();
	public abstract String headerNameTypeCount();
	public abstract String headerNameSecurity();
	public abstract String headerNameReprocessed();
	public abstract String headerNameReprocessedValue();
	public abstract String headerSingleton();
	public abstract String headerVolumeTotal();
	public abstract String selectOne();
	public abstract String confirmStupidDecision();
	public abstract String failedToSave();
	public abstract String lineEndingsWindows();
	public abstract String lineEndingsMac();
	public abstract String lineEndingsUnix();
}