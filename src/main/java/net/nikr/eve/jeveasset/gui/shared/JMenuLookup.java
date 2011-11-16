/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;


public class JMenuLookup extends JMenuTool implements ActionListener{

	private static final String ACTION_BROWSE_EVE_CENTRAL = "ACTION_BROWSE_EVE_CENTRAL";
	private static final String ACTION_BROWSE_EVE_MARKETDATA= "ACTION_BROWSE_EVE_MARKETDATA";
	private static final String ACTION_BROWSE_EVE_MARKETS = "ACTION_BROWSE_EVE_MARKETS";
	private static final String ACTION_BROWSE_GAMES_CHRUKER = "ACTION_BROWSE_GAMES_CHRUKER";
	private static final String ACTION_BROWSE_EVE_ITEM_DATABASE = "ACTION_BROWSE_EVE_ITEM_DATABASE";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_STATION = "ACTION_BROWSE_EVEMAPS_DOTLAN_STATION";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM = "ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_REGION = "ACTION_BROWSE_EVEMAPS_DOTLAN_REGION";


	public JMenuLookup(Program program, Object object) {
		super(GuiShared.get().lookup(), program, object);
		
		this.setIcon(Images.LINK_LOOKUP.getIcon());

		JMenuItem menuItem;
		JMenu jSubMenu;

		jSubMenu = new JMenu(GuiShared.get().dotlan());
		jSubMenu.setIcon(Images.LINK_DOTLAN_EVEMAPS.getIcon());
		jSubMenu.setEnabled(station != null || system != null || region != null);
		add(jSubMenu);

		menuItem = new JMenuItem(GuiShared.get().station());
		menuItem.setIcon(Images.LOC_STATION.getIcon());
		menuItem.setEnabled(station != null);
		menuItem.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_STATION);
		menuItem.addActionListener(this);
		jSubMenu.add(menuItem);

		menuItem = new JMenuItem(GuiShared.get().system());
		menuItem.setIcon(Images.LOC_SYSTEM.getIcon());
		menuItem.setEnabled(system != null);
		menuItem.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM);
		menuItem.addActionListener(this);
		jSubMenu.add(menuItem);

		menuItem = new JMenuItem(GuiShared.get().region());
		menuItem.setIcon(Images.LOC_REGION.getIcon());
		menuItem.setEnabled(region != null);
		menuItem.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_REGION);
		menuItem.addActionListener(this);
		jSubMenu.add(menuItem);

		addSeparator();

		menuItem = new JMenuItem(GuiShared.get().eveCentral());
		menuItem.setIcon(Images.LINK_EVE_CENTRAL.getIcon());
		menuItem.setEnabled(typeId != 0 && isMarketGroup);
		menuItem.setActionCommand(ACTION_BROWSE_EVE_CENTRAL);
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem(GuiShared.get().eveMarketdata());
		menuItem.setIcon(Images.LINK_EVE_MARKETDATA.getIcon());
		menuItem.setEnabled(typeId != 0 && isMarketGroup);
		menuItem.setActionCommand(ACTION_BROWSE_EVE_MARKETDATA);
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem(GuiShared.get().eveMarkets());
		menuItem.setIcon(Images.LINK_EVE_MARKETS.getIcon());
		menuItem.setEnabled(typeId != 0 && isMarketGroup);
		menuItem.setActionCommand(ACTION_BROWSE_EVE_MARKETS);
		menuItem.addActionListener(this);
		add(menuItem);

		addSeparator();

		menuItem = new JMenuItem(GuiShared.get().chruker());
		menuItem.setIcon(Images.LINK_CHRUKER.getIcon());
		menuItem.setEnabled(typeId != 0);
		menuItem.setActionCommand(ACTION_BROWSE_GAMES_CHRUKER);
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem(GuiShared.get().eveOnline());
		menuItem.setIcon(Images.MISC_EVE.getIcon());
		menuItem.setEnabled(typeId != 0);
		menuItem.setActionCommand(ACTION_BROWSE_EVE_ITEM_DATABASE);
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
		if (ACTION_BROWSE_EVE_ITEM_DATABASE.equals(e.getActionCommand())){
			DesktopUtil.browse("http://wiki.eveonline.com/wiki/"+typeName.replace(" ", "_"), program);
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
