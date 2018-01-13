/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JLabelMultiline;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.menu.JSystemDialog;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class UserLocationSettingsPanel extends JSettingsPanel {

	private enum UserListAction {
		DELETE, EDIT
	}

	private final JComboBox<MyLocation> jItems;
	private final JButton jEdit;
	private final JButton jDelete;
	private final JSystemDialog jSystemDialog;
	private final Set<Long> delete = new HashSet<Long>();
	private final List<Citadel> edit = new ArrayList<Citadel>();
	private final Map<Long, MyLocation> citadels = new HashMap<Long, MyLocation>();

	public UserLocationSettingsPanel(Program program, SettingsDialog settingsDialog) {
		super(program, settingsDialog, GuiShared.get().location(), Images.LOC_LOCATIONS.getIcon());

		ListenerClass listener = new ListenerClass();

		jSystemDialog = new JSystemDialog(program);

		jItems = new JComboBox<MyLocation>();

		jEdit = new JButton(DialoguesSettings.get().editItem());
		jEdit.setActionCommand(UserListAction.EDIT.name());
		jEdit.addActionListener(listener);

		jDelete = new JButton(DialoguesSettings.get().deleteItem());
		jDelete.setActionCommand(UserListAction.DELETE.name());
		jDelete.addActionListener(listener);

		JLabelMultiline jHelp = new JLabelMultiline(DialoguesSettings.get().locationsInstructions());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jItems)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jEdit, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jDelete, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
				.addComponent(jHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jItems, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jEdit, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDelete, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		);
	}

	@Override
	public boolean save() {
		CitadelGetter.remove(delete);
		CitadelGetter.set(edit);
		return !delete.isEmpty() || !edit.isEmpty();
	}

	@Override
	public void load() {
		delete.clear();
		edit.clear();
		citadels.clear();
		for (Map.Entry<Long, Citadel> entry : CitadelGetter.getAll()) {
			if (entry.getValue().userLocation) {
				citadels.put(entry.getKey(), entry.getValue().getLocation());
			}
		}
		if (citadels.isEmpty()) {
			setEnabledAll(false);
			jItems.setModel(new ListComboBoxModel<MyLocation>());
			jItems.getModel().setSelectedItem(DialoguesSettings.get().itemEmpty());
		} else {
			setEnabledAll(true);
			jItems.setModel(new ListComboBoxModel<MyLocation>(new ArrayList<MyLocation>(new TreeSet<MyLocation>(citadels.values()))));
		}
	}

	private void updateGUI() {
		if (citadels.isEmpty()) {
			setEnabledAll(false);
			jItems.setModel(new ListComboBoxModel<MyLocation>());
			jItems.getModel().setSelectedItem(DialoguesSettings.get().itemEmpty());
		} else {
			setEnabledAll(true);
			Object selectedItem = jItems.getSelectedItem();
			jItems.setModel(new ListComboBoxModel<MyLocation>(new ArrayList<MyLocation>(new TreeSet<MyLocation>(citadels.values()))));
			jItems.setSelectedItem(selectedItem);
		}
	}

	private MyLocation getSelectedItem() {
		Object object = jItems.getSelectedItem();
		if (object != null && object instanceof MyLocation) {
			return (MyLocation)object;
		}
		return null;
	}

	private void setEnabledAll(final boolean b) {
		jItems.setEnabled(b);
		jEdit.setEnabled(b);
		jDelete.setEnabled(b);
	}

	public Long deleteLocation(MyLocation location) {
		if (location == null) { //Cancel
			return null;
		}
		int value2 = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), GuiShared.get().locationClearConfirm(location.getLocation()), GuiShared.get().locationClear(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (value2 != JOptionPane.OK_OPTION) {
			return null;
		}
		return location.getLocationID();
	}


	public Citadel editLocation(MyLocation renameLocation) {
		if (renameLocation == null) { //Cancel
			return null;
		}
		String locationName;
		if (renameLocation.isUserLocation()) { //Input previous value
			locationName = getLocationName(CitadelGetter.get(renameLocation.getLocationID()).getName());
		} else {
			locationName = getLocationName("");
		}

		if (locationName == null) { //Cancel
			return null;
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
			return null;
		}
		return new Citadel(renameLocation.getLocationID(), locationName, system.getSystemID(), system.getSystem(), system.getRegionID(), system.getRegion(), true, true);
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

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (UserListAction.DELETE.name().equals(e.getActionCommand())) {
				MyLocation location = getSelectedItem();
				if (location != null) {
					Long locationID = deleteLocation(location);
					if (locationID != null) {
						delete.add(locationID);
						citadels.remove(locationID);
						updateGUI();
					}
				}
			}
			if (UserListAction.EDIT.name().equals(e.getActionCommand())) {
				MyLocation location = getSelectedItem();
				if (location != null) {
					Citadel citadel = editLocation(location);
					if (citadel != null) {
						edit.add(citadel);
						citadels.put(citadel.id, citadel.getLocation());
						updateGUI();
					}
				}
			}
		}
	}
}
