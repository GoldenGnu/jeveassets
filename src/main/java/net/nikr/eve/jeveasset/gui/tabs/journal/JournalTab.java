/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterMatcher;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.i18n.TabsJournal;


public class JournalTab extends JMainTabPrimary {

	private final JAutoColumnTable jTable;
	private final JButton jClearNew;

	//Table
	private final JournalFilterControl filterControl;
	private final EnumTableFormatAdaptor<JournalTableFormat, MyJournal> tableFormat;
	private final DefaultEventTableModel<MyJournal> tableModel;
	private final EventList<MyJournal> eventList;
	private final FilterList<MyJournal> filterList;
	private final DefaultEventSelectionModel<MyJournal> selectionModel;

	public static final String NAME = "journal"; //Not to be changed!

	public JournalTab(final Program program) {
		super(program, NAME, TabsJournal.get().title(), Images.TOOL_JOURNAL.getIcon(), true);

		JFixedToolBar jToolBar = new JFixedToolBar();

		jClearNew = new JButton(TabsJournal.get().clearNew(), Images.UPDATE_DONE_OK.getIcon());
		jClearNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.get().getTableChanged().put(NAME, new Date());
				jTable.repaint();
				jClearNew.setEnabled(false);
				program.saveSettings("Table Changed (journal cleared)");
			}
		});
		jToolBar.addButton(jClearNew);

		//Table Format
		tableFormat = TableFormatFactory.journalTableFormat();
		//Backend
		eventList = program.getProfileData().getJournalEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyJournal> sortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedList);
		eventList.getReadWriteLock().readLock().unlock();
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
		installTable(jTable);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new JournalFilterControl(sortedList);
		//Menu
		installTableTool(new JournalTableMenu(), tableFormat, tableModel, jTable, filterControl, MyJournal.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar, jToolBar.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
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
	public void clearData() {
		filterControl.clearCache();
	}

	@Override
	public void updateCache() {
		Date current = Settings.get().getTableChanged(NAME);
		boolean newFound = false;
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (MyJournal journal : eventList) {
				if (current.before(journal.getAdded())) {
					newFound = true;
					break;
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		filterControl.createCache();
		final boolean found = newFound;
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				jClearNew.setEnabled(found);
			}
		});
	}

	@Override
	public Collection<LocationType> getLocations() {
		return new ArrayList<>(); //No Location
	}

	private class JournalTableMenu implements TableMenu<MyJournal> {
		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME);
		}

		@Override
		public MenuData<MyJournal> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public void addInfoMenu(JPopupMenu jPopupMenu) { }

		@Override
		public void addToolMenu(JComponent jComponent) {
			JMenu jJournal = new JMenu(TabsJournal.get().findIn());
			jJournal.setIcon(Images.TOOL_JOURNAL.getIcon());
			jComponent.add(jJournal);

			Set<String> contractIDs = new HashSet<>();
			Set<String> industryJobIDs = new HashSet<>();
			Set<String> transactionIDs = new HashSet<>();
			for (MyJournal journal : selectionModel.getSelected()) {
				if (null == journal.getContextType()) {
					continue;
				}
				switch (journal.getContextType()) {
					case CONTRACT_ID:
						addSafe(contractIDs, journal.getContextID());
						break;
					case INDUSTRY_JOB_ID:
						addSafe(industryJobIDs, journal.getContextID());
						break;
					case MARKET_TRANSACTION_ID:
						addSafe(transactionIDs, journal.getContextID());
						break;
				}
			}

			JMenuItem jContracts = new JMenuItem(TabsJournal.get().contracts(), Images.TOOL_CONTRACTS.getIcon());
			jContracts.setEnabled(!contractIDs.isEmpty());
			jContracts.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<Filter> filters = JMenuAssetFilter.getFilters(contractIDs, ContractsTableFormat.CONTRACT_ID, CompareType.EQUALS);
					program.getContractsTab().addFilters(filters);
					program.getMainWindow().addTab(program.getContractsTab());
				}
			});
			jJournal.add(jContracts);

			JMenuItem jIndustryJobs = new JMenuItem(TabsJournal.get().industryJobs(), Images.TOOL_INDUSTRY_JOBS.getIcon());
			jIndustryJobs.setEnabled(!industryJobIDs.isEmpty());
			jIndustryJobs.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<Filter> filters = JMenuAssetFilter.getFilters(industryJobIDs, IndustryJobTableFormat.JOB_ID, CompareType.EQUALS);
					program.getIndustryJobsTab().addFilters(filters);
					program.getMainWindow().addTab(program.getIndustryJobsTab());
				}
			});
			jJournal.add(jIndustryJobs);

			JMenuItem jTransactions = new JMenuItem(TabsJournal.get().transactions(), Images.TOOL_TRANSACTION.getIcon());
			jTransactions.setEnabled(!transactionIDs.isEmpty());
			jTransactions.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<Filter> filters = JMenuAssetFilter.getFilters(transactionIDs, TransactionTableFormat.TRANSACTION_ID, CompareType.EQUALS);
					program.getTransactionsTab().addFilters(filters);
					program.getMainWindow().addTab(program.getTransactionsTab());
				}
			});
			jJournal.add(jTransactions);
		}
	}

	private void addSafe(Set<String> set, Long value) {
		if (value == null || value < 100) {
			return;
		}
		set.add(FilterMatcher.format(value, false));
	}

	private class JournalFilterControl extends FilterControl<MyJournal> {

		public JournalFilterControl(SortedList<MyJournal> exportEventList) {
			super(program.getMainWindow().getFrame(),
					NAME,
					tableFormat,
					eventList,
					exportEventList,
					filterList
					);
		}

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Journal Table: " + msg); //Save Journal Filters and Export Settings
		}
	}
}