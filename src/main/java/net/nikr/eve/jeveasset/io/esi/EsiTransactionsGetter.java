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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterWalletTransactionsResponse;
import net.troja.eve.esi.model.CorporationWalletTransactionsResponse;


public class EsiTransactionsGetter extends AbstractEsiGetter {

	private final boolean saveHistory;

	public EsiTransactionsGetter(UpdateTask updateTask, EsiOwner owner, boolean saveHistory) {
		super(updateTask, owner, false, owner.getTransactionsNextUpdate(), TaskType.TRANSACTIONS, NO_RETRIES);
		this.saveHistory = saveHistory;
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		Set<Long> existing = new HashSet<Long>();
		if (saveHistory) {
			for (MyTransaction transaction : owner.getTransactions()) {
				existing.add(transaction.getTransactionID());
			}
		}
		if (owner.isCorporation()) {
			for (int i = 1; i < 8; i++) { //Division 1-7
				final int division = i;
				List<CorporationWalletTransactionsResponse> responses = updateIDs(existing, DEFAULT_RETRIES, new IDsHandler<CorporationWalletTransactionsResponse>() {
					@Override
					public List<CorporationWalletTransactionsResponse> get(ApiClient apiClient, Long fromID) throws ApiException {
						return getWalletApiAuth(apiClient).getCorporationsCorporationIdWalletsDivisionTransactions((int) owner.getOwnerID(), division, DATASOURCE, fromID, null, USER_AGENT, null);
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
			List<CharacterWalletTransactionsResponse> responses = updateIDs(existing, DEFAULT_RETRIES, new IDsHandler<CharacterWalletTransactionsResponse>() {
				@Override
				public List<CharacterWalletTransactionsResponse> get(ApiClient apiClient, Long fromID) throws ApiException {
					return getWalletApiAuth(apiClient).getCharactersCharacterIdWalletTransactions((int) owner.getOwnerID(), DATASOURCE, fromID, null, USER_AGENT, null);
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
	protected void setNextUpdate(Date date) {
		owner.setTransactionsNextUpdate(date);
	}

	@Override
	protected boolean inScope() {
		return owner.isTransactions();
	}

}
