/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.FactionWarfareSystemsResponse;
import net.troja.eve.esi.model.FactionsResponse;


public class EsiFactionWarfareGetter extends AbstractEsiGetter {

	public EsiFactionWarfareGetter(UpdateTask updateTask) {
		super(updateTask, null, false, Settings.get().getFactionWarfareNextUpdate(), TaskType.FACTION_WARFARE);
	}

	@Override
	protected void update() throws ApiException {
		List<FactionWarfareSystemsResponse> factionWarfareSystems = update(DEFAULT_RETRIES, new EsiHandler<List<FactionWarfareSystemsResponse>>() {
			@Override
			public ApiResponse<List<FactionWarfareSystemsResponse>> get() throws ApiException {
				return getFactionWarfareApiOpen().getFwSystemsWithHttpInfo(DATASOURCE, null);
			}
		});
		List<FactionsResponse> factions = update(DEFAULT_RETRIES, new EsiHandler<List<FactionsResponse>>() {
			@Override
			public ApiResponse<List<FactionsResponse>> get() throws ApiException {
				return getUniverseApiOpen().getUniverseFactionsWithHttpInfo(null, DATASOURCE, null, null);
			}
		});
		Map<Integer, String> factionNames = new HashMap<>();
		for (FactionsResponse faction : factions) {
			factionNames.put(faction.getFactionId(), faction.getName());
		}
		Map<Long, String> systemOwners = new HashMap<>();
		for (FactionWarfareSystemsResponse system : factionWarfareSystems) {
			systemOwners.put((long) system.getSolarSystemId(), factionNames.get(system.getOccupierFactionId()));
		}
		Settings.get().setFactionWarfareSystemOwners(systemOwners);
	}

	@Override
	protected void setNextUpdate(Date date) {
		Settings.get().setFactionWarfareNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return true;
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		return null;
	}

}
