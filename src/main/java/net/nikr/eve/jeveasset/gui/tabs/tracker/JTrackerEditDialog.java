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
package net.nikr.eve.jeveasset.gui.tabs.tracker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.TrackerData;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.JOptionInput;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JSelectionDialog;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.i18n.TabsTracker;


public class JTrackerEditDialog extends JDialogCentered {

	private enum TrackerEditAction {
		OK,
		CANCEL,
		EDIT_WALLET,
		EDIT_ASSETS
	}

	private static final int FIELD_WIDTH = 140;

	//GUI
	private final JTextField jDate;
	private final JTextField jWalletBalance;
	private final JTextField jImplants;
	private final JButton jWalletBalanceFilterable;
	private final JTextField jAssets;
	private final JButton jAssetsFilterable;
	private final JTextField jSellOrders;
	private final JTextField jEscrows;
	private final JTextField jEscrowsToCover;
	private final JTextField jManufacturing;
	private final JTextField jContractCollateral;
	private final JTextField jContractValue;
	private final JTextField jSkillPoints;
	private final JButton jOK;
	private final JSelectionDialog<String> jSelectionDialog;

	private final List<FilterUpdate> balanceUpdates = new ArrayList<>();
	private final List<FilterUpdate> assetUpdates = new ArrayList<>();

	//Data
	private Value value;
	private boolean update;

