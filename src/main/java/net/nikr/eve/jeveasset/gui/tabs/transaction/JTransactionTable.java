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

package net.nikr.eve.jeveasset.gui.tabs.transaction;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


public class JTransactionTable extends JAutoColumnTable {

	final DefaultEventTableModel<Transaction> tableModel;

	public JTransactionTable(Program program, final DefaultEventTableModel<Transaction> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Transaction transaction = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		//User set price
		if (columnName.equals(TransactionTableFormat.NAME.getColumnName())) {
			if (transaction.isSell()) {
				if (!isSelected) {
					component.setBackground(new Color(200, 255, 200));
				} else {
					component.setBackground(this.getSelectionBackground().darker());
				}
			} else {
				if (!isSelected) {
					component.setBackground(new Color(255, 200, 200));
				} else {
					component.setBackground(this.getSelectionBackground().darker());
				}
			}
		}
		if (columnName.equals(TransactionTableFormat.VALUE.getColumnName()) && transaction.isBuy()) {
			if (!isSelected) {
				component.setForeground(Color.RED.darker());
			} else {
				component.setForeground(new Color(255, 200, 200));
			}
		}
		return component;
	}
	
}
