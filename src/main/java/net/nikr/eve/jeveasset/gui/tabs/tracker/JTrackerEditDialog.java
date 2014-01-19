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
package net.nikr.eve.jeveasset.gui.tabs.tracker;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.Date;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsTracker;


public class JTrackerEditDialog extends JDialogCentered {

	private enum TrackerEditAction {
		OK,
		CANCEL
	}

	//GUI
	private JTextField jDate;
	private JTextField jWalletBalance;
	private JTextField jAssets;
	private JTextField jSellOrders;
	private JTextField jEscrows;
	private JTextField jEscrowsToCover;
	private JTextField jManufacturing;
	private JButton jOK;

	//Data
	private TrackerData trackerData;
	private boolean update;

	public JTrackerEditDialog(Program program) {
		super(program, TabsTracker.get().edit(), Images.TOOL_TRACKER.getImage());

		ListenerClass listener = new ListenerClass();

		JLabel jDateLabel = new JLabel(TabsTracker.get().date());
		jDate = new JTextField();
		jDate.setEditable(false);
		jDate.setOpaque(false);
		jDate.setHorizontalAlignment(JLabel.RIGHT);

		JLabel jWalletBalanceLabel = new JLabel(TabsTracker.get().walletBalance());
		jWalletBalance = new JTextField();
		jWalletBalance.setHorizontalAlignment(JTextField.RIGHT);
		jWalletBalance.addFocusListener(listener);

		JLabel jAssetsLabel = new JLabel(TabsTracker.get().assets());
		jAssets = new JTextField();
		jAssets.setHorizontalAlignment(JTextField.RIGHT);
		jAssets.addFocusListener(listener);

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
						.addComponent(jSellOrdersLabel)
						.addComponent(jEscrowsLabel)
						.addComponent(jEscrowsToCoverLabel)
						.addComponent(jManufacturingLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jDate, 100, 100, 100)
						.addComponent(jWalletBalance, 100, 100, 100)
						.addComponent(jAssets, 100, 100, 100)
						.addComponent(jSellOrders, 100, 100, 100)
						.addComponent(jEscrows, 100, 100, 100)
						.addComponent(jEscrowsToCover, 100, 100, 100)
						.addComponent(jManufacturing, 100, 100, 100)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jDateLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jWalletBalanceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jWalletBalance, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAssetsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSellOrdersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSellOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jEscrowsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEscrows, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jEscrowsToCoverLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEscrowsToCover, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jManufacturingLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jManufacturing, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public boolean showEdit(TrackerData trackerData) {
		this.trackerData = trackerData;
		update = false;
		jWalletBalance.setText(format(trackerData.getWalletBalance()));
		jAssets.setText(format(trackerData.getAssets()));
		jSellOrders.setText(format(trackerData.getSellOrders()));
		jEscrows.setText(format(trackerData.getEscrows()));
		jEscrowsToCover.setText(format(trackerData.getEscrowsToCover()));
		jManufacturing.setText(format(trackerData.getManufacturing()));
		jDate.setText(format(trackerData.getDate()));
		setVisible(true);
		return update;
	}

	private String format(double d) {
		return Formater.longFormat(d);
	}
	
	private String format(Date d) {
		return Formater.columnDate(d);
	}

	private double parse(String s) throws ParseException {
		return Formater.longParse(s);
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
	protected void windowShown() {
		
	}

	@Override
	protected void save() {
		try {
			double walletBalanc = parse(jWalletBalance.getText());
			double assets = parse(jAssets.getText());
			double sellOrders = parse(jSellOrders.getText());
			double escrows = parse(jEscrows.getText());
			double escrowsToCover = parse(jEscrowsToCover.getText());
			double manufacturing = parse(jManufacturing.getText());
			trackerData.setWalletBalance(walletBalanc);
			trackerData.setAssets(assets);
			trackerData.setSellOrders(sellOrders);
			trackerData.setEscrows(escrows);
			trackerData.setEscrowsToCover(escrowsToCover);
			trackerData.setManufacturing(manufacturing);
			update = true;
			setVisible(false);
		} catch (ParseException ex) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsTracker.get().invalid(), TabsTracker.get().error(), JOptionPane.ERROR_MESSAGE);
		}
	}

	private class ListenerClass implements ActionListener, FocusListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (TrackerEditAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (TrackerEditAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
			JTextField jTextField = (JTextField) e.getSource();
			jTextField.setBackground(Color.WHITE);
		}

		@Override
		public void focusLost(FocusEvent e) {
			JTextField jTextField = (JTextField) e.getSource();
			try {
				parse(jTextField.getText());
				jTextField.setBackground(Color.WHITE);
			} catch (ParseException ex) {
				jTextField.setBackground(new Color(255, 200, 200));
			}
			jTextField.setCaretPosition(jTextField.getText().length());
		}
	}
}