	public JTrackerEditDialog(Program program) {
		super(program, TabsTracker.get().edit(), Images.TOOL_TRACKER.getImage());

		ListenerClass listener = new ListenerClass();

		jSelectionDialog = new JSelectionDialog<>(program);

		JLabel jDateLabel = new JLabel(TabsTracker.get().date());
		jDate = new JTextField();
		jDate.setEditable(false);
		jDate.setEnabled(false);
		jDate.setHorizontalAlignment(JLabel.RIGHT);

		JLabel jWalletBalanceLabel = new JLabel(TabsTracker.get().walletBalance());
		jWalletBalance = new JTextField();
		jWalletBalance.setHorizontalAlignment(JTextField.RIGHT);
		jWalletBalance.addFocusListener(listener);

		JLabel jImplantsLabel = new JLabel(TabsTracker.get().implants());
		jImplants = new JTextField();
		jImplants.setHorizontalAlignment(JTextField.RIGHT);
		jImplants.addFocusListener(listener);

		jWalletBalanceFilterable = new JButton(Images.EDIT_EDIT.getIcon());
		jWalletBalanceFilterable.setActionCommand(TrackerEditAction.EDIT_WALLET.name());
		jWalletBalanceFilterable.addActionListener(listener);

		JLabel jAssetsLabel = new JLabel(TabsTracker.get().assets());
		jAssets = new JTextField();
		jAssets.setHorizontalAlignment(JTextField.RIGHT);
		jAssets.addFocusListener(listener);

		jAssetsFilterable = new JButton(Images.EDIT_EDIT.getIcon());
		jAssetsFilterable.setActionCommand(TrackerEditAction.EDIT_ASSETS.name());
		jAssetsFilterable.addActionListener(listener);

		JLabel jSellOrdersLabel = new JLabel(TabsTracker.get().sellOrders());
		jSellOrders = new JTextField();
		jSellOrders.setHorizontalAlignment(JTextField.RIGHT);
		jSellOrders.addFocusListener(listener);

		JLabel jEscrowsLabel = new JLabel(TabsTracker.get().escrows());
		jEscrows = new JTextField();
		jEscrows.setHorizontalAlignment(JTextField.RIGHT);
		jEscrows.addFocusListener(listener);

		JLabel jEscrowsToCoverLabel = new JLabel(TabsTracker.get().escrowsToCover());
		jEscrowsToCover = new JTextField();
		jEscrowsToCover.setHorizontalAlignment(JTextField.RIGHT);
		jEscrowsToCover.addFocusListener(listener);

		JLabel jManufacturingLabel = new JLabel(TabsTracker.get().manufacturing());
		jManufacturing = new JTextField();
		jManufacturing.setHorizontalAlignment(JTextField.RIGHT);
		jManufacturing.addFocusListener(listener);

		JLabel jContractCollateralLabel = new JLabel(TabsTracker.get().contractCollateral());
		jContractCollateral = new JTextField();
		jContractCollateral.setHorizontalAlignment(JTextField.RIGHT);
		jContractCollateral.addFocusListener(listener);

		JLabel jContractValueLabel = new JLabel(TabsTracker.get().contractValue());
		jContractValue = new JTextField();
		jContractValue.setHorizontalAlignment(JTextField.RIGHT);
		jContractValue.addFocusListener(listener);

		JLabel jSkillPointValueLabel = new JLabel(TabsTracker.get().skillPoints());
		jSkillPoints = new JTextField();
		jSkillPoints.setHorizontalAlignment(JTextField.RIGHT);
		jSkillPoints.addFocusListener(listener);

		jOK = new JButton(TabsTracker.get().ok());
		jOK.setActionCommand(TrackerEditAction.OK.name());
		jOK.addActionListener(listener);

		JButton jCancel = new JButton(TabsTracker.get().cancel());
		jCancel.setActionCommand(TrackerEditAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jDateLabel)
						.addComponent(jWalletBalanceLabel)
						.addComponent(jAssetsLabel)
						.addComponent(jImplantsLabel)
						.addComponent(jSellOrdersLabel)
						.addComponent(jEscrowsLabel)
						.addComponent(jEscrowsToCoverLabel)
						.addComponent(jManufacturingLabel)
						.addComponent(jContractCollateralLabel)
						.addComponent(jContractValueLabel)
						.addComponent(jSkillPointValueLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jDate, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jWalletBalance, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jAssets, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jImplants, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jSellOrders, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jEscrows, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jEscrowsToCover, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jManufacturing, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jContractCollateral, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jContractValue, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jSkillPoints, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jWalletBalanceFilterable)
						.addComponent(jAssetsFilterable)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jDateLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jWalletBalanceLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jWalletBalance, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jWalletBalanceFilterable, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAssetsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAssets, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAssetsFilterable, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jImplantsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jImplants, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSellOrdersLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSellOrders, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jEscrowsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jEscrows, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jEscrowsToCoverLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jEscrowsToCover, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jManufacturingLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jManufacturing, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jContractCollateralLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractCollateral, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jContractValueLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractValue, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSkillPointValueLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSkillPoints, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	public boolean showEdit(Value value) {
		this.value = value;
		update = false;
		balanceUpdates.clear();
		assetUpdates.clear();
		if (value.getBalanceFilter().size() < 2) {
			jWalletBalance.setEnabled(true);
			jWalletBalanceFilterable.setVisible(false);
		} else {
			jWalletBalance.setEnabled(false);
			jWalletBalanceFilterable.setVisible(true);
		}
		if (value.getAssetsFilter().size() < 2) {
			jAssets.setEnabled(true);
			jAssetsFilterable.setVisible(false);
		} else {
			jAssets.setEnabled(false);
			jAssetsFilterable.setVisible(true);
		}
		jWalletBalance.setText(format(value.getBalanceTotal()));
		jAssets.setText(format(value.getAssetsTotal()));
		jImplants.setText(format(value.getImplants()));
		jSellOrders.setText(format(value.getSellOrders()));
		jEscrows.setText(format(value.getEscrows()));
		jEscrowsToCover.setText(format(value.getEscrowsToCover()));
		jManufacturing.setText(format(value.getManufacturing()));
		jContractCollateral.setText(format(value.getContractCollateral()));
		jContractValue.setText(format(value.getContractValue()));
		jSkillPoints.setText(format(value.getSkillPoints()));
		jDate.setText(format(value.getDate()));
		setVisible(true);
		return update;
	}

	private String format(double d) {
		return Formatter.longFormat(d);
	}

	private String format(Date d) {
		return Formatter.columnDate(d);
	}

	private double parse(String s) throws ParseException {
		return Formatter.longParse(s);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jWalletBalance;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		try {
			double walletBalanc = parse(jWalletBalance.getText());
			double assets = parse(jAssets.getText());
			double implants = parse(jImplants.getText());
			double sellOrders = parse(jSellOrders.getText());
			double escrows = parse(jEscrows.getText());
			double escrowsToCover = parse(jEscrowsToCover.getText());
			double manufacturing = parse(jManufacturing.getText());
			double contractCollateral = parse(jContractCollateral.getText());
			double contractValue = parse(jContractValue.getText());
			double skillPointValue = parse(jSkillPoints.getText());
			try {
				TrackerData.writeLock();
				if (value.getBalanceFilter().isEmpty()) {
					value.setBalanceTotal(walletBalanc);
				} else if (value.getBalanceFilter().size() == 1) {
					for (Map.Entry<String, Double> entry : value.getBalanceFilter().entrySet()) {
						//Just done once...
						value.removeBalance(entry.getKey()); //Remove old value
						value.addBalance(entry.getKey(), walletBalanc); //Add new value
					}
				} else {
					for (FilterUpdate filterUpdate : balanceUpdates) {
						value.removeBalance(filterUpdate.getKey());
						value.addBalance(filterUpdate.getKey(), filterUpdate.getValue());
					}
				}
				if (value.getAssetsFilter().isEmpty()) {
					value.setAssetsTotal(assets);
				} else if (value.getAssetsFilter().size() == 1) {
					for (Map.Entry<AssetValue, Double> entry : value.getAssetsFilter().entrySet()) {
						//Just done once...
						value.removeAssets(entry.getKey()); //Remove old value
						value.addAssets(entry.getKey(), assets); //Add new value
					}
				} else {
					for (FilterUpdate filterUpdate : assetUpdates) {
						AssetValue assetValue = AssetValue.create(filterUpdate.getKey());
						value.removeAssets(assetValue);
						value.addAssets(assetValue, filterUpdate.getValue());
					}
				}
				value.setImplants(implants);
				value.setSellOrders(sellOrders);
				value.setEscrows(escrows);
				value.setEscrowsToCover(escrowsToCover);
				value.setManufacturing(manufacturing);
				value.setContractCollateral(contractCollateral);
				value.setContractValue(contractValue);
				value.setSkillPoints((long)skillPointValue);
			} finally {
				TrackerData.writeUnlock();
			}
			TrackerData.save("Edited");
			update = true;
			setVisible(false);
		} catch (ParseException ex) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsTracker.get().invalid(), TabsTracker.get().error(), JOptionPane.ERROR_MESSAGE);
		}
	}

	private Double getValue(Double balance) {
		String balanceReturn = JOptionInput.showInputDialog(getDialog(), TabsTracker.get().enterNewValue(), format(balance));
		if (balanceReturn == null) {
			return null; //Cancel
		}
		try {
			return parse(balanceReturn);
		} catch (ParseException ex) {
			JOptionPane.showMessageDialog(getDialog(), TabsTracker.get().invalidNumberMsg(), TabsTracker.get().invalidNumberTitle(), JOptionPane.WARNING_MESSAGE);
			return getValue(balance);
		}
	}

	private class ListenerClass implements ActionListener, FocusListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (TrackerEditAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (TrackerEditAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (TrackerEditAction.EDIT_WALLET.name().equals(e.getActionCommand())) {
				//Create values for selection dialog
				Set<String> ids = new TreeSet<>();
				for (String id : value.getBalanceFilter().keySet()) {
					ids.add(TabsTracker.get().division(id));
				}
				//Select Division
				String returnValue = jSelectionDialog.show(TabsTracker.get().selectDivision(), ids);
				if (returnValue == null) {
					return; //Cancel
				}
				Double balance = null;
				String key = null;
				//Match return value with key and balance
				for (Map.Entry<String, Double> entry : value.getBalanceFilter().entrySet()) {
					if (TabsTracker.get().division(entry.getKey()).equals(returnValue)) {
						balance = entry.getValue();
						key = entry.getKey();
						break; //Item found
					}
				}
				if (balance != null) { //Item found
					balance = getValue(balance); //Get new value
					if (balance != null) { //Update number
						balanceUpdates.add(new FilterUpdate(key, balance)); //Add update to queue (will only be executed if this dialog closed by pressing OK)

						//Update displayed total - only a GUI thing, the textfield is never used when getBalanceFilter is not empty
						Map<String, Double> map = new HashMap<>(value.getBalanceFilter());
						for (FilterUpdate filterUpdate : balanceUpdates) {
							map.put(filterUpdate.getKey(), filterUpdate.getValue());
						}
						double total = 0;
						for (double d : map.values()) {
							total = total + d;
						}
						jWalletBalance.setText(format(total));
					}
				}
			} else if (TrackerEditAction.EDIT_ASSETS.name().equals(e.getActionCommand())) {
				//Create values for selection dialog
				Map<String, Set<String>> values = new TreeMap<>();
				for (AssetValue assetValue : value.getAssetsFilter().keySet()) {
					String location = assetValue.getLocation();
					Set<String> flags = values.get(location);
					if (flags == null) {
						flags = new TreeSet<>();
						values.put(location, flags);
					}
					String flag = assetValue.getFlag();
					if (flag == null) {
						flag = TabsTracker.get().other();
					}
					flags.add(flag);
				}
				//Select Location
				String returnLocation = null;
				if (values.keySet().size() > 1) {
					returnLocation = jSelectionDialog.show(TabsTracker.get().selectLocation(), values.keySet());
				} else { //Size is always 1 (one) or 0 (zero)
					for (String s : values.keySet()) {
						returnLocation = s; //Only done if size is 1 (one) AKA only done once
					}
				}
				if (returnLocation == null) {
					return; //Cancelled or Empty
				}

				//Select Flag
				Set<String> flags = values.get(returnLocation);
				String returnFlag = TabsTracker.get().other(); //Used if size is 1
				if (flags.size() > 1) { //Always contain "Other" flag
					returnFlag = jSelectionDialog.show(TabsTracker.get().selectFlag(), flags);
					if (returnFlag == null) {
						return; //Cancel
					}
				} else if (flags.size() == 1) { //Size is always 1 (one) or 0 (zero)
					for (String s : flags) {
						returnFlag = s; //Only done if size is 1 (one) AKA only done once
					}
				}
				AssetValue assetValue;
				if (returnFlag.equals(TabsTracker.get().other())) {
					assetValue = AssetValue.create(returnLocation);
				} else {
					assetValue = AssetValue.create(returnLocation + " > " + returnFlag);
				}
				Double asset = value.getAssetsFilter().get(assetValue);
				if (asset != null) { //Item found
					asset = getValue(asset); //Get new value
					if (asset != null) { //Update number
						assetUpdates.add(new FilterUpdate(assetValue.getID(), asset)); //Add update to queue (will only be executed if this dialog closed by pressing OK)

						//Update displayed total - only a GUI thing, the textfield is never used when getAssetsFilter is not empty
						Map<String, Double> map = new HashMap<>();
						for (Map.Entry<AssetValue, Double> entry : value.getAssetsFilter().entrySet()) {
							map.put(entry.getKey().getID(), entry.getValue());
						}
						for (FilterUpdate filterUpdate : assetUpdates) {
							map.put(filterUpdate.getKey(), filterUpdate.getValue());
						}
						double total = 0;
						for (double d : map.values()) {
							total = total + d;
						}
						jAssets.setText(format(total));
					}
				}
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
			JTextField jTextField = (JTextField) e.getSource();
			ColorSettings.configReset(jTextField);
		}

		@Override
		public void focusLost(FocusEvent e) {
			JTextField jTextField = (JTextField) e.getSource();
			try {
				parse(jTextField.getText());
				ColorSettings.configReset(jTextField);
			} catch (ParseException ex) {
				ColorSettings.config(jTextField, ColorEntry.GLOBAL_ENTRY_INVALID);
			}
			jTextField.setCaretPosition(jTextField.getText().length());
		}
	}

	private static class FilterUpdate {
		private final String key;
		private final Double value;

		public FilterUpdate(String key, Double value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public Double getValue() {
			return value;
		}
	}
}
