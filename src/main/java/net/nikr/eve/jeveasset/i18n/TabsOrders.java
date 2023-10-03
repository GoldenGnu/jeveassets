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

public abstract class TabsOrders extends Bundle {

	public static TabsOrders get() {
		return BundleServiceFactory.getBundleService().get(TabsOrders.class);
	}

	public TabsOrders(final Locale locale) {
		super(locale);
	}

	public abstract String activeBuyOrders();
	public abstract String activeSellOrders();
	public abstract String buy();
	public abstract String clearNew();
	public abstract String columnOrderType();
	public abstract String columnName();
	public abstract String columnGroup();
	public abstract String columnVolumeRemain();
	public abstract String columnVolumeTotal();
	public abstract String columnPrice();
	public abstract String columnOutbidPrice();
	public abstract String columnOutbidPriceToolTip();
	public abstract String columnOutbidCount();
	public abstract String columnOutbidCountToolTip();
	public abstract String columnOutbidDelta();
	public abstract String columnOutbidDeltaToolTip();
	public abstract String columnEveUi();
	public abstract String columnEveUiToolTip();
	public abstract String columnBrokersFee();
	public abstract String columnBrokersFeeToolTip();
	public abstract String columnEdits();
	public abstract String columnEditsToolTip();
	public abstract String columnPriceReprocessed();
	public abstract String columnPriceManufacturing();
	public abstract String columnPriceManufacturingToolTip();
	public abstract String columnIssued();
	public abstract String columnIssuedToolTip();
	public abstract String columnIssuedFirst();
	public abstract String columnIssuedFirstToolTip();
	public abstract String columnExpires();
	public abstract String columnChanged();
	public abstract String columnChangedToolTip();
	public abstract String columnRange();
	public abstract String columnRemainingValue();
	public abstract String columnStatus();
	public abstract String columnMinimumQuantity();
	public abstract String columnOwner();
	public abstract String columnIssuedBy();
	public abstract String columnOwned();
	public abstract String columnOwnedToolTip();
	public abstract String columnWalletDivision();
	public abstract String columnLocation();
	public abstract String columnSystem();
	public abstract String columnConstellation();
	public abstract String columnRegion();
	public abstract String columnTransactionPrice();
	public abstract String columnTransactionPriceToolTip();
	public abstract String columnTransactionMargin();
	public abstract String columnTransactionMarginToolTip();
	public abstract String columnTransactionProfitDifference();
	public abstract String columnTransactionProfitDifferenceToolTip();
	public abstract String columnTransactionProfitPercent();
	public abstract String columnTransactionProfitPercentToolTip();
	public abstract String columnMarketPrice();
	public abstract String columnMarketPriceToolTip();
	public abstract String columnMarketPriceSellMin();
	public abstract String columnMarketPriceSellMinToolTip();
	public abstract String columnMarketPriceBuyMax();
	public abstract String columnMarketPriceBuyMaxToolTip();
	public abstract String columnMarketMargin();
	public abstract String columnMarketMarginToolTip();
	public abstract String columnMarketProfit();
	public abstract String columnMarketProfitToolTip();
	public abstract String columnVolume();
	public abstract String columnTypeID();
	public abstract String columnOrderID();
	public abstract String eveUiOpen();
	public abstract String lastEsiUpdateToolTip();
	public abstract String lastLogUpdateToolTip();
	public abstract String lastClipboardToolTip();
	public abstract String logClear();
	public abstract String logClose();
	public abstract String logError();
	public abstract String logOK();
	public abstract String logTitle();
	public abstract String market();
	public abstract String marketLogTypeToolTip();
	public abstract String none();
	public abstract String ownerInvalidScopeMsg();
	public abstract String ownerInvalidScopeTitle();
	public abstract String ownerNotFoundMsg();
	public abstract String ownerNotFoundTitle();
	public abstract String rangeStation();
	public abstract String rangeSolarSystem();
	public abstract String rangeRegion();
	public abstract String rangeJump();
	public abstract String rangeJumps(String range);
	public abstract String sell();
	public abstract String sellOrderRangeToolTip();
	public abstract String sellOrderRangeLastToolTip();
	public abstract String sellOrderRangeSelcted(String selected);
	public abstract String status();
	public abstract String statusActive();
	public abstract String statusClosed();
	public abstract String statusFulfilled();
	public abstract String statusExpired();
	public abstract String statusPartiallyFulfilled();
	public abstract String statusCancelled();
	public abstract String statusPending();
	public abstract String statusCharacterDeleted();
	public abstract String statusUnknown();
	public abstract String totalSellOrders();
	public abstract String totalBuyOrders();
	public abstract String totalEscrow();
	public abstract String totalToCover();
	public abstract String unknownLocationsMsg();
	public abstract String unknownLocationsMsgLater();
	public abstract String unknownLocationsTitle();
	public abstract String updateNoActiveMsg();
	public abstract String updateNoActiveTitle();
	public abstract String updateOutbidEsi();
	public abstract String updateOutbidEsiAuto();
	public abstract String updateOutbidFileBuy();
	public abstract String updateOutbidFileSell();
	public abstract String updateOutbidWhen(String time);
	public abstract String updateOutbidUpdating();
	public abstract String updateTitle();
}
