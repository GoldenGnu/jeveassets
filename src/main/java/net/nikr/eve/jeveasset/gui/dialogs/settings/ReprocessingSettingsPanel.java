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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ReprocessSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.components.JDoubleField;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class ReprocessingSettingsPanel extends JSettingsPanel {

	private static final int LEVEL0 = 0;
	private static final int LEVEL1 = 1;
	private static final int LEVEL2 = 2;
	private static final int LEVEL3 = 3;
	private static final int LEVEL4 = 4;
	private static final int LEVEL5 = 5;

	private final JRadioButton jStation50;
	private final JRadioButton jStationOther;
	private final JTextField jStation;
	private final JRadioButton[] jReprocessing;
	private final JRadioButton[] jReprocessingEfficiency;
	private final JRadioButton[] jOreProcessing;
	private final JRadioButton[] jScrapmetalProcessing;

	public ReprocessingSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().reprocessing(), Images.SETTINGS_REPROCESSING.getIcon());

		ListenerClass listener = new ListenerClass();

		JLabel jNotes = new JLabel(DialoguesSettings.get().reprocessingWarning());

		JLabel jStationLabel = new JLabel(DialoguesSettings.get().stationEquipment());
		jStation50 = new JRadioButton(DialoguesSettings.get().fiftyPercent());
		jStation50.addActionListener(listener);
		jStationOther = new JRadioButton();
		jStationOther.addActionListener(listener);
		jStation = new JDoubleField(DocumentFactory.ValueFlag.POSITIVE_AND_ZERO);
		jStation.addMouseListener(listener);
		jStation.setColumns(7);
		JLabel jStationPercentLabel = new JLabel(DialoguesSettings.get().percentSymbol());

		ButtonGroup jStationButtonGroup = new ButtonGroup();
		jStationButtonGroup.add(jStation50);
		jStationButtonGroup.add(jStationOther);

		JLabel jOre0 = new JLabel(DialoguesSettings.get().zero());
		JLabel jOre1 = new JLabel(DialoguesSettings.get().one());
		JLabel jOre2 = new JLabel(DialoguesSettings.get().two());
		JLabel jOre3 = new JLabel(DialoguesSettings.get().three());
		JLabel jOre4 = new JLabel(DialoguesSettings.get().four());
		JLabel jOre5 = new JLabel(DialoguesSettings.get().five());

		JLabel jReprocessingLabel= new JLabel(DialoguesSettings.get().reprocessingLevel());
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

		JLabel jOreLabel = new JLabel(DialoguesSettings.get().oreProcessingLevel());
		jOreProcessing = new JRadioButton[6];
		jOreProcessing[LEVEL0] = new JRadioButton();
		jOreProcessing[LEVEL1] = new JRadioButton();
		jOreProcessing[LEVEL2] = new JRadioButton();
		jOreProcessing[LEVEL3] = new JRadioButton();
		jOreProcessing[LEVEL4] = new JRadioButton();
		jOreProcessing[LEVEL5] = new JRadioButton();

		ButtonGroup jOreButtonGroup = new ButtonGroup();
		jOreButtonGroup.add(jOreProcessing[LEVEL0]);
		jOreButtonGroup.add(jOreProcessing[LEVEL1]);
		jOreButtonGroup.add(jOreProcessing[LEVEL2]);
		jOreButtonGroup.add(jOreProcessing[LEVEL3]);
		jOreButtonGroup.add(jOreProcessing[LEVEL4]);
		jOreButtonGroup.add(jOreProcessing[LEVEL5]);

		JLabel jScrap0 = new JLabel(DialoguesSettings.get().zero());
		JLabel jScrap1 = new JLabel(DialoguesSettings.get().one());
		JLabel jScrap2 = new JLabel(DialoguesSettings.get().two());
		JLabel jScrap3 = new JLabel(DialoguesSettings.get().three());
		JLabel jScrap4 = new JLabel(DialoguesSettings.get().four());
		JLabel jScrap5 = new JLabel(DialoguesSettings.get().five());

		JLabel jScrapmetalLabel = new JLabel(DialoguesSettings.get().scrapmetalProcessingLevel());
		jScrapmetalProcessing = new JRadioButton[6];
		jScrapmetalProcessing[LEVEL0] = new JRadioButton();
		jScrapmetalProcessing[LEVEL1] = new JRadioButton();
		jScrapmetalProcessing[LEVEL2] = new JRadioButton();
		jScrapmetalProcessing[LEVEL3] = new JRadioButton();
		jScrapmetalProcessing[LEVEL4] = new JRadioButton();
		jScrapmetalProcessing[LEVEL5] = new JRadioButton();

		ButtonGroup jScrapmetalButtonGroup = new ButtonGroup();
		jScrapmetalButtonGroup.add(jScrapmetalProcessing[LEVEL0]);
		jScrapmetalButtonGroup.add(jScrapmetalProcessing[LEVEL1]);
		jScrapmetalButtonGroup.add(jScrapmetalProcessing[LEVEL2]);
		jScrapmetalButtonGroup.add(jScrapmetalProcessing[LEVEL3]);
		jScrapmetalButtonGroup.add(jScrapmetalProcessing[LEVEL4]);
		jScrapmetalButtonGroup.add(jScrapmetalProcessing[LEVEL5]);


		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jNotes)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jStationLabel)
						.addComponent(jReprocessingLabel)
						.addComponent(jReprocessingEfficiencyLabel)
						.addComponent(jOreLabel)
						.addComponent(jScrapmetalLabel)
					)
					.addGap(5)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jStation50)
							.addComponent(jStationOther)
							.addComponent(jStation, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(jStationPercentLabel)
						)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(jOre0)
								.addComponent(jReprocessing[LEVEL0])
								.addComponent(jReprocessingEfficiency[LEVEL0])
								.addComponent(jOreProcessing[LEVEL0])
								.addComponent(jScrap0)
								.addComponent(jScrapmetalProcessing[LEVEL0])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(jOre1)
								.addComponent(jReprocessing[LEVEL1])
								.addComponent(jReprocessingEfficiency[LEVEL1])
								.addComponent(jOreProcessing[LEVEL1])
								.addComponent(jScrap1)
								.addComponent(jScrapmetalProcessing[LEVEL1])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(jOre2)
								.addComponent(jReprocessing[LEVEL2])
								.addComponent(jReprocessingEfficiency[LEVEL2])
								.addComponent(jOreProcessing[LEVEL2])
								.addComponent(jScrap2)
								.addComponent(jScrapmetalProcessing[LEVEL2])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(jOre3)
								.addComponent(jReprocessing[LEVEL3])
								.addComponent(jReprocessingEfficiency[LEVEL3])
								.addComponent(jOreProcessing[LEVEL3])
								.addComponent(jScrap3)
								.addComponent(jScrapmetalProcessing[LEVEL3])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(jOre4)
								.addComponent(jReprocessing[LEVEL4])
								.addComponent(jReprocessingEfficiency[LEVEL4])
								.addComponent(jOreProcessing[LEVEL4])
								.addComponent(jScrap4)
								.addComponent(jScrapmetalProcessing[LEVEL4])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(jOre5)
								.addComponent(jReprocessing[LEVEL5])
								.addComponent(jReprocessingEfficiency[LEVEL5])
								.addComponent(jOreProcessing[LEVEL5])
								.addComponent(jScrap5)
								.addComponent(jScrapmetalProcessing[LEVEL5])
							)
						)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jStationLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jStation50, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jStationOther, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jStation, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jStationPercentLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jOre0, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOre1, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOre2, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOre3, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOre4, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOre5, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(0)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jReprocessingLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessing[LEVEL0], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessing[LEVEL1], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessing[LEVEL2], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessing[LEVEL3], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessing[LEVEL4], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessing[LEVEL5], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jReprocessingEfficiencyLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessingEfficiency[LEVEL0], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessingEfficiency[LEVEL1], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessingEfficiency[LEVEL2], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessingEfficiency[LEVEL3], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessingEfficiency[LEVEL4], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jReprocessingEfficiency[LEVEL5], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jOreLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOreProcessing[LEVEL0], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOreProcessing[LEVEL1], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOreProcessing[LEVEL2], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOreProcessing[LEVEL3], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOreProcessing[LEVEL4], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOreProcessing[LEVEL5], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jScrap0, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrap1, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrap2, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrap3, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrap4, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrap5, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(0)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jScrapmetalLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrapmetalProcessing[LEVEL0], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrapmetalProcessing[LEVEL1], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrapmetalProcessing[LEVEL2], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrapmetalProcessing[LEVEL3], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrapmetalProcessing[LEVEL4], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScrapmetalProcessing[LEVEL5], Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(Program.getButtonsHeight())
				.addComponent(jNotes, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}



	@Override
	public UpdateType save() {
		ReprocessSettings reprocessSettings = new ReprocessSettings(Double.parseDouble(jStation.getText()), getSelected(jReprocessing), getSelected(jReprocessingEfficiency), getSelected(jOreProcessing), getSelected(jScrapmetalProcessing));
		boolean update = !Settings.get().getReprocessSettings().equals(reprocessSettings);
		Settings.get().setReprocessSettings(reprocessSettings);
		//Update table if needed
		return update ? UpdateType.FULL_UPDATE : UpdateType.NONE;
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
		jOreProcessing[reprocessSettings.getOreProcessingLevel()].setSelected(true);
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
			setEnabled(jOreProcessing, false);
			jOreProcessing[LEVEL0].setSelected(true);
		} else {
			setEnabled(jOreProcessing, true);
		}
	}

	private void validateStation() {
		if (jStation50.isSelected()) {
			jStation.setText("50");
			jStation.setEnabled(false);
		}
		if (jStationOther.isSelected()) {
			jStation.setEnabled(true);
		}
	}

	private class ListenerClass extends MouseAdapter implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			validateSkills();
			validateStation();
			if (e.getSource().equals(jStationOther)) {
				jStation.requestFocusInWindow();
				jStation.selectAll();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			jStationOther.setSelected(true);
			jStation.setEnabled(true);
			jStation.requestFocusInWindow();
			jStation.selectAll();
		}
	}
}
