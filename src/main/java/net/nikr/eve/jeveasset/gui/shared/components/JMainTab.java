/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.SettingsUpdateListener;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;


public abstract class JMainTab {

	private String title;
	private Icon icon;
	private boolean closeable;
	private final List<JLabel> statusbarLabels = new ArrayList<>();
	protected Program program;
	protected JPanel jPanel;
	protected GroupLayout layout;
	private JAutoColumnTable jTable;
	private DefaultEventSelectionModel<?> eventSelectionModel;
	private DefaultEventTableModel<?> eventTableModel;
	private FilterControl<?> filterControl;
	private List<Object> selected;
	private int[] selectedColumns;
	private String toolName;
	private Class<?> clazz;
	protected JMainTab(final boolean load) { }

	public JMainTab(final Program program, final String toolName, final String title, final Icon icon, final boolean closeable) {
		this.program = program;
		this.toolName = toolName;
		this.title = title;
		this.icon = icon;
		this.closeable = closeable;

		program.addMainTab(toolName, this);

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	}

	public final <T extends Enum<T> & EnumTableColumn<Q>, Q> void installTableTool(final TableMenu<Q> tableMenu, EnumTableFormatAdaptor<T, Q> tableFormat, DefaultEventTableModel<Q> tableModel, JAutoColumnTable jTable, EventList<Q> eventList, final Class<Q> clazz) {
		installTableTool(tableMenu, tableFormat, tableModel, jTable, eventList, null, clazz);
	}

	public final <T extends Enum<T> & EnumTableColumn<Q>, Q> void installTableTool(final TableMenu<Q> tableMenu, EnumTableFormatAdaptor<T, Q> tableFormat, DefaultEventTableModel<Q> tableModel, JAutoColumnTable jTable, FilterControl<Q> filterControl, final Class<Q> clazz) {
		installTableTool(tableMenu, tableFormat, tableModel, jTable, filterControl.getEventList(), filterControl, clazz);
	}

	private <T extends Enum<T> & EnumTableColumn<Q>, Q> void installTableTool(final TableMenu<Q> tableMenu, EnumTableFormatAdaptor<T, Q> tableFormat, DefaultEventTableModel<Q> tableModel, JAutoColumnTable jTable, EventList<Q> eventList, FilterControl<Q> filterControl, final Class<Q> clazz) {
		this.clazz = clazz;
		MenuManager.install(program, tableMenu, jTable, new ColumnManager<>(program, toolName, tableFormat, tableModel, jTable, eventList, filterControl), clazz);
		if(filterControl != null && toolName != null && !toolName.isEmpty()) {
			filterControl.clearCurrentFilters();
			filterControl.addFilters(Settings.get().getCurrentTableFilters(toolName));
			filterControl.setFilterShown(Settings.get().getCurrentTableFiltersShown(toolName));
			SettingsUpdateListener listener = new ListenerClass();
			filterControl.getSettingsUpdateListenerList().add(listener);
			this.filterControl = filterControl;
		}
	}

	public void updateTableMenu() {
		MenuManager.update(program ,clazz);
	}

	public void createTableMenu() {
		MenuManager.create(program ,clazz);
	}

	public void tableStructureChanged() {
		if (eventTableModel != null && jTable != null) {
			eventTableModel.fireTableStructureChanged();
			jTable.autoResizeColumns();
		}
	}

	public void tableDataChanged() {
		beforeUpdateDataKeepCache();
		if (eventTableModel != null) {
			eventTableModel.fireTableDataChanged();
		}
		afterUpdateData();
	}

	public void repaintTable() {
		if (jTable != null) {
			jTable.repaint();
		}
	}

