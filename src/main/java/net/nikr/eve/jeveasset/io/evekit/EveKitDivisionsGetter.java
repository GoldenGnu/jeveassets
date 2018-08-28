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
import enterprises.orbital.evekit.client.model.Division;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitDivisionsGetter extends AbstractEveKitGetter {

	public EveKitDivisionsGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, false, owner.getAssetNextUpdate(), TaskType.DIVISIONS, false, null);
	}

	@Override
	protected void get(ApiClient apiClient, Long at, boolean first) throws ApiException {
		List<Division> response = getCorporationApi(apiClient).getDivisions(owner.getAccessKey(), owner.getAccessCred(), atFilter(at), null, null, false,
				null, null, null);
		owner.setAssetDivisions(EveKitConverter.toAssetDivisions(response));
		owner.setWalletDivisions(EveKitConverter.toWalletDivisions(response));
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.DIVISIONS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(Date date) {
		//Nope
	}
	
}
