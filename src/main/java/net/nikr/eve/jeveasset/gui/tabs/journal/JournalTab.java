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

package net.nikr.eve.jeveasset.gui.tabs.journal;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
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
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.i18n.TabsJournal;


public class JournalTab extends JMainTab {

	private JAutoColumnTable jTable;

	//Table
	private JournalFilterControl filterControl;
	private EnumTableFormatAdaptor<JournalTableFormat, Journal> tableFormat;
	private DefaultEventTableModel<Journal> tableModel;
	private FilterList<Journal> filterList;
	private EventList<Journal> eventList;
	private DefaultEventSelectionModel<Journal> selectionModel;

	public static final String NAME = "journal"; //Not to be changed!

	public JournalTab(final Program program) {
		super(program, TabsJournal.get().title(), Images.TOOL_JOURNAL.getIcon(), true);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<JournalTableFormat, Journal>(JournalTableFormat.class);
		//Backend
		eventList = program.getJournalEventList();
		//Sorting (per column)
		SortedList<Journal> sortedList = new SortedList<Journal>(eventList);
		//Filter
		filterList = new FilterList<Journal>(sortedList);
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JJournalTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		PaddingTableCellRenderer.install(jTable, 1);
		//Sorting
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, NAME);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new JournalFilterControl(
				program.getMainWindow().getFrame(),
				tableFormat,
				sortedList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, new JournalTableMenu(), jTable, Journal.class);

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
	public void updateData() { }

	private class JournalTableMenu implements TableMenu<Journal> {
		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return tableFormat.getMenu(program, tableModel, jTable, NAME);
		}

		@Override
		public MenuData<Journal> getMenuData() {
			return new MenuData<Journal>(selectionModel.getSelected());
		}

		@Override
		public void addInfoMenu(JComponent jComponent) { }

		@Override
		public void addToolMenu(JComponent jComponent) {
			//FIXME - - > Journal - ToolMenu
			//Link with contracts and Transactions
		}
	}

	public static class JournalFilterControl extends FilterControl<Journal> {

		private EnumTableFormatAdaptor<JournalTableFormat, Journal> tableFormat;

		public JournalFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<JournalTableFormat, Journal> tableFormat, final EventList<Journal> eventList, final FilterList<Journal> filterList, final Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final Journal item, final String column) {
			JournalTableFormat format = JournalTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return JournalTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<Journal>> getColumns() {
			return columnsAsList(JournalTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<Journal>> getShownColumns() {
			return new ArrayList<EnumTableColumn<Journal>>(tableFormat.getShownColumns());
		}
	}
}