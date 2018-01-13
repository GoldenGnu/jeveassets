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
import java.util.Set;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuRouting<T> extends MenuManager.JAutoMenu<T> {

	private enum MenuRoutingAction {
		SYSTEM,
		STATION
	}
	
	private MenuData<T> menuData;
	private final JMenuItem jSystem;
	private final JMenuItem jStation;

	public JMenuRouting(Program program) {
		super("Routing", program);
		this.setIcon(Images.TOOL_ROUTING.getIcon());

		ListenerClass listener = new ListenerClass();

		jStation = new JMenuItem(GuiShared.get().uiStation());
		jStation.setIcon(Images.LOC_STATION.getIcon());
		jStation.setActionCommand(MenuRoutingAction.STATION.name());
		jStation.addActionListener(listener);
		add(jStation);

		jSystem = new JMenuItem(GuiShared.get().uiSystem());
		jSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jSystem.setActionCommand(MenuRoutingAction.SYSTEM.name());
		jSystem.addActionListener(listener);
		add(jSystem);
	}

	@Override
	public void setMenuData(MenuData<T> menuData) {
		this.menuData = menuData;
		jStation.setEnabled(!menuData.getAutopilotStationLocations().isEmpty() && menuData.getContracts().size() <= 1);
		jSystem.setEnabled(!menuData.getSystemLocations().isEmpty() && menuData.getContracts().size() <= 1);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuRoutingAction.STATION.name().equals(e.getActionCommand())) {
				Set<MyLocation> locations;
				if (menuData.getContracts().size() == 1) {
					MyLocation station = JMenuUI.selectLocation(program, menuData.getAutopilotStationLocations());
					if (station == null) {
						return;
					}
					locations = Collections.singleton(station);
				} else {
					locations = menuData.getAutopilotStationLocations();
				}
				
				program.getMainWindow().addTab(program.getRoutingTab());
				for (MyLocation location : locations) {
					program.getRoutingTab().addLocation(location);
				}
			} else if (MenuRoutingAction.SYSTEM.name().equals(e.getActionCommand())) {
				Set<MyLocation> locations;
				if (menuData.getContracts().size() == 1) {
					MyLocation system = JMenuUI.selectLocation(program, menuData.getSystemLocations());
					if (system == null) {
						return;
					}
					locations = Collections.singleton(system);
				} else {
					locations = menuData.getSystemLocations();
				}
				program.getMainWindow().addTab(program.getRoutingTab());
				for (MyLocation location : locations) {
					program.getRoutingTab().addLocation(location);
				}
			}
		}
	}
	
}
