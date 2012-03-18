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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.gui.shared.JAutoColumnTable;


public class JAssetTable extends JAutoColumnTable {

	private EventTableModel<Asset> tableModel;

	private Program program;

	public JAssetTable(Program program, EventTableModel<Asset> tableModel) {
		super(tableModel);
		this.program = program;
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Asset asset = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
		
		//Default Colors
		component.setForeground(isSelected ? this.getSelectionForeground() : this.getForeground());
		component.setBackground(isSelected ? this.getSelectionBackground() : this.getBackground());

		//User set price
		if (asset.isUserPrice() && columnName.equals(EveAssetTableFormat.PRICE.getColumnName())){
			if (!isSelected){
				component.setBackground( new Color(230,230,230) );
			} else {
				component.setBackground( this.getSelectionBackground().darker() );
			}
			return component;
		}
		//User set name
		if (asset.isUserName() && columnName.equals(EveAssetTableFormat.NAME.getColumnName())){
			if (!isSelected){
				component.setBackground( new Color(230,230,230) );
			} else {
				component.setBackground( this.getSelectionBackground().darker() );
			}
			return component;
		}
		//Blueprint Original
		if (asset.isBpo()
				&& asset.isBlueprint()
				&& (columnName.equals(EveAssetTableFormat.PRICE.getColumnName())
				|| columnName.equals(EveAssetTableFormat.PRICE_SELL_MIN.getColumnName())
				|| columnName.equals(EveAssetTableFormat.PRICE_BUY_MAX.getColumnName())
				|| columnName.equals(EveAssetTableFormat.NAME.getColumnName()))){
			if (!isSelected){
				component.setBackground( new Color(255,255,200) );
			} else {
				component.setBackground( this.getSelectionBackground().darker() );
			}
			return component;
		}

		//Reproccessing Colors
		if (program.getSettings().isReprocessColors() && !isSelected){
			if (asset.getPriceReprocessed() > asset.getPrice()){ //Reprocessed highest
				if (this.isRowSelected(row) && program.getSettings().isHighlightSelectedRows()){
					component.setBackground( new Color(255,160,160) );
				} else {
					component.setBackground( new Color(255,200,200) );
				}
				return component;
			}
			if (asset.getPriceReprocessed() < asset.getPrice()){ //Price highest
				if (this.isRowSelected(row) && program.getSettings().isHighlightSelectedRows()){
					component.setBackground( new Color(160,255,160) );
				} else {
					component.setBackground( new Color(200,255,200) );
				}
				return component;
			}

		}

		//Reproccessed is greater then price
		if (asset.getPriceReprocessed() > asset.getPrice() && columnName.equals(EveAssetTableFormat.PRICE_REPROCESSED.getColumnName())){
			if (!isSelected){
				component.setBackground( new Color(255,255,200) );
			} else {
				component.setBackground( this.getSelectionBackground().darker() );
			}
			return component;
		}

		//Selected row highlighting
		if (this.isRowSelected(row) && !isSelected && program.getSettings().isHighlightSelectedRows()){
			component.setBackground( new Color(220,240,255) );
			return component;
		}
		return component;
	}
}
