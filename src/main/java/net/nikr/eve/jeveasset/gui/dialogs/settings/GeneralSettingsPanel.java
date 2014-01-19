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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class GeneralSettingsPanel extends JSettingsPanel {

	private JCheckBox jStable;
	private JCheckBox jDev;

	private JCheckBox jEnterFilters;
	private JCheckBox jHighlightSelectedRow;


	public GeneralSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().general(),  Images.DIALOG_SETTINGS.getIcon());

		jStable = new JCheckBox(DialoguesSettings.get().searchForNewVersion(Program.PROGRAM_NAME));

		jDev = new JCheckBox(DialoguesSettings.get().searchForNewVersionBeta());

		jEnterFilters = new JCheckBox(DialoguesSettings.get().enterFilter());

		jHighlightSelectedRow = new JCheckBox(DialoguesSettings.get().highlightSelectedRow());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jStable)
				.addGroup(layout.createSequentialGroup()
					.addGap(20)
					.addComponent(jDev)
				)
				.addComponent(jEnterFilters)
				.addComponent(jHighlightSelectedRow)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jStable, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jDev, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGap(20)
				.addComponent(jEnterFilters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jHighlightSelectedRow, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	@Override
	public boolean save() {
		Settings.get().setAutoUpdate(jStable.isSelected());
		Settings.get().setUpdateDev(jDev.isSelected());
		boolean update = jHighlightSelectedRow.isSelected() != Settings.get().isHighlightSelectedRows();
		Settings.get().setFilterOnEnter(jEnterFilters.isSelected());
		Settings.get().setHighlightSelectedRows(jHighlightSelectedRow.isSelected());
		return update;
	}

	@Override
	public void load() {
		jStable.setSelected(Settings.get().isAutoUpdate());
		jDev.setSelected(Settings.get().isUpdateDev());
		jEnterFilters.setSelected(Settings.get().isFilterOnEnter());
		jHighlightSelectedRow.setSelected(Settings.get().isHighlightSelectedRows());
	}
}
