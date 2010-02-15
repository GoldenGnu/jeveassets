/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.asset.ApiAsset;
import com.beimin.eveapi.industry.ApiIndustryJob;
import com.beimin.eveapi.order.ApiMarketOrder;
import com.beimin.eveapi.utils.stationlist.ApiStation;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.MarketOrder;

public class ApiConverter {

	public static List<EveAsset> apiMarketOrder(List<ApiMarketOrder> marketOrders, Human human, boolean bCorp, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		List<EveAsset> eveAssets = new Vector<EveAsset>();
		for (int a = 0; a < marketOrders.size(); a++){
			ApiMarketOrder apiMarketOrder = marketOrders.get(a);
			if (apiMarketOrder.getBid() == 0
					&& apiMarketOrder.getOrderState() == 0
					&& apiMarketOrder.getVolRemaining() > 0
					){
				EveAsset eveAsset = apiMarketOrderToEveAsset(apiMarketOrder, human, bCorp, conquerableStations, locations, items);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static EveAsset apiMarketOrderToEveAsset(ApiMarketOrder apiMarketOrder, Human human, boolean bCorp, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		int typeID = (int)apiMarketOrder.getTypeID();
		int locationID = (int) apiMarketOrder.getStationID();
		long count = apiMarketOrder.getVolRemaining();
		long id = apiMarketOrder.getOrderID();
		String flag = "Market Order";
		boolean corporationAsset = bCorp;
		boolean singleton  = false;

		String name = ApiIdConverter.name(typeID, items);
		String group = ApiIdConverter.group(typeID, items);
		String category = ApiIdConverter.category(typeID, items);
		double basePrice = ApiIdConverter.priceBase(typeID, items);
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, items);
		float volume = ApiIdConverter.volume(typeID, items);
		String meta = ApiIdConverter.meta(typeID, items);

		String owner = ApiIdConverter.owner(human, bCorp);

		String location = ApiIdConverter.location(locationID, null, conquerableStations, locations);
		String container = ApiIdConverter.container(locationID, null);
		String region = ApiIdConverter.region(locationID, null, conquerableStations, locations);
		String security = ApiIdConverter.security(locationID, null, conquerableStations, locations);

		return new EveAsset(name, group, category, owner, count, location, container, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security);
	}

	public static List<EveAsset> apiIndustryJob(List<ApiIndustryJob> industryJobs, Human human, boolean bCorp, List<Long> bpos, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		List<EveAsset> eveAssets = new Vector<EveAsset>();
		for (int a = 0; a < industryJobs.size(); a++){
			ApiIndustryJob apiIndustryJob = industryJobs.get(a);
			long id = apiIndustryJob.getInstalledItemID();
			if (apiIndustryJob.getCompleted() == 0){
				EveAsset eveAsset = apiIndustryJobToEveAsset(apiIndustryJob, human, bCorp, conquerableStations, locations, items);
				eveAssets.add(eveAsset);
			}
			//Mark original blueprints
			boolean isCopy = (apiIndustryJob.getInstalledItemCopy() > 0);
			if (bpos.contains(id)){
				bpos.remove(bpos.indexOf(id));
			}
			if (!isCopy){
				bpos.add(id);
			}
		}
		return eveAssets;
	}
	
	private static EveAsset apiIndustryJobToEveAsset(ApiIndustryJob apiIndustryJob, Human human, boolean bCorp, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		int typeID = (int) apiIndustryJob.getInstalledItemTypeID();
		int locationID = (int) apiIndustryJob.getInstalledItemLocationID();
		long count = apiIndustryJob.getInstalledItemQuantity();
		long id = apiIndustryJob.getInstalledItemID();
		int nFlag = apiIndustryJob.getInstalledItemFlag();
		boolean corporationAsset = bCorp;
		boolean singleton  = false;

		String flag = ApiIdConverter.flag(nFlag);

		String name = ApiIdConverter.name(typeID, items);
		String group = ApiIdConverter.group(typeID, items);
		String category = ApiIdConverter.category(typeID, items);
		double basePrice = ApiIdConverter.priceBase(typeID, items);
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, items);
		float volume = ApiIdConverter.volume(typeID, items);
		String meta = ApiIdConverter.meta(typeID, items);

		String owner = ApiIdConverter.owner(human, bCorp);

		String location = ApiIdConverter.location(locationID, null, conquerableStations, locations);
		if (location.contains("Error !")){
			locationID = (int) apiIndustryJob.getContainerLocationID();
			location = ApiIdConverter.location(locationID, null, conquerableStations, locations);
		}
		if (location.contains("Error !")){
			location = "Unknown";
		}
		String container = ApiIdConverter.container(locationID, null);
		String region = ApiIdConverter.region(locationID, null, conquerableStations, locations);
		String security = ApiIdConverter.security(locationID, null, conquerableStations, locations);

		return new EveAsset(name, group, category, owner, count, location, container, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security);
	}

	public static List<EveAsset> apiAsset(Human human, List<ApiAsset> assets, boolean bCorp, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		List<EveAsset> eveAssets = new Vector<EveAsset>();
		apiAsset(human, assets, eveAssets, null, bCorp, conquerableStations, locations, items);
		return eveAssets;
	}
	private static void apiAsset(Human human, List<ApiAsset> assets, List<EveAsset> eveAssets, EveAsset parentEveAsset, boolean bCorp, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		for (int a = 0; a < assets.size(); a++){
			ApiAsset asset = assets.get(a);
			EveAsset eveAsset = apiAssetsToEveAsset(human, asset, parentEveAsset, bCorp, conquerableStations, locations, items);
			if (parentEveAsset == null){
				eveAssets.add(eveAsset);
			} else {
				parentEveAsset.addEveAsset(eveAsset);
			}
			apiAsset(human, new Vector<ApiAsset>(asset.getAssets()), eveAssets, eveAsset, bCorp, conquerableStations, locations, items);
		}
	}
	private static EveAsset apiAssetsToEveAsset(Human human, ApiAsset apiAsset, EveAsset parentEveAsset, boolean bCorp, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		String name = ApiIdConverter.name(apiAsset.getTypeID(), items);
		String group = ApiIdConverter.group(apiAsset.getTypeID(), items);
		String category = ApiIdConverter.category(apiAsset.getTypeID(), items);
		double basePrice = ApiIdConverter.priceBase(apiAsset.getTypeID(), items);
		String meta = ApiIdConverter.meta(apiAsset.getTypeID(), items);
		boolean marketGroup = ApiIdConverter.marketGroup(apiAsset.getTypeID(), items);
		float volume = ApiIdConverter.volume(apiAsset.getTypeID(), items);

		String owner = ApiIdConverter.owner(human, bCorp);
		long count = apiAsset.getQuantity();
		long id = apiAsset.getItemID();
		int typeID = apiAsset.getTypeID();
		boolean corporationAsset = bCorp;
		boolean singleton  = apiAsset.getSingleton();
		
		String container = ApiIdConverter.container(apiAsset.getLocationID(), parentEveAsset);
		String flag = ApiIdConverter.flag(apiAsset.getFlag());
		
		String security = ApiIdConverter.security(apiAsset.getLocationID(), parentEveAsset, conquerableStations, locations);
		String region = ApiIdConverter.region(apiAsset.getLocationID(), parentEveAsset, conquerableStations, locations);
		String location = ApiIdConverter.location(apiAsset.getLocationID(), parentEveAsset, conquerableStations, locations);

		return new EveAsset(name, group, category, owner, count, location, container, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, apiAsset.getLocationID(), singleton, security);
	}
	public static List<MarketOrder> apiMarketOrdersToMarketOrders(List<ApiMarketOrder> apiMarketOrders, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		List<MarketOrder> marketOrders = new Vector<MarketOrder>();
		for (int a = 0; a < apiMarketOrders.size(); a++){
			marketOrders.add(apiMarketOrderToMarketOrder(apiMarketOrders.get(a), conquerableStations, locations, items));
		}
		return marketOrders;
	}
	private static MarketOrder apiMarketOrderToMarketOrder(ApiMarketOrder apiMarketOrder, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		String name = ApiIdConverter.name((int)apiMarketOrder.getTypeID(), items);
		String location = ApiIdConverter.location((int)apiMarketOrder.getStationID(), null, conquerableStations, locations);
		return new MarketOrder(apiMarketOrder, name, location);
	}
	public static List<IndustryJob> apiIndustryJobsToIndustryJobs(List<ApiIndustryJob> apiIndustryJobs, String owner, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		List<IndustryJob> industryJobs = new Vector<IndustryJob>();
		for (int a = 0; a < apiIndustryJobs.size(); a++){
			industryJobs.add(apiIndustryJobToIndustryJob(apiIndustryJobs.get(a), owner, conquerableStations, locations, items));
		}
		return industryJobs;
	}

	private static IndustryJob apiIndustryJobToIndustryJob(ApiIndustryJob apiIndustryJob, String owner, Map<Integer, ApiStation> conquerableStations, Map<Integer, Location> locations, Map<Integer, Item> items){
		String name = ApiIdConverter.name((int)apiIndustryJob.getInstalledItemTypeID(), items);
		String location = ApiIdConverter.location((int)apiIndustryJob.getInstalledItemLocationID(), null, conquerableStations, locations);
		return new IndustryJob(apiIndustryJob, name, location, owner);
	}
}
