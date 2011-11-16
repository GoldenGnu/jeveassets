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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.TableSettings;
import net.nikr.eve.jeveasset.gui.shared.JColumnTable;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.DoubleCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.FloatCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.LongCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.IntegerCellRenderer;


public class JAssetTable extends JColumnTable {

	private EventTableModel<Asset> eventTableModel;
	private DoubleCellRenderer doubleCellRenderer;
	private LongCellRenderer longCellRenderer;
	private TableCellRenderer tableCellRenderer;
	private IntegerCellRenderer integerCellRenderer;
	private FloatCellRenderer floatCellRenderer;

	private Program program;

	public JAssetTable(Program program, EventTableModel<Asset> eventTableModel, TableSettings tableSettings) {
		super(eventTableModel, tableSettings);
		this.program = program;
		this.eventTableModel = eventTableModel;

		doubleCellRenderer = new DoubleCellRenderer();
		longCellRenderer = new LongCellRenderer();
		integerCellRenderer = new IntegerCellRenderer();
		floatCellRenderer = new FloatCellRenderer();
		tableCellRenderer = new DefaultTableCellRenderer();
		this.setDefaultRenderer(Double.class, new DoubleCellRenderer());
		this.setDefaultRenderer(Long.class, new LongCellRenderer());
		this.setDefaultRenderer(Float.class, new FloatCellRenderer());
		this.setDefaultRenderer(Integer.class, new IntegerCellRenderer());
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		Object value = getValueAt(row, column);

        boolean isSelected = false;
        boolean hasFocus = false;

        // Only indicate the selection and focused cell if not printing
        if (!isPaintingForPrint()) {
            isSelected = isCellSelected(row, column);

            boolean rowIsLead =
                (selectionModel.getLeadSelectionIndex() == row);
            boolean colIsLead =
                (columnModel.getSelectionModel().getLeadSelectionIndex() == column);

            hasFocus = (rowIsLead && colIsLead) && isFocusOwner();
        }

		
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
		if (eventTableModel.getRowCount() >= row){
			Asset eveAsset = eventTableModel.getElementAt(row);
			//User set price
			if (eveAsset.isUserPrice() && columnName.equals("Price")){
				Component c = doubleCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				if (!isSelected){
					c.setBackground( new Color(230,230,230) );
				} else {
					c.setBackground( this.getSelectionBackground().darker() );
				}
				return c;
			}
			//Blueprint Original
			if (eveAsset.isBpo()
					&& eveAsset.isBlueprint()
					&& (columnName.equals("Price")
					|| columnName.equals("Sell Min")
					|| columnName.equals("Buy Max")
					|| columnName.equals("Name"))){
				Component c;
				if (columnName.equals("Name")){
					c = tableCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				} else {
					c = doubleCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				}
				if (!isSelected){
					c.setBackground( new Color(255,255,200) );
				} else {
					c.setBackground( this.getSelectionBackground().darker() );
				}
				return c;
			}
			
			//Reproccessing Colors
			if (program.getSettings().isReprocessColors() && !isSelected){
				Component c = getMatchingTableCellRendererComponent(value, isSelected, hasFocus, row, column);
				if (eveAsset.getPriceReprocessed() > eveAsset.getPrice()){ //Reprocessed highest
					if (this.isRowSelected(row) && program.getSettings().isHighlightSelectedRows()){
						c.setBackground( new Color(255,160,160) );
					} else {
						c.setBackground( new Color(255,200,200) );
					}
					return c;
				}
				if (eveAsset.getPriceReprocessed() < eveAsset.getPrice()){ //Price highest
					if (this.isRowSelected(row) && program.getSettings().isHighlightSelectedRows()){
						c.setBackground( new Color(160,255,160) );
					} else {
						c.setBackground( new Color(200,255,200) );
					}
					return c;
				}
				
			}

			//Reproccessed is greater then price
			if (eveAsset.getPriceReprocessed() > eveAsset.getPrice() && columnName.equals("Reprocessed")){
				Component c = doubleCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				if (!isSelected){
					c.setBackground( new Color(255,255,200) );
				} else {
					c.setBackground( this.getSelectionBackground().darker() );
				}
				return c;
			}

			//Selected row highlighting
			if (this.isRowSelected(row) && !isSelected && program.getSettings().isHighlightSelectedRows()){
				Component c = getMatchingTableCellRendererComponent(value, isSelected, hasFocus, row, column);
				c.setBackground( new Color(220,240,255) );
				return c;
			}
			
		}
		return super.prepareRenderer(renderer, row, column);
	}

	private Component getMatchingTableCellRendererComponent(Object value, boolean isSelected, boolean hasFocus, int row, int column){
		Component c = tableCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		if (getColumnClass(column).equals(Integer.class)) c = integerCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		if (getColumnClass(column).equals(Float.class)) c = floatCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		if (getColumnClass(column).equals(Double.class)) c = doubleCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		if (getColumnClass(column).equals(Long.class)) c = longCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		return c;
	}

	
}
