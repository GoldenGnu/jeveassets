/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

public class TrackerToolSettingsPanel extends JSettingsPanel {

	private final JCheckBox jUseAssetPriceForSellOrders;

	public TrackerToolSettingsPanel(Program program, SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().tracker(), Images.TOOL_TRACKER.getIcon());

		jUseAssetPriceForSellOrders = new JCheckBox(DialoguesSettings.get().useAssetPriceForSellOrders());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jUseAssetPriceForSellOrders)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
					.addComponent(jUseAssetPriceForSellOrders, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	@Override
	public UpdateType save() {
		Settings.get().setTrackerUseAssetPriceForSellOrders(jUseAssetPriceForSellOrders.isSelected());
		return UpdateType.NONE;
	}

	@Override
	public void load() {
		jUseAssetPriceForSellOrders.setSelected(Settings.get().isTrackerUseAssetPriceForSellOrders());
	}
}
