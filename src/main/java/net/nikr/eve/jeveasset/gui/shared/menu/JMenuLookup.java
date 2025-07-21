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
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.Icon;
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
		ZKILLBOARD_SYSTEM(GuiShared.get().system(), Images.LOC_SYSTEM.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getSystemLocations()) {
					urls.add("https://zkillboard.com/system/" +location.getLocationID() + "/");
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getSystemLocations().isEmpty();
			}
		},
		ZKILLBOARD_CONSTELLATION(GuiShared.get().constellation(), Images.LOC_CONSTELLATION.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getConstellationLocations()) {
					urls.add("https://zkillboard.com/constellation/" +location.getLocationID() + "/");
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getConstellationLocations().isEmpty();
			}
		},
		ZKILLBOARD_REGION(GuiShared.get().region(), Images.LOC_REGION.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getRegionLocations()) {
					urls.add("https://zkillboard.com/region/" +location.getLocationID() + "/");
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getRegionLocations().isEmpty();
			}
		},
		ZKILLBOARD_OVERVIEW_GROUP(TabsOverview.get().locations(), Images.LOC_REGION.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				return Collections.emptySet();
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return null;
			}

			@Override
			public void open(Program program, MenuData<?> menuData) {
				OverviewGroup overviewGroup = program.getOverviewTab(true).getSelectGroup();
				if (overviewGroup == null) {
					return;
				}
				MenuData<String> data = new MenuData<>();
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
							data.getSystemLocations().add(myLocation);
						}
						if (location.isConstellation()) {
							data.getConstellationLocations().add(myLocation);
						}
						if (location.isRegion()) {
							data.getRegionLocations().add(myLocation);
						}
						break; //Location found
					}
				}
				Set<String> urls = new HashSet<>();
				urls.addAll(LookupLinks.ZKILLBOARD_SYSTEM.getLinks(data));
				urls.addAll(LookupLinks.ZKILLBOARD_CONSTELLATION.getLinks(data));
				urls.addAll(LookupLinks.ZKILLBOARD_REGION.getLinks(data));
				DesktopUtil.browse(urls, program);
			}
		},
		EVEMAPS_DOTLAN_STATION(GuiShared.get().station(), Images.LOC_STATION.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (String station : menuData.getStationNames()) {
					urls.add("https://evemaps.dotlan.net/station/" + station.replace(" ", "_"));
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getStationNames().isEmpty();
			}
		},
		EVEMAPS_DOTLAN_PLANET(GuiShared.get().planet(), Images.LOC_PLANET.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (String planet : menuData.getPlanetNames()) {
					urls.add("https://evemaps.dotlan.net/system/" + replaceLast(planet, " ", "/").replace(" ", "_"));
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getPlanetNames().isEmpty();
			}
		},
		EVEMAPS_DOTLAN_SYSTEM(GuiShared.get().system(), Images.LOC_SYSTEM.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (String system : menuData.getSystemNames()) {
					urls.add("https://evemaps.dotlan.net/system/" + system.replace(" ", "_"));
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getSystemNames().isEmpty();
			}
		},
		EVEMAPS_DOTLAN_SYSTEM_REGION(GuiShared.get().systemRegion(), Images.LOC_SYSTEM.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation system : menuData.getSystemLocations()) {
					urls.add("https://evemaps.dotlan.net/map/" + system.getRegion().replace(" ", "_")+ "/" + system.getSystem().replace(" ", "_"));
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getSystemLocations().isEmpty();
			}
		},
		EVEMAPS_DOTLAN_CONSTELLATION(GuiShared.get().constellation(), Images.LOC_CONSTELLATION.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation constellation : menuData.getConstellationLocations()) {
					urls.add("https://evemaps.dotlan.net/map/" + constellation.getRegion().replace(" ", "_") + "/" + constellation.getConstellation().replace(" ", "_"));
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getConstellationLocations().isEmpty();
			}
		},
		EVEMAPS_DOTLAN_REGION(GuiShared.get().region(), Images.LOC_REGION.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (String region : menuData.getRegionNames()) {
					urls.add("https://evemaps.dotlan.net/map/" + region.replace(" ", "_"));
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getRegionNames().isEmpty();
			}
		},
		EVEMAPS_DOTLAN_OVERVIEW_GROUP(TabsOverview.get().locations(), Images.LOC_LOCATIONS.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				return Collections.emptySet();
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return null;
			}

			@Override
			public void open(Program program, MenuData<?> menuData) {
				OverviewGroup overviewGroup = program.getOverviewTab(true).getSelectGroup();
				if (overviewGroup == null) {
					return;
				}
				MenuData<String> data = new MenuData<>();
				for (OverviewLocation location : overviewGroup.getLocations()) {
					if (location.isStation()) {
						data.getStationNames().add(location.getName());
					}
					if (location.isPlanet()) {
						data.getPlanetNames().add(location.getName());
					}
					if (location.isSystem()) {
						data.getSystemNames().add(location.getName());
					}
					if (location.isRegion()) {
						data.getRegionNames().add(location.getName());
					}
					for (MyLocation myLocation : StaticData.get().getLocations()) {
						if (!myLocation.getLocation().equals(location.getName())) {
							continue; //Not the location you're looking for
						}
						if (location.isConstellation()) {
							data.getConstellationLocations().add(myLocation);
						}
						break; //Location found
					}
				}
				Set<String> urls = new HashSet<>();
				urls.addAll(LookupLinks.EVEMAPS_DOTLAN_STATION.getLinks(data));
				urls.addAll(LookupLinks.EVEMAPS_DOTLAN_PLANET.getLinks(data));
				urls.addAll(LookupLinks.EVEMAPS_DOTLAN_SYSTEM.getLinks(data));
				urls.addAll(LookupLinks.EVEMAPS_DOTLAN_CONSTELLATION.getLinks(data));
				urls.addAll(LookupLinks.EVEMAPS_DOTLAN_REGION.getLinks(data));
				DesktopUtil.browse(urls, program);
			}
		},
		EVEMISSIONEER_SYSTEM(GuiShared.get().system(), Images.LOC_SYSTEM.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getSystemLocations()) {
					urls.add("https://evemissioneer.com/s/" +location.getLocationID());
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getSystemLocations().isEmpty();
			}
		},
		EVEMISSIONEER_CONSTELLATION(GuiShared.get().constellation(), Images.LOC_CONSTELLATION.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getConstellationLocations()) {
					urls.add("https://evemissioneer.com/c/" +location.getLocationID());
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getConstellationLocations().isEmpty();
			}
		},
		EVEMISSIONEER_REGION(GuiShared.get().region(), Images.LOC_REGION.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (MyLocation location : menuData.getRegionLocations()) {
					urls.add("https://evemissioneer.com/r/" +location.getLocationID());
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getRegionLocations().isEmpty();
			}
		},
	//Market
		FUZZWORK_MARKET(GuiShared.get().fuzzworkMarket(), Images.LINK_FUZZWORK.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://market.fuzzwork.co.uk/hub/type/" + marketTypeID + "/");
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getMarketTypeIDs().isEmpty();
			}
		},
		EVE_TYCOON(GuiShared.get().eveTycoon(), Images.LINK_EVE_TYCOON.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://evetycoon.com/market/" + marketTypeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getMarketTypeIDs().isEmpty();
			}
		},
		ADAM4EVE(GuiShared.get().adam4eve(), Images.LINK_ADAM4EVE.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://www.adam4eve.eu/commodity.php?typeID=" + marketTypeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getMarketTypeIDs().isEmpty();
			}
		},
		EVE_MARKET_BROWSER(GuiShared.get().eveMarketBrowser(), Images.LINK_EVE_MARKET_BROWSER.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://evemarketbrowser.com/region/0/type/" + marketTypeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getMarketTypeIDs().isEmpty();
			}
		},
		JITA_SPACE_MARKET (GuiShared.get().jitaSpace(), Images.LINK_JITA_SPACE.getIcon()){
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://www.jita.space/market/" + marketTypeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getMarketTypeIDs().isEmpty();
			}
		},
		EVECONOMY(GuiShared.get().eveconomy(), Images.LINK_EVECONOMY.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int marketTypeID : menuData.getMarketTypeIDs()) {
					urls.add("https://eveconomy.online/item/" + marketTypeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getMarketTypeIDs().isEmpty();
			}
		},
	//Info
		GAMES_CHRUKER(GuiShared.get().chruker(), Images.LINK_CHRUKER.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("http://games.chruker.dk/eve_online/item.php?type_id=" + typeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getTypeIDs().isEmpty();
			}
		},
		EVE_INFO(GuiShared.get().eveInfo(), Images.LINK_EVEINFO.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://eveinfo.com/item/" + typeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getTypeIDs().isEmpty();
			}
		},
		FUZZWORK_ITEMS(GuiShared.get().fuzzworkItems(), Images.LINK_FUZZWORK.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://www.fuzzwork.co.uk/info/?typeid=" + typeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getTypeIDs().isEmpty();
			}
		},
		ZKILLBOARD_ITEM(GuiShared.get().zKillboard(), Images.LINK_ZKILLBOARD.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://zkillboard.com/item/" + typeID + "/");
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getTypeIDs().isEmpty();
			}
		},
		EVE_REF(GuiShared.get().eveRef(), Images.LINK_EVE_REF.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://everef.net/types/" + typeID+ "?utm_source=jeveassets");
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getTypeIDs().isEmpty();
			}
		},
		JITA_SPACE_ITEM(GuiShared.get().jitaSpace(), Images.LINK_JITA_SPACE.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getTypeIDs()) {
					urls.add("https://www.jita.space/type/" + typeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getTypeIDs().isEmpty();
			}
		},
	//Industry
		FUZZWORK_BLUEPRINTS(GuiShared.get().fuzzworkBlueprints(), Images.LINK_FUZZWORK.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://www.fuzzwork.co.uk/blueprint/?typeid=" + typeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getBlueprintTypeIDs().isEmpty();
			}
		},
		LAZY_BLACKSMITH_INVENTION(GuiShared.get().lazyBlacksmithInvention(), Images.MISC_INVENTION.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getInventionTypeIDs()) {
					urls.add("https://lzb.eveskillboard.com/blueprint/invention/" + typeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getInventionTypeIDs().isEmpty();
			}
		},
		LAZY_BLACKSMITH_RESEARCH(GuiShared.get().lazyBlacksmithResearch(), Images.MISC_COPYING.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://lzb.eveskillboard.com/blueprint/research_copy/" + typeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getBlueprintTypeIDs().isEmpty();
			}
		},
		LAZY_BLACKSMITH_MANUFACTURING(GuiShared.get().lazyBlacksmithManufacturing(), Images.MISC_MANUFACTURING.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://lzb.eveskillboard.com/blueprint/manufacturing/" + typeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getBlueprintTypeIDs().isEmpty();
			}
		},
		EVE_COOKBOOK(GuiShared.get().eveCookbook(), Images.LINK_EVE_COOKBOOK.getIcon()) {
			@Override
			public Set<String> getLinks(MenuData<?> menuData) {
				Set<String> urls = new HashSet<>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					urls.add("https://evecookbook.com/?blueprintTypeId=" + typeID);
				}
				return urls;
			}

			@Override
			public <T> Boolean isEnabled(MenuData<T> menuData) {
				return !menuData.getBlueprintTypeIDs().isEmpty();
			}
		};

		private final String title;
		private final Icon icon;

		private LookupLinks(String title, Icon icon) {
			this.title = title;
			this.icon = icon;
		}

		public abstract Set<String> getLinks(MenuData<?> menuData);
		public abstract <T> Boolean isEnabled(MenuData<T> menuData);

		public JMenuItem createMenuItem(ActionListener listener) {
			JMenuItem jMenuItem = new JMenuItem(title);
			jMenuItem.setIcon(icon);
			jMenuItem.setActionCommand(name());
			jMenuItem.addActionListener(listener);
			return jMenuItem;
		}

		public void open(Program program, MenuData<?> menuData) {
			DesktopUtil.browse(getLinks(menuData), program);
		}
	}

	private final JMenu jLocations;
	private final JMenu jzKillboard;
	private final JMenuItem jzKillboardLocations;
	private final JMenu jDotlan;
	private final JMenuItem jDotlanLocations;
	private final JMenu jEveMissioneer;
	private final JMenu jMarket;
	private final JMenu jItemDatabase;
	private final JMenu jIndustry;
	private final Map<LookupLinks, JMenuItem> jMenuItems = new EnumMap<>(LookupLinks.class);

	public JMenuLookup(final Program program) {
		super(GuiShared.get().lookup(), program);

		ListenerClass listener = new ListenerClass();

		this.setIcon(Images.LINK_LOOKUP.getIcon());
//Locations
		jLocations = new JMenu(GuiShared.get().location());
		jLocations.setIcon(Images.LOC_LOCATIONS.getIcon());
		add(jLocations);

	//zKillboard
		jzKillboard = new JMenu(GuiShared.get().zKillboard());
		jzKillboard.setIcon(Images.LINK_ZKILLBOARD.getIcon());
		jLocations.add(jzKillboard);
		add(jzKillboard, LookupLinks.ZKILLBOARD_SYSTEM, listener);
		add(jzKillboard, LookupLinks.ZKILLBOARD_CONSTELLATION, listener);
		add(jzKillboard, LookupLinks.ZKILLBOARD_REGION, listener);
		jzKillboardLocations = add(jzKillboard, LookupLinks.ZKILLBOARD_OVERVIEW_GROUP, listener);
	//Dotlan
		jDotlan = new JMenu(GuiShared.get().dotlan());
		jDotlan.setIcon(Images.LINK_DOTLAN_EVEMAPS.getIcon());
		jLocations.add(jDotlan);
		add(jDotlan, LookupLinks.EVEMAPS_DOTLAN_PLANET, listener);
		add(jDotlan, LookupLinks.EVEMAPS_DOTLAN_SYSTEM, listener);
		add(jDotlan, LookupLinks.EVEMAPS_DOTLAN_SYSTEM_REGION, listener);
		add(jDotlan, LookupLinks.EVEMAPS_DOTLAN_CONSTELLATION, listener);
		add(jDotlan, LookupLinks.EVEMAPS_DOTLAN_REGION, listener);
		jDotlanLocations = add(jDotlan, LookupLinks.EVEMAPS_DOTLAN_OVERVIEW_GROUP, listener);
	//EveMissioneer
		jEveMissioneer = new JMenu(GuiShared.get().eveMissioneer());
		jEveMissioneer.setIcon(Images.LINK_EVEMISSIONEER.getIcon());
		jLocations.add(jEveMissioneer);
		add(jEveMissioneer, LookupLinks.EVEMISSIONEER_SYSTEM, listener);
		add(jEveMissioneer, LookupLinks.EVEMISSIONEER_CONSTELLATION, listener);
		add(jEveMissioneer, LookupLinks.EVEMISSIONEER_REGION, listener);

//Market
		jMarket = new JMenu(GuiShared.get().market());
		jMarket.setIcon(Images.ORDERS_SELL.getIcon());
		add(jMarket);
		add(jMarket, LookupLinks.FUZZWORK_MARKET, listener);
		add(jMarket, LookupLinks.EVE_TYCOON, listener);
		add(jMarket, LookupLinks.ADAM4EVE, listener);
		add(jMarket, LookupLinks.EVE_MARKET_BROWSER, listener);
		add(jMarket, LookupLinks.JITA_SPACE_MARKET, listener);
		add(jMarket, LookupLinks.EVECONOMY, listener);

//Items
		jItemDatabase = new JMenu(GuiShared.get().itemDatabase());
		jItemDatabase.setIcon(Images.TOOL_ASSETS.getIcon());
		add(jItemDatabase);
		add(jItemDatabase, LookupLinks.GAMES_CHRUKER, listener);
		add(jItemDatabase, LookupLinks.EVE_INFO, listener);
		add(jItemDatabase, LookupLinks.FUZZWORK_ITEMS, listener);
		add(jItemDatabase, LookupLinks.ZKILLBOARD_ITEM, listener);
		add(jItemDatabase, LookupLinks.EVE_REF, listener);
		add(jItemDatabase, LookupLinks.JITA_SPACE_ITEM, listener);

//Industry
		jIndustry = new JMenu(GuiShared.get().industry());
		jIndustry.setIcon(Images.TOOL_INDUSTRY_JOBS.getIcon());
		add(jIndustry);

		add(jIndustry, LookupLinks.FUZZWORK_BLUEPRINTS, listener);
	//KhonSpace
		JMenu jKhonSpace = new JMenu(GuiShared.get().lazyBlacksmith());
		jKhonSpace.setIcon(Images.LINK_KHON_SPACE.getIcon());
		jIndustry.add(jKhonSpace);
		add(jKhonSpace, LookupLinks.LAZY_BLACKSMITH_INVENTION, listener);
		add(jKhonSpace, LookupLinks.LAZY_BLACKSMITH_RESEARCH, listener);
		add(jKhonSpace, LookupLinks.LAZY_BLACKSMITH_MANUFACTURING, listener);
	//Other
		add(jIndustry, LookupLinks.EVE_COOKBOOK, listener);
	}

	@Override
	public void updateMenuData() {
		for (Map.Entry<LookupLinks, JMenuItem> entry : jMenuItems.entrySet()) {
			Boolean enabled = entry.getKey().isEnabled(menuData);
			if (enabled != null) {
				entry.getValue().setEnabled(enabled);
			}
		}
	//Location
		jLocations.setEnabled(!menuData.getStationNames().isEmpty() || !menuData.getSystemNames().isEmpty() || !menuData.getRegionNames().isEmpty());
	//Market
		jMarket.setEnabled(!menuData.getMarketTypeIDs().isEmpty());
	//Info
		jItemDatabase.setEnabled(!menuData.getTypeIDs().isEmpty());
	//Industry
		jIndustry.setEnabled(!menuData.getBlueprintTypeIDs().isEmpty());
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

	private JMenuItem add(JMenu jMenu, LookupLinks lookupLinks, ListenerClass listener) {
		JMenuItem jMenuItem = lookupLinks.createMenuItem(listener);
		jMenuItems.put(lookupLinks, jMenuItem);
		jMenu.add(jMenuItem);
		return jMenuItem;
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			//Locations
			try {
				LookupLinks links = LookupLinks.valueOf(e.getActionCommand());
				links.open(program, menuData);
			} catch (IllegalArgumentException ex) {
				//Ignore this
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
