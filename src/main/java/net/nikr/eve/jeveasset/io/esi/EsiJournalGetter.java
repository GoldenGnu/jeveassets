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
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;


public class EsiJournalGetter extends AbstractEsiGetter {

	private boolean saveHistory;

	public void load(UpdateTask updateTask, List<EsiOwner> owners, boolean saveHistory) {
		this.saveHistory = saveHistory;
		super.load(updateTask, owners);
	}

	@Override
	protected void get(EsiOwner owner) throws ApiException {
		if (owner.isCorporation()) {
			for (int division = 1; division < 8; division++) {
				Long fromID = null;
				List<CorporationWalletJournalResponse> journals = getWalletApiAuth().getCorporationsCorporationIdWalletsDivisionJournal((int) owner.getOwnerID(), division, DATASOURCE, fromID, null, null, null);
				int fixedDivision = division+999;
				owner.setJournal(EsiConverter.toJournalsCorporation(journals, owner, fixedDivision, saveHistory));
			}
		} else {
			Long fromID = null;
			List<CharacterWalletJournalResponse> journals = getWalletApiAuth().getCharactersCharacterIdWalletJournal((int) owner.getOwnerID(), DATASOURCE, fromID, null, null, null);
			owner.setJournal(EsiConverter.toJournals(journals, owner, 1000, saveHistory));
		}
	}

	@Override
	protected String getTaskName() {
		return "Journals";
	}

	@Override
	protected void setNextUpdate(EsiOwner owner, Date date) {
		owner.setJournalNextUpdate(date);
	}

	@Override
	protected Date getNextUpdate(EsiOwner owner) {
		return owner.getJournalNextUpdate();
	}

	@Override
	protected boolean inScope(EsiOwner owner) {
		return owner.isJournal();
	}

	@Override
	protected boolean enabled(EsiOwner owner) {
		if (owner.isCorporation()) {
			return EsiScopes.CORPORATION_WALLET.isEnabled();
		} else {
			return EsiScopes.CHARACTER_WALLET.isEnabled();
		}
	}

}
