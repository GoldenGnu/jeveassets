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
import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.response.shared.AssetListResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.MyAccount;
import net.nikr.eve.jeveasset.data.MyAccount.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class AssetsGetter extends AbstractApiGetter<AssetListResponse> {

	public AssetsGetter() {
		super("Assets", true, false);
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, List<MyAccount> accounts) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
	}

	@Override
	protected int getProgressEnd() {
		return 80;
	}

	@Override
	protected AssetListResponse getResponse(final boolean bCorp) throws ApiException {
		if (bCorp) {
			return new com.beimin.eveapi.parser.corporation.AssetListParser()
					.getResponse(Owner.getApiAuthorization(getOwner()));
		} else {
			return new com.beimin.eveapi.parser.pilot.AssetListParser()
					.getResponse(Owner.getApiAuthorization(getOwner()));
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
		List<Asset<?>> eveAssets = new ArrayList<Asset<?>>(response.getAll());
		List<MyAsset> assets = ApiConverter.convertAsset(eveAssets, getOwner());
		getOwner().setAssets(assets);
	}

	@Override
	protected void updateFailed(final Owner ownerFrom, final Owner ownerTo) {
		ownerTo.setAssets(ownerFrom.getAssets());
		ownerTo.setAssetNextUpdate(ownerFrom.getAssetNextUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		return AccessMask.ASSET_LIST.getAccessMask();
	}
}
