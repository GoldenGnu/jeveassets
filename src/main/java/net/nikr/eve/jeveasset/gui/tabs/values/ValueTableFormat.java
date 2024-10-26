/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
			return from.getBalanceTotal();
		}
	},
	DIVISION_1(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnWalletDivision1();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBalanceFilter().get("1");
		}
	},
	DIVISION_2(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnWalletDivision2();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBalanceFilter().get("2");
		}
	},
	DIVISION_3(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnWalletDivision3();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBalanceFilter().get("3");
		}
	},
	DIVISION_4(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnWalletDivision4();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBalanceFilter().get("4");
		}
	},
	DIVISION_5(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnWalletDivision5();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBalanceFilter().get("5");
		}
	},
	DIVISION_6(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnWalletDivision6();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBalanceFilter().get("6");
		}
	},
	DIVISION_7(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnWalletDivision7();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getBalanceFilter().get("7");
		}
	},
	ASSETS(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnAssets();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getAssetsTotal();
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
	CONTRACT_COLLATERAL(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnContractCollateral();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getContractCollateral();
		}
	},
	CONTRACT_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnContractValue();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getContractValue();
		}
	},
	SKILL_POINT_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnSkillPointValue();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getSkillPointValue();
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
	},
	CURRENT_SHIP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnCurrentShip();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getActiveShip();
		}
	},
	CURRENT_STATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnCurrentStation();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getCurrentStation();
		}
	},
	CURRENT_SYSTEM(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnCurrentSystem();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getCurrentSystem();
		}
	},
	CURRENT_CONSTELLATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnCurrentConstellation();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getCurrentConstellation();
		}
	},
	CURRENT_REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsValues.get().columnCurrentRegion();
		}
		@Override
		public Object getColumnValue(final Value from) {
			return from.getCurrentRegion();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

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

}
