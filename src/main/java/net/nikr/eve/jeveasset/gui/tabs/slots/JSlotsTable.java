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

package net.nikr.eve.jeveasset.gui.tabs.slots;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


public class JSlotsTable extends JAutoColumnTable {

	private final DefaultEventTableModel<Slots> tableModel;

	public JSlotsTable(final Program program, final DefaultEventTableModel<Slots> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Slots slots = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
		//Grand Total
		if (slots.isGrandTotal()) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_GRAND_TOTAL, isSelected);
			return component;
		}
		if (slots.isEmpty() && columnName.equals(SlotsTableFormat.NAME.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_ENTRY_INVALID, isSelected);
			return component;
		}
		if (slots.isManufacturingFree() && columnName.equals(SlotsTableFormat.MANUFACTURING_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FREE, isSelected);
			return component;
		}
		if (slots.isManufacturingDone() && columnName.equals(SlotsTableFormat.MANUFACTURING_DONE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_DONE, isSelected);
			return component;
		}
		if (slots.isManufacturingFull() && columnName.equals(SlotsTableFormat.MANUFACTURING_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FULL, isSelected);
			return component;
		}
		if (slots.isReactionsFree() && columnName.equals(SlotsTableFormat.REACTIONS_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FREE, isSelected);
			return component;
		}
		if (slots.isReactionsDone() && columnName.equals(SlotsTableFormat.REACTIONS_DONE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_DONE, isSelected);
			return component;
		}
		if (slots.isReactionsFull() && columnName.equals(SlotsTableFormat.REACTIONS_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FULL, isSelected);
			return component;
		}
		if (slots.isResearchFree() && columnName.equals(SlotsTableFormat.RESEARCH_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FREE, isSelected);
			return component;
		}
		if (slots.isResearchDone() && columnName.equals(SlotsTableFormat.RESEARCH_DONE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_DONE, isSelected);
			return component;
		}
		if (slots.isResearchFull() && columnName.equals(SlotsTableFormat.RESEARCH_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FULL, isSelected);
			return component;
		}
		if (slots.isMarketOrdersFree() && columnName.equals(SlotsTableFormat.MARKET_ORDERS_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FREE, isSelected);
			return component;
		}
		if (slots.isMarketOrdersFull() && columnName.equals(SlotsTableFormat.MARKET_ORDERS_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FULL, isSelected);
			return component;
		}
		if (slots.isContractCharacterFree() && columnName.equals(SlotsTableFormat.CONTRACT_CHARACTER_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FREE, isSelected);
			return component;
		}
		if (slots.isContractCharacterFull() && columnName.equals(SlotsTableFormat.CONTRACT_CHARACTER_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FULL, isSelected);
			return component;
		}
		if (slots.isContractCorporationFree() && columnName.equals(SlotsTableFormat.CONTRACT_CORPORATION_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FREE, isSelected);
			return component;
		}
		if (slots.isContractCorporationFull() && columnName.equals(SlotsTableFormat.CONTRACT_CORPORATION_FREE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.INDUSTRY_SLOTS_FULL, isSelected);
			return component;
		}
		return component;
	}

}
