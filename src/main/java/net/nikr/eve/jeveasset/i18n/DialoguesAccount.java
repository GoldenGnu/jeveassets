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
public abstract class DialoguesAccount extends Bundle {

	public static DialoguesAccount get() {
		return Main.getBundleService().get(DialoguesAccount.class);
	}

	public DialoguesAccount(final Locale locale) {
		super(locale);
	}

	public abstract String dialogueNameAccountImport();

	public abstract String previousArrow();
	public abstract String nextArrow();
	public abstract String cancel();
	public abstract String ok();

	public abstract String failApiError();
	public abstract String failApiErrorText(String s);
	public abstract String failApiException();
	public abstract String failApiExceptionText();
	public abstract String failExist();
	public abstract String failExistText();
	public abstract String failGeneric();
	public abstract String failGenericText(String s);
	public abstract String failNotEnoughPrivileges();
	public abstract String failNotEnoughPrivilegesText();
	public abstract String failNotValid();
	public abstract String failNotValidText();
	public abstract String okLimited();
	public abstract String okLimitedText();
	public abstract String okValid();
	public abstract String okValidText();

	public abstract String keyId();
	public abstract String vCode();
	public abstract String helpText();
	public abstract String validatingMessage();

	public abstract String dialogueNameAccountManagement();

	public abstract String accountExpired();
	public abstract String accountInvalid();
	public abstract String add();
	public abstract String collapse();
	public abstract String expand();
	public abstract String showAssets();
	public abstract String checkAll();
	public abstract String uncheckAll();
	public abstract String checkSelected();
	public abstract String uncheckSelected();
	public abstract String showCorp();
	public abstract String close();
	public abstract String noOwners();


	public abstract String deleteAccountQuestion();
	public abstract String deleteAccount();

	public abstract String delete();
	public abstract String edit();

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
	public abstract String tableFormatYes();
	public abstract String tableFormatNo();
	public abstract String tableFormatExpires();
}
