/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.gui.images.Images;


public class JMenuAssetFilter extends JMenuTools implements ActionListener {

	private final static String ACTION_ADD_STATION_FILTER = "ACTION_ADD_STATION_FILTER";
	private final static String ACTION_ADD_SYSTEM_FILTER = "ACTION_ADD_SYSTEM_FILTER";
	private final static String ACTION_ADD_REGION_FILTER = "ACTION_ADD_REGION_FILTER";
	private final static String ACTION_ADD_ITEM_TYPE_FILTER = "ACTION_ADD_ITEM_TYPE_FILTER";


	protected JMenuAssetFilter(Arguments arguments) {
		super("Add Asset Filter", Images.ICON_TOOL_ASSETS, arguments);
	}

	@Override
	protected void createMenu() {
		if (typeId != 0){
			jMenuItem = new JMenuItem("Item Type");
			jMenuItem.setIcon(Images.ICON_ADD);
			jMenuItem.setActionCommand(ACTION_ADD_ITEM_TYPE_FILTER);
			jMenuItem.addActionListener(this);
			add(jMenuItem);
		}

		if ((station != null || system != null || region != null) && typeId != 0) this.addSeparator();
		

		if (station != null){
			jMenuItem = new JMenuItem("Station");
			jMenuItem.setIcon(Images.ICON_STATION);
			jMenuItem.setActionCommand(ACTION_ADD_STATION_FILTER);
			jMenuItem.addActionListener(this);
			add(jMenuItem);
		}
		if (system != null){
			jMenuItem = new JMenuItem("System");
			jMenuItem.setIcon(Images.ICON_SYSTEM);
			jMenuItem.setActionCommand(ACTION_ADD_SYSTEM_FILTER);
			jMenuItem.addActionListener(this);
			add(jMenuItem);
		}
		if (region != null){
			jMenuItem = new JMenuItem("Region");
			jMenuItem.setIcon(Images.ICON_REGION);
			jMenuItem.setActionCommand(ACTION_ADD_REGION_FILTER);
			jMenuItem.addActionListener(this);
			add(jMenuItem);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD_STATION_FILTER.equals(e.getActionCommand())){
			AssetFilter assetFilter = new AssetFilter("Location", station, AssetFilter.MODE_EQUALS, true, null);
			program.getAssetsTab().addFilter(assetFilter, true);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_SYSTEM_FILTER.equals(e.getActionCommand())){
			AssetFilter assetFilter = new AssetFilter("Location", system, AssetFilter.MODE_CONTAIN, true, null);
			program.getAssetsTab().addFilter(assetFilter, true);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_REGION_FILTER.equals(e.getActionCommand())){
			AssetFilter assetFilter = new AssetFilter("Region", region, AssetFilter.MODE_EQUALS, true, null);
			program.getAssetsTab().addFilter(assetFilter, true);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_ITEM_TYPE_FILTER.equals(e.getActionCommand())){
			AssetFilter assetFilter = new AssetFilter("Name", typeName, AssetFilter.MODE_EQUALS, true, null);
			program.getAssetsTab().addFilter(assetFilter, true);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
	}
}
