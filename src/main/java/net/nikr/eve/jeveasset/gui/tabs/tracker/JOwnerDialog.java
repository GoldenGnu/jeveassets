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

package net.nikr.eve.jeveasset.gui.tabs.tracker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;


public class JOwnerDialog extends JDialogCentered {

	private enum OwnerSelectAction {
		OK,
		CANCEL
	}

	private final JComboBox jOwners;
	private final JButton jOK;
	private final JButton jCancel;
	private final ListenerClass listener;

	private String owner;

	public JOwnerDialog(Program program) {
		super(program, "Select Owner", Images.TOOL_TRACKER.getImage());

		listener = new ListenerClass();

		jOwners = new JComboBox();

		jOK = new JButton("OK");
		jOK.setActionCommand(OwnerSelectAction.OK.name());
		jOK.addActionListener(listener);

		jCancel = new JButton("Cancel");
		jCancel.setActionCommand(OwnerSelectAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jOwners)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jOwners, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public String show(Object[] owners) {
		jOwners.setModel(new DefaultComboBoxModel(owners));
		owner = null;
		setVisible(true);
		return owner;
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOwners;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		owner = (String) jOwners.getSelectedItem();
		setVisible(false);
	}

	private class ListenerClass implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (OwnerSelectAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (OwnerSelectAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}
	}
	
}
