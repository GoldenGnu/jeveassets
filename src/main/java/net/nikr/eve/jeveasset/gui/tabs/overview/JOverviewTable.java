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

package net.nikr.eve.jeveasset.gui.tabs.overview;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


class JOverviewTable extends JAutoColumnTable {

	private List<String> groupedLocations = new ArrayList<String>();
	private DefaultEventTableModel<Overview> tableModel;

	public JOverviewTable(final Program program, final DefaultEventTableModel<Overview> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	public void setGroupedLocations(final List<String> groupedLocations) {
		this.groupedLocations = groupedLocations;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Overview overview = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		if (groupedLocations.contains(overview.getName()) && columnName.equals(OverviewTableFormat.NAME.getColumnName())) { //In group
			if (!isSelected) {
				component.setBackground(new Color(200, 255, 200));
			} else {
				component.setBackground(this.getSelectionBackground().darker());
			}
			return component;
		}
		if (groupedLocations.contains(overview.getLocation().getSystem()) && columnName.equals(OverviewTableFormat.SYSTEM.getColumnName())) { //In group
			if (!isSelected) {
				component.setBackground(new Color(200, 255, 200));
			} else {
				component.setBackground(this.getSelectionBackground().darker());
			}
			return component;
		}
		if (groupedLocations.contains(overview.getLocation().getRegion()) && columnName.equals(OverviewTableFormat.REGION.getColumnName())) { //In group
			if (!isSelected) {
				component.setBackground(new Color(200, 255, 200));
			} else {
				component.setBackground(this.getSelectionBackground().darker());
			}
			return component;
		}
		return component;
	}
}
