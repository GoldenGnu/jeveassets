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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JMenu;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material;
import net.nikr.eve.jeveasset.gui.tabs.overview.Overview;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;


public abstract class JMenuTool<T> extends JMenu {
	protected final Program program;
	protected final Set<Integer> typeIDs = new HashSet<Integer>();
	protected final Map<Integer, Double> prices = new HashMap<Integer, Double>();
	protected final Set<String> typeNames = new HashSet<String>();
	protected final Set<String> stations = new HashSet<String>();
	protected final Set<String> systems = new HashSet<String>();
	protected final Set<String> regions = new HashSet<String>();
	protected final Set<Integer> marketTypeIDs = new HashSet<Integer>();
	protected final Set<Integer> blueprintTypeIDs = new HashSet<Integer>();

	protected JMenuTool(final String title, final Program program, final List<T> items) {
		super(title);
		this.program = program;
		init(items);
	}

	private void init(final List<T> items) {
		if (items == null) { //Skip null
			return;
		}

		for (T t : items) {
			if (t == null) { //Skip null
				continue;
			}

			if (t instanceof Material) {
				Material material = (Material) t;
				init(material.isMarketGroup(),
						material.getTypeName(),
						material.getTypeID(),
						material.getStation(),
						material.getSystem(),
						material.getRegion(),
						material.getPrice(),
						false
						);
			}
			if (t instanceof Module) {
				Module module = (Module) t;
				init(module.isMarketGroup(),
						module.getTypeName(),
						module.getTypeID(),
						module.getLocation(),
						module.getSystem(),
						module.getRegion(),
						module.getPrice(),
						false
						);
			}
			if (t instanceof MarketOrder) {
				MarketOrder marketOrder = (MarketOrder) t;
				init(true,
						marketOrder.getName(),
						marketOrder.getTypeID(),
						marketOrder.getLocation(),
						marketOrder.getSystem(),
						marketOrder.getRegion(),
						null, //TODO - can not edit price from Orders Tool
						false
						);
			}
			if (t instanceof IndustryJob) {
				IndustryJob industryJob = (IndustryJob) t;
				init(true,
						industryJob.getName(),
						industryJob.getInstalledItemTypeID(),
						industryJob.getLocation(),
						industryJob.getSystem(),
						industryJob.getRegion(),
						null, //TODO - can not edit price from Jobs Tool
						industryJob.getInstalledItemCopy() > 0
						);
			}
			if (t instanceof Asset) {
				Asset eveAsset = (Asset) t;
				init(eveAsset.isMarketGroup(),
						eveAsset.getTypeName(),
						eveAsset.getTypeID(),
						eveAsset.getLocation(),
						eveAsset.getSystem(),
						eveAsset.getRegion(),
						eveAsset.getPrice(),
						(eveAsset.isBlueprint() && !eveAsset.isBpo())
						);
			}
			if (t instanceof Overview) {
				Overview overview = (Overview) t;
				init(false,
						null,
						null,
						overview.isStation() && !overview.isGroup() ? overview.getName() : null,
						!overview.isRegion() && !overview.isGroup() ? overview.getSolarSystem() : null,
						!overview.isGroup() ? overview.getRegion() : null,
						null,
						false
						);
			}
			if (t instanceof Item) {
				Item item = (Item) t;
				init(item.isMarketGroup(),
						item.getName(),
						item.getTypeID(),
						null,
						null,
						null,
						(double) item.getPrice(),
						false
						);
			}
			if (t instanceof StockpileItem) { //
				StockpileItem item = (StockpileItem) t;
				init(item.isMarketGroup(),
						(t instanceof StockpileTotal) ? null : item.getTypeName(),
						(t instanceof StockpileTotal) ? null : item.getTypeID(),
						item.getStockpile().getLocation(),
						item.getStockpile().getSystem(),
						item.getStockpile().getRegion(),
						item.getPrice(),
						item.isBPC()
						);
			}
		}
	}

	private void init(final boolean marketGroup, final String typeName, final Integer typeID, final String station, final String system, final String region, final Double price, final boolean copy) {
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
			if (copy){
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
}
