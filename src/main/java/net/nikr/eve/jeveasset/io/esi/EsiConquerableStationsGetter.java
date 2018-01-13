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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.SovereigntyStructuresResponse;
import net.troja.eve.esi.model.UniverseNamesResponse;


public class EsiConquerableStationsGetter extends AbstractEsiGetter {

	public EsiConquerableStationsGetter(UpdateTask updateTask) {
		super(updateTask, null, false, Settings.get().getConquerableStationsNextUpdate(), TaskType.CONQUERABLE_STATIONS, NO_RETRIES);
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		List<SovereigntyStructuresResponse> structures = getSovereigntyApiOpen(apiClient).getSovereigntyStructures(DATASOURCE, USER_AGENT, null);
		Map<Integer, SovereigntyStructuresResponse> map = new HashMap<Integer, SovereigntyStructuresResponse>();
		for (SovereigntyStructuresResponse structure : structures) {
			try {
				if (structure.getStructureTypeId() == 32226 //Territorial Claim Unit
						|| structure.getStructureTypeId() == 32458 //Infrastructure Hub
						) {
					continue;
				}
				int stationID = Math.toIntExact(structure.getStructureId());
				map.put(stationID, structure);
			} catch (ArithmeticException ex) {
				//Outpost: No problem
			}
		}
		List<List<Integer>> batches = splitList(map.keySet(), UNIVERSE_BATCH_SIZE);
		Map<List<Integer>, List<UniverseNamesResponse>> responses = updateList(batches, DEFAULT_RETRIES, new ListHandler<List<Integer>, List<UniverseNamesResponse>>() {
			@Override
			public List<UniverseNamesResponse> get(ApiClient apiClient, List<Integer> t) throws ApiException {
				return getUniverseApiOpen(apiClient).postUniverseNames(t, DATASOURCE, USER_AGENT, null);
			}
		});

		List<Citadel> citadels = new ArrayList<>();
		for (Map.Entry<List<Integer>, List<UniverseNamesResponse>> entry : responses.entrySet()) {
			for (UniverseNamesResponse lookup : entry.getValue()) {
				SovereigntyStructuresResponse station = map.get(lookup.getId());
				citadels.add(ApiIdConverter.getCitadel(station, lookup.getName()));
			}
		}
		CitadelGetter.set(citadels);
	}

	@Override
	protected void setNextUpdate(Date date) {
		Settings.get().setConquerableStationsNextUpdate(date);
	}

	@Override
	protected boolean inScope() {
		return true;
	}

}
