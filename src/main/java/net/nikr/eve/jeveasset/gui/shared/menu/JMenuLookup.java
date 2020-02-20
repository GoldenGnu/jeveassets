/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab.OverviewAction;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsOverview;
import net.nikr.eve.jeveasset.io.online.EvepraisalGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;


public class JMenuLookup<T> extends JAutoMenu<T> {

	private enum MenuLookupAction {
		EVE_MARKETDATA,
		EVEMARKETER,
		GAMES_CHRUKER,
		FUZZWORK_ITEMS,
		ZKILLBOARD_ITEM,
		ZKILLBOARD_SYSTEM,
		ZKILLBOARD_REGION,
		EVE_REF,
		FUZZWORK_BLUEPRINTS,
		KHON_SPACE_INVENTION,
		KHON_SPACE_RESEARCH,
		KHON_SPACE_MANUFACTURING,
		FUZZWORK_MARKET,
		EVEMAPS_DOTLAN_STATION,
		EVEMAPS_DOTLAN_PLANET,
		EVEMAPS_DOTLAN_SYSTEM,
		EVEMAPS_DOTLAN_REGION,
		EVE_INFO,
		EVEPRAISAL,
		ADAM4EVE,
		EVEHUB,
		EVEMARKETHELPER,
	}

	private final JMenu jLocations;
	private final JMenu jDotlan;
	private final JMenuItem jDotlanStation;
	private final JMenuItem jDotlanPlanet;
	private final JMenuItem jDotlanSystem;
	private final JMenuItem jDotlanRegion;
	private final JMenuItem jDotlanLocations;
	private final JMenuItem jzKillboardSystem;
	private final JMenuItem jzKillboardRegion;
	private final JMenu jMarket;
	private final JMenuItem jEveMarketdata;
	private final JMenuItem jEveMarketer;
	private final JMenuItem jFuzzworkMarket;
	private final JMenuItem jEvepraisal;
	private final JMenuItem jEveHub;
	private final JMenuItem jEveMarketHelper;
	private final JMenu jItemDatabase;
	private final JMenuItem jFuzzworkItems;
	private final JMenuItem jChruker;
	private final JMenuItem jzKillboardItem;
	private final JMenuItem jEveRef;
	private final JMenuItem jEveInfo;
	private final JMenu jIndustry;
	private final JMenuItem jFuzzworkBlueprints;
	private final JMenuItem jKhonSpaceInvention;
	private final JMenuItem jKhonSpaceResearch;
	private final JMenuItem jKhonSpaceManufacturing;
	private final JMenuItem jAdam4eve;

