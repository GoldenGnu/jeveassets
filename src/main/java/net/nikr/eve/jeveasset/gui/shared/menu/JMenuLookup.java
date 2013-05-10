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
import java.util.HashSet;
import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsOverview;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;


public class JMenuLookup<T> extends JAutoMenu<T> implements ActionListener {

	private static final String ACTION_BROWSE_EVE_CENTRAL = "ACTION_BROWSE_EVE_CENTRAL";
	private static final String ACTION_BROWSE_EVE_MARKETDATA = "ACTION_BROWSE_EVE_MARKETDATA";
	private static final String ACTION_BROWSE_EVE_MARKETS = "ACTION_BROWSE_EVE_MARKETS";
	private static final String ACTION_BROWSE_GAMES_CHRUKER = "ACTION_BROWSE_GAMES_CHRUKER";
	private static final String ACTION_BROWSE_EVE_ITEM_DATABASE = "ACTION_BROWSE_EVE_ITEM_DATABASE";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_STATION = "ACTION_BROWSE_EVEMAPS_DOTLAN_STATION";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM = "ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_REGION = "ACTION_BROWSE_EVEMAPS_DOTLAN_REGION";
	private static final String ACTION_BROWSE_EVEMARKETEER = "ACTION_BROWSE_EVEMARKETEER";
	private static final String ACTION_BROWSE_EVE_ADDICTS = "ACTION_BROWSE_EVE_ADDICTS";

	private final Program program;
	private final JMenu jDotlan;
	private final JMenuItem jDotlanStation;
	private final JMenuItem jDotlanSystem;
	private final JMenuItem jDotlanRegion;
	private final JMenuItem jDotlanLocations;
	private final JMenuItem jEveCentral;
	private final JMenuItem jEveMarketdata;
	private final JMenuItem jEveMarketeer;
	private final JMenuItem jEveMarkets;
	private final JMenuItem jEveAddicts;
	private final JMenuItem jChruker;
	private final JMenuItem jEveOnline;

	private MenuData<T> menuData;

	public JMenuLookup(final Program program) {
		super(GuiShared.get().lookup());
		this.program = program;

		this.setIcon(Images.LINK_LOOKUP.getIcon());

		jDotlan = new JMenu(GuiShared.get().dotlan());
		jDotlan.setIcon(Images.LINK_DOTLAN_EVEMAPS.getIcon());
		add(jDotlan);

		jDotlanStation = new JMenuItem(GuiShared.get().station());
		jDotlanStation.setIcon(Images.LOC_STATION.getIcon());
		jDotlanStation.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_STATION);
		jDotlanStation.addActionListener(this);
		jDotlan.add(jDotlanStation);

