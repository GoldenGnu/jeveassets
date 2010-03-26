/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.gui.settings;

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
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.UserPrice;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.JSettingsPanel;


public class PriceSettingsPanel extends JSettingsPanel implements ActionListener, FocusListener {

	public final static String ACTION_DEFAULT = "ACTION_DEFAULT";
	public final static String ACTION_DELETE = "ACTION_DELETE";
	public final static String ACTION_SELECTED = "ACTION_SELECTED";

	private JComboBox jAssets;
	private JTextField jPrice;
	private JButton jDefault;
	private JButton jDelete;

	private Map<Integer, UserPrice> userPrices;
	private UserPrice lastUserPrice = null;

	private UserPrice newUserPrice = null;

	public PriceSettingsPanel(Program program, JDialogCentered jDialogCentered) {
		super(program, jDialogCentered.getDialog(), "Price");

		JLabel jSetPriceLable = new JLabel("Assets:");
		jAssets = new JComboBox();
		jAssets.setActionCommand(ACTION_SELECTED);
		jAssets.addActionListener(this);

		JLabel jPriceLable = new JLabel("Price:");
		jPrice = new JTextField();
		JCopyPopup.install(jPrice);
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

		JTextArea jHelp = new JTextArea("\r\nTo add new price:\r\n1. Right click a row in the table\r\n2. Select \"Set Price...\" in the popup menu");
		jHelp.setEditable(false);
		jHelp.setOpaque(false);
		jHelp.setFont(jSetPriceLable.getFont() );

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
				.addComponent(jHelp)

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
				.addComponent(jHelp)
		);
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
		}
		Collections.sort(names);
		if (!userPrices.isEmpty()){
			lastUserPrice = names.get(0);
			jAssets.setModel( new DefaultComboBoxModel(names) );
			jPrice.setText(String.valueOf(lastUserPrice.getPrice()));
			setEnabledAll(true);
		} else {
			jAssets.setModel( new DefaultComboBoxModel() );
			jAssets.addItem("No prices found");
			jPrice.setText("");
			setEnabledAll(false);
		}
	}

	public void setNewPrice(UserPrice newUserPrice){
		this.newUserPrice = newUserPrice;
	}

	@Override
	public void save() {
		updatePrice();
		//Update Settings
		program.getSettings().setUserPrices(userPrices);
		//Update table
		program.updateEventList();
		this.newUserPrice = null;
	}

	@Override
	public void load(){
		//Clear temp settings
		userPrices = new HashMap<Integer, UserPrice>();
		if (newUserPrice != null){
			if (!program.getSettings().getUserPrices().containsKey(newUserPrice.getTypeID())){
				userPrices.put(newUserPrice.getTypeID(), newUserPrice);
			}

		}
		for (Map.Entry<Integer,UserPrice> entry : program.getSettings().getUserPrices().entrySet()){
			UserPrice userPrice = new UserPrice(entry.getValue());
			userPrices.put(userPrice.getTypeID(), userPrice);
		}
		update();
		if (newUserPrice != null){
			jAssets.setSelectedItem(newUserPrice);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_DEFAULT.equals(e.getActionCommand())){
			Object o = jAssets.getSelectedItem();
			if (o instanceof UserPrice){
				List<EveAsset> assets = program.getSettings().getAllAssets();
				UserPrice userPrice = (UserPrice) o;
				for (int a = 0; a < assets.size(); a++){
					if (assets.get(a).getTypeId() == userPrice.getTypeID()){
						jPrice.setText(String.valueOf(assets.get(a).getDefaultPrice()) );
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
