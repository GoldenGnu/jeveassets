/*
 * Copyright 2009
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
package net.nikr.eve.jeveasset.gui.dialogs;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.JSettingsPanel;


public class SettingsDialog extends JDialogCentered implements ActionListener {

	public final static String ACTION_OK = "ACTION_OK";
	public final static String ACTION_CANCEL = "ACTION_CANCEL";

	private JTabbedPane jTabbedPane;
	private JButton jOK;
	private List<JSettingsPanel> jSettingsPanels;

	private boolean tabSelected = false;

	public SettingsDialog(Program program, Image image) {
		super(program, "Settings", image);
		jSettingsPanels = new Vector<JSettingsPanel>();

		jTabbedPane = new JTabbedPane();

		jOK = new JButton("OK");
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);

		JButton jCancel = new JButton("Cancel");
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jTabbedPane)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jTabbedPane)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public void add(JSettingsPanel jSettingsPanel, Icon icon){
		jTabbedPane.addTab(jSettingsPanel.getTitle(), icon, jSettingsPanel.getPanel());
		jSettingsPanels.add(jSettingsPanel);
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
		JComponent jComponent = jSettingsPanels.get(jTabbedPane.getSelectedIndex()).getDefaultFocus();
		if (jComponent != null){
			jComponent.requestFocus();
		}
	}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		for (int a = 0; a < jSettingsPanels.size(); a++){
			jSettingsPanels.get(a).save();
		}
		setVisible(false);
		for (int a = 0; a < jSettingsPanels.size(); a++){
			jSettingsPanels.get(a).closed();
		}
	}

	public void setVisible(int number) {
		jTabbedPane.setSelectedIndex(number);
		tabSelected = true;
		setVisible(true);
	}

	public void setVisible(Component c) {
		jTabbedPane.setSelectedComponent(c);
		tabSelected = true;
		setVisible(true);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			for (int a = 0; a < jSettingsPanels.size(); a++){
				jSettingsPanels.get(a).load();
			}
			if (!tabSelected){
				jTabbedPane.setSelectedIndex(0);
			}
		} else {
			tabSelected = false;
		}
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())){
			save();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			setVisible(false);
		}

	}
}
