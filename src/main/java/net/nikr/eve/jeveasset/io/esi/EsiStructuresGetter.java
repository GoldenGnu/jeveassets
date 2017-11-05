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
	private static Set<Long> IDS;
	private final List<OwnerType> ownerTypes;
	
	public EsiStructuresGetter(UpdateTask updateTask, EsiOwner owner, List<OwnerType> ownerTypes) {
		super(updateTask, owner, false, owner.getStructuresNextUpdate(), TaskType.STRUCTURES, NO_RETRIES);
		this.ownerTypes = ownerTypes;
	}

	public static void reset() {
		IDS = null;
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		if (owner.isCorporation()) {
			return; //Corporation accounts don't get structures
		}
		buildIDs(ownerTypes);
		Map<Long, StructureResponse> responses = updateListSlow(IDS, true, DEFAULT_RETRIES, new ListHandlerSlow<Long, StructureResponse>() {
			@Override
			public StructureResponse get(ApiClient apiClient, Long k) throws ApiException {
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

	private static synchronized void buildIDs(List<OwnerType> ownerTypes) {
		if (IDS == null) {
			Set<Long> itemIDs = new HashSet<Long>();
			Set<Long> locationIDs = new HashSet<Long>();
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
			IDS = locationIDs;
		}
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
		MyLocation locationEnd = ApiIdConverter.getLocation(locationID);
		if (locationEnd.isEmpty() || locationEnd.isUserLocation() || locationEnd.isCitadel()) {
			locationIDs.add(locationEnd.getLocationID());
		}
	}
}
