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

package net.nikr.eve.jeveasset.gui.tabs.transaction;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


public class JTransactionTable extends JAutoColumnTable {

	private final DefaultEventTableModel<MyTransaction> tableModel;

	public JTransactionTable(Program program, final DefaultEventTableModel<MyTransaction> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		MyTransaction transaction = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		if (columnName.equals(TransactionTableFormat.NAME.getColumnName())) {
			if (transaction.isSell()) {
				ColorSettings.configCell(component, ColorEntry.TRANSACTIONS_SOLD, isSelected);
			} else {
				ColorSettings.configCell(component, ColorEntry.TRANSACTIONS_BOUGHT, isSelected);
			}
		}
		if (columnName.equals(TransactionTableFormat.VALUE.getColumnName()) && transaction.isBuy()) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_VALUE_NEGATIVE, isSelected);
		}
		//User set location
		if (transaction.getLocation().isUserLocation() && columnName.equals(TransactionTableFormat.LOCATION.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.CUSTOM_USER_LOCATION, isSelected);
			return component;
		}
		//Added date
		if (columnName.equals(TransactionTableFormat.ADDED.getColumnName()) && Settings.get().getTableChanged(TransactionTab.NAME).before(transaction.getAdded())) {
			ColorSettings.configCell(component, ColorEntry.TRANSACTIONS_NEW, isSelected);
		}
		return component;
	}

}