		jDotlanSystem = new JMenuItem(GuiShared.get().system());
		jDotlanSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jDotlanSystem.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM);
		jDotlanSystem.addActionListener(this);
		jDotlan.add(jDotlanSystem);

		jDotlanRegion = new JMenuItem(GuiShared.get().region());
		jDotlanRegion.setIcon(Images.LOC_REGION.getIcon());
		jDotlanRegion.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_REGION);
		jDotlanRegion.addActionListener(this);
		jDotlan.add(jDotlanRegion);

		jDotlanLocations = new JMenuItem(TabsOverview.get().locations());
		jDotlanLocations.setIcon(Images.LOC_LOCATIONS.getIcon());

		addSeparator();

		jEveCentral = new JMenuItem(GuiShared.get().eveCentral());
		jEveCentral.setIcon(Images.LINK_EVE_CENTRAL.getIcon());
		jEveCentral.setActionCommand(ACTION_BROWSE_EVE_CENTRAL);
		jEveCentral.addActionListener(this);
		add(jEveCentral);

		jEveMarketdata = new JMenuItem(GuiShared.get().eveMarketdata());
		jEveMarketdata.setIcon(Images.LINK_EVE_MARKETDATA.getIcon());
		jEveMarketdata.setActionCommand(ACTION_BROWSE_EVE_MARKETDATA);
		jEveMarketdata.addActionListener(this);
		add(jEveMarketdata);

		jEveMarketeer = new JMenuItem(GuiShared.get().eveMarketeer());
		jEveMarketeer.setIcon(Images.LINK_EVEMARKETEER.getIcon());
		jEveMarketeer.setActionCommand(ACTION_BROWSE_EVEMARKETEER);
		jEveMarketeer.addActionListener(this);
		add(jEveMarketeer);

		jEveMarkets = new JMenuItem(GuiShared.get().eveMarkets());
		jEveMarkets.setIcon(Images.LINK_EVE_MARKETS.getIcon());
		jEveMarkets.setActionCommand(ACTION_BROWSE_EVE_MARKETS);
		jEveMarkets.addActionListener(this);
		add(jEveMarkets);

		jEveAddicts = new JMenuItem(GuiShared.get().eveAddicts());
		jEveAddicts.setIcon(Images.LINK_EVE_ADDICTS.getIcon());
		jEveAddicts.setActionCommand(ACTION_BROWSE_EVE_ADDICTS);
		jEveAddicts.addActionListener(this);
		add(jEveAddicts);

		addSeparator();

		jChruker = new JMenuItem(GuiShared.get().chruker());
		jChruker.setIcon(Images.LINK_CHRUKER.getIcon());
		jChruker.setActionCommand(ACTION_BROWSE_GAMES_CHRUKER);
		jChruker.addActionListener(this);
		add(jChruker);

		jEveOnline = new JMenuItem(GuiShared.get().eveOnline());
		jEveOnline.setIcon(Images.MISC_EVE.getIcon());
		jEveOnline.setActionCommand(ACTION_BROWSE_EVE_ITEM_DATABASE);
		jEveOnline.addActionListener(this);
		add(jEveOnline);
	}

	@Override
	public void setMenuData(MenuData<T> menuData) {
		this.menuData = menuData;
		jDotlan.setEnabled(!menuData.getStations().isEmpty() || !menuData.getSystems().isEmpty() || !menuData.getRegions().isEmpty());
		jDotlanStation.setEnabled(!menuData.getStations().isEmpty());
		jDotlanSystem.setEnabled(!menuData.getSystems().isEmpty());
		jDotlanRegion.setEnabled(!menuData.getRegions().isEmpty());
		jEveCentral.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveMarketdata.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveMarketeer.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveMarkets.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveAddicts.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jChruker.setEnabled(!menuData.getTypeIDs().isEmpty());
		jEveOnline.setEnabled(!menuData.getTypeNames().isEmpty());
	}

	public void setTool(Object object) {
		if (object instanceof OverviewTab) {
			OverviewTab overviewTab = (OverviewTab) object;
			//Remove all action listeners
			for (ActionListener listener : jDotlanLocations.getActionListeners()) {
				jDotlanLocations.removeActionListener(listener);
			}
			boolean enabled = overviewTab.isGroupAndNotEmpty();
			jDotlanLocations.setActionCommand(OverviewTab.ACTION_GROUP_LOOKUP);
			jDotlanLocations.addActionListener(overviewTab.getListenerClass());
			jDotlanLocations.setEnabled(enabled);
			jDotlan.add(jDotlanLocations);
			jDotlan.setEnabled(enabled || !overviewTab.isGroup());
		} else {
			jDotlan.remove(jDotlanLocations);
		}
	}

	protected static boolean confirmOpenLinks(final Program program, final int size) {
		if (size <= 1) {
			return true;
		}
		int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), GuiShared.get().openLinks(size), GuiShared.get().openLinksTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		return (value == JOptionPane.OK_OPTION);
	}

	public static void browseDotlan(final Program program, Set<String> stations, Set<String> systems, Set<String> regions) {
		if (stations == null) {
			stations = new HashSet<String>();
		}
		if (systems == null) {
			systems = new HashSet<String>();
		}
		if (regions == null) {
			regions = new HashSet<String>();
		}
		if (!confirmOpenLinks(program, stations.size() + systems.size() + regions.size())) {
			return;
		}
		for (String station : stations) {
			DesktopUtil.browse("http://evemaps.dotlan.net/outpost/" + station.replace(" ", "_"), program);
		}
		for (String system : systems) {
			DesktopUtil.browse("http://evemaps.dotlan.net/system/" + system.replace(" ", "_"), program);
		}
		for (String region : regions) {
			DesktopUtil.browse("http://evemaps.dotlan.net/map/" + region.replace(" ", "_"), program);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_BROWSE_EVEMAPS_DOTLAN_STATION.equals(e.getActionCommand())) {
			browseDotlan(program, menuData.getStations(), null, null);
		}
		if (ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM.equals(e.getActionCommand())) {
			browseDotlan(program, null, menuData.getSystems(), null);
		}
		if (ACTION_BROWSE_EVEMAPS_DOTLAN_REGION.equals(e.getActionCommand())) {
			browseDotlan(program, null, null, menuData.getRegions());
		}
		if (ACTION_BROWSE_EVE_CENTRAL.equals(e.getActionCommand())) {
			if (!confirmOpenLinks(program, menuData.getMarketTypeIDs().size())) {
				return;
			}
			for (int marketTypeID : menuData.getMarketTypeIDs()) {
				DesktopUtil.browse("http://www.eve-central.com/home/quicklook.html?typeid=" + marketTypeID, program);
			}
		}
		if (ACTION_BROWSE_EVE_MARKETDATA.equals(e.getActionCommand())) {
			if (!confirmOpenLinks(program, menuData.getMarketTypeIDs().size())) {
				return;
			}
			for (int marketTypeID : menuData.getMarketTypeIDs()) {
				DesktopUtil.browse("http://eve-marketdata.com/price_check.php?type_id=" + marketTypeID, program);
			}
		}
		if (ACTION_BROWSE_EVEMARKETEER.equals(e.getActionCommand())) {
			if (!confirmOpenLinks(program, menuData.getMarketTypeIDs().size())) {
				return;
			}
			for (int marketTypeID : menuData.getMarketTypeIDs()) {
				DesktopUtil.browse("http://www.evemarketeer.com/item/info/" + marketTypeID, program);
			}
		}
		if (ACTION_BROWSE_EVE_MARKETS.equals(e.getActionCommand())) {
			if (!confirmOpenLinks(program, menuData.getMarketTypeIDs().size())) {
				return;
			}
			for (int marketTypeID : menuData.getMarketTypeIDs()) {
				DesktopUtil.browse("http://www.eve-markets.net/detail.php?typeid=" + marketTypeID, program);
			}
		}
		if (ACTION_BROWSE_EVE_ADDICTS.equals(e.getActionCommand())) {
			if (!confirmOpenLinks(program, menuData.getMarketTypeIDs().size())) {
				return;
			}
			for (int marketTypeID : menuData.getMarketTypeIDs()) {
				DesktopUtil.browse("http://eve.addicts.nl/?typeID=" + marketTypeID, program);
			}
		}
		if (ACTION_BROWSE_GAMES_CHRUKER.equals(e.getActionCommand())) {
			if (!confirmOpenLinks(program, menuData.getTypeIDs().size())) {
				return;
			}
			for (int typeID : menuData.getTypeIDs()) {
				DesktopUtil.browse("http://games.chruker.dk/eve_online/item.php?type_id=" + typeID, program);
			}
		}
		if (ACTION_BROWSE_EVE_ITEM_DATABASE.equals(e.getActionCommand())) {
			if (!confirmOpenLinks(program, menuData.getTypeNames().size())) {
				return;
			}
			for (String typeName : menuData.getTypeNames()) {
				DesktopUtil.browse("http://wiki.eveonline.com/wiki/" + typeName.replace(" ", "_"), program);
			}
		}
	}
}
