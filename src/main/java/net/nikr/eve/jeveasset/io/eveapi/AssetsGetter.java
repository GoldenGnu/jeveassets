/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

import com.beimin.eveapi.core.ApiException;
import com.beimin.eveapi.shared.assetlist.ApiAsset;
import com.beimin.eveapi.shared.assetlist.AssetListResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class AssetsGetter extends AbstractApiGetter<AssetListResponse> {

	private Settings settings;

	public AssetsGetter() {
		super("Assets", 2, true, false);
	}

	public void load(UpdateTask updateTask, Settings settings) {
		this.settings = settings;
		super.load(updateTask, settings.isForceUpdate(), settings.getAccounts());
	}

	@Override
	protected AssetListResponse getResponse(boolean bCorp) throws ApiException {
		if (bCorp){
			return com.beimin.eveapi.corporation
					.assetlist.AssetListParser.getInstance()
					.getResponse(Human.getApiAuthorization(getHuman()));
		} else {
			return com.beimin.eveapi.character
					.assetlist.AssetListParser.getInstance()
					.getResponse(Human.getApiAuthorization(getHuman()));
		}
	}

	@Override
	protected Date getNextUpdate() {
		return getHuman().getAssetNextUpdate();
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		getHuman().setAssetNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(AssetListResponse response) {
		List<ApiAsset> apiAssets = new ArrayList<ApiAsset>(response.getAssets());
		List<EveAsset> assets = ApiConverter.apiAsset(getHuman(), apiAssets, settings);
		getHuman().setAssets(assets);
	}
	
	@Override
	protected void setData(Human human){
		getHuman().setAssets(human.getAssets());
	}

	@Override
	protected void clearData(){
		getHuman().setAssets(new ArrayList<EveAsset>());
	}
}
