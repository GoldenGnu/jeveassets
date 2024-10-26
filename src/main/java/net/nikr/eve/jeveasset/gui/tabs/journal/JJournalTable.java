/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.journal;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


public class JJournalTable extends JAutoColumnTable {

	private final DefaultEventTableModel<MyJournal> tableModel;

	public JJournalTable(Program program, final DefaultEventTableModel<MyJournal> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		MyJournal journal = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		if (columnName.equals(JournalTableFormat.AMOUNT.getColumnName()) && journal.getAmount() < 0) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_VALUE_NEGATIVE, isSelected);
		}
		if (columnName.equals(JournalTableFormat.BALANCE.getColumnName()) && journal.getBalance() < 0) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_VALUE_NEGATIVE, isSelected);
		}
		//Added date
		if (columnName.equals(JournalTableFormat.ADDED.getColumnName()) && Settings.get().getTableChanged(JournalTab.NAME).before(journal.getAdded())) {
			ColorSettings.configCell(component, ColorEntry.JOURNAL_NEW, isSelected);
		}
		return component;
	}

}
