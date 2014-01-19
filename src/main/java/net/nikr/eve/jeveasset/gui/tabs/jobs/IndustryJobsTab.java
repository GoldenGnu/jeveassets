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

package net.nikr.eve.jeveasset.gui.tabs.jobs;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob.IndustryActivity;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.i18n.TabsJobs;


public class IndustryJobsTab extends JMainTab {

	private JAutoColumnTable jTable;
	private JLabel jInventionSuccess;

	//Table
	private EventList<IndustryJob> eventList;
	private FilterList<IndustryJob> filterList;
	private DefaultEventTableModel<IndustryJob> tableModel;
	private DefaultEventSelectionModel<IndustryJob> selectionModel;
	private IndustryJobsFilterControl filterControl;
	private EnumTableFormatAdaptor<IndustryJobTableFormat, IndustryJob> tableFormat;

	public static final String NAME = "industryjobs"; //Not to be changed!

	public IndustryJobsTab(final Program program) {
		super(program, TabsJobs.get().industry(), Images.TOOL_INDUSTRY_JOBS.getIcon(), true);

		ListenerClass listener = new ListenerClass();
		//Table Format
		tableFormat = new EnumTableFormatAdaptor<IndustryJobTableFormat, IndustryJob>(IndustryJobTableFormat.class);
		//Backend
		eventList = program.getIndustryJobsEventList();
		//Sorting (per column)
		SortedList<IndustryJob> sortedList = new SortedList<IndustryJob>(eventList);
		//Filter
		filterList = new FilterList<IndustryJob>(sortedList);
		filterList.addListEventListener(listener);
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JAutoColumnTable(program, tableModel);
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
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		Map<String, List<Filter>> defaultFilters = new HashMap<String, List<Filter>>();
		List<Filter> filter;
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.OR, IndustryJobTableFormat.STATE, CompareType.EQUALS,  IndustryJobState.STATE_ACTIVE.toString()));
		filter.add(new Filter(LogicType.OR, IndustryJobTableFormat.STATE, CompareType.EQUALS,  IndustryJobState.STATE_PENDING.toString()));
		filter.add(new Filter(LogicType.OR, IndustryJobTableFormat.STATE, CompareType.EQUALS,  IndustryJobState.STATE_READY.toString()));
		defaultFilters.put(TabsJobs.get().active(), filter);
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.AND, IndustryJobTableFormat.STATE, CompareType.EQUALS_NOT,  IndustryJobState.STATE_ACTIVE.toString()));
		filter.add(new Filter(LogicType.AND, IndustryJobTableFormat.STATE, CompareType.EQUALS_NOT,  IndustryJobState.STATE_PENDING.toString()));
		filter.add(new Filter(LogicType.AND, IndustryJobTableFormat.STATE, CompareType.EQUALS_NOT,  IndustryJobState.STATE_READY.toString()));
		defaultFilters.put(TabsJobs.get().completed(), filter);
		filterControl = new IndustryJobsFilterControl(
				program.getMainWindow().getFrame(),
				tableFormat,
				sortedList,
				filterList,
				Settings.get().getTableFilters(NAME),
				defaultFilters
				);

		//Menu
		installMenu(program, new JobsTableMenu(), jTable, IndustryJob.class);

		jInventionSuccess = StatusPanel.createLabel(TabsJobs.get().inventionSuccess(), Images.JOBS_INVENTION_SUCCESS.getIcon());
		this.addStatusbarLabel(jInventionSuccess);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 700, 700, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 100, 400, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateData() { }

	private class JobsTableMenu implements TableMenu<IndustryJob> {
		@Override
		public MenuData<IndustryJob> getMenuData() {
			return new MenuData<IndustryJob>(selectionModel.getSelected());
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
		public void addInfoMenu(JComponent jComponent) {
			JMenuInfo.industryJob(jComponent, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ListEventListener<IndustryJob> {
		@Override
		public void listChanged(final ListEvent<IndustryJob> listChanges) {
			int count = 0;
			double success = 0;
			for (IndustryJob industryJob : filterList) {
				if (industryJob.getActivity() == IndustryActivity.ACTIVITY_REVERSE_INVENTION && industryJob.isCompleted()) {
					count++;
					if (industryJob.getState() == IndustryJobState.STATE_DELIVERED) {
						success++;
					}
				}
			}
			if (count <= 0) {
				jInventionSuccess.setText(Formater.percentFormat(0.0));
			} else {
				jInventionSuccess.setText(Formater.percentFormat(success / count));
			}
		}
	}

	public static class IndustryJobsFilterControl extends FilterControl<IndustryJob> {

		private EnumTableFormatAdaptor<IndustryJobTableFormat, IndustryJob> tableFormat;

		public IndustryJobsFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<IndustryJobTableFormat, IndustryJob> tableFormat, final EventList<IndustryJob> eventList, final FilterList<IndustryJob> filterList, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
			super(jFrame, NAME, eventList, filterList, filters, defaultFilters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final IndustryJob item, final String column) {
			IndustryJobTableFormat format = IndustryJobTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return IndustryJobTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<IndustryJob>> getColumns() {
			return columnsAsList(IndustryJobTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<IndustryJob>> getShownColumns() {
			return new ArrayList<EnumTableColumn<IndustryJob>>(tableFormat.getShownColumns());
		}
	}
}
