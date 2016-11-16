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

import java.util.Date;
import java.util.List;

import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.Asset;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitAssetGetter extends AbstractEveKitGetter {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected void get(final EveKitOwner owner) throws ApiException {
	  // Return current assets.  Paging required for large asset lists.
	  List<Asset> assets = retrievePagedResults(new BatchRetriever<Asset>() {

      @Override
      public List<Asset> getNextBatch(
                                      long contid) throws ApiException {
        return getCommonApi().getAssets(owner.getAccessKey(), owner.getAccessCred(), null, contid, Integer.MAX_VALUE, null,
                                        null, null, null, null, null, null, null, null);
      }

      @Override
      public long getCid(
                         Asset obj) {
        return obj.getCid();
      }
	  
	  });
	  
		Date assetLastUpdate = null;
		for (Asset asset : assets) {
			if (assetLastUpdate == null || assetLastUpdate.getTime() < asset.getLifeStart()) { //Newer
				assetLastUpdate = new Date(asset.getLifeStart());
			}
		}
		owner.setAssets(EveKitConverter.convertAssets(assets, owner));
		owner.setAssetLastUpdate(assetLastUpdate);
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

}
