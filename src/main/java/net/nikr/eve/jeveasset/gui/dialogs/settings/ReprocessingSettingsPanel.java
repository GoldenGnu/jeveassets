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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.ReprocessSettings;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class ReprocessingSettingsPanel extends JSettingsPanel {

	private static final int LEVEL0 = 0;
	private static final int LEVEL1 = 1;
	private static final int LEVEL2 = 2;
	private static final int LEVEL3 = 3;
	private static final int LEVEL4 = 4;
	private static final int LEVEL5 = 5;

	private JRadioButton jStation50;
	private JRadioButton jStationOther;
	private JTextField jStation;
	private JRadioButton[] jReprocessing;
	private JRadioButton[] jReprocessingEfficiency;
	private JRadioButton[] jScrapmetalProcessing;

	public ReprocessingSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().reprocessing(), Images.SETTINGS_REPROCESSING.getIcon());

		ListenerClass listener = new ListenerClass();

		JLabel jNotes = new JLabel(DialoguesSettings.get().reprocessingWarning());

		JLabel jStationLabel = new JLabel(DialoguesSettings.get().stationEquipment());
		jStation50 = new JRadioButton(DialoguesSettings.get().fiftyPercent());
		jStation50.addActionListener(listener);
		jStationOther = new JRadioButton(DialoguesSettings.get().customPercent());
		jStationOther.addActionListener(listener);
		jStation = new JIntegerField(DocumentFactory.ValueFlag.POSITIVE_AND_ZERO);
		JLabel jStationPercentLabel = new JLabel(DialoguesSettings.get().percentSymbol());

		ButtonGroup jStationButtonGroup = new ButtonGroup();
		jStationButtonGroup.add(jStation50);
		jStationButtonGroup.add(jStationOther);

		JLabel j0 = new JLabel(DialoguesSettings.get().zero());
		JLabel j1 = new JLabel(DialoguesSettings.get().one());
		JLabel j2 = new JLabel(DialoguesSettings.get().two());
		JLabel j3 = new JLabel(DialoguesSettings.get().three());
		JLabel j4 = new JLabel(DialoguesSettings.get().four());
		JLabel j5 = new JLabel(DialoguesSettings.get().five());

		JLabel jReprocessingLabel=  new JLabel(DialoguesSettings.get().reprocessingLevel());
		jReprocessing = new JRadioButton[6];
		jReprocessing[LEVEL0] = new JRadioButton();
		jReprocessing[LEVEL0].addActionListener(listener);
		jReprocessing[LEVEL1] = new JRadioButton();
		jReprocessing[LEVEL1].addActionListener(listener);
		jReprocessing[LEVEL2] = new JRadioButton();
		jReprocessing[LEVEL2].addActionListener(listener);
		jReprocessing[LEVEL3] = new JRadioButton();
		jReprocessing[LEVEL3].addActionListener(listener);
		jReprocessing[LEVEL4] = new JRadioButton();
		jReprocessing[LEVEL4].addActionListener(listener);
		jReprocessing[LEVEL5] = new JRadioButton();
		jReprocessing[LEVEL5].addActionListener(listener);

		ButtonGroup jReprocessingButtonGroup = new ButtonGroup();
		jReprocessingButtonGroup.add(jReprocessing[LEVEL0]);
		jReprocessingButtonGroup.add(jReprocessing[LEVEL1]);
		jReprocessingButtonGroup.add(jReprocessing[LEVEL2]);
		jReprocessingButtonGroup.add(jReprocessing[LEVEL3]);
		jReprocessingButtonGroup.add(jReprocessing[LEVEL4]);
		jReprocessingButtonGroup.add(jReprocessing[LEVEL5]);

		JLabel jReprocessingEfficiencyLabel = new JLabel(DialoguesSettings.get().reprocessingEfficiencyLevel());
		jReprocessingEfficiency = new JRadioButton[6];
		jReprocessingEfficiency[LEVEL0] = new JRadioButton();
		jReprocessingEfficiency[LEVEL0].addActionListener(listener);
		jReprocessingEfficiency[LEVEL1] = new JRadioButton();
		jReprocessingEfficiency[LEVEL1].addActionListener(listener);
		jReprocessingEfficiency[LEVEL2] = new JRadioButton();
		jReprocessingEfficiency[LEVEL2].addActionListener(listener);
		jReprocessingEfficiency[LEVEL3] = new JRadioButton();
		jReprocessingEfficiency[LEVEL3].addActionListener(listener);
		jReprocessingEfficiency[LEVEL4] = new JRadioButton();
		jReprocessingEfficiency[LEVEL4].addActionListener(listener);
		jReprocessingEfficiency[LEVEL5] = new JRadioButton();
		jReprocessingEfficiency[LEVEL5].addActionListener(listener);

		ButtonGroup jReprocessingEfficiencyButtonGroup = new ButtonGroup();
		jReprocessingEfficiencyButtonGroup.add(jReprocessingEfficiency[LEVEL0]);
		jReprocessingEfficiencyButtonGroup.add(jReprocessingEfficiency[LEVEL1]);
		jReprocessingEfficiencyButtonGroup.add(jReprocessingEfficiency[LEVEL2]);
		jReprocessingEfficiencyButtonGroup.add(jReprocessingEfficiency[LEVEL3]);
		jReprocessingEfficiencyButtonGroup.add(jReprocessingEfficiency[LEVEL4]);
		jReprocessingEfficiencyButtonGroup.add(jReprocessingEfficiency[LEVEL5]);

		JLabel jScrapmetalProcessingLabel = new JLabel(DialoguesSettings.get().scrapMetalProcessingLevel());
		jScrapmetalProcessing = new JRadioButton[6];
		jScrapmetalProcessing[LEVEL0] = new JRadioButton();
		jScrapmetalProcessing[LEVEL1] = new JRadioButton();
		jScrapmetalProcessing[LEVEL2] = new JRadioButton();
		jScrapmetalProcessing[LEVEL3] = new JRadioButton();
		jScrapmetalProcessing[LEVEL4] = new JRadioButton();
		jScrapmetalProcessing[LEVEL5] = new JRadioButton();

		ButtonGroup jProcessingButtonGroup = new ButtonGroup();
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL0]);
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL1]);
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL2]);
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL3]);
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL4]);
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL5]);


		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jNotes)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jStationLabel)
						.addComponent(jReprocessingLabel)
						.addComponent(jReprocessingEfficiencyLabel)
						.addComponent(jScrapmetalProcessingLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jStation50)
							.addComponent(jStationOther)
							.addComponent(jStation)
							.addComponent(jStationPercentLabel)
						)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j0)
								.addComponent(jReprocessing[LEVEL0])
								.addComponent(jReprocessingEfficiency[LEVEL0])
								.addComponent(jScrapmetalProcessing[LEVEL0])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j1)
								.addComponent(jReprocessing[LEVEL1])
								.addComponent(jReprocessingEfficiency[LEVEL1])
								.addComponent(jScrapmetalProcessing[LEVEL1])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j2)
								.addComponent(jReprocessing[LEVEL2])
								.addComponent(jReprocessingEfficiency[LEVEL2])
								.addComponent(jScrapmetalProcessing[LEVEL2])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j3)
								.addComponent(jReprocessing[LEVEL3])
								.addComponent(jReprocessingEfficiency[LEVEL3])
								.addComponent(jScrapmetalProcessing[LEVEL3])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j4)
								.addComponent(jReprocessing[LEVEL4])
								.addComponent(jReprocessingEfficiency[LEVEL4])
								.addComponent(jScrapmetalProcessing[LEVEL4])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j5)
								.addComponent(jReprocessing[LEVEL5])
								.addComponent(jReprocessingEfficiency[LEVEL5])
								.addComponent(jScrapmetalProcessing[LEVEL5])
							)
						)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jStationLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStation50, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStationOther, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStationPercentLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(j0, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(j1, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(j2, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(j3, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(j4, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(j5, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(0)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jReprocessingLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessing[LEVEL0], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessing[LEVEL1], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessing[LEVEL2], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessing[LEVEL3], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessing[LEVEL4], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessing[LEVEL5], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jReprocessingEfficiencyLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessingEfficiency[LEVEL0], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessingEfficiency[LEVEL1], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessingEfficiency[LEVEL2], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessingEfficiency[LEVEL3], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessingEfficiency[LEVEL4], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jReprocessingEfficiency[LEVEL5], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jScrapmetalProcessingLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL0], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL1], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL2], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL3], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL4], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL5], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jNotes, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}



	@Override
	public boolean save() {
		ReprocessSettings reprocessSettings = new ReprocessSettings(Integer.parseInt(jStation.getText()), getSelected(jReprocessing), getSelected(jReprocessingEfficiency), getSelected(jScrapmetalProcessing));
		boolean update = !Settings.get().getReprocessSettings().equals(reprocessSettings);
		Settings.get().setReprocessSettings(reprocessSettings);
		//Update table if needed
		return update;
	}

	@Override
	public void load() {
		ReprocessSettings reprocessSettings = Settings.get().getReprocessSettings();
		if (reprocessSettings.getStation() == 50) {
			 jStation50.setSelected(true);
		} else {
			jStationOther.setSelected(true);
		}
		jStation.setText(String.valueOf(reprocessSettings.getStation()));
		jReprocessing[reprocessSettings.getReprocessingLevel()].setSelected(true);
		jReprocessingEfficiency[reprocessSettings.getReprocessingEfficiencyLevel()].setSelected(true);
		jScrapmetalProcessing[reprocessSettings.getScrapmetalProcessingLevel()].setSelected(true);
		validateSkills();
		validateStation();
	}

	private int getSelected(final JRadioButton[] jRadioButtons) {
		for (int i = 0; i < jRadioButtons.length; i++) {
			if (jRadioButtons[i].isSelected()) {
				return i;
			}
		}
		return 0;
	}

	private void setEnabled(final JRadioButton[] jRadioButtons, final boolean enabled) {
		for (JRadioButton jRadioButton : jRadioButtons) {
			jRadioButton.setEnabled(enabled);
		}
	}

	private void validateSkills() {
		if (getSelected(jReprocessing) < 4) {
			setEnabled(jReprocessingEfficiency, false);
			jReprocessingEfficiency[LEVEL0].setSelected(true);
		} else {
			setEnabled(jReprocessingEfficiency, true);
		}
		if (getSelected(jReprocessingEfficiency) < 5) {
			setEnabled(jScrapmetalProcessing, false);
			jScrapmetalProcessing[LEVEL0].setSelected(true);
		} else {
			setEnabled(jScrapmetalProcessing, true);
		}
	}

	private void validateStation() {
		if (jStation50.isSelected()) {
			jStation.setText("50");
			jStation.setEditable(false);
		}
		if (jStationOther.isSelected()) {
			jStation.setEditable(true);
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			validateSkills();
			validateStation();
		}
	}
}
