/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.menu;

import ca.odell.glazedlists.SeparatorList;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;

public class JMenuCopy extends JMenuItem {

	public JMenuCopy(final JTable jTable) {
		super(GuiShared.get().copy());
		this.setIcon(Images.EDIT_COPY.getIcon());
		this.addActionListener(new CopyListener(jTable));
	}

	private static void copy(JTable jTable) {
		String tableText = "";
		String separatorText = "";
		int[] selectedRows = jTable.getSelectedRows();
		int[] selectedColumns = jTable.getSelectedColumns();
		for (int i = 0; i < selectedRows.length; i++) {
			String rowText = "";
			for (int b = 0; b < selectedColumns.length; b++) {
				Object value = jTable.getValueAt(selectedRows[i], selectedColumns[b]);
				if (value instanceof SeparatorList.Separator) { //Handle Separator's
					SeparatorList.Separator<?> separator = (SeparatorList.Separator) value;
					Object object = separator.first();
					if (object instanceof CopySeparator) {
						CopySeparator copySeperator = (CopySeparator) object;
						separatorText = copySeperator.getCopyString();
					}
					break;
				}
				if (value != null) { //Ignore null
					if (!rowText.isEmpty()) {
						rowText = rowText + "\t";
					}
					rowText = rowText + value;
				}
			}
			if (!rowText.isEmpty() || (!separatorText.isEmpty() && (i + 1) == selectedRows.length)) {
				tableText = tableText + separatorText + (!rowText.isEmpty() && !separatorText.isEmpty()? "\t" : "")  + rowText + "\r\n";
			}
		}
		copyToClipboard(tableText);
	}

	private static void copyToClipboard(final Object obj) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			try {
				sm.checkSystemClipboardAccess();
			} catch (Exception ex) {
				return;
			}
		}
		Toolkit tk = Toolkit.getDefaultToolkit();
		StringSelection st =
				new StringSelection(String.valueOf(obj));
		Clipboard cp = tk.getSystemClipboard();
		cp.setContents(st, null);
	}

	public interface CopySeparator {
		public String getCopyString();
	}

	public static void installCopyFormatter(JTable jTable) {
		jTable.addKeyListener(new CopyListener(jTable));
	}

	private static class CopyListener extends KeyAdapter implements ActionListener {

		private JTable jTable;

		public CopyListener(JTable jTable) {
			this.jTable = jTable;
		}
		
		@Override
		public void keyReleased(KeyEvent event) {
			if (event.isControlDown()) {
				if (event.getKeyCode() == KeyEvent.VK_C) { // Copy                        
					copy(jTable);
				} else if (event.getKeyCode() == KeyEvent.VK_X) { // Cut 
					copy(jTable);
				}
			}
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			copy(jTable);
		}
	}
}
