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

package net.nikr.eve.jeveasset.gui.shared;

import ca.odell.glazedlists.SeparatorList;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat;


public class CopyHandler {

	public static void installCopyAction(AbstractButton abstractButton, JTable jTable) {
		abstractButton.addActionListener(new ListenerClass(jTable));
	}

	/**
	 * Install default copy format
	 * @param jTable 
	 */
	public static void installCopyFormatter(JTable jTable) {
		ListenerClass listenerClass = new ListenerClass(jTable);
		//jTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl C"), "copy");
		jTable.getActionMap().put("copy", listenerClass);
		jTable.getActionMap().put("cut", listenerClass);	
	}

	private static void copy(JTable jTable) {
		//Rows
		int[] rows;
		if (jTable.getRowSelectionAllowed()) { //Selected rows
			rows = jTable.getSelectedRows();
		} else { //All rows (if row selection is not allowed)
			rows = new int[jTable.getRowCount()];
			for (int i = 0; i < jTable.getRowCount(); i++) {
				rows[i] = i;
			}
		}

		//Columns
		int[] columns;
		if (jTable.getColumnSelectionAllowed()) { //Selected columns
			columns = jTable.getSelectedColumns();
		} else { //All columns (if column selection is not allowed)
			columns = new int[jTable.getColumnCount()];
			for (int i = 0; i < jTable.getColumnCount(); i++) {
				columns[i] = i;
			}
		}
		StringBuilder tableText = new StringBuilder(); //Table text buffer
		String separatorText = ""; //Separator text buffer (is never added to, only set for each separator)
		int rowCount = 0; //used to find last row

		for (int row : rows) {
			rowCount++; //count rows
			StringBuilder rowText = new StringBuilder(); //Row text buffer
			boolean firstColumn = true; //used to find first column
			for (int column : columns) {
				//Get value
				Object value = jTable.getValueAt(row, column);

				//Handle Separator
				if (value instanceof SeparatorList.Separator) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator) value;
					Object object = separator.first();
					if (object instanceof CopySeparator) {
						CopySeparator copySeparator = (CopySeparator) object;
						separatorText = copySeparator.getCopyString();
					}
					break;
				}

				//Add tab separator (except for first column)
				if (firstColumn) {
					firstColumn = false;
				} else {
					rowText.append("\t");
				}

				//Add value
				if (value != null) { //Ignore null
					//Format value to displayed value
					if (value instanceof Float) {
						rowText.append(Formater.floatFormat(value));
					} else if (value instanceof Double) {
						rowText.append(Formater.doubleFormat(value));
					} else if (value instanceof Integer) {
						rowText.append(Formater.integerFormat(value));
					} else if (value instanceof Long) {
						rowText.append(Formater.longFormat(value));
					} else if (value instanceof Date) {
						rowText.append(Formater.columnDate(value));
					} else if (value instanceof TreeTableFormat.HierarchyColumn) {
						TreeTableFormat.HierarchyColumn hierarchyColumn = (TreeTableFormat.HierarchyColumn) value;
						rowText.append(hierarchyColumn.getExport());
					} else {
						rowText.append(value.toString()); //Default
					}
				}
			}

			//Add
			if (rowText.length() > 0 || (!separatorText.isEmpty() && rowCount == rows.length)) {
				tableText.append(separatorText); //Add separator text (will be empty for normal tables)
				if (rowText.length() > 0 && !separatorText.isEmpty()) { //Add tab separator (if needed)
					tableText.append("\t");
				}
				tableText.append(rowText.toString()); //Add row text (will be empty if only copying sinlge separator)
				if (rowCount != rows.length) {
					tableText.append("\r\n");
				} //Add end line
			}
		}
		copyToClipboard(tableText.toString()); //Send it all to the clipboard
	}

	private static void copyToClipboard(final String text) {
		SecurityManager securityManager = System.getSecurityManager();
		if (securityManager != null) {
			try {
				securityManager.checkSystemClipboardAccess();
			} catch (Exception ex) {
				return;
			}
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		StringSelection selection = new StringSelection(text);
		Clipboard clipboard = toolkit.getSystemClipboard();
		clipboard.setContents(selection, null);
	}

	/**
	 * SeparatorList.Separator string value implementation
	 */
	public interface CopySeparator {
		public String getCopyString();
	}

	private static class ListenerClass extends AbstractAction {

		private JTable jTable;

		public ListenerClass(JTable jTable) {
			this.jTable = jTable;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			copy(jTable);
		}
	}
}
