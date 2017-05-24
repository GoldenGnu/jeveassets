/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.esi;

import com.beimin.eveapi.model.shared.AccountBalance;
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.model.shared.Blueprint;
import com.beimin.eveapi.model.shared.IndustryJob;
import com.beimin.eveapi.model.shared.MarketOrder;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.MyAccountBalance;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.data.esi.EsiOwner;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CharacterBlueprintsResponse;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterWalletsResponse;


public class EsiConverter {

	private EsiConverter() { }

	public static List<MyAsset> convertAssets(EsiOwner owner, List<CharacterAssetsResponse> responses) {
		List<Asset> assets = new ArrayList<Asset>();
		Map<Long, CharacterAssetsResponse> parents = new HashMap<Long, CharacterAssetsResponse>();
		Map<Long, Asset> cache = new HashMap<Long, Asset>();
		for (CharacterAssetsResponse response : responses) {
			parents.put(response.getItemId(), response);
		}
		for (CharacterAssetsResponse response : responses) {
			deepAsset(assets, parents, cache, owner, response);
		}
		return ApiConverter.convertAsset(assets, owner);
	}

	private static Asset deepAsset(List<Asset> assets, Map<Long, CharacterAssetsResponse> parents, Map<Long, Asset> cache, EsiOwner owner, CharacterAssetsResponse response) {
		Asset asset = cache.get(response.getItemId());
		if (asset != null) {
			return asset; //Asset already converted
		}
		CharacterAssetsResponse parent = parents.get(response.getLocationId());
		Asset parentEveAsset = null;
		if (parent != null) {
			parentEveAsset = cache.get(response.getLocationId());
			if (parentEveAsset == null) {
				parentEveAsset = deepAsset(assets, parents, cache, owner, parent);
				cache.put(parentEveAsset.getItemID(), parentEveAsset);
			}
		}

		asset = toAsset(response, parentEveAsset);
		cache.put(asset.getItemID(), asset);
		if (parent == null) {
			assets.add(asset);
		}
		return asset;
	}

	private static Asset toAsset(final CharacterAssetsResponse asset, final Asset parentAsset) {
		Asset eveApiAsset = new Asset();
		int count;
		int rawQuantity;
		boolean singleton = asset.getIsSingleton();
		if (null == asset.getQuantity()) { //Packaged
			count = 1;
			if (asset.getIsSingleton()) { //Unpackaged > Quantity should tell us if it's a BPC or BPO, but, it's null!!!
				rawQuantity = 0;
			} else { //Stacked > Packaged > BPO
				rawQuantity = -1; //BP0
			}
		} else {
			if (asset.getQuantity() < 0) { //rawQuantity: Quantity should tell us if it's a BPC or BPO
				count = 1;
				rawQuantity = asset.getQuantity();
				if (asset.getQuantity() == -2) { //BPC does not return the correct singleton value
					singleton = true;
				}
			} else if (asset.getQuantity() > 1) { //In stack > Packaged > BPO
				count = asset.getQuantity();
				rawQuantity = -1;
			} else if (!asset.getIsSingleton()) { //Not singleton > Packaged > BPO
				count = asset.getQuantity();
				rawQuantity = -1;
			} else { //Quantity = 0 or 1 and item is not packed
				count = 1;
				rawQuantity = 0;
			}
		}
		eveApiAsset.setItemID(asset.getItemId());
		eveApiAsset.setLocationID(asset.getLocationId());
		eveApiAsset.setTypeID(asset.getTypeId());
		eveApiAsset.setQuantity(count);
		eveApiAsset.setRawQuantity(rawQuantity);
		eveApiAsset.setFlag(toFlagID(asset.getLocationFlag().toString()));
		eveApiAsset.setSingleton(singleton);
		if (parentAsset != null) {
			parentAsset.getAssets().add(eveApiAsset);
		}
		return eveApiAsset;
	}

	public static List<MyAccountBalance> convertAccountBallances(EsiOwner owner, List<CharacterWalletsResponse> responses) {
		List<MyAccountBalance> accountBalances = new ArrayList<MyAccountBalance>();
		for (CharacterWalletsResponse response : responses) {
			AccountBalance accountBalance = new AccountBalance();
			accountBalance.setAccountID(0);
			accountBalance.setAccountKey(response.getWalletId());
			accountBalance.setBalance(response.getBalance());
			MyAccountBalance myAccountBalance = new MyAccountBalance(accountBalance, owner);
			accountBalances.add(myAccountBalance);
		}
		return accountBalances;
	}

	public static List<MyIndustryJob> convertIndustryJobs(EsiOwner owner, List<CharacterIndustryJobsResponse> responses) {
		List<IndustryJob> industryJobs = new ArrayList<IndustryJob>();
		for (CharacterIndustryJobsResponse response : responses) {
			industryJobs.add(toIndustryJob(response));
		}
		return ApiConverter.convertIndustryJobs(industryJobs, owner);
	}

