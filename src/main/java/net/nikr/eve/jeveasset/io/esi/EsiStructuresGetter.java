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

import java.util.ArrayList;
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
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.StructureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsiStructuresGetter extends AbstractEsiGetter {

	private static final Logger LOG = LoggerFactory.getLogger(EsiStructuresGetter.class);

	public EsiStructuresGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getStructuresNextUpdate(), TaskType.STRUCTURES);
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		if (owner.isCorporation()) {
			return; //Corporation accounts don't get structures
		}
		Map<Long, StructureResponse> responses = updateList(getIDs(owner), new ListHandler<Long, StructureResponse>() {
			@Override
			public StructureResponse get(ApiClient apiClient, Long t) throws ApiException {
				try {
					return getUniverseApiAuth(apiClient).getUniverseStructuresStructureId(t, DATASOURCE, null, USER_AGENT, null);
				} catch (ApiException ex) {
					if (ex.getCode() != 403 && ex.getCode() != 404) { //Ignore 403: Forbidden and 404: Structure not found
						throw ex;
					} else { //Ignore error, but, still handle error limit
						LOG.warn("Failed to find locationID: " + t);
					}
				}
				return null;
			}
		});

		List<Citadel> citadels = new ArrayList<Citadel>();
		for (Map.Entry<Long, StructureResponse> entry : responses.entrySet()) {
			citadels.add(ApiIdConverter.getCitadel(entry.getValue(), entry.getKey()));
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

	private Set<Long> getIDs(OwnerType owner) {
		Set<Long> itemIDs = new HashSet<Long>();
		Set<Long> locationIDs = new HashSet<Long>();
		for (RawAsset asset : owner.getAssets()) {
			long locationID = asset.getLocationID();
			MyLocation location = ApiIdConverter.getLocation(locationID);
			if (location.isEmpty() || location.isUserLocation() || location.isCitadel()) {
				locationIDs.add(location.getLocationID());
			}
		}
		getAssetItemIDs(itemIDs, owner.getAssets());
		for (RawBlueprint blueprint : owner.getBlueprints().values()) {
			itemIDs.add(blueprint.getItemID());
			MyLocation location = ApiIdConverter.getLocation(blueprint.getLocationID());
			if (location.isEmpty() || location.isUserLocation() || location.isCitadel()) {
				locationIDs.add(location.getLocationID());
			}
		}
		for (RawContract contract : owner.getContracts().keySet()) {
			long locationEndID = contract.getEndLocationID();
			MyLocation locationEnd = ApiIdConverter.getLocation(locationEndID);
			if (locationEnd.isEmpty() || locationEnd.isUserLocation() || locationEnd.isCitadel()) {
				locationIDs.add(locationEnd.getLocationID());
			}
			long locationStartID = contract.getStartLocationID();
			MyLocation locationStart = ApiIdConverter.getLocation(locationStartID);
			if (locationStart.isEmpty() || locationStart.isUserLocation() || locationStart.isCitadel()) {
				locationIDs.add(locationStart.getLocationID());
			}
		}
		for (MyIndustryJob industryJob : owner.getIndustryJobs()) {
			long locationID = industryJob.getLocationID();
			MyLocation location = ApiIdConverter.getLocation(locationID);
			if (location.isEmpty() || location.isUserLocation() || location.isCitadel()) {
				locationIDs.add(location.getLocationID());
			}
		}
		for (RawMarketOrder marketOrder : owner.getMarketOrders()) {
			long locationID = marketOrder.getLocationID();
			MyLocation location = ApiIdConverter.getLocation(locationID);
			if (location.isEmpty() || location.isUserLocation() || location.isCitadel()) {
				locationIDs.add(location.getLocationID());
			}
		}
		locationIDs.removeAll(itemIDs);
		return locationIDs;
	}

	private void getAssetItemIDs(Set<Long> itemIDs, List<MyAsset> assets) {
		for (MyAsset asset : assets) {
			itemIDs.add(asset.getItemID());
			getAssetItemIDs(itemIDs, asset.getAssets());
		}
	}
}
