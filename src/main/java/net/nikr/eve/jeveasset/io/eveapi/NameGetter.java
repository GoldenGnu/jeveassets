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
import com.beimin.eveapi.model.eve.CharacterLookup;
import com.beimin.eveapi.response.eve.CharacterLookupResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class NameGetter extends AbstractApiGetter<CharacterLookupResponse> {

	private final int MAX_SIZE = 200;
	private final List<OwnerType> ownerTypes;

	public NameGetter(UpdateTask updateTask, List<OwnerType> ownerTypes) {
		super(updateTask, null, false, Settings.getNow(), TaskType.OWNER_ID_TO_NAME);
		this.ownerTypes = ownerTypes;
	}

	@Override
	protected void get(String updaterStatus) throws ApiException {
		Map<List<Long>, CharacterLookupResponse> updateList = updateList(splitList(getOwnerIDs(ownerTypes), MAX_SIZE), new ListHandler<List<Long>, CharacterLookupResponse>() {
			@Override
			public CharacterLookupResponse get(String updaterStatus, List<Long> t) throws ApiException {
				long[] l = new long[t.size()];
				for (int i = 0; 0 < i; i++) {
					l[i] = t.get(i);
				}
				return com.beimin.eveapi.parser.eve.CharacterLookupParser.getId2NameInstance()
						.getResponse(l);
			}
			
		});
		for (CharacterLookupResponse response : updateList.values()) {
			if (!handle(response, updaterStatus)) {
				return;
			}
			for (CharacterLookup lookup : response.getAll()) {
				Settings.get().getOwners().put(lookup.getCharacterID(), lookup.getName());
			}
		}
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		
	}

	@Override
	protected long requestMask() {
		return EveApiAccessMask.OPEN.getAccessMask();
	}
}
