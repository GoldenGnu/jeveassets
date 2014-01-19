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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab.OverviewAction;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsOverview;


public class JMenuAssetFilter<T> extends JAutoMenu<T> {

	private enum MenuAssetFilterAction {
		STATION_FILTER,
		SYSTEM_FILTER,
		REGION_FILTER,
		ITEM_TYPE_FILTER
	}

	private MenuData<T> menuData;

	private final JMenuItem jTypeID;
	private final JMenuItem jStation;
	private final JMenuItem jSystem;
	private final JMenuItem jRegion;
	private final JMenuItem jLocations;

	public JMenuAssetFilter(final Program program) {
		super(GuiShared.get().addFilter(), program);

		ListenerClass listener = new ListenerClass();

		this.setIcon(Images.TOOL_ASSETS.getIcon());

		jTypeID = new JMenuItem(GuiShared.get().item());
		jTypeID.setIcon(Images.EDIT_ADD.getIcon());
		jTypeID.setActionCommand(MenuAssetFilterAction.ITEM_TYPE_FILTER.name());
		jTypeID.addActionListener(listener);
		add(jTypeID);

		addSeparator();

		jStation = new JMenuItem(GuiShared.get().station());
		jStation.setIcon(Images.LOC_STATION.getIcon());
		jStation.setActionCommand(MenuAssetFilterAction.STATION_FILTER.name());
		jStation.addActionListener(listener);
		add(jStation);

		jSystem = new JMenuItem(GuiShared.get().system());
		jSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jSystem.setActionCommand(MenuAssetFilterAction.SYSTEM_FILTER.name());
		jSystem.addActionListener(listener);
		add(jSystem);

		jRegion = new JMenuItem(GuiShared.get().region());
		jRegion.setIcon(Images.LOC_REGION.getIcon());
		jRegion.setActionCommand(MenuAssetFilterAction.REGION_FILTER.name());
		jRegion.addActionListener(listener);
		add(jRegion);

		jLocations = new JMenuItem(TabsOverview.get().locations());
		jLocations.setIcon(Images.LOC_LOCATIONS.getIcon());
	}

	@Override
	public void setMenuData(MenuData<T> menuData) {
		this.menuData = menuData;
		jTypeID.setEnabled(!menuData.getTypeIDs().isEmpty());
		jStation.setEnabled(!menuData.getStations().isEmpty());
		jSystem.setEnabled(!menuData.getSystems().isEmpty());
		jRegion.setEnabled(!menuData.getRegions().isEmpty());
	}

	public void setTool(Object object) {
		if (object instanceof OverviewTab) {
			OverviewTab overviewTab = (OverviewTab) object;
			//Remove all action listeners
			for (ActionListener listener : jLocations.getActionListeners()) {
				jLocations.removeActionListener(listener);
			}
			jLocations.setActionCommand(OverviewAction.GROUP_ASSET_FILTER.name());
			jLocations.addActionListener(overviewTab.getListenerClass());
			jLocations.setEnabled(overviewTab.isGroupAndNotEmpty());
			this.add(jLocations);
		} else {
			this.remove(jLocations);
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuAssetFilterAction.STATION_FILTER.name().equals(e.getActionCommand())) {
				for (String station : menuData.getStations()) {
					Filter filter = new Filter(LogicType.AND, AssetTableFormat.LOCATION, CompareType.EQUALS, station);
					program.getAssetsTab().addFilter(filter);
				}
				program.getMainWindow().addTab(program.getAssetsTab());
			}
			if (MenuAssetFilterAction.SYSTEM_FILTER.name().equals(e.getActionCommand())) {
				for (String system : menuData.getSystems()) {
					Filter filter = new Filter(LogicType.AND, AssetTableFormat.LOCATION, CompareType.CONTAINS, system);
					program.getAssetsTab().addFilter(filter);
				}
				program.getMainWindow().addTab(program.getAssetsTab());
			}
			if (MenuAssetFilterAction.REGION_FILTER.name().equals(e.getActionCommand())) {
				for (String region : menuData.getRegions()) {
					Filter filter = new Filter(LogicType.AND, AssetTableFormat.REGION, CompareType.EQUALS, region);
					program.getAssetsTab().addFilter(filter);
				}
				program.getMainWindow().addTab(program.getAssetsTab());
			}
			if (MenuAssetFilterAction.ITEM_TYPE_FILTER.name().equals(e.getActionCommand())) {
				for (String typeName : menuData.getTypeNames()) {
					Filter filter = new Filter(LogicType.AND, AssetTableFormat.NAME, CompareType.CONTAINS, typeName);
					program.getAssetsTab().addFilter(filter);
				}
				program.getMainWindow().addTab(program.getAssetsTab());
			}
		}
	}
}
