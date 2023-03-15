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
import java.util.Set;
import java.util.TreeSet;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;

public class RawMarketOrder {

	public enum MarketOrderRange {
		_1("1", TabsOrders.get().rangeJump()),
		_10("10"),
		_2("2"),
		_20("20"),
		_3("3"),
		_30("30"),
		_4("4"),
		_40("40"),
		_5("5"),
		REGION("region", TabsOrders.get().rangeRegion()),
		SOLARSYSTEM("solarsystem", TabsOrders.get().rangeSolarSystem()),
		STATION("station", TabsOrders.get().rangeStation());

		private static final MarketOrderRange[] SORTED = {
			REGION, SOLARSYSTEM, STATION, _1, _2, _3, _4, _5, _10, _20, _30, _40
		};

		private final String value;
		private final String text;

		MarketOrderRange(String value) {
			this(value, TabsOrders.get().rangeJumps(value));
		}

		MarketOrderRange(String value, String text) {
			this.value = value;
			this.text = text;
		}

		public static MarketOrderRange[] valuesSorted() {
			return SORTED;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return text;
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

		public String getValue() {
			return value;
		}
	}

	private Integer walletDivision = null;
	private Integer duration = null;
	private Double escrow = null;
	private Boolean isBuyOrder = null;
	private Boolean isCorp = null;
	private Date issued = null;
	private final Set<Change> changes = new TreeSet<>();
	private Integer issuedBy = null;
	private Long locationId = null;
	private Integer minVolume = null;
	private Long orderId = null;
	private Double price = null;
	private String range = null;
	private MarketOrderRange rangeEnum = null;
	private Integer regionId = null;
	private String state = null;
	private MarketOrderState stateEnum = null;
	private Integer typeId = null;
	private Integer volumeRemain = null;
	private Integer volumeTotal = null;
	private Date changed;
	private boolean updateChanged = false;

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
		changes.addAll(marketOrder.changes);
		issuedBy = marketOrder.issuedBy;
		locationId = marketOrder.locationId;
		minVolume = marketOrder.minVolume;
		orderId = marketOrder.orderId;
		price = marketOrder.price;
		range = marketOrder.range;
		rangeEnum = marketOrder.rangeEnum;
		regionId = marketOrder.regionId;
		state = marketOrder.state;
		stateEnum = marketOrder.stateEnum;
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
		issuedBy = null;
		locationId = marketOrder.getLocationId();
		minVolume = RawConverter.toInteger(marketOrder.getMinVolume(), 0);
		orderId = marketOrder.getOrderId();
		price = marketOrder.getPrice();
		range = marketOrder.getRangeString();
		rangeEnum = RawConverter.toMarketOrderRange(marketOrder.getRange());
		regionId = marketOrder.getRegionId();
		state = MarketOrderState.OPEN.getValue();
		stateEnum = MarketOrderState.OPEN;
		typeId = marketOrder.getTypeId();
		volumeRemain = marketOrder.getVolumeRemain();
		volumeTotal = marketOrder.getVolumeTotal();
		changes.add(new Change(this));
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
		issuedBy = null;
		locationId = marketOrder.getLocationId();
		minVolume = RawConverter.toInteger(marketOrder.getMinVolume(), 0);
		orderId = marketOrder.getOrderId();
		price = marketOrder.getPrice();
		range = marketOrder.getRangeString();
		rangeEnum = RawConverter.toMarketOrderRange(marketOrder.getRange());
		regionId = marketOrder.getRegionId();
		state = marketOrder.getStateString();
		stateEnum = RawConverter.toMarketOrderState(marketOrder.getState());
		typeId = marketOrder.getTypeId();
		volumeRemain = marketOrder.getVolumeRemain();
		volumeTotal = marketOrder.getVolumeTotal();
		changes.add(new Change(this));
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
		issuedBy = marketOrder.getIssuedBy();
		locationId = marketOrder.getLocationId();
		minVolume = RawConverter.toInteger(marketOrder.getMinVolume(), 0);
		orderId = marketOrder.getOrderId();
		price = marketOrder.getPrice();
		range = marketOrder.getRangeString();
		rangeEnum = RawConverter.toMarketOrderRange(marketOrder.getRange());
		regionId = marketOrder.getRegionId();
		state = MarketOrderState.OPEN.getValue();
		stateEnum = MarketOrderState.OPEN;
		typeId = marketOrder.getTypeId();
		volumeRemain = marketOrder.getVolumeRemain();
		volumeTotal = marketOrder.getVolumeTotal();
		changes.add(new Change(this));
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
		issuedBy = marketOrder.getIssuedBy();
		locationId = marketOrder.getLocationId();
		minVolume = RawConverter.toInteger(marketOrder.getMinVolume(), 0);
		orderId = marketOrder.getOrderId();
		price = marketOrder.getPrice();
		range = marketOrder.getRangeString();
		rangeEnum = RawConverter.toMarketOrderRange(marketOrder.getRange());
		regionId = marketOrder.getRegionId();
		state = marketOrder.getStateString();
		stateEnum = RawConverter.toMarketOrderState(marketOrder.getState());
		typeId = marketOrder.getTypeId();
		volumeRemain = marketOrder.getVolumeRemain();
		volumeTotal = marketOrder.getVolumeTotal();
		changes.add(new Change(this));
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

