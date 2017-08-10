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
package net.nikr.eve.jeveasset.io.evekit;

import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.Blueprint;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitBlueprintsGetter extends AbstractEveKitListGetter<Blueprint> {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners, boolean first) {
		super.load(updateTask, owners, first);
	}

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners, Long at) {
		super.load(updateTask, owners, at);
	}

	@Override
	protected List<Blueprint> get(EveKitOwner owner, String at, Long contid) throws ApiException {
		return getCommonApi().getBlueprints(owner.getAccessKey(), owner.getAccessCred(), at, contid, getMaxResults(), getReverse(),
				null, null, null, null, null, null, null, null, null);
	}

	@Override
	protected void set(EveKitOwner owner, List<Blueprint> data) throws ApiException {
		Map<Long, RawBlueprint> blueprints = new HashMap<Long, RawBlueprint>();
		for (Blueprint blueprint : data) {
			blueprints.put(blueprint.getItemID(), new RawBlueprint(blueprint));
		}
		owner.setBlueprints(blueprints);
	}

	@Override
	protected long getCID(Blueprint obj) {
		return obj.getCid();
	}

	@Override
	protected Long getLifeStart(Blueprint obj) {
		return obj.getLifeStart();
	}

	@Override
	protected String getTaskName() {
		return "Blueprints";
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.BLUEPRINTS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		owner.setBlueprintsNextUpdate(date);
	}

	@Override
	protected Date getNextUpdate(EveKitOwner owner) {
		return owner.getBlueprintsNextUpdate();
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

	@Override
	protected void saveCID(EveKitOwner owner, Long cid) { } //Always get all data

	@Override
	protected Long loadCID(EveKitOwner owner) {
		return null; //Always get all data
	}
}
