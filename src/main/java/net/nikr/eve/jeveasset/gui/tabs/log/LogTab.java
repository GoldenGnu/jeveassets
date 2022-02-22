/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.LogManager;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.TabsLog;


public class LogTab extends JMainTabSecondary {

	private enum LogAction {
		COLLAPSE,
		EXPAND,
	}
	//GUI
	private final JLogTable jTable;

	//Table
	private final DefaultEventTableModel<AssetLogSource> tableModel;
	private final EventList<AssetLogSource> eventList;
	private final FilterList<AssetLogSource> filterList;
	private final SeparatorList<AssetLogSource> separatorList;
	private final LogFilterControl filterControl;
	private final EnumTableFormatAdaptor<LogTableFormat, AssetLogSource> tableFormat;
	private final DefaultEventSelectionModel<AssetLogSource> selectionModel;

	public static final String NAME = "log"; //Not to be changed!

	public LogTab(final Program program) {
		super(program, NAME, TabsLog.get().toolTitle(), Images.TOOL_LOG.getIcon(), true);

		layout.setAutoCreateGaps(true);

		ListenerClass listener = new ListenerClass();

		//Table Format
		tableFormat = TableFormatFactory.logTableFormat();
		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<AssetLogSource> sortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();

		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedList);
		eventList.getReadWriteLock().readLock().unlock();
		//Separator
		eventList.getReadWriteLock().readLock().lock();
		separatorList = new SeparatorList<>(filterList, new LogSeparatorComparator(), 1, Integer.MAX_VALUE);
		eventList.getReadWriteLock().readLock().unlock();
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JLogTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new LogSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new LogSeparatorTableCell(jTable, separatorList));
		PaddingTableCellRenderer.install(jTable, 3);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		//Sorting
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		
		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new LogFilterControl(sortedList);
		//Menu
		installTableTool(new LogTableMenu(), tableFormat, tableModel, jTable, filterControl, AssetLogSource.class);

		JFixedToolBar jToolBar = new JFixedToolBar();

		JButton jCollapse = new JButton(TabsLog.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(LogAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jToolBar.addButton(jCollapse);

		JButton jExpand = new JButton(TabsLog.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(LogAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jToolBar.addButton(jExpand);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
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
	public Collection<LocationType> getLocations() {
		return new ArrayList<>(); //No Location
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (LogAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			}
			if (LogAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			}
		}
	}

	private class LogTableMenu implements MenuManager.TableMenu<AssetLogSource> {
		@Override
		public MenuData<AssetLogSource> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JComponent jComponent) { }

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class LogFilterControl extends FilterControl<AssetLogSource> {

		public LogFilterControl(EventList<AssetLogSource> exportEventList) {
			super(program.getMainWindow().getFrame(),
					NAME,
					tableFormat,
					eventList,
					exportEventList,
					filterList,
					Settings.get().getTableFilters(NAME)
					);
		}

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Log Table: " + msg); //Save Asset Filters and Export Setttings
		}
	}
	
}
