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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.PriceHistoryDatabase;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class PriceHistoryToolSettingsPanel extends JSettingsPanel {

	private final JButton jClearBlacklist;

	public PriceHistoryToolSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().priceHistory(), Images.TOOL_PRICE_HISTORY.getIcon());

		jClearBlacklist = new JButton(DialoguesSettings.get().clearBlacklist());
		jClearBlacklist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnValue = JOptionPane.showConfirmDialog(parent, DialoguesSettings.get().clearBlacklistMsg(), DialoguesSettings.get().clearBlacklistTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, Images.LINK_ZKILLBOARD_32.getIcon());
				if (returnValue == JOptionPane.OK_OPTION) {
					PriceHistoryDatabase.clearZBlacklist();
				}
			}
		});

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jClearBlacklist)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jClearBlacklist, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	@Override
	public UpdateType save() {
		return UpdateType.NONE;
	}

	@Override
	public void load() { }

}
