/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import com.beimin.eveapi.shared.assetlist.ApiAsset;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiConverter {

	private static Logger LOG = LoggerFactory.getLogger(ApiConverter.class);;

	public static List<EveAsset> apiMarketOrder(List<ApiMarketOrder> marketOrders, Human human, boolean bCorp, Settings settings){
		List<EveAsset> eveAssets = new ArrayList<EveAsset>();
		for (ApiMarketOrder apiMarketOrder : marketOrders){
			if (apiMarketOrder.getBid() == 0
					&& apiMarketOrder.getOrderState() == 0
					&& apiMarketOrder.getVolRemaining() > 0
					){
				EveAsset eveAsset = apiMarketOrderToEveAsset(apiMarketOrder, human, bCorp, settings);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static EveAsset apiMarketOrderToEveAsset(ApiMarketOrder apiMarketOrder, Human human, boolean bCorp, Settings settings){
		int typeID = (int)apiMarketOrder.getTypeID();
		int locationID = (int) apiMarketOrder.getStationID();
		long count = apiMarketOrder.getVolRemaining();
		long id = apiMarketOrder.getOrderID();
		String flag = "Market Order";
		boolean corporationAsset = bCorp;
		boolean singleton  = true;

		//Calculated:
		String name = ApiIdConverter.name(typeID, settings.getItems());
		String group = ApiIdConverter.group(typeID, settings.getItems());
		String category = ApiIdConverter.category(typeID, settings.getItems());
		double basePrice = ApiIdConverter.priceBase(typeID, settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
		float volume = ApiIdConverter.volume(typeID, settings.getItems());
		String meta = ApiIdConverter.meta(typeID, settings.getItems());
		String owner = ApiIdConverter.owner(human, bCorp);
		String location = ApiIdConverter.location(locationID, null, settings.getConquerableStations(), settings.getLocations());
		String region = ApiIdConverter.region(locationID, null, settings.getConquerableStations(), settings.getLocations());
		String security = ApiIdConverter.security(locationID, null, settings.getConquerableStations(), settings.getLocations());
		String solarSystem = ApiIdConverter.solarSystem(locationID, null, settings.getConquerableStations(), settings.getLocations());
		int solarSystemId = ApiIdConverter.solarSystemId(locationID, null, settings.getConquerableStations(), settings.getLocations());
		List<EveAsset> parents = new ArrayList<EveAsset>();

		return new EveAsset(name, group, category, owner, count, location, parents, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security, solarSystem, solarSystemId);
	}

	public static List<EveAsset> apiIndustryJob(List<ApiIndustryJob> industryJobs, Human human, boolean bCorp, Settings settings){
		List<EveAsset> eveAssets = new ArrayList<EveAsset>();
		for (ApiIndustryJob apiIndustryJob : industryJobs){
			long id = apiIndustryJob.getInstalledItemID();
			if (apiIndustryJob.getCompleted() == 0){
				EveAsset eveAsset = apiIndustryJobToEveAsset(apiIndustryJob, human, bCorp, settings);
				eveAssets.add(eveAsset);
			}
			//Mark original blueprints
			boolean isCopy = (apiIndustryJob.getInstalledItemCopy() > 0);
			List<Long> bpos = settings.getBpos();
			if (bpos.contains(id)){
				bpos.remove(bpos.indexOf(id));
			}
			if (!isCopy){
				bpos.add(id);
			}
		}
		return eveAssets;
	}
	
	private static EveAsset apiIndustryJobToEveAsset(ApiIndustryJob apiIndustryJob, Human human, boolean bCorp, Settings settings){
		int typeID = (int) apiIndustryJob.getInstalledItemTypeID();
		int locationID = (int) apiIndustryJobLocationId(apiIndustryJob, settings);
		long count = apiIndustryJob.getInstalledItemQuantity();
		long id = apiIndustryJob.getInstalledItemID();
		int nFlag = apiIndustryJob.getInstalledItemFlag();
		boolean corporationAsset = bCorp;
		boolean singleton  = false;

		//Calculated:
		String flag = ApiIdConverter.flag(nFlag);
		String name = ApiIdConverter.name(typeID, settings.getItems());
		String group = ApiIdConverter.group(typeID, settings.getItems());
		String category = ApiIdConverter.category(typeID, settings.getItems());
		double basePrice = ApiIdConverter.priceBase(typeID, settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
		float volume = ApiIdConverter.volume(typeID, settings.getItems());
		String meta = ApiIdConverter.meta(typeID, settings.getItems());
		String owner = ApiIdConverter.owner(human, bCorp);
		String location = ApiIdConverter.location(locationID, null, settings.getConquerableStations(), settings.getLocations());
		String region = ApiIdConverter.region(locationID, null, settings.getConquerableStations(), settings.getLocations());
		String security = ApiIdConverter.security(locationID, null, settings.getConquerableStations(), settings.getLocations());
		String solarSystem = ApiIdConverter.solarSystem(locationID, null, settings.getConquerableStations(), settings.getLocations());
		int solarSystemId = ApiIdConverter.solarSystemId(locationID, null, settings.getConquerableStations(), settings.getLocations());
		List<EveAsset> parents = new ArrayList<EveAsset>();

		return new EveAsset(name, group, category, owner, count, location, parents, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security, solarSystem, solarSystemId);
	}

	public static List<EveAsset> apiAsset(Human human, List<ApiAsset> assets, boolean bCorp, Settings settings){
		List<EveAsset> eveAssets = new ArrayList<EveAsset>();
		apiAsset(human, assets, eveAssets, null, bCorp, settings);
		return eveAssets;
	}
	private static void apiAsset(Human human, List<ApiAsset> assets, List<EveAsset> eveAssets, EveAsset parentEveAsset, boolean bCorp, Settings settings){
		for (ApiAsset asset : assets){
			EveAsset eveAsset = apiAssetsToEveAsset(human, asset, parentEveAsset, bCorp, settings);
			if (parentEveAsset == null){
				eveAssets.add(eveAsset);
			} else {
				parentEveAsset.addEveAsset(eveAsset);
			}
			apiAsset(human, new ArrayList<ApiAsset>(asset.getAssets()), eveAssets, eveAsset, bCorp, settings);
		}
	}
	private static EveAsset apiAssetsToEveAsset(Human human, ApiAsset apiAsset, EveAsset parentEveAsset, boolean bCorp, Settings settings){
		long count = apiAsset.getQuantity();
		String flag = ApiIdConverter.flag(apiAsset.getFlag());
		long id = apiAsset.getItemID();
		int typeID = apiAsset.getTypeID();
		int locationID = apiAsset.getLocationID();
		boolean singleton  = apiAsset.getSingleton();
		boolean corporationAsset = bCorp;
		String owner = ApiIdConverter.owner(human, bCorp);

		//Calculated:
		String name = ApiIdConverter.name(apiAsset.getTypeID(), settings.getItems());
		String group = ApiIdConverter.group(apiAsset.getTypeID(), settings.getItems());
		String category = ApiIdConverter.category(apiAsset.getTypeID(), settings.getItems());
		double basePrice = ApiIdConverter.priceBase(apiAsset.getTypeID(), settings.getItems());
		String meta = ApiIdConverter.meta(apiAsset.getTypeID(), settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(apiAsset.getTypeID(), settings.getItems());
		float volume = ApiIdConverter.volume(apiAsset.getTypeID(), settings.getItems());
		String security = ApiIdConverter.security(apiAsset.getLocationID(), parentEveAsset, settings.getConquerableStations(), settings.getLocations());
		String region = ApiIdConverter.region(apiAsset.getLocationID(), parentEveAsset, settings.getConquerableStations(), settings.getLocations());
		String location = ApiIdConverter.location(apiAsset.getLocationID(), parentEveAsset, settings.getConquerableStations(), settings.getLocations());
		String solarSystem = ApiIdConverter.solarSystem(locationID, parentEveAsset, settings.getConquerableStations(), settings.getLocations());
		int solarSystemId = ApiIdConverter.solarSystemId(locationID, parentEveAsset, settings.getConquerableStations(), settings.getLocations());
		List<EveAsset> parents = ApiIdConverter.parents(parentEveAsset);

		return new EveAsset(name, group, category, owner, count, location, parents, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security, solarSystem, solarSystemId);
	}
	public static List<MarketOrder> apiMarketOrdersToMarketOrders(List<ApiMarketOrder> apiMarketOrders, Settings settings){
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		for (ApiMarketOrder apiMarketOrder : apiMarketOrders){
			marketOrders.add(apiMarketOrderToMarketOrder(apiMarketOrder, settings));
		}
		return marketOrders;
	}
	private static MarketOrder apiMarketOrderToMarketOrder(ApiMarketOrder apiMarketOrder, Settings settings){
		String name = ApiIdConverter.name((int)apiMarketOrder.getTypeID(), settings.getItems());
		String location = ApiIdConverter.location((int)apiMarketOrder.getStationID(), null, settings.getConquerableStations(), settings.getLocations());
		return new MarketOrder(apiMarketOrder, name, location);
	}
	public static List<IndustryJob> apiIndustryJobsToIndustryJobs(List<ApiIndustryJob> apiIndustryJobs, String owner, Settings settings){
		List<IndustryJob> industryJobs = new ArrayList<IndustryJob>();
		for (ApiIndustryJob apiIndustryJob : apiIndustryJobs){
			industryJobs.add(apiIndustryJobToIndustryJob(apiIndustryJob, owner, settings));
		}
		return industryJobs;
	}

	private static IndustryJob apiIndustryJobToIndustryJob(ApiIndustryJob apiIndustryJob, String owner, Settings settings){
		String name = ApiIdConverter.name((int)apiIndustryJob.getInstalledItemTypeID(), settings.getItems());
		int locationID = (int)apiIndustryJobLocationId(apiIndustryJob, settings);
		String location = ApiIdConverter.location(locationID, null, settings.getConquerableStations(), settings.getLocations());
		return new IndustryJob(apiIndustryJob, name, location, owner);
	}
	
	private static long apiIndustryJobLocationId(ApiIndustryJob apiIndustryJob, Settings settings){
		boolean location = ApiIdConverter.locationTest((int)apiIndustryJob.getInstalledItemLocationID(), null, settings.getConquerableStations(), settings.getLocations());
		if (location) return apiIndustryJob.getInstalledItemLocationID();
		location = ApiIdConverter.locationTest((int)apiIndustryJob.getContainerLocationID(), null, settings.getConquerableStations(), settings.getLocations());
		if (location) return apiIndustryJob.getContainerLocationID();
		LOG.error("Failed to find locationID for IndustryJob. InstalledItemLocationID: "+apiIndustryJob.getInstalledItemLocationID()+" - ContainerLocationID: "+apiIndustryJob.getContainerLocationID());
		return -1;
	}
}
