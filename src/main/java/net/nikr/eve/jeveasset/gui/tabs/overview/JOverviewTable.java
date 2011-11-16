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

package net.nikr.eve.jeveasset.gui.tabs.overview;

import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.data.Overview;
import net.nikr.eve.jeveasset.gui.shared.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.DoubleCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.FloatCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.IntegerCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.LongCellRenderer;


class JOverviewTable extends JAutoColumnTable{

	private DoubleCellRenderer doubleCellRenderer;
	private LongCellRenderer longCellRenderer;
	private TableCellRenderer tableCellRenderer;
	private IntegerCellRenderer integerCellRenderer;
	private FloatCellRenderer floatCellRenderer;
	private List<String> groupedLocations = new ArrayList<String>();
	private EventTableModel eventTableModel;

	public JOverviewTable(EventTableModel eventTableModel) {
		super(eventTableModel);
		this.eventTableModel = eventTableModel;

		doubleCellRenderer = new DoubleCellRenderer();
		longCellRenderer = new LongCellRenderer();
		integerCellRenderer = new IntegerCellRenderer();
		floatCellRenderer = new FloatCellRenderer();
		tableCellRenderer = new DefaultTableCellRenderer();
	}

	public void setGroupedLocations(List<String> groupedLocations) {
		this.groupedLocations = groupedLocations;
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
			Overview overview = (Overview) eventTableModel.getElementAt(row);
			if (groupedLocations.contains(overview.getName()) && columnName.equals(OverviewTableFormat.NAME.getColumnName())){ //In group
				Component c = this.getMatchingTableCellRendererComponent(value, isSelected, hasFocus, row, column);
				if (!isSelected){
					c.setBackground( new Color(200,255,200) );
				} else {
					c.setBackground( this.getSelectionBackground().darker() );
				}
				return c;
			}
			if (groupedLocations.contains(overview.getSolarSystem()) && columnName.equals(OverviewTableFormat.SYSTEM.getColumnName())){ //In group
				Component c = this.getMatchingTableCellRendererComponent(value, isSelected, hasFocus, row, column);
				if (!isSelected){
					c.setBackground( new Color(200,255,200) );
				} else {
					c.setBackground( this.getSelectionBackground().darker() );
				}
				return c;
			}
			if (groupedLocations.contains(overview.getRegion()) && columnName.equals(OverviewTableFormat.REGION.getColumnName())){ //In group
				Component c = this.getMatchingTableCellRendererComponent(value, isSelected, hasFocus, row, column);
				if (!isSelected){
					c.setBackground( new Color(200,255,200) );
				} else {
					c.setBackground( this.getSelectionBackground().darker() );
				}
				return c;
			}

		}



		return super.prepareRenderer(renderer, row, column);
	}

	private Component getMatchingTableCellRendererComponent(Object value, boolean isSelected, boolean hasFocus, int row, int column){
		Component c = tableCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		if (value instanceof Integer) c = integerCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		if (value instanceof Float) c = floatCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		if (value instanceof Double) c = doubleCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		if (value instanceof Long) c = longCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		return c;
	}
}
