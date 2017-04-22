/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Citadel;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class JMenuLocation<T> extends MenuManager.JAutoMenu<T> {

	private enum MenuLocationAction {
		EDIT,
		CLEAR
	}
	private final JMenuItem jEdit;
	private final JMenuItem jReset;
	private final JSelectionDialog<MyLocation> jLocationDialog;
	private final JSystemDialog jSystemDialog;

	private MenuData<T> menuData;

	public JMenuLocation(final Program program) {
		super((GuiShared.get().location()), program);
		setIcon(Images.LOC_LOCATIONS.getIcon());

		ListenerClass listener = new ListenerClass();

		jLocationDialog = new JSelectionDialog<MyLocation>(program, GuiShared.get().locationRename(), GuiShared.get().locationID());
		jSystemDialog = new JSystemDialog(program);

		jEdit = new JMenuItem(GuiShared.get().itemEdit());
		jEdit.setIcon(Images.EDIT_EDIT.getIcon());
		jEdit.setActionCommand(MenuLocationAction.EDIT.name());
		jEdit.addActionListener(listener);
		add(jEdit);

		addSeparator();

		jReset = new JMenuItem(GuiShared.get().itemDelete());
		jReset.setIcon(Images.EDIT_DELETE.getIcon());
		jReset.setActionCommand(MenuLocationAction.CLEAR.name());
		jReset.addActionListener(listener);
		add(jReset);
	}

	
	
	@Override
	public void setMenuData(MenuData<T> menuData) {
		this.menuData = menuData;
		jEdit.setEnabled(!menuData.getEmptyStations().isEmpty() || !menuData.getUserStations().isEmpty());
		jReset.setEnabled(!menuData.getUserStations().isEmpty());
		
	}

	private String getLocationName(String text) {
		text = (String) JOptionPane.showInputDialog(program.getMainWindow().getFrame(), GuiShared.get().locationName(), GuiShared.get().locationRename(), JOptionPane.PLAIN_MESSAGE, null, null, text);
		if (text == null) {
			return null;
		}
		if (text.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().locationEmpty(), GuiShared.get().locationRename(), JOptionPane.WARNING_MESSAGE);
			return getLocationName(text);
		}
		return text;
	}

	private void deleteLocation(MyLocation location) {
		Citadel citadel = new Citadel();
		citadel.id = location.getLocationID();
		CitadelGetter.set(citadel);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuLocationAction.CLEAR.name().equals(e.getActionCommand())) {
				if (menuData.getUserStations().size() == 1) { //Single
					MyLocation location = menuData.getUserStations().iterator().next();
					int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), GuiShared.get().locationClearConfirm(location.getLocation()), GuiShared.get().locationClear(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (value == JOptionPane.OK_OPTION) {
						deleteLocation(location);
						program.updateEventLists();
					}
				} else {
					int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), GuiShared.get().locationClearConfirmAll(menuData.getUserStations().size()), GuiShared.get().locationClear(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (value == JOptionPane.OK_OPTION) { //All
						for (MyLocation location : menuData.getUserStations()) {
							deleteLocation(location);
						}
						program.updateEventLists();
					} else { //Single
						MyLocation location = jLocationDialog.showDialog(menuData.getUserStations());
						if (location == null) { //Cancel
							return;
						}
						int value2 = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), GuiShared.get().locationClearConfirm(location.getLocation()), GuiShared.get().locationClear(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
						if (value2 == JOptionPane.OK_OPTION) {
							deleteLocation(location);
							program.updateEventLists();
						}
					}
				}
			} else if (MenuLocationAction.EDIT.name().equals(e.getActionCommand())) {
				Set<MyLocation> emptyAndUserStations = new HashSet<MyLocation>();
				emptyAndUserStations.addAll(menuData.getEmptyStations());
				emptyAndUserStations.addAll(menuData.getUserStations());
				MyLocation renameLocation = jLocationDialog.showDialog(emptyAndUserStations);
				if (renameLocation == null) { //Cancel
					return;
				}
				String locationName;
				if (renameLocation.isUserLocation()) { //Input previous value
					locationName = getLocationName(CitadelGetter.get(renameLocation.getLocationID()).getName());
				} else {
					locationName = getLocationName("");
				}
				
				if (locationName == null) { //Cancel
					return;
				}
				//Create data for the system dialog
				List<MyLocation> locations = new ArrayList<MyLocation>(); 
				for (MyLocation system : StaticData.get().getLocations().values()) {
					if (system.isSystem()) {
						locations.add(system);
					}
				}
				jSystemDialog.updateData(locations);
				MyLocation system;
				if (renameLocation.isUserLocation()) { //Input previous value
					MyLocation renameSystem = ApiIdConverter.getLocation(renameLocation.getSystemID());
					system = jSystemDialog.show(renameSystem);
				} else {
					system = jSystemDialog.show();
				}
				if (system == null) { //Cancel
					return;
				}
				Citadel citadel = new Citadel(renameLocation.getLocationID(), locationName, system.getSystemID(), system.getSystem(), system.getRegionID(), system.getRegion());
				CitadelGetter.set(citadel);
				program.updateEventLists();
			}
		}
	}
}
