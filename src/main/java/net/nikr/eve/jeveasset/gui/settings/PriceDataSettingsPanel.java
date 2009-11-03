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

package net.nikr.eve.jeveasset.gui.settings;

import java.util.Date;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.PriceDataSettings;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.JSettingsPanel;


public class PriceDataSettingsPanel extends JSettingsPanel {
	
	private JComboBox jRegions;
	private JComboBox jDefaultPrice;

	private PriceDataSettings oldPriceDataSettings;
	
	public PriceDataSettingsPanel(Program program, JDialogCentered jDialogCentered) {
		super(program, jDialogCentered.getDialog(), "Price Data");

		JLabel jRegionsLabel = new JLabel("Regions to include:");

		jRegions = new JComboBox(PriceDataSettings.REGIONS);

		JLabel jDefaultPriceLabel = new JLabel("Price to use:");
		jDefaultPrice = new JComboBox(EveAsset.getPriceSources());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jRegionsLabel)
						.addComponent(jDefaultPriceLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jRegions)
						.addComponent(jDefaultPrice)
					)
				)

		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jRegionsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRegions, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jDefaultPriceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDefaultPrice, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)

		);
	}

	@Override
	public void save() {
		int region = jRegions.getSelectedIndex();
		String defaultPrice = (String) jDefaultPrice.getSelectedItem();
		if (!defaultPrice.equals(EveAsset.getPriceSource())) program.updateEventList();
		PriceDataSettings newPriceDataSettings = new PriceDataSettings(region, defaultPrice);
		oldPriceDataSettings = program.getSettings().getPriceDataSettings();
		program.getSettings().setPriceDataSettings( newPriceDataSettings );
	}

	@Override
	public void load(){
		PriceDataSettings priceDataSettings = program.getSettings().getPriceDataSettings();
		jRegions.setSelectedIndex(priceDataSettings.getRegion());
		jDefaultPrice.setSelectedItem(EveAsset.getPriceSource());
	}

	@Override
	public void closed() {
		PriceDataSettings newPriceDataSettings = program.getSettings().getPriceDataSettings();
		if (oldPriceDataSettings.equals(newPriceDataSettings)) return;
		Date date = program.getSettings().getPriceDataNextUpdate();
		String nextUpdate = Formater.weekdayAndTime(date)+" GMT";
		if (Settings.isUpdatable(date)){
			int nReturn = JOptionPane.showConfirmDialog(program.getFrame(), "Update price data with the new settings?", "Price Data", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.YES_OPTION){
				program.updatePriceData();
				return;
			}
			nextUpdate = "Now";
		}
		JOptionPane.showMessageDialog(program.getFrame(), "New settings not in use, yet....\r\nYou need to update the price data\r\nbefore the new settings will in use\r\nNext update: "+nextUpdate, "Price Data", JOptionPane.PLAIN_MESSAGE);
	}
}
