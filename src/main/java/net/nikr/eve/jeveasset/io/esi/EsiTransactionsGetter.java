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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterWalletTransactionsResponse;
import net.troja.eve.esi.model.CorporationWalletTransactionsResponse;


public class EsiTransactionsGetter extends AbstractEsiGetter {

	private boolean saveHistory;

	public void load(UpdateTask updateTask, List<EsiOwner> owners, boolean saveHistory) {
		this.saveHistory = saveHistory;
		super.load(updateTask, owners);
	}

	@Override
	protected void get(EsiOwner owner) throws ApiException {
		Set<Long> existing = new HashSet<Long>();
		if (saveHistory) {
			for (MyTransaction transaction : owner.getTransactions()) {
				existing.add(transaction.getTransactionID());
			}
		}
		if (owner.isCorporation()) {
			for (int i = 1; i < 8; i++) { //Division 1-7
				final int division = i;
				List<CorporationWalletTransactionsResponse> responses = updateIDs(owner, existing, new EsiListHandler<CorporationWalletTransactionsResponse>() {
					@Override
					public List<CorporationWalletTransactionsResponse> get(EsiOwner owner, Long fromID) throws ApiException {
						return getWalletApiAuth().getCorporationsCorporationIdWalletsDivisionTransactions((int) owner.getOwnerID(), division, DATASOURCE, fromID, null, null, null);
					}

					@Override
					public Long getID(CorporationWalletTransactionsResponse response) {
						return response.getTransactionId();
					}
				});
				int fixedDivision = division + 999;
				owner.setTransactions(EsiConverter.toTransactionCorporation(responses, owner, fixedDivision, saveHistory));
			}
		} else {
			List<CharacterWalletTransactionsResponse> responses = updateIDs(owner, existing, new EsiListHandler<CharacterWalletTransactionsResponse>() {
				@Override
				public List<CharacterWalletTransactionsResponse> get(EsiOwner owner, Long fromID) throws ApiException {
					return getWalletApiAuth().getCharactersCharacterIdWalletTransactions((int) owner.getOwnerID(), DATASOURCE, fromID, null, null, null);
				}

				@Override
				public Long getID(CharacterWalletTransactionsResponse response) {
					return response.getTransactionId();
				}
			});
			owner.setTransactions(EsiConverter.toTransaction(responses, owner, 1000, saveHistory));
		}
	}

	@Override
	protected String getTaskName() {
		return "Transactions";
	}

	@Override
	protected void setNextUpdate(EsiOwner owner, Date date) {
		owner.setTransactionsNextUpdate(date);
	}

	@Override
	protected Date getNextUpdate(EsiOwner owner) {
		return owner.getTransactionsNextUpdate();
	}

	@Override
	protected boolean inScope(EsiOwner owner) {
		return owner.isTransactions();
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
