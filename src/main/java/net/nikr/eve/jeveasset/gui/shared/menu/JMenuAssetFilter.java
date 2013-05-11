/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsOverview;


public class JMenuAssetFilter<T> extends JAutoMenu<T> implements ActionListener {

	private static final String ACTION_ADD_STATION_FILTER = "ACTION_ADD_STATION_FILTER";
	private static final String ACTION_ADD_SYSTEM_FILTER = "ACTION_ADD_SYSTEM_FILTER";
	private static final String ACTION_ADD_REGION_FILTER = "ACTION_ADD_REGION_FILTER";
	private static final String ACTION_ADD_ITEM_TYPE_FILTER = "ACTION_ADD_ITEM_TYPE_FILTER";

	private Program program;
	private MenuData<T> menuData;

	private final JMenuItem jTypeID;
	private final JMenuItem jStation;
	private final JMenuItem jSystem;
	private final JMenuItem jRegion;
	private final JMenuItem jLocations;

	public JMenuAssetFilter(final Program program) {
		super(GuiShared.get().add());
		this.program = program;

		this.setIcon(Images.TOOL_ASSETS.getIcon());

		jTypeID = new JMenuItem(GuiShared.get().item());
		jTypeID.setIcon(Images.EDIT_ADD.getIcon());
		jTypeID.setActionCommand(ACTION_ADD_ITEM_TYPE_FILTER);
		jTypeID.addActionListener(this);
		add(jTypeID);

		addSeparator();

		jStation = new JMenuItem(GuiShared.get().station());
		jStation.setIcon(Images.LOC_STATION.getIcon());
		jStation.setActionCommand(ACTION_ADD_STATION_FILTER);
		jStation.addActionListener(this);
		add(jStation);

		jSystem = new JMenuItem(GuiShared.get().system());
		jSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jSystem.setActionCommand(ACTION_ADD_SYSTEM_FILTER);
		jSystem.addActionListener(this);
		add(jSystem);

		jRegion = new JMenuItem(GuiShared.get().region());
		jRegion.setIcon(Images.LOC_REGION.getIcon());
		jRegion.setActionCommand(ACTION_ADD_REGION_FILTER);
		jRegion.addActionListener(this);
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
			jLocations.setActionCommand(OverviewTab.ACTION_GROUP_ASSET_FILTER);
			jLocations.addActionListener(overviewTab.getListenerClass());
			jLocations.setEnabled(overviewTab.isGroupAndNotEmpty());
			this.add(jLocations);
		} else {
			this.remove(jLocations);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_ADD_STATION_FILTER.equals(e.getActionCommand())) {
			for (String station : menuData.getStations()) {
				Filter filter = new Filter(LogicType.AND, AssetTableFormat.LOCATION, CompareType.EQUALS, station);
				program.getAssetsTab().addFilter(filter);
			}
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_SYSTEM_FILTER.equals(e.getActionCommand())) {
			for (String system : menuData.getSystems()) {
				Filter filter = new Filter(LogicType.AND, AssetTableFormat.LOCATION, CompareType.CONTAINS, system);
				program.getAssetsTab().addFilter(filter);
			}
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_REGION_FILTER.equals(e.getActionCommand())) {
			for (String region : menuData.getRegions()) {
				Filter filter = new Filter(LogicType.AND, AssetTableFormat.REGION, CompareType.EQUALS, region);
				program.getAssetsTab().addFilter(filter);
			}
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_ITEM_TYPE_FILTER.equals(e.getActionCommand())) {
			for (String typeName : menuData.getTypeNames()) {
				Filter filter = new Filter(LogicType.AND, AssetTableFormat.NAME, CompareType.CONTAINS, typeName);
				program.getAssetsTab().addFilter(filter);
			}
			program.getMainWindow().addTab(program.getAssetsTab());
		}
	}
}
