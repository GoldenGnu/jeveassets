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
package net.nikr.eve.jeveasset.gui.tabs.orders;

import java.util.Date;


public class MarketLog {

	private Double price;
	private Double volRemaining;
	private Integer typeID;
	private Integer range;
	private Long orderID;
	private Integer volEntered;
	private Integer minVolume;
	private Boolean bid;
	private Date issueDate;
	private Integer duration;
	private Long stationID;
	private Long regionID;
	private Long solarSystemID;
	private Integer jumps;
	private String empty;

	public MarketLog() {
	}

	public MarketLog(Double price, Double volRemaining, Integer typeID, Integer range, Long orderID, Integer volEntered, Integer minVolume, Boolean bid, Date issueDate, Integer duration, Long stationID, Long regionID, Long solarSystemID, Integer jumps, String empty) {
		this.price = price;
		this.volRemaining = volRemaining;
		this.typeID = typeID;
		this.range = range;
		this.orderID = orderID;
		this.volEntered = volEntered;
		this.minVolume = minVolume;
		this.bid = bid;
		this.issueDate = issueDate;
		this.duration = duration;
		this.stationID = stationID;
		this.regionID = regionID;
		this.solarSystemID = solarSystemID;
		this.jumps = jumps;
		this.empty = empty;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getVolRemaining() {
		return volRemaining;
	}

	public void setVolRemaining(Double volRemaining) {
		this.volRemaining = volRemaining;
	}

	public Integer getTypeID() {
		return typeID;
	}

	public void setTypeID(Integer typeID) {
		this.typeID = typeID;
	}

	public Integer getRange() {
		return range;
	}

	public void setRange(Integer range) {
		this.range = range;
	}

	public Long getOrderID() {
		return orderID;
	}

	public void setOrderID(Long orderID) {
		this.orderID = orderID;
	}

	public Integer getVolEntered() {
		return volEntered;
	}

	public void setVolEntered(Integer volEntered) {
		this.volEntered = volEntered;
	}

	public Integer getMinVolume() {
		return minVolume;
	}

	public void setMinVolume(Integer minVolume) {
		this.minVolume = minVolume;
	}

	public Boolean getBid() {
		return bid;
	}

	public void setBid(Boolean bid) {
		this.bid = bid;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Long getStationID() {
		return stationID;
	}

	public void setStationID(Long stationID) {
		this.stationID = stationID;
	}

	public Long getRegionID() {
		return regionID;
	}

	public void setRegionID(Long regionID) {
		this.regionID = regionID;
	}

	public Long getSolarSystemID() {
		return solarSystemID;
	}

	public void setSolarSystemID(Long solarSystemID) {
		this.solarSystemID = solarSystemID;
	}

	public Integer getJumps() {
		return jumps;
	}

	public void setJumps(Integer jumps) {
		this.jumps = jumps;
	}

	public String getEmpty() {
		return empty;
	}

	public void setEmpty(String empty) {
		this.empty = empty;
	}
}
