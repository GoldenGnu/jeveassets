/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JContainerDialog extends JDialogCentered {

	private enum ContainerDialogAction {
		OK, CANCEL
	}

	private final JComboBox<MyAsset> jContainers;
	private final JButton jOK;
	private MyAsset selected = null;

	public JContainerDialog(Program program) {
		super(program, GuiShared.get().containerTitle());

		ListenerClass listenerClass = new ListenerClass();

		JLabel jText = new JLabel(GuiShared.get().containerText());

		jContainers = new JComboBox<MyAsset>();

		jOK = new JButton(GuiShared.get().ok());
		jOK.setActionCommand(ContainerDialogAction.OK.name());
		jOK.addActionListener(listenerClass);

		JButton jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(ContainerDialogAction.CANCEL.name());
		jCancel.addActionListener(listenerClass);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jText)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jContainers, 220, 220, 220)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jText, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jContainers, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	public MyAsset showDialog(MyAsset asset) {
		if (asset.getParents().size() == 1) {
			return asset.getParents().get(0);
		}
		selected = null;
		jContainers.setModel( new ListComboBoxModel<MyAsset>(asset.getParents()));
		setVisible(true);
		return selected;
		
	}
	

	@Override
	protected JComponent getDefaultFocus() {
		return jContainers;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void save() {
		selected = (MyAsset) jContainers.getSelectedItem();
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
