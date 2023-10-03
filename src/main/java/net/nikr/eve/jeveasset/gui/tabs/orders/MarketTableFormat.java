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

package net.nikr.eve.jeveasset.gui.tabs.orders;

import ca.odell.glazedlists.GlazedLists;
import java.awt.Component;
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.LongInt;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.gui.shared.table.containers.YesNo;
import net.nikr.eve.jeveasset.i18n.TabsOrders;


public enum MarketTableFormat implements EnumTableColumn<MyMarketOrder> {
	ORDER_TYPE(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnOrderType();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			if (!from.isBuyOrder()) {
				return TabsOrders.get().sell();
			} else {
				return TabsOrders.get().buy();
			}
		}
	},
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnName();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getItem().getTypeName();
		}
	},
	GROUP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnGroup();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getItem().getGroup();
		}
	},
	QUANTITY(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnVolumeRemain();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getVolumeRemain();
		}
	},
	QUANTITY_ENTERED(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnVolumeTotal();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getVolumeTotal();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnPrice();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getPrice();
		}
	},
	OUTBID_PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnOutbidPrice();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnOutbidPriceToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getOutbidPrice();
		}
	},
	OUTBID_COUNT(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnOutbidCount();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnOutbidCountToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getOutbidCount();
		}
	},
	OUTBID_DELTA(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnOutbidDelta();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnOutbidDeltaToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getOutbidDelta();
		}
	},
	EVE_UI(Component.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnEveUi();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnEveUiToolTip();
		}
		@Override
		public boolean isColumnEditable(Object baseObject) {
			return true;
		}

		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getButton();
		}
	},
	BROKERS_FEE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnBrokersFee();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnBrokersFeeToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getBrokersFee();
		}
	},
	EDITS(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnEdits();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnEditsToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getEdits();
		}
	},
	PRICE_REPROCESSED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnPriceReprocessed();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getPriceReprocessed();
		}
	},
	PRICE_MANUFACTURING(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnPriceManufacturing();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnPriceManufacturingToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getItem().getPriceManufacturing();
		}
	},
	ISSUED(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnIssued();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnIssuedToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getIssued();
		}
	},
	ISSUED_FIRST(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnIssuedFirst();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnIssuedFirstToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getCreatedOrIssued();
		}
	},
	EXPIRES(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnExpires();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getExpires();
		}
	},
	CHANGED(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnChanged();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnChangedToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getChanged();
		}
	},
	RANGE(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnRange();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getRange().toString();
		}
	},
	STATUS(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnStatus();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getStateFormatted();
		}
	},
	MIN_VOLUME(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnMinimumQuantity();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getMinVolume();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getOwnerName();
		}
	},
	ISSUED_BY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnIssuedBy();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getIssuedByName();
		}
	},
	OWNED(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnOwned();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnOwnedToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return new YesNo(from.isOwned());
		}
	},
	WalletDivision(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnWalletDivision();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getWalletDivision();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnLocation();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getLocation().getLocation();
		}
	},
	SYSTEM(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnSystem();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getLocation().getSystem();
		}
	},
	CONSTELLATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnConstellation();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getLocation().getConstellation();
		}
	},
	REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnRegion();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getLocation().getRegion();
		}
	},
	VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnRemainingValue();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getVolumeRemain() * from.getPrice();
		}
	},
	TRANSACTION_PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnTransactionPrice();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnTransactionPriceToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getTransactionPrice();
		}
	},
	TRANSACTION_MARGIN(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnTransactionMargin();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnTransactionMarginToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getTransactionMargin();
		}
	},
	TRANSACTION_PROFIT(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnTransactionProfitDifference();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnTransactionProfitDifferenceToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getTransactionProfitDifference();
		}
	},
	TRANSACTION_PROFIT_PERCENT(Percent.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnTransactionProfitPercent();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnTransactionProfitPercentToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getTransactionProfitPercent();
		}
	},
	MARKET_PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnMarketPrice();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnMarketPriceToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getDynamicPrice();
		}
	},
	MARKET_PRICE_SELL_MIN(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnMarketPriceSellMin();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnMarketPriceSellMinToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getPriceSellMin();
		}
	},
	MARKET_PRICE_BUY_MAX(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnMarketPriceBuyMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnMarketPriceBuyMaxToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getPriceBuyMax();
		}
	},
	MARKET_MARGIN(Percent.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnMarketMargin();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnMarketMarginToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return Percent.create(from.getMarketMargin());
		}
	},
	MARKET_PROFIT(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnMarketProfit();
		}
		@Override
		public String getColumnToolTip() {
			return TabsOrders.get().columnMarketProfitToolTip();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getMarketProfit();
		}
	},
	VOLUME(Float.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnVolume();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getItem().getVolumePackaged();
		}
	},
	TYPE_ID(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnTypeID();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return from.getTypeID();
		}
	},
	ORDER_ID(LongInt.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnOrderID();
		}
		@Override
		public Object getColumnValue(final MyMarketOrder from) {
			return new LongInt(from.getOrderID());
		}
		@Override
		public boolean isShowDefault() {
			return false;
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private MarketTableFormat(final Class<?> type, final Comparator<?> comparator) {
		this.type = type;
		this.comparator = comparator;
	}
	@Override
	public Class<?> getType() {
		return type;
	}
	@Override
	public Comparator<?> getComparator() {
		return comparator;
	}
	@Override
	public String toString() {
		return getColumnName();
	}

}
