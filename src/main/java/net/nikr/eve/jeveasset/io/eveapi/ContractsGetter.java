/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import com.beimin.eveapi.shared.contract.ContractsResponse;
import com.beimin.eveapi.shared.contract.EveContract;
import com.beimin.eveapi.shared.contract.items.EveContractItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Account.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;


public class ContractsGetter extends AbstractApiGetter<ContractsResponse>{

	public ContractsGetter() {
		super("Contracts", true, false);
	}

	@Override
	public void load(UpdateTask updateTask, boolean forceUpdate, List<Account> accounts) {
		super.load(updateTask, forceUpdate, accounts);
	}

	@Override
	protected int getProgressStart() {
		return 0;
	}

	@Override
	protected int getProgressEnd() {
		return 30;
	}

	@Override
	protected ContractsResponse getResponse(boolean bCorp) throws ApiException {
		if (bCorp) {
			return com.beimin.eveapi.corporation.contract.ContractsParser.getInstance().getResponse(Owner.getApiAuthorization(getOwner()));
		} else {
			return com.beimin.eveapi.character.contract.ContractsParser.getInstance().getResponse(Owner.getApiAuthorization(getOwner()));
		}
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getContractsNextUpdate();
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		getOwner().setContractsNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(ContractsResponse response) {
		List<EveContract> contracts = new ArrayList<EveContract>(response.getAll());
		for (EveContract contract : contracts) {
			//Find existing contract
			List<EveContractItem> contractItems = new ArrayList<EveContractItem>();
			for (Map.Entry<EveContract, List<EveContractItem>> entry : getOwner().getContracts().entrySet()) {
				if (entry.getKey().getContractID() == contract.getContractID()) {
					contractItems = entry.getValue();
					break;
				}
			}
			getOwner().getContracts().put(contract, contractItems);
		}
	}

	@Override
	protected void updateFailed(Owner ownerFrom, Owner ownerTo) {
		ownerTo.setContractsNextUpdate(ownerFrom.getContractsNextUpdate());
		for (Map.Entry<EveContract, List<EveContractItem>> entry : ownerFrom.getContracts().entrySet()) {
			ownerTo.setContracts(entry.getKey(), entry.getValue());
		}
	}

	@Override
	protected long requestMask(boolean bCorp) {
		if (bCorp) {
			return AccessMask.CONTRACTS_CORP.getAccessMask();
		} else {
			return AccessMask.CONTRACTS_CHAR.getAccessMask();
		}
	}
	
}
