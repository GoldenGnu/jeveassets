/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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


public class ExperimentalSettingsPanel extends JSettingsPanel {

	private final JCheckBox jCellValueCache;

	public ExperimentalSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, "Experimental", Images.JOBS_INVENTION_SUCCESS.getIcon());
		jCellValueCache = new JCheckBox("Filter cell value cache");

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jCellValueCache)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jCellValueCache, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	@Override
	public UpdateType save() {
		Settings.get().setCellValueCache(jCellValueCache.isSelected());
		return UpdateType.NONE;
	}

	@Override
	public void load() {
		jCellValueCache.setSelected(Settings.get().isColumnValueCache());
	}

}
