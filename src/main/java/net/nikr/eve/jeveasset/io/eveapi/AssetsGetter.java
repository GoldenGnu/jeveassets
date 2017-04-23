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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.parser.character.CharAssetListParser;
import com.beimin.eveapi.parser.corporation.CorpAssetListParser;
import com.beimin.eveapi.response.shared.AssetListResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccount;
import net.nikr.eve.jeveasset.data.eveapi.EveApiOwner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccessMask;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class AssetsGetter extends AbstractApiGetter<AssetListResponse> {

	public AssetsGetter() {
		super("Assets", true, false);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, List<EveApiAccount> accounts) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected int getProgressStart() {
		return 0;
	}

	@Override
	protected int getProgressEnd() {
		return 80;
	}

	@Override
	protected AssetListResponse getResponse(final boolean bCorp) throws ApiException {
		if (bCorp) {
			return new CorpAssetListParser()
					.getResponse(EveApiOwner.getApiAuthorization(getOwner()), true);
		} else {
			return new CharAssetListParser()
					.getResponse(EveApiOwner.getApiAuthorization(getOwner()), true);
		}
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getAssetNextUpdate();
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getOwner().setAssetNextUpdate(nextUpdate);
		getOwner().setAssetLastUpdate(Settings.getNow());
	}

	@Override
	protected void setData(final AssetListResponse response) {
		List<Asset> flatAssets = new ArrayList<Asset>(response.getAll()); // Get new asset from the flat list
		Map<Long, Asset> lookupAssets = new HashMap<Long, Asset>();
		for (Asset asset : flatAssets) { //Create Lookup table
			lookupAssets.put(asset.getItemID(), asset);
		}
		List<Asset> treeAssets = new ArrayList<Asset>();
		for (Asset asset : flatAssets) { //Make Tree
			if (//Ignore:
					asset.getFlag() != 7 //Skill
					&& asset.getFlag() != 61 //Skill In Training
					&& asset.getFlag() != 89 //Implant
					) {
				Asset parentAsset = lookupAssets.get(asset.getLocationID());
				if (parentAsset != null) {
					asset.setLocationID(0L);
					parentAsset.add(asset);
				} else {
					treeAssets.add(asset);
				}
			}
		}
		getOwner().setAssets(ApiConverter.convertAsset(treeAssets, getOwner()));  //Convert and add MyAssets
	}

	@Override
	protected void updateFailed(final EveApiOwner ownerFrom, final EveApiOwner ownerTo) {
		ownerTo.setAssets(ownerFrom.getAssets());
		ownerTo.setAssetNextUpdate(ownerFrom.getAssetNextUpdate());
		ownerTo.setAssetLastUpdate(ownerFrom.getAssetLastUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		return EveApiAccessMask.ASSET_LIST.getAccessMask();
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
