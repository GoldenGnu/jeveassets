/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderRange;
import net.nikr.eve.jeveasset.data.api.raw.RawPublicMarketOrder;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DEFAULT_RETRIES;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.getMarketApiOpen;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.MarketOrdersResponse;
import net.troja.eve.esi.model.MarketStructuresResponse;
import net.troja.eve.esi.model.StructureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EsiPublicMarketOrdersGetter extends AbstractEsiGetter {

	private static final Logger LOG = LoggerFactory.getLogger(EsiPublicMarketOrdersGetter.class);

	private final ProfileData profileData;
	private final SellOrderRange sellOrderRange;
	private final UpdateTask updateTask;
	private boolean first = true;
	
	public EsiPublicMarketOrdersGetter(UpdateTask updateTask, ProfileData profileData, SellOrderRange sellOrderRange) {
		super(updateTask, null, false, Settings.get().getPublicMarketOrdersNextUpdate(), TaskType.PUBLIC_MARKET_ORDERS);
		this.updateTask = updateTask;
		this.profileData = profileData;
		this.sellOrderRange = sellOrderRange;
	}

	@Override
	protected void update() throws ApiException {
		Set<Long> structureIDs = new HashSet<>();
		Set<Integer> regionIDs = new HashSet<>();
		Map<Integer, List<MyMarketOrder>> typeIDs = new HashMap<>();
		for (OwnerType ownerType : profileData.getOwners().values()) {
			for (MyMarketOrder marketOrder : ownerType.getMarketOrders()) {
				if (marketOrder.isActive()) {
					//StructuresIDs
					if (marketOrder.getLocationID() > 100000000) {
						structureIDs.add(marketOrder.getLocationID());
					}
					//TypeIDs
					List<MyMarketOrder> list = typeIDs.get(marketOrder.getTypeID());
					if (list == null) {
						list = new ArrayList<>();
						typeIDs.put(marketOrder.getTypeID(), list);
					}
					list.add(marketOrder);
					//RegionIDs
					Integer regionID = RawConverter.toInteger(marketOrder.getLocation().getRegionID());
					if (regionID >= 10000000 && regionID <= 13000000) {
						regionIDs.add(regionID);
					}
				}
			}
		}
		Data data = new Data(typeIDs);
		AtomicInteger count = new AtomicInteger(0);
		List<RawPublicMarketOrder> orders = new ArrayList<>();
		//Update public market orders
		List<MarketOrdersResponse> responses = updatePagedList(regionIDs, new PagedListHandler<Integer, MarketOrdersResponse>() {
			@Override
			protected List<MarketOrdersResponse> get(Integer k) throws ApiException {
				try {
					return updatePages(DEFAULT_RETRIES, new EsiPagesHandler<MarketOrdersResponse>() {
						@Override
						public ApiResponse<List<MarketOrdersResponse>> get(Integer page) throws ApiException {
							return getMarketApiOpen().getMarketsRegionIdOrdersWithHttpInfo("all", k, DATASOURCE, null, page, null);
						}
					});
				} finally {
					setProgressAll(regionIDs.size(), count.incrementAndGet(), 0, 40);
				}
			}
		});
		orders.addAll(EsiConverter.toPublicMarketOrders(responses));
		for (MarketOrdersResponse ordersResponse : responses) {
			//Find leaking market structures
			if (ordersResponse.getLocationId() > 100000000) {
				structureIDs.add(ordersResponse.getLocationId());
			}
			//Map known locationID <=> systemID
			data.getLocationToSystem().put(ordersResponse.getLocationId(), RawConverter.toLong(ordersResponse.getSystemId()));
		}
		//Get public structures
		structureIDs.addAll(update(DEFAULT_RETRIES, new EsiHandler<List<Long>>() {
			@Override
			public ApiResponse<List<Long>> get() throws ApiException {
				return getUniverseApiOpen().getUniverseStructuresWithHttpInfo(DATASOURCE, "market", null);
			}
		}));
		//Update orders in structures
		count.set(0);
		boolean updated = false;
		for (EsiOwner esiOwner : profileData.getProfileManager().getEsiOwners()) {
			if (esiOwner.isMarketStructures()) {
				List<MarketStructuresResponse> structuresResponses = updatePagedList(structureIDs, new PagedListHandler<Long, MarketStructuresResponse>() {
					@Override
					protected List<MarketStructuresResponse> get(Long k) throws ApiException {
						try {
							return updatePages(DEFAULT_RETRIES, new EsiPagesHandler<MarketStructuresResponse>() {
								@Override
								public ApiResponse<List<MarketStructuresResponse>> get(Integer page) throws ApiException {
									return esiOwner.getMarketApiAuth().getMarketsStructuresStructureIdWithHttpInfo(k, DATASOURCE, null, page, null);
								}
							});
						} catch (ApiException ex) {
							if (ex.getCode() == 403 && ex.getResponseBody().toLowerCase().contains("market access denied")) {
								System.out.println(ex.getResponseBody() + "|" + ex.getMessage());
								return null;
							} else {
								throw ex;
							}
						} finally {
							setProgressAll(structureIDs.size(), count.incrementAndGet(), 40, 80);
						}
					}
				});
				orders.addAll(EsiConverter.toPublicMarketOrdersStructures(structuresResponses));
				updated = true;
				break;
			}
		}
		if (!updated) {
			addError(null, "NO ENOUGH ACCESS PRIVILEGES", "No character with market orders structure scope found\r\n(Add scope: [Options] > [Acounts...] > [Edit])");
		}
		//Process data
		process(data, orders);
		setProgressAll(100, 90, 0, 100);
		CitadelGetter.set(data.getCitadels().values());
		setProgressAll(100, 93, 0, 100);
		profileData.setMarketOrdersUpdates(data.getUpdates());
		setProgressAll(100, 96, 0, 100);
		Settings.get().setMarketOrdersUnderbid(data.getUnderbids());
		setProgressAll(100, 100, 0, 100);
	}

	private void setProgressAll(final float progressEnd, final float progressNow, final int minimum, final int maximum) {
		if (updateTask != null) {
			updateTask.setTaskProgress(progressEnd, progressNow, minimum, maximum);
			updateTask.setTotalProgress(progressEnd, progressNow, minimum, maximum);
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		if (first) {
			first = false;
			Settings.get().setPublicMarketOrdersNextUpdate(date);
		}
	}

	@Override
	protected boolean haveAccess() {
		return true; //Public
	}

	private void process(Data data, List<RawPublicMarketOrder> publicMarketOrders) {
		//Process the orders
		for (RawPublicMarketOrder ordersResponse : publicMarketOrders) {
			List<MyMarketOrder> orders = data.getTypeIDs().get(ordersResponse.getTypeId());
			if (orders != null) {
				//Orders to match
				for (MyMarketOrder marketOrder : orders) {
					if (isSameOrder(marketOrder, ordersResponse)) { //Orders to be updated
						data.getUpdates().put(ordersResponse.getOrderId(), ordersResponse);
						continue;
					}
					if (!isSameType(marketOrder, ordersResponse)) { //Both buy or both sell
						continue;
					}
					if (!isInRange(data, marketOrder, ordersResponse)) { //Order range overlap
						continue;
					}
					Underbid underbid = data.getUnderbids().get(marketOrder.getOrderID());
					if (underbid == null) {
						underbid = new Underbid(ordersResponse);
						data.getUnderbids().put(marketOrder.getOrderID(), underbid);
					}
					if (marketOrder.isBuyOrder()) { //Buy (underbid is higher)
						underbid.setPrice(Math.max(underbid.getPrice(), ordersResponse.getPrice()));
						if (ordersResponse.getPrice() > marketOrder.getPrice()) {
							underbid.addCount(ordersResponse.getVolumeRemain());
						}
					} else { //Sell  (underbid is lower)
						underbid.setPrice(Math.min(underbid.getPrice(), ordersResponse.getPrice()));
						if (marketOrder.getPrice() > ordersResponse.getPrice()) {
							underbid.addCount(ordersResponse.getVolumeRemain());
						}
					}

				}
			}
		}
	}

	private boolean isInRange(Data data, MyMarketOrder marketOrder, RawPublicMarketOrder other) {
		Long fromSystemID = getSystemID(data, marketOrder.getLocationID());
		Long toSystemID = getSystemID(data, other.getLocationId());
		MyLocation fromSystemLocation = ApiIdConverter.getLocation(fromSystemID);
		MyLocation toSystemLocation = ApiIdConverter.getLocation(toSystemID);
		if (fromSystemLocation.isEmpty() || toSystemLocation.isEmpty()) {
			LOG.warn("Unknown market location ignored");
			return false; //We can't work with unknown locations
		}
		if (!Objects.equals(fromSystemLocation.getRegionID(), toSystemLocation.getRegionID())) {
			return false; //Must be in same region
		}
		if (marketOrder.isBuyOrder()) {
			if (marketOrder.getRange() == MarketOrderRange.REGION
					|| other.getRange() == MarketOrderRange.REGION) {
				return true; //Match everything
			} else if (marketOrder.getRange() == MarketOrderRange.STATION
					&& other.getRange() == MarketOrderRange.STATION) {
				return Objects.equals(marketOrder.getLocationID(), other.getLocationId()); //Only match if in the same station
			} else {
				int range = getRange(other.getRange()) + getRange(marketOrder.getRange()); //Find overlapping area
				//int range = Math.max(getRange(response), getRange(marketOrder)); //Use the order with the max range
				Integer distance = profileData.distanceBetween(fromSystemID, toSystemID);
				if (distance == null) {
					return false;
				}
				return distance <= range;
			}
		} else {//Sell order
			switch (sellOrderRange) {
				case REGION:
					return true; //Match everything
				case SYSTEM:
					return Objects.equals(fromSystemID, toSystemID); //Only match if in the same system
				case STATION:
					return Objects.equals(marketOrder.getLocation().getStationID(), other.getLocationId()); //Only match if in the same station
				default:
					return false;
			}
		}
	}

	private Long getSystemID(Data data, long locationID) {
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
			return citadel.systemId;
		}
		for (EsiOwner esiOwner : profileData.getProfileManager().getEsiOwners()) {
			if (esiOwner.isStructures()) {
				try {
					StructureResponse response = update(DEFAULT_RETRIES, new EsiHandler<StructureResponse>() {
						@Override
						public ApiResponse<StructureResponse> get() throws ApiException {
							return esiOwner.getUniverseApiAuth().getUniverseStructuresStructureIdWithHttpInfo(locationID, DATASOURCE, null, null);
						}
					});
					data.getCitadels().put(locationID, ApiIdConverter.getCitadel(response, locationID));
					return RawConverter.toLong(response.getSolarSystemId());
				} catch (ApiException ex) {
					handleHeaders(ex);
					LOG.error(ex.getMessage(), ex);
					break; //Only try one time
				}
			}
		}
		LOG.warn("Unknown market location");
		return null;
	}

	private int getRange(MarketOrderRange range) {
		switch (range) {
			case REGION: return Integer.MAX_VALUE;
			case SOLARSYSTEM: return 0;
			case STATION: return 0;
			case _1: return 1;
			case _2: return 2;
			case _3: return 3;
			case _4: return 4;
			case _5: return 5;
			case _10: return 10;
			case _20: return 20;
			case _30: return 30;
			case _40: return 40;
		}
		return Integer.MAX_VALUE;
	}

	private boolean isSameType(MyMarketOrder marketOrder, RawPublicMarketOrder response) {
		return Objects.equals(marketOrder.isBuyOrder(), response.getIsBuyOrder());
	}

	private boolean isSameOrder(MyMarketOrder marketOrder, RawPublicMarketOrder response) {
		return Objects.equals(marketOrder.getOrderID(), response.getOrderId());
	}

	public static class Underbid {
		private Double price;
		private long count;

		public Underbid(Double price, long count) {
			this.price = price;
			this.count = count;
		}

		public Underbid(RawPublicMarketOrder ordersResponse) {
			this.price = ordersResponse.getPrice();
			this.count = 0;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public long getCount() {
			return count;
		}

		public void addCount(long count) {
			this.count = this.count + count;
		}
	}

	public static enum SellOrderRange {
		REGION("Region"),
		SYSTEM("System"),
		STATION("Station");

		private final String text;
		
		private SellOrderRange(String text) {		
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	private static class Data {
		private final Map<Long, Underbid> underbids = new HashMap<>();
		private final Map<Long, RawPublicMarketOrder> updates = new HashMap<>();
		private final Map<Long, Long> locationToSystem = new HashMap<>();
		private final Map<Long, Citadel> citadels = new HashMap<>();
		private final Map<Integer, List<MyMarketOrder>> typeIDs;

		public Data(Map<Integer, List<MyMarketOrder>> typeIDs) {
			this.typeIDs = typeIDs;
		}

		public Map<Long, Underbid> getUnderbids() {
			return underbids;
		}

		public Map<Long, RawPublicMarketOrder> getUpdates() {
			return updates;
		}

		public Map<Long, Long> getLocationToSystem() {
			return locationToSystem;
		}

		public Map<Long, Citadel> getCitadels() {
			return citadels;
		}

		public Map<Integer, List<MyMarketOrder>> getTypeIDs() {
			return typeIDs;
		}
	}

}
