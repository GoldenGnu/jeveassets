/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

public abstract class GuiFrame extends Bundle {

	public static GuiFrame get() {
		return BundleServiceFactory.getBundleService().get(GuiFrame.class);
	}

	public GuiFrame(final Locale locale) {
		super(locale);
	}

	public abstract String about();
	public abstract String accounts();
	public abstract String business();
	public abstract String change();
	public abstract String clickToShow(String text);
	public abstract String clickToApply(String text);
	public abstract String close();
	public abstract String contracts();
	public abstract String credits();
	public abstract String eve();
	public abstract String exit();
	public abstract String exitMsg(int size);
	public abstract String exitTitle(int size);
	public abstract String file();
	public abstract String help();
	public abstract String inventory();
	public abstract String items();
	public abstract String industry();
	public abstract String industryPlot();
	public abstract String journal();
	public abstract String license();
	public abstract String linkFaq();
	public abstract String linkForum();
	public abstract String linkGitHub();
	public abstract String log();
	public abstract String market();
	public abstract String materials();
	public abstract String misc();
	public abstract String netWorth();
	public abstract String not();
	public abstract String options();
	public abstract String options1();
	public abstract String overview();
	public abstract String profiles();
	public abstract String programUpdateText();
	public abstract String programUpdateTip();
	public abstract String readme();
	public abstract String reprocessed();
	public abstract String routing();
	public abstract String sendBugReport();
	public abstract String ship();
	public abstract String stockpile();
	public abstract String table();
	public abstract String tools();
	public abstract String tracker();
	public abstract String transaction();
	public abstract String tree();
	public abstract String updatable();
	public abstract String update();
	public abstract String update1();
	public abstract String updateEveKit();
	public abstract String updateStructure();
	public abstract String updatingInProgressMsg();
	public abstract String updatingInProgressTitle();
	public abstract String values();
	public abstract String valueTable();
	public abstract String windowTitle(String programName, String programVersion, int portable, int profileCount, String activeProfileName);
}