	private static IndustryJob toIndustryJob(CharacterIndustryJobsResponse response) {
		IndustryJob industryJob = new IndustryJob();
		industryJob.setJobID(response.getJobId());
		industryJob.setInstallerID(response.getInstallerId());
		industryJob.setInstallerName(""); //Set in ProfileData
		industryJob.setFacilityID(response.getFacilityId());
		MyLocation location = ApiIdConverter.getLocation(response.getStationId());
		industryJob.setSolarSystemID(location.getSystemID());
		industryJob.setStationID(response.getStationId());
		industryJob.setActivityID(response.getActivityId());
		industryJob.setBlueprintID(response.getBlueprintId());
		industryJob.setBlueprintTypeID(response.getBlueprintTypeId());
		Item blueprint = ApiIdConverter.getItem(response.getBlueprintTypeId());
		industryJob.setBlueprintTypeName(blueprint.getTypeName());
		industryJob.setBlueprintLocationID(response.getBlueprintLocationId());
		industryJob.setOutputLocationID(response.getOutputLocationId());
		industryJob.setRuns(response.getRuns());
		industryJob.setCost(response.getCost());
		industryJob.setTeamID(0); //Teams have been removed from the game: https://community.eveonline.com/news/dev-blogs/teams-removal/
		industryJob.setLicensedRuns(response.getLicensedRuns());
		industryJob.setProbability(response.getProbability());
		industryJob.setProductTypeID(response.getProductTypeId());
		Item product = ApiIdConverter.getItem(response.getProductTypeId());
		industryJob.setProductTypeName(product.getTypeName());
		int status;
		switch (response.getStatus()) {
			case ACTIVE: status = 1; break;
			case PAUSED: status = 2; break;
			case READY: status = 3; break;
			case DELIVERED: status = 101; break;
			case CANCELLED: status = 102; break;
			case REVERTED: status = 103; break;
			default: status = 1; //Should never happen!
		}
		industryJob.setStatus(status);
		Date start = toDate(response.getStartDate());
		Date end = toDate(response.getEndDate());
		int timeInSeconds = 0;
		if (start != null && end != null) {
			try {
				timeInSeconds = Math.toIntExact(TimeUnit.MILLISECONDS.toSeconds(end.getTime() - start.getTime()));
			} catch (ArithmeticException ex) {
				//Failed to convert to int
			}
		}
		industryJob.setTimeInSeconds(timeInSeconds);
		industryJob.setCompletedDate(toDate(response.getCompletedDate()));
		industryJob.setStartDate(start);
		industryJob.setEndDate(end);
		industryJob.setPauseDate(toDate(response.getPauseDate()));
		industryJob.setCompletedCharacterID(response.getCompletedCharacterId());
		return industryJob;
	}

	static List<MyMarketOrder> convertMarketOrders(EsiOwner owner, List<CharacterOrdersResponse> responses) {
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		for (CharacterOrdersResponse response : responses) {
			marketOrders.add(toMarketOrder(owner, response));
		}
		return ApiConverter.convertMarketOrders(marketOrders, owner);
	}

	private static MarketOrder toMarketOrder(EsiOwner owner, CharacterOrdersResponse response) {
		MarketOrder marketOrder = new MarketOrder();
		marketOrder.setOrderID(response.getOrderId());
		marketOrder.setCharID(owner.getOwnerID());
		marketOrder.setStationID(response.getLocationId());
		marketOrder.setVolEntered(response.getVolumeTotal());
		marketOrder.setVolRemaining(response.getVolumeRemain());
		marketOrder.setMinVolume(response.getMinVolume());
		int state;
		switch(response.getState()) {
			case OPEN: state = 0; break;
			case CLOSED: state = 1; break;
			case EXPIRED: state = 2; break;
			case CANCELLED: state = 3; break;
			case PENDING: state = 4; break;
			case CHARACTER_DELETED: state = 5; break;
			default: state = 0; //Should never happen!
		}
		marketOrder.setOrderState(state);
		marketOrder.setTypeID(response.getTypeId());
		int range;
		switch (response.getRange()) {
			case REGION: range = 32767; break;
			case SOLARSYSTEM: range = 0; break;
			case STATION: range = -1; break;
			case _1: range = 1; break;
			case _2: range = 2; break;
			case _3: range = 3; break;
			case _4: range = 4; break;
			case _5: range = 5; break;
			case _10: range = 10; break;
			case _20: range = 20; break;
			case _30: range = 30; break;
			case _40: range = 40; break;
			default: range = 32767; //Should never happen!
		}
		marketOrder.setRange(range);
		marketOrder.setAccountKey(response.getAccountId());
		marketOrder.setDuration(response.getDuration());
		marketOrder.setEscrow(response.getEscrow());
		marketOrder.setPrice(response.getPrice());
		marketOrder.setBid(response.getIsBuyOrder() ? 1 : 0);
		marketOrder.setIssued(toDate(response.getIssued()));
		return marketOrder;
	}

	static Map<Long, Blueprint> convertBlueprints(EsiOwner owner, List<CharacterBlueprintsResponse> responses) {
		Map<Long, Blueprint> blueprints = new HashMap<Long, Blueprint>();
		for (CharacterBlueprintsResponse blueprint : responses) {
			blueprints.put(blueprint.getItemId(), toBlueprint(blueprint));
		}
		return blueprints;
	}

	private static Blueprint toBlueprint(CharacterBlueprintsResponse response) {
		Blueprint blueprint = new Blueprint();
		blueprint.setItemID(response.getItemId());
		blueprint.setLocationID(response.getLocationId());
		blueprint.setTypeID(response.getTypeId());
		Item item = ApiIdConverter.getItem(response.getTypeId());
		blueprint.setTypeName(item.getTypeName());
		blueprint.setFlagID(toFlagID(response.getLocationFlag().toString()));
		blueprint.setQuantity(response.getQuantity());
		blueprint.setTimeEfficiency(response.getTimeEfficiency());
		blueprint.setMaterialEfficiency(response.getMaterialEfficiency());
		blueprint.setRuns(response.getRuns());
		return blueprint;
	}

	private static Date toDate(OffsetDateTime dateTime) {
		if (dateTime == null) {
			return null;
		} else {
			return Date.from(dateTime.toInstant());
		}	
	}

	private static int toFlagID(String flagName) {
		int flagID = 0;
		for (ItemFlag flag : StaticData.get().getItemFlags().values()) {
			if (flag.getFlagName().equals(flagName)) {
				flagID = flag.getFlagID();
				break;
			}
		}
		return flagID;
	}
}
