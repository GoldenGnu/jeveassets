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
package net.nikr.eve.jeveasset.io.esi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DEFAULT_RETRIES;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterBookmarksResponse;
import net.troja.eve.esi.model.CorporationBookmarksResponse;


public class EsiBookmarksGetter extends AbstractEsiGetter {

	public EsiBookmarksGetter(UpdateTask updateTask, EsiOwner esiOwner) {
		super(updateTask, esiOwner, false, esiOwner.getBookmarksNextUpdate(), TaskType.BOOKMARKS, NO_RETRIES);
	}

	

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		if (owner.isCorporation()) {
			List<CorporationBookmarksResponse> bookmarks = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationBookmarksResponse>() {
				@Override
				public List<CorporationBookmarksResponse> get(ApiClient apiClient, Integer page) throws ApiException {
					return getBookmarksApiAuth(apiClient).getCorporationsCorporationIdBookmarks((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
				
			});
			List<Citadel> citadels = new ArrayList<Citadel>();
			for (CorporationBookmarksResponse bookmark : bookmarks) {
				Citadel citadel = ApiIdConverter.getCitadel(bookmark);
				if (citadel != null) {
					citadels.add(citadel);
				}
			}
			CitadelGetter.set(citadels);
		} else {
			List<CharacterBookmarksResponse> bookmarks = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CharacterBookmarksResponse>() {
				@Override
				public List<CharacterBookmarksResponse> get(ApiClient apiClient, Integer page) throws ApiException {
					return getBookmarksApiAuth(apiClient).getCharactersCharacterIdBookmarks((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			List<Citadel> citadels = new ArrayList<Citadel>();
			for (CharacterBookmarksResponse bookmark : bookmarks) {
				Citadel citadel = ApiIdConverter.getCitadel(bookmark);
				if (citadel != null) {
					citadels.add(citadel);
				}
			}
			CitadelGetter.set(citadels);
		}
	}

	@Override
	protected boolean inScope() {
		return owner.isBookmarks();
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setBookmarksNextUpdate(date);
	}
	
}
