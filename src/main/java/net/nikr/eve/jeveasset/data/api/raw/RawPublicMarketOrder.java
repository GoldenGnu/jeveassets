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
package net.nikr.eve.jeveasset.data.api.raw;

import java.util.Date;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderRange;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketLog;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.MarketOrdersResponse;
import net.troja.eve.esi.model.MarketStructuresResponse;


public class RawPublicMarketOrder {

	private final Integer duration;
	private final Integer minVolume;
	private final Boolean isBuyOrder;
	private final Double price;
	private final Integer systemId;
	private final Integer typeId;
	private final String range;
	private final MarketOrderRange rangeEnum;
	private final Integer volumeTotal;
	private final Date issued;
	private final Long orderId;
	private final Integer volumeRemain;
	private final Long locationId;

	public RawPublicMarketOrder(MarketOrdersResponse marketOrder) {
		this.duration = marketOrder.getDuration();
		this.minVolume = marketOrder.getMinVolume();
		this.isBuyOrder = marketOrder.getIsBuyOrder();
		this.price = marketOrder.getPrice();
		this.systemId = marketOrder.getSystemId();
		this.typeId = marketOrder.getTypeId();
		this.range = marketOrder.getRangeString();
		this.rangeEnum = RawConverter.toMarketOrderRange(marketOrder.getRange());
		this.volumeTotal = marketOrder.getVolumeTotal();
		this.issued = RawConverter.toDate(marketOrder.getIssued());
		this.orderId = marketOrder.getOrderId();
		this.volumeRemain = marketOrder.getVolumeRemain();
		this.locationId = marketOrder.getLocationId();
	}

	public RawPublicMarketOrder(MarketStructuresResponse marketOrder, final Long systemId) {
		this.duration = marketOrder.getDuration();
		this.minVolume = marketOrder.getMinVolume();
		this.isBuyOrder = marketOrder.getIsBuyOrder();
		this.price = marketOrder.getPrice();
		this.systemId = RawConverter.toInteger(systemId);
		this.typeId = marketOrder.getTypeId();
		this.range = marketOrder.getRangeString();
		this.rangeEnum = RawConverter.toMarketOrderRange(marketOrder.getRange());
		this.volumeTotal = marketOrder.getVolumeTotal();
		this.issued = RawConverter.toDate(marketOrder.getIssued());
		this.orderId = marketOrder.getOrderId();
		this.volumeRemain = marketOrder.getVolumeRemain();
		this.locationId = marketOrder.getLocationId();
	}

	public RawPublicMarketOrder(MarketLog marketLog) {
		this.duration = marketLog.getDuration();
		this.minVolume = marketLog.getMinVolume();
		this.isBuyOrder = marketLog.getBid();
		this.price = marketLog.getPrice();
		this.systemId = RawConverter.toInteger(marketLog.getSolarSystemID());
		this.typeId = marketLog.getTypeID();
		this.range = null;
		this.rangeEnum = RawConverter.toMarketOrderRange(marketLog.getRange(), null, null);
		this.volumeTotal = marketLog.getVolEntered();
		this.issued = marketLog.getIssueDate();
		this.orderId = marketLog.getOrderID();
		this.volumeRemain = RawConverter.toInteger(marketLog.getVolRemaining());
		this.locationId = marketLog.getStationID();
	}

	public Integer getDuration() {
		return duration;
	}

	public Integer getMinVolume() {
		return minVolume;
	}

	public Boolean isBuyOrder() {
		return isBuyOrder;
	}

	public Double getPrice() {
		return price;
	}

	public Integer getSystemID() {
		return systemId;
	}

	public Integer getTypeID() {
		return typeId;
	}

	public MarketOrderRange getRange() {
		return rangeEnum;
	}

	public String getRangeString() {
		return range;
	}

	public Integer getVolumeTotal() {
		return volumeTotal;
	}

	public Date getIssued() {
		return issued;
	}

	public Long getOrderID() {
		return orderId;
	}

	public Integer getVolumeRemain() {
		return volumeRemain;
	}

	public Long getLocationID() {
		return locationId;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.orderId);
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
		final RawPublicMarketOrder other = (RawPublicMarketOrder) obj;
		if (!Objects.equals(this.orderId, other.orderId)) {
			return false;
		}
		return true;
	}
}
