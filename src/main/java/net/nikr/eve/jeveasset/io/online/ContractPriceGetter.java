/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.online;

import net.nikr.eve.jeveasset.data.settings.ContractPriceManager;
import eve.nikr.net.client.ApiException;
import eve.nikr.net.client.ApiResponse;
import eve.nikr.net.client.api.PricesApi;
import eve.nikr.net.client.model.Prices;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceSettings;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceItem;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ReturnData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker.TaskCancelledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractPriceGetter extends AbstractGetter<EsiOwner> {

	private static final Logger LOG = LoggerFactory.getLogger(ContractPriceGetter.class);
	private static final int RETRIES = 3;
	
	private static final PricesApi API = new PricesApi();
	static {
		API.getApiClient().setUserAgent(System.getProperty("http.agent"));
	}

	private final ProfileData profileData;
	
	public ContractPriceGetter(final UpdateTask updateTask, final ProfileData profileData) {
		super(updateTask, null, false, ContractPriceManager.get().getNextUpdate(), TaskType.CONTRACT_PRICES, "Contracts Appraisal");
		this.profileData = profileData;
	}

	@Override
	protected void setNextUpdate(Date date) { }

	@Override
	protected boolean haveAccess() {
		return true;
	}

	@Override
	public void run() {
		update(new HashSet<>());
	}
	
	public void update(Set<Integer> exclude) {
		List<Update> updates = new ArrayList<>();
		final ContractPriceSettings contractPriceSettings = Settings.get().getContractPriceSettings();
		for (ContractPriceItem contractPriceType : profileData.getContractPricesTypes()) {
			updates.add(new Update(contractPriceSettings, contractPriceType));
		}
		ContractPriceManager priceManager = ContractPriceManager.get();
		try {
			List<Future<ReturnData>> futures = startSubThreads(updates, true);
			for (Future<ReturnData> future : futures) {
				if (future.isDone()) {
					ReturnData returnValue = future.get(); //Get data from ESI
					if (returnValue != null) {
						priceManager.addPrices(returnValue);
					}
				}
			}
			priceManager.save();
			LOG.info("Contract prices updated");
		} catch (ExecutionException ex) {
			addError(null, ex.getMessage(), ex.getMessage(), ex);
		} catch (InterruptedException ex) {
			addError(null, ex.getMessage(), ex.getMessage(), ex);
		} catch (TaskCancelledException ex) {
			logInfo(null, "Cancelled");
		} catch (Throwable ex) {
			addError(null, ex.getMessage(), "Unknown Error: " + ex.getMessage(), ex);
		}
	}

	private static class Update implements Callable<ReturnData> {

		private final ContractPriceSettings contractPriceSettings;
		private final ContractPriceItem contractPriceType;
		private int retry = 0;

		public Update(ContractPriceSettings contractPriceSettings, ContractPriceItem contractPriceType) {
			this.contractPriceSettings = contractPriceSettings;
			this.contractPriceType = contractPriceType;
		}

		@Override
		public ReturnData call() throws Exception {
			return update();
		}

		private ReturnData update() throws ApiException {
			try {
				ApiResponse<Prices> apiResponse = API.getPricesWithHttpInfo(contractPriceType.getTypeID(), contractPriceSettings.isIncludePrivate(), contractPriceType.isBpc(), contractPriceSettings.getSecurityValues(), contractPriceType.getMe(), contractPriceType.getTe());
				if (apiResponse.getStatusCode() == 204) {
					return null;
				} else {
					return new ReturnData(contractPriceType, apiResponse.getData(), getHeaderExpires(apiResponse.getHeaders()));
				}
			} catch (ApiException ex) {
				if (ex.getCode() == 404) {
					LOG.info(contractPriceType.getTypeID() + " not found", ex);
					return null;
				} else {
					retry++;
					if (retry < RETRIES) {
						LOG.warn("Retrying " + contractPriceType.getTypeID() + ": " + retry + " of " +  RETRIES);
						return update();
					} else {
						LOG.error("Failed to get " + contractPriceType.getTypeID() + ": " + ex.getCode() + " " + ex.getResponseBody(), ex);
						throw ex;
					}
				}
			}
		}
	}
}
