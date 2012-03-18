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
 * @author Candle
 */
public abstract class DialoguesAccount extends Bundle {
	public static DialoguesAccount get() {
		return BundleCache.get(DialoguesAccount.class);
	}

	public static DialoguesAccount get(Locale locale) {
		return BundleCache.get(DialoguesAccount.class, locale);
	}

	public DialoguesAccount(Locale locale) {
		super(locale);
	}

	public abstract String dialogueNameAccountImport();

	public abstract String previousArrow();
	public abstract String nextArrow();
	public abstract String cancel();
	public abstract String ok();

	public abstract String accountAlreadyImported();
	public abstract String noAccess();
	public abstract String noInternetConnection();
	public abstract String accountNotValid();
	public abstract String notEnoughAccess();
	public abstract String accountValid();
	public abstract String accountAlreadyImportedText();
	public abstract String noAccessText();
	public abstract String noInternetConnectionText();
	public abstract String accountNotValidText();
	public abstract String notEnoughAccessText();
	public abstract String accountValidText();

	public abstract String keyId();
	public abstract String vCode();
	public abstract String helpText();
	public abstract String validatingMessage();

	public abstract String dialogueNameAccountManagement();

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
	public abstract String tableFormatYes();
	public abstract String tableFormatNo();
	public abstract String tableFormatExpires();
}