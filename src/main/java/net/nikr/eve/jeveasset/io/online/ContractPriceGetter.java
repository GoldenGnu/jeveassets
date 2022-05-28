/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.io.shared.ThreadWoker;
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
	private final boolean all;

	public ContractPriceGetter(final UpdateTask updateTask, final ProfileData profileData, boolean all) {
		super(updateTask, null, false, ContractPriceManager.get().getNextUpdate(), TaskType.CONTRACT_PRICES, "Contracts Appraisal");
		this.profileData = profileData;
		this.all = all;
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
		final ContractPriceManager priceManager = ContractPriceManager.get();
		for (ContractPriceItem contractPriceType : profileData.getContractPricesTypes()) {
			if (!priceManager.isFailed(contractPriceType) && (all || !priceManager.haveContractPrice(contractPriceType))) {
				updates.add(new Update(contractPriceSettings, contractPriceType, all));
			}
		}
		try {
			List<Future<ReturnData>> futures = startSubThreads(updates, true);
			int done = 0;
			int failed = 0;
			for (Future<ReturnData> future : futures) {
				if (future.isDone()) {
					try {
						ReturnData returnValue = future.get(); //Get data
						if (returnValue != null) {
							priceManager.addPrices(returnValue);
							if (returnValue.isEmpty()) {
								done++;
							} else {
								failed++;
							}
						}
					} catch (ExecutionException ex) {
						ThreadWoker.throwExecutionException(ApiException.class, ex);
					}
				}
			}
			priceManager.save();
			LOG.info(done + " contract prices updated (" + failed + " empty/failed)");
		} catch (ApiException ex) {
			logWarn(ex.getResponseBody(), ex.getMessage());
			addError(ex.getCode(), "Error Code: " + ex.getCode() + "\r\n" + ex.getResponseBody(), ex);
		} catch (TaskCancelledException ex) {
			logInfo(null, "Cancelled");
		} catch (InterruptedException ex) {
			addError(ex.getMessage(), ex.getMessage(), ex);
		} catch (Exception ex) {
			addError(ex.getMessage(), "Unknown Error: " + ex.getMessage(), ex);
		}
	}

	private static class Update implements Callable<ReturnData> {

		private final ContractPriceSettings contractPriceSettings;
		private final ContractPriceItem contractPriceType;
		private final boolean all;
		private int retry = 0;

		public Update(ContractPriceSettings contractPriceSettings, ContractPriceItem contractPriceType, boolean all) {
			this.contractPriceSettings = contractPriceSettings;
			this.contractPriceType = contractPriceType;
			this.all = all;
		}

		@Override
		public ReturnData call() throws Exception {
			return update();
		}

		private ReturnData update() throws ApiException {
			try {
				ApiResponse<Prices> apiResponse = API.getPricesWithHttpInfo(contractPriceType.getTypeID(), contractPriceSettings.isIncludePrivate(), contractPriceType.isBpc(), contractPriceSettings.getSecurityValues(), contractPriceType.getMe(), contractPriceType.getTe());
				if (apiResponse.getStatusCode() == 204) {
					return new ReturnData(contractPriceType, getHeaderExpires(apiResponse.getHeaders()));
				} else {
					return new ReturnData(contractPriceType, getHeaderExpires(apiResponse.getHeaders()), apiResponse.getData(), all);
				}
			} catch (ApiException ex) {
				if (ex.getCode() == 404) {
					LOG.info(contractPriceType.getTypeID() + " not found", ex);
					return new ReturnData(contractPriceType, getHeaderExpires(ex.getResponseHeaders()));
				} else {
					retry++;
					if (retry < RETRIES) {
						LOG.warn("Retrying " + contractPriceType.getTypeID() + ": " + retry + " of " + RETRIES);
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
