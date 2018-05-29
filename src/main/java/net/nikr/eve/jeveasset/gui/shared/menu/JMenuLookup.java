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
import java.util.HashSet;
import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab.OverviewAction;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsOverview;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;


public class JMenuLookup<T> extends JAutoMenu<T> {

	private enum MenuLookupAction {
		//EVE_CENTRAL,
		EVE_MARKETDATA,
		EVE_MARKETS,
		EVEMARKETER,
		GAMES_CHRUKER,
		FUZZWORK_ITEMS,
		FUZZWORK_BLUEPRINTS,
		FUZZWORK_MARKET,
		EVEMAPS_DOTLAN_STATION,
		EVEMAPS_DOTLAN_SYSTEM,
		EVEMAPS_DOTLAN_REGION,
		EVE_INFO,
	}

	private final JMenu jDotlan;
	private final JMenuItem jDotlanStation;
	private final JMenuItem jDotlanSystem;
	private final JMenuItem jDotlanRegion;
	private final JMenuItem jDotlanLocations;
	//private final JMenuItem jEveCentral;
	private final JMenu jMarket;
	private final JMenuItem jEveMarketdata;
	private final JMenuItem jEveMarketer;
	private final JMenuItem jEveMarkets;
	private final JMenuItem jFuzzworkMarket;
	private final JMenu jItemDatabase;
	private final JMenuItem jFuzzworkItems;
	private final JMenuItem jChruker;
	private final JMenuItem jEveInfo;
	private final JMenu jIndustry;
	private final JMenuItem jFuzzworkBlueprints;

	private MenuData<T> menuData;

	public JMenuLookup(final Program program) {
		super(GuiShared.get().lookup(), program);

		ListenerClass listener = new ListenerClass();

		this.setIcon(Images.LINK_LOOKUP.getIcon());

		jDotlan = new JMenu(GuiShared.get().dotlan());
		jDotlan.setIcon(Images.LINK_DOTLAN_EVEMAPS.getIcon());
		add(jDotlan);

		jDotlanStation = new JMenuItem(GuiShared.get().station());
		jDotlanStation.setIcon(Images.LOC_STATION.getIcon());
		jDotlanStation.setActionCommand(MenuLookupAction.EVEMAPS_DOTLAN_STATION.name());
		jDotlanStation.addActionListener(listener);
		jDotlan.add(jDotlanStation);

		jDotlanSystem = new JMenuItem(GuiShared.get().system());
		jDotlanSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jDotlanSystem.setActionCommand(MenuLookupAction.EVEMAPS_DOTLAN_SYSTEM.name());
		jDotlanSystem.addActionListener(listener);
		jDotlan.add(jDotlanSystem);

		jDotlanRegion = new JMenuItem(GuiShared.get().region());
		jDotlanRegion.setIcon(Images.LOC_REGION.getIcon());
		jDotlanRegion.setActionCommand(MenuLookupAction.EVEMAPS_DOTLAN_REGION.name());
		jDotlanRegion.addActionListener(listener);
		jDotlan.add(jDotlanRegion);

		jDotlanLocations = new JMenuItem(TabsOverview.get().locations());
		jDotlanLocations.setIcon(Images.LOC_LOCATIONS.getIcon());

		jMarket = new JMenu(GuiShared.get().market());
		jMarket.setIcon(Images.ORDERS_SELL.getIcon());
		add(jMarket);
		
		/*
		jEveCentral = new JMenuItem(GuiShared.get().eveCentral());
		jEveCentral.setIcon(Images.LINK_EVE_CENTRAL.getIcon());
		jEveCentral.setActionCommand(MenuLookupAction.EVE_CENTRAL.name());
		jEveCentral.addActionListener(listener);
		add(jEveCentral);
		*/

		jEveMarketdata = new JMenuItem(GuiShared.get().eveMarketdata());
		jEveMarketdata.setIcon(Images.LINK_EVE_MARKETDATA.getIcon());
		jEveMarketdata.setActionCommand(MenuLookupAction.EVE_MARKETDATA.name());
		jEveMarketdata.addActionListener(listener);
		jMarket.add(jEveMarketdata);

		jEveMarkets = new JMenuItem(GuiShared.get().eveMarkets());
		jEveMarkets.setIcon(Images.LINK_EVE_MARKETS.getIcon());
		jEveMarkets.setActionCommand(MenuLookupAction.EVE_MARKETS.name());
		jEveMarkets.addActionListener(listener);
		jMarket.add(jEveMarkets);

		jEveMarketer = new JMenuItem(GuiShared.get().eveMarketer());
		jEveMarketer.setIcon(Images.LINK_EVEMARKETER.getIcon());
		jEveMarketer.setActionCommand(MenuLookupAction.EVEMARKETER.name());
		jEveMarketer.addActionListener(listener);
		jMarket.add(jEveMarketer);

		jFuzzworkMarket = new JMenuItem(GuiShared.get().fuzzworkMarket());
		jFuzzworkMarket.setIcon(Images.LINK_FUZZWORK.getIcon());
		jFuzzworkMarket.setActionCommand(MenuLookupAction.FUZZWORK_MARKET.name());
		jFuzzworkMarket.addActionListener(listener);
		jMarket.add(jFuzzworkMarket);

		jItemDatabase = new JMenu(GuiShared.get().itemDatabase());
		jItemDatabase.setIcon(Images.TOOL_ASSETS.getIcon());
		add(jItemDatabase);

		jChruker = new JMenuItem(GuiShared.get().chruker());
		jChruker.setIcon(Images.LINK_CHRUKER.getIcon());
		jChruker.setActionCommand(MenuLookupAction.GAMES_CHRUKER.name());
		jChruker.addActionListener(listener);
		jItemDatabase.add(jChruker);

		jEveInfo = new JMenuItem(GuiShared.get().eveInfo());
		jEveInfo.setIcon(Images.LINK_EVEINFO.getIcon());
		jEveInfo.setActionCommand(MenuLookupAction.EVE_INFO.name());
		jEveInfo.addActionListener(listener);
		jItemDatabase.add(jEveInfo);

		jFuzzworkItems = new JMenuItem(GuiShared.get().fuzzworkItems());
		jFuzzworkItems.setIcon(Images.LINK_FUZZWORK.getIcon());
		jFuzzworkItems.setActionCommand(MenuLookupAction.FUZZWORK_ITEMS.name());
		jFuzzworkItems.addActionListener(listener);
		jItemDatabase.add(jFuzzworkItems);

		jIndustry = new JMenu(GuiShared.get().industry());
		jIndustry.setIcon(Images.TOOL_INDUSTRY_JOBS.getIcon());
		add(jIndustry);

		jFuzzworkBlueprints = new JMenuItem(GuiShared.get().fuzzworkBlueprints());
		jFuzzworkBlueprints.setIcon(Images.LINK_FUZZWORK.getIcon());
		jFuzzworkBlueprints.setActionCommand(MenuLookupAction.FUZZWORK_BLUEPRINTS.name());
		jFuzzworkBlueprints.addActionListener(listener);
		jIndustry.add(jFuzzworkBlueprints);

	}

