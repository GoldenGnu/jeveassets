/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.*;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.i18n.TabsItems;


public class ItemsTab extends JMainTab{
	
	private JAutoColumnTable jTable;
	
	//Table
	private ItemsFilterControl filterControl;
	private EnumTableFormatAdaptor<ItemTableFormat, Item> tableFormat;
	private EventTableModel<Item> tableModel;
	private FilterList<Item> filterList;
	private EventList<Item> eventList;
	private EventSelectionModel<Item> selectionModel;
	
	public static final String NAME = "items"; //Not to be changed!

	public ItemsTab(Program program) {
		super(program, TabsItems.get().items(), Images.TOOL_ITEMS.getIcon(), true);
		
		//Table format
		tableFormat = new EnumTableFormatAdaptor<ItemTableFormat, Item>(ItemTableFormat.class);
		tableFormat.setColumns(program.getSettings().getTableColumns().get(NAME));
		//Backend
		eventList = new BasicEventList<Item>();
		//Backend
		filterList = new FilterList<Item>(eventList);
		//For soring the table
		SortedList<Item> sortedList = new SortedList<Item>(filterList);
		//Table Model
		tableModel = new EventTableModel<Item>(sortedList, tableFormat);
		//Tables
		jTable = new JAutoColumnTable(tableModel);
		jTable.setCellSelectionEnabled(true);
		//Table Selection
		selectionModel = new EventSelectionModel<Item>(sortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTableMenu(jTable);
		//Sorters
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter

		filterControl = new ItemsFilterControl(
				program.getMainWindow().getFrame(),
				program.getSettings().getTableFilters(NAME),
				filterList,
				eventList);
		
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
			eventList.addAll( program.getSettings().getItems().values() );
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		
	}
	
	

	@Override
	public void updateTableMenu(JComponent jComponent) {
		jComponent.removeAll();
		jComponent.setEnabled(true);
		
		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);

	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
	//FILTER
		jComponent.add(filterControl.getMenu(jTable, selectionModel.getSelected()));
	//ASSET FILTER
		jComponent.add(new JMenuAssetFilter<Item>(program, selectionModel.getSelected()));
	//STOCKPILE
		jComponent.add(new JMenuStockpile<Item>(program, selectionModel.getSelected()));
	//LOOKUP
		jComponent.add(new JMenuLookup<Item>(program, selectionModel.getSelected()));
	//EDIT
		jComponent.add(new JMenuEditItem<Item>(program, selectionModel.getSelected()));
	//COLUMNS
		jComponent.add(tableFormat.getMenu(program, tableModel, jTable));
	}

	@Override
	public void updateData() {}
	
	
	public static class ItemsFilterControl extends FilterControl<Item>{

		public ItemsFilterControl(JFrame jFrame, Map<String, List<Filter>> filters, FilterList<Item> filterList, EventList<Item> eventList) {
			super(jFrame, NAME, filters, filterList, eventList);
		}
		
		@Override
		protected Object getColumnValue(Item item, String column) {
			ItemTableFormat format = ItemTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}
		
		@Override
		protected boolean isNumericColumn(Enum column) {
			ItemTableFormat format = (ItemTableFormat) column;
			if (Number.class.isAssignableFrom(format.getType())) {
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		protected boolean isDateColumn(Enum column) {
			ItemTableFormat format = (ItemTableFormat) column;
			if (format.getType().getName().equals(Date.class.getName())) {
				return true;
			} else {
				return false;
			}
		}


		@Override
		public Enum[] getColumns() {
			return ItemTableFormat.values();
		}
		
		@Override
		protected Enum valueOf(String column) {
			return ItemTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<Item>> getEnumColumns() {
			return columnsAsList(ItemTableFormat.values());
		}
		
	}
}