	public final void saveSettings() {
		//Save Settings
		if (eventTableModel != null && jTable != null && toolName != null) {
			TableFormat<?> tableFormat = eventTableModel.getTableFormat();
			if (tableFormat instanceof EnumTableFormatAdaptor) {
				EnumTableFormatAdaptor<?, ?> formatAdaptor = (EnumTableFormatAdaptor<?, ?>) tableFormat;
				Settings.get().getTableColumns().put(toolName, formatAdaptor.getColumns());
				Settings.get().getTableResize().put(toolName, formatAdaptor.getResizeMode());
				Settings.get().getTableColumnsWidth().put(toolName, jTable.getColumnsWidth());
				if(filterControl != null) {
					Settings.get().getCurrentTableFilters().put(toolName, filterControl.getCurrentFilters());
				}
			}
		}
	}

	public final synchronized void addStatusbarLabel(final JLabel jLabel) {
		statusbarLabels.add(jLabel);
	}

	public final synchronized void clearStatusbarLabels() {
		statusbarLabels.clear();
	}

	public synchronized List<JLabel> getStatusbarLabels() {
		return new ArrayList<>(statusbarLabels); //Copy
	}

	public final void beforeUpdateData() {
		beforeUpdateData(true);
	}

	public final void beforeUpdateDataKeepCache() {
		beforeUpdateData(false);
	}

	private void beforeUpdateData(boolean resetFormulaCache) {
		if (eventSelectionModel != null) {
			selected = new ArrayList<>(eventSelectionModel.getSelected());
		}
		if (jTable != null) {
			selectedColumns = jTable.getColumnModel().getSelectedColumns();
			jTable.lock();
		} else {
			selectedColumns = null;
		}
		if (jTable instanceof JSeparatorTable) {
			JSeparatorTable jSeparatorTable = (JSeparatorTable) jTable;
			jSeparatorTable.saveExpandedState();
		}
		if (resetFormulaCache) {
			MenuManager.updateFormula(clazz);
		} else {
			MenuManager.lock(clazz);
		}
	}

	public final void afterUpdateData() {
		MenuManager.updateJumps(clazz);
		MenuManager.unlock(clazz);
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
		if (selectedColumns != null && jTable != null) {
			for (int index : selectedColumns) {
				jTable.getColumnModel().getSelectionModel().addSelectionInterval(index, index);
			}
		}
		if (jTable != null) {
			jTable.unlock();
		}
		if (jTable instanceof JSeparatorTable) {
			JSeparatorTable jSeparatorTable = (JSeparatorTable) jTable;
			jSeparatorTable.loadExpandedState();
		}
	}

	public abstract void updateNames(Set<Long> itemIDs);
	public abstract void updateLocations(Set<Long> locationIDs);
	public abstract void updatePrices(Set<Integer> typeIDs);
	public abstract void updateData();
	public abstract void updateCache();
	public abstract void clearData();
	public abstract Collection<LocationType> getLocations();

	public final Icon getIcon() {
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

	/**
	 * Table automation
	 * 1. Saving table settings (TableColumns, TableResize, TableColumnsWidth)
	 * 2. Restore table selection after update
	 * 3. Restore expanded state for JSeparatorTable after update
	 * 4. Lock/unlock table doing update
	 *
	 * @param jTable
	 */
	protected final void installTable(final JAutoColumnTable jTable) {
		//Table Selection
		ListSelectionModel selectionModel = jTable.getSelectionModel();
		if (selectionModel instanceof DefaultEventSelectionModel) {
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
			if (tableFormat instanceof EnumTableFormatAdaptor) {
				EnumTableFormatAdaptor<?, ?> formatAdaptor = (EnumTableFormatAdaptor<?, ?>) tableFormat;
				formatAdaptor.setColumns(Settings.get().getTableColumns().get(toolName));
				formatAdaptor.setResizeMode(Settings.get().getTableResize().get(toolName));
				jTable.setColumnsWidth(Settings.get().getTableColumnsWidth().get(toolName));
				eventTableModel.fireTableStructureChanged();
			}
		}
	}

	/***
	 * Inner class to define the listener for settings updates and perform actions when the event fires.
	 */
	private class ListenerClass implements SettingsUpdateListener {
		@Override
		public void settingChanged() {
			//Shows in a primitive so we need to update it before saving
			program.saveSettings("Save current filter change.");
		}
	}
}
