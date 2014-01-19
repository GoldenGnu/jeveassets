/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.TabsOverview;


public class JOverviewMenu extends JMenu {

	private enum OverviewMenu {
		RENAME,
		DELETE,
		NEW
	}

	private Program program;
	private AddToGroup addToGroup = new AddToGroup();
	private RemoveFromGroup removeFromGroup = new RemoveFromGroup();

	public JOverviewMenu(Program program, OverviewTab overviewTab, List<Overview> selected) {
		super(TabsOverview.get().groups());
		this.program = program;
		this.setIcon(Images.LOC_GROUPS.getIcon());

		ListenerClass listener = new ListenerClass();

		JMenuItem  jMenuItem;
		JCheckBoxMenuItem jCheckBoxMenuItem;

		//Station, System, Region views
		if (!overviewTab.getSelectedView().equals(TabsOverview.get().groups())) {
			jMenuItem = new JMenuItem(TabsOverview.get().add());
			jMenuItem.setIcon(Images.EDIT_ADD.getIcon());
			jMenuItem.setEnabled(!selected.isEmpty());
			jMenuItem.setActionCommand(OverviewMenu.NEW.name());
			jMenuItem.addActionListener(listener);
			this.add(jMenuItem);

			if (!Settings.get().getOverviewGroups().isEmpty()) {
				this.addSeparator();
			}

			for (Map.Entry<String, OverviewGroup> entry : Settings.get().getOverviewGroups().entrySet()) {
				OverviewGroup overviewGroup = entry.getValue();
				boolean found = overviewGroup.getLocations().containsAll(overviewTab.getSelectedLocations());
				jCheckBoxMenuItem = new JCheckBoxMenuItem(overviewGroup.getName());
				if (overviewTab.getSelectedView().equals(TabsOverview.get().stations())) {
					jCheckBoxMenuItem.setIcon(Images.LOC_STATION.getIcon());
				}
				if (overviewTab.getSelectedView().equals(TabsOverview.get().systems())) {
					jCheckBoxMenuItem.setIcon(Images.LOC_SYSTEM.getIcon());
				}
				if (overviewTab.getSelectedView().equals(TabsOverview.get().regions())) {
					jCheckBoxMenuItem.setIcon(Images.LOC_REGION.getIcon());
				}
				jCheckBoxMenuItem.setEnabled(!selected.isEmpty());
				jCheckBoxMenuItem.setActionCommand(overviewGroup.getName());
				jCheckBoxMenuItem.addActionListener(addToGroup);
				jCheckBoxMenuItem.setSelected(found);
				this.add(jCheckBoxMenuItem);
			}
		}
		//Groups view
		if (overviewTab.getSelectedView().equals(TabsOverview.get().groups())) {
			jMenuItem = new JMenuItem(TabsOverview.get().renameGroup());
			jMenuItem.setIcon(Images.EDIT_RENAME.getIcon());
			jMenuItem.setEnabled(selected.size() == 1);
			jMenuItem.setActionCommand(OverviewMenu.RENAME.name());
			jMenuItem.addActionListener(listener);
			this.add(jMenuItem);

			jMenuItem = new JMenuItem(TabsOverview.get().deleteGroup());
			jMenuItem.setIcon(Images.EDIT_DELETE.getIcon());
			jMenuItem.setEnabled(selected.size() == 1);
			jMenuItem.setActionCommand(OverviewMenu.DELETE.name());
			jMenuItem.addActionListener(listener);
			this.add(jMenuItem);

			if (selected.size() == 1) { //Add the group locations
				Overview overview = selected.get(0);
				OverviewGroup overviewGroup = Settings.get().getOverviewGroups().get(overview.getName());
				if (overviewGroup != null) {
					if (!overviewGroup.getLocations().isEmpty()) {
						this.addSeparator();
					}
					for (OverviewLocation location : overviewGroup.getLocations()) {
						jCheckBoxMenuItem = new JCheckBoxMenuItem(location.getName());
						if (location.isStation()) {
							jCheckBoxMenuItem.setIcon(Images.LOC_STATION.getIcon());
						}
						if (location.isSystem()) {
							jCheckBoxMenuItem.setIcon(Images.LOC_SYSTEM.getIcon());
						}
						if (location.isRegion()) {
							jCheckBoxMenuItem.setIcon(Images.LOC_REGION.getIcon());
						}
						jCheckBoxMenuItem.setActionCommand(location.getName());
						jCheckBoxMenuItem.addActionListener(removeFromGroup);
						jCheckBoxMenuItem.setSelected(true);
						this.add(jCheckBoxMenuItem);
					}
				}
			}
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Group
			if (OverviewMenu.NEW.name().equals(e.getActionCommand())) {
				String value = JOptionPane.showInputDialog(program.getMainWindow().getFrame(), TabsOverview.get().groupName(), TabsOverview.get().addGroup(), JOptionPane.PLAIN_MESSAGE);
				if (value != null) {
					OverviewGroup overviewGroup = new OverviewGroup(value);
					Settings.get().getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
					overviewGroup.addAll(program.getOverviewTab().getSelectedLocations());
					program.getOverviewTab().updateTable();
				}
			}
			if (OverviewMenu.DELETE.name().equals(e.getActionCommand())) {
				OverviewGroup overviewGroup = program.getOverviewTab().getSelectGroup();
				int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsOverview.get().deleteTheGroup(overviewGroup.getName()), TabsOverview.get().deleteGroup(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (value == JOptionPane.OK_OPTION) {
					Settings.get().getOverviewGroups().remove(overviewGroup.getName());
					program.getOverviewTab().updateTable();
				}
			}
			if (OverviewMenu.RENAME.name().equals(e.getActionCommand())) {
				OverviewGroup overviewGroup = program.getOverviewTab().getSelectGroup();
				String value = (String) JOptionPane.showInputDialog(program.getMainWindow().getFrame(), TabsOverview.get().groupName(), TabsOverview.get().renameGroup(), JOptionPane.PLAIN_MESSAGE, null, null, overviewGroup.getName());
				if (value != null) {
					Settings.get().getOverviewGroups().remove(overviewGroup.getName());
					overviewGroup.setName(value);
					Settings.get().getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
					program.getOverviewTab().updateTable();
				}
			}
		}
	}

	class AddToGroup implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			OverviewGroup overviewGroup = Settings.get().getOverviewGroups().get(e.getActionCommand());
			if (overviewGroup != null) {
				List<OverviewLocation> locations = program.getOverviewTab().getSelectedLocations();
				if (overviewGroup.getLocations().containsAll(locations)) {
					overviewGroup.removeAll(locations);
				} else { //Remove
					overviewGroup.addAll(locations);
				}
				program.getOverviewTab().updateTable();
			}
		}
	}

	class RemoveFromGroup implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			OverviewGroup overviewGroup = program.getOverviewTab().getSelectGroup();
			String location = e.getActionCommand();
			overviewGroup.remove(new OverviewLocation(location));
			program.getOverviewTab().updateTable();
		}
	}
	
}
