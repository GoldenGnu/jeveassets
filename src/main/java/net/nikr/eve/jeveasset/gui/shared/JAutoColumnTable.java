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

package net.nikr.eve.jeveasset.gui.shared;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import net.nikr.eve.jeveasset.data.ISK;
import net.nikr.eve.jeveasset.data.MarketOrder.Quantity;
import net.nikr.eve.jeveasset.data.Module.ModulePriceValue;
import net.nikr.eve.jeveasset.gui.shared.JColumnTable.TableColumnUtil;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.DateCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.DoubleCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.FloatCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.IntegerCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.LongCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.ToStringCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;


public class JAutoColumnTable extends JTable {

	private JScrollPane jScroll;
	private EventTableModel eventTableModel;
	private EnumTableFormatAdaptor formatAdaptor = null;

	public JAutoColumnTable(EventTableModel eventTableModel) {
		setModel(eventTableModel);
		this.eventTableModel = eventTableModel;

		TableFormat tableFormat = eventTableModel.getTableFormat();
		if (tableFormat instanceof EnumTableFormatAdaptor){
			formatAdaptor = (EnumTableFormatAdaptor) tableFormat;
		}

		//Scroll
		jScroll = new JScrollPane(this);

		//Listeners
		ModelListener modelListener = new ModelListener();
		jScroll.addComponentListener(modelListener);
		eventTableModel.addTableModelListener(modelListener);
		this.addPropertyChangeListener("model", modelListener);
		this.getColumnModel().addColumnModelListener(modelListener);
		this.getTableHeader().addMouseListener(modelListener);

		//Renders
		this.setDefaultRenderer(Float.class, new FloatCellRenderer());
		this.setDefaultRenderer(Double.class, new DoubleCellRenderer());
		this.setDefaultRenderer(Long.class, new LongCellRenderer());
		this.setDefaultRenderer(Integer.class, new IntegerCellRenderer());
		this.setDefaultRenderer(Date.class, new DateCellRenderer());
		this.setDefaultRenderer(Quantity.class, new ToStringCellRenderer());
		this.setDefaultRenderer(ISK.class, new ToStringCellRenderer());
		this.setDefaultRenderer(ModulePriceValue.class, new ToStringCellRenderer());
	}

	public JScrollPane getScrollPanel() {
		return jScroll;
	}

	private void autoResizeColumns() {
		TableColumnUtil.resizeColumnsText(this, jScroll);
	}

	class ModelListener implements TableModelListener, ComponentListener, PropertyChangeListener, TableColumnModelListener, MouseListener{

		boolean columnMoved = false;
		int from = 0;
		int to = 0;

		@Override
		public void tableChanged(TableModelEvent e) {
			autoResizeColumns();
		}

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

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Object newValue = evt.getNewValue();
			Object oldValue = evt.getOldValue();
			if (newValue instanceof TableModel && oldValue instanceof TableModel){
				TableModel newModel = (TableModel) newValue;
				TableModel oldModel = (TableModel) oldValue;
				oldModel.removeTableModelListener(this);
				newModel.addTableModelListener(this);

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
				if (formatAdaptor != null) formatAdaptor.moveColumn(from, to);
				eventTableModel.fireTableStructureChanged();
				autoResizeColumns();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}
}