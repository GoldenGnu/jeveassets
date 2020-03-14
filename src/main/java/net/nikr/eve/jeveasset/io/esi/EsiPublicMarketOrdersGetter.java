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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DEFAULT_RETRIES;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.getMarketApiOpen;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.MarketOrdersResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EsiPublicMarketOrdersGetter extends AbstractEsiGetter {

	private static final Logger LOG = LoggerFactory.getLogger(EsiPublicMarketOrdersGetter.class);

	private final ProfileData profileData;
	private final SellOrderRange sellOrderRange;
	
	public EsiPublicMarketOrdersGetter(UpdateTask updateTask, ProfileData profileData, SellOrderRange sellOrderRange) {
		super(updateTask, null, false, Settings.get().getPublicMarketOrdersNextUpdate(), TaskType.PUBLIC_MARKET_ORDERS);
		this.profileData = profileData;
		this.sellOrderRange = sellOrderRange;
	}

	@Override
	protected void update() throws ApiException {
		List<MyMarketOrder> marketOrders = getMarketOrders();

		Map<Long, Underbid> underbids = new HashMap<>();
		Map<Long, MarketOrdersResponse> updates = new HashMap<>();

		Map<Integer, Map<Integer, List<MyMarketOrder>>> regionIDs = getRegionIDs(marketOrders);
		int count = 0;
		for (Map.Entry<Integer, Map<Integer, List<MyMarketOrder>>> entry : regionIDs.entrySet()) {
			count++;
			List<MarketOrdersResponse> updatePages = updatePages(DEFAULT_RETRIES, new EsiPagesHandler<MarketOrdersResponse>() {
				@Override
				public ApiResponse<List<MarketOrdersResponse>> get(Integer page) throws ApiException {
					return getMarketApiOpen().getMarketsRegionIdOrdersWithHttpInfo("all", entry.getKey(), DATASOURCE, null, page, null);
				}
			});
			for (MarketOrdersResponse ordersResponse : updatePages) {
				List<MyMarketOrder> orders = entry.getValue().get(ordersResponse.getTypeId());
				if (orders != null) {
					//Orders to be updated
					for (MyMarketOrder marketOrder : orders) {
						if (isSameOrder(marketOrder, ordersResponse)) {
							updates.put(ordersResponse.getOrderId(), ordersResponse);
						}
					}
					//Orders to match
					for (MyMarketOrder marketOrder : orders) {
						if (isSameOrder(marketOrder, ordersResponse)) {
							continue;
						}
						if (!isSameType(marketOrder, ordersResponse)) { //Both buy or both sell
							continue;
						}
						if (!isInRange(marketOrder, updates.get(marketOrder.getOrderID()), ordersResponse)) { //Order range overlap
							continue;
						}
						Underbid underbid = underbids.get(marketOrder.getOrderID());
						if (underbid == null) {
							underbid = new Underbid(ordersResponse);
							underbids.put(marketOrder.getOrderID(), underbid);
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
			setProgress(regionIDs.size(), count, 0, 100);
		}
		profileData.setMarketOrdersUpdates(updates);
		Settings.get().setMarketOrdersUnderbid(underbids);
	}

	@Override
	protected void setNextUpdate(Date date) {
		Settings.get().setPublicMarketOrdersNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return true; //Public
	}

	private List<MyMarketOrder> getMarketOrders() {
		List<MyMarketOrder> marketOrders = new ArrayList<>();
		for (OwnerType ownerType : profileData.getOwners().values()) {
			for (MyMarketOrder marketOrder : ownerType.getMarketOrders()) {
				if (marketOrder.isActive()) {
					marketOrders.add(marketOrder);
				}
			}
		}
		return marketOrders;
	}

	private Map<Integer, Map<Integer, List<MyMarketOrder>>> getRegionIDs(List<MyMarketOrder> marketOrders) {
		Map<Integer, Map<Integer, List<MyMarketOrder>>> regionIDs = new HashMap<>();
		for (MyMarketOrder marketOrder : marketOrders) {
			Integer regionID = RawConverter.toInteger(marketOrder.getLocation().getRegionID());
			if (regionID < 10000000 || regionID > 13000000) {
				continue;
			}
			Map<Integer, List<MyMarketOrder>> ordersMap = regionIDs.get(regionID);
			if (ordersMap == null) {
				ordersMap = new HashMap<>();
				regionIDs.put(regionID, ordersMap);
			}
			List<MyMarketOrder> orders = ordersMap.get(marketOrder.getTypeID());
			if (orders == null) {
				orders = new ArrayList<>();
				ordersMap.put(marketOrder.getTypeID(), orders);
			}
			orders.add(marketOrder);
		}
		return regionIDs;
	}

	private boolean isInRange(MyMarketOrder marketOrder, MarketOrdersResponse same, MarketOrdersResponse other) {
		if (marketOrder.isBuyOrder()) {
			if (marketOrder.getRange() == RawMarketOrder.MarketOrderRange.REGION
					|| other.getRange() == MarketOrdersResponse.RangeEnum.REGION) {
				return true; //Match everything
			} else if (marketOrder.getRange() == RawMarketOrder.MarketOrderRange.STATION
					&& other.getRange() == MarketOrdersResponse.RangeEnum.STATION) {
				return Objects.equals(marketOrder.getLocationID(), other.getLocationId()); //Only match if in the same station
			} else {
				int range = getRange(other) + getRange(marketOrder); //Find overlapping area
				long from;
				if (!marketOrder.getLocation().isEmpty()) {
					from = marketOrder.getLocation().getSystemID();
				} else if (same != null) {
					from = same.getSystemId();
				} else {
					LOG.warn("Null location! my: " + marketOrder.getLocation().getSystemID() + " response: " + other.getSystemId() + " same: " + (same != null ? same.getSystemId() : "null"));
					return false;
				}
				Integer distance = profileData.distanceBetween(from, RawConverter.toLong(other.getSystemId()));
				if (distance == null) {
									}
				//int range = Math.max(getRange(response), getRange(marketOrder)); //Use the order with the max range
				return distance <= range;
			}
		} else {//Sell order
			switch (sellOrderRange) {
				case REGION:
					return true; //Match everything
				case SYSTEM:
					return Objects.equals(marketOrder.getLocation().getSystemID(), other.getSystemId()); //Only match if in the same system
				case STATION:
					return Objects.equals(marketOrder.getLocation().getStationID(), other.getLocationId()); //Only match if in the same station
				default:
					return false;
			}
		}
	}

	private int getRange(MarketOrdersResponse response) {
		switch (response.getRange()) {
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

	private int getRange(MyMarketOrder marketOrder) {
		switch (marketOrder.getRange()) {
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

	private boolean isSameType(MyMarketOrder marketOrder, MarketOrdersResponse response) {
		return Objects.equals(marketOrder.isBuyOrder(), response.getIsBuyOrder());
	}

	private boolean isSameOrder(MyMarketOrder marketOrder, MarketOrdersResponse response) {
		return Objects.equals(marketOrder.getOrderID(), response.getOrderId());
	}

	public static class Underbid {
		private Double price;
		private long count;

		public Underbid(Double price, long count) {
			this.price = price;
			this.count = count;
		}

		public Underbid(MarketOrdersResponse ordersResponse) {
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

}
