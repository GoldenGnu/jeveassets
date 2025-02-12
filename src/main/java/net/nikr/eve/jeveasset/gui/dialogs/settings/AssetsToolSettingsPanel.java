/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class AssetsToolSettingsPanel extends JSettingsPanel {

	private static enum ContractsOwnerFormat {
		ISSUER_CHARACTER() {
			@Override
			protected String getName() {
				return DialoguesSettings.get().contractAssetsCharacter();
			}
		},
		ISSUER_CORPORATION() {
			@Override
			protected String getName() {
				return DialoguesSettings.get().contractAssetsCorporation();
			}
		},
		ISSUER_BOTH() {
			@Override
			protected String getName() {
				return DialoguesSettings.get().contractAssetsBoth();
			}
		};

		@Override
		public String toString() {
			return getName();
		}

		protected abstract String getName();
	}

	private final JCheckBox jSellOrders;
	private final JCheckBox jBuyOrders;
	private final JCheckBox jSellContracts;
	private final JCheckBox jBuyContracts;
	private final JComboBox<ContractsOwnerFormat> jContractsOwner;
	private final JCheckBox jManufacturing;
	private final JCheckBox jCopying;
	private final JCheckBox jContainerItemID;

	public AssetsToolSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().assets(), Images.TOOL_ASSETS.getIcon());

		jSellOrders = new JCheckBox(DialoguesSettings.get().includeSellOrders());
		jBuyOrders = new JCheckBox(DialoguesSettings.get().includeBuyOrders());
		jSellContracts = new JCheckBox(DialoguesSettings.get().includeSellContracts());
		jBuyContracts = new JCheckBox(DialoguesSettings.get().includeBuyContracts());
		JLabel jContractsOwnerLabel = new JLabel(DialoguesSettings.get().contractAssetsLabel());
		JLabel jContractsOwnerWarn = new JLabel(DialoguesSettings.get().contractAssetsLabelWarn());
		jContractsOwner = new JComboBox<>(ContractsOwnerFormat.values());
		jContractsOwner.setPrototypeDisplayValue(ContractsOwnerFormat.ISSUER_BOTH);
		jContractsOwner.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jContractsOwnerWarn.setVisible(jContractsOwner.getItemAt(jContractsOwner.getSelectedIndex()) == ContractsOwnerFormat.ISSUER_BOTH);
			}
		});
		jManufacturing = new JCheckBox(DialoguesSettings.get().includeManufacturing());
		jCopying = new JCheckBox(DialoguesSettings.get().includeCopying());
		jContainerItemID = new JCheckBox(DialoguesSettings.get().showContainerItemID());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jSellOrders)
						.addComponent(jBuyOrders)
						.addComponent(jSellContracts)
						.addComponent(jBuyContracts)
						.addComponent(jManufacturing)
						.addComponent(jCopying)
					)
					.addGap(0, 0, 100)
					.addGroup(layout.createParallelGroup()
						.addComponent(jContractsOwnerLabel)
						.addComponent(jContractsOwner, 200, 200, 200)
						.addComponent(jContractsOwnerWarn)
					)
				)
				.addComponent(jContainerItemID)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jSellOrders, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jBuyOrders, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGap(0)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(jSellContracts, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jContractsOwnerLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						)
						.addGap(0)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(jBuyContracts, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jContractsOwner, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						)
						.addGap(0)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(jContractsOwnerWarn, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jManufacturing, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						)
					)
				)
				.addGap(0)
				.addComponent(jCopying, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jContainerItemID, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	@Override
	public UpdateType save() {
		ContractsOwnerFormat contractsOwnerFormat = jContractsOwner.getItemAt(jContractsOwner.getSelectedIndex());
		
		boolean fullUpdate = jSellOrders.isSelected() != Settings.get().isIncludeSellOrders()
						|| jBuyOrders.isSelected() != Settings.get().isIncludeBuyOrders()
						|| jSellContracts.isSelected() != Settings.get().isIncludeSellContracts()
						|| jBuyContracts.isSelected() != Settings.get().isIncludeBuyContracts()
						|| jManufacturing.isSelected() != Settings.get().isIncludeManufacturing()
						|| jCopying.isSelected() != Settings.get().isIncludeCopying()
						|| Settings.get().isIncludeCopying()
						|| (contractsOwnerFormat != ContractsOwnerFormat.ISSUER_CHARACTER && !Settings.get().isAssetsContractsOwnerCorporation() && !Settings.get().isAssetsContractsOwnerBoth())
						|| (contractsOwnerFormat != ContractsOwnerFormat.ISSUER_CORPORATION && Settings.get().isAssetsContractsOwnerCorporation())
						|| (contractsOwnerFormat != ContractsOwnerFormat.ISSUER_BOTH && Settings.get().isAssetsContractsOwnerBoth())
						
						;
		boolean updateContainers = jContainerItemID.isSelected() != Settings.get().isContainersShowItemID();
		Settings.get().setIncludeSellOrders(jSellOrders.isSelected());
		Settings.get().setIncludeBuyOrders(jBuyOrders.isSelected());
		Settings.get().setIncludeSellContracts(jSellContracts.isSelected());
		Settings.get().setIncludeBuyContracts(jBuyContracts.isSelected());
		Settings.get().setIncludeManufacturing(jManufacturing.isSelected());
		Settings.get().setIncludeCopying(jCopying.isSelected());
		Settings.get().setContainersShowItemID(jContainerItemID.isSelected());
		Settings.get().setAssetsContractsOwnerCorporation(contractsOwnerFormat == ContractsOwnerFormat.ISSUER_CORPORATION);
		Settings.get().setAssetsContractsOwnerBoth(contractsOwnerFormat == ContractsOwnerFormat.ISSUER_BOTH);
		if (fullUpdate) {
			return UpdateType.FULL_UPDATE;
		} else if (updateContainers) {
			return UpdateType.UPDATE_ASSET_TABLES;
		} else {
			return UpdateType.NONE;
		}
	}

	@Override
	public void load() {
		jSellOrders.setSelected(Settings.get().isIncludeSellOrders());
		jBuyOrders.setSelected(Settings.get().isIncludeBuyOrders());
		jSellContracts.setSelected(Settings.get().isIncludeSellContracts());
		jBuyContracts.setSelected(Settings.get().isIncludeBuyContracts());
		jManufacturing.setSelected(Settings.get().isIncludeManufacturing());
		jCopying.setSelected(Settings.get().isIncludeCopying());
		jContainerItemID.setSelected(Settings.get().isContainersShowItemID());
		if (Settings.get().isAssetsContractsOwnerCorporation()) {
			jContractsOwner.setSelectedItem(ContractsOwnerFormat.ISSUER_CORPORATION);
		} else if (Settings.get().isAssetsContractsOwnerBoth()) {
			jContractsOwner.setSelectedItem(ContractsOwnerFormat.ISSUER_BOTH);
		} else {
			jContractsOwner.setSelectedItem(ContractsOwnerFormat.ISSUER_CHARACTER);
		}
	}
}

