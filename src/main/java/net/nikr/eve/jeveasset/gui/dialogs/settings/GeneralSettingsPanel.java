/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class GeneralSettingsPanel extends JSettingsPanel {

	private JCheckBox jStable;
	private JCheckBox jDev;


	public GeneralSettingsPanel(Program program, SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().general(),  Images.DIALOG_SETTINGS.getIcon());

		jStable = new JCheckBox(DialoguesSettings.get().searchForNewVersion(Program.PROGRAM_NAME));

		jDev = new JCheckBox(DialoguesSettings.get().searchForNewVersionBeta());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jStable)
				.addGroup(layout.createSequentialGroup()
					.addGap(20)
					.addComponent(jDev)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jStable, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jDev, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	@Override
	public boolean save() {
		program.getSettings().setAutoUpdate(jStable.isSelected());
		program.getSettings().setUpdateDev(jDev.isSelected());
		return false;
	}

	@Override
	public void load() {
		jStable.setSelected(program.getSettings().isAutoUpdate());
		jDev.setSelected(program.getSettings().isUpdateDev());
	}
}
