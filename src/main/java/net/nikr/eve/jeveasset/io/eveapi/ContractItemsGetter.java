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
package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.parser.character.CharContractItemsParser;
import com.beimin.eveapi.parser.corporation.CorpContractItemsParser;
import com.beimin.eveapi.response.shared.ContractItemsResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;

public class ContractItemsGetter extends AbstractApiGetter<ContractItemsResponse> {

	public ContractItemsGetter(UpdateTask updateTask, EveApiOwner owner) {
		super(updateTask, owner, false, Settings.getNow(), TaskType.CONTRACT_ITEMS);
	}

	@Override
	protected void get(String updaterStatus) throws ApiException {
		List<MyContract> contracts = new ArrayList<MyContract>();
		for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
			MyContract contract = entry.getKey();
			if (contract.isIgnoreContract()) {
				continue; //Ignore courier
			}
			if (!entry.getValue().isEmpty()) {
				continue; //Ignore existing
			}
			///XXX - Workaround for alien contracts
			if ((owner.getOwnerID() != contract.getAcceptorID()
					&& owner.getOwnerID() != contract.getAssigneeID()
					&& owner.getOwnerID() != contract.getIssuerID())
					&& owner.getOwnerID() != contract.getIssuerCorpID()) {
				continue; //Ignore not owned
			}
			if ((owner.getOwnerID() != contract.getAcceptorID()
					&& owner.getOwnerID() != contract.getAssigneeID()
					&& owner.getOwnerID() != contract.getIssuerID())
					&& !contract.isForCorp()) {
				continue; //Only IssuerCorpID match and is not for corp
			}
			if (entry.getValue() != null && !entry.getValue().isEmpty()) { //Set already updated
				continue; //Ignore already updated
			}
			contracts.add(contract);
		}
		Map<MyContract, ContractItemsResponse> updateList = updateList(contracts, NO_RETRIES, new ListHandler<MyContract, ContractItemsResponse>() {
			@Override
			public ContractItemsResponse get(String updaterStatus, MyContract t) throws ApiException {
				if (owner.isCorporation()) {
					return new CorpContractItemsParser()
							.getResponse(EveApiOwner.getApiAuthorization(owner), t.getContractID());
				} else {
					return new CharContractItemsParser()
							.getResponse(EveApiOwner.getApiAuthorization(owner), t.getContractID());
				}
			}
		});
		for (Map.Entry<MyContract, ContractItemsResponse> entry : updateList.entrySet()) {
			if (!handle(entry.getValue(), updaterStatus)) {
				continue;
			}
			owner.setContracts(EveApiConverter.toContractItems(entry.getKey(), entry.getValue().getAll(), owner));
		}
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		//Do nothing...
	}

	@Override
	protected long requestMask() {
		if (owner.isCorporation()) {
			return EveApiAccessMask.CONTRACTS_CORP.getAccessMask();
		} else {
			return EveApiAccessMask.CONTRACTS_CHAR.getAccessMask();
		}
	}

}
