/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

/**
 *
 * @author Candle
 */
public abstract class DialoguesSettings extends Bundle {

	public static DialoguesSettings get() {
		return BundleServiceFactory.getBundleService().get(DialoguesSettings.class);
	}

	public DialoguesSettings(final Locale locale) {
		super(locale);
	}

	// used in GeneralSettingsPanel
	public abstract String general();
	public abstract String enterFilter();
	public abstract String highlightSelectedRow();
	public abstract String focusEveOnline();
	public abstract String focusEveOnlineLinuxCmd();
	public abstract String focusEveOnlineLinuxHelp();
	public abstract String focusEveOnlineLinuxHelp2();
	public abstract String copyDecimalSeparator();
	public abstract String transactionsMargin();
	public abstract String transactionsPrice();
	public abstract String transactionsPriceAverage();
	public abstract String transactionsPriceLatest();
	public abstract String transactionsPriceMaximum();
	public abstract String transactionsPriceMinimum();
	public abstract String transactionsProfit();
	public abstract String includeDays();
	public abstract String days();

	// used in ShowToolSettingsPanel
	public abstract String show();
	public abstract String saveTools();
	public abstract String selectTools();
	public abstract String toolsOrderHelp();

	// used in AssetsToolSettingsPanel
	public abstract String assets();
	public abstract String includeSellContracts();
	public abstract String includeSellOrders();
	public abstract String includeBuyContracts();
	public abstract String includeBuyOrders();
	public abstract String includeManufacturing();

	// used in OverviewToolSettingsPanel
	public abstract String overview();
	public abstract String ignoreContainers();

	// used in StockpileToolSettingsPanel
	public abstract String stockpile();
	public abstract String stockpileColors();
	public abstract String stockpileSwitchTab();
	public abstract String stockpileTwoGroups();
	public abstract String stockpileThreeGroups();
	public abstract String percentPlusSymbol();

	public abstract String saveHistoryWarning(); //Transactions & Journal & Market Orders

	// used in MarketOrdersToolSettingsPanel
	public abstract String marketOrders();
	public abstract String marketOrdersSaveHistory();
	public abstract String expireWarnDays();
	public abstract String remainingWarnPercent();

	// used in TransactionsToolSettingsPanel
	public abstract String transactions();
	public abstract String transactionsSaveHistory();

	// used in TransactionsToolSettingsPanel
	public abstract String journal();
	public abstract String journalSaveHistory();

	// used in ContractToolSettingsPanel
	public abstract String contracts();
	public abstract String contractsSaveHistory();

	// used in TrackerToolSettingsPanel
	public abstract String tracker();
	public abstract String useAssetPriceForSellOrders();

	// used in ColorSettingsPanel
	public abstract String colors();
	public abstract String collapse();
	public abstract String expand();
	public abstract String columnName();
	public abstract String columnBackground();
	public abstract String columnForeground();
	public abstract String columnPreview();
	public abstract String columnSelected();
	public abstract String testText();
	public abstract String testSelectedText();
	public abstract String overwriteMsg();
	public abstract String overwriteTitle();
	public abstract String theme();
	public abstract String lookAndFeel();
	public abstract String lookAndFeelDefault();
	public abstract String lookAndFeelDefaultName(String name);
	public abstract String lookAndFeelFlatLight();
	public abstract String lookAndFeelFlatDark();
	public abstract String lookAndFeelFlatIntelliJ();
	public abstract String lookAndFeelFlatDarcula();
	public abstract String lookAndFeelNimbusDark();
	public abstract String lookAndFeelMsg();
	public abstract String lookAndFeelTitle();

	// used in PriceDataSettingsPanel
	public abstract String changeSourceWarning();
	public abstract String includeRegions();
	public abstract String includeStations();
	public abstract String includeSystems();
	public abstract String notConfigurable();
	public abstract String price();
	public abstract String priceBase();
	public abstract String priceData();
	public abstract String priceReprocessed();
	public abstract String priceTech1();
	public abstract String priceTech2();
	public abstract String source();
	public abstract String janiceApiKey();
	public abstract String janiceApiKeyMsg();
	public abstract String janiceApiKeyTitle();

	// used in ProxySettingsPanel
	public abstract String proxy();
	public abstract String type();
	public abstract String address();
	public abstract String port();
	public abstract String auth();
	public abstract String username();
	public abstract String password();

	// used in ReprocessingSettingsPanel
	public abstract String reprocessing();
	public abstract String reprocessingWarning();
	public abstract String stationEquipment();
	public abstract String fiftyPercent();
	public abstract String percentSymbol();
	public abstract String reprocessingLevel();
	public abstract String reprocessingEfficiencyLevel();
	public abstract String oreProcessingLevel();
	public abstract String scrapmetalProcessingLevel();
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
	public abstract String tools();
	public abstract String values();

	// used in UserItemNameSettingsPanel
	public abstract String names();
	public abstract String name();
	public abstract String namesInstruction();

	// used in JUserListPanel
	public abstract String badInput();
	public abstract String deleteItem();
	public abstract String deleteTypeTitle(String type);
	public abstract String editItem();
	public abstract String editTypeTitle(String type);
	public abstract String inputNotValid();
	public abstract String itemEmpty();
	public abstract String items(int size);

	// used in UserPriceSettingsPanel
	public abstract String pricePrices();
	public abstract String pricePrice();
	public abstract String priceInstructions();

	// used in UserLocationSettingsPanel
	public abstract String locationsInstructions();

	// used in WindowSettingsPanel
	public abstract String windowWindow();
	public abstract String windowSaveOnExit();
	public abstract String windowAlwaysOnTop();
	public abstract String windowFixed();
	public abstract String windowWidth();
	public abstract String windowHeight();
	public abstract String windowX();
	public abstract String windowY();
	public abstract String windowMaximised();
	public abstract String windowDefault();

	// used in ContractPriceSettingsPanel
	public abstract String contractPrices();
	public abstract String includePrivate();
	public abstract String defaultBPC();
	public abstract String priceMode();
	public abstract String updateRequired();
	public abstract String security();
	public abstract String feedback();
}
