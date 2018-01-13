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
package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.gui.dialogs.update.StructureUpdateDialog;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JSelectionDialog;
import net.nikr.eve.jeveasset.i18n.GuiFrame;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;


public class JMenuLocation<T> extends MenuManager.JAutoMenu<T> {

	private enum MenuLocationAction {
		EDIT,
		CLEAR,
		UPDATE
	}

	private final JMenuItem jUpdate;
	private final JMenuItem jEdit;
	private final JMenuItem jReset;
	private final JSelectionDialog<MyLocation> jLocationDialog;

	private MenuData<T> menuData;

	public JMenuLocation(final Program program) {
		super((GuiShared.get().location()), program);
		setIcon(Images.LOC_LOCATIONS.getIcon());

		ListenerClass listener = new ListenerClass();

		jLocationDialog = new JSelectionDialog<MyLocation>(program);

		jEdit = new JMenuItem(GuiShared.get().itemEdit());
		jEdit.setIcon(Images.EDIT_EDIT.getIcon());
		jEdit.setActionCommand(MenuLocationAction.EDIT.name());
		jEdit.addActionListener(listener);
		add(jEdit);

		addSeparator();

		jUpdate = new JMenuItem(GuiShared.get().updateStructures());
		jUpdate.setIcon(Images.DIALOG_UPDATE.getIcon());
		jUpdate.setActionCommand(MenuLocationAction.UPDATE.name());
		jUpdate.addActionListener(listener);
		add(jUpdate);

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
		jEdit.setEnabled(!menuData.getEditableCitadelLocations().isEmpty());
		jUpdate.setEnabled(!menuData.getEditableCitadelLocations().isEmpty());
		if (StructureUpdateDialog.structuresUpdatable(program)) {
			jUpdate.setIcon(Images.DIALOG_UPDATE.getIcon());
			jUpdate.setToolTipText(GuiFrame.get().updatable());
		} else {
			jUpdate.setIcon(Images.DIALOG_UPDATE_DISABLED.getIcon());
			jUpdate.setToolTipText(GuiFrame.get().not());
		}
		jReset.setEnabled(!menuData.getUserLocations().isEmpty());
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuLocationAction.CLEAR.name().equals(e.getActionCommand())) {
				if (menuData.getUserLocations().size() == 1) { //Single
					MyLocation location = menuData.getUserLocations().iterator().next();
					Long locationID = program.getUserLocationSettingsPanel().deleteLocation(location);
					if (locationID != null) {
						CitadelGetter.remove(locationID);
						program.updateLocations(Collections.singleton(location.getLocationID()));
					}
				} else {
					int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), GuiShared.get().locationClearConfirmAll(menuData.getUserLocations().size()), GuiShared.get().locationClear(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (value == JOptionPane.OK_OPTION) { //All
						Set<Long> locationIDs = new HashSet<>();
						for (MyLocation location : menuData.getUserLocations()) {
							locationIDs.add(location.getLocationID());
						}
						CitadelGetter.remove(locationIDs);
						program.updateLocations(locationIDs);
					} else { //Single
						MyLocation location = jLocationDialog.show(GuiShared.get().locationID(), menuData.getUserLocations());
						Long locationID = program.getUserLocationSettingsPanel().deleteLocation(location);
						if (locationID != null) {
							CitadelGetter.remove(locationID);
							program.updateLocations(Collections.singleton(location.getLocationID()));
						}
					}
				}
			} else if (MenuLocationAction.EDIT.name().equals(e.getActionCommand())) {
				MyLocation renameLocation = jLocationDialog.show(GuiShared.get().locationID(), menuData.getEditableCitadelLocations());
				Citadel citadel = program.getUserLocationSettingsPanel().editLocation(renameLocation);
				if (citadel != null) {
					CitadelGetter.set(citadel);
					program.updateLocations(Collections.singleton(renameLocation.getLocationID()));
				}
			} else if (MenuLocationAction.UPDATE.name().equals(e.getActionCommand())) {
				program.updateStructures(menuData.getEditableCitadelLocations());
			}
		}
	}
}
