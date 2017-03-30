/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
import enterprises.orbital.evekit.client.model.Asset;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitAssetGetter extends AbstractEveKitListGetter<Asset> {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected List<Asset> get(EveKitOwner owner, Long contid) throws ApiException {
		return getCommonApi().getAssets(owner.getAccessKey(), owner.getAccessCred(), null, contid, MAX_RESULTS, REVERSE,
				null, null, null, null, null, null, null, null);
	}

	@Override
	protected void set(EveKitOwner owner, List<Asset> data) throws ApiException {
		Date assetLastUpdate = null;
		for (Asset asset : data) {
			if (assetLastUpdate == null || assetLastUpdate.getTime() < asset.getLifeStart()) { //Newer
				assetLastUpdate = new Date(asset.getLifeStart());
			}
		}
		owner.setAssets(EveKitConverter.convertAssets(data, owner));
		owner.setAssetLastUpdate(assetLastUpdate);
	}

	@Override
	protected long getCid(Asset obj) {
		return obj.getCid();
	}

	@Override
	protected boolean isNow(Asset obj) {
		return obj.getLifeEnd() == Long.MAX_VALUE;
	}

	@Override
	protected boolean isValid(Asset obj) {
		return obj.getFlag() != 7 //Skill
				&& obj.getFlag() != 61 //Skill In Training
				&& obj.getFlag() != 89; //Implant;
	}

	@Override
	protected String getTaskName() {
		return "Assets";
	}

	@Override
	protected int getProgressEnd() {
		return 80;
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.ASSET_LIST.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		owner.setAssetNextUpdate(date);
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

	@Override
	protected void saveCid(EveKitOwner owner, Long contid) { } //Always get all data

	@Override
	protected Long loadCid(EveKitOwner owner) {
		return null; //Always get all data
	}
}
