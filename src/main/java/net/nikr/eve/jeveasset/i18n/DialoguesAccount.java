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

/**
 *
 * @author Candle
 */
public abstract class DialoguesAccount extends Bundle {

	public static DialoguesAccount get() {
		return BundleServiceFactory.getBundleService().get(DialoguesAccount.class);
	}

	public DialoguesAccount(final Locale locale) {
		super(locale);
	}

	public abstract String dialogueNameAccountExport();
	public abstract String dialogueNameAccountImport();

	public abstract String previousArrow();
	public abstract String nextArrow();
	public abstract String cancel();
	public abstract String ok();

	public abstract String failApiError();
	public abstract String failApiErrorText(String s);
	public abstract String failNotEnoughPrivileges();
	public abstract String failNotEnoughPrivilegesText();
	public abstract String failNotValid();
	public abstract String failNotValidText();
	public abstract String failWrongEntry();
	public abstract String failWrongEntryText();
	public abstract String okUpdate();
	public abstract String okUpdateLimitedText();
	public abstract String okUpdateText();
	public abstract String okLimited();
	public abstract String okLimitedExportText();
	public abstract String okLimitedText();
	public abstract String okValid();
	public abstract String okValidText();

	public abstract String authCode();
	public abstract String authentication();
	public abstract String authorize();
	public abstract String accessKey();
	public abstract String esiHelpText();
	public abstract String validatingMessage();
	public abstract String scopes();
	public abstract String corporation();
	public abstract String character();
	public abstract String workaroundLabel();
	public abstract String workaroundCheckbox();
	public abstract String scopeAssets();
	public abstract String scopeWallet();
	public abstract String scopeBlueprints();
	public abstract String scopeBookmarks();
	public abstract String scopeIndustryJobs();
	public abstract String scopeMarketOrders();
	public abstract String scopeMarketStructures();
	public abstract String scopeContracts();
	public abstract String scopeRoles();
	public abstract String scopeStructures();
	public abstract String scopeShipType();
	public abstract String scopeShipLocation();
	public abstract String scopeOpenWindows();
	public abstract String scopePlanetaryInteraction();
	public abstract String scopeAutopilot();
	public abstract String scopeDivisions();
	public abstract String scopeSkills();
	public abstract String scopeMining();

	public abstract String dialogueNameAccountManagement();

	public abstract String accountExpired();
	public abstract String accountInvalid();
	public abstract String accountMigrated();
	public abstract String accountCanMigrate();
	public abstract String add();
	public abstract String revalidate();
	public abstract String revalidateMsgAll(int total);
	public abstract String revalidateMsgNone(int total);
	public abstract String revalidateMsgSome(int total, int done, int failed);
	public abstract String collapse();
	public abstract String expand();
	public abstract String showAssets();
	public abstract String checkAll();
	public abstract String uncheckAll();
	public abstract String checkSelected();
	public abstract String uncheckSelected();
	public abstract String share();
	public abstract String shareExport();
	public abstract String shareExportClipboard();
	public abstract String shareExportFail();
	public abstract String shareExportFile();
	public abstract String shareExportHelp();
	public abstract String shareImport();
	public abstract String shareImportClipboard();
	public abstract String shareImportFile();
	public abstract String shareImportHelp();
	public abstract String close();
	public abstract String noOwners();

	public abstract String migrateTitle();
	public abstract String migrateDone();
	public abstract String migrateEsiEmpty(String ownerName);
	public abstract String migrateEsiSelect();
	public abstract String migrateOk();
	public abstract String migrateAll();
	public abstract String migrateEsiAccountName(String name, int included, int total);
	public abstract String migrateHelp();
	public abstract String accountMigratedDoneMsg();
	public abstract String accountMigratedDoneTitle();

	public abstract String deleteAccountQuestion();
	public abstract String deleteAccount();

	public abstract String delete();
	public abstract String edit();
	public abstract String migrate();

	public abstract String tableFormatName();
	public abstract String tableFormatCorporation();
	public abstract String tableFormatAssetList();
	public abstract String tableFormatAccountBalance();
	public abstract String tableFormatIndustryJobs();
	public abstract String tableFormatMarketOrders();
	public abstract String tableFormatJournal();
	public abstract String tableFormatTransactions();
	public abstract String tableFormatContracts();
	public abstract String tableFormatLocations();
	public abstract String tableFormatStructures();
	public abstract String tableFormatMarketStructures();
	public abstract String tableFormatBlueprints();
	public abstract String tableFormatBookmarks();
	public abstract String tableFormatDivisions();
	public abstract String tableFormatShip();
	public abstract String tableFormatPlanetaryInteraction();
	public abstract String tableFormatOpenWindows();
	public abstract String tableFormatAutopilot();
	public abstract String tableFormatSkills();
	public abstract String tableFormatMining();
	public abstract String tableFormatYes();
	public abstract String tableFormatNo();
	public abstract String tableFormatExpires();
}
