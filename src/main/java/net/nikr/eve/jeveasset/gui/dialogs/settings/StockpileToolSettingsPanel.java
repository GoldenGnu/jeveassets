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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Colors;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class StockpileToolSettingsPanel extends JSettingsPanel {
	
	private final JCheckBox jSwitchTab;
	private final JRadioButton jTwoGroups;
	private final JRadioButton jThreeGroups;
	private final JFormattedTextField jGroup2;
	private final JFormattedTextField jGroup3;
	private final JLabel jGroup1Label;
	private final JLabel jGroup2Label;
	private final JLabel jGroup3Label;
	
	public StockpileToolSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().stockpile(), Images.TOOL_STOCKPILE.getIcon());

		jSwitchTab = new JCheckBox(DialoguesSettings.get().stockpileSwitchTab());

		ButtonGroup group = new ButtonGroup();

		jTwoGroups = new JRadioButton(DialoguesSettings.get().stockpileTwoGroups());
		jTwoGroups.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useTwoGroups();
			}
		});
		group.add(jTwoGroups);

		jThreeGroups = new JRadioButton(DialoguesSettings.get().stockpileThreeGroups());
		jThreeGroups.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useThreeGroups();
			}
		});
		group.add(jThreeGroups);

		JLabel jColors = new JLabel(DialoguesSettings.get().stockpileColors());

		jGroup1Label = new JLabel();
		jGroup1Label.setOpaque(true);
		jGroup1Label.setHorizontalAlignment(JLabel.CENTER);
		jGroup1Label.setBackground(Colors.LIGHT_RED.getColor());
		jGroup1Label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		jGroup2 = new JFormattedTextField(new DecimalFormat("##0", new DecimalFormatSymbols(Locale.ENGLISH)) );
		jGroup2.setOpaque(true);
		jGroup2.setHorizontalAlignment(JLabel.TRAILING);
		jGroup2.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				validate();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						jGroup2.selectAll();
					}
				});
			}
			@Override
			public void focusLost(FocusEvent e) {
				validate();
			}
		});

		jGroup2Label = new JLabel();
		jGroup2Label.setOpaque(true);
		jGroup2Label.setHorizontalAlignment(JLabel.LEADING);
		jGroup2Label.setBackground(Colors.LIGHT_RED.getColor());
		jGroup2Label.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
		jGroup2Label.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				jGroup2.requestFocusInWindow();
			}
		});

		jGroup3 = new JFormattedTextField(new DecimalFormat("##0", new DecimalFormatSymbols(Locale.ENGLISH)) );
		jGroup3.setOpaque(true);
		jGroup3.setHorizontalAlignment(JLabel.TRAILING);
		jGroup3.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				validate();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						jGroup3.selectAll();
					}
				});
			}
			@Override
			public void focusLost(FocusEvent e) {
				validate();
			}
		});

		jGroup3Label = new JLabel();
		jGroup3Label.setOpaque(true);
		jGroup3Label.setHorizontalAlignment(JLabel.LEADING);
		jGroup3Label.setBackground(Colors.LIGHT_RED.getColor());
		jGroup3Label.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
		jGroup3Label.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (jGroup3.isEnabled()) {
					jGroup3.requestFocusInWindow();
				}
			}
		});


		jTwoGroups.setSelected(true);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jSwitchTab)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jColors)
						.addComponent(jGroup1Label, 70, 70, Integer.MAX_VALUE)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jTwoGroups)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jGroup2, 35, 35, Integer.MAX_VALUE)
							.addGap(0)
							.addComponent(jGroup2Label, 35, 35, Integer.MAX_VALUE)
						)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jThreeGroups)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jGroup3, 35, 35, Integer.MAX_VALUE)
							.addGap(0)
							.addComponent(jGroup3Label, 35, 35, Integer.MAX_VALUE)
						)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jSwitchTab, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGap(20)
				.addGroup(layout.createParallelGroup()
					.addComponent(jColors, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTwoGroups, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jThreeGroups, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jGroup1Label, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jGroup2, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jGroup2Label, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jGroup3, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jGroup3Label, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	@Override
	public boolean save() {
		int group2 = getNumber(jGroup2);
		int group3 = getNumber(jGroup3);
		boolean updated = Settings.get().isStockpileHalfColors() != jThreeGroups.isSelected()
				|| group2 != Settings.get().getStockpileColorGroup2()
				|| group3 != Settings.get().getStockpileColorGroup3();
		Settings.get().setStockpileColorGroup2(group2);
		Settings.get().setStockpileFocusTab(jSwitchTab.isSelected());
		Settings.get().setStockpileHalfColors(jThreeGroups.isSelected());
		Settings.get().setStockpileColorGroup3(group3);
		return updated;
	}

	@Override
	public void load() {
		jSwitchTab.setSelected(Settings.get().isStockpileFocusTab());
		int group1 = Settings.get().getStockpileColorGroup2();
		int group2 = Settings.get().getStockpileColorGroup3();
		if (Settings.get().isStockpileHalfColors()) {
			jThreeGroups.setSelected(true);
			if (group1 > 0) {
				jGroup2.setText(String.valueOf(group1));
			} else {
				jGroup2.setText("50");
			}
			if (group2 > 0) {
				jGroup3.setText(String.valueOf(group2));
			} else {
				jGroup3.setText("100");
			}
			useThreeGroups();
		} else {
			jTwoGroups.setSelected(true);
			if (group1 > 0) {
				jGroup2.setText(String.valueOf(group1));
			} else {
				jGroup2.setText("100");
			}
			useTwoGroups();
		}
		validate();
	}

	private void validate() {
		int group2 = getNumber(jGroup2);
		if (group2 <= 0) {
			group2 = 1;
			jGroup2.setText("1");
		}
		jGroup1Label.setText("0-" + jGroup2.getText() + DialoguesSettings.get().percentSymbol());
		if (jGroup3.isEnabled()) {
			int group3 = getNumber(jGroup3);
			if (group2 >= group3) {
				jGroup3.setText(String.valueOf(group2 + 1));
			}
			jGroup2Label.setText("-" + jGroup3.getText() + DialoguesSettings.get().percentSymbol());
			jGroup3Label.setText(DialoguesSettings.get().percentPlusSymbol());
		} else {
			jGroup3Label.setText("");
			jGroup2Label.setText(DialoguesSettings.get().percentPlusSymbol());
		}
	}

	private void useTwoGroups() {
		jGroup2Label.setBackground(Colors.LIGHT_GREEN.getColor());
		jGroup3Label.setBackground(Color.LIGHT_GRAY);
		jGroup3.setText("");
		jGroup3.setEnabled(false);
		jGroup2.requestFocusInWindow();
	}
	private void useThreeGroups() {
		jGroup2Label.setBackground(Colors.LIGHT_YELLOW.getColor());
		jGroup3Label.setBackground(Colors.LIGHT_GREEN.getColor());
		jGroup3.setEnabled(true);
		jGroup2.requestFocusInWindow();
	}

	private int getNumber(JTextComponent jText) {
		int number;
		try {
			number = Integer.valueOf(jText.getText());
		} catch (NumberFormatException ex) {
			number = 0;
		}
		return number;
	}
}
