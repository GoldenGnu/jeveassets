/*
 * Copyright 2009-2015 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.dialogs.bugs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import net.nikr.eve.jeveasset.NikrUncaughtExceptionHandler;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.DialoguesBugs;

public class BugsDialog extends JDialogCentered {

	private enum BugsAction {
		SEND,
		CANCEL
	}

	private final JButton jSend;
	private final JButton jCancel;
	private final JTextArea jBug;
	private final ListenerClass listener;

	public BugsDialog(Program program) {
		super(program, DialoguesBugs.get().toolTitle(), Images.MISC_DEBUG.getImage());

		listener = new ListenerClass();

		JTextArea jHelp = new JTextArea(DialoguesBugs.get().help());
		jHelp.setEditable(false);
		jHelp.setFocusable(false);
		jHelp.setOpaque(false);
		jHelp.setFont(jPanel.getFont());
		jHelp.setBorder(null);

		jBug = new JTextArea();
		jBug.setFont(jPanel.getFont());
		jBug.setTabSize(4);
		jBug.setLineWrap(true);
		jBug.setWrapStyleWord(true);
		JScrollPane jBugScroll = new JScrollPane(jBug);

		jSend = new JButton(DialoguesBugs.get().send());
		jSend.setActionCommand(BugsAction.SEND.name());
		jSend.addActionListener(listener);

		jCancel = new JButton(DialoguesBugs.get().cancel());
		jCancel.setActionCommand(BugsAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jHelp, GroupLayout.Alignment.LEADING)
				.addComponent(jBugScroll, 300, 300, 300)
				.addGroup(layout.createSequentialGroup()
						.addComponent(jSend, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(jHelp)
				.addComponent(jBugScroll, 150, 150, 150)
				.addGroup(layout.createParallelGroup()
						.addComponent(jSend, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jBug;
	}

	@Override
	protected JButton getDefaultButton() {
		return jSend;
	}

	@Override
	protected void windowShown() {
		jBug.setText("");
	}

	@Override
	protected void save() {
		setEnabled(false);
		final String text = jBug.getText();
		if (text.trim().isEmpty()) {
			JOptionPane.showMessageDialog(getDialog(), DialoguesBugs.get().msgEmpty(),  DialoguesBugs.get().msgTitle(), JOptionPane.PLAIN_MESSAGE);
		} else {
			SendBug sendBug = new SendBug(text);
			sendBug.execute();
		}
	}

	public void setEnabled(boolean enabled) {
		jBug.setEnabled(enabled);
		jSend.setEnabled(enabled);
		jCancel.setEnabled(enabled);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (BugsAction.SEND.name().equals(e.getActionCommand())) {
				save();
			} else if (BugsAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}
	}

	private class SendBug extends SwingWorker<Void, Void> {

		private final String text;
		private String result;

		public SendBug(String text) {
			this.text = text;
		}

		@Override
		protected Void doInBackground() throws Exception {
			result = NikrUncaughtExceptionHandler.send(text);
			return null;
		}

		@Override
		protected void done() {
			JOptionPane.showMessageDialog(getDialog(), result, DialoguesBugs.get().msgTitle(), JOptionPane.PLAIN_MESSAGE);
			setEnabled(true);
			setVisible(false);
		}
	}
}
