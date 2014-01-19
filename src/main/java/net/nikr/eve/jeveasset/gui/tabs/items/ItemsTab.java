/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import ca.odell.glazedlists.*;
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
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.i18n.TabsItems;


public class ItemsTab extends JMainTab {

	private JAutoColumnTable jTable;

	//Table
	private ItemsFilterControl filterControl;
	private EnumTableFormatAdaptor<ItemTableFormat, Item> tableFormat;
	private DefaultEventTableModel<Item> tableModel;
	private FilterList<Item> filterList;
	private EventList<Item> eventList;
	private DefaultEventSelectionModel<Item> selectionModel;

	public static final String NAME = "items"; //Not to be changed!

	public ItemsTab(final Program program) {
		super(program, TabsItems.get().items(), Images.TOOL_ITEMS.getIcon(), true);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<ItemTableFormat, Item>(ItemTableFormat.class);
		//Backend
		eventList = new BasicEventList<Item>();
		//Sorting (per column)
		SortedList<Item> sortedList = new SortedList<Item>(eventList);
		//Filter
		filterList = new FilterList<Item>(sortedList);
		
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
				program.getMainWindow().getFrame(),
				tableFormat,
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
	public void updateData() { }

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

	public static class ItemsFilterControl extends FilterControl<Item> {

		private EnumTableFormatAdaptor<ItemTableFormat, Item> tableFormat;

		public ItemsFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<ItemTableFormat, Item> tableFormat, final EventList<Item> eventList, final FilterList<Item> filterList, final Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
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
	}
}
