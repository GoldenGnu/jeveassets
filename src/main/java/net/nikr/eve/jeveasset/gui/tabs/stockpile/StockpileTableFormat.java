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
package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.GlazedLists;
import java.awt.Component;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileStock;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public enum StockpileTableFormat implements EnumTableColumn<StockpileItem> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnName();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getName();
		}
	},
	TAGS(Tags.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnTags();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getTags();
		}
	},
	GROUP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnGroup();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getItem().getGroup();
		}
	},
	CATEGORY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCategory();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getItem().getCategory();
		}
	},
	META(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnMeta();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getMeta();
		}
	},
	COUNT_MINIMUM(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountMinimum();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getCountMinimum();
		}
		@Override
		public boolean isColumnEditable(final Object baseObject) {
			if (baseObject instanceof StockpileItem) {
				return ((StockpileItem)baseObject).isEditable();
			}
			return false;
		}
		@Override
		public boolean setColumnValue(final Object baseObject, final Object editedValue) {
			if ((editedValue instanceof Double) && (baseObject instanceof StockpileItem)) {
				StockpileItem item = (StockpileItem) baseObject;
				double before = item.getCountMinimum();
				double after = (Double) editedValue;
				item.setCountMinimum(after);
				return before != after;
			}
			return false;
		}
	},
	COUNT_MINIMUM_MULTIPLIED(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountMinimumMultiplied();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getCountMinimumMultiplied();
		}
	},
	COUNT_NOW(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNow();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getCountNow();
		}
	},
	COUNT_NEEDED(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNeeded();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getCountNeeded();
		}
	},
	PERCENT_NEEDED(Percent.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnPercentNeeded();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return Percent.create(from.getPercentNeeded());
		}
	},
	COUNT_NOW_INVENTORY(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNowInventory();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getInventoryCountNow();
		}
	},
	COUNT_NOW_BUY_ORDERS(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNowBuyOrders();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getBuyOrdersCountNow();
		}
	},
	COUNT_NOW_SELL_ORDERS(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNowSellOrders();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getSellOrdersCountNow();
		}
	},
	COUNT_NOW_BUY_TRANSACTIONS(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNowBuyTransactions();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getBuyTransactionsCountNow();
		}
	},
	COUNT_NOW_SELL_TRANSACTIONS(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNowSellTransactions();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getSellTransactionsCountNow();
		}
	},
	COUNT_NOW_JOBS(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNowJobs();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getJobsCountNow();
		}
	},
	COUNT_NOW_BUYING_CONTRACTS(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNowBuyingContracts();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getBuyingContractsCountNow();
		}
	},
	COUNT_NOW_BOUGHT_CONTRACTS(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNowBoughtContracts();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getBoughtContractsCountNow();
		}
	},
	COUNT_NOW_SELLING_CONTRACTS(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNowSellingContracts();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getSellingContractsCountNow();
		}
	},
	COUNT_NOW_SOLD_CONTRACTS(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNowSoldContracts();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getSoldContractsCountNow();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnPrice();
		}
		@Override
		public String getColumnToolTip() {
			return TabsStockpile.get().columnPriceToolTip();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getDynamicPrice();
		}
	},
	PRICE_SELL_MIN(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnPriceSellMin();
		}
		@Override
		public String getColumnToolTip() {
			return TabsStockpile.get().columnPriceSellMinToolTip();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getPriceSellMin();
		}
	},
	PRICE_BUY_MAX(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnPriceBuyMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsStockpile.get().columnPriceSellMinToolTip();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getPriceBuyMax();
		}
	},
	PRICE_TRANSACTION_AVERAGE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnPriceTransactionAverage();
		}

		@Override
		public String getColumnToolTip() {
			return TabsStockpile.get().columnPriceTransactionAverageToolTip();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getTransactionAveragePrice();
		}
	},
	EVE_UI(Component.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnEveUi();
		}
		@Override
		public String getColumnToolTip() {
			return TabsStockpile.get().columnEveUiToolTip();
		}
		@Override
		public boolean isColumnEditable(Object baseObject) {
			if (baseObject instanceof StockpileTotal || baseObject instanceof SubpileStock) {
				return false;
			}
			return true;
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getButton();
		}
	},
	VALUE_NOW(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnValueNow();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getValueNow();
		}
	},
	VALUE_NEEDED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnValueNeeded();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getValueNeeded();
		}
	},
	VOLUME_NOW(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnVolumeNow();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getVolumeNow();
		}
	},
	VOLUME_NEEDED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnVolumeNeeded();
		}
		@Override
		public Object getColumnValue(final StockpileItem from) {
			return from.getVolumeNeeded();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private StockpileTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
