/* 
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.PriceDataSettings;
import net.nikr.eve.jeveasset.data.PriceDataSettings.FactionPrice;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class PriceDataSettingsPanel extends JSettingsPanel {

	public final static String ACTION_SOURCE_SELECTED = "ACTION_SOURCE_SELECTED";

	
	
	private JComboBox jRegions;
	private JComboBox jPriceType;
	private JComboBox jSource;
	private JComboBox jFaction;
	
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
		jRegions = new JComboBox(PriceDataSettings.REGIONS_EVE_CENTRAL);

		JLabel jPriceTypeLabel = new JLabel(DialoguesSettings.get().price());
		jPriceType = new JComboBox(Asset.getPriceTypes().toArray());

		
		JLabel jSourceLabel = new JLabel(DialoguesSettings.get().source());
		jSource = new JComboBox(PriceDataSettings.SOURCES);
		jSource.setActionCommand(ACTION_SOURCE_SELECTED);
		jSource.addActionListener(new ListenerClass());

		JLabel jFactionLabel = new JLabel(DialoguesSettings.get().faction());
		jFaction = new JComboBox(PriceDataSettings.FactionPrice.values());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jRegionsLabel)
						.addComponent(jPriceTypeLabel)
						.addComponent(jSourceLabel)
						.addComponent(jFactionLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jRegions)
						.addComponent(jPriceType)
						.addComponent(jSource)
						.addComponent(jFaction)
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
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jFactionLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jFaction, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jWarning, 48, 48, 48) //FIXME got not height
		);
	}

	@Override
	public boolean save() {
		//Get Region
		int region = jRegions.getSelectedIndex();
		if (region < 0) region = program.getSettings().getPriceDataSettings().getRegion();

		//Price Type
		Object o = jPriceType.getSelectedItem();
		Asset.PriceMode priceType;
		if (o  instanceof Asset.PriceMode){
			priceType = (Asset.PriceMode) o;
		} else {
			priceType = Asset.getPriceType();
		}

		//Source
		String source = (String) jSource.getSelectedItem();
		
		//Faction Price
		FactionPrice factionPrice = (FactionPrice) jFaction.getSelectedItem();

		//Eval if table need to be updated
		boolean updateTable = !priceType.equals(Asset.getPriceType())
				|| factionPrice != program.getSettings().getPriceDataSettings().getFactionPrice();

		//Update settings
		program.getSettings().setPriceDataSettings( new PriceDataSettings(region, source, factionPrice) );
		Asset.setPriceType(priceType);
		
		//Update table if needed
		return updateTable;
	}

	@Override
	public void load(){
		PriceDataSettings priceDataSettings = program.getSettings().getPriceDataSettings();
		jRegions.setSelectedIndex(priceDataSettings.getRegion());
		jPriceType.setSelectedItem(Asset.getPriceType());
		jSource.setSelectedItem(priceDataSettings.getSource());
		jFaction.setSelectedItem(priceDataSettings.getFactionPrice());
	}

	private class ListenerClass  implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (ACTION_SOURCE_SELECTED.equals(e.getActionCommand())){
				String source = (String) jSource.getSelectedItem();
				if (source.equals(PriceDataSettings.SOURCE_EVE_CENTRAL)){
					jRegions.setSelectedIndex(program.getSettings().getPriceDataSettings().getRegion());
					jPriceType.setSelectedItem(Asset.getPriceType());
					jRegions.setEnabled(true);
					jPriceType.setEnabled(true);
				}
				if (source.equals(PriceDataSettings.SOURCE_EVE_MARKETDATA)){
					jRegions.getModel().setSelectedItem("Not Configurable");
					jPriceType.getModel().setSelectedItem("Not Configurable");
					jRegions.setEnabled(false);
					jPriceType.setEnabled(false);
				}
			}
		}
	}
}