	@Override
	public void setMenuData(MenuData<T> menuData) {
		this.menuData = menuData;
		jDotlan.setEnabled(!menuData.getStationNames().isEmpty() || !menuData.getSystemNames().isEmpty() || !menuData.getRegionNames().isEmpty());
		jDotlanStation.setEnabled(!menuData.getStationNames().isEmpty());
		jDotlanSystem.setEnabled(!menuData.getSystemNames().isEmpty());
		jDotlanRegion.setEnabled(!menuData.getRegionNames().isEmpty());
		jMarket.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		//jEveCentral.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveMarketdata.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveMarketer.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveMarkets.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jFuzzworkMarket.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jItemDatabase.setEnabled(!menuData.getTypeIDs().isEmpty());
		jFuzzworkItems.setEnabled(!menuData.getTypeIDs().isEmpty());
		jChruker.setEnabled(!menuData.getTypeIDs().isEmpty());
		jIndustry.setEnabled(!menuData.getTypeIDs().isEmpty());
		jFuzzworkBlueprints.setEnabled(!menuData.getTypeIDs().isEmpty());
		jEveInfo.setEnabled(!menuData.getTypeIDs().isEmpty());
	}

	public void setTool(Object object) {
		if (object instanceof OverviewTab) {
			OverviewTab overviewTab = (OverviewTab) object;
			//Remove all action listeners
			for (ActionListener listener : jDotlanLocations.getActionListeners()) {
				jDotlanLocations.removeActionListener(listener);
			}
			boolean enabled = overviewTab.isGroupAndNotEmpty();
			jDotlanLocations.setActionCommand(OverviewAction.GROUP_LOOKUP.name());
			jDotlanLocations.addActionListener(overviewTab.getListenerClass());
			jDotlanLocations.setEnabled(enabled);
			jDotlan.add(jDotlanLocations);
			jDotlan.setEnabled(enabled || !overviewTab.isGroup());
		} else {
			jDotlan.remove(jDotlanLocations);
		}
	}

	public static void browseDotlan(final Program program, Set<String> stations, Set<String> systems, Set<String> regions) {
		Set<String> urls = new HashSet<String>();
		if (stations != null) {
			for (String station : stations) {
				urls.add("http://evemaps.dotlan.net/outpost/" + station.replace(" ", "_"));
			}
		}
		if (systems != null) {
			for (String system : systems) {
				urls.add("http://evemaps.dotlan.net/system/" + system.replace(" ", "_"));
			}
		}
		if (regions != null) {
			for (String region : regions) {
				urls.add("http://evemaps.dotlan.net/map/" + region.replace(" ", "_"));
			}
		}
		DesktopUtil.browse(urls, program);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuLookupAction.EVEMAPS_DOTLAN_STATION.name().equals(e.getActionCommand())) {
				browseDotlan(program, menuData.getStationNames(), null, null);
			} else if (MenuLookupAction.EVEMAPS_DOTLAN_SYSTEM.name().equals(e.getActionCommand())) {
				browseDotlan(program, null, menuData.getSystemNames(), null);
			} else if (MenuLookupAction.EVEMAPS_DOTLAN_REGION.name().equals(e.getActionCommand())) {
				browseDotlan(program, null, null, menuData.getRegionNames());
			/*
			} else if (MenuLookupAction.EVE_CENTRAL.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("http://www.eve-central.com/home/quicklook.html?typeid=" + marketTypeID);
				}
				DesktopUtil.browse(urls, program);
			*/
			} else if (MenuLookupAction.EVE_MARKETDATA.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("http://eve-marketdata.com/price_check.php?type_id=" + marketTypeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.EVEMARKETER.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://evemarketer.com/types/" + marketTypeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.EVE_MARKETS.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("http://www.eve-markets.net/detail.php?typeid=" + marketTypeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.GAMES_CHRUKER.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("http://games.chruker.dk/eve_online/item.php?type_id=" + typeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.FUZZWORK_ITEMS.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://www.fuzzwork.co.uk/info/?typeid=" + typeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.FUZZWORK_BLUEPRINTS.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://www.fuzzwork.co.uk/blueprint/?typeid=" + typeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.FUZZWORK_MARKET.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://market.fuzzwork.co.uk/hub/type/" + typeID + "/");
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.EVE_INFO.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://eveinfo.com/item/" + typeID);
				}
				DesktopUtil.browse(urls, program);
			}
		}
	}
}
