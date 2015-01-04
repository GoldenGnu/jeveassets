/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.eveapi;


import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.Blueprint;
import com.beimin.eveapi.response.shared.BlueprintsResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.MyAccount;
import net.nikr.eve.jeveasset.data.MyAccount.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;


public class BlueprintsGetter extends AbstractApiGetter<BlueprintsResponse> {

	public BlueprintsGetter() {
		super("Blueprints", true, false);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<MyAccount> accounts) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected BlueprintsResponse getResponse(final boolean bCorp) throws ApiException {
		if (bCorp) {
			return new com.beimin.eveapi.parser.corporation.BlueprintsParser()
					.getResponse(Owner.getApiAuthorization(getOwner()));
		} else {
			return new com.beimin.eveapi.parser.pilot.BlueprintsParser()
					.getResponse(Owner.getApiAuthorization(getOwner()));
		}
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getOwner().setBlueprintsNextUpdate(nextUpdate);
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getBlueprintsNextUpdate();
	}

	@Override
	protected void setData(final BlueprintsResponse response) {
		List<Blueprint> blueprints = new ArrayList<Blueprint>(response.getAll());
		Map<Long, Blueprint> blueprintsMap = new HashMap<Long, Blueprint>();
		for (Blueprint blueprint : blueprints) {
			blueprintsMap.put(blueprint.getItemID(), blueprint);
		}
		getOwner().setBlueprints(blueprintsMap);
	}

	@Override
	protected void updateFailed(final Owner ownerFrom, final Owner ownerTo) {
		ownerTo.setBlueprints(ownerFrom.getBlueprints());
		ownerTo.setBlueprintsNextUpdate(ownerFrom.getBlueprintsNextUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		return AccessMask.ASSET_LIST.getAccessMask();
	}
}
