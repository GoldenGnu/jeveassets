/* 
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Asset.PriceMode;
import net.nikr.eve.jeveasset.data.PriceDataSettings;
import net.nikr.eve.jeveasset.data.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.PriceDataSettings.RegionType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class PriceDataSettingsPanel extends JSettingsPanel {

	public final static String ACTION_SOURCE_SELECTED = "ACTION_SOURCE_SELECTED";

	private JComboBox jRegions;
	private JComboBox jPriceType;
	private JComboBox jSource;
	
	public PriceDataSettingsPanel(Program program, SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().priceData(), Images.SETTINGS_PRICE_DATA.getIcon());
		JTextArea jWarning = new JTextArea(DialoguesSettings.get().changeSourceWarning());
		jWarning.setFont(this.getPanel().getFont());
		jWarning.setBackground(this.getPanel().getBackground());
		jWarning.setLineWrap(true);
		jWarning.setWrapStyleWord(true);
		jWarning.setFocusable(false);
		jWarning.setEditable(false);

		JLabel jRegionsLabel = new JLabel(DialoguesSettings.get().includeRegions());
		jRegions = new JComboBox(RegionType.values());

		JLabel jPriceTypeLabel = new JLabel(DialoguesSettings.get().price());
		jPriceType = new JComboBox(PriceMode.values());
		
		JLabel jSourceLabel = new JLabel(DialoguesSettings.get().source());
		jSource = new JComboBox(PriceSource.values());
		jSource.setActionCommand(ACTION_SOURCE_SELECTED);
		jSource.addActionListener(new ListenerClass());

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
				.addComponent(jWarning, 48, 48, 48)
		);
	}

	@Override
	public boolean save() {
		Object object;
		
		//Get Region (can be a String)
		object = jRegions.getSelectedItem();
		RegionType regionType;
		if (object instanceof RegionType){
			regionType = (RegionType) object;
		} else {
			regionType = program.getSettings().getPriceDataSettings().getRegion();
		}
		
		//Price Type (can be a String)
		object = jPriceType.getSelectedItem();
		PriceMode priceType;
		if (object  instanceof PriceMode){
			priceType = (PriceMode) object;
		} else {
			priceType = Asset.getPriceType();
		}
		
		//Source
		PriceSource source = (PriceSource) jSource.getSelectedItem();
		
		//Eval if table need to be updated
		boolean updateTable = !priceType.equals(Asset.getPriceType());

		//Update settings
		program.getSettings().setPriceDataSettings( new PriceDataSettings(regionType, source) );
		Asset.setPriceType(priceType);
		
		//Update table if needed
		return updateTable;
	}

	@Override
	public void load(){
		jSource.setSelectedItem(program.getSettings().getPriceDataSettings().getSource());
	}
	
	private void updateSource(PriceSource source){
		//Price Types
		jPriceType.setModel(new DefaultComboBoxModel(source.getPriceTypes()));
		jPriceType.setSelectedItem( Asset.getPriceType() );
		if (source.getPriceTypes().length <= 0){ //Empty
			jPriceType.getModel().setSelectedItem(DialoguesSettings.get().notConfigurable());
			jPriceType.setEnabled(false);
		} else {
			jPriceType.setEnabled(true);
		}

		//Regions
		if (source.supportsMultipleLocations()){
			jRegions.setModel( new DefaultComboBoxModel(RegionType.getMultipleLocations()) );
			jRegions.setSelectedItem( program.getSettings().getPriceDataSettings().getRegion() );
			jRegions.setEnabled(true);
		} else if (source.supportsSindleLocations()){
			jRegions.setModel( new DefaultComboBoxModel(RegionType.getSingleLocations()) );
			jRegions.setSelectedItem( program.getSettings().getPriceDataSettings().getRegion() );
			jRegions.setEnabled(true);
		} else {
			jRegions.getModel().setSelectedItem(DialoguesSettings.get().notConfigurable());
			jRegions.setEnabled(false);
		}
	}

	private class ListenerClass implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (ACTION_SOURCE_SELECTED.equals(e.getActionCommand())){
				PriceSource priceSource = (PriceSource) jSource.getSelectedItem();
				updateSource(priceSource);
			}
		}
	}
}
