/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.contracts;

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.gui.shared.Colors;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;


public class JContractsTable extends JSeparatorTable {

	private final DefaultEventTableModel<MyContractItem> tableModel;

	public JContractsTable(final Program program, final DefaultEventTableModel<MyContractItem> tableModel, SeparatorList<?> separatorList) {
		super(program, tableModel, separatorList);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Object object = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		if (object instanceof MyContractItem) {
			MyContractItem item = (MyContractItem) object;
			if (columnName.equals(ContractsTableFormat.NAME.getColumnName())) {
				if (isSelected) {
					component.setBackground(this.getSelectionBackground().darker());
				} else if (item.getContract().isCourierContract()) {
					component.setBackground(Colors.LIGHT_YELLOW.getColor());
				} else if (item.isIncluded()) {
					component.setBackground(Colors.LIGHT_GREEN.getColor());
				} else {
					component.setBackground(Colors.LIGHT_RED.getColor());
				}
			}
			//User set location
			if ((item.getContract().getStartLocation() != null && item.getContract().getStartLocation().isUserLocation() && columnName.equals(ContractsTableFormat.START_STATION.getColumnName()))
					|| (item.getContract().getEndLocation() != null && item.getContract().getEndLocation().isUserLocation() && columnName.equals(ContractsTableFormat.END_STATION.getColumnName()))) {
				if (!isSelected) {
					component.setBackground(Colors.LIGHT_GRAY.getColor());
				} else {
					component.setBackground(this.getSelectionBackground().darker());
				}
				return component;
			}
		}
		return component;
	}
}
