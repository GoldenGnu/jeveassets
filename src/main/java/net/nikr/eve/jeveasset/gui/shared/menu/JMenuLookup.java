/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab.OverviewTableMenu;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsOverview;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;


public class JMenuLookup<T> extends JAutoMenu<T> {

	public static enum LookupLinks {
	//Locations
		EVEMAPS_DOTLAN_STATION() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (String station : menuData.getStationNames()) {
					urls.add("https://evemaps.dotlan.net/station/" + station.replace(" ", "_"));
				}
				return urls;
			}
		},
		EVEMAPS_DOTLAN_PLANET() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (String planet : menuData.getPlanetNames()) {
					urls.add("https://evemaps.dotlan.net/system/" + replaceLast(planet, " ", "/").replace(" ", "_"));
				}
				return urls;
			}
		},
		EVEMAPS_DOTLAN_SYSTEM() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (String system : menuData.getSystemNames()) {
					urls.add("https://evemaps.dotlan.net/system/" + system.replace(" ", "_"));
				}
				return urls;
			}
		},
		EVEMAPS_DOTLAN_SYSTEM_REGION() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation system : menuData.getSystemLocations()) {
					urls.add("https://evemaps.dotlan.net/map/" + system.getRegion().replace(" ", "_")+ "/" + system.getSystem().replace(" ", "_"));
				}
				return urls;
			}
		},
		EVEMAPS_DOTLAN_CONSTELLATION() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation constellation : menuData.getConstellationLocations()) {
					urls.add("https://evemaps.dotlan.net/map/" + constellation.getRegion().replace(" ", "_") + "/" + constellation.getConstellation().replace(" ", "_"));
				}
				return urls;
			}
		},
		EVEMAPS_DOTLAN_REGION() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (String region : menuData.getRegionNames()) {
					urls.add("https://evemaps.dotlan.net/map/" + region.replace(" ", "_"));
				}
				return urls;
			}
		},
		EVEMAPS_DOTLAN_OVERVIEW_GROUP() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				return Collections.emptySet();
			}
		},
		ZKILLBOARD_SYSTEM() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getSystemLocations()) {
					urls.add("https://zkillboard.com/system/" +location.getLocationID() + "/");
				}
				return urls;
			}
		},
		ZKILLBOARD_CONSTELLATION() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getConstellationLocations()) {
					urls.add("https://zkillboard.com/constellation/" +location.getLocationID() + "/");
				}
				return urls;
			}
		},
		ZKILLBOARD_OVERVIEW_GROUP() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				return Collections.emptySet();
			}
		},
		ZKILLBOARD_REGION() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getRegionLocations()) {
					urls.add("https://zkillboard.com/region/" +location.getLocationID() + "/");
				}
				return urls;
			}
		},
		EVEMISSIONEER_SYSTEM() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getSystemLocations()) {
					urls.add("https://evemissioneer.com/s/" +location.getLocationID());
				}
				return urls;
			}
		},
		EVEMISSIONEER_CONSTELLATION() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getConstellationLocations()) {
					urls.add("https://evemissioneer.com/c/" +location.getLocationID());
				}
				return urls;
			}
		},
		EVEMISSIONEER_REGION() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getRegionLocations()) {
					urls.add("https://evemissioneer.com/r/" +location.getLocationID());
				}
				return urls;
			}
		},
	//Market
		ADAM4EVE() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://www.adam4eve.eu/commodity.php?typeID=" + marketTypeID);
				}
				return urls;
			}
		},
		FUZZWORK_MARKET() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://market.fuzzwork.co.uk/hub/type/" + marketTypeID + "/");
				}
				return urls;
			}
		},
		EVE_TYCOON() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://evetycoon.com/market/" + marketTypeID);
				}
				return urls;
			}
		},
		EVE_MARKET_BROWSER {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://evemarketbrowser.com/region/0/type/" + marketTypeID);
				}
				return urls;
			}
		},
		JITA_SPACE_MARKET {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://www.jita.space/market/" + marketTypeID);
				}
				return urls;
			}
		},
		EVECONOMY {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://eveconomy.online/item/" + marketTypeID);
				}
				return urls;
			}
		},
	//Info
		GAMES_CHRUKER() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("http://games.chruker.dk/eve_online/item.php?type_id=" + typeID);
				}
				return urls;
			}
		},
		FUZZWORK_ITEMS() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://www.fuzzwork.co.uk/info/?typeid=" + typeID);
				}
				return urls;
			}
		},
		ZKILLBOARD_ITEM() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://zkillboard.com/item/" + typeID + "/");
				}
				return urls;
			}
		},
		EVE_REF() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://everef.net/types/" + typeID+ "?utm_source=jeveassets");
				}
				return urls;
			}
		},
		EVE_INFO() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://eveinfo.com/item/" + typeID);
				}
				return urls;
			}
		},
		JITA_SPACE_ITEM {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://www.jita.space/type/" + typeID);
				}
				return urls;
			}
		},
	//Industry
		FUZZWORK_BLUEPRINTS() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://www.fuzzwork.co.uk/blueprint/?typeid=" + typeID);
				}
				return urls;
			}
		},
		LAZY_BLACKSMITH_INVENTION() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getInventionTypeIDs()) {
					urls.add("https://lzb.eveskillboard.com/blueprint/invention/" + typeID);
				}
				return urls;
			}
		},
		LAZY_BLACKSMITH_MANUFACTURING() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://lzb.eveskillboard.com/blueprint/manufacturing/" + typeID);
				}
				return urls;
			}
		},
		LAZY_BLACKSMITH_RESEARCH() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://lzb.eveskillboard.com/blueprint/research_copy/" + typeID);
				}
				return urls;
			}
		},
		EVE_COOKBOOK() {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://evecookbook.com/?blueprintTypeId=" + typeID);
				}
				return urls;
			}
		},
		;

		public abstract Set<String> getLinks( MenuData<?> menuData);
	}

	private final JMenu jLocations;
	private final JMenu jDotlan;
	private final JMenuItem jDotlanStation;
	private final JMenuItem jDotlanPlanet;
	private final JMenuItem jDotlanSystem;
	private final JMenuItem jDotlanSystemRegion;
	private final JMenuItem jDotlanConstellation;
	private final JMenuItem jDotlanRegion;
	private final JMenuItem jDotlanLocations;
	private final JMenu jzKillboard;
	private final JMenuItem jzKillboardSystem;
	private final JMenuItem jzKillboardConstellation;
	private final JMenuItem jzKillboardRegion;
	private final JMenuItem jzKillboardLocations;
	private final JMenu jEveMissioneer;
	private final JMenuItem jEveMissioneerSystem;
	private final JMenuItem jEveMissioneerConstellation;
	private final JMenuItem jEveMissioneerRegion;
	private final JMenu jMarket;
	private final JMenuItem jFuzzworkMarket;
	private final JMenuItem jEveTycoon;
	private final JMenu jItemDatabase;
	private final JMenuItem jFuzzworkItems;
	private final JMenuItem jChruker;
	private final JMenuItem jzKillboardItem;
	private final JMenuItem jEveRef;
	private final JMenuItem jEveInfo;
	private final JMenu jIndustry;
	private final JMenuItem jFuzzworkBlueprints;
	private final JMenuItem jLazyBlacksmithInvention;
	private final JMenuItem jLazyBlacksmithResearch;
	private final JMenuItem jLazyBlacksmithManufacturing;
	private final JMenuItem jEveCookbook;
	private final JMenuItem jAdam4eve;
	private final JMenuItem jEveMarketBrowser;
	private final JMenuItem jJitaSpaceMarket;
	private final JMenuItem jJitaSpaceItem;
	private final JMenuItem jEveconomy;

	public JMenuLookup(final Program program) {
		super(GuiShared.get().lookup(), program);

		ListenerClass listener = new ListenerClass();

		this.setIcon(Images.LINK_LOOKUP.getIcon());

		jLocations = new JMenu(GuiShared.get().location());
		jLocations.setIcon(Images.LOC_LOCATIONS.getIcon());
		add(jLocations);

		jzKillboard = new JMenu(GuiShared.get().zKillboard());
		jzKillboard.setIcon(Images.LINK_ZKILLBOARD.getIcon());
		jLocations.add(jzKillboard);

		jzKillboardSystem = new JMenuItem(GuiShared.get().system());
		jzKillboardSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jzKillboardSystem.setActionCommand(LookupLinks.ZKILLBOARD_SYSTEM.name());
		jzKillboardSystem.addActionListener(listener);
		jzKillboard.add(jzKillboardSystem);

		jzKillboardConstellation = new JMenuItem(GuiShared.get().constellation());
		jzKillboardConstellation.setIcon(Images.LOC_CONSTELLATION.getIcon());
		jzKillboardConstellation.setActionCommand(LookupLinks.ZKILLBOARD_CONSTELLATION.name());
		jzKillboardConstellation.addActionListener(listener);
		jzKillboard.add(jzKillboardConstellation);

		jzKillboardRegion = new JMenuItem(GuiShared.get().region());
		jzKillboardRegion.setIcon(Images.LOC_REGION.getIcon());
		jzKillboardRegion.setActionCommand(LookupLinks.ZKILLBOARD_REGION.name());
		jzKillboardRegion.addActionListener(listener);
		jzKillboard.add(jzKillboardRegion);

		jzKillboardLocations = new JMenuItem(TabsOverview.get().locations());
		jzKillboardLocations.setIcon(Images.LOC_REGION.getIcon());
		jzKillboardLocations.setActionCommand(LookupLinks.ZKILLBOARD_OVERVIEW_GROUP.name());
		jzKillboardLocations.addActionListener(listener);

		jDotlan = new JMenu(GuiShared.get().dotlan());
		jDotlan.setIcon(Images.LINK_DOTLAN_EVEMAPS.getIcon());
		jLocations.add(jDotlan);

		jDotlanStation = new JMenuItem(GuiShared.get().station());
		jDotlanStation.setIcon(Images.LOC_STATION.getIcon());
		jDotlanStation.setActionCommand(LookupLinks.EVEMAPS_DOTLAN_STATION.name());
		jDotlanStation.addActionListener(listener);
		jDotlan.add(jDotlanStation);

		jDotlanPlanet = new JMenuItem(GuiShared.get().planet());
		jDotlanPlanet.setIcon(Images.LOC_PLANET.getIcon());
		jDotlanPlanet.setActionCommand(LookupLinks.EVEMAPS_DOTLAN_PLANET.name());
		jDotlanPlanet.addActionListener(listener);
		jDotlan.add(jDotlanPlanet);

		jDotlanSystem = new JMenuItem(GuiShared.get().system());
		jDotlanSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jDotlanSystem.setActionCommand(LookupLinks.EVEMAPS_DOTLAN_SYSTEM.name());
		jDotlanSystem.addActionListener(listener);
		jDotlan.add(jDotlanSystem);

		jDotlanSystemRegion = new JMenuItem(GuiShared.get().systemRegion());
		jDotlanSystemRegion.setIcon(Images.LOC_SYSTEM.getIcon());
		jDotlanSystemRegion.setActionCommand(LookupLinks.EVEMAPS_DOTLAN_SYSTEM_REGION.name());
		jDotlanSystemRegion.addActionListener(listener);
		jDotlan.add(jDotlanSystemRegion);

		jDotlanConstellation = new JMenuItem(GuiShared.get().constellation());
		jDotlanConstellation.setIcon(Images.LOC_CONSTELLATION.getIcon());
		jDotlanConstellation.setActionCommand(LookupLinks.EVEMAPS_DOTLAN_CONSTELLATION.name());
		jDotlanConstellation.addActionListener(listener);
		jDotlan.add(jDotlanConstellation);

		jDotlanRegion = new JMenuItem(GuiShared.get().region());
		jDotlanRegion.setIcon(Images.LOC_REGION.getIcon());
		jDotlanRegion.setActionCommand(LookupLinks.EVEMAPS_DOTLAN_REGION.name());
		jDotlanRegion.addActionListener(listener);
		jDotlan.add(jDotlanRegion);

		jDotlanLocations = new JMenuItem(TabsOverview.get().locations());
		jDotlanLocations.setIcon(Images.LOC_LOCATIONS.getIcon());
		jDotlanLocations.setActionCommand(LookupLinks.EVEMAPS_DOTLAN_OVERVIEW_GROUP.name());
		jDotlanLocations.addActionListener(listener);

		jEveMissioneer = new JMenu(GuiShared.get().eveMissioneer());
		jEveMissioneer.setIcon(Images.LINK_EVEMISSIONEER.getIcon());
		jLocations.add(jEveMissioneer);

		jEveMissioneerSystem = new JMenuItem(GuiShared.get().system());
		jEveMissioneerSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jEveMissioneerSystem.setActionCommand(LookupLinks.EVEMISSIONEER_SYSTEM.name());
		jEveMissioneerSystem.addActionListener(listener);
		jEveMissioneer.add(jEveMissioneerSystem);

		jEveMissioneerConstellation = new JMenuItem(GuiShared.get().constellation());
		jEveMissioneerConstellation.setIcon(Images.LOC_CONSTELLATION.getIcon());
		jEveMissioneerConstellation.setActionCommand(LookupLinks.EVEMISSIONEER_CONSTELLATION.name());
		jEveMissioneerConstellation.addActionListener(listener);
		jEveMissioneer.add(jEveMissioneerConstellation);

		jEveMissioneerRegion = new JMenuItem(GuiShared.get().region());
		jEveMissioneerRegion.setIcon(Images.LOC_REGION.getIcon());
		jEveMissioneerRegion.setActionCommand(LookupLinks.EVEMISSIONEER_REGION.name());
		jEveMissioneerRegion.addActionListener(listener);
		jEveMissioneer.add(jEveMissioneerRegion);

		jMarket = new JMenu(GuiShared.get().market());
		jMarket.setIcon(Images.ORDERS_SELL.getIcon());
		add(jMarket);

		jFuzzworkMarket = new JMenuItem(GuiShared.get().fuzzworkMarket());
		jFuzzworkMarket.setIcon(Images.LINK_FUZZWORK.getIcon());
		jFuzzworkMarket.setActionCommand(LookupLinks.FUZZWORK_MARKET.name());
		jFuzzworkMarket.addActionListener(listener);
		jMarket.add(jFuzzworkMarket);

		jEveTycoon = new JMenuItem(GuiShared.get().eveTycoon());
		jEveTycoon.setIcon(Images.LINK_EVE_TYCOON.getIcon());
		jEveTycoon.setActionCommand(LookupLinks.EVE_TYCOON.name());
		jEveTycoon.addActionListener(listener);
		jMarket.add(jEveTycoon);

		jAdam4eve = new JMenuItem(GuiShared.get().adam4eve());
		jAdam4eve.setIcon(Images.LINK_ADAM4EVE.getIcon());
		jAdam4eve.setActionCommand(LookupLinks.ADAM4EVE.name());
		jAdam4eve.addActionListener(listener);
		jMarket.add(jAdam4eve);

		jEveMarketBrowser = new JMenuItem(GuiShared.get().eveMarketBrowser());
		jEveMarketBrowser.setIcon(Images.LINK_EVE_MARKET_BROWSER.getIcon());
		jEveMarketBrowser.setActionCommand(LookupLinks.EVE_MARKET_BROWSER.name());
		jEveMarketBrowser.addActionListener(listener);
		jMarket.add(jEveMarketBrowser);

		jJitaSpaceMarket = new JMenuItem(GuiShared.get().jitaSpace());
		jJitaSpaceMarket.setIcon(Images.LINK_JITA_SPACE.getIcon());
		jJitaSpaceMarket.setActionCommand(LookupLinks.JITA_SPACE_MARKET.name());
		jJitaSpaceMarket.addActionListener(listener);
		jMarket.add(jJitaSpaceMarket);

		jEveconomy = new JMenuItem(GuiShared.get().eveconomy());
		jEveconomy.setIcon(Images.LINK_EVECONOMY.getIcon());
		jEveconomy.setActionCommand(LookupLinks.EVECONOMY.name());
		jEveconomy.addActionListener(listener);
		jMarket.add(jEveconomy);

		jItemDatabase = new JMenu(GuiShared.get().itemDatabase());
		jItemDatabase.setIcon(Images.TOOL_ASSETS.getIcon());
		add(jItemDatabase);

		jChruker = new JMenuItem(GuiShared.get().chruker());
		jChruker.setIcon(Images.LINK_CHRUKER.getIcon());
		jChruker.setActionCommand(LookupLinks.GAMES_CHRUKER.name());
		jChruker.addActionListener(listener);
		jItemDatabase.add(jChruker);

		jEveInfo = new JMenuItem(GuiShared.get().eveInfo());
		jEveInfo.setIcon(Images.LINK_EVEINFO.getIcon());
		jEveInfo.setActionCommand(LookupLinks.EVE_INFO.name());
		jEveInfo.addActionListener(listener);
		jItemDatabase.add(jEveInfo);

		jFuzzworkItems = new JMenuItem(GuiShared.get().fuzzworkItems());
		jFuzzworkItems.setIcon(Images.LINK_FUZZWORK.getIcon());
		jFuzzworkItems.setActionCommand(LookupLinks.FUZZWORK_ITEMS.name());
		jFuzzworkItems.addActionListener(listener);
		jItemDatabase.add(jFuzzworkItems);

		jzKillboardItem = new JMenuItem(GuiShared.get().zKillboard());
		jzKillboardItem.setIcon(Images.LINK_ZKILLBOARD.getIcon());
		jzKillboardItem.setActionCommand(LookupLinks.ZKILLBOARD_ITEM.name());
		jzKillboardItem.addActionListener(listener);
		jItemDatabase.add(jzKillboardItem);

		jEveRef = new JMenuItem(GuiShared.get().eveRef());
		jEveRef.setIcon(Images.LINK_EVE_REF.getIcon());
		jEveRef.setActionCommand(LookupLinks.EVE_REF.name());
		jEveRef.addActionListener(listener);
		jItemDatabase.add(jEveRef);

		jJitaSpaceItem = new JMenuItem(GuiShared.get().jitaSpace());
		jJitaSpaceItem.setIcon(Images.LINK_JITA_SPACE.getIcon());
		jJitaSpaceItem.setActionCommand(LookupLinks.JITA_SPACE_ITEM.name());
		jJitaSpaceItem.addActionListener(listener);
		jItemDatabase.add(jJitaSpaceItem);

		jIndustry = new JMenu(GuiShared.get().industry());
		jIndustry.setIcon(Images.TOOL_INDUSTRY_JOBS.getIcon());
		add(jIndustry);

		jFuzzworkBlueprints = new JMenuItem(GuiShared.get().fuzzworkBlueprints());
		jFuzzworkBlueprints.setIcon(Images.LINK_FUZZWORK.getIcon());
		jFuzzworkBlueprints.setActionCommand(LookupLinks.FUZZWORK_BLUEPRINTS.name());
		jFuzzworkBlueprints.addActionListener(listener);
		jIndustry.add(jFuzzworkBlueprints);

		JMenu jKhonSpace = new JMenu(GuiShared.get().lazyBlacksmith());
		jKhonSpace.setIcon(Images.LINK_KHON_SPACE.getIcon());
		jIndustry.add(jKhonSpace);

		jLazyBlacksmithInvention = new JMenuItem(GuiShared.get().lazyBlacksmithInvention());
		jLazyBlacksmithInvention.setIcon(Images.MISC_INVENTION.getIcon());
		jLazyBlacksmithInvention.setActionCommand(LookupLinks.LAZY_BLACKSMITH_INVENTION.name());
		jLazyBlacksmithInvention.addActionListener(listener);
		jKhonSpace.add(jLazyBlacksmithInvention);

		jLazyBlacksmithResearch = new JMenuItem(GuiShared.get().lazyBlacksmithResearch());
		jLazyBlacksmithResearch.setIcon(Images.MISC_COPYING.getIcon());
		jLazyBlacksmithResearch.setActionCommand(LookupLinks.LAZY_BLACKSMITH_RESEARCH.name());
		jLazyBlacksmithResearch.addActionListener(listener);
		jKhonSpace.add(jLazyBlacksmithResearch);

		jLazyBlacksmithManufacturing = new JMenuItem(GuiShared.get().lazyBlacksmithManufacturing());
		jLazyBlacksmithManufacturing.setIcon(Images.MISC_MANUFACTURING.getIcon());
		jLazyBlacksmithManufacturing.setActionCommand(LookupLinks.LAZY_BLACKSMITH_MANUFACTURING.name());
		jLazyBlacksmithManufacturing.addActionListener(listener);
		jKhonSpace.add(jLazyBlacksmithManufacturing);

		jEveCookbook = new JMenuItem(GuiShared.get().eveCookbook());
		jEveCookbook.setIcon(Images.LINK_EVE_COOKBOOK.getIcon());
		jEveCookbook.setActionCommand(LookupLinks.EVE_COOKBOOK.name());
		jEveCookbook.addActionListener(listener);
		jIndustry.add(jEveCookbook);
	}

	@Override
	public void updateMenuData() {
	//Location
		jLocations.setEnabled(!menuData.getStationNames().isEmpty() || !menuData.getSystemNames().isEmpty() || !menuData.getRegionNames().isEmpty());
		jDotlanStation.setEnabled(!menuData.getStationNames().isEmpty());
		jDotlanPlanet.setEnabled(!menuData.getPlanetNames().isEmpty());
		jDotlanSystem.setEnabled(!menuData.getSystemNames().isEmpty());
		jDotlanConstellation.setEnabled(!menuData.getConstellationLocations().isEmpty());
		jDotlanRegion.setEnabled(!menuData.getRegionNames().isEmpty());
		jzKillboardSystem.setEnabled(!menuData.getSystemLocations().isEmpty());
		jzKillboardConstellation.setEnabled(!menuData.getConstellationLocations().isEmpty());
		jzKillboardRegion.setEnabled(!menuData.getRegionLocations().isEmpty());
		jEveMissioneerSystem.setEnabled(!menuData.getSystemLocations().isEmpty());
		jEveMissioneerConstellation.setEnabled(!menuData.getConstellationLocations().isEmpty());
		jEveMissioneerRegion.setEnabled(!menuData.getRegionLocations().isEmpty());
	//Market
		jMarket.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jFuzzworkMarket.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveTycoon.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jAdam4eve.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveMarketBrowser.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jJitaSpaceMarket.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
		jEveconomy.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
	//Info
		jItemDatabase.setEnabled(!menuData.getTypeIDs().isEmpty());
		jFuzzworkItems.setEnabled(!menuData.getTypeIDs().isEmpty());
		jzKillboardItem.setEnabled(!menuData.getTypeIDs().isEmpty());
		jChruker.setEnabled(!menuData.getTypeIDs().isEmpty());
		jEveInfo.setEnabled(!menuData.getTypeIDs().isEmpty());
	//Industry
		jIndustry.setEnabled(!menuData.getBlueprintTypeIDs().isEmpty());
		jFuzzworkBlueprints.setEnabled(!menuData.getBlueprintTypeIDs().isEmpty());
		jLazyBlacksmithInvention.setEnabled(!menuData.getInventionTypeIDs().isEmpty());
		jLazyBlacksmithResearch.setEnabled(!menuData.getBlueprintTypeIDs().isEmpty());
		jLazyBlacksmithManufacturing.setEnabled(!menuData.getBlueprintTypeIDs().isEmpty());
	}

	public void setTool(Object object) {
		if (object instanceof OverviewTableMenu) {
			OverviewTab overviewTab = ((OverviewTableMenu)object).getOverviewTab();
			boolean enabled = overviewTab.isGroupAndNotEmpty();
			jDotlanLocations.setEnabled(enabled);
			jDotlan.add(jDotlanLocations);

			jzKillboardLocations.setEnabled(enabled);
			jzKillboard.add(jzKillboardLocations);

			jLocations.setEnabled(enabled || !overviewTab.isGroup());
		} else {
			jDotlan.remove(jDotlanLocations);
			jzKillboard.remove(jzKillboardLocations);
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
		//Locations
			if (LookupLinks.ZKILLBOARD_SYSTEM.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.ZKILLBOARD_SYSTEM.getLinks(menuData), program);
			} else if (LookupLinks.ZKILLBOARD_REGION.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.ZKILLBOARD_REGION.getLinks(menuData), program);
			} else if (LookupLinks.ZKILLBOARD_CONSTELLATION.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.ZKILLBOARD_CONSTELLATION.getLinks(menuData), program);
			} else if (LookupLinks.ZKILLBOARD_OVERVIEW_GROUP.name().equals(e.getActionCommand())) {
				OverviewGroup overviewGroup = program.getOverviewTab().getSelectGroup();
				if (overviewGroup == null) {
					return;
				}
				MenuData<String> menuData = new MenuData<>();
				for (OverviewLocation location : overviewGroup.getLocations()) {
					if (location.isStation()) {
						continue;
					}
					if (location.isPlanet()) {
						continue;
					}
					for (MyLocation myLocation : StaticData.get().getLocations()) {
						if (!myLocation.getLocation().equals(location.getName())) {
							continue; //Not the location you're looking for
						}
						if (location.isSystem()) {
							menuData.getSystemLocations().add(myLocation);
						}
						if (location.isConstellation()) {
							menuData.getConstellationLocations().add(myLocation);
						}
						if (location.isRegion()) {
							menuData.getRegionLocations().add(myLocation);
						}
						break; //Location found
					}
				}
				Set<String> urls = new HashSet<>();
				urls.addAll(LookupLinks.ZKILLBOARD_SYSTEM.getLinks(menuData));
				urls.addAll(LookupLinks.ZKILLBOARD_CONSTELLATION.getLinks(menuData));
				urls.addAll(LookupLinks.ZKILLBOARD_REGION.getLinks(menuData));
				DesktopUtil.browse(urls, program);
			}else if (LookupLinks.EVEMAPS_DOTLAN_STATION.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVEMAPS_DOTLAN_STATION.getLinks(menuData), program);
			} else if (LookupLinks.EVEMAPS_DOTLAN_PLANET.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVEMAPS_DOTLAN_PLANET.getLinks(menuData), program);
			} else if (LookupLinks.EVEMAPS_DOTLAN_SYSTEM.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVEMAPS_DOTLAN_SYSTEM.getLinks(menuData), program);
			} else if (LookupLinks.EVEMAPS_DOTLAN_SYSTEM_REGION.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVEMAPS_DOTLAN_SYSTEM_REGION.getLinks(menuData), program);
			} else if (LookupLinks.EVEMAPS_DOTLAN_CONSTELLATION.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVEMAPS_DOTLAN_CONSTELLATION.getLinks(menuData), program);
			} else if (LookupLinks.EVEMAPS_DOTLAN_REGION.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVEMAPS_DOTLAN_REGION.getLinks(menuData), program);
			} else if (LookupLinks.EVEMAPS_DOTLAN_OVERVIEW_GROUP.name().equals(e.getActionCommand())) {
				OverviewGroup overviewGroup = program.getOverviewTab().getSelectGroup();
				if (overviewGroup == null) {
					return;
				}
				MenuData<String> menuData = new MenuData<>();
				for (OverviewLocation location : overviewGroup.getLocations()) {
					if (location.isStation()) {
						menuData.getStationNames().add(location.getName());
					}
					if (location.isPlanet()) {
						menuData.getPlanetNames().add(location.getName());
					}
					if (location.isSystem()) {
						menuData.getSystemNames().add(location.getName());
					}
					if (location.isRegion()) {
						menuData.getRegionNames().add(location.getName());
					}
					for (MyLocation myLocation : StaticData.get().getLocations()) {
						if (!myLocation.getLocation().equals(location.getName())) {
							continue; //Not the location you're looking for
						}
						if (location.isConstellation()) {
							menuData.getConstellationLocations().add(myLocation);
						}
						break; //Location found
					}
				}
				Set<String> urls = new HashSet<>();
				urls.addAll(LookupLinks.EVEMAPS_DOTLAN_STATION.getLinks(menuData));
				urls.addAll(LookupLinks.EVEMAPS_DOTLAN_PLANET.getLinks(menuData));
				urls.addAll(LookupLinks.EVEMAPS_DOTLAN_SYSTEM.getLinks(menuData));
				urls.addAll(LookupLinks.EVEMAPS_DOTLAN_CONSTELLATION.getLinks(menuData));
				urls.addAll(LookupLinks.EVEMAPS_DOTLAN_REGION.getLinks(menuData));
				DesktopUtil.browse(urls, program);
			} else if (LookupLinks.EVEMISSIONEER_SYSTEM.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVEMISSIONEER_SYSTEM.getLinks(menuData), program);
			} else if (LookupLinks.EVEMISSIONEER_CONSTELLATION.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVEMISSIONEER_CONSTELLATION.getLinks(menuData), program);
			} else if (LookupLinks.EVEMISSIONEER_REGION.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVEMISSIONEER_REGION.getLinks(menuData), program);
		//Market
			} else if (LookupLinks.ADAM4EVE.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.ADAM4EVE.getLinks(menuData), program);
			} else if (LookupLinks.FUZZWORK_MARKET.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.FUZZWORK_MARKET.getLinks(menuData), program);
			} else if (LookupLinks.EVE_TYCOON.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVE_TYCOON.getLinks(menuData), program);
			} else if (LookupLinks.EVE_MARKET_BROWSER.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVE_MARKET_BROWSER.getLinks(menuData), program);
			} else if (LookupLinks.JITA_SPACE_MARKET.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.JITA_SPACE_MARKET.getLinks(menuData), program);
			} else if (LookupLinks.EVECONOMY.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVECONOMY.getLinks(menuData), program);
		//Info
			} else if (LookupLinks.GAMES_CHRUKER.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.GAMES_CHRUKER.getLinks(menuData), program);
			} else if (LookupLinks.FUZZWORK_ITEMS.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.FUZZWORK_ITEMS.getLinks(menuData), program);
			} else if (LookupLinks.ZKILLBOARD_ITEM.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.ZKILLBOARD_ITEM.getLinks(menuData), program);
			} else if (LookupLinks.EVE_REF.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVE_REF.getLinks(menuData), program);
			} else if (LookupLinks.EVE_INFO.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVE_INFO.getLinks(menuData), program);
			} else if (LookupLinks.JITA_SPACE_ITEM.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.JITA_SPACE_ITEM.getLinks(menuData), program);
		//Industry
			} else if (LookupLinks.FUZZWORK_BLUEPRINTS.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.FUZZWORK_BLUEPRINTS.getLinks(menuData), program);
			} else if (LookupLinks.LAZY_BLACKSMITH_INVENTION.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.LAZY_BLACKSMITH_INVENTION.getLinks(menuData), program);
			} else if (LookupLinks.LAZY_BLACKSMITH_MANUFACTURING.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.LAZY_BLACKSMITH_MANUFACTURING.getLinks(menuData), program);
			} else if (LookupLinks.LAZY_BLACKSMITH_RESEARCH.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.LAZY_BLACKSMITH_RESEARCH.getLinks(menuData), program);
			} else if (LookupLinks.EVE_COOKBOOK.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(LookupLinks.EVE_COOKBOOK.getLinks(menuData), program);
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
