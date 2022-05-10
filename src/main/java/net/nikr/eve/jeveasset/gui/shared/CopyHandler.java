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

package net.nikr.eve.jeveasset.gui.shared;

import ca.odell.glazedlists.SeparatorList;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.gui.shared.table.containers.HierarchyColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CopyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(CopyHandler.class);

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

	public static void toClipboard(final String text) {
		toClipboard(text, 0);
	}

	private static void toClipboard(final String text, int retries) {
		if (text == null) {
			return;
		}
		if (text.length() == 0) {
			return;
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		StringSelection selection = new StringSelection(text);
		Clipboard clipboard = toolkit.getSystemClipboard();
		try {
			clipboard.setContents(selection, null);
		} catch (IllegalStateException ex) {
			if (retries < 3) { //Retry 3 times
				retries++;
				LOG.info("Retrying copy to clipboard (" + retries + " of 3)" );
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex1) {
					//No problem
				}
				toClipboard(text, retries);
			} else {
				LOG.error(ex.getMessage(), ex);
			}
		}
	}

	public static String fromClipboard() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		Transferable transferable = clipboard.getContents(null);
		try {
			return (String) transferable.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException ex) {
			return null;
		} catch (IOException ex) {
			return null;
		}
	}

	public static void paste(JTextComponent jText) {
		String s = CopyHandler.fromClipboard();
		if (s == null) {
			return;
		}
		String text = jText.getText();
		String before = text.substring(0, jText.getSelectionStart());
		String after = text.substring(jText.getSelectionEnd(), text.length());
		jText.setText(before + s + after);
		int caretPosition = before.length() + s.length();
		if (caretPosition <= jText.getText().length()) {
			jText.setCaretPosition(before.length() + s.length());
		}
	}

	public static void cut(JTextComponent jText) {
		String s = jText.getSelectedText();
		if (s == null) {
			return;
		}
		if (s.length() == 0) {
			return;
		}
		String text = jText.getText();
		String before = text.substring(0, jText.getSelectionStart());
		String after = text.substring(jText.getSelectionEnd(), text.length());
		jText.setText(before + after);
		CopyHandler.toClipboard(s);
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
				if (value instanceof NumberValue) {
					value = ((NumberValue)value).getNumber();
				}
				if (value != null) { //Ignore null
					if (value instanceof Number) {
						rowText.append(Formater.copyFormat((Number)value));
					} else if (value instanceof Date) {
						rowText.append(Formater.columnDate(value));
					} else if (value instanceof HierarchyColumn) {
						HierarchyColumn hierarchyColumn = (HierarchyColumn) value;
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
		toClipboard(tableText.toString()); //Send it all to the clipboard
	}

	/**
	 * SeparatorList.Separator string value implementation
	 */
	public interface CopySeparator {
		public String getCopyString();
	}

	private static class ListenerClass extends AbstractAction {

		private final JTable jTable;

		public ListenerClass(JTable jTable) {
			this.jTable = jTable;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			copy(jTable);
		}
	}
}
