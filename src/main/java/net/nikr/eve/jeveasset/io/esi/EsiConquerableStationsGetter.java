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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.SovereigntyStructuresResponse;
import net.troja.eve.esi.model.UniverseNamesResponse;


public class EsiConquerableStationsGetter extends AbstractEsiGetter {

	private UpdateTask updateTask;

	@Override
	public void load(UpdateTask updateTask) {
		this.updateTask = updateTask;
		super.load(updateTask);
	}

	@Override
	protected void get(EsiOwner owner) throws ApiException {
		List<SovereigntyStructuresResponse> structures = getSovereigntyApiOpen().getSovereigntyStructures(DATASOURCE, System.getProperty("http.agent"), "");
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
		List<Citadel> citadels = new ArrayList<>();
		List<List<Integer>> batches = splitList(map.keySet(), UNIVERSE_BATCH_SIZE);
		int progress = 0;
		for (List<Integer> batch : batches) {
			List<UniverseNamesResponse> stations = getUniverseApiOpen().postUniverseNames(batch, DATASOURCE, System.getProperty("http.agent"), null);
			for (UniverseNamesResponse lookup : stations) {
				SovereigntyStructuresResponse station = map.get(lookup.getId());
				citadels.add(ApiIdConverter.getCitadel(station, lookup.getName()));
			}
			progress++;
			updateTask.setTaskProgress(batches.size(), progress, 0, 100);
		}
		CitadelGetter.set(citadels);
	}

	@Override
	protected String getTaskName() {
		return "Conquerable Stations";
	}

	@Override
	protected void setNextUpdate(EsiOwner owner, Date date) {
		Settings.get().setConquerableStationsNextUpdate(date);
	}

	@Override
	protected Date getNextUpdate(EsiOwner owner) {
		return Settings.get().getConquerableStationsNextUpdate();
	}

	@Override
	protected boolean inScope(EsiOwner owner) {
		return true;
	}

	@Override
	protected boolean enabled(EsiOwner owner) {
		return EsiScopes.CONQUERABLE_STATIONS.isEnabled();
	}

}
