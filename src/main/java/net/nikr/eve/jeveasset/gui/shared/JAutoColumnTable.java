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
package net.nikr.eve.jeveasset.gui.shared;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.event.*;
import javax.swing.table.*;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.DateCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.DoubleCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.FloatCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.IntegerCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.LongCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.ToStringCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;


public class JAutoColumnTable extends JTable {
	
	private JViewport jViewport = null;
	private int size = 0;
	
	public JAutoColumnTable(TableModel tableModel) {
		super(tableModel);

		//Listeners
		ModelListener modelListener = new ModelListener();
		this.addHierarchyListener(modelListener);
		this.getModel().addTableModelListener(modelListener);
		this.addPropertyChangeListener("model", modelListener);
		this.getTableHeader().addMouseListener(modelListener);
		this.addPropertyChangeListener("tableHeader", modelListener);
		this.getColumnModel().addColumnModelListener(modelListener);
		this.addPropertyChangeListener("columnModel", modelListener);
		
		//Renders
		this.setDefaultRenderer(Float.class, new FloatCellRenderer());
		this.setDefaultRenderer(Double.class, new DoubleCellRenderer());
		this.setDefaultRenderer(Long.class, new LongCellRenderer());
		this.setDefaultRenderer(Integer.class, new IntegerCellRenderer());
		this.setDefaultRenderer(Date.class, new DateCellRenderer());
		this.setDefaultRenderer(String.class, new ToStringCellRenderer(SwingConstants.LEFT));
		this.setDefaultRenderer(Object.class, new ToStringCellRenderer());
	}
	
	public void autoResizeColumns() {
		resizeColumnsText(this);
	}
	
	private JTable getTable(){
		return this;
	}
	
	private EventTableModel getEventTableModel(){
		TableModel model = this.getModel();
		if (model instanceof EventTableModel){
			return (EventTableModel) model;
		} else {
			return null;
		}
	}
	
	private EnumTableFormatAdaptor getEnumTableFormatAdaptor(){
		if (getEventTableModel() != null){
			TableFormat tableFormat = getEventTableModel().getTableFormat();
			if (tableFormat instanceof EnumTableFormatAdaptor){
				return (EnumTableFormatAdaptor) tableFormat;
			}
		}
		return null;
	}

	private JViewport getParentViewport(){
		Container container = this.getParent();
		if (container instanceof JViewport){
			return (JViewport) container;
		} else {
			return null;
		}
	}
	
	private void resizeColumnsText(JTable jTable) {
		size = 0;
		for (int i = 0; i < jTable.getColumnCount(); i++) {
				size = size+resizeColumn(jTable, jTable.getColumnModel().getColumn(i), i);
		}
		updateScroll();
	}
	
	private void updateScroll(){
		if (jViewport != null && size < jViewport.getSize().width){
			getTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		} else {
			getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
	}

	private int resizeColumn(JTable jTable, TableColumn column, int columnIndex) {
		//Header
		TableCellRenderer renderer = column.getHeaderRenderer();
		if (renderer == null) renderer = jTable.getTableHeader().getDefaultRenderer();
		Component component = renderer.getTableCellRendererComponent(jTable, column.getHeaderValue(), false, false, 0, 0);
		int maxWidth = component.getPreferredSize().width;
		
		//Rows
		for (int a = 0; a < jTable.getRowCount(); a++){
			renderer = jTable.getCellRenderer(a, columnIndex);
			if (renderer instanceof SeparatorTableCell) continue;
			component = renderer.getTableCellRendererComponent(jTable, jTable.getValueAt(a, columnIndex), false, false, a, columnIndex);
			maxWidth = Math.max(maxWidth, component.getPreferredSize().width);
		}
		column.setPreferredWidth(maxWidth+4);
		return maxWidth+4;
	}

	private class ModelListener implements TableModelListener, ComponentListener,
			PropertyChangeListener, HierarchyListener, TableColumnModelListener, MouseListener{

		boolean columnMoved = false;
		int from = 0;
		int to = 0;
		private int rowsLastTime = 0;
		private int rowsCount = 0;
		
		@Override
		public void tableChanged(TableModelEvent e) {
			//FIXME JAutoColumnTable.tableChanged() removed jTable.isEditing() - this might cause a bug
			//if(getTable().isEditing()) getTable().getCellEditor().cancelCellEditing();
			if (e.getType() == TableModelEvent.DELETE) rowsCount = rowsCount - (Math.abs(e.getFirstRow()-e.getLastRow())+1);
			if (e.getType() == TableModelEvent.INSERT) rowsCount = rowsCount + (Math.abs(e.getFirstRow()-e.getLastRow())+1);
			if (Math.abs(rowsLastTime + rowsCount) == getRowCount() && e.getType() != TableModelEvent.UPDATE) { //Last Table Update
				rowsLastTime = getRowCount();
				rowsCount = 0;
				autoResizeColumns();
			}
		}

		@Override
		public void componentResized(ComponentEvent e) {
			updateScroll();
		}

		@Override
		public void componentMoved(ComponentEvent e) {}

		@Override
		public void componentShown(ComponentEvent e) {}

		@Override
		public void componentHidden(ComponentEvent e) {}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Object newValue = evt.getNewValue();
			Object oldValue = evt.getOldValue();
			if (newValue instanceof JTableHeader && oldValue instanceof JTableHeader){
				JTableHeader newModel = (JTableHeader) newValue;
				JTableHeader oldModel = (JTableHeader) oldValue;
				oldModel.removeMouseListener(this);
				newModel.addMouseListener(this);

			}
			if (newValue instanceof TableColumnModel && oldValue instanceof TableColumnModel){
				TableColumnModel newModel = (TableColumnModel) newValue;
				TableColumnModel oldModel = (TableColumnModel) oldValue;
				oldModel.removeColumnModelListener(this);
				newModel.addColumnModelListener(this);

			}
			if (newValue instanceof TableModel && oldValue instanceof TableModel){
				TableModel newModel = (TableModel) newValue;
				TableModel oldModel = (TableModel) oldValue;
				oldModel.removeTableModelListener(this);
				newModel.addTableModelListener(this);
			}
		}

		@Override
		public void hierarchyChanged(HierarchyEvent e) {
			if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) == HierarchyEvent.PARENT_CHANGED){
				if (jViewport != null) jViewport.removeComponentListener(this);
				jViewport = getParentViewport();
				if (jViewport != null) jViewport.addComponentListener(this);
			}
		}
		
		@Override
		public void columnAdded(TableColumnModelEvent e) {}

		@Override
		public void columnRemoved(TableColumnModelEvent e) {}
			
		@Override
		public void columnMoved(TableColumnModelEvent e) {
			if (e.getFromIndex() != e.getToIndex()){
				if (!columnMoved) from = e.getFromIndex();
				to = e.getToIndex();
				columnMoved = true;
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
			columnMoved = false;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (columnMoved){
				columnMoved = false;
				EnumTableFormatAdaptor tableFormat = getEnumTableFormatAdaptor();
				EventTableModel model = getEventTableModel();
				if (tableFormat != null && model != null){
					tableFormat.moveColumn(from, to);
					model.fireTableStructureChanged();
				}
				autoResizeColumns();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}
}