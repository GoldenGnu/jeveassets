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

/**
 *
 * @author Candle
 */
public abstract class DialoguesExport extends Bundle {
	public static DialoguesExport get() {
		return Main.getBundleService().get(DialoguesExport.class);
	}

	public DialoguesExport(final Locale locale) {
		super(locale);
	}
	public abstract String cancel();
	public abstract String columns();
	public abstract String comma();
	public abstract String confirmStupidDecision();
	public abstract String createTable();
	public abstract String csv();
	public abstract String currentFilter();
	public abstract String decimalSeparator();
	public abstract String defaultSettings();
	public abstract String dot();
	public abstract String dropTable();
	public abstract String export();
	public abstract String extendedInserts();
	public abstract String failedToSave();
	public abstract String fieldTerminated();
	public abstract String filters();
	public abstract String format();
	public abstract String html();
	public abstract String htmlHeaderRepeat();
	public abstract String htmlIGB();
	public abstract String htmlStyled();
	public abstract String lineEndingsMac();
	public abstract String lineEndingsWindows();
	public abstract String lineEndingsUnix();
	public abstract String linesTerminated();
	public abstract String noFilter();
	public abstract String noSavedFilter();
	public abstract String ok();
	public abstract String options();
	public abstract String savedFilter();
	public abstract String selectOne();
	public abstract String semicolon();
	public abstract String sql();
	public abstract String tableName();
	public abstract String viewCurrent();
	public abstract String viewSaved();
	public abstract String viewSelect();
	public abstract String viewNoSaved();
}
