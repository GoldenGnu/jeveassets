/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.values;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerSkillPointFilter;
import net.nikr.eve.jeveasset.i18n.TabsValues;


public class ValueTableTab extends JMainTabSecondary {

	//GUI
	private final JAutoColumnTable jTable;
	private final JButton jSkillPointsFilters;

	//Table
	private final ValueFilterControl filterControl;
	private final DefaultEventTableModel<Value> tableModel;
	private final EventList<Value> eventList;
	private final FilterList<Value> filterList;
	private final EnumTableFormatAdaptor<ValueTableFormat, Value> tableFormat;
	private final DefaultEventSelectionModel<Value> selectionModel;

	public static final String NAME = "value"; //Not to be changed!

	public ValueTableTab(final Program program) {
		super(program, NAME, TabsValues.get().title(), Images.TOOL_VALUE_TABLE.getIcon(), true);

		JFixedToolBar jToolBar = new JFixedToolBar();

		jSkillPointsFilters = new JButton(TabsValues.get().skillPointFilters(), Images.LOC_INCLUDE.getIcon());
		jSkillPointsFilters.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				program.getTrackerTab().showSkillPointsFilter();
			}
		});
		jToolBar.addButton(jSkillPointsFilters);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<>(ValueTableFormat.class);
		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<Value> columnSortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Sorting Total
		eventList.getReadWriteLock().readLock().lock();
		SortedList<Value> totalSortedList = new SortedList<>(columnSortedList, new TotalComparator());
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(totalSortedList);
		eventList.getReadWriteLock().readLock().unlock();
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JValueTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, columnSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new ValueFilterControl(totalSortedList);
		//Menu
		installTableTool(new ValueTableMenu(), tableFormat, tableModel, jTable, filterControl, Value.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateData() {
		boolean isAll = true;
		for (TrackerSkillPointFilter filter : Settings.get().getTrackerSettings().getSkillPointFilters().values()) {
			if (!filter.isEmpty()) {
				isAll = false;
				break;
			}
		}
		if (isAll) {
			jSkillPointsFilters.setIcon(Images.LOC_INCLUDE.getIcon());
		} else {
			jSkillPointsFilters.setIcon(Images.EDIT_EDIT_WHITE.getIcon());
		}
		Map<String, Value> values = DataSetCreator.createDataSet(program.getProfileData(), Settings.getNow());
		Value total = values.get(TabsValues.get().grandTotal());
		total.setSkillPoints(0);
		for (Value value : values.values()) {
			TrackerSkillPointFilter skillPointFilter = Settings.get().getTrackerSettings().getSkillPointFilters().get(value.getName());
			if (skillPointFilter != null) {
				if (skillPointFilter.isEnabled()) {
					value.setSkillPointsMinimum(skillPointFilter.getMinimum());
					total.addSkillPointValue(value.getSkillPoints(), skillPointFilter.getMinimum());
				} else {
					value.setSkillPoints(0);
				}
			} else {
				total.addSkillPointValue(value.getSkillPoints(), 0);
			}
		}
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(values.values());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void clearData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		filterControl.clearCache();
	}

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	@Override
	public Collection<LocationType> getLocations() {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return new ArrayList<>(eventList);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}

	private class ValueTableMenu implements TableMenu<Value> {
		@Override
		public MenuData<Value> getMenuData() {
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

	private class ValueFilterControl extends FilterControl<Value> {

		public ValueFilterControl(EventList<Value> exportEventList) {
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
			program.saveSettings("ISK Talbe: " + msg); //Save ISK Filters and Export Setttings
		}
	}

	public static class TotalComparator implements Comparator<Value> {
		@Override
		public int compare(final Value o1, final Value o2) {
			if (o1.isGrandTotal() && o2.isGrandTotal()) {
				return 0;  //Equal (both StockpileTotal)
			} else if (o1.isGrandTotal()) {
				return 1;  //After
			} else if (o2.isGrandTotal()) {
				return -1; //Before
			} else {
				return 0;  //Equal (not StockpileTotal)
			}
		}
	}
	
}
