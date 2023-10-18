/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.nikr.eve.jeveasset.data.api.raw.RawPublicMarketOrder;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.tabs.orders.OutbidProcesser;
import net.nikr.eve.jeveasset.gui.tabs.orders.OutbidProcesser.OutbidProcesserInput;
import net.nikr.eve.jeveasset.gui.tabs.orders.OutbidProcesser.OutbidProcesserOutput;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DEFAULT_RETRIES;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.getMarketApiOpen;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.api.MarketApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.MarketOrdersResponse;
import net.troja.eve.esi.model.MarketStructuresResponse;
import net.troja.eve.esi.model.StructureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EsiPublicMarketOrdersGetter extends AbstractEsiGetter {

	private static final Logger LOG = LoggerFactory.getLogger(EsiPublicMarketOrdersGetter.class);
	private static final Long OFFSET = 1000L * 3L; // 3 seconds

	private final UpdateTask updateTask;
	private final OutbidProcesserInput input;
	private final OutbidProcesserOutput output;
	private boolean publicMarketOrders = false;
	private boolean modified = false;
	private Date nextUpdate = null;
	private Date lastUpdate;

	public EsiPublicMarketOrdersGetter(UpdateTask updateTask, OutbidProcesserInput input, OutbidProcesserOutput output) {
		super(updateTask, null, false, Settings.get().getPublicMarketOrdersNextUpdate(), TaskType.PUBLIC_MARKET_ORDERS);
		this.updateTask = updateTask;
		this.input = input;
		this.output = output;
	}

	@Override
	protected void update() throws ApiException {
		AtomicInteger count = new AtomicInteger(0);
		//Update public market orders
		publicMarketOrders = true;
		List<MarketOrdersResponse> responses = updatePagedList(input.getRegionIDs(), new PagedListHandler<Integer, MarketOrdersResponse>() {
			@Override
			protected List<MarketOrdersResponse> get(Integer k) throws ApiException {
				try {
					return updatePages(DEFAULT_RETRIES, new EsiPagesHandler<MarketOrdersResponse>() {
						@Override
						public ApiResponse<List<MarketOrdersResponse>> get(Integer page) throws ApiException {
							ApiResponse<List<MarketOrdersResponse>> response = getMarketApiOpen().getMarketsRegionIdOrdersWithHttpInfo("all", k, DATASOURCE, null, page, null);
							String header = getHeader(response.getHeaders(), "last-modified");
							if (header != null) {
								Date date = Formatter.parseExpireDate(header);
								if (lastUpdate == null) {
									lastUpdate = date;
								} else if (!modified && !lastUpdate.equals(date)){
									modified = true;
								}
							}
							return response;
						}
					});
				} finally {
					setProgressAll(input.getRegionIDs().size(), count.incrementAndGet(), 0, 40);
				}
			}
		});
		if (modified) {
			addWarning("last-modified changed while updating", "Cache expired while updating");
		}
		publicMarketOrders = false;
		Map<Integer, Set<RawPublicMarketOrder>> orders = EsiConverter.toPublicMarketOrders(responses);
		for (MarketOrdersResponse ordersResponse : responses) {
			//Find leaking market structures
			if (ordersResponse.getLocationId() > 100000000) {
				input.getStructureIDs().add(ordersResponse.getLocationId());
			}
			//Map known locationID <=> systemID
			input.getLocationToSystem().put(ordersResponse.getLocationId(), RawConverter.toLong(ordersResponse.getSystemId()));
		}
		//Get public structures
		input.getStructureIDs().addAll(update(DEFAULT_RETRIES, new EsiHandler<List<Long>>() {
			@Override
			public ApiResponse<List<Long>> get() throws ApiException {
				return getUniverseApiOpen().getUniverseStructuresWithHttpInfo(DATASOURCE, "market", null);
			}
		}));
		//Update orders in structures
		count.set(0);
		MarketApi marketApi = input.getMarketApi();
		if (marketApi != null) {
			List<MarketStructuresResponse> structuresResponses = updatePagedList(input.getStructureIDs(), new PagedListHandler<Long, MarketStructuresResponse>() {
				@Override
				protected List<MarketStructuresResponse> get(Long k) throws ApiException {
					try {
						return updatePages(DEFAULT_RETRIES, new EsiPagesHandler<MarketStructuresResponse>() {
							@Override
							public ApiResponse<List<MarketStructuresResponse>> get(Integer page) throws ApiException {
								return marketApi.getMarketsStructuresStructureIdWithHttpInfo(k, DATASOURCE, null, page, null);
							}
						});
					} catch (ApiException ex) {
						if (ex.getCode() == 403 && ex.getResponseBody().toLowerCase().contains("market access denied")) {
							LOG.warn(ex.getMessage() + ":\n" + ex.getResponseBody());
							return null;
						} else {
							throw ex;
						}
					} finally {
						setProgressAll(input.getStructureIDs().size(), count.incrementAndGet(), 40, 90);
					}
				}
			});
			for (MarketStructuresResponse response : structuresResponses) {
				RawPublicMarketOrder marketOrder = new RawPublicMarketOrder(response, getSystemID(input, response.getLocationId()));
				Set<RawPublicMarketOrder> set = orders.get(marketOrder.getTypeID());
				if (set == null) {
					set = new HashSet<>();
					orders.put(marketOrder.getTypeID(), set);
				}
				set.add(marketOrder);
			}
		} else {
			addError("NO ENOUGH ACCESS PRIVILEGES", "No character with market orders structure scope found\r\n(Add scope: [Options] > [Acounts...] > [Edit])");
		}
		input.addOrders(orders, lastUpdate);
		//Process data
		OutbidProcesser.process(input, output);
		if (output.hasUnknownLocations()) {
			updateTask.addWarning(TaskType.PUBLIC_MARKET_ORDERS.getTaskName(), "Market orders in unknown locations ignored");
		}
		if (lastUpdate != null) {
			Settings.lock("Public Orders (last update)");
			Settings.get().setPublicMarketOrdersLastUpdate(lastUpdate);
			Settings.unlock("Public Orders (last update)");
		}
		setProgressAll(100, 100, 90, 100);
	}

	private void setProgressAll(final float progressEnd, final float progressNow, final int minimum, final int maximum) {
		if (updateTask != null) {
			updateTask.setTaskProgress(progressEnd, progressNow, minimum, maximum);
			updateTask.setTotalProgress(progressEnd, progressNow, minimum, maximum);
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		if (publicMarketOrders && (nextUpdate == null || nextUpdate.before(date))) {
			nextUpdate = date;
			Date fixedDate = new Date(date.getTime() + OFFSET);
			Settings.lock("Public Orders (next update)");
			Settings.get().setPublicMarketOrdersNextUpdate(fixedDate);
			Settings.unlock("Public Orders (next update)");
		}
	}

	@Override
	protected boolean haveAccess() {
		return true; //Public
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		return null;
	}

	private Long getSystemID(OutbidProcesser.OutbidProcesserInput data, long locationID) {
		Long systemID = data.getLocationToSystem().get(locationID);
		if (systemID != null) {
			return systemID;
		}
		MyLocation location = ApiIdConverter.getLocation(locationID);
		if (!location.isEmpty()) {
			return location.getSystemID();
		}
		Citadel citadel = data.getCitadels().get(locationID);
		if (citadel != null) {
			return citadel.getSystemID();
		}
		UniverseApi structuresApi = data.getStructuresApi();
		if (structuresApi != null) {
			try {
				StructureResponse response = update(DEFAULT_RETRIES, new AbstractEsiGetter.EsiHandler<StructureResponse>() {
					@Override
					public ApiResponse<StructureResponse> get() throws ApiException {
						return structuresApi.getUniverseStructuresStructureIdWithHttpInfo(locationID, DATASOURCE, null, null);
					}
				});
				data.getCitadels().put(locationID, ApiIdConverter.getCitadel(response, locationID));
				return RawConverter.toLong(response.getSolarSystemId());
			} catch (ApiException ex) {
				handleHeaders(ex);
				LOG.error(ex.getMessage(), ex);
			}
		}
		LOG.warn("Unknown market location");
		return null;
	}

}
