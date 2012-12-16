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
import com.beimin.eveapi.shared.contract.EveContract;
import com.beimin.eveapi.shared.contract.items.EveContractItem;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.tabs.contracts.Contract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractItem;
import net.nikr.eve.jeveasset.i18n.General;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApiConverter {

	private static final Logger LOG = LoggerFactory.getLogger(ApiConverter.class);;

	private ApiConverter() { }

	public static List<Asset> apiMarketOrder(final List<ApiMarketOrder> marketOrders, final Human human, final Settings settings) {
		List<Asset> eveAssets = new ArrayList<Asset>();
		for (ApiMarketOrder apiMarketOrder : marketOrders) {
			if (apiMarketOrder.getOrderState() == 0 && apiMarketOrder.getVolRemaining() > 0
					&& ((apiMarketOrder.getBid() < 1 && settings.isIncludeSellOrders())
					|| (apiMarketOrder.getBid() > 0 && settings.isIncludeBuyOrders()))
					) {
				Asset eveAsset = apiMarketOrderToEveAsset(apiMarketOrder, human, settings);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static Asset apiMarketOrderToEveAsset(final ApiMarketOrder apiMarketOrder, final Human human, final Settings settings) {
		int typeID = apiMarketOrder.getTypeID();
		long locationID = apiMarketOrder.getStationID();
		long count = apiMarketOrder.getVolRemaining();
		long itemId = apiMarketOrder.getOrderID();
		String flag;
		if (apiMarketOrder.getBid() < 1) { //Sell
			flag = General.get().marketOrderSellFlag();
		} else { //Buy
			flag = General.get().marketOrderBuyFlag();
		}
		int flagID = 0;
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
		int meta = ApiIdConverter.meta(typeID, settings.getItems());
		String tech = ApiIdConverter.tech(typeID, settings.getItems());
		String owner = human.getName();
		String location = ApiIdConverter.locationName(locationID, null, settings.getLocations());
		String region = ApiIdConverter.regionName(locationID, null, settings.getLocations());
		String security = ApiIdConverter.security(locationID, null, settings.getLocations());
		String solarSystem = ApiIdConverter.systemName(locationID, null, settings.getLocations());
		long solarSystemId  = ApiIdConverter.systemID(locationID, null, settings.getLocations());
		List<Asset> parents = new ArrayList<Asset>();
		boolean piMaterial = ApiIdConverter.piMaterial(typeID, settings.getItems());

		return new Asset(name, group, category, owner, count, location, parents, flag, flagID, basePrice, meta, tech, itemId, typeID, marketGroup, corporation, volume, region, locationID, singleton, security, solarSystem, solarSystemId, rawQuantity, piMaterial);
	}

	public static List<Asset> apiIndustryJob(final List<ApiIndustryJob> industryJobs, final Human human, final Settings settings) {
		List<Asset> eveAssets = new ArrayList<Asset>();
		for (ApiIndustryJob apiIndustryJob : industryJobs) {
			if (!apiIndustryJob.isCompleted()) {
				Asset eveAsset = apiIndustryJobToEveAsset(apiIndustryJob, human, settings);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static Asset apiIndustryJobToEveAsset(final ApiIndustryJob apiIndustryJob, final Human human, final Settings settings) {
		int typeID = apiIndustryJob.getInstalledItemTypeID();
		long locationID = apiIndustryJobLocationId(apiIndustryJob, settings);
		long count = apiIndustryJob.getInstalledItemQuantity();
		long id = apiIndustryJob.getInstalledItemID();
		int flagID = apiIndustryJob.getInstalledItemFlag();
		boolean corporation = human.isCorporation();
		boolean singleton  = false;
		int rawQuantity;
		if (apiIndustryJob.getInstalledItemCopy() == 0) {
			rawQuantity = 0; //0 = BPO
		} else {
			rawQuantity = -2; //-2 = BPC
		}

		//Calculated:
		String name = ApiIdConverter.typeName(typeID, settings.getItems());
		String group = ApiIdConverter.group(typeID, settings.getItems());
		String category = ApiIdConverter.category(typeID, settings.getItems());
		double basePrice = ApiIdConverter.priceBase(typeID, settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
		float volume = ApiIdConverter.volume(typeID, settings.getItems());
		int meta = ApiIdConverter.meta(typeID, settings.getItems());
		String tech = ApiIdConverter.tech(typeID, settings.getItems());
		String owner = human.getName();
		String location = ApiIdConverter.locationName(locationID, null, settings.getLocations());
		String region = ApiIdConverter.regionName(locationID, null, settings.getLocations());
		String security = ApiIdConverter.security(locationID, null, settings.getLocations());
		String solarSystem = ApiIdConverter.systemName(locationID, null, settings.getLocations());
		long solarSystemId  = ApiIdConverter.systemID(locationID, null, settings.getLocations());
		List<Asset> parents = new ArrayList<Asset>();
		String flag = ApiIdConverter.flag(flagID, null, settings.getItemFlags());
		boolean piMaterial = ApiIdConverter.piMaterial(typeID, settings.getItems());

		return new Asset(name, group, category, owner, count, location, parents, flag, flagID, basePrice, meta, tech, id, typeID, marketGroup, corporation, volume, region, locationID, singleton, security, solarSystem, solarSystemId, rawQuantity, piMaterial);
	}

	public static List<Asset> apiAsset(final Human human, final List<EveAsset<?>> assets, final Settings settings) {
		List<Asset> eveAssets = new ArrayList<Asset>();
		apiAsset(human, assets, eveAssets, null, settings);
		return eveAssets;
	}
	private static void apiAsset(final Human human, final List<EveAsset<?>> assets, final List<Asset> eveAssets, final Asset parentEveAsset, final Settings settings) {
		for (EveAsset<?> asset : assets) {
			Asset eveAsset = apiAssetsToEveAsset(human, asset, parentEveAsset, settings);
			if (parentEveAsset == null) {
				eveAssets.add(eveAsset);
			} else {
				parentEveAsset.addEveAsset(eveAsset);
			}
			apiAsset(human, new ArrayList<EveAsset<?>>(asset.getAssets()), eveAssets, eveAsset, settings);
		}
	}
	private static Asset apiAssetsToEveAsset(final Human human, final EveAsset<?> apiAsset, final Asset parentEveAsset, final Settings settings) {
		long count = apiAsset.getQuantity();
		int flagID = apiAsset.getFlag();
		long itemId = apiAsset.getItemID();
		int typeID = apiAsset.getTypeID();
		long locationID;
		if (apiAsset.getLocationID() != null) { //Top level
			locationID = apiAsset.getLocationID();
		} else if (parentEveAsset != null) { //Sub level
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
		int meta = ApiIdConverter.meta(typeID, settings.getItems());
		String tech = ApiIdConverter.tech(typeID, settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(apiAsset.getTypeID(), settings.getItems());
		float volume = ApiIdConverter.volume(apiAsset.getTypeID(), settings.getItems());
		String security = ApiIdConverter.security(locationID, parentEveAsset, settings.getLocations());
		String region = ApiIdConverter.regionName(locationID, parentEveAsset, settings.getLocations());
		String location = ApiIdConverter.locationName(locationID, parentEveAsset, settings.getLocations());
		String solarSystem = ApiIdConverter.systemName(locationID, parentEveAsset, settings.getLocations());
		long solarSystemId  = ApiIdConverter.systemID(locationID, parentEveAsset, settings.getLocations());
		List<Asset> parents = ApiIdConverter.parents(parentEveAsset);
		String flag = ApiIdConverter.flag(flagID, parentEveAsset, settings.getItemFlags());
		boolean piMaterial = ApiIdConverter.piMaterial(typeID, settings.getItems());

		return new Asset(name, group, category, owner, count, location, parents, flag, flagID, basePrice, meta, tech, itemId, typeID, marketGroup, corporation, volume, region, locationID, singleton, security, solarSystem, solarSystemId, rawQuantity, piMaterial);
	}
	public static List<MarketOrder> apiMarketOrdersToMarketOrders(final Human human, final List<ApiMarketOrder> apiMarketOrders, final Settings settings) {
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		for (ApiMarketOrder apiMarketOrder : apiMarketOrders) {
			marketOrders.add(apiMarketOrderToMarketOrder(human, apiMarketOrder, settings));
		}
		return marketOrders;
	}
	private static MarketOrder apiMarketOrderToMarketOrder(final Human human, final ApiMarketOrder apiMarketOrder, final Settings settings) {
		String name = ApiIdConverter.typeName(apiMarketOrder.getTypeID(), settings.getItems());
		String location = ApiIdConverter.locationName(apiMarketOrder.getStationID(), null, settings.getLocations());
		String system = ApiIdConverter.systemName(apiMarketOrder.getStationID(), null, settings.getLocations());
		String region = ApiIdConverter.regionName(apiMarketOrder.getStationID(), null, settings.getLocations());
		String owner = human.getName();
		return new MarketOrder(apiMarketOrder, name, location, system, region, owner);
	}
	public static List<ContractItem> eveContractItemsToContractItems(final Human human, final Map<EveContract, List<EveContractItem>> contracts, final Settings settings) {
		List<ContractItem> contractItem = new ArrayList<ContractItem>();
		for (Entry<EveContract, List<EveContractItem>> entry : contracts.entrySet()) {
			for (EveContractItem eveContractItem : entry.getValue()) {
				contractItem.add(eveContractItemToContractItem(human, eveContractItem, entry.getKey(), settings));
			}
		}
		return contractItem;
	}
	private static ContractItem eveContractItemToContractItem(final Human human, final EveContractItem eveContractItem, EveContract eveContract, final Settings settings) {
		String name = ApiIdConverter.typeName(eveContractItem.getTypeID(), settings.getItems());
		Contract contract = eveContractToContract(human, eveContract, settings);
		return new ContractItem(eveContractItem, contract, name);
	}
	private static Contract eveContractToContract(final Human human, final EveContract eveContract, final Settings settings) {
		return new Contract(eveContract);
	}
	public static List<IndustryJob> apiIndustryJobsToIndustryJobs(final List<ApiIndustryJob> apiIndustryJobs, final String owner, final Settings settings) {
		List<IndustryJob> industryJobs = new ArrayList<IndustryJob>();
		for (ApiIndustryJob apiIndustryJob : apiIndustryJobs) {
			industryJobs.add(apiIndustryJobToIndustryJob(apiIndustryJob, owner, settings));
		}
		return industryJobs;
	}

	private static IndustryJob apiIndustryJobToIndustryJob(final ApiIndustryJob apiIndustryJob, final String owner, final Settings settings) {
		String name = ApiIdConverter.typeName(apiIndustryJob.getInstalledItemTypeID(), settings.getItems());
		long locationID = apiIndustryJobLocationId(apiIndustryJob, settings);
		String location = ApiIdConverter.locationName(locationID, null, settings.getLocations());
		String system = ApiIdConverter.systemName(locationID, null, settings.getLocations());
		String region = ApiIdConverter.regionName(locationID, null, settings.getLocations());
		return new IndustryJob(apiIndustryJob, name, location, system, region, owner);
	}

	private static long apiIndustryJobLocationId(final ApiIndustryJob apiIndustryJob, final Settings settings) {
		boolean location = ApiIdConverter.locationTest(apiIndustryJob.getInstalledItemLocationID(), null, settings.getLocations());
		if (location) {
			return apiIndustryJob.getInstalledItemLocationID();
		}
		location = ApiIdConverter.locationTest(apiIndustryJob.getContainerLocationID(), null, settings.getLocations());
		if (location) {
			return apiIndustryJob.getContainerLocationID();
		}
		LOG.error("Failed to find locationID for IndustryJob. InstalledItemLocationID: " + apiIndustryJob.getInstalledItemLocationID() + " - ContainerLocationID: " + apiIndustryJob.getContainerLocationID());
		return -1;
	}
}