	public JMenuLookup(final Program program) {
		super(GuiShared.get().lookup(), program);

		ListenerClass listener = new ListenerClass();

		this.setIcon(Images.LINK_LOOKUP.getIcon());

		jLocations = new JMenu(GuiShared.get().location());
		jLocations.setIcon(Images.LOC_LOCATIONS.getIcon());
		add(jLocations);

		JMenu jzKillboard = new JMenu(GuiShared.get().zKillboard());
		jzKillboard.setIcon(Images.LINK_ZKILLBOARD.getIcon());
		jLocations.add(jzKillboard);

		jzKillboardSystem = new JMenuItem(GuiShared.get().system());
		jzKillboardSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jzKillboardSystem.setActionCommand(MenuLookupAction.ZKILLBOARD_SYSTEM.name());
		jzKillboardSystem.addActionListener(listener);
		jzKillboard.add(jzKillboardSystem);

		jzKillboardRegion = new JMenuItem(GuiShared.get().region());
		jzKillboardRegion.setIcon(Images.LOC_REGION.getIcon());
		jzKillboardRegion.setActionCommand(MenuLookupAction.ZKILLBOARD_REGION.name());
		jzKillboardRegion.addActionListener(listener);
		jzKillboard.add(jzKillboardRegion);

		jDotlan = new JMenu(GuiShared.get().dotlan());
		jDotlan.setIcon(Images.LINK_DOTLAN_EVEMAPS.getIcon());
		jLocations.add(jDotlan);

		jDotlanStation = new JMenuItem(GuiShared.get().station());
		jDotlanStation.setIcon(Images.LOC_STATION.getIcon());
		jDotlanStation.setActionCommand(MenuLookupAction.EVEMAPS_DOTLAN_STATION.name());
		jDotlanStation.addActionListener(listener);
		jDotlan.add(jDotlanStation);

		jDotlanPlanet = new JMenuItem(GuiShared.get().planet());
		jDotlanPlanet.setIcon(Images.LOC_PLANET.getIcon());
		jDotlanPlanet.setActionCommand(MenuLookupAction.EVEMAPS_DOTLAN_PLANET.name());
		jDotlanPlanet.addActionListener(listener);
		jDotlan.add(jDotlanPlanet);

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

		jEveMarketdata = new JMenuItem(GuiShared.get().eveMarketData());
		jEveMarketdata.setIcon(Images.LINK_EVE_MARKETDATA.getIcon());
		jEveMarketdata.setActionCommand(MenuLookupAction.EVE_MARKETDATA.name());
		jEveMarketdata.addActionListener(listener);
		jMarket.add(jEveMarketdata);

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

		jEvepraisal = new JMenuItem(GuiShared.get().evepraisal());
		jEvepraisal.setIcon(Images.LINK_EVEPRAISAL.getIcon());
		jEvepraisal.setActionCommand(MenuLookupAction.EVEPRAISAL.name());
		jEvepraisal.addActionListener(listener);
		jMarket.add(jEvepraisal);

		jAdam4eve = new JMenuItem(GuiShared.get().adam4eve());
		jAdam4eve.setIcon(Images.LINK_ADAM4EVE.getIcon());
		jAdam4eve.setActionCommand(MenuLookupAction.ADAM4EVE.name());
		jAdam4eve.addActionListener(listener);
		jMarket.add(jAdam4eve);

		jEveHub = new JMenuItem(GuiShared.get().eveHub());
		jEveHub.setIcon(Images.LINK_EVEHUB.getIcon());
		jEveHub.setActionCommand(MenuLookupAction.EVEHUB.name());
		jEveHub.addActionListener(listener);
		jMarket.add(jEveHub);

		jEveMarketHelper = new JMenuItem(GuiShared.get().eveMarketHelper());
		jEveMarketHelper.setIcon(Images.LINK_EVEMARKETHELPER.getIcon());
		jEveMarketHelper.setActionCommand(MenuLookupAction.EVEMARKETHELPER.name());
		jEveMarketHelper.addActionListener(listener);
		jMarket.add(jEveMarketHelper);
		
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

		jzKillboardItem = new JMenuItem(GuiShared.get().zKillboard());
		jzKillboardItem.setIcon(Images.LINK_ZKILLBOARD.getIcon());
		jzKillboardItem.setActionCommand(MenuLookupAction.ZKILLBOARD_ITEM.name());
		jzKillboardItem.addActionListener(listener);
		jItemDatabase.add(jzKillboardItem);

		jEveRef = new JMenuItem(GuiShared.get().eveRef());
		//jEveRef.setIcon(Images.LINK_EVE_REF.getIcon());
		jEveRef.setActionCommand(MenuLookupAction.EVE_REF.name());
		jEveRef.addActionListener(listener);
		jItemDatabase.add(jEveRef);

		jIndustry = new JMenu(GuiShared.get().industry());
		jIndustry.setIcon(Images.TOOL_INDUSTRY_JOBS.getIcon());
		add(jIndustry);

		jFuzzworkBlueprints = new JMenuItem(GuiShared.get().fuzzworkBlueprints());
		jFuzzworkBlueprints.setIcon(Images.LINK_FUZZWORK.getIcon());
		jFuzzworkBlueprints.setActionCommand(MenuLookupAction.FUZZWORK_BLUEPRINTS.name());
		jFuzzworkBlueprints.addActionListener(listener);
		jIndustry.add(jFuzzworkBlueprints);

		JMenu jKhonSpace = new JMenu(GuiShared.get().khonSpace());
		jKhonSpace.setIcon(Images.LINK_KHON_SPACE.getIcon());
		jIndustry.add(jKhonSpace);
		
		jKhonSpaceInvention = new JMenuItem(GuiShared.get().khonSpaceInvention());
		jKhonSpaceInvention.setIcon(Images.MISC_INVENTION.getIcon());
		jKhonSpaceInvention.setActionCommand(MenuLookupAction.KHON_SPACE_INVENTION.name());
		jKhonSpaceInvention.addActionListener(listener);
		jKhonSpace.add(jKhonSpaceInvention);

		jKhonSpaceResearch = new JMenuItem(GuiShared.get().khonSpaceResearch());
		jKhonSpaceResearch.setIcon(Images.MISC_COPYING.getIcon());
		jKhonSpaceResearch.setActionCommand(MenuLookupAction.KHON_SPACE_RESEARCH.name());
		jKhonSpaceResearch.addActionListener(listener);
		jKhonSpace.add(jKhonSpaceResearch);

		jKhonSpaceManufacturing = new JMenuItem(GuiShared.get().khonSpaceManufacturing());
		jKhonSpaceManufacturing.setIcon(Images.MISC_MANUFACTURING.getIcon());
		jKhonSpaceManufacturing.setActionCommand(MenuLookupAction.KHON_SPACE_MANUFACTURING.name());
		jKhonSpaceManufacturing.addActionListener(listener);
		jKhonSpace.add(jKhonSpaceManufacturing);
	}

