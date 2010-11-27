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

package net.nikr.eve.jeveasset.gui.tabs.overview;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.OverviewGroup;
import net.nikr.eve.jeveasset.data.OverviewLocation;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsOverview;


public class OverviewGroupDialog extends JDialogCentered implements ActionListener{

	private final static String ACTION_OK = "ACTION_OK";
	private final static String ACTION_CANCEL = "ACTION_CANCEL";

	private JTextField jName;
	private JLabel jNameLabel;
	private JRadioButton jNone;
	private JRadioButton jStation;
	private JRadioButton jSystem;
	private JRadioButton jRegion;
	private JButton jOK;
	private JButton jCancel;

	private String station;
	private String system;
	private String region;
	private OverviewGroup overviewGroup;
	private OverviewTab overviewTab;

	public OverviewGroupDialog(Program program, OverviewTab overviewTab) {
		super(program, TabsOverview.get().whitespace(), Images.IMAGE_DIALOG_OVERVIEW_GROUPS);
		this.overviewTab = overviewTab;
		
		jNameLabel = new JLabel();

		jNone = new JRadioButton(TabsOverview.get().none());
		jStation = new JRadioButton();
		jSystem = new JRadioButton();
		jRegion = new JRadioButton();

		ButtonGroup bg = new ButtonGroup();
		bg.add(jNone);
		bg.add(jStation);
		bg.add(jSystem);
		bg.add(jRegion);

		jName = new JTextField();

		jOK = new JButton(TabsOverview.get().oK());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);

		jCancel = new JButton(TabsOverview.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				
				.addComponent(jName, 250, 250, Short.MAX_VALUE)
				.addComponent(jNone)
				.addComponent(jSystem)
				.addComponent(jRegion)
				.addComponent(jStation)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jNameLabel, 250, 250, Short.MAX_VALUE)
					//.addComponent(jSelect, 250, 250, Short.MAX_VALUE)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jNameLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jName, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jNone, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jStation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jSystem, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jRegion, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGap(Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public void groupNew(String station, String system, String region){
		show(station, system, region, null);
	}

	public void groupRename(OverviewGroup overviewGroup){
		show(null, null, null, overviewGroup);
	}

	public void groupAdd(String station, String system, String region, OverviewGroup overviewGroup){
		show(station, system, region, overviewGroup);
	}

	private void show(String station, String system, String region, OverviewGroup overviewGroup){
		this.station = station;
		this.system = system;
		this.region = region;
		this.overviewGroup = overviewGroup;
		jNone.setSelected(true);
		if (overviewGroup != null){ //Edit or Rename group
			for (int a = 0; a < overviewGroup.getLocations().size(); a++){
				OverviewLocation location = overviewGroup.getLocations().get(a);
				if (location.getName().equals(station)) jStation.setSelected(true);
				if (location.getName().equals(system)) jSystem.setSelected(true);
				if (location.getName().equals(region)) jRegion.setSelected(true);
			}
			if (station == null && system == null && region == null){
				this.getDialog().setTitle(TabsOverview.get().rename());
				jNameLabel.setText(TabsOverview.get().group());
				jNameLabel.setFont(jName.getFont());
				jName.setText(overviewGroup.getName());
				jName.setVisible(true);
				jName.requestFocusInWindow();
			} else {
				this.getDialog().setTitle(TabsOverview.get().edit());
				jNameLabel.setText(overviewGroup.getName());
				Font font = jName.getFont();
				jNameLabel.setFont( new Font(font.getName(), Font.BOLD, font.getSize()+2));
				jName.setText(overviewGroup.getName());
				jName.setVisible(false);
			}
			
		} else { //Create new group
			this.getDialog().setTitle(TabsOverview.get().new_());
			jNameLabel.setText(TabsOverview.get().group());
			jNameLabel.setFont(jName.getFont());
			jName.setText(TabsOverview.get().whitespace());
			jName.setVisible(true);
			jName.requestFocusInWindow();
		}
		if (station != null){
			jStation.setVisible(true);
			jStation.setText(TabsOverview.get().whitespace1(station));
		} else {
			jStation.setVisible(false);
		}
		if (system != null){
			jSystem.setVisible(true);
			jSystem.setText(TabsOverview.get().whitespace2(system));

		} else {
			jSystem.setVisible(false);
		}
		if (region != null){
			jRegion.setVisible(true);
			jRegion.setText(TabsOverview.get().whitespace3(region));
		} else {
			jRegion.setVisible(false);
		}
		if (station != null || system != null || region != null){
			jNone.setVisible(true);
		} else {
			jNone.setVisible(false);
		}
		this.setVisible(true);
	}

	@Override
	protected JComponent getDefaultFocus() {
		if(jName.isVisible()) return jName;
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {
		
	}

	@Override
	protected void windowActivated() {
		
	}

	@Override
	protected void save() {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())){
			if (overviewGroup == null){ //new group
				//Check for empty name
				if (jName.getText().isEmpty()){
					JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsOverview.get().the(), TabsOverview.get().new_(), JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				//Check for duplicates
				for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
					if (entry.getKey().toLowerCase().equals(jName.getText().toLowerCase())){
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsOverview.get().the1(), TabsOverview.get().new_(), JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				String group = jName.getText();
				overviewGroup = new OverviewGroup(group);
				if (jStation.isSelected()) overviewGroup.add(new OverviewLocation(station, OverviewLocation.LocationType.TYPE_STATION));
				if (jSystem.isSelected()) overviewGroup.add(new OverviewLocation(system, OverviewLocation.LocationType.TYPE_SYSTEM));
				if (jRegion.isSelected()) overviewGroup.add(new OverviewLocation(region, OverviewLocation.LocationType.TYPE_REGION));
				program.getSettings().getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
			} else { //Edit/Rename group
				if (!jName.getText().equals(overviewGroup.getName())){
					if (jName.getText().isEmpty()){
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsOverview.get().the(), TabsOverview.get().rename(), JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					//Check for duplicates
					for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
							//Check if it's just a case change
						if (entry.getKey().toLowerCase().equals(jName.getText().toLowerCase())
										&& !jName.getText().toLowerCase().equals(overviewGroup.getName().toLowerCase())){
							JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsOverview.get().the1(), TabsOverview.get().rename(), JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}
					program.getSettings().getOverviewGroups().remove(overviewGroup.getName());
					overviewGroup.setName(jName.getText());
					program.getSettings().getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
				}
				overviewGroup.remove(new OverviewLocation(station, OverviewLocation.LocationType.TYPE_STATION));
				overviewGroup.remove(new OverviewLocation(system, OverviewLocation.LocationType.TYPE_SYSTEM));
				overviewGroup.remove(new OverviewLocation(region, OverviewLocation.LocationType.TYPE_REGION));
				if (jStation.isSelected()) overviewGroup.add(new OverviewLocation(station, OverviewLocation.LocationType.TYPE_STATION));
				if (jSystem.isSelected()) overviewGroup.add(new OverviewLocation(system, OverviewLocation.LocationType.TYPE_SYSTEM));
				if (jRegion.isSelected()) overviewGroup.add(new OverviewLocation(region, OverviewLocation.LocationType.TYPE_REGION));
			}
			overviewTab.updateTable();
			this.setVisible(false);
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			this.setVisible(false);
		}
	}

}
