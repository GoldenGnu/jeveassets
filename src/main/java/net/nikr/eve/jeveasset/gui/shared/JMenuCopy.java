/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuCopy extends JMenuItem implements ActionListener, ClipboardOwner {

	private static final String ACTION_COPY = "ACTION_COPY";

	private JTable jTable;

	public JMenuCopy(JTable jTable) {
		super(GuiShared.get().copy());
		this.jTable = jTable;
		this.setIcon(Images.EDIT_COPY.getIcon());
		this.setActionCommand(ACTION_COPY);
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_COPY.equals(e.getActionCommand())){
			String s = "";
			int[] selectedRows = jTable.getSelectedRows();
			int[] selectedColumns = jTable.getSelectedColumns();
			for (int a = 0; a < selectedRows.length; a++){
				for (int b = 0; b < selectedColumns.length; b++){
					if (b != 0) s = s + "	";
					s = s + jTable.getValueAt(selectedRows[a], selectedColumns[b]);
				}
				if ( (a + 1) < selectedRows.length ) s = s + "\r\n";
			}
			copyToClipboard(s);
		}
	}


	private void copyToClipboard(Object o){
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
		new StringSelection(String.valueOf(o));
		Clipboard cp = tk.getSystemClipboard();
		cp.setContents(st, this);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}


}
