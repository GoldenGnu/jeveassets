/*
 * Copyright 2009, Niklas Kyster Rasmussen
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

package net.nikr.eve.jeveasset.gui.dialogs;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.UserPrice;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;


public class PriceSettingsDialog extends JDialogCentered implements ActionListener, FocusListener {

	public final static String ACTION_SAVE = "ACTION_SAVE";
	public final static String ACTION_CANCEL = "ACTION_CANCEL";
	public final static String ACTION_DEFAULT = "ACTION_DEFAULT";
	public final static String ACTION_DELETE = "ACTION_DELETE";
	public final static String ACTION_SELECTED = "ACTION_SELECTED";

	private JComboBox jAssets;
	private JTextField jPrice;
	private JButton jDefault;
	private JButton jSave;
	private JButton jDelete;

	private Map<Integer, UserPrice> userPrices;
	private UserPrice lastUserPrice = null;

	public PriceSettingsDialog(Program program, Image image) {
		super(program, "Price Settings", image);

		JLabel jSetPriceLable = new JLabel("Assets:");

		jAssets = new JComboBox();
		jAssets.setActionCommand(ACTION_SELECTED);
		jAssets.addActionListener(this);
		jPanel.add(jAssets);

		JLabel jPriceLable = new JLabel("Price:");
		jPanel.add(jPriceLable);

		jPrice = new JTextField();
		JCopyPopup.install(jPrice);
		jPrice.setActionCommand(ACTION_SAVE);
		jPrice.addActionListener(this);
		jPrice.addFocusListener(this);
		jPanel.add(jPrice);

		jDelete = new JButton("Delete");
		jDelete.setActionCommand(ACTION_DELETE);
		jDelete.addActionListener(this);
		jPanel.add(jDelete);

		jDefault = new JButton("Default");
		jDefault.setActionCommand(ACTION_DEFAULT);
		jDefault.addActionListener(this);
		jPanel.add(jDefault);

		jSave = new JButton("Save");
		jSave.setActionCommand(ACTION_SAVE);
		jSave.addActionListener(this);
		jPanel.add(jSave);

		JButton jCancel = new JButton("Cancel");
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);
		jPanel.add(jCancel);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jPriceLable)
						.addComponent(jSetPriceLable)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jAssets, 200, 200, 200)
						.addComponent(jPrice, 200, 200, 200)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jDelete, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jDefault, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)

					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jSave, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jSetPriceLable, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDelete, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceLable, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPrice, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDefault, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSave, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private void save(){
		updatePrice();
		//Update Settings
		program.getSettings().setUserPrices(userPrices);
		//Update table
		program.assetsChanged();
		this.setVisible(false);
	}

	private void setEnabledAll(boolean b){
		jAssets.setEnabled(b);
		jPrice.setEnabled(b);
		jDefault.setEnabled(b);
		jDelete.setEnabled(b);
	}

	private void updatePrice(){
		double price;
		try {
			price = Double.valueOf(jPrice.getText());
		} catch(NumberFormatException ex) {
			price = -1;
		}
		if (price >= 0){
			lastUserPrice.setPrice(price);
			//updating last UserPrice
			if (userPrices.containsKey(lastUserPrice.getTypeID())){
				userPrices.remove(lastUserPrice.getTypeID());
			}
			userPrices.put(lastUserPrice.getTypeID(), lastUserPrice);
		}
	}

	private void update(){
		Vector<UserPrice> names = new Vector<UserPrice>();
		lastUserPrice = null;
		for (Map.Entry<Integer,UserPrice> entry : userPrices.entrySet()){
			UserPrice userPrice = entry.getValue();
			names.add(userPrice);
			if (lastUserPrice == null) lastUserPrice = userPrice;

		}
		Collections.sort(names);
		if (!userPrices.isEmpty()){
			jAssets.setModel( new DefaultComboBoxModel(names) );
			jPrice.setText(String.valueOf(lastUserPrice.getPrice()));
			setEnabledAll(true);
		} else {
			jAssets.setModel( new DefaultComboBoxModel() );
			jAssets.addItem("No prices found");
			jPrice.setText("");
			setEnabledAll(false);
		}
		if (program.getSettings().getUserPrices().isEmpty()){
			jSave.setEnabled(false);
		} else {
			jSave.setEnabled(true);
		}
	}

	public void setVisible(boolean b, UserPrice addedUserPrice) {
		if (b){
			userPrices = new HashMap<Integer, UserPrice>();
			if (addedUserPrice != null){
				if (!program.getSettings().getUserPrices().containsKey(addedUserPrice.getTypeID())){
					userPrices.put(addedUserPrice.getTypeID(), addedUserPrice);
				}
				
			}
			for (Map.Entry<Integer,UserPrice> entry : program.getSettings().getUserPrices().entrySet()){
				UserPrice userPrice = new UserPrice(entry.getValue());
				userPrices.put(userPrice.getTypeID(), userPrice);
			}
			update();
			if (addedUserPrice != null){
				jAssets.setSelectedItem(addedUserPrice);
				jSave.setEnabled(true);
			}
		}
		super.setVisible(b);
		
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jPrice;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}
	
	@Override
	public void setVisible(boolean b) {
		setVisible(b, null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			this.setVisible(false);
		}
		if (ACTION_SAVE.equals(e.getActionCommand())){
			save();
		}
		if (ACTION_DEFAULT.equals(e.getActionCommand())){
			Object o = jAssets.getSelectedItem();
			if (o instanceof UserPrice){
				List<EveAsset> assets = program.getSettings().getAllAssets();
				UserPrice userPrice = (UserPrice) o;
				for (int a = 0; a < assets.size(); a++){
					if (assets.get(a).getTypeId() == userPrice.getTypeID()){
						jPrice.setText(String.valueOf(assets.get(a).getPriceSellMedian()) );
						break;
					}
				}
			}
			
		}
		if (ACTION_DELETE.equals(e.getActionCommand())){
			Object o = jAssets.getSelectedItem();
			if (o instanceof UserPrice){
				UserPrice userPrice = (UserPrice) o;
				if (userPrices.containsKey(userPrice.getTypeID())){
					userPrices.remove(userPrice.getTypeID());
				}
			}
			update();
		}
		if (ACTION_SELECTED.equals(e.getActionCommand())){
			Object o = jAssets.getSelectedItem();
			if (o instanceof UserPrice){
				UserPrice userPrice = (UserPrice) o;
				if (userPrices.containsKey(userPrice.getTypeID())){
					userPrice = userPrices.get(userPrice.getTypeID());
				}
				if (!lastUserPrice.equals(userPrice)){
					updatePrice();
					lastUserPrice = userPrice;
				}
				jPrice.setText(String.valueOf(userPrice.getPrice()));
				
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (jPrice.equals(e.getSource())){
			jPrice.selectAll();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		
	}
}
