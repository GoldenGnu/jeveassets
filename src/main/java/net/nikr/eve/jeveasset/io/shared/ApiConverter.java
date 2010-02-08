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
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.SettingsInterface;

public class ApiConverter {

	public static List<EveAsset> apiMarketOrder(List<ApiMarketOrder> marketOrders, SettingsInterface settings, Human human, boolean bCorp){
		List<EveAsset> eveAssets = new Vector<EveAsset>();
		for (int a = 0; a < marketOrders.size(); a++){
			ApiMarketOrder apiMarketOrder = marketOrders.get(a);
			if (apiMarketOrder.getBid() == 0
					&& apiMarketOrder.getOrderState() == 0
					&& apiMarketOrder.getVolRemaining() > 0
					){
				EveAsset eveAsset = apiMarketOrderToEveAsset(apiMarketOrder, settings, human, bCorp);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static EveAsset apiMarketOrderToEveAsset(ApiMarketOrder apiMarketOrder, SettingsInterface settings, Human human, boolean bCorp){
		int typeID = (int)apiMarketOrder.getTypeID();
		int locationID = (int) apiMarketOrder.getStationID();
		long count = apiMarketOrder.getVolRemaining();
		long id = apiMarketOrder.getOrderID();
		String flag = "Market Order";
		boolean corporationAsset = bCorp;
		boolean singleton  = false;

		String name = ApiIdConverter.name(typeID, settings);
		String group = ApiIdConverter.group(typeID, settings);
		String category = ApiIdConverter.category(typeID, settings);
		double basePrice = ApiIdConverter.priceBase(typeID, settings);
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings);
		float volume = ApiIdConverter.volume(typeID, settings);
		String meta = ApiIdConverter.meta(typeID, settings);

		String owner = ApiIdConverter.owner(human, bCorp);

		String location = ApiIdConverter.location(locationID, null, settings);
		String container = ApiIdConverter.container(locationID, null);
		String region = ApiIdConverter.region(locationID, null, settings);
		String security = ApiIdConverter.security(locationID, null, settings);

		return new EveAsset(name, group, category, owner, count, location, container, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security);
	}

	public static List<EveAsset> apiIndustryJob(List<ApiIndustryJob> industryJobs, SettingsInterface settings, Human human, boolean bCorp, List<Long> bpos){
		List<EveAsset> eveAssets = new Vector<EveAsset>();
		for (int a = 0; a < industryJobs.size(); a++){
			ApiIndustryJob apiIndustryJob = industryJobs.get(a);
			long id = apiIndustryJob.getInstalledItemID();
			if (apiIndustryJob.getCompleted() == 0){
				EveAsset eveAsset = apiIndustryJobToEveAsset(apiIndustryJob, settings, human, bCorp);
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
	
	private static EveAsset apiIndustryJobToEveAsset(ApiIndustryJob apiIndustryJob, SettingsInterface settings, Human human, boolean bCorp){
		int typeID = (int) apiIndustryJob.getInstalledItemTypeID();
		int locationID = (int) apiIndustryJob.getInstalledItemLocationID();
		long count = apiIndustryJob.getInstalledItemQuantity();
		long id = apiIndustryJob.getInstalledItemID();
		int nFlag = apiIndustryJob.getInstalledItemFlag();
		boolean corporationAsset = bCorp;
		boolean singleton  = false;

		String flag = ApiIdConverter.flag(nFlag);

		String name = ApiIdConverter.name(typeID, settings);
		String group = ApiIdConverter.group(typeID, settings);
		String category = ApiIdConverter.category(typeID, settings);
		double basePrice = ApiIdConverter.priceBase(typeID, settings);
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings);
		float volume = ApiIdConverter.volume(typeID, settings);
		String meta = ApiIdConverter.meta(typeID, settings);

		String owner = ApiIdConverter.owner(human, bCorp);

		String location = ApiIdConverter.location(locationID, null, settings);
		if (location.contains("Error !")){
			locationID = (int) apiIndustryJob.getContainerLocationID();
			location = ApiIdConverter.location(locationID, null, settings);
		}
		if (location.contains("Error !")){
			location = "Unknown";
		}
		String container = ApiIdConverter.container(locationID, null);
		String region = ApiIdConverter.region(locationID, null, settings);
		String security = ApiIdConverter.security(locationID, null, settings);

		return new EveAsset(name, group, category, owner, count, location, container, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security);
	}

	public static List<EveAsset> apiAsset(SettingsInterface setttings, Human human, List<ApiAsset> assets, boolean bCorp){
		List<EveAsset> eveAssets = new Vector<EveAsset>();
		apiAsset(setttings, human, assets, eveAssets, null, bCorp);
		return eveAssets;
	}
	private static void apiAsset(SettingsInterface setttings, Human human, List<ApiAsset> assets, List<EveAsset> eveAssets, EveAsset parentEveAsset, boolean bCorp){
		for (int a = 0; a < assets.size(); a++){
			ApiAsset asset = assets.get(a);
			EveAsset eveAsset = apiAssetsToEveAsset(setttings, human, asset, parentEveAsset, bCorp);
			if (parentEveAsset == null){
				eveAssets.add(eveAsset);
			} else {
				parentEveAsset.addEveAsset(eveAsset);
			}
			apiAsset(setttings, human, new Vector<ApiAsset>(asset.getAssets()), eveAssets, eveAsset, bCorp);
		}
	}
	private static EveAsset apiAssetsToEveAsset(SettingsInterface settings, Human human, ApiAsset apiAsset, EveAsset parentEveAsset, boolean bCorp){
		String name = ApiIdConverter.name(apiAsset.getTypeID(), settings); //OK
		String group = ApiIdConverter.group(apiAsset.getTypeID(), settings); //OK
		String category = ApiIdConverter.category(apiAsset.getTypeID(), settings); //OK
		String owner = ApiIdConverter.owner(human, bCorp); //Semi-OK (Fix not confirmed)
		long count = apiAsset.getQuantity(); //OK
		String location = ApiIdConverter.location(apiAsset.getLocationID(), parentEveAsset, settings); //NOT OKAY!
		String container = ApiIdConverter.container(apiAsset.getLocationID(), parentEveAsset); //Should be okay
		String flag = ApiIdConverter.flag(apiAsset.getFlag()); //should be okay
		double basePrice = ApiIdConverter.priceBase(apiAsset.getTypeID(), settings); //OK
		String meta = ApiIdConverter.meta(apiAsset.getTypeID(), settings); //OK - but some is missiong from data export
		boolean marketGroup = ApiIdConverter.marketGroup(apiAsset.getTypeID(), settings); //OK
		float volume = ApiIdConverter.volume(apiAsset.getTypeID(), settings);
		String region = ApiIdConverter.region(apiAsset.getLocationID(), parentEveAsset, settings);
		long id = apiAsset.getItemID(); //OK
		int typeID = apiAsset.getTypeID(); //OK
		boolean corporationAsset = bCorp; //Semi-OK - OLD: (owner.equals(human.getCorporation()));
		boolean singleton  = apiAsset.getSingleton();
		String security = ApiIdConverter.security(apiAsset.getLocationID(), parentEveAsset, settings); //NOT OKAY!

		return new EveAsset(name, group, category, owner, count, location, container, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, apiAsset.getLocationID(), singleton, security);
	}
	public static List<MarketOrder> apiMarketOrdersToMarketOrders(List<ApiMarketOrder> apiMarketOrders, SettingsInterface settings){
		List<MarketOrder> marketOrders = new Vector<MarketOrder>();
		for (int a = 0; a < apiMarketOrders.size(); a++){
			marketOrders.add(apiMarketOrderToMarketOrder(apiMarketOrders.get(a), settings));
		}
		return marketOrders;
	}
	private static MarketOrder apiMarketOrderToMarketOrder(ApiMarketOrder apiMarketOrder, SettingsInterface settings){
		String name = ApiIdConverter.name((int)apiMarketOrder.getTypeID(), settings);
		String location = ApiIdConverter.location((int)apiMarketOrder.getStationID(), null, settings);
		return new MarketOrder(apiMarketOrder, name, location);
	}
	public static List<IndustryJob> apiIndustryJobsToIndustryJobs(List<ApiIndustryJob> apiIndustryJobs, SettingsInterface settings, String owner){
		List<IndustryJob> industryJobs = new Vector<IndustryJob>();
		for (int a = 0; a < apiIndustryJobs.size(); a++){
			industryJobs.add(apiIndustryJobToIndustryJob(apiIndustryJobs.get(a), settings, owner));
		}
		return industryJobs;
	}

	private static IndustryJob apiIndustryJobToIndustryJob(ApiIndustryJob apiIndustryJob, SettingsInterface settings, String owner){
		String name = ApiIdConverter.name((int)apiIndustryJob.getInstalledItemTypeID(), settings);
		String location = ApiIdConverter.location((int)apiIndustryJob.getInstalledItemLocationID(), null, settings);
		return new IndustryJob(apiIndustryJob, name, location, owner);
	}
}
