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
import com.beimin.eveapi.model.shared.ContractItem;
import com.beimin.eveapi.model.shared.ContractType;
import com.beimin.eveapi.parser.character.CharContractItemsParser;
import com.beimin.eveapi.parser.corporation.CorpContractItemsParser;
import com.beimin.eveapi.response.shared.ContractItemsResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccount;
import net.nikr.eve.jeveasset.data.eveapi.EveApiOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class ContractItemsGetter extends AbstractApiGetter<ContractItemsResponse> {

	private MyContract currentContract;
	private Map<Long, List<ContractItem>> savedItems = new HashMap<Long, List<ContractItem>>();

	public ContractItemsGetter() {
		super("Contracts", false, false);
	}

	//FIXME - - > Move to overwrite load (See: JournalGetter)
	public void load(UpdateTask updateTask, boolean forceUpdate, List<EveApiAccount> accounts) {
		//Calc size
		int size = 0;
		for (EveApiAccount account : accounts) {
			for (EveApiOwner owner : account.getOwners()) {
				size = size + owner.getContracts().size();
			}
		}
		int count = 0;
		for (EveApiAccount account : accounts) {
			for (EveApiOwner owner : account.getOwners()) {
				for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
					MyContract contract = entry.getKey();
					if (updateTask != null && updateTask.isCancelled()) {
						return; //We are done here...
					}
					count++; //Also count COURIER
					if (contract.getType() == ContractType.COURIER) {
						continue; //Ignore courier
					}
					if (!entry.getValue().isEmpty()) {
							continue; //Ignore existing
 					}
					///XXX - Workaround for alien contracts
					if ((owner.getOwnerID() != contract.getAcceptorID()
							&& owner.getOwnerID() != contract.getAssigneeID()
							&& owner.getOwnerID() != contract.getIssuerID())
							&& owner.getOwnerID() != contract.getIssuerCorpID()
							) {
						continue; //Ignore not owned
					}
					if ((owner.getOwnerID() != contract.getAcceptorID()
							&& owner.getOwnerID() != contract.getAssigneeID()
							&& owner.getOwnerID() != contract.getIssuerID())
							&& !contract.isForCorp()
							) {
						continue; //Only IssuerCorpID match and is not for corp
					}
					List<ContractItem> items = savedItems.get(contract.getContractID());
					if (items != null) { //Set already updated
						owner.getContracts().put(contract, ApiConverter.convertContractItems(items, contract));
						continue; //Ignore already updated
					}
					this.setTaskName("Contract Item ("+contract.getContractID()+")");
					currentContract = contract;
					super.loadOwner(updateTask, forceUpdate, owner);
					if (updateTask != null) {
						updateTask.setTaskProgress(size, count, getProgressStart(), getProgressEnd());
					}
				}
			}
		}
	}

	@Override
	protected int getProgressStart() {
		return 30;
	}

	@Override
	protected int getProgressEnd() {
		return 90;
	}

	@Override
	protected ContractItemsResponse getResponse(boolean bCorp) throws ApiException {
		if (bCorp) {
			return new CorpContractItemsParser()
					.getResponse(EveApiOwner.getApiAuthorization(getOwner()), currentContract.getContractID());
		} else {
			return new CharContractItemsParser()
					.getResponse(EveApiOwner.getApiAuthorization(getOwner()), currentContract.getContractID());
		}
	}

	@Override
	protected Date getNextUpdate() {
		return new Date();
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		//Do nothing...
	}

	@Override
	protected void setData(ContractItemsResponse response) {
		List<ContractItem> contractItems = response.getAll();
		getOwner().getContracts().put(currentContract, ApiConverter.convertContractItems(contractItems, currentContract));
		savedItems.put(currentContract.getContractID(), contractItems);
	}

	@Override
	protected void updateFailed(EveApiOwner ownerFrom, EveApiOwner ownerTo) {
		//Never called
	}

	@Override
	protected long requestMask(boolean bCorp) {
		if (bCorp) {
			return EveApiAccessMask.CONTRACTS_CORP.getAccessMask();
		} else {
			return EveApiAccessMask.CONTRACTS_CHAR.getAccessMask();
		}
	}
	
}
