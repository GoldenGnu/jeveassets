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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.esi.EsiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterContractsItemsResponse;

public class EsiContractItemsGetter extends AbstractEsiGetter {

	@Override
	public void load(UpdateTask updateTask, List<EsiOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected void get(EsiOwner owner) throws ApiException {
		List<MyContract> contracts = new ArrayList<MyContract>();
		for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
			if (entry.getKey().isCourier()) {
				continue;
			}
			if (entry.getValue() != null && !entry.getValue().isEmpty()) { //Not null and not empty
				continue;
			}
			contracts.add(entry.getKey());

		}
		for (MyContract contract : contracts) {
			List<CharacterContractsItemsResponse> responses = getContractsApiAuth().getCharactersCharacterIdContractsContractIdItems((int) owner.getOwnerID(), contract.getContractID(), DATASOURCE, null, null, null);
			owner.setContracts(EsiConverter.toContractItems(contract, responses, owner));
		}
	}

	@Override
	protected int getProgressStart() {
		return 50;
	}

	@Override
	protected int getProgressEnd() {
		return 100;
	}

	@Override
	protected String getTaskName() {
		return "Contract Items";
	}

	@Override
	protected void setNextUpdate(EsiOwner owner, Date date) {
		//We will never update again...
	}

	@Override
	protected Date getNextUpdate(EsiOwner owner) {
		return Settings.getNow();
	}

	@Override
	protected boolean inScope(EsiOwner owner) {
		return owner.isContracts();
	}

}
