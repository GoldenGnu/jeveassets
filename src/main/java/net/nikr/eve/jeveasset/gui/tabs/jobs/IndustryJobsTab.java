/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob.IndustryJobStatus;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.JStatusLabel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.TabsJobs;
import net.nikr.eve.jeveasset.io.local.profile.ProfileDatabase;


public class IndustryJobsTab extends JMainTabPrimary {

	private final JAutoColumnTable jTable;
	private final JStatusLabel jCount;
	private final JStatusLabel jInventionSuccess;
	private final JStatusLabel jManufactureOutputValue;

	//Table
	private final EventList<MyIndustryJob> eventList;
	private final FilterList<MyIndustryJob> filterList;
	private final DefaultEventTableModel<MyIndustryJob> tableModel;
	private final DefaultEventSelectionModel<MyIndustryJob> selectionModel;
	private final IndustryJobsFilterControl filterControl;
	private final EnumTableFormatAdaptor<IndustryJobTableFormat, MyIndustryJob> tableFormat;

	public static final String NAME = "industryjobs"; //Not to be changed!

	public IndustryJobsTab(final Program program) {
		super(program, NAME, TabsJobs.get().industry(), Images.TOOL_INDUSTRY_JOBS.getIcon(), true);

		ListenerClass listener = new ListenerClass();
		//Table Format
		tableFormat = TableFormatFactory.industryJobTableFormat();
		//Backend
		eventList = program.getProfileData().getIndustryJobsEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyIndustryJob> sortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();

		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedList);
		eventList.getReadWriteLock().readLock().unlock();
		filterList.addListEventListener(listener);
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JIndustryJobsTable(program, tableModel);
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
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new IndustryJobsFilterControl(sortedList);
		//Menu
		installTableTool(new JobsTableMenu(), tableFormat, tableModel, jTable, filterControl, MyIndustryJob.class);

		jInventionSuccess = StatusPanel.createLabel(TabsJobs.get().inventionSuccess(), Images.JOBS_INVENTION_SUCCESS.getIcon(), AutoNumberFormat.PERCENT);
		this.addStatusbarLabel(jInventionSuccess);

		jManufactureOutputValue = StatusPanel.createLabel(TabsJobs.get().manufactureJobsValue(), Images.TOOL_VALUES.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jManufactureOutputValue);

		jCount = StatusPanel.createLabel(TabsJobs.get().count(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.ITEMS);
		this.addStatusbarLabel(jCount);

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
	public void clearData() {
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

	public void addFilters(final List<Filter> filters) {
		filterControl.addFilters(filters);
	}

	private MyIndustryJob getSelectedIndustryJob() {
		int index = jTable.getSelectedRow();
		if (index < 0 || index >= tableModel.getRowCount()) {
			return null;
		}
		return tableModel.getElementAt(index);
	}

	private class JobsTableMenu implements TableMenu<MyIndustryJob> {
		@Override
		public MenuData<MyIndustryJob> getMenuData() {
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
		public void addInfoMenu(JPopupMenu jPopupMenu) {
			JMenuInfo.industryJob(jPopupMenu, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) {
			MyIndustryJob industryJob = getSelectedIndustryJob();
			boolean enabled = industryJob != null && !industryJob.isESI() && selectionModel.getSelected().size() == 1;

			JMenu jStatus = new JMenu(TabsJobs.get().status());
			jStatus.setIcon(Images.MISC_STATUS.getIcon());
			if (!enabled) {
				jStatus.setIcon(jStatus.getDisabledIcon());
			}
			jComponent.add(jStatus);

			JRadioButtonMenuItem jMenuItem;
			for (IndustryJobStatus status : IndustryJobStatus.values()) {
				jMenuItem = new JRadioButtonMenuItem(MyIndustryJob.getStatusName(status));
				jMenuItem.setEnabled(enabled);
				jMenuItem.setSelected(enabled && status == industryJob.getStatus());
				jMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (industryJob == null || industryJob.isESI() || industryJob.getStatus() == status) {
							return;
						}
						boolean before = industryJob.isNotDeliveredToAssets();
						industryJob.setStatus(status);
						if (before != industryJob.isNotDeliveredToAssets()) {
							program.updateEventListsWithProgress();
						} else {
							tableModel.fireTableDataChanged();
						}
						program.saveTable(ProfileDatabase.Table.INDUSTRY_JOBS);
					}
				});
				jStatus.add(jMenuItem);
			}
			MenuManager.addSeparator(jComponent);
		}
	}

	private class ListenerClass implements ListEventListener<MyIndustryJob> {
		@Override
		public void listChanged(final ListEvent<MyIndustryJob> listChanges) {
			int inventionCount = 0;
			long count = 0;
			double success = 0;
			double outputValue = 0;
			try {
				filterList.getReadWriteLock().readLock().lock();
				for (MyIndustryJob industryJob : filterList) {
					count++;
					if (industryJob.isInvention() && industryJob.isDone()) {
						inventionCount++;
						if (industryJob.isCompletedSuccessful()) {
							success++;
						}
					}
					if (industryJob.isNotDeliveredToAssets()) { //Only include active jobs
						outputValue += industryJob.getOutputValue();
					}
				}
			} finally {
				filterList.getReadWriteLock().readLock().unlock();
			}
			if (inventionCount <= 0) {
				jInventionSuccess.setNumber(0.0);
			} else {
				jInventionSuccess.setNumber(success / inventionCount);
			}
			jManufactureOutputValue.setNumber(outputValue);
			jCount.setNumber(count);
		}
	}

	public class IndustryJobsFilterControl extends FilterControl<MyIndustryJob> {

		public IndustryJobsFilterControl(EventList<MyIndustryJob> exportEventList) {
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
			program.saveSettings("Industry Jobs Table: " + msg); //Save Industry Job Filters and Export Settings
		}
	}
}
