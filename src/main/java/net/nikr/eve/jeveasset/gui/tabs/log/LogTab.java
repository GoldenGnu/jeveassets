/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.log;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.LogManager;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.i18n.TabsLog;


public class LogTab extends JMainTabSecondary {

	//GUI
	private final JLogTable jTable;

	//Table
	private final DefaultEventTableModel<MyLog> tableModel;
	private final EventList<MyLog> eventList;
	private final FilterList<MyLog> filterList;
	private final LogFilterControl filterControl;
	private final EnumTableFormatAdaptor<LogTableFormat, MyLog> tableFormat;
	private final DefaultEventSelectionModel<MyLog> selectionModel;

	public static final String NAME = "log"; //Not to be changed!

	public LogTab(final Program program) {
		super(program, TabsLog.get().toolTitle(), Images.TOOL_LOG.getIcon(), true);

		layout.setAutoCreateGaps(true);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<LogTableFormat, MyLog>(LogTableFormat.class);
		//Backend
		eventList = new EventListManager<MyLog>().create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyLog> sortedList = new SortedList<MyLog>(eventList);
		eventList.getReadWriteLock().readLock().unlock();

		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<MyLog>(sortedList);
		eventList.getReadWriteLock().readLock().unlock();
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JLogTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		//Sorting
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		
		//Listeners
		installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new LogFilterControl(
				program.getMainWindow().getFrame(),
				eventList,
				sortedList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, new LogTableMenu(), jTable, MyLog.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	@Override
	public void clearData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void updateData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(LogManager.getList());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	private class LogTableMenu implements MenuManager.TableMenu<MyLog> {
		@Override
		public MenuData<MyLog> getMenuData() {
			return new MenuData<MyLog>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return tableFormat.getMenu(program, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JComponent jComponent) { }

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class LogFilterControl extends FilterControl<MyLog> {

		public LogFilterControl(JFrame jFrame, EventList<MyLog> eventList, EventList<MyLog> exportEventList, FilterList<MyLog> filterList, Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, exportEventList, filterList, filters);
		}

		@Override
		protected Object getColumnValue(final MyLog item, final String column) {
			return tableFormat.getColumnValue(item, column);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return LogTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<MyLog>> getColumns() {
			return new ArrayList<EnumTableColumn<MyLog>>(tableFormat.getOrderColumns());
		}

		@Override
		protected List<EnumTableColumn<MyLog>> getShownColumns() {
			return new ArrayList<EnumTableColumn<MyLog>>(tableFormat.getShownColumns());
		}

		@Override
		protected void updateFilters() { }

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("Log Table: " + msg); //Save Asset Filters and Export Setttings
		}
	}
	
}
