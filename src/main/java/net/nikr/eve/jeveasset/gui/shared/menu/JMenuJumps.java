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

import ca.odell.glazedlists.GlazedLists;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.JumpType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuJumps<T> extends MenuManager.JAutoMenu<T> {

	private enum MenuJumpsAction {
		ADD_SELECTED, ADD_OTHER, CLEAR
	}

	private final JMenuItem jAddOther;
	private final JMenuItem jAddSelected;
	private final JMenuItem jClear;
	private final Class<T> clazz;

	private MenuData<T> menuData;

	public JMenuJumps(Program program, Class<T> clazz) {
		super(GuiShared.get().jumps(), program);
		this.clazz = clazz;
		ListenerClass listener = new ListenerClass();

		this.setIcon(Images.TOOL_ROUTING.getIcon());

		jAddOther = new JMenuItem(GuiShared.get().jumpsAddCustom());
		jAddOther.setIcon(Images.EDIT_SET.getIcon());
		jAddOther.setActionCommand(MenuJumpsAction.ADD_OTHER.name());
		jAddOther.addActionListener(listener);

		jAddSelected = new JMenuItem(GuiShared.get().jumpsAddSelected());
		jAddSelected.setIcon(Images.EDIT_ADD.getIcon());
		jAddSelected.setActionCommand(MenuJumpsAction.ADD_SELECTED.name());
		jAddSelected.addActionListener(listener);

		jClear = new JMenuItem(GuiShared.get().jumpsClear());
		jClear.setIcon(Images.EDIT_DELETE.getIcon());
		jClear.setActionCommand(MenuJumpsAction.CLEAR.name());
		jClear.addActionListener(listener);
	}

	@Override
	public void setMenuData(MenuData<T> menuData) {
		this.menuData = menuData;

		removeAll();

		add(jAddSelected);
		jAddSelected.setEnabled(!menuData.getSystemLocations().isEmpty());

		add(jAddOther);

		add(jClear);
		jClear.setEnabled(!Settings.get().getJumpLocations(clazz).isEmpty());

		if (!Settings.get().getJumpLocations(clazz).isEmpty()) {
			addSeparator();
		}

		for (final MyLocation location : Settings.get().getJumpLocations(clazz)) {
			//Add to menu
			final JMenuItem jMenuItem = new JMenuItem(location.getSystem()); //FIXME !Jumps i18n - Clear System
			jMenuItem.setIcon(Images.EDIT_DELETE.getIcon());
			jMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//Remove from settings
					Settings.get().removeJumpLocation(clazz, location);
					//Update Data
					updateJumpsData();
					//Remove from asset tab
					removeColumn(location);
					//Update GUI
					updateJumpsGUI();
				}
			});
			add(jMenuItem);
		}
	}

	private void updateJumpsData() {
		program.getProfileData().updateJumps(new ArrayList<JumpType>(program.getProfileData().getAssetsEventList()), MyAsset.class);
		program.getProfileData().updateJumps(new ArrayList<JumpType>(program.getTreeTab().getEventList()), TreeAsset.class);
	}

	private void updateJumpsGUI() {
		program.getTreeTab().tableStructureChanged();
		program.getAssetsTab().tableStructureChanged();
	}

	private void addColumn(MyLocation location) {
		if (TreeAsset.class.isAssignableFrom(clazz)) {
			program.getTreeTab().addColumn(location);
		} else if (MyAsset.class.isAssignableFrom(clazz)) {
			program.getAssetsTab().addColumn(location);
		}
	}

	private void removeColumn(MyLocation location) {
		if (TreeAsset.class.isAssignableFrom(clazz)) {
			program.getTreeTab().removeColumn(location);
		} else if (MyAsset.class.isAssignableFrom(clazz)) {
			program.getAssetsTab().removeColumn(location);
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuJumpsAction.ADD_SELECTED.name().equals(e.getActionCommand())) {
				//Add to settings
				for (final MyLocation location : menuData.getSystemLocations()) {
					Settings.get().addJumpLocation(clazz, location);
				}
				//Update Data
				updateJumpsData();
				//Add to tab
				for (final MyLocation location : menuData.getSystemLocations()) {
					addColumn(location);
				}
				//Update GUI
				updateJumpsGUI();
			}
			if (MenuJumpsAction.CLEAR.name().equals(e.getActionCommand())) {
				//Save locations for removal
				List<MyLocation> locations = new ArrayList<>(Settings.get().getJumpLocations(clazz));
				//Clear settings
				Settings.get().clearJumpLocations(clazz);
				//Update Data
				updateJumpsData();
				//Clear tab
				for (MyLocation location : locations) {
					removeColumn(location);
				}
				//Update GUI
				updateJumpsGUI();
			}
			if (MenuJumpsAction.ADD_OTHER.name().equals(e.getActionCommand())) {
				//Clear tab
				SolarSystem solarSystem = program.getRoutingTab().getSolarSystem();
				if (solarSystem != null) {
					MyLocation location = StaticData.get().getLocations().get(solarSystem.getSystemID());
					if (location != null) {
						//Add to settings
						Settings.get().addJumpLocation(clazz, location);
						//Update Data
						updateJumpsData();
						//Add to tab
						addColumn(location);
						//Update GUI
						updateJumpsGUI();
					}
				}
			}
		}
	}

	public static class Column<Q extends JumpType> implements EnumTableColumn<Q> {

		private final String systemName;
		private final long systemID;

		public Column(String systemName, long systemID) {
			this.systemName = systemName;
			this.systemID = systemID;

		}

		@Override
		public Class<?> getType() {
			return Integer.class;
		}

		@Override
		public Comparator<?> getComparator() {
			return GlazedLists.<Integer>comparableComparator();
		}

		@Override
		public String getColumnName() {
			return systemName;
		}

		@Override
		public Object getColumnValue(Q from) {
			return from.getJumps(systemID);
		}

		@Override
		public String name() {
			return systemName;
		}

		@Override
		public boolean isColumnEditable(Object baseObject) {
			return false;
		}

		@Override
		public boolean isShowDefault() {
			return true;
		}

		@Override
		public boolean setColumnValue(Object baseObject, Object editedValue) {
			return false;
		}

		@Override
		public String toString() {
			return getColumnName();
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 59 * hash + (int) (this.systemID ^ (this.systemID >>> 32));
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Column<?> other = (Column<?>) obj;
			return this.systemID == other.systemID;
		}
	}
}
