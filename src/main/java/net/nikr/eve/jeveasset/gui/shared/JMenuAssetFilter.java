/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.tabs.assets.EveAssetTableFormat;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuAssetFilter extends JMenuTool implements ActionListener {

	private final static String ACTION_ADD_STATION_FILTER = "ACTION_ADD_STATION_FILTER";
	private final static String ACTION_ADD_SYSTEM_FILTER = "ACTION_ADD_SYSTEM_FILTER";
	private final static String ACTION_ADD_REGION_FILTER = "ACTION_ADD_REGION_FILTER";
	private final static String ACTION_ADD_ITEM_TYPE_FILTER = "ACTION_ADD_ITEM_TYPE_FILTER";


	public JMenuAssetFilter(Program program, Object object) {
		super(GuiShared.get().add(), program, object);

		this.setIcon(Images.TOOL_ASSETS.getIcon());

		JMenuItem jMenuItem;

		jMenuItem = new JMenuItem(GuiShared.get().item());
		jMenuItem.setIcon(Images.EDIT_ADD.getIcon());
		jMenuItem.setEnabled(typeId != 0);
		jMenuItem.setActionCommand(ACTION_ADD_ITEM_TYPE_FILTER);
		jMenuItem.addActionListener(this);
		add(jMenuItem);

		if ((station != null || system != null || region != null) && typeId != 0) addSeparator();


		jMenuItem = new JMenuItem(GuiShared.get().station());
		jMenuItem.setIcon(Images.LOC_STATION.getIcon());
		jMenuItem.setEnabled(station != null);
		jMenuItem.setActionCommand(ACTION_ADD_STATION_FILTER);
		jMenuItem.addActionListener(this);
		add(jMenuItem);

		jMenuItem = new JMenuItem(GuiShared.get().system());
		jMenuItem.setIcon(Images.LOC_SYSTEM.getIcon());
		jMenuItem.setEnabled(system != null);
		jMenuItem.setActionCommand(ACTION_ADD_SYSTEM_FILTER);
		jMenuItem.addActionListener(this);
		add(jMenuItem);

		jMenuItem = new JMenuItem(GuiShared.get().region());
		jMenuItem.setIcon(Images.LOC_REGION.getIcon());
		jMenuItem.setEnabled(region != null);
		jMenuItem.setActionCommand(ACTION_ADD_REGION_FILTER);
		jMenuItem.addActionListener(this);
		add(jMenuItem);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD_STATION_FILTER.equals(e.getActionCommand())){
			Filter filter = new Filter(LogicType.AND, EveAssetTableFormat.LOCATION, CompareType.EQUALS, station);
			program.getAssetsTab().addFilter(filter);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_SYSTEM_FILTER.equals(e.getActionCommand())){
			Filter filter = new Filter(LogicType.AND, EveAssetTableFormat.LOCATION, CompareType.CONTAINS, system);
			program.getAssetsTab().addFilter(filter);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_REGION_FILTER.equals(e.getActionCommand())){
			Filter filter = new Filter(LogicType.AND, EveAssetTableFormat.REGION, CompareType.EQUALS, region);
			program.getAssetsTab().addFilter(filter);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_ITEM_TYPE_FILTER.equals(e.getActionCommand())){
			Filter filter = new Filter(LogicType.AND, EveAssetTableFormat.NAME, CompareType.CONTAINS, typeName);
			program.getAssetsTab().addFilter(filter);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
	}
}
