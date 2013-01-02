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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractItem;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material;
import net.nikr.eve.jeveasset.gui.tabs.overview.Overview;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedInterface;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;


public class MenuData<T> {

	private final Set<Integer> typeIDs = new HashSet<Integer>();
	private final Map<Integer, Double> prices = new HashMap<Integer, Double>();
	private final Set<String> typeNames = new HashSet<String>();
	private final Set<String> stations = new HashSet<String>();
	private final Set<String> systems = new HashSet<String>();
	private final Set<String> regions = new HashSet<String>();
	private final Set<Integer> marketTypeIDs = new HashSet<Integer>();
	private final Set<Integer> blueprintTypeIDs = new HashSet<Integer>();

	public MenuData(final List<T> items) {
		if (items == null) { //Skip null
			return;
		}

		for (T t : items) {
			if (t == null) { //Skip null
				continue;
			}

			if (t instanceof Material) {
				Material material = (Material) t;
				add(material.isMarketGroup(), //Market Group
						material.getTypeName(), //Type Name
						material.getTypeID(), //typeID
						material.getStation(), //Station or Location Name
						material.getSystem(), //System Name
						material.getRegion(), //Region Name
						material.getPrice(), //Price
						false //Blueprint Copy
						);
				continue; //done
			}
			if (t instanceof Module) {
				Module module = (Module) t;
				add(module.isMarketGroup(), //Market Group
						module.getTypeName(), //Type Name
						module.getTypeID(), //typeID
						module.getLocation(), //Station or Location Name
						module.getSystem(), //System Name
						module.getRegion(), //Region Name
						module.getPrice(), //Price
						false //Blueprint Copy
						);
				continue; //done
			}
			if (t instanceof MarketOrder) {
				MarketOrder marketOrder = (MarketOrder) t;
				add(true, //Market Group
						marketOrder.getName(), //Type Name
						marketOrder.getTypeID(), //typeID
						marketOrder.getLocation(), //Station or Location Name
						marketOrder.getSystem(), //System Name
						marketOrder.getRegion(), //Region Name
						//TODO - can not edit price from Orders Tool
						null, //Price 
						false //Blueprint Copy
						);
				continue; //done
			}
			if (t instanceof IndustryJob) {
				IndustryJob industryJob = (IndustryJob) t;
				add(true, //Market Group
						industryJob.getName(), //Type Name
						industryJob.getInstalledItemTypeID(), //typeID
						industryJob.getLocation(), //Station or Location Name
						industryJob.getSystem(), //System Name
						industryJob.getRegion(), //Region Name
						//TODO - can not edit price from Jobs Tool
						null, //Price 
						industryJob.getInstalledItemCopy() > 0 //Blueprint Copy
						);
				continue; //done
			}
			if (t instanceof Asset) {
				Asset eveAsset = (Asset) t;
				add(eveAsset.isMarketGroup(), //Market Group
						eveAsset.getTypeName(), //Type Name
						eveAsset.getTypeID(), //typeID
						eveAsset.getLocation(), //Station or Location Name
						eveAsset.getSystem(), //System Name
						eveAsset.getRegion(), //Region Name
						eveAsset.getPrice(), //Price 
						(eveAsset.isBlueprint() && !eveAsset.isBpo()) //Blueprint Copy
						);
				continue; //done
			}
			if (t instanceof Overview) {
				Overview overview = (Overview) t;
				add(false, //Market Group
						null, //Type Name
						null, //typeID
						overview.isStation() && !overview.isGroup() ? overview.getName() : null, //Station or Location Name
						!overview.isRegion() && !overview.isGroup() ? overview.getSolarSystem() : null, //System Name
						!overview.isGroup() ? overview.getRegion() : null, //Region Name
						null, //Price 
						false //Blueprint Copy
						);
				continue; //done
			}
			if (t instanceof Item) {
				Item item = (Item) t;
				add(item.isMarketGroup(), //Market Group
						item.getName(), //Type Name
						item.getTypeID(), //typeID
						null, //Station or Location Name
						null, //System Name
						null, //Region Name
						(double) item.getPrice(), //Price 
						false //Blueprint Copy
						);
				continue; //done
			}
			if (t instanceof StockpileItem) { //
				StockpileItem item = (StockpileItem) t;
				add(item.isMarketGroup(), //Market Group
						(t instanceof StockpileTotal) ? null : item.getTypeName(), //Type Name
						(t instanceof StockpileTotal) ? null : item.getTypeID(), //typeID
						item.getStockpile().getLocation(), //Station or Location Name
						item.getStockpile().getSystem(), //System Name
						item.getStockpile().getRegion(), //Region Name
						item.getPrice(), //Price 
						item.isBPC() //Blueprint Copy
						);
				continue; //done
			}
			if (t instanceof ReprocessedInterface) { //
				ReprocessedInterface item = (ReprocessedInterface) t;
				add(item.isMarketGroup(), //Market Group
						item.getName(), //Type Name
						item.getTypeID(), //typeID
						null, //Station or Location Name
						null, //System Name
						null, //Region Name
						item.getPrice(), //Price 
						false //Blueprint Copy
						);
				continue; //done
			}
			if (t instanceof ContractItem) { //
				ContractItem item = (ContractItem) t;
				add(item.isMarketGroup(), //Market Group
						item.getContract().isCourier() ? null : item.getName(), //Type Name
						item.getContract().isCourier() ? null : item.getTypeID(), //typeID
						item.getContract().getStartStation(), //Station or Location Name
						item.getContract().getSystem(), //System Name
						item.getContract().getRegion(), //Region Name
						//TODO - can not edit price from Contract
						null, //Price 
						false //Blueprint Copy
						);
				continue; //done
			}
		}
	}

	private void add(final boolean marketGroup, final String typeName, final Integer typeID, final String station, final String system, final String region, final Double price, final boolean copy) {
		//Type Name
		if (typeName != null) {
			typeNames.add(typeName);
		}

		//TypeID
		if (typeID != null) {
			//TypeID
			typeIDs.add(typeID);
			//Market TypeID
			if (marketGroup) {
				marketTypeIDs.add(typeID);
			}
			//Blueprint TypeID
			int blueprintTypeID;
			if (copy) {
				blueprintTypeID = -typeID;
			} else {
				blueprintTypeID = typeID;
			}
			blueprintTypeIDs.add(blueprintTypeID);
			//Price TypeID
			if (price != null) { //Not unique
				prices.put(blueprintTypeID, price);
			}
		}
		//Locations
		//Station (can be a system or a region)
		if (station != null && system != null && region != null
				&& !station.equals(system) //Not system
				&& !station.equals(region) //Not region
				) {
			stations.add(station);
		}
		//System
		if (system != null) {
			systems.add(system);
		}
		//Region
		if (region != null) {
			regions.add(region);
		}
	}

	public Set<Integer> getTypeIDs() {
		return typeIDs;
	}

	public Map<Integer, Double> getPrices() {
		return prices;
	}

	public Set<String> getTypeNames() {
		return typeNames;
	}

	public Set<String> getStations() {
		return stations;
	}

	public Set<String> getSystems() {
		return systems;
	}

	public Set<String> getRegions() {
		return regions;
	}

	public Set<Integer> getMarketTypeIDs() {
		return marketTypeIDs;
	}

	public Set<Integer> getBlueprintTypeIDs() {
		return blueprintTypeIDs;
	}
}
