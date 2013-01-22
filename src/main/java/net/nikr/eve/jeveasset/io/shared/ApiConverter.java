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

	public static List<Asset> apiMarketOrder(final List<ApiMarketOrder> marketOrders, final Owner owner, final Settings settings) {
		List<Asset> eveAssets = new ArrayList<Asset>();
		for (ApiMarketOrder apiMarketOrder : marketOrders) {
			if (apiMarketOrder.getOrderState() == 0 && apiMarketOrder.getVolRemaining() > 0
					&& ((apiMarketOrder.getBid() < 1 && settings.isIncludeSellOrders())
					|| (apiMarketOrder.getBid() > 0 && settings.isIncludeBuyOrders()))
					) {
				Asset eveAsset = apiMarketOrderToEveAsset(apiMarketOrder, owner, settings);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static Asset apiMarketOrderToEveAsset(final ApiMarketOrder apiMarketOrder, final Owner owner, final Settings settings) {
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
		boolean singleton  = true;
		int rawQuantity = 0;

		return createAsset(settings, null, owner.isCorporation(), owner.getName(), owner.getOwnerID(), count, flagID, itemId, typeID, locationID, singleton, rawQuantity, flag);
	}

	public static List<Asset> apiIndustryJob(final List<ApiIndustryJob> industryJobs, final Owner owner, final Settings settings) {
		List<Asset> eveAssets = new ArrayList<Asset>();
		for (ApiIndustryJob apiIndustryJob : industryJobs) {
			if (!apiIndustryJob.isCompleted()) {
				Asset eveAsset = apiIndustryJobToEveAsset(apiIndustryJob, owner, settings);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static Asset apiIndustryJobToEveAsset(final ApiIndustryJob apiIndustryJob, final Owner owner, final Settings settings) {
		int typeID = apiIndustryJob.getInstalledItemTypeID();
		long locationID = apiIndustryJobLocationId(apiIndustryJob, settings);
		long count = apiIndustryJob.getInstalledItemQuantity();
		long id = apiIndustryJob.getInstalledItemID();
		int flagID = apiIndustryJob.getInstalledItemFlag();
		boolean singleton  = false;
		int rawQuantity;
		if (apiIndustryJob.getInstalledItemCopy() == 0) {
			rawQuantity = 0; //0 = BPO
		} else {
			rawQuantity = -2; //-2 = BPC
		}

		return createAsset(settings, null, owner.isCorporation(), owner.getName(), owner.getOwnerID(), count, flagID, id, typeID, locationID, singleton, rawQuantity, null);
	}

	public static List<Asset> apiAsset(final Owner owner, final List<EveAsset<?>> assets, final Settings settings) {
		List<Asset> eveAssets = new ArrayList<Asset>();
		apiAsset(owner, assets, eveAssets, null, settings);
		return eveAssets;
	}
	private static void apiAsset(final Owner owner, final List<EveAsset<?>> assets, final List<Asset> eveAssets, final Asset parentEveAsset, final Settings settings) {
		for (EveAsset<?> asset : assets) {
			Asset eveAsset = apiAssetsToEveAsset(owner, asset, parentEveAsset, settings);
			if (parentEveAsset == null) {
				eveAssets.add(eveAsset);
			} else {
				parentEveAsset.addEveAsset(eveAsset);
			}
			apiAsset(owner, new ArrayList<EveAsset<?>>(asset.getAssets()), eveAssets, eveAsset, settings);
		}
	}

	private static Asset apiAssetsToEveAsset(final Owner owner, final EveAsset<?> apiAsset, final Asset parentEveAsset, final Settings settings) {
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
		int rawQuantity = apiAsset.getRawQuantity();

		return createAsset(settings, parentEveAsset, owner.isCorporation(), owner.getName(), owner.getOwnerID(), count, flagID, itemId, typeID, locationID, singleton, rawQuantity, null);

	}
	public static List<MarketOrder> apiMarketOrdersToMarketOrders(final Owner owner, final List<ApiMarketOrder> apiMarketOrders, final Settings settings) {
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		for (ApiMarketOrder apiMarketOrder : apiMarketOrders) {
			marketOrders.add(apiMarketOrderToMarketOrder(owner, apiMarketOrder, settings));
		}
		return marketOrders;
	}
	private static MarketOrder apiMarketOrderToMarketOrder(final Owner owner, final ApiMarketOrder apiMarketOrder, final Settings settings) {
		String name = ApiIdConverter.typeName(apiMarketOrder.getTypeID(), settings.getItems());
		String location = ApiIdConverter.locationName(apiMarketOrder.getStationID(), null, settings.getLocations());
		String system = ApiIdConverter.systemName(apiMarketOrder.getStationID(), null, settings.getLocations());
		String region = ApiIdConverter.regionName(apiMarketOrder.getStationID(), null, settings.getLocations());
		String ownerName = owner.getName();
		return new MarketOrder(apiMarketOrder, name, location, system, region, ownerName);
	}
	public static List<ContractItem> eveContractItemsToContractItems(final Map<EveContract, List<EveContractItem>> contracts, final Settings settings) {
		List<ContractItem> contractItem = new ArrayList<ContractItem>();
		for (Entry<EveContract, List<EveContractItem>> entry : contracts.entrySet()) {
			for (EveContractItem eveContractItem : entry.getValue()) {
				contractItem.add(eveContractItemToContractItem(eveContractItem, entry.getKey(), settings));
			}
		}
		return contractItem;
	}
	private static ContractItem eveContractItemToContractItem(final EveContractItem eveContractItem, EveContract eveContract, final Settings settings) {
		String name = ApiIdConverter.typeName(eveContractItem.getTypeID(), settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(eveContractItem.getTypeID(), settings.getItems());
		Contract contract = eveContractToContract(eveContract, settings);
		return new ContractItem(eveContractItem, contract, name, marketGroup);
	}

	
	public static List<Asset> eveContracts(Map<EveContract, List<EveContractItem>> contracts, Settings settings, List<Long> contractIDs) {
		List<Asset> list = new ArrayList<Asset>();
		for (Map.Entry<EveContract, List<EveContractItem>> entry : contracts.entrySet()) {
			EveContract contract = entry.getKey();
			long contractID = contract.getContractID();
			if (!contractIDs.contains(contractID)) { //Only add each contract once!
				contractIDs.add(contractID);
				for (EveContractItem contractItem : entry.getValue()) {
					Asset asset = eveContract(contract, contractItem, settings);
					list.add(asset);
				}
			}
		}
		return list;
	}
	private static Asset eveContract(EveContract contract, EveContractItem contractItem, Settings settings) {
		long count = contractItem.getQuantity();
		int flagID = 0;
		String flag;
		if (contractItem.isIncluded()) { //Sell
			flag = General.get().contractIncluded();
		} else { //Buy
			flag = General.get().contractExcluded();
		}
		long itemId = 0;
		int typeID = contractItem.getTypeID();
		long locationID = contract.getStartStationID();
		boolean singleton  = contractItem.isSingleton();
		int rawQuantity = 0;
		long ownerID = contract.getIssuerID();
		String ownerName = settings.getOwners().get(ownerID);

		return createAsset(settings, null, false, ownerName, ownerID, count, flagID, itemId, typeID, locationID, singleton, rawQuantity, flag);
	}
	public static Contract eveContractToContract(final EveContract eveContract, final Settings settings) {
		String acceptor = ApiIdConverter.ownerName(eveContract.getAcceptorID(), settings.getOwners());
		String assignee = ApiIdConverter.ownerName(eveContract.getAssigneeID(), settings.getOwners());
		String issuerCorp = ApiIdConverter.ownerName(eveContract.getIssuerCorpID(), settings.getOwners());
		String issuer = ApiIdConverter.ownerName(eveContract.getIssuerID(), settings.getOwners());
		String endStation = ApiIdConverter.locationName(eveContract.getEndStationID(), null, settings.getLocations());
		String startStation = ApiIdConverter.locationName(eveContract.getStartStationID(), null, settings.getLocations());
		String system = ApiIdConverter.systemName(eveContract.getStartStationID(), null, settings.getLocations());
		String region = ApiIdConverter.regionName(eveContract.getStartStationID(), null, settings.getLocations());
		return new Contract(eveContract, acceptor, assignee, issuerCorp, issuer, endStation, startStation, system, region);
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

	public static Asset createAsset(final Settings settings, final Asset parentEveAsset,
			boolean corporation, String ownerName, long ownerID, long count, int flagID, long itemId,
			int typeID, long locationID, boolean singleton, int rawQuantity, String flag) {
		//Calculated:
		String name = ApiIdConverter.typeName(typeID, settings.getItems());
		String group = ApiIdConverter.group(typeID, settings.getItems());
		String category = ApiIdConverter.category(typeID, settings.getItems());
		double basePrice = ApiIdConverter.priceBase(typeID, settings.getItems());
		int meta = ApiIdConverter.meta(typeID, settings.getItems());
		String tech = ApiIdConverter.tech(typeID, settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
		float volume = ApiIdConverter.volume(typeID, settings.getItems());
		String security = ApiIdConverter.security(locationID, parentEveAsset, settings.getLocations());
		String region = ApiIdConverter.regionName(locationID, parentEveAsset, settings.getLocations());
		String location = ApiIdConverter.locationName(locationID, parentEveAsset, settings.getLocations());
		String solarSystem = ApiIdConverter.systemName(locationID, parentEveAsset, settings.getLocations());
		long solarSystemId  = ApiIdConverter.systemID(locationID, parentEveAsset, settings.getLocations());
		long regionID = ApiIdConverter.regionID(locationID, parentEveAsset, settings.getLocations());
		List<Asset> parents = ApiIdConverter.parents(parentEveAsset);
		if (flag == null) {
			flag = ApiIdConverter.flag(flagID, parentEveAsset, settings.getItemFlags());
		}
		boolean piMaterial = ApiIdConverter.piMaterial(typeID, settings.getItems());

		return new Asset(name, group, category, ownerName, count, location, parents, flag, flagID, basePrice, meta, tech, itemId, typeID, marketGroup, corporation, volume, region, locationID, singleton, security, solarSystem, solarSystemId, rawQuantity, piMaterial, regionID, ownerID);
	}
}
