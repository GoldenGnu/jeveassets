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
public abstract class DialoguesSettings extends Bundle {
	public static DialoguesSettings get() {
		return BundleCache.get(DialoguesSettings.class);
	}

	public static DialoguesSettings get(Locale locale) {
		return BundleCache.get(DialoguesSettings.class, locale);
	}

	public DialoguesSettings(Locale locale) {
		super(locale);
	}
	// used in AssetsToolSettingsPanel
	public abstract String assets();
	public abstract String enterFilter();
	public abstract String hilightSelected();
	public abstract String showSellOrReprocessColours();

	// used in GeneralSettingsPanel
	public abstract String general();
	public abstract String searchForNewVersion(String programName);
	public abstract String searchForNewVersionBeta();

	// used in OverviewToolSettingsPanel
	public abstract String overview();
	public abstract String ignoreAuditLogContainers();

	// used in PriceDataSettingsPanel
	public abstract String priceData();
	public abstract String changeSourceWarning();
	public abstract String includeRegions();
	public abstract String price();
	public abstract String source();

	// used in ProxySettingsPanel
	public abstract String proxy();
	public abstract String type();
	public abstract String address();
	public abstract String port();
	public abstract String enable();
	public abstract String apiProxy();

	// used in ReprocessingSettingsPanel
	public abstract String reprocessing();
	public abstract String reprocessingWarning();
	public abstract String stationEquipment();
	public abstract String fiftyPercent();
	public abstract String customPercent();
	public abstract String percentSymbol();
	public abstract String refiningLevel();
	public abstract String refiningEfficiencyLevel();
	public abstract String scrapMetalProcessingLevel();
	public abstract String zero();
	public abstract String one();
	public abstract String two();
	public abstract String three();
	public abstract String four();
	public abstract String five();

	// used in SettingsDialog
	public abstract String settings(String programName);
	public abstract String root();
	public abstract String ok();
	public abstract String apply();
	public abstract String cancel();

	// used in UserItemNameSettingsPanel
	public abstract String names();
	public abstract String name();
	public abstract String namesInstruction();

	// used in UserPriceSettingsPanel
	public abstract String pricePrices();
	public abstract String pricePrice();
	public abstract String priceInstructions();

	// used in WindowSettingsPanel
	public abstract String windowWindow();
	public abstract String windowSaveOnExit();
	public abstract String windowFixed();
	public abstract String windowWidth();
	public abstract String windowHeight();
	public abstract String windowX();
	public abstract String windowY();
	public abstract String windowMaximised();
	public abstract String windowDefault();
}