	@Override
	public void updateMenuData() {
	//Location
		jLocations.setEnabled(!menuData.getStationNames().isEmpty() || !menuData.getSystemNames().isEmpty() || !menuData.getRegionNames().isEmpty());
		jDotlanStation.setEnabled(!menuData.getStationNames().isEmpty());
		jDotlanPlanet.setEnabled(!menuData.getPlanetNames().isEmpty());
		jDotlanSystem.setEnabled(!menuData.getSystemNames().isEmpty());
		jDotlanRegion.setEnabled(!menuData.getRegionNames().isEmpty());
		jzKillboardSystem.setEnabled(!menuData.getSystemLocations().isEmpty());
		jzKillboardRegion.setEnabled(!menuData.getRegionLocations().isEmpty());
	//Market
		jMarket.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveMarketdata.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveMarketer.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jFuzzworkMarket.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jAdam4eve.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveHub.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveMarketHelper.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
	//Info
		jItemDatabase.setEnabled(!menuData.getTypeIDs().isEmpty());
		jFuzzworkItems.setEnabled(!menuData.getTypeIDs().isEmpty());
		jzKillboardItem.setEnabled(!menuData.getTypeIDs().isEmpty());
		jChruker.setEnabled(!menuData.getTypeIDs().isEmpty());
		jEveInfo.setEnabled(!menuData.getTypeIDs().isEmpty());
	//Industry
		jIndustry.setEnabled(!menuData.getBlueprintTypeIDs().isEmpty());
		jFuzzworkBlueprints.setEnabled(!menuData.getBlueprintTypeIDs().isEmpty());
		jKhonSpaceInvention.setEnabled(!menuData.getInventionTypeIDs().isEmpty());
		jKhonSpaceResearch.setEnabled(!menuData.getBlueprintTypeIDs().isEmpty());
		jKhonSpaceManufacturing.setEnabled(!menuData.getBlueprintTypeIDs().isEmpty());
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
			jLocations.setEnabled(enabled || !overviewTab.isGroup());
		} else {
			jDotlan.remove(jDotlanLocations);
		}
	}

	public static void browseDotlan(final Program program, Set<String> stations, Set<String> planets, Set<String> systems, Set<String> regions) {
		Set<String> urls = new HashSet<String>();
		if (planets != null) {
			for (String planet : planets) {
				urls.add("http://evemaps.dotlan.net/system/" + replaceLast(planet, " ", "/").replace(" ", "_"));
			}
		}
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
		//Locations
			if (MenuLookupAction.EVEMAPS_DOTLAN_STATION.name().equals(e.getActionCommand())) {
				browseDotlan(program, menuData.getStationNames(), null, null, null);
			} else if (MenuLookupAction.EVEMAPS_DOTLAN_PLANET.name().equals(e.getActionCommand())) {
				browseDotlan(program, null, menuData.getPlanetNames(), null, null);
			} else if (MenuLookupAction.EVEMAPS_DOTLAN_SYSTEM.name().equals(e.getActionCommand())) {
				browseDotlan(program, null, null, menuData.getSystemNames(), null);
			} else if (MenuLookupAction.EVEMAPS_DOTLAN_REGION.name().equals(e.getActionCommand())) {
				browseDotlan(program, null, null, null, menuData.getRegionNames());
			} else if (MenuLookupAction.ZKILLBOARD_SYSTEM.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (MyLocation location : menuData.getSystemLocations()) {
					urls.add("https://zkillboard.com/system/" + location.getLocationID()+ "/");
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.ZKILLBOARD_REGION.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (MyLocation location : menuData.getRegionLocations()) {
					urls.add("https://zkillboard.com/region/" + location.getLocationID()+ "/");
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.EVE_MARKETDATA.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("http://eve-marketdata.com/price_check.php?type_id=" + marketTypeID);
				}
				DesktopUtil.browse(urls, program);
		//Market
			} else if (MenuLookupAction.EVEMARKETER.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://evemarketer.com/types/" + marketTypeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.ADAM4EVE.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getMarketTypeIDs()) {
					urls.add("https://www.adam4eve.eu/commodity.php?typeID=" + typeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.EVEHUB.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getMarketTypeIDs()) {
					urls.add("https://eve-hub.com/market/chart/10000002/" + typeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.EVEMARKETHELPER.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getMarketTypeIDs()) {
					Item item = ApiIdConverter.getItem(typeID);
					urls.add("https://www.evemarkethelper.net/Market/" + typeID +"-" + item.getTypeName().replace(" ", "-").replace("'", "").toLowerCase());
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.EVEPRAISAL.name().equals(e.getActionCommand())) {
				String evepraisal = EvepraisalGetter.post(menuData.getItemCounts());
				if (evepraisal != null) {
					DesktopUtil.browse("https://evepraisal.com/a/" + evepraisal, program);
				}
			} else if (MenuLookupAction.FUZZWORK_MARKET.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getMarketTypeIDs()) {
					urls.add("https://market.fuzzwork.co.uk/hub/type/" + typeID + "/");
				}
				DesktopUtil.browse(urls, program);
		//Info
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
			} else if (MenuLookupAction.ZKILLBOARD_ITEM.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://zkillboard.com/item/" + typeID + "/");
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.EVE_REF.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://everef.net/type/" + typeID+ "?utm_source=jeveassets");
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.EVE_INFO.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://eveinfo.com/item/" + typeID);
				}
				DesktopUtil.browse(urls, program);
		//Industry
			} else if (MenuLookupAction.FUZZWORK_BLUEPRINTS.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://www.fuzzwork.co.uk/blueprint/?typeid=" + typeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.KHON_SPACE_INVENTION.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getInventionTypeIDs()) {
					urls.add("https://khon.space/blueprint/invention/" + typeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.KHON_SPACE_MANUFACTURING.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://khon.space/blueprint/manufacturing/" + typeID);
				}
				DesktopUtil.browse(urls, program);
			} else if (MenuLookupAction.KHON_SPACE_RESEARCH.name().equals(e.getActionCommand())) {
				Set<String> urls = new HashSet<String>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://khon.space/blueprint/research_copy/" + typeID);
				}
				DesktopUtil.browse(urls, program);
			}
		}
	}

	public static String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos)
				 + replacement
				 + string.substring(pos + toReplace.length(), string.length());
		} else {
			return string;
		}
	}
}
