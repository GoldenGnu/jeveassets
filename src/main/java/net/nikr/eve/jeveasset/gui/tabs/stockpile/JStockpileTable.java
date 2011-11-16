/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;


public class JStockpileTable extends JSeparatorTable{

	private EventTableModel tableModel;
	
	public JStockpileTable(EventTableModel tableModel) {
		super(tableModel);
		this.tableModel = tableModel;
	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Object object = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
		
		//Default Foreground
		component.setForeground(isSelected ? this.getSelectionForeground() : Color.BLACK);
		
		if (object instanceof StockpileItem){
			StockpileItem stockpileItem = (StockpileItem) object;
			//Background
			if (columnName.equals(StockpileTableFormat.NAME.getColumnName())){
				component.setForeground(Color.BLACK);
				if (stockpileItem.isOK()){
					component.setBackground( new Color(200,255,200) );
				} else {
					component.setBackground( new Color(255,200,200) );
				}
			} else if (isSelected){ //Selected
				component.setBackground(this.getSelectionBackground());
			} else if (object instanceof StockpileTotal){ //Total
				component.setBackground( new Color(255,255,200) );
			} else { //Default
				component.setBackground(Color.WHITE);
			}
			//Foreground
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_INVENTORY.getColumnName()) && !stockpileItem.getStockpile().isInventory()) {
				component.setForeground(Color.GRAY);
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_BUY_ORDERS.getColumnName()) && !stockpileItem.getStockpile().isBuyOrders()) {
				component.setForeground(Color.GRAY);
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_SELL_ORDERS.getColumnName()) && !stockpileItem.getStockpile().isSellOrders()) {
				component.setForeground(Color.GRAY);
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_JOBS.getColumnName()) && !stockpileItem.getStockpile().isJobs()) {
				component.setForeground(Color.GRAY);
			}
			if (columnName.equals(StockpileTableFormat.COUNT_NEEDED.getColumnName()) && stockpileItem.getCountNeeded() < 0){
				component.setForeground(Color.RED.darker());
			}
			if (columnName.equals(StockpileTableFormat.VALUE_NEEDED.getColumnName()) && stockpileItem.getValueNeeded() < 0){
				component.setForeground(Color.RED.darker());
			}
			if (columnName.equals(StockpileTableFormat.VOLUME_NEEDED.getColumnName()) && stockpileItem.getVolumeNeeded() < 0){
				component.setForeground(Color.RED.darker());
			}
		}
		return component;
	}
	
}
