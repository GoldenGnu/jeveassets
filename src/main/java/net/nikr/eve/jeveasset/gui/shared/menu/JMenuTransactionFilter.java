/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab.OverviewTableMenu;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsOverview;


public class JMenuTransactionFilter<T> extends JAutoMenu<T> {

	private enum MenuTransactionFilterAction {
		STATION_FILTER,
		PLANET_FILTER,
		SYSTEM_FILTER,
		CONSTELLATION_FILTER,
		REGION_FILTER,
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

	public JMenuTransactionFilter(final Program program) {
		super(GuiShared.get().addTransactionFilter(), program);

		ListenerClass listener = new ListenerClass();

		this.setIcon(Images.TOOL_TRANSACTION.getIcon());

		jTypeID = new JMenuItem(GuiShared.get().item());
		jTypeID.setIcon(Images.EDIT_ADD.getIcon());
		jTypeID.setActionCommand(MenuTransactionFilterAction.ITEM_TYPE_FILTER.name());
		jTypeID.addActionListener(listener);
		add(jTypeID);

		addSeparator();

		jStation = new JMenuItem(GuiShared.get().station());
		jStation.setIcon(Images.LOC_STATION.getIcon());
		jStation.setActionCommand(MenuTransactionFilterAction.STATION_FILTER.name());
		jStation.addActionListener(listener);
		add(jStation);

		jPlanet = new JMenuItem(GuiShared.get().planet());
		jPlanet.setIcon(Images.LOC_PLANET.getIcon());
		jPlanet.setActionCommand(MenuTransactionFilterAction.PLANET_FILTER.name());
		jPlanet.addActionListener(listener);
		add(jPlanet);

		jSystem = new JMenuItem(GuiShared.get().system());
		jSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jSystem.setActionCommand(MenuTransactionFilterAction.SYSTEM_FILTER.name());
		jSystem.addActionListener(listener);
		add(jSystem);

		jConstellation = new JMenuItem(GuiShared.get().constellation());
		jConstellation.setIcon(Images.LOC_CONSTELLATION.getIcon());
		jConstellation.setActionCommand(MenuTransactionFilterAction.CONSTELLATION_FILTER.name());
		jConstellation.addActionListener(listener);
		add(jConstellation);

		jRegion = new JMenuItem(GuiShared.get().region());
		jRegion.setIcon(Images.LOC_REGION.getIcon());
		jRegion.setActionCommand(MenuTransactionFilterAction.REGION_FILTER.name());
		jRegion.addActionListener(listener);
		add(jRegion);

		jLocations = new JMenuItem(TabsOverview.get().locations());
		jLocations.setIcon(Images.LOC_LOCATIONS.getIcon());
		jLocations.setActionCommand(MenuTransactionFilterAction.OVERVIEW_GROUP_FILTER.name());
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

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuTransactionFilterAction.STATION_FILTER.name().equals(e.getActionCommand())) {
				for (String station : menuData.getStationAndCitadelNames()) {
					Filter filter = new Filter(LogicType.AND, TransactionTableFormat.LOCATION, CompareType.EQUALS, station);
					program.getTransactionsTab().addFilter(filter);
				}
				program.getMainWindow().addTab(program.getTransactionsTab());
			} else if (MenuTransactionFilterAction.PLANET_FILTER.name().equals(e.getActionCommand())) {
				for (String planet : menuData.getPlanetNames()) {
					Filter filter = new Filter(LogicType.AND, TransactionTableFormat.LOCATION, CompareType.EQUALS, planet);
					program.getTransactionsTab().addFilter(filter);
				}
				program.getMainWindow().addTab(program.getTransactionsTab());
			} else if (MenuTransactionFilterAction.SYSTEM_FILTER.name().equals(e.getActionCommand())) {
				for (String system : menuData.getSystemNames()) {
					Filter filter = new Filter(LogicType.AND, TransactionTableFormat.SYSTEM, CompareType.EQUALS, system);
					program.getTransactionsTab().addFilter(filter);
				}
				program.getMainWindow().addTab(program.getTransactionsTab());
			} else if (MenuTransactionFilterAction.CONSTELLATION_FILTER.name().equals(e.getActionCommand())) {
				for (String constellation : menuData.getConstellationNames()) {
					Filter filter = new Filter(LogicType.AND, TransactionTableFormat.CONSTELLATION, CompareType.EQUALS, constellation);
					program.getTransactionsTab().addFilter(filter);
				}
				program.getMainWindow().addTab(program.getTransactionsTab());
			} else if (MenuTransactionFilterAction.REGION_FILTER.name().equals(e.getActionCommand())) {
				for (String region : menuData.getRegionNames()) {
					Filter filter = new Filter(LogicType.AND, TransactionTableFormat.REGION, CompareType.EQUALS, region);
					program.getTransactionsTab().addFilter(filter);
				}
				program.getMainWindow().addTab(program.getTransactionsTab());
			} else if (MenuTransactionFilterAction.OVERVIEW_GROUP_FILTER.name().equals(e.getActionCommand())) {
				OverviewGroup overviewGroup = program.getOverviewTab().getSelectGroup();
				if (overviewGroup == null) {
					return;
				}
				List<Filter> filters = new ArrayList<>();
				for (OverviewLocation location : overviewGroup.getLocations()) {
					if (location.isStation()) {
						Filter filter = new Filter(LogicType.OR, TransactionTableFormat.LOCATION, CompareType.EQUALS, location.getName());
						filters.add(filter);
					}
					if (location.isPlanet()) {
						Filter filter = new Filter(LogicType.OR, TransactionTableFormat.LOCATION, CompareType.EQUALS, location.getName());
						filters.add(filter);
					}
					if (location.isSystem()) {
						Filter filter = new Filter(LogicType.OR, TransactionTableFormat.SYSTEM, CompareType.EQUALS, location.getName());
						filters.add(filter);
					}
					if (location.isConstellation()) {
						Filter filter = new Filter(LogicType.OR, TransactionTableFormat.CONSTELLATION, CompareType.EQUALS, location.getName());
						filters.add(filter);
					}
					if (location.isRegion()) {
						Filter filter = new Filter(LogicType.OR, TransactionTableFormat.REGION, CompareType.EQUALS, location.getName());
						filters.add(filter);
					}
				}
				program.getTransactionsTab().addFilters(filters);
				program.getMainWindow().addTab(program.getTransactionsTab());
			} else if (MenuTransactionFilterAction.ITEM_TYPE_FILTER.name().equals(e.getActionCommand())) {
				for (String typeName : menuData.getTypeNames()) {
					Filter filter = new Filter(LogicType.AND, TransactionTableFormat.NAME, CompareType.CONTAINS, typeName);
					program.getTransactionsTab().addFilter(filter);
				}
				program.getMainWindow().addTab(program.getTransactionsTab());
			}
		}
	}
}
