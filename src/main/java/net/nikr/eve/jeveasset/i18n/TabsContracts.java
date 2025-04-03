/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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


public abstract class TabsContracts extends Bundle {

	public static TabsContracts get() {
		return BundleServiceFactory.getBundleService().get(TabsContracts.class);
	}

	public TabsContracts(final Locale locale) {
		super(locale);
	}

	public abstract String auction();
	public abstract String availabilityPrivate();
	public abstract String availabilityPublic();
	public abstract String bought();
	public abstract String buying();
	public abstract String collapse();
	public abstract String collateralAcceptor();
	public abstract String collateralIssuer();
	public abstract String columnAccepted();
	public abstract String columnAcceptor();
	public abstract String columnAssignee();
	public abstract String columnAvailability();
	public abstract String columnBuyout();
	public abstract String columnCollateral();
	public abstract String columnCompleted();
	public abstract String columnContractID();
	public abstract String columnEndStation();
	public abstract String columnExpired();
	public abstract String columnIncluded();
	public abstract String columnIssued();
	public abstract String columnForCorp();
	public abstract String columnIssuer();
	public abstract String columnIssuerCorp();
	public abstract String columnItemID();
	public abstract String columnMarketPrice();
	public abstract String columnPriceReprocessed();
	public abstract String columnPriceReprocessedToolTip();
	public abstract String columnPriceManufacturing();
	public abstract String columnPriceManufacturingToolTip();
	public abstract String columnMarketValue();
	public abstract String columnMarketValueToolTip();
	public abstract String columnValueReprocessed();
	public abstract String columnValueReprocessedToolTip();
	public abstract String columnValueManufacturing();
	public abstract String columnValueManufacturingToolTip();
	public abstract String columnMaterialEfficiency();
	public abstract String columnMaterialEfficiencyToolTip();
	public abstract String columnName();
	public abstract String columnNumDays();
	public abstract String columnOwned();
	public abstract String columnOwnedToolTip();
	public abstract String columnPrice();
	public abstract String columnQuantity();
	public abstract String columnRecordID();
	public abstract String columnReward();
	public abstract String columnRuns();
	public abstract String columnRunsToolTip();
	public abstract String columnSingleton();
	public abstract String columnStartStation();
	public abstract String columnStatus();
	public abstract String columnTimeEfficiency();
	public abstract String columnTimeEfficiencyToolTip();
	public abstract String columnTitle();
	public abstract String columnType();
	public abstract String columnTypeID();
	public abstract String columnVolume();
	public abstract String contractCount();
	public abstract String courier();
	public abstract String excluded();
	public abstract String expand();
	public abstract String included();
	public abstract String itemExchange();
	public abstract String loan();
	public abstract String notAccepted();
	public abstract String packaged();
	public abstract String publicContract();
	public abstract String sellingPrice();
	public abstract String sellingAssets();
	public abstract String sold();
	public abstract String status();
	public abstract String statusArchived();
	public abstract String statusCancelled();
	public abstract String statusCompleted();
	public abstract String statusCompletedByContractor();
	public abstract String statusCompletedByIssuer();
	public abstract String statusDeleted();
	public abstract String statusFailed();
	public abstract String statusInProgress();
	public abstract String statusOutstanding();
	public abstract String statusRejected();
	public abstract String statusReversed();
	public abstract String statusUnknown();
	public abstract String title();
	public abstract String type();
	public abstract String unknown();
	public abstract String unpackaged();
}
