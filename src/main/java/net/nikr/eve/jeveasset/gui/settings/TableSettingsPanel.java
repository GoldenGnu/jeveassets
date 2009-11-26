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

package net.nikr.eve.jeveasset.gui.settings;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.JSettingsPanel;


public class TableSettingsPanel extends JSettingsPanel {

	private JCheckBox jEnterFilters;
	private JCheckBox jMarkSelectedRow;


	public TableSettingsPanel(Program program, JDialogCentered jDialogCentered) {
		super(program, jDialogCentered.getDialog(), "Table");

		jEnterFilters = new JCheckBox("Only filter when enter is pressed");

		jMarkSelectedRow = new JCheckBox("Highlight selected row(s)");

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jEnterFilters)
				.addComponent(jMarkSelectedRow)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jEnterFilters)
				.addComponent(jMarkSelectedRow)
		);
	}

	@Override
	public void save() {
		program.getSettings().setFilterOnEnter(jEnterFilters.isSelected());
		program.getSettings().setHighlightSelectedRows(jMarkSelectedRow.isSelected());
	}

	@Override
	public void load() {
		jEnterFilters.setSelected(program.getSettings().isFilterOnEnter());
		jMarkSelectedRow.setSelected(program.getSettings().isHighlightSelectedRows());
	}

	@Override
	public void closed() {

	}

	@Override
	public JComponent getDefaultFocus() {
		return null;
	}

}
