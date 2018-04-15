/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;

public class RawMarketOrder {

	public enum MarketOrderRange {
		_1("1"),
		_10("10"),
		_2("2"),
		_20("20"),
		_3("3"),
		_30("30"),
		_4("4"),
		_40("40"),
		_5("5"),
		REGION("region"),
		SOLARSYSTEM("solarsystem"),
		STATION("station");

		private final String value;

		MarketOrderRange(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		public static MarketOrderRange fromValue(String text) {
            for (MarketOrderRange b : MarketOrderRange.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
	}

	public enum MarketOrderState {
		CANCELLED("cancelled"),
		CHARACTER_DELETED("character_deleted"),
		CLOSED("closed"),
		EXPIRED("expired"),
		OPEN("open"),
		PENDING("pending"),
		UNKNOWN("Unknown");

		private final String value;

		MarketOrderState(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		public static MarketOrderState fromValue(String text) {
            for (MarketOrderState b : MarketOrderState.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
	}

	private Integer walletDivision = null;
	private Integer duration = null;
	private Double escrow = null;
	private Boolean isBuyOrder = null;
	private Boolean isCorp = null;
	private Date issued = null;
	private Long locationId = null;
	private Integer minVolume = null;
	private Long orderId = null;
	private Double price = null;
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
		walletDivision = marketOrder.walletDivision;
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
	 * ESI Character
	 *
	 * @param marketOrder
	 */
	public RawMarketOrder(CharacterOrdersResponse marketOrder) {
		walletDivision = 1;
		duration = marketOrder.getDuration();
		escrow = RawConverter.toDouble(marketOrder.getEscrow(), 0);
		isBuyOrder = RawConverter.toBoolean(marketOrder.getIsBuyOrder());
		isCorp = marketOrder.getIsCorporation();
		issued = RawConverter.toDate(marketOrder.getIssued());
		locationId = marketOrder.getLocationId();
		minVolume = RawConverter.toInteger(marketOrder.getMinVolume(), 0);
		orderId = marketOrder.getOrderId();
		price = marketOrder.getPrice();
		range = MarketOrderRange.valueOf(marketOrder.getRange().name());
		regionId = marketOrder.getRegionId();
		state = MarketOrderState.OPEN;
		typeId = marketOrder.getTypeId();
		volumeRemain = marketOrder.getVolumeRemain();
		volumeTotal = marketOrder.getVolumeTotal();
	}

	/**
	 * ESI Character History
	 *
	 * @param marketOrder
	 */
	public RawMarketOrder(CharacterOrdersHistoryResponse marketOrder) {
		walletDivision = 1;
		duration = marketOrder.getDuration();
		escrow = RawConverter.toDouble(marketOrder.getEscrow(), 0);
		isBuyOrder = RawConverter.toBoolean(marketOrder.getIsBuyOrder());
		isCorp = marketOrder.getIsCorporation();
		issued = RawConverter.toDate(marketOrder.getIssued());
		locationId = marketOrder.getLocationId();
		minVolume = RawConverter.toInteger(marketOrder.getMinVolume(), 0);
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
	 * ESI Corporation
	 *
	 * @param marketOrder
	 */
	public RawMarketOrder(CorporationOrdersResponse marketOrder) {
		walletDivision = marketOrder.getWalletDivision();
		duration = marketOrder.getDuration();
		escrow = RawConverter.toDouble(marketOrder.getEscrow(), 0);
		isBuyOrder = RawConverter.toBoolean(marketOrder.getIsBuyOrder());
		isCorp = true;
		issued = RawConverter.toDate(marketOrder.getIssued());
		locationId = marketOrder.getLocationId();
		minVolume = RawConverter.toInteger(marketOrder.getMinVolume(), 0);
		orderId = marketOrder.getOrderId();
		price = marketOrder.getPrice();
		range = MarketOrderRange.valueOf(marketOrder.getRange().name());
		regionId = marketOrder.getRegionId();
		state = MarketOrderState.OPEN;
		typeId = marketOrder.getTypeId();
		volumeRemain = marketOrder.getVolumeRemain();
		volumeTotal = marketOrder.getVolumeTotal();
	}
	
	/**
	 * ESI Corporation History
	 *
	 * @param marketOrder
	 */
	public RawMarketOrder(CorporationOrdersHistoryResponse marketOrder) {
		walletDivision = marketOrder.getWalletDivision();
		duration = marketOrder.getDuration();
		escrow = RawConverter.toDouble(marketOrder.getEscrow(), 0);
		isBuyOrder = RawConverter.toBoolean(marketOrder.getIsBuyOrder());
		isCorp = true;
		issued = RawConverter.toDate(marketOrder.getIssued());
		locationId = marketOrder.getLocationId();
		minVolume = RawConverter.toInteger(marketOrder.getMinVolume(), 0);
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
	 */
	public RawMarketOrder(enterprises.orbital.evekit.client.model.MarketOrder marketOrder) {
		walletDivision = marketOrder.getWalletDivision() + 999;
		duration = marketOrder.getDuration();
		escrow = RawConverter.toDouble(marketOrder.getEscrow(), 0);
		isBuyOrder = marketOrder.getBid();
		isCorp = marketOrder.getCorp();
		issued = RawConverter.toDate(marketOrder.getIssuedDate());
		locationId = marketOrder.getLocationID();
		minVolume = RawConverter.toInteger(marketOrder.getMinVolume(), 0);
		orderId = marketOrder.getOrderID();
		price = marketOrder.getPrice();
		range = RawConverter.toMarketOrderRange(marketOrder.getOrderRange());
		regionId = marketOrder.getRegionID();
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
		walletDivision = marketOrder.getAccountKey();
		duration = marketOrder.getDuration();
		escrow = marketOrder.getEscrow();
		isBuyOrder = marketOrder.getBid() > 0;
		this.isCorp = isCorp;
		issued = marketOrder.getIssued();
		locationId = marketOrder.getStationID();
		minVolume = marketOrder.getMinVolume();
		orderId = marketOrder.getOrderID();
		price = marketOrder.getPrice();
		range = RawConverter.toMarketOrderRange(marketOrder.getRange());
		regionId = (int) ApiIdConverter.getLocation(marketOrder.getStationID()).getRegionID();
		state = RawConverter.toMarketOrderState(marketOrder.getOrderState());
		typeId = marketOrder.getTypeID();
		volumeRemain = marketOrder.getVolRemaining();
		volumeTotal = marketOrder.getVolEntered();
	}

	public Integer getWalletDivision() {
		return walletDivision;
	}

	public void setWalletDivision(Integer walletDivision) {
		this.walletDivision = walletDivision;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Double getEscrow() {
		return escrow;
	}

	public void setEscrow(Double escrow) {
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
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

	public final Integer getVolumeRemain() {
		return volumeRemain;
	}

	public void setVolumeRemain(Integer volumeRemain) {
		this.volumeRemain = volumeRemain;
	}

	public final Integer getVolumeTotal() {
		return volumeTotal;
	}

	public void setVolumeTotal(Integer volumeTotal) {
		this.volumeTotal = volumeTotal;
	}

}
