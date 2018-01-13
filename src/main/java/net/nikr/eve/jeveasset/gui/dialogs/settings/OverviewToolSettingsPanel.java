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


public class OverviewToolSettingsPanel extends JSettingsPanel {

	private final JCheckBox jIgnoreSecureContainers;

	public OverviewToolSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().overview(), Images.TOOL_OVERVIEW.getIcon());
		jIgnoreSecureContainers = new JCheckBox(DialoguesSettings.get().ignoreAuditLogContainers());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jIgnoreSecureContainers)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jIgnoreSecureContainers, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	@Override
	public boolean save() {
		boolean update = jIgnoreSecureContainers.isSelected() != Settings.get().isIgnoreSecureContainers();
		Settings.get().setIgnoreSecureContainers(jIgnoreSecureContainers.isSelected());
		return update;
	}

	@Override
	public void load() {
		jIgnoreSecureContainers.setSelected(Settings.get().isIgnoreSecureContainers());
	}

}
