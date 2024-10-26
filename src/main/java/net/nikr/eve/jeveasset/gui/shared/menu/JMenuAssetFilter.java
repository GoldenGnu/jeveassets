/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
import java.util.List;
import java.util.Set;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab.OverviewTableMenu;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsOverview;


public class JMenuAssetFilter<T> extends JAutoMenu<T> {

	private enum MenuAssetFilterAction {
		STATION_FILTER,
		PLANET_FILTER,
		SYSTEM_FILTER,
		REGION_FILTER,
		CONSTELLATION_FILTER,
		OVERVIEW_GROUP_FILTER,
		ITEM_TYPE_FILTER
	}

	private final JMenuItem jTypeID;
	private final JMenuItem jPlanet;
	private final JMenuItem jStation;
	private final JMenuItem jSystem;
	private final JMenuItem jConstellation;
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

		jPlanet = new JMenuItem(GuiShared.get().planet());
		jPlanet.setIcon(Images.LOC_PLANET.getIcon());
		jPlanet.setActionCommand(MenuAssetFilterAction.PLANET_FILTER.name());
		jPlanet.addActionListener(listener);
		add(jPlanet);

		jSystem = new JMenuItem(GuiShared.get().system());
		jSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jSystem.setActionCommand(MenuAssetFilterAction.SYSTEM_FILTER.name());
		jSystem.addActionListener(listener);
		add(jSystem);

		jConstellation = new JMenuItem(GuiShared.get().constellation());
		jConstellation.setIcon(Images.LOC_CONSTELLATION.getIcon());
		jConstellation.setActionCommand(MenuAssetFilterAction.CONSTELLATION_FILTER.name());
		jConstellation.addActionListener(listener);
		add(jConstellation);

		jRegion = new JMenuItem(GuiShared.get().region());
		jRegion.setIcon(Images.LOC_REGION.getIcon());
		jRegion.setActionCommand(MenuAssetFilterAction.REGION_FILTER.name());
		jRegion.addActionListener(listener);
		add(jRegion);

		jLocations = new JMenuItem(TabsOverview.get().locations());
		jLocations.setIcon(Images.LOC_LOCATIONS.getIcon());
		jLocations.setActionCommand(MenuAssetFilterAction.OVERVIEW_GROUP_FILTER.name());
		jLocations.addActionListener(listener);
	}

	@Override
	public void updateMenuData() {
		jTypeID.setEnabled(!menuData.getTypeIDs().isEmpty());
		jStation.setEnabled(!menuData.getStationAndCitadelNames().isEmpty());
		jPlanet.setEnabled(!menuData.getPlanetNames().isEmpty());
		jSystem.setEnabled(!menuData.getSystemNames().isEmpty());
		jConstellation.setEnabled(!menuData.getConstellationNames().isEmpty());
		jRegion.setEnabled(!menuData.getRegionNames().isEmpty());
	}

	public void setTool(Object object) {
		if (object instanceof OverviewTableMenu) {
			OverviewTab overviewTab = ((OverviewTableMenu)object).getOverviewTab();
			jLocations.setEnabled(overviewTab.isGroupAndNotEmpty());
			add(jLocations);
		} else {
			remove(jLocations);
		}
	}

	public static <T extends Enum<T> & EnumTableColumn<Q>, Q> List<Filter> getFilters(Set<String> names, T column, CompareType compareType) {
		List<Filter> filters = new ArrayList<>();
		boolean and = names.size() < 2;
		for (String name : names) {
			filters.add(new Filter(and ? LogicType.AND : LogicType.OR, column, compareType, name));
		}
		return filters;
	}

	private void addFilters(Set<String> names, AssetTableFormat column, CompareType compareType) {
		List<Filter> filters = getFilters(names, column, compareType);
		program.getAssetsTab().addFilters(filters);
		program.getMainWindow().addTab(program.getAssetsTab());
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuAssetFilterAction.STATION_FILTER.name().equals(e.getActionCommand())) {
				addFilters(menuData.getStationAndCitadelNames(), AssetTableFormat.LOCATION, CompareType.EQUALS);
			} else if (MenuAssetFilterAction.PLANET_FILTER.name().equals(e.getActionCommand())) {
				addFilters(menuData.getPlanetNames(), AssetTableFormat.LOCATION, CompareType.EQUALS);
			} else if (MenuAssetFilterAction.SYSTEM_FILTER.name().equals(e.getActionCommand())) {
				addFilters(menuData.getSystemNames(), AssetTableFormat.SYSTEM, CompareType.EQUALS);
			} else if (MenuAssetFilterAction.CONSTELLATION_FILTER.name().equals(e.getActionCommand())) {
				addFilters(menuData.getConstellationNames(), AssetTableFormat.CONSTELLATION, CompareType.EQUALS);
			} else if (MenuAssetFilterAction.REGION_FILTER.name().equals(e.getActionCommand())) {
				addFilters(menuData.getRegionNames(), AssetTableFormat.REGION, CompareType.EQUALS);
			} else if (MenuAssetFilterAction.OVERVIEW_GROUP_FILTER.name().equals(e.getActionCommand())) {
				OverviewGroup overviewGroup = program.getOverviewTab(true).getSelectGroup();
				if (overviewGroup == null) {
					return;
				}
				List<Filter> filters = new ArrayList<>();
				for (OverviewLocation location : overviewGroup.getLocations()) {
					if (location.isStation()) {
						filters.add(new Filter(LogicType.OR, AssetTableFormat.LOCATION, CompareType.EQUALS, location.getName()));
					}
					if (location.isPlanet()) {
						filters.add(new Filter(LogicType.OR, AssetTableFormat.LOCATION, CompareType.EQUALS, location.getName()));
					}
					if (location.isSystem()) {
						filters.add(new Filter(LogicType.OR, AssetTableFormat.SYSTEM, CompareType.EQUALS, location.getName()));
					}
					if (location.isConstellation()) {
						filters.add(new Filter(LogicType.OR, AssetTableFormat.CONSTELLATION, CompareType.EQUALS, location.getName()));
					}
					if (location.isRegion()) {
						filters.add(new Filter(LogicType.OR, AssetTableFormat.REGION, CompareType.EQUALS, location.getName()));
					}
				}
				program.getAssetsTab().addFilters(filters);
				program.getMainWindow().addTab(program.getAssetsTab());
			} else if (MenuAssetFilterAction.ITEM_TYPE_FILTER.name().equals(e.getActionCommand())) {
				addFilters(menuData.getTypeNames(), AssetTableFormat.NAME, CompareType.CONTAINS);
			}
		}
	}
}
