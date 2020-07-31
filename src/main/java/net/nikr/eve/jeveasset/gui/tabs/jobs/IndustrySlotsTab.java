/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.jobs;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.i18n.TabsIndustrySlots;


public class IndustrySlotsTab extends JMainTabSecondary {

	//GUI
	private final JAutoColumnTable jTable;

	//Table
	private final IndustrySlotFilterControl filterControl;
	private final DefaultEventTableModel<IndustrySlot> tableModel;
	private final EventList<IndustrySlot> eventList;
	private final FilterList<IndustrySlot> filterList;
	private final EnumTableFormatAdaptor<IndustrySlotTableFormat, IndustrySlot> tableFormat;
	private final DefaultEventSelectionModel<IndustrySlot> selectionModel;

	public static final String NAME = "industryslots"; //Not to be changed!

	public IndustrySlotsTab(final Program program) {
		super(program, TabsIndustrySlots.get().title(), Images.TOOL_INDUSTRY_SLOTS.getIcon(), true);
		//Table Format
		tableFormat = new EnumTableFormatAdaptor<>(IndustrySlotTableFormat.class);
		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<IndustrySlot> columnSortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Sorting Total
		eventList.getReadWriteLock().readLock().lock();
		SortedList<IndustrySlot> totalSortedList = new SortedList<>(columnSortedList, new TotalComparator());
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(totalSortedList);
		eventList.getReadWriteLock().readLock().unlock();
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new IndustrySlotTable(program, tableModel);
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
		installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new IndustrySlotFilterControl(
				tableFormat,
				program.getMainWindow().getFrame(),
				eventList,
				totalSortedList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, new IndustrySlotTableMenu(), jTable, IndustrySlot.class);

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
	public void updateData() {
		List<IndustrySlot> industrySlots = new ArrayList<>();
		IndustrySlot total = new IndustrySlot(TabsIndustrySlots.get().grandTotal());
		for (OwnerType ownerType : program.getOwnerTypes()) {
			if (ownerType.isCorporation()) {
				continue;
			}
			industrySlots.add(new IndustrySlot(ownerType));
			total.count(ownerType);
		}
		industrySlots.add(total);
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(industrySlots);
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

	private class IndustrySlotTableMenu implements TableMenu<IndustrySlot> {
		@Override
		public MenuData<IndustrySlot> getMenuData() {
			return new MenuData<>();
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

	private class IndustrySlotFilterControl extends FilterControl<IndustrySlot> {

		private final EnumTableFormatAdaptor<IndustrySlotTableFormat, IndustrySlot> tableFormat;

		public IndustrySlotFilterControl(EnumTableFormatAdaptor<IndustrySlotTableFormat, IndustrySlot> tableFormat, JFrame jFrame, EventList<IndustrySlot> eventList, EventList<IndustrySlot> exportEventList, FilterList<IndustrySlot> filterList, Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, exportEventList, filterList, filters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final IndustrySlot value, final String column) {
			IndustrySlotTableFormat format = IndustrySlotTableFormat.valueOf(column);
			return format.getColumnValue(value);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return IndustrySlotTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<IndustrySlot>> getColumns() {
			return columnsAsList(IndustrySlotTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<IndustrySlot>> getShownColumns() {
			return new ArrayList<>(tableFormat.getShownColumns());
		}

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("ISK Talbe: " + msg); //Save ISK Filters and Export Setttings
		}
	}

	public static class TotalComparator implements Comparator<IndustrySlot> {
		@Override
		public int compare(final IndustrySlot o1, final IndustrySlot o2) {
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
