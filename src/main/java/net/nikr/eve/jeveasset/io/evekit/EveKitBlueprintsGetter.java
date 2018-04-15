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
package net.nikr.eve.jeveasset.io.evekit;

import enterprises.orbital.evekit.client.ApiClient;
import enterprises.orbital.evekit.client.ApiException;
import enterprises.orbital.evekit.client.model.Blueprint;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;


public class EveKitBlueprintsGetter extends AbstractEveKitGetter implements EveKitPagesHandler<Blueprint> {

	public EveKitBlueprintsGetter(UpdateTask updateTask, EveKitOwner owner, boolean first) {
		super(updateTask, owner, false, owner.getBlueprintsNextUpdate(), TaskType.BLUEPRINTS, first, null);
	}

	public EveKitBlueprintsGetter(UpdateTask updateTask, EveKitOwner owner, Long at) {
		super(updateTask, owner, false, owner.getBlueprintsNextUpdate(), TaskType.BLUEPRINTS, false, at);
	}

	public EveKitBlueprintsGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, false, owner.getBlueprintsNextUpdate(), TaskType.BLUEPRINTS, false, null);
	}

	@Override
	protected void get(ApiClient apiClient, Long at, boolean first) throws ApiException {
		List<Blueprint> data = updatePages(this);
		if (data == null) {
			return;
		}
		Map<Long, RawBlueprint> blueprints = new HashMap<Long, RawBlueprint>();
		for (Blueprint blueprint : data) {
			blueprints.put(blueprint.getItemID(), new RawBlueprint(blueprint));
		}
		owner.setBlueprints(blueprints);
	}

	

	@Override
	public List<Blueprint> get(ApiClient apiClient, String at, Long contid, Integer maxResults) throws ApiException {
		return getCommonApi(apiClient).getBlueprints(owner.getAccessKey(), owner.getAccessCred(), at, contid, maxResults, false,
				null, null, null, null, null, null, null, null);
	}

	@Override
	public long getCID(Blueprint obj) {
		return obj.getCid();
	}

	@Override
	public Long getLifeStart(Blueprint obj) {
		return obj.getLifeStart();
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.BLUEPRINTS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setBlueprintsNextUpdate(date);
	}

	@Override
	public void saveCID(Long cid) { } //Always get all data

	@Override
	public Long loadCID() {
		return null; //Always get all data
	}
}
