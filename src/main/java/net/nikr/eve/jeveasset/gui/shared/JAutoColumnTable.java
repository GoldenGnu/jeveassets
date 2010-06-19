/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared;

import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import net.nikr.eve.jeveasset.data.MarketOrder.Quantity;
import net.nikr.eve.jeveasset.gui.dialogs.account.SeparatorTableCell;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.DoubleCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.FloatCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.IntegerCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.LongCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.QuantityCellRenderer;


public class JAutoColumnTable extends JTable {

	private final List<String> columnNames;
	private EventTableModel eventTableModel;
	private JScrollPane jScroll;

	public JAutoColumnTable(EventTableModel dm, List<String> columnNames) {
		setModel(dm);
		eventTableModel = dm;
		this.columnNames = columnNames;

		ModelListener modelListener = new ModelListener();

		jScroll = new JScrollPane(this);
		jScroll.addComponentListener(modelListener);
		getTableHeader().addMouseListener(modelListener);
		getColumnModel().addColumnModelListener(modelListener);
		dm.addTableModelListener(modelListener);
		this.setDefaultRenderer(Float.class, new FloatCellRenderer());
		this.setDefaultRenderer(Double.class, new DoubleCellRenderer());
		this.setDefaultRenderer(Long.class, new LongCellRenderer());
		this.setDefaultRenderer(Integer.class, new IntegerCellRenderer());
		this.setDefaultRenderer(Quantity.class, new QuantityCellRenderer());
	}

	public JScrollPane getScrollPanel() {
		return jScroll;
	}

	private void autoResizeColumns() {
		if (this.getRowCount() > 0){
			int size = 0;
			this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			for (int i = 0; i < this.getColumnCount(); i++) {
				 size = size+autoResizeColumn(this.getColumnModel().getColumn(i));
			}
			if (size < jScroll.getSize().width){
				this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			}
		} else {
			for (int i = 0; i < this.getColumnCount(); i++) {
				this.getColumnModel().getColumn(i).setPreferredWidth(75);
			}
			this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
	}

	private int autoResizeColumn(TableColumn column) {
		int maxWidth = 0;
		TableCellRenderer renderer = column.getHeaderRenderer();
		if (renderer == null) {
			renderer = this.getTableHeader().getDefaultRenderer();
		}
		Component component = renderer.getTableCellRendererComponent(this, column.getHeaderValue(), false, false, 0, 0);
		maxWidth = component.getPreferredSize().width;
		for (int a = 0; a < this.getRowCount(); a++){
			renderer = this.getCellRenderer(a, column.getModelIndex());
			if (renderer instanceof SeparatorTableCell) continue;
			component = renderer.getTableCellRendererComponent(this, this.getValueAt(a, column.getModelIndex()), false, false, a, column.getModelIndex());
			maxWidth = Math.max(maxWidth, component.getPreferredSize().width);
		}
		column.setPreferredWidth(maxWidth+4);
		return maxWidth+4;
	}

	

	class ModelListener implements TableModelListener, TableColumnModelListener, MouseListener, ComponentListener {

		private boolean columnMoved = false;
		private List<String> tempMainTableColumnNames;

		@Override
		public void tableChanged(TableModelEvent e) {
			autoResizeColumns();
		}

		@Override
		public void columnAdded(TableColumnModelEvent e) {}

		@Override
		public void columnRemoved(TableColumnModelEvent e) {}

		@Override
		public void columnMoved(TableColumnModelEvent e) {
			if (e.getFromIndex() != e.getToIndex()){
				columnMoved = true;

				String movingColumnName = tempMainTableColumnNames.get(e.getFromIndex());
				String movingToColumnName = tempMainTableColumnNames.get(e.getToIndex());

				int movingIndex = tempMainTableColumnNames.indexOf(movingColumnName);
				tempMainTableColumnNames.remove(movingIndex);

				int movingToIndex = tempMainTableColumnNames.indexOf(movingToColumnName);
				if (e.getToIndex() > e.getFromIndex()) movingToIndex = movingToIndex + 1;
				tempMainTableColumnNames.add(movingToIndex, movingColumnName);

				List<String> mainTableColumnVisible = new ArrayList<String>();
				String columnOrder = "";
				String columnVisible = "";
				for (int a = 0; a < tempMainTableColumnNames.size(); a++){
					columnOrder = columnOrder+tempMainTableColumnNames.get(a)+" ";
					if (columnNames.contains(tempMainTableColumnNames.get(a))){
						columnVisible = columnVisible+tempMainTableColumnNames.get(a)+" ";
						mainTableColumnVisible.add(tempMainTableColumnNames.get(a));
					}
				}
				tempMainTableColumnNames = mainTableColumnVisible;
			}
		}

		@Override
		public void columnMarginChanged(ChangeEvent e) {}

		@Override
		public void columnSelectionChanged(ListSelectionEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getSource().equals(getTableHeader())){
				tempMainTableColumnNames = new ArrayList<String>(columnNames);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getSource().equals(getTableHeader()) && columnMoved){
				columnMoved = false;
				columnNames.clear();
				columnNames.addAll(tempMainTableColumnNames);
				eventTableModel.fireTableStructureChanged();
				autoResizeColumns();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}


		@Override
		public void componentResized(ComponentEvent e) {
			autoResizeColumns();
		}

		@Override
		public void componentMoved(ComponentEvent e) {}

		@Override
		public void componentShown(ComponentEvent e) {}

		@Override
		public void componentHidden(ComponentEvent e) {}
	}
}
