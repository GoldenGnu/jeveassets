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

package net.nikr.eve.jeveasset.gui.tabs.jobs;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.*;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.i18n.TabsJobs;


public class IndustryJobsTab extends JMainTab {

	private JAutoColumnTable jTable;

	private EventList<IndustryJob> jobsEventList;
	private EventTableModel<IndustryJob> jobsTableModel;

	private IndustryJobData data;
	private IndustryJobsFilterControl filterControl;
	private EnumTableFormatAdaptor<IndustryJobTableFormat, IndustryJob> industryJobsTableFormat;
	
	public static final String NAME = "industryjobs"; //Not to be changed!

	public IndustryJobsTab(Program program) {
		super(program, TabsJobs.get().industry(), Images.TOOL_INDUSTRY_JOBS.getIcon(), true);

		//Table format
		industryJobsTableFormat = new EnumTableFormatAdaptor<IndustryJobTableFormat, IndustryJob>(IndustryJobTableFormat.class);
		industryJobsTableFormat.setColumns(program.getSettings().getTableColumns().get(NAME));
		//Backend
		jobsEventList = new BasicEventList<IndustryJob>();
		
		FilterList<IndustryJob> filterList = new FilterList<IndustryJob>(jobsEventList);
		//For soring the table
		SortedList<IndustryJob> sortedList = new SortedList<IndustryJob>(filterList);
		//Table Model
		jobsTableModel = new EventTableModel<IndustryJob>(sortedList, industryJobsTableFormat);
		//Tables
		jTable = new JAutoColumnTable(jobsTableModel);
		jTable.setCellSelectionEnabled(true);
		//Table Selection
		EventSelectionModel<IndustryJob> selectionModel = new EventSelectionModel<IndustryJob>(sortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTableMenu(jTable);
		//Sorters
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, industryJobsTableFormat);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		
		//Filter
		filterControl = new IndustryJobsFilterControl(
				program.getMainWindow().getFrame(),
				program.getSettings().getTableFilters(NAME),
				filterList,
				jobsEventList);

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
	public void updateSettings(){
		program.getSettings().getTableColumns().put(NAME, industryJobsTableFormat.getColumns());
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();

		selectClickedCell(e);

		updateTableMenu(jTablePopupMenu);

		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void updateData() {

		if (data == null) {
			data = new IndustryJobData(program);
		}
		data.updateData();

		if (!data.getCharacters().isEmpty()){
			jTable.setEnabled(true);
			Collections.sort(data.getCharacters());
			data.getCharacters().add(0, TabsJobs.get().all());
		} else {
			jTable.setEnabled(false);
		}
		try {
			jobsEventList.getReadWriteLock().writeLock().lock();
			jobsEventList.clear();
			jobsEventList.addAll( data.getAll() );
		} finally {
			jobsEventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSingleRow = jTable.getSelectedRows().length == 1;
		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);
		
		IndustryJob industryJob = isSingleRow ? jobsTableModel.getElementAt(jTable.getSelectedRow()) : null;
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
		jComponent.add(filterControl.getMenu(jTable, industryJob));
		jComponent.add(new JMenuAssetFilter(program, industryJob));
		jComponent.add(new JMenuStockpile(program, industryJob));
		jComponent.add(new JMenuLookup(program, industryJob));
		
		//Columns
		jComponent.add(industryJobsTableFormat.getMenu(jobsTableModel, jTable));
	}
	
	public static class IndustryJobsFilterControl extends FilterControl<IndustryJob>{

		public IndustryJobsFilterControl(JFrame jFrame, Map<String, List<Filter>> filters, FilterList<IndustryJob> filterList, EventList<IndustryJob> eventList) {
			super(jFrame, filters, filterList, eventList);
		}
		
		@Override
		protected Object getColumnValue(IndustryJob item, String column) {
			IndustryJobTableFormat format = IndustryJobTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}
		
		@Override
		protected boolean isNumericColumn(Enum column) {
			IndustryJobTableFormat format = (IndustryJobTableFormat) column;
			if (Number.class.isAssignableFrom(format.getType())) {
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		protected boolean isDateColumn(Enum column) {
			IndustryJobTableFormat format = (IndustryJobTableFormat) column;
			if (format.getType().getName().equals(Date.class.getName())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Enum[] getColumns() {
			return IndustryJobTableFormat.values();
		}
		
		@Override
		protected Enum valueOf(String column) {
			return IndustryJobTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<IndustryJob>> getEnumColumns() {
			return columnsAsList(IndustryJobTableFormat.values());
		}
	}
}
