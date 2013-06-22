/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.table;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.TableCellRenderers.DateCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableCellRenderers.DoubleCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableCellRenderers.FloatCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableCellRenderers.IntegerCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableCellRenderers.LongCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableCellRenderers.ToStringCellRenderer;


public class JAutoColumnTable extends JTable {

	private JViewport jViewport = null;
	private int size = 0;
	private ResizeMode resizeMode = null;
	private boolean loadingWidth = false;
	private final Map<String, Integer> columnsWidth = new HashMap<String, Integer>();
	private final Map<Integer, Integer> rowsWidth = new HashMap<Integer, Integer>();
	protected Program program;
	private boolean autoResizeLock = false;
	private final Set<Class> disableColumnResizeCache = new HashSet<Class>();

	public JAutoColumnTable(final Program program, final TableModel tableModel) {
		super(tableModel);
		this.program = program;

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

		autoResizeColumns();

		fixScrollPaneRedraw();
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);

		if (component instanceof JPanel) { //Ignore Separator Panels
			return component;
		}

		//Default Colors
		if (isCellSelected(row, column)) {
			component.setForeground(this.getSelectionForeground());
			component.setBackground(this.getSelectionBackground());
		} else {
			component.setForeground(this.getForeground());
			//Highlight selected row
			if (Settings.get().isHighlightSelectedRows() && this.isRowSelected(row)) {
				component.setBackground(new Color(220, 240, 255));
				return component;
			} else {
				component.setBackground(this.getBackground());
			}
		}
		return component;
	}

	@Override
	public Component prepareEditor(TableCellEditor editor, int row, int column) {
		Component component = super.prepareEditor(editor, row, column);
		if (component instanceof JTextComponent) {
			JTextComponent jTextComponent = (JTextComponent) component;
			jTextComponent.selectAll();
		}
		return component;
	}

	public void lock() {
		autoResizeLock = true;
	}

	public void unlock() {
		if (isLocked()) { //only if locked
			autoResizeLock = false; //unlock
			autoResizeColumns(); //Update after unlock
		}
	}

	public boolean isLocked() {
		return autoResizeLock;
	}

	public final void autoResizeColumns() {
		if (isLocked()) {
			return;
		}
		EnumTableFormatAdaptor<?, ?> tableFormat = getEnumTableFormatAdaptor();
		loadingWidth = true;
		if (tableFormat == null || tableFormat.getResizeMode() == ResizeMode.TEXT) {
			resizeColumnsText();
		} else if (tableFormat.getResizeMode() == ResizeMode.WINDOW) {
			resizeColumnsWindow();
		} else if (tableFormat.getResizeMode() == ResizeMode.NONE) {
			resizeColumnsNone();
		}
		loadingWidth = false;
	}

	public void setColumnsWidth(final Map<String, Integer> columnsWidth) {
		if (columnsWidth != null) {
			this.columnsWidth.putAll(columnsWidth);
		}
	}

	public Map<String, Integer> getColumnsWidth() {
		return columnsWidth;
	}

	public void disableColumnResizeCache(Class columnClass) {
		disableColumnResizeCache.add(columnClass);
	}

	public void enableColumnResizeCache(Class columnClass) {
		disableColumnResizeCache.remove(columnClass);
	}

	private JTable getTable() {
		return this;
	}

	private DefaultEventTableModel<?> getEventTableModel() {
		TableModel model = this.getModel();
		if (model instanceof DefaultEventTableModel) {
			return (DefaultEventTableModel) model;
		} else {
			return null;
		}
	}

	private EnumTableFormatAdaptor<?, ?> getEnumTableFormatAdaptor() {
		if (getEventTableModel() != null) {
			TableFormat<?> tableFormat = getEventTableModel().getTableFormat();
			if (tableFormat instanceof EnumTableFormatAdaptor) {
				return (EnumTableFormatAdaptor) tableFormat;
			}
		}
		return null;
	}

	private JScrollPane getParentScrollPane() {
		Container container = this.getParent();
		if (container != null) {
			container = container.getParent();
		}

		if (container instanceof JScrollPane) {
			return (JScrollPane) container;
		} else {
			return null;
		}
	}

	/**
	 * This is a work-around for issue #254. The JScrollPane viewport gets
	 * corrupted when it is moved to the right with the horizontal scrollbar.
	 * The following AdjustmentListener cannot fix the issue but it forces
	 * AWT to repaint the viewport content. Because the event firing frequency
	 * is lower than the viewport scroll rate, it may still flicker during
	 * the scrolling, but at least ensures that the viewport is drawn properly
	 * when the scrolling is stopped.
	 * This is bug somewhere between OpenJDK and certain graphics drivers
	 * under Linux and can be fixed by disabling the driver's acceleration.
	 * @author Jan
	 */
	private void fixScrollPaneRedraw() {
		/* This component has not been added to the JScrollPanel at
		 * construction time. This one listens to an ANCESTOR_ADD
		 * event and registers the repaint method at the JScrollPanel
		 * parent as soon as this component has been added to it.
		 */
		this.addAncestorListener(new AncestorListener() {

			@Override
			public void ancestorAdded(final AncestorEvent ae) {
				JComponent jComponent = ae.getComponent();
				if (jComponent instanceof JAutoColumnTable) {
					JAutoColumnTable jTable = (JAutoColumnTable) jComponent;
					JScrollPane jScrollPane = jTable.getParentScrollPane();
					if (jScrollPane != null) {
						jScrollPane.getHorizontalScrollBar().addAdjustmentListener(new JScrollPaneAdjustmentListener(jScrollPane));
					}
				}
			}

			@Override
			public void ancestorMoved(final AncestorEvent event) { }

			@Override
			public void ancestorRemoved(final AncestorEvent event) { }

		});
	}

	private JViewport getParentViewport() {
		Container container = this.getParent();
		if (container instanceof JViewport) {
			return (JViewport) container;
		} else {
			return null;
		}
	}

	private void resizeColumnsText() {
		size = 0;
		if (resizeMode != ResizeMode.TEXT) {
			resizeMode = ResizeMode.TEXT;
			this.getTableHeader().setResizingAllowed(false);
		}
		for (int i = 0; i < getColumnCount(); i++) {
			size = size + resizeColumn(this, getColumnModel().getColumn(i), i);
		}
		updateScroll();
	}

	public void resizeColumnsWindow() {
		if (resizeMode != ResizeMode.WINDOW) { //Only do once
			resizeMode = ResizeMode.WINDOW;
			this.getTableHeader().setResizingAllowed(true);
			for (int i = 0; i < getColumnCount(); i++) {
				getColumnModel().getColumn(i).setPreferredWidth(75);
			}
		}
		updateScroll();
	}
	public void resizeColumnsNone() {
		if (resizeMode != ResizeMode.NONE) { //Only do once
			resizeMode = ResizeMode.NONE;
			this.getTableHeader().setResizingAllowed(true);
		}
		EnumTableFormatAdaptor<?, ?> tableFormat = getEnumTableFormatAdaptor();
		List<SimpleColumn> columns = tableFormat.getColumns();
		int i = 0;
		for (SimpleColumn column : columns) {
			if (column.isShown()) {
				Integer width = columnsWidth.get(column.getEnumName());
				if (width != null) {
					getColumnModel().getColumn(i).setPreferredWidth(width);
				}
				i++;
			}
		}
		updateScroll();
	}

	private void updateScroll() {
		EnumTableFormatAdaptor<?, ?> tableFormat = getEnumTableFormatAdaptor();
		if (tableFormat == null || tableFormat.getResizeMode() == ResizeMode.TEXT) {
			if (jViewport != null && size < jViewport.getSize().width) {
				this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			} else {
				this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			}
		} else if (tableFormat.getResizeMode() == ResizeMode.WINDOW) {
			this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		} else if (tableFormat.getResizeMode() == ResizeMode.NONE) {
			this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
	}

	private int resizeColumn(final JTable jTable, final TableColumn column, final int columnIndex) {
		//Header width
		TableCellRenderer renderer = column.getHeaderRenderer();
		if (renderer == null) {
			renderer = jTable.getTableHeader().getDefaultRenderer();
		}
		Component component = renderer.getTableCellRendererComponent(jTable, column.getHeaderValue(), false, false, 0, 0);
		int maxWidth = component.getPreferredSize().width;

		//Rows width
		for (int i = 0; i < jTable.getRowCount(); i++) {
			final Object rowValue = jTable.getValueAt(i, columnIndex); //Get cell value
			if (rowValue == null) { //Ignore null
				continue;
			}
			boolean useCache = !disableColumnResizeCache.contains(rowValue.getClass());
			final int key = rowValue.toString().hashCode(); //value hash
			if (rowsWidth.containsKey(key) && useCache) { //Load row width
				maxWidth = Math.max(maxWidth, rowsWidth.get(key));
			} else { //Calculate the row width
				renderer = jTable.getCellRenderer(i, columnIndex);
				//Ignore SeparatorTableCell
				if (renderer instanceof SeparatorTableCell) {
					continue;
				}
				component = renderer.getTableCellRendererComponent(jTable, jTable.getValueAt(i, columnIndex), false, false, i, columnIndex);
				int width = component.getPreferredSize().width;
				if (useCache) {
					rowsWidth.put(key, width);
				}
				maxWidth = Math.max(maxWidth, width);
			}
		}
		//Add margin
		maxWidth = maxWidth + 4;
		//Set width
		column.setPreferredWidth(maxWidth);
		return maxWidth; //Return width
	}

	protected void saveColumnsWidth() {
		EnumTableFormatAdaptor<?, ?> tableFormat = getEnumTableFormatAdaptor();
		if (!loadingWidth && tableFormat != null && tableFormat.getResizeMode() == ResizeMode.NONE) {
			List<SimpleColumn> columns = tableFormat.getColumns();
			int i = 0;
			for (SimpleColumn column : columns) {
				if (column.isShown()) {
					int width = getColumnModel().getColumn(i).getPreferredWidth();
					columnsWidth.put(column.getEnumName(), width);
					i++;
				}
			}
		}
	}

	private class ModelListener implements TableModelListener, ComponentListener,
			PropertyChangeListener, HierarchyListener, TableColumnModelListener, MouseListener {

		private boolean columnMoved = false;
		private boolean columnResized = false;
		private int from = 0;
		private int to = 0;
		private int rowsLastTime = 0;
		private int rowsCount = 0;

		@Override
		public void tableChanged(final TableModelEvent e) {
			//XXX - Workaround for Java 7
			if (getTable().isEditing()) {
				getTable().getCellEditor().cancelCellEditing();
			}
			if (e.getType() == TableModelEvent.DELETE) {
				rowsCount = rowsCount - (Math.abs(e.getFirstRow() - e.getLastRow()) + 1);
			}
			if (e.getType() == TableModelEvent.INSERT) {
				rowsCount = rowsCount + (Math.abs(e.getFirstRow() - e.getLastRow()) + 1);
			}
			if (Math.abs(rowsLastTime + rowsCount) == getRowCount() //Last Table Update
					&& (e.getType() != TableModelEvent.UPDATE
					|| (e.getType() == TableModelEvent.UPDATE && e.getFirstRow() >= 0))) {
				rowsLastTime = getRowCount();
				rowsCount = 0;
				autoResizeColumns();
			}
		}

		@Override
		public void componentResized(final ComponentEvent e) {
			updateScroll();
		}

		@Override
		public void componentMoved(final ComponentEvent e) { }

		@Override
		public void componentShown(final ComponentEvent e) { }

		@Override
		public void componentHidden(final ComponentEvent e) { }

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			Object newValue = evt.getNewValue();
			Object oldValue = evt.getOldValue();
			if (newValue instanceof JTableHeader && oldValue instanceof JTableHeader) {
				JTableHeader newModel = (JTableHeader) newValue;
				JTableHeader oldModel = (JTableHeader) oldValue;
				oldModel.removeMouseListener(this);
				newModel.addMouseListener(this);

			}
			if (newValue instanceof TableColumnModel && oldValue instanceof TableColumnModel) {
				TableColumnModel newModel = (TableColumnModel) newValue;
				TableColumnModel oldModel = (TableColumnModel) oldValue;
				oldModel.removeColumnModelListener(this);
				newModel.addColumnModelListener(this);

			}
			if (newValue instanceof TableModel && oldValue instanceof TableModel) {
				TableModel newModel = (TableModel) newValue;
				TableModel oldModel = (TableModel) oldValue;
				oldModel.removeTableModelListener(this);
				newModel.addTableModelListener(this);
			}
		}

		@Override
		public void hierarchyChanged(final HierarchyEvent e) {
			if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) == HierarchyEvent.PARENT_CHANGED) {
				if (jViewport != null) {
					jViewport.removeComponentListener(this);
				}
				jViewport = getParentViewport();
				if (jViewport != null) {
					jViewport.addComponentListener(this);
				}
			}
		}

		@Override
		public void columnAdded(final TableColumnModelEvent e) { }

		@Override
		public void columnRemoved(final TableColumnModelEvent e) { }

		@Override
		public void columnMoved(final TableColumnModelEvent e) {
			if (e.getFromIndex() != e.getToIndex()) {
				if (!columnMoved) {
					from = e.getFromIndex();
				}
				to = e.getToIndex();
				columnMoved = true;
			}
		}

		@Override
		public void columnMarginChanged(final ChangeEvent e) {
			columnResized = true;
		}

		@Override
		public void columnSelectionChanged(final ListSelectionEvent e) { }

		@Override
		public void mouseClicked(final MouseEvent e) { }

		@Override
		public void mousePressed(final MouseEvent e) {
			columnMoved = false;
			columnResized = false;
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (columnMoved) {
				columnMoved = false;
				EnumTableFormatAdaptor<?, ?> tableFormat = getEnumTableFormatAdaptor();
				DefaultEventTableModel<?> model = getEventTableModel();
				if (tableFormat != null && model != null) {
					tableFormat.moveColumn(from, to);
					model.fireTableStructureChanged();
				}
				autoResizeColumns();
			}
			if (columnResized) {
				columnResized = false;
				saveColumnsWidth();
			}
		}

		@Override
		public void mouseEntered(final MouseEvent e) { }

		@Override
		public void mouseExited(final MouseEvent e) { }
	}

	/**
	 * @see JAutoColumnTable#fixScrollPaneRedraw()
	 */
	private class JScrollPaneAdjustmentListener implements AdjustmentListener {
		/**
		 * Holds the JScrollPane we want to force repainting its content.
		 */
		private JScrollPane jScrollPane;

		/**
		 * Holds the last scrollbar position for direction tracking.
		 */
		private int lastValue;

		private boolean repaint;

		public JScrollPaneAdjustmentListener(final JScrollPane jScrollPane) {
			this.jScrollPane = jScrollPane;
			repaint = false;
		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent e) {
			if (e.getValue() > lastValue) {
				// scrollbar has been dragged to the right
				repaint = true;
			}
			if (!e.getValueIsAdjusting() && repaint) {
				//Done scrolling - repaint if needed
				jScrollPane.repaint();
				repaint = false;
			}
			lastValue = e.getValue();
		}
	}
}
