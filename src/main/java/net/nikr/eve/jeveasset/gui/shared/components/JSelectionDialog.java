/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JSelectionDialog<T> extends JDialogCentered {

	private enum ContainerDialogAction {
		OK, CANCEL
	}

	private final JComboBox<T> jLocations;
	private final JButton jOK;
	private T selected = null;

	public JSelectionDialog(Program program) {
		super(program, "");

		ListenerClass listenerClass = new ListenerClass();

		jLocations = new JComboBox<T>();

		jOK = new JButton(GuiShared.get().ok());
		jOK.setActionCommand(ContainerDialogAction.OK.name());
		jOK.addActionListener(listenerClass);

		JButton jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(ContainerDialogAction.CANCEL.name());
		jCancel.addActionListener(listenerClass);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jLocations, 220, 220, 220)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jLocations, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}
	
	public T show(String title, Collection<T> data) {
		if (data.size() == 1) {
			return data.iterator().next();
		}
		getDialog().setTitle(title);
		selected = null;
		jLocations.setModel(new ListComboBoxModel<T>(data));
		setVisible(true);
		return selected;
		
	}
	

	@Override
	protected JComponent getDefaultFocus() {
		return jLocations;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void save() {
		selected = jLocations.getItemAt(jLocations.getSelectedIndex());
		setVisible(false);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ContainerDialogAction.OK.name().equals(e.getActionCommand())) {
				save();
			}
			if (ContainerDialogAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}
	}
	
}
