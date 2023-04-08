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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JDoubleField;
import static net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileItemDialog.round;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class StockpileBpDialog extends JDialogCentered {

	private enum StockpileBpAction {
		CANCEL,
		OK,
		TYPE_CHANGE
	}

	private static final int WIDTH = 200;

	private final JButton jOK;
	private final JLabel jBlueprintTypeLabel;
	private final JComboBox<String> jBlueprintType;
	private final JComboBox<Integer> jManufacturingMe;
	private final JCheckBox jManufacturingEngineeringComplex;
	private final JDoubleField jManufacturingFacility;

	private final List<JComponent> manufacturingComponents = new ArrayList<>();
	private BpData returnValue;

	public StockpileBpDialog(Program program) {
		super(program, "", Images.TOOL_STOCKPILE.getImage());

		ListenerClass listener = new ListenerClass();

		jBlueprintTypeLabel = new JLabel();
		jBlueprintType = new JComboBox<>();
		jBlueprintType.setActionCommand(StockpileBpAction.TYPE_CHANGE.name());
		jBlueprintType.addActionListener(listener);

		JLabel jManufacturingMeLabel = new JLabel(TabsStockpile.get().me());
		manufacturingComponents.add(jManufacturingMeLabel);
		Integer[] me = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		jManufacturingMe = new JComboBox<>(me);
		jManufacturingMe.setPrototypeDisplayValue(10);
		jManufacturingMe.setMaximumRowCount(me.length);
		manufacturingComponents.add(jManufacturingMe);

		JLabel jManufacturingFacilityLabel = new JLabel(TabsStockpile.get().blueprintFacility());
		manufacturingComponents.add(jManufacturingFacilityLabel);
		jManufacturingFacility = new JDoubleField("0");
		manufacturingComponents.add(jManufacturingFacility);

		JLabel jManufacturingPercentLabel = new JLabel(TabsStockpile.get().blueprintPercent());
		manufacturingComponents.add(jManufacturingPercentLabel);

		jManufacturingEngineeringComplex = new JCheckBox(TabsStockpile.get().blueprintEngineeringComplex());
		manufacturingComponents.add(jManufacturingEngineeringComplex);

		jOK = new JButton(TabsStockpile.get().ok());
		jOK.setActionCommand(StockpileBpAction.OK.name());
		jOK.addActionListener(listener);

		JButton jCancel = new JButton(TabsStockpile.get().cancel());
		jCancel.setActionCommand(StockpileBpAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createParallelGroup()
					.addComponent(jBlueprintTypeLabel, WIDTH, WIDTH, WIDTH)
					.addComponent(jBlueprintType, WIDTH, WIDTH, WIDTH)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
							.addComponent(jManufacturingMeLabel)
							.addComponent(jManufacturingFacilityLabel)
						)
						.addGroup(layout.createParallelGroup()
							.addComponent(jManufacturingEngineeringComplex)
							.addComponent(jManufacturingMe)
							.addGroup(layout.createSequentialGroup()
								.addComponent(jManufacturingFacility)
								.addGap(2)
								.addComponent(jManufacturingPercentLabel)
							)
						)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jBlueprintTypeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGap(0)
				.addComponent(jBlueprintType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jManufacturingMeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jManufacturingMe, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jManufacturingFacilityLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jManufacturingFacility, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jManufacturingPercentLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jManufacturingEngineeringComplex, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	public BpData show(String title, String msg, String[] options) {
		returnValue = null;
		jBlueprintTypeLabel.setText(msg);
		getDialog().setTitle(title);
		jBlueprintType.setModel(new DefaultComboBoxModel<>(options));
		jBlueprintType.setSelectedIndex(0);
		jManufacturingMe.setSelectedIndex(0);
		jManufacturingFacility.setText("0");
		jManufacturingEngineeringComplex.setSelected(false);
		setVisible(true);
		return returnValue;
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jBlueprintType;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		String type = jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex());
		if (type.equals(TabsStockpile.get().materialsManufacturing())
				|| type.equals(TabsStockpile.get().materialsReaction())) {
			int me = jManufacturingMe.getItemAt(jManufacturingMe.getSelectedIndex());
			double ec = jManufacturingEngineeringComplex.isSelected() ? 0.99 : 1;
			double facility;
			try {
				facility = (100.0 - Double.parseDouble(jManufacturingFacility.getText())) / 100.0;
			} catch (NumberFormatException ex) {
				facility = 1; //1 = no bonus
			}
			returnValue = new BpData(type, me, ec, facility);
		} else {
			returnValue = new BpData(type);
		}
		setVisible(false);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (StockpileBpAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (StockpileBpAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (StockpileBpAction.TYPE_CHANGE.name().equals(e.getActionCommand())) {
				String type = jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex());
				if (type.equals(TabsStockpile.get().materialsManufacturing()) || type.equals(TabsStockpile.get().materialsReaction())) {
					for (JComponent jComponent : manufacturingComponents) {
						jComponent.setVisible(true);
					}
					getDialog().pack();
				} else {
					for (JComponent jComponent : manufacturingComponents) {
						jComponent.setVisible(false);
					}
					getDialog().pack();
				}
			}
		}
	}

	public static class BpData {

		private final String type;
		private final Integer me;
		private final Double ec;
		private final Double facility;

		public BpData(String type) {
			this.type = type;
			this.me = null;
			this.ec = null;
			this.facility = null;
		}

		public BpData(String type, Integer me, double ec, double facility) {
			this.type = type;
			this.me = me;
			this.ec = ec;
			this.facility = facility;
		}

		public boolean isManufacturing() {
			return me != null && ec != null&& facility != null;
		}

		public String getType() {
			return type;
		}

		public Integer getMe() {
			return me;
		}

		public Double getEc() {
			return ec;
		}

		public Double getFacility() {
			return facility;
		}

		public boolean matches(String value) {
			return type.equals(value);
		}

		public double doMath(int quantity, double countMinimum) {
			return Math.max(countMinimum, Math.ceil(round((quantity * ((100.0 - me) / 100.0) * ec * facility) * countMinimum, 2)));
		}
	}
}
