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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceSettings;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceSettings.ContractPriceMode;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceSettings.ContractPriceSecurity;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JLabelMultiline;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class ContractPriceSettingsPanel extends JSettingsPanel {

	private final JCheckBox jIncludePrivate;
	private final JCheckBox jDefaultBPC;
	private final JCheckBox jFeedback;
	private final JComboBox<ContractPriceMode> jMode;
	private final JMultiSelectionList<ContractPriceSecurity> jSecurity;
	
	
	public ContractPriceSettingsPanel(Program program, SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().contractPrices(), Images.MISC_CONTRACTS_APPRAISAL.getIcon());

		jIncludePrivate = new JCheckBox(DialoguesSettings.get().includePrivate());
		jDefaultBPC = new JCheckBox(DialoguesSettings.get().defaultBPC());
		jFeedback = new JCheckBox(DialoguesSettings.get().feedback());
		JLabel jPriceModeLabel = new JLabel(DialoguesSettings.get().priceMode());
		jMode = new JComboBox<>(ContractPriceMode.values());
		JLabel jSecurityLabel = new JLabel(DialoguesSettings.get().security());
		jSecurity = new JMultiSelectionList<>(Arrays.asList(ContractPriceSecurity.values()));
		jSecurity.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (jSecurity.getSelectedIndices().length != ContractPriceSecurity.values().length) {
					jIncludePrivate.setEnabled(false);
					jIncludePrivate.setSelected(false);
				} else {
					jIncludePrivate.setEnabled(true);
				}
			}
		});
		jSecurity.setBorder(new JScrollPane(jSecurity).getBorder());
		JLabelMultiline jWarning = new JLabelMultiline(DialoguesSettings.get().updateRequired(), 2);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jPriceModeLabel)
					.addComponent(jMode)
				)
				.addComponent(jSecurityLabel)
				.addComponent(jSecurity, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(jIncludePrivate)
				.addComponent(jDefaultBPC)
				.addComponent(jFeedback)
				.addComponent(jWarning)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceModeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMode, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jSecurityLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSecurity)
				.addComponent(jIncludePrivate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jDefaultBPC, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jFeedback, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jWarning)
		);
		
	}

	@Override
	public boolean save() {
		final ContractPriceSettings old = Settings.get().getContractPriceSettings();
		Object object = jMode.getSelectedItem();
		boolean includePrivate = jIncludePrivate.isSelected();
		boolean defaultBPC = jDefaultBPC.isSelected();
		boolean feedback = jFeedback.isSelected();
		boolean updated = false;
		if (old.getContractPriceMode() != object) {
			updated = true;
		}
		List<ContractPriceSecurity> contractPriceSecurity = jSecurity.getSelectedValuesList();
		if (old.isDefaultBPC() != defaultBPC) {
			updated = true;
		}
		if (object instanceof ContractPriceMode) {
			old.setContractPriceMode((ContractPriceMode) object);
		}
		old.setContractPriceSecurity(new HashSet<>(contractPriceSecurity));
		old.setIncludePrivate(includePrivate);
		old.setDefaultBPC(defaultBPC);
		old.setFeedback(feedback);
		if (contractPriceSecurity.isEmpty()) {
			jSecurity.setSelectedIndex(ContractPriceSecurity.HIGH_SEC.ordinal());
		}
		return updated;
	}

	@Override
	public void load() {
		final ContractPriceSettings contractPriceSettings = Settings.get().getContractPriceSettings();
		jMode.setSelectedItem(contractPriceSettings.getContractPriceMode());
		jIncludePrivate.setSelected(contractPriceSettings.isIncludePrivate());
		jDefaultBPC.setSelected(contractPriceSettings.isDefaultBPC());
		jFeedback.setSelected(contractPriceSettings.isFeedback());
		jSecurity.clearSelection();
		for (ContractPriceSecurity contractPriceSecurity : contractPriceSettings.getContractPriceSecurity()) {
			jSecurity.addSelection(contractPriceSecurity.ordinal(), true);
		}
	}
	
}
