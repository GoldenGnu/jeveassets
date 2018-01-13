/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.Colors;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;


public class JStockpileTable extends JSeparatorTable {

	private final DefaultEventTableModel<StockpileItem> tableModel;

	public JStockpileTable(final Program program, final DefaultEventTableModel<StockpileItem> tableModel, SeparatorList<?> separatorList) {
		super(program, tableModel, separatorList);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Object object = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		if (object instanceof StockpileItem) {
			StockpileItem stockpileItem = (StockpileItem) object;
			//Background
			if (columnName.equals(StockpileTableFormat.NAME.getColumnName())) {
				component.setForeground(Color.BLACK);
				if (isSelected) {
					component.setBackground(this.getSelectionBackground().darker());
				} else if (Settings.get().isStockpileHalfColors()) {
					if (stockpileItem.getPercentNeeded() >= (Settings.get().getStockpileColorGroup3() / 100.0) ) {
						//Group 3
						component.setBackground(Colors.LIGHT_GREEN.getColor());
					} else if (stockpileItem.getPercentNeeded() >= (Settings.get().getStockpileColorGroup2() / 100.0) ) {
						//Group 2
						component.setBackground(Colors.LIGHT_YELLOW.getColor());
					} else {
						//Group 1
						component.setBackground(Colors.LIGHT_RED.getColor());
					}
				} else {
					if (stockpileItem.getPercentNeeded() >= (Settings.get().getStockpileColorGroup2() / 100.0) ) {
						//Group 2
						component.setBackground(Colors.LIGHT_GREEN.getColor());
					} else {
						//Group 1
						component.setBackground(Colors.LIGHT_RED.getColor());
					}
				}
			} else if (object instanceof StockpileTotal) { //Total
				if (!isSelected) {
					component.setBackground(Colors.LIGHT_GRAY.getColor());
				} else {
					component.setBackground(this.getSelectionBackground().darker());
				}
			}
			//Foreground
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_INVENTORY.getColumnName()) && !stockpileItem.getStockpile().isAssets()) {
				component.setForeground(component.getBackground());
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_BUY_ORDERS.getColumnName()) && !stockpileItem.getStockpile().isBuyOrders()) {
				component.setForeground(component.getBackground());
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_SELL_ORDERS.getColumnName()) && !stockpileItem.getStockpile().isSellOrders()) {
				component.setForeground(component.getBackground());
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_BUY_TRANSACTIONS.getColumnName()) && !stockpileItem.getStockpile().isBuyTransactions()) {
				component.setForeground(component.getBackground());
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_SELL_TRANSACTIONS.getColumnName()) && !stockpileItem.getStockpile().isSellTransactions()) {
				component.setForeground(component.getBackground());
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_JOBS.getColumnName()) && !stockpileItem.getStockpile().isJobs()) {
				component.setForeground(component.getBackground());
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_SELLING_CONTRACTS.getColumnName()) && !stockpileItem.getStockpile().isSellingContracts()) {
				component.setForeground(component.getBackground());
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_SOLD_CONTRACTS.getColumnName()) && !stockpileItem.getStockpile().isSoldContracts()) {
				component.setForeground(component.getBackground());
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_BUYING_CONTRACTS.getColumnName()) && !stockpileItem.getStockpile().isBuyingContracts()) {
				component.setForeground(component.getBackground());
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_BOUGHT_CONTRACTS.getColumnName()) && !stockpileItem.getStockpile().isBoughtContracts()) {
				component.setForeground(component.getBackground());
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NEEDED.getColumnName()) && stockpileItem.getCountNeeded() < 0) {
				if (!isSelected) {
					component.setForeground(Color.RED.darker());
				} else {
					component.setForeground(Colors.LIGHT_RED.getColor());
				}
			}
			if (columnName.equals(StockpileTableFormat.VALUE_NEEDED.getColumnName()) && stockpileItem.getValueNeeded() < 0) {
				if (!isSelected) {
					component.setForeground(Color.RED.darker());
				} else {
					component.setForeground(Colors.LIGHT_RED.getColor());
				}
			}
			if (columnName.equals(StockpileTableFormat.VOLUME_NEEDED.getColumnName()) && stockpileItem.getVolumeNeeded() < 0) {
				if (!isSelected) {
					component.setForeground(Color.RED.darker());
				} else {
					component.setForeground(Colors.LIGHT_RED.getColor());
				}
			}
		}
		return component;
	}
}
