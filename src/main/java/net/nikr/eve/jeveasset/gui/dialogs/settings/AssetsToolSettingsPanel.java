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
import javax.swing.JLabel;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class AssetsToolSettingsPanel extends JSettingsPanel {

	private final JCheckBox jReprocessColors;
	private final JCheckBox jSellOrders;
	private final JCheckBox jBuyOrders;
	private final JCheckBox jSellContracts;
	private final JCheckBox jBuyContracts;
	private final JTextField jMaxOrderAge;

	public AssetsToolSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().assets(), Images.TOOL_ASSETS.getIcon());

		jReprocessColors = new JCheckBox(DialoguesSettings.get().showSellOrReprocessColours());
		jSellOrders = new JCheckBox(DialoguesSettings.get().includeSellOrders());
		jBuyOrders = new JCheckBox(DialoguesSettings.get().includeBuyOrders());
		jSellContracts = new JCheckBox(DialoguesSettings.get().includeSellContracts());
		jBuyContracts = new JCheckBox(DialoguesSettings.get().includeBuyContracts());
		jMaxOrderAge = new JIntegerField("0", DocumentFactory.ValueFlag.POSITIVE_AND_ZERO);
		JLabel jMaxOrderAgeLabel = new JLabel(DialoguesSettings.get().maximumPurchaseAge());
		JLabel jDaysLabel = new JLabel(DialoguesSettings.get().days());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jReprocessColors)
				.addComponent(jSellOrders)
				.addComponent(jBuyOrders)
				.addComponent(jSellContracts)
				.addComponent(jBuyContracts)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jMaxOrderAgeLabel)
					.addComponent(jMaxOrderAge, 75, 75, 75)
					.addComponent(jDaysLabel)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jReprocessColors, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jSellOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jBuyOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jSellContracts, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jBuyContracts, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGap(20)
				.addGroup(layout.createParallelGroup()
					.addComponent(jMaxOrderAgeLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jMaxOrderAge, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDaysLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	@Override
	public boolean save() {
		int maximumPurchaseAge;
		try {
			maximumPurchaseAge = Integer.valueOf(jMaxOrderAge.getText());
		} catch (NumberFormatException ex) {
			maximumPurchaseAge = 0;
		}
		boolean update = jReprocessColors.isSelected() != Settings.get().isReprocessColors()
						|| jSellOrders.isSelected() != Settings.get().isIncludeSellOrders()
						|| jBuyOrders.isSelected() != Settings.get().isIncludeBuyOrders()
						|| jSellContracts.isSelected() != Settings.get().isIncludeSellContracts()
						|| jBuyContracts.isSelected() != Settings.get().isIncludeBuyContracts()
						|| maximumPurchaseAge != Settings.get().getMaximumPurchaseAge()
						;
		Settings.get().setReprocessColors(jReprocessColors.isSelected());
		Settings.get().setIncludeSellOrders(jSellOrders.isSelected());
		Settings.get().setIncludeBuyOrders(jBuyOrders.isSelected());
		Settings.get().setIncludeSellContracts(jSellContracts.isSelected());
		Settings.get().setIncludeBuyContracts(jBuyContracts.isSelected());
		Settings.get().setMaximumPurchaseAge(maximumPurchaseAge);
		return update;
	}

	@Override
	public void load() {
		jReprocessColors.setSelected(Settings.get().isReprocessColors());
		jSellOrders.setSelected(Settings.get().isIncludeSellOrders());
		jBuyOrders.setSelected(Settings.get().isIncludeBuyOrders());
		jSellContracts.setSelected(Settings.get().isIncludeSellContracts());
		jBuyContracts.setSelected(Settings.get().isIncludeBuyContracts());
		jMaxOrderAge.setText(String.valueOf(Settings.get().getMaximumPurchaseAge()));
	}
}

