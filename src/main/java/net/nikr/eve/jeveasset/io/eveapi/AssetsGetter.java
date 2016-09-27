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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.response.shared.AssetListResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.MyAccount;
import net.nikr.eve.jeveasset.data.MyAccount.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class AssetsGetter extends AbstractApiGetter<AssetListResponse> {

	private boolean flat;

	public AssetsGetter() {
		super("Assets", true, false);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, List<MyAccount> accounts) {
		flat = false;
		super.loadAccounts(updateTask, forceUpdate, accounts);
		flat = true;
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected int getProgressStart() {
		if (!flat) {
			return 0;
		} else {
			return 40;
		}
	}

	@Override
	protected int getProgressEnd() {
		if (!flat) {
			return 40;
		} else {
			return 80;
		}
	}

	@Override
	protected AssetListResponse getResponse(final boolean bCorp) throws ApiException {
		if (bCorp) {
			return new com.beimin.eveapi.parser.corporation.AssetListParser()
					.getResponse(Owner.getApiAuthorization(getOwner()), flat);
		} else {
			return new com.beimin.eveapi.parser.pilot.PilotAssetListParser()
					.getResponse(Owner.getApiAuthorization(getOwner()), flat);
		}
	}

	@Override
	protected Date getNextUpdate() {
		if (flat) {
			return new Date();
		} else {
			return getOwner().getAssetNextUpdate();
		}
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getOwner().setAssetNextUpdate(nextUpdate);
		getOwner().setAssetLastUpdate(Settings.getNow());
	}

	@Override
	protected void setData(final AssetListResponse response) {
		if (!flat) {
			List<Asset> eveAssets = new ArrayList<Asset>(response.getAll());
			List<MyAsset> assets = ApiConverter.convertAsset(eveAssets, getOwner());
			getOwner().setAssets(assets);
		} else {
			Set<Long> itemIDs = new HashSet<Long>(); //Hold current all itemIDs
			deepAssets(getOwner().getAssets(), itemIDs); //Get all current itemIDs
			List<Asset> assets = new ArrayList<Asset>(response.getAll()); // Get new asset from the flat list
			List<Asset> assetsInclude = new ArrayList<Asset>(); // Get new asset from the flat list
			for (Asset asset : assets) { //Find new assets
				if (!itemIDs.contains(asset.getItemID()) && 
						//Ignore:
						asset.getFlag() != 7 //Skill
						&& asset.getFlag() != 61 //Skill In Training
						&& asset.getFlag() != 89 //Implant
						) {
					assetsInclude.add(asset);
					
				}
			}
			getOwner().getAssets().addAll(ApiConverter.convertAsset(assetsInclude, getOwner()));  //Convert and add MyAssets
		}
	}

	@Override
	protected void updateFailed(final Owner ownerFrom, final Owner ownerTo) {
		ownerTo.setAssets(ownerFrom.getAssets());
		ownerTo.setAssetNextUpdate(ownerFrom.getAssetNextUpdate());
		ownerTo.setAssetLastUpdate(ownerFrom.getAssetLastUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		return AccessMask.ASSET_LIST.getAccessMask();
	}

	private void deepAssets(List<MyAsset> assets, Set<Long> itemIDs) {
		for (MyAsset myAsset : assets) {
			itemIDs.add(myAsset.getItemID());
			if (!myAsset.getAssets().isEmpty()) {
				deepAssets(myAsset.getAssets(), itemIDs);
			}
		}
	}
}
