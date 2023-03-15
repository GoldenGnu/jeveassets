/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

import java.awt.Component;
import java.awt.HeadlessException;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.UNINITIALIZED_VALUE;
import static javax.swing.JOptionPane.getRootFrame;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JOptionInput {

	private static final Logger LOG = LoggerFactory.getLogger(JOptionInput.class);

	public static String showInputDialog(Object message) throws HeadlessException {
		return showInputDialog(null, message);
	}

	public static String showInputDialog(Object message, Object initialSelectionValue) {
		return showInputDialog(null, message, initialSelectionValue);
	}

	public static String showInputDialog(Component parentComponent, Object message) throws HeadlessException {
		return showInputDialog(parentComponent, message, getString("OptionPane.inputDialogTitle", parentComponent), QUESTION_MESSAGE);
	}

	public static String showInputDialog(Component parentComponent, Object message, Object initialSelectionValue) {
		return (String) showInputDialog(parentComponent, message, getString("OptionPane.inputDialogTitle", parentComponent), QUESTION_MESSAGE, null, null, initialSelectionValue);
	}

	public static String showInputDialog(Component parentComponent, Object message, String title, int messageType) {
		return (String) showInputDialog(parentComponent, message, title,
				messageType, null, null, null);
	}

	public static Object showInputDialog(Component parentComponent, Object message, String title, int messageType, Icon icon, Object[] selectionValues, Object initialSelectionValue) {
		try {
			return internalInputDialog(parentComponent, message, title, messageType, icon, selectionValues, initialSelectionValue);
		} catch (Throwable ex) { //Fallback on ANY error
			LOG.error(ex.getMessage(), ex);
			return JOptionPane.showInputDialog(parentComponent, message, title, messageType, icon, selectionValues, initialSelectionValue);
		}
	}

	private static Object internalInputDialog(Component parentComponent, Object message, String title, int messageType, Icon icon, Object[] selectionValues, Object initialSelectionValue) {
		JOptionPane pane = new JOptionPane(message, messageType,
				OK_CANCEL_OPTION, icon,
				null, null);

		pane.setWantsInput(true);
		pane.setSelectionValues(selectionValues);
		pane.setInitialSelectionValue(initialSelectionValue);
		pane.setComponentOrientation(((parentComponent == null)
				? getRootFrame() : parentComponent).getComponentOrientation());

		TextManager.installAll(pane);

		JDialog dialog = pane.createDialog(parentComponent, title);

		pane.selectInitialValue();
		dialog.setVisible(true);
		dialog.dispose();

		Object value = pane.getInputValue();

		if (value == UNINITIALIZED_VALUE) {
			return null;
		}
		return value;
	}

	private static String getString(Object key, Component c) {
		Locale l = (c == null) ? Locale.getDefault() : c.getLocale();
		return UIManager.getString(key, l);
	}

}
