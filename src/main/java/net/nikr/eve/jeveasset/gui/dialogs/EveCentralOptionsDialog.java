/*
 * Copyright 2009
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

package net.nikr.eve.jeveasset.gui.dialogs;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.MarketstatSettings;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.NumberPlainDocument;
import net.nikr.eve.jeveasset.io.EveCentralMarketstatReader;


public class EveCentralOptionsDialog extends JDialogCentered implements ActionListener {

	public final static String ACTION_CANCEL = "ACTION_CANCEL";
	public final static String ACTION_SAVE = "ACTION_SAVE";

	private JComboBox jRegions;
	private JTextField jAge;
	private JTextField jQuantity;
	private JButton jSave;

	public EveCentralOptionsDialog(Program program, Image image) {
		super(program, "Eve-Central Options", image);

		JLabel jRegionsLabel = new JLabel("Regions to include:");

		jRegions = new JComboBox(MarketstatSettings.REGIONS);
		jPanel.add(jRegions);

		JLabel jQuantityLabel = new JLabel("Minimum quantity of orders:");

		jQuantity = new JTextField();
		JCopyPopup.install(jQuantity);
		jQuantity.setDocument( new NumberPlainDocument() );
		jPanel.add(jQuantity);

		JLabel jQuantityUnlimitedLabel = new JLabel("(Zero for no limit)");

		JLabel jAgeLabel = new JLabel("Maximum age of orders (in days):");

		jAge = new JTextField();
		JCopyPopup.install(jAge);
		jAge.setDocument( new NumberPlainDocument() );
		jPanel.add(jAge);

		JLabel jAgeUnlimitedLabel = new JLabel("(Zero for unlimited)");

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
						.addComponent(jRegionsLabel)
						.addComponent(jAgeLabel)
						.addComponent(jQuantityLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jRegions)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(jQuantity, 70, 70, 70)
								.addComponent(jAge, 70, 70, 70)
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(jQuantityUnlimitedLabel)
								.addComponent(jAgeUnlimitedLabel)
							)
						)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jSave, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)

		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jRegionsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRegions, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jAgeLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAge, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAgeUnlimitedLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jQuantityLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jQuantity, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jQuantityUnlimitedLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jSave, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private void updateValues(){
		MarketstatSettings marketstatSettings = program.getSettings().getMarketstatSettings();
		jRegions.setSelectedIndex(marketstatSettings.getRegion());
		jAge.setText(String.valueOf(marketstatSettings.getAge()) );
		jQuantity.setText(String.valueOf(marketstatSettings.getQuantity()));
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jRegions;
	}

	@Override
	protected JButton getDefaultButton() {
		return jSave;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		int region = jRegions.getSelectedIndex();
		int age = Integer.valueOf(jAge.getText());
		int quantity = Integer.valueOf(jQuantity.getText());
		MarketstatSettings newMarketstatSettings = new MarketstatSettings(region, age, quantity);
		MarketstatSettings oldMarketstatSettings = program.getSettings().getMarketstatSettings();
		program.getSettings().setMarketstatSettings( newMarketstatSettings );

		this.setVisible(false);

		if (oldMarketstatSettings.equals(newMarketstatSettings)) return;

		String nextUpdate = Formater.weekdayAndTime(program.getSettings().getMarketstatsNextUpdate())+" GMT";
		if (EveCentralMarketstatReader.isMarketstatUpdatable(program.getSettings())){
			int nReturn = JOptionPane.showConfirmDialog(program.getFrame(), "Update price data from Eve-Central, with the new settings?", "Update prices", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.YES_OPTION){
				program.updatePriceData();
				return;
			}
			nextUpdate = "Now";
		}
		JOptionPane.showMessageDialog(program.getFrame(), "New settings not in use, yet....\r\nYou need to update the price data from EVE-Central\r\nbefore the new settings will in used\r\nNext update: "+nextUpdate, "Eve-Central Options", JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			updateValues();
		}
		super.setVisible(b);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			this.setVisible(false);
		}
		if (ACTION_SAVE.equals(e.getActionCommand())){
			save();
		}
	}



}
