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
import net.nikr.eve.jeveasset.gui.shared.components.JLabelMultiline;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class JournalToolSettingsPanel extends JSettingsPanel {

	private final JCheckBox jSaveHistory;

	public JournalToolSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().journal(), Images.TOOL_JOURNAL.getIcon());

		jSaveHistory = new JCheckBox(DialoguesSettings.get().journalSaveHistory());

		JLabelMultiline jSaveHistoryWarning = new JLabelMultiline(DialoguesSettings.get().saveHistoryWarning(), 2);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jSaveHistory)
				.addComponent(jSaveHistoryWarning, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jSaveHistory, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSaveHistoryWarning, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		);
	}
		
	@Override
	public boolean save() {
		Settings.get().setJournalHistory(jSaveHistory.isSelected());
		return false;
	}

	@Override
	public void load() {
		jSaveHistory.setSelected(Settings.get().isJournalHistory());
	}
	
}
