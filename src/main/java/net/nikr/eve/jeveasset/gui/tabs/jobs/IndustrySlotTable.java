/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


public class IndustrySlotTable extends JAutoColumnTable {

	private final DefaultEventTableModel<IndustrySlot> tableModel;

	public IndustrySlotTable(final Program program, final DefaultEventTableModel<IndustrySlot> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		IndustrySlot industrySlot = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
		//Grand Total
		if (industrySlot.isGrandTotal()) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_GRAND_TOTAL, isSelected);
			return component;
		}
		if (industrySlot.isEmpty() && columnName.equals(IndustrySlotTableFormat.NAME.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_ENTRY_INVALID, isSelected);
			return component;
		}
		if (industrySlot.isManufacturingFree() && columnName.equals(IndustrySlotTableFormat.MANUFACTURING_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FREE, isSelected);
			return component;
		}
		if (industrySlot.isManufacturingDone() && columnName.equals(IndustrySlotTableFormat.MANUFACTURING_DONE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_DONE, isSelected);
			return component;
		}
		if (industrySlot.isManufacturingFull() && columnName.equals(IndustrySlotTableFormat.MANUFACTURING_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FULL, isSelected);
			return component;
		}
		if (industrySlot.isReactionsFree() && columnName.equals(IndustrySlotTableFormat.REACTIONS_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FREE, isSelected);
			return component;
		}
		if (industrySlot.isReactionsDone() && columnName.equals(IndustrySlotTableFormat.REACTIONS_DONE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_DONE, isSelected);
			return component;
		}
		if (industrySlot.isReactionsFull() && columnName.equals(IndustrySlotTableFormat.REACTIONS_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FULL, isSelected);
			return component;
		}
		if (industrySlot.isResearchFree() && columnName.equals(IndustrySlotTableFormat.RESEARCH_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FREE, isSelected);
			return component;
		}
		if (industrySlot.isResearchDone() && columnName.equals(IndustrySlotTableFormat.RESEARCH_DONE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_DONE, isSelected);
			return component;
		}
		if (industrySlot.isResearchFull() && columnName.equals(IndustrySlotTableFormat.RESEARCH_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FULL, isSelected);
			return component;
		}
		return component;
	}

}
