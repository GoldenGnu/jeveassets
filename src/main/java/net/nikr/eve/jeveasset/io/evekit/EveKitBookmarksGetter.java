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
import enterprises.orbital.evekit.client.ApiResponse;
import enterprises.orbital.evekit.client.model.Bookmark;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter.EveKitPagesHandler;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class EveKitBookmarksGetter extends AbstractEveKitGetter implements EveKitPagesHandler<Bookmark> {

	public EveKitBookmarksGetter(UpdateTask updateTask, EveKitOwner eveKitOwner) {
		super(updateTask, eveKitOwner, false, eveKitOwner.getBookmarksNextUpdate(), TaskType.BOOKMARKS, false, null);
	}

	@Override
	protected void update(Long at, boolean first) throws ApiException {
		List<Bookmark> data = updatePages(this);
		if (data == null) {
			return;
		}
		List<Citadel> citadels = new ArrayList<Citadel>();
		for (Bookmark bookmark : data) {
			Citadel citadel = ApiIdConverter.getCitadel(bookmark);
			if (citadel != null) {
				citadels.add(citadel);
			}
		}
		CitadelGetter.set(citadels);
	}

	@Override
	protected long getAccessMask() {
		return EveKitAccessMask.BOOKMARKS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setBookmarksNextUpdate(date);
	}

	@Override
	public ApiResponse<List<Bookmark>> get(String at, Long cid, Integer maxResults) throws ApiException {
		return getCommonApi().getBookmarksWithHttpInfo(owner.getAccessKey(), owner.getAccessCred(), at, cid, maxResults, false,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Override
	public long getCID(Bookmark k) {
		return k.getCid();
	}

	@Override
	public Long getLifeStart(Bookmark obj) {
		return obj.getLifeStart();
	}

	@Override
	public void saveCID(Long cid) { } //Always get all data

	@Override
	public Long loadCID() {
		return null; //Always get all data
	}
	
}
