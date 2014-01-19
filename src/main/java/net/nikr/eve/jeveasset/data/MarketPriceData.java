/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import java.util.Date;


public class MarketPriceData {

	private Date latestDate = null;
	private double latest = 0;
	private double maximum = 0;
	private double minimum = -1;
	private double total = 0;
	private double count = 0;

	public MarketPriceData() { }

	public void update(final double price, final Date date) {
		//Max
		if (price > maximum) {
			this.maximum = price;
		}
		//Min
		if (price < minimum || minimum < 0) {
			this.minimum = price;
		}
		//Average
		total = total + price;
		count++;
		//Latest
		if (latestDate == null || latestDate.before(date)) {
			latest = price;
			latestDate = date;
		}
	}

	public double getLatest() {
		return latest;
	}

	public double getMaximum() {
		return maximum;
	}

	public double getMinimum() {
		if (minimum < 0) {
			return 0;
		} else {
			return minimum;
		}
	}

	public double getAverage() {
		if (total > 0 && count > 0) {
			return total / count;
		} else {
			return 0;
		}
	}
}
