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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class GeneralSettingsPanel extends JSettingsPanel {

	private final JCheckBox jEnterFilters;
	private final JCheckBox jHighlightSelectedRow;
	private final JCheckBox jStrongColors;


	public GeneralSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().general(),  Images.DIALOG_SETTINGS.getIcon());

		jEnterFilters = new JCheckBox(DialoguesSettings.get().enterFilter());

		jHighlightSelectedRow = new JCheckBox(DialoguesSettings.get().highlightSelectedRow());

		jStrongColors = new JCheckBox(DialoguesSettings.get().strongColors());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jEnterFilters)
				.addComponent(jHighlightSelectedRow)
				.addComponent(jStrongColors)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jEnterFilters, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jHighlightSelectedRow, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jStrongColors, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	@Override
	public boolean save() {
		boolean update = jHighlightSelectedRow.isSelected() != Settings.get().isHighlightSelectedRows() || Settings.get().isStrongColors() != jStrongColors.isSelected();
		Settings.get().setFilterOnEnter(jEnterFilters.isSelected());
		Settings.get().setHighlightSelectedRows(jHighlightSelectedRow.isSelected());
		Settings.get().setStrongColors(jStrongColors.isSelected());
		return update;
	}

	@Override
	public void load() {
		jEnterFilters.setSelected(Settings.get().isFilterOnEnter());
		jHighlightSelectedRow.setSelected(Settings.get().isHighlightSelectedRows());
		jStrongColors.setSelected(Settings.get().isStrongColors());
	}
}
