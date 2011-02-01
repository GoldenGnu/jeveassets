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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMenuLookup extends JMenuTool implements ActionListener{

	private final static Logger LOG = LoggerFactory.getLogger(JMenuLookup.class);

	private static final String ACTION_BROWSE_EVE_CENTRAL = "ACTION_BROWSE_EVE_CENTRAL";
	private static final String ACTION_BROWSE_EVE_MARKETDATA= "ACTION_BROWSE_EVE_MARKETDATA";
	private static final String ACTION_BROWSE_EVE_MARKETS = "ACTION_BROWSE_EVE_MARKETS";
	private static final String ACTION_BROWSE_GAMES_CHRUKER = "ACTION_BROWSE_GAMES_CHRUKER";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_STATION = "ACTION_BROWSE_EVEMAPS_DOTLAN_STATION";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM = "ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_REGION = "ACTION_BROWSE_EVEMAPS_DOTLAN_REGION";


	public JMenuLookup(Program program, Object object) {
		super("Lookup", program, object);
		
		this.setIcon(Images.ICON_EXTERNAL_LINK);

		JMenuItem menuItem;
		JMenu jSubMenu;

		jSubMenu = new JMenu("Dotlan EveMaps");
		jSubMenu.setIcon(Images.ICON_DOTLAN_EVEMAPS);
		jSubMenu.setEnabled(station != null || system != null || region != null);
		add(jSubMenu);

		menuItem = new JMenuItem("Station");
		menuItem.setIcon(Images.ICON_STATION);
		menuItem.setEnabled(station != null);
		menuItem.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_STATION);
		menuItem.addActionListener(this);
		jSubMenu.add(menuItem);

		menuItem = new JMenuItem("System");
		menuItem.setIcon(Images.ICON_SYSTEM);
		menuItem.setEnabled(system != null);
		menuItem.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM);
		menuItem.addActionListener(this);
		jSubMenu.add(menuItem);

		menuItem = new JMenuItem("Region");
		menuItem.setIcon(Images.ICON_REGION);
		menuItem.setEnabled(region != null);
		menuItem.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_REGION);
		menuItem.addActionListener(this);
		jSubMenu.add(menuItem);

		if ((station != null || system != null || region != null) && typeId != 0) addSeparator();

		menuItem = new JMenuItem("Eve-Central");
		menuItem.setIcon(Images.ICON_EVE_CENTRAL);
		menuItem.setEnabled(typeId != 0 && isMarketGroup);
		menuItem.setActionCommand(ACTION_BROWSE_EVE_CENTRAL);
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem("Eve-Marketdata");
		menuItem.setIcon(Images.ICON_EVE_MARKETDATA);
		menuItem.setEnabled(typeId != 0 && isMarketGroup);
		menuItem.setActionCommand(ACTION_BROWSE_EVE_MARKETDATA);
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem("Eve-Markets");
		menuItem.setIcon(Images.ICON_EVE_MARKETS);
		menuItem.setEnabled(typeId != 0 && isMarketGroup);
		menuItem.setActionCommand(ACTION_BROWSE_EVE_MARKETS);
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem("Chruker Item Database");
		menuItem.setIcon(Images.ICON_CHRUKER);
		menuItem.setEnabled(typeId != 0);
		menuItem.setActionCommand(ACTION_BROWSE_GAMES_CHRUKER);
		menuItem.addActionListener(this);
		add(menuItem);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_BROWSE_EVE_CENTRAL.equals(e.getActionCommand())){
			DesktopUtil.browse("http://www.eve-central.com/home/quicklook.html?typeid="+typeId, program);
		}
		if (ACTION_BROWSE_EVE_MARKETDATA.equals(e.getActionCommand())){
			DesktopUtil.browse("http://eve-marketdata.com/price_check.php?type_id="+typeId, program);
		}
		if (ACTION_BROWSE_EVE_MARKETS.equals(e.getActionCommand())){
			DesktopUtil.browse("http://www.eve-markets.net/detail.php?typeid="+typeId, program);
		}
		if (ACTION_BROWSE_GAMES_CHRUKER.equals(e.getActionCommand())){
			DesktopUtil.browse("http://games.chruker.dk/eve_online/item.php?type_id="+typeId, program);
		}
		if (ACTION_BROWSE_EVEMAPS_DOTLAN_STATION.equals(e.getActionCommand())){
			DesktopUtil.browse("http://evemaps.dotlan.net/outpost/"+station.replace(" ", "_"), program);
		}
		if (ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM.equals(e.getActionCommand())){
			DesktopUtil.browse("http://evemaps.dotlan.net/system/"+system.replace(" ", "_"), program);
		}
		if (ACTION_BROWSE_EVEMAPS_DOTLAN_REGION.equals(e.getActionCommand())){
			DesktopUtil.browse("http://evemaps.dotlan.net/map/"+region.replace(" ", "_"), program);
		}
	}
}
