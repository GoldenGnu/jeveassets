/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.PriceDataSettings;


public class PriceDataSettingsPanel extends JSettingsPanel implements ActionListener {

	public final static String ACTION_SOURCE_SELECTED = "ACTION_SOURCE_SELECTED";

	private JComboBox jRegions;
	private JComboBox jPriceType;
	private JComboBox jSource;
	
	public PriceDataSettingsPanel(Program program, SettingsDialog optionsDialog, Icon icon) {
		super(program, optionsDialog, "Price Data", icon);
		JTextArea jWarning = new JTextArea("Note: When changing the source and/or region, the changes doesn't take effect until next time you update the price data.");
		jWarning.setFont(this.getPanel().getFont());
		jWarning.setBackground(this.getPanel().getBackground());
		jWarning.setLineWrap(true);
		jWarning.setWrapStyleWord(true);
		jWarning.setFocusable(false);
		jWarning.setEditable(false);

		JLabel jRegionsLabel = new JLabel("Regions to include:");
		jRegions = new JComboBox();

		JLabel jPriceTypeLabel = new JLabel("Price to use:");
		jPriceType = new JComboBox( new Vector<EveAsset.PriceMode>(EveAsset.getPriceTypes()));

		JLabel jSourceLabel = new JLabel("Source to use:");
		jSource = new JComboBox(PriceDataSettings.SOURCES);
		jSource.setActionCommand(ACTION_SOURCE_SELECTED);
		jSource.addActionListener(this);



		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jRegionsLabel)
						.addComponent(jPriceTypeLabel)
						.addComponent(jSourceLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jRegions)
						.addComponent(jPriceType)
						.addComponent(jSource)
					)
				)
				.addComponent(jWarning)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jSourceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSource, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jRegionsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRegions, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jPriceTypeLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPriceType, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jWarning)
		);
	}

	@Override
	public boolean save() {
		//Get data for GUI
		int region = jRegions.getSelectedIndex();
		EveAsset.PriceMode priceType = (EveAsset.PriceMode) jPriceType.getSelectedItem();
		String source = (String) jSource.getSelectedItem();
		boolean update = !priceType.equals(EveAsset.getPriceType());
		//Create new settings
		PriceDataSettings newPriceDataSettings = new PriceDataSettings(region, source);
		//Set new settings
		program.getSettings().setPriceDataSettings( newPriceDataSettings );
		EveAsset.setPriceType(priceType);
		//Update table if needed
		return update;
	}

	@Override
	public void load(){
		PriceDataSettings priceDataSettings = program.getSettings().getPriceDataSettings();
		jSource.setSelectedItem(priceDataSettings.getSource());
		jRegions.setSelectedIndex(priceDataSettings.getRegion());
		jPriceType.setSelectedItem(EveAsset.getPriceType());
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_SOURCE_SELECTED.equals(e.getActionCommand())){
			String source = (String) jSource.getSelectedItem();
			String region = (String) jRegions.getSelectedItem();
			if (source.equals(PriceDataSettings.SOURCE_EVE_CENTRAL)){
				jRegions.setModel( new DefaultComboBoxModel(PriceDataSettings.REGIONS_EVE_CENTRAL));
			}
			if (source.equals(PriceDataSettings.SOURCE_EVE_METRICS)){
				jRegions.setModel( new DefaultComboBoxModel(PriceDataSettings.REGIONS_EVE_METRICS));
			}
			jRegions.setSelectedItem(region);
		}
	}
}
