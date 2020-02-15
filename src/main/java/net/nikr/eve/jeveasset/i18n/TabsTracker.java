/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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


public abstract class TabsTracker extends Bundle {

	public static TabsTracker get() {
		return BundleServiceFactory.getBundleService().get(TabsTracker.class);
	}

	public TabsTracker(final Locale locale) {
		super(locale);
	}

	public abstract String allProfiles();
	public abstract String assets();
	public abstract String autoZoom();
	public abstract String cancel();
	public abstract String characterCorporations();
	public abstract String characterWallet();
	public abstract String checkAllLocationsMsg();
	public abstract String checkAllLocationsTitle();
	public abstract String clear();
	public abstract String contractCollateral();
	public abstract String contractValue();
	public abstract String corporationWallet();
	public abstract String date();
	public abstract String day1();
	public abstract String delete();
	public abstract String deleteSelected();
	public abstract String division(String id);
	public abstract String edit();
	public abstract String empty();
	public abstract String enterNewValue();
	public abstract String error();
	public abstract String escrows();
	public abstract String escrowsToCover();
	public abstract String filterTitle();
	public abstract String from();
	public abstract String grandTotal();
	public abstract String help();
	public abstract String helpLegacyData();
	public abstract String helpNewData();
	public abstract String importFile();
	public abstract String importFileImport();
	public abstract String importFileInvalidMsg();
	public abstract String importFileInvalidTitle();
	public abstract String importFileOverwriteMsg();
	public abstract String importFileOverwriteTitle();
	public abstract String includeZero();
	public abstract String invalid();
	public abstract String invalidNumberMsg();
	public abstract String invalidNumberTitle();
	public abstract String knownLocations();
	public abstract String manufacturing();
	public abstract String month1();
	public abstract String months3();
	public abstract String months6();
	public abstract String newSelected();
	public abstract String noDataFound();
	public abstract String note();
	public abstract String notesAdd();
	public abstract String notesDeleteMsg(String note);
	public abstract String notesDeleteTitle();
	public abstract String notesEditMsg();
	public abstract String notesEditTitle();
	public abstract String ok();
	public abstract String other();
	public abstract String quickDate();
	public abstract String reset();
	public abstract String search();
	public abstract String selectDivision();
	public abstract String selectFlag();
	public abstract String selectLocation();
	public abstract String selectOwner();
	public abstract String selectionDate();
	public abstract String selectionIsk();
	public abstract String selectionNote();
	public abstract String selectionShortDate();
	public abstract String selectionShortIsk();
	public abstract String selectionShortNote();
	public abstract String sellOrders();
	public abstract String statusAssets();
	public abstract String statusBalance();
	public abstract String statusContractCollateral();
	public abstract String statusContractValue();
	public abstract String statusEscrows();
	public abstract String statusEscrowsToCover();
	public abstract String statusManufacturing();
	public abstract String statusSellOrders();
	public abstract String statusTotal();
	public abstract String title();
	public abstract String to();
	public abstract String today();
	public abstract String total();
	public abstract String unknownLocations();
	public abstract String walletBalance();
	public abstract String week1();
	public abstract String week2();
	public abstract String year1();
	public abstract String years2();
	
}
