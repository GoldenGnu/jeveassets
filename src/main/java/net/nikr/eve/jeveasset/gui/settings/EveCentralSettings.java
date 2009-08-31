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

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.MarketstatSettings;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.JSettingsPanel;
import net.nikr.eve.jeveasset.gui.shared.NumberPlainDocument;
import net.nikr.eve.jeveasset.io.EveCentralMarketstatReader;


public class EveCentralSettings extends JSettingsPanel {
	
	private JComboBox jRegions;
	private JTextField jAge;
	private JTextField jQuantity;
	private JComboBox jDefaultPrice;

	private MarketstatSettings oldMarketstatSettings;
	
	public EveCentralSettings(Program program, JDialogCentered jDialogCentered) {
		super(program, jDialogCentered.getDialog(), "Eve-Central");

		JLabel jRegionsLabel = new JLabel("Regions to include:");

		jRegions = new JComboBox(MarketstatSettings.REGIONS);

		JLabel jQuantityLabel = new JLabel("Minimum quantity of orders:");

		jQuantity = new JTextField();
		JCopyPopup.install(jQuantity);
		jQuantity.setDocument( new NumberPlainDocument() );

		JLabel jQuantityUnlimitedLabel = new JLabel("(Zero for no limit)");

		JLabel jAgeLabel = new JLabel("Maximum age of orders (in days):");

		jAge = new JTextField();
		JCopyPopup.install(jAge);
		jAge.setDocument( new NumberPlainDocument() );

		JLabel jDefaultPriceLabel = new JLabel("Price to use:");
		jDefaultPrice = new JComboBox(EveAsset.PRICE_SOURCES);


		JLabel jAgeUnlimitedLabel = new JLabel("(Zero for unlimited)");

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jRegionsLabel)
						.addComponent(jAgeLabel)
						.addComponent(jQuantityLabel)
						.addComponent(jDefaultPriceLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jRegions)
						.addComponent(jDefaultPrice)
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
					.addComponent(jDefaultPriceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDefaultPrice, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)

		);
	}

	@Override
	public void save() {
		int region = jRegions.getSelectedIndex();
		int age = Integer.valueOf(jAge.getText());
		int quantity = Integer.valueOf(jQuantity.getText());
		String defaultPrice = (String) jDefaultPrice.getSelectedItem();
		if (!defaultPrice.equals(EveAsset.getPriceSource())) program.assetsChanged();
		MarketstatSettings newMarketstatSettings = new MarketstatSettings(region, age, quantity, defaultPrice);
		oldMarketstatSettings = program.getSettings().getMarketstatSettings();
		program.getSettings().setMarketstatSettings( newMarketstatSettings );
	}

	@Override
	public void load(){
		MarketstatSettings marketstatSettings = program.getSettings().getMarketstatSettings();
		jRegions.setSelectedIndex(marketstatSettings.getRegion());
		jAge.setText(String.valueOf(marketstatSettings.getAge()) );
		jQuantity.setText(String.valueOf(marketstatSettings.getQuantity()));
		jDefaultPrice.setSelectedItem(EveAsset.getPriceSource());
	}

	@Override
	public void closed() {
		MarketstatSettings newMarketstatSettings = program.getSettings().getMarketstatSettings();
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
}
