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

package net.nikr.eve.jeveasset.gui.tabs.values;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsValues;


public enum ValueTableFormat implements EnumTableColumn<Value> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getName();
		}
	},
	TOTAL(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnTotal();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getTotal();
		}
	},
	BALANCE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnWalletBalance();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBalance();
		}
	},
	ASSETS(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnAssets();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getAssets();
		}
	},
	SELL_ORDERS(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnSellOrders();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getSellOrders();
		}
	},
	ESCROWS(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnEscrows();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getEscrows();
		}
	},
	ESCROWS_TO_COVER(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnEscrowsToCover();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getEscrowsToCover();
		}
	},
	MANUFACTURING(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnManufacturing();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getManufacturing();
		}
	},
	BEST_ASSET_NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnBestAsset();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBestAssetName();
		}
	},
	BEST_ASSET_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnBestAsset();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBestAssetValue();
		}
	},
	BEST_SHIP_FITTED_NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnBestShipFitted();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBestShipFittedName();
		}
	},
	BEST_SHIP_FITTED_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnBestShipFitted();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBestShipFittedValue();
		}
	},
	BEST_SHIP_NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnBestShip();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBestShipName();
		}
	},
	BEST_SHIP_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnBestShip();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBestShipValue();
		}
	},
	BEST_MODULE_NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnBestModule();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBestModuleName();
		}
	},
	BEST_MODULE_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnBestModule();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBestModuleValue();
		}
	};

	private Class<?> type;
	private Comparator<?> comparator;

	private ValueTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	@Override
	public boolean isColumnEditable(final Object baseObject) {
		return false;
	}
	@Override
	public boolean isShowDefault() {
		return true;
	}
	@Override
	public Value setColumnValue(final Object baseObject, final Object editedValue) {
		return null;
	}
	//XXX - TableFormat.getColumnValue(...) Workaround
	@Override public abstract Object getColumnValue(final Value from);
}
