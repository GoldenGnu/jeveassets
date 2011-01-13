/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data;

import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import javax.management.timer.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MarketOrder extends ApiMarketOrder implements Comparable<MarketOrder>  {

	private final static Logger LOG = LoggerFactory.getLogger(MarketOrder.class);

	private String name;
	private String location;
	private String system;
	private String region;
	private String rangeFormated;
	private String status;
	private Quantity quantity;


	public MarketOrder(ApiMarketOrder apiMarketOrder, String name, String location, String system, String region) {
		this.setAccountKey(apiMarketOrder.getAccountKey());
		this.setBid(apiMarketOrder.getBid());
		this.setCharID(apiMarketOrder.getCharID());
		this.setDuration(apiMarketOrder.getDuration());
		this.setEscrow(apiMarketOrder.getEscrow());
		this.setIssued(apiMarketOrder.getIssued());
		this.setMinVolume(apiMarketOrder.getMinVolume());
		this.setOrderID(apiMarketOrder.getOrderID());
		this.setOrderState(apiMarketOrder.getOrderState());
		this.setPrice(apiMarketOrder.getPrice());
		this.setRange(apiMarketOrder.getRange());
		this.setStationID(apiMarketOrder.getStationID());
		this.setTypeID(apiMarketOrder.getTypeID());
		this.setVolEntered(apiMarketOrder.getVolEntered());
		this.setVolRemaining(apiMarketOrder.getVolRemaining());
		this.name = name;
		this.location = location;
		this.system = system;
		this.region = region;
		quantity = new Quantity(getVolEntered(), getVolRemaining());
		rangeFormated = "";
		if (this.getRange() == -1) rangeFormated = "Station";
		if (this.getRange() == 0) rangeFormated = "Solar System";
		if (this.getRange() == 32767) rangeFormated = "Region";
		if (this.getRange() == 1) rangeFormated = "1 Jump";
		if (this.getRange() > 1 && this.getRange() < 32767) rangeFormated = this.getRange()+" Jumps";
		//0 = open/active, 1 = closed, 2 = expired (or fulfilled), 3 = cancelled, 4 = pending, 5 = character deleted.
		status = "";
		switch (this.getOrderState()){
			case 0: 
				status = "Active";
				break;
			case 1: 
				status = "Closed";
				break;
			case 2:
				if (this.getVolRemaining() == 0){
					status = "Fulfilled";
					
				} else if (this.getVolRemaining() == this.getVolEntered()){
					status = "Expired";
				} else {
					status = "Partially Fulfilled";
				}
				break;
			case 3: 
				status = "Cancelled";
				break;
			case 4: 
				status = "Pending";
				break;
		}

	}

	@Override
	public int compareTo(MarketOrder o) {
		Long thisID = this.getOrderID();
		Long thatID = o.getOrderID();
		return thisID.compareTo(thatID);
	}

	public String getExpireIn(){
		long timeRemaining;
		timeRemaining = (this.getIssued().getTime() + ((this.getDuration()) * Timer.ONE_DAY)) - Settings.getGmtNow().getTime();
		if (timeRemaining > 0){
			//long sec = (timeRemaining/1000) % 60; // unused, leaving it here as it may be useful in the future, feel free to remove it.
			long min = (timeRemaining/(1000*60)) % 60;
			long hours = (timeRemaining/(1000*60*60)) % 24;
			long days = (timeRemaining/(1000*60*60*24));
			return days+"d "+hours+"h "+min+"m ";
		} else {
			return "Expired";
		}
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public String getRegion() {
		return region;
	}

	public String getSystem() {
		return system;
	}

	public String getRangeFormated() {
		return rangeFormated;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public String getStatus() {
		return status;
	}

	public class Quantity implements Comparable<Quantity> {
		int QuantityEntered;
		int QuantityRemaining;

		public Quantity(int QuantityEntered, int QuantityRemaining) {
			this.QuantityEntered = QuantityEntered;
			this.QuantityRemaining = QuantityRemaining;
		}

		@Override
		public String toString(){
			return QuantityRemaining+"/"+QuantityEntered;
		}

		public int getQuantityEntered() {
			return QuantityEntered;
		}

		public int getQuantityRemaining() {
			return QuantityRemaining;
		}

		@Override
		public int compareTo(Quantity o) {
			Integer thatQuantityRemaining = o.getQuantityRemaining();
			Integer thisQuantityRemaining = QuantityRemaining;
			int result = thatQuantityRemaining.compareTo(thisQuantityRemaining);
			if (result != 0) return result;
			Integer thatQuantityEntered = o.getQuantityEntered();
			Integer thisQuantityEntered = QuantityEntered;
			return thatQuantityEntered.compareTo(thisQuantityEntered);
		}

	}
}
