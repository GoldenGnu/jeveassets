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


import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import enterprises.orbital.evekit.client.model.Contract;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.nikr.eve.jeveasset.data.evekit.EveKitAccessMask;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;


public class EveKitContractsGetter extends AbstractEveKitGetter {

	@Override
	public void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		super.load(updateTask, owners);
	}

	@Override
	protected void get(final EveKitOwner owner) throws ApiException {
	  // Contracts change state, but are never removed.  So a call to getContracts here will return every
	  // contract ever stored in EveKit because they will all be live at the current time.  To avoid that,
	  // we filter on the "dateIssued" attribute.
	  //
	  // We know that contracts can't have a duration of more than 2 weeks, so we double this duration
	  // to create a query threshold.
	  final long threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(28);
	  // Page to make sure we get all desired results
		List<Contract> contracts = retrievePagedResults(new BatchRetriever<Contract>() {

      @Override
      public List<Contract> getNextBatch(
                                         long contid)
        throws ApiException {
        return getCommonApi().getContracts(owner.getAccessKey(), owner.getAccessCred(), null, contid, Integer.MAX_VALUE, null,
                                           null, null, null, null, null, null, null, null, null, null, null, null, ek_range(threshold, Long.MAX_VALUE), 
                                           null, null, null, null, null, null, null, null, null);
      }

      @Override
      public long getCid(
                         Contract obj) {
        return obj.getCid();
      } 
		    
		});
		owner.setContracts(EveKitConverter.convertContracts(contracts));
		for (Map.Entry<MyContract, List<MyContractItem>> entry : owner.getContracts().entrySet()) {
			System.out.println("MyContract: " + entry.getKey().getTitle());
			for (MyContractItem contractItem : entry.getValue()) {
				System.out.println("	MyContractItem: " + contractItem.getName());
			}
		}
	}

	@Override
	protected String getTaskName() {
		return "Contracts";
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
