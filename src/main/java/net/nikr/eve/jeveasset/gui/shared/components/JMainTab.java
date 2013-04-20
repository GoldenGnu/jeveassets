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

package net.nikr.eve.jeveasset.gui.shared.components;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;


public abstract class JMainTab {

	private String title;
	private Icon icon;
	private boolean closeable;
	private List<JLabel> statusbarLabels = new ArrayList<JLabel>();
	protected Program program;
	protected JPanel jPanel;
	protected GroupLayout layout;
	private JAutoColumnTable jTable;
	private DefaultEventSelectionModel<?> eventSelectionModel;
	private DefaultEventTableModel<?> eventTableModel;
	private List<?> selected;
	private String toolName;

	protected JMainTab(final boolean load) { }

	public JMainTab(final Program program, final String title, final Icon icon, final boolean closeable) {
		this.program = program;
		this.title = title;
		this.icon = icon;
		this.closeable = closeable;

		program.addMainTab(this);

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	}

	public final void updateTableMenu(JComponent jComponent) {
		createMenu(jComponent);
	}
	/** Must be called after setting SelectionModel.
	 * @param e mouse event
	 */
	private void showTablePopupMenu(final MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();

		selectClickedCell(e);

		//updateTableMenu(jTablePopupMenu);
		createMenu(jTablePopupMenu);

		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	protected abstract MenuData<?> getMenuData();
	protected abstract JMenu getFilterMenu();
	protected abstract JMenu getColumnMenu();

	protected JMenu getNameMenu() {
		return null;
	}
	protected void addInfoMenu(JComponent jComponent) { }
	protected void addToolMenu(JComponent jComponent) { }

	private void createMenu(JComponent jComponent) {
		jComponent.removeAll();
		boolean notEmpty = false;
	//Logic
		boolean isSelected;
		if (jTable != null) {
			isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);
		} else {
			isSelected = false;
		}
	//DATA
		MenuData<?> menuData = getMenuData();
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu) {
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
			notEmpty = true;
		}
	//TOOL MENU
		addToolMenu(jComponent);
	//FILTER
		JMenu filterMenu = getFilterMenu();
		if (filterMenu != null) {
			jComponent.add(filterMenu);
			notEmpty = true;
		}
	//ASSET FILTER
		if (menuData != null) {
			notEmpty = menuData.addAssetFilter(program, jComponent);
		}
	//STOCKPILE (Add To)
		if (menuData != null) {
			notEmpty = menuData.addStockpile(program, jComponent);
		}
	//LOOKUP
		if (menuData != null) {
			notEmpty = menuData.addLookup(program, jComponent);
		}
	//EDIT
		if (menuData != null) {
			notEmpty = menuData.addPrice(program, jComponent);
		}
		JMenu nameMenu = getNameMenu();
		if (nameMenu != null) {
			jComponent.add(nameMenu);
			notEmpty = true;
		}
	//REPROCESSED
		if (menuData != null) {
			notEmpty = menuData.addReprocessed(program, jComponent);
		}
	//COLUMNS
		JMenu columnMenu = getColumnMenu();
		if (columnMenu != null) {
			jComponent.add(columnMenu);
			notEmpty = true;
		}
	//INFO
		addInfoMenu(jComponent);
		jComponent.setEnabled(notEmpty);
	}

	public final void saveSettings() {
		//Save Settings
		if (eventTableModel != null && jTable != null && toolName != null) {
			TableFormat<?> tableFormat = eventTableModel.getTableFormat();
			if (tableFormat instanceof  EnumTableFormatAdaptor) {
				EnumTableFormatAdaptor<?, ?> formatAdaptor = (EnumTableFormatAdaptor<?, ?>) tableFormat;
				program.getSettings().getTableColumns().put(toolName, formatAdaptor.getColumns());
				program.getSettings().getTableResize().put(toolName, formatAdaptor.getResizeMode());
				program.getSettings().getTableColumnsWidth().put(toolName, jTable.getColumnsWidth());
			}
		}
		
	}

	public void addStatusbarLabel(final JLabel jLabel) {
		statusbarLabels.add(jLabel);
	}

	public List<JLabel> getStatusbarLabels() {
		return statusbarLabels;
	}

	public final void beforeUpdateData() {
		if (eventSelectionModel != null) {
			selected = new ArrayList<Object>(eventSelectionModel.getSelected());
		}
		if (jTable != null) {
			jTable.lock();
		}
		if (jTable instanceof JSeparatorTable) {
			JSeparatorTable jSeparatorTable = (JSeparatorTable) jTable;
			jSeparatorTable.saveExpandedState();
		}
	}

	public final void afterUpdateData() {
		//FIXME JMainTab.afterUpdateData() is too slow
		if (eventSelectionModel != null && eventTableModel != null && selected != null) {
			eventSelectionModel.setValueIsAdjusting(true);
			for (int i = 0; i < eventTableModel.getRowCount(); i++) {
				Object object = eventTableModel.getElementAt(i);
				if (selected.contains(object)) {
					eventSelectionModel.addSelectionInterval(i, i);
				}
			}
			eventSelectionModel.setValueIsAdjusting(false);
			selected = null;
		}
		if (jTable != null) {
			jTable.unlock();
		}
		if (jTable instanceof JSeparatorTable) {
			JSeparatorTable jSeparatorTable = (JSeparatorTable) jTable;
			jSeparatorTable.loadExpandedState();
		}
	}

	public abstract void updateData();

	public void updateDataTableLock() {
		beforeUpdateData();
		updateData();
		afterUpdateData();
	}

	public Icon getIcon() {
		return icon;
	}

	public JPanel getPanel() {
		return jPanel;
	}

	public String getTitle() {
		return title;
	}

	public boolean isCloseable() {
		return closeable;
	}

	protected void addSeparator(final JComponent jComponent) {
		if (jComponent instanceof JMenu) {
			JMenu jMenu = (JMenu) jComponent;
			jMenu.addSeparator();
		}
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;
			jPopupMenu.addSeparator();
		}
		if (jComponent instanceof JDropDownButton) {
			JDropDownButton jDropDownButton = (JDropDownButton) jComponent;
			jDropDownButton.addSeparator();
		}
	}

	protected void installTable(final JAutoColumnTable jTable, String toolName) {
		this.toolName = toolName;

		//Table Selection
		ListSelectionModel selectionModel = jTable.getSelectionModel();
		if (selectionModel instanceof  DefaultEventSelectionModel) {
			this.eventSelectionModel = (DefaultEventSelectionModel<?>) selectionModel;
		}
		TableModel tableModel = jTable.getModel();
		if (tableModel instanceof DefaultEventTableModel) {
			this.eventTableModel = (DefaultEventTableModel<?>) tableModel;
		}

		//Table lock
		this.jTable = jTable;

		//Load Settings
		if (eventTableModel != null && toolName != null) {
			TableFormat<?> tableFormat = eventTableModel.getTableFormat();
			if (tableFormat instanceof  EnumTableFormatAdaptor) {
				EnumTableFormatAdaptor<?, ?> formatAdaptor = (EnumTableFormatAdaptor<?, ?>) tableFormat;
				formatAdaptor.setColumns(program.getSettings().getTableColumns().get(toolName));
				formatAdaptor.setResizeMode(program.getSettings().getTableResize().get(toolName));
				jTable.setColumnsWidth(program.getSettings().getTableColumnsWidth().get(toolName));
				eventTableModel.fireTableStructureChanged();
			}
		}

		//Table Menu
		TableMenuListener listener = new TableMenuListener(jTable);
		jTable.addMouseListener(listener);
		jTable.getSelectionModel().addListSelectionListener(listener);
		jTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
	}

	protected void selectClickedCell(final MouseEvent e) {
		Object source = e.getSource();
		if (source instanceof JTable) {
			JTable jTable = (JTable) source;

			//Rows
			boolean clickInRowsSelection = false;
			int[] selectedRows = jTable.getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++) {
				if (selectedRows[i] == jTable.rowAtPoint(e.getPoint())) {
					clickInRowsSelection = true;
					break;
				}
			}

			//Column
			boolean clickInColumnsSelection = false;
			int[] selectedColumns = jTable.getSelectedColumns();
			for (int i = 0; i < selectedColumns.length; i++) {
				if (selectedColumns[i] == jTable.columnAtPoint(e.getPoint())) {
					clickInColumnsSelection = true;
					break;
				}
			}

			//Clicked outside selection, select clicked cell
			if (!clickInRowsSelection || !clickInColumnsSelection) {
				jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
				jTable.setColumnSelectionInterval(jTable.columnAtPoint(e.getPoint()), jTable.columnAtPoint(e.getPoint()));
			}
		}
	}

	private class TableMenuListener implements MouseListener, ListSelectionListener {

		private JTable jTable;

		public TableMenuListener(final JTable jTable) {
			this.jTable = jTable;
		}

		@Override
		public void mouseClicked(final MouseEvent e) { }

		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.getSource().equals(jTable) && e.isPopupTrigger()) {
				showTablePopupMenu(e);
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (e.getSource().equals(jTable) && e.isPopupTrigger()) {
				showTablePopupMenu(e);
			}
		}

		@Override
		public void mouseEntered(final MouseEvent e) { }

		@Override
		public void mouseExited(final MouseEvent e) { }

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				program.updateTableMenu();
			}
		}
	}
}
