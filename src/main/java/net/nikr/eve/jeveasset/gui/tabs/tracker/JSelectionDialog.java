/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
import java.util.Collection;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.i18n.TabsTracker;


public class JSelectionDialog extends JDialogCentered {

	private enum OwnerSelectAction {
		OK,
		CANCEL
	}

	private final JComboBox<String> jOwners;
	private final JButton jOK;
	private final JButton jCancel;
	private final ListenerClass listener;

	private String returnValue;

	public JSelectionDialog(Program program) {
		super(program, "", Images.TOOL_TRACKER.getImage());

		listener = new ListenerClass();

		jOwners = new JComboBox<String>();

		jOK = new JButton(TabsTracker.get().ok());
		jOK.setActionCommand(OwnerSelectAction.OK.name());
		jOK.addActionListener(listener);

		jCancel = new JButton(TabsTracker.get().cancel());
		jCancel.setActionCommand(OwnerSelectAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jOwners)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jOwners, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	public String showOwner(Collection<String> owners) {
		getDialog().setTitle(TabsTracker.get().selectOwner());
		return show(owners);
	}

	public String showDivision(Collection<String> divisions) {
		getDialog().setTitle(TabsTracker.get().selectDivision());
		return show(divisions);
	}

	public String showLocation(Collection<String> locations) {
		getDialog().setTitle(TabsTracker.get().selectLocation());
		return show(locations);
	}

	public String showFlag(Collection<String> flags) {
		getDialog().setTitle(TabsTracker.get().selectFlag());
		return show(flags);
	}

	private String show(Collection<String> data) {
		jOwners.setModel(new ListComboBoxModel<String>(data));
		returnValue = null;
		setVisible(true);
		return returnValue;
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
		returnValue = (String) jOwners.getSelectedItem();
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
