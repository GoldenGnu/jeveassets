/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps.Jump;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuJumps<T extends Enum<T> & EnumTableColumn<Q>, Q> extends JAutoMenu<Q> {

	private enum MenuJumpsAction {
		ADD_SELECTED, ADD_OTHER, CLEAR
	}

	private final JMenuItem jAddOther;
	private final JMenuItem jAddSelected;
	private final JMenuItem jClear;
	private final ColumnManager<T, Q> columnManager;

	public JMenuJumps(Program program, ColumnManager<T, Q> columnManager) {
		super(GuiShared.get().jumps(), program);
		this.columnManager = columnManager;
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
	public void updateMenuData() {
		removeAll();

		add(jAddSelected);
		jAddSelected.setEnabled(!menuData.getSystemLocations().isEmpty());

		add(jAddOther);

		add(jClear);
		
		Set<Jump> locations = columnManager.getJumps();
		
		jClear.setEnabled(!locations.isEmpty());

		if (!locations.isEmpty()) {
			addSeparator();
		}

		for (final Jump jump : locations) {
			//Add to menu
			final JMenuItem jMenuItem = new JMenuItem(jump.getName()); //FIXME !Jumps i18n - Clear System
			jMenuItem.setIcon(Images.EDIT_DELETE.getIcon());
			jMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//Remove from settings
					columnManager.removeColumn(jump);
				}
			});
			add(jMenuItem);
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuJumpsAction.ADD_SELECTED.name().equals(e.getActionCommand())) {
				columnManager.addColumns(menuData.getSystemLocations());
			}
			if (MenuJumpsAction.CLEAR.name().equals(e.getActionCommand())) {
				columnManager.clearJumpColumns();
			}
			if (MenuJumpsAction.ADD_OTHER.name().equals(e.getActionCommand())) {
				//Clear tab
				SolarSystem solarSystem = program.getRoutingTab().getSolarSystem();
				if (solarSystem != null) {
					MyLocation location = StaticData.get().getLocation(solarSystem.getSystemID());
					if (location != null) {
						columnManager.addColumn(new Jump(location));
					}
				}
			}
		}
	}

	public static class Jump {
		private final MyLocation from;
		private final Map<Object, Integer> jumps = new HashMap<>();
		private Integer index;

		public Jump(MyLocation from) {
			this(from, null);
		}

		public Jump(MyLocation from, Integer index) {
			this.from = from;
			this.index = index;
		}

		public String getName() {
			return from.getSystem();
		}

		public long getSystemID() {
			return from.getSystemID();
		}

		public Map<Object, Integer> getJumps() {
			return jumps;
		}

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 89 * hash + (int) (this.from.getSystemID() ^ (this.from.getSystemID() >>> 32));
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Jump other = (Jump) obj;
			if (this.from.getSystemID() != other.from.getSystemID()) {
				return false;
			}
			return true;
		}
	}
}
