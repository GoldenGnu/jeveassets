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
package net.nikr.eve.jeveasset.gui.tabs.orders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderRange;
import net.nikr.eve.jeveasset.data.api.raw.RawPublicMarketOrder;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.sde.RouteFinder;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.api.MarketApi;
import net.troja.eve.esi.api.UniverseApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OutbidProcesser {

	private static final Logger LOG = LoggerFactory.getLogger(OutbidProcesser.class);

	private final OutbidProcesserInput input;
	private final OutbidProcesserOutput output;

	private OutbidProcesser(OutbidProcesserInput input, OutbidProcesserOutput output) {
		this.input = input;
		this.output = output;
	}

	public static void process(OutbidProcesserInput input, OutbidProcesserOutput output) {
		OutbidProcesser processer = new OutbidProcesser(input, output);
		processer.process();
	}
	
	private void process() {
		//Process the orders
		for (RawPublicMarketOrder ordersResponse : input.getMarketOrders()) {
			List<MyMarketOrder> orders = input.getTypeIDs().get(ordersResponse.getTypeId());
			if (orders != null) {
				//Orders to match
				for (MyMarketOrder marketOrder : orders) {
					if (isSameOrder(marketOrder, ordersResponse)) { //Orders to be updated
						output.getUpdates().put(ordersResponse.getOrderId(), ordersResponse);
						continue;
					}
					if (!isSameType(marketOrder, ordersResponse)) { //Both buy or both sell
						continue;
					}
					if (!isInRange(marketOrder, ordersResponse)) { //Order range overlap
						continue;
					}
					Outbid outbid = output.getOutbids().get(marketOrder.getOrderID());
					if (outbid == null) {
						outbid = new Outbid(ordersResponse);
						output.getOutbids().put(marketOrder.getOrderID(), outbid);
					}
					if (marketOrder.isBuyOrder()) { //Buy (outbid is higher)
						outbid.setPrice(Math.max(outbid.getPrice(), ordersResponse.getPrice()));
						if (ordersResponse.getPrice() > marketOrder.getPrice()) {
							outbid.addCount(ordersResponse.getVolumeRemain());
						}
					} else { //Sell  (outbid is lower)
						outbid.setPrice(Math.min(outbid.getPrice(), ordersResponse.getPrice()));
						if (marketOrder.getPrice() > ordersResponse.getPrice()) {
							outbid.addCount(ordersResponse.getVolumeRemain());
						}
					}

				}
			}
		}
		CitadelGetter.set(input.getCitadels().values());
	}

	private boolean isInRange(MyMarketOrder marketOrder, RawPublicMarketOrder other) {
		Long fromSystemID = getSystemID(marketOrder.getLocationID());
		Long toSystemID = RawConverter.toLong(other.getSystemId());
		MyLocation fromSystemLocation = ApiIdConverter.getLocation(fromSystemID);
		MyLocation toSystemLocation = ApiIdConverter.getLocation(toSystemID);
		if (fromSystemLocation.isEmpty() || toSystemLocation.isEmpty()) {
			LOG.warn("Unknown market location ignored");
			return false; //We can't work with unknown locations
		}
		if (!Objects.equals(fromSystemLocation.getRegionID(), toSystemLocation.getRegionID())) {
			return false; //Must be in same region
		}
		RawMarketOrder.MarketOrderRange marketOrderRange;
		if (marketOrder.isBuyOrder()) {
			marketOrderRange = marketOrder.getRange();
		} else {
			marketOrderRange = input.getSellOrderRange();
		}
		if (marketOrderRange == RawMarketOrder.MarketOrderRange.REGION
				|| other.getRange() == RawMarketOrder.MarketOrderRange.REGION) {
			return true; //Match everything
		} else if (marketOrderRange == RawMarketOrder.MarketOrderRange.STATION
				&& other.getRange() == RawMarketOrder.MarketOrderRange.STATION) {
			return Objects.equals(marketOrder.getLocationID(), other.getLocationId()); //Only match if in the same station
		} else {
			int range = getRange(other.getRange()) + getRange(marketOrderRange); //Find overlapping area
			//int range = Math.max(getRange(response), getRange(marketOrder)); //Use the order with the max range
			Integer distance = RouteFinder.get().distanceBetween(fromSystemID, toSystemID);
			if (distance == null) {
				return false;
			}
			return distance <= range;
		}
	}



	public static int getRange(RawMarketOrder.MarketOrderRange range) {
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
		return Objects.equals(marketOrder.isBuyOrder(), response.isBuyOrder());
	}

	private boolean isSameOrder(MyMarketOrder marketOrder, RawPublicMarketOrder response) {
		return Objects.equals(marketOrder.getOrderID(), response.getOrderId());
	}

	private Long getSystemID(long locationID) {
		Long systemID = input.getLocationToSystem().get(locationID);
		if (systemID != null) {
			return systemID;
		}
		MyLocation location = ApiIdConverter.getLocation(locationID);
		if (!location.isEmpty()) {
			return location.getSystemID();
		}
		Citadel citadel = input.getCitadels().get(locationID);
		if (citadel != null) {
			return citadel.systemId;
		}
		LOG.warn("Unknown market location");
		return null;
	}

	public static class OutbidProcesserInput {

		private static final Map<Integer, DatedMarketOrders> MARKET_ORDERS = Collections.synchronizedMap(new HashMap<>());
		
		private final Map<Long, Long> locationToSystem = new HashMap<>();
		private final Map<Long, Citadel> citadels = new HashMap<>();
		private final Map<Integer, List<MyMarketOrder>> typeIDs = new HashMap<>();
		private final Set<Long> structureIDs = new HashSet<>();
		private final Set<Integer> regionIDs = new HashSet<>();
		private final MarketOrderRange sellOrderRange;
		private UniverseApi structuresApi = null;
		private MarketApi marketApi = null;

		public OutbidProcesserInput(ProfileData profileData, MarketOrderRange sellOrderRange) {	
			this.sellOrderRange = sellOrderRange;
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
			for (EsiOwner esiOwner : profileData.getProfileManager().getEsiOwners()) {
				if (esiOwner.isStructures()) {
					structuresApi = esiOwner.getUniverseApiAuth();
				}
				if (esiOwner.isMarketStructures()) {
					marketApi = esiOwner.getMarketApiAuth();
				}
			}
		}

		public void addOrders(Map<Integer, List<RawPublicMarketOrder>> orders, Date date) {
			if (date == null) {
				return;
			}
			synchronized (MARKET_ORDERS) {
				for (Map.Entry<Integer, List<RawPublicMarketOrder>> entry : orders.entrySet()) {
					DatedMarketOrders datedMarketOrders = MARKET_ORDERS.get(entry.getKey());
					if (datedMarketOrders != null && datedMarketOrders.getDate().after(date)) {
						return; //Current is newer
					}
					datedMarketOrders = new DatedMarketOrders(date, entry.getValue());
					MARKET_ORDERS.put(entry.getKey(), datedMarketOrders);
				}
			}
		}

		public List<RawPublicMarketOrder> getMarketOrders() {
			List<RawPublicMarketOrder> marketOrders = new ArrayList<>();
			synchronized (MARKET_ORDERS) {
				for (DatedMarketOrders datedMarketOrders : MARKET_ORDERS.values()) {
					marketOrders.addAll(datedMarketOrders.getMarketOrders());
				}
			}
			return marketOrders;
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

		public Set<Long> getStructureIDs() {
			return structureIDs;
		}

		public Set<Integer> getRegionIDs() {
			return regionIDs;
		}

		public MarketOrderRange getSellOrderRange() {
			return sellOrderRange;
		}

		public UniverseApi getStructuresApi() {
			return structuresApi;
		}

		public MarketApi getMarketApi() {
			return marketApi;
		}
	}

	public static class OutbidProcesserOutput {
		private final Map<Long, Outbid> outbids = new HashMap<>();
		private final Map<Long, RawPublicMarketOrder> updates = new HashMap<>();

		public Map<Long, Outbid> getOutbids() {
			return outbids;
		}

		public Map<Long, RawPublicMarketOrder> getUpdates() {
			return updates;
		}

	}

	private static class DatedMarketOrders {

		private final Date date;
		private final List<RawPublicMarketOrder> marketOrders;

		public DatedMarketOrders(Date date, List<RawPublicMarketOrder> marketOrders) {
			this.date = date;
			this.marketOrders = marketOrders;
		}

		public Date getDate() {
			return date;
		}

		public List<RawPublicMarketOrder> getMarketOrders() {
			return marketOrders;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 47 * hash + Objects.hashCode(this.date);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final DatedMarketOrders other = (DatedMarketOrders) obj;
			if (!Objects.equals(this.date, other.date)) {
				return false;
			}
			return true;
		}

	}
}
