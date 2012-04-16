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

package net.nikr.eve.jeveasset.gui.shared;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;


public abstract class JMenuTool<T> extends JMenu {
	protected Program program;
	protected List<Integer> typeIDs = new ArrayList<Integer>();
	protected List<Double> prices = new ArrayList<Double>();
	protected List<String> typeNames = new ArrayList<String>();
	protected List<String> stations = new ArrayList<String>();
	protected List<String> systems = new ArrayList<String>();
	protected List<String> regions = new ArrayList<String>();
	protected List<Integer> marketTypeIDs = new ArrayList<Integer>();

	protected JMenuTool(String title, Program program, List<T> items) {
		super(title);
		this.program = program;
		init(items);
	}

	private void init(List<T> items){
		if (items == null) return; //Skip null
		
		for (T t : items){
			if (t == null) continue; //Skip null
			
			if (t instanceof Material){
				Material material = (Material) t;
				init(	material.isMarketGroup(),
						material.getTypeName(),
						material.getTypeID(),
						material.getStation(),
						material.getSystem(),
						material.getRegion(),
						material.getPrice()
						);
			}
			if (t instanceof Module){
				Module module = (Module) t;
				init(	module.isMarketGroup(),
						module.getTypeName(),
						module.getTypeID(),
						module.getLocation(),
						module.getSystem(),
						module.getRegion(),
						module.getPrice()
						);
			}
			if (t instanceof MarketOrder){
				MarketOrder marketOrder = (MarketOrder) t;
				init(	true,
						marketOrder.getName(),
						marketOrder.getTypeID(),
						marketOrder.getLocation(),
						marketOrder.getSystem(),
						marketOrder.getRegion(),
						null //TODO - can not edit price from Orders Tool
						);
			}
			if (t instanceof IndustryJob){
				IndustryJob industryJob = (IndustryJob) t;
				init(	true,
						industryJob.getName(),
						industryJob.getInstalledItemTypeID(),
						industryJob.getLocation(),
						industryJob.getSystem(),
						industryJob.getRegion(),
						null //TODO - can not edit price from Jobs Tool
						);
			}
			if (t instanceof Asset){
				Asset eveAsset = (Asset) t;
				init(	eveAsset.isMarketGroup(),
						eveAsset.getTypeName(),
						eveAsset.getTypeID(),
						eveAsset.getLocation(),
						eveAsset.getSystem(),
						eveAsset.getRegion(),
						eveAsset.getPrice()
						);
			}
			if (t instanceof Overview){
				Overview overview = (Overview) t;
				init(	false,
						null,
						null,
						overview.isStation() && !overview.isGroup() ? overview.getName() : null,
						!overview.isRegion() && !overview.isGroup() ? overview.getSolarSystem() : null,
						!overview.isGroup() ? overview.getRegion() : null,
						null
						);
			}
			if (t instanceof StockpileItem){ //
				StockpileItem item = (StockpileItem) t;
				init(	item.isMarketGroup(),
						(t instanceof StockpileTotal) ? null : item.getName(),
						(t instanceof StockpileTotal) ? null : item.getTypeID(),
						item.getStockpile().getLocation(),
						item.getStockpile().getSystem(),
						item.getStockpile().getRegion(),
						item.getPrice()
						);
			}
		}
	}

	private void init(boolean marketGroup, String typeName, Integer typeID, String station, String system, String region, Double price){
		if (typeID != null && marketGroup && !marketTypeIDs.contains(typeID)) marketTypeIDs.add(typeID);
		if (typeName != null && !typeNames.contains(typeName)) typeNames.add(typeName);
		if (typeID != null && !typeIDs.contains(typeID)) typeIDs.add(typeID);
		//station can be a system
		if (station != null && system != null && !station.equals(system) && !stations.contains(station)){
			stations.add(station);
		}
		if (system != null && !systems.contains(system)) systems.add(system);
		if (region != null && !regions.contains(region)) regions.add(region);
		if (price != null && !prices.contains(price)) prices.add(price);
	}
}
