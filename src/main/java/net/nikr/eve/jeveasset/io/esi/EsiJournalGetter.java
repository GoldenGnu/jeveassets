/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;


public class EsiJournalGetter extends AbstractEsiGetter {

	private final boolean saveHistory;

	public EsiJournalGetter(UpdateTask updateTask, EsiOwner owner, boolean saveHistory) {
		super(updateTask, owner, false, owner.getJournalNextUpdate(), TaskType.JOURNAL);
		this.saveHistory = saveHistory;
	}

	@Override
	protected void update() throws ApiException {
		if (owner.isCorporation()) {
			for (int i = 1; i < 8; i++) { //Division 1-7
				final int division = i;
				List<CorporationWalletJournalResponse> journals = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CorporationWalletJournalResponse>() {
					@Override
					public ApiResponse<List<CorporationWalletJournalResponse>> get(Integer page) throws ApiException {
						return getWalletApiAuth().getCorporationsCorporationIdWalletsDivisionJournalWithHttpInfo((int) owner.getOwnerID(), division, DATASOURCE, null, page, null);
					}
				});
				int fixedDivision = division + 999;
				owner.setJournal(EsiConverter.toJournalsCorporation(journals, owner, fixedDivision, saveHistory));
			}
		} else {
			List<CharacterWalletJournalResponse> journals = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<CharacterWalletJournalResponse>() {
				@Override
				public ApiResponse<List<CharacterWalletJournalResponse>> get(Integer page) throws ApiException {
					return getWalletApiAuth().getCharactersCharacterIdWalletJournalWithHttpInfo((int) owner.getOwnerID(), DATASOURCE, null, page, null);
				}
			});
			owner.setJournal(EsiConverter.toJournals(journals, owner, 1000, saveHistory));
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setJournalNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return owner.isJournal();
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		RolesEnum[] roles = {RolesEnum.DIRECTOR, RolesEnum.ACCOUNTANT, RolesEnum.JUNIOR_ACCOUNTANT};
		return roles;
	}

}
