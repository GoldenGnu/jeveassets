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
package net.nikr.eve.jeveasset.io.esi;

import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CorporationAssetsResponse;

public class EsiAssetsGetter extends AbstractEsiGetter {

	@Override
	public void load(UpdateTask updateTask, List<EsiOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected void get(EsiOwner owner) throws ApiException {
		if (owner.isCorporation()) {
			List<CorporationAssetsResponse> responses = updatePages(owner, new EsiPagesHandler<CorporationAssetsResponse>() {
				@Override
				public List<CorporationAssetsResponse> get(EsiOwner owner, Integer page) throws ApiException {
					return getAssetsApiAuth().getCorporationsCorporationIdAssets((int) owner.getOwnerID(), DATASOURCE, page, null, null, null);
				}
			});
			owner.setAssets(EsiConverter.toAssetsCorporation(responses, owner));
		} else {
			List<CharacterAssetsResponse> responses = updatePages(owner, new EsiPagesHandler<CharacterAssetsResponse>() {
				@Override
				public List<CharacterAssetsResponse> get(EsiOwner owner, Integer page) throws ApiException {
					return getAssetsApiAuth().getCharactersCharacterIdAssets((int) owner.getOwnerID(), DATASOURCE, page, null, null, null);
				}
			});
			owner.setAssets(EsiConverter.toAssets(responses, owner));
		}
	}

	@Override
	protected void setNextUpdate(EsiOwner owner, Date date) {
		owner.setAssetNextUpdate(date);
		owner.setAssetLastUpdate(Settings.getNow());
	}

	@Override
	protected Date getNextUpdate(EsiOwner owner) {
		return owner.getAssetNextUpdate();
	}

	@Override
	protected boolean inScope(EsiOwner owner) {
		return owner.isAssetList();
	}

	@Override
	protected boolean enabled(EsiOwner owner) {
		if (owner.isCorporation()) {
			return EsiScopes.CORPORATION_ASSETS.isEnabled();
		} else {
			return EsiScopes.CHARACTER_ASSETS.isEnabled();
		}
	}

	@Override
	protected String getTaskName() {
		return "Assets";
	}
}
