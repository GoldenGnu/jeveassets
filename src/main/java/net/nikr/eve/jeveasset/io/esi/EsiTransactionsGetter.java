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
import net.troja.eve.esi.model.CharacterWalletTransactionsResponse;


public class EsiTransactionsGetter extends AbstractEsiGetter {

	@Override
	public void load(UpdateTask updateTask, List<EsiOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected void get(EsiOwner owner) throws ApiException {
		Long fromId = null;
		List<CharacterWalletTransactionsResponse> responses = getWalletApiAuth().getCharactersCharacterIdWalletTransactions((int) owner.getOwnerID(), DATASOURCE, fromId, null, null, null);
		owner.setTransactions(EsiConverter.toTransaction(responses, owner, 1000));
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

}
