/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import javax.swing.tree.DefaultMutableTreeNode;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class AssetsToolSettingsPanel extends JSettingsPanel {
		private JCheckBox jEnterFilters;
		private JCheckBox jMarkSelectedRow;
		private JCheckBox jReprocessColors;

	public AssetsToolSettingsPanel(Program program, SettingsDialog settingsDialog, DefaultMutableTreeNode parentNode) {
		super(program, settingsDialog, DialoguesSettings.get().assets(), Images.TOOL_ASSETS.getIcon(), parentNode);

		jEnterFilters = new JCheckBox(DialoguesSettings.get().enterFilter());

		jMarkSelectedRow = new JCheckBox(DialoguesSettings.get().hilightSelected());

		jReprocessColors = new JCheckBox(DialoguesSettings.get().showSellOrReprocessColours());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jEnterFilters)
				.addComponent(jMarkSelectedRow)
				.addComponent(jReprocessColors)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jEnterFilters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jMarkSelectedRow, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jReprocessColors, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	@Override
	public boolean save() {
		boolean update = jMarkSelectedRow.isSelected() != program.getSettings().isHighlightSelectedRows()
						|| jReprocessColors.isSelected() != program.getSettings().isReprocessColors();
		program.getSettings().setFilterOnEnter(jEnterFilters.isSelected());
		program.getSettings().setHighlightSelectedRows(jMarkSelectedRow.isSelected());
		program.getSettings().setReprocessColors(jReprocessColors.isSelected());
		return update;
	}

	@Override
	public void load() {
		jEnterFilters.setSelected(program.getSettings().isFilterOnEnter());
		jMarkSelectedRow.setSelected(program.getSettings().isHighlightSelectedRows());
		jReprocessColors.setSelected(program.getSettings().isReprocessColors());
	}
}

