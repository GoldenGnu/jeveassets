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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class JDialogCentered {

	private static final Logger LOG = LoggerFactory.getLogger(JDialogCentered.class);

	private enum DialogCenteredAction {
		OK, CANCEL
	}

	public static final int NO_RESTRICTIONS = 0;
	public static final int WORDS_ONLY = 1;
	public static final int INTEGERS_ONLY = 2;
	public static final int NUMBERS_ONLY = 3;

	protected Program program;
	protected Window parent;
	protected JPanel jPanel;
	protected GroupLayout layout;

	private JDialog dialog;
	private boolean firstActivating = false;
	private boolean firstFocus = false;

	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected JDialogCentered(final boolean load) { }

	public JDialogCentered(final Program program, final String title) {
		this(program, title, program.getMainWindow().getFrame(), null);
	}
	public JDialogCentered(final Program program, final String title, final Image image) {
		this(program, title, program.getMainWindow().getFrame(), image);
	}
	public JDialogCentered(final Program program, final String title, final Window parent) {
		this(program, title, parent, null);
	}
	public JDialogCentered(final Program program, final String title, final Window parent, final Image image) {
		this.program = program;
		this.parent = parent;

		ListenerClass listener = new ListenerClass();

		dialog = new JDialog(parent, JDialog.DEFAULT_MODALITY_TYPE);
		dialog.setTitle(title);
		dialog.setResizable(false);
		dialog.addWindowListener(listener);
		dialog.addWindowFocusListener(listener);
		if (image != null) {
			dialog.setIconImage(image);
		}

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		dialog.add(jPanel);

		dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), DialogCenteredAction.CANCEL.name());
		dialog.getRootPane().getActionMap().put(DialogCenteredAction.CANCEL.name(), new HideAction(DialogCenteredAction.CANCEL.name()));
		dialog.getRootPane().getActionMap().put(DefaultEditorKit.insertBreakAction, new HideAction(DialogCenteredAction.OK.name()));
	}

	protected abstract JComponent getDefaultFocus();
	protected abstract JButton getDefaultButton();
	protected abstract void windowShown();
	protected abstract void save();

	public JDialog getDialog() {
		return dialog;
	}

	public void setVisible(final boolean b) {
		if (b) {
			LOG.info("Showing: {} Dialog", dialog.getTitle());
			dialog.pack();
			if (dialog.isResizable()) {
				dialog.setMinimumSize(dialog.getSize());
			}

			//Get the parent size
			Dimension screenSize = parent.getSize();

			//Calculate the frame location
			int x = (screenSize.width - dialog.getWidth()) / 2;
			int y = (screenSize.height - dialog.getHeight()) / 2;

			//Set the new frame location
			dialog.setLocation(x, y);
			dialog.setLocationRelativeTo(parent);

			firstActivating = true;
			firstFocus = true;
		} else {
			LOG.info("Hiding: {} Dialog", dialog.getTitle());
		}
		dialog.setVisible(b);
	}

	//Find JTextComponent(s) and overwrite the default enter action
	private void fixTextComponents(final JComponent jComponent) {
		for (int i = 0; i < jComponent.getComponentCount(); i++) {
			Component c = jComponent.getComponent(i);
			if (c instanceof JTextComponent) {
				JTextComponent jTextComponent = (JTextComponent) c;
				if (!jTextComponent.isEditable()) {
					jTextComponent.getActionMap().put(DefaultEditorKit.insertBreakAction, new HideAction(DialogCenteredAction.OK.name()));
				}
			}
			if (c instanceof JComponent) {
				fixTextComponents((JComponent) c);
			}
		}
	}

	private class ListenerClass implements WindowListener, WindowFocusListener {
		@Override
		public void windowOpened(final WindowEvent e) {
			//Set default close button
			if (dialog.getRootPane().getDefaultButton() == null) {
				dialog.getRootPane().setDefaultButton(getDefaultButton());
			}
			//Fix none editable JTextComponent(s)
			fixTextComponents(jPanel);
		}

		@Override
		public void windowClosing(final WindowEvent e) {
			LOG.info("Hiding: {} Dialog (close)", dialog.getTitle());
			setVisible(false);
		}

		@Override
		public void windowClosed(final WindowEvent e)  { }

		@Override
		public void windowIconified(final WindowEvent e) { }

		@Override
		public void windowDeiconified(final WindowEvent e) { }

		@Override
		public void windowActivated(final WindowEvent e) {
			if (firstActivating) {
				firstActivating = false;
				windowShown();
			}
		}

		@Override
		public void windowGainedFocus(final WindowEvent e) {
			//We can not change focus before dialog have focus...
			JComponent defaultFocus = getDefaultFocus();
			if (defaultFocus == null) {
				LOG.warn("No default focus for: {}", dialog.getTitle());
				return;
			}
			if (firstFocus) {
				firstFocus = false;
				if (defaultFocus.isEnabled()) {
					defaultFocus.requestFocusInWindow();
				}
			}
		}

		@Override
		public void windowLostFocus(final WindowEvent e) { }

		@Override
		public void windowDeactivated(final WindowEvent e) { }
	}

	private class HideAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public HideAction(final String actionCommand) {
			this.putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (DialogCenteredAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
			if (DialogCenteredAction.OK.name().equals(e.getActionCommand())) {
				save();
			}

		}
	}
}
