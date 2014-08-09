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
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob.IndustryActivity;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.i18n.TabsJobs;


public class IndustryJobsTab extends JMainTab {

	private final JAutoColumnTable jTable;
	private final JLabel jInventionSuccess;
	private final JLabel jManufactureOutputValue;

	//Table
	private final EventList<MyIndustryJob> eventList;
	private final FilterList<MyIndustryJob> filterList;
	private final DefaultEventTableModel<MyIndustryJob> tableModel;
	private final DefaultEventSelectionModel<MyIndustryJob> selectionModel;
	private final IndustryJobsFilterControl filterControl;
	private final EnumTableFormatAdaptor<IndustryJobTableFormat, MyIndustryJob> tableFormat;

	public static final String NAME = "industryjobs"; //Not to be changed!

	public IndustryJobsTab(final Program program) {
		super(program, TabsJobs.get().industry(), Images.TOOL_INDUSTRY_JOBS.getIcon(), true);

		ListenerClass listener = new ListenerClass();
		//Table Format
		tableFormat = new EnumTableFormatAdaptor<IndustryJobTableFormat, MyIndustryJob>(IndustryJobTableFormat.class);
		//Backend
		eventList = program.getIndustryJobsEventList();
		//Sorting (per column)
		SortedList<MyIndustryJob> sortedList = new SortedList<MyIndustryJob>(eventList);
		//Filter
		filterList = new FilterList<MyIndustryJob>(sortedList);
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
		filter.add(new Filter(LogicType.AND, IndustryJobTableFormat.STATE, CompareType.EQUALS_NOT, IndustryJobState.STATE_DELIVERED.toString()));
		defaultFilters.put(TabsJobs.get().active(), filter);
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.AND, IndustryJobTableFormat.STATE, CompareType.EQUALS, IndustryJobState.STATE_DELIVERED.toString()));
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
		installMenu(program, new JobsTableMenu(), jTable, MyIndustryJob.class);

		jInventionSuccess = StatusPanel.createLabel(TabsJobs.get().inventionSuccess(), Images.JOBS_INVENTION_SUCCESS.getIcon());
		this.addStatusbarLabel(jInventionSuccess);

		jManufactureOutputValue = StatusPanel.createLabel(TabsJobs.get().manufactureJobsValue(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jManufactureOutputValue);

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

	private class JobsTableMenu implements TableMenu<MyIndustryJob> {
		@Override
		public MenuData<MyIndustryJob> getMenuData() {
			return new MenuData<MyIndustryJob>(selectionModel.getSelected());
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

	private class ListenerClass implements ListEventListener<MyIndustryJob> {
		@Override
		public void listChanged(final ListEvent<MyIndustryJob> listChanges) {
			int count = 0;
			double success = 0;
			double outputValue = 0;
			for (MyIndustryJob industryJob : filterList) {
				if (industryJob.isInvention() && industryJob.isCompleted()) {
					count++;
					if (industryJob.isDelivered()) {
						success++;
					}
				}
				outputValue += industryJob.getOutputValue();
			}
			if (count <= 0) {
				jInventionSuccess.setText(Formater.percentFormat(0.0));
			} else {
				jInventionSuccess.setText(Formater.percentFormat(success / count));
			}
			jManufactureOutputValue.setText(Formater.iskFormat(outputValue));
		}
	}

	public class IndustryJobsFilterControl extends FilterControl<MyIndustryJob> {

		private final EnumTableFormatAdaptor<IndustryJobTableFormat, MyIndustryJob> tableFormat;

		public IndustryJobsFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<IndustryJobTableFormat, MyIndustryJob> tableFormat, final EventList<MyIndustryJob> eventList, final FilterList<MyIndustryJob> filterList, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
			super(jFrame, NAME, eventList, filterList, filters, defaultFilters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final MyIndustryJob item, final String column) {
			IndustryJobTableFormat format = IndustryJobTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return IndustryJobTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<MyIndustryJob>> getColumns() {
			return columnsAsList(IndustryJobTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<MyIndustryJob>> getShownColumns() {
			return new ArrayList<EnumTableColumn<MyIndustryJob>>(tableFormat.getShownColumns());
		}

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("Save Industry Job " + msg); //Save Industry Job Filters and Export Setttings
		}
	}
}
