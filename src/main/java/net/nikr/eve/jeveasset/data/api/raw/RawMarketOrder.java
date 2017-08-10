/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterOrdersResponse;

public class RawMarketOrder {

	public enum MarketOrderRange {
		STATION("station"),
		REGION("region"),
		SOLARSYSTEM("solarsystem"),
		_1("1"),
		_2("2"),
		_3("3"),
		_4("4"),
		_5("5"),
		_10("10"),
		_20("20"),
		_30("30"),
		_40("40");

		private final String value;

		MarketOrderRange(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	public enum MarketOrderState {
		OPEN("open"),
		CLOSED("closed"),
		EXPIRED("expired"),
		CANCELLED("cancelled"),
		PENDING("pending"),
		CHARACTER_DELETED("character_deleted");

		private final String value;

		MarketOrderState(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

	}

	private Integer accountId = null;
	private Integer duration = null;
	private Float escrow = null;
	private Boolean isBuyOrder = null;
	private Boolean isCorp = null;
	private Date issued = null;
	private Long locationId = null;
	private Integer minVolume = null;
	private Long orderId = null;
	private Float price = null;
	private MarketOrderRange range = null;
	private Integer regionId = null;
	private MarketOrderState state = null;
	private Integer typeId = null;
	private Integer volumeRemain = null;
	private Integer volumeTotal = null;

	/**
	 * New
	 */
	private RawMarketOrder() {
	}

	public static RawMarketOrder create() {
		return new RawMarketOrder();
	}

	/**
	 * Raw
	 *
	 * @param marketOrder
	 */
	protected RawMarketOrder(RawMarketOrder marketOrder) {
		accountId = marketOrder.accountId;
		duration = marketOrder.duration;
		escrow = marketOrder.escrow;
		isBuyOrder = marketOrder.isBuyOrder;
		isCorp = marketOrder.isCorp;
		issued = marketOrder.issued;
		locationId = marketOrder.locationId;
		minVolume = marketOrder.minVolume;
		orderId = marketOrder.orderId;
		price = marketOrder.price;
		range = marketOrder.range;
		regionId = marketOrder.regionId;
		state = marketOrder.state;
		typeId = marketOrder.typeId;
		volumeRemain = marketOrder.volumeRemain;
		volumeTotal = marketOrder.volumeTotal;
	}

	/**
	 * ESI
	 *
	 * @param marketOrder
	 */
	public RawMarketOrder(CharacterOrdersResponse marketOrder) {
		accountId = marketOrder.getAccountId();
		duration = marketOrder.getDuration();
		escrow = marketOrder.getEscrow();
		isBuyOrder = marketOrder.getIsBuyOrder();
		isCorp = marketOrder.getIsCorp();
		issued = RawConverter.toDate(marketOrder.getIssued());
		locationId = marketOrder.getLocationId();
		minVolume = marketOrder.getMinVolume();
		orderId = marketOrder.getOrderId();
		price = marketOrder.getPrice();
		range = MarketOrderRange.valueOf(marketOrder.getRange().name());
		regionId = marketOrder.getRegionId();
		state = MarketOrderState.valueOf(marketOrder.getState().name());
		typeId = marketOrder.getTypeId();
		volumeRemain = marketOrder.getVolumeRemain();
		volumeTotal = marketOrder.getVolumeTotal();
	}

	/**
	 * EveKit
	 *
	 * @param marketOrder
	 * @param isCorp
	 */
	public RawMarketOrder(enterprises.orbital.evekit.client.model.MarketOrder marketOrder, boolean isCorp) {
		accountId = marketOrder.getAccountKey();
		duration = marketOrder.getDuration();
		escrow = RawConverter.toFloat(marketOrder.getEscrow());
		isBuyOrder = marketOrder.getBid();
		this.isCorp = isCorp;
		issued = RawConverter.toDate(marketOrder.getIssuedDate());
		locationId = marketOrder.getStationID();
		minVolume = marketOrder.getMinVolume();
		orderId = marketOrder.getOrderID();
		price = RawConverter.toFloat(marketOrder.getPrice());
		range = RawConverter.toMarketOrderRange(marketOrder.getOrderRange());
		regionId = (int) ApiIdConverter.getLocation(marketOrder.getStationID()).getRegionID();
		state = RawConverter.toMarketOrderState(marketOrder.getOrderState());
		typeId = marketOrder.getTypeID();
		volumeRemain = marketOrder.getVolRemaining();
		volumeTotal = marketOrder.getVolEntered();
	}

	/**
	 * EveAPI
	 *
	 * @param marketOrder
	 * @param isCorp
	 */
	public RawMarketOrder(com.beimin.eveapi.model.shared.MarketOrder marketOrder, boolean isCorp) {
		accountId = marketOrder.getAccountKey();
		duration = marketOrder.getDuration();
		escrow = (float) marketOrder.getEscrow();
		isBuyOrder = marketOrder.getBid() > 0;
		this.isCorp = isCorp;
		issued = marketOrder.getIssued();
		locationId = marketOrder.getStationID();
		minVolume = marketOrder.getMinVolume();
		orderId = marketOrder.getOrderID();
		price = (float) marketOrder.getPrice();
		range = RawConverter.toMarketOrderRange(marketOrder.getRange());
		regionId = (int) ApiIdConverter.getLocation(marketOrder.getStationID()).getRegionID();
		state = RawConverter.toMarketOrderState(marketOrder.getOrderState());
		typeId = marketOrder.getTypeID();
		volumeRemain = marketOrder.getVolRemaining();
		volumeTotal = marketOrder.getVolEntered();
	}

	public Integer getAccountID() {
		return accountId;
	}

	public void setAccountID(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Float getEscrow() {
		return escrow;
	}

	public void setEscrow(Float escrow) {
		this.escrow = escrow;
	}

	public Boolean isBuyOrder() {
		return isBuyOrder;
	}

	public void setBuyOrder(Boolean isBuyOrder) {
		this.isBuyOrder = isBuyOrder;
	}

	public Boolean isCorp() {
		return isCorp;
	}

	public void setCorp(Boolean isCorp) {
		this.isCorp = isCorp;
	}

	public Date getIssued() {
		return issued;
	}

	public void setIssued(Date issued) {
		this.issued = issued;
	}

	public long getLocationID() {
		return locationId;
	}

	public void setLocationID(Long locationId) {
		this.locationId = locationId;
	}

	public Integer getMinVolume() {
		return minVolume;
	}

	public void setMinVolume(Integer minVolume) {
		this.minVolume = minVolume;
	}

	public Long getOrderID() {
		return orderId;
	}

	public void setOrderID(Long orderId) {
		this.orderId = orderId;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public final MarketOrderRange getRange() {
		return range;
	}

	public void setRange(MarketOrderRange range) {
		this.range = range;
	}

	public Integer getRegionID() {
		return regionId;
	}

	public void setRegionID(Integer regionId) {
		this.regionId = regionId;
	}

	public final MarketOrderState getState() {
		return state;
	}

	public void setState(MarketOrderState state) {
		this.state = state;
	}

	public Integer getTypeID() {
		return typeId;
	}

	public void setTypeID(Integer typeId) {
		this.typeId = typeId;
	}

	public final Integer getVolRemaining() {
		return volumeRemain;
	}

	public void setVolumeRemain(Integer volumeRemain) {
		this.volumeRemain = volumeRemain;
	}

	public final Integer getVolEntered() {
		return volumeTotal;
	}

	public void setVolumeTotal(Integer volumeTotal) {
		this.volumeTotal = volumeTotal;
	}

}
