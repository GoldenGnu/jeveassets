/*
 * Copyright 2009, Niklas Kyster Rasmussen
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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;


public class JCopyPopup implements MouseListener, ActionListener, ClipboardOwner {

	public static final String ACTION_CUT = "ACTION_CUT";
	public static final String ACTION_COPY = "ACTION_COPY";
	public static final String ACTION_PASTE = "ACTION_PASTE";

	private JTextComponent component;
	private JPopupMenu jPopupMenu;
	private Clipboard clipboard;

	public static void install(JTextComponent component){
		new JCopyPopup(component);
	}

	private JCopyPopup(JTextComponent component) {
		this.component = component;
		component.addMouseListener(this);
		clipboard = component.getToolkit().getSystemClipboard();
	}
	
	private void showPopupMenu(MouseEvent e){
		if (!component.hasFocus()) component.requestFocus();

		jPopupMenu = new JPopupMenu();
		JMenuItem jMenuItem;

		String s = component.getSelectedText();
		boolean canCopy = true;
		if (s == null){
			canCopy = false;
		} else if (s.equals("")){
			canCopy = false;
		}

		if (component.isEditable()){
			jMenuItem = new JMenuItem("Cut");
			jMenuItem.setIcon(  ImageGetter.getIcon("cut.png") );
			jMenuItem.setActionCommand(ACTION_CUT);
			jMenuItem.addActionListener(this);
			jMenuItem.setEnabled(canCopy);
			jPopupMenu.add(jMenuItem);
		}

		jMenuItem = new JMenuItem("Copy");
		jMenuItem.setIcon(  ImageGetter.getIcon("page_copy.png") );
		jMenuItem.setActionCommand(ACTION_COPY);
		jMenuItem.addActionListener(this);
		jMenuItem.setEnabled(canCopy);
		jPopupMenu.add(jMenuItem);

		if (component.isEditable()){
			jMenuItem = new JMenuItem("Paste");
			jMenuItem.setIcon(  ImageGetter.getIcon("page_paste.png") );
			jMenuItem.setActionCommand(ACTION_PASTE);
			jMenuItem.addActionListener(this);
			jPopupMenu.add(jMenuItem);
		}
		
		jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isPopupTrigger()){
			showPopupMenu(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()){
			showPopupMenu(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()){
			showPopupMenu(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_CUT.equals(e.getActionCommand())){
			SecurityManager sm = System.getSecurityManager();
			if (sm != null) {
				try {
					sm.checkSystemClipboardAccess();
				} catch (Exception ex) {
					return;
				}
			}

			String s = component.getSelectedText();
			if (s == null) return;
			if (s.equals("")) return;
			String text = component.getText();
			String before = text.substring(0, component.getSelectionStart());
			String after = text.substring(component.getSelectionEnd(), text.length());
			component.setText(before+after);
			StringSelection st = new StringSelection(s);
			clipboard.setContents(st, this);
		}


		if (ACTION_COPY.equals(e.getActionCommand())){
			SecurityManager sm = System.getSecurityManager();
			if (sm != null) {
				try {
					sm.checkSystemClipboardAccess();
				} catch (Exception ex) {
					return;
				}
			}
			String s = component.getSelectedText();
			if (s == null) return;
			if (s.equals("")) return;
			StringSelection st = new StringSelection(s);
			clipboard.setContents(st, this);
		}
		if (ACTION_PASTE.equals(e.getActionCommand())){
			SecurityManager sm = System.getSecurityManager();
			if (sm != null) {
				try {
					sm.checkSystemClipboardAccess();
				} catch (Exception ex) {
					return;
				}
			}
			Transferable transferable = clipboard.getContents(this);
			try {
				String s = (String) transferable.getTransferData(DataFlavor.stringFlavor);
				String text = component.getText();
				String before = text.substring(0, component.getSelectionStart());
				String after = text.substring(component.getSelectionEnd(), text.length());
				component.setText(before+s+after);
				int caretPosition = before.length()+s.length();
				if (caretPosition <= component.getText().length()){
					component.setCaretPosition(before.length()+s.length());
				}
			} catch (UnsupportedFlavorException ex) {
				return;
			} catch (IOException ex) {
				return;
			}
		}
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {

	}
}
