/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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


public class AssetsToolSettingsPanel extends JSettingsPanel {

	private final JCheckBox jSellOrders;
	private final JCheckBox jBuyOrders;
	private final JCheckBox jSellContracts;
	private final JCheckBox jBuyContracts;
	private final JCheckBox jManufacturing;
	private final JCheckBox jCopying;

	public AssetsToolSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().assets(), Images.TOOL_ASSETS.getIcon());

		jSellOrders = new JCheckBox(DialoguesSettings.get().includeSellOrders());
		jBuyOrders = new JCheckBox(DialoguesSettings.get().includeBuyOrders());
		jSellContracts = new JCheckBox(DialoguesSettings.get().includeSellContracts());
		jBuyContracts = new JCheckBox(DialoguesSettings.get().includeBuyContracts());
		jManufacturing = new JCheckBox(DialoguesSettings.get().includeManufacturing());
		jCopying = new JCheckBox(DialoguesSettings.get().includeCopying());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jSellOrders)
				.addComponent(jBuyOrders)
				.addComponent(jSellContracts)
				.addComponent(jBuyContracts)
				.addComponent(jManufacturing)
				.addComponent(jCopying)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jSellOrders, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jBuyOrders, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSellContracts, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jBuyContracts, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jManufacturing, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jCopying, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	@Override
	public UpdateType save() {
		boolean update = jSellOrders.isSelected() != Settings.get().isIncludeSellOrders()
						|| jBuyOrders.isSelected() != Settings.get().isIncludeBuyOrders()
						|| jSellContracts.isSelected() != Settings.get().isIncludeSellContracts()
						|| jBuyContracts.isSelected() != Settings.get().isIncludeBuyContracts()
						|| jManufacturing.isSelected() != Settings.get().isIncludeManufacturing()
						|| jCopying.isSelected() != Settings.get().isIncludeCopying()
						;
		Settings.get().setIncludeSellOrders(jSellOrders.isSelected());
		Settings.get().setIncludeBuyOrders(jBuyOrders.isSelected());
		Settings.get().setIncludeSellContracts(jSellContracts.isSelected());
		Settings.get().setIncludeBuyContracts(jBuyContracts.isSelected());
		Settings.get().setIncludeManufacturing(jManufacturing.isSelected());
		Settings.get().setIncludeCopying(jCopying.isSelected());
		return update ? UpdateType.FULL_UPDATE : UpdateType.NONE;
	}

	@Override
	public void load() {
		jSellOrders.setSelected(Settings.get().isIncludeSellOrders());
		jBuyOrders.setSelected(Settings.get().isIncludeBuyOrders());
		jSellContracts.setSelected(Settings.get().isIncludeSellContracts());
		jBuyContracts.setSelected(Settings.get().isIncludeBuyContracts());
		jManufacturing.setSelected(Settings.get().isIncludeManufacturing());
		jCopying.setSelected(Settings.get().isIncludeCopying());
	}
}

