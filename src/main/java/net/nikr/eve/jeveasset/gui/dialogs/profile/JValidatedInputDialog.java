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

package net.nikr.eve.jeveasset.gui.dialogs.profile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.DialoguesProfiles;


public class JValidatedInputDialog extends JDialogCentered implements ActionListener {

	private final static String ACTION_OK = "ACTION_OK";
	private final static String ACTION_CANCEL = "ACTION_CANCEL";
	
	private JTextArea jMessage;
	private JTextField jName;
	private JButton jOK;
	private JButton jCancel;

	private boolean failed = false;

	public JValidatedInputDialog(Program program, JDialogCentered jDialogCentered) {
		super(program, "", jDialogCentered.getDialog());

		jMessage = new JTextArea();
		jMessage.setFocusable(false);
		jMessage.setEditable(false);
		jMessage.setBackground(getDialog().getBackground());
		jMessage.setFont(getDialog().getFont());

		jName = new JTextField();

		jOK = new JButton(DialoguesProfiles.get().ok());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);

		jCancel = new JButton(DialoguesProfiles.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jMessage, 250, 250, 250)
				.addComponent(jName, 250, 250, 250)
				.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jMessage)
				.addComponent(jName, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public String show(String title, String message, String defaultValue, int restrictions){
		if (title != null){
			this.getDialog().setTitle(title);
		} else {
			this.getDialog().setTitle("");
		}
		if (message != null){
			jMessage.setText(message);
		} else {
			jMessage.setText("");
		}
		switch (restrictions){
			case WORDS_ONLY:
				jName.setDocument( DocumentFactory.getWordPlainDocument() );
				break;
			case INTEGERS_ONLY:
				jName.setDocument( DocumentFactory.getIntegerPlainDocument() );
				break;
			case NUMBERS_ONLY:
				jName.setDocument( DocumentFactory.getDoublePlainDocument() );
				break;
			case NO_RESTRICTIONS:
			default:
				jName.setDocument( new PlainDocument() );
		}
		if (defaultValue != null){
			jName.setText(defaultValue);
		} else {
			jName.setText("");
		}
		jName.selectAll();
		this.setVisible(true);
		if (failed) return null;
		return jName.getText();
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jName;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {
		
	}

	@Override
	protected void save() {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())){
			failed = false;
			this.setVisible(false);
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			failed = true;
			this.setVisible(false);
		}
	}
}
