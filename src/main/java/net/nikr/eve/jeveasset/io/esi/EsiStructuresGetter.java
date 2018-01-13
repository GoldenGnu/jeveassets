/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.gui.tabs.values.Value.AssetValue;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.StructureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsiStructuresGetter extends AbstractEsiGetter {

	private static final Logger LOG = LoggerFactory.getLogger(EsiStructuresGetter.class);
	private final static Set<Long> IDS = new HashSet<Long>();
	private final static Set<Long> DONE = new HashSet<Long>();
	private final boolean tracker;

	public EsiStructuresGetter(UpdateTask updateTask, EsiOwner owner, boolean tracker) {
		super(updateTask, owner, false, owner.getStructuresNextUpdate(), TaskType.STRUCTURES, NO_RETRIES);
		this.tracker = tracker;
	}

	public static String estimate(List<EsiOwner> esiOwners,  List<OwnerType> ownerTypes, Set<MyLocation> locations, boolean tracker) {
		int total = 0;
		if (locations != null) { //Locations
			EsiStructuresGetter.createIDsFromLocations(locations);
			total = IDS.size() * esiOwners.size();
		} else if (ownerTypes != null) { 
			EsiStructuresGetter.createIDsFromOwners(ownerTypes, tracker);
			total = IDS.size() * esiOwners.size();
		} else {
			DONE.clear();
			for (EsiOwner esiOwner : esiOwners) {
				total = total + buildIDs(esiOwner, tracker).size();
			}
		}
		total = (int)(total / 100.0 * 60.0 * 1000.0); //100 errors a minute to ms
		return Formater.milliseconds(total, true, true);
	}

	public static void createIDsFromOwners(List<OwnerType> ownerTypes, boolean tracker) {
		IDS.clear();
		DONE.clear();
		IDS.addAll(buildIDs(ownerTypes, tracker));
	}

	public static void createIDsFromLocations(Set<MyLocation> locations) {
		IDS.clear();
		DONE.clear();
		IDS.addAll(buildIDs(locations));
	}

	public static void createIDsFromOwner() {
		IDS.clear();
		DONE.clear();
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		if (owner.isCorporation()) {
			return; //Corporation accounts don't get structures
		}
		boolean ownerUpdate = IDS.isEmpty();
		if (ownerUpdate) {
			IDS.addAll(buildIDs(owner, tracker));
		}
		Map<Long, StructureResponse> responses = updateListSlow(IDS, true, DEFAULT_RETRIES, new ListHandlerSlow<Long, StructureResponse>() {
			@Override
			public StructureResponse get(ApiClient apiClient, Long k) throws ApiException {
				pause();
				return getUniverseApiAuth(apiClient).getUniverseStructuresStructureId(k, DATASOURCE, null, USER_AGENT, null);
			}
			@Override
			protected void handle(ApiException ex, Long k) throws ApiException {
				if ((ex.getCode() == 403 && ex.getMessage().toLowerCase().contains("forbidden"))
						|| (ex.getCode() == 404 && ex.getMessage().toLowerCase().contains("structure not found"))
						|| (ex.getCode() == 502 && ex.getMessage().toLowerCase().contains("could not determine docking access"))) {
					LOG.warn("Failed to find locationID: " + k);
				} else {
					LOG.error("Failed to find locationID: " + k, ex);
					throw ex;
				}
			}
		});

		List<Citadel> citadels = new ArrayList<Citadel>();
		for (Map.Entry<Long, StructureResponse> entry : responses.entrySet()) {
			citadels.add(ApiIdConverter.getCitadel(entry.getValue(), entry.getKey()));
		}
		if (ownerUpdate) {
			DONE.addAll(responses.keySet()); //Add Completed
			IDS.clear();
		} else {
			IDS.removeAll(responses.keySet()); //Remove completed structures
		}
		CitadelGetter.set(citadels);
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setStructuresNextUpdate(date);
	}

	@Override
	protected boolean inScope() {
		if (owner.isCorporation()) {
			return true; //Overwrite the default, so, we don't get errors
		} else {
			return owner.isStructures();
		}
	}

	private static Set<Long> buildIDs(EsiOwner esiOwner, boolean tracker) {
		Set<Long> locationIDs = buildIDs(Collections.singletonList(esiOwner), tracker);
		locationIDs.removeAll(DONE);
		return locationIDs;
	}

	private static Set<Long> buildIDs(Set<MyLocation> locations) {
		Set<Long> locationIDs = new HashSet<Long>();
		for (MyLocation locationEnd : locations) {
			if (locationEnd.isEmpty() || locationEnd.isUserLocation() || locationEnd.isCitadel()) {
				locationIDs.add(locationEnd.getLocationID());
			}
		}
		return locationIDs;
	}

	private static Set<Long> buildIDs(List<OwnerType> ownerTypes, boolean tracker) {
		Set<Long> itemIDs = new HashSet<Long>();
		Set<Long> locationIDs = new HashSet<Long>();
		if (tracker) {
			for (List<Value> values : Settings.get().getTrackerData().values()) {
				for (Value value : values) {
					for (AssetValue assetValue : value.getAssetsFilter().keySet()) {
						add(locationIDs, assetValue.getLocationID());
					}
				}
			}
		}
		for (OwnerType ownerType : ownerTypes) {
			for (RawAsset asset : ownerType.getAssets()) {
				add(locationIDs, asset.getLocationID());
			}
			getAssetItemIDs(itemIDs, ownerType.getAssets());
			for (RawBlueprint blueprint : ownerType.getBlueprints().values()) {
				itemIDs.add(blueprint.getItemID());
				add(locationIDs, blueprint.getLocationID());
			}
			for (RawContract contract : ownerType.getContracts().keySet()) {
				add(locationIDs, contract.getEndLocationID());
				add(locationIDs, contract.getStartLocationID());
			}
			for (MyIndustryJob industryJob : ownerType.getIndustryJobs()) {
				add(locationIDs, industryJob.getStationID());
				add(locationIDs, industryJob.getBlueprintLocationID());
				add(locationIDs, industryJob.getOutputLocationID());
			}
			for (RawMarketOrder marketOrder : ownerType.getMarketOrders()) {
				add(locationIDs, marketOrder.getLocationID());
			}
		}
		locationIDs.removeAll(itemIDs);
		return locationIDs;
	}

	private static void getAssetItemIDs(Set<Long> itemIDs, List<MyAsset> assets) {
		for (MyAsset asset : assets) {
			itemIDs.add(asset.getItemID());
			getAssetItemIDs(itemIDs, asset.getAssets());
		}
	}

	private static void add(Set<Long> locationIDs, Long locationID) {
		if (locationID == null) {
			return;
		}
		if (locationID < 100000000) {
			return;
		}
		MyLocation location = ApiIdConverter.getLocation(locationID);
		if (location.isEmpty() || location.isUserLocation() || location.isCitadel()) {
			locationIDs.add(location.getLocationID());
		}
	}
}
