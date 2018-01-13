/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.items;

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
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.i18n.TabsItems;


public class ItemsTab extends JMainTabPrimary {

	private final JAutoColumnTable jTable;

	//Table
	private final ItemsFilterControl filterControl;
	private final EnumTableFormatAdaptor<ItemTableFormat, Item> tableFormat;
	private final DefaultEventTableModel<Item> tableModel;
	private final FilterList<Item> filterList;
	private final EventList<Item> eventList;
	private final DefaultEventSelectionModel<Item> selectionModel;

	public static final String NAME = "items"; //Not to be changed!

	public ItemsTab(final Program program) {
		super(program, TabsItems.get().items(), Images.TOOL_ITEMS.getIcon(), true);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<ItemTableFormat, Item>(ItemTableFormat.class);
		//Backend
		eventList = new EventListManager<Item>().create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<Item> sortedList = new SortedList<Item>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<Item>(sortedList);
		eventList.getReadWriteLock().readLock().unlock();
		
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JAutoColumnTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
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
		filterControl = new ItemsFilterControl(
				tableFormat,
				program.getMainWindow().getFrame(),
				eventList,
				sortedList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, new ItemTableMenu(), jTable, Item.class);

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

		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(StaticData.get().getItems().values());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void clearData() {
		filterControl.clearCache();
	}

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	private class ItemTableMenu implements TableMenu<Item> {
		@Override
		public MenuData<Item> getMenuData() {
			return new MenuData<Item>(selectionModel.getSelected());
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

	private class ItemsFilterControl extends FilterControl<Item> {

		private final EnumTableFormatAdaptor<ItemTableFormat, Item> tableFormat;

		public ItemsFilterControl(EnumTableFormatAdaptor<ItemTableFormat, Item> tableFormat, JFrame jFrame, EventList<Item> eventList, SortedList<Item> sortedList, FilterList<Item> filterList, Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, sortedList, filterList, filters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final Item item, final String column) {
			ItemTableFormat format = ItemTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return ItemTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<Item>> getColumns() {
			return columnsAsList(ItemTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<Item>> getShownColumns() {
			return new ArrayList<EnumTableColumn<Item>>(tableFormat.getShownColumns());
		}

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("Items Table: " + msg); //Save Item Filters and Export Setttings
		}
	}
}
