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

package net.nikr.eve.jeveasset.gui.tabs.jobs;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


public class JIndustryJobsTable extends JAutoColumnTable {

	private final DefaultEventTableModel<MyIndustryJob> tableModel;

	public JIndustryJobsTable(Program program, final DefaultEventTableModel<MyIndustryJob> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		MyIndustryJob industryJob = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		//BPO
		if (industryJob.isBPO() && columnName.equals(IndustryJobTableFormat.NAME.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_BPO, isSelected);
			return component;
		}
		//BPC
		if (industryJob.isBPC() && columnName.equals(IndustryJobTableFormat.NAME.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_BPC, isSelected);
			return component;
		}
		//Completed
		if (industryJob.getState() == MyIndustryJob.IndustryJobState.STATE_DONE && columnName.equals(IndustryJobTableFormat.END_DATE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_JOBS_DONE, isSelected);
			return component;
		}
		if (columnName.equals(IndustryJobTableFormat.ACTIVITY.getColumnName())) {
			ColorEntry entry = null;
			switch (industryJob.getActivity()) {
				case ACTIVITY_MANUFACTURING:
					entry = ColorEntry.INDUSTRY_JOBS_ACTIVITY_MANUFACTURING;
					break;
				case ACTIVITY_RESEARCHING_TECHNOLOGY:
					entry = ColorEntry.INDUSTRY_JOBS_ACTIVITY_RESEARCHING_TECHNOLOGY;
					break;
				case ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY:
					entry = ColorEntry.INDUSTRY_JOBS_ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY;
					break;
				case ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY:
					entry = ColorEntry.INDUSTRY_JOBS_ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY;
					break;
				case ACTIVITY_COPYING:
					entry = ColorEntry.INDUSTRY_JOBS_ACTIVITY_COPYING;
					break;
				case ACTIVITY_DUPLICATING:
					entry = ColorEntry.INDUSTRY_JOBS_ACTIVITY_DUPLICATING;
					break;
				case ACTIVITY_REVERSE_ENGINEERING:
					entry = ColorEntry.INDUSTRY_JOBS_ACTIVITY_REVERSE_ENGINEERING;
					break;
				case ACTIVITY_REVERSE_INVENTION:
					entry = ColorEntry.INDUSTRY_JOBS_ACTIVITY_REVERSE_INVENTION;
					break;
				case ACTIVITY_REACTIONS:
					entry = ColorEntry.INDUSTRY_JOBS_ACTIVITY_REACTIONS;
					break;
			}
			if (entry != null) {
				ColorSettings.configCell(component, entry, isSelected);
				return component;
			}
		}
		//Delivered
		if (industryJob.getState() == MyIndustryJob.IndustryJobState.STATE_DELIVERED && columnName.equals(IndustryJobTableFormat.END_DATE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_JOBS_DELIVERED, isSelected);
			return component;
		}
		//User set location
		if (industryJob.getLocation().isUserLocation() && columnName.equals(IndustryJobTableFormat.LOCATION.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.CUSTOM_USER_LOCATION, isSelected);
			return component;
		}
		return component;
	}

}
