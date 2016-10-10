/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.evekit;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.ContractItem;
import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class EveKitContractItemsGetter extends AbstractEveKitGetter {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected void get(EveKitOwner owner) throws ApiException {
	  // In an ideal world, we'd only retrieve contract items based on the contracts we retrieved in the ContractsGetter.
	  // We can't do that as written, so we'll do something slightly evil: we'll retrieve contract items live at the current
	  // time in reverse order, and stop when we find a "lifeStart" past our threshold.  We set our threshold based on the
	  // maximum length of a contract, which is 2 weeks, and we double it to provide some buffer.
	  long threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(28);
		List<ContractItem> contractItems = new ArrayList<>();
		// Start paging since there may be many items before the threshold
		List<ContractItem> batch = getCommonApi().getContractItems(owner.getAccessKey(), owner.getAccessCred(), null, Long.MAX_VALUE, Integer.MAX_VALUE, 
		                                                           true, null, null, null, null, null, null, null);
		while (!batch.isEmpty()) {
		  contractItems.addAll(batch);
		  if (batch.get(0).getLifeStart() < threshold)
		    // This contract item was created before our threshold, so stop retrieval
		    break;
		  batch = getCommonApi().getContractItems(owner.getAccessKey(), owner.getAccessCred(), null, batch.get(0).getCid(), Integer.MAX_VALUE, 
		                                          true, null, null, null, null, null, null, null);
		}
		EveKitConverter.convertContractItems(owner.getContracts(), contractItems);
	}

	@Override
	protected String getTaskName() {
		return "Contract Items";
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
	protected long getAccessMask() {
		return EveKitAccessMask.CONTRACTS.getAccessMask();
	}

	@Override
	protected void setNextUpdate(EveKitOwner owner, Date date) {
		owner.setContractsNextUpdate(date);
	}

	@Override
	protected ApiClient getApiClient() {
		return getCommonApi().getApiClient();
	}

}
