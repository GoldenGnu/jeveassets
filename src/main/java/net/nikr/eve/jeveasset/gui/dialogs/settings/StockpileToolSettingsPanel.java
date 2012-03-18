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

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.tree.DefaultMutableTreeNode;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class StockpileToolSettingsPanel extends JSettingsPanel {
		private JCheckBox jSwitchTab;
		private JRadioButton jHalfColors;
		private JRadioButton jDefaultColors;
		
		private final int LABEL_WIDTH = 70;

	public StockpileToolSettingsPanel(Program program, SettingsDialog settingsDialog, DefaultMutableTreeNode parentNode) {
		super(program, settingsDialog, DialoguesSettings.get().stockpile(), Images.TOOL_STOCKPILE.getIcon(), parentNode);

		jSwitchTab = new JCheckBox(DialoguesSettings.get().stockpileSwitchTab());
		
		ButtonGroup group = new ButtonGroup();
		
		jDefaultColors = new JRadioButton();
		group.add(jDefaultColors);
		
		
		JLabel jColors = createLabel(DialoguesSettings.get().stockpileColors(), Color.LIGHT_GRAY, null);
		
		JLabel jDefaultGreen = createLabel(DialoguesSettings.get().stockpile100(), new Color(200,255,200), jDefaultColors);
		
		JLabel jDefaultRed = createLabel(DialoguesSettings.get().stockpile0_100(), new Color(255,200,200), jDefaultColors);
		
		jHalfColors = new JRadioButton();
		group.add(jHalfColors);
		
		JLabel jHalfGreen = createLabel(DialoguesSettings.get().stockpile100(), new Color(200,255,200), jHalfColors);

		JLabel jHalfYellow = createLabel(DialoguesSettings.get().stockpile50_100(), new Color(255,255,200), jHalfColors);
		
		JLabel jHalfRed = createLabel(DialoguesSettings.get().stockpile0_50(), new Color(255,200,200), jHalfColors);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jSwitchTab)
				.addComponent(jColors, 300, 300, Integer.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jDefaultColors)
						.addComponent(jHalfColors)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jDefaultRed, LABEL_WIDTH*2, LABEL_WIDTH*2, Integer.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jHalfRed, LABEL_WIDTH, LABEL_WIDTH, Integer.MAX_VALUE)
							.addComponent(jHalfYellow, LABEL_WIDTH, LABEL_WIDTH, Integer.MAX_VALUE)
						)
					)
					
					.addGroup(layout.createParallelGroup()
						.addComponent(jDefaultGreen, LABEL_WIDTH, LABEL_WIDTH, Integer.MAX_VALUE)
						.addComponent(jHalfGreen, LABEL_WIDTH, LABEL_WIDTH, Integer.MAX_VALUE)
					)
				)
				
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jSwitchTab, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGap(20)
				.addComponent(jColors, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4)
				.addGroup(layout.createParallelGroup()
					
					.addComponent(jDefaultColors, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addGroup(layout.createSequentialGroup()
						.addGap(3)
						.addGroup(layout.createParallelGroup()
							.addComponent(jDefaultGreen, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4)
							.addComponent(jDefaultRed, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4)
						)
					)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jHalfColors, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addGroup(layout.createSequentialGroup()
						.addGap(3)
						.addGroup(layout.createParallelGroup()
							.addComponent(jHalfGreen, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4)
							.addComponent(jHalfYellow, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4)
							.addComponent(jHalfRed, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4, Program.BUTTONS_HEIGHT-4)
						)
					)
				)
		);
	}

	@Override
	public boolean save() {
		boolean updated = program.getSettings().isStockpileHalfColors() != jHalfColors.isSelected();
		program.getSettings().setStockpileFocusTab(jSwitchTab.isSelected());
		program.getSettings().setStockpileHalfColors(jHalfColors.isSelected());
		return updated;
	}

	@Override
	public void load() {
		jSwitchTab.setSelected(program.getSettings().isStockpileFocusTab());
		if (program.getSettings().isStockpileHalfColors()){
			jHalfColors.setSelected(true);
		} else {
			jDefaultColors.setSelected(true);
		}
	}
	
	private JLabel createLabel(String title, Color color, final JRadioButton jRadioButton){
		JLabel jLabel = new JLabel(title);
		jLabel.setOpaque(true);
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		jLabel.setBackground(color);
		jLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		if (jRadioButton != null){
			jLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					jRadioButton.setSelected(true);
				}
			});
		}
		return jLabel;
	}
}
