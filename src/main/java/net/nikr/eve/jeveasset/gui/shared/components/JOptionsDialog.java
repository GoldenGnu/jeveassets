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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JOptionsDialog extends JDialogCentered {

	private enum StockpileImportAction {
		OK,
		CANCEL
	}

	private static final CancelOption CANCEL_OPTION = new CancelOption();

	private final JLabel jName;
	private final JCheckBox jAll;
	private final JButton jOK;
	private final JButton jCancel;
	private final List<OptionsContainer> containers = new ArrayList<>();
	private final ListenerClass listenerClass;
	private OptionEnum option = null;


	public JOptionsDialog(Program program) {
		super(program, GuiShared.get().importOptions());

		listenerClass = new ListenerClass();

		jName = new JLabel();
		jName.setFont(jName.getFont().deriveFont(Font.BOLD));

		jAll = new JCheckBox();

		jOK = new JButton(GuiShared.get().ok());
		jOK.setActionCommand(StockpileImportAction.OK.name());
		jOK.addActionListener(listenerClass);

		jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(StockpileImportAction.CANCEL.name());
		jCancel.addActionListener(listenerClass);
	}

	private void doLayout(List<OptionEnum> options, OptionEnum defaultOption, boolean showAll) {
		jPanel.removeAll();
		containers.clear();

		Group optionHorizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		Group helpHorizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		Group horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		Group verticalGroup = layout.createSequentialGroup();
		//Name
		if (!jName.getText().isEmpty()) {
			verticalGroup.addComponent(jName, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight());
			horizontalGroup.addComponent(jName);
		}
		//Options
		if (defaultOption == null && !options.isEmpty()) {
			defaultOption = options.get(0);
		}
		ButtonGroup buttonGroup = new ButtonGroup();
		for (OptionEnum optionEnum : options) {
			OptionsContainer optionsContainer = new OptionsContainer(optionEnum);
			containers.add(optionsContainer);
			buttonGroup.add(optionsContainer.getRadioButton());
			optionHorizontalGroup.addComponent(optionsContainer.getRadioButton());
			helpHorizontalGroup.addComponent(optionsContainer.getLabel());
			optionsContainer.getRadioButton().setSelected(defaultOption == optionEnum);
			verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(optionsContainer.getRadioButton(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(optionsContainer.getLabel(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
			);
		}
		horizontalGroup
				.addGroup(layout.createSequentialGroup()
					.addGroup(optionHorizontalGroup)
					.addGap(20)
					.addGroup(helpHorizontalGroup)
				);
		verticalGroup.addGap(20);
		//All
		if (showAll) {
			horizontalGroup
				.addGroup(layout.createSequentialGroup()
					.addComponent(jAll)
					.addGap(0, 0, Integer.MAX_VALUE)
				);
			verticalGroup
					.addComponent(jAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGap(20);

		}
		//OK/Cancel
		verticalGroup.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				);
		horizontalGroup.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				);

		layout.setHorizontalGroup(horizontalGroup);
		layout.setVerticalGroup(verticalGroup);
	}

	public OptionEnum show(String heading, String title, String all, boolean enableAll, boolean showAll, List<OptionEnum> options, OptionEnum defaultOption) {
		option = null;
		jName.setText(heading);
		getDialog().setTitle(title);
		doLayout(options, defaultOption, showAll);
		jAll.setSelected(false);
		jAll.setText(all);
		jAll.setEnabled(enableAll);
		setVisible(true);
		return option;
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		for (OptionsContainer container : containers) {
			if (container.getRadioButton().isSelected()) {
				option = container.getOption();
				option.setAll(jAll.isSelected());
				break;
			}
		}
		setVisible(false);
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (StockpileImportAction.OK.name().equals(e.getActionCommand())) {
				save();
			}
			if (StockpileImportAction.CANCEL.name().equals(e.getActionCommand())) {
				option = CANCEL_OPTION;
				setVisible(false);
			}
		}
	}

	public static interface OptionEnum {
		public String getText();
		public String getHelp();
		public boolean isAll();
		public void setAll(boolean all);
	}

	private static class CancelOption implements OptionEnum {

		@Override
		public String getText() {
			return "";
		}

		@Override
		public String getHelp() {
			return "";
		}

		@Override
		public boolean isAll() {
			return true;
		}

		@Override
		public void setAll(boolean all) { }

	}

	private static class OptionsContainer {
		private final OptionEnum option;
		private final JRadioButton jRadioButton;
		private final JLabel jLabel;

		public OptionsContainer(OptionEnum option) {
			this.option = option;
			option.setAll(false); //Reset

			jRadioButton = new JRadioButton(option.getText());

			jLabel = new JLabel(option.getHelp());
		}

		public OptionEnum getOption() {
			return option;
		}

		public JRadioButton getRadioButton() {
			return jRadioButton;
		}

		public JLabel getLabel() {
			return jLabel;
		}
	}

}
