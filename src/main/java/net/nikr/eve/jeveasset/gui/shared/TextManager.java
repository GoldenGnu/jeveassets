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

import com.sun.jna.Platform;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.CompoundUndoManager;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public final class TextManager {

	private enum CopyPopupAction {
		CUT, COPY, PASTE
	}

	private final JTextComponent component;
	private final JPopupMenu jPopupMenu;
	private final JMenuItem jCut;
	private final JMenuItem jCopy;
	private final JMenuItem jPaste;
	private final JMenuItem jUndo;
	private final JMenuItem jRedo;
	
	public static void installAll(final Container container) {
		for (Component component : container.getComponents()) {
			if (component instanceof Container) {
				installAll((Container) component);
			}
			if (component instanceof JTextComponent) {
				installTextComponent((JTextComponent) component);
			}
		}
	}

	public static void installTextComponent(final JTextComponent component) {
		//Make sure this component does not already have a UndoManager
		Document document = component.getDocument();
		if (document instanceof AbstractDocument) {
			AbstractDocument abstractDocument = (AbstractDocument) document;
			for (UndoableEditListener editListener : abstractDocument.getUndoableEditListeners()) {
				if (editListener.getClass().equals(CompoundUndoManager.class)) {
					CompoundUndoManager undoManager = (CompoundUndoManager) editListener;
					undoManager.reset();
					return; //already installed
				}
			}
		}
		new TextManager(component);
	}

	private TextManager(final JTextComponent component) {
		this.component = component;

		ListenerClass listener = new ListenerClass();

		component.addMouseListener(listener);

		jPopupMenu = new JPopupMenu();

		jCut = new JMenuItem(GuiShared.get().cut());
		jCut.setIcon(Images.EDIT_CUT.getIcon());
		if (Platform.isMac()) {
			jCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.META_DOWN_MASK));
		} else {
			jCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		}
		jCut.setActionCommand(CopyPopupAction.CUT.name());
		jCut.addActionListener(listener);

		jCopy = new JMenuItem(GuiShared.get().copy());
		if (Platform.isMac()) {
			jCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_DOWN_MASK));
		} else {
			jCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		}
		jCopy.setIcon(Images.EDIT_COPY.getIcon());
		jCopy.setActionCommand(CopyPopupAction.COPY.name());
		jCopy.addActionListener(listener);

		jPaste = new JMenuItem(GuiShared.get().paste());
		jPaste.setIcon(Images.EDIT_PASTE.getIcon());
		if (Platform.isMac()) {
			jPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_DOWN_MASK));
		} else {
			jPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		}
		jPaste.setActionCommand(CopyPopupAction.PASTE.name());
		jPaste.addActionListener(listener);

		CompoundUndoManager undoManager = new CompoundUndoManager(component);

		jUndo = new JMenuItem(undoManager.getUndoAction());
		jUndo.setIcon(Images.EDIT_UNDO.getIcon());

		jRedo = new JMenuItem(undoManager.getRedoAction());
		jRedo.setIcon(Images.EDIT_REDO.getIcon());
	}

	private void showPopupMenu(final MouseEvent e) {
		if (!component.isFocusable()) { //Don't show anything for unfocusable components
			return;
		}

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
			jPopupMenu.addSeparator();
			jPopupMenu.add(jUndo);
			jPopupMenu.add(jRedo);
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
				CopyHandler.cut(component);
			}
			if (CopyPopupAction.COPY.name().equals(e.getActionCommand())) {
				CopyHandler.toClipboard(component.getSelectedText());
			}
			if (CopyPopupAction.PASTE.name().equals(e.getActionCommand())) {
				CopyHandler.paste(component);
			}
		}

	}
}
