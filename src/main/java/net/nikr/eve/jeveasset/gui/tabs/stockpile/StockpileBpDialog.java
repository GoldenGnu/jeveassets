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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingFacility;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


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
	private final JComboBox<Integer> jMe;
	private final JComboBox<ManufacturingFacility> jFacility;
	private final JComboBox<ManufacturingRigs> jRigs;
	private final JComboBox<ManufacturingSecurity> jSecurity;

	private final List<JComponent> manufacturingComponents = new ArrayList<>();
	private BpData returnValue;

	public StockpileBpDialog(Program program) {
		super(program, "", Images.TOOL_STOCKPILE.getImage());

		ListenerClass listener = new ListenerClass();

		jBlueprintTypeLabel = new JLabel();
		jBlueprintType = new JComboBox<>();
		jBlueprintType.setActionCommand(StockpileBpAction.TYPE_CHANGE.name());
		jBlueprintType.addActionListener(listener);

		JLabel jMeLabel = new JLabel(TabsStockpile.get().me());
		manufacturingComponents.add(jMeLabel);
		Integer[] me = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		jMe = new JComboBox<>(me);
		jMe.setPrototypeDisplayValue(10);
		jMe.setMaximumRowCount(me.length);
		manufacturingComponents.add(jMe);

		JLabel jFacilityLabel = new JLabel(TabsStockpile.get().blueprintFacility());
		manufacturingComponents.add(jFacilityLabel);
		jFacility = new JComboBox<>(ManufacturingFacility.values());
		jFacility.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ManufacturingFacility facility = jFacility.getItemAt(jFacility.getSelectedIndex());
				if (facility == ManufacturingFacility.STATION) {
					jRigs.setSelectedIndex(0);
					jRigs.setEnabled(false);
				} else {
					jRigs.setEnabled(true);
				}
			}
		});
		manufacturingComponents.add(jFacility);

		JLabel jRigsLabel = new JLabel(TabsStockpile.get().blueprintRigs());
		manufacturingComponents.add(jRigsLabel);
		jRigs = new JComboBox<>(ManufacturingRigs.values());
		jRigs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ManufacturingRigs rigs = jRigs.getItemAt(jRigs.getSelectedIndex());
				if (rigs == ManufacturingRigs.NONE) {
					jSecurity.setSelectedIndex(0);
					jSecurity.setEnabled(false);
				} else {
					jSecurity.setEnabled(true);
				}
			}
		});
		manufacturingComponents.add(jRigs);

		JLabel jSecurityLabel = new JLabel(TabsStockpile.get().blueprintSecurity());
		manufacturingComponents.add(jSecurityLabel);
		jSecurity = new JComboBox<>(ManufacturingSecurity.values());
		manufacturingComponents.add(jSecurity);

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
							.addComponent(jMeLabel)
							.addComponent(jFacilityLabel)
							.addComponent(jRigsLabel)
							.addComponent(jSecurityLabel)
						)
						.addGroup(layout.createParallelGroup()
							.addComponent(jMe)
							.addComponent(jFacility)
							.addComponent(jRigs)
							.addComponent(jSecurity)
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
					.addComponent(jMeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMe, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jFacilityLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFacility, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jRigsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRigs, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSecurityLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSecurity, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
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
		jMe.setSelectedIndex(0);
		jFacility.setSelectedIndex(0);
		jRigs.setSelectedIndex(0);
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
			int me = jMe.getItemAt(jMe.getSelectedIndex());
			ManufacturingFacility facility = jFacility.getItemAt(jFacility.getSelectedIndex());
			ManufacturingRigs rigs = jRigs.getItemAt(jRigs.getSelectedIndex());
			ManufacturingSecurity security = jSecurity.getItemAt(jSecurity.getSelectedIndex());
			returnValue = new BpData(type, me, facility, rigs, security);
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
		private final ManufacturingFacility facility;
		private final ManufacturingRigs rigs;
		private final ManufacturingSecurity security;

		public BpData(String type) {
			this.type = type;
			this.me = null;
			this.facility = null;
			this.rigs = null;
			this.security = null;
		}

		public BpData(String type, Integer me, ManufacturingFacility facility, ManufacturingRigs rigs, ManufacturingSecurity security) {
			this.type = type;
			this.me = me;
			this.facility = facility;
			this.rigs = rigs;
			this.security = security;
		}

		public boolean matches(String value) {
			return type.equals(value);
		}

		public double doMath(int quantity, double countMinimum) {
			return ApiIdConverter.getManufacturingQuantity(quantity, me, facility, rigs, security, countMinimum);
		}
	}
}
