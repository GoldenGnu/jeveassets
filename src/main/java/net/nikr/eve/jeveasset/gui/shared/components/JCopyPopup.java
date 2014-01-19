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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public final class JCopyPopup {

	//FIXME - - - > JTextComponent: need to use JCopyPopup
	private enum CopyPopupAction {
		CUT, COPY, PASTE
	}

	private JTextComponent component;
	private JPopupMenu jPopupMenu;
	private JMenuItem jCut;
	private JMenuItem jCopy;
	private JMenuItem jPaste;

	private Clipboard clipboard;

	public static void install(final JTextComponent component) {
		JCopyPopup jCopyPopup = new JCopyPopup(component);
	}

	private JCopyPopup(final JTextComponent component) {
		this.component = component;

		ListenerClass listener = new ListenerClass();

		component.addMouseListener(listener);

		jPopupMenu = new JPopupMenu();

		jCut = new JMenuItem(GuiShared.get().cut());
		jCut.setIcon(Images.EDIT_CUT.getIcon());
		jCut.setActionCommand(CopyPopupAction.CUT.name());
		jCut.addActionListener(listener);

		jCopy = new JMenuItem(GuiShared.get().copy());
		jCopy.setIcon(Images.EDIT_COPY.getIcon());
		jCopy.setActionCommand(CopyPopupAction.COPY.name());
		jCopy.addActionListener(listener);

		jPaste = new JMenuItem(GuiShared.get().paste());
		jPaste.setIcon(Images.EDIT_PASTE.getIcon());
		jPaste.setActionCommand(CopyPopupAction.PASTE.name());
		jPaste.addActionListener(listener);

		clipboard = component.getToolkit().getSystemClipboard();
	}

	private void showPopupMenu(final MouseEvent e) {
		if (!component.hasFocus()) {
			component.requestFocus();
		}

		jPopupMenu.removeAll();

		String s = component.getSelectedText();
		boolean canCopy = true;
		if (s == null) {
			canCopy = false;
		} else if (s.length() == 0) {
			canCopy = false;
		}

		if (component.isEditable()) {
			jCut.setEnabled(canCopy);
			jPopupMenu.add(jCut);
		}

		jCopy.setEnabled(canCopy);
		jPopupMenu.add(jCopy);

		if (component.isEditable()) {
			jPopupMenu.add(jPaste);
		}

		jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	private class ListenerClass implements MouseListener, ActionListener {
		@Override
		public void mouseClicked(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopupMenu(e);
			}
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopupMenu(e);
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopupMenu(e);
			}
		}

		@Override
		public void mouseEntered(final MouseEvent e) { }

		@Override
		public void mouseExited(final MouseEvent e) { }

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (CopyPopupAction.CUT.name().equals(e.getActionCommand())) {
				SecurityManager sm = System.getSecurityManager();
				if (sm != null) {
					try {
						sm.checkSystemClipboardAccess();
					} catch (Exception ex) {
						return;
					}
				}

				String s = component.getSelectedText();
				if (s == null) {
					return;
				}
				if (s.length() == 0) {
					return;
				}
				String text = component.getText();
				String before = text.substring(0, component.getSelectionStart());
				String after = text.substring(component.getSelectionEnd(), text.length());
				component.setText(before + after);
				StringSelection st = new StringSelection(s);
				clipboard.setContents(st, null);
			}
			if (CopyPopupAction.COPY.name().equals(e.getActionCommand())) {
				SecurityManager sm = System.getSecurityManager();
				if (sm != null) {
					try {
						sm.checkSystemClipboardAccess();
					} catch (Exception ex) {
						return;
					}
				}
				String s = component.getSelectedText();
				if (s == null) {
					return;
				}
				if (s.length() == 0) {
					return;
				}
				StringSelection st = new StringSelection(s);
				clipboard.setContents(st, null);
			}
			if (CopyPopupAction.PASTE.name().equals(e.getActionCommand())) {
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
					component.setText(before + s + after);
					int caretPosition = before.length() + s.length();
					if (caretPosition <= component.getText().length()) {
						component.setCaretPosition(before.length() + s.length());
					}
				} catch (UnsupportedFlavorException ex) {

				} catch (IOException ex) {

				}
			}
		}
	}
}
