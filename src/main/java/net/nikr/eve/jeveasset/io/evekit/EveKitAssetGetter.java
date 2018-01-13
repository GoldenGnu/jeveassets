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
import enterprises.orbital.evekit.client.model.Asset;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;


public class EveKitAssetGetter extends AbstractEveKitGetter implements EveKitPagesHandler<Asset> {

	public EveKitAssetGetter(UpdateTask updateTask, EveKitOwner owner, boolean first) {
		super(updateTask, owner, false, owner.getAssetNextUpdate(), TaskType.ASSETS, first, null);
	}

	public EveKitAssetGetter(UpdateTask updateTask, EveKitOwner owner, Long at) {
		super(updateTask, owner, false, owner.getAssetNextUpdate(), TaskType.ASSETS, false, at);
	}

	public EveKitAssetGetter(UpdateTask updateTask, EveKitOwner owner) {
		super(updateTask, owner, false, owner.getAssetNextUpdate(), TaskType.ASSETS, false, null);
	}

	@Override
	protected void get(ApiClient apiClient, Long at, boolean first) throws ApiException {
		List<Asset> data = updatePages(this);
		if (data == null) {
			return;
		}
		Date assetLastUpdate = null;
		for (Asset asset : data) {
			if (assetLastUpdate == null || assetLastUpdate.getTime() < asset.getLifeStart()) { //Newer
				assetLastUpdate = new Date(asset.getLifeStart());
			}
		}
		owner.setAssetLastUpdate(assetLastUpdate);
		owner.setAssets(EveKitConverter.toAssets(data, owner));
	}

	@Override
	public List<Asset> get(ApiClient apiClient, String at, Long contid, Integer maxResults) throws ApiException {
		return getCommonApi(apiClient).getAssets(owner.getAccessKey(), owner.getAccessCred(), at, contid, maxResults, false,
				null, null, null, null, null, null, null, null);
	}

	@Override
	public long getCID(Asset obj) {
		return obj.getCid();
	}

	@Override
	public Long getLifeStart(Asset obj) {
		return obj.getLifeStart();
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.ASSET_LIST.getAccessMask();
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setAssetNextUpdate(date);
	}

	@Override
	public void saveCID(Long cid) { } //Always get all data

	@Override
	public Long loadCID() {
		return null; //Always get all data
	}
}
