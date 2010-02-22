/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import net.nikr.eve.jeveasset.Program;


public class JMessageDialog extends JDialogCentered implements ActionListener {
	
	private final static String ACTION_OK = "ACTION_OK";

	private JTextArea jMessage;
	private JButton jOK;

	public JMessageDialog(Program program, Window parent) {
		super(program, "", parent);

		jMessage = new JTextArea();
		jMessage.setFocusable(false);
		jMessage.setEditable(false);
		jMessage.setBackground(getDialog().getBackground());
		jMessage.setFont(getDialog().getFont());


		jOK = new JButton("OK");
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.CENTER)
				.addComponent(jMessage)
				.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jMessage)
				.addGap(20)
				.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	public void show(String title, String message){
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
		this.setVisible(true);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {

	}

	@Override
	protected void windowActivated() {

	}

	@Override
	protected void save() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())){
			this.setVisible(false);
		}
	}
}
