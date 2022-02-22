/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.data.api.raw.RawPublicMarketOrder;


public class Outbid {

	private Double price;
	private long count;

	public Outbid(Double price, long count) {
		this.price = price;
		this.count = count;
	}

	public Outbid(RawPublicMarketOrder ordersResponse) {
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
