/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.gui.shared.JAutoColumnTable;


public class JMarketOrdersTable extends JAutoColumnTable {

	private EventTableModel<MarketOrder> tableModel;
	
	public JMarketOrdersTable(EventTableModel<MarketOrder> tableModel) {
		super(tableModel);
		this.tableModel = tableModel;
	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		MarketOrder marketOrder = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
		
		//Default Colors
		component.setForeground(isSelected ? this.getSelectionForeground() : this.getForeground());
		component.setBackground(isSelected ? this.getSelectionBackground() : this.getBackground());
		
		if (columnName.equals(MarketTableFormat.EXPIRES.getColumnName())){
			if (marketOrder.getExpires().before(new Date())){
				if (isSelected){
					component.setBackground( this.getSelectionBackground().darker() );
				} else {
					component.setBackground( new Color(255,200,200) );
				}
			}
		}
		return component;
	}
	
}
