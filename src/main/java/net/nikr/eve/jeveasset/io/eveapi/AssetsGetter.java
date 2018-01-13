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
package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.parser.character.CharAssetListParser;
import com.beimin.eveapi.parser.corporation.CorpAssetListParser;
import com.beimin.eveapi.response.shared.AssetListResponse;
import java.util.Date;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class AssetsGetter extends AbstractApiGetter<AssetListResponse> {

	public AssetsGetter(UpdateTask updateTask, EveApiOwner owner) {
		super(updateTask, owner, false, owner.getAssetNextUpdate(), TaskType.ASSETS);
	}

	@Override
	protected void get(String updaterStatus) throws ApiException {
		AssetListResponse response;
		if (owner.isCorporation()) {
			response = new CorpAssetListParser()
					.getResponse(EveApiOwner.getApiAuthorization(owner), true);
		} else {
			response = new CharAssetListParser()
					.getResponse(EveApiOwner.getApiAuthorization(owner), true);
		}
		if (!handle(response, updaterStatus)) {
			return;
		}
		owner.setAssets(EveApiConverter.toAssets(response.getAll(), owner));;
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		owner.setAssetNextUpdate(nextUpdate);
		owner.setAssetLastUpdate(Settings.getNow());
	}

	@Override
	protected long requestMask() {
		return EveApiAccessMask.ASSET_LIST.getAccessMask();
	}
}
