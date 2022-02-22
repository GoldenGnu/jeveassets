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

import com.sun.jna.Platform;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.DecimalSeparator;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.Settings.TransactionProfitPrice;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class GeneralSettingsPanel extends JSettingsPanel {
	
	private final JCheckBox jEnterFilters;
	private final JCheckBox jHighlightSelectedRow;
	private final JCheckBox jFocusEveOnline;
	private final JTextField jMaxOrderAge;
	private final JTextField jTransactionProfitMargin;
	private final JComboBox<TransactionProfitPrice> jTransactionProfitPrice;
	private final JComboBox<DecimalSeparator> jDecimalSeparator;


	public GeneralSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().general(),  Images.DIALOG_SETTINGS.getIcon());

		jEnterFilters = new JCheckBox(DialoguesSettings.get().enterFilter());

		jHighlightSelectedRow = new JCheckBox(DialoguesSettings.get().highlightSelectedRow());

		jFocusEveOnline = new JCheckBox(DialoguesSettings.get().focusEveOnline());

		JLabel jDecimalSeparatorLabel = new JLabel(DialoguesSettings.get().copyDecimalSeparator());
		jDecimalSeparator = new JComboBox<>(DecimalSeparator.values());

		JLabel jFocusEveOnlineLinuxHelp = new JLabel(DialoguesSettings.get().focusEveOnlineLinuxHelp());
		jFocusEveOnlineLinuxHelp.setVisible(Platform.isLinux());
		JLabel jFocusEveOnlineLinuxHelp2 = new JLabel(DialoguesSettings.get().focusEveOnlineLinuxHelp2());
		jFocusEveOnlineLinuxHelp2.setVisible(Platform.isLinux());
		JTextField jFocusEveOnlineLinuxCmd = new JTextField(DialoguesSettings.get().focusEveOnlineLinuxCmd());
		jFocusEveOnlineLinuxCmd.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jFocusEveOnlineLinuxCmd.selectAll();
			}
		});
		jFocusEveOnlineLinuxCmd.setEditable(false);
		jFocusEveOnlineLinuxCmd.setVisible(Platform.isLinux());

		JLabel jTransactionProfitLabel = new JLabel(DialoguesSettings.get().transactionsProfit());

		JLabel jMaxOrderAgeLabel = new JLabel(DialoguesSettings.get().includeDays());
		jMaxOrderAge = new JIntegerField("0", DocumentFactory.ValueFlag.POSITIVE_AND_ZERO);
		JLabel jDaysLabel = new JLabel(DialoguesSettings.get().days());

		JLabel jTransactionProfitPriceLabel = new JLabel(DialoguesSettings.get().transactionsPrice());
		jTransactionProfitPrice = new JComboBox<>(TransactionProfitPrice.values());
		jTransactionProfitPrice.setPrototypeDisplayValue(TransactionProfitPrice.LASTEST);

		JLabel jTransactionProfitMarginLabel = new JLabel(DialoguesSettings.get().transactionsMargin());
		jTransactionProfitMargin = new JIntegerField("0", DocumentFactory.ValueFlag.POSITIVE_AND_ZERO);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jEnterFilters)
				.addComponent(jHighlightSelectedRow)
				.addComponent(jFocusEveOnline)
				.addGroup(layout.createSequentialGroup()
					.addGap(25)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jFocusEveOnlineLinuxHelp)
						.addComponent(jFocusEveOnlineLinuxHelp2)
						.addComponent(jFocusEveOnlineLinuxCmd)
					)
				)
				.addComponent(jTransactionProfitLabel)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jDecimalSeparatorLabel)
						.addGroup(layout.createSequentialGroup()
							.addGap(25)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(jTransactionProfitPriceLabel)
								.addComponent(jMaxOrderAgeLabel)
								.addComponent(jTransactionProfitMarginLabel)
							)
						)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jMaxOrderAge)
						.addComponent(jTransactionProfitPrice)
						.addComponent(jTransactionProfitMargin)
						.addComponent(jDecimalSeparator)
					)
					.addComponent(jDaysLabel)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jEnterFilters, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jHighlightSelectedRow, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jFocusEveOnline, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jFocusEveOnlineLinuxHelp, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jFocusEveOnlineLinuxHelp2, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jFocusEveOnlineLinuxCmd, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGap(10)
				.addGroup(layout.createParallelGroup()
					.addComponent(jDecimalSeparatorLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDecimalSeparator, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jTransactionProfitLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jMaxOrderAgeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMaxOrderAge, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDaysLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jTransactionProfitPriceLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTransactionProfitPrice, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jTransactionProfitMarginLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTransactionProfitMargin, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
		TransactionProfitPrice transactionProfitPrice = jTransactionProfitPrice.getItemAt(jTransactionProfitPrice.getSelectedIndex());
		int transactionProfitMargin;
		try {
			transactionProfitMargin = Integer.valueOf(jTransactionProfitMargin.getText());
		} catch (NumberFormatException ex) {
			transactionProfitMargin = 0;
		}
		DecimalSeparator copyDecimalSeparator = jDecimalSeparator.getItemAt(jDecimalSeparator.getSelectedIndex());
		boolean update = jHighlightSelectedRow.isSelected() != Settings.get().isHighlightSelectedRows()
						|| maximumPurchaseAge != Settings.get().getMaximumPurchaseAge()
						|| transactionProfitPrice != Settings.get().getTransactionProfitPrice()
						|| copyDecimalSeparator != 	Settings.get().getCopySettings().getCopyDecimalSeparator()
						;
		Settings.get().setFilterOnEnter(jEnterFilters.isSelected());
		Settings.get().setHighlightSelectedRows(jHighlightSelectedRow.isSelected());
		Settings.get().setFocusEveOnlineOnEsiUiCalls(jFocusEveOnline.isSelected());
		Settings.get().setMaximumPurchaseAge(maximumPurchaseAge);
		Settings.get().setTransactionProfitPrice(transactionProfitPrice);
		Settings.get().setTransactionProfitMargin(transactionProfitMargin);
		Settings.get().getCopySettings().setCopyDecimalSeparator(copyDecimalSeparator);
		return update;
	}

	@Override
	public void load() {
		jEnterFilters.setSelected(Settings.get().isFilterOnEnter());
		jHighlightSelectedRow.setSelected(Settings.get().isHighlightSelectedRows());
		jFocusEveOnline.setSelected(Settings.get().isFocusEveOnlineOnEsiUiCalls());
		jMaxOrderAge.setText(String.valueOf(Settings.get().getMaximumPurchaseAge()));
		jTransactionProfitPrice.setSelectedItem(Settings.get().getTransactionProfitPrice());
		jTransactionProfitMargin.setText(String.valueOf(Settings.get().getTransactionProfitMargin()));
		jDecimalSeparator.setSelectedItem(Settings.get().getCopySettings().getCopyDecimalSeparator());
	}
}
