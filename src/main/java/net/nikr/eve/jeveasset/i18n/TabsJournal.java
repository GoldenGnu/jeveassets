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


public abstract class TabsJournal extends Bundle {

	public static TabsJournal get() {
		return BundleServiceFactory.getBundleService().get(TabsJournal.class);
	}

	public TabsJournal(final Locale locale) {
		super(locale);
	}

	public abstract String clearNew();
	public abstract String contextAllianceID();
	public abstract String contextCharacterID();
	public abstract String contextContractID();
	public abstract String contextCorporationID();
	public abstract String contextEveID();
	public abstract String contextIndustryJobID();
	public abstract String contextPlanetID();
	public abstract String contextStationID();
	public abstract String contextStructureID();
	public abstract String contextSystemID();
	public abstract String contextTransactionID();
	public abstract String contextTypeID();
	public abstract String contracts();
	public abstract String findIn();
	public abstract String industryJobs();
	public abstract String title();
	public abstract String total();
	public abstract String totalNegative();
	public abstract String totalPositive();
	public abstract String transactions();
	public abstract String columnAccountKey();
	public abstract String columnAmount();
	public abstract String columnBalance();
	public abstract String columnDate();
	public abstract String columnOwner();
	public abstract String columnOwnerName1();
	public abstract String columnOwnerName2();
	public abstract String columnReason();
	public abstract String columnRefType();
	public abstract String columnTaxAmount();
	public abstract String columnAdded();
	public abstract String columnAddedToolTip();
	public abstract String columnContextName();
	public abstract String columnContextType();
	public abstract String columnContextID();
}