	public int getEdits() {
		if (changes.isEmpty() || changes.size() == 1) { //0 or 1 = 0;
			return 0;
		} else {
			return changes.size() - 1; // > 1
		}
	}

	public Date getChanged() {
		return changed;
	}

	public void setChanged(Date changed) {
		this.changed = changed;
		updateChanged = false;
	}

	public boolean isUpdateChanged() {
		return updateChanged;
	}

	public Set<Change> getChanges() {
		return changes;
	}

	public void addChangesLegacy(Date date) {
		if (date != null) {
			changes.add(new Change(date, null, null));
		}
	}

	public void addChanges(Set<Change> change) {
		if (change != null) {
			Change current = new Change(this);
			updateChanged = !change.contains(current);
			changes.add(current); //Add current
			changes.addAll(change); //Add old
		}
	}

	public boolean addChanges(RawPublicMarketOrder response) {
		if (response != null) {
			//Set new data
			setPrice(response.getPrice());
			setVolumeRemain(response.getVolumeRemain());
			setIssued(response.getIssued());
			//Add change
			return changes.add(new Change(response));
		}
		return false;
	}

	public Integer getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(Integer issuedBy) {
		this.issuedBy = issuedBy;
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
		return rangeEnum;
	}

	public void setRange(MarketOrderRange range) {
		this.rangeEnum = range;
	}

	public String getRangeString() {
		return range;
	}

	public void setRangeString(String rangeString) {
		this.range = rangeString;
	}

	public Integer getRegionID() {
		return regionId;
	}

	public void setRegionID(Integer regionId) {
		this.regionId = regionId;
	}

	public final MarketOrderState getState() {
		return stateEnum;
	}

	public void setState(MarketOrderState state) {
		this.stateEnum = state;
	}

	public String getStateString() {
		return state;
	}

	public void setStateString(String stateString) {
		this.state = stateString;
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

	public static class Change implements Comparable<Change> {
		private final Date date;
		private final Double price;
		private final Integer volumeRemaining;

		public Change(RawPublicMarketOrder response) {
			this(response.getIssued(), response.getPrice(), response.getVolumeRemain());
		}

		public Change(RawMarketOrder response) {
			this(response.getIssued(), response.getPrice(), response.getVolumeRemain());
		}

		public Change(Date date, Double price, Integer volumeRemaining) {
			this.date = date;
			this.price = price;
			this.volumeRemaining = volumeRemaining;
		}

		public Date getDate() {
			return date;
		}

		public Double getPrice() {
			return price;
		}

		public Integer getVolumeRemaining() {
			return volumeRemaining;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 53 * hash + Objects.hashCode(this.date);
			hash = 53 * hash + Objects.hashCode(this.price);
			hash = 53 * hash + Objects.hashCode(this.volumeRemaining);
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
			final Change other = (Change) obj;
			if (!Objects.equals(this.date, other.date)) {
				return false;
			}
			if (!Objects.equals(this.price, other.price)) {
				return false;
			}
			if (!Objects.equals(this.volumeRemaining, other.volumeRemaining)) {
				return false;
			}
			return true;
		}

		@Override
		public int compareTo(Change change) {
			return date.compareTo(change.date);
		}
	}
}
