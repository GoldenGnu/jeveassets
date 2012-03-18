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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.shared.assetlist.EveAsset;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.i18n.General;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiConverter {

	private static Logger LOG = LoggerFactory.getLogger(ApiConverter.class);;

	private ApiConverter() {}

	public static List<Asset> apiMarketOrder(List<ApiMarketOrder> marketOrders, Human human, Settings settings){
		List<Asset> eveAssets = new ArrayList<Asset>();
		for (ApiMarketOrder apiMarketOrder : marketOrders){
			if (apiMarketOrder.getBid() == 0
					&& apiMarketOrder.getOrderState() == 0
					&& apiMarketOrder.getVolRemaining() > 0
					){
				Asset eveAsset = apiMarketOrderToEveAsset(apiMarketOrder, human, settings);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static Asset apiMarketOrderToEveAsset(ApiMarketOrder apiMarketOrder, Human human, Settings settings){
		int typeID = apiMarketOrder.getTypeID();
		long locationID = apiMarketOrder.getStationID();
		long count = apiMarketOrder.getVolRemaining();
		long itemId = apiMarketOrder.getOrderID();
		String flag = General.get().marketOrderFlag();
		boolean corporation = human.isCorporation();
		boolean singleton  = true;
		int rawQuantity = 0;

		//Calculated:
		String name = ApiIdConverter.typeName(typeID, settings.getItems());
		String group = ApiIdConverter.group(typeID, settings.getItems());
		String category = ApiIdConverter.category(typeID, settings.getItems());
		double basePrice = ApiIdConverter.priceBase(typeID, settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
		float volume = ApiIdConverter.volume(typeID, settings.getItems());
		String meta = ApiIdConverter.meta(typeID, settings.getItems());
		String owner = human.getName();
		String location = ApiIdConverter.locationName(locationID, null, settings.getLocations());
		String region = ApiIdConverter.regionName(locationID, null, settings.getLocations());
		String security = ApiIdConverter.security(locationID, null, settings.getLocations());
		String solarSystem = ApiIdConverter.systemName(locationID, null, settings.getLocations());
		long solarSystemId  = ApiIdConverter.systemID(locationID, null, settings.getLocations());
		List<Asset> parents = new ArrayList<Asset>();
		boolean piMaterial = ApiIdConverter.piMaterial(typeID, settings.getItems());

		return new Asset(name, group, category, owner, count, location, parents, flag, basePrice, meta, itemId, typeID, marketGroup, corporation, volume, region, locationID, singleton, security, solarSystem, solarSystemId, rawQuantity, piMaterial);
	}

	public static List<Asset> apiIndustryJob(List<ApiIndustryJob> industryJobs, Human human, Settings settings){
		List<Asset> eveAssets = new ArrayList<Asset>();
		for (ApiIndustryJob apiIndustryJob : industryJobs){
			if (!apiIndustryJob.isCompleted()){
				Asset eveAsset = apiIndustryJobToEveAsset(apiIndustryJob, human, settings);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}
	
	private static Asset apiIndustryJobToEveAsset(ApiIndustryJob apiIndustryJob, Human human, Settings settings){
		int typeID = apiIndustryJob.getInstalledItemTypeID();
		long locationID = apiIndustryJobLocationId(apiIndustryJob, settings);
		long count = apiIndustryJob.getInstalledItemQuantity();
		long id = apiIndustryJob.getInstalledItemID();
		int nFlag = apiIndustryJob.getInstalledItemFlag();
		boolean corporation = human.isCorporation();
		boolean singleton  = false;
		int rawQuantity = (apiIndustryJob.getInstalledItemCopy() == 0) ? 0 : -2; //0 = BPO  -2 = BPC

		//Calculated:
		String flag = ApiIdConverter.flag(nFlag, settings.getItemFlags());
		String name = ApiIdConverter.typeName(typeID, settings.getItems());
		String group = ApiIdConverter.group(typeID, settings.getItems());
		String category = ApiIdConverter.category(typeID, settings.getItems());
		double basePrice = ApiIdConverter.priceBase(typeID, settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
		float volume = ApiIdConverter.volume(typeID, settings.getItems());
		String meta = ApiIdConverter.meta(typeID, settings.getItems());
		String owner = human.getName();
		String location = ApiIdConverter.locationName(locationID, null, settings.getLocations());
		String region = ApiIdConverter.regionName(locationID, null, settings.getLocations());
		String security = ApiIdConverter.security(locationID, null, settings.getLocations());
		String solarSystem = ApiIdConverter.systemName(locationID, null, settings.getLocations());
		long solarSystemId  = ApiIdConverter.systemID(locationID, null, settings.getLocations());
		List<Asset> parents = new ArrayList<Asset>();
		boolean piMaterial = ApiIdConverter.piMaterial(typeID, settings.getItems());

		return new Asset(name, group, category, owner, count, location, parents, flag, basePrice, meta, id, typeID, marketGroup, corporation, volume, region, locationID, singleton, security, solarSystem, solarSystemId, rawQuantity, piMaterial);
	}

	public static List<Asset> apiAsset(Human human, List<EveAsset<?>> assets, Settings settings){
		List<Asset> eveAssets = new ArrayList<Asset>();
		apiAsset(human, assets, eveAssets, null, settings);
		return eveAssets;
	}
	private static void apiAsset(Human human, List<EveAsset<?>> assets, List<Asset> eveAssets, Asset parentEveAsset, Settings settings){
		for (EveAsset<?> asset : assets){
			Asset eveAsset = apiAssetsToEveAsset(human, asset, parentEveAsset, settings);
			if (parentEveAsset == null){
				eveAssets.add(eveAsset);
			} else {
				parentEveAsset.addEveAsset(eveAsset);
			}
			apiAsset(human, new ArrayList<EveAsset<?>>(asset.getAssets()), eveAssets, eveAsset, settings);
		}
	}
	private static Asset apiAssetsToEveAsset(Human human, EveAsset<?> apiAsset, Asset parentEveAsset, Settings settings){
		long count = apiAsset.getQuantity();
		String flag = ApiIdConverter.flag(apiAsset.getFlag(), settings.getItemFlags());
		long itemId = apiAsset.getItemID();
		int typeID = apiAsset.getTypeID();
		long locationID;
		if (apiAsset.getLocationID() != null){ //Top level
			locationID = apiAsset.getLocationID();
		} else if(parentEveAsset != null){ //Sub level
			locationID = parentEveAsset.getLocationID();
		} else { //Fail (fallback)
			locationID = 0;
		}
		boolean singleton  = apiAsset.getSingleton();
		boolean corporation = human.isCorporation();
		String owner = human.getName();
		int rawQuantity = apiAsset.getRawQuantity();
		//Calculated:
		String name = ApiIdConverter.typeName(apiAsset.getTypeID(), settings.getItems());
		String group = ApiIdConverter.group(apiAsset.getTypeID(), settings.getItems());
		String category = ApiIdConverter.category(apiAsset.getTypeID(), settings.getItems());
		double basePrice = ApiIdConverter.priceBase(apiAsset.getTypeID(), settings.getItems());
		String meta = ApiIdConverter.meta(apiAsset.getTypeID(), settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(apiAsset.getTypeID(), settings.getItems());
		float volume = ApiIdConverter.volume(apiAsset.getTypeID(), settings.getItems());
		String security = ApiIdConverter.security(locationID, parentEveAsset, settings.getLocations());
		String region = ApiIdConverter.regionName(locationID, parentEveAsset, settings.getLocations());
		String location = ApiIdConverter.locationName(locationID, parentEveAsset, settings.getLocations());
		String solarSystem = ApiIdConverter.systemName(locationID, parentEveAsset, settings.getLocations());
		long solarSystemId  = ApiIdConverter.systemID(locationID, parentEveAsset, settings.getLocations());
		List<Asset> parents = ApiIdConverter.parents(parentEveAsset);
		boolean piMaterial = ApiIdConverter.piMaterial(typeID, settings.getItems());

		return new Asset(name, group, category, owner, count, location, parents, flag, basePrice, meta, itemId, typeID, marketGroup, corporation, volume, region, locationID, singleton, security, solarSystem, solarSystemId, rawQuantity, piMaterial);
	}
	public static List<MarketOrder> apiMarketOrdersToMarketOrders(Human human, List<ApiMarketOrder> apiMarketOrders, Settings settings){
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		for (ApiMarketOrder apiMarketOrder : apiMarketOrders){
			marketOrders.add(apiMarketOrderToMarketOrder(human, apiMarketOrder, settings));
		}
		return marketOrders;
	}
	private static MarketOrder apiMarketOrderToMarketOrder(Human human, ApiMarketOrder apiMarketOrder, Settings settings){
		String name = ApiIdConverter.typeName(apiMarketOrder.getTypeID(), settings.getItems());
		String location = ApiIdConverter.locationName(apiMarketOrder.getStationID(), null, settings.getLocations());
		String system = ApiIdConverter.systemName(apiMarketOrder.getStationID(), null, settings.getLocations());
		String region = ApiIdConverter.regionName(apiMarketOrder.getStationID(), null, settings.getLocations());
		String owner = human.getName();
		return new MarketOrder(apiMarketOrder, name, location, system, region, owner);
	}
	public static List<IndustryJob> apiIndustryJobsToIndustryJobs(List<ApiIndustryJob> apiIndustryJobs, String owner, Settings settings){
		List<IndustryJob> industryJobs = new ArrayList<IndustryJob>();
		for (ApiIndustryJob apiIndustryJob : apiIndustryJobs){
			industryJobs.add(apiIndustryJobToIndustryJob(apiIndustryJob, owner, settings));
		}
		return industryJobs;
	}

	private static IndustryJob apiIndustryJobToIndustryJob(ApiIndustryJob apiIndustryJob, String owner, Settings settings){
		String name = ApiIdConverter.typeName(apiIndustryJob.getInstalledItemTypeID(), settings.getItems());
		long locationID = apiIndustryJobLocationId(apiIndustryJob, settings);
		String location = ApiIdConverter.locationName(locationID, null, settings.getLocations());
		String system = ApiIdConverter.systemName(locationID, null, settings.getLocations());
		String region = ApiIdConverter.regionName(locationID, null, settings.getLocations());
		return new IndustryJob(apiIndustryJob, name, location, system, region, owner);
	}
	
	private static long apiIndustryJobLocationId(ApiIndustryJob apiIndustryJob, Settings settings){
		boolean location = ApiIdConverter.locationTest(apiIndustryJob.getInstalledItemLocationID(), null, settings.getLocations());
		if (location) return apiIndustryJob.getInstalledItemLocationID();
		location = ApiIdConverter.locationTest(apiIndustryJob.getContainerLocationID(), null, settings.getLocations());
		if (location) return apiIndustryJob.getContainerLocationID();
		LOG.error("Failed to find locationID for IndustryJob. InstalledItemLocationID: "+apiIndustryJob.getInstalledItemLocationID()+" - ContainerLocationID: "+apiIndustryJob.getContainerLocationID());
		return -1;
	}
}